import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiClient } from '../api/apiClient';
import { useTranslation } from 'react-i18next';

export default function Login() {
    const { t, i18n } = useTranslation();
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const changeLanguage = (lang) => {
        i18n.changeLanguage(lang);
        localStorage.setItem('appLang', lang);
    };

    const handleLogin = async (e) => {
        e.preventDefault();
        setError('');

        const encodedToken = btoa(`${username}:${password}`);

        try {
            // Zeptáme se backendu (jestli heslo platí)
            await apiClient.get('/api/currencies/strongest?base=USD&symbols=CZK', {
                headers: {
                    'Authorization': `Basic ${encodedToken}`
                }
            });

            // 1. Uložíme token
            localStorage.setItem('authToken', encodedToken);

            // 2. Dáme Reactu 100 milisekund čas, aby si to uvědomil, a pak ho přesměrujeme
            setTimeout(() => {
                navigate('/dashboard', { replace: true });
            }, 100);

        } catch (err) {
            if (!err.response) {
                setError(t('login.errorServer'));
            } else if (err.response.status === 401) {
                setError(t('login.errorAuth'));
            } else {
                setError(t('login.errorGeneric'));
            }
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-zinc-950 font-sans text-zinc-100">
            {/* Přepínač jazyků */}
            <div className="absolute top-6 right-6 flex gap-4">
                <button
                    className={`text-sm font-bold transition-colors ${i18n.language === 'cs' ? 'text-rose-500' : 'text-zinc-500 hover:text-zinc-300'}`}
                    onClick={() => changeLanguage('cs')}>CZ</button>
                <button
                    className={`text-sm font-bold transition-colors ${i18n.language === 'en' ? 'text-rose-500' : 'text-zinc-500 hover:text-zinc-300'}`}
                    onClick={() => changeLanguage('en')}>EN</button>
            </div>

            {/* Přihlašovací karta */}
            <div className="w-full max-w-md bg-zinc-900 border border-zinc-800 rounded-2xl shadow-2xl p-8">
                <h1 className="text-3xl font-extrabold text-center mb-8 tracking-tight text-white">
                    {t('login.title')}
                    <span className="text-rose-600">.</span>
                </h1>

                {error && (
                    <div className="mb-6 p-4 bg-rose-500/10 border border-rose-500/20 text-rose-500 rounded-lg text-sm text-center font-medium">
                        {error}
                    </div>
                )}

                <form onSubmit={handleLogin} className="flex flex-col gap-6">
                    <div className="flex flex-col gap-2">
                        <label className="text-xs font-semibold text-zinc-400 uppercase tracking-wider">{t('login.username')}</label>
                        <input
                            type="text"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                            className="bg-zinc-950 border border-zinc-800 rounded-lg p-3 text-white focus:outline-none focus:border-rose-500 focus:ring-1 focus:ring-rose-500 transition-all placeholder-zinc-600"
                        />
                    </div>
                    <div className="flex flex-col gap-2">
                        <label className="text-xs font-semibold text-zinc-400 uppercase tracking-wider">{t('login.password')}</label>
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            className="bg-zinc-950 border border-zinc-800 rounded-lg p-3 text-white focus:outline-none focus:border-rose-500 focus:ring-1 focus:ring-rose-500 transition-all"
                        />
                    </div>
                    <button
                        type="submit"
                        className="mt-2 w-full bg-rose-600 hover:bg-rose-500 text-white font-bold py-3.5 rounded-lg transition-colors shadow-lg shadow-rose-600/20"
                    >
                        {t('login.submit')}
                    </button>
                </form>
            </div>
        </div>
    );
}