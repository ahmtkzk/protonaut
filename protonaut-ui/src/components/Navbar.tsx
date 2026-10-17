import React from 'react';
import {
  Compass,
  GitMerge,
  History,
  Settings,
  Sun,
  Moon,
  Layers,
  ShieldCheck,
  ShieldAlert,
} from 'lucide-react';
import { useProtonautStore, ActiveView } from '../store/useProtonautStore';

export const Navbar: React.FC = () => {
  const {
    activeView,
    setActiveView,
    theme,
    toggleTheme,
    environment,
    setEnvironment,
    auth,
  } = useProtonautStore();

  const navItems: Array<{ id: ActiveView; label: string; icon: React.ReactNode }> = [
    { id: 'explorer', label: 'Explorer', icon: <Compass size={18} /> },
    { id: 'workflows', label: 'Workflows', icon: <GitMerge size={18} /> },
    { id: 'history', label: 'History', icon: <History size={18} /> },
    { id: 'settings', label: 'Settings', icon: <Settings size={18} /> },
  ];

  const hasAuth = auth.type !== 'none';

  return (
    <header className="h-14 border-b border-slate-800 bg-slate-950/80 backdrop-blur px-4 flex items-center justify-between sticky top-0 z-50">
      {/* Brand & Logo */}
      <div className="flex items-center space-x-6">
        <div
          onClick={() => {
            useProtonautStore.getState().selectMethod(null, null);
            setActiveView('explorer');
          }}
          className="flex items-center space-x-2.5 cursor-pointer group"
        >
          <div className="w-8 h-8 rounded-lg bg-gradient-to-tr from-indigo-600 via-indigo-500 to-cyan-400 flex items-center justify-center text-white shadow-lg shadow-indigo-500/20 group-hover:scale-105 transition-transform">
            <Layers size={18} className="stroke-[2.5]" />
          </div>
          <div>
            <div className="flex items-center space-x-1.5">
              <span className="font-bold text-base tracking-tight text-white group-hover:text-indigo-400 transition-colors">
                Protonaut
              </span>
              <span className="text-[10px] uppercase font-mono px-1.5 py-0.5 rounded bg-indigo-500/10 text-indigo-400 border border-indigo-500/20 font-semibold">
                gRPC
              </span>
            </div>
          </div>
        </div>

        {/* View Switcher */}
        <nav className="flex items-center space-x-1">
          {navItems.map((item) => {
            const isActive = activeView === item.id;
            return (
              <button
                key={item.id}
                onClick={() => setActiveView(item.id)}
                className={`flex items-center space-x-2 px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-slate-800 text-indigo-400 border border-slate-700/60 shadow-sm'
                    : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/40'
                }`}
              >
                {item.icon}
                <span>{item.label}</span>
              </button>
            );
          })}
        </nav>
      </div>

      {/* Right Tools */}
      <div className="flex items-center space-x-3">
        {/* Environment Selector */}
        <div className="flex items-center space-x-1.5 bg-slate-900 border border-slate-800 rounded-lg px-2.5 py-1">
          <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
          <select
            value={environment}
            onChange={(e) => setEnvironment(e.target.value)}
            className="bg-transparent text-xs text-slate-300 font-medium focus:outline-none cursor-pointer"
          >
            <option value="development" className="bg-slate-900 text-slate-200">Development</option>
            <option value="staging" className="bg-slate-900 text-slate-200">Staging</option>
            <option value="production" className="bg-slate-900 text-slate-200">Production</option>
          </select>
        </div>

        {/* Auth Badge */}
        <div
          onClick={() => setActiveView('settings')}
          className={`flex items-center space-x-1.5 text-xs px-2.5 py-1 rounded-lg border cursor-pointer transition-colors ${
            hasAuth
              ? 'bg-emerald-500/10 border-emerald-500/30 text-emerald-400 hover:bg-emerald-500/20'
              : 'bg-slate-800/60 border-slate-700 text-slate-400 hover:text-slate-200'
          }`}
          title={hasAuth ? `Authenticated via ${auth.type}` : 'Anonymous / No Auth'}
        >
          {hasAuth ? <ShieldCheck size={14} /> : <ShieldAlert size={14} />}
          <span className="capitalize">{hasAuth ? auth.type : 'No Auth'}</span>
        </div>

        {/* Theme Toggle */}
        <button
          onClick={toggleTheme}
          className="p-1.5 rounded-lg border border-slate-800 bg-slate-900 text-slate-400 hover:text-slate-200 hover:bg-slate-800 transition-colors"
          title={`Switch to ${theme === 'dark' ? 'Light' : 'Dark'} mode`}
        >
          {theme === 'dark' ? <Sun size={16} /> : <Moon size={16} />}
        </button>
      </div>
    </header>
  );
};
