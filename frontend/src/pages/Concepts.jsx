import React, { useMemo, useState } from 'react';
import { GLOSSARY, GLOSSARY_CATS, GLOSSARY_MEDIR } from '../data/glossary.js';
import { useIsMobile } from '../hooks.js';
import Icon from '../Icon.jsx';

// Renderiza o ícone da categoria (emoji consistente) com fallback para "•".
const CatIcon = ({ cat }) => GLOSSARY_CATS[cat] ? <Icon emoji={GLOSSARY_CATS[cat]} /> : '•';

const norm = (s) => s.toLowerCase().normalize('NFD').replace(/[̀-ͯ]/g, '');

// Conteúdo do detalhe de um conceito. Reutilizado no painel lado-a-lado (desktop) e no
// acordeão (mobile) — sem duplicar a renderização.
function ConceptDetail({ g }) {
  const m = GLOSSARY_MEDIR[g.termo] || {};
  return (
    <>
      <div className="gd-head">
        <div className="em"><Icon emoji={GLOSSARY_CATS[g.cat] || '📖'} alt={g.cat} /></div>
        <div><h2>{g.termo}</h2><div className="sub">{g.cat} · {g.resumo}</div></div>
      </div>
      <div className="gd-body">
        <div className="gd-sec"><h3>O que é</h3><p>{g.oque}</p></div>
        {m.tipos && (
          <div className="gd-sec"><h3>Tipos e características</h3>
            {m.tipos.map((t, k) => <div className="gd-tipo" key={k}><b>{t.nome}</b> — {t.carac}</div>)}
          </div>
        )}
        <div className="gd-sec"><h3>Por que importa</h3><p>{g.porque}</p></div>
        <div className="gd-sec"><h3>No que influencia</h3><p>{g.influencia}</p></div>
        {(m.caseiro || m.profissional) && (
          <div className="gd-sec"><h3>Como descobrir no seu terreno</h3>
            {m.caseiro && (
              <div className="medir-blk caseiro"><span className="medir-tag">🧪 Você mesmo pode fazer</span>
                <ul>{m.caseiro.map((x, k) => <li key={k}>{x}</li>)}</ul></div>
            )}
            {m.profissional && (
              <div className="medir-blk prof"><span className="medir-tag">🔬 Análise profissional</span>
                <ul>{m.profissional.map((x, k) => <li key={k}>{x}</li>)}</ul></div>
            )}
          </div>
        )}
        <div className="gd-sec"><h3>Como agir</h3><div className="gd-dica">{g.comoagir}</div></div>
        <div className="gd-sec"><h3>Fonte</h3><p className="muted">{g.fonte}</p></div>
      </div>
    </>
  );
}

export default function Concepts() {
  const [open, setOpen] = useState(null);   // índice selecionado (desktop) / expandido (mobile)
  const [q, setQ] = useState('');
  const isMobile = useIsMobile();

  const filtered = useMemo(() => {
    const nq = norm(q);
    return GLOSSARY.map((g, i) => ({ g, i })).filter(({ g }) =>
      !nq || norm(g.termo + ' ' + g.resumo + ' ' + g.oque + ' ' + g.porque + ' ' + g.influencia + ' ' + g.comoagir).includes(nq));
  }, [q]);

  const search = (
    <input type="search" placeholder="🔎 Buscar conceito…" value={q} onChange={e => setQ(e.target.value)} />
  );
  const empty = filtered.length === 0 && <p className="concept-empty">Nenhum termo encontrado.</p>;

  // Mobile: busca + acordeão (um aberto por vez).
  if (isMobile) {
    return (
      <div id="conceptsView">
        <div className="accordion">
          {search}
          {filtered.map(({ g, i }) => {
            const isOpen = open === i;
            return (
              <div className={'acc-item' + (isOpen ? ' open' : '')} key={i}>
                <button className="acc-header" aria-expanded={isOpen} onClick={() => setOpen(isOpen ? null : i)}>
                  <span className="ico"><CatIcon cat={g.cat} /></span>
                  <span className="acc-name"><b>{g.termo}</b><small className="acc-sub">{g.cat}</small></span>
                  <span className="chev" aria-hidden="true">▾</span>
                </button>
                {isOpen && <div className="acc-body"><ConceptDetail g={g} /></div>}
              </div>
            );
          })}
          {empty}
        </div>
      </div>
    );
  }

  // Desktop: busca + lista (esq) + detalhe (dir), sempre mostrando um conceito.
  const sel = (open != null && GLOSSARY[open]) ? open : (filtered[0]?.i ?? 0);
  const g = GLOSSARY[sel];
  return (
    <div id="conceptsView">
      <div className="guide-wrap">
        <aside className="guide-list concept-aside">
          {search}
          <div>
            {filtered.map(({ g, i }) => (
              <button key={i} className={'concept-item' + (i === sel ? ' active' : '')} onClick={() => setOpen(i)}>
                <span className="ci-ico"><CatIcon cat={g.cat} /></span>
                <span className="ci-txt"><b>{g.termo}</b><small>{g.cat}</small></span>
              </button>
            ))}
          </div>
          {empty}
        </aside>
        <section className="guide-detail"><ConceptDetail g={g} /></section>
      </div>
    </div>
  );
}
