// Automated verification script for Signup & Auth flow through API Gateway (port 8080)
const GATEWAY_URL = 'http://localhost:8080/api';

async function request(path, options = {}) {
  const res = await fetch(`${GATEWAY_URL}${path}`, {
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    ...options
  });
  const text = await res.text();
  let json;
  try {
    json = JSON.parse(text);
  } catch (e) {
    json = text;
  }
  return { status: res.status, data: json };
}

async function runTests() {
  console.log('=== Starting End-to-End Verification ===\n');

  // 1. Existing demo accounts
  console.log('1. Testing existing demo accounts:');
  const demoPh = await request('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username: 'demo_policyholder', password: 'policyholder123' })
  });
  console.log('   demo_policyholder login:', demoPh.status === 200 ? 'PASS (200)' : `FAIL (${demoPh.status})`, 'Role:', demoPh.data.role);

  const demoAdj = await request('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username: 'demo_adjuster', password: 'adjuster123' })
  });
  console.log('   demo_adjuster login:', demoAdj.status === 200 ? 'PASS (200)' : `FAIL (${demoAdj.status})`, 'Role:', demoAdj.data.role);

  // 2. Validation failures (400)
  console.log('\n2. Testing validation rejections:');
  const emptyUser = await request('/auth/register', {
    method: 'POST',
    body: JSON.stringify({ username: '', password: 'password123' })
  });
  console.log('   Empty username rejected:', emptyUser.status === 400 ? 'PASS (400)' : `FAIL (${emptyUser.status})`, emptyUser.data);

  const shortPass = await request('/auth/register', {
    method: 'POST',
    body: JSON.stringify({ username: 'validuser', password: '123' })
  });
  console.log('   Short password rejected (<6 chars):', shortPass.status === 400 ? 'PASS (400)' : `FAIL (${shortPass.status})`, shortPass.data);

  // 3. New public signup (without email, username + password only)
  const testUsername = `user_${Date.now()}`;
  console.log(`\n3. Testing new signup (${testUsername}):`);
  const regRes = await request('/auth/register', {
    method: 'POST',
    body: JSON.stringify({ username: testUsername, password: 'password123' })
  });
  console.log('   Registration status:', regRes.status === 201 ? 'PASS (201 Created)' : `FAIL (${regRes.status})`, regRes.data);

  // 4. Duplicate username rejection (409 Conflict)
  console.log('\n4. Testing duplicate username rejection:');
  const dupRes = await request('/auth/register', {
    method: 'POST',
    body: JSON.stringify({ username: testUsername, password: 'password123' })
  });
  console.log('   Duplicate username status:', dupRes.status === 409 ? 'PASS (409 Conflict)' : `FAIL (${dupRes.status})`, dupRes.data);

  // 5. Attempted privilege escalation (passing role: ADJUSTER)
  const adjusterAttemptUser = `adj_attempt_${Date.now()}`;
  console.log(`\n5. Testing public signup attempting role=ADJUSTER (${adjusterAttemptUser}):`);
  const regAdjAttempt = await request('/auth/register', {
    method: 'POST',
    body: JSON.stringify({ username: adjusterAttemptUser, password: 'password123', role: 'ADJUSTER' })
  });
  console.log('   Registration response:', regAdjAttempt.status === 201 ? 'PASS (201 Created)' : `FAIL (${regAdjAttempt.status})`);

  const loginAdjAttempt = await request('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username: adjusterAttemptUser, password: 'password123' })
  });
  console.log('   Login role assigned:', loginAdjAttempt.data.role === 'POLICYHOLDER' ? 'PASS (Strictly POLICYHOLDER)' : `FAIL (${loginAdjAttempt.data.role})`);

  // 6. Login with newly registered user
  console.log(`\n6. Testing login with newly registered user (${testUsername}):`);
  const newLogin = await request('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username: testUsername, password: 'password123' })
  });
  console.log('   Login status:', newLogin.status === 200 ? 'PASS (200 OK)' : `FAIL (${newLogin.status})`);
  console.log('   JWT received:', !!newLogin.data.accessToken ? 'PASS (JWT present)' : 'FAIL (No JWT)');
  console.log('   Role assigned:', newLogin.data.role === 'POLICYHOLDER' ? 'PASS (POLICYHOLDER)' : `FAIL (${newLogin.data.role})`);

  // 7. Microservice policy/claim access for new user
  console.log('\n7. Testing microservice policy & claim access for new user:');
  const token = newLogin.data.accessToken;
  const policiesRes = await request('/policies', { headers: { Authorization: `Bearer ${token}` } });
  console.log('   GET /api/policies status:', policiesRes.status === 200 ? 'PASS (200 OK)' : `FAIL (${policiesRes.status})`, 'Data:', policiesRes.data);

  const claimsRes = await request('/claims', { headers: { Authorization: `Bearer ${token}` } });
  console.log('   GET /api/claims status:', claimsRes.status === 200 ? 'PASS (200 OK)' : `FAIL (${claimsRes.status})`, 'Data:', claimsRes.data);

  // 8. Settlement and Policy service integrity with demo_adjuster
  console.log('\n8. Testing adjuster services access (integrity check):');
  const adjToken = demoAdj.data.accessToken;
  const allPolicies = await request('/policies', { headers: { Authorization: `Bearer ${adjToken}` } });
  console.log('   Adjuster GET /api/policies:', allPolicies.status === 200 ? 'PASS (200 OK)' : `FAIL (${allPolicies.status})`, `Found ${Array.isArray(allPolicies.data) ? allPolicies.data.length : 0} policies`);

  const allClaims = await request('/claims', { headers: { Authorization: `Bearer ${adjToken}` } });
  console.log('   Adjuster GET /api/claims:', allClaims.status === 200 ? 'PASS (200 OK)' : `FAIL (${allClaims.status})`, `Found ${Array.isArray(allClaims.data) ? allClaims.data.length : 0} claims`);

  const allSettlements = await request('/settlements', { headers: { Authorization: `Bearer ${adjToken}` } });
  console.log('   Adjuster GET /api/settlements:', allSettlements.status === 200 ? 'PASS (200 OK)' : `FAIL (${allSettlements.status})`);

  console.log('\n=== All Tests Finished ===');
}

runTests().catch(console.error);
