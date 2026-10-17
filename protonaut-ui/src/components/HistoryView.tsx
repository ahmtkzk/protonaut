import React from 'react';
import { History, Trash2, Clock, CheckCircle2, XCircle, ArrowUpRight } from 'lucide-react';
import { useProtonautStore } from '../store/useProtonautStore';

export const HistoryView: React.FC = () => {
  const { history, clearHistory, selectMethod } = useProtonautStore();

  return (
    <div className="flex-1 overflow-y-auto p-8 max-w-5xl mx-auto space-y-6">
      <div className="flex items-center justify-between pb-4 border-b border-slate-800">
        <div>
          <h2 className="text-xl font-bold text-white tracking-tight">Execution History</h2>
          <p className="text-xs text-slate-400">Past interactive gRPC method executions</p>
        </div>

        {history.length > 0 && (
          <button
            onClick={clearHistory}
            className="flex items-center space-x-1.5 text-xs text-rose-400 hover:text-rose-300 bg-rose-500/10 px-3 py-1.5 rounded-lg border border-rose-500/20 transition-colors"
          >
            <Trash2 size={13} />
            <span>Clear History</span>
          </button>
        )}
      </div>

      {history.length === 0 ? (
        <div className="p-16 text-center text-xs text-slate-400 border border-dashed border-slate-800 rounded-xl space-y-2">
          <History size={28} className="mx-auto text-slate-400" />
          <p>No requests recorded yet. Executions from the Explorer will appear here.</p>
        </div>
      ) : (
        <div className="space-y-3">
          {history.map((item) => (
            <div
              key={item.id}
              className="bg-slate-950 border border-slate-800 rounded-xl p-4 flex items-center justify-between hover:border-slate-700 transition-colors"
            >
              <div className="space-y-1">
                <div className="flex items-center space-x-2.5">
                  <span className="font-semibold text-xs text-white">
                    {item.serviceId}.{item.methodId}
                  </span>
                  <span className="text-[10px] font-mono px-1.5 py-0.2 rounded bg-slate-800 text-slate-400">
                    {item.environment}
                  </span>
                </div>

                <div className="flex items-center space-x-4 text-[11px] text-slate-400 font-mono">
                  <span className="flex items-center space-x-1">
                    <Clock size={12} />
                    <span>{new Date(item.timestamp).toLocaleTimeString()}</span>
                  </span>
                  <span>{item.durationMs}ms</span>
                </div>
              </div>

              <div className="flex items-center space-x-3">
                <div
                  className={`flex items-center space-x-1 text-xs font-mono font-bold px-2.5 py-1 rounded-lg ${
                    item.status.code === 0
                      ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/30'
                      : 'bg-rose-500/10 text-rose-400 border border-rose-500/30'
                  }`}
                >
                  {item.status.code === 0 ? <CheckCircle2 size={13} /> : <XCircle size={13} />}
                  <span>{item.status.name}</span>
                </div>

                <button
                  onClick={() => selectMethod(item.serviceId, item.methodId)}
                  className="p-1.5 rounded-lg bg-slate-900 hover:bg-slate-800 text-slate-400 hover:text-indigo-400 border border-slate-800 transition-colors"
                  title="Open Method in Explorer"
                >
                  <ArrowUpRight size={14} />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
