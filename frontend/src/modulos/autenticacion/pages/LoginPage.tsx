import React, { useState } from 'react'
import { Form, Input, Button, Card, message, Typography } from 'antd'
import { UserOutlined, LockOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../../contextos/AuthContext'
import type { AuthUser } from '../../../contextos/AuthContext'
import { authApi } from '../../../servicios/api'

const { Title, Text } = Typography

interface LoginFormValues {
  username: string
  password: string
}

const LoginPage: React.FC = () => {
  const [loading, setLoading] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()

  const onFinish = async (values: LoginFormValues) => {
    setLoading(true)
    try {
      const data = await authApi.login(values.username, values.password)
      const { token, user: responseUser, ...rest } = data
      const user = responseUser || rest
      login(user as AuthUser, token)
      message.success('Bienvenido al Sistema de Farmacia')
      navigate('/dashboard')
    } catch (error: any) {
      console.error("Error en login:", error)
      message.error(error.response?.data?.message || 'Error al iniciar sesión')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div
      style={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'linear-gradient(135deg, #1890ff 0%, #40a9ff 100%)',
      }}
    >
      <Card
        style={{ width: 400, boxShadow: '0 8px 24px rgba(0,0,0,0.15)', borderRadius: 12 }}
      >
        <div style={{ textAlign: 'center' }}>
          <img
            src="../../../../public/assets/logo_inicio.jpg"
            alt="Logo PG"
            style={{ width: 250, marginBottom: 12, display: 'block', margin: '0 auto 0px' }}
          />
        </div>
        <div style={{ textAlign: 'center', marginBottom: 32 }}>
          <Title level={2} style={{ fontSize: 35, marginBottom: 15, marginTop: 20, color: '#407FB5' }}>
            Sistema Integral de Salud Cooperativa
          </Title>
          <Text type="secondary">¡Al gran pueblo argentino, salud!</Text>
        </div>

        <Form
          name="login"
          onFinish={onFinish}
          autoComplete="off"
          size="large"
        >
          <Form.Item
            name="username"
            rules={[{ required: true, message: 'Por favor ingrese su usuario' }]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="Usuario"
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[{ required: true, message: 'Por favor ingrese su contraseña' }]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="Contraseña"
            />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading} block>
              Iniciar Sesión
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  )
}

export default LoginPage
