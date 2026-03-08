import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios'

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('farmacia_token')
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

api.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('farmacia_token')
      localStorage.removeItem('farmacia_user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// Tipos
export interface Persona {
  id: number
  nombre: string
  apellido: string
  tipoDocumento: string
  numeroDocumento: string
  fechaNacimiento: string
  sexo: string
  telefono?: string
  email?: string
  direccion?: string
}

export interface Usuario {
  id: number
  username: string
  rol: string
  persona: Persona
}

export interface Medicamento {
  id: number
  nombre: string
  principioActivo: string
  formaFarmaceutica: string
  concentracion: string
  unidadPresentacion: string
  requiereReceta: boolean
  stockActual: number
  precio: number
}

export interface Lote {
  id: number
  numeroLote: string
  fechaVencimiento: string
  cantidadInicial: number
  cantidadActual: number
  medicamento: Medicamento
}

export interface HistoriaClinica {
  id: number
  paciente: Persona
  fechaApertura: string
  ultimaActualizacion: string
  antecedentesPersonales: string
  antecedentesFamiliares: string
  alergias: string
  medicamentosActuales: string
}

export interface Receta {
  id: number
  paciente: Persona
  medico: string
  fechaEmision: string
  fechaVencimiento: string
  estado: string
  detalles: RecetaDetalle[]
}

export interface RecetaDetalle {
  id: number
  medicamento: Medicamento
  cantidad: number
  dosis: string
  indicaciones: string
  dispensado: boolean
}

export interface Dispensacion {
  id: number
  receta: Receta
  paciente: Persona
  fechaDispensacion: string
  items: ItemDispensacion[]
  total: number
  estado: string
}

export interface ItemDispensacion {
  medicamento: Medicamento
  cantidad: number
  precioUnitario: number
  subtotal: number
}

// Auth API
export const authApi = {
  login: async (email: string, password: string) => {
    const response = await api.post('/auth/login', { email, password })
    return response.data
  },
  registro: async (userData: { email: string; password: string; rol: string; persona: Partial<Persona> }) => {
    const response = await api.post('/auth/registro', userData)
    return response.data
  },
}

// Pacientes API
export const pacientesApi = {
  getAll: async () => {
    const response = await api.get('/pacientes')
    return response.data
  },
  getById: async (id: number) => {
    const response = await api.get(`/pacientes/${id}`)
    return response.data
  },
  create: async (persona: Partial<Persona>) => {
    const response = await api.post('/pacientes', persona)
    return response.data
  },
  update: async (id: number, persona: Partial<Persona>) => {
    const response = await api.put(`/pacientes/${id}`, persona)
    return response.data
  },
  delete: async (id: number) => {
    const response = await api.delete(`/pacientes/${id}`)
    return response.data
  },
  search: async (query: string) => {
    const response = await api.get(`/pacientes/buscar?q=${query}`)
    return response.data
  },
}

// Medicamentos API
export const medicamentosApi = {
  getAll: async () => {
    const response = await api.get('/medicamentos')
    return response.data
  },
  getById: async (id: number) => {
    const response = await api.get(`/medicamentos/${id}`)
    return response.data
  },
  create: async (medicamento: Partial<Medicamento>) => {
    const response = await api.post('/medicamentos', medicamento)
    return response.data
  },
  update: async (id: number, medicamento: Partial<Medicamento>) => {
    const response = await api.put(`/medicamentos/${id}`, medicamento)
    return response.data
  },
  delete: async (id: number) => {
    const response = await api.delete(`/medicamentos/${id}`)
    return response.data
  },
  search: async (query: string) => {
    const response = await api.get(`/medicamentos/buscar?q=${query}`)
    return response.data
  },
}

// Inventario API
export const inventarioApi = {
  getLotes: async () => {
    const response = await api.get('/inventario/lotes')
    return response.data
  },
  getLoteById: async (id: number) => {
    const response = await api.get(`/inventario/lotes/${id}`)
    return response.data
  },
  ingresoLote: async (lote: { medicamentoId: number; cantidad: number; numeroLote: string; fechaVencimiento: string }) => {
    const response = await api.post('/inventario/lotes/ingreso', lote)
    return response.data
  },
  ajusteStock: async (ajuste: { loteId: number; cantidad: number; motivo: string }) => {
    const response = await api.post('/inventario/ajuste', ajuste)
    return response.data
  },
  getMovimientos: async () => {
    const response = await api.get('/inventario/movimientos')
    return response.data
  },
}

// Historia Clínica API
export const historiaClinicaApi = {
  getAll: async () => {
    const response = await api.get('/historia-clinica')
    return response.data
  },
  getByPaciente: async (pacienteId: number) => {
    const response = await api.get(`/historia-clinica/paciente/${pacienteId}`)
    return response.data
  },
  create: async (historia: Partial<HistoriaClinica>) => {
    const response = await api.post('/historia-clinica', historia)
    return response.data
  },
  update: async (id: number, historia: Partial<HistoriaClinica>) => {
    const response = await api.put(`/historia-clinica/${id}`, historia)
    return response.data
  },
}

// Recetas API
export const recetasApi = {
  getAll: async () => {
    const response = await api.get('/recetas')
    return response.data
  },
  getById: async (id: number) => {
    const response = await api.get(`/recetas/${id}`)
    return response.data
  },
  getByPaciente: async (pacienteId: number) => {
    const response = await api.get(`/recetas/paciente/${pacienteId}`)
    return response.data
  },
  create: async (receta: Partial<Receta>) => {
    const response = await api.post('/recetas', receta)
    return response.data
  },
  validarReceta: async (id: number) => {
    const response = await api.put(`/recetas/${id}/validar`)
    return response.data
  },
}

// Dispensación API
export const dispensacionApi = {
  getAll: async () => {
    const response = await api.get('/dispensaciones')
    return response.data
  },
  getById: async (id: number) => {
    const response = await api.get(`/dispensaciones/${id}`)
    return response.data
  },
  crear: async (dispensacion: { recetaId: number; items: { medicamentoId: number; cantidad: number }[] }) => {
    const response = await api.post('/dispensaciones', dispensacion)
    return response.data
  },
  confirmar: async (id: number) => {
    const response = await api.put(`/dispensaciones/${id}/confirmar`)
    return response.data
  },
  cancelar: async (id: number) => {
    const response = await api.put(`/dispensaciones/${id}/cancelar`)
    return response.data
  },
}

export default api
