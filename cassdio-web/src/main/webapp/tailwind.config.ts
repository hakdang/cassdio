import type { Config } from 'tailwindcss';

export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        console: {
          bg: '#f6f7f9',
          surface: '#ffffff',
          line: '#d9dee7',
          muted: '#5e6a78',
          ink: '#17202a',
          brand: '#1f6feb',
          success: '#157347',
          danger: '#b42318',
          warning: '#b7791f',
        },
      },
      boxShadow: {
        console: '0 8px 30px rgba(23, 32, 42, 0.08)',
      },
      borderRadius: {
        console: '8px',
      },
      zIndex: {
        header: '40',
        sidebar: '30',
        dropdown: '50',
      },
    },
  },
  plugins: [],
} satisfies Config;
