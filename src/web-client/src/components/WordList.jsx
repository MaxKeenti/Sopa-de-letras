import React from 'react';

const WordList = ({ targetWords, foundWords }) => {
    return (
        <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-lg min-w-[200px]">
            <h3 className="text-xl font-bold mb-4 border-b pb-2 dark:border-gray-700">Palabras</h3>
            <ul className="space-y-2">
                {targetWords.map((word, idx) => (
                    <li key={idx} className={`px-3 py-2 rounded transition-all ${
                        foundWords.includes(word) 
                        ? 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-100 line-through' 
                        : 'bg-gray-100 dark:bg-gray-700'
                    }`}>
                        {word}
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default WordList;
