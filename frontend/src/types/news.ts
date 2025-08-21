export type NewsItemDto = {
  id: number;
  title: string;
  summary: string | null;
  url: string;
  source: string | null;
  isLocal: boolean;
  cityId: number | null;
  confidence: number | null;
  decidedAt: string | null; // ISO-строка
};

export type ClassifyResponse = {
  classified: number;
  totalItems: number;
};