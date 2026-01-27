/** @type {import('tailwindcss').Config} */
export default {
    content: [
        "./index.html",
        "./src/**/*.{js,ts,jsx,tsx}",
    ],
    theme: {
        extend: {
            colors: {
                primary: "#4f46e5", // Standard Indigo
                secondary: "#111827", // Standard Dark Gray
            }
        },
    },
    plugins: [],
}
