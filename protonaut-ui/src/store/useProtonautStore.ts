import { create } from 'zustand';
import { HistoryItem } from '../types';

export type ActiveView = 'explorer' | 'workflows' | 'history' | 'settings';
export type ThemeMode = 'dark' | 'light';

interface AuthConfig {
  type: 'none' | 'bearer' | 'basic' | 'apiKey' | 'custom';
  token?: string;
  username?: string;
  password?: string;
  key?: string;
  value?: string;
}

interface ProtonautStore {
  activeView: ActiveView;
  selectedServiceId: string | null;
  selectedMethodId: string | null;
  theme: ThemeMode;
  environment: string;
  history: HistoryItem[];
  auth: AuthConfig;
  savedHeaders: Array<{ key: string; value: string; enabled: boolean }>;
  editorSettings: {
    tabSize: number;
    fontSize: number;
    autoFormat: boolean;
    storagePolicy: 'local' | 'session' | 'memory';
  };

  setActiveView: (view: ActiveView) => void;
  selectMethod: (serviceId: string | null, methodId: string | null) => void;
  toggleTheme: () => void;
  setEnvironment: (env: string) => void;
  addHistoryItem: (item: HistoryItem) => void;
  clearHistory: () => void;
  setAuth: (auth: AuthConfig) => void;
  setSavedHeaders: (headers: Array<{ key: string; value: string; enabled: boolean }>) => void;
  updateSettings: (settings: Partial<ProtonautStore['editorSettings']>) => void;
  resetAll: () => void;
}

const STORAGE_KEY = 'protonaut_store_v1';

function loadPersistedState(): Partial<ProtonautStore> {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (raw) {
      return JSON.parse(raw);
    }
  } catch (_) {}
  return {};
}

function persistState(state: ProtonautStore) {
  if (state.editorSettings.storagePolicy === 'local') {
    try {
      const toSave = {
        theme: state.theme,
        environment: state.environment,
        history: state.history.slice(0, 50),
        savedHeaders: state.savedHeaders,
        editorSettings: state.editorSettings,
      };
      localStorage.setItem(STORAGE_KEY, JSON.stringify(toSave));
    } catch (_) {}
  }
}

export const useProtonautStore = create<ProtonautStore>((set, get) => {
  const initial = loadPersistedState();

  return {
    activeView: 'explorer',
    selectedServiceId: null,
    selectedMethodId: null,
    theme: (initial.theme as ThemeMode) || 'dark',
    environment: initial.environment || 'development',
    history: initial.history || [],
    auth: { type: 'none' },
    savedHeaders: initial.savedHeaders || [
      { key: 'x-request-id', value: '{{uuid}}', enabled: true },
      { key: 'x-client-name', value: 'protonaut', enabled: true },
      { key: 'x-timestamp', value: '{{timestamp}}', enabled: true },
    ],
    editorSettings: initial.editorSettings || {
      tabSize: 2,
      fontSize: 14,
      autoFormat: true,
      storagePolicy: 'local',
    },

    setActiveView: (view) => set({ activeView: view }),

    selectMethod: (serviceId, methodId) =>
      set({
        selectedServiceId: serviceId,
        selectedMethodId: methodId,
        activeView: 'explorer',
      }),

    toggleTheme: () => {
      const nextTheme = get().theme === 'dark' ? 'light' : 'dark';
      set({ theme: nextTheme });
      document.documentElement.classList.toggle('dark', nextTheme === 'dark');
      persistState(get());
    },

    setEnvironment: (env) => {
      set({ environment: env });
      persistState(get());
    },

    addHistoryItem: (item) => {
      const updated = [item, ...get().history.slice(0, 49)];
      set({ history: updated });
      persistState(get());
    },

    clearHistory: () => {
      set({ history: [] });
      persistState(get());
    },

    setAuth: (auth) => set({ auth }),

    setSavedHeaders: (headers) => {
      set({ savedHeaders: headers });
      persistState(get());
    },

    updateSettings: (newSettings) => {
      set((state) => ({
        editorSettings: { ...state.editorSettings, ...newSettings },
      }));
      persistState(get());
    },

    resetAll: () => {
      localStorage.removeItem(STORAGE_KEY);
      set({
        activeView: 'explorer',
        selectedServiceId: null,
        selectedMethodId: null,
        theme: 'dark',
        environment: 'development',
        history: [],
        auth: { type: 'none' },
        savedHeaders: [
          { key: 'x-request-id', value: '{{uuid}}', enabled: true },
          { key: 'x-client-name', value: 'protonaut', enabled: true },
        ],
        editorSettings: {
          tabSize: 2,
          fontSize: 14,
          autoFormat: true,
          storagePolicy: 'local',
        },
      });
    },
  };
});
