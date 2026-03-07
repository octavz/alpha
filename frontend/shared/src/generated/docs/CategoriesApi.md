# .CategoriesApi

All URIs are relative to *http://localhost:3000*

Method | HTTP request | Description
------------- | ------------- | -------------
[**createCategory**](CategoriesApi.md#createCategory) | **POST** /api/v1/categories | Create new category (admin only)
[**deleteCategory**](CategoriesApi.md#deleteCategory) | **DELETE** /api/v1/categories/{categoryId} | Delete category (admin only)
[**getAllCategories**](CategoriesApi.md#getAllCategories) | **GET** /api/v1/categories | Get all active categories
[**getCategory**](CategoriesApi.md#getCategory) | **GET** /api/v1/categories/{categoryId} | Get category by ID
[**updateCategory**](CategoriesApi.md#updateCategory) | **PUT** /api/v1/categories/{categoryId} | Update category (admin only)


# **createCategory**
> ApiResponseCategoryResponse createCategory(createCategoryRequest)


### Example


```typescript
import { createConfiguration, CategoriesApi } from '';
import type { CategoriesApiCreateCategoryRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CategoriesApi(configuration);

const request: CategoriesApiCreateCategoryRequest = {
  
  createCategoryRequest: {
    name: "name_example",
    description: "description_example",
    icon: "icon_example",
    parentId: "parentId_example",
    sortOrder: 1,
    isActive: true,
  },
};

const data = await apiInstance.createCategory(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **createCategoryRequest** | **CreateCategoryRequest**|  |


### Return type

**ApiResponseCategoryResponse**

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

# **deleteCategory**
> ApiResponseUnit deleteCategory()


### Example


```typescript
import { createConfiguration, CategoriesApi } from '';
import type { CategoriesApiDeleteCategoryRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CategoriesApi(configuration);

const request: CategoriesApiDeleteCategoryRequest = {
  
  categoryId: "categoryId_example",
};

const data = await apiInstance.deleteCategory(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **categoryId** | [**string**] |  | defaults to undefined


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

# **getAllCategories**
> ApiResponseListCategoryResponse getAllCategories()


### Example


```typescript
import { createConfiguration, CategoriesApi } from '';

const configuration = createConfiguration();
const apiInstance = new CategoriesApi(configuration);

const request = {};

const data = await apiInstance.getAllCategories(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters
This endpoint does not need any parameter.


### Return type

**ApiResponseListCategoryResponse**

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

# **getCategory**
> ApiResponseCategoryResponse getCategory()


### Example


```typescript
import { createConfiguration, CategoriesApi } from '';
import type { CategoriesApiGetCategoryRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CategoriesApi(configuration);

const request: CategoriesApiGetCategoryRequest = {
  
  categoryId: "categoryId_example",
};

const data = await apiInstance.getCategory(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **categoryId** | [**string**] |  | defaults to undefined


### Return type

**ApiResponseCategoryResponse**

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

# **updateCategory**
> ApiResponseCategoryResponse updateCategory(updateCategoryRequest)


### Example


```typescript
import { createConfiguration, CategoriesApi } from '';
import type { CategoriesApiUpdateCategoryRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CategoriesApi(configuration);

const request: CategoriesApiUpdateCategoryRequest = {
  
  categoryId: "categoryId_example",
  
  updateCategoryRequest: {
    name: "name_example",
    description: "description_example",
    icon: "icon_example",
    parentId: "parentId_example",
    sortOrder: 1,
    isActive: true,
  },
};

const data = await apiInstance.updateCategory(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **updateCategoryRequest** | **UpdateCategoryRequest**|  |
 **categoryId** | [**string**] |  | defaults to undefined


### Return type

**ApiResponseCategoryResponse**

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


