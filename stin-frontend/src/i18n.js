import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import csTranslation from './locales/cs.json';
import enTranslation from './locales/en.json';

i18n.use(initReactI18next).init({
    resources: {
        cs: { translation: csTranslation },
        en: { translation: enTranslation }
    },
    lng: localStorage.getItem('appLang') || 'cs',
    fallbackLng: 'en',
    interpolation: { escapeValue: false }
});

export default i18n;