# .HealthApi

All URIs are relative to *http://localhost:3000*

Method | HTTP request | Description
------------- | ------------- | -------------
[**healthCheck**](HealthApi.md#healthCheck) | **GET** /api/v1/health | Health check


# **healthCheck**
> ApiResponseHealthResponse healthCheck()

Check if the API is running

### Example


```typescript
import { createConfiguration, HealthApi } from '';

const configuration = createConfiguration();
const apiInstance = new HealthApi(configuration);

const request = {};

const data = await apiInstance.healthCheck(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters
This endpoint does not need any parameter.


### Return type

**ApiResponseHealthResponse**

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


