import React from 'react';

const GameStatus = ({ message }) => {
    return (
        <div className={`w-full max-w-md p-3 mb-6 rounded text-center font-bold text-white shadow transition-all ${
            message.type === 'error' ? 'bg-red-500' :
            message.type === 'success' ? 'bg-green-500' :
            message.type === 'warning' ? 'bg-orange-500' :
            'bg-blue-500'
        }`}>
            {message.text}
        </div>
    );
};

export default GameStatus;
