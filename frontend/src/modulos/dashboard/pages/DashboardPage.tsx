import React from 'react'
import { Card, Row, Col, Statistic, Typography } from 'antd'
import {
  UserOutlined,
  MedicineBoxOutlined,
  FileTextOutlined,
  ShoppingCartOutlined,
  ArrowUpOutlined,
  ArrowDownOutlined,
} from '@ant-design/icons'

const { Title } = Typography

const DashboardPage: React.FC = () => {
  return (
    <div>
      <Title level={2}>Dashboard</Title>
      <Title level={5} type="secondary">Resumen del Sistema de Farmacia</Title>

      <Row gutter={[16, 16]} style={{ marginTop: 24 }}>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Pacientes Registrados"
              value={1250}
              prefix={<UserOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>

        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Medicamentos en Stock"
              value={458}
              prefix={<MedicineBoxOutlined />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>

        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Recetas Pendientes"
              value={23}
              prefix={<FileTextOutlined />}
              valueStyle={{ color: '#faad14' }}
            />
          </Card>
        </Col>

        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Dispensaciones Hoy"
              value={45}
              prefix={<ShoppingCartOutlined />}
              valueStyle={{ color: '#722ed1' }}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]} style={{ marginTop: 24 }}>
        <Col xs={24} lg={12}>
          <Card title="Alertas de Inventario" style={{ height: '100%' }}>
            <Statistic
              title="Productos con Stock Bajo"
              value={12}
              prefix={<ArrowDownOutlined />}
              valueStyle={{ color: '#ff4d4f' }}
            />
            <br />
            <Statistic
              title="Productos por Vencer"
              value={5}
              prefix={<ArrowDownOutlined />}
              valueStyle={{ color: '#faad14' }}
            />
          </Card>
        </Col>

        <Col xs={24} lg={12}>
          <Card title="Actividad Reciente" style={{ height: '100%' }}>
            <ul style={{ paddingLeft: 20 }}>
              <li>Nuevo paciente registrado: Juan Pérez</li>
              <li>Receta dispensada: Maria García</li>
              <li>Ingreso de lote: Amoxicilina 500mg</li>
              <li>Ajuste de stock: Ibuprofeno 400mg</li>
              <li>SÓLO A MODO DE EJEMPLO. AÚN NO ACTUALIZA</li>
            </ul>
          </Card>
        </Col>
      </Row>
    </div>
  )
}

export default DashboardPage
