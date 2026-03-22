import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from '../contextos/AuthContext'
import MainLayout from '../componentes/layout/MainLayout'
import LoginPage from '../modulos/autenticacion/pages/LoginPage'
import DashboardPage from '../modulos/dashboard/pages/DashboardPage'
import PacientesPage from '../modulos/pacientes/pages/PacientesPage'
import MedicamentosPage from '../modulos/catalogo/pages/MedicamentosPage'
import InventarioPage from '../modulos/inventario/pages/InventarioPage'
import HistoriaClinicaPage from '../modulos/historia_clinica/pages/HistoriaClinicaPage'
import RecetasPage from '../modulos/dispensacion/pages/RecetasPage'
import DispensacionPage from '../modulos/dispensacion/pages/DispensacionPage'
import AdministracionPage from '../modulos/administracion/pages/AdministracionPage'

interface ProtectedRouteProps {
  children: React.ReactNode
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children }) => {
  const { isAuthenticated } = useAuth()

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  return <MainLayout>{children}</MainLayout>
}

const AppRoutes: React.FC = () => {
  const { isAuthenticated } = useAuth()

  return (
    <Routes>
      <Route
        path="/login"
        element={isAuthenticated ? <Navigate to="/dashboard" replace /> : <LoginPage />}
      />

      <Route
        path="/dashboard"
        element={
          <ProtectedRoute>
            <DashboardPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/pacientes"
        element={
          <ProtectedRoute>
            <PacientesPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/medicamentos"
        element={
          <ProtectedRoute>
            <MedicamentosPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/inventario"
        element={
          <ProtectedRoute>
            <InventarioPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/historia-clinica"
        element={
          <ProtectedRoute>
            <HistoriaClinicaPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/recetas"
        element={
          <ProtectedRoute>
            <RecetasPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/dispensacion"
        element={
          <ProtectedRoute>
            <DispensacionPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/administracion"
        element={
          <ProtectedRoute>
            <AdministracionPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/configuracion"
        element={
          <ProtectedRoute>
            <DashboardPage />
          </ProtectedRoute>
        }
      />

      <Route path="/" element={<Navigate to="/dashboard" replace />} />
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  )
}

export default AppRoutes
