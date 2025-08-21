import { http } from './client';
import type { NewsItemDto, ClassifyResponse } from '../types/news';

export function fetchNews(params: {
  cityId?: number;
  page?: number;
  size?: number;
  scope?: 'local' | 'global' | 'all';
}) {
  const qs = new URLSearchParams();
  if (params.cityId != null) qs.set('cityId', String(params.cityId));
  if (params.scope) qs.set('scope', params.scope);
  qs.set('page', String(params.page ?? 0));
  qs.set('size', String(params.size ?? 20));
  return http.get<NewsItemDto[]>(`/api/news?${qs.toString()}`);
}

export function classifyBatch(limit = 50) {
  return http.post<ClassifyResponse>(`/api/news/classify?limit=${limit}`);
}