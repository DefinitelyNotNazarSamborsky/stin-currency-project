import { apiClient } from './apiClient';

const getHeaders = () => {
    const token = sessionStorage.getItem('authToken');
    return token ? { 'Authorization': `Basic ${token}` } : {};
};

const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

export const currencyService = {
    getAvailableSymbols: async () => ['USD', 'CZK', 'EUR', 'GBP', 'CHF', 'PLN', 'JPY', 'AUD', 'CAD'],

    getSettings: async () => {
        try {
            const res = await apiClient.get('/api/settings', { headers: getHeaders() });
            return res.data || {};
        } catch (err) {
            if (err.response && err.response.status === 401) {
                window.location.href = '/login';
            }
            return {};
        }
    },

    saveSettings: async (settings) => {
        try {
            await apiClient.post('/api/settings', settings, { headers: getHeaders() });
        } catch (err) {
            console.error('Chyba ukládání nastavení:', err);
        }
    },

    getExtremes: async (base, symbols) => {
        const symStr = symbols.join(',');
        const strongRes = await apiClient.get(`/api/currencies/strongest?base=${base}&symbols=${symStr}`, { headers: getHeaders() });
        await delay(1200);
        const weakRes = await apiClient.get(`/api/currencies/weakest?base=${base}&symbols=${symStr}`, { headers: getHeaders() });
        return { strongestCurrency: strongRes.data, weakestCurrency: weakRes.data };
    },

    getHistoryChart: async (base, symbols, startDate, endDate) => {
        const symStr = symbols.join(',');
        try {
            const res = await apiClient.get(
                `/api/currencies/history?base=${base}&symbols=${symStr}&startDate=${startDate}&endDate=${endDate}`,
                { headers: getHeaders() }
            );
            return res.data;
        } catch (err) {
            console.error('Chyba načítání historie', err);
            return [];
        }
    }
};