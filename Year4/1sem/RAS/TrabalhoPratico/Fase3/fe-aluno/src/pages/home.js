import React from "react";

import { AppHeader } from "../components/AppHeader";
import { Layout, theme, Row, Col, Card, Avatar } from "antd";
import { Footer } from "antd/es/layout/layout";
import carlos from "../assets/images/carlos.jpg";
import duarte from "../assets/images/duarte.jpg";
import escudeiro from "../assets/images/escudeiro.jpg";
import francisca from "../assets/images/francisca.jpg";
import joaopedro from "../assets/images/joaopedro.jpg";
import juliana from "../assets/images/juliana.jpg";
import lucena from "../assets/images/lucena.jpg";
import nsimba from "../assets/images/nsimba.jpg";
import picao from "../assets/images/picao.png";
import raposo from "../assets/images/raposo.jpg";
import senra from "../assets/images/senra.jpg";
import ze from "../assets/images/ze.jpg";
const { Meta } = Card;
const { Header, Content } = Layout;

function AppHome() {
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
          minHeight: "calc(100vh - 128px)",
        }}
      >
        <Row justify={"center"}>
          <h1>GRUPO 8B</h1>
        </Row>

        <Row>
          <Col span={8} align="center">
            <Card style={{ marginBottom: "8px", marginRight: "8px" }}>
              <Meta
                avatar={<Avatar src={joaopedro} size={64} />}
                title="João Braga"
                description="PG53951"
              ></Meta>
            </Card>
          </Col>
          <Col span={8} align="center">
            <Card style={{ marginBottom: "8px", marginRight: "8px" }}>
              <Meta
                avatar={<Avatar src={nsimba} size={64} />}
                title="Nsimba Teresa "
                description="PG51636"
              ></Meta>
            </Card>
          </Col>
          <Col span={8} align="center">
            <Card>
              <Meta
                avatar={<Avatar src={duarte} size={64} />}
                title="Duarte Parente"
                description="PG53791"
              ></Meta>
            </Card>
          </Col>
        </Row>
        <Row>
          <Col span={8} align="center">
            <Card style={{ marginBottom: "8px", marginRight: "8px" }}>
              <Meta
                avatar={<Avatar src={juliana} size={64} />}
                title="Juliana Silvério"
                description="PG50006"
              ></Meta>
            </Card>
          </Col>
          <Col span={8} align="center">
            <Card style={{ marginBottom: "8px", marginRight: "8px" }}>
              <Meta
                avatar={<Avatar src={raposo} size={64} />}
                title="Miguel Raposo"
                description="A94942"
              ></Meta>
            </Card>
          </Col>
          <Col span={8} align="center">
            <Card>
              <Meta
                avatar={<Avatar src={ze} size={64} />}
                title="José Fonte"
                description="A91775"
              ></Meta>
            </Card>
          </Col>
        </Row>
        <Row>
          <Col span={8} align="center">
            <Card style={{ marginBottom: "8px", marginRight: "8px" }}>
              <Meta
                avatar={<Avatar src={carlos} size={64} />}
                title="Carlos Machado"
                description="PG52675"
              ></Meta>
            </Card>
          </Col>
          <Col span={8} align="center">
            <Card style={{ marginBottom: "8px", marginRight: "8px" }}>
              <Meta
                avatar={<Avatar src={francisca} size={64} />}
                title="Francisca Lemos"
                description="PG52693"
              ></Meta>
            </Card>
          </Col>
          <Col span={8} align="center">
            <Card>
              <Meta
                avatar={<Avatar src={escudeiro} size={64} />}
                title="Bernardo Escudeiro"
                description="A96075"
              ></Meta>
            </Card>
          </Col>
        </Row>
        <Row>
          <Col span={8} align="center">
            <Card style={{ marginBottom: "8px", marginRight: "8px" }}>
              <Meta
                avatar={<Avatar src={picao} size={64} />}
                title="Rafael Picão"
                description="PG54162"
              ></Meta>
            </Card>
          </Col>
          <Col span={8} align="center">
            <Card style={{ marginBottom: "8px", marginRight: "8px" }}>
              <Meta
                avatar={<Avatar src={lucena} size={64} />}
                title="André Lucena"
                description="PG52672"
              ></Meta>
            </Card>
          </Col>
          <Col span={8} align="center">
            <Card>
              <Meta
                avatar={<Avatar src={senra} size={64} />}
                title="Miguel Senra"
                description="PG54093"
              ></Meta>
            </Card>
          </Col>
        </Row>
      </Content>
      <Footer style={{ textAlign: "center", fontWeight: "500" }}>
        Requisitos de Arquiteturas de Software ©2023 Desenvolvido por Grupo 8B
      </Footer>
    </Layout>
  );
}

export default AppHome;
