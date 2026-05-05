import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login';
import Dashboard from './components/Dashboard';

// Ochranná komponenta
const ProtectedRoute = ({ children }) => {
    const token = localStorage.getItem('authToken');
    if (!token) return <Navigate to="/login" replace />;
    return children;
};

export default function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route
                    path="/dashboard"
                    element={
                        <ProtectedRoute>
                            <Dashboard />
                        </ProtectedRoute>
                    }
                />
                <Route path="*" element={<Navigate to="/dashboard" replace />} />
            </Routes>
        </BrowserRouter>
    );
}