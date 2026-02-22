// Temporary backend types for Eden Treaty
export interface App {
  api: {
    auth: {
      login: {
        post: (body: { email: string; password: string }) => Promise<{
          data?: {
            success: boolean
            data?: {
              accessToken: string
              refreshToken: string
              user: {
                id: string
                email: string
                name: string
              }
            }
            error?: {
              code: string
              message: string
              statusCode: number
            }
          }
          error?: any
          status?: number
        }>
      }
      register: {
        post: (body: { email: string; password: string; name: string }) => Promise<{
          data?: {
            success: boolean
            data?: {
              accessToken: string
              refreshToken: string
              user: {
                id: string
                email: string
                name: string
              }
            }
            error?: {
              code: string
              message: string
              statusCode: number
            }
          }
          error?: any
          status?: number
        }>
      }
      refresh: {
        post: (body: { refreshToken: string }) => Promise<{
          data?: {
            success: boolean
            data?: {
              accessToken: string
              refreshToken: string
            }
            error?: {
              code: string
              message: string
              statusCode: number
            }
          }
          error?: any
          status?: number
        }>
      }
      logout: {
        post: () => Promise<{
          data?: {
            success: boolean
            message: string
          }
          error?: any
          status?: number
        }>
      }
      me: {
        get: () => Promise<{
          data?: {
            success: boolean
            data?: {
              user: {
                id: string
                email: string
                name: string
              }
            }
            error?: {
              code: string
              message: string
              statusCode: number
            }
          }
          error?: any
          status?: number
        }>
      }
    }
    health: {
      get: () => Promise<{
        data?: {
          status: string
          timestamp: string
          message: string
        }
        error?: any
        status?: number
      }>
    }
  }
}