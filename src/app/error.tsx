'use client';

export default function ErrorPage({
  error,
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  return (
    <div className="min-h-screen flex items-center justify-center bg-[#090D14] text-white p-4">
      <div className="text-center space-y-3 max-w-md">
        <h1 className="text-2xl font-bold text-rose-400">Simulation Error Encountered</h1>
        <p className="text-xs text-slate-400">{error.message || 'An unexpected simulation error occurred.'}</p>
        <button
          onClick={() => reset()}
          className="px-4 py-2 bg-[#38BDF8] text-[#090D14] font-bold text-xs uppercase tracking-wider rounded-lg"
        >
          Recover Simulation
        </button>
      </div>
    </div>
  );
}
