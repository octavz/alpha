package com.alpha.web.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ApiError? = null
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> {
            return ApiResponse(success = true, data = data)
        }
        
        fun <T> success(): ApiResponse<T> {
            return ApiResponse(success = true, data = null)
        }
        
        fun <T> error(error: ApiError): ApiResponse<T> {
            return ApiResponse(success = false, error = error)
        }
        
        fun <T> error(code: String, message: String, statusCode: Int): ApiResponse<T> {
            return ApiResponse(success = false, error = ApiError(code, message, statusCode))
        }
    }
}

data class ApiError(
    val code: String,
    val message: String,
    val statusCode: Int
)