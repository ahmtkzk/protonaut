import React from 'react';
import {
  Compass,
  GitMerge,
  FileCode,
  ShieldCheck,
  Server,
  Layers,
  Activity,
  ExternalLink,
  Code2,
} from 'lucide-react';
import { EnvironmentInfo, ServiceDescriptorModel } from '../types';
import { useProtonautStore } from '../store/useProtonautStore';

interface HomeDashboardProps {
  info?: EnvironmentInfo;
  services: ServiceDescriptorModel[];
}

export const HomeDashboard: React.FC<HomeDashboardProps> = ({ info, services }) => {
  const { setActiveView, selectMethod } = useProtonautStore();

  const totalMethods = services.reduce((acc, s) => acc + s.methods.length, 0);
  const unaryCount = services.reduce(
    (acc, s) => acc + s.methods.filter((m) => m.type === 'UNARY').length,
    0
  );
  const streamingCount = totalMethods - unaryCount;

  return (
    <div className="flex-1 overflow-y-auto p-8 max-w-6xl mx-auto space-y-8">
      {/* Hero Banner */}
      <div className="relative overflow-hidden rounded-2xl bg-gradient-to-br from-slate-900 via-indigo-950/40 to-slate-900 border border-slate-800 p-8 shadow-2xl">
        <div className="absolute -right-10 -top-10 w-72 h-72 bg-indigo-500/10 rounded-full blur-3xl pointer-events-none" />

        <div className="relative z-10 space-y-4">
          <div className="flex items-center space-x-3">
            <span className="px-2.5 py-1 rounded-full bg-indigo-500/10 border border-indigo-500/30 text-indigo-400 text-xs font-semibold tracking-wide uppercase">
              {info?.teamName || 'Platform Engineering'}
            </span>
            <span className="text-slate-400 text-xs">•</span>
            <span className="text-xs text-slate-400 font-mono">v{info?.version || '1.0.0'}</span>
            <span className="text-slate-400 text-xs">•</span>
            <div className="flex items-center space-x-1.5 text-xs text-emerald-400 bg-emerald-500/10 border border-emerald-500/20 px-2 py-0.5 rounded-full font-mono">
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse" />
              <span>{info?.status || 'Online'}</span>
            </div>
          </div>

          <h1 className="text-3xl font-bold tracking-tight text-white sm:text-4xl">
            {info?.projectName || 'Protonaut gRPC Explorer'}
          </h1>

          <p className="text-slate-400 max-w-2xl text-sm leading-relaxed">
            {info?.description ||
              'Modern, swagger-like developer toolkit, interactive request executor, protobuf inspector and workflow studio embedded directly in your application.'}
          </p>

          <div className="flex flex-wrap items-center gap-3 pt-2">
            <button
              onClick={() => {
                if (services.length > 0 && services[0].methods.length > 0) {
                  selectMethod(services[0].id, services[0].methods[0].name);
                }
              }}
              className="flex items-center space-x-2 bg-indigo-600 hover:bg-indigo-500 text-white px-4 py-2 rounded-lg text-xs font-semibold transition-all shadow-lg shadow-indigo-600/20 hover:scale-102"
            >
              <Compass size={15} />
              <span>Start Exploring Methods</span>
            </button>

            <button
              onClick={() => setActiveView('workflows')}
              className="flex items-center space-x-2 bg-slate-800 hover:bg-slate-700 text-slate-200 border border-slate-700 px-4 py-2 rounded-lg text-xs font-semibold transition-all"
            >
              <GitMerge size={15} />
              <span>Open Workflow Studio</span>
            </button>
          </div>
        </div>
      </div>

      {/* Discovery Summary Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div className="bg-slate-900/60 border border-slate-800 rounded-xl p-5 space-y-1">
          <div className="flex items-center justify-between text-slate-400 text-xs font-medium">
            <span>Discovered Services</span>
            <Server size={16} className="text-indigo-400" />
          </div>
          <div className="text-2xl font-bold text-white font-mono">{services.length}</div>
          <div className="text-[11px] text-slate-400">Registered gRPC definitions</div>
        </div>

        <div className="bg-slate-900/60 border border-slate-800 rounded-xl p-5 space-y-1">
          <div className="flex items-center justify-between text-slate-400 text-xs font-medium">
            <span>RPC Methods</span>
            <Layers size={16} className="text-cyan-400" />
          </div>
          <div className="text-2xl font-bold text-white font-mono">{totalMethods}</div>
          <div className="text-[11px] text-slate-400">Ready for interactive calls</div>
        </div>

        <div className="bg-slate-900/60 border border-slate-800 rounded-xl p-5 space-y-1">
          <div className="flex items-center justify-between text-slate-400 text-xs font-medium">
            <span>Unary Methods</span>
            <Activity size={16} className="text-emerald-400" />
          </div>
          <div className="text-2xl font-bold text-emerald-400 font-mono">{unaryCount}</div>
          <div className="text-[11px] text-slate-400">Standard request-response</div>
        </div>

        <div className="bg-slate-900/60 border border-slate-800 rounded-xl p-5 space-y-1">
          <div className="flex items-center justify-between text-slate-400 text-xs font-medium">
            <span>Streaming Methods</span>
            <Activity size={16} className="text-amber-400" />
          </div>
          <div className="text-2xl font-bold text-amber-400 font-mono">{streamingCount}</div>
          <div className="text-[11px] text-slate-400">Server & Bi-directional streams</div>
        </div>
      </div>

      {/* Network & Endpoints Information */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="bg-slate-900/40 border border-slate-800 rounded-xl p-6 space-y-4">
          <div className="flex items-center space-x-2.5">
            <div className="w-8 h-8 rounded-lg bg-indigo-500/10 flex items-center justify-center text-indigo-400">
              <Server size={18} />
            </div>
            <div>
              <h3 className="text-sm font-semibold text-white">Browser Entry Points</h3>
              <p className="text-xs text-slate-400">Active Protonaut HTTP and gRPC network listeners</p>
            </div>
          </div>

          <div className="space-y-2 text-xs font-mono">
            <div className="flex items-center justify-between bg-slate-950 p-3 rounded-lg border border-slate-800/80">
              <span className="text-slate-400">gRPC Browser Port</span>
              <span className="text-indigo-400 font-semibold">{info?.grpcPort || 6570}</span>
            </div>
            <div className="flex items-center justify-between bg-slate-950 p-3 rounded-lg border border-slate-800/80">
              <span className="text-slate-400">Dedicated REST Port</span>
              <span className={info?.restEnabled ? 'text-emerald-400 font-semibold' : 'text-slate-400'}>
                {info?.restEnabled ? info?.restPort || 8590 : 'Disabled'}
              </span>
            </div>
            <div className="flex items-center justify-between bg-slate-950 p-3 rounded-lg border border-slate-800/80">
              <span className="text-slate-400">Current Host Address</span>
              <span className="text-slate-300">{info?.host || '0.0.0.0'}</span>
            </div>
          </div>
        </div>

        <div className="bg-slate-900/40 border border-slate-800 rounded-xl p-6 space-y-4">
          <div className="flex items-center space-x-2.5">
            <div className="w-8 h-8 rounded-lg bg-emerald-500/10 flex items-center justify-center text-emerald-400">
              <ShieldCheck size={18} />
            </div>
            <div>
              <h3 className="text-sm font-semibold text-white">Security & Environment</h3>
              <p className="text-xs text-slate-400">Role-based controls & execution safeguards</p>
            </div>
          </div>

          <div className="space-y-2 text-xs">
            <div className="flex items-center justify-between bg-slate-950 p-3 rounded-lg border border-slate-800/80">
              <span className="text-slate-400">Active Environment</span>
              <span className="text-slate-200 font-semibold capitalize font-mono">{info?.environment || 'development'}</span>
            </div>
            <div className="flex items-center justify-between bg-slate-950 p-3 rounded-lg border border-slate-800/80">
              <span className="text-slate-400">SSRF Protection</span>
              <span className="text-emerald-400 font-mono">Enforced (Local Channel)</span>
            </div>
            <div className="flex items-center justify-between bg-slate-950 p-3 rounded-lg border border-slate-800/80">
              <span className="text-slate-400">Sensitive Header Masking</span>
              <span className="text-emerald-400 font-mono">Active</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
