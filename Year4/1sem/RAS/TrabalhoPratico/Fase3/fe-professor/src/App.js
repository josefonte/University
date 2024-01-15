import React, { useState } from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import './App.css';
import Navbar from './components/Navbar';
import CreateExam from './components/CreateExam';
import CorrectExam from './components/CorrectExam';
import CreateQuestions from './components/CreateQuestions';
import ExamList  from './components/ExamList';
import ChoicePage from './components/ChoicePage';
import Login from './components/Login';
function App() {
  const [exams, setExams] = useState([]);

  const addExam = (newExam) => {
    setExams([...exams, newExam]);
  };

  const id_docente = "d123"

  return (
    <div className="wrapper">
      <BrowserRouter>
        <Navbar />

        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/exam-list" element={<ExamList idDocente={id_docente}/>} />
          <Route path="/choice-page/:idProva" element={<ChoicePage idDocente={id_docente}/>} />
          <Route path="/create-exam" element={<CreateExam idDocente={id_docente} />} />
          <Route path="/create-questions/:idProva" element={<CreateQuestions idDocente={id_docente}/>} />
          <Route path="/correct-exam" element={<CorrectExam />} />  
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
