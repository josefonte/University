import React from "react";
import { AppHeader } from "../components/AppHeader";
import { Calendar } from "antd";
import { Layout, theme } from "antd";
const { Header, Content } = Layout;

function AppAgenda() {
  const {
    token: { colorBgContainer },
  } = theme.useToken();

  return (
    <Layout>
      <Header
        style={{
          background: colorBgContainer,
        }}
      >
        <AppHeader />
      </Header>

      <Content
        style={{
          padding: "20px 15%",
          minHeight: "calc(100vh - 64px)",
        }}
      >
        <h3 style={{ paddingLeft: "10px" }}>Agenda</h3>

        <Calendar />
      </Content>
    </Layout>
  );
}

export default AppAgenda;
