const API_URL = '/api/game';

export const startGame = async () => {
    try {
        const response = await fetch(`${API_URL}/start`, {
            method: 'POST',
        });
        return await response.json();
    } catch (error) {
        console.error("Error starting game:", error);
        return { status: 'error', message: error.message };
    }
};

export const validateWord = async (word) => {
    try {
        const response = await fetch(`${API_URL}/validate`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ word }),
        });
        return await response.json();
    } catch (error) {
        console.error("Error validating word:", error);
        return { status: 'invalid' };
    }
};
