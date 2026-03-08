import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Modal, Form, Input, Select, DatePicker, message, Popconfirm, Card, Row, Col } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined } from '@ant-design/icons'
import { pacientesApi, Persona } from '../../../servicios/api'

const { Option } = Select

const PacientesPage: React.FC = () => {
  const [data, setData] = useState<Persona[]>([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [editingRecord, setEditingRecord] = useState<Persona | null>(null)
  const [form] = Form.useForm()

  const fetchData = async () => {
    setLoading(true)
    try {
      const response = await pacientesApi.getAll()
      setData(response)
    } catch (error) {
      message.error('Error al cargar pacientes')
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

  const handleEdit = (record: Persona) => {
    setEditingRecord(record)
    form.setFieldsValue({
      ...record,
      fechaNacimiento: record.fechaNacimiento ? new Date(record.fechaNacimiento) : null,
    })
    setModalVisible(true)
  }

  const handleDelete = async (id: number) => {
    try {
      await pacientesApi.delete(id)
      message.success('Paciente eliminado')
      fetchData()
    } catch (error) {
      message.error('Error al eliminar paciente')
    }
  }

  const handleSubmit = async (values: any) => {
    try {
      if (editingRecord) {
        await pacientesApi.update(editingRecord.id, values)
        message.success('Paciente actualizado')
      } else {
        await pacientesApi.create(values)
        message.success('Paciente registrado')
      }
      setModalVisible(false)
      form.resetFields()
      fetchData()
    } catch (error) {
      message.error('Error al guardar paciente')
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
      title: 'Nombre',
      dataIndex: 'nombre',
      key: 'nombre',
    },
    {
      title: 'Apellido',
      dataIndex: 'apellido',
      key: 'apellido',
    },
    {
      title: 'Tipo Documento',
      dataIndex: 'tipoDocumento',
      key: 'tipoDocumento',
    },
    {
      title: 'Número Documento',
      dataIndex: 'numeroDocumento',
      key: 'numeroDocumento',
    },
    {
      title: 'Teléfono',
      dataIndex: 'telefono',
      key: 'telefono',
    },
    {
      title: 'Acciones',
      key: 'acciones',
      render: (_: any, record: Persona) => (
        <Space>
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          />
          <Popconfirm
            title="¿Está seguro de eliminar este paciente?"
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
          <h2 style={{ margin: 0 }}>Pacientes</h2>
        </Col>
        <Col>
          <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
            Nuevo Paciente
          </Button>
        </Col>
      </Row>

      <Card>
        <Table
          columns={columns}
          dataSource={data}
          loading={loading}
          rowKey="id"
          pagination={{ pageSize: 10 }}
        />
      </Card>

      <Modal
        title={editingRecord ? 'Editar Paciente' : 'Nuevo Paciente'}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        footer={null}
        width={600}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
        >
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="nombre"
                label="Nombre"
                rules={[{ required: true, message: 'Ingrese el nombre' }]}
              >
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="apellido"
                label="Apellido"
                rules={[{ required: true, message: 'Ingrese el apellido' }]}
              >
                <Input />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="tipoDocumento"
                label="Tipo Documento"
                rules={[{ required: true, message: 'Seleccione el tipo' }]}
              >
                <Select>
                  <Option value="DNI">DNI</Option>
                  <Option value="CI">Cédula de Identidad</Option>
                  <Option value="PASAPORTE">Pasaporte</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="numeroDocumento"
                label="Número Documento"
                rules={[{ required: true, message: 'Ingrese el número' }]}
              >
                <Input />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="fechaNacimiento"
                label="Fecha Nacimiento"
              >
                <DatePicker style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="sexo"
                label="Sexo"
              >
                <Select>
                  <Option value="MASCULINO">Masculino</Option>
                  <Option value="FEMENINO">Femenino</Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="telefono" label="Teléfono">
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="email" label="Email">
                <Input type="email" />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item name="direccion" label="Dirección">
            <Input.TextArea rows={2} />
          </Form.Item>

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

export default PacientesPage
