import { useEffect, useState } from 'react';
import api from '../services/api';

function PolicyholderDashboard() {
  const [policies, setPolicies] = useState([]);
  const [claims, setClaims] = useState([]);
  const [error, setError] = useState('');
  
  // Claim form state
  const [policyNumber, setPolicyNumber] = useState('');
  const [claimedAmount, setClaimedAmount] = useState('');
  const [incidentDate, setIncidentDate] = useState('');
  const [description, setDescription] = useState('');

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [policiesRes, claimsRes] = await Promise.all([
        api.get('/policies'),
        api.get('/claims')
      ]);
      setPolicies(policiesRes.data);
      setClaims(claimsRes.data);
      if (policiesRes.data.length > 0) {
        setPolicyNumber(policiesRes.data[0].policyNumber);
      }
    } catch (err) {
      setError(err.message);
    }
  };

  const handleSubmitClaim = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await api.post('/claims', {
        policyNumber,
        claimedAmount: parseFloat(claimedAmount),
        incidentDate,
        description
      });
      setClaimedAmount('');
      setIncidentDate('');
      setDescription('');
      fetchData(); // Refresh claims
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div>
      <h2>Policyholder Dashboard</h2>
      {error && <div className="error-message">{error}</div>}
      
      <div className="grid">
        <div className="card">
          <h3>Submit a New Claim</h3>
          <form onSubmit={handleSubmitClaim}>
            <label>Select Policy</label>
            <select value={policyNumber} onChange={e => setPolicyNumber(e.target.value)} required>
              <option value="">Select a policy</option>
              {policies.map(p => (
                <option key={p.policyNumber} value={p.policyNumber}>
                  {p.policyNumber} (Limit: ${p.coverageLimit})
                </option>
              ))}
            </select>
            
            <label>Claimed Amount ($)</label>
            <input type="number" step="0.01" value={claimedAmount} onChange={e => setClaimedAmount(e.target.value)} required />
            
            <label>Incident Date</label>
            <input type="date" value={incidentDate} onChange={e => setIncidentDate(e.target.value)} required />
            
            <label>Description</label>
            <textarea value={description} onChange={e => setDescription(e.target.value)} required />
            
            <button type="submit">Submit Claim</button>
          </form>
        </div>

        <div className="card">
          <h3>My Active Policies</h3>
          {policies.length === 0 ? <p>No active policies found.</p> : (
            <ul>
              {policies.map(p => (
                <li key={p.policyNumber} style={{marginBottom: '10px'}}>
                  <strong>{p.policyNumber}</strong> - {p.policyType}<br/>
                  Limit: ${p.coverageLimit} | Deductible: ${p.deductible}
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>

      <div className="card">
        <h3>My Claims History</h3>
        {claims.length === 0 ? <p>No claims submitted.</p> : (
          <table>
            <thead>
              <tr>
                <th>Claim #</th>
                <th>Policy #</th>
                <th>Amount</th>
                <th>Date</th>
                <th>Status</th>
                <th>Details</th>
              </tr>
            </thead>
            <tbody>
              {claims.map(c => (
                <tr key={c.claimNumber}>
                  <td>{c.claimNumber}</td>
                  <td>{c.policyNumber}</td>
                  <td>${c.claimedAmount}</td>
                  <td>{c.incidentDate}</td>
                  <td><span className={`status-badge status-${c.status}`}>{c.status}</span></td>
                  <td>{c.rejectionReason || (c.approvedAmount ? `Approved: $${c.approvedAmount}` : '-')}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}

export default PolicyholderDashboard;
