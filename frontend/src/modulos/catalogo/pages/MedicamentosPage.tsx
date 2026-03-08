import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Modal, Form, Input, Select, InputNumber, Switch, message, Popconfirm, Card, Row, Col, Tag } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined } from '@ant-design/icons'
import { medicamentosApi, Medicamento } from '../../../servicios/api'

const { Option } = Select

const MedicamentosPage: React.FC = () => {
  const [data, setData] = useState<Medicamento[]>([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [editingRecord, setEditingRecord] = useState<Medicamento | null>(null)
  const [searchText, setSearchText] = useState('')
  const [form] = Form.useForm()

  const fetchData = async () => {
    setLoading(true)
    try {
      const response = await medicamentosApi.getAll()
      console.log('Respuesta de medicamentos:', response);
      setData(response)
    } catch (error) {
      message.error('Error al cargar medicamentos')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchData()
  }, [])

  const handleAdd = () => {
    setEditingRecord(null)
    form.resetFields()
    setModalVisible(true)
  }

  const handleEdit = (record: Medicamento) => {
    setEditingRecord(record)
    form.setFieldsValue(record)
    setModalVisible(true)
  }

  const handleDelete = async (id: number) => {
    try {
      await medicamentosApi.delete(id)
      message.success('Medicamento eliminado')
      fetchData()
    } catch (error) {
      message.error('Error al eliminar medicamento')
    }
  }

  const handleSubmit = async (values: any) => {
    try {
      if (editingRecord) {
        await medicamentosApi.update(editingRecord.id, values)
        message.success('Medicamento actualizado')
      } else {
        await medicamentosApi.create(values)
        message.success('Medicamento registrado')
      }
      setModalVisible(false)
      form.resetFields()
      fetchData()
    } catch (error) {
      message.error('Error al guardar medicamento')
    }
  }

  console.log('data en render:', data);
  const filteredData = data.filter(item =>
    (item.nombreComercial || '').toLowerCase().includes(searchText.toLowerCase()) ||
    (item.nombreGenerico || '').toLowerCase().includes(searchText.toLowerCase())
  )

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 60,
    },
    {
      title: 'Nombre Comercial',
      dataIndex: 'nombreComercial',
      key: 'nombreComercial',
    },
    {
      title: 'Nombre Genérico',
      dataIndex: 'nombreGenerico',
      key: 'nombreGenerico',
    },
    {
      title: 'Laboratorio',
      dataIndex: 'laboratorio',
      key: 'laboratorio',
    },
    {
      title: 'Forma Farmacéutica',
      dataIndex: 'formaFarmaceutica',
      key: 'formaFarmaceutica',
    },
    {
      title: 'Concentración',
      dataIndex: 'concentracion',
      key: 'concentracion',
    },
    {
      title: 'Unidad',
      dataIndex: 'unidadPresentacion',
      key: 'unidadPresentacion',
    },
    {
      title: 'Stock',
      dataIndex: 'stockTotal',
      key: 'stockTotal',
      render: (stock: number) => (
        <Tag color={stock > 10 ? 'green' : stock > 0 ? 'orange' : 'red'}>
          {stock}
        </Tag>
      ),
    },
    {
      title: 'Precio',
      dataIndex: 'precioVenta',
      key: 'precioVenta',
      render: (precio: number | undefined) => precio != null ? `$${precio.toFixed(2)}` : '-',
    },
    {
      title: 'Requiere Receta',
      dataIndex: 'requiereReceta',
      key: 'requiereReceta',
      render: (requiere: boolean) => (
        <Tag color={requiere ? 'blue' : 'default'}>
          {requiere ? 'Sí' : 'No'}
        </Tag>
      ),
    },
    {
      title: 'Activo',
      dataIndex: 'activo',
      key: 'activo',
      render: (activo: boolean) => (
        <Tag color={activo ? 'green' : 'red'}>
          {activo ? 'Activo' : 'Inactivo'}
        </Tag>
      ),
    },
    {
      title: 'Acciones',
      key: 'acciones',
      render: (_: any, record: Medicamento) => (
        <Space>
          <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)} />
          <Popconfirm
            title="¿Está seguro de eliminar este medicamento?"
            onConfirm={() => handleDelete(record.id)}
            okText="Sí"
            cancelText="No"
          >
            <Button type="link" danger icon={<DeleteOutlined />} />
          </Popconfirm>
        </Space>
      ),
    },
  ]

  return (
    <div>
      <Row justify="space-between" align="middle" style={{ marginBottom: 16 }}>
        <Col>
          <h2 style={{ margin: 0 }}>Medicamentos</h2>
        </Col>
        <Col>
          <Space>
            <Input
              placeholder="Buscar..."
              prefix={<SearchOutlined />}
              value={searchText}
              onChange={e => setSearchText(e.target.value)}
              style={{ width: 200 }}
            />
            <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
              Nuevo Medicamento
            </Button>
          </Space>
        </Col>
      </Row>

      <Card>
        <Table
          columns={columns}
          dataSource={filteredData}
          loading={loading}
          rowKey="id"
          pagination={{ pageSize: 10 }}
        />
      </Card>

      <Modal
        title={editingRecord ? 'Editar Medicamento' : 'Nuevo Medicamento'}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        footer={null}
        width={700}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
        >
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="nombreComercial"
                label="Nombre Comercial"
                rules={[{ required: true, message: 'Ingrese el nombre comercial' }]}
              >
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="nombreGenerico"
                label="Nombre Genérico / Principio Activo"
              >
                <Input />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={8}>
              <Form.Item
                name="formaFarmaceutica"
                label="Forma Farmacéutica"
                rules={[{ required: true, message: 'Seleccione' }]}
              >
                <Select>
                  <Option value="COMPRIMIDO">Comprimido</Option>
                  <Option value="CÁPSULA">Cápsula</Option>
                  <Option value="JARABE">Jarabe</Option>
                  <Option value="INYECCIÓN">Inyección</Option>
                  <Option value="GOTAS">Gotas</Option>
                  <Option value="CREMA">Crema</Option>
                  <Option value="POMADA">Pomada</Option>
                  <Option value="SUSPENSIÓN">Suspensión</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                name="concentracion"
                label="Concentración"
                rules={[{ required: true, message: 'Ingrese la concentración' }]}
              >
                <Input placeholder="ej: 500mg" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                name="unidadPresentacion"
                label="Unidad"
                rules={[{ required: true, message: 'Seleccione' }]}
              >
                <Select>
                  <Option value="COMPRIMIDOS">Comprimidos</Option>
                  <Option value="CÁPSULAS">Cápsulas</Option>
                  <Option value="ML">ml</Option>
                  <Option value="G">g</Option>
                  <Option value="AMPOLLAS">Ampollas</Option>
                  <Option value="TUBOS">Tubos</Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={8}>
              <Form.Item name="stockTotal" label="Stock Total">
                <InputNumber min={0} style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="precioVenta" label="Precio">
                <InputNumber min={0} step={0.01} style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="codigoBarras" label="Código de Barras">
                <Input />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={8}>
              <Form.Item name="laboratorio" label="Laboratorio">
                <Input />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="requiereReceta" label="Requiere Receta" valuePropName="checked">
                <Switch />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="activo" label="Activo" valuePropName="checked">
                <Switch defaultChecked />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
            <Space>
              <Button onClick={() => setModalVisible(false)}>Cancelar</Button>
              <Button type="primary" htmlType="submit">
                {editingRecord ? 'Actualizar' : 'Guardar'}
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default MedicamentosPage