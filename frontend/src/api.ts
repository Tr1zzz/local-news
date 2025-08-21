// import type { CityDto, NewsItemDto } from "./types";

// const BASE = import.meta.env.VITE_API_BASE ?? "http://localhost:8080";

// /** Get news (by city or general feed) */
// export async function fetchNews(params: {
//   cityId?: number;
//   page?: number;
//   size?: number;
//   scope?: "local" | "global" | "all";
// }): Promise<NewsItemDto[]> {
//   const qs = new URLSearchParams();
//   if (params.cityId != null) qs.set("cityId", String(params.cityId));
//   if (params.scope) qs.set("scope", params.scope);
//   qs.set("page", String(params.page ?? 0));
//   qs.set("size", String(params.size ?? 20));

//   const res = await fetch(`${BASE}/api/news?${qs.toString()}`);
//   if (!res.ok) throw new Error(`HTTP ${res.status}`);
//   return (await res.json()) as NewsItemDto[];
// }

// /** Reclassify a fresh batch of raw news */
// type ClassifyResponse = { classified: number; totalItems: number };

// export async function classifyBatch(limit = 50): Promise<ClassifyResponse> {
//   const res = await fetch(`${BASE}/api/news/classify?limit=${limit}`, {
//     method: "POST",
//   });
//   if (!res.ok) throw new Error(`HTTP ${res.status}`);
//   return (await res.json()) as ClassifyResponse;
// }

// /** City suggestions for autocomplete */
// export async function fetchCitySuggestions(
//   query: string,
//   limit = 10
// ): Promise<CityDto[]> {
//   const url = `${BASE}/api/cities?` +
//     new URLSearchParams({ query, limit: String(limit) }).toString();

//   const res = await fetch(url);
//   if (!res.ok) throw new Error(`HTTP ${res.status}`);
//   return (await res.json()) as CityDto[];
// }
