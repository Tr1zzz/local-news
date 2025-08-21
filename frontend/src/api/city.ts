import { http } from './client';
import type { CityDto } from '../types/city';

export function fetchCitySuggestions(query: string, limit = 10, init?: RequestInit) {
  const qs = new URLSearchParams({ query, limit: String(limit) });
  return http.get<CityDto[]>(`/api/cities?${qs.toString()}`, init);
}