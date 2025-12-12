import React from 'react';

const GameControls = ({ gameActive, onStart, theme, onThemeChange, elapsedTime, formatTime, foundWordsCount, targetWordsCount, playerName, setPlayerName }) => {
    return (
        <div className="flex flex-wrap gap-4 items-center mb-6 w-full max-w-4xl justify-center">
            <div className="flex flex-col">
                <input 
                    type="text" 
                    placeholder="Nombre del Jugador" 
                    className="px-3 py-2 rounded border border-gray-300 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                    value={playerName}
                    onChange={(e) => setPlayerName(e.target.value)}
                    disabled={gameActive}
                />
            </div>

            <button className="btn" onClick={onStart} disabled={!playerName.trim() || (gameActive && foundWordsCount < targetWordsCount)}>
                {gameActive ? "Reiniciar" : "Iniciar Juego"}
            </button>
            
            <div className="flex items-center gap-2">
                <label className="text-sm font-semibold dark:text-gray-300">Tema:</label>
                <select 
                   className="theme-select"
                   value={theme} 
                   onChange={(e) => onThemeChange(e.target.value)}
                >
                    <option value="light">Claro</option>
                    <option value="dark">Oscuro</option>
                    <option value="auto">Auto</option>
                </select>
            </div>

            {gameActive && (
                <div className="px-4 py-2 bg-gray-800 text-yellow-500 font-mono text-xl rounded border border-gray-600">
                    {formatTime(elapsedTime)}
                </div>
            )}
        </div>
    );
};

export default GameControls;
