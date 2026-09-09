import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'Destiny — Ultra-Deep Life Simulation Engine',
  description: 'Destiny: Deterministic, offline-first life simulation engine with autonomous NPCs, generational lineages, and variable-tick chronology.',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" className="dark">
      <body className="bg-[#070B12] text-[#E2E8F0] min-h-screen antialiased selection:bg-[#38BDF8] selection:text-[#021626]">
        {children}
      </body>
    </html>
  );
}
