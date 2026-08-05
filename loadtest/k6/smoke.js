import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8081';

export const options = {
    scenarios: {
        smoke: {
            executor: 'constant-arrival-rate',
            rate: 5,
            timeUnit: '1s',
            duration: '30s',
            preAllocatedVUs: 10,
            maxVUs: 20,
        },
    },
    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(95)<500'],
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
