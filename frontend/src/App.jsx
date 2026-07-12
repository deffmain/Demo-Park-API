import React from 'react';
import { Routes, Route, Navigate, Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from './auth.jsx';
import Login from './pages/Login.jsx';
import Projects from './pages/Projects.jsx';
import Planner from './pages/Planner.jsx';
import Guide from './pages/Guide.jsx';
import Concepts from './pages/Concepts.jsx';
import Privacy from './pages/Privacy.jsx';
import Account from './pages/Account.jsx';

function Protected({ children }) {
  const { user } = useAuth();
  return user ? children : <Navigate to="/login" replace />;
}

function Header() {
  const { user, logout } = useAuth();
  const loc = useLocation();
  const nav = useNavigate();
  if (!user) return null;
  const tab = (to, label) => (
    <button
      className={(to === '/' ? loc.pathname === '/' || loc.pathname.startsWith('/p/') : loc.pathname.startsWith(to)) ? 'active' : ''}
      onClick={() => nav(to)}>{label}</button>
  );
  return (
    <header>
      <h1>🌱 Cultiva</h1>
      <nav className="tabs">
        {tab('/', '🧭 Projetos')}
        {tab('/guia', '📚 Guia das culturas')}
        {tab('/conceitos', '📖 Conceitos')}
      </nav>
      <div className="header-actions">
        <span style={{ alignSelf: 'center', fontSize: 13, opacity: .9 }}>{user.name}</span>
        <button onClick={() => nav('/conta')}>⚙️ Conta</button>
        <button onClick={logout}>Sair</button>
      </div>
    </header>
  );
}

// Rodapé com contato do controlador + política (LGPD, issue #11). Oculto na tela cheia
// do planejador (/p/:id) para não interferir no layout de altura fixa.
function Footer() {
  const loc = useLocation();
  if (loc.pathname.startsWith('/p/')) return null;
  return (
    <footer style={{ flex: '0 0 auto', textAlign: 'center', padding: '8px 12px', fontSize: 12, opacity: .8 }}>
      <Link to="/privacidade">Política de Privacidade</Link>
      <span> · Contato: <a href="mailto:contato@cultiva.app">contato@cultiva.app</a></span>
    </footer>
  );
}

export default function App() {
  return (
    <div className="shell">
      <Header />
      <main className="shell-main">
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/" element={<Protected><Projects /></Protected>} />
          <Route path="/p/:id" element={<Protected><Planner /></Protected>} />
          <Route path="/guia" element={<Protected><Guide /></Protected>} />
          <Route path="/conceitos" element={<Protected><Concepts /></Protected>} />
          <Route path="/conta" element={<Protected><Account /></Protected>} />
          <Route path="/privacidade" element={<Privacy />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
      <Footer />
    </div>
  );
}
