import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../services/api';
import { jwtDecode } from 'jwt-decode';

function Login({ setRole }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    try {
      const res = await api.post('/auth/login', { username, password });
      const { accessToken } = res.data;
      
      localStorage.setItem('token', accessToken);
      
      const decoded = jwtDecode(accessToken);
      const userRole = decoded.role;
      localStorage.setItem('role', userRole);
      
      if (userRole === 'POLICYHOLDER') {
        localStorage.setItem('latest_policyholder_uuid', decoded.userId);
        localStorage.setItem('latest_policyholder_username', decoded.sub);
      }

      setRole(userRole);

      navigate('/');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="container" style={{ maxWidth: '400px', marginTop: '100px' }}>
      <div className="card">
        <h2>Vanguard Insurance Login</h2>
        {error && <div className="error-message">{error}</div>}
        <form onSubmit={handleLogin}>
          <div>
            <label>Username</label>
            <input 
              type="text" 
              value={username} 
              onChange={e => setUsername(e.target.value)} 
              required 
            />
          </div>
          <div>
            <label>Password</label>
            <input 
              type="password" 
              value={password} 
              onChange={e => setPassword(e.target.value)} 
              required 
            />
          </div>
          <button type="submit" style={{ width: '100%', marginTop: '15px' }}>Login</button>
          <div style={{ marginTop: '15px', textAlign: 'center' }}>
            <span>Don't have an account? </span>
            <Link to="/signup" style={{ color: '#0056b3', textDecoration: 'none', fontWeight: 'bold' }}>
              Sign up
            </Link>
          </div>
        </form>
      </div>
    </div>
  );
}

export default Login;
