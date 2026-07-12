import React from 'react';
import { Link } from 'react-router-dom';

// Política de Privacidade — MODELO (issue #11). Texto base para o MVP; deve ser
// revisado pelo jurídico e ter o contato do controlador configurado antes do lançamento.
const CONTATO = 'contato@cultiva.app'; // TODO: configurar o e-mail real do controlador

export default function Privacy() {
  return (
    <div className="doc-page" style={{ maxWidth: 760, margin: '0 auto', padding: 24, lineHeight: 1.6 }}>
      <h1>Política de Privacidade — Cultiva</h1>
      <p className="muted"><em>Documento modelo, sujeito a revisão jurídica. Versão dos termos: 2026-06-25.</em></p>

      <h2>Quem somos (controlador)</h2>
      <p>O Cultiva é o controlador dos dados tratados neste aplicativo. Contato do encarregado:
        <a href={'mailto:' + CONTATO}> {CONTATO}</a>.</p>

      <h2>Quais dados coletamos</h2>
      <ul>
        <li><strong>Cadastro:</strong> nome e e-mail.</li>
        <li><strong>Projetos de plantio:</strong> layout, solo, culturas e, quando você informa um
          município, a <strong>localização</strong> (latitude/longitude/cidade) usada para o clima.</li>
        <li><strong>Consentimento:</strong> data e versão dos termos aceitos no cadastro.</li>
      </ul>

      <h2>Para que usamos e base legal</h2>
      <p>Usamos os dados para autenticar você e operar a simulação (execução do serviço solicitado)
        e mediante o seu <strong>consentimento</strong>, registrado no cadastro. O clima por município
        é obtido de serviço externo a partir da localização que você fornece.</p>

      <h2>Seus direitos (LGPD)</h2>
      <ul>
        <li><strong>Acesso e portabilidade:</strong> em <Link to="/conta">Minha conta</Link> você pode
          <strong> exportar todos os seus dados</strong> em JSON.</li>
        <li><strong>Exclusão (esquecimento):</strong> em <Link to="/conta">Minha conta</Link> você pode
          <strong> excluir sua conta</strong> — anonimizamos seu cadastro e apagamos seus projetos.</li>
        <li>Para outras solicitações, escreva para <a href={'mailto:' + CONTATO}>{CONTATO}</a>.</li>
      </ul>

      <h2>Retenção</h2>
      <p>Mantemos os dados enquanto a conta existir. Ao excluir a conta, os projetos são apagados e o
        cadastro é anonimizado.</p>

      <h2>Créditos</h2>
      <p className="muted">Ícones de emoji por <a href="https://github.com/jdecked/twemoji" target="_blank" rel="noreferrer">Twemoji</a>,
        licenciados sob <a href="https://creativecommons.org/licenses/by/4.0/" target="_blank" rel="noreferrer">CC-BY 4.0</a>.</p>

      <p style={{ marginTop: 24 }}><Link to="/login">← Voltar</Link></p>
    </div>
  );
}
