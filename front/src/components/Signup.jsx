import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../services/api';

function Signup() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();

  const handleSignup = async (e) => {
    e.preventDefault();
    setError('');

    // Frontend validation
    const trimmedUsername = username.trim();
    if (!trimmedUsername) {
      setError('Username is required.');
      return;
    }
    if (trimmedUsername.length < 4) {
      setError('Username must be at least 4 characters long.');
      return;
    }
    if (!password) {
      setError('Password is required.');
      return;
    }
    if (password.length < 6) {
      setError('Password must be at least 6 characters long.');
      return;
    }
    if (!confirmPassword) {
      setError('Please confirm your password.');
      return;
    }
    if (password !== confirmPassword) {
      setError('Passwords do not match.');
      return;
    }

    setIsSubmitting(true);
    try {
      await api.post('/auth/register', {
        username: trimmedUsername,
        password
      });
      setSuccessMessage('Account created successfully. Please log in.');
    } catch (err) {
      setError(err.message || 'Registration failed. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="container" style={{ maxWidth: '400px', marginTop: '80px' }}>
      <div className="card">
        <h2>Vanguard Commercial Insurance</h2>
        <h3 style={{ marginTop: '5px', marginBottom: '15px', color: '#555' }}>Create Account</h3>

        {/* Role indicator notice */}
        <div style={{
          backgroundColor: '#e7f3fe',
          borderLeft: '4px solid #0056b3',
          padding: '10px',
          marginBottom: '15px',
          fontSize: '0.9rem',
          color: '#004085'
        }}>
          All new accounts are registered as <strong>Policyholder</strong> accounts.
        </div>

        {error && <div className="error-message">{error}</div>}

        {successMessage ? (
          <div style={{ textAlign: 'center' }}>
            <div style={{
              color: '#155724',
              backgroundColor: '#d4edda',
              border: '1px solid #c3e6cb',
              padding: '12px',
              borderRadius: '4px',
              marginBottom: '20px'
            }}>
              {successMessage}
            </div>
            <button 
              type="button" 
              onClick={() => navigate('/login')} 
              style={{ width: '100%' }}
            >
              Go to Login
            </button>
          </div>
        ) : (
          <form onSubmit={handleSignup}>
            <div>
              <label>Username</label>
              <input
                type="text"
                value={username}
                onChange={e => setUsername(e.target.value)}
                placeholder="Choose a username (min 4 characters)"
                disabled={isSubmitting}
                required
              />
            </div>
            <div>
              <label>Password</label>
              <input
                type="password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                placeholder="Enter password (min 6 characters)"
                disabled={isSubmitting}
                required
              />
            </div>
            <div>
              <label>Confirm Password</label>
              <input
                type="password"
                value={confirmPassword}
                onChange={e => setConfirmPassword(e.target.value)}
                placeholder="Re-enter your password"
                disabled={isSubmitting}
                required
              />
            </div>
            <button 
              type="submit" 
              style={{ width: '100%', marginTop: '15px' }}
              disabled={isSubmitting}
            >
              {isSubmitting ? 'Creating Account...' : 'Sign Up'}
            </button>
            <div style={{ marginTop: '15px', textAlign: 'center' }}>
              <span>Already have an account? </span>
              <Link to="/login" style={{ color: '#0056b3', textDecoration: 'none', fontWeight: 'bold' }}>
                Log in
              </Link>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}

export default Signup;
