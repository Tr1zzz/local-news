import { useEffect, useMemo, useRef, useState } from "react";
import { fetchCitySuggestions } from '../api/city'
import type { CityDto } from '../types/city'

type Props = {
  onSelect: (city: CityDto) => void;
  initialLabel?: string;
};

export default function CitySearch({ onSelect, initialLabel }: Props) {
  const [q, setQ] = useState("");
  const [open, setOpen] = useState(false);
  const [items, setItems] = useState<CityDto[]>([]);
  const [loading, setLoading] = useState(false);
  const boxRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const onDocClick = (e: MouseEvent) => {
      if (!boxRef.current) return;
      if (!boxRef.current.contains(e.target as Node)) setOpen(false);
    };
    document.addEventListener("click", onDocClick);
    return () => document.removeEventListener("click", onDocClick);
  }, []);

  // debounce
  useEffect(() => {
    if (!q.trim()) {
      setItems([]);
      return;
    }
    setLoading(true);
    const t = setTimeout(async () => {
      try {
        const res = await fetchCitySuggestions(q, 8);
        setItems(res);
        setOpen(true);
      } finally {
        setLoading(false);
      }
    }, 250);
    return () => clearTimeout(t);
  }, [q]);

  const placeholder = useMemo(() => initialLabel ?? "Search city (e.g. Boston, MA)", [initialLabel]);

  return (
    <div ref={boxRef} style={{ position: "relative", width: "100%", maxWidth: 520 }}>
      <input
        value={q}
        onChange={(e) => setQ(e.target.value)}
        placeholder={placeholder}
        onFocus={() => q && setOpen(true)}
        className="input"
      />
      {loading && <div className="hint">Loading…</div>}
      {open && items.length > 0 && (
        <div className="dropdown">
          {items.map((c) => (
            <div
              key={c.id}
              className="option"
              onClick={() => {
                onSelect(c);
                setQ(`${c.name}, ${c.stateId}`);
                setOpen(false);
              }}
            >
              <div className="option-title">{c.name}, {c.stateId}</div>
              <div className="option-sub">{c.stateName} {c.population ? `• ${c.population.toLocaleString()} ppl` : ""}</div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
