import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Modal, Form, Input, Select, DatePicker, message, Popconfirm, Card, Row, Col, Divider } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined } from '@ant-design/icons'
import { pacientesApi, Persona } from '../../../servicios/api'
import dayjs from 'dayjs';
const { Option } = Select

const PacientesPage: React.FC = () => {
  const [data, setData] = useState<Persona[]>([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [editingRecord, setEditingRecord] = useState<Persona | null>(null)
  const [showHistoriaClinica, setShowHistoriaClinica] = useState(false)
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
    setShowHistoriaClinica(false)
    form.resetFields()
    setModalVisible(true)
  }

  const handleEdit = (record: Persona) => {
    setEditingRecord(record)
    setShowHistoriaClinica(false)
    // Obtener el primer domicilio activo (si existe)
    const domicilio = record.domicilios?.find(d => d.activo) || {} as any
    form.setFieldsValue({
      ...record,
      fechaNacimiento: record.fechaNacimiento ? dayjs(record.fechaNacimiento) : null,
      sexo: record.sexo === 'M' ? 'MASCULINO' : record.sexo === 'F' ? 'FEMENINO' : 'OTRO',
      // Campos de domicilio
      calle: domicilio.calle,
      numero: domicilio.numero,
      piso: domicilio.piso,
      depto: domicilio.depto,
      manzana: domicilio.manzana,
      cuadricula: domicilio.cuadricula,
      barrio: domicilio.barrio,
      localidad: domicilio.localidad,
      provincia: domicilio.provincia,
      codigoPostal: domicilio.codigoPostal,
      tipoDomicilio: domicilio.tipo,
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
      const payload = {
        ...values,
        fechaNacimiento: values.fechaNacimiento ? values.fechaNacimiento.format('YYYY-MM-DD') : null,
        sexo: values.sexo === 'MASCULINO' ? 'M' : values.sexo === 'FEMENINO' ? 'F' : 'OTRO',
      }
      if (showHistoriaClinica) {
        payload.historiaClinica = {
          medicoId: values.medicoId,
          fechaConsulta: values.fechaConsulta ? values.fechaConsulta.format('YYYY-MM-DDTHH:mm:ss') : dayjs().format('YYYY-MM-DDTHH:mm:ss'),
          motivoConsulta: values.motivoConsulta,
          diagnostico: values.diagnostico,
          observaciones: values.observaciones,
          recetas: values.recetas || []
        }
      }
      if (editingRecord) {
        await pacientesApi.update(editingRecord.id, payload)
        message.success('Paciente actualizado')
      } else {
        await pacientesApi.create(payload)
        message.success('Paciente registrado')
      }
      setModalVisible(false)
      form.resetFields()
      fetchData()
    } catch (error: any) {
      console.error('Error al guardar:', error)
      message.error('Error al guardar paciente')
    }
  }

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
    { title: 'Nombre', dataIndex: 'nombre', key: 'nombre' },
    { title: 'Apellido', dataIndex: 'apellido', key: 'apellido' },
    { title: 'Tipo Doc.', dataIndex: 'tipoDocumento', key: 'tipoDocumento' },
    { title: 'Número Doc.', dataIndex: 'numeroDocumento', key: 'numeroDocumento' },
    { title: 'Teléfono', dataIndex: 'telefono', key: 'telefono' },
    {
      title: 'Acciones',
      key: 'acciones',
      render: (_: any, record: Persona) => (
        <Space>
          <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)} />
          <Popconfirm title="¿Eliminar?" onConfirm={() => handleDelete(record.id)} okText="Sí" cancelText="No">
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
                  <Option value="LE">Libreta de Enrolamiento</Option>
                  <Option value="LC">Libreta Cívica</Option>
                  <Option value="PASAPORTE">Pasaporte</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="numeroDocumento"
                label="Número Documento"
                rules={[{ required: true, message: 'Ingrese el número sin puntos ni comas' }]}
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
                  <Option value="M">Masculino</Option>
                  <Option value="F">Femenino</Option>
                  <Option value="O">Otro</Option>
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

          <Divider orientation="left">Domicilio</Divider>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="calle" label="Calle" rules={[{ required: true }]}>
                <Input />
              </Form.Item>
            </Col>
            <Col span={4}>
              <Form.Item name="numero" label="Número" rules={[{ required: true }]}>
                <Input />
              </Form.Item>
            </Col>
            <Col span={4}>
              <Form.Item name="piso" label="Piso">
                <Input />
              </Form.Item>
            </Col>
            <Col span={4}>
              <Form.Item name="depto" label="Depto">
                <Input />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={6}>
              <Form.Item name="manzana" label="Manzana">
                <Input />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="cuadricula" label="Cuadrícula">
                <Input />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="barrio" label="Barrio">
                <Input />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="tipoDomicilio" label="Tipo" initialValue="CASA">
                <Select>
                  <Option value="CASA">Casa</Option>
                  <Option value="TRABAJO">Trabajo</Option>
                  <Option value="OTRO">Otro</Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={8}>
              <Form.Item name="localidad" label="Localidad" rules={[{ required: true }]}>
                <Input />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="provincia" label="Provincia" rules={[{ required: true }]}>
                <Input />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="codigoPostal" label="Código Postal" rules={[{ required: true }]}>
                <Input />
              </Form.Item>
            </Col>
          </Row>

          {showHistoriaClinica && (
            <>
              <Divider orientation="left">Historia Clínica</Divider>

              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    name="medicoId"
                    label="Médico (ID)"
                    rules={[{ required: true, message: 'Ingrese el ID del médico' }]}
                  >
                    <Input />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="fechaConsulta"
                    label="Fecha Consulta"
                    initialValue={dayjs()}
                  >
                    <DatePicker showTime style={{ width: '100%' }} />
                  </Form.Item>
                </Col>
              </Row>

              <Form.Item
                name="motivoConsulta"
                label="Motivo Consulta"
              >
                <Input.TextArea rows={3} />
              </Form.Item>

              <Form.Item
                name="diagnostico"
                label="Diagnóstico"
              >
                <Input.TextArea rows={3} />
              </Form.Item>

              <Form.Item
                name="observaciones"
                label="Observaciones"
              >
                <Input.TextArea rows={3} />
              </Form.Item>

              <Divider orientation="left">Recetas</Divider>

              <Form.List name="recetas">
                {(fields, { add, remove }) => (
                  <>
                    {fields.map(({ key, name, ...restField }) => (
                      <div key={key} style={{ marginBottom: 16, border: '1px solid #d9d9d9', padding: 16, borderRadius: 4 }}>
                        <Row gutter={16}>
                          <Col span={12}>
                            <Form.Item
                              {...restField}
                              name={[name, 'vigenciaDias']}
                              label="Vigencia (días)"
                              initialValue={30}
                            >
                              <Input type="number" />
                            </Form.Item>
                          </Col>
                          <Col span={12}>
                            <Form.Item
                              {...restField}
                              name={[name, 'observaciones']}
                              label="Observaciones"
                            >
                              <Input />
                            </Form.Item>
                          </Col>
                        </Row>

                        <Form.List name={[name, 'detalles']}>
                          {(detalleFields, { add: addDetalle, remove: removeDetalle }) => (
                            <>
                              {detalleFields.map(({ key: dKey, name: dName, ...dRest }) => (
                                <Row key={dKey} gutter={16} align="middle">
                                  <Col span={8}>
                                    <Form.Item
                                      {...dRest}
                                      name={[dName, 'medicamentoId']}
                                      label="Medicamento ID"
                                      rules={[{ required: true, message: 'Ingrese ID del medicamento' }]}
                                    >
                                      <Input />
                                    </Form.Item>
                                  </Col>
                                  <Col span={4}>
                                    <Form.Item
                                      {...dRest}
                                      name={[dName, 'cantidad']}
                                      label="Cantidad"
                                      rules={[{ required: true, message: 'Ingrese cantidad' }]}
                                    >
                                      <Input type="number" />
                                    </Form.Item>
                                  </Col>
                                  <Col span={8}>
                                    <Form.Item
                                      {...dRest}
                                      name={[dName, 'dosificacion']}
                                      label="Dosificación"
                                    >
                                      <Input />
                                    </Form.Item>
                                  </Col>
                                  <Col span={4}>
                                    <Button danger onClick={() => removeDetalle(dName)}>
                                      Eliminar
                                    </Button>
                                  </Col>
                                </Row>
                              ))}
                              <Button type="dashed" onClick={() => addDetalle()} block style={{ marginBottom: 8 }}>
                                Agregar Detalle
                              </Button>
                            </>
                          )}
                        </Form.List>

                        <Button danger onClick={() => remove(name)} style={{ marginTop: 8 }}>
                          Eliminar Receta
                        </Button>
                      </div>
                    ))}
                    <Button type="dashed" onClick={() => add()} block>
                      Agregar Receta
                    </Button>
                  </>
                )}
              </Form.List>
            </>
          )}

          <Form.Item style={{ textAlign: 'right' }}>
            <Space>
              <Button danger onClick={() => setShowHistoriaClinica(true)} disabled={showHistoriaClinica}>
                Cargar Historia Clínica
              </Button>
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