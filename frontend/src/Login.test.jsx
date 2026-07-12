import { describe, it, expect, afterEach } from 'vitest';
import { render, screen, fireEvent, cleanup } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { AuthProvider } from './auth.jsx';
import Login from './pages/Login.jsx';

// Smoke test do componente crítico de login (issue #7): renderiza e alterna o modo.
// Não dispara chamadas de rede (não submete) — valida a UI e o toggle entrar/criar conta.
function renderLogin() {
  return render(
    <MemoryRouter>
      <AuthProvider>
        <Login />
      </AuthProvider>
    </MemoryRouter>
  );
}

describe('Login (componente crítico)', () => {
  afterEach(() => {
    cleanup();
    localStorage.clear();
  });

  it('mostra o formulário de entrar, sem o campo Nome', () => {
    renderLogin();
    expect(screen.getByText('🌱 Cultiva')).toBeTruthy();
    expect(screen.getByText('Simulador de plantio')).toBeTruthy();
    // no modo "login" não há campo Nome
    expect(screen.queryByText('Nome')).toBeNull();
  });

  it('alterna para "Criar conta" e revela o campo Nome', () => {
    renderLogin();
    fireEvent.click(screen.getByRole('button', { name: 'Criar conta' }));
    expect(screen.getByText('Nome')).toBeTruthy();
  });
});
