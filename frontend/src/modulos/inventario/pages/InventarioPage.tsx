import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Modal, Form, Input, Select, InputNumber, DatePicker, message, Card, Row, Col, Tag, Tabs } from 'antd'
import { PlusOutlined, ArrowDownOutlined, ArrowUpOutlined } from '@ant-design/icons'
import { inventarioApi, Lote } from '../../../servicios/api'

const { Option } = Select
const { RangePicker } = DatePicker

const InventarioPage: React.FC = () => {
  const [lotes, setLotes] = useState<Lote[]>([])
  const [loading, setLoading] = useState(false)
  const [ingresoModalVisible, setIngresoModalVisible] = useState(false)
  const [ajusteModalVisible, setAjusteModalVisible] = useState(false)
  const [activeTab, setActiveTab] = useState('lotes')
  const [ingresoForm] = Form.useForm()
  const [ajusteForm] = Form.useForm()

  const fetchLotes = async () => {
    setLoading(true)
    try {
      const response = await inventarioApi.getLotes()
      setLotes(response)
    } catch (error) {
      message.error('Error al cargar inventario')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchLotes()
  }, [])

  const handleIngreso = async (values: any) => {
    try {
      await inventarioApi.ingresoLote(values)
      message.success('Lote registrado exitosamente')
      setIngresoModalVisible(false)
      ingresoForm.resetFields()
      fetchLotes()
    } catch (error) {
      message.error('Error al registrar lote')
    }
  }

  const handleAjuste = async (values: any) => {
    try {
      await inventarioApi.ajusteStock(values)
      message.success('Stock ajustado exitosamente')
      setAjusteModalVisible(false)
      ajusteForm.resetFields()
      fetchLotes()
    } catch (error) {
      message.error('Error al ajustar stock')
    }
  }

  const getStockColor = (stock: number, cantidadInicial: number) => {
    const percentage = (stock / cantidadInicial) * 100
    if (percentage > 50) return 'green'
    if (percentage > 20) return 'orange'
    return 'red'
  }

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 60,
    },
    {
      title: 'Número Lote',
      dataIndex: 'numeroLote',
      key: 'numeroLote',
    },
    {
      title: 'Medicamento',
      dataIndex: ['medicamento', 'nombre'],
      key: 'medicamento',
    },
    {
      title: 'Cant. Inicial',
      dataIndex: 'cantidadInicial',
      key: 'cantidadInicial',
    },
    {
      title: 'Stock Actual',
      dataIndex: 'cantidadActual',
      key: 'cantidadActual',
      render: (stock: number, record: Lote) => (
        <Tag color={getStockColor(stock, record.cantidadInicial)}>
          {stock}
        </Tag>
      ),
    },
    {
      title: 'Fecha Vencimiento',
      dataIndex: 'fechaVencimiento',
      key: 'fechaVencimiento',
      render: (date: string) => {
        const vencDate = new Date(date)
        const today = new Today()
        const isNear = vencDate.getTime() - today.getTime() < 90 * 24 * 60 * 60 * 1000
        return <Tag color={isNear ? 'orange' : 'default'}>{new Date(date).toLocaleDateString()}</Tag>
      },
    },
  ]

  const Today = () => {
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    return today
  }

  const tabItems = [
    {
      key: 'lotes',
      label: 'Lotes',
      children: (
        <Table
          columns={columns}
          dataSource={lotes}
          loading={loading}
          rowKey="id"
          pagination={{ pageSize: 10 }}
        />
      ),
    },
    {
      key: 'movimientos',
      label: 'Movimientos',
      children: (
        <Table
          columns={[
            { title: 'ID', dataIndex: 'id', key: 'id' },
            { title: 'Tipo', dataIndex: 'tipo', key: 'tipo' },
            { title: 'Cantidad', dataIndex: 'cantidad', key: 'cantidad' },
            { title: 'Fecha', dataIndex: 'fecha', key: 'fecha' },
            { title: 'Usuario', dataIndex: 'usuario', key: 'usuario' },
          ]}
          dataSource={[]}
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
          <h2 style={{ margin: 0 }}>Inventario</h2>
        </Col>
        <Col>
          <Space>
            <Button
              type="primary"
              icon={<ArrowDownOutlined />}
              onClick={() => setIngresoModalVisible(true)}
            >
              Ingreso de Lote
            </Button>
            <Button
              icon={<ArrowUpOutlined />}
              onClick={() => setAjusteModalVisible(true)}
            >
              Ajuste de Stock
            </Button>
          </Space>
        </Col>
      </Row>

      <Card>
        <Tabs activeKey={activeTab} onChange={setActiveTab} items={tabItems} />
      </Card>

      <Modal
        title="Ingreso de Lote"
        open={ingresoModalVisible}
        onCancel={() => setIngresoModalVisible(false)}
        footer={null}
      >
        <Form form={ingresoForm} layout="vertical" onFinish={handleIngreso}>
          <Form.Item name="medicamentoId" label="Medicamento" rules={[{ required: true }]}>
            <Select>
              <Option value={1}>Amoxicilina 500mg</Option>
              <Option value={2}>Ibuprofeno 400mg</Option>
            </Select>
          </Form.Item>
          <Form.Item name="cantidad" label="Cantidad" rules={[{ required: true }]}>
            <InputNumber min={1} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="numeroLote" label="Número de Lote" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="fechaVencimiento" label="Fecha de Vencimiento" rules={[{ required: true }]}>
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item>
            <Space style={{ width: '100%', justifyContent: 'flex-end' }}>
              <Button onClick={() => setIngresoModalVisible(false)}>Cancelar</Button>
              <Button type="primary" htmlType="submit">Guardar</Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="Ajuste de Stock"
        open={ajusteModalVisible}
        onCancel={() => setAjusteModalVisible(false)}
        footer={null}
      >
        <Form form={ajusteForm} layout="vertical" onFinish={handleAjuste}>
          <Form.Item name="loteId" label="Lote" rules={[{ required: true }]}>
            <Select>
              {lotes.map(lote => (
                <Option key={lote.id} value={lote.id}>
                  {lote.medicamento.nombre} - Lote: {lote.numeroLote}
                </Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item name="cantidad" label="Nueva Cantidad" rules={[{ required: true }]}>
            <InputNumber min={0} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="motivo" label="Motivo del Ajuste">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item>
            <Space style={{ width: '100%', justifyContent: 'flex-end' }}>
              <Button onClick={() => setAjusteModalVisible(false)}>Cancelar</Button>
              <Button type="primary" htmlType="submit">Guardar</Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default InventarioPage
