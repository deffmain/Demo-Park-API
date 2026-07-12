import { useState, useEffect } from 'react';

// True quando a viewport é estreita (mobile). Breakpoint 900px = onde Guia/Conceitos
// deixam de caber lado-a-lado e passam a empilhar (vira acordeão). (frente A)
export function useIsMobile(breakpoint = 900) {
  const get = () => (typeof window !== 'undefined' ? window.innerWidth <= breakpoint : false);
  const [mobile, setMobile] = useState(get);
  useEffect(() => {
    const onResize = () => setMobile(get());
    window.addEventListener('resize', onResize);
    return () => window.removeEventListener('resize', onResize);
  }, [breakpoint]);
  return mobile;
}
