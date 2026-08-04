import { useState, type FormEvent } from 'react';
import { createOrder } from '../api/orders';

export function OrderForm() {
  const [customerId, setCustomerId] = useState('');
  const [address, setAddress] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      await createOrder({ customerId: Number(customerId), address });
      setCustomerId('');
      setAddress('');
      // The new order shows up via the order-service SSE stream — no local state update needed here.
    } catch (submitError) {
      setError(submitError instanceof Error ? submitError.message : String(submitError));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form className="order-form" onSubmit={handleSubmit}>
      <h2>새 주문 접수</h2>
      <label>
        고객 ID
        <input
          type="number"
          value={customerId}
          onChange={(event) => setCustomerId(event.target.value)}
          required
        />
      </label>
      <label>
        배달 주소
        <input
          type="text"
          value={address}
          onChange={(event) => setAddress(event.target.value)}
          required
        />
      </label>
      <button type="submit" disabled={submitting}>
        {submitting ? '접수 중...' : '주문 접수'}
      </button>
      {error && <p className="error">{error}</p>}
    </form>
  );
}
