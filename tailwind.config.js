/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './src/pages/**/*.{js,ts,jsx,tsx,mdx}',
    './src/components/**/*.{js,ts,jsx,tsx,mdx}',
    './src/app/**/*.{js,ts,jsx,tsx,mdx}',
    './components/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        background: '#090D14',
        foreground: '#F1F5F9',
        destiny: {
          bg: '#090D14',
          surface: '#111723',
          surfaceVariant: '#182235',
          primary: '#38BDF8',
          secondary: '#94A3B8',
          success: '#10B981',
          outline: '#26354D',
        },
      },
    },
  },
  plugins: [],
};
