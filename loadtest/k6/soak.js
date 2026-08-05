import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8081';
const RATE = parseInt(__ENV.RATE || '5', 10);
const DURATION = __ENV.DURATION || '10m';

export const options = {
    scenarios: {
        soak: {
            executor: 'constant-arrival-rate',
            rate: RATE,
            timeUnit: '1s',
            duration: DURATION,
            preAllocatedVUs: Math.max(20, RATE * 2),
            maxVUs: Math.max(50, RATE * 4),
        },
    },
    thresholds: {
        http_req_failed: ['rate<0.01'],
    },
};

const DISTRICTS = ['강남구', '서초구', '마포구', '종로구', '송파구', '영등포구'];

export default function () {
    const district = DISTRICTS[Math.floor(Math.random() * DISTRICTS.length)];
    const payload = JSON.stringify({
        customerId: Math.floor(Math.random() * 100000) + 1,
        address: `서울시 ${district} 부하테스트로 ${Math.floor(Math.random() * 999) + 1}`,
    });

    const res = http.post(`${BASE_URL}/api/orders`, payload, {
        headers: { 'Content-Type': 'application/json' },
    });

    check(res, {
        'status is 202': (r) => r.status === 202,
    });
}
