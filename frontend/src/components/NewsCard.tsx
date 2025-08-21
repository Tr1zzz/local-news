import type { NewsItemDto } from '../types/news'

export default function NewsCard({ item }: { item: NewsItemDto }) {
  const host = useHost(item.url);

  return (
    <a className="card" href={item.url} target="_blank" rel="noreferrer">
      <div className="card-header">
        <div className="source">{host || item.source}</div>
        <div className={`pill ${item.isLocal ? "pill-local" : "pill-global"}`}>
          {item.isLocal ? "Local" : "Global"}
        </div>
      </div>
      <div className="title">{item.title}</div>
      {item.summary && <div className="summary">{item.summary}</div>}
      <div className="card-footer">
        {item.confidence != null && <span className="conf">Confidence: {item.confidence}%</span>}
        {item.decidedAt && <span className="time"> • {new Date(item.decidedAt).toLocaleString()}</span>}
      </div>
    </a>
  );
}

function useHost(url?: string) {
  try {
    if (!url) return "";
    return new URL(url).host.replace(/^www\./, "");
  } catch {
    return "";
  }
}
