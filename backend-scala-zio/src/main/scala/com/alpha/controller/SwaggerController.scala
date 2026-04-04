package com.alpha.controller

import zio.*
import zio.http.*
import zio.json.*

object SwaggerController:

  private val swaggerHtml = """<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Alpha API - Swagger UI</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swagger-ui-dist@5/swagger-ui.css" />
</head>
<body>
  <div id="swagger-ui"></div>
  <script src="https://cdn.jsdelivr.net/npm/swagger-ui-dist@5/swagger-ui-bundle.js" crossorigin></script>
  <script>
    window.onload = () => {
      window.ui = SwaggerUIBundle({
        url: '/api-docs/openapi.json',
        dom_id: '#swagger-ui',
        presets: [SwaggerUIBundle.presets.apis],
        layout: "BaseLayout"
      });
    };
  </script>
</body>
</html>"""

  private val openApiJson = """{
  "openapi": "3.0.3",
  "info": {
    "title": "Alpha API",
    "version": "1.0.0",
    "description": "Business directory platform API"
  },
  "servers": [
    { "url": "http://localhost:3000", "description": "Development server" }
  ],
  "tags": [
    { "name": "Health" },
    { "name": "Auth" },
    { "name": "Categories" },
    { "name": "Regions" },
    { "name": "Businesses" },
    { "name": "Appointments" },
    { "name": "Reviews" },
    { "name": "Services" },
    { "name": "Business Hours" }
  ],
  "paths": {
    "/api/v1/health": {
      "get": { "tags": ["Health"], "summary": "Health check", "responses": { "200": { "description": "OK" } } }
    },
    "/api/v1/auth/register": {
      "post": { "tags": ["Auth"], "summary": "Register a new user", "requestBody": { "required": true, "content": { "application/json": { "schema": { "$ref": "#/components/schemas/RegisterUserRequest" } } } }, "responses": { "200": { "description": "Registered" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/auth/login": {
      "post": { "tags": ["Auth"], "summary": "Login", "requestBody": { "required": true, "content": { "application/json": { "schema": { "$ref": "#/components/schemas/LoginUserRequest" } } } }, "responses": { "200": { "description": "Logged in" }, "401": { "description": "Unauthorized" } } }
    },
    "/api/v1/auth/refresh": {
      "post": { "tags": ["Auth"], "summary": "Refresh token", "requestBody": { "required": true, "content": { "application/json": { "schema": { "$ref": "#/components/schemas/RefreshTokenRequest" } } } }, "responses": { "200": { "description": "Tokens refreshed" }, "401": { "description": "Unauthorized" } } }
    },
    "/api/v1/auth/logout": {
      "post": { "tags": ["Auth"], "summary": "Logout", "parameters": [{ "name": "X-Session-Id", "in": "header", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "204": { "description": "Logged out" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/auth/verify-email": {
      "post": { "tags": ["Auth"], "summary": "Verify email", "requestBody": { "required": true, "content": { "application/json": { "schema": { "$ref": "#/components/schemas/VerifyEmailRequest" } } } }, "responses": { "200": { "description": "Email verified" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/auth/forgot-password": {
      "post": { "tags": ["Auth"], "summary": "Request password reset", "requestBody": { "required": true, "content": { "application/json": { "schema": { "$ref": "#/components/schemas/ForgotPasswordRequest" } } } }, "responses": { "200": { "description": "Reset email sent" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/auth/reset-password": {
      "post": { "tags": ["Auth"], "summary": "Reset password", "requestBody": { "required": true, "content": { "application/json": { "schema": { "$ref": "#/components/schemas/ResetPasswordRequest" } } } }, "responses": { "200": { "description": "Password reset" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/auth/change-password": {
      "post": { "tags": ["Auth"], "summary": "Change password", "parameters": [{ "name": "X-User-Id", "in": "header", "required": true, "schema": { "type": "string", "format": "uuid" } }], "requestBody": { "required": true, "content": { "application/json": { "schema": { "$ref": "#/components/schemas/ChangePasswordRequest" } } } }, "responses": { "200": { "description": "Password changed" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/auth/me": {
      "get": { "tags": ["Auth"], "summary": "Get current user profile", "parameters": [{ "name": "X-User-Id", "in": "header", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "200": { "description": "User profile" }, "404": { "description": "Not found" } } },
      "put": { "tags": ["Auth"], "summary": "Update current user profile", "parameters": [{ "name": "X-User-Id", "in": "header", "required": true, "schema": { "type": "string", "format": "uuid" } }], "requestBody": { "required": true, "content": { "application/json": { "schema": { "$ref": "#/components/schemas/UpdateProfileRequest" } } } }, "responses": { "200": { "description": "Profile updated" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/categories": {
      "get": { "tags": ["Categories"], "summary": "List all categories", "responses": { "200": { "description": "Category list" } } },
      "post": { "tags": ["Categories"], "summary": "Create category", "requestBody": { "required": true, "content": { "application/json": { "schema": { "$ref": "#/components/schemas/CreateCategoryRequest" } } } }, "responses": { "200": { "description": "Category created" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/categories/{id}": {
      "get": { "tags": ["Categories"], "summary": "Get category by ID", "parameters": [{ "name": "id", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "200": { "description": "Category" }, "404": { "description": "Not found" } } },
      "put": { "tags": ["Categories"], "summary": "Update category", "parameters": [{ "name": "id", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "requestBody": { "required": true, "content": { "application/json": { "schema": { "$ref": "#/components/schemas/UpdateCategoryRequest" } } } }, "responses": { "200": { "description": "Category updated" }, "400": { "description": "Bad request" } } },
      "delete": { "tags": ["Categories"], "summary": "Delete category", "parameters": [{ "name": "id", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "204": { "description": "Deleted" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/regions": {
      "get": { "tags": ["Regions"], "summary": "List all regions", "responses": { "200": { "description": "Region list" } } },
      "post": { "tags": ["Regions"], "summary": "Create region", "requestBody": { "required": true, "content": { "application/json": { "schema": { "$ref": "#/components/schemas/CreateRegionRequest" } } } }, "responses": { "200": { "description": "Region created" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/regions/{id}": {
      "get": { "tags": ["Regions"], "summary": "Get region by ID", "parameters": [{ "name": "id", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "200": { "description": "Region" }, "404": { "description": "Not found" } } },
      "put": { "tags": ["Regions"], "summary": "Update region", "parameters": [{ "name": "id", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "requestBody": { "required": true, "content": { "application/json": { "schema": { "$ref": "#/components/schemas/UpdateRegionRequest" } } } }, "responses": { "200": { "description": "Region updated" }, "400": { "description": "Bad request" } } },
      "delete": { "tags": ["Regions"], "summary": "Delete region", "parameters": [{ "name": "id", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "204": { "description": "Deleted" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/regions/code/{code}": {
      "get": { "tags": ["Regions"], "summary": "Get region by code", "parameters": [{ "name": "code", "in": "path", "required": true, "schema": { "type": "string" } }], "responses": { "200": { "description": "Region" }, "404": { "description": "Not found" } } }
    },
    "/api/v1/regions/search": {
      "get": { "tags": ["Regions"], "summary": "Search regions", "parameters": [{ "name": "q", "in": "query", "required": true, "schema": { "type": "string" } }], "responses": { "200": { "description": "Region list" }, "400": { "description": "Missing query" } } }
    },
    "/api/v1/businesses": {
      "get": { "tags": ["Businesses"], "summary": "List businesses", "responses": { "200": { "description": "Business list" } } },
      "post": { "tags": ["Businesses"], "summary": "Create business", "parameters": [{ "name": "X-User-Id", "in": "header", "required": true, "schema": { "type": "string", "format": "uuid" } }], "requestBody": { "required": true, "content": { "application/json": { "schema": { "$ref": "#/components/schemas/CreateBusinessRequest" } } } }, "responses": { "200": { "description": "Business created" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/businesses/{id}": {
      "get": { "tags": ["Businesses"], "summary": "Get business by ID", "parameters": [{ "name": "id", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "200": { "description": "Business" }, "404": { "description": "Not found" } } },
      "put": { "tags": ["Businesses"], "summary": "Update business", "parameters": [{ "name": "id", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "requestBody": { "required": true, "content": { "application/json": { "schema": { "$ref": "#/components/schemas/UpdateBusinessRequest" } } } }, "responses": { "200": { "description": "Business updated" }, "400": { "description": "Bad request" } } },
      "delete": { "tags": ["Businesses"], "summary": "Delete business", "parameters": [{ "name": "id", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "204": { "description": "Deleted" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/businesses/slug/{slug}": {
      "get": { "tags": ["Businesses"], "summary": "Get business by slug", "parameters": [{ "name": "slug", "in": "path", "required": true, "schema": { "type": "string" } }], "responses": { "200": { "description": "Business" }, "404": { "description": "Not found" } } }
    },
    "/api/v1/businesses/search": {
      "get": { "tags": ["Businesses"], "summary": "Search businesses", "parameters": [{ "name": "q", "in": "query", "required": true, "schema": { "type": "string" } }], "responses": { "200": { "description": "Business list" }, "400": { "description": "Missing query" } } }
    },
    "/api/v1/businesses/my-businesses": {
      "get": { "tags": ["Businesses"], "summary": "Get current user's businesses", "parameters": [{ "name": "X-User-Id", "in": "header", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "200": { "description": "Business list" }, "400": { "description": "Missing header" } } }
    },
    "/api/v1/businesses/region/{regionId}": {
      "get": { "tags": ["Businesses"], "summary": "Get businesses by region", "parameters": [{ "name": "regionId", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "200": { "description": "Business list" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/businesses/category/{categoryId}": {
      "get": { "tags": ["Businesses"], "summary": "Get businesses by category", "parameters": [{ "name": "categoryId", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "200": { "description": "Business list" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/businesses/featured": {
      "get": { "tags": ["Businesses"], "summary": "Get featured businesses", "parameters": [{ "name": "limit", "in": "query", "schema": { "type": "integer", "default": 20 } }], "responses": { "200": { "description": "Business list" } } }
    },
    "/api/v1/businesses/{id}/verify": {
      "post": { "tags": ["Businesses"], "summary": "Verify business (admin)", "parameters": [{ "name": "id", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "200": { "description": "Business verified" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/appointments": {
      "get": { "tags": ["Appointments"], "summary": "List appointments", "parameters": [{ "name": "businessId", "in": "query", "schema": { "type": "string", "format": "uuid" } }, { "name": "userId", "in": "query", "schema": { "type": "string", "format": "uuid" } }], "responses": { "200": { "description": "Appointment list" }, "400": { "description": "Missing parameter" } } },
      "post": { "tags": ["Appointments"], "summary": "Create appointment", "requestBody": { "required": true, "content": { "application/json": { "schema": { "$ref": "#/components/schemas/CreateAppointmentRequest" } } } }, "responses": { "200": { "description": "Appointment created" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/appointments/{id}": {
      "get": { "tags": ["Appointments"], "summary": "Get appointment by ID", "parameters": [{ "name": "id", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "200": { "description": "Appointment" }, "404": { "description": "Not found" } } },
      "put": { "tags": ["Appointments"], "summary": "Update appointment", "parameters": [{ "name": "id", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "requestBody": { "required": true, "content": { "application/json": { "schema": { "$ref": "#/components/schemas/UpdateAppointmentRequest" } } } }, "responses": { "200": { "description": "Appointment updated" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/appointments/search": {
      "get": { "tags": ["Appointments"], "summary": "Search appointments", "parameters": [{ "name": "businessId", "in": "query", "schema": { "type": "string", "format": "uuid" } }, { "name": "userId", "in": "query", "schema": { "type": "string", "format": "uuid" } }, { "name": "status", "in": "query", "schema": { "type": "string" } }, { "name": "date", "in": "query", "schema": { "type": "string", "format": "date" } }], "responses": { "200": { "description": "Appointment list" } } }
    },
    "/api/v1/appointments/business/{businessId}": {
      "get": { "tags": ["Appointments"], "summary": "Get appointments by business", "parameters": [{ "name": "businessId", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "200": { "description": "Appointment list" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/appointments/business/{businessId}/availability": {
      "get": { "tags": ["Appointments"], "summary": "Get business availability", "parameters": [{ "name": "businessId", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }, { "name": "date", "in": "query", "required": true, "schema": { "type": "string", "format": "date" } }, { "name": "serviceId", "in": "query", "schema": { "type": "string", "format": "uuid" } }], "responses": { "200": { "description": "Availability slots" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/appointments/{id}/cancel": {
      "post": { "tags": ["Appointments"], "summary": "Cancel appointment", "parameters": [{ "name": "id", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "requestBody": { "required": false, "content": { "application/json": { "schema": { "$ref": "#/components/schemas/CancelAppointmentRequest" } } } }, "responses": { "200": { "description": "Appointment cancelled" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/reviews": {
      "get": { "tags": ["Reviews"], "summary": "List reviews (redirect)", "responses": { "400": { "description": "Use /reviews/business/{id} or /reviews/user/{id}" } } },
      "post": { "tags": ["Reviews"], "summary": "Create review", "requestBody": { "required": true, "content": { "application/json": { "schema": { "$ref": "#/components/schemas/CreateReviewRequest" } } } }, "responses": { "200": { "description": "Review created" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/reviews/{id}": {
      "get": { "tags": ["Reviews"], "summary": "Get review by ID", "parameters": [{ "name": "id", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "200": { "description": "Review" }, "404": { "description": "Not found" } } },
      "delete": { "tags": ["Reviews"], "summary": "Delete review", "parameters": [{ "name": "id", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "204": { "description": "Deleted" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/reviews/business/{businessId}": {
      "get": { "tags": ["Reviews"], "summary": "Get reviews by business", "parameters": [{ "name": "businessId", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "200": { "description": "Review list" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/reviews/business/{businessId}/approved": {
      "get": { "tags": ["Reviews"], "summary": "Get approved reviews by business", "parameters": [{ "name": "businessId", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "200": { "description": "Review list" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/reviews/user/{userId}": {
      "get": { "tags": ["Reviews"], "summary": "Get reviews by user", "parameters": [{ "name": "userId", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "200": { "description": "Review list" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/reviews/business/{businessId}/average-rating": {
      "get": { "tags": ["Reviews"], "summary": "Get average rating for business", "parameters": [{ "name": "businessId", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "200": { "description": "Average rating" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/reviews/{id}/approve": {
      "post": { "tags": ["Reviews"], "summary": "Approve review", "parameters": [{ "name": "id", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "200": { "description": "Review approved" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/services/{id}": {
      "get": { "tags": ["Services"], "summary": "Get service by ID", "parameters": [{ "name": "id", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "200": { "description": "Service" }, "404": { "description": "Not found" } } },
      "put": { "tags": ["Services"], "summary": "Update service", "parameters": [{ "name": "id", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "requestBody": { "required": true, "content": { "application/json": { "schema": { "$ref": "#/components/schemas/UpdateServiceRequest" } } } }, "responses": { "200": { "description": "Service updated" }, "400": { "description": "Bad request" } } },
      "delete": { "tags": ["Services"], "summary": "Delete service", "parameters": [{ "name": "id", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "204": { "description": "Deleted" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/services/business/{businessId}": {
      "get": { "tags": ["Services"], "summary": "Get services by business", "parameters": [{ "name": "businessId", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "200": { "description": "Service list" }, "400": { "description": "Bad request" } } },
      "post": { "tags": ["Services"], "summary": "Create service for business", "parameters": [{ "name": "businessId", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "requestBody": { "required": true, "content": { "application/json": { "schema": { "$ref": "#/components/schemas/CreateServiceRequest" } } } }, "responses": { "200": { "description": "Service created" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/services/business/{businessId}/active": {
      "get": { "tags": ["Services"], "summary": "Get active services by business", "parameters": [{ "name": "businessId", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "200": { "description": "Service list" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/business-hours/{id}": {
      "get": { "tags": ["Business Hours"], "summary": "Get business hours by ID", "parameters": [{ "name": "id", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "200": { "description": "Business hours" }, "404": { "description": "Not found" } } },
      "put": { "tags": ["Business Hours"], "summary": "Update business hours", "parameters": [{ "name": "id", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "requestBody": { "required": true, "content": { "application/json": { "schema": { "$ref": "#/components/schemas/UpdateBusinessHoursRequest" } } } }, "responses": { "200": { "description": "Business hours updated" }, "400": { "description": "Bad request" } } },
      "delete": { "tags": ["Business Hours"], "summary": "Delete business hours", "parameters": [{ "name": "id", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "204": { "description": "Deleted" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/business-hours/business/{businessId}": {
      "get": { "tags": ["Business Hours"], "summary": "Get business hours by business", "parameters": [{ "name": "businessId", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "200": { "description": "Business hours list" }, "400": { "description": "Bad request" } } },
      "delete": { "tags": ["Business Hours"], "summary": "Delete all business hours for business", "parameters": [{ "name": "businessId", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }], "responses": { "204": { "description": "Deleted" }, "400": { "description": "Bad request" } } }
    },
    "/api/v1/business-hours/business/{businessId}/day/{dayOfWeek}": {
      "get": { "tags": ["Business Hours"], "summary": "Get business hours by business and day", "parameters": [{ "name": "businessId", "in": "path", "required": true, "schema": { "type": "string", "format": "uuid" } }, { "name": "dayOfWeek", "in": "path", "required": true, "schema": { "type": "integer" } }], "responses": { "200": { "description": "Business hours" }, "404": { "description": "Not found" } } }
    },
    "/api/v1/business-hours": {
      "post": { "tags": ["Business Hours"], "summary": "Create business hours", "requestBody": { "required": true, "content": { "application/json": { "schema": { "$ref": "#/components/schemas/CreateBusinessHoursRequest" } } } }, "responses": { "200": { "description": "Business hours created" }, "400": { "description": "Bad request" } } }
    }
  },
  "components": {
    "schemas": {
      "RegisterUserRequest": { "type": "object", "required": ["email", "password"], "properties": { "email": { "type": "string", "format": "email" }, "password": { "type": "string", "minLength": 8 }, "name": { "type": "string" }, "phone": { "type": "string" }, "role": { "type": "string" }, "regionId": { "type": "string", "format": "uuid" } } },
      "LoginUserRequest": { "type": "object", "required": ["email", "password"], "properties": { "email": { "type": "string", "format": "email" }, "password": { "type": "string" } } },
      "RefreshTokenRequest": { "type": "object", "required": ["refreshToken"], "properties": { "refreshToken": { "type": "string" } } },
      "VerifyEmailRequest": { "type": "object", "required": ["token"], "properties": { "token": { "type": "string" } } },
      "ForgotPasswordRequest": { "type": "object", "required": ["email"], "properties": { "email": { "type": "string", "format": "email" } } },
      "ResetPasswordRequest": { "type": "object", "required": ["token", "newPassword"], "properties": { "token": { "type": "string" }, "newPassword": { "type": "string", "minLength": 8 } } },
      "ChangePasswordRequest": { "type": "object", "required": ["currentPassword", "newPassword"], "properties": { "currentPassword": { "type": "string" }, "newPassword": { "type": "string", "minLength": 8 } } },
      "UpdateProfileRequest": { "type": "object", "properties": { "name": { "type": "string" }, "phone": { "type": "string" }, "avatarUrl": { "type": "string", "format": "uri" } } },
      "CreateCategoryRequest": { "type": "object", "required": ["name", "slug"], "properties": { "name": { "type": "string" }, "slug": { "type": "string" }, "description": { "type": "string" }, "icon": { "type": "string" }, "parentId": { "type": "string", "format": "uuid" }, "sortOrder": { "type": "integer", "default": 0 } } },
      "UpdateCategoryRequest": { "type": "object", "properties": { "name": { "type": "string" }, "description": { "type": "string" }, "icon": { "type": "string" }, "parentId": { "type": "string", "format": "uuid" }, "sortOrder": { "type": "integer" }, "isActive": { "type": "boolean" } } },
      "CreateRegionRequest": { "type": "object", "required": ["name", "code", "country", "timezone"], "properties": { "name": { "type": "string" }, "code": { "type": "string" }, "country": { "type": "string" }, "timezone": { "type": "string" } } },
      "UpdateRegionRequest": { "type": "object", "properties": { "name": { "type": "string" }, "code": { "type": "string" }, "country": { "type": "string" }, "timezone": { "type": "string" }, "isActive": { "type": "boolean" } } },
      "CreateBusinessRequest": { "type": "object", "required": ["name", "slug", "categoryId", "regionId"], "properties": { "name": { "type": "string" }, "slug": { "type": "string" }, "description": { "type": "string" }, "email": { "type": "string", "format": "email" }, "phone": { "type": "string" }, "website": { "type": "string", "format": "uri" }, "addressLine1": { "type": "string" }, "addressLine2": { "type": "string" }, "city": { "type": "string" }, "state": { "type": "string" }, "zipCode": { "type": "string" }, "country": { "type": "string" }, "latitude": { "type": "number" }, "longitude": { "type": "number" }, "categoryId": { "type": "string", "format": "uuid" }, "regionId": { "type": "string", "format": "uuid" } } },
      "UpdateBusinessRequest": { "type": "object", "properties": { "name": { "type": "string" }, "description": { "type": "string" }, "email": { "type": "string", "format": "email" }, "phone": { "type": "string" }, "website": { "type": "string", "format": "uri" }, "addressLine1": { "type": "string" }, "addressLine2": { "type": "string" }, "city": { "type": "string" }, "state": { "type": "string" }, "zipCode": { "type": "string" }, "country": { "type": "string" }, "latitude": { "type": "number" }, "longitude": { "type": "number" }, "categoryId": { "type": "string", "format": "uuid" }, "regionId": { "type": "string", "format": "uuid" }, "logoUrl": { "type": "string", "format": "uri" }, "coverImageUrl": { "type": "string", "format": "uri" } } },
      "CreateAppointmentRequest": { "type": "object", "required": ["businessId", "appointmentDate", "startTime", "endTime", "customerName", "customerEmail"], "properties": { "businessId": { "type": "string", "format": "uuid" }, "userId": { "type": "string", "format": "uuid" }, "serviceId": { "type": "string", "format": "uuid" }, "appointmentDate": { "type": "string", "format": "date" }, "startTime": { "type": "string", "format": "time" }, "endTime": { "type": "string", "format": "time" }, "servicePointNumber": { "type": "integer" }, "customerName": { "type": "string" }, "customerEmail": { "type": "string", "format": "email" }, "customerPhone": { "type": "string" }, "customerNotes": { "type": "string" } } },
      "UpdateAppointmentRequest": { "type": "object", "properties": { "status": { "type": "string", "enum": ["PENDING", "CONFIRMED", "COMPLETED", "CANCELLED", "NO_SHOW"] }, "customerNotes": { "type": "string" } } },
      "CancelAppointmentRequest": { "type": "object", "properties": { "reason": { "type": "string" } } },
      "CreateReviewRequest": { "type": "object", "required": ["businessId", "userId", "rating"], "properties": { "businessId": { "type": "string", "format": "uuid" }, "userId": { "type": "string", "format": "uuid" }, "appointmentId": { "type": "string", "format": "uuid" }, "rating": { "type": "integer", "minimum": 1, "maximum": 5 }, "title": { "type": "string" }, "comment": { "type": "string" } } },
      "CreateServiceRequest": { "type": "object", "required": ["name", "durationMinutes"], "properties": { "name": { "type": "string" }, "description": { "type": "string" }, "durationMinutes": { "type": "integer" }, "price": { "type": "number" }, "isActive": { "type": "boolean", "default": true }, "sortOrder": { "type": "integer", "default": 0 } } },
      "UpdateServiceRequest": { "type": "object", "properties": { "name": { "type": "string" }, "description": { "type": "string" }, "durationMinutes": { "type": "integer" }, "price": { "type": "number" }, "isActive": { "type": "boolean" }, "sortOrder": { "type": "integer" } } },
      "CreateBusinessHoursRequest": { "type": "object", "required": ["businessId", "dayOfWeek"], "properties": { "businessId": { "type": "string", "format": "uuid" }, "dayOfWeek": { "type": "integer", "minimum": 0, "maximum": 6 }, "openTime": { "type": "string" }, "closeTime": { "type": "string" }, "isClosed": { "type": "boolean", "default": false } } },
      "UpdateBusinessHoursRequest": { "type": "object", "properties": { "openTime": { "type": "string" }, "closeTime": { "type": "string" }, "isClosed": { "type": "boolean" } } }
    }
  }
}"""

  val routes = Routes(
    Method.GET / "swagger" -> handler(Response.html(swaggerHtml)),
    Method.GET / "api-docs" / "openapi.json" -> handler(Response.json(openApiJson))
  )
