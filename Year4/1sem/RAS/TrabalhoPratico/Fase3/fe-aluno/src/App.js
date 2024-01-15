import React from "react";
import { Route, Routes, BrowserRouter as Router } from "react-router-dom";
import AppAgenda from "./pages/agenda";
import AppProvasAtivas from "./pages/provasAtivas";
import AppProvasConcluidas from "./pages/provasConcluidas";
import AppHome from "./pages/home";
import AppStartProva from "./pages/startProva";
import AppConsultarProva from "./pages/consultarProva";
import AppResolverProva from "./pages/resolverProva";
import AppLogin from "./pages/login";

function App() {
  const id_aluno = "1";

  return (
    <>
      <Router>
        <Routes>
          <Route path="/" element={<AppHome />} />
          <Route path="/agenda" element={<AppAgenda />} />
          <Route
            path="/provas-ativas"
            element={<AppProvasAtivas idAluno={id_aluno} />}
          />
          <Route
            path="/provas-ativas/:idProva"
            element={<AppStartProva idAluno={id_aluno} />}
          />
          <Route path="/provas-concluidas"
            element={<AppProvasConcluidas idAluno={id_aluno} />}
          />
          <Route
            path="/provas-concluidas/:id"
            element={<AppConsultarProva idAluno={id_aluno} />}
          />
          <Route
            path="/provas-ativas/resolver-prova/:idProva"
            element={<AppResolverProva idAluno={id_aluno} />}
          />
          <Route path="/login" element={<AppLogin />} />
        </Routes>
      </Router>
    </>
  );
}

export default App;
