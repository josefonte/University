import React, { useState, useEffect } from "react";
import { useLocation, Link } from "react-router-dom";

import {
  CalendarTwoTone,
  FileTextTwoTone,
  ProfileTwoTone,
  BellOutlined,
  UserOutlined,
} from "@ant-design/icons";

import { Avatar, Space, Badge, Menu, theme } from "antd";

const items = [
  {
    label: "Agenda",
    key: "agenda",
    icon: <CalendarTwoTone />,
    path: "/agenda",
  },
  {
    label: "Provas Ativas",
    key: "ativas",
    icon: <FileTextTwoTone />,
    path: "/provas-ativas",
  },
  {
    label: "Provas Concluídas",
    key: "concluidas",
    icon: <ProfileTwoTone />,
    path: "/provas-concluidas",
  },
];

export function AppHeader() {
  const [current, setCurrent] = useState("");
  const location = useLocation();
  const currentPath = location.pathname;

  console.log(currentPath);

  useEffect(() => {
    if (currentPath === "/") {
      setCurrent("");
    } else if (currentPath === "/agenda") {
      setCurrent("agenda");
    } else if (currentPath === "/provas-ativas") {
      setCurrent("ativas");
    } else if (currentPath === "/provas-concluidas") {
      setCurrent("concluidas");
    }
  }, [currentPath]);

  const onClick = (e) => {
    setCurrent(e.key);
  };
  const {
    token: { colorBgContainer },
  } = theme.useToken();

  return (
    <div
      style={{
        alignItems: "flex-start",
        justifyContent: "space-between",
        display: "flex",
        flexDirection: "row",
        background: colorBgContainer,
      }}
    >
      <div className="logo" style={{ maxWidth: "90px", paddingLeft: "10px" }}>
        <Link to={"/"}>
          <h2 style={{ color: "black", margin: "0 0 0 0", cursor: "pointer" }}>
            ProbUM
          </h2>
        </Link>
      </div>

      <Menu
        style={{ minWidth: "450px" }}
        mode="horizontal"
        onClick={onClick}
        selectedKeys={[current]}
      >
        {items.map((item) => (
          <Menu.Item key={item.key} icon={item.icon}>
            <Link to={item.path}>{item.label}</Link>
          </Menu.Item>
        ))}
      </Menu>

      <div className="logo" style={{ paddingRight: "10px" }}>
        <Space direction="horizontal" wrap size={16}>
          <Badge count={1}>
            <BellOutlined style={{ fontSize: "18px", paddingTop: "2px" }} />
          </Badge>

          <Avatar size="small" icon={<UserOutlined />} />
          <Link to={"/login"}>
            <p style={{ margin: "0 0 0 0" }}> Paulo Flores(d458)</p>
          </Link>
        </Space>
      </div>
    </div>
  );
}
