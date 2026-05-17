export type HealthResponse = {
  status: string;
  service: string;
  timestamp: string;
};

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? '';

export async function fetchHealth(): Promise<HealthResponse> {
  const response = await fetch(`${apiBaseUrl}/api/health`);

  if (!response.ok) {
    throw new Error(`Health request failed with status ${response.status}`);
  }

  return response.json() as Promise<HealthResponse>;
}
