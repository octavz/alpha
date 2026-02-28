package com.alpha.domain.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(
    name = "categories",
    indexes = [
        Index(name = "categories_slug_idx", columnList = "slug"),
        Index(name = "categories_parent_id_idx", columnList = "parent_id")
    ]
)
class CategoryEntity : BaseEntity() {
    
    @Column(name = "name", nullable = false)
    var name: String = ""
    
    @Column(name = "slug", unique = true, nullable = false)
    var slug: String = ""
    
    @Column(name = "description")
    var description: String? = null
    
    @Column(name = "icon")
    var icon: String? = null
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = jakarta.persistence.ForeignKey(name = "fk_categories_parent"))
    var parent: CategoryEntity? = null
    
    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0
    
    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
    
    // Relationships
    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], orphanRemoval = true)
    var children: MutableSet<CategoryEntity> = mutableSetOf()
    
    @OneToMany(mappedBy = "category", cascade = [CascadeType.ALL], orphanRemoval = true)
    var businesses: MutableSet<BusinessEntity> = mutableSetOf()
}