import React from 'react';

const GameBoard = ({ board, isSelected, foundCells, onCellClick }) => {
    return (
        <div className="flex flex-col gap-1 p-3 bg-gray-800 dark:bg-black rounded-lg shadow-2xl overflow-x-auto">
            {board.map((row, rIndex) => (
                <div key={rIndex} className="flex gap-1">
                    {row.map((letter, cIndex) => (
                        <div 
                            key={`${rIndex}-${cIndex}`} 
                            className={`cell 
                                ${isSelected(rIndex, cIndex) ? 'cell-start' : ''}
                                ${foundCells.has(`${rIndex}-${cIndex}`) ? 'found-cell' : ''}
                                `}
                            onClick={() => onCellClick(rIndex, cIndex)}
                        >
                            {letter}
                        </div>
                    ))}
                </div>
            ))}
        </div>
    );
};

export default GameBoard;
