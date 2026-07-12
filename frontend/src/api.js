// Base da API. Vazia por padrão → caminhos relativos (mesma origem): é o caso do DEV
// e do deploy self-hosted, onde o Nginx serve a SPA e faz proxy de /api para o backend.
// Quando frontend e backend ficam em domínios diferentes (ex.: Vercel + Railway), defina
// VITE_API_BASE no build (ex.: https://cultiva.up.railway.app) e o backend deve liberar
// essa origem em CORS_ORIGINS. As rotas continuam relativas no código; só o destino do
// fetch ganha o prefixo. (deploy Vercel/Railway — issue #9)
const API_BASE = import.meta.env?.VITE_API_BASE ?? '';

const TOKEN_KEY = 'cultiva_token';
const REFRESH_KEY = 'cultiva_refresh';

export const getToken = () => localStorage.getItem(TOKEN_KEY);
export const setToken = (t) => t ? localStorage.setItem(TOKEN_KEY, t) : localStorage.removeItem(TOKEN_KEY);
export const getRefreshToken = () => localStorage.getItem(REFRESH_KEY);
export const setRefreshToken = (t) => t ? localStorage.setItem(REFRESH_KEY, t) : localStorage.removeItem(REFRESH_KEY);

/** Guarda o par de tokens vindo de login/registro/refresh. */
export function setSession(resp) {
  setToken(resp && resp.token ? resp.token : null);
  setRefreshToken(resp && resp.refreshToken ? resp.refreshToken : null);
}
/** Limpa a sessão local (não chama o servidor). */
export function clearSession() {
  setToken(null);
  setRefreshToken(null);
}

/**
 * Erro com formato estável para o resto do app:
 *   { message, status, fieldErrors, isNetwork, isAuth }
 * Componentes podem mostrar `e.message` direto ou olhar `e.fieldErrors`.
 */
export class ApiError extends Error {
  constructor({ message, status, fieldErrors, isNetwork = false }) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.fieldErrors = fieldErrors || null;
    this.isNetwork = isNetwork;
    this.isAuth = status === 401;
  }
}

function defaultMessageFor(status) {
  if (status === 400) return 'Dados inválidos. Confira os campos.';
  if (status === 401) return 'Sessão expirada. Faça login novamente.';
  if (status === 403) return 'Você não tem permissão para esta ação.';
  if (status === 404) return 'Recurso não encontrado.';
  if (status === 409) return 'Operação conflita com dados existentes.';
  if (status === 422) return 'Não foi possível processar — verifique os dados.';
  if (status === 429) return 'Muitas tentativas. Aguarde um instante e tente novamente.';
  if (status >= 500) return 'Erro no servidor. Tente novamente em instantes.';
  return 'Erro ' + status;
}

// Renovação do access token via refresh token (issue #5). Single-flight: várias
// requisições que tomam 401 ao mesmo tempo compartilham UMA única chamada de refresh.
let refreshPromise = null;

async function doRefresh() {
  const rt = getRefreshToken();
  if (!rt) return false;
  let res;
  try {
    res = await fetch(API_BASE + '/api/auth/refresh', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken: rt }),
    });
  } catch (e) {
    return false; // erro de rede: não derruba a sessão (pode ser transitório)
  }
  if (!res.ok) {
    clearSession(); // refresh inválido/expirado: sessão acabou de fato
    return false;
  }
  const data = await res.json().catch(() => null);
  if (!data || !data.token) {
    clearSession();
    return false;
  }
  setToken(data.token);
  setRefreshToken(data.refreshToken);
  return true;
}

function refreshOnce() {
  if (!refreshPromise) {
    refreshPromise = doRefresh().finally(() => { refreshPromise = null; });
  }
  return refreshPromise;
}

async function req(method, url, body, _retried = false) {
  const headers = { 'Content-Type': 'application/json' };
  const t = getToken();
  if (t) headers.Authorization = 'Bearer ' + t;

  let res;
  try {
    res = await fetch(API_BASE + url, { method, headers, body: body ? JSON.stringify(body) : undefined });
  } catch (e) {
    // Falha de rede / servidor offline / CORS / DNS — `fetch` rejeita sem `res`.
    throw new ApiError({
      message: 'Sem conexão com o servidor. Verifique sua internet.',
      status: 0,
      isNetwork: true,
    });
  }

  // 401 em rota protegida: tenta UMA renovação transparente e repete a requisição.
  // Rotas /api/auth/* (login, refresh, ...) não entram nesse fluxo (evita laço).
  const isAuthRoute = url.startsWith('/api/auth/');
  if (res.status === 401 && !isAuthRoute) {
    if (!_retried && await refreshOnce()) {
      return req(method, url, body, true); // repete com o novo access token
    }
    // refresh falhou (ou o retry também deu 401): sessão encerrada.
    clearSession();
    window.dispatchEvent(new CustomEvent('cultiva:session-expired'));
  }

  if (res.status === 204) return null;

  const data = await res.json().catch(() => null);

  if (!res.ok) {
    const message = (data && data.message) || defaultMessageFor(res.status);
    const fieldErrors = data && Array.isArray(data.fieldErrors) ? data.fieldErrors : null;
    throw new ApiError({ message, status: res.status, fieldErrors });
  }

  return data;
}

export const api = {
  register: (b) => req('POST', '/api/auth/register', b),
  login: (b) => req('POST', '/api/auth/login', b),
  refresh: (refreshToken) => req('POST', '/api/auth/refresh', { refreshToken }),
  logout: (refreshToken) => req('POST', '/api/auth/logout', { refreshToken }),
  logoutAll: () => req('POST', '/api/auth/logout-all'),
  exportMe: () => req('GET', '/api/me/export'),       // LGPD: dados pessoais (issue #11)
  deleteMe: () => req('DELETE', '/api/me'),           // LGPD: excluir conta
  crops: () => req('GET', '/api/reference/crops'),
  climates: () => req('GET', '/api/reference/climates'),
  listProjects: () => req('GET', '/api/projects'),
  getProject: (id) => req('GET', '/api/projects/' + id),
  createProject: (b) => req('POST', '/api/projects', b),
  updateProject: (id, b) => req('PUT', '/api/projects/' + id, b),
  deleteProject: (id) => req('DELETE', '/api/projects/' + id),
  simulate: (id) => req('GET', '/api/projects/' + id + '/simulate'),
  simulatePreview: (b) => req('POST', '/api/simulate', b),   // simula sem gravar (preview)
  geocode: (city) => req('GET', '/api/geo?city=' + encodeURIComponent(city)),
  alerts: (id) => req('GET', '/api/projects/' + id + '/alerts'),
  refreshAlerts: (id) => req('POST', '/api/projects/' + id + '/alerts/refresh'),
  dismissAlert: (id) => req('PUT', '/api/alerts/' + id + '/dismiss'),
};
