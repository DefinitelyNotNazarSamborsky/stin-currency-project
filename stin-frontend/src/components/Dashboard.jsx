import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { currencyService } from '../api/currencyService';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from 'recharts';
import { useTranslation } from 'react-i18next';

export default function Dashboard() {
    const navigate = useNavigate();
    const { t, i18n } = useTranslation();
    const today = new Date().toISOString().split('T')[0];

    const [baseCurrency, setBaseCurrency] = useState('USD');
    const [selectedCurrencies, setSelectedCurrencies] = useState(['CZK', 'EUR']);
    const [availableCurrencies, setAvailableCurrencies] = useState([]);
    const [startDate, setStartDate] = useState('2024-01-01');
    const [endDate, setEndDate] = useState(today);

    const [error, setError] = useState(null);
    const [isFetching, setIsFetching] = useState(false);
    const [extremesData, setExtremesData] = useState(null);
    const [averagesData, setAveragesData] = useState(null);
    const [chartData, setChartData] = useState([]);

    const [isSettingsLoaded, setIsSettingsLoaded] = useState(false);

    const COLORS = ['#e11d48', '#3b82f6', '#22c55e', '#f59e0b', '#a855f7', '#06b6d4'];

    useEffect(() => {
        if (error) {
            const timer = setTimeout(() => {
                setError(null);
            }, 5000);
            return () => clearTimeout(timer);
        }
    }, [error]);

    useEffect(() => {
        const token = localStorage.getItem('authToken');

        if (!token) {
            navigate("/login");
            return;
        }

        fetch("/api/me", {
            headers: {
                'Authorization': token
            },
            credentials: "include"
        })
            .then(res => {
                if (res.status === 401) navigate("/login");
            });
    }, []);

    useEffect(() => {
        const loadInitialData = async () => {
            try {
                const symbols = await currencyService.getAvailableSymbols();
                setAvailableCurrencies(symbols);

                const settings = await currencyService.getSettings();

                if (settings && settings.baseCurrency !== null && settings.baseCurrency !== undefined) {
                    setBaseCurrency(settings.baseCurrency);
                    if (Array.isArray(settings.selectedCurrencies)) {
                        setSelectedCurrencies(settings.selectedCurrencies);
                    }
                    setIsSettingsLoaded(true);
                } else {
                    setIsSettingsLoaded(true);
                }
            } catch (err) {
                console.error("Inicializace selhala:", err);
            }
        };
        loadInitialData();
    }, []);

    useEffect(() => {
        if (isSettingsLoaded) {
            currencyService.saveSettings({
                baseCurrency: baseCurrency,
                selectedCurrencies: selectedCurrencies
            }).catch(err => console.error("Nepodařilo se uložit nastavení na server:", err));
        }
    }, [baseCurrency, selectedCurrencies, isSettingsLoaded]);

    const handleLogout = () => {
        localStorage.removeItem('authToken');
        navigate('/login');
    };

    const handleResetSettings = () => {
        setBaseCurrency('USD');
        setSelectedCurrencies(['CZK', 'EUR']);
    };
    //const handleResetSettings = () => {
    //    setBaseCurrency('USD');
    //    setSelectedCurrencies(['CZK', 'EUR']);
    //    setError(t('api.errorDashboard') || 'Testovací chybová hláška API!');
    //};

    const handleFetchData = async () => {
        if (selectedCurrencies.length === 0) {
            setError(t('dashboard.noCurrencyError'));
            return;
        }
        setIsFetching(true);
        setError(null);

        try {
            const extremes = await currencyService.getExtremes(baseCurrency, selectedCurrencies);
            setExtremesData(extremes);
        } catch (err) {
            setError(t('api.errorDashboard') || 'Nepodařilo se načíst data z API. Zkuste to prosím znovu.');
        }
        try {
            const chart = await currencyService.getHistoryChart(baseCurrency, selectedCurrencies, startDate, endDate);
            setChartData(chart);

            const calculatedAverages = {};
            selectedCurrencies.forEach(currency => {
                const values = chart
                    .map(day => parseFloat(day[currency]))
                    .filter(val => !isNaN(val));

                if (values.length > 0) {
                    const sum = values.reduce((acc, val) => acc + val, 0);
                    calculatedAverages[currency] = sum / values.length;
                }
            });

            setAveragesData(calculatedAverages);
        } catch (err) {
            console.error('Chart and average error:', err);
        }

        setIsFetching(false);
    };

    return (
        <div className="min-h-screen bg-zinc-950 text-zinc-100 flex flex-col font-sans">
            <header className="h-16 bg-zinc-900 border-b border-zinc-800 flex justify-between items-center px-6">
                <div className="text-xl font-extrabold tracking-tight">
                    {t('dashboard.headerTitle')}<span className="text-rose-600">.</span>
                </div>
                <div className="flex items-center gap-6">
                    <div className="flex gap-3">
                        <button className={`text-sm font-bold ${i18n.language === 'cs' ? 'text-rose-500' : 'text-zinc-500'}`} onClick={() => i18n.changeLanguage('cs')}>CZ</button>
                        <button className={`text-sm font-bold ${i18n.language === 'en' ? 'text-rose-500' : 'text-zinc-500'}`} onClick={() => i18n.changeLanguage('en')}>EN</button>
                    </div>
                    <div className="h-4 w-px bg-zinc-700"></div>
                    <button onClick={handleLogout} className="text-sm font-semibold text-zinc-400 hover:text-rose-500 transition-colors">
                        {t('dashboard.logout')}
                    </button>
                </div>
            </header>

            <div className="flex flex-1 overflow-hidden">
                <aside className="w-80 bg-zinc-900/50 border-r border-zinc-800 p-6 flex flex-col gap-6 overflow-y-auto">
                    <div className="flex justify-between items-center">
                        <h3 className="text-sm font-bold uppercase tracking-widest text-zinc-500">{t('dashboard.settingsTitle') || 'Nastavení'}</h3>
                        <button onClick={handleResetSettings} className="text-xs text-zinc-400 hover:text-rose-400 transition-colors">
                            {t('dashboard.reset') || 'Reset'}
                        </button>
                    </div>

                    <div className="flex flex-col gap-2">
                        <label className="text-xs font-semibold text-zinc-400">{t('dashboard.baseCurrency')}</label>
                        <select
                            value={baseCurrency}
                            onChange={(e) => setBaseCurrency(e.target.value)}
                            className="bg-zinc-950 border border-zinc-800 rounded-lg p-2.5 text-white focus:outline-none focus:border-rose-500"
                        >
                            {availableCurrencies.map(c => <option key={c} value={c}>{c}</option>)}
                        </select>
                    </div>

                    <div className="flex flex-col gap-2">
                        <label className="text-xs font-semibold text-zinc-400">{t('dashboard.targetCurrencies')}</label>
                        <div className="grid grid-cols-2 gap-2 bg-zinc-950 border border-zinc-800 rounded-lg p-3 max-h-40 overflow-y-auto">
                            {availableCurrencies.filter(c => c !== baseCurrency).map(c => (
                                <label key={c} className="flex items-center gap-2 text-sm cursor-pointer hover:text-rose-400">
                                    <input
                                        type="checkbox"
                                        className="accent-rose-600 cursor-pointer"
                                        checked={selectedCurrencies.includes(c)}
                                        onChange={(e) => {
                                            if(e.target.checked) setSelectedCurrencies([...selectedCurrencies, c]);
                                            else setSelectedCurrencies(selectedCurrencies.filter(curr => curr !== c));
                                        }}
                                    /> {c}
                                </label>
                            ))}
                        </div>
                    </div>

                    <div className="flex flex-col gap-2">
                        <label className="text-xs font-semibold text-zinc-400">{t('dashboard.dateFrom')}</label>
                        <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} className="bg-zinc-950 border border-zinc-800 rounded-lg p-2.5 text-white" />

                        <label className="text-xs font-semibold text-zinc-400 mt-2">{t('dashboard.dateTo')}</label>
                        <input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} className="bg-zinc-950 border border-zinc-800 rounded-lg p-2.5 text-white" />
                    </div>

                    <button
                        onClick={handleFetchData}
                        disabled={isFetching}
                        className="mt-4 bg-rose-600 hover:bg-rose-500 disabled:bg-zinc-800 text-white font-bold py-3 rounded-lg shadow-lg shadow-rose-600/20 transition-all"
                    >
                        {isFetching ? t('dashboard.loadingBtn') : t('dashboard.analyzeBtn')}
                    </button>
                </aside>

                <main className="flex-1 p-8 overflow-y-auto">
                    {error && (
                        <div className="fixed bottom-6 right-6 z-50 bg-rose-600 border border-rose-500 text-white px-6 py-4 rounded-xl shadow-[0_0_20px_rgba(225,29,72,0.4)] flex items-center gap-4 animate-bounce">
                            <svg className="w-6 h-6 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                            </svg>
                            <span className="font-semibold text-sm max-w-sm">{error}</span>
                            <button
                                onClick={() => setError(null)}
                                className="ml-2 text-rose-200 hover:text-white transition-colors focus:outline-none"
                            >
                                <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                                </svg>
                            </button>
                        </div>
                    )}

                    {extremesData ? (
                        <div className="flex flex-col gap-8 h-full">
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <div className="bg-zinc-900 border border-zinc-800 rounded-2xl p-6 shadow-xl">
                                    <h4 className="text-sm font-semibold text-zinc-400 uppercase tracking-wider mb-2">{t('results.strongest')}</h4>
                                    <div className="text-4xl font-black text-rose-500">{extremesData.strongestCurrency || '-'}</div>
                                </div>
                                <div className="bg-zinc-900 border border-zinc-800 rounded-2xl p-6 shadow-xl">
                                    <h4 className="text-sm font-semibold text-zinc-400 uppercase tracking-wider mb-2">{t('results.weakest')}</h4>
                                    <div className="text-4xl font-black text-blue-500">{extremesData.weakestCurrency || '-'}</div>
                                </div>
                            </div>
                            {averagesData && Object.keys(averagesData).length > 0 && (
                                <div className="bg-zinc-900 border border-zinc-800 rounded-2xl p-6 shadow-xl">
                                    <h4 className="text-sm font-semibold text-zinc-400 uppercase tracking-wider mb-4">
                                        {t('results.average')}
                                    </h4>
                                    <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                                        {Object.entries(averagesData).map(([currency, avg]) => (
                                            <div key={currency} className="flex flex-col gap-1 bg-zinc-950 rounded-xl p-4 border border-zinc-800">
                                                <span className="text-xs font-semibold text-zinc-500 uppercase">{currency}</span>
                                                <span className="text-2xl font-black text-emerald-400">
                                                    {typeof avg === 'number' ? avg.toFixed(4) : '-'}
                                                </span>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            )}
                            <div className="bg-zinc-900 border border-zinc-800 rounded-2xl p-6 shadow-xl flex-1 min-h-[400px] flex flex-col">
                                <h3 className="text-lg font-bold mb-6">{t('results.average')} ({t('results.chartTitle')})</h3>
                                {chartData.length > 0 ? (
                                    <ResponsiveContainer width="100%" height="100%">
                                        <LineChart data={chartData} margin={{ top: 10, right: 20, left: 0, bottom: 0 }}>
                                            <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#27272a" />
                                            <XAxis
                                                dataKey="date"
                                                stroke="#a1a1aa"
                                                tick={{fill: '#a1a1aa', fontSize: 11}}
                                                tickFormatter={(val) => val.slice(5)}
                                            />
                                            <YAxis stroke="#a1a1aa" tick={{fill: '#a1a1aa'}} domain={['auto', 'auto']} />
                                            <Tooltip
                                                contentStyle={{ backgroundColor: '#18181b', borderColor: '#27272a', borderRadius: '8px', color: '#fff' }}
                                            />
                                            <Legend />
                                            {selectedCurrencies.map((sym, index) => (
                                                <Line
                                                    key={sym}
                                                    type="monotone"
                                                    dataKey={sym}
                                                    stroke={COLORS[index % COLORS.length]}
                                                    strokeWidth={2}
                                                    dot={false}
                                                />
                                            ))}
                                        </LineChart>
                                    </ResponsiveContainer>
                                ) : (
                                    <div className="flex-1 flex items-center justify-center text-zinc-600">{t('results.noDataAverages')}</div>
                                )}
                            </div>
                        </div>
                    ) : (
                        <div className="h-full flex items-center justify-center text-zinc-600 text-lg">
                            {isFetching ? t('results.fetching') : t('results.selectParams')}
                        </div>
                    )}
                </main>
            </div>
        </div>
    );
}