import React from "react";

import { Layout, theme, Row, Button, Checkbox, Form, Input } from "antd";
import { Footer } from "antd/es/layout/layout";
import { Link } from "react-router-dom";

const { Content } = Layout;

const onFinish = (values) => {
  console.log("Success:", values);
};
const onFinishFailed = (errorInfo) => {
  console.log("Failed:", errorInfo);
};

function AppLogin() {
  const {
    token: { colorBgContainer },
  } = theme.useToken();

  return (
    <Layout>
      <Content
        style={{
          padding: "20px 20%",
          minHeight: "calc(100vh - 64px)",
        }}
      >
        <Row justify={"center"}>
          <Link to={"/"}>
            <h1 style={{ color: "black" }}>ProbUM</h1>
          </Link>
        </Row>

        <Row justify={"center"}>
          <Form
            name="basic"
            labelCol={{
              span: 8,
            }}
            wrapperCol={{
              span: 16,
            }}
            style={{
              maxWidth: 700,
              minWidth: 550,
            }}
            initialValues={{
              remember: true,
            }}
            onFinish={onFinish}
            onFinishFailed={onFinishFailed}
            autoComplete="off"
          >
            <Form.Item
              label="Username"
              name="username"
              rules={[
                {
                  required: true,
                  message: "Please input your username!",
                },
              ]}
            >
              <Input />
            </Form.Item>

            <Form.Item
              label="Password"
              name="password"
              rules={[
                {
                  required: true,
                  message: "Please input your password!",
                },
              ]}
            >
              <Input.Password />
            </Form.Item>

            <Form.Item
              name="remember"
              valuePropName="checked"
              wrapperCol={{
                offset: 8,
                span: 16,
              }}
            >
              <Checkbox>Remember me</Checkbox>
            </Form.Item>

            <Form.Item
              wrapperCol={{
                offset: 8,
                span: 16,
              }}
            >
              <Button type="primary" htmlType="submit">
                Entrar
              </Button>
            </Form.Item>
          </Form>
        </Row>
      </Content>
      <Footer style={{ textAlign: "center", fontWeight: "500" }}>
        Requisitos de Arquiteturas de Software ©2023 Desenvolvido por Grupo 8B
      </Footer>
    </Layout>
  );
}

export default AppLogin;