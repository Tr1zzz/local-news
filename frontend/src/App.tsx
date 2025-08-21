import { useEffect, useRef, useState } from "react";
import CitySearch from "./components/CitySearch";
import NewsCard from "./components/NewsCard";
import { fetchNews, classifyBatch } from '././api/news'
import type { CityDto } from '././types/city'
import type { NewsItemDto } from '././types/news'
import "./index.css";

type Scope = "local" | "global" | "all";

export default function App() {
  const [selected, setSelected] = useState<CityDto | null>(null);
  const [news, setNews] = useState<NewsItemDto[]>([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [scope, setScope] = useState<Scope>("local"); // <— filter

  // for "up/down"
  const listEndRef = useRef<HTMLDivElement | null>(null);
  const [showTopBtn, setShowTopBtn] = useState(false);

  async function load(cityId?: number, pageIndex = 0, nextScope: Scope = scope) {
    setLoading(true);
    try {
      const res = await fetchNews({ cityId, page: pageIndex, size: 20, scope: nextScope });
      setNews(res);
      setPage(pageIndex);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load(undefined, 0, scope); // initial load
    const onScroll = () => setShowTopBtn(window.scrollY > 400);
    window.addEventListener("scroll", onScroll, { passive: true });
    return () => window.removeEventListener("scroll", onScroll);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  async function onCityPick(city: CityDto) {
    setSelected(city);
    setScope("local");             // keep local feed for a city
    await load(city.id, 0, "local");
    scrollToTop();
  }

  async function onRefreshClick() {
    try {
      setLoading(true);
      await classifyBatch(50);
      await load(selected?.id ?? undefined, 0, scope);
    } catch (e) {
      alert("Failed to refresh: " + (e as Error).message);
    } finally {
      setLoading(false);
    }
  }

  function scrollToTop() {
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  function scrollToBottom() {
    if (listEndRef.current) {
      listEndRef.current.scrollIntoView({ behavior: "smooth", block: "end" });
    } else {
      window.scrollTo({ top: document.body.scrollHeight, behavior: "smooth" });
    }
  }

  return (
    <div className="container">
      <header className="top" style={{ display: "flex", gap: 12, alignItems: "center", justifyContent: "space-between" }}>
        <h1 style={{ margin: 0 }}>Local News</h1>
        <div style={{ display: "flex", gap: 8 }}>
          <button className="btn" onClick={onRefreshClick} disabled={loading}>
            {loading ? "Refreshing…" : "Refresh data"}
          </button>
        </div>
      </header>

      <div className="controls" style={{ display: "flex", gap: 8, alignItems: "center", flexWrap: "wrap" }}>
        <CitySearch
          onSelect={onCityPick}
          initialLabel={selected ? `${selected.name}, ${selected.stateId}` : undefined}
        />
        {selected && (
          <button
            className="btn-secondary"
            onClick={() => { setSelected(null); load(undefined, 0, scope); scrollToTop(); }}
            disabled={loading}
          >
            Clear city
          </button>
        )}

        {/* Feed switcher */}
        {!selected && (
          <div style={{ display: "flex", gap: 6 }}>
            {(["local","global","all"] as Scope[]).map(s => (
              <button
                key={s}
                className={`btn ${scope === s ? "active" : ""}`}
                onClick={() => { setScope(s); load(undefined, 0, s); }}
                disabled={loading}
              >
                {s === "local" ? "Local" : s === "global" ? "Global" : "All"}
              </button>
            ))}
          </div>
        )}
      </div>

      <div className="hint" style={{ marginBottom: 8 }}>
        {selected
          ? `Showing news for ${selected.name}, ${selected.stateId}`
          : scope === "global"
              ? "Showing global feed"
              : scope === "all"
                  ? "Showing all news"
                  : "Showing local feed"}
      </div>

      {loading ? (
        <div className="loader">Loading…</div>
      ) : (
        <>
          <div className="grid">
            {news.length === 0 ? (
              <div className="empty">No news found.</div>
            ) : (
              news.map(n => <NewsCard key={n.id} item={n} />)
            )}
          </div>
          <div ref={listEndRef} />
        </>
      )}

      <footer className="pager" style={{ display: "flex", gap: 12, alignItems: "center", justifyContent: "center", margin: "16px 0" }}>
        <button
          className="btn"
          disabled={page <= 0 || loading}
          onClick={() => { load(selected?.id ?? undefined, Math.max(0, page - 1), scope); scrollToTop(); }}
        >
          ← Prev
        </button>
        <span>Page {page + 1}</span>
        <button
          className="btn"
          disabled={loading || news.length < 20}
          onClick={() => { load(selected?.id ?? undefined, page + 1, scope); scrollToTop(); }}
        >
          Next →
        </button>
      </footer>

      {/* Floating quick scroll buttons */}
      <div style={{ position: "fixed", right: 16, bottom: 16, display: "flex", flexDirection: "column", gap: 8, zIndex: 50 }}>
        {showTopBtn && (
          <button className="btn" onClick={scrollToTop} style={{ padding: "10px 14px", borderRadius: 999 }} aria-label="Scroll to top">
            ▲ Top
          </button>
        )}
        <button className="btn" onClick={scrollToBottom} style={{ padding: "10px 14px", borderRadius: 999 }} aria-label="Scroll to bottom">
          ▼ Bottom
        </button>
      </div>
    </div>
  );
}
