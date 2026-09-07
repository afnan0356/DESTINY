import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'Destiny — Technical Foundation (Prompt 01 of 20)',
  description: 'Technical foundation for Destiny ultra-deep life simulation game.',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" className="dark">
      <body className="bg-[#090D14] text-[#F1F5F9] min-h-screen antialiased selection:bg-[#38BDF8] selection:text-[#021626]">
        {children}
      </body>
    </html>
  );
}
