import React from 'react'
import { Layout, theme, Dropdown, Avatar, Space } from 'antd'
import { UserOutlined, LogoutOutlined, SettingOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../contextos/AuthContext'

const { Header: AntHeader } = Layout

const HeaderComponent: React.FC = () => {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const {
    token: { colorBgContainer },
  } = theme.useToken()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const items = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: 'Perfil',
    },
    {
      key: 'settings',
      icon: <SettingOutlined />,
      label: 'Configuración',
    },
    {
      type: 'divider',
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: 'Cerrar Sesión',
      danger: true,
      onClick: handleLogout,
    },
  ]

  const displayName = user
    ? user.nombre || user.apellido
      ? `${user.nombre || ''} ${user.apellido || ''}`.trim()
      : user.username || user.email
    : 'Usuario'

  return (
    <AntHeader style={{ padding: '0 24px', background: colorBgContainer, display: 'flex', justifyContent: 'flex-end', alignItems: 'center' }}>
      <Dropdown menu={{ items }} placement="bottomRight">
        <Space style={{ cursor: 'pointer' }}>
          <Avatar icon={<UserOutlined />} style={{ backgroundColor: '#1890ff' }} />
          <span>{displayName}</span>
        </Space>
      </Dropdown>
    </AntHeader>
  )
}

export default HeaderComponent
