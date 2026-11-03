import { useEffect, useState } from 'react';
import api from '../services/api';

function AdjusterDashboard() {
  const [claims, setClaims] = useState([]);
  const [policies, setPolicies] = useState([]);
  const [error, setError] = useState('');
  
  // Action states
  const [selectedClaim, setSelectedClaim] = useState(null);
  const [actionType, setActionType] = useState(null); // 'approve' | 'reject' | 'view_settlement' | 'create_policy'
  const [approvedAmount, setApprovedAmount] = useState('');
  const [rejectReason, setRejectReason] = useState('');
  const [settlementData, setSettlementData] = useState(null);

  // Policy form state
  const [holderUserId, setHolderUserId] = useState(localStorage.getItem('latest_policyholder_uuid') || '');
  const [holderUsername, setHolderUsername] = useState(localStorage.getItem('latest_policyholder_username') || '');
  const [policyType, setPolicyType] = useState('PROPERTY');
  const [coverageLimit, setCoverageLimit] = useState('');
  const [deductible, setDeductible] = useState('');
  const [premium, setPremium] = useState('');
  const [startDate, setStartDate] = useState(new Date().toISOString().split('T')[0]);
  const [endDate, setEndDate] = useState(new Date(new Date().setFullYear(new Date().getFullYear() + 1)).toISOString().split('T')[0]);
  const [status, setStatus] = useState('ACTIVE');
  const [policySuccess, setPolicySuccess] = useState('');

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [claimsRes, policiesRes] = await Promise.all([
        api.get('/claims'),
        api.get('/policies')
      ]);
      setClaims(claimsRes.data);
      setPolicies(policiesRes.data);
    } catch (err) {
      setError(err.message);
    }
  };

  const handleValidate = async (claimNumber) => {
    setError('');
    try {
      await api.post(`/claims/${claimNumber}/validate`);
      fetchData();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleApprove = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await api.put(`/claims/${selectedClaim.claimNumber}/approve`, {
        approvedAmount: parseFloat(approvedAmount)
      });
      setActionType(null);
      fetchData();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleReject = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await api.put(`/claims/${selectedClaim.claimNumber}/reject`, {
        reason: rejectReason
      });
      setActionType(null);
      fetchData();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleViewSettlement = async (claimNumber) => {
    setError('');
    try {
      const res = await api.get(`/settlements/claim/${claimNumber}`);
      setSettlementData(res.data);
      setActionType('view_settlement');
    } catch (err) {
      setError(err.message);
    }
  };

  const handleCreatePolicy = async (e) => {
    e.preventDefault();
    setError('');
    setPolicySuccess('');
    try {
      await api.post('/policies', {
        holderUserId,
        holderUsername,
        policyType,
        coverageLimit: parseFloat(coverageLimit),
        deductible: parseFloat(deductible),
        premium: parseFloat(premium),
        startDate,
        endDate,
        status
      });
      setPolicySuccess('Policy created successfully!');
      fetchData(); // Refresh policies
      setActionType(null);
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div>
      <h2>Adjuster Dashboard</h2>
      {error && <div className="error-message">{error}</div>}
      {policySuccess && <div style={{ color: 'green', marginBottom: '10px' }}>{policySuccess}</div>}

      <div style={{ marginBottom: '20px' }}>
        <button onClick={() => setActionType('create_policy')} className="success">Create New Policy</button>
      </div>

      {actionType === 'create_policy' && (
        <div className="card">
          <h3>Create Policy</h3>
          <form onSubmit={handleCreatePolicy}>
            <div className="grid" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
              <div>
                <label>Holder User ID (UUID)</label>
                <input type="text" value={holderUserId} onChange={e => setHolderUserId(e.target.value)} required placeholder="e.g. 123e4567-e89b-12d3-a456-426614174000" />
              </div>
              <div>
                <label>Holder Username</label>
                <input type="text" value={holderUsername} onChange={e => setHolderUsername(e.target.value)} required />
              </div>
              <div>
                <label>Policy Type</label>
                <select value={policyType} onChange={e => setPolicyType(e.target.value)}>
                  <option value="PROPERTY">Property</option>
                  <option value="LIABILITY">Liability</option>
                  <option value="MARINE">Marine</option>
                  <option value="FLEET">Fleet</option>
                </select>
              </div>
              <div>
                <label>Coverage Limit ($)</label>
                <input type="number" step="0.01" value={coverageLimit} onChange={e => setCoverageLimit(e.target.value)} required />
              </div>
              <div>
                <label>Deductible ($)</label>
                <input type="number" step="0.01" value={deductible} onChange={e => setDeductible(e.target.value)} required />
              </div>
              <div>
                <label>Premium ($)</label>
                <input type="number" step="0.01" value={premium} onChange={e => setPremium(e.target.value)} required />
              </div>
              <div>
                <label>Start Date</label>
                <input type="date" value={startDate} onChange={e => setStartDate(e.target.value)} required />
              </div>
              <div>
                <label>End Date</label>
                <input type="date" value={endDate} onChange={e => setEndDate(e.target.value)} required />
              </div>
              <div>
                <label>Status</label>
                <select value={status} onChange={e => setStatus(e.target.value)}>
                  <option value="ACTIVE">Active</option>
                  <option value="EXPIRED">Expired</option>
                  <option value="SUSPENDED">Suspended</option>
                  <option value="CANCELLED">Cancelled</option>
                </select>
              </div>
            </div>
            <div style={{ marginTop: '15px' }}>
              <button type="submit" className="success">Create Policy</button>
              <button type="button" onClick={() => setActionType(null)} style={{marginLeft: '10px'}}>Cancel</button>
            </div>
          </form>
        </div>
      )}

      {actionType === 'approve' && selectedClaim && (
        <div className="card">
          <h3>Approve Claim: {selectedClaim.claimNumber}</h3>
          <p>Requested Amount: ${selectedClaim.claimedAmount}</p>
          <form onSubmit={handleApprove}>
            <label>Approved Amount ($)</label>
            <input type="number" step="0.01" value={approvedAmount} onChange={e => setApprovedAmount(e.target.value)} required />
            <button type="submit" className="success">Confirm Approval</button>
            <button type="button" onClick={() => setActionType(null)} style={{marginLeft: '10px'}}>Cancel</button>
          </form>
        </div>
      )}

      {actionType === 'reject' && selectedClaim && (
        <div className="card">
          <h3>Reject Claim: {selectedClaim.claimNumber}</h3>
          <form onSubmit={handleReject}>
            <label>Reason for Rejection</label>
            <textarea value={rejectReason} onChange={e => setRejectReason(e.target.value)} required />
            <button type="submit" className="danger">Confirm Rejection</button>
            <button type="button" onClick={() => setActionType(null)} style={{marginLeft: '10px'}}>Cancel</button>
          </form>
        </div>
      )}

      {actionType === 'view_settlement' && settlementData && (
        <div className="card">
          <h3>Settlement Details: {settlementData.settlementNumber}</h3>
          <p><strong>Claim:</strong> {settlementData.claimNumber} | <strong>Policy:</strong> {settlementData.policyNumber}</p>
          <p><strong>Approved Amount:</strong> ${settlementData.approvedAmount}</p>
          <p><strong>Deductible Applied:</strong> ${settlementData.deductible}</p>
          <p><strong>Final Payout:</strong> ${settlementData.payoutAmount}</p>
          <p><strong>Status:</strong> <span className={`status-badge status-${settlementData.status}`}>{settlementData.status}</span></p>
          <button type="button" onClick={() => setActionType(null)}>Close</button>
        </div>
      )}

      <div className="card">
        <h3>All Policies</h3>
        {policies.length === 0 ? <p>No policies found.</p> : (
          <table>
            <thead>
              <tr>
                <th>Policy #</th>
                <th>Holder</th>
                <th>Type</th>
                <th>Limit</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {policies.map(p => (
                <tr key={p.policyNumber}>
                  <td>{p.policyNumber}</td>
                  <td>{p.holderUsername}</td>
                  <td>{p.policyType}</td>
                  <td>${p.coverageLimit}</td>
                  <td><span className={`status-badge status-${p.status}`}>{p.status}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      <div className="card" style={{ marginTop: '20px' }}>
        <h3>All Claims</h3>
        {claims.length === 0 ? <p>No claims found.</p> : (
          <table>
            <thead>
              <tr>
                <th>Claim #</th>
                <th>Policy #</th>
                <th>User</th>
                <th>Requested</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {claims.map(c => (
                <tr key={c.claimNumber}>
                  <td>{c.claimNumber}</td>
                  <td>{c.policyNumber}</td>
                  <td>{c.claimantUsername}</td>
                  <td>${c.claimedAmount}</td>
                  <td><span className={`status-badge status-${c.status}`}>{c.status}</span></td>
                  <td>
                    {c.status === 'SUBMITTED' && (
                      <button onClick={() => handleValidate(c.claimNumber)}>Validate Coverage</button>
                    )}
                    {c.status === 'VALIDATED' && (
                      <>
                        <button className="success" onClick={() => { setSelectedClaim(c); setApprovedAmount(c.claimedAmount); setActionType('approve'); }} style={{marginRight: '5px'}}>Approve</button>
                        <button className="danger" onClick={() => { setSelectedClaim(c); setActionType('reject'); }}>Reject</button>
                      </>
                    )}
                    {(c.status === 'SETTLED' || c.status === 'SETTLEMENT_FAILED') && (
                      <button onClick={() => handleViewSettlement(c.claimNumber)}>View Settlement</button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}

export default AdjusterDashboard;
