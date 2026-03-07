# .BusinessesApi

All URIs are relative to *http://localhost:3000*

Method | HTTP request | Description
------------- | ------------- | -------------
[**createBusiness**](BusinessesApi.md#createBusiness) | **POST** /api/v1/businesses | Create a new business
[**deleteBusiness**](BusinessesApi.md#deleteBusiness) | **DELETE** /api/v1/businesses/{businessId} | Delete business
[**getBusiness**](BusinessesApi.md#getBusiness) | **GET** /api/v1/businesses/{businessId} | Get business by ID
[**getBusinessBySlug**](BusinessesApi.md#getBusinessBySlug) | **GET** /api/v1/businesses/slug/{slug} | Get business by slug
[**getBusinessesByCategory**](BusinessesApi.md#getBusinessesByCategory) | **GET** /api/v1/businesses/category/{categoryId} | Get businesses by category
[**getBusinessesByRegion**](BusinessesApi.md#getBusinessesByRegion) | **GET** /api/v1/businesses/region/{regionId} | Get businesses by region
[**getFeaturedBusinesses**](BusinessesApi.md#getFeaturedBusinesses) | **GET** /api/v1/businesses/featured | Get featured businesses
[**getUserBusinesses**](BusinessesApi.md#getUserBusinesses) | **GET** /api/v1/businesses/my-businesses | Get current user\&#39;s businesses
[**searchBusinesses**](BusinessesApi.md#searchBusinesses) | **GET** /api/v1/businesses/search | Search businesses
[**updateBusiness**](BusinessesApi.md#updateBusiness) | **PUT** /api/v1/businesses/{businessId} | Update business
[**verifyBusiness**](BusinessesApi.md#verifyBusiness) | **POST** /api/v1/businesses/{businessId}/verify | Verify business (admin only)


# **createBusiness**
> ApiResponseBusinessDto createBusiness(createBusinessRequest)


### Example


```typescript
import { createConfiguration, BusinessesApi } from '';
import type { BusinessesApiCreateBusinessRequest } from '';

const configuration = createConfiguration();
const apiInstance = new BusinessesApi(configuration);

const request: BusinessesApiCreateBusinessRequest = {
  
  createBusinessRequest: {
    name: "name_example",
    description: "description_example",
    categoryId: "categoryId_example",
    regionId: "regionId_example",
    email: "email_example",
    phone: "phone_example",
    website: "website_example",
    addressLine1: "addressLine1_example",
    addressLine2: "addressLine2_example",
    city: "city_example",
    state: "state_example",
    zipCode: "zipCode_example",
    country: "country_example",
    latitude: 3.14,
    longitude: 3.14,
    logoUrl: "logoUrl_example",
    coverImageUrl: "coverImageUrl_example",
    servicePointsCount: 1,
  },
};

const data = await apiInstance.createBusiness(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **createBusinessRequest** | **CreateBusinessRequest**|  |


### Return type

**ApiResponseBusinessDto**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **deleteBusiness**
> ApiResponseUnit deleteBusiness()


### Example


```typescript
import { createConfiguration, BusinessesApi } from '';
import type { BusinessesApiDeleteBusinessRequest } from '';

const configuration = createConfiguration();
const apiInstance = new BusinessesApi(configuration);

const request: BusinessesApiDeleteBusinessRequest = {
  
  businessId: "businessId_example",
};

const data = await apiInstance.deleteBusiness(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **businessId** | [**string**] |  | defaults to undefined


### Return type

**ApiResponseUnit**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **getBusiness**
> ApiResponseBusinessDto getBusiness()


### Example


```typescript
import { createConfiguration, BusinessesApi } from '';
import type { BusinessesApiGetBusinessRequest } from '';

const configuration = createConfiguration();
const apiInstance = new BusinessesApi(configuration);

const request: BusinessesApiGetBusinessRequest = {
  
  businessId: "businessId_example",
};

const data = await apiInstance.getBusiness(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **businessId** | [**string**] |  | defaults to undefined


### Return type

**ApiResponseBusinessDto**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **getBusinessBySlug**
> ApiResponseBusinessDto getBusinessBySlug()


### Example


```typescript
import { createConfiguration, BusinessesApi } from '';
import type { BusinessesApiGetBusinessBySlugRequest } from '';

const configuration = createConfiguration();
const apiInstance = new BusinessesApi(configuration);

const request: BusinessesApiGetBusinessBySlugRequest = {
  
  slug: "slug_example",
};

const data = await apiInstance.getBusinessBySlug(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **slug** | [**string**] |  | defaults to undefined


### Return type

**ApiResponseBusinessDto**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **getBusinessesByCategory**
> ApiResponsePageBusinessDto getBusinessesByCategory()


### Example


```typescript
import { createConfiguration, BusinessesApi } from '';
import type { BusinessesApiGetBusinessesByCategoryRequest } from '';

const configuration = createConfiguration();
const apiInstance = new BusinessesApi(configuration);

const request: BusinessesApiGetBusinessesByCategoryRequest = {
  
  categoryId: "categoryId_example",
  
  pageable: {
    page: 0,
    size: 1,
    sort: [
      "sort_example",
    ],
  },
};

const data = await apiInstance.getBusinessesByCategory(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **categoryId** | [**string**] |  | defaults to undefined
 **pageable** | **Pageable** |  | defaults to undefined


### Return type

**ApiResponsePageBusinessDto**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **getBusinessesByRegion**
> ApiResponsePageBusinessDto getBusinessesByRegion()


### Example


```typescript
import { createConfiguration, BusinessesApi } from '';
import type { BusinessesApiGetBusinessesByRegionRequest } from '';

const configuration = createConfiguration();
const apiInstance = new BusinessesApi(configuration);

const request: BusinessesApiGetBusinessesByRegionRequest = {
  
  regionId: "regionId_example",
  
  pageable: {
    page: 0,
    size: 1,
    sort: [
      "sort_example",
    ],
  },
};

const data = await apiInstance.getBusinessesByRegion(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **regionId** | [**string**] |  | defaults to undefined
 **pageable** | **Pageable** |  | defaults to undefined


### Return type

**ApiResponsePageBusinessDto**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **getFeaturedBusinesses**
> ApiResponsePageBusinessDto getFeaturedBusinesses()


### Example


```typescript
import { createConfiguration, BusinessesApi } from '';
import type { BusinessesApiGetFeaturedBusinessesRequest } from '';

const configuration = createConfiguration();
const apiInstance = new BusinessesApi(configuration);

const request: BusinessesApiGetFeaturedBusinessesRequest = {
  
  pageable: {
    page: 0,
    size: 1,
    sort: [
      "sort_example",
    ],
  },
};

const data = await apiInstance.getFeaturedBusinesses(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pageable** | **Pageable** |  | defaults to undefined


### Return type

**ApiResponsePageBusinessDto**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **getUserBusinesses**
> ApiResponsePageBusinessDto getUserBusinesses()


### Example


```typescript
import { createConfiguration, BusinessesApi } from '';
import type { BusinessesApiGetUserBusinessesRequest } from '';

const configuration = createConfiguration();
const apiInstance = new BusinessesApi(configuration);

const request: BusinessesApiGetUserBusinessesRequest = {
  
  pageable: {
    page: 0,
    size: 1,
    sort: [
      "sort_example",
    ],
  },
};

const data = await apiInstance.getUserBusinesses(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pageable** | **Pageable** |  | defaults to undefined


### Return type

**ApiResponsePageBusinessDto**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **searchBusinesses**
> ApiResponsePageBusinessDto searchBusinesses()


### Example


```typescript
import { createConfiguration, BusinessesApi } from '';
import type { BusinessesApiSearchBusinessesRequest } from '';

const configuration = createConfiguration();
const apiInstance = new BusinessesApi(configuration);

const request: BusinessesApiSearchBusinessesRequest = {
  
  pageable: {
    page: 0,
    size: 1,
    sort: [
      "sort_example",
    ],
  },
  
  query: "query_example",
  
  regionId: "regionId_example",
  
  categoryId: "categoryId_example",
};

const data = await apiInstance.searchBusinesses(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pageable** | **Pageable** |  | defaults to undefined
 **query** | [**string**] |  | (optional) defaults to undefined
 **regionId** | [**string**] |  | (optional) defaults to undefined
 **categoryId** | [**string**] |  | (optional) defaults to undefined


### Return type

**ApiResponsePageBusinessDto**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **updateBusiness**
> ApiResponseBusinessDto updateBusiness(updateBusinessRequest)


### Example


```typescript
import { createConfiguration, BusinessesApi } from '';
import type { BusinessesApiUpdateBusinessRequest } from '';

const configuration = createConfiguration();
const apiInstance = new BusinessesApi(configuration);

const request: BusinessesApiUpdateBusinessRequest = {
  
  businessId: "businessId_example",
  
  updateBusinessRequest: {
    name: "name_example",
    description: "description_example",
    categoryId: "categoryId_example",
    regionId: "regionId_example",
    email: "email_example",
    phone: "phone_example",
    website: "website_example",
    addressLine1: "addressLine1_example",
    addressLine2: "addressLine2_example",
    city: "city_example",
    state: "state_example",
    zipCode: "zipCode_example",
    country: "country_example",
    latitude: 3.14,
    longitude: 3.14,
    logoUrl: "logoUrl_example",
    coverImageUrl: "coverImageUrl_example",
    isActive: true,
    isFeatured: true,
    verificationStatus: "PENDING",
    servicePointsCount: 1,
  },
};

const data = await apiInstance.updateBusiness(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **updateBusinessRequest** | **UpdateBusinessRequest**|  |
 **businessId** | [**string**] |  | defaults to undefined


### Return type

**ApiResponseBusinessDto**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **verifyBusiness**
> ApiResponseBusinessDto verifyBusiness()


### Example


```typescript
import { createConfiguration, BusinessesApi } from '';
import type { BusinessesApiVerifyBusinessRequest } from '';

const configuration = createConfiguration();
const apiInstance = new BusinessesApi(configuration);

const request: BusinessesApiVerifyBusinessRequest = {
  
  businessId: "businessId_example",
};

const data = await apiInstance.verifyBusiness(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **businessId** | [**string**] |  | defaults to undefined


### Return type

**ApiResponseBusinessDto**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)


