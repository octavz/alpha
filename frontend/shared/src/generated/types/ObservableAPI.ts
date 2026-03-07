import { ResponseContext, RequestContext, HttpFile, HttpInfo } from '../http/http';
import { Configuration, ConfigurationOptions, mergeConfiguration } from '../configuration'
import type { Middleware } from '../middleware';
import { Observable, of, from } from '../rxjsStub';
import {mergeMap, map} from  '../rxjsStub';
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

import { AppointmentsApiRequestFactory, AppointmentsApiResponseProcessor} from "../apis/AppointmentsApi";
export class ObservableAppointmentsApi {
    private requestFactory: AppointmentsApiRequestFactory;
    private responseProcessor: AppointmentsApiResponseProcessor;
    private configuration: Configuration;

    public constructor(
        configuration: Configuration,
        requestFactory?: AppointmentsApiRequestFactory,
        responseProcessor?: AppointmentsApiResponseProcessor
    ) {
        this.configuration = configuration;
        this.requestFactory = requestFactory || new AppointmentsApiRequestFactory(configuration);
        this.responseProcessor = responseProcessor || new AppointmentsApiResponseProcessor();
    }

    /**
     * Cancel appointment
     * @param appointmentId
     */
    public cancelAppointmentWithHttpInfo(appointmentId: string, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseAppointmentDto>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.cancelAppointment(appointmentId, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.cancelAppointmentWithHttpInfo(rsp)));
            }));
    }

    /**
     * Cancel appointment
     * @param appointmentId
     */
    public cancelAppointment(appointmentId: string, _options?: ConfigurationOptions): Observable<ApiResponseAppointmentDto> {
        return this.cancelAppointmentWithHttpInfo(appointmentId, _options).pipe(map((apiResponse: HttpInfo<ApiResponseAppointmentDto>) => apiResponse.data));
    }

    /**
     * Create a new appointment
     * @param createAppointmentRequest
     */
    public createAppointmentWithHttpInfo(createAppointmentRequest: CreateAppointmentRequest, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseAppointmentDto>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.createAppointment(createAppointmentRequest, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.createAppointmentWithHttpInfo(rsp)));
            }));
    }

    /**
     * Create a new appointment
     * @param createAppointmentRequest
     */
    public createAppointment(createAppointmentRequest: CreateAppointmentRequest, _options?: ConfigurationOptions): Observable<ApiResponseAppointmentDto> {
        return this.createAppointmentWithHttpInfo(createAppointmentRequest, _options).pipe(map((apiResponse: HttpInfo<ApiResponseAppointmentDto>) => apiResponse.data));
    }

    /**
     * Get appointment by ID
     * @param appointmentId
     */
    public getAppointmentWithHttpInfo(appointmentId: string, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseAppointmentDto>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.getAppointment(appointmentId, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getAppointmentWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get appointment by ID
     * @param appointmentId
     */
    public getAppointment(appointmentId: string, _options?: ConfigurationOptions): Observable<ApiResponseAppointmentDto> {
        return this.getAppointmentWithHttpInfo(appointmentId, _options).pipe(map((apiResponse: HttpInfo<ApiResponseAppointmentDto>) => apiResponse.data));
    }

    /**
     * Get business appointments
     * @param businessId
     * @param pageable
     */
    public getBusinessAppointmentsWithHttpInfo(businessId: string, pageable: Pageable, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponsePageAppointmentDto>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.getBusinessAppointments(businessId, pageable, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getBusinessAppointmentsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get business appointments
     * @param businessId
     * @param pageable
     */
    public getBusinessAppointments(businessId: string, pageable: Pageable, _options?: ConfigurationOptions): Observable<ApiResponsePageAppointmentDto> {
        return this.getBusinessAppointmentsWithHttpInfo(businessId, pageable, _options).pipe(map((apiResponse: HttpInfo<ApiResponsePageAppointmentDto>) => apiResponse.data));
    }

    /**
     * Get business availability
     * @param businessId
     * @param date
     */
    public getBusinessAvailabilityWithHttpInfo(businessId: string, date: string, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseListString>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.getBusinessAvailability(businessId, date, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getBusinessAvailabilityWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get business availability
     * @param businessId
     * @param date
     */
    public getBusinessAvailability(businessId: string, date: string, _options?: ConfigurationOptions): Observable<ApiResponseListString> {
        return this.getBusinessAvailabilityWithHttpInfo(businessId, date, _options).pipe(map((apiResponse: HttpInfo<ApiResponseListString>) => apiResponse.data));
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
    public searchAppointmentsWithHttpInfo(pageable: Pageable, businessId?: string, customerId?: string, status?: 'PENDING' | 'CONFIRMED' | 'COMPLETED' | 'CANCELLED' | 'NO_SHOW', startDate?: string, endDate?: string, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponsePageAppointmentDto>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.searchAppointments(pageable, businessId, customerId, status, startDate, endDate, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.searchAppointmentsWithHttpInfo(rsp)));
            }));
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
    public searchAppointments(pageable: Pageable, businessId?: string, customerId?: string, status?: 'PENDING' | 'CONFIRMED' | 'COMPLETED' | 'CANCELLED' | 'NO_SHOW', startDate?: string, endDate?: string, _options?: ConfigurationOptions): Observable<ApiResponsePageAppointmentDto> {
        return this.searchAppointmentsWithHttpInfo(pageable, businessId, customerId, status, startDate, endDate, _options).pipe(map((apiResponse: HttpInfo<ApiResponsePageAppointmentDto>) => apiResponse.data));
    }

    /**
     * Update appointment
     * @param appointmentId
     * @param updateAppointmentRequest
     */
    public updateAppointmentWithHttpInfo(appointmentId: string, updateAppointmentRequest: UpdateAppointmentRequest, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseAppointmentDto>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.updateAppointment(appointmentId, updateAppointmentRequest, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.updateAppointmentWithHttpInfo(rsp)));
            }));
    }

    /**
     * Update appointment
     * @param appointmentId
     * @param updateAppointmentRequest
     */
    public updateAppointment(appointmentId: string, updateAppointmentRequest: UpdateAppointmentRequest, _options?: ConfigurationOptions): Observable<ApiResponseAppointmentDto> {
        return this.updateAppointmentWithHttpInfo(appointmentId, updateAppointmentRequest, _options).pipe(map((apiResponse: HttpInfo<ApiResponseAppointmentDto>) => apiResponse.data));
    }

}

import { AuthenticationApiRequestFactory, AuthenticationApiResponseProcessor} from "../apis/AuthenticationApi";
export class ObservableAuthenticationApi {
    private requestFactory: AuthenticationApiRequestFactory;
    private responseProcessor: AuthenticationApiResponseProcessor;
    private configuration: Configuration;

    public constructor(
        configuration: Configuration,
        requestFactory?: AuthenticationApiRequestFactory,
        responseProcessor?: AuthenticationApiResponseProcessor
    ) {
        this.configuration = configuration;
        this.requestFactory = requestFactory || new AuthenticationApiRequestFactory(configuration);
        this.responseProcessor = responseProcessor || new AuthenticationApiResponseProcessor();
    }

    /**
     * Change password
     * @param currentPassword
     * @param newPassword
     */
    public changePasswordWithHttpInfo(currentPassword: string, newPassword: string, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseUnit>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.changePassword(currentPassword, newPassword, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.changePasswordWithHttpInfo(rsp)));
            }));
    }

    /**
     * Change password
     * @param currentPassword
     * @param newPassword
     */
    public changePassword(currentPassword: string, newPassword: string, _options?: ConfigurationOptions): Observable<ApiResponseUnit> {
        return this.changePasswordWithHttpInfo(currentPassword, newPassword, _options).pipe(map((apiResponse: HttpInfo<ApiResponseUnit>) => apiResponse.data));
    }

    /**
     * Request password reset
     * @param forgotPasswordRequest
     */
    public forgotPasswordWithHttpInfo(forgotPasswordRequest: ForgotPasswordRequest, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseUnit>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.forgotPassword(forgotPasswordRequest, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.forgotPasswordWithHttpInfo(rsp)));
            }));
    }

    /**
     * Request password reset
     * @param forgotPasswordRequest
     */
    public forgotPassword(forgotPasswordRequest: ForgotPasswordRequest, _options?: ConfigurationOptions): Observable<ApiResponseUnit> {
        return this.forgotPasswordWithHttpInfo(forgotPasswordRequest, _options).pipe(map((apiResponse: HttpInfo<ApiResponseUnit>) => apiResponse.data));
    }

    /**
     * Get current user profile
     */
    public getCurrentUserWithHttpInfo(_options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseUserResponse>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.getCurrentUser(_config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getCurrentUserWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get current user profile
     */
    public getCurrentUser(_options?: ConfigurationOptions): Observable<ApiResponseUserResponse> {
        return this.getCurrentUserWithHttpInfo(_options).pipe(map((apiResponse: HttpInfo<ApiResponseUserResponse>) => apiResponse.data));
    }

    /**
     * Login user
     * @param loginRequest
     */
    public loginWithHttpInfo(loginRequest: LoginRequest, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseAuthResponse>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.login(loginRequest, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.loginWithHttpInfo(rsp)));
            }));
    }

    /**
     * Login user
     * @param loginRequest
     */
    public login(loginRequest: LoginRequest, _options?: ConfigurationOptions): Observable<ApiResponseAuthResponse> {
        return this.loginWithHttpInfo(loginRequest, _options).pipe(map((apiResponse: HttpInfo<ApiResponseAuthResponse>) => apiResponse.data));
    }

    /**
     * Logout user
     * @param logoutRequest
     */
    public logoutWithHttpInfo(logoutRequest: LogoutRequest, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseUnit>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.logout(logoutRequest, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.logoutWithHttpInfo(rsp)));
            }));
    }

    /**
     * Logout user
     * @param logoutRequest
     */
    public logout(logoutRequest: LogoutRequest, _options?: ConfigurationOptions): Observable<ApiResponseUnit> {
        return this.logoutWithHttpInfo(logoutRequest, _options).pipe(map((apiResponse: HttpInfo<ApiResponseUnit>) => apiResponse.data));
    }

    /**
     * Refresh access token
     * @param refreshTokenRequest
     */
    public refreshTokenWithHttpInfo(refreshTokenRequest: RefreshTokenRequest, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseTokenRefreshResponse>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.refreshToken(refreshTokenRequest, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.refreshTokenWithHttpInfo(rsp)));
            }));
    }

    /**
     * Refresh access token
     * @param refreshTokenRequest
     */
    public refreshToken(refreshTokenRequest: RefreshTokenRequest, _options?: ConfigurationOptions): Observable<ApiResponseTokenRefreshResponse> {
        return this.refreshTokenWithHttpInfo(refreshTokenRequest, _options).pipe(map((apiResponse: HttpInfo<ApiResponseTokenRefreshResponse>) => apiResponse.data));
    }

    /**
     * Register a new user
     * @param registerRequest
     */
    public registerWithHttpInfo(registerRequest: RegisterRequest, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseAuthResponse>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.register(registerRequest, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.registerWithHttpInfo(rsp)));
            }));
    }

    /**
     * Register a new user
     * @param registerRequest
     */
    public register(registerRequest: RegisterRequest, _options?: ConfigurationOptions): Observable<ApiResponseAuthResponse> {
        return this.registerWithHttpInfo(registerRequest, _options).pipe(map((apiResponse: HttpInfo<ApiResponseAuthResponse>) => apiResponse.data));
    }

    /**
     * Reset password with token
     * @param resetPasswordRequest
     */
    public resetPasswordWithHttpInfo(resetPasswordRequest: ResetPasswordRequest, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseUnit>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.resetPassword(resetPasswordRequest, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.resetPasswordWithHttpInfo(rsp)));
            }));
    }

    /**
     * Reset password with token
     * @param resetPasswordRequest
     */
    public resetPassword(resetPasswordRequest: ResetPasswordRequest, _options?: ConfigurationOptions): Observable<ApiResponseUnit> {
        return this.resetPasswordWithHttpInfo(resetPasswordRequest, _options).pipe(map((apiResponse: HttpInfo<ApiResponseUnit>) => apiResponse.data));
    }

    /**
     * Update user profile
     * @param [name]
     * @param [phone]
     * @param [avatarUrl]
     */
    public updateProfileWithHttpInfo(name?: string, phone?: string, avatarUrl?: string, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseUserResponse>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.updateProfile(name, phone, avatarUrl, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.updateProfileWithHttpInfo(rsp)));
            }));
    }

    /**
     * Update user profile
     * @param [name]
     * @param [phone]
     * @param [avatarUrl]
     */
    public updateProfile(name?: string, phone?: string, avatarUrl?: string, _options?: ConfigurationOptions): Observable<ApiResponseUserResponse> {
        return this.updateProfileWithHttpInfo(name, phone, avatarUrl, _options).pipe(map((apiResponse: HttpInfo<ApiResponseUserResponse>) => apiResponse.data));
    }

    /**
     * Verify email address
     * @param verifyEmailRequest
     */
    public verifyEmailWithHttpInfo(verifyEmailRequest: VerifyEmailRequest, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseUnit>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.verifyEmail(verifyEmailRequest, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.verifyEmailWithHttpInfo(rsp)));
            }));
    }

    /**
     * Verify email address
     * @param verifyEmailRequest
     */
    public verifyEmail(verifyEmailRequest: VerifyEmailRequest, _options?: ConfigurationOptions): Observable<ApiResponseUnit> {
        return this.verifyEmailWithHttpInfo(verifyEmailRequest, _options).pipe(map((apiResponse: HttpInfo<ApiResponseUnit>) => apiResponse.data));
    }

}

import { BusinessesApiRequestFactory, BusinessesApiResponseProcessor} from "../apis/BusinessesApi";
export class ObservableBusinessesApi {
    private requestFactory: BusinessesApiRequestFactory;
    private responseProcessor: BusinessesApiResponseProcessor;
    private configuration: Configuration;

    public constructor(
        configuration: Configuration,
        requestFactory?: BusinessesApiRequestFactory,
        responseProcessor?: BusinessesApiResponseProcessor
    ) {
        this.configuration = configuration;
        this.requestFactory = requestFactory || new BusinessesApiRequestFactory(configuration);
        this.responseProcessor = responseProcessor || new BusinessesApiResponseProcessor();
    }

    /**
     * Create a new business
     * @param createBusinessRequest
     */
    public createBusinessWithHttpInfo(createBusinessRequest: CreateBusinessRequest, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseBusinessDto>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.createBusiness(createBusinessRequest, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.createBusinessWithHttpInfo(rsp)));
            }));
    }

    /**
     * Create a new business
     * @param createBusinessRequest
     */
    public createBusiness(createBusinessRequest: CreateBusinessRequest, _options?: ConfigurationOptions): Observable<ApiResponseBusinessDto> {
        return this.createBusinessWithHttpInfo(createBusinessRequest, _options).pipe(map((apiResponse: HttpInfo<ApiResponseBusinessDto>) => apiResponse.data));
    }

    /**
     * Delete business
     * @param businessId
     */
    public deleteBusinessWithHttpInfo(businessId: string, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseUnit>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.deleteBusiness(businessId, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.deleteBusinessWithHttpInfo(rsp)));
            }));
    }

    /**
     * Delete business
     * @param businessId
     */
    public deleteBusiness(businessId: string, _options?: ConfigurationOptions): Observable<ApiResponseUnit> {
        return this.deleteBusinessWithHttpInfo(businessId, _options).pipe(map((apiResponse: HttpInfo<ApiResponseUnit>) => apiResponse.data));
    }

    /**
     * Get business by ID
     * @param businessId
     */
    public getBusinessWithHttpInfo(businessId: string, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseBusinessDto>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.getBusiness(businessId, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getBusinessWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get business by ID
     * @param businessId
     */
    public getBusiness(businessId: string, _options?: ConfigurationOptions): Observable<ApiResponseBusinessDto> {
        return this.getBusinessWithHttpInfo(businessId, _options).pipe(map((apiResponse: HttpInfo<ApiResponseBusinessDto>) => apiResponse.data));
    }

    /**
     * Get business by slug
     * @param slug
     */
    public getBusinessBySlugWithHttpInfo(slug: string, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseBusinessDto>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.getBusinessBySlug(slug, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getBusinessBySlugWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get business by slug
     * @param slug
     */
    public getBusinessBySlug(slug: string, _options?: ConfigurationOptions): Observable<ApiResponseBusinessDto> {
        return this.getBusinessBySlugWithHttpInfo(slug, _options).pipe(map((apiResponse: HttpInfo<ApiResponseBusinessDto>) => apiResponse.data));
    }

    /**
     * Get businesses by category
     * @param categoryId
     * @param pageable
     */
    public getBusinessesByCategoryWithHttpInfo(categoryId: string, pageable: Pageable, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponsePageBusinessDto>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.getBusinessesByCategory(categoryId, pageable, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getBusinessesByCategoryWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get businesses by category
     * @param categoryId
     * @param pageable
     */
    public getBusinessesByCategory(categoryId: string, pageable: Pageable, _options?: ConfigurationOptions): Observable<ApiResponsePageBusinessDto> {
        return this.getBusinessesByCategoryWithHttpInfo(categoryId, pageable, _options).pipe(map((apiResponse: HttpInfo<ApiResponsePageBusinessDto>) => apiResponse.data));
    }

    /**
     * Get businesses by region
     * @param regionId
     * @param pageable
     */
    public getBusinessesByRegionWithHttpInfo(regionId: string, pageable: Pageable, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponsePageBusinessDto>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.getBusinessesByRegion(regionId, pageable, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getBusinessesByRegionWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get businesses by region
     * @param regionId
     * @param pageable
     */
    public getBusinessesByRegion(regionId: string, pageable: Pageable, _options?: ConfigurationOptions): Observable<ApiResponsePageBusinessDto> {
        return this.getBusinessesByRegionWithHttpInfo(regionId, pageable, _options).pipe(map((apiResponse: HttpInfo<ApiResponsePageBusinessDto>) => apiResponse.data));
    }

    /**
     * Get featured businesses
     * @param pageable
     */
    public getFeaturedBusinessesWithHttpInfo(pageable: Pageable, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponsePageBusinessDto>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.getFeaturedBusinesses(pageable, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getFeaturedBusinessesWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get featured businesses
     * @param pageable
     */
    public getFeaturedBusinesses(pageable: Pageable, _options?: ConfigurationOptions): Observable<ApiResponsePageBusinessDto> {
        return this.getFeaturedBusinessesWithHttpInfo(pageable, _options).pipe(map((apiResponse: HttpInfo<ApiResponsePageBusinessDto>) => apiResponse.data));
    }

    /**
     * Get current user\'s businesses
     * @param pageable
     */
    public getUserBusinessesWithHttpInfo(pageable: Pageable, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponsePageBusinessDto>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.getUserBusinesses(pageable, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getUserBusinessesWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get current user\'s businesses
     * @param pageable
     */
    public getUserBusinesses(pageable: Pageable, _options?: ConfigurationOptions): Observable<ApiResponsePageBusinessDto> {
        return this.getUserBusinessesWithHttpInfo(pageable, _options).pipe(map((apiResponse: HttpInfo<ApiResponsePageBusinessDto>) => apiResponse.data));
    }

    /**
     * Search businesses
     * @param pageable
     * @param [query]
     * @param [regionId]
     * @param [categoryId]
     */
    public searchBusinessesWithHttpInfo(pageable: Pageable, query?: string, regionId?: string, categoryId?: string, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponsePageBusinessDto>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.searchBusinesses(pageable, query, regionId, categoryId, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.searchBusinessesWithHttpInfo(rsp)));
            }));
    }

    /**
     * Search businesses
     * @param pageable
     * @param [query]
     * @param [regionId]
     * @param [categoryId]
     */
    public searchBusinesses(pageable: Pageable, query?: string, regionId?: string, categoryId?: string, _options?: ConfigurationOptions): Observable<ApiResponsePageBusinessDto> {
        return this.searchBusinessesWithHttpInfo(pageable, query, regionId, categoryId, _options).pipe(map((apiResponse: HttpInfo<ApiResponsePageBusinessDto>) => apiResponse.data));
    }

    /**
     * Update business
     * @param businessId
     * @param updateBusinessRequest
     */
    public updateBusinessWithHttpInfo(businessId: string, updateBusinessRequest: UpdateBusinessRequest, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseBusinessDto>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.updateBusiness(businessId, updateBusinessRequest, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.updateBusinessWithHttpInfo(rsp)));
            }));
    }

    /**
     * Update business
     * @param businessId
     * @param updateBusinessRequest
     */
    public updateBusiness(businessId: string, updateBusinessRequest: UpdateBusinessRequest, _options?: ConfigurationOptions): Observable<ApiResponseBusinessDto> {
        return this.updateBusinessWithHttpInfo(businessId, updateBusinessRequest, _options).pipe(map((apiResponse: HttpInfo<ApiResponseBusinessDto>) => apiResponse.data));
    }

    /**
     * Verify business (admin only)
     * @param businessId
     */
    public verifyBusinessWithHttpInfo(businessId: string, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseBusinessDto>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.verifyBusiness(businessId, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.verifyBusinessWithHttpInfo(rsp)));
            }));
    }

    /**
     * Verify business (admin only)
     * @param businessId
     */
    public verifyBusiness(businessId: string, _options?: ConfigurationOptions): Observable<ApiResponseBusinessDto> {
        return this.verifyBusinessWithHttpInfo(businessId, _options).pipe(map((apiResponse: HttpInfo<ApiResponseBusinessDto>) => apiResponse.data));
    }

}

import { CategoriesApiRequestFactory, CategoriesApiResponseProcessor} from "../apis/CategoriesApi";
export class ObservableCategoriesApi {
    private requestFactory: CategoriesApiRequestFactory;
    private responseProcessor: CategoriesApiResponseProcessor;
    private configuration: Configuration;

    public constructor(
        configuration: Configuration,
        requestFactory?: CategoriesApiRequestFactory,
        responseProcessor?: CategoriesApiResponseProcessor
    ) {
        this.configuration = configuration;
        this.requestFactory = requestFactory || new CategoriesApiRequestFactory(configuration);
        this.responseProcessor = responseProcessor || new CategoriesApiResponseProcessor();
    }

    /**
     * Create new category (admin only)
     * @param createCategoryRequest
     */
    public createCategoryWithHttpInfo(createCategoryRequest: CreateCategoryRequest, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseCategoryResponse>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.createCategory(createCategoryRequest, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.createCategoryWithHttpInfo(rsp)));
            }));
    }

    /**
     * Create new category (admin only)
     * @param createCategoryRequest
     */
    public createCategory(createCategoryRequest: CreateCategoryRequest, _options?: ConfigurationOptions): Observable<ApiResponseCategoryResponse> {
        return this.createCategoryWithHttpInfo(createCategoryRequest, _options).pipe(map((apiResponse: HttpInfo<ApiResponseCategoryResponse>) => apiResponse.data));
    }

    /**
     * Delete category (admin only)
     * @param categoryId
     */
    public deleteCategoryWithHttpInfo(categoryId: string, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseUnit>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.deleteCategory(categoryId, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.deleteCategoryWithHttpInfo(rsp)));
            }));
    }

    /**
     * Delete category (admin only)
     * @param categoryId
     */
    public deleteCategory(categoryId: string, _options?: ConfigurationOptions): Observable<ApiResponseUnit> {
        return this.deleteCategoryWithHttpInfo(categoryId, _options).pipe(map((apiResponse: HttpInfo<ApiResponseUnit>) => apiResponse.data));
    }

    /**
     * Get all active categories
     */
    public getAllCategoriesWithHttpInfo(_options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseListCategoryResponse>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.getAllCategories(_config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getAllCategoriesWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get all active categories
     */
    public getAllCategories(_options?: ConfigurationOptions): Observable<ApiResponseListCategoryResponse> {
        return this.getAllCategoriesWithHttpInfo(_options).pipe(map((apiResponse: HttpInfo<ApiResponseListCategoryResponse>) => apiResponse.data));
    }

    /**
     * Get category by ID
     * @param categoryId
     */
    public getCategoryWithHttpInfo(categoryId: string, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseCategoryResponse>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.getCategory(categoryId, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getCategoryWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get category by ID
     * @param categoryId
     */
    public getCategory(categoryId: string, _options?: ConfigurationOptions): Observable<ApiResponseCategoryResponse> {
        return this.getCategoryWithHttpInfo(categoryId, _options).pipe(map((apiResponse: HttpInfo<ApiResponseCategoryResponse>) => apiResponse.data));
    }

    /**
     * Update category (admin only)
     * @param categoryId
     * @param updateCategoryRequest
     */
    public updateCategoryWithHttpInfo(categoryId: string, updateCategoryRequest: UpdateCategoryRequest, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseCategoryResponse>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.updateCategory(categoryId, updateCategoryRequest, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.updateCategoryWithHttpInfo(rsp)));
            }));
    }

    /**
     * Update category (admin only)
     * @param categoryId
     * @param updateCategoryRequest
     */
    public updateCategory(categoryId: string, updateCategoryRequest: UpdateCategoryRequest, _options?: ConfigurationOptions): Observable<ApiResponseCategoryResponse> {
        return this.updateCategoryWithHttpInfo(categoryId, updateCategoryRequest, _options).pipe(map((apiResponse: HttpInfo<ApiResponseCategoryResponse>) => apiResponse.data));
    }

}

import { HealthApiRequestFactory, HealthApiResponseProcessor} from "../apis/HealthApi";
export class ObservableHealthApi {
    private requestFactory: HealthApiRequestFactory;
    private responseProcessor: HealthApiResponseProcessor;
    private configuration: Configuration;

    public constructor(
        configuration: Configuration,
        requestFactory?: HealthApiRequestFactory,
        responseProcessor?: HealthApiResponseProcessor
    ) {
        this.configuration = configuration;
        this.requestFactory = requestFactory || new HealthApiRequestFactory(configuration);
        this.responseProcessor = responseProcessor || new HealthApiResponseProcessor();
    }

    /**
     * Check if the API is running
     * Health check
     */
    public healthCheckWithHttpInfo(_options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseHealthResponse>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.healthCheck(_config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.healthCheckWithHttpInfo(rsp)));
            }));
    }

    /**
     * Check if the API is running
     * Health check
     */
    public healthCheck(_options?: ConfigurationOptions): Observable<ApiResponseHealthResponse> {
        return this.healthCheckWithHttpInfo(_options).pipe(map((apiResponse: HttpInfo<ApiResponseHealthResponse>) => apiResponse.data));
    }

}

import { RegionsApiRequestFactory, RegionsApiResponseProcessor} from "../apis/RegionsApi";
export class ObservableRegionsApi {
    private requestFactory: RegionsApiRequestFactory;
    private responseProcessor: RegionsApiResponseProcessor;
    private configuration: Configuration;

    public constructor(
        configuration: Configuration,
        requestFactory?: RegionsApiRequestFactory,
        responseProcessor?: RegionsApiResponseProcessor
    ) {
        this.configuration = configuration;
        this.requestFactory = requestFactory || new RegionsApiRequestFactory(configuration);
        this.responseProcessor = responseProcessor || new RegionsApiResponseProcessor();
    }

    /**
     * Create new region (admin only)
     * @param createRegionRequest
     */
    public createRegionWithHttpInfo(createRegionRequest: CreateRegionRequest, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseRegionResponse>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.createRegion(createRegionRequest, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.createRegionWithHttpInfo(rsp)));
            }));
    }

    /**
     * Create new region (admin only)
     * @param createRegionRequest
     */
    public createRegion(createRegionRequest: CreateRegionRequest, _options?: ConfigurationOptions): Observable<ApiResponseRegionResponse> {
        return this.createRegionWithHttpInfo(createRegionRequest, _options).pipe(map((apiResponse: HttpInfo<ApiResponseRegionResponse>) => apiResponse.data));
    }

    /**
     * Delete region (admin only)
     * @param regionId
     */
    public deleteRegionWithHttpInfo(regionId: string, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseUnit>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.deleteRegion(regionId, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.deleteRegionWithHttpInfo(rsp)));
            }));
    }

    /**
     * Delete region (admin only)
     * @param regionId
     */
    public deleteRegion(regionId: string, _options?: ConfigurationOptions): Observable<ApiResponseUnit> {
        return this.deleteRegionWithHttpInfo(regionId, _options).pipe(map((apiResponse: HttpInfo<ApiResponseUnit>) => apiResponse.data));
    }

    /**
     * Get all active regions
     */
    public getAllRegionsWithHttpInfo(_options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseListRegionResponse>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.getAllRegions(_config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getAllRegionsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get all active regions
     */
    public getAllRegions(_options?: ConfigurationOptions): Observable<ApiResponseListRegionResponse> {
        return this.getAllRegionsWithHttpInfo(_options).pipe(map((apiResponse: HttpInfo<ApiResponseListRegionResponse>) => apiResponse.data));
    }

    /**
     * Get region by ID
     * @param regionId
     */
    public getRegionWithHttpInfo(regionId: string, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseRegionResponse>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.getRegion(regionId, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getRegionWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get region by ID
     * @param regionId
     */
    public getRegion(regionId: string, _options?: ConfigurationOptions): Observable<ApiResponseRegionResponse> {
        return this.getRegionWithHttpInfo(regionId, _options).pipe(map((apiResponse: HttpInfo<ApiResponseRegionResponse>) => apiResponse.data));
    }

    /**
     * Get region by code
     * @param code
     */
    public getRegionByCodeWithHttpInfo(code: string, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseRegionResponse>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.getRegionByCode(code, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getRegionByCodeWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get region by code
     * @param code
     */
    public getRegionByCode(code: string, _options?: ConfigurationOptions): Observable<ApiResponseRegionResponse> {
        return this.getRegionByCodeWithHttpInfo(code, _options).pipe(map((apiResponse: HttpInfo<ApiResponseRegionResponse>) => apiResponse.data));
    }

    /**
     * Search regions
     * @param search
     */
    public searchRegionsWithHttpInfo(search: string, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseListRegionResponse>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.searchRegions(search, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.searchRegionsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Search regions
     * @param search
     */
    public searchRegions(search: string, _options?: ConfigurationOptions): Observable<ApiResponseListRegionResponse> {
        return this.searchRegionsWithHttpInfo(search, _options).pipe(map((apiResponse: HttpInfo<ApiResponseListRegionResponse>) => apiResponse.data));
    }

    /**
     * Update region (admin only)
     * @param regionId
     * @param updateRegionRequest
     */
    public updateRegionWithHttpInfo(regionId: string, updateRegionRequest: UpdateRegionRequest, _options?: ConfigurationOptions): Observable<HttpInfo<ApiResponseRegionResponse>> {
        const _config = mergeConfiguration(this.configuration, _options);

        const requestContextPromise = this.requestFactory.updateRegion(regionId, updateRegionRequest, _config);
        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (const middleware of _config.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => _config.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (const middleware of _config.middleware.reverse()) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.updateRegionWithHttpInfo(rsp)));
            }));
    }

    /**
     * Update region (admin only)
     * @param regionId
     * @param updateRegionRequest
     */
    public updateRegion(regionId: string, updateRegionRequest: UpdateRegionRequest, _options?: ConfigurationOptions): Observable<ApiResponseRegionResponse> {
        return this.updateRegionWithHttpInfo(regionId, updateRegionRequest, _options).pipe(map((apiResponse: HttpInfo<ApiResponseRegionResponse>) => apiResponse.data));
    }

}
