# .RegionsApi

All URIs are relative to *http://localhost:3000*

Method | HTTP request | Description
------------- | ------------- | -------------
[**createRegion**](RegionsApi.md#createRegion) | **POST** /api/v1/regions | Create new region (admin only)
[**deleteRegion**](RegionsApi.md#deleteRegion) | **DELETE** /api/v1/regions/{regionId} | Delete region (admin only)
[**getAllRegions**](RegionsApi.md#getAllRegions) | **GET** /api/v1/regions | Get all active regions
[**getRegion**](RegionsApi.md#getRegion) | **GET** /api/v1/regions/{regionId} | Get region by ID
[**getRegionByCode**](RegionsApi.md#getRegionByCode) | **GET** /api/v1/regions/code/{code} | Get region by code
[**searchRegions**](RegionsApi.md#searchRegions) | **GET** /api/v1/regions/search | Search regions
[**updateRegion**](RegionsApi.md#updateRegion) | **PUT** /api/v1/regions/{regionId} | Update region (admin only)


# **createRegion**
> ApiResponseRegionResponse createRegion(createRegionRequest)


### Example


```typescript
import { createConfiguration, RegionsApi } from '';
import type { RegionsApiCreateRegionRequest } from '';

const configuration = createConfiguration();
const apiInstance = new RegionsApi(configuration);

const request: RegionsApiCreateRegionRequest = {
  
  createRegionRequest: {
    name: "name_example",
    code: "code_example",
    country: "country_example",
    timezone: "timezone_example",
    isActive: true,
  },
};

const data = await apiInstance.createRegion(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **createRegionRequest** | **CreateRegionRequest**|  |


### Return type

**ApiResponseRegionResponse**

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

# **deleteRegion**
> ApiResponseUnit deleteRegion()


### Example


```typescript
import { createConfiguration, RegionsApi } from '';
import type { RegionsApiDeleteRegionRequest } from '';

const configuration = createConfiguration();
const apiInstance = new RegionsApi(configuration);

const request: RegionsApiDeleteRegionRequest = {
  
  regionId: "regionId_example",
};

const data = await apiInstance.deleteRegion(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **regionId** | [**string**] |  | defaults to undefined


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

# **getAllRegions**
> ApiResponseListRegionResponse getAllRegions()


### Example


```typescript
import { createConfiguration, RegionsApi } from '';

const configuration = createConfiguration();
const apiInstance = new RegionsApi(configuration);

const request = {};

const data = await apiInstance.getAllRegions(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters
This endpoint does not need any parameter.


### Return type

**ApiResponseListRegionResponse**

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

# **getRegion**
> ApiResponseRegionResponse getRegion()


### Example


```typescript
import { createConfiguration, RegionsApi } from '';
import type { RegionsApiGetRegionRequest } from '';

const configuration = createConfiguration();
const apiInstance = new RegionsApi(configuration);

const request: RegionsApiGetRegionRequest = {
  
  regionId: "regionId_example",
};

const data = await apiInstance.getRegion(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **regionId** | [**string**] |  | defaults to undefined


### Return type

**ApiResponseRegionResponse**

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

# **getRegionByCode**
> ApiResponseRegionResponse getRegionByCode()


### Example


```typescript
import { createConfiguration, RegionsApi } from '';
import type { RegionsApiGetRegionByCodeRequest } from '';

const configuration = createConfiguration();
const apiInstance = new RegionsApi(configuration);

const request: RegionsApiGetRegionByCodeRequest = {
  
  code: "code_example",
};

const data = await apiInstance.getRegionByCode(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **code** | [**string**] |  | defaults to undefined


### Return type

**ApiResponseRegionResponse**

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

# **searchRegions**
> ApiResponseListRegionResponse searchRegions()


### Example


```typescript
import { createConfiguration, RegionsApi } from '';
import type { RegionsApiSearchRegionsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new RegionsApi(configuration);

const request: RegionsApiSearchRegionsRequest = {
  
  search: "search_example",
};

const data = await apiInstance.searchRegions(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **search** | [**string**] |  | defaults to undefined


### Return type

**ApiResponseListRegionResponse**

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

# **updateRegion**
> ApiResponseRegionResponse updateRegion(updateRegionRequest)


### Example


```typescript
import { createConfiguration, RegionsApi } from '';
import type { RegionsApiUpdateRegionRequest } from '';

const configuration = createConfiguration();
const apiInstance = new RegionsApi(configuration);

const request: RegionsApiUpdateRegionRequest = {
  
  regionId: "regionId_example",
  
  updateRegionRequest: {
    name: "name_example",
    code: "code_example",
    country: "country_example",
    timezone: "timezone_example",
    isActive: true,
  },
};

const data = await apiInstance.updateRegion(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **updateRegionRequest** | **UpdateRegionRequest**|  |
 **regionId** | [**string**] |  | defaults to undefined


### Return type

**ApiResponseRegionResponse**

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


