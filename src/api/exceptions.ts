/**
 * 自定义异常基类
 */
export class AppException extends Error {
  constructor(
    public message: string,
    public code: number = 0,
    public originalError?: Error
  ) {
    super(message);
    this.name = this.constructor.name;
    if (Error.captureStackTrace) {
      Error.captureStackTrace(this, this.constructor);
    }
  }
}

/**
 * 网络请求异常
 */
export class NetworkException extends AppException {
  constructor(message: string, originalError?: Error) {
    super(message, 0, originalError);
  }
}

/**
 * API 业务逻辑异常
 */
export class ApiBusinessException extends AppException {
  constructor(
    public apiCode: number,
    message: string,
    originalError?: Error
  ) {
    super(message, apiCode, originalError);
  }
}

/**
 * 认证异常
 */
export class AuthException extends AppException {
  constructor(message: string, originalError?: Error) {
    super(message, 401, originalError);
  }
}

/**
 * 令牌异常
 */
export class TokenException extends AuthException {
  constructor(message: string, originalError?: Error) {
    super(message, originalError);
  }
}

/**
 * 刷新令牌异常
 */
export class TokenRefreshException extends TokenException {
  constructor(message: string, originalError?: Error) {
    super(message, originalError);
  }
}

/**
 * 无效令牌格式异常
 */
export class InvalidTokenFormatException extends TokenException {
  constructor(tokenType: 'access' | 'refresh', originalError?: Error) {
    super(`Invalid ${tokenType} token format`, originalError);
  }
}

/**
 * 令牌缺失异常
 */
export class TokenMissingException extends TokenException {
  constructor(tokenType: 'access' | 'refresh') {
    super(`${tokenType} token is missing`);
  }
}

/**
 * 用户未认证异常
 */
export class UserNotAuthenticatedException extends AuthException {
  constructor() {
    super('User not authenticated');
  }
}
