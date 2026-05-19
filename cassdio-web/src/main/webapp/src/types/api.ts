export type ApiResponse<T> = {
  code: string;
  message: string;
  data: T;
  timestamp: number;
};

export type ApiErrorResponse = {
  code: string;
  message: string;
  errors?: Array<{
    field?: string;
    message: string;
  }>;
  timestamp?: number;
  correlationId?: string;
};

export type HealthResponse = {
  status: string;
  service: string;
  timestamp: string;
};
