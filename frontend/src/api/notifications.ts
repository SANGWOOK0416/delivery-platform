import { getJson } from './client';
import type { NotificationStatus } from '../types';

const NOTIFICATION_SERVICE_BASE_URL = import.meta.env.VITE_NOTIFICATION_SERVICE_URL ?? 'http://localhost:8083';

export function fetchLatestNotifications(): Promise<NotificationStatus[]> {
  return getJson<NotificationStatus[]>(`${NOTIFICATION_SERVICE_BASE_URL}/api/notifications/latest`);
}

export function notificationStreamUrl(): string {
  return `${NOTIFICATION_SERVICE_BASE_URL}/api/notifications/stream`;
}
