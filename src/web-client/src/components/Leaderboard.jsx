import React from 'react';

const Leaderboard = ({ show, onClose, scores }) => {
    if (!show) return null;

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white dark:bg-gray-800 p-8 rounded-lg shadow-xl max-w-md w-full relative">
                <button 
                    onClick={onClose}
                    className="absolute top-2 right-2 text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200"
                >
                    ✕
                </button>
                <h2 className="text-2xl font-bold mb-4 dark:text-white text-center">Tabla de Puntajes</h2>
                
                {scores.length === 0 ? (
                    <p className="text-center text-gray-500 dark:text-gray-400">No hay puntajes registrados aún.</p>
                ) : (
                    <div className="max-h-96 overflow-y-auto">
                        <table className="w-full text-left">
                            <thead className="bg-gray-100 dark:bg-gray-700">
                                <tr>
                                    <th className="px-4 py-2 dark:text-gray-200">Jugador</th>
                                    <th className="px-4 py-2 dark:text-gray-200">Tiempo</th>
                                </tr>
                            </thead>
                            <tbody>
                                {scores.map((score, index) => (
                                    <tr key={index} className="border-b border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700">
                                        <td className="px-4 py-2 dark:text-gray-300">{score.name}</td>
                                        <td className="px-4 py-2 dark:text-gray-300 font-mono">{score.time}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
                
                <div className="mt-6 flex justify-center">
                    <button 
                        onClick={onClose}
                        className="btn bg-gray-500 hover:bg-gray-600 border-none"
                    >
                        Cerrar
                    </button>
                </div>
            </div>
        </div>
    );
};

export default Leaderboard;
