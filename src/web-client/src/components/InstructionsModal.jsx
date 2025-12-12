import React from 'react';

const InstructionsModal = ({ show, onClose }) => {
    if (!show) return null;

    return (
        <div className="fixed inset-0 bg-black/70 flex justify-center items-center z-50">
            <div className="bg-white dark:bg-gray-800 p-8 rounded-xl max-w-lg text-center shadow-2xl">
                <h2 className="text-2xl font-bold text-yellow-500 mb-4">Cómo Jugar</h2>
                <ul className="text-left mb-6 space-y-2 dark:text-gray-200">
                    <li>Presiona <strong>Iniciar Juego</strong> para comenzar.</li>
                    <li>Busca las palabras listadas en la sopa de letras.</li>
                    <li>Para seleccionar una palabra:
                        <ol className="list-decimal ml-6 mt-1">
                            <li>Haz click en la <strong>primera letra</strong>.</li>
                            <li>Haz click en la <strong>última letra</strong>.</li>
                        </ol>
                    </li>
                    <li>La selección debe ser horizontal, vertical o diagonal.</li>
                </ul>
                <button className="btn bg-green-500 hover:bg-green-600 w-full" onClick={onClose}>¡Entendido!</button>
            </div>
        </div>
    );
};

export default InstructionsModal;
