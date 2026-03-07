// TODO: better import syntax?
import {BaseAPIRequestFactory, RequiredError, COLLECTION_FORMATS} from './baseapi';
import {Configuration} from '../configuration';
import {RequestContext, HttpMethod, ResponseContext, HttpFile, HttpInfo} from '../http/http';
import {ObjectSerializer} from '../models/ObjectSerializer';
import {ApiException} from './exception';
import {canConsumeForm, isCodeInRange} from '../util';
import {SecurityAuthentication} from '../auth/auth';


import { ApiResponseAppointmentDto } from '../models/ApiResponseAppointmentDto';
import { ApiResponseListString } from '../models/ApiResponseListString';
import { ApiResponsePageAppointmentDto } from '../models/ApiResponsePageAppointmentDto';
import { CreateAppointmentRequest } from '../models/CreateAppointmentRequest';
import { Pageable } from '../models/Pageable';
import { UpdateAppointmentRequest } from '../models/UpdateAppointmentRequest';

/**
 * no description
 */
export class AppointmentsApiRequestFactory extends BaseAPIRequestFactory {

    /**
     * Cancel appointment
     * @param appointmentId 
     */
    public async cancelAppointment(appointmentId: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'appointmentId' is not null or undefined
        if (appointmentId === null || appointmentId === undefined) {
            throw new RequiredError("AppointmentsApi", "cancelAppointment", "appointmentId");
        }


        // Path Params
        const localVarPath = '/api/v1/appointments/{appointmentId}/cancel'
            .replace('{' + 'appointmentId' + '}', encodeURIComponent(String(appointmentId)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.POST);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")


        let authMethod: SecurityAuthentication | undefined;
        // Apply auth methods
        authMethod = _config.authMethods["bearerAuth"]
        if (authMethod?.applySecurityAuthentication) {
            await authMethod?.applySecurityAuthentication(requestContext);
        }
        
        const defaultAuth: SecurityAuthentication | undefined = _config?.authMethods?.default
        if (defaultAuth?.applySecurityAuthentication) {
            await defaultAuth?.applySecurityAuthentication(requestContext);
        }

        return requestContext;
    }

    /**
     * Create a new appointment
     * @param createAppointmentRequest 
     */
    public async createAppointment(createAppointmentRequest: CreateAppointmentRequest, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'createAppointmentRequest' is not null or undefined
        if (createAppointmentRequest === null || createAppointmentRequest === undefined) {
            throw new RequiredError("AppointmentsApi", "createAppointment", "createAppointmentRequest");
        }


        // Path Params
        const localVarPath = '/api/v1/appointments';

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.POST);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")


        // Body Params
        const contentType = ObjectSerializer.getPreferredMediaType([
            "application/json"
        ]);
        requestContext.setHeaderParam("Content-Type", contentType);
        const serializedBody = ObjectSerializer.stringify(
            ObjectSerializer.serialize(createAppointmentRequest, "CreateAppointmentRequest", ""),
            contentType
        );
        requestContext.setBody(serializedBody);

        let authMethod: SecurityAuthentication | undefined;
        // Apply auth methods
        authMethod = _config.authMethods["bearerAuth"]
        if (authMethod?.applySecurityAuthentication) {
            await authMethod?.applySecurityAuthentication(requestContext);
        }
        
        const defaultAuth: SecurityAuthentication | undefined = _config?.authMethods?.default
        if (defaultAuth?.applySecurityAuthentication) {
            await defaultAuth?.applySecurityAuthentication(requestContext);
        }

        return requestContext;
    }

    /**
     * Get appointment by ID
     * @param appointmentId 
     */
    public async getAppointment(appointmentId: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'appointmentId' is not null or undefined
        if (appointmentId === null || appointmentId === undefined) {
            throw new RequiredError("AppointmentsApi", "getAppointment", "appointmentId");
        }


        // Path Params
        const localVarPath = '/api/v1/appointments/{appointmentId}'
            .replace('{' + 'appointmentId' + '}', encodeURIComponent(String(appointmentId)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")


        let authMethod: SecurityAuthentication | undefined;
        // Apply auth methods
        authMethod = _config.authMethods["bearerAuth"]
        if (authMethod?.applySecurityAuthentication) {
            await authMethod?.applySecurityAuthentication(requestContext);
        }
        
        const defaultAuth: SecurityAuthentication | undefined = _config?.authMethods?.default
        if (defaultAuth?.applySecurityAuthentication) {
            await defaultAuth?.applySecurityAuthentication(requestContext);
        }

        return requestContext;
    }

    /**
     * Get business appointments
     * @param businessId 
     * @param pageable 
     */
    public async getBusinessAppointments(businessId: string, pageable: Pageable, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'businessId' is not null or undefined
        if (businessId === null || businessId === undefined) {
            throw new RequiredError("AppointmentsApi", "getBusinessAppointments", "businessId");
        }


        // verify required parameter 'pageable' is not null or undefined
        if (pageable === null || pageable === undefined) {
            throw new RequiredError("AppointmentsApi", "getBusinessAppointments", "pageable");
        }


        // Path Params
        const localVarPath = '/api/v1/appointments/business/{businessId}'
            .replace('{' + 'businessId' + '}', encodeURIComponent(String(businessId)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")

        // Query Params
        if (pageable !== undefined) {
            const serializedParams = ObjectSerializer.serialize(pageable, "Pageable", "");
            for (const key of Object.keys(serializedParams)) {
                requestContext.setQueryParam(key, serializedParams[key]);
            }
        }


        let authMethod: SecurityAuthentication | undefined;
        // Apply auth methods
        authMethod = _config.authMethods["bearerAuth"]
        if (authMethod?.applySecurityAuthentication) {
            await authMethod?.applySecurityAuthentication(requestContext);
        }
        
        const defaultAuth: SecurityAuthentication | undefined = _config?.authMethods?.default
        if (defaultAuth?.applySecurityAuthentication) {
            await defaultAuth?.applySecurityAuthentication(requestContext);
        }

        return requestContext;
    }

    /**
     * Get business availability
     * @param businessId 
     * @param date 
     */
    public async getBusinessAvailability(businessId: string, date: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'businessId' is not null or undefined
        if (businessId === null || businessId === undefined) {
            throw new RequiredError("AppointmentsApi", "getBusinessAvailability", "businessId");
        }


        // verify required parameter 'date' is not null or undefined
        if (date === null || date === undefined) {
            throw new RequiredError("AppointmentsApi", "getBusinessAvailability", "date");
        }


        // Path Params
        const localVarPath = '/api/v1/appointments/business/{businessId}/availability'
            .replace('{' + 'businessId' + '}', encodeURIComponent(String(businessId)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")

        // Query Params
        if (date !== undefined) {
            requestContext.setQueryParam("date", ObjectSerializer.serialize(date, "string", "date"));
        }


        let authMethod: SecurityAuthentication | undefined;
        // Apply auth methods
        authMethod = _config.authMethods["bearerAuth"]
        if (authMethod?.applySecurityAuthentication) {
            await authMethod?.applySecurityAuthentication(requestContext);
        }
        
        const defaultAuth: SecurityAuthentication | undefined = _config?.authMethods?.default
        if (defaultAuth?.applySecurityAuthentication) {
            await defaultAuth?.applySecurityAuthentication(requestContext);
        }

        return requestContext;
    }

    /**
     * Search appointments
     * @param pageable 
     * @param businessId 
     * @param customerId 
     * @param status 
     * @param startDate 
     * @param endDate 
     */
    public async searchAppointments(pageable: Pageable, businessId?: string, customerId?: string, status?: 'PENDING' | 'CONFIRMED' | 'COMPLETED' | 'CANCELLED' | 'NO_SHOW', startDate?: string, endDate?: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'pageable' is not null or undefined
        if (pageable === null || pageable === undefined) {
            throw new RequiredError("AppointmentsApi", "searchAppointments", "pageable");
        }







        // Path Params
        const localVarPath = '/api/v1/appointments/search';

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")

        // Query Params
        if (businessId !== undefined) {
            requestContext.setQueryParam("businessId", ObjectSerializer.serialize(businessId, "string", "uuid"));
        }

        // Query Params
        if (customerId !== undefined) {
            requestContext.setQueryParam("customerId", ObjectSerializer.serialize(customerId, "string", "uuid"));
        }

        // Query Params
        if (status !== undefined) {
            requestContext.setQueryParam("status", ObjectSerializer.serialize(status, "'PENDING' | 'CONFIRMED' | 'COMPLETED' | 'CANCELLED' | 'NO_SHOW'", ""));
        }

        // Query Params
        if (startDate !== undefined) {
            requestContext.setQueryParam("startDate", ObjectSerializer.serialize(startDate, "string", "date"));
        }

        // Query Params
        if (endDate !== undefined) {
            requestContext.setQueryParam("endDate", ObjectSerializer.serialize(endDate, "string", "date"));
        }

        // Query Params
        if (pageable !== undefined) {
            const serializedParams = ObjectSerializer.serialize(pageable, "Pageable", "");
            for (const key of Object.keys(serializedParams)) {
                requestContext.setQueryParam(key, serializedParams[key]);
            }
        }


        let authMethod: SecurityAuthentication | undefined;
        // Apply auth methods
        authMethod = _config.authMethods["bearerAuth"]
        if (authMethod?.applySecurityAuthentication) {
            await authMethod?.applySecurityAuthentication(requestContext);
        }
        
        const defaultAuth: SecurityAuthentication | undefined = _config?.authMethods?.default
        if (defaultAuth?.applySecurityAuthentication) {
            await defaultAuth?.applySecurityAuthentication(requestContext);
        }

        return requestContext;
    }

    /**
     * Update appointment
     * @param appointmentId 
     * @param updateAppointmentRequest 
     */
    public async updateAppointment(appointmentId: string, updateAppointmentRequest: UpdateAppointmentRequest, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'appointmentId' is not null or undefined
        if (appointmentId === null || appointmentId === undefined) {
            throw new RequiredError("AppointmentsApi", "updateAppointment", "appointmentId");
        }


        // verify required parameter 'updateAppointmentRequest' is not null or undefined
        if (updateAppointmentRequest === null || updateAppointmentRequest === undefined) {
            throw new RequiredError("AppointmentsApi", "updateAppointment", "updateAppointmentRequest");
        }


        // Path Params
        const localVarPath = '/api/v1/appointments/{appointmentId}'
            .replace('{' + 'appointmentId' + '}', encodeURIComponent(String(appointmentId)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.PUT);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")


        // Body Params
        const contentType = ObjectSerializer.getPreferredMediaType([
            "application/json"
        ]);
        requestContext.setHeaderParam("Content-Type", contentType);
        const serializedBody = ObjectSerializer.stringify(
            ObjectSerializer.serialize(updateAppointmentRequest, "UpdateAppointmentRequest", ""),
            contentType
        );
        requestContext.setBody(serializedBody);

        let authMethod: SecurityAuthentication | undefined;
        // Apply auth methods
        authMethod = _config.authMethods["bearerAuth"]
        if (authMethod?.applySecurityAuthentication) {
            await authMethod?.applySecurityAuthentication(requestContext);
        }
        
        const defaultAuth: SecurityAuthentication | undefined = _config?.authMethods?.default
        if (defaultAuth?.applySecurityAuthentication) {
            await defaultAuth?.applySecurityAuthentication(requestContext);
        }

        return requestContext;
    }

}

export class AppointmentsApiResponseProcessor {

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to cancelAppointment
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async cancelAppointmentWithHttpInfo(response: ResponseContext): Promise<HttpInfo<ApiResponseAppointmentDto >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: ApiResponseAppointmentDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponseAppointmentDto", ""
            ) as ApiResponseAppointmentDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: ApiResponseAppointmentDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponseAppointmentDto", ""
            ) as ApiResponseAppointmentDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to createAppointment
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async createAppointmentWithHttpInfo(response: ResponseContext): Promise<HttpInfo<ApiResponseAppointmentDto >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: ApiResponseAppointmentDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponseAppointmentDto", ""
            ) as ApiResponseAppointmentDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: ApiResponseAppointmentDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponseAppointmentDto", ""
            ) as ApiResponseAppointmentDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to getAppointment
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async getAppointmentWithHttpInfo(response: ResponseContext): Promise<HttpInfo<ApiResponseAppointmentDto >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: ApiResponseAppointmentDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponseAppointmentDto", ""
            ) as ApiResponseAppointmentDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: ApiResponseAppointmentDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponseAppointmentDto", ""
            ) as ApiResponseAppointmentDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to getBusinessAppointments
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async getBusinessAppointmentsWithHttpInfo(response: ResponseContext): Promise<HttpInfo<ApiResponsePageAppointmentDto >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: ApiResponsePageAppointmentDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponsePageAppointmentDto", ""
            ) as ApiResponsePageAppointmentDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: ApiResponsePageAppointmentDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponsePageAppointmentDto", ""
            ) as ApiResponsePageAppointmentDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to getBusinessAvailability
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async getBusinessAvailabilityWithHttpInfo(response: ResponseContext): Promise<HttpInfo<ApiResponseListString >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: ApiResponseListString = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponseListString", ""
            ) as ApiResponseListString;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: ApiResponseListString = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponseListString", ""
            ) as ApiResponseListString;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to searchAppointments
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async searchAppointmentsWithHttpInfo(response: ResponseContext): Promise<HttpInfo<ApiResponsePageAppointmentDto >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: ApiResponsePageAppointmentDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponsePageAppointmentDto", ""
            ) as ApiResponsePageAppointmentDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: ApiResponsePageAppointmentDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponsePageAppointmentDto", ""
            ) as ApiResponsePageAppointmentDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to updateAppointment
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async updateAppointmentWithHttpInfo(response: ResponseContext): Promise<HttpInfo<ApiResponseAppointmentDto >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: ApiResponseAppointmentDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponseAppointmentDto", ""
            ) as ApiResponseAppointmentDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: ApiResponseAppointmentDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponseAppointmentDto", ""
            ) as ApiResponseAppointmentDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

}
