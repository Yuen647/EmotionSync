/** @type {import('tailwindcss').Config} */
export default {
  corePlugins: {
    preflight: false,
  },
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      textColor: {
        primary: '#2D8DD2',
        gray: '#566273',
      },
      backgroundColor: {
        white: '#fff',
        default: '#F9F9F9',
        primary: '#2D8DD2',
      },
      borderColor: {
        default: '#C9C5BC',
        primary: '#2D8DD2',
      },
      colors: {
        primary: '#2D8DD2',
      },
    },
  },
  plugins: [],
}

