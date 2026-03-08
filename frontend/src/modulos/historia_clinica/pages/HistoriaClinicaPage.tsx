import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Modal, Form, Input, Select, DatePicker, message, Card, Row, Col, Tabs, Descriptions, Tag } from 'antd'
import { PlusOutlined, EditOutlined, EyeOutlined } from '@ant-design/icons'
import { historiaClinicaApi, pacientesApi, HistoriaClinica } from '../../../servicios/api'

const { TextArea } = Input
const { Option } = Select

const HistoriaClinicaPage: React.FC = () => {
  const [data, setData] = useState<HistoriaClinica[]>([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [viewModalVisible, setViewModalVisible] = useState(false)
  const [selectedHistoria, setSelectedHistoria] = useState<HistoriaClinica | null>(null)
  const [pacientes, setPacientes] = useState<any[]>([])
  const [editingRecord, setEditingRecord] = useState<HistoriaClinica | null>(null)
  const [form] = Form.useForm()

  const fetchData = async () => {
    setLoading(true)
    try {
      const response = await historiaClinicaApi.getAll()
      setData(response)
    } catch (error) {
      message.error('Error al cargar historias clínicas')
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
    fetchData()
    fetchPacientes()
  }, [])

  const handleAdd = () => {
    setEditingRecord(null)
    form.resetFields()
    setModalVisible(true)
  }

  const handleEdit = (record: HistoriaClinica) => {
    setEditingRecord(record)
    form.setFieldsValue(record)
    setModalVisible(true)
  }

  const handleView = (record: HistoriaClinica) => {
    setSelectedHistoria(record)
    setViewModalVisible(true)
  }

  const handleSubmit = async (values: any) => {
    try {
      if (editingRecord) {
        await historiaClinicaApi.update(editingRecord.id, values)
        message.success('Historia clínica actualizada')
      } else {
        await historiaClinicaApi.create(values)
        message.success('Historia clínica registrada')
      }
      setModalVisible(false)
      form.resetFields()
      fetchData()
    } catch (error) {
      message.error('Error al guardar historia clínica')
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
      dataIndex: ['paciente', 'nombre'],
      key: 'paciente',
      render: (_: any, record: HistoriaClinica) => `${record.paciente.nombre} ${record.paciente.apellido}`,
    },
    {
      title: 'Nro. Documento',
      dataIndex: ['paciente', 'numeroDocumento'],
      key: 'numeroDocumento',
    },
    {
      title: 'Fecha Apertura',
      dataIndex: 'fechaApertura',
      key: 'fechaApertura',
      render: (date: string) => new Date(date).toLocaleDateString(),
    },
    {
      title: 'Última Actualización',
      dataIndex: 'ultimaActualizacion',
      key: 'ultimaActualizacion',
      render: (date: string) => new Date(date).toLocaleDateString(),
    },
    {
      title: 'Acciones',
      key: 'acciones',
      render: (_: any, record: HistoriaClinica) => (
        <Space>
          <Button type="link" icon={<EyeOutlined />} onClick={() => handleView(record)}>
            Ver
          </Button>
          <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
            Editar
          </Button>
        </Space>
      ),
    },
  ]

  return (
    <div>
      <Row justify="space-between" align="middle" style={{ marginBottom: 16 }}>
        <Col>
          <h2 style={{ margin: 0 }}>Historias Clínicas</h2>
        </Col>
        <Col>
          <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
            Nueva Historia
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
        title={editingRecord ? 'Editar Historia Clínica' : 'Nueva Historia Clínica'}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        footer={null}
        width={700}
      >
        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          <Row gutter={16}>
            <Col span={24}>
              <Form.Item name="pacienteId" label="Paciente" rules={[{ required: true, message: 'Seleccione el paciente' }]}>
                <Select showSearch placeholder="Buscar paciente...">
                  {pacientes.map(p => (
                    <Option key={p.id} value={p.id}>
                      {p.nombre} {p.apellido} - {p.numeroDocumento}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={24}>
              <Form.Item name="antecedentesPersonales" label="Antecedentes Personales">
                <TextArea rows={3} placeholder="Enfermedades previas, cirugías, etc." />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={24}>
              <Form.Item name="antecedentesFamiliares" label="Antecedentes Familiares">
                <TextArea rows={3} placeholder="Enfermedades familiares" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="alergias" label="Alergias">
                <TextArea rows={2} placeholder="Alergias a medicamentos" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="medicamentosActuales" label="Medicamentos Actuales">
                <TextArea rows={2} placeholder="Medicamentos en uso" />
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

      <Modal
        title="Historia Clínica"
        open={viewModalVisible}
        onCancel={() => setViewModalVisible(false)}
        footer={null}
        width={800}
      >
        {selectedHistoria && (
          <Descriptions bordered column={2}>
            <Descriptions.Item label="Paciente">
              {selectedHistoria.paciente.nombre} {selectedHistoria.paciente.apellido}
            </Descriptions.Item>
            <Descriptions.Item label="Documento">
              {selectedHistoria.paciente.numeroDocumento}
            </Descriptions.Item>
            <Descriptions.Item label="Fecha de Apertura">
              {new Date(selectedHistoria.fechaApertura).toLocaleDateString()}
            </Descriptions.Item>
            <Descriptions.Item label="Última Actualización">
              {new Date(selectedHistoria.ultimaActualizacion).toLocaleDateString()}
            </Descriptions.Item>
            <Descriptions.Item label="Antecedentes Personales" span={2}>
              {selectedHistoria.antecedentesPersonales || 'No registrados'}
            </Descriptions.Item>
            <Descriptions.Item label="Antecedentes Familiares" span={2}>
              {selectedHistoria.antecedentesFamiliares || 'No registrados'}
            </Descriptions.Item>
            <Descriptions.Item label="Alergias">
              <Tag color="red">{selectedHistoria.alergias || 'Sin alergias'}</Tag>
            </Descriptions.Item>
            <Descriptions.Item label="Medicamentos Actuales">
              {selectedHistoria.medicamentosActuales || 'Ninguno'}
            </Descriptions.Item>
          </Descriptions>
        )}
      </Modal>
    </div>
  )
}

export default HistoriaClinicaPage
