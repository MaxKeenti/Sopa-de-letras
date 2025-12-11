import React, { useState, useEffect } from 'react';
import './App.css';
import { startGame, validateWord } from './services/api';

function App() {
  const [board, setBoard] = useState([]);
  const [targetWords, setTargetWords] = useState([]);
  const [foundWords, setFoundWords] = useState([]);
  const [selectionStart, setSelectionStart] = useState(null); // {r, c}
  const [message, setMessage] = useState({ text: "Presiona Iniciar para Jugar", type: "info" });
  const [gameActive, setGameActive] = useState(false);
  const [startTime, setStartTime] = useState(null);
  const [showInstructions, setShowInstructions] = useState(true);

  const handleStart = async () => {
    setMessage({ text: "Cargando...", type: "info" });
    const data = await startGame();
    if (data.status === 'ok') {
      setBoard(data.board.map(row => row.split('')));
      setTargetWords(data.words);
      setFoundWords([]);
      setSelectionStart(null);
      setGameActive(true);
      setStartTime(Date.now());
      setMessage({ text: "¡Encuentra las palabras!", type: "info" });
    } else {
      setMessage({ text: "Error al iniciar el juego: " + data.message, type: "error" });
    }
  };

  const handleCellClick = async (rowIndex, colIndex) => {
    if (!gameActive) return;

    if (!selectionStart) {
      // First click: Select Start
      setSelectionStart({ r: rowIndex, c: colIndex });
      setMessage({ text: "Selecciona el final de la palabra", type: "info" });
    } else {
      // Second click: Select End and Validate
      const start = selectionStart;
      const end = { r: rowIndex, c: colIndex };
      
      // Calculate path
      const path = getLinePath(start, end);
      
      if (path.length === 0) {
        setMessage({ text: "Selección inválida: Debe ser una línea recta o diagonal", type: "warning" });
        setSelectionStart(null);
        return;
      }

      // Form word
      const formedWord = path.map(cell => board[cell.r][cell.c]).join("");
      
      // Validate
      const result = await validateWord(formedWord);
      
      if (result.status === 'valid') {
         processFoundWord(formedWord);
      } else {
         // Try reverse
         const reverseWord = formedWord.split('').reverse().join('');
         const resReverse = await validateWord(reverseWord);
         if (resReverse.status === 'valid') {
             processFoundWord(reverseWord);
         } else {
             setMessage({ text: `Palabra incorrecta: ${formedWord}`, type: "error" });
         }
      }
      
      setSelectionStart(null);
    }
  };

  const procesFoundWord = (word) => {
      if (!foundWords.includes(word)) {
           const newFound = [...foundWords, word];
           setFoundWords(newFound);
           setMessage({ text: `¡Encontrado: ${word}!`, type: "success" });
           
           if (newFound.length === targetWords.length) {
               const timeTaken = ((Date.now() - startTime) / 1000).toFixed(2);
               setMessage({ text: `¡Juego Terminado! Tiempo: ${timeTaken}s`, type: "success" });
               setGameActive(false);
           }
       } else {
           setMessage({ text: `Ya encontrado: ${word}`, type: "warning" });
       }
  };
  
  // Fix typo in function name call above or rename function
  const processFoundWord = procesFoundWord; 

  // Bresenham-like line algorithm or simple slope check
  const getLinePath = (start, end) => {
      const path = [];
      const dr = end.r - start.r;
      const dc = end.c - start.c;
      
      if (dr === 0 && dc === 0) return [start]; // Same cell?

      // Check if horizontal, vertical, or diagonal
      if (dr === 0 || dc === 0 || Math.abs(dr) === Math.abs(dc)) {
          const steps = Math.max(Math.abs(dr), Math.abs(dc));
          const rStep = dr / steps;
          const cStep = dc / steps;
          
          for (let i = 0; i <= steps; i++) {
              path.push({ r: start.r + i * rStep, c: start.c + i * cStep });
          }
          return path;
      }
      
      return []; // Invalid
  };

  const isSelected = (r, c) => {
      if (selectionStart && selectionStart.r === r && selectionStart.c === c) return true;
      // Note: We don't visualize the path dynamically on hover yet, only start point.
      // If we wanted to, we'd need tracking hover state.
      return false;
  };

  return (
    <div className="App">
       {showInstructions && (
          <div className="modal-overlay">
              <div className="modal-content">
                  <h2>Cómo Jugar</h2>
                  <ul>
                      <li>Presiona <strong>Iniciar Juego</strong> para comenzar.</li>
                      <li>Busca las palabras listadas en la sopa de letras.</li>
                      <li>Para seleccionar una palabra:
                          <ol>
                              <li>Haz click en la <strong>primera letra</strong>.</li>
                              <li>Haz click en la <strong>última letra</strong>.</li>
                          </ol>
                      </li>
                      <li>La selección debe ser horizontal, vertical o diagonal.</li>
                  </ul>
                  <button className="modal-button" onClick={() => setShowInstructions(false)}>¡Entendido!</button>
              </div>
          </div>
      )}

      <h1>Sopa de Letras</h1>
      
      <div className="controls">
          <button onClick={handleStart} disabled={gameActive && foundWords.length < targetWords.length}>
              {gameActive ? "Reiniciar" : "Iniciar Juego"}
          </button>
          {/* Removed Validate Button */}
      </div>

      <div className={`message-banner ${message.type}`}>
          {message.text}
      </div>
      
      <div className="game-container">
          <div className="board">
              {board.map((row, rIndex) => (
                  <div key={rIndex} className="row">
                      {row.map((letter, cIndex) => (
                          <div 
                              key={`${rIndex}-${cIndex}`} 
                              className={`cell 
                                  ${isSelected(rIndex, cIndex) ? 'selected-start' : ''}
                                  `}
                              onClick={() => handleCellClick(rIndex, cIndex)}
                          >
                              {letter}
                          </div>
                      ))}
                  </div>
              ))}
          </div>
          
          <div className="word-list">
              <h3>Palabras a Encontrar</h3>
              <ul>
                  {targetWords.map((word, idx) => (
                      <li key={idx} className={foundWords.includes(word) ? 'found' : ''}>
                          {word}
                      </li>
                  ))}
              </ul>
          </div>
      </div>
    </div>
  );
}

export default App;
