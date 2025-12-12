import React, { useState, useEffect } from 'react';
import './App.css';
import { startGame, validateWord } from './services/api';
import InstructionsModal from './components/InstructionsModal';
import GameControls from './components/GameControls';
import GameStatus from './components/GameStatus';
import GameBoard from './components/GameBoard';
import WordList from './components/WordList';

function App() {
  const [board, setBoard] = useState([]);
  const [targetWords, setTargetWords] = useState([]);
  const [foundWords, setFoundWords] = useState([]);
  const [foundCells, setFoundCells] = useState(new Set()); // Strings "r-c"
  const [selectionStart, setSelectionStart] = useState(null); // {r, c}
  const [message, setMessage] = useState({ text: "Presiona Iniciar para Jugar", type: "info" });
  const [gameActive, setGameActive] = useState(false);
  const [startTime, setStartTime] = useState(null);
  const [elapsedTime, setElapsedTime] = useState(0); // Seconds
  const [showInstructions, setShowInstructions] = useState(true);
  const [theme, setTheme] = useState('auto'); // 'light', 'dark', 'auto'

  // Theme effect
  useEffect(() => {
    const applyTheme = () => {
      let isDark = false;
      if (theme === 'auto') {
        isDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
      } else {
        isDark = theme === 'dark';
      }
      
      if (isDark) {
        document.documentElement.classList.add('dark');
      } else {
        document.documentElement.classList.remove('dark');
      }
    };

    applyTheme();

    // Listener for system changes when in auto mode
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
    const handleChange = () => {
        if (theme === 'auto') applyTheme();
    };
    
    mediaQuery.addEventListener('change', handleChange);
    return () => mediaQuery.removeEventListener('change', handleChange);
  }, [theme]);

  useEffect(() => {
    let interval = null;
    if (gameActive && startTime) {
      interval = setInterval(() => {
        setElapsedTime(Math.floor((Date.now() - startTime) / 1000));
      }, 1000);
    } else if (!gameActive) {
      if (interval) clearInterval(interval);
    }
    return () => {
      if (interval) clearInterval(interval);
    };
  }, [gameActive, startTime]);

  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const handleStart = async () => {
    setMessage({ text: "Cargando...", type: "info" });
    const data = await startGame();
    if (data.status === 'ok') {
      setBoard(data.board.map(row => row.split('')));
      setTargetWords(data.words);
      setFoundWords([]);
      setFoundCells(new Set());
      setSelectionStart(null);
      setGameActive(true);
      setStartTime(Date.now());
      setElapsedTime(0);
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
         processFoundWord(formedWord, path);
      } else {
         // Try reverse
         const reverseWord = formedWord.split('').reverse().join('');
         const resReverse = await validateWord(reverseWord);
         if (resReverse.status === 'valid') {
             processFoundWord(reverseWord, path); // Pass same path, order doesn't matter for highlighting
         } else {
             setMessage({ text: `Palabra incorrecta: ${formedWord}`, type: "error" });
         }
      }
      
      setSelectionStart(null);
    }
  };

  const processFoundWord = (word, path) => {
      if (!foundWords.includes(word)) {
           const newFound = [...foundWords, word];
           setFoundWords(newFound);
           
           // Update found cells
           const newFoundCells = new Set(foundCells);
           path.forEach(cell => {
               newFoundCells.add(`${cell.r}-${cell.c}`);
           });
           setFoundCells(newFoundCells);

           setMessage({ text: `¡Encontrado: ${word}!`, type: "success" });
           
           if (newFound.length === targetWords.length) {
               const totalSeconds = Math.floor((Date.now() - startTime) / 1000);
               setMessage({ text: `¡Juego Terminado! Tiempo: ${formatTime(totalSeconds)}`, type: "success" });
               setGameActive(false);
           }
       } else {
           setMessage({ text: `Ya encontrado: ${word}`, type: "warning" });
       }
  };

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
      return false;
  };

  return (
    <div className="min-h-screen bg-gray-100 dark:bg-gray-900 text-gray-900 dark:text-gray-100 flex flex-col items-center justify-center p-8 transition-colors duration-300">
       <InstructionsModal 
           show={showInstructions} 
           onClose={() => setShowInstructions(false)} 
       />

       <h1 className="text-5xl font-bold mb-8 tracking-wider">Sopa de Letras</h1>
       
       <GameControls 
           gameActive={gameActive}
           onStart={handleStart}
           theme={theme}
           onThemeChange={setTheme}
           elapsedTime={elapsedTime}
           formatTime={formatTime}
           foundWordsCount={foundWords.length}
           targetWordsCount={targetWords.length}
       />

       {board.length > 0 && (
          <>
             <GameStatus message={message} />
             
             <div className="flex flex-wrap justify-center gap-8 items-start w-full max-w-6xl">
                 <GameBoard 
                     board={board}
                     isSelected={isSelected}
                     foundCells={foundCells}
                     onCellClick={handleCellClick}
                 />
                 
                 <WordList 
                     targetWords={targetWords} 
                     foundWords={foundWords} 
                 />
             </div>
          </>
       )}
    </div>
  );
}

export default App;
