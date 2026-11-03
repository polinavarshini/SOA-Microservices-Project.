const http = require('http');

async function test() {
  const loginRes = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username: 'demo_adjuster', password: 'adjuster123' })
  });
  const loginData = await loginRes.json();
  const token = loginData.accessToken;
  
  const polRes = await fetch('http://localhost:8080/api/policies', {
    headers: { 'Authorization': 'Bearer ' + token }
  });
  const polData = await polRes.text();
  console.log('ADJUSTER POLICIES:', polData);
}
test();
