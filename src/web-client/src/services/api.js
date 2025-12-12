const API_URL = '/api/game';

export const startGame = async (playerName) => {
    try {
        const response = await fetch(`${API_URL}/start`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ playerName }),
        });
        return await response.json();
    } catch (error) {
        console.error("Error starting game:", error);
        return { status: 'error', message: error.message };
    }
};

export const validateWord = async (word, playerName) => {
    try {
        const response = await fetch(`${API_URL}/validate`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ word, playerName }),
        });
        return await response.json();
    } catch (error) {
        console.error("Error validating word:", error);
        return { status: 'invalid' };
    }
};

export const endGame = async (playerName, time) => {
    try {
        const response = await fetch(`${API_URL}/end`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ playerName, time }),
        });
        return await response.json();
    } catch (error) {
        console.error("Error ending game:", error);
        return { status: 'error' };
    }
};
