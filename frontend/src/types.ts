export interface Order {
  orderId: number;
  customerId: number;
  address: string;
  createdAt: string;
}

export type DeliveryStatus = 'SENT' | 'FAILED';

export interface NotificationStatus {
  orderId: number;
  status: DeliveryStatus;
  precipitationType: number | null;
  failureReason: string | null;
  attemptedAt: string;
}
