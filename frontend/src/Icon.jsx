import React, { useState } from 'react';

// Ícone consistente em TODOS os aparelhos. (frente A — padronização de ícones)
//
// Problema: emoji é renderizado pela fonte do SO (Segoe no Windows, Apple no iOS,
// Noto no Android) → fica diferente em cada device. Aqui renderizamos o emoji como
// SVG do Twemoji (igual em todo lugar), com fallback para o emoji do sistema se a
// imagem falhar.
//
// FASE 2 (SVGs próprios por cultura): preencha CUSTOM com name -> URL do SVG (ex.:
// import quiabo from './assets/crops/quiabo.svg'; CUSTOM.quiabo = quiabo). O componente
// já dá prioridade ao custom; emoji vira só o fallback. Nada mais muda nos chamadores.
const CUSTOM = {};

// Twemoji (CC-BY 4.0). Fork mantido jdecked/twemoji. Trocar por assets próprios/local
// é só mudar esta função (chamadores não mudam).
function twemojiUrl(emoji) {
  const cps = [];
  for (const ch of emoji) {
    const cp = ch.codePointAt(0);
    if (cp !== 0xfe0f) cps.push(cp.toString(16));   // ignora o seletor de variação
  }
  return `https://cdn.jsdelivr.net/gh/jdecked/twemoji@15.1.0/assets/svg/${cps.join('-')}.svg`;
}

export default function Icon({ emoji, name, alt, className = '' }) {
  const [failed, setFailed] = useState(false);
  const src = (name && CUSTOM[name]) || (emoji ? twemojiUrl(emoji) : null);

  // Fallback: emoji do sistema (ou broto genérico). Mantém acessibilidade com aria.
  if (failed || !src) {
    return (
      <span className={`emoji-fallback ${className}`} role={alt ? 'img' : undefined}
        aria-label={alt || undefined} aria-hidden={alt ? undefined : true}>{emoji || '🌱'}</span>
    );
  }
  return (
    <img className={`emoji-icon ${className}`} src={src} alt={alt || ''} draggable={false}
      aria-hidden={alt ? undefined : true} onError={() => setFailed(true)} />
  );
}
