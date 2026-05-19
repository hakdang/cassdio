const AUTH_STORAGE_KEY = 'cassdio.session';

export type AuthSession = {
  email: string;
  accessToken: string;
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

    const session: AuthSession = {
      email,
      accessToken: `phase-1-placeholder-token:${email}`,
    };

    window.localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(session));
    return session;
  },

  logout() {
    window.localStorage.removeItem(AUTH_STORAGE_KEY);
  },

  isAuthenticated() {
    return this.getSession() !== null;
  },
};
