import React, { useState } from 'react';
import axios from 'axios';
import Board from './components/Board';

function App() {
  const [boardData, setBoardData] = useState(null);
  const [playerName, setPlayerName] = useState('Player1');
  const [isPlaying, setIsPlaying] = useState(false);
  const [message, setMessage] = useState('');

  const startGame = async () => {
    try {
      setMessage('Connecting...');
      const response = await axios.post('/api/game/start', { name: playerName });
      if (response.data.status === 'OK') {
        // Parse board string: CHARS (225) # WORDS
        const chars = response.data.board;
        setBoardData({ chars, words: response.data.hiddenWords });
        setIsPlaying(true);
        setMessage('Game Started! Find the words.');
      } else {
        setMessage('Error starting game.');
      }
    } catch (error) {
      console.error(error);
      setMessage('Failed to connect to server.');
    }
  };

  const handleFinish = async (time) => {
    try {
        await axios.post('/api/game/finish', { name: playerName, time });
        setIsPlaying(false);
        setMessage(`Game Over! Time: ${time}`);
        setBoardData(null);
    } catch (err) {
        console.error(err);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col items-center justify-center p-4">
      <h1 className="text-4xl font-bold mb-8 text-blue-600">Sopa de Letras</h1>
      
      {!isPlaying ? (
        <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
           <div className="mb-4">
             <label className="block text-gray-700 text-sm font-bold mb-2">Player Name</label>
             <input 
                type="text" 
                value={playerName} 
                onChange={(e) => setPlayerName(e.target.value)}
                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
             />
           </div>
           <button 
             onClick={startGame}
             className="w-full bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
           >
             Start Game
           </button>
           <p className="mt-4 text-center text-red-500">{message}</p>
        </div>
      ) : (
        <div className="flex flex-col items-center">
            <Board boardString={boardData.chars} onFinish={handleFinish} onMessage={(msg) => setMessage(msg)} />
            <p className="mt-4 text-xl font-semibold">{message}</p>
        </div>
      )}
    </div>
  );
}

export default App;
