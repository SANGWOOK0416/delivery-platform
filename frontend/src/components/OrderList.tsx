import type { NotificationStatus, Order } from '../types';

interface OrderListProps {
  orders: Order[];
  statuses: Record<number, NotificationStatus>;
}

const PRECIPITATION_LABEL: Record<number, string> = {
  0: '맑음',
  1: '비',
  2: '비/눈',
  3: '눈',
  4: '소나기',
};

function statusLabel(status: NotificationStatus | undefined): string {
  if (!status) return '처리 중';
  return status.status === 'SENT' ? '발송 완료' : '발송 실패';
}

function riskLabel(status: NotificationStatus | undefined): string {
  if (!status || status.precipitationType == null) return '-';
  return PRECIPITATION_LABEL[status.precipitationType] ?? '알 수 없음';
}

export function OrderList({ orders, statuses }: OrderListProps) {
  return (
    <table className="order-list">
      <thead>
        <tr>
          <th>주문 ID</th>
          <th>고객 ID</th>
          <th>주소</th>
          <th>접수 시각</th>
          <th>날씨</th>
          <th>알림 상태</th>
        </tr>
      </thead>
      <tbody>
        {orders.length === 0 && (
          <tr>
            <td colSpan={6}>아직 접수된 주문이 없습니다.</td>
          </tr>
        )}
        {orders.map((order) => {
          const status = statuses[order.orderId];
          return (
            <tr key={order.orderId} className={status?.status === 'FAILED' ? 'row-failed' : undefined}>
              <td>{order.orderId}</td>
              <td>{order.customerId}</td>
              <td>{order.address}</td>
              <td>{new Date(order.createdAt).toLocaleString()}</td>
              <td>{riskLabel(status)}</td>
              <td title={status?.failureReason ?? undefined}>{statusLabel(status)}</td>
            </tr>
          );
        })}
      </tbody>
    </table>
  );
}
