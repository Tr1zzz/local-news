export type CityDto = {
  id: number;
  name: string;
  stateId: string;
  stateName: string;
  lat: number | null;
  lon: number | null;
  population: number | null;
};