import React, { useState, useEffect } from 'react';
import './App.css';
import { startGame, validateWord } from './services/api';

function App() {
  const [board, setBoard] = useState([]);
  const [targetWords, setTargetWords] = useState([]);
  const [foundWords, setFoundWords] = useState([]);
  const [selectedCells, setSelectedCells] = useState([]);
  const [message, setMessage] = useState("Press Start to Play");
  const [gameActive, setGameActive] = useState(false);
  const [startTime, setStartTime] = useState(null);

  const handleStart = async () => {
    setMessage("Loading...");
    const data = await startGame();
    if (data.status === 'ok') {
      setBoard(data.board.map(row => row.split('')));
      setTargetWords(data.words);
      setFoundWords([]);
      setSelectedCells([]);
      setGameActive(true);
      setStartTime(Date.now());
      setMessage("Find the words!");
    } else {
      setMessage("Error starting game: " + data.message);
    }
  };

  const handleCellClick = (rowIndex, colIndex) => {
    if (!gameActive) return;

    const cellId = `${rowIndex}-${colIndex}`;
    const isSelected = selectedCells.some(c => c.id === cellId);

    if (isSelected) {
      setSelectedCells(selectedCells.filter(c => c.id !== cellId));
    } else {
      setSelectedCells([...selectedCells, { id: cellId, r: rowIndex, c: colIndex, letter: board[rowIndex][colIndex] }]);
    }
  };

  const checkSelection = async () => {
    if (selectedCells.length === 0) return;

    // Sort by position to form word
    // Simple logic: check if they are linear (same row, col or diagonal)
    // For now, let's just concatenate letters in selection order or sorted order?
    // User might select indiscriminately. Let's try to form a word from selection.
    // Ideally, we enforce selection rules (dragging).
    // Here we simplified: Just take selected letters and see if they form a word in any permutation?
    // No, standard is distinct selection.
    
    // Let's assume the user selects letters in order OR we sort them if they are in line.
    // For simplicity in this demo: User must select contiguous block.
    // We will just send the string formed by the selection order.
    
    const formedWord = selectedCells.map(c => c.letter).join("");
    const result = await validateWord(formedWord);

    if (result.status === 'valid') {
       if (!foundWords.includes(formedWord)) {
           setFoundWords([...foundWords, formedWord]);
           setMessage(`Found: ${formedWord}!`);
           if (foundWords.length + 1 === targetWords.length) {
               const timeTaken = ((Date.now() - startTime) / 1000).toFixed(2);
               setMessage(`Game Over! Time: ${timeTaken}s`);
               setGameActive(false);
           }
       } else {
           setMessage(`Already found: ${formedWord}`);
       }
       setSelectedCells([]);
    } else {
       // Try reverse
       const reverseWord = formedWord.split('').reverse().join('');
       const resReverse = await validateWord(reverseWord);
       if (resReverse.status === 'valid') {
          if (!foundWords.includes(reverseWord)) {
              setFoundWords([...foundWords, reverseWord]);
              setMessage(`Found: ${reverseWord}!`);
              if (foundWords.length + 1 === targetWords.length) {
                  const timeTaken = ((Date.now() - startTime) / 1000).toFixed(2);
                  setMessage(`Game Over! Time: ${timeTaken}s`);
                  setGameActive(false);
              }
          }
           setSelectedCells([]);
       } else {
           setMessage(`Invalid word: ${formedWord}`);
       }
    }
  };
  
  const isCellDefined = (r, c) => {
      // check if part of found word? 
      // Need refined logic to map found words back to cells.
      return false;
  }

  return (
    <div className="App">
      <h1>Sopa de Letras</h1>
      <div className="controls">
          <button onClick={handleStart} disabled={gameActive && foundWords.length < targetWords.length}>
              {gameActive ? "Restart" : "Start Game"}
          </button>
          <button onClick={checkSelection} disabled={!gameActive || selectedCells.length === 0}>
             Validate Selection
          </button>
          <span className="message">{message}</span>
      </div>
      
      <div className="game-container">
          <div className="board">
              {board.map((row, rIndex) => (
                  <div key={rIndex} className="row">
                      {row.map((letter, cIndex) => (
                          <div 
                              key={`${rIndex}-${cIndex}`} 
                              className={`cell 
                                  ${selectedCells.some(c => c.id === `${rIndex}-${cIndex}`) ? 'selected' : ''}
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
              <h3>Words to Find</h3>
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
