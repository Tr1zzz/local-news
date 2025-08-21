export const BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080';

type Json =
  | null | boolean | number | string
  | Json[] | { [k: string]: Json };

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(BASE + path, init);
  if (!res.ok) {
    const text = await res.text().catch(() => '');
    throw new Error(`HTTP ${res.status}${text ? `: ${text}` : ''}`);
  }
  if (res.status === 204) return undefined as T;

    const text = await res.text();
  return (text ? JSON.parse(text) : undefined) as T;
}

export const http = {
  get<T>(path: string, init?: RequestInit) {
    return request<T>(path, init);
  },

  post<T>(path: string, body?: Json, init?: Omit<RequestInit, 'body' | 'method'>) {
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
      ...(init?.headers ?? {}),
    };

    return request<T>(path, {
      ...init,
      method: 'POST',
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    });
  },
};