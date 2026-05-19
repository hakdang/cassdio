import axios from 'axios';

const AUTH_STORAGE_KEY = 'cassdio.session';

export type AuthSession = {
  email: string;
  accessToken: string;
  displayName?: string;
  memberId?: string;
};

export type LoginInput = {
  email: string;
  password: string;
};

export const authService = {
  getSession(): AuthSession | null {
    const rawSession = window.localStorage.getItem(AUTH_STORAGE_KEY);
    return rawSession ? (JSON.parse(rawSession) as AuthSession) : null;
  },

  getAccessToken(): string | null {
    return this.getSession()?.accessToken ?? null;
  },

  async login({ email, password }: LoginInput): Promise<AuthSession> {
    if (!email || !password) {
      throw new Error('Email and password are required.');
    }

    const response = await axios.post(
      `${import.meta.env.VITE_API_BASE_URL ?? ''}/api/auth/login`,
      { email, password },
      {
        withCredentials: true,
        headers: { Accept: 'application/json' },
      },
    );
    const payload = response.data.data;
    const session: AuthSession = {
      email: payload.member.email,
      displayName: payload.member.displayName,
      memberId: payload.member.memberId,
      accessToken: payload.accessToken,
    };

    window.localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(session));
    return session;
  },

  async refresh(): Promise<AuthSession> {
    const response = await axios.post(
      `${import.meta.env.VITE_API_BASE_URL ?? ''}/api/auth/refresh`,
      {},
      {
        withCredentials: true,
        headers: { Accept: 'application/json' },
      },
    );
    const payload = response.data.data;
    const session: AuthSession = {
      email: payload.member.email,
      displayName: payload.member.displayName,
      memberId: payload.member.memberId,
      accessToken: payload.accessToken,
    };
    window.localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(session));
    return session;
  },

  async logout() {
    try {
      await axios.post(
        `${import.meta.env.VITE_API_BASE_URL ?? ''}/api/auth/logout`,
        {},
        {
          withCredentials: true,
          headers: this.getAccessToken() ? { Authorization: `Bearer ${this.getAccessToken()}` } : undefined,
        },
      );
    } catch {
      // Local logout must still complete if the server session is already gone.
    }
    window.localStorage.removeItem(AUTH_STORAGE_KEY);
  },

  clear() {
    window.localStorage.removeItem(AUTH_STORAGE_KEY);
  },

  isAuthenticated() {
    return this.getSession() !== null;
  },
};
