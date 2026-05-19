import axios, { AxiosError, AxiosHeaders, type AxiosInstance, type AxiosRequestConfig, type InternalAxiosRequestConfig } from 'axios';
import { authService } from '../services/auth';
import type { ApiErrorResponse, ApiResponse } from '../types/api';

export type ApiClientSettings = {
  defaultTimeoutMs: number;
  queryTimeoutMs: number;
  baseUrl: string;
};

export type RequestTimeoutScope = 'default' | 'query';

export type CassdioRequestConfig = AxiosRequestConfig & {
  timeoutScope?: RequestTimeoutScope;
  correlationId?: string;
};

export class ApiClientError extends Error {
  status?: number;
  code?: string;
  correlationId?: string;

  constructor(message: string, options: { status?: number; code?: string; correlationId?: string } = {}) {
    super(message);
    this.name = 'ApiClientError';
    this.status = options.status;
    this.code = options.code;
    this.correlationId = options.correlationId;
  }
}

const defaultSettings: ApiClientSettings = {
  defaultTimeoutMs: 3000,
  queryTimeoutMs: 3000,
  baseUrl: import.meta.env.VITE_API_BASE_URL ?? '',
};

let runtimeSettings: ApiClientSettings = { ...defaultSettings };

function resolveTimeout(config: CassdioRequestConfig) {
  return config.timeoutScope === 'query' ? runtimeSettings.queryTimeoutMs : runtimeSettings.defaultTimeoutMs;
}

function mapError(error: AxiosError<ApiErrorResponse>) {
  const payload = error.response?.data;

  return new ApiClientError(payload?.message ?? error.message, {
    status: error.response?.status,
    code: payload?.code,
    correlationId: payload?.correlationId ?? error.config?.headers?.['X-Correlation-Id']?.toString(),
  });
}

export function createApiClient(settings: ApiClientSettings = runtimeSettings): AxiosInstance {
  runtimeSettings = { ...runtimeSettings, ...settings };

  const client = axios.create({
    baseURL: runtimeSettings.baseUrl,
    timeout: runtimeSettings.defaultTimeoutMs,
    headers: {
      Accept: 'application/json',
    },
  });

  client.interceptors.request.use((config: InternalAxiosRequestConfig) => {
    const requestConfig = config as InternalAxiosRequestConfig & CassdioRequestConfig;
    const token = authService.getAccessToken();

    requestConfig.timeout = resolveTimeout(requestConfig);
    requestConfig.headers = AxiosHeaders.from(requestConfig.headers);
    requestConfig.headers.set('X-Correlation-Id', requestConfig.correlationId ?? crypto.randomUUID());

    if (token) {
      requestConfig.headers.set('Authorization', `Bearer ${token}`);
    }

    return requestConfig;
  });

  client.interceptors.response.use(
    (response) => response,
    async (error: AxiosError<ApiErrorResponse>) => {
      if (error.response?.status === 401) {
        authService.logout();
      }

      throw mapError(error);
    },
  );

  return client;
}

export function updateApiClientSettings(settings: Partial<ApiClientSettings>) {
  runtimeSettings = { ...runtimeSettings, ...settings };
}

export function getApiClientSettings() {
  return { ...runtimeSettings };
}

export const apiClient = createApiClient();

export async function getApiData<T>(url: string, config?: CassdioRequestConfig): Promise<T> {
  const response = await apiClient.get<ApiResponse<T>>(url, config);
  return response.data.data;
}
