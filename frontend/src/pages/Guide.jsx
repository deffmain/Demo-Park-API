import React, { useEffect, useState } from 'react';
import { api } from '../api.js';
import { useIsMobile } from '../hooks.js';
import Icon from '../Icon.jsx';

const SOIL_NOME = { arenoso: 'Arenoso', argiloso: 'Argiloso', siltoso: 'Siltoso', franco: 'Franco', humoso: 'Humoso' };

// Conteúdo do detalhe de uma cultura. Reutilizado tanto no painel lado-a-lado (desktop)
// quanto dentro do acordeão (mobile) — sem duplicar a renderização.
function CultureDetail({ c, byId }) {
  const g = c.guia || {};
  const tag = (ids, cor) => (ids && ids.length)
    ? ids.map(i => <span className="gd-tag" key={i} style={{ background: cor }}><Icon emoji={byId[i]?.emoji} /> {byId[i]?.nome || i}</span>)
    : <span className="muted">nenhuma conhecida</span>;

  const epoca = c.bolting ? 'Outono e Inverno (espiga/floresce no calor)'
    : c.clima === 'quente' ? 'Primavera e Verão (sensível à geada)'
      : 'Outono a Primavera (aprecia clima ameno/frio)';
  const geadaTxt = c.geada >= -1 ? `Sensível: morre já em geada leve (~${c.geada}°C).`
    : c.geada >= -5 ? `Tolera frio moderado; morre em geada forte (≤ ${c.geada}°C).`
      : `Muito rústica ao frio (suporta até ~${c.geada}°C).`;
  const aguaSem = Math.round(5.0 * c.kc * 7);

  return (
    <>
      <div className="gd-head">
        <div className="em"><Icon emoji={c.emoji} alt={c.nome} /></div>
        <div><h2>{c.nome}</h2><div className="sub">{c.familia} · clima {c.clima} · ciclo ~{c.dias} dias</div></div>
      </div>
      <div className="gd-chips">
        <span className="gd-chip">⏱️ {c.dias} dias</span>
        <span className="gd-chip">📏 {c.espacamentoCm} cm</span>
        <span className="gd-chip">☀️ {c.sun}</span>
        <span className="gd-chip">⚗️ pH {c.phMin}–{c.phMax}</span>
        <span className="gd-chip">🌡️ {c.tOtMin}–{c.tOtMax}°C</span>
        <span className="gd-chip">🍽️ consumo {c.feeder}</span>
      </div>
      <div className="gd-body">
        <div className="gd-grid">
          <div className="gd-sec"><h3>🌡️ Clima ideal</h3>
            <div className="gd-row"><span>Temperatura ótima</span><b>{c.tOtMin}–{c.tOtMax}°C</b></div>
            <div className="gd-row"><span>Época de plantio</span><b>{epoca}</b></div>
            <div className="gd-row"><span>Frio / geada</span><b>{geadaTxt}</b></div>
          </div>
          <div className="gd-sec"><h3>🪨 Solo ideal</h3>
            <div className="gd-row"><span>Tipos preferidos</span><b>{(c.solosIdeais || []).map(s => SOIL_NOME[s] || s).join(', ')}</b></div>
            <div className="gd-row"><span>pH ideal</span><b>{c.phMin}–{c.phMax}</b></div>
            <div className="gd-row"><span>Drenagem</span><b>{c.sensivelEncharcamento ? 'precisa boa drenagem' : 'tolera solos mais pesados'}</b></div>
          </div>
          <div className="gd-sec"><h3>💧 Água & irrigação</h3>
            <div className="gd-row"><span>Necessidade (verão)</span><b>~{aguaSem} mm/semana</b></div>
            <div className="gd-row"><span>Seca</span><b>morre após ~{c.diasSeca} dias de déficit total</b></div>
          </div>
          <div className="gd-sec"><h3>🧪 Adubação & sal</h3>
            <div className="gd-row"><span>Consumo de nutrientes</span><b>{c.feeder}</b></div>
            <div className="gd-row"><span>Tolerância ao sal</span><b>limiar {c.ecLim} dS/m</b></div>
          </div>
        </div>

        <div className="gd-sec"><h3>🌱 Como plantar</h3>
          <p><b>Propagação:</b> {g.propagacao || '—'} · <b>Profundidade:</b> {g.profundidade || '—'} · <b>Espaçamento:</b> {c.espacamentoCm} cm</p>
          <p style={{ marginTop: 6 }}>{g.comoPlantar}</p>
        </div>
        <div className="gd-sec"><h3>⏳ Tempo até a maturidade</h3>
          <p>Ciclo de <b>~{c.dias} dias</b> até a colheita. Janela de colheita de <b>~{c.janela} dias</b>. {g.colheita}</p>
        </div>
        <div className="gd-sec"><h3>🤝 Consórcio (plantar perto)</h3>
          <p style={{ marginBottom: 6 }}><b>Boas companheiras:</b></p>
          <div className="gd-tags">{tag(c.amigas, 'var(--bom)')}</div>
          <p style={{ margin: '10px 0 6px' }}><b>Evite ao lado de:</b></p>
          <div className="gd-tags">{tag(c.inimigas, 'var(--critico)')}</div>
        </div>
        <div className="gd-sec"><h3>⚠️ Atitudes a evitar</h3>
          <ul className="gd-avoid">{(g.evitar || []).map((e, k) => <li key={k}>{e}</li>)}</ul>
        </div>
        <div className="gd-sec"><h3>💡 Dica do cultivo</h3><div className="gd-dica">{c.dica}</div></div>
      </div>
    </>
  );
}

export default function Guide() {
  const [crops, setCrops] = useState(null);
  const [open, setOpen] = useState(null);   // id selecionado (desktop) / expandido (mobile)
  const isMobile = useIsMobile();

  useEffect(() => {
    api.crops().then(list => {
      list.sort((a, b) => a.nome.localeCompare(b.nome));
      setCrops(list);
    });
  }, []);

  if (!crops) return <div className="guide-wrap"><p className="muted" style={{ padding: 16 }}>Carregando culturas…</p></div>;
  const byId = Object.fromEntries(crops.map(c => [c.id, c]));

  // Mobile: acordeão — um item aberto por vez, expande o detalhe inline.
  if (isMobile) {
    return (
      <div id="guideView">
        <div className="accordion">
          {crops.map(cr => {
            const isOpen = open === cr.id;
            return (
              <div className={'acc-item' + (isOpen ? ' open' : '')} key={cr.id}>
                <button className="acc-header" aria-expanded={isOpen} onClick={() => setOpen(isOpen ? null : cr.id)}>
                  <Icon emoji={cr.emoji} className="ico" />
                  <span className="acc-name">{cr.nome}</span>
                  <span className="chev" aria-hidden="true">▾</span>
                </button>
                {isOpen && <div className="acc-body"><CultureDetail c={cr} byId={byId} /></div>}
              </div>
            );
          })}
        </div>
      </div>
    );
  }

  // Desktop: lista + detalhe lado a lado (sempre mostra uma cultura).
  const c = byId[open] || crops[0];
  return (
    <div id="guideView">
      <div className="guide-wrap">
        <aside className="guide-list">
          {crops.map(cr => (
            <button key={cr.id} className={cr.id === c.id ? 'active' : ''} onClick={() => setOpen(cr.id)}>
              <Icon emoji={cr.emoji} className="ico" />{cr.nome}
            </button>
          ))}
        </aside>
        <section className="guide-detail"><CultureDetail c={c} byId={byId} /></section>
      </div>
    </div>
  );
}
