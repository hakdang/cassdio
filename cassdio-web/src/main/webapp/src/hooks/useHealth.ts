import { useCallback, useEffect, useState } from 'react';
import { fetchHealth, type HealthResponse } from '../utils/api';

type UseHealthOptions = {
  pollIntervalMs?: number;
};

export function useHealth(options: UseHealthOptions = {}) {
  const [data, setData] = useState<HealthResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const refresh = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      setData(await fetchHealth());
    } catch {
      setError('API status is not available.');
      setData(null);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void refresh();

    if (!options.pollIntervalMs) {
      return undefined;
    }

    const intervalId = window.setInterval(() => {
      void refresh();
    }, options.pollIntervalMs);

    return () => window.clearInterval(intervalId);
  }, [options.pollIntervalMs, refresh]);

  return { data, loading, error, refresh };
}
