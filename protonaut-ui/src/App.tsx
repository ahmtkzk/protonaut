import React, { useEffect, useState } from 'react';
import { Navbar } from './components/Navbar';
import { Sidebar } from './components/Sidebar';
import { HomeDashboard } from './components/HomeDashboard';
import { MethodExplorer } from './components/MethodExplorer';
import { WorkflowStudio } from './components/WorkflowStudio';
import { HistoryView } from './components/HistoryView';
import { SettingsView } from './components/SettingsView';
import { useProtonautStore } from './store/useProtonautStore';
import { api } from './services/api';
import { ServiceDescriptorModel, EnvironmentInfo } from './types';

export const App: React.FC = () => {
  const { activeView, selectedServiceId, selectedMethodId, theme } = useProtonautStore();

  const [services, setServices] = useState<ServiceDescriptorModel[]>([]);
  const [info, setInfo] = useState<EnvironmentInfo | undefined>();
  const [isLoading, setIsLoading] = useState(true);

  const loadData = async () => {
    setIsLoading(true);
    try {
      const [servicesData, infoData] = await Promise.all([
        api.getServices().catch(() => []),
        api.getInfo().catch(() => undefined),
      ]);
      setServices(servicesData);
      setInfo(infoData);
    } catch (err) {
      console.error('Failed to load services or info:', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  // Sync theme with document element
  useEffect(() => {
    document.documentElement.classList.toggle('dark', theme === 'dark');
    document.documentElement.classList.toggle('light', theme === 'light');
  }, [theme]);

  // Find currently selected service and method
  const selectedService = services.find(
    (s) => s.id === selectedServiceId || s.name === selectedServiceId || s.fullName === selectedServiceId
  );
  const selectedMethod = selectedService?.methods.find(
    (m) => m.name === selectedMethodId || m.id === selectedMethodId
  );

  return (
    <div className="flex flex-col h-screen w-screen overflow-hidden bg-slate-900 text-slate-100">
      <Navbar />

      <main className="flex-1 flex overflow-hidden">
        {activeView === 'explorer' && (
          <>
            <Sidebar
              services={services}
              isLoading={isLoading}
              onRefresh={loadData}
            />

            {selectedService && selectedMethod ? (
              <MethodExplorer service={selectedService} method={selectedMethod} />
            ) : (
              <HomeDashboard info={info} services={services} />
            )}
          </>
        )}

        {activeView === 'workflows' && <WorkflowStudio services={services} />}

        {activeView === 'history' && <HistoryView />}

        {activeView === 'settings' && <SettingsView />}
      </main>
    </div>
  );
};
