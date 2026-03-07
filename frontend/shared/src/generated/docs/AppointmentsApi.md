# .AppointmentsApi

All URIs are relative to *http://localhost:3000*

Method | HTTP request | Description
------------- | ------------- | -------------
[**cancelAppointment**](AppointmentsApi.md#cancelAppointment) | **POST** /api/v1/appointments/{appointmentId}/cancel | Cancel appointment
[**createAppointment**](AppointmentsApi.md#createAppointment) | **POST** /api/v1/appointments | Create a new appointment
[**getAppointment**](AppointmentsApi.md#getAppointment) | **GET** /api/v1/appointments/{appointmentId} | Get appointment by ID
[**getBusinessAppointments**](AppointmentsApi.md#getBusinessAppointments) | **GET** /api/v1/appointments/business/{businessId} | Get business appointments
[**getBusinessAvailability**](AppointmentsApi.md#getBusinessAvailability) | **GET** /api/v1/appointments/business/{businessId}/availability | Get business availability
[**searchAppointments**](AppointmentsApi.md#searchAppointments) | **GET** /api/v1/appointments/search | Search appointments
[**updateAppointment**](AppointmentsApi.md#updateAppointment) | **PUT** /api/v1/appointments/{appointmentId} | Update appointment


# **cancelAppointment**
> ApiResponseAppointmentDto cancelAppointment()


### Example


```typescript
import { createConfiguration, AppointmentsApi } from '';
import type { AppointmentsApiCancelAppointmentRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AppointmentsApi(configuration);

const request: AppointmentsApiCancelAppointmentRequest = {
  
  appointmentId: "appointmentId_example",
};

const data = await apiInstance.cancelAppointment(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **appointmentId** | [**string**] |  | defaults to undefined


### Return type

**ApiResponseAppointmentDto**

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

# **createAppointment**
> ApiResponseAppointmentDto createAppointment(createAppointmentRequest)


### Example


```typescript
import { createConfiguration, AppointmentsApi } from '';
import type { AppointmentsApiCreateAppointmentRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AppointmentsApi(configuration);

const request: AppointmentsApiCreateAppointmentRequest = {
  
  createAppointmentRequest: {
    businessId: "businessId_example",
    serviceId: "serviceId_example",
    appointmentDate: new Date('1970-01-01').toISOString().split('T')[0];,
    startTime: {
      hour: 1,
      minute: 1,
      second: 1,
      nano: 1,
    },
    endTime: {
      hour: 1,
      minute: 1,
      second: 1,
      nano: 1,
    },
    servicePointNumber: 1,
    customerName: "customerName_example",
    customerEmail: "customerEmail_example",
    customerPhone: "customerPhone_example",
    customerNotes: "customerNotes_example",
  },
};

const data = await apiInstance.createAppointment(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **createAppointmentRequest** | **CreateAppointmentRequest**|  |


### Return type

**ApiResponseAppointmentDto**

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

# **getAppointment**
> ApiResponseAppointmentDto getAppointment()


### Example


```typescript
import { createConfiguration, AppointmentsApi } from '';
import type { AppointmentsApiGetAppointmentRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AppointmentsApi(configuration);

const request: AppointmentsApiGetAppointmentRequest = {
  
  appointmentId: "appointmentId_example",
};

const data = await apiInstance.getAppointment(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **appointmentId** | [**string**] |  | defaults to undefined


### Return type

**ApiResponseAppointmentDto**

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

# **getBusinessAppointments**
> ApiResponsePageAppointmentDto getBusinessAppointments()


### Example


```typescript
import { createConfiguration, AppointmentsApi } from '';
import type { AppointmentsApiGetBusinessAppointmentsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AppointmentsApi(configuration);

const request: AppointmentsApiGetBusinessAppointmentsRequest = {
  
  businessId: "businessId_example",
  
  pageable: {
    page: 0,
    size: 1,
    sort: [
      "sort_example",
    ],
  },
};

const data = await apiInstance.getBusinessAppointments(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **businessId** | [**string**] |  | defaults to undefined
 **pageable** | **Pageable** |  | defaults to undefined


### Return type

**ApiResponsePageAppointmentDto**

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

# **getBusinessAvailability**
> ApiResponseListString getBusinessAvailability()


### Example


```typescript
import { createConfiguration, AppointmentsApi } from '';
import type { AppointmentsApiGetBusinessAvailabilityRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AppointmentsApi(configuration);

const request: AppointmentsApiGetBusinessAvailabilityRequest = {
  
  businessId: "businessId_example",
  
  date: new Date('1970-01-01').toISOString().split('T')[0];,
};

const data = await apiInstance.getBusinessAvailability(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **businessId** | [**string**] |  | defaults to undefined
 **date** | [**string**] |  | defaults to undefined


### Return type

**ApiResponseListString**

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

# **searchAppointments**
> ApiResponsePageAppointmentDto searchAppointments()


### Example


```typescript
import { createConfiguration, AppointmentsApi } from '';
import type { AppointmentsApiSearchAppointmentsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AppointmentsApi(configuration);

const request: AppointmentsApiSearchAppointmentsRequest = {
  
  pageable: {
    page: 0,
    size: 1,
    sort: [
      "sort_example",
    ],
  },
  
  businessId: "businessId_example",
  
  customerId: "customerId_example",
  
  status: "PENDING",
  
  startDate: new Date('1970-01-01').toISOString().split('T')[0];,
  
  endDate: new Date('1970-01-01').toISOString().split('T')[0];,
};

const data = await apiInstance.searchAppointments(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pageable** | **Pageable** |  | defaults to undefined
 **businessId** | [**string**] |  | (optional) defaults to undefined
 **customerId** | [**string**] |  | (optional) defaults to undefined
 **status** | [**&#39;PENDING&#39; | &#39;CONFIRMED&#39; | &#39;COMPLETED&#39; | &#39;CANCELLED&#39; | &#39;NO_SHOW&#39;**]**Array<&#39;PENDING&#39; &#124; &#39;CONFIRMED&#39; &#124; &#39;COMPLETED&#39; &#124; &#39;CANCELLED&#39; &#124; &#39;NO_SHOW&#39;>** |  | (optional) defaults to undefined
 **startDate** | [**string**] |  | (optional) defaults to undefined
 **endDate** | [**string**] |  | (optional) defaults to undefined


### Return type

**ApiResponsePageAppointmentDto**

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

# **updateAppointment**
> ApiResponseAppointmentDto updateAppointment(updateAppointmentRequest)


### Example


```typescript
import { createConfiguration, AppointmentsApi } from '';
import type { AppointmentsApiUpdateAppointmentRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AppointmentsApi(configuration);

const request: AppointmentsApiUpdateAppointmentRequest = {
  
  appointmentId: "appointmentId_example",
  
  updateAppointmentRequest: {
    appointmentDate: new Date('1970-01-01').toISOString().split('T')[0];,
    startTime: {
      hour: 1,
      minute: 1,
      second: 1,
      nano: 1,
    },
    endTime: {
      hour: 1,
      minute: 1,
      second: 1,
      nano: 1,
    },
    servicePointNumber: 1,
    customerName: "customerName_example",
    customerEmail: "customerEmail_example",
    customerPhone: "customerPhone_example",
    customerNotes: "customerNotes_example",
    status: "PENDING",
    cancelledReason: "cancelledReason_example",
  },
};

const data = await apiInstance.updateAppointment(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **updateAppointmentRequest** | **UpdateAppointmentRequest**|  |
 **appointmentId** | [**string**] |  | defaults to undefined


### Return type

**ApiResponseAppointmentDto**

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


