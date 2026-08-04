import { useEffect, useRef } from 'react';
import { orderStreamUrl } from '../api/orders';
import type { Order } from '../types';

const EVENT_NAME = 'order-created';

/**
 * Subscribes once to order-service's SSE stream and always invokes the latest callback via a
 * ref, so passing a fresh inline function each render doesn't tear down and reconnect the
 * EventSource.
 */
export function useOrdersStream(onNewOrder: (order: Order) => void): void {
  const callbackRef = useRef(onNewOrder);
  callbackRef.current = onNewOrder;

  useEffect(() => {
    const eventSource = new EventSource(orderStreamUrl());

    const handleOrderCreated = (event: MessageEvent<string>) => {
      const order = JSON.parse(event.data) as Order;
      callbackRef.current(order);
    };

    eventSource.addEventListener(EVENT_NAME, handleOrderCreated);

    return () => {
      eventSource.removeEventListener(EVENT_NAME, handleOrderCreated);
      eventSource.close();
    };
  }, []);
}
