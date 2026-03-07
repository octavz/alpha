import { ResponseContext, RequestContext, HttpFile, HttpInfo } from '../http/http';
import { Configuration, PromiseConfigurationOptions, wrapOptions } from '../configuration'
import { PromiseMiddleware, Middleware, PromiseMiddlewareWrapper } from '../middleware';

import { ApiError } from '../models/ApiError';
import { ApiResponseAppointmentDto } from '../models/ApiResponseAppointmentDto';
import { ApiResponseAuthResponse } from '../models/ApiResponseAuthResponse';
import { ApiResponseBusinessDto } from '../models/ApiResponseBusinessDto';
import { ApiResponseCategoryResponse } from '../models/ApiResponseCategoryResponse';
import { ApiResponseHealthResponse } from '../models/ApiResponseHealthResponse';
import { ApiResponseListCategoryResponse } from '../models/ApiResponseListCategoryResponse';
import { ApiResponseListRegionResponse } from '../models/ApiResponseListRegionResponse';
import { ApiResponseListString } from '../models/ApiResponseListString';
import { ApiResponsePageAppointmentDto } from '../models/ApiResponsePageAppointmentDto';
import { ApiResponsePageBusinessDto } from '../models/ApiResponsePageBusinessDto';
import { ApiResponseRegionResponse } from '../models/ApiResponseRegionResponse';
import { ApiResponseTokenRefreshResponse } from '../models/ApiResponseTokenRefreshResponse';
import { ApiResponseUnit } from '../models/ApiResponseUnit';
import { ApiResponseUserResponse } from '../models/ApiResponseUserResponse';
import { AppointmentDto } from '../models/AppointmentDto';
import { AuthResponse } from '../models/AuthResponse';
import { BusinessDto } from '../models/BusinessDto';
import { CategoryResponse } from '../models/CategoryResponse';
import { CreateAppointmentRequest } from '../models/CreateAppointmentRequest';
import { CreateBusinessRequest } from '../models/CreateBusinessRequest';
import { CreateCategoryRequest } from '../models/CreateCategoryRequest';
import { CreateRegionRequest } from '../models/CreateRegionRequest';
import { ForgotPasswordRequest } from '../models/ForgotPasswordRequest';
import { HealthResponse } from '../models/HealthResponse';
import { LocalTime } from '../models/LocalTime';
import { LoginRequest } from '../models/LoginRequest';
import { LogoutRequest } from '../models/LogoutRequest';
import { PageAppointmentDto } from '../models/PageAppointmentDto';
import { PageBusinessDto } from '../models/PageBusinessDto';
import { Pageable } from '../models/Pageable';
import { Pageablenull } from '../models/Pageablenull';
import { RefreshTokenRequest } from '../models/RefreshTokenRequest';
import { RegionResponse } from '../models/RegionResponse';
import { RegisterRequest } from '../models/RegisterRequest';
import { ResetPasswordRequest } from '../models/ResetPasswordRequest';
import { Sortnull } from '../models/Sortnull';
import { TokenRefreshResponse } from '../models/TokenRefreshResponse';
import { UpdateAppointmentRequest } from '../models/UpdateAppointmentRequest';
import { UpdateBusinessRequest } from '../models/UpdateBusinessRequest';
import { UpdateCategoryRequest } from '../models/UpdateCategoryRequest';
import { UpdateRegionRequest } from '../models/UpdateRegionRequest';
import { UserResponse } from '../models/UserResponse';
import { VerifyEmailRequest } from '../models/VerifyEmailRequest';
import { ObservableAppointmentsApi } from './ObservableAPI';

import { AppointmentsApiRequestFactory, AppointmentsApiResponseProcessor} from "../apis/AppointmentsApi";
export class PromiseAppointmentsApi {
    private api: ObservableAppointmentsApi

    public constructor(
        configuration: Configuration,
        requestFactory?: AppointmentsApiRequestFactory,
        responseProcessor?: AppointmentsApiResponseProcessor
    ) {
        this.api = new ObservableAppointmentsApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Cancel appointment
     * @param appointmentId
     */
    public cancelAppointmentWithHttpInfo(appointmentId: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseAppointmentDto>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.cancelAppointmentWithHttpInfo(appointmentId, observableOptions);
        return result.toPromise();
    }

    /**
     * Cancel appointment
     * @param appointmentId
     */
    public cancelAppointment(appointmentId: string, _options?: PromiseConfigurationOptions): Promise<ApiResponseAppointmentDto> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.cancelAppointment(appointmentId, observableOptions);
        return result.toPromise();
    }

    /**
     * Create a new appointment
     * @param createAppointmentRequest
     */
    public createAppointmentWithHttpInfo(createAppointmentRequest: CreateAppointmentRequest, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseAppointmentDto>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createAppointmentWithHttpInfo(createAppointmentRequest, observableOptions);
        return result.toPromise();
    }

    /**
     * Create a new appointment
     * @param createAppointmentRequest
     */
    public createAppointment(createAppointmentRequest: CreateAppointmentRequest, _options?: PromiseConfigurationOptions): Promise<ApiResponseAppointmentDto> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createAppointment(createAppointmentRequest, observableOptions);
        return result.toPromise();
    }

    /**
     * Get appointment by ID
     * @param appointmentId
     */
    public getAppointmentWithHttpInfo(appointmentId: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseAppointmentDto>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getAppointmentWithHttpInfo(appointmentId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get appointment by ID
     * @param appointmentId
     */
    public getAppointment(appointmentId: string, _options?: PromiseConfigurationOptions): Promise<ApiResponseAppointmentDto> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getAppointment(appointmentId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get business appointments
     * @param businessId
     * @param pageable
     */
    public getBusinessAppointmentsWithHttpInfo(businessId: string, pageable: Pageable, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponsePageAppointmentDto>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getBusinessAppointmentsWithHttpInfo(businessId, pageable, observableOptions);
        return result.toPromise();
    }

    /**
     * Get business appointments
     * @param businessId
     * @param pageable
     */
    public getBusinessAppointments(businessId: string, pageable: Pageable, _options?: PromiseConfigurationOptions): Promise<ApiResponsePageAppointmentDto> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getBusinessAppointments(businessId, pageable, observableOptions);
        return result.toPromise();
    }

    /**
     * Get business availability
     * @param businessId
     * @param date
     */
    public getBusinessAvailabilityWithHttpInfo(businessId: string, date: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseListString>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getBusinessAvailabilityWithHttpInfo(businessId, date, observableOptions);
        return result.toPromise();
    }

    /**
     * Get business availability
     * @param businessId
     * @param date
     */
    public getBusinessAvailability(businessId: string, date: string, _options?: PromiseConfigurationOptions): Promise<ApiResponseListString> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getBusinessAvailability(businessId, date, observableOptions);
        return result.toPromise();
    }

    /**
     * Search appointments
     * @param pageable
     * @param [businessId]
     * @param [customerId]
     * @param [status]
     * @param [startDate]
     * @param [endDate]
     */
    public searchAppointmentsWithHttpInfo(pageable: Pageable, businessId?: string, customerId?: string, status?: 'PENDING' | 'CONFIRMED' | 'COMPLETED' | 'CANCELLED' | 'NO_SHOW', startDate?: string, endDate?: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponsePageAppointmentDto>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.searchAppointmentsWithHttpInfo(pageable, businessId, customerId, status, startDate, endDate, observableOptions);
        return result.toPromise();
    }

    /**
     * Search appointments
     * @param pageable
     * @param [businessId]
     * @param [customerId]
     * @param [status]
     * @param [startDate]
     * @param [endDate]
     */
    public searchAppointments(pageable: Pageable, businessId?: string, customerId?: string, status?: 'PENDING' | 'CONFIRMED' | 'COMPLETED' | 'CANCELLED' | 'NO_SHOW', startDate?: string, endDate?: string, _options?: PromiseConfigurationOptions): Promise<ApiResponsePageAppointmentDto> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.searchAppointments(pageable, businessId, customerId, status, startDate, endDate, observableOptions);
        return result.toPromise();
    }

    /**
     * Update appointment
     * @param appointmentId
     * @param updateAppointmentRequest
     */
    public updateAppointmentWithHttpInfo(appointmentId: string, updateAppointmentRequest: UpdateAppointmentRequest, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseAppointmentDto>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateAppointmentWithHttpInfo(appointmentId, updateAppointmentRequest, observableOptions);
        return result.toPromise();
    }

    /**
     * Update appointment
     * @param appointmentId
     * @param updateAppointmentRequest
     */
    public updateAppointment(appointmentId: string, updateAppointmentRequest: UpdateAppointmentRequest, _options?: PromiseConfigurationOptions): Promise<ApiResponseAppointmentDto> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateAppointment(appointmentId, updateAppointmentRequest, observableOptions);
        return result.toPromise();
    }


}



import { ObservableAuthenticationApi } from './ObservableAPI';

import { AuthenticationApiRequestFactory, AuthenticationApiResponseProcessor} from "../apis/AuthenticationApi";
export class PromiseAuthenticationApi {
    private api: ObservableAuthenticationApi

    public constructor(
        configuration: Configuration,
        requestFactory?: AuthenticationApiRequestFactory,
        responseProcessor?: AuthenticationApiResponseProcessor
    ) {
        this.api = new ObservableAuthenticationApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Change password
     * @param currentPassword
     * @param newPassword
     */
    public changePasswordWithHttpInfo(currentPassword: string, newPassword: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseUnit>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.changePasswordWithHttpInfo(currentPassword, newPassword, observableOptions);
        return result.toPromise();
    }

    /**
     * Change password
     * @param currentPassword
     * @param newPassword
     */
    public changePassword(currentPassword: string, newPassword: string, _options?: PromiseConfigurationOptions): Promise<ApiResponseUnit> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.changePassword(currentPassword, newPassword, observableOptions);
        return result.toPromise();
    }

    /**
     * Request password reset
     * @param forgotPasswordRequest
     */
    public forgotPasswordWithHttpInfo(forgotPasswordRequest: ForgotPasswordRequest, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseUnit>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.forgotPasswordWithHttpInfo(forgotPasswordRequest, observableOptions);
        return result.toPromise();
    }

    /**
     * Request password reset
     * @param forgotPasswordRequest
     */
    public forgotPassword(forgotPasswordRequest: ForgotPasswordRequest, _options?: PromiseConfigurationOptions): Promise<ApiResponseUnit> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.forgotPassword(forgotPasswordRequest, observableOptions);
        return result.toPromise();
    }

    /**
     * Get current user profile
     */
    public getCurrentUserWithHttpInfo(_options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseUserResponse>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getCurrentUserWithHttpInfo(observableOptions);
        return result.toPromise();
    }

    /**
     * Get current user profile
     */
    public getCurrentUser(_options?: PromiseConfigurationOptions): Promise<ApiResponseUserResponse> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getCurrentUser(observableOptions);
        return result.toPromise();
    }

    /**
     * Login user
     * @param loginRequest
     */
    public loginWithHttpInfo(loginRequest: LoginRequest, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseAuthResponse>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.loginWithHttpInfo(loginRequest, observableOptions);
        return result.toPromise();
    }

    /**
     * Login user
     * @param loginRequest
     */
    public login(loginRequest: LoginRequest, _options?: PromiseConfigurationOptions): Promise<ApiResponseAuthResponse> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.login(loginRequest, observableOptions);
        return result.toPromise();
    }

    /**
     * Logout user
     * @param logoutRequest
     */
    public logoutWithHttpInfo(logoutRequest: LogoutRequest, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseUnit>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.logoutWithHttpInfo(logoutRequest, observableOptions);
        return result.toPromise();
    }

    /**
     * Logout user
     * @param logoutRequest
     */
    public logout(logoutRequest: LogoutRequest, _options?: PromiseConfigurationOptions): Promise<ApiResponseUnit> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.logout(logoutRequest, observableOptions);
        return result.toPromise();
    }

    /**
     * Refresh access token
     * @param refreshTokenRequest
     */
    public refreshTokenWithHttpInfo(refreshTokenRequest: RefreshTokenRequest, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseTokenRefreshResponse>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.refreshTokenWithHttpInfo(refreshTokenRequest, observableOptions);
        return result.toPromise();
    }

    /**
     * Refresh access token
     * @param refreshTokenRequest
     */
    public refreshToken(refreshTokenRequest: RefreshTokenRequest, _options?: PromiseConfigurationOptions): Promise<ApiResponseTokenRefreshResponse> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.refreshToken(refreshTokenRequest, observableOptions);
        return result.toPromise();
    }

    /**
     * Register a new user
     * @param registerRequest
     */
    public registerWithHttpInfo(registerRequest: RegisterRequest, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseAuthResponse>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.registerWithHttpInfo(registerRequest, observableOptions);
        return result.toPromise();
    }

    /**
     * Register a new user
     * @param registerRequest
     */
    public register(registerRequest: RegisterRequest, _options?: PromiseConfigurationOptions): Promise<ApiResponseAuthResponse> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.register(registerRequest, observableOptions);
        return result.toPromise();
    }

    /**
     * Reset password with token
     * @param resetPasswordRequest
     */
    public resetPasswordWithHttpInfo(resetPasswordRequest: ResetPasswordRequest, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseUnit>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.resetPasswordWithHttpInfo(resetPasswordRequest, observableOptions);
        return result.toPromise();
    }

    /**
     * Reset password with token
     * @param resetPasswordRequest
     */
    public resetPassword(resetPasswordRequest: ResetPasswordRequest, _options?: PromiseConfigurationOptions): Promise<ApiResponseUnit> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.resetPassword(resetPasswordRequest, observableOptions);
        return result.toPromise();
    }

    /**
     * Update user profile
     * @param [name]
     * @param [phone]
     * @param [avatarUrl]
     */
    public updateProfileWithHttpInfo(name?: string, phone?: string, avatarUrl?: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseUserResponse>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateProfileWithHttpInfo(name, phone, avatarUrl, observableOptions);
        return result.toPromise();
    }

    /**
     * Update user profile
     * @param [name]
     * @param [phone]
     * @param [avatarUrl]
     */
    public updateProfile(name?: string, phone?: string, avatarUrl?: string, _options?: PromiseConfigurationOptions): Promise<ApiResponseUserResponse> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateProfile(name, phone, avatarUrl, observableOptions);
        return result.toPromise();
    }

    /**
     * Verify email address
     * @param verifyEmailRequest
     */
    public verifyEmailWithHttpInfo(verifyEmailRequest: VerifyEmailRequest, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseUnit>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.verifyEmailWithHttpInfo(verifyEmailRequest, observableOptions);
        return result.toPromise();
    }

    /**
     * Verify email address
     * @param verifyEmailRequest
     */
    public verifyEmail(verifyEmailRequest: VerifyEmailRequest, _options?: PromiseConfigurationOptions): Promise<ApiResponseUnit> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.verifyEmail(verifyEmailRequest, observableOptions);
        return result.toPromise();
    }


}



import { ObservableBusinessesApi } from './ObservableAPI';

import { BusinessesApiRequestFactory, BusinessesApiResponseProcessor} from "../apis/BusinessesApi";
export class PromiseBusinessesApi {
    private api: ObservableBusinessesApi

    public constructor(
        configuration: Configuration,
        requestFactory?: BusinessesApiRequestFactory,
        responseProcessor?: BusinessesApiResponseProcessor
    ) {
        this.api = new ObservableBusinessesApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Create a new business
     * @param createBusinessRequest
     */
    public createBusinessWithHttpInfo(createBusinessRequest: CreateBusinessRequest, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseBusinessDto>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createBusinessWithHttpInfo(createBusinessRequest, observableOptions);
        return result.toPromise();
    }

    /**
     * Create a new business
     * @param createBusinessRequest
     */
    public createBusiness(createBusinessRequest: CreateBusinessRequest, _options?: PromiseConfigurationOptions): Promise<ApiResponseBusinessDto> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createBusiness(createBusinessRequest, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete business
     * @param businessId
     */
    public deleteBusinessWithHttpInfo(businessId: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseUnit>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteBusinessWithHttpInfo(businessId, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete business
     * @param businessId
     */
    public deleteBusiness(businessId: string, _options?: PromiseConfigurationOptions): Promise<ApiResponseUnit> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteBusiness(businessId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get business by ID
     * @param businessId
     */
    public getBusinessWithHttpInfo(businessId: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseBusinessDto>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getBusinessWithHttpInfo(businessId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get business by ID
     * @param businessId
     */
    public getBusiness(businessId: string, _options?: PromiseConfigurationOptions): Promise<ApiResponseBusinessDto> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getBusiness(businessId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get business by slug
     * @param slug
     */
    public getBusinessBySlugWithHttpInfo(slug: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseBusinessDto>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getBusinessBySlugWithHttpInfo(slug, observableOptions);
        return result.toPromise();
    }

    /**
     * Get business by slug
     * @param slug
     */
    public getBusinessBySlug(slug: string, _options?: PromiseConfigurationOptions): Promise<ApiResponseBusinessDto> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getBusinessBySlug(slug, observableOptions);
        return result.toPromise();
    }

    /**
     * Get businesses by category
     * @param categoryId
     * @param pageable
     */
    public getBusinessesByCategoryWithHttpInfo(categoryId: string, pageable: Pageable, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponsePageBusinessDto>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getBusinessesByCategoryWithHttpInfo(categoryId, pageable, observableOptions);
        return result.toPromise();
    }

    /**
     * Get businesses by category
     * @param categoryId
     * @param pageable
     */
    public getBusinessesByCategory(categoryId: string, pageable: Pageable, _options?: PromiseConfigurationOptions): Promise<ApiResponsePageBusinessDto> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getBusinessesByCategory(categoryId, pageable, observableOptions);
        return result.toPromise();
    }

    /**
     * Get businesses by region
     * @param regionId
     * @param pageable
     */
    public getBusinessesByRegionWithHttpInfo(regionId: string, pageable: Pageable, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponsePageBusinessDto>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getBusinessesByRegionWithHttpInfo(regionId, pageable, observableOptions);
        return result.toPromise();
    }

    /**
     * Get businesses by region
     * @param regionId
     * @param pageable
     */
    public getBusinessesByRegion(regionId: string, pageable: Pageable, _options?: PromiseConfigurationOptions): Promise<ApiResponsePageBusinessDto> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getBusinessesByRegion(regionId, pageable, observableOptions);
        return result.toPromise();
    }

    /**
     * Get featured businesses
     * @param pageable
     */
    public getFeaturedBusinessesWithHttpInfo(pageable: Pageable, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponsePageBusinessDto>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getFeaturedBusinessesWithHttpInfo(pageable, observableOptions);
        return result.toPromise();
    }

    /**
     * Get featured businesses
     * @param pageable
     */
    public getFeaturedBusinesses(pageable: Pageable, _options?: PromiseConfigurationOptions): Promise<ApiResponsePageBusinessDto> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getFeaturedBusinesses(pageable, observableOptions);
        return result.toPromise();
    }

    /**
     * Get current user\'s businesses
     * @param pageable
     */
    public getUserBusinessesWithHttpInfo(pageable: Pageable, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponsePageBusinessDto>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getUserBusinessesWithHttpInfo(pageable, observableOptions);
        return result.toPromise();
    }

    /**
     * Get current user\'s businesses
     * @param pageable
     */
    public getUserBusinesses(pageable: Pageable, _options?: PromiseConfigurationOptions): Promise<ApiResponsePageBusinessDto> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getUserBusinesses(pageable, observableOptions);
        return result.toPromise();
    }

    /**
     * Search businesses
     * @param pageable
     * @param [query]
     * @param [regionId]
     * @param [categoryId]
     */
    public searchBusinessesWithHttpInfo(pageable: Pageable, query?: string, regionId?: string, categoryId?: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponsePageBusinessDto>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.searchBusinessesWithHttpInfo(pageable, query, regionId, categoryId, observableOptions);
        return result.toPromise();
    }

    /**
     * Search businesses
     * @param pageable
     * @param [query]
     * @param [regionId]
     * @param [categoryId]
     */
    public searchBusinesses(pageable: Pageable, query?: string, regionId?: string, categoryId?: string, _options?: PromiseConfigurationOptions): Promise<ApiResponsePageBusinessDto> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.searchBusinesses(pageable, query, regionId, categoryId, observableOptions);
        return result.toPromise();
    }

    /**
     * Update business
     * @param businessId
     * @param updateBusinessRequest
     */
    public updateBusinessWithHttpInfo(businessId: string, updateBusinessRequest: UpdateBusinessRequest, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseBusinessDto>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateBusinessWithHttpInfo(businessId, updateBusinessRequest, observableOptions);
        return result.toPromise();
    }

    /**
     * Update business
     * @param businessId
     * @param updateBusinessRequest
     */
    public updateBusiness(businessId: string, updateBusinessRequest: UpdateBusinessRequest, _options?: PromiseConfigurationOptions): Promise<ApiResponseBusinessDto> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateBusiness(businessId, updateBusinessRequest, observableOptions);
        return result.toPromise();
    }

    /**
     * Verify business (admin only)
     * @param businessId
     */
    public verifyBusinessWithHttpInfo(businessId: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseBusinessDto>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.verifyBusinessWithHttpInfo(businessId, observableOptions);
        return result.toPromise();
    }

    /**
     * Verify business (admin only)
     * @param businessId
     */
    public verifyBusiness(businessId: string, _options?: PromiseConfigurationOptions): Promise<ApiResponseBusinessDto> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.verifyBusiness(businessId, observableOptions);
        return result.toPromise();
    }


}



import { ObservableCategoriesApi } from './ObservableAPI';

import { CategoriesApiRequestFactory, CategoriesApiResponseProcessor} from "../apis/CategoriesApi";
export class PromiseCategoriesApi {
    private api: ObservableCategoriesApi

    public constructor(
        configuration: Configuration,
        requestFactory?: CategoriesApiRequestFactory,
        responseProcessor?: CategoriesApiResponseProcessor
    ) {
        this.api = new ObservableCategoriesApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Create new category (admin only)
     * @param createCategoryRequest
     */
    public createCategoryWithHttpInfo(createCategoryRequest: CreateCategoryRequest, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseCategoryResponse>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createCategoryWithHttpInfo(createCategoryRequest, observableOptions);
        return result.toPromise();
    }

    /**
     * Create new category (admin only)
     * @param createCategoryRequest
     */
    public createCategory(createCategoryRequest: CreateCategoryRequest, _options?: PromiseConfigurationOptions): Promise<ApiResponseCategoryResponse> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createCategory(createCategoryRequest, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete category (admin only)
     * @param categoryId
     */
    public deleteCategoryWithHttpInfo(categoryId: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseUnit>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteCategoryWithHttpInfo(categoryId, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete category (admin only)
     * @param categoryId
     */
    public deleteCategory(categoryId: string, _options?: PromiseConfigurationOptions): Promise<ApiResponseUnit> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteCategory(categoryId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get all active categories
     */
    public getAllCategoriesWithHttpInfo(_options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseListCategoryResponse>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getAllCategoriesWithHttpInfo(observableOptions);
        return result.toPromise();
    }

    /**
     * Get all active categories
     */
    public getAllCategories(_options?: PromiseConfigurationOptions): Promise<ApiResponseListCategoryResponse> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getAllCategories(observableOptions);
        return result.toPromise();
    }

    /**
     * Get category by ID
     * @param categoryId
     */
    public getCategoryWithHttpInfo(categoryId: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseCategoryResponse>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getCategoryWithHttpInfo(categoryId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get category by ID
     * @param categoryId
     */
    public getCategory(categoryId: string, _options?: PromiseConfigurationOptions): Promise<ApiResponseCategoryResponse> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getCategory(categoryId, observableOptions);
        return result.toPromise();
    }

    /**
     * Update category (admin only)
     * @param categoryId
     * @param updateCategoryRequest
     */
    public updateCategoryWithHttpInfo(categoryId: string, updateCategoryRequest: UpdateCategoryRequest, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseCategoryResponse>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateCategoryWithHttpInfo(categoryId, updateCategoryRequest, observableOptions);
        return result.toPromise();
    }

    /**
     * Update category (admin only)
     * @param categoryId
     * @param updateCategoryRequest
     */
    public updateCategory(categoryId: string, updateCategoryRequest: UpdateCategoryRequest, _options?: PromiseConfigurationOptions): Promise<ApiResponseCategoryResponse> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateCategory(categoryId, updateCategoryRequest, observableOptions);
        return result.toPromise();
    }


}



import { ObservableHealthApi } from './ObservableAPI';

import { HealthApiRequestFactory, HealthApiResponseProcessor} from "../apis/HealthApi";
export class PromiseHealthApi {
    private api: ObservableHealthApi

    public constructor(
        configuration: Configuration,
        requestFactory?: HealthApiRequestFactory,
        responseProcessor?: HealthApiResponseProcessor
    ) {
        this.api = new ObservableHealthApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Check if the API is running
     * Health check
     */
    public healthCheckWithHttpInfo(_options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseHealthResponse>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.healthCheckWithHttpInfo(observableOptions);
        return result.toPromise();
    }

    /**
     * Check if the API is running
     * Health check
     */
    public healthCheck(_options?: PromiseConfigurationOptions): Promise<ApiResponseHealthResponse> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.healthCheck(observableOptions);
        return result.toPromise();
    }


}



import { ObservableRegionsApi } from './ObservableAPI';

import { RegionsApiRequestFactory, RegionsApiResponseProcessor} from "../apis/RegionsApi";
export class PromiseRegionsApi {
    private api: ObservableRegionsApi

    public constructor(
        configuration: Configuration,
        requestFactory?: RegionsApiRequestFactory,
        responseProcessor?: RegionsApiResponseProcessor
    ) {
        this.api = new ObservableRegionsApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Create new region (admin only)
     * @param createRegionRequest
     */
    public createRegionWithHttpInfo(createRegionRequest: CreateRegionRequest, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseRegionResponse>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createRegionWithHttpInfo(createRegionRequest, observableOptions);
        return result.toPromise();
    }

    /**
     * Create new region (admin only)
     * @param createRegionRequest
     */
    public createRegion(createRegionRequest: CreateRegionRequest, _options?: PromiseConfigurationOptions): Promise<ApiResponseRegionResponse> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createRegion(createRegionRequest, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete region (admin only)
     * @param regionId
     */
    public deleteRegionWithHttpInfo(regionId: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseUnit>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteRegionWithHttpInfo(regionId, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete region (admin only)
     * @param regionId
     */
    public deleteRegion(regionId: string, _options?: PromiseConfigurationOptions): Promise<ApiResponseUnit> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteRegion(regionId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get all active regions
     */
    public getAllRegionsWithHttpInfo(_options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseListRegionResponse>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getAllRegionsWithHttpInfo(observableOptions);
        return result.toPromise();
    }

    /**
     * Get all active regions
     */
    public getAllRegions(_options?: PromiseConfigurationOptions): Promise<ApiResponseListRegionResponse> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getAllRegions(observableOptions);
        return result.toPromise();
    }

    /**
     * Get region by ID
     * @param regionId
     */
    public getRegionWithHttpInfo(regionId: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseRegionResponse>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getRegionWithHttpInfo(regionId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get region by ID
     * @param regionId
     */
    public getRegion(regionId: string, _options?: PromiseConfigurationOptions): Promise<ApiResponseRegionResponse> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getRegion(regionId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get region by code
     * @param code
     */
    public getRegionByCodeWithHttpInfo(code: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseRegionResponse>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getRegionByCodeWithHttpInfo(code, observableOptions);
        return result.toPromise();
    }

    /**
     * Get region by code
     * @param code
     */
    public getRegionByCode(code: string, _options?: PromiseConfigurationOptions): Promise<ApiResponseRegionResponse> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getRegionByCode(code, observableOptions);
        return result.toPromise();
    }

    /**
     * Search regions
     * @param search
     */
    public searchRegionsWithHttpInfo(search: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseListRegionResponse>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.searchRegionsWithHttpInfo(search, observableOptions);
        return result.toPromise();
    }

    /**
     * Search regions
     * @param search
     */
    public searchRegions(search: string, _options?: PromiseConfigurationOptions): Promise<ApiResponseListRegionResponse> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.searchRegions(search, observableOptions);
        return result.toPromise();
    }

    /**
     * Update region (admin only)
     * @param regionId
     * @param updateRegionRequest
     */
    public updateRegionWithHttpInfo(regionId: string, updateRegionRequest: UpdateRegionRequest, _options?: PromiseConfigurationOptions): Promise<HttpInfo<ApiResponseRegionResponse>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateRegionWithHttpInfo(regionId, updateRegionRequest, observableOptions);
        return result.toPromise();
    }

    /**
     * Update region (admin only)
     * @param regionId
     * @param updateRegionRequest
     */
    public updateRegion(regionId: string, updateRegionRequest: UpdateRegionRequest, _options?: PromiseConfigurationOptions): Promise<ApiResponseRegionResponse> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateRegion(regionId, updateRegionRequest, observableOptions);
        return result.toPromise();
    }


}



