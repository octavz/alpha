import { ResponseContext, RequestContext, HttpFile, HttpInfo } from '../http/http';
import { Configuration, ConfigurationOptions } from '../configuration'
import type { Middleware } from '../middleware';

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

import { ObservableAppointmentsApi } from "./ObservableAPI";
import { AppointmentsApiRequestFactory, AppointmentsApiResponseProcessor} from "../apis/AppointmentsApi";

export interface AppointmentsApiCancelAppointmentRequest {
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof AppointmentsApicancelAppointment
     */
    appointmentId: string
}

export interface AppointmentsApiCreateAppointmentRequest {
    /**
     * 
     * @type CreateAppointmentRequest
     * @memberof AppointmentsApicreateAppointment
     */
    createAppointmentRequest: CreateAppointmentRequest
}

export interface AppointmentsApiGetAppointmentRequest {
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof AppointmentsApigetAppointment
     */
    appointmentId: string
}

export interface AppointmentsApiGetBusinessAppointmentsRequest {
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof AppointmentsApigetBusinessAppointments
     */
    businessId: string
    /**
     * 
     * Defaults to: undefined
     * @type Pageable
     * @memberof AppointmentsApigetBusinessAppointments
     */
    pageable: Pageable
}

export interface AppointmentsApiGetBusinessAvailabilityRequest {
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof AppointmentsApigetBusinessAvailability
     */
    businessId: string
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof AppointmentsApigetBusinessAvailability
     */
    date: string
}

export interface AppointmentsApiSearchAppointmentsRequest {
    /**
     * 
     * Defaults to: undefined
     * @type Pageable
     * @memberof AppointmentsApisearchAppointments
     */
    pageable: Pageable
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof AppointmentsApisearchAppointments
     */
    businessId?: string
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof AppointmentsApisearchAppointments
     */
    customerId?: string
    /**
     * 
     * Defaults to: undefined
     * @type &#39;PENDING&#39; | &#39;CONFIRMED&#39; | &#39;COMPLETED&#39; | &#39;CANCELLED&#39; | &#39;NO_SHOW&#39;
     * @memberof AppointmentsApisearchAppointments
     */
    status?: 'PENDING' | 'CONFIRMED' | 'COMPLETED' | 'CANCELLED' | 'NO_SHOW'
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof AppointmentsApisearchAppointments
     */
    startDate?: string
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof AppointmentsApisearchAppointments
     */
    endDate?: string
}

export interface AppointmentsApiUpdateAppointmentRequest {
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof AppointmentsApiupdateAppointment
     */
    appointmentId: string
    /**
     * 
     * @type UpdateAppointmentRequest
     * @memberof AppointmentsApiupdateAppointment
     */
    updateAppointmentRequest: UpdateAppointmentRequest
}

export class ObjectAppointmentsApi {
    private api: ObservableAppointmentsApi

    public constructor(configuration: Configuration, requestFactory?: AppointmentsApiRequestFactory, responseProcessor?: AppointmentsApiResponseProcessor) {
        this.api = new ObservableAppointmentsApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Cancel appointment
     * @param param the request object
     */
    public cancelAppointmentWithHttpInfo(param: AppointmentsApiCancelAppointmentRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseAppointmentDto>> {
        return this.api.cancelAppointmentWithHttpInfo(param.appointmentId,  options).toPromise();
    }

    /**
     * Cancel appointment
     * @param param the request object
     */
    public cancelAppointment(param: AppointmentsApiCancelAppointmentRequest, options?: ConfigurationOptions): Promise<ApiResponseAppointmentDto> {
        return this.api.cancelAppointment(param.appointmentId,  options).toPromise();
    }

    /**
     * Create a new appointment
     * @param param the request object
     */
    public createAppointmentWithHttpInfo(param: AppointmentsApiCreateAppointmentRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseAppointmentDto>> {
        return this.api.createAppointmentWithHttpInfo(param.createAppointmentRequest,  options).toPromise();
    }

    /**
     * Create a new appointment
     * @param param the request object
     */
    public createAppointment(param: AppointmentsApiCreateAppointmentRequest, options?: ConfigurationOptions): Promise<ApiResponseAppointmentDto> {
        return this.api.createAppointment(param.createAppointmentRequest,  options).toPromise();
    }

    /**
     * Get appointment by ID
     * @param param the request object
     */
    public getAppointmentWithHttpInfo(param: AppointmentsApiGetAppointmentRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseAppointmentDto>> {
        return this.api.getAppointmentWithHttpInfo(param.appointmentId,  options).toPromise();
    }

    /**
     * Get appointment by ID
     * @param param the request object
     */
    public getAppointment(param: AppointmentsApiGetAppointmentRequest, options?: ConfigurationOptions): Promise<ApiResponseAppointmentDto> {
        return this.api.getAppointment(param.appointmentId,  options).toPromise();
    }

    /**
     * Get business appointments
     * @param param the request object
     */
    public getBusinessAppointmentsWithHttpInfo(param: AppointmentsApiGetBusinessAppointmentsRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponsePageAppointmentDto>> {
        return this.api.getBusinessAppointmentsWithHttpInfo(param.businessId, param.pageable,  options).toPromise();
    }

    /**
     * Get business appointments
     * @param param the request object
     */
    public getBusinessAppointments(param: AppointmentsApiGetBusinessAppointmentsRequest, options?: ConfigurationOptions): Promise<ApiResponsePageAppointmentDto> {
        return this.api.getBusinessAppointments(param.businessId, param.pageable,  options).toPromise();
    }

    /**
     * Get business availability
     * @param param the request object
     */
    public getBusinessAvailabilityWithHttpInfo(param: AppointmentsApiGetBusinessAvailabilityRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseListString>> {
        return this.api.getBusinessAvailabilityWithHttpInfo(param.businessId, param.date,  options).toPromise();
    }

    /**
     * Get business availability
     * @param param the request object
     */
    public getBusinessAvailability(param: AppointmentsApiGetBusinessAvailabilityRequest, options?: ConfigurationOptions): Promise<ApiResponseListString> {
        return this.api.getBusinessAvailability(param.businessId, param.date,  options).toPromise();
    }

    /**
     * Search appointments
     * @param param the request object
     */
    public searchAppointmentsWithHttpInfo(param: AppointmentsApiSearchAppointmentsRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponsePageAppointmentDto>> {
        return this.api.searchAppointmentsWithHttpInfo(param.pageable, param.businessId, param.customerId, param.status, param.startDate, param.endDate,  options).toPromise();
    }

    /**
     * Search appointments
     * @param param the request object
     */
    public searchAppointments(param: AppointmentsApiSearchAppointmentsRequest, options?: ConfigurationOptions): Promise<ApiResponsePageAppointmentDto> {
        return this.api.searchAppointments(param.pageable, param.businessId, param.customerId, param.status, param.startDate, param.endDate,  options).toPromise();
    }

    /**
     * Update appointment
     * @param param the request object
     */
    public updateAppointmentWithHttpInfo(param: AppointmentsApiUpdateAppointmentRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseAppointmentDto>> {
        return this.api.updateAppointmentWithHttpInfo(param.appointmentId, param.updateAppointmentRequest,  options).toPromise();
    }

    /**
     * Update appointment
     * @param param the request object
     */
    public updateAppointment(param: AppointmentsApiUpdateAppointmentRequest, options?: ConfigurationOptions): Promise<ApiResponseAppointmentDto> {
        return this.api.updateAppointment(param.appointmentId, param.updateAppointmentRequest,  options).toPromise();
    }

}

import { ObservableAuthenticationApi } from "./ObservableAPI";
import { AuthenticationApiRequestFactory, AuthenticationApiResponseProcessor} from "../apis/AuthenticationApi";

export interface AuthenticationApiChangePasswordRequest {
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof AuthenticationApichangePassword
     */
    currentPassword: string
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof AuthenticationApichangePassword
     */
    newPassword: string
}

export interface AuthenticationApiForgotPasswordRequest {
    /**
     * 
     * @type ForgotPasswordRequest
     * @memberof AuthenticationApiforgotPassword
     */
    forgotPasswordRequest: ForgotPasswordRequest
}

export interface AuthenticationApiGetCurrentUserRequest {
}

export interface AuthenticationApiLoginRequest {
    /**
     * 
     * @type LoginRequest
     * @memberof AuthenticationApilogin
     */
    loginRequest: LoginRequest
}

export interface AuthenticationApiLogoutRequest {
    /**
     * 
     * @type LogoutRequest
     * @memberof AuthenticationApilogout
     */
    logoutRequest: LogoutRequest
}

export interface AuthenticationApiRefreshTokenRequest {
    /**
     * 
     * @type RefreshTokenRequest
     * @memberof AuthenticationApirefreshToken
     */
    refreshTokenRequest: RefreshTokenRequest
}

export interface AuthenticationApiRegisterRequest {
    /**
     * 
     * @type RegisterRequest
     * @memberof AuthenticationApiregister
     */
    registerRequest: RegisterRequest
}

export interface AuthenticationApiResetPasswordRequest {
    /**
     * 
     * @type ResetPasswordRequest
     * @memberof AuthenticationApiresetPassword
     */
    resetPasswordRequest: ResetPasswordRequest
}

export interface AuthenticationApiUpdateProfileRequest {
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof AuthenticationApiupdateProfile
     */
    name?: string
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof AuthenticationApiupdateProfile
     */
    phone?: string
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof AuthenticationApiupdateProfile
     */
    avatarUrl?: string
}

export interface AuthenticationApiVerifyEmailRequest {
    /**
     * 
     * @type VerifyEmailRequest
     * @memberof AuthenticationApiverifyEmail
     */
    verifyEmailRequest: VerifyEmailRequest
}

export class ObjectAuthenticationApi {
    private api: ObservableAuthenticationApi

    public constructor(configuration: Configuration, requestFactory?: AuthenticationApiRequestFactory, responseProcessor?: AuthenticationApiResponseProcessor) {
        this.api = new ObservableAuthenticationApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Change password
     * @param param the request object
     */
    public changePasswordWithHttpInfo(param: AuthenticationApiChangePasswordRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseUnit>> {
        return this.api.changePasswordWithHttpInfo(param.currentPassword, param.newPassword,  options).toPromise();
    }

    /**
     * Change password
     * @param param the request object
     */
    public changePassword(param: AuthenticationApiChangePasswordRequest, options?: ConfigurationOptions): Promise<ApiResponseUnit> {
        return this.api.changePassword(param.currentPassword, param.newPassword,  options).toPromise();
    }

    /**
     * Request password reset
     * @param param the request object
     */
    public forgotPasswordWithHttpInfo(param: AuthenticationApiForgotPasswordRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseUnit>> {
        return this.api.forgotPasswordWithHttpInfo(param.forgotPasswordRequest,  options).toPromise();
    }

    /**
     * Request password reset
     * @param param the request object
     */
    public forgotPassword(param: AuthenticationApiForgotPasswordRequest, options?: ConfigurationOptions): Promise<ApiResponseUnit> {
        return this.api.forgotPassword(param.forgotPasswordRequest,  options).toPromise();
    }

    /**
     * Get current user profile
     * @param param the request object
     */
    public getCurrentUserWithHttpInfo(param: AuthenticationApiGetCurrentUserRequest = {}, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseUserResponse>> {
        return this.api.getCurrentUserWithHttpInfo( options).toPromise();
    }

    /**
     * Get current user profile
     * @param param the request object
     */
    public getCurrentUser(param: AuthenticationApiGetCurrentUserRequest = {}, options?: ConfigurationOptions): Promise<ApiResponseUserResponse> {
        return this.api.getCurrentUser( options).toPromise();
    }

    /**
     * Login user
     * @param param the request object
     */
    public loginWithHttpInfo(param: AuthenticationApiLoginRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseAuthResponse>> {
        return this.api.loginWithHttpInfo(param.loginRequest,  options).toPromise();
    }

    /**
     * Login user
     * @param param the request object
     */
    public login(param: AuthenticationApiLoginRequest, options?: ConfigurationOptions): Promise<ApiResponseAuthResponse> {
        return this.api.login(param.loginRequest,  options).toPromise();
    }

    /**
     * Logout user
     * @param param the request object
     */
    public logoutWithHttpInfo(param: AuthenticationApiLogoutRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseUnit>> {
        return this.api.logoutWithHttpInfo(param.logoutRequest,  options).toPromise();
    }

    /**
     * Logout user
     * @param param the request object
     */
    public logout(param: AuthenticationApiLogoutRequest, options?: ConfigurationOptions): Promise<ApiResponseUnit> {
        return this.api.logout(param.logoutRequest,  options).toPromise();
    }

    /**
     * Refresh access token
     * @param param the request object
     */
    public refreshTokenWithHttpInfo(param: AuthenticationApiRefreshTokenRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseTokenRefreshResponse>> {
        return this.api.refreshTokenWithHttpInfo(param.refreshTokenRequest,  options).toPromise();
    }

    /**
     * Refresh access token
     * @param param the request object
     */
    public refreshToken(param: AuthenticationApiRefreshTokenRequest, options?: ConfigurationOptions): Promise<ApiResponseTokenRefreshResponse> {
        return this.api.refreshToken(param.refreshTokenRequest,  options).toPromise();
    }

    /**
     * Register a new user
     * @param param the request object
     */
    public registerWithHttpInfo(param: AuthenticationApiRegisterRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseAuthResponse>> {
        return this.api.registerWithHttpInfo(param.registerRequest,  options).toPromise();
    }

    /**
     * Register a new user
     * @param param the request object
     */
    public register(param: AuthenticationApiRegisterRequest, options?: ConfigurationOptions): Promise<ApiResponseAuthResponse> {
        return this.api.register(param.registerRequest,  options).toPromise();
    }

    /**
     * Reset password with token
     * @param param the request object
     */
    public resetPasswordWithHttpInfo(param: AuthenticationApiResetPasswordRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseUnit>> {
        return this.api.resetPasswordWithHttpInfo(param.resetPasswordRequest,  options).toPromise();
    }

    /**
     * Reset password with token
     * @param param the request object
     */
    public resetPassword(param: AuthenticationApiResetPasswordRequest, options?: ConfigurationOptions): Promise<ApiResponseUnit> {
        return this.api.resetPassword(param.resetPasswordRequest,  options).toPromise();
    }

    /**
     * Update user profile
     * @param param the request object
     */
    public updateProfileWithHttpInfo(param: AuthenticationApiUpdateProfileRequest = {}, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseUserResponse>> {
        return this.api.updateProfileWithHttpInfo(param.name, param.phone, param.avatarUrl,  options).toPromise();
    }

    /**
     * Update user profile
     * @param param the request object
     */
    public updateProfile(param: AuthenticationApiUpdateProfileRequest = {}, options?: ConfigurationOptions): Promise<ApiResponseUserResponse> {
        return this.api.updateProfile(param.name, param.phone, param.avatarUrl,  options).toPromise();
    }

    /**
     * Verify email address
     * @param param the request object
     */
    public verifyEmailWithHttpInfo(param: AuthenticationApiVerifyEmailRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseUnit>> {
        return this.api.verifyEmailWithHttpInfo(param.verifyEmailRequest,  options).toPromise();
    }

    /**
     * Verify email address
     * @param param the request object
     */
    public verifyEmail(param: AuthenticationApiVerifyEmailRequest, options?: ConfigurationOptions): Promise<ApiResponseUnit> {
        return this.api.verifyEmail(param.verifyEmailRequest,  options).toPromise();
    }

}

import { ObservableBusinessesApi } from "./ObservableAPI";
import { BusinessesApiRequestFactory, BusinessesApiResponseProcessor} from "../apis/BusinessesApi";

export interface BusinessesApiCreateBusinessRequest {
    /**
     * 
     * @type CreateBusinessRequest
     * @memberof BusinessesApicreateBusiness
     */
    createBusinessRequest: CreateBusinessRequest
}

export interface BusinessesApiDeleteBusinessRequest {
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof BusinessesApideleteBusiness
     */
    businessId: string
}

export interface BusinessesApiGetBusinessRequest {
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof BusinessesApigetBusiness
     */
    businessId: string
}

export interface BusinessesApiGetBusinessBySlugRequest {
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof BusinessesApigetBusinessBySlug
     */
    slug: string
}

export interface BusinessesApiGetBusinessesByCategoryRequest {
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof BusinessesApigetBusinessesByCategory
     */
    categoryId: string
    /**
     * 
     * Defaults to: undefined
     * @type Pageable
     * @memberof BusinessesApigetBusinessesByCategory
     */
    pageable: Pageable
}

export interface BusinessesApiGetBusinessesByRegionRequest {
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof BusinessesApigetBusinessesByRegion
     */
    regionId: string
    /**
     * 
     * Defaults to: undefined
     * @type Pageable
     * @memberof BusinessesApigetBusinessesByRegion
     */
    pageable: Pageable
}

export interface BusinessesApiGetFeaturedBusinessesRequest {
    /**
     * 
     * Defaults to: undefined
     * @type Pageable
     * @memberof BusinessesApigetFeaturedBusinesses
     */
    pageable: Pageable
}

export interface BusinessesApiGetUserBusinessesRequest {
    /**
     * 
     * Defaults to: undefined
     * @type Pageable
     * @memberof BusinessesApigetUserBusinesses
     */
    pageable: Pageable
}

export interface BusinessesApiSearchBusinessesRequest {
    /**
     * 
     * Defaults to: undefined
     * @type Pageable
     * @memberof BusinessesApisearchBusinesses
     */
    pageable: Pageable
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof BusinessesApisearchBusinesses
     */
    query?: string
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof BusinessesApisearchBusinesses
     */
    regionId?: string
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof BusinessesApisearchBusinesses
     */
    categoryId?: string
}

export interface BusinessesApiUpdateBusinessRequest {
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof BusinessesApiupdateBusiness
     */
    businessId: string
    /**
     * 
     * @type UpdateBusinessRequest
     * @memberof BusinessesApiupdateBusiness
     */
    updateBusinessRequest: UpdateBusinessRequest
}

export interface BusinessesApiVerifyBusinessRequest {
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof BusinessesApiverifyBusiness
     */
    businessId: string
}

export class ObjectBusinessesApi {
    private api: ObservableBusinessesApi

    public constructor(configuration: Configuration, requestFactory?: BusinessesApiRequestFactory, responseProcessor?: BusinessesApiResponseProcessor) {
        this.api = new ObservableBusinessesApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Create a new business
     * @param param the request object
     */
    public createBusinessWithHttpInfo(param: BusinessesApiCreateBusinessRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseBusinessDto>> {
        return this.api.createBusinessWithHttpInfo(param.createBusinessRequest,  options).toPromise();
    }

    /**
     * Create a new business
     * @param param the request object
     */
    public createBusiness(param: BusinessesApiCreateBusinessRequest, options?: ConfigurationOptions): Promise<ApiResponseBusinessDto> {
        return this.api.createBusiness(param.createBusinessRequest,  options).toPromise();
    }

    /**
     * Delete business
     * @param param the request object
     */
    public deleteBusinessWithHttpInfo(param: BusinessesApiDeleteBusinessRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseUnit>> {
        return this.api.deleteBusinessWithHttpInfo(param.businessId,  options).toPromise();
    }

    /**
     * Delete business
     * @param param the request object
     */
    public deleteBusiness(param: BusinessesApiDeleteBusinessRequest, options?: ConfigurationOptions): Promise<ApiResponseUnit> {
        return this.api.deleteBusiness(param.businessId,  options).toPromise();
    }

    /**
     * Get business by ID
     * @param param the request object
     */
    public getBusinessWithHttpInfo(param: BusinessesApiGetBusinessRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseBusinessDto>> {
        return this.api.getBusinessWithHttpInfo(param.businessId,  options).toPromise();
    }

    /**
     * Get business by ID
     * @param param the request object
     */
    public getBusiness(param: BusinessesApiGetBusinessRequest, options?: ConfigurationOptions): Promise<ApiResponseBusinessDto> {
        return this.api.getBusiness(param.businessId,  options).toPromise();
    }

    /**
     * Get business by slug
     * @param param the request object
     */
    public getBusinessBySlugWithHttpInfo(param: BusinessesApiGetBusinessBySlugRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseBusinessDto>> {
        return this.api.getBusinessBySlugWithHttpInfo(param.slug,  options).toPromise();
    }

    /**
     * Get business by slug
     * @param param the request object
     */
    public getBusinessBySlug(param: BusinessesApiGetBusinessBySlugRequest, options?: ConfigurationOptions): Promise<ApiResponseBusinessDto> {
        return this.api.getBusinessBySlug(param.slug,  options).toPromise();
    }

    /**
     * Get businesses by category
     * @param param the request object
     */
    public getBusinessesByCategoryWithHttpInfo(param: BusinessesApiGetBusinessesByCategoryRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponsePageBusinessDto>> {
        return this.api.getBusinessesByCategoryWithHttpInfo(param.categoryId, param.pageable,  options).toPromise();
    }

    /**
     * Get businesses by category
     * @param param the request object
     */
    public getBusinessesByCategory(param: BusinessesApiGetBusinessesByCategoryRequest, options?: ConfigurationOptions): Promise<ApiResponsePageBusinessDto> {
        return this.api.getBusinessesByCategory(param.categoryId, param.pageable,  options).toPromise();
    }

    /**
     * Get businesses by region
     * @param param the request object
     */
    public getBusinessesByRegionWithHttpInfo(param: BusinessesApiGetBusinessesByRegionRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponsePageBusinessDto>> {
        return this.api.getBusinessesByRegionWithHttpInfo(param.regionId, param.pageable,  options).toPromise();
    }

    /**
     * Get businesses by region
     * @param param the request object
     */
    public getBusinessesByRegion(param: BusinessesApiGetBusinessesByRegionRequest, options?: ConfigurationOptions): Promise<ApiResponsePageBusinessDto> {
        return this.api.getBusinessesByRegion(param.regionId, param.pageable,  options).toPromise();
    }

    /**
     * Get featured businesses
     * @param param the request object
     */
    public getFeaturedBusinessesWithHttpInfo(param: BusinessesApiGetFeaturedBusinessesRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponsePageBusinessDto>> {
        return this.api.getFeaturedBusinessesWithHttpInfo(param.pageable,  options).toPromise();
    }

    /**
     * Get featured businesses
     * @param param the request object
     */
    public getFeaturedBusinesses(param: BusinessesApiGetFeaturedBusinessesRequest, options?: ConfigurationOptions): Promise<ApiResponsePageBusinessDto> {
        return this.api.getFeaturedBusinesses(param.pageable,  options).toPromise();
    }

    /**
     * Get current user\'s businesses
     * @param param the request object
     */
    public getUserBusinessesWithHttpInfo(param: BusinessesApiGetUserBusinessesRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponsePageBusinessDto>> {
        return this.api.getUserBusinessesWithHttpInfo(param.pageable,  options).toPromise();
    }

    /**
     * Get current user\'s businesses
     * @param param the request object
     */
    public getUserBusinesses(param: BusinessesApiGetUserBusinessesRequest, options?: ConfigurationOptions): Promise<ApiResponsePageBusinessDto> {
        return this.api.getUserBusinesses(param.pageable,  options).toPromise();
    }

    /**
     * Search businesses
     * @param param the request object
     */
    public searchBusinessesWithHttpInfo(param: BusinessesApiSearchBusinessesRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponsePageBusinessDto>> {
        return this.api.searchBusinessesWithHttpInfo(param.pageable, param.query, param.regionId, param.categoryId,  options).toPromise();
    }

    /**
     * Search businesses
     * @param param the request object
     */
    public searchBusinesses(param: BusinessesApiSearchBusinessesRequest, options?: ConfigurationOptions): Promise<ApiResponsePageBusinessDto> {
        return this.api.searchBusinesses(param.pageable, param.query, param.regionId, param.categoryId,  options).toPromise();
    }

    /**
     * Update business
     * @param param the request object
     */
    public updateBusinessWithHttpInfo(param: BusinessesApiUpdateBusinessRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseBusinessDto>> {
        return this.api.updateBusinessWithHttpInfo(param.businessId, param.updateBusinessRequest,  options).toPromise();
    }

    /**
     * Update business
     * @param param the request object
     */
    public updateBusiness(param: BusinessesApiUpdateBusinessRequest, options?: ConfigurationOptions): Promise<ApiResponseBusinessDto> {
        return this.api.updateBusiness(param.businessId, param.updateBusinessRequest,  options).toPromise();
    }

    /**
     * Verify business (admin only)
     * @param param the request object
     */
    public verifyBusinessWithHttpInfo(param: BusinessesApiVerifyBusinessRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseBusinessDto>> {
        return this.api.verifyBusinessWithHttpInfo(param.businessId,  options).toPromise();
    }

    /**
     * Verify business (admin only)
     * @param param the request object
     */
    public verifyBusiness(param: BusinessesApiVerifyBusinessRequest, options?: ConfigurationOptions): Promise<ApiResponseBusinessDto> {
        return this.api.verifyBusiness(param.businessId,  options).toPromise();
    }

}

import { ObservableCategoriesApi } from "./ObservableAPI";
import { CategoriesApiRequestFactory, CategoriesApiResponseProcessor} from "../apis/CategoriesApi";

export interface CategoriesApiCreateCategoryRequest {
    /**
     * 
     * @type CreateCategoryRequest
     * @memberof CategoriesApicreateCategory
     */
    createCategoryRequest: CreateCategoryRequest
}

export interface CategoriesApiDeleteCategoryRequest {
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof CategoriesApideleteCategory
     */
    categoryId: string
}

export interface CategoriesApiGetAllCategoriesRequest {
}

export interface CategoriesApiGetCategoryRequest {
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof CategoriesApigetCategory
     */
    categoryId: string
}

export interface CategoriesApiUpdateCategoryRequest {
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof CategoriesApiupdateCategory
     */
    categoryId: string
    /**
     * 
     * @type UpdateCategoryRequest
     * @memberof CategoriesApiupdateCategory
     */
    updateCategoryRequest: UpdateCategoryRequest
}

export class ObjectCategoriesApi {
    private api: ObservableCategoriesApi

    public constructor(configuration: Configuration, requestFactory?: CategoriesApiRequestFactory, responseProcessor?: CategoriesApiResponseProcessor) {
        this.api = new ObservableCategoriesApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Create new category (admin only)
     * @param param the request object
     */
    public createCategoryWithHttpInfo(param: CategoriesApiCreateCategoryRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseCategoryResponse>> {
        return this.api.createCategoryWithHttpInfo(param.createCategoryRequest,  options).toPromise();
    }

    /**
     * Create new category (admin only)
     * @param param the request object
     */
    public createCategory(param: CategoriesApiCreateCategoryRequest, options?: ConfigurationOptions): Promise<ApiResponseCategoryResponse> {
        return this.api.createCategory(param.createCategoryRequest,  options).toPromise();
    }

    /**
     * Delete category (admin only)
     * @param param the request object
     */
    public deleteCategoryWithHttpInfo(param: CategoriesApiDeleteCategoryRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseUnit>> {
        return this.api.deleteCategoryWithHttpInfo(param.categoryId,  options).toPromise();
    }

    /**
     * Delete category (admin only)
     * @param param the request object
     */
    public deleteCategory(param: CategoriesApiDeleteCategoryRequest, options?: ConfigurationOptions): Promise<ApiResponseUnit> {
        return this.api.deleteCategory(param.categoryId,  options).toPromise();
    }

    /**
     * Get all active categories
     * @param param the request object
     */
    public getAllCategoriesWithHttpInfo(param: CategoriesApiGetAllCategoriesRequest = {}, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseListCategoryResponse>> {
        return this.api.getAllCategoriesWithHttpInfo( options).toPromise();
    }

    /**
     * Get all active categories
     * @param param the request object
     */
    public getAllCategories(param: CategoriesApiGetAllCategoriesRequest = {}, options?: ConfigurationOptions): Promise<ApiResponseListCategoryResponse> {
        return this.api.getAllCategories( options).toPromise();
    }

    /**
     * Get category by ID
     * @param param the request object
     */
    public getCategoryWithHttpInfo(param: CategoriesApiGetCategoryRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseCategoryResponse>> {
        return this.api.getCategoryWithHttpInfo(param.categoryId,  options).toPromise();
    }

    /**
     * Get category by ID
     * @param param the request object
     */
    public getCategory(param: CategoriesApiGetCategoryRequest, options?: ConfigurationOptions): Promise<ApiResponseCategoryResponse> {
        return this.api.getCategory(param.categoryId,  options).toPromise();
    }

    /**
     * Update category (admin only)
     * @param param the request object
     */
    public updateCategoryWithHttpInfo(param: CategoriesApiUpdateCategoryRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseCategoryResponse>> {
        return this.api.updateCategoryWithHttpInfo(param.categoryId, param.updateCategoryRequest,  options).toPromise();
    }

    /**
     * Update category (admin only)
     * @param param the request object
     */
    public updateCategory(param: CategoriesApiUpdateCategoryRequest, options?: ConfigurationOptions): Promise<ApiResponseCategoryResponse> {
        return this.api.updateCategory(param.categoryId, param.updateCategoryRequest,  options).toPromise();
    }

}

import { ObservableHealthApi } from "./ObservableAPI";
import { HealthApiRequestFactory, HealthApiResponseProcessor} from "../apis/HealthApi";

export interface HealthApiHealthCheckRequest {
}

export class ObjectHealthApi {
    private api: ObservableHealthApi

    public constructor(configuration: Configuration, requestFactory?: HealthApiRequestFactory, responseProcessor?: HealthApiResponseProcessor) {
        this.api = new ObservableHealthApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Check if the API is running
     * Health check
     * @param param the request object
     */
    public healthCheckWithHttpInfo(param: HealthApiHealthCheckRequest = {}, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseHealthResponse>> {
        return this.api.healthCheckWithHttpInfo( options).toPromise();
    }

    /**
     * Check if the API is running
     * Health check
     * @param param the request object
     */
    public healthCheck(param: HealthApiHealthCheckRequest = {}, options?: ConfigurationOptions): Promise<ApiResponseHealthResponse> {
        return this.api.healthCheck( options).toPromise();
    }

}

import { ObservableRegionsApi } from "./ObservableAPI";
import { RegionsApiRequestFactory, RegionsApiResponseProcessor} from "../apis/RegionsApi";

export interface RegionsApiCreateRegionRequest {
    /**
     * 
     * @type CreateRegionRequest
     * @memberof RegionsApicreateRegion
     */
    createRegionRequest: CreateRegionRequest
}

export interface RegionsApiDeleteRegionRequest {
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof RegionsApideleteRegion
     */
    regionId: string
}

export interface RegionsApiGetAllRegionsRequest {
}

export interface RegionsApiGetRegionRequest {
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof RegionsApigetRegion
     */
    regionId: string
}

export interface RegionsApiGetRegionByCodeRequest {
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof RegionsApigetRegionByCode
     */
    code: string
}

export interface RegionsApiSearchRegionsRequest {
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof RegionsApisearchRegions
     */
    search: string
}

export interface RegionsApiUpdateRegionRequest {
    /**
     * 
     * Defaults to: undefined
     * @type string
     * @memberof RegionsApiupdateRegion
     */
    regionId: string
    /**
     * 
     * @type UpdateRegionRequest
     * @memberof RegionsApiupdateRegion
     */
    updateRegionRequest: UpdateRegionRequest
}

export class ObjectRegionsApi {
    private api: ObservableRegionsApi

    public constructor(configuration: Configuration, requestFactory?: RegionsApiRequestFactory, responseProcessor?: RegionsApiResponseProcessor) {
        this.api = new ObservableRegionsApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Create new region (admin only)
     * @param param the request object
     */
    public createRegionWithHttpInfo(param: RegionsApiCreateRegionRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseRegionResponse>> {
        return this.api.createRegionWithHttpInfo(param.createRegionRequest,  options).toPromise();
    }

    /**
     * Create new region (admin only)
     * @param param the request object
     */
    public createRegion(param: RegionsApiCreateRegionRequest, options?: ConfigurationOptions): Promise<ApiResponseRegionResponse> {
        return this.api.createRegion(param.createRegionRequest,  options).toPromise();
    }

    /**
     * Delete region (admin only)
     * @param param the request object
     */
    public deleteRegionWithHttpInfo(param: RegionsApiDeleteRegionRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseUnit>> {
        return this.api.deleteRegionWithHttpInfo(param.regionId,  options).toPromise();
    }

    /**
     * Delete region (admin only)
     * @param param the request object
     */
    public deleteRegion(param: RegionsApiDeleteRegionRequest, options?: ConfigurationOptions): Promise<ApiResponseUnit> {
        return this.api.deleteRegion(param.regionId,  options).toPromise();
    }

    /**
     * Get all active regions
     * @param param the request object
     */
    public getAllRegionsWithHttpInfo(param: RegionsApiGetAllRegionsRequest = {}, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseListRegionResponse>> {
        return this.api.getAllRegionsWithHttpInfo( options).toPromise();
    }

    /**
     * Get all active regions
     * @param param the request object
     */
    public getAllRegions(param: RegionsApiGetAllRegionsRequest = {}, options?: ConfigurationOptions): Promise<ApiResponseListRegionResponse> {
        return this.api.getAllRegions( options).toPromise();
    }

    /**
     * Get region by ID
     * @param param the request object
     */
    public getRegionWithHttpInfo(param: RegionsApiGetRegionRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseRegionResponse>> {
        return this.api.getRegionWithHttpInfo(param.regionId,  options).toPromise();
    }

    /**
     * Get region by ID
     * @param param the request object
     */
    public getRegion(param: RegionsApiGetRegionRequest, options?: ConfigurationOptions): Promise<ApiResponseRegionResponse> {
        return this.api.getRegion(param.regionId,  options).toPromise();
    }

    /**
     * Get region by code
     * @param param the request object
     */
    public getRegionByCodeWithHttpInfo(param: RegionsApiGetRegionByCodeRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseRegionResponse>> {
        return this.api.getRegionByCodeWithHttpInfo(param.code,  options).toPromise();
    }

    /**
     * Get region by code
     * @param param the request object
     */
    public getRegionByCode(param: RegionsApiGetRegionByCodeRequest, options?: ConfigurationOptions): Promise<ApiResponseRegionResponse> {
        return this.api.getRegionByCode(param.code,  options).toPromise();
    }

    /**
     * Search regions
     * @param param the request object
     */
    public searchRegionsWithHttpInfo(param: RegionsApiSearchRegionsRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseListRegionResponse>> {
        return this.api.searchRegionsWithHttpInfo(param.search,  options).toPromise();
    }

    /**
     * Search regions
     * @param param the request object
     */
    public searchRegions(param: RegionsApiSearchRegionsRequest, options?: ConfigurationOptions): Promise<ApiResponseListRegionResponse> {
        return this.api.searchRegions(param.search,  options).toPromise();
    }

    /**
     * Update region (admin only)
     * @param param the request object
     */
    public updateRegionWithHttpInfo(param: RegionsApiUpdateRegionRequest, options?: ConfigurationOptions): Promise<HttpInfo<ApiResponseRegionResponse>> {
        return this.api.updateRegionWithHttpInfo(param.regionId, param.updateRegionRequest,  options).toPromise();
    }

    /**
     * Update region (admin only)
     * @param param the request object
     */
    public updateRegion(param: RegionsApiUpdateRegionRequest, options?: ConfigurationOptions): Promise<ApiResponseRegionResponse> {
        return this.api.updateRegion(param.regionId, param.updateRegionRequest,  options).toPromise();
    }

}
