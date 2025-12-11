import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';

const Board = ({ boardString, onFinish, onMessage }) => {
    const [currentWord, setCurrentWord] = useState('');
    const [selectedIndices, setSelectedIndices] = useState([]); // Visual selection
    const [foundIndices, setFoundIndices] = useState([]);
    const [startTime] = useState(Date.now());
    const [currentTime, setCurrentTime] = useState(0);

    // Parse board string into 15x15
    const grid = [];
    if (boardString) {
        for(let i=0; i<15; i++) {
            const row = [];
            for(let j=0; j<15; j++) {
                row.push(boardString[i*15 + j]);
            }
            grid.push(row);
        }
    }

    useEffect(() => {
        const timer = setInterval(() => {
            setCurrentTime(Math.floor((Date.now() - startTime) / 1000));
        }, 1000);
        return () => clearInterval(timer);
    }, [startTime]);

    const submitWord = useCallback(async (wordToSubmit) => {
        if (!wordToSubmit) return;
        
        try {
            const res = await axios.post('/api/game/validate', { word: wordToSubmit });
            if (res.data.valid) {
                 // Note: With loose selection, we might not have a perfect path to highlight 'found', 
                 // but we can at least add the currently selected indices if they match the word length 
                 // and are consistent. For now, we'll rely on the word message.
                 // Ideally the server returns the indices of the found word to highlight them permanently.
                 // Since we don't have that, we will just keep the current selection as 'found' 
                 // if it matches the length, or just rely on the 'found' message.
                 // For this implementation, let's add current selectedIndices to foundIndices.
                 setFoundIndices(prev => [...prev, ...selectedIndices]);
                 onMessage(`Found: ${wordToSubmit}!`);
            } else {
                 onMessage('Invalid word.');
            }
        } catch(e) { console.error(e); }
        
        // Reset current attempt
        setCurrentWord('');
        setSelectedIndices([]);
    }, [selectedIndices, onMessage]);

    const handleCellClick = (r, c) => {
        const idx = r * 15 + c;
        const char = boardString[idx];
        
        setCurrentWord(prev => prev + char);
        setSelectedIndices(prev => [...prev, idx]);
    };

    // Keyboard handling
    useEffect(() => {
        const handleKeyDown = (e) => {
            // Check for Ctrl/Cmd + Key (Submit current + Start new)
            if ((e.ctrlKey || e.metaKey) && e.key.length === 1 && /[a-zA-Z]/.test(e.key)) {
                e.preventDefault();
                // Submit current word FIRST
                if (currentWord.length > 0) {
                     submitWord(currentWord);
                }
                // Then start new word with the pressed key
                const char = e.key.toUpperCase();
                setCurrentWord(char);
                setSelectedIndices([]); // We don't have an index for keyboard input unless we search the grid, so clear selection
                return;
            }

            // Enter to submit
            if (e.key === 'Enter') {
                submitWord(currentWord);
                return;
            }

            // Backspace
            if (e.key === 'Backspace') {
                setCurrentWord(prev => prev.slice(0, -1));
                setSelectedIndices(prev => prev.slice(0, -1));
                return;
            }

            // Escape
            if (e.key === 'Escape') {
                setCurrentWord('');
                setSelectedIndices([]);
                return;
            }

            // Regular typing (A-Z)
            if (e.key.length === 1 && /[a-zA-Z]/.test(e.key) && !e.ctrlKey && !e.metaKey && !e.altKey) {
                const char = e.key.toUpperCase();
                setCurrentWord(prev => prev + char);
                // No index associated with pure typing
            }
        };

        window.addEventListener('keydown', handleKeyDown);
        return () => window.removeEventListener('keydown', handleKeyDown);
    }, [currentWord, submitWord]);


    // Formatting time
    const formatTime = (s) => {
        const min = Math.floor(s / 60).toString().padStart(2,'0');
        const sec = (s % 60).toString().padStart(2,'0');
        return `${min}:${sec}`;
    };

    return (
        <div className="flex flex-col items-center">
            <div className="mb-4 text-xl font-mono bg-white px-4 py-2 rounded shadow flex justify-between w-full max-w-lg">
                <span>Time: {formatTime(currentTime)}</span>
                <span className="font-bold text-blue-600 tracking-widest">{currentWord}</span>
            </div>

            <div 
                className="grid gap-1 bg-white p-2 rounded shadow-lg select-none"
                style={{ gridTemplateColumns: 'repeat(15, minmax(0, 1fr))' }}
            >
                {grid.map((row, r) => (
                    row.map((char, c) => {
                        const idx = r * 15 + c;
                        // Determine if this specific instance is part of the current selection flow
                        // This is tricky if indices are repeated, but for visual feedback simple inclusion is okay
                        const isSelected = selectedIndices.includes(idx);
                        
                        // We can also try to highlight only the *latest* click if we wanted, but simple valid path is fine.
                        const isFound = foundIndices.includes(idx); 
                        
                        return (
                            <div 
                                key={idx}
                                onClick={() => handleCellClick(r, c)}
                                className={`grid-cell cursor-pointer flex items-center justify-center w-8 h-8 border border-gray-200 text-lg font-bold
                                    ${isSelected ? 'bg-yellow-200 text-yellow-800' : 'hover:bg-gray-100'} 
                                    ${isFound ? 'bg-green-300 text-green-900' : ''}
                                `}
                            >
                                {char}
                            </div>
                        );
                    })
                ))}
            </div>
            
            <div className="flex gap-4 mt-6">
                 <button 
                    onClick={() => {
                        setCurrentWord('');
                        setSelectedIndices([]);
                    }}
                    className="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded"
                >
                    Clear
                </button>
                <button 
                    onClick={() => submitWord(currentWord)}
                    className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
                >
                    Submit Word
                </button>
                <button 
                    onClick={() => onFinish(formatTime(currentTime))}
                    className="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded"
                >
                    Finish Game
                </button>
            </div>
            
            <div className="mt-4 text-sm text-gray-600">
                <p>Controls: Type or Click letters. <b>Enter</b> to submit. <b>Ctrl+Key</b> to submit & start new word.</p>
            </div>
        </div>
    );
};

export default Board;

