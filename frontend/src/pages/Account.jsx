import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../api.js';
import { useAuth } from '../auth.jsx';

// Minha conta — direitos do titular LGPD (issue #11): exportar e excluir os dados.
export default function Account() {
  const { user, logout } = useAuth();
  const nav = useNavigate();
  const [busy, setBusy] = useState('');
  const [msg, setMsg] = useState('');

  async function exportData() {
    setBusy('export'); setMsg('');
    try {
      const data = await api.exportMe();
      const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'cultiva-meus-dados.json';
      a.click();
      URL.revokeObjectURL(url);
      setMsg('Download iniciado.');
    } catch (e) {
      setMsg(e.message || 'Falha ao exportar.');
    } finally {
      setBusy('');
    }
  }

  async function deleteAccount() {
    if (!window.confirm('Excluir sua conta? Seus projetos serão apagados e o cadastro anonimizado. Esta ação não pode ser desfeita.')) return;
    setBusy('delete'); setMsg('');
    try {
      await api.deleteMe();
      await logout();
      nav('/login', { replace: true });
    } catch (e) {
      setMsg(e.message || 'Falha ao excluir.');
      setBusy('');
    }
  }

  return (
    <div className="doc-page" style={{ maxWidth: 640, margin: '0 auto', padding: 24, lineHeight: 1.6 }}>
      <h1>Minha conta</h1>
      <p className="muted">{user?.name} · {user?.email}</p>

      <h2>Meus dados (LGPD)</h2>
      <p>Você pode baixar tudo o que guardamos sobre você ou excluir sua conta.</p>
      <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap', marginTop: 12 }}>
        <button className="btn-apply" disabled={busy === 'export'} onClick={exportData}>
          {busy === 'export' ? 'Exportando…' : '⬇️ Exportar meus dados'}
        </button>
        <button disabled={busy === 'delete'} onClick={deleteAccount}
          style={{ background: '#e53935', color: '#fff', border: 'none', borderRadius: 6, padding: '8px 14px', cursor: 'pointer' }}>
          {busy === 'delete' ? 'Excluindo…' : '🗑️ Excluir minha conta'}
        </button>
      </div>
      {msg && <p style={{ marginTop: 12 }}>{msg}</p>}
    </div>
  );
}
