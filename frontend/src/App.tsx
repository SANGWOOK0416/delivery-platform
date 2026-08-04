import { useCallback, useEffect, useState } from 'react';
import { fetchOrders } from './api/orders';
import { fetchLatestNotifications } from './api/notifications';
import { useOrdersStream } from './hooks/useOrdersStream';
import { useNotificationsStream } from './hooks/useNotificationsStream';
import { OrderForm } from './components/OrderForm';
import { OrderList } from './components/OrderList';
import type { NotificationStatus, Order } from './types';
import './App.css';

function App() {
  const [orders, setOrders] = useState<Order[]>([]);
  const [statuses, setStatuses] = useState<Record<number, NotificationStatus>>({});
  const [loadError, setLoadError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;

    Promise.all([fetchOrders(), fetchLatestNotifications()])
      .then(([orderList, statusList]) => {
        if (cancelled) return;
        setOrders(orderList);
        setStatuses(Object.fromEntries(statusList.map((status) => [status.orderId, status])));
      })
      .catch((error: unknown) => {
        if (!cancelled) setLoadError(error instanceof Error ? error.message : String(error));
      });

    return () => {
      cancelled = true;
    };
  }, []);

  const handleNewOrder = useCallback((order: Order) => {
    setOrders((current) =>
      current.some((existing) => existing.orderId === order.orderId) ? current : [order, ...current]
    );
  }, []);

  const handleStatusChange = useCallback((status: NotificationStatus) => {
    setStatuses((current) => ({ ...current, [status.orderId]: status }));
  }, []);

  useOrdersStream(handleNewOrder);
  useNotificationsStream(handleStatusChange);

  return (
    <div className="app">
      <h1>배달 플랫폼 대시보드</h1>
      {loadError && <p className="error">초기 데이터를 불러오지 못했습니다: {loadError}</p>}
      <OrderForm />
      <OrderList orders={orders} statuses={statuses} />
    </div>
  );
}

export default App;
