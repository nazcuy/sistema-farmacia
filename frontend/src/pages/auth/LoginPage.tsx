import React, { useState } from "react";
import { Form, Input, Button, Card, message, Typography } from "antd";
import { UserOutlined, LockOutlined } from "@ant-design/icons";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../contextos/AuthContext";
import type { AuthUser } from "../../contextos/AuthContext";
import { authApi } from "../../servicios/api";

const { Title } = Typography;

const LoginPage = () => {
    const [form] = Form.useForm();
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();
    const { login } = useAuth();

    const handleLogin = async (values: { email: string; password: string }) => {
        setLoading(true);
        try {
            const data = await authApi.login(values.email, values.password);
            // data contiene: { token, id, email, nombre, rol }
            const user = {
                id: data.id,
                email: data.email,
                rol: data.rol,
            };
            login(user, data.token);
            navigate("/");
        } catch (error) {
            message.error(
                "Falló el inicio de sesión. Por favor, verifica tus credenciales.",
            );
        } finally {
            setLoading(false);
        }
    };

    return (
        <Card>
            <Form form={form} onFinish={handleLogin}>
                <Form.Item
                    name="email"
                    rules={[{ required: true, message: "Por favor, ingrese su email" }]}
                >
                    <Input placeholder="Email" />
                </Form.Item>
                <Form.Item
                    name="password"
                    rules={[
                        { required: true, message: "Por favor, ingrese su contraseña" },
                    ]}
                >
                    <Input.Password placeholder="Password" />
                </Form.Item>
                <Button type="primary" htmlType="submit" loading={loading}>
                    Login
                </Button>
            </Form>
            <Title level={3}>¡Bienvenido al sistema sanitario!</Title>
        </Card>
    );
};

export default LoginPage;
