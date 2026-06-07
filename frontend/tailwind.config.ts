import type { Config } from 'tailwindcss';

export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      boxShadow: {
        table: '0 24px 80px rgba(5, 18, 14, 0.32)',
      },
      colors: {
        felt: {
          900: '#0b2a22',
          800: '#0f3f32',
          700: '#145443',
        },
      },
    },
  },
  plugins: [],
} satisfies Config;
