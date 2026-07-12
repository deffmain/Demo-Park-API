import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../api.js';

function todayISO() {
  const d = new Date();
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
}

export default function Projects() {
  const nav = useNavigate();
  const [list, setList] = useState(null);
  const [err, setErr] = useState('');

  async function load() {
    try { setList(await api.listProjects()); }
    catch (e) { setErr(e.message); }
  }
  useEffect(() => { load(); }, []);

  async function create() {
    const name = prompt('Nome da nova plantação:', 'Minha horta');
    if (!name) return;
    try {
      const p = await api.createProject({ name, climate: 'cerrado', startDate: todayISO(), day: 0, irrig: 30 });
      nav('/p/' + p.id);
    } catch (e) {
      setErr(e.message || 'Não foi possível criar a plantação.');
    }
  }

  async function remove(id, name) {
    if (!confirm(`Apagar "${name}"?`)) return;
    try {
      await api.deleteProject(id);
      load();
    } catch (e) {
      setErr(e.message || 'Não foi possível apagar a plantação.');
    }
  }

  return (
    <div className="projects-wrap">
      <div className="projects-head">
        <h2>Suas plantações</h2>
        <button className="btn-apply" onClick={create}>➕ Nova plantação</button>
      </div>
      {err && <div className="auth-err">{err}</div>}
      {list === null ? <p className="muted">Carregando…</p>
        : list.length === 0 ? <div className="empty-state">Nenhuma plantação ainda. Crie a primeira!</div>
          : (
            <div className="project-list">
              {list.map(p => (
                <div className="project-card" key={p.id}>
                  <div className="pc-main" onClick={() => nav('/p/' + p.id)}>
                    <b>🌿 {p.name}</b>
                    <small>atualizado em {new Date(p.updatedAt).toLocaleString('pt-BR')}</small>
                  </div>
                  <button className="pc-del" title="Apagar" onClick={() => remove(p.id, p.name)}>🗑️</button>
                </div>
              ))}
            </div>
          )}
    </div>
  );
}
