import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Modal, Form, Input, Select, DatePicker, message, Card, Row, Col, Tag, Tabs } from 'antd'
import { PlusOutlined, CheckOutlined, CloseOutlined } from '@ant-design/icons'
import { recetasApi, pacientesApi, Receta } from '../../../servicios/api'

const { Option } = Select

const RecetasPage: React.FC = () => {
  const [recetas, setRecetas] = useState<Receta[]>([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [pacientes, setPacientes] = useState<any[]>([])
  const [form] = Form.useForm()
  const [activeTab, setActiveTab] = useState('pendientes')

  const fetchRecetas = async () => {
    setLoading(true)
    try {
      const response = await recetasApi.getAll()
      setRecetas(response)
    } catch (error) {
      message.error('Error al cargar recetas')
    } finally {
      setLoading(false)
    }
  }

  const fetchPacientes = async () => {
    try {
      const response = await pacientesApi.getAll()
      setPacientes(response)
    } catch (error) {
      console.error('Error al cargar pacientes')
    }
  }

  useEffect(() => {
    fetchRecetas()
    fetchPacientes()
  }, [])

  const handleCreate = async (values: any) => {
    try {
      await recetasApi.create(values)
      message.success('Receta registrada')
      setModalVisible(false)
      form.resetFields()
      fetchRecetas()
    } catch (error) {
      message.error('Error al registrar receta')
    }
  }

  const handleValidar = async (id: number) => {
    try {
      await recetasApi.validarReceta(id)
      message.success('Receta validada')
      fetchRecetas()
    } catch (error) {
      message.error('Error al validar receta')
    }
  }

  const getEstadoColor = (estado: string) => {
    switch (estado) {
      case 'PENDIENTE': return 'orange'
      case 'VALIDADA': return 'blue'
      case 'DISPENSADA': return 'green'
      case 'VENCIDA': return 'red'
      default: return 'default'
    }
  }

  const filteredRecetas = activeTab === 'pendientes'
    ? recetas.filter(r => r.estado === 'PENDIENTE')
    : recetas

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 60,
    },
    {
      title: 'Paciente',
      key: 'paciente',
      render: (_: any, record: Receta) => `${record.paciente.nombre} ${record.paciente.apellido}`,
    },
    {
      title: 'Médico',
      dataIndex: 'medico',
      key: 'medico',
    },
    {
      title: 'Fecha Emisión',
      dataIndex: 'fechaEmision',
      key: 'fechaEmision',
      render: (date: string) => new Date(date).toLocaleDateString(),
    },
    {
      title: 'Fecha Vencimiento',
      dataIndex: 'fechaVencimiento',
      key: 'fechaVencimiento',
      render: (date: string) => new Date(date).toLocaleDateString(),
    },
    {
      title: 'Estado',
      dataIndex: 'estado',
      key: 'estado',
      render: (estado: string) => <Tag color={getEstadoColor(estado)}>{estado}</Tag>,
    },
    {
      title: 'Items',
      dataIndex: 'detalles',
      key: 'detalles',
      render: (detalles: any[]) => `${detalles?.length || 0} items`,
    },
    {
      title: 'Acciones',
      key: 'acciones',
      render: (_: any, record: Receta) => (
        <Space>
          {record.estado === 'PENDIENTE' && (
            <Button
              type="link"
              icon={<CheckOutlined />}
              onClick={() => handleValidar(record.id)}
            >
              Validar
            </Button>
          )}
          {record.estado === 'VALIDADA' && (
            <Button type="link">
              Dispensar
            </Button>
          )}
        </Space>
      ),
    },
  ]

  const tabItems = [
    {
      key: 'pendientes',
      label: 'Pendientes',
      children: (
        <Table
          columns={columns}
          dataSource={filteredRecetas}
          loading={loading}
          rowKey="id"
          pagination={{ pageSize: 10 }}
        />
      ),
    },
    {
      key: 'todas',
      label: 'Todas',
      children: (
        <Table
          columns={columns}
          dataSource={recetas}
          loading={loading}
          rowKey="id"
          pagination={{ pageSize: 10 }}
        />
      ),
    },
  ]

  return (
    <div>
      <Row justify="space-between" align="middle" style={{ marginBottom: 16 }}>
        <Col>
          <h2 style={{ margin: 0 }}>Recetas</h2>
        </Col>
        <Col>
          <Button type="primary" icon={<PlusOutlined />} onClick={() => setModalVisible(true)}>
            Nueva Receta
          </Button>
        </Col>
      </Row>

      <Card>
        <Tabs activeKey={activeTab} onChange={setActiveTab} items={tabItems} />
      </Card>

      <Modal
        title="Nueva Receta"
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        footer={null}
        width={700}
      >
        <Form form={form} layout="vertical" onFinish={handleCreate}>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="pacienteId" label="Paciente" rules={[{ required: true }]}>
                <Select showSearch placeholder="Buscar paciente...">
                  {pacientes.map(p => (
                    <Option key={p.id} value={p.id}>
                      {p.nombre} {p.apellido}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="medico" label="Médico" rules={[{ required: true }]}>
                <Input placeholder="Nombre del médico" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="fechaEmision" label="Fecha Emisión" rules={[{ required: true }]}>
                <DatePicker style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="fechaVencimiento" label="Fecha Vencimiento" rules={[{ required: true }]}>
                <DatePicker style={{ width: '100%' }} />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item>
            <Space style={{ width: '100%', justifyContent: 'flex-end' }}>
              <Button onClick={() => setModalVisible(false)}>Cancelar</Button>
              <Button type="primary" htmlType="submit">Guardar</Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default RecetasPage
