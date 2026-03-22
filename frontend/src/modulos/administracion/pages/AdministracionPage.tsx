import React, { useState, useEffect } from "react";
import {
    Card,
    Table,
    Button,
    Modal,
    Form,
    Input,
    Select,
    Switch,
    Space,
    message,
} from "antd";
import { PlusOutlined, EditOutlined } from "@ant-design/icons";
import { authApi } from "../../../servicios/api";

type UsuarioAdmin = {
    id: number;
    email: string;
    nombre: string;
    apellido: string;
    rol: string;
    activo: boolean;
};

const roleOptions = [
    { value: "ADMINISTRADOR", label: "Administrador" },
    { value: "FARMACEUTICO", label: "Farmacéutico" },
    { value: "MEDICO", label: "Médico" },
];

const AdministracionPage: React.FC = () => {
    const [usuarios, setUsuarios] = useState<UsuarioAdmin[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [modalVisible, setModalVisible] = useState<boolean>(false);
    const [editingUsuario, setEditingUsuario] = useState<UsuarioAdmin | null>(
        null,
    );
    const [form] = Form.useForm();

    const cargarUsuarios = async () => {
        setLoading(true);
        try {
            const response = await authApi.getUsuarios();
            setUsuarios(response);
        } catch (error) {
            message.error("Error al cargar usuarios");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        cargarUsuarios();
    }, []);

    const abrirModalNuevo = () => {
        setEditingUsuario(null);
        form.resetFields();
        setModalVisible(true);
    };

    const editarUsuario = (usuario: UsuarioAdmin) => {
        setEditingUsuario(usuario);
        form.setFieldsValue({
            email: usuario.email,
            nombre: usuario.nombre,
            apellido: usuario.apellido,
            rol: usuario.rol,
            activo: usuario.activo,
            password: undefined,
        });
        setModalVisible(true);
    };

    const handleSubmit = async (values: any) => {
        try {
            if (editingUsuario) {
                await authApi.actualizarUsuario(editingUsuario.id, {
                    nombre: values.nombre,
                    apellido: values.apellido,
                    rol: values.rol,
                    activo: values.activo,
                });
                message.success("Usuario actualizado correctamente");
            } else {
                await authApi.registro({
                    email: values.email,
                    password: values.password,
                    nombre: values.nombre,
                    apellido: values.apellido,
                    rol: values.rol,
                });
                message.success("Usuario creado correctamente");
            }
            setModalVisible(false);
            cargarUsuarios();
        } catch (error: any) {
            console.error(error);
            message.error(error?.response?.data?.error || "Error guardando usuario");
        }
    };

    const columns = [
        { title: "ID", dataIndex: "id", key: "id", width: 70 },
        { title: "Email", dataIndex: "email", key: "email" },
        { title: "Nombre", dataIndex: "nombre", key: "nombre" },
        { title: "Apellido", dataIndex: "apellido", key: "apellido" },
        { title: "Rol", dataIndex: "rol", key: "rol" },
        {
            title: "Activo",
            dataIndex: "activo",
            key: "activo",
            render: (activo: boolean) => (activo ? "Sí" : "No"),
        },
        {
            title: "Acciones",
            key: "acciones",
            render: (_: any, record: UsuarioAdmin) => (
                <Space>
                    <Button icon={<EditOutlined />} onClick={() => editarUsuario(record)}>
                        Editar
                    </Button>
                </Space>
            ),
        },
    ];

    return (
        <div>
            <div
                style={{
                    display: "flex",
                    justifyContent: "space-between",
                    marginBottom: 16,
                }}
            >
                <h2 style={{ margin: 0 }}>Administración de Usuarios</h2>
                <Button
                    type="primary"
                    icon={<PlusOutlined />}
                    onClick={abrirModalNuevo}
                >
                    Nuevo Usuario
                </Button>
            </div>

            <Card>
                <Table
                    dataSource={usuarios}
                    columns={columns}
                    loading={loading}
                    rowKey="id"
                    pagination={{ pageSize: 10 }}
                />
            </Card>

            <Modal
                title={editingUsuario ? "Editar Usuario" : "Nuevo Usuario"}
                open={modalVisible}
                onCancel={() => setModalVisible(false)}
                footer={null}
            >
                <Form layout="vertical" form={form} onFinish={handleSubmit}>
                    <Form.Item
                        name="email"
                        label="Email"
                        rules={[
                            { required: true, message: "Ingrese email válido" },
                            { type: "email" },
                        ]}
                    >
                        <Input disabled={!!editingUsuario} />
                    </Form.Item>
                    {!editingUsuario && (
                        <Form.Item
                            name="password"
                            label="Contraseña"
                            rules={[
                                { required: true, message: "Ingrese contraseña" },
                                { min: 6, message: "Mínimo 6 caracteres" },
                            ]}
                        >
                            <Input.Password />
                        </Form.Item>
                    )}

                    <Form.Item
                        name="nombre"
                        label="Nombre"
                        rules={[{ required: true, message: "Ingrese nombre" }]}
                    >
                        <Input />
                    </Form.Item>

                    <Form.Item
                        name="apellido"
                        label="Apellido"
                        rules={[{ required: true, message: "Ingrese apellido" }]}
                    >
                        <Input />
                    </Form.Item>

                    <Form.Item
                        name="rol"
                        label="Rol"
                        rules={[{ required: true, message: "Seleccione rol" }]}
                    >
                        <Select options={roleOptions} />
                    </Form.Item>

                    <Form.Item
                        name="activo"
                        label="Activo"
                        valuePropName="checked"
                        initialValue={true}
                    >
                        <Switch checkedChildren="Sí" unCheckedChildren="No" />
                    </Form.Item>

                    <Form.Item style={{ textAlign: "right" }}>
                        <Space>
                            <Button onClick={() => setModalVisible(false)}>Cancelar</Button>
                            <Button type="primary" htmlType="submit">
                                {editingUsuario ? "Actualizar" : "Crear"}
                            </Button>
                        </Space>
                    </Form.Item>
                </Form>
            </Modal>
        </div>
    );
};

export default AdministracionPage;
