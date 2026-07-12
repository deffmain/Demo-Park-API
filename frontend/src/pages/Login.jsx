import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../auth.jsx';

export default function Login() {
  const { user, login, register } = useAuth();
  const nav = useNavigate();
  const [mode, setMode] = useState('login');
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [accepted, setAccepted] = useState(false);
  const [err, setErr] = useState('');
  const [fieldErrs, setFieldErrs] = useState({});
  const [busy, setBusy] = useState(false);

  if (user) { nav('/', { replace: true }); return null; }

  async function submit(e) {
    e.preventDefault();
    setErr(''); setFieldErrs({}); setBusy(true);
    try {
      if (mode === 'login') await login({ email, password });
      else await register({ name, email, password, acceptedTerms: accepted });
      nav('/', { replace: true });
    } catch (e) {
      setErr(e.message || 'Falha na autenticação.');
      if (e.fieldErrors) {
        const map = {};
        e.fieldErrors.forEach(f => { map[f.field] = f.message; });
        setFieldErrs(map);
      }
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="auth-wrap">
      <form className="auth-card" onSubmit={submit}>
        <h1>🌱 Cultiva</h1>
        <p className="muted">Simulador de plantio</p>
        <div className="auth-toggle">
          <button type="button" className={mode === 'login' ? 'on' : ''} onClick={() => setMode('login')}>Entrar</button>
          <button type="button" className={mode === 'register' ? 'on' : ''} onClick={() => setMode('register')}>Criar conta</button>
        </div>
        {mode === 'register' && (
          <>
            <label className="field">Nome</label>
            <input value={name} onChange={e => setName(e.target.value)} required />
            {fieldErrs.name && <small className="field-err">{fieldErrs.name}</small>}
          </>
        )}
        <label className="field">E-mail</label>
        <input type="email" value={email} onChange={e => setEmail(e.target.value)} required />
        {fieldErrs.email && <small className="field-err">{fieldErrs.email}</small>}
        <label className="field">Senha</label>
        <input type="password" value={password} onChange={e => setPassword(e.target.value)} required minLength={6} />
        {fieldErrs.password && <small className="field-err">{fieldErrs.password}</small>}
        {mode === 'register' && (
          <label className="consent">
            <input type="checkbox" checked={accepted} onChange={e => setAccepted(e.target.checked)} required />
            <span> Li e aceito os <a href="/privacidade" target="_blank" rel="noreferrer">Termos e a Política de Privacidade</a>.</span>
          </label>
        )}
        {err && <div className="auth-err">{err}</div>}
        <button className="btn-apply" disabled={busy} type="submit">
          {busy ? 'Aguarde…' : mode === 'login' ? 'Entrar' : 'Criar conta'}
        </button>
      </form>
    </div>
  );
}
