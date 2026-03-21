import React, { useState } from 'react'
import { Layout, Menu, theme } from 'antd'
import {
  DashboardOutlined,
  MedicineBoxOutlined,
  UserOutlined,
  FileTextOutlined,
  ShoppingCartOutlined,
  HistoryOutlined,
  SettingOutlined,
  TeamOutlined,
} from '@ant-design/icons'
import { useNavigate, useLocation } from 'react-router-dom'

const { Sider } = Layout

const Sidebar: React.FC = () => {
  const [collapsed, setCollapsed] = useState(false)
  const navigate = useNavigate()
  const location = useLocation()
  const {
    token: { colorBgContainer },
  } = theme.useToken()

  const menuItems = [
    {
      key: '/dashboard',
      icon: <DashboardOutlined />,
      label: 'Estado de Situación',
    },
    {
      key: '/pacientes',
      icon: <UserOutlined />,
      label: 'Pacientes',
    },
    {
      key: '/medicamentos',
      icon: <MedicineBoxOutlined />,
      label: 'Medicamentos',
    },
    {
      key: '/inventario',
      icon: <ShoppingCartOutlined />,
      label: 'Inventario',
    },
    {
      key: '/historia-clinica',
      icon: <FileTextOutlined />,
      label: 'Historia Clínica',
    },
    {
      key: '/recetas',
      icon: <FileTextOutlined />,
      label: 'Recetas',
    },
    {
      key: '/dispensacion',
      icon: <HistoryOutlined />,
      label: 'Dispensación',
    },
    {
      key: '/administracion',
      icon: <TeamOutlined />,
      label: 'Administración',
    },
    {
      key: '/configuracion',
      icon: <SettingOutlined />,
      label: 'Configuración',
    },
  ]

  const handleMenuClick = (key: string) => {
    navigate(key)
  }

  return (
    <Sider
      collapsible
      collapsed={collapsed}
      onCollapse={(value) => setCollapsed(value)}
      style={{ background: colorBgContainer }}
      breakpoint="lg"
      collapsedWidth="80"
    >
      <div
        style={{
          height: 64,
          margin: 16,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          background: '#1890ff',
          borderRadius: 8,
          color: '#fff',
          fontWeight: 'bold',
          fontSize: collapsed ? 14 : 18,
        }}
      >
        {collapsed ? 'FS' : 'Farmacia Salud'}
      </div>
      <Menu
        theme="light"
        mode="inline"
        selectedKeys={[location.pathname]}
        items={menuItems}
        onClick={({ key }) => handleMenuClick(key)}
        style={{ borderRight: 0 }}
      />
    </Sider>
  )
}

export default Sidebar
