// Preview de solo no painel (pH/fertilidade/EC/riscos). A simulação real roda no backend.
export const EC_BASE = 0.6;

export const SOILS = {
  arenoso: { nome: 'Arenoso', cor: '#e8d6a8', ph: 5.8, fertilidade: 25, retencaoAgua: 20, drenagem: 90, desc: 'Drena muito rápido, esquenta fácil, mas retém pouca água e nutrientes.' },
  argiloso: { nome: 'Argiloso', cor: '#c98a63', ph: 6.0, fertilidade: 55, retencaoAgua: 85, drenagem: 25, desc: 'Retém muita água e nutrientes, porém compacta e drena mal.' },
  siltoso: { nome: 'Siltoso', cor: '#bfa980', ph: 6.3, fertilidade: 55, retencaoAgua: 65, drenagem: 50, desc: 'Macio e fértil, retenção média-alta; pode encrostar na superfície.' },
  franco: { nome: 'Franco (ideal)', cor: '#9c6b43', ph: 6.5, fertilidade: 70, retencaoAgua: 60, drenagem: 65, desc: 'Equilíbrio entre areia, silte e argila. O solo mais versátil.' },
  humoso: { nome: 'Humoso', cor: '#5d4030', ph: 6.4, fertilidade: 90, retencaoAgua: 75, drenagem: 60, desc: 'Rico em matéria orgânica, muito fértil e com boa estrutura.' }
};

export const AMENDMENTS = {
  composto: { nome: 'Composto orgânico', emoji: '🍂', unidade: 'kg/m²', rec: [2, 5], max: 12, efeito: { ph: 0.05, fert: 6, ret: 2, dren: 1 }, dEC: 0.05, risco: { acima: 8, txt: 'Excesso de matéria orgânica pode imobilizar nitrogênio e reter água demais.' }, nota: 'Adubo de fundo versátil; incorpore antes do plantio.' },
  esterco: { nome: 'Esterco curtido', emoji: '💩', unidade: 'kg/m²', rec: [1, 3], max: 8, efeito: { ph: 0.08, fert: 9, ret: 2, dren: 0 }, dEC: 0.25, risco: { acima: 5, txt: 'Esterco fresco ou em excesso saliniza o solo e queima as raízes — use bem curtido.' }, nota: 'Rico em nutrientes; nunca aplique fresco junto às plantas.' },
  humus: { nome: 'Húmus de minhoca', emoji: '🪱', unidade: 'kg/m²', rec: [1, 3], max: 8, efeito: { ph: 0.04, fert: 7, ret: 4, dren: 2 }, dEC: 0.03, risco: { acima: 7, txt: 'Em excesso encarece sem ganho proporcional.' }, nota: 'Suave e equilibrado, bom para mudas e cobertura.' },
  npk: { nome: 'NPK 10-10-10', emoji: '🧪', unidade: 'g/m²', rec: [20, 60], max: 150, efeito: { ph: -0.004, fert: 0.4, ret: 0, dren: 0 }, dEC: 0.02, risco: { acima: 100, txt: 'Excesso de adubo mineral saliniza o solo e queima raízes (estresse salino).' }, nota: 'Adubo de cobertura de ação rápida; parcele em duas aplicações.' },
  calcario: { nome: 'Calcário (sobe pH)', emoji: '⛰️', unidade: 'g/m²', rec: [100, 300], max: 800, efeito: { ph: 0.004, fert: 0.02, ret: 0, dren: 0 }, dEC: 0.001, risco: { acima: 500, txt: 'Supercalagem: pH alto demais trava micronutrientes (ferro, manganês, zinco).' }, nota: 'Reage devagar; aplique ~30–60 dias antes do plantio.' },
  enxofre: { nome: 'Enxofre (baixa pH)', emoji: '🟡', unidade: 'g/m²', rec: [30, 100], max: 250, efeito: { ph: -0.008, fert: 0, ret: 0, dren: 0 }, dEC: 0.002, risco: { acima: 180, txt: 'Acidificação excessiva prejudica microbiota e disponibilidade de nutrientes.' }, nota: 'Use para acidificar solos alcalinos (ex.: para morango, batata).' },
  cinza: { nome: 'Cinza de madeira', emoji: '🔥', unidade: 'g/m²', rec: [50, 150], max: 400, efeito: { ph: 0.003, fert: 0.05, ret: 0, dren: 0 }, dEC: 0.004, risco: { acima: 300, txt: 'Alcaliniza demais e concentra sais de potássio.' }, nota: 'Fonte de potássio que também sobe o pH; alternativa ao calcário.' },
  areia: { nome: 'Areia (drenagem)', emoji: '🏖️', unidade: 'kg/m²', rec: [3, 8], max: 20, efeito: { ph: 0, fert: -0.5, ret: -2, dren: 3 }, dEC: 0, risco: { acima: 15, txt: 'Areia demais empobrece o solo e reduz a retenção de água.' }, nota: 'Solta solos argilosos pesados; combine com matéria orgânica.' },
  turfa: { nome: 'Turfa (baixa pH)', emoji: '🟤', unidade: 'kg/m²', rec: [1, 4], max: 10, efeito: { ph: -0.1, fert: 0.5, ret: 5, dren: -1 }, dEC: 0.01, risco: { acima: 8, txt: 'Pode acidificar e encharcar em excesso.' }, nota: 'Acidifica e aumenta retenção; boa para canteiros arenosos.' }
};

export const doseRecomendada = (id) => Math.round((AMENDMENTS[id].rec[0] + AMENDMENTS[id].rec[1]) / 2 * 10) / 10;

export function normalizeAmendments(arr) {
  if (!Array.isArray(arr)) return [];
  return arr.map(a => typeof a === 'string' ? { id: a, dose: doseRecomendada(a) } : a).filter(a => AMENDMENTS[a.id]);
}

const IDADE_MAX = 100000; // estado final do regime (todas as aplicações)

export const passoDias = (freq) => ({ diaria: 1, semanal: 7, quinzenal: 15, mensal: 30 }[freq] || 0);

// dose acumulada de um insumo até 'idade' (dias após o plantio), conforme freq/duração
export function cumulativeDose(a, idade) {
  const dose = a.dose || 0;
  if (idade < 0) return 0;
  const passo = passoDias(a.freq);
  if (passo === 0) return dose;                 // única: aplicada uma vez, persiste
  const dur = a.duracaoDias != null ? a.duracaoDias : 30;
  const eff = Math.min(idade, Math.max(0, dur));
  return dose * (Math.floor(eff / passo) + 1);
}

export function deriveSoilAt(soilCfg, idade) {
  const base = SOILS[soilCfg.type] || SOILS.arenoso;
  let ph = base.ph, fert = base.fertilidade, ret = base.retencaoAgua, dren = base.drenagem;
  for (const a of normalizeAmendments(soilCfg.amendments)) {
    const am = AMENDMENTS[a.id]; if (!am) continue;
    const d = cumulativeDose(a, idade);
    ph += am.efeito.ph * d; fert += am.efeito.fert * d; ret += am.efeito.ret * d; dren += am.efeito.dren * d;
  }
  const clamp = v => Math.max(0, Math.min(100, Math.round(v)));
  return { type: soilCfg.type, nome: base.nome, ph: Math.max(3.5, Math.min(9.5, Math.round(ph * 10) / 10)), fertilidade: clamp(fert), retencaoAgua: clamp(ret), drenagem: clamp(dren) };
}

export function soilECAt(soilCfg, idade) {
  let ec = EC_BASE;
  for (const a of normalizeAmendments(soilCfg.amendments)) ec += (AMENDMENTS[a.id].dEC || 0) * cumulativeDose(a, idade);
  return Math.round(ec * 100) / 100;
}

// estado final do regime (usado na pré-visualização do painel e nos riscos)
export const deriveSoil = (cfg) => deriveSoilAt(cfg, IDADE_MAX);
export const soilEC = (cfg) => soilECAt(cfg, IDADE_MAX);

export function soilRisks(soilCfg) {
  const risks = [];
  const ams = normalizeAmendments(soilCfg.amendments);
  for (const a of ams) {
    const am = AMENDMENTS[a.id];
    if (am.risco && a.dose > am.risco.acima) risks.push(`${am.emoji} ${am.nome}: dose ${a.dose} ${am.unidade} acima do seguro (${am.risco.acima}). ${am.risco.txt}`);
  }
  const d = deriveSoil(soilCfg);
  if (d.ph >= 7.6) risks.push(`⚗️ pH ${d.ph} alcalino demais — trava micronutrientes (Fe, Mn, Zn).`);
  if (d.ph <= 4.8) risks.push(`⚗️ pH ${d.ph} ácido demais — risco de toxidez por alumínio.`);
  return risks;
}
