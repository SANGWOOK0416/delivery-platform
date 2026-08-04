export interface ApiResponse<T> {
  status: number;
  message: string;
  data: T;
}

export async function getJson<T>(url: string): Promise<T> {
  const response = await fetch(url);
  if (!response.ok) {
    throw new Error(`Request to ${url} failed with status ${response.status}`);
  }
  const body: ApiResponse<T> = await response.json();
  return body.data;
}

export async function postJson<TRequest, TResponse>(url: string, request: TRequest): Promise<TResponse> {
  const response = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  });
  if (!response.ok) {
    throw new Error(`Request to ${url} failed with status ${response.status}`);
  }
  const body: ApiResponse<TResponse> = await response.json();
  return body.data;
}
