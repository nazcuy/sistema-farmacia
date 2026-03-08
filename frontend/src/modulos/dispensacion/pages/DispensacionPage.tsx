import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Modal, Form, Input, Select, message, Card, Row, Col, Tag, Steps, List, Badge } from 'antd'
import { ShoppingCartOutlined, CheckOutlined, CloseOutlined } from '@ant-design/icons'
import { dispensacionApi, recetasApi, Dispensacion } from '../../../servicios/api'

const { Option } = Select

const DispensacionPage: React.FC = () => {
  const [dispensaciones, setDispensaciones] = useState<Dispensacion[]>([])
  const [recetasValidadas, setRecetasValidadas] = useState<any[]>([])
  const [loading, setLoading] = useState(false)
  const [dispensarModalVisible, setDispensarModalVisible] = useState(false)
  const [selectedReceta, setSelectedReceta] = useState<any>(null)
  const [form] = Form.useForm()

  const fetchDispensaciones = async () => {
    setLoading(true)
    try {
      const response = await dispensacionApi.getAll()
      setDispensaciones(response)
    } catch (error) {
      console.error('Error al cargar dispensaciones')
    } finally {
      setLoading(false)
    }
  }

  const fetchRecetasValidadas = async () => {
    try {
      const response = await recetasApi.getAll()
      const validadas = response.filter((r: any) => r.estado === 'VALIDADA')
      setRecetasValidadas(validadas)
    } catch (error) {
      console.error('Error al cargar recetas')
    }
  }

  useEffect(() => {
    fetchDispensaciones()
    fetchRecetasValidadas()
  }, [])

  const handleDispensar = (receta: any) => {
    setSelectedReceta(receta)
    setDispensarModalVisible(true)
  }

  const handleConfirm = async (values: any) => {
    try {
      await dispensacionApi.crear({
        recetaId: selectedReceta.id,
        items: values.items,
      })
      message.success('Dispensación creada exitosamente')
      setDispensarModalVisible(false)
      form.resetFields()
      fetchDispensaciones()
    } catch (error) {
      message.error('Error al crear dispensación')
    }
  }

  const getEstadoColor = (estado: string) => {
    switch (estado) {
      case 'COMPLETADA': return 'success'
      case 'PENDIENTE': return 'processing'
      case 'CANCELADA': return 'error'
      default: return 'default'
    }
  }

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
      render: (_: any, record: Dispensacion) => `${record.paciente.nombre} ${record.paciente.apellido}`,
    },
    {
      title: 'Receta Nro.',
      dataIndex: ['receta', 'id'],
      key: 'receta',
    },
    {
      title: 'Fecha',
      dataIndex: 'fechaDispensacion',
      key: 'fechaDispensacion',
      render: (date: string) => new Date(date).toLocaleString(),
    },
    {
      title: 'Total',
      dataIndex: 'total',
      key: 'total',
      render: (total: number) => `$${total.toFixed(2)}`,
    },
    {
      title: 'Estado',
      dataIndex: 'estado',
      key: 'estado',
      render: (estado: string) => <Badge status={getEstadoColor(estado) as any} text={estado} />,
    },
    {
      title: 'Acciones',
      key: 'acciones',
      render: (_: any, record: Dispensacion) => (
        <Space>
          {record.estado === 'PENDIENTE' && (
            <>
              <Button type="link" icon={<CheckOutlined />} onClick={() => handleConfirmDispensacion(record.id)}>
                Confirmar
              </Button>
              <Button type="link" danger icon={<CloseOutlined />} onClick={() => handleCancelarDispensacion(record.id)}>
                Cancelar
              </Button>
            </>
          )}
        </Space>
      ),
    },
  ]

  const handleConfirmDispensacion = async (id: number) => {
    try {
      await dispensacionApi.confirmar(id)
      message.success('Dispensación confirmada')
      fetchDispensaciones()
    } catch (error) {
      message.error('Error al confirmar')
    }
  }

  const handleCancelarDispensacion = async (id: number) => {
    try {
      await dispensacionApi.cancelar(id)
      message.warning('Dispensación cancelada')
      fetchDispensaciones()
    } catch (error) {
      message.error('Error al cancelar')
    }
  }

  return (
    <div>
      <Row gutter={[16, 16]}>
        <Col xs={24} lg={12}>
          <Card title="Dispensaciones" style={{ height: '100%' }}>
            <Table
              columns={columns}
              dataSource={dispensaciones}
              loading={loading}
              rowKey="id"
              pagination={{ pageSize: 10 }}
              size="small"
            />
          </Card>
        </Col>

        <Col xs={24} lg={12}>
          <Card title="Recetas para Dispensar">
            <List
              size="small"
              dataSource={recetasValidadas}
              renderItem={(item: any) => (
                <List.Item
                  actions={[
                    <Button
                      type="primary"
                      size="small"
                      icon={<ShoppingCartOutlined />}
                      onClick={() => handleDispensar(item)}
                    >
                      Dispensar
                    </Button>
                  ]}
                >
                  <List.Item.Meta
                    title={`Receta #${item.id}`}
                    description={`Paciente: ${item.paciente?.nombre || 'N/A'} ${item.paciente?.apellido || ''}`}
                  />
                </List.Item>
              )}
              locale={{ emptyText: 'No hay recetas validadas' }}
            />
          </Card>
        </Col>
      </Row>

      <Modal
        title={`Dispensar Receta #${selectedReceta?.id}`}
        open={dispensarModalVisible}
        onCancel={() => setDispensarModalVisible(false)}
        footer={null}
        width={600}
      >
        {selectedReceta && (
          <div>
            <Row gutter={16} style={{ marginBottom: 16 }}>
              <Col span={12}>
                <strong>Paciente:</strong> {selectedReceta.paciente?.nombre} {selectedReceta.paciente?.apellido}
              </Col>
              <Col span={12}>
                <strong>Médico:</strong> {selectedReceta.medico}
              </Col>
            </Row>

            <Steps
              current={0}
              items={[
                { title: 'Verificar Receta' },
                { title: 'Seleccionar Medicamentos' },
                { title: 'Confirmar Dispensación' },
              ]}
              style={{ marginBottom: 24 }}
            />

            <Form form={form} layout="vertical" onFinish={handleConfirm}>
              <Form.Item label="Detalles de la Receta">
                <List
                  size="small"
                  dataSource={selectedReceta.detalles || []}
                  renderItem={(item: any) => (
                    <List.Item>
                      <span>{item.medicamento?.nombre}</span>
                      <span>Cant: {item.cantidad}</span>
                    </List.Item>
                  )}
                />
              </Form.Item>

              <Form.Item>
                <Space style={{ width: '100%', justifyContent: 'flex-end' }}>
                  <Button onClick={() => setDispensarModalVisible(false)}>Cancelar</Button>
                  <Button type="primary" htmlType="submit">Confirmar Dispensación</Button>
                </Space>
              </Form.Item>
            </Form>
          </div>
        )}
      </Modal>
    </div>
  )
}

export default DispensacionPage
