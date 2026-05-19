import type { HealthResponse } from '../types/api';
import { getApiData } from './apiClient';

export async function fetchHealth(): Promise<HealthResponse> {
  return getApiData<HealthResponse>('/api/health');
}

export type { HealthResponse };
