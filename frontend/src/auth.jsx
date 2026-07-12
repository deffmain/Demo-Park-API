import React, { createContext, useContext, useEffect, useState } from 'react';
import { api, setSession, clearSession, getRefreshToken } from './api.js';

const AuthCtx = createContext(null);
const USER_KEY = 'cultiva_user';

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const s = localStorage.getItem(USER_KEY);
    return s ? JSON.parse(s) : null;
  });

  function persist(resp) {
    setSession(resp); // access token + refresh token
    const u = { userId: resp.userId, name: resp.name, email: resp.email };
    localStorage.setItem(USER_KEY, JSON.stringify(u));
    setUser(u);
  }

  const login = async (b) => persist(await api.login(b));
  const register = async (b) => persist(await api.register(b));
  const logout = async () => {
    // Revoga a sessão no servidor (best-effort) antes de limpar o estado local.
    try { await api.logout(getRefreshToken()); } catch (e) { /* ignora falha de rede */ }
    clearSession();
    localStorage.removeItem(USER_KEY);
    setUser(null);
  };

  // Quando o backend devolve 401 em uma rota protegida (token expirado/inválido),
  // o cliente HTTP dispara `cultiva:session-expired` — derruba a sessão aqui.
  useEffect(() => {
    const onExpired = () => {
      localStorage.removeItem(USER_KEY);
      setUser(null);
    };
    window.addEventListener('cultiva:session-expired', onExpired);
    return () => window.removeEventListener('cultiva:session-expired', onExpired);
  }, []);

  return <AuthCtx.Provider value={{ user, login, register, logout }}>{children}</AuthCtx.Provider>;
}

export const useAuth = () => useContext(AuthCtx);
