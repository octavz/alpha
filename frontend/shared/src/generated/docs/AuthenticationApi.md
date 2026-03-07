# .AuthenticationApi

All URIs are relative to *http://localhost:3000*

Method | HTTP request | Description
------------- | ------------- | -------------
[**changePassword**](AuthenticationApi.md#changePassword) | **POST** /api/v1/auth/change-password | Change password
[**forgotPassword**](AuthenticationApi.md#forgotPassword) | **POST** /api/v1/auth/forgot-password | Request password reset
[**getCurrentUser**](AuthenticationApi.md#getCurrentUser) | **GET** /api/v1/auth/me | Get current user profile
[**login**](AuthenticationApi.md#login) | **POST** /api/v1/auth/login | Login user
[**logout**](AuthenticationApi.md#logout) | **POST** /api/v1/auth/logout | Logout user
[**refreshToken**](AuthenticationApi.md#refreshToken) | **POST** /api/v1/auth/refresh | Refresh access token
[**register**](AuthenticationApi.md#register) | **POST** /api/v1/auth/register | Register a new user
[**resetPassword**](AuthenticationApi.md#resetPassword) | **POST** /api/v1/auth/reset-password | Reset password with token
[**updateProfile**](AuthenticationApi.md#updateProfile) | **PUT** /api/v1/auth/me | Update user profile
[**verifyEmail**](AuthenticationApi.md#verifyEmail) | **POST** /api/v1/auth/verify-email | Verify email address


# **changePassword**
> ApiResponseUnit changePassword()


### Example


```typescript
import { createConfiguration, AuthenticationApi } from '';
import type { AuthenticationApiChangePasswordRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AuthenticationApi(configuration);

const request: AuthenticationApiChangePasswordRequest = {
  
  currentPassword: "currentPassword_example",
  
  newPassword: "newPassword_example",
};

const data = await apiInstance.changePassword(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **currentPassword** | [**string**] |  | defaults to undefined
 **newPassword** | [**string**] |  | defaults to undefined


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

# **forgotPassword**
> ApiResponseUnit forgotPassword(forgotPasswordRequest)


### Example


```typescript
import { createConfiguration, AuthenticationApi } from '';
import type { AuthenticationApiForgotPasswordRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AuthenticationApi(configuration);

const request: AuthenticationApiForgotPasswordRequest = {
  
  forgotPasswordRequest: {
    email: "email_example",
  },
};

const data = await apiInstance.forgotPassword(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **forgotPasswordRequest** | **ForgotPasswordRequest**|  |


### Return type

**ApiResponseUnit**

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

# **getCurrentUser**
> ApiResponseUserResponse getCurrentUser()


### Example


```typescript
import { createConfiguration, AuthenticationApi } from '';

const configuration = createConfiguration();
const apiInstance = new AuthenticationApi(configuration);

const request = {};

const data = await apiInstance.getCurrentUser(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters
This endpoint does not need any parameter.


### Return type

**ApiResponseUserResponse**

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

# **login**
> ApiResponseAuthResponse login(loginRequest)


### Example


```typescript
import { createConfiguration, AuthenticationApi } from '';
import type { AuthenticationApiLoginRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AuthenticationApi(configuration);

const request: AuthenticationApiLoginRequest = {
  
  loginRequest: {
    email: "email_example",
    password: "password_example",
  },
};

const data = await apiInstance.login(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **loginRequest** | **LoginRequest**|  |


### Return type

**ApiResponseAuthResponse**

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

# **logout**
> ApiResponseUnit logout(logoutRequest)


### Example


```typescript
import { createConfiguration, AuthenticationApi } from '';
import type { AuthenticationApiLogoutRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AuthenticationApi(configuration);

const request: AuthenticationApiLogoutRequest = {
  
  logoutRequest: {
    sessionId: "sessionId_example",
  },
};

const data = await apiInstance.logout(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **logoutRequest** | **LogoutRequest**|  |


### Return type

**ApiResponseUnit**

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

# **refreshToken**
> ApiResponseTokenRefreshResponse refreshToken(refreshTokenRequest)


### Example


```typescript
import { createConfiguration, AuthenticationApi } from '';
import type { AuthenticationApiRefreshTokenRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AuthenticationApi(configuration);

const request: AuthenticationApiRefreshTokenRequest = {
  
  refreshTokenRequest: {
    refreshToken: "refreshToken_example",
  },
};

const data = await apiInstance.refreshToken(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **refreshTokenRequest** | **RefreshTokenRequest**|  |


### Return type

**ApiResponseTokenRefreshResponse**

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

# **register**
> ApiResponseAuthResponse register(registerRequest)


### Example


```typescript
import { createConfiguration, AuthenticationApi } from '';
import type { AuthenticationApiRegisterRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AuthenticationApi(configuration);

const request: AuthenticationApiRegisterRequest = {
  
  registerRequest: {
    email: "email_example",
    password: "password_example",
    name: "name_example",
    phone: "phone_example",
    role: "CUSTOMER",
    regionId: "regionId_example",
    requiredRole: "CUSTOMER",
  },
};

const data = await apiInstance.register(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **registerRequest** | **RegisterRequest**|  |


### Return type

**ApiResponseAuthResponse**

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

# **resetPassword**
> ApiResponseUnit resetPassword(resetPasswordRequest)


### Example


```typescript
import { createConfiguration, AuthenticationApi } from '';
import type { AuthenticationApiResetPasswordRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AuthenticationApi(configuration);

const request: AuthenticationApiResetPasswordRequest = {
  
  resetPasswordRequest: {
    token: "token_example",
    newPassword: "newPassword_example",
  },
};

const data = await apiInstance.resetPassword(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **resetPasswordRequest** | **ResetPasswordRequest**|  |


### Return type

**ApiResponseUnit**

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

# **updateProfile**
> ApiResponseUserResponse updateProfile()


### Example


```typescript
import { createConfiguration, AuthenticationApi } from '';
import type { AuthenticationApiUpdateProfileRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AuthenticationApi(configuration);

const request: AuthenticationApiUpdateProfileRequest = {
  
  name: "name_example",
  
  phone: "phone_example",
  
  avatarUrl: "avatarUrl_example",
};

const data = await apiInstance.updateProfile(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | [**string**] |  | (optional) defaults to undefined
 **phone** | [**string**] |  | (optional) defaults to undefined
 **avatarUrl** | [**string**] |  | (optional) defaults to undefined


### Return type

**ApiResponseUserResponse**

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

# **verifyEmail**
> ApiResponseUnit verifyEmail(verifyEmailRequest)


### Example


```typescript
import { createConfiguration, AuthenticationApi } from '';
import type { AuthenticationApiVerifyEmailRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AuthenticationApi(configuration);

const request: AuthenticationApiVerifyEmailRequest = {
  
  verifyEmailRequest: {
    token: "token_example",
  },
};

const data = await apiInstance.verifyEmail(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **verifyEmailRequest** | **VerifyEmailRequest**|  |


### Return type

**ApiResponseUnit**

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


