import { getJson, postJson } from './client';
import type { Order } from '../types';

const ORDER_SERVICE_BASE_URL = import.meta.env.VITE_ORDER_SERVICE_URL ?? 'http://localhost:8081';

export interface CreateOrderRequest {
  customerId: number;
  address: string;
}

export interface OrderAcceptedResponse {
  orderId: number;
}

export function fetchOrders(): Promise<Order[]> {
  return getJson<Order[]>(`${ORDER_SERVICE_BASE_URL}/api/orders`);
}

export function createOrder(request: CreateOrderRequest): Promise<OrderAcceptedResponse> {
  return postJson<CreateOrderRequest, OrderAcceptedResponse>(`${ORDER_SERVICE_BASE_URL}/api/orders`, request);
}

export function orderStreamUrl(): string {
  return `${ORDER_SERVICE_BASE_URL}/api/orders/stream`;
}
