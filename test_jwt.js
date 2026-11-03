const http = require('http');

async function test() {
  const loginRes2 = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username: 'demo_policyholder', password: 'policyholder123' })
  });
  const loginData2 = await loginRes2.json();
  const token2 = loginData2.accessToken;
  const parts = token2.split('.');
  const payload = JSON.parse(Buffer.from(parts[1], 'base64').toString('utf8'));
  console.log('POLICYHOLDER JWT PAYLOAD:', payload);
}
test();
