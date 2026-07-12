import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { api, setSession, getToken, getRefreshToken } from './api.js';

/** Resposta fake no formato que o api.js usa (status, ok, json()). */
function jsonRes(status, body) {
  return { status, ok: status >= 200 && status < 300, json: async () => body };
}

let calls;

beforeEach(() => {
  localStorage.clear();
  calls = [];
});

afterEach(() => {
  vi.restoreAllMocks();
});

describe('renovação transparente do access token (issue #5)', () => {
  it('renova em 401 e repete a requisição com o novo token', async () => {
    setSession({ token: 'expirado', refreshToken: 'rt0' });

    global.fetch = vi.fn(async (url, opts) => {
      calls.push({ url, auth: opts && opts.headers && opts.headers.Authorization });
      if (url === '/api/auth/refresh') return jsonRes(200, { token: 'novo', refreshToken: 'rt1' });
      if (opts && opts.headers && opts.headers.Authorization === 'Bearer novo') return jsonRes(200, { ok: true });
      return jsonRes(401, { message: 'expirado' });
    });

    const data = await api.listProjects();

    expect(data).toEqual({ ok: true });
    expect(getToken()).toBe('novo');
    expect(getRefreshToken()).toBe('rt1');
    expect(calls.filter((c) => c.url === '/api/auth/refresh')).toHaveLength(1);
  });

  it('desloga (limpa sessão + evento) quando o refresh falha', async () => {
    setSession({ token: 'expirado', refreshToken: 'rt-ruim' });

    global.fetch = vi.fn(async (url) => {
      if (url === '/api/auth/refresh') return jsonRes(401, { message: 'refresh inválido' });
      return jsonRes(401, { message: 'expirado' });
    });

    let expirou = false;
    window.addEventListener('cultiva:session-expired', () => { expirou = true; }, { once: true });

    await expect(api.listProjects()).rejects.toMatchObject({ status: 401 });
    expect(expirou).toBe(true);
    expect(getToken()).toBeNull();
    expect(getRefreshToken()).toBeNull();
  });

  it('usa um único refresh para várias requisições concorrentes (single-flight)', async () => {
    setSession({ token: 'expirado', refreshToken: 'rt0' });

    global.fetch = vi.fn(async (url, opts) => {
      calls.push({ url });
      if (url === '/api/auth/refresh') return jsonRes(200, { token: 'novo', refreshToken: 'rt1' });
      if (opts && opts.headers && opts.headers.Authorization === 'Bearer novo') return jsonRes(200, { ok: true });
      return jsonRes(401, { message: 'expirado' });
    });

    const results = await Promise.all([api.listProjects(), api.simulate(1), api.alerts(1)]);

    results.forEach((r) => expect(r).toEqual({ ok: true }));
    expect(calls.filter((c) => c.url === '/api/auth/refresh')).toHaveLength(1);
  });

  it('não tenta refresh em rota de auth (login 401 passa direto)', async () => {
    global.fetch = vi.fn(async (url) => {
      calls.push({ url });
      return jsonRes(401, { message: 'E-mail ou senha inválidos.' });
    });

    await expect(api.login({ email: 'a@b.com', password: 'x' })).rejects.toMatchObject({ status: 401 });
    expect(calls.filter((c) => c.url === '/api/auth/refresh')).toHaveLength(0);
  });
});
