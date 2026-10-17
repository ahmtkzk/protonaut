import React from 'react';
import {
  Settings,
  Shield,
  Palette,
  HardDrive,
  Trash2,
  Key,
  User,
  Check,
} from 'lucide-react';
import { useProtonautStore } from '../store/useProtonautStore';

export const SettingsView: React.FC = () => {
  const {
    theme,
    toggleTheme,
    editorSettings,
    updateSettings,
    resetAll,
    auth,
    setAuth,
  } = useProtonautStore();

  return (
    <div className="flex-1 overflow-y-auto p-8 max-w-4xl mx-auto space-y-8">
      <div className="pb-4 border-b border-slate-800">
        <h2 className="text-xl font-bold text-white tracking-tight">Protonaut Settings</h2>
        <p className="text-xs text-slate-400">Configure appearance, authentication, editor behavior, and browser storage</p>
      </div>

      {/* Authentication Profiles */}
      <div className="bg-slate-950 border border-slate-800 rounded-xl p-6 space-y-4">
        <div className="flex items-center space-x-2.5">
          <Shield size={18} className="text-indigo-400" />
          <h3 className="text-sm font-semibold text-white">Default Authentication Profile</h3>
        </div>
        <p className="text-xs text-slate-400">
          Set authentication once to reuse across all gRPC requests in the explorer and workflows.
        </p>

        <div className="flex flex-wrap gap-2 pt-1">
          {(['none', 'bearer', 'basic', 'apiKey'] as const).map((mode) => (
            <button
              key={mode}
              onClick={() => setAuth({ ...auth, type: mode })}
              className={`px-3 py-1.5 rounded-lg text-xs font-semibold capitalize transition-colors ${
                auth.type === mode
                  ? 'bg-indigo-600 text-white shadow-sm'
                  : 'bg-slate-900 border border-slate-800 text-slate-400 hover:text-slate-200'
              }`}
            >
              {mode}
            </button>
          ))}
        </div>

        {auth.type === 'bearer' && (
          <div className="space-y-1.5 pt-2">
            <label className="text-xs text-slate-400">Bearer Token (JWT / OAuth)</label>
            <input
              type="password"
              placeholder="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
              value={auth.token || ''}
              onChange={(e) => setAuth({ ...auth, token: e.target.value })}
              className="w-full bg-slate-900 border border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-200 font-mono focus:outline-none focus:border-indigo-500"
            />
          </div>
        )}

        {auth.type === 'basic' && (
          <div className="grid grid-cols-2 gap-4 pt-2">
            <div className="space-y-1.5">
              <label className="text-xs text-slate-400">Username</label>
              <input
                type="text"
                placeholder="admin"
                value={auth.username || ''}
                onChange={(e) => setAuth({ ...auth, username: e.target.value })}
                className="w-full bg-slate-900 border border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-200 font-mono focus:outline-none focus:border-indigo-500"
              />
            </div>
            <div className="space-y-1.5">
              <label className="text-xs text-slate-400">Password</label>
              <input
                type="password"
                placeholder="••••••••"
                value={auth.password || ''}
                onChange={(e) => setAuth({ ...auth, password: e.target.value })}
                className="w-full bg-slate-900 border border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-200 font-mono focus:outline-none focus:border-indigo-500"
              />
            </div>
          </div>
        )}

        {auth.type === 'apiKey' && (
          <div className="grid grid-cols-2 gap-4 pt-2">
            <div className="space-y-1.5">
              <label className="text-xs text-slate-400">Metadata Header Key</label>
              <input
                type="text"
                placeholder="x-api-key"
                value={auth.key || ''}
                onChange={(e) => setAuth({ ...auth, key: e.target.value })}
                className="w-full bg-slate-900 border border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-200 font-mono focus:outline-none focus:border-indigo-500"
              />
            </div>
            <div className="space-y-1.5">
              <label className="text-xs text-slate-400">API Key Value</label>
              <input
                type="password"
                placeholder="key_secret_..."
                value={auth.value || ''}
                onChange={(e) => setAuth({ ...auth, value: e.target.value })}
                className="w-full bg-slate-900 border border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-200 font-mono focus:outline-none focus:border-indigo-500"
              />
            </div>
          </div>
        )}
      </div>

      {/* Editor Preferences */}
      <div className="bg-slate-950 border border-slate-800 rounded-xl p-6 space-y-4">
        <div className="flex items-center space-x-2.5">
          <Palette size={18} className="text-indigo-400" />
          <h3 className="text-sm font-semibold text-white">Editor & Appearance</h3>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="space-y-1.5">
            <label className="text-xs text-slate-400">Theme</label>
            <select
              value={theme}
              onChange={() => toggleTheme()}
              className="w-full bg-slate-900 border border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-200 focus:outline-none"
            >
              <option value="dark">Dark Theme</option>
              <option value="light">Light Theme</option>
            </select>
          </div>

          <div className="space-y-1.5">
            <label className="text-xs text-slate-400">Indentation Tab Size</label>
            <select
              value={editorSettings.tabSize}
              onChange={(e) => updateSettings({ tabSize: Number(e.target.value) })}
              className="w-full bg-slate-900 border border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-200 focus:outline-none"
            >
              <option value={2}>2 Spaces</option>
              <option value={4}>4 Spaces</option>
            </select>
          </div>

          <div className="space-y-1.5">
            <label className="text-xs text-slate-400">Font Size</label>
            <select
              value={editorSettings.fontSize}
              onChange={(e) => updateSettings({ fontSize: Number(e.target.value) })}
              className="w-full bg-slate-900 border border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-200 focus:outline-none"
            >
              <option value={12}>12 px</option>
              <option value={14}>14 px</option>
              <option value={16}>16 px</option>
            </select>
          </div>
        </div>
      </div>

      {/* Browser Storage Policy & Reset */}
      <div className="bg-slate-950 border border-slate-800 rounded-xl p-6 space-y-4">
        <div className="flex items-center space-x-2.5">
          <HardDrive size={18} className="text-indigo-400" />
          <h3 className="text-sm font-semibold text-white">Browser Storage Policy</h3>
        </div>

        <div className="space-y-2">
          <label className="text-xs text-slate-400">Storage Destination</label>
          <div className="flex gap-2">
            {[
              { id: 'local', label: 'Local Storage (Persistent)' },
              { id: 'memory', label: 'Memory Only (No persistence)' },
            ].map((pol) => (
              <button
                key={pol.id}
                onClick={() => updateSettings({ storagePolicy: pol.id as any })}
                className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-colors ${
                  editorSettings.storagePolicy === pol.id
                    ? 'bg-indigo-600 text-white'
                    : 'bg-slate-900 border border-slate-800 text-slate-400'
                }`}
              >
                {pol.label}
              </button>
            ))}
          </div>
        </div>

        <div className="pt-4 border-t border-slate-800 flex items-center justify-between">
          <div>
            <div className="text-xs font-semibold text-rose-400">Reset All Local Protonaut Data</div>
            <div className="text-[11px] text-slate-400">Clears history, header presets, cached workflows and settings</div>
          </div>

          <button
            onClick={() => {
              if (confirm('Are you sure you want to delete all stored Protonaut settings and history?')) {
                resetAll();
                alert('All Protonaut local settings have been reset.');
              }
            }}
            className="flex items-center space-x-1.5 bg-rose-500/10 hover:bg-rose-500/20 text-rose-400 border border-rose-500/30 text-xs px-3 py-1.5 rounded-lg transition-colors"
          >
            <Trash2 size={13} />
            <span>Reset All Data</span>
          </button>
        </div>
      </div>
    </div>
  );
};
