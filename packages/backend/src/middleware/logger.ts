import { Elysia } from 'elysia'

export const logger = new Elysia()
  .onRequest(({ request, set }) => {
    console.log(`[${new Date().toISOString()}] ${request.method} ${request.url}`)
  })
  .onError(({ error, request }) => {
    console.error(`[${new Date().toISOString()}] ${request.method} ${request.url} - Error:`, error)
  })
  .onResponse(({ request, response }) => {
    if (response instanceof Response) {
      console.log(`[${new Date().toISOString()}] ${request.method} ${request.url} - ${response.status}`)
    }
  })