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
      key: '/recetas',
      icon: <FileTextOutlined />,
      label: 'Recetas',
    },
    {
      key: '/historia-clinica',
      icon: <HistoryOutlined />,
      label: 'Historia Clínica',
    },
    {
      key: '/administracion',
      icon: <TeamOutlined />,
      label: 'Administración',
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
      <div>
        <img
          src="../../../public/assets/logo.png"
          alt="Logo Carrica"
          style={{ width: 250, margin: '16px auto', display: 'block' }}
        />
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
