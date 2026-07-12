import React, { useEffect, useRef, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { api } from '../api.js';
import { SOILS, AMENDMENTS, deriveSoil, soilEC, soilRisks, doseRecomendada, normalizeAmendments, deriveSoilAt, soilECAt } from '../data/soil.js';
import Icon from '../Icon.jsx';

const SEASON_NOME = { verao: 'Verão', outono: 'Outono', inverno: 'Inverno', primavera: 'Primavera' };
const SEASON_ORDER = ['verao', 'outono', 'inverno', 'primavera'];
const seasonFromMonth = (m) => (m === 11 || m <= 1) ? 'verao' : m <= 4 ? 'outono' : m <= 7 ? 'inverno' : 'primavera';
const key = (x, y) => `${x},${y}`;
const clone = (o) => JSON.parse(JSON.stringify(o));

// Largura da viewport (reativa) — usada para dimensionar a grade no mobile. (frente A)
function useViewportWidth() {
  const [vw, setVw] = useState(() => (typeof window !== 'undefined' ? window.innerWidth : 1200));
  useEffect(() => {
    const onResize = () => setVw(window.innerWidth);
    window.addEventListener('resize', onResize);
    return () => window.removeEventListener('resize', onResize);
  }, []);
  return vw;
}
// Tamanho da célula que faz a grade CABER na largura disponível quando o layout empilha
// (≤1100px). Acima disso o planejador é "enquadrado" em 3 colunas → mantém o tamanho base.
function fitCell(cols, base, vw) {
  if (vw > 1100 || !cols) return base;
  const GAP = 2, PADDING = 28, MIN = 30;   // gap do #grid + folga do container; mín. legível/tocável
  const fit = Math.floor((vw - PADDING - GAP * (cols - 1)) / cols);
  return Math.max(MIN, Math.min(base, fit));
}

export default function Planner() {
  const { id } = useParams();
  const nav = useNavigate();
  const vw = useViewportWidth();   // p/ dimensionar a grade no mobile (frente A)

  const [meta, setMeta] = useState(null);          // {name,w,h,cellCm,zoom,day,startDate,season,climate,irrig,seasons}
  const [cells, setCells] = useState({});
  const [crops, setCrops] = useState([]);
  const [climates, setClimates] = useState([]);
  const [tool, setTool] = useState('bed');
  const [activeSoil, setActiveSoil] = useState({ type: 'franco', amendments: [] });
  const [activeCrop, setActiveCrop] = useState(null);
  const [selected, setSelected] = useState(null);
  const [sim, setSim] = useState(null);
  const [envOpen, setEnvOpen] = useState(false);
  const [status, setStatus] = useState('');
  const [alerts, setAlerts] = useState([]);
  const [editMode, setEditMode] = useState(false);
  // Seleção múltipla para aplicação de solo/adubo em um grupo de células.
  // `selectMode` ativa o "lápis": cliques marcam/desmarcam, em vez de pintar.
  const [selectMode, setSelectMode] = useState(false);
  const [selection, setSelection] = useState(() => new Set());

  const painting = useRef(false);
  const previewTimer = useRef(null);
  const saveTimer = useRef(null);
  // Sentinela para evitar que o setActiveSoil feito durante o carregamento do
  // projeto dispare um save imediato (efeito ouvindo activeSoil mais abaixo).
  const activeSoilHydrated = useRef(false);
  const cropsById = Object.fromEntries(crops.map(c => [c.id, c]));

  // -------- carregar --------
  useEffect(() => {
    let alive = true;
    (async () => {
      const [p, cr, cl] = await Promise.all([api.getProject(id), api.crops(), api.climates()]);
      if (!alive) return;
      cr.sort((a, b) => a.nome.localeCompare(b.nome));
      setCrops(cr); setClimates(cl);
      setActiveCrop(cr[0]?.id);
      let seasons = p.seasonsJson;
      if (!seasons || !seasons.verao) {
        const c = cl.find(x => x.id === (p.climate || 'cerrado')) || cl[0];
        seasons = c ? clone(c.seasons) : null;
      }
      const metaObj = { name: p.name, w: p.gridW, h: p.gridH, cellCm: p.cellCm, zoom: p.zoom, day: p.day,
        startDate: p.startDate, season: p.season, climate: p.climate, irrig: p.irrig, seasons,
        latitude: p.latitude, longitude: p.longitude, city: p.city };
      setMeta(metaObj);
      setCells(p.cellsJson || {});
      if (p.activeSoilJson && typeof p.activeSoilJson === 'object') {
        setActiveSoil({
          type: p.activeSoilJson.type || 'franco',
          amendments: Array.isArray(p.activeSoilJson.amendments) ? p.activeSoilJson.amendments : []
        });
      }
      activeSoilHydrated.current = true;
      preview(p.cellsJson || {}, metaObj);
      api.refreshAlerts(id).then(setAlerts).catch(() => { });
    })();
    return () => { alive = false; };
    // eslint-disable-next-line
  }, [id]);

  // -------- preview (POST, sem gravar) + save (PUT) --------
  const buildPayload = (c, m) => ({
    name: m.name, gridW: m.w, gridH: m.h, cellCm: m.cellCm, zoom: m.zoom, day: m.day,
    startDate: m.startDate, season: m.season, climate: m.climate, irrig: m.irrig,
    latitude: m.latitude, longitude: m.longitude, city: m.city,
    seasonsJson: m.seasons, cellsJson: c, activeSoilJson: activeSoil
  });
  // preview roda no servidor SEM tocar no banco (caminho quente das edições)
  async function preview(c, mOv) {
    try {
      const s = await api.simulatePreview(buildPayload(c ?? cells, { ...meta, ...(mOv || {}) }));
      setSim(s);
    } catch (e) { setStatus('⚠ erro na simulação'); }
  }
  // save persiste no banco (caminho frio, debounce maior)
  async function save(c, mOv) {
    try {
      setStatus('salvando…');
      await api.updateProject(id, buildPayload(c ?? cells, { ...meta, ...(mOv || {}) }));
      setStatus('✓ salvo');
      api.refreshAlerts(id).then(setAlerts).catch(() => { });   // realinha alertas (geada/água)
    } catch (e) { setStatus('⚠ erro ao salvar'); }
  }
  function scheduleSync(c, m) {
    setStatus('✎ não salvo');
    clearTimeout(previewTimer.current);
    previewTimer.current = setTimeout(() => preview(c, m), 400);   // preview rápido (sem banco)
    clearTimeout(saveTimer.current);
    saveTimer.current = setTimeout(() => save(c, m), 2500);        // auto-save espaçado
  }
  useEffect(() => {
    const up = () => { if (painting.current) { painting.current = false; scheduleSync(); } };
    window.addEventListener('mouseup', up);
    return () => window.removeEventListener('mouseup', up);
    // eslint-disable-next-line
  });

  // Modo de seleção só faz sentido com a ferramenta de solo ativa; ao trocar de ferramenta,
  // sai do modo e limpa a seleção para não confundir o usuário. O destaque de célula
  // inspecionada (.sel) também só faz sentido em 'inspect' — sem limpar, ele convive
  // com o :hover de outra célula e parecem dois contornos sobrepostos.
  useEffect(() => {
    if (tool !== 'soil' && selectMode) {
      setSelectMode(false);
      setSelection(new Set());
    }
    if (tool !== 'inspect' && selected) {
      setSelected(null);
    }
  }, [tool, selectMode, selected]);

  // Persiste o "pincel" (tipo de solo + insumos do painel) no banco quando muda.
  // Sem esse save dedicado, o usuário podia adicionar insumos no painel sem clicar
  // em "Aplicar" e perder tudo ao recarregar — o auto-save antigo só disparava
  // quando uma célula era tocada. O guard `activeSoilHydrated` evita um save
  // espúrio logo após o carregamento do projeto.
  useEffect(() => {
    if (!meta || !activeSoilHydrated.current) return;
    scheduleSync();
    // eslint-disable-next-line
  }, [activeSoil]);

  if (!meta) return <div style={{ padding: 20 }} className="muted">Carregando projeto…</div>;

  // -------- ações de ferramenta --------
  const cloneSoil = () => ({ type: activeSoil.type, amendments: activeSoil.amendments.map(a => ({ ...a })) });
  function applyTool(x, y, draft) {
    const k = key(x, y);
    const c = { ...(draft[k] || { bed: false, soil: null, crop: null }) };
    if (tool === 'bed') { c.bed = true; if (!c.soil) c.soil = cloneSoil(); draft[k] = c; }
    else if (tool === 'soil') { c.bed = true; c.soil = cloneSoil(); draft[k] = c; }
    else if (tool === 'plant') { c.bed = true; if (!c.soil) c.soil = cloneSoil(); c.crop = { id: activeCrop, plantedDay: meta.day }; draft[k] = c; }
    else if (tool === 'erase') { delete draft[k]; }
    else if (tool === 'inspect') { setSelected({ x, y }); }
  }
  function toggleSelectAt(x, y) {
    const k = key(x, y);
    setSelection(prev => {
      const next = new Set(prev);
      if (next.has(k)) next.delete(k); else next.add(k);
      return next;
    });
  }
  function addToSelection(x, y) {
    const k = key(x, y);
    setSelection(prev => prev.has(k) ? prev : new Set(prev).add(k));
  }
  function paintAt(x, y, isDrag = false) {
    // No modo seleção: clique único alterna, arrastar apenas adiciona (evita
    // toggle caótico ao passar várias vezes pela mesma célula).
    if (selectMode) {
      if (isDrag) addToSelection(x, y); else toggleSelectAt(x, y);
      return;
    }
    if (tool === 'inspect') { setSelected({ x, y }); return; }
    setCells(prev => { const d = { ...prev }; applyTool(x, y, d); return d; });
  }
  // Aplica o solo/adubo do painel a um conjunto de chaves "x,y".
  function applySoilToKeys(keys) {
    if (!keys || keys.length === 0) return;
    setCells(prev => {
      const d = { ...prev };
      for (const k of keys) {
        const c = { ...(d[k] || { bed: false, soil: null, crop: null }) };
        c.bed = true;
        c.soil = cloneSoil();
        d[k] = c;
      }
      scheduleSync(d);
      return d;
    });
  }
  function applySoilToAllBeds() {
    const keys = Object.keys(cells).filter(k => cells[k] && (cells[k].bed || cells[k].crop));
    if (keys.length === 0) { setStatus('⚠ nenhum canteiro para aplicar'); return; }
    applySoilToKeys(keys);
    setStatus(`✓ adubo aplicado em ${keys.length} ${keys.length > 1 ? 'células' : 'célula'}`);
  }
  function applySoilToSelection() {
    const keys = Array.from(selection);
    if (keys.length === 0) { setStatus('⚠ selecione células primeiro'); return; }
    applySoilToKeys(keys);
    setStatus(`✓ adubo aplicado em ${keys.length} ${keys.length > 1 ? 'células' : 'célula'} selecionadas`);
  }
  function clearSelection() { setSelection(new Set()); }
  function toggleSelectMode() {
    setSelectMode(v => {
      const next = !v;
      if (!next) setSelection(new Set());  // sair do modo limpa a seleção
      return next;
    });
  }
  function applyDims(nw, nh) {
    nw = Math.max(2, Math.min(40, nw)); nh = Math.max(2, Math.min(30, nh));
    const nc = {};
    for (const k in cells) { const [cx, cy] = k.split(',').map(Number); if (cx < nw && cy < nh) nc[k] = cells[k]; }
    setCells(nc);
    setMeta(m => ({ ...m, w: nw, h: nh }));
    scheduleSync(nc, { w: nw, h: nh });
  }

  // -------- atualizar meta --------
  function patchMeta(patch, sync = true) {
    setMeta(prev => ({ ...prev, ...patch }));
    if (sync) scheduleSync(undefined, patch);
  }
  function setDay(v) {
    const day = Math.max(0, Math.min(365, v));
    patchMeta({ day });
  }

  // -------- estação atual / data --------
  function simDate(extraDays = 0) {
    if (!meta.startDate) return null;
    const d = new Date(meta.startDate + 'T00:00:00');
    d.setDate(d.getDate() + meta.day + extraDays);
    return d;
  }
  function activeSeason() {
    if (meta.season && meta.season !== 'auto') return meta.season;
    const d = simDate();
    return d ? seasonFromMonth(d.getMonth()) : 'verao';
  }
  const estNome = SEASON_NOME[activeSeason()];
  const estTMedia = meta.seasons?.[activeSeason()]?.tMedia;

  // ============ RENDER ============
  return (
    <div className="planner-screen">
      <div className="planner-bar">
        <button className="ghost" onClick={() => nav('/')}>← Projetos</button>
        <b>🌿 {meta.name}</b>
        <span className="muted" style={{ marginLeft: 'auto' }}>{status}</span>
        <button className="btn-apply" style={{ width: 'auto', margin: 0 }} onClick={() => save()}>💾 Salvar</button>
      </div>

      {alerts.length > 0 && (
        <div className="alerts-bar">
          {alerts.map(a => (
            <div className={'alert-item ' + a.type} key={a.id}>
              <span className="a-ic">{a.type === 'frost' ? '❄️' : '💧'}</span>
              <span className="a-msg"><b>{a.title}</b> — {a.message}</span>
              <button className="a-x" title="Dispensar" onClick={() => api.dismissAlert(a.id).then(() => setAlerts(alerts.filter(x => x.id !== a.id)))}>✕</button>
            </div>
          ))}
        </div>
      )}

      <div className="app">
        {/* ----------- ESQUERDA ----------- */}
        <div className="left">
          <div className="card">
            <h2>Ferramentas</h2>
            <div className="body">
              <div className="tool-grid">
                {[['bed', '🟫', 'Canteiro'], ['soil', '🪨', 'Solo'], ['plant', '🌱', 'Plantar'], ['inspect', '🔍', 'Inspecionar'], ['erase', '🧽', 'Apagar']].map(([t, ic, lb]) => (
                  <button key={t} className={tool === t ? 'active' : ''} onClick={() => setTool(t)}><span className="ico">{ic}</span>{lb}</button>
                ))}
              </div>
              <p className="hint">Clique e arraste na grade. Salvamento e simulação são automáticos.</p>
            </div>
          </div>

          {tool === 'soil' && (
            <SoilPanel activeSoil={activeSoil} setActiveSoil={setActiveSoil}
              selectMode={selectMode} selection={selection}
              onApplyAll={applySoilToAllBeds} onToggleSelectMode={toggleSelectMode}
              onApplyToSelection={applySoilToSelection} onClearSelection={clearSelection} />
          )}
          {tool === 'plant' && <PlantPanel crops={crops} activeCrop={activeCrop} setActiveCrop={setActiveCrop} nav={nav} />}

          <EnvCard meta={meta} estNome={estNome} estTMedia={estTMedia} simDate={simDate} onOpen={() => setEnvOpen(true)} />

          <TerrainCard meta={meta} setCells={setCells} cells={cells} patchMeta={patchMeta} setMetaRaw={setMeta} sync={scheduleSync} />
        </div>

        {/* ----------- CENTRO ----------- */}
        <div className="center">
          <div className="card">
            <h2 className="layout-head">
              <span>Layout da plantação</span>
              {sim?.summary && (
                <span className="layout-weather">
                  📅 {simDate() ? simDate().toLocaleDateString('pt-BR', { day: '2-digit', month: 'short' }) : `dia ${meta.day}`}
                  {' · '}{estNome}{' · '}🌡️ {sim.summary.estacaoTMedia}°C
                  <small> (mín {sim.summary.estacaoMinNoite}°)</small>
                  <em className={sim.summary.climaReal ? 'real' : ''}>{sim.summary.climaReal ? '🛰️ clima real' : 'climatologia'}</em>
                </span>
              )}
            </h2>
            <div className="body">
              <div className="field-wrap">
                <Grid meta={meta} cells={cells} sim={sim} selected={selected} cropsById={cropsById}
                  selection={selection} selectMode={selectMode} cell={fitCell(meta.w, 60, vw)}
                  onDown={(x, y) => { painting.current = true; paintAt(x, y, false); }}
                  onOver={(x, y) => { if (painting.current) paintAt(x, y, true); }} />
              </div>
              <div className="legend">
                <span><b style={{ background: '#eef1ea' }}></b>Vazio</span>
                <span><b style={{ background: '#9c6b43' }}></b>Canteiro</span>
                <span><b style={{ background: 'var(--excelente)' }}></b>Excelente</span>
                <span><b style={{ background: 'var(--atencao)' }}></b>Atenção</span>
                <span><b style={{ background: 'var(--critico)' }}></b>Crítica</span>
                <span><b style={{ background: '#6b6b6b' }}></b>Perdida 💀</span>
                <button className="edit-open" onClick={() => setEditMode(true)} title="Editar o canteiro em tela cheia">⛶ Editar canteiro</button>
              </div>
            </div>
          </div>

          <div className="card">
            <h2>Linha do tempo (simulação · 1 ano)</h2>
            <div className="body">
              <div className="time-row">
                <span className="day">{`Dia ${meta.day}`}{simDate() ? ' · ' + simDate().toLocaleDateString('pt-BR', { day: '2-digit', month: 'short', year: 'numeric' }) : ''} · {estNome}</span>
                <input type="range" min="0" max="365" value={meta.day} onChange={e => setDay(+e.target.value)} />
              </div>
              <div className="time-nav">
                {[['-30', '⟲ mês'], ['-7', '−7d'], ['0', 'hoje'], ['7', '+7d'], ['30', 'mês ⟳']].map(([j, lb]) => (
                  <button key={j} onClick={() => setDay(+j === 0 ? 0 : meta.day + +j)}>{lb}</button>
                ))}
              </div>
            </div>
          </div>
        </div>

        {/* ----------- DIREITA ----------- */}
        <div className="right">
          <div className="card">
            <h2>Inspetor</h2>
            <div className="body"><Inspector selected={selected} cells={cells} sim={sim} cropsById={cropsById} day={meta.day} /></div>
          </div>
          <div className="card">
            <h2>Análise da cultura</h2>
            <div className="body"><Analysis sim={sim} /></div>
          </div>
          <div className="card">
            <h2>Sugestões de melhoria</h2>
            <div className="body"><Suggestions sim={sim} /></div>
          </div>
        </div>
      </div>

      {envOpen && <EnvModal meta={meta} climates={climates} patchMeta={patchMeta} onClose={() => setEnvOpen(false)} />}

      {editMode && (
        <div className="edit-overlay">
          <div className="edit-bar">
            <b>🌱 Editando o canteiro</b>
            <div className="edit-tools">
              {[['bed', '🟫', 'Canteiro'], ['soil', '🪨', 'Terra/Solo'], ['plant', '🌱', 'Plantar'], ['erase', '🧽', 'Apagar']].map(([t, ic, lb]) => (
                <button key={t} className={tool === t ? 'active' : ''} onClick={() => setTool(t)} title={lb}><span>{ic}</span>{lb}</button>
              ))}
            </div>
            <div className="edit-dims">
              <span>Colunas</span>
              <button onClick={() => applyDims(meta.w - 1, meta.h)}>−</button><b>{meta.w}</b><button onClick={() => applyDims(meta.w + 1, meta.h)}>＋</button>
              <span>Linhas</span>
              <button onClick={() => applyDims(meta.w, meta.h - 1)}>−</button><b>{meta.h}</b><button onClick={() => applyDims(meta.w, meta.h + 1)}>＋</button>
            </div>
            <button className="btn-apply" style={{ width: 'auto', margin: 0 }} onClick={() => setEditMode(false)}>✓ Concluir</button>
          </div>
          <div className="edit-main">
            {(tool === 'soil' || tool === 'plant') && (
              <aside className="edit-aside">
                {tool === 'soil' && (
                  <SoilPanel activeSoil={activeSoil} setActiveSoil={setActiveSoil}
                    selectMode={selectMode} selection={selection}
                    onApplyAll={applySoilToAllBeds} onToggleSelectMode={toggleSelectMode}
                    onApplyToSelection={applySoilToSelection} onClearSelection={clearSelection} />
                )}
                {tool === 'plant' && <PlantPanel crops={crops} activeCrop={activeCrop} setActiveCrop={setActiveCrop} nav={nav} />}
              </aside>
            )}
            <div className="field-wrap edit-grid">
              <Grid meta={meta} cells={cells} sim={sim} selected={selected} cropsById={cropsById} cell={fitCell(meta.w, 64, vw)}
                selection={selection} selectMode={selectMode}
                onDown={(x, y) => { painting.current = true; paintAt(x, y, false); }}
                onOver={(x, y) => { if (painting.current) paintAt(x, y, true); }} />
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

/* ===================== Subcomponentes ===================== */

function Grid({ meta, cells, sim, selected, cropsById, onDown, onOver, cell = 60, selection, selectMode }) {
  const em = (id) => cropsById[id]?.emoji || '🌱';
  const zoom = cell;
  const cellsArr = [];
  for (let y = 0; y < meta.h; y++) for (let x = 0; x < meta.w; x++) cellsArr.push([x, y]);
  return (
    <div id="grid" className={selectMode ? 'select-mode' : ''} style={{ gridTemplateColumns: `repeat(${meta.w}, ${zoom}px)`, width: 'max-content', margin: '0 auto' }}>
      {cellsArr.map(([x, y]) => {
        const k = key(x, y);
        const c = cells[k];
        const sr = sim?.cells?.[k];
        const cls = ['cell'];
        const style = { width: zoom, height: zoom, background: '#eef1ea' };
        let inner = null;
        if (c && (c.bed || c.crop)) {
          cls.push('bed');
          style.background = c.soil ? (SOILS[deriveSoil(c.soil).type]?.cor || '#cdbfa6') : '#cdbfa6';
        }
        if (selected && selected.x === x && selected.y === y) cls.push('sel');
        if (selection && selection.has(k)) cls.push('in-sel');
        if (c && c.crop) {
          const crop = c.crop;
          if (sr && sr.lost) {
            cls.push('lost');
            const ico = sr.lostKind === 'passou' ? '⚠️' : sr.lostKind === 'bolting' ? '🌼' : '💀';
            inner = (<>
              <span style={{ fontSize: Math.round(zoom * 0.45), filter: 'grayscale(1)', opacity: .45 }}><Icon emoji={em(crop.id)} /></span>
              <span className="stage">{ico}</span>
            </>);
          } else {
            const frac = sr ? sr.frac : 0.4;
            const scale = 0.55 + 0.45 * Math.min(1, frac);
            inner = (<>
              <span style={{ fontSize: Math.round(zoom * 0.5 * scale) }}><Icon emoji={em(crop.id)} /></span>
              {sr && <span className="stage">{sr.stageIcone}</span>}
              {sr && <div className="health"><i style={{ width: sr.score + '%', background: sr.statusCor }} /></div>}
            </>);
          }
        } else if (c && c.bed) { cls.push('empty-bed'); inner = '·'; }
        return (
          <div key={key(x, y)} className={cls.join(' ')} style={style}
            title={sr ? `${sr.cropNome} · ${sr.statusNome} ${sr.lost ? '— ' + sr.causa : '(' + sr.score + '%)'}` : ''}
            onMouseDown={() => onDown(x, y)} onMouseOver={() => onOver(x, y)}>
            {inner}
          </div>
        );
      })}
    </div>
  );
}
function metricBar(label, val, cor) {
  return (
    <div className="metric"><div className="lab"><span>{label}</span><b>{val}/100</b></div>
      <div className="bar"><i style={{ width: val + '%', background: cor || 'var(--verde-claro)' }} /></div></div>
  );
}

function SoilPanel({ activeSoil, setActiveSoil, selectMode, selection,
                    onApplyAll, onToggleSelectMode, onApplyToSelection, onClearSelection }) {
  const used = new Set(activeSoil.amendments.map(a => a.id));
  const d = deriveSoil(activeSoil);
  const risks = soilRisks(activeSoil);
  const phCor = d.ph < 5.5 || d.ph > 7.2 ? 'var(--atencao)' : 'var(--bom)';
  const setField = (i, f, v) => setActiveSoil(s => ({ ...s, amendments: s.amendments.map((a, k) => k === i ? { ...a, [f]: v } : a) }));
  const remove = (i) => setActiveSoil(s => ({ ...s, amendments: s.amendments.filter((_, k) => k !== i) }));
  const FREQS = [['unica', 'Única'], ['diaria', 'Diária'], ['semanal', 'Semanal'], ['quinzenal', 'Quinzenal'], ['mensal', 'Mensal']];
  const selCount = selection ? selection.size : 0;
  return (
    <div className="card">
      <h2>Simular solo</h2>
      <div className="body">
        {onApplyAll && (
          <div className="soil-apply-bar">
            <button type="button" className="btn-apply-all" onClick={onApplyAll}
              title="Aplica este adubo a todos os canteiros existentes">
              🪴 Aplicar a todos os canteiros
            </button>
            <button type="button" className={'btn-pencil' + (selectMode ? ' on' : '')} onClick={onToggleSelectMode}
              title="Selecionar células específicas para adubar (modo lápis)">
              ✏️
            </button>
          </div>
        )}
        {selectMode && (
          <div className="soil-select-status">
            <span><b>{selCount}</b> {selCount === 1 ? 'célula selecionada' : 'células selecionadas'}</span>
            <span className="muted" style={{ flex: 1, fontSize: 11 }}>Clique nas células do canteiro para marcar/desmarcar.</span>
            <button type="button" className="btn-apply-sel" disabled={selCount === 0} onClick={onApplyToSelection}>
              ✓ Aplicar à seleção
            </button>
            <button type="button" className="ghost" disabled={selCount === 0} onClick={onClearSelection}>Limpar</button>
          </div>
        )}
        <label className="field">Tipo de solo</label>
        <select value={activeSoil.type} onChange={e => setActiveSoil(s => ({ ...s, type: e.target.value }))}>
          {Object.entries(SOILS).map(([k, s]) => <option key={k} value={k}>{s.nome}</option>)}
        </select>
        <p className="muted">{SOILS[activeSoil.type].desc}</p>
        <label className="field">Adicionar insumo / adubo</label>
        <select value="" onChange={e => { if (e.target.value) setActiveSoil(s => ({ ...s, amendments: [...s.amendments, { id: e.target.value, dose: doseRecomendada(e.target.value), freq: 'unica', duracaoDias: 30 }] })); }}>
          <option value="">+ adicionar insumo / adubo…</option>
          {Object.keys(AMENDMENTS).filter(k => !used.has(k)).map(k => <option key={k} value={k}>{AMENDMENTS[k].emoji} {AMENDMENTS[k].nome}</option>)}
        </select>
        <div>
          {activeSoil.amendments.map((a, i) => {
            const am = AMENDMENTS[a.id]; const over = a.dose > (am.risco?.acima ?? Infinity);
            const freq = a.freq || 'unica';
            return (
              <div className={'amend-dose' + (over ? ' warn' : '')} key={a.id}>
                <div className="head"><span>{am.emoji} {am.nome}</span><button onClick={() => remove(i)}>✕</button></div>
                <div className="dose-row">
                  <input type="range" min="0" max={am.max} step={am.unidade.startsWith('g') ? 5 : 0.5} value={a.dose} onChange={e => setField(i, 'dose', parseFloat(e.target.value))} />
                  <span className="dose-val">{a.dose} {am.unidade}<small>/aplic.</small></span>
                </div>
                <div className="freq-row">
                  <span>Aplicar:</span>
                  <select value={freq} onChange={e => setField(i, 'freq', e.target.value)}>
                    {FREQS.map(([v, lb]) => <option key={v} value={v}>{lb}</option>)}
                  </select>
                  {freq !== 'unica' && (
                    <span>por <input type="number" min="1" max="365" value={a.duracaoDias ?? 30} onChange={e => setField(i, 'duracaoDias', parseInt(e.target.value) || 1)} /> dias</span>
                  )}
                </div>
                <div className="rec">Recomendado: {am.rec[0]}–{am.rec[1]} {am.unidade}/aplicação. {freq === 'unica' ? 'Incorporação única no plantio.' : 'O efeito acumula a cada aplicação.'} {am.nota}{over ? <><br />⚠️ {am.risco.txt}</> : null}</div>
              </div>
            );
          })}
        </div>
        <div className="soil-preview">
          <b style={{ fontSize: 12 }}>Resultado ao fim do regime (acúmulo)</b>
          <div className="metric"><div className="lab"><span>pH</span><b style={{ color: phCor }}>{d.ph}</b></div></div>
          {metricBar('Fertilidade', d.fertilidade)}
          {metricBar('Retenção de água', d.retencaoAgua, '#42a5f5')}
          {metricBar('Drenagem', d.drenagem, '#8d6e63')}
          {risks.length ? risks.map((r, k) => <div className="risk" key={k}>{r}</div>)
            : <div className="risk" style={{ borderColor: 'var(--bom)', background: '#f1f8f1' }}>✅ Sem riscos detectados nesta mistura.</div>}
        </div>
        <p className="hint">Defina a dose por aplicação, a frequência e por quantos dias. Aplicação <b>única</b> = incorporação no plantio; <b>periódica</b> acumula no solo (e mais aplicações = mais fertilidade, mas também mais sal). O resultado acima é o estado ao fim do regime.</p>
      </div>
    </div>
  );
}

function PlantPanel({ crops, activeCrop, setActiveCrop, nav }) {
  const c = crops.find(x => x.id === activeCrop);
  return (
    <div className="card">
      <h2>Escolher cultura</h2>
      <div className="body">
        <div className="crop-grid">
          {crops.map(cr => (
            <button key={cr.id} className={activeCrop === cr.id ? 'active' : ''} onClick={() => setActiveCrop(cr.id)}>
              <Icon emoji={cr.emoji} className="ico" />{cr.nome}
            </button>
          ))}
        </div>
        {c && (
          <div className="hint">
            <b><Icon emoji={c.emoji} /> {c.nome}</b> · {c.familia}<br />
            Ciclo ~{c.dias} dias · espaçamento {c.espacamentoCm} cm · sol {c.sun}<br />
            pH ideal {c.phMin}–{c.phMax} · solos: {(c.solosIdeais || []).join(', ')} · consumo {c.feeder}<br />
            🌡️ ótimo {c.tOtMin}–{c.tOtMax}°C<br />
            <i>💡 {c.dica}</i><br />
            <button className="link-guide" onClick={() => nav('/guia')}>📖 Ver guia completo</button>
          </div>
        )}
      </div>
    </div>
  );
}

function EnvCard({ meta, estNome, estTMedia, simDate, onOpen }) {
  const climNome = meta.climate === 'custom' ? 'Personalizado' : meta.climate;
  const d = simDate();
  return (
    <div className="card env-card" onClick={onOpen} title="Clique para configurar">
      <h2>Ambiente &amp; clima <span className="gear">⚙️</span></h2>
      <div className="body">
        <div className="env-row"><span>🌎 Clima</span><b style={{ textTransform: 'capitalize' }}>{climNome}</b></div>
        <div className="env-row"><span>📍 Local</span><b>{meta.city ? meta.city + ' · clima real' : 'climatologia'}</b></div>
        <div className="env-row"><span>📅 Início</span><b>{d ? d.toLocaleDateString('pt-BR') : '—'}</b></div>
        <div className="env-row"><span>🍂 Estação</span><b>{estNome}{estTMedia != null ? ` (~${estTMedia}°C)` : ''}</b></div>
        <div className="env-row"><span>💧 Irrigação</span><b>{meta.irrig} mm/sem</b></div>
        <div className="env-cta">⚙️ Clique para configurar</div>
      </div>
    </div>
  );
}

function EnvModal({ meta, climates, patchMeta, onClose }) {
  const seasons = meta.seasons || {};
  const [cityInput, setCityInput] = useState(meta.city || '');
  const [geoMsg, setGeoMsg] = useState(meta.latitude != null ? '✓ clima real ativado para esta localização' : '');
  async function buscarCidade() {
    const q = cityInput.trim();
    if (!q) return;
    setGeoMsg('buscando…');
    try {
      const g = await api.geocode(q);
      const nome = g.name + (g.region ? ', ' + g.region : '');
      patchMeta({ city: nome, latitude: g.latitude, longitude: g.longitude });
      setGeoMsg(`✓ ${nome} (${g.latitude.toFixed(2)}, ${g.longitude.toFixed(2)}) — clima diário real ativado`);
    } catch (e) { setGeoMsg('⚠ município não encontrado'); }
  }
  const limparLocal = () => { patchMeta({ city: null, latitude: null, longitude: null }); setCityInput(''); setGeoMsg('Sem localização — usando a climatologia da região.'); };
  const setClimate = (id) => {
    const c = climates.find(x => x.id === id);
    patchMeta({ climate: id, seasons: c ? clone(c.seasons) : meta.seasons });
  };
  const setSeasonVal = (k, field, val) => {
    const s = clone(meta.seasons || {});
    s[k] = { ...s[k], [field]: val };
    patchMeta({ climate: 'custom', seasons: s });
  };
  return (
    <div className="modal-overlay" onClick={e => { if (e.target.className === 'modal-overlay') onClose(); }}>
      <div className="modal">
        <div className="modal-head"><h2>🌦️ Ambiente &amp; clima</h2><button className="modal-close" onClick={onClose}>✕</button></div>
        <div className="modal-body">
          <div className="modal-grid">
            <div>
              <label className="field">📍 Localização (clima diário real)</label>
              <div style={{ display: 'flex', gap: 6 }}>
                <input style={{ flex: 1 }} placeholder="Ex.: Goiânia" value={cityInput}
                  onChange={e => setCityInput(e.target.value)}
                  onKeyDown={e => { if (e.key === 'Enter') buscarCidade(); }} />
                <button className="ghost" type="button" onClick={buscarCidade}>Buscar</button>
              </div>
              {geoMsg && <p className="hint" style={{ marginTop: 4 }}>{geoMsg}{meta.latitude != null && <> · <a style={{ cursor: 'pointer', color: 'var(--critico)' }} onClick={limparLocal}>remover</a></>}</p>}
              <label className="field">Data de início do plantio</label>
              <input type="date" value={meta.startDate || ''} onChange={e => patchMeta({ startDate: e.target.value || null })} />
              <label className="field">Estação do ano</label>
              <select value={meta.season} onChange={e => patchMeta({ season: e.target.value })}>
                <option value="auto">🔄 Automática (pela data)</option>
                {SEASON_ORDER.map(k => <option key={k} value={k}>{SEASON_NOME[k]}</option>)}
              </select>
              <label className="field">Irrigação semanal: <b>{meta.irrig}</b> mm</label>
              <input type="range" min="0" max="60" value={meta.irrig} onChange={e => patchMeta({ irrig: +e.target.value })} />
            </div>
            <div>
              <label className="field">Região / clima</label>
              <select value={meta.climate} onChange={e => setClimate(e.target.value)}>
                {climates.map(c => <option key={c.id} value={c.id}>{c.nome}</option>)}
                <option value="custom">Personalizado</option>
              </select>
              <table className="clima-tab">
                <tbody>
                  <tr><th>Estação</th><th>Média °C</th><th>Mín. noite</th><th>Chuva</th></tr>
                  {SEASON_ORDER.map(k => {
                    const s = seasons[k] || {};
                    return (
                      <tr key={k}>
                        <td>{SEASON_NOME[k]}</td>
                        <td><input type="number" value={s.tMedia ?? ''} onChange={e => setSeasonVal(k, 'tMedia', parseFloat(e.target.value) || 0)} /></td>
                        <td className="frost"><input type="number" value={s.minNoite ?? ''} onChange={e => setSeasonVal(k, 'minNoite', parseFloat(e.target.value) || 0)} /></td>
                        <td>{s.chuvaMmSemana ?? '—'} mm</td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
              <p className="hint">A geada só mata quando a mínima noturna fica ≤ ao limite da cultura. No Cerrado/Goiás, mantenha a mínima de inverno ~13°C.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

function TerrainCard({ meta, cells, setCells, patchMeta, setMetaRaw, sync }) {
  const [w, setW] = useState(meta.w), [h, setH] = useState(meta.h);
  return (
    <div className="card">
      <h2>Terreno &amp; layout</h2>
      <div className="body">
        <div className="dim-row">
          <div><label className="field">Colunas</label><input type="number" min="2" max="40" value={w} onChange={e => setW(+e.target.value)} /></div>
          <div><label className="field">Linhas</label><input type="number" min="2" max="30" value={h} onChange={e => setH(+e.target.value)} /></div>
        </div>
        <label className="field">Tamanho real da célula: <b>{meta.cellCm}</b> cm</label>
        <input type="range" min="10" max="100" step="5" value={meta.cellCm} onChange={e => patchMeta({ cellCm: +e.target.value })} />
        <button className="btn-apply" onClick={() => {
          const nw = Math.max(2, Math.min(40, w)), nh = Math.max(2, Math.min(30, h));
          const nc = {};
          for (const k in cells) { const [cx, cy] = k.split(',').map(Number); if (cx < nw && cy < nh) nc[k] = cells[k]; }
          setCells(nc);
          setMetaRaw(m => ({ ...m, w: nw, h: nh }));
          sync(nc, { w: nw, h: nh });
        }}>Aplicar dimensões</button>
        <p className="hint">Terreno: <b>{(meta.w * meta.cellCm / 100).toFixed(2)} × {(meta.h * meta.cellCm / 100).toFixed(2)} m</b> = {(meta.w * meta.h * Math.pow(meta.cellCm / 100, 2)).toFixed(2)} m²</p>
      </div>
    </div>
  );
}

function Inspector({ selected, cells, sim, cropsById, day }) {
  if (!selected) return <div className="empty-state">Selecione 🔍 e clique numa célula.</div>;
  const c = cells[key(selected.x, selected.y)];
  if (!c || (!c.bed && !c.crop)) return <div className="empty-state">Célula vazia.</div>;
  const sr = sim?.cells?.[key(selected.x, selected.y)];
  const idade = c.crop ? Math.max(0, day - (c.crop.plantedDay || 0)) : 100000;
  const soil = c.soil ? deriveSoilAt(c.soil, idade) : null;
  const ams = c.soil ? normalizeAmendments(c.soil.amendments) : [];
  return (
    <div>
      {soil && <>
        <div className="inspect-row"><span>Solo</span><b>{soil.nome}</b></div>
        <div className="inspect-row"><span>pH</span><b>{soil.ph}</b></div>
        <div className="inspect-row"><span>Fertilidade</span><b>{soil.fertilidade}/100</b></div>
        <div className="inspect-row"><span>Retenção de água</span><b>{soil.retencaoAgua}/100</b></div>
        <div className="inspect-row"><span>Drenagem</span><b>{soil.drenagem}/100</b></div>
        <div className="inspect-row"><span>Salinidade (EC)</span><b>{soilECAt(c.soil, idade)} dS/m</b></div>
        {ams.length > 0 && <div className="inspect-row"><span>Insumos</span><b style={{ textAlign: 'right' }}>{ams.map(a => `${AMENDMENTS[a.id].emoji} ${a.dose}${AMENDMENTS[a.id].unidade.replace('/m²', '')}`).join(', ')}</b></div>}
        {soilRisks(c.soil).map((r, k) => <div className="risk" key={k}>{r}</div>)}
      </>}
      {c.crop && sr && <>
        <hr style={{ border: 0, borderTop: '1px solid #eee', margin: '8px 0' }} />
        <div className="inspect-row"><span>Cultura</span><b><Icon emoji={sr.emoji} /> {sr.cropNome}</b></div>
        {sr.lost ? <>
          <div className="inspect-row"><span>Situação</span><b style={{ color: sr.statusCor }}>{sr.statusNome}</b></div>
          <div className="risk">💀 {sr.causa}</div>
        </> : <>
          <div className="inspect-row"><span>Estágio</span><b>{sr.stageIcone} {sr.stageNome} ({Math.round(sr.frac * 100)}%)</b></div>
          <div className="inspect-row"><span>Idade</span><b>{sr.idade} dias</b></div>
          <div className="inspect-row"><span>Colheita prevista</span><b>{sr.frac >= 1 ? 'pronto p/ colher' : 'dia ' + sr.harvestDay}</b></div>
          <div className="inspect-row"><span>Vitalidade</span><b style={{ color: sr.statusCor }}>{sr.score}% · {sr.statusNome}</b></div>
        </>}
        {sr.issues?.length > 0 && <>
          <div style={{ marginTop: 8 }}><b>Diagnóstico:</b></div>
          {sr.issues.map((i, k) => <div className="muted" style={{ marginTop: 3 }} key={k}>{i.sev === 'high' ? '⚠️' : '•'} {i.txt}</div>)}
        </>}
      </>}
    </div>
  );
}

function Analysis({ sim }) {
  if (!sim || sim.summary.plants === 0) return <div className="empty-state">Plante algo para ver as análises aqui.</div>;
  const s = sim.summary;
  const statusCor = s.vitalidadeMedia >= 80 ? 'var(--excelente)' : s.vitalidadeMedia >= 60 ? 'var(--bom)' : s.vitalidadeMedia >= 40 ? 'var(--atencao)' : 'var(--critico)';
  return (
    <div>
      <div className="kpi-grid">
        <div className="kpi"><div className="v">{s.plants}</div><div className="l">plantas ({s.areaM2} m²)</div></div>
        <div className="kpi"><div className="v" style={{ color: s.perdas ? 'var(--critico)' : 'var(--bom)' }}>{s.perdas}</div><div className="l">perdas no período</div></div>
        <div className="kpi"><div className="v" style={{ color: statusCor }}>{s.vitalidadeMedia}%</div><div className="l">vitalidade média</div></div>
        <div className="kpi"><div className="v">{s.prodKg}<small style={{ fontSize: 12 }}>kg</small></div><div className="l">colheita estimada</div></div>
      </div>
      <div style={{ marginTop: 10 }}>
        <div className="muted">Estação atual: <b>{s.estacaoNome}</b> (~{s.estacaoTMedia}°C) · água {s.ofertaMm} mm/sem ofertados vs {s.demandaMm} mm/sem exigidos ({s.demandaL} L/sem)</div>
        <div className="bar"><i style={{ width: Math.min(100, s.ofertaMm / Math.max(1, s.demandaMm) * 50) + '%', background: s.ofertaMm >= s.demandaMm ? 'var(--bom)' : 'var(--critico)' }} /></div>
      </div>
      {sim.harvests?.length > 0 && (
        <div style={{ marginTop: 12 }}>
          <b style={{ fontSize: 12 }}>Próximas colheitas</b>
          {sim.harvests.map((h, k) => (
            <div className="harvest-item" key={k}><span><Icon emoji={h.emoji} /> {h.nome}</span>
              <span className="tag" style={{ background: h.pronto ? 'var(--excelente)' : 'var(--bom)' }}>{h.pronto ? 'pronto ✅' : 'dia ' + h.dia}</span></div>
          ))}
        </div>
      )}
    </div>
  );
}

function Suggestions({ sim }) {
  if (!sim) return <div className="empty-state">Nenhuma sugestão ainda.</div>;
  const arr = sim.suggestions || [];
  if (arr.length === 0) return <div className="sugg ok"><span className="t">✅ Tudo certo!</span>Nenhum problema detectado.</div>;
  return (
    <div>
      {arr.map((i, k) => (
        <div className={'sugg ' + (i.sev === 'high' ? 'crit' : '')} key={k}>
          <span className="t">{i.sev === 'high' ? '⚠️' : '💡'} {i.txt}</span>
          {i.fix}
          <div className="n">afeta {i.n} {i.n > 1 ? 'plantas' : 'planta'}</div>
        </div>
      ))}
    </div>
  );
}
