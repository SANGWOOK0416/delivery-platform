import { useEffect, useRef } from 'react';
import { notificationStreamUrl } from '../api/notifications';
import type { NotificationStatus } from '../types';

const EVENT_NAME = 'notification-status-changed';

/**
 * Subscribes once to notification-service's SSE stream and always invokes the latest callback
 * via a ref, so passing a fresh inline function each render doesn't tear down and reconnect
 * the EventSource.
 */
export function useNotificationsStream(onStatusChange: (status: NotificationStatus) => void): void {
  const callbackRef = useRef(onStatusChange);
  callbackRef.current = onStatusChange;

  useEffect(() => {
    const eventSource = new EventSource(notificationStreamUrl());

    const handleStatusChanged = (event: MessageEvent<string>) => {
      const status = JSON.parse(event.data) as NotificationStatus;
      callbackRef.current(status);
    };

    eventSource.addEventListener(EVENT_NAME, handleStatusChanged);

    return () => {
      eventSource.removeEventListener(EVENT_NAME, handleStatusChanged);
      eventSource.close();
    };
  }, []);
}
