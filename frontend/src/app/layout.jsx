import "./globals.css";

export const metadata = {
  title: "Talent Bridge",
  description: "Find your perfect job match with Talent Bridge",
};

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body className="antialiased">
        {children}
      </body>
    </html>
  );
}
