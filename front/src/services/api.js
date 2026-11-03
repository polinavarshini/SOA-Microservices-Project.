import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    let message = 'An unexpected error occurred';
    if (error.response) {
      const status = error.response.status;
      if (status === 401) {
        message = 'Session expired or unauthorized. Please log in again.';
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        // window.location.href = '/login'; // Let components handle redirect to avoid reload loops
      } else if (status === 403) {
        message = 'You do not have permission to perform this action.';
      } else if (status === 404) {
        message = 'Requested resource not found.';
      } else if (status === 429) {
        message = 'Too many requests. Please try again later.';
      } else if (status >= 500) {
        message = error.response.data?.error || 'Server error. Downstream service might be unavailable.';
      } else {
        message = error.response.data?.message || error.response.data?.error || `Error: ${status}`;
      }
    } else if (error.request) {
      message = 'Network error. Please check your connection to the API Gateway.';
    }
    return Promise.reject(new Error(message));
  }
);

export default api;
