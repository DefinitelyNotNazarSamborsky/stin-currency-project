import { apiClient } from './apiClient';

const getHeaders = () => {
    const token = localStorage.getItem('authToken');
    return token ? { 'Authorization': `Basic ${token}` } : {};
};

const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

export const currencyService = {
    getAvailableSymbols: async () => ['USD', 'CZK', 'EUR', 'GBP', 'CHF', 'PLN', 'JPY', 'AUD', 'CAD'],

    getExtremes: async (base, symbols) => {
        const symStr = symbols.join(',');
        const strongRes = await apiClient.get(`/api/currencies/strongest?base=${base}&symbols=${symStr}`, { headers: getHeaders() });
        await delay(1200);
        const weakRes = await apiClient.get(`/api/currencies/weakest?base=${base}&symbols=${symStr}`, { headers: getHeaders() });

        return {
            strongestCurrency: strongRes.data,
            weakestCurrency: weakRes.data
        };
    },

    getHistory: async (base, symbols, startDate, endDate) => {
        const averages = {};
        for (const sym of symbols) {
            try {
                const res = await apiClient.get(
                    `/api/currencies/average?base=${base}&symbols=${symbols.join(',')}&startDate=${startDate}&endDate=${endDate}&targetCurrency=${sym}`,
                    { headers: getHeaders() }
                );
                if (res.data) averages[sym] = res.data;
            } catch (err) {
                console.error(`Chyba pro měnu ${sym}`, err);
            }
            await delay(1200);
        }
        return { averages };
    }
};