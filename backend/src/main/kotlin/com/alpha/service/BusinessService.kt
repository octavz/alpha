package com.alpha.service

import com.alpha.domain.entity.BusinessEntity
import com.alpha.domain.entity.CategoryEntity
import com.alpha.domain.entity.RegionEntity
import com.alpha.domain.entity.UserEntity
import com.alpha.domain.enums.UserRole
import com.alpha.domain.enums.VerificationStatus
import com.alpha.domain.repository.*
import com.alpha.service.dto.*
import com.alpha.service.exception.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.*

@Service
@Transactional
class BusinessService(
    private val businessRepository: BusinessRepository,
    private val userRepository: UserRepository,
    private val regionRepository: RegionRepository,
    private val categoryRepository: CategoryRepository,
    private val securityContextService: SecurityContextService
) {
    
    fun createBusiness(request: CreateBusinessRequest): BusinessDto {
        val currentUserId = securityContextService.getCurrentUserId()
        val user = userRepository.findById(currentUserId)
            .orElseThrow { NotFoundException("User not found") }
        
        val region = regionRepository.findById(request.regionId)
            .orElseThrow { NotFoundException("Region not found") }
        
        val category = categoryRepository.findById(request.categoryId)
            .orElseThrow { NotFoundException("Category not found") }
        
        val business = BusinessEntity().apply {
            name = request.name
            description = request.description
            addressLine1 = request.addressLine1
            addressLine2 = request.addressLine2
            city = request.city
            state = request.state
            zipCode = request.zipCode
            country = request.country
            phone = request.phone
            email = request.email
            website = request.website
            latitude = request.latitude
            longitude = request.longitude
            this.region = region
            this.category = category
            this.user = user
            slug = generateSlug(request.name)
            verificationStatus = com.alpha.domain.enums.VerificationStatus.PENDING
            isActive = true
            isFeatured = false
            createdAt = OffsetDateTime.now()
            updatedAt = OffsetDateTime.now()
        }
        
        val savedBusiness = businessRepository.save(business)
        return BusinessDto.fromEntity(savedBusiness)
    }
    
    fun updateBusiness(businessId: UUID, request: UpdateBusinessRequest): BusinessDto {
        val business = businessRepository.findById(businessId)
            .orElseThrow { NotFoundException("Business not found") }
        
        // Check if user owns the business or is admin
        val currentUserId = securityContextService.getCurrentUserId()
        val currentUserRole = securityContextService.getCurrentUserRole()
        
        if (business.user?.id != currentUserId && currentUserRole != UserRole.ADMIN) {
            throw AuthorizationException("You don't have permission to update this business")
        }
        
        request.name?.let { business.name = it }
        request.description?.let { business.description = it }
        request.addressLine1?.let { business.addressLine1 = it }
        request.addressLine2?.let { business.addressLine2 = it }
        request.city?.let { business.city = it }
        request.state?.let { business.state = it }
        request.zipCode?.let { business.zipCode = it }
        request.country?.let { business.country = it }
        request.phone?.let { business.phone = it }
        request.email?.let { business.email = it }
        request.website?.let { business.website = it }
        request.latitude?.let { business.latitude = it }
        request.longitude?.let { business.longitude = it }
        request.regionId?.let {
            val region = regionRepository.findById(it)
                .orElseThrow { NotFoundException("Region not found") }
            business.region = region
        }
        request.categoryId?.let {
            val category = categoryRepository.findById(it)
                .orElseThrow { NotFoundException("Category not found") }
            business.category = category
        }
        
        business.updatedAt = OffsetDateTime.now()
        val updatedBusiness = businessRepository.save(business)
        return BusinessDto.fromEntity(updatedBusiness)
    }
    
    fun getBusiness(businessId: UUID): BusinessDto {
        val business = businessRepository.findById(businessId)
            .orElseThrow { NotFoundException("Business not found") }
        return BusinessDto.fromEntity(business)
    }
    
    fun getBusinessBySlug(slug: String): BusinessDto {
        val business = businessRepository.findBySlug(slug)
            ?: throw NotFoundException("Business not found")
        return BusinessDto.fromEntity(business)
    }
    
    fun searchBusinesses(
        query: String?,
        regionId: UUID?,
        categoryId: UUID?,
        pageable: Pageable
    ): Page<BusinessDto> {
        val businesses = if (query != null) {
            businessRepository.searchActiveVerified(query, pageable)
        } else if (regionId != null) {
            businessRepository.findActiveVerifiedByRegion(regionId, pageable)
        } else if (categoryId != null) {
            businessRepository.findActiveVerifiedByCategory(categoryId, pageable)
        } else {
            businessRepository.findActiveVerifiedBusinesses(pageable)
        }
        return businesses.map { BusinessDto.fromEntity(it) }
    }
    
    fun getUserBusinesses(userId: UUID, pageable: Pageable): Page<BusinessDto> {
        // Note: Need to implement paginated version of findByUserId
        val allBusinesses = businessRepository.findByUserId(userId)
        val start = pageable.pageNumber * pageable.pageSize
        val end = minOf(start + pageable.pageSize, allBusinesses.size)
        val pageContent = allBusinesses.subList(start, end)
        
        return org.springframework.data.domain.PageImpl(
            pageContent.map { BusinessDto.fromEntity(it) },
            pageable,
            allBusinesses.size.toLong()
        )
    }
    
    fun getBusinessesByRegion(regionId: UUID, pageable: Pageable): Page<BusinessDto> {
        val businesses = businessRepository.findActiveVerifiedByRegion(regionId, pageable)
        return businesses.map { BusinessDto.fromEntity(it) }
    }
    
    fun getBusinessesByCategory(categoryId: UUID, pageable: Pageable): Page<BusinessDto> {
        val businesses = businessRepository.findActiveVerifiedByCategory(categoryId, pageable)
        return businesses.map { BusinessDto.fromEntity(it) }
    }
    
    fun getFeaturedBusinesses(pageable: Pageable): Page<BusinessDto> {
        // Note: Need to implement paginated version of findByIsFeaturedTrue
        val allBusinesses = businessRepository.findByIsFeaturedTrue()
        val start = pageable.pageNumber * pageable.pageSize
        val end = minOf(start + pageable.pageSize, allBusinesses.size)
        val pageContent = allBusinesses.subList(start, end)
        
        return org.springframework.data.domain.PageImpl(
            pageContent.map { BusinessDto.fromEntity(it) },
            pageable,
            allBusinesses.size.toLong()
        )
    }
    
    fun verifyBusiness(businessId: UUID): BusinessDto {
        val business = businessRepository.findById(businessId)
            .orElseThrow { NotFoundException("Business not found") }
        
        // Only admins can verify businesses
        if (!securityContextService.isCurrentUserAdmin()) {
            throw AuthorizationException("Only admins can verify businesses")
        }
        
        business.verificationStatus = com.alpha.domain.enums.VerificationStatus.APPROVED
        business.updatedAt = OffsetDateTime.now()
        val verifiedBusiness = businessRepository.save(business)
        return BusinessDto.fromEntity(verifiedBusiness)
    }
    
    fun deleteBusiness(businessId: UUID) {
        val business = businessRepository.findById(businessId)
            .orElseThrow { NotFoundException("Business not found") }
        
        // Check if user owns the business or is admin
        val currentUserId = securityContextService.getCurrentUserId()
        val currentUserRole = securityContextService.getCurrentUserRole()
        
        if (business.user?.id != currentUserId && currentUserRole != UserRole.ADMIN) {
            throw AuthorizationException("You don't have permission to delete this business")
        }
        
        businessRepository.delete(business)
    }
    
    private fun generateSlug(name: String): String {
        return name.lowercase()
            .replace(" ", "-")
            .replace("[^a-z0-9-]".toRegex(), "")
            .replace("-+".toRegex(), "-")
            .trim('-')
    }
}