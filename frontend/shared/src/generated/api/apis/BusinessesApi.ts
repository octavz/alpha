// TODO: better import syntax?
import {BaseAPIRequestFactory, RequiredError, COLLECTION_FORMATS} from './baseapi';
import {Configuration} from '../configuration';
import {RequestContext, HttpMethod, ResponseContext, HttpFile, HttpInfo} from '../http/http';
import {ObjectSerializer} from '../models/ObjectSerializer';
import {ApiException} from './exception';
import {canConsumeForm, isCodeInRange} from '../util';
import {SecurityAuthentication} from '../auth/auth';


import { ApiResponseBusinessDto } from '../models/ApiResponseBusinessDto';
import { ApiResponsePageBusinessDto } from '../models/ApiResponsePageBusinessDto';
import { ApiResponseUnit } from '../models/ApiResponseUnit';
import { CreateBusinessRequest } from '../models/CreateBusinessRequest';
import { Pageable } from '../models/Pageable';
import { UpdateBusinessRequest } from '../models/UpdateBusinessRequest';

/**
 * no description
 */
export class BusinessesApiRequestFactory extends BaseAPIRequestFactory {

    /**
     * Create a new business
     * @param createBusinessRequest 
     */
    public async createBusiness(createBusinessRequest: CreateBusinessRequest, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'createBusinessRequest' is not null or undefined
        if (createBusinessRequest === null || createBusinessRequest === undefined) {
            throw new RequiredError("BusinessesApi", "createBusiness", "createBusinessRequest");
        }


        // Path Params
        const localVarPath = '/api/v1/businesses';

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.POST);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")


        // Body Params
        const contentType = ObjectSerializer.getPreferredMediaType([
            "application/json"
        ]);
        requestContext.setHeaderParam("Content-Type", contentType);
        const serializedBody = ObjectSerializer.stringify(
            ObjectSerializer.serialize(createBusinessRequest, "CreateBusinessRequest", ""),
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
     * Delete business
     * @param businessId 
     */
    public async deleteBusiness(businessId: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'businessId' is not null or undefined
        if (businessId === null || businessId === undefined) {
            throw new RequiredError("BusinessesApi", "deleteBusiness", "businessId");
        }


        // Path Params
        const localVarPath = '/api/v1/businesses/{businessId}'
            .replace('{' + 'businessId' + '}', encodeURIComponent(String(businessId)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.DELETE);
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
     * Get business by ID
     * @param businessId 
     */
    public async getBusiness(businessId: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'businessId' is not null or undefined
        if (businessId === null || businessId === undefined) {
            throw new RequiredError("BusinessesApi", "getBusiness", "businessId");
        }


        // Path Params
        const localVarPath = '/api/v1/businesses/{businessId}'
            .replace('{' + 'businessId' + '}', encodeURIComponent(String(businessId)));

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
     * Get business by slug
     * @param slug 
     */
    public async getBusinessBySlug(slug: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'slug' is not null or undefined
        if (slug === null || slug === undefined) {
            throw new RequiredError("BusinessesApi", "getBusinessBySlug", "slug");
        }


        // Path Params
        const localVarPath = '/api/v1/businesses/slug/{slug}'
            .replace('{' + 'slug' + '}', encodeURIComponent(String(slug)));

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
     * Get businesses by category
     * @param categoryId 
     * @param pageable 
     */
    public async getBusinessesByCategory(categoryId: string, pageable: Pageable, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'categoryId' is not null or undefined
        if (categoryId === null || categoryId === undefined) {
            throw new RequiredError("BusinessesApi", "getBusinessesByCategory", "categoryId");
        }


        // verify required parameter 'pageable' is not null or undefined
        if (pageable === null || pageable === undefined) {
            throw new RequiredError("BusinessesApi", "getBusinessesByCategory", "pageable");
        }


        // Path Params
        const localVarPath = '/api/v1/businesses/category/{categoryId}'
            .replace('{' + 'categoryId' + '}', encodeURIComponent(String(categoryId)));

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
     * Get businesses by region
     * @param regionId 
     * @param pageable 
     */
    public async getBusinessesByRegion(regionId: string, pageable: Pageable, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'regionId' is not null or undefined
        if (regionId === null || regionId === undefined) {
            throw new RequiredError("BusinessesApi", "getBusinessesByRegion", "regionId");
        }


        // verify required parameter 'pageable' is not null or undefined
        if (pageable === null || pageable === undefined) {
            throw new RequiredError("BusinessesApi", "getBusinessesByRegion", "pageable");
        }


        // Path Params
        const localVarPath = '/api/v1/businesses/region/{regionId}'
            .replace('{' + 'regionId' + '}', encodeURIComponent(String(regionId)));

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
     * Get featured businesses
     * @param pageable 
     */
    public async getFeaturedBusinesses(pageable: Pageable, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'pageable' is not null or undefined
        if (pageable === null || pageable === undefined) {
            throw new RequiredError("BusinessesApi", "getFeaturedBusinesses", "pageable");
        }


        // Path Params
        const localVarPath = '/api/v1/businesses/featured';

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
     * Get current user\'s businesses
     * @param pageable 
     */
    public async getUserBusinesses(pageable: Pageable, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'pageable' is not null or undefined
        if (pageable === null || pageable === undefined) {
            throw new RequiredError("BusinessesApi", "getUserBusinesses", "pageable");
        }


        // Path Params
        const localVarPath = '/api/v1/businesses/my-businesses';

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
     * Search businesses
     * @param pageable 
     * @param query 
     * @param regionId 
     * @param categoryId 
     */
    public async searchBusinesses(pageable: Pageable, query?: string, regionId?: string, categoryId?: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'pageable' is not null or undefined
        if (pageable === null || pageable === undefined) {
            throw new RequiredError("BusinessesApi", "searchBusinesses", "pageable");
        }





        // Path Params
        const localVarPath = '/api/v1/businesses/search';

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")

        // Query Params
        if (query !== undefined) {
            requestContext.setQueryParam("query", ObjectSerializer.serialize(query, "string", ""));
        }

        // Query Params
        if (regionId !== undefined) {
            requestContext.setQueryParam("regionId", ObjectSerializer.serialize(regionId, "string", "uuid"));
        }

        // Query Params
        if (categoryId !== undefined) {
            requestContext.setQueryParam("categoryId", ObjectSerializer.serialize(categoryId, "string", "uuid"));
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
     * Update business
     * @param businessId 
     * @param updateBusinessRequest 
     */
    public async updateBusiness(businessId: string, updateBusinessRequest: UpdateBusinessRequest, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'businessId' is not null or undefined
        if (businessId === null || businessId === undefined) {
            throw new RequiredError("BusinessesApi", "updateBusiness", "businessId");
        }


        // verify required parameter 'updateBusinessRequest' is not null or undefined
        if (updateBusinessRequest === null || updateBusinessRequest === undefined) {
            throw new RequiredError("BusinessesApi", "updateBusiness", "updateBusinessRequest");
        }


        // Path Params
        const localVarPath = '/api/v1/businesses/{businessId}'
            .replace('{' + 'businessId' + '}', encodeURIComponent(String(businessId)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.PUT);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")


        // Body Params
        const contentType = ObjectSerializer.getPreferredMediaType([
            "application/json"
        ]);
        requestContext.setHeaderParam("Content-Type", contentType);
        const serializedBody = ObjectSerializer.stringify(
            ObjectSerializer.serialize(updateBusinessRequest, "UpdateBusinessRequest", ""),
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
     * Verify business (admin only)
     * @param businessId 
     */
    public async verifyBusiness(businessId: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'businessId' is not null or undefined
        if (businessId === null || businessId === undefined) {
            throw new RequiredError("BusinessesApi", "verifyBusiness", "businessId");
        }


        // Path Params
        const localVarPath = '/api/v1/businesses/{businessId}/verify'
            .replace('{' + 'businessId' + '}', encodeURIComponent(String(businessId)));

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

}

export class BusinessesApiResponseProcessor {

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to createBusiness
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async createBusinessWithHttpInfo(response: ResponseContext): Promise<HttpInfo<ApiResponseBusinessDto >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: ApiResponseBusinessDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponseBusinessDto", ""
            ) as ApiResponseBusinessDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: ApiResponseBusinessDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponseBusinessDto", ""
            ) as ApiResponseBusinessDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to deleteBusiness
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async deleteBusinessWithHttpInfo(response: ResponseContext): Promise<HttpInfo<ApiResponseUnit >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: ApiResponseUnit = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponseUnit", ""
            ) as ApiResponseUnit;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: ApiResponseUnit = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponseUnit", ""
            ) as ApiResponseUnit;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to getBusiness
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async getBusinessWithHttpInfo(response: ResponseContext): Promise<HttpInfo<ApiResponseBusinessDto >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: ApiResponseBusinessDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponseBusinessDto", ""
            ) as ApiResponseBusinessDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: ApiResponseBusinessDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponseBusinessDto", ""
            ) as ApiResponseBusinessDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to getBusinessBySlug
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async getBusinessBySlugWithHttpInfo(response: ResponseContext): Promise<HttpInfo<ApiResponseBusinessDto >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: ApiResponseBusinessDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponseBusinessDto", ""
            ) as ApiResponseBusinessDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: ApiResponseBusinessDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponseBusinessDto", ""
            ) as ApiResponseBusinessDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to getBusinessesByCategory
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async getBusinessesByCategoryWithHttpInfo(response: ResponseContext): Promise<HttpInfo<ApiResponsePageBusinessDto >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: ApiResponsePageBusinessDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponsePageBusinessDto", ""
            ) as ApiResponsePageBusinessDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: ApiResponsePageBusinessDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponsePageBusinessDto", ""
            ) as ApiResponsePageBusinessDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to getBusinessesByRegion
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async getBusinessesByRegionWithHttpInfo(response: ResponseContext): Promise<HttpInfo<ApiResponsePageBusinessDto >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: ApiResponsePageBusinessDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponsePageBusinessDto", ""
            ) as ApiResponsePageBusinessDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: ApiResponsePageBusinessDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponsePageBusinessDto", ""
            ) as ApiResponsePageBusinessDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to getFeaturedBusinesses
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async getFeaturedBusinessesWithHttpInfo(response: ResponseContext): Promise<HttpInfo<ApiResponsePageBusinessDto >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: ApiResponsePageBusinessDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponsePageBusinessDto", ""
            ) as ApiResponsePageBusinessDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: ApiResponsePageBusinessDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponsePageBusinessDto", ""
            ) as ApiResponsePageBusinessDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to getUserBusinesses
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async getUserBusinessesWithHttpInfo(response: ResponseContext): Promise<HttpInfo<ApiResponsePageBusinessDto >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: ApiResponsePageBusinessDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponsePageBusinessDto", ""
            ) as ApiResponsePageBusinessDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: ApiResponsePageBusinessDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponsePageBusinessDto", ""
            ) as ApiResponsePageBusinessDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to searchBusinesses
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async searchBusinessesWithHttpInfo(response: ResponseContext): Promise<HttpInfo<ApiResponsePageBusinessDto >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: ApiResponsePageBusinessDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponsePageBusinessDto", ""
            ) as ApiResponsePageBusinessDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: ApiResponsePageBusinessDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponsePageBusinessDto", ""
            ) as ApiResponsePageBusinessDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to updateBusiness
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async updateBusinessWithHttpInfo(response: ResponseContext): Promise<HttpInfo<ApiResponseBusinessDto >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: ApiResponseBusinessDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponseBusinessDto", ""
            ) as ApiResponseBusinessDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: ApiResponseBusinessDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponseBusinessDto", ""
            ) as ApiResponseBusinessDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to verifyBusiness
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async verifyBusinessWithHttpInfo(response: ResponseContext): Promise<HttpInfo<ApiResponseBusinessDto >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: ApiResponseBusinessDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponseBusinessDto", ""
            ) as ApiResponseBusinessDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: ApiResponseBusinessDto = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ApiResponseBusinessDto", ""
            ) as ApiResponseBusinessDto;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

}
