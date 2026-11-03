import { useEffect, useState } from 'react';
import { Routes, Route, Navigate, useNavigate } from 'react-router-dom';
import Login from './components/Login';
import Signup from './components/Signup';
import PolicyholderDashboard from './components/PolicyholderDashboard';
import AdjusterDashboard from './components/AdjusterDashboard';

function App() {
  const [role, setRole] = useState(localStorage.getItem('role'));
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    setRole(null);
    navigate('/login');
  };

  return (
    <>
      <header>
        <h1>Vanguard Commercial Insurance</h1>
        {role && (
          <div>
            <span style={{ marginRight: '15px' }}>Logged in as: <strong>{role}</strong></span>
            <button className="danger" onClick={handleLogout}>Logout</button>
          </div>
        )}
      </header>
      
      <div className="container">
        <Routes>
          <Route path="/login" element={role ? <Navigate to="/" /> : <Login setRole={setRole} />} />
          <Route path="/signup" element={role ? <Navigate to="/" /> : <Signup />} />
          
          <Route path="/" element={
            !role ? <Navigate to="/login" /> : (
              role === 'POLICYHOLDER' ? <PolicyholderDashboard /> : <AdjusterDashboard />
            )
          } />
          
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </div>
    </>
  );
}

export default App;
