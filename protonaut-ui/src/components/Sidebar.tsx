import React, { useState, useMemo } from 'react';
import {
  Search,
  ChevronDown,
  ChevronRight,
  Box,
  Layers,
  ArrowRight,
  Radio,
  RefreshCw,
} from 'lucide-react';
import { ServiceDescriptorModel, MethodDescriptorModel, MethodType } from '../types';
import { useProtonautStore } from '../store/useProtonautStore';

interface SidebarProps {
  services: ServiceDescriptorModel[];
  isLoading: boolean;
  onRefresh: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ services, isLoading, onRefresh }) => {
  const [search, setSearch] = useState('');
  const [collapsedServices, setCollapsedServices] = useState<Record<string, boolean>>({});

  const { selectedServiceId, selectedMethodId, selectMethod } = useProtonautStore();

  const toggleCollapse = (serviceId: string) => {
    setCollapsedServices((prev) => ({ ...prev, [serviceId]: !prev[serviceId] }));
  };

  const filteredServices = useMemo(() => {
    if (!search.trim()) return services;
    const query = search.toLowerCase();

    return services
      .map((svc) => {
        const matchesSvc =
          svc.name.toLowerCase().includes(query) ||
          svc.fullName.toLowerCase().includes(query) ||
          svc.packageName.toLowerCase().includes(query);

        const matchedMethods = svc.methods.filter(
          (m) =>
            m.name.toLowerCase().includes(query) ||
            m.fullMethodName.toLowerCase().includes(query)
        );

        if (matchesSvc || matchedMethods.length > 0) {
          return {
            ...svc,
            methods: matchesSvc ? svc.methods : matchedMethods,
          };
        }
        return null;
      })
      .filter(Boolean) as ServiceDescriptorModel[];
  }, [services, search]);

  const renderMethodBadge = (type: MethodType) => {
    switch (type) {
      case 'UNARY':
        return (
          <span className="text-[10px] font-mono px-1 py-0.2 rounded bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 font-semibold">
            UNARY
          </span>
        );
      case 'SERVER_STREAMING':
        return (
          <span className="text-[10px] font-mono px-1 py-0.2 rounded bg-amber-500/10 text-amber-400 border border-amber-500/20 font-semibold">
            STREAM
          </span>
        );
      case 'CLIENT_STREAMING':
        return (
          <span className="text-[10px] font-mono px-1 py-0.2 rounded bg-purple-500/10 text-purple-400 border border-purple-500/20 font-semibold">
            CLIENT
          </span>
        );
      case 'BIDI_STREAMING':
        return (
          <span className="text-[10px] font-mono px-1 py-0.2 rounded bg-cyan-500/10 text-cyan-400 border border-cyan-500/20 font-semibold">
            BIDI
          </span>
        );
    }
  };

  const totalMethods = useMemo(
    () => services.reduce((acc, s) => acc + s.methods.length, 0),
    [services]
  );

  return (
    <aside className="w-80 border-r border-slate-800 bg-slate-950 flex flex-col h-[calc(100vh-3.5rem)] shrink-0">
      {/* Search Header */}
      <div className="p-3 border-b border-slate-800/80 space-y-2">
        <div className="flex items-center justify-between text-xs text-slate-400 px-1">
          <span className="font-semibold uppercase tracking-wider text-[11px] text-slate-400">
            Services ({services.length}) • Methods ({totalMethods})
          </span>
          <button
            onClick={onRefresh}
            disabled={isLoading}
            className="hover:text-indigo-400 transition-colors p-1"
            title="Refresh Services"
          >
            <RefreshCw size={13} className={isLoading ? 'animate-spin' : ''} />
          </button>
        </div>

        <div className="relative">
          <Search size={14} className="absolute left-3 top-2.5 text-slate-400" />
          <input
            type="text"
            placeholder="Search service or method..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full bg-slate-900 border border-slate-800 rounded-lg pl-8 pr-3 py-1.5 text-xs text-slate-200 placeholder-slate-400 focus:outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 transition-all"
          />
        </div>
      </div>

      {/* Services and Methods Tree */}
      <div className="flex-1 overflow-y-auto p-2 space-y-1">
        {isLoading && services.length === 0 ? (
          <div className="p-4 text-center text-xs text-slate-400 space-y-2">
            <div className="animate-spin w-5 h-5 border-2 border-indigo-500 border-t-transparent rounded-full mx-auto" />
            <p>Discovering gRPC services...</p>
          </div>
        ) : filteredServices.length === 0 ? (
          <div className="p-6 text-center text-xs text-slate-400">
            No gRPC services matched your search.
          </div>
        ) : (
          filteredServices.map((service) => {
            const isCollapsed = collapsedServices[service.id];
            const isServiceActive = selectedServiceId === service.id;

            return (
              <div key={service.id} className="rounded-lg overflow-hidden border border-slate-800/40 bg-slate-900/30">
                {/* Service Header */}
                <div
                  onClick={() => toggleCollapse(service.id)}
                  className={`flex items-center justify-between px-2.5 py-2 cursor-pointer hover:bg-slate-800/60 transition-colors ${
                    isServiceActive ? 'bg-slate-800/40' : ''
                  }`}
                >
                  <div className="flex items-center space-x-2 min-w-0">
                    <span className="text-slate-400">
                      {isCollapsed ? <ChevronRight size={14} /> : <ChevronDown size={14} />}
                    </span>
                    <Box size={15} className="text-indigo-400 shrink-0" />
                    <span className="font-semibold text-xs text-slate-200 truncate" title={service.fullName}>
                      {service.name}
                    </span>
                  </div>
                  <span className="text-[10px] font-mono text-slate-400 bg-slate-800 px-1.5 py-0.5 rounded">
                    {service.methods.length}
                  </span>
                </div>

                {/* Methods List */}
                {!isCollapsed && (
                  <div className="py-1 pl-4 pr-1 space-y-0.5 border-t border-slate-800/30">
                    {service.methods.map((method) => {
                      const isMethodActive =
                        selectedServiceId === service.id && selectedMethodId === method.name;

                      return (
                        <button
                          key={method.name}
                          onClick={() => selectMethod(service.id, method.name)}
                          className={`w-full text-left flex items-center justify-between px-2.5 py-1.5 rounded-md text-xs transition-colors group ${
                            isMethodActive
                              ? 'bg-indigo-600/20 text-indigo-300 font-medium border border-indigo-500/30 shadow-sm'
                              : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/40'
                          }`}
                        >
                          <div className="flex items-center space-x-2 min-w-0">
                            <Radio
                              size={12}
                              className={`shrink-0 ${
                                isMethodActive ? 'text-indigo-400' : 'text-slate-400 group-hover:text-slate-400'
                              }`}
                            />
                            <span className="truncate">{method.name}</span>
                          </div>
                          <div className="shrink-0 ml-2">
                            {renderMethodBadge(method.type)}
                          </div>
                        </button>
                      );
                    })}
                  </div>
                )}
              </div>
            );
          })
        )}
      </div>
    </aside>
  );
};
