import React, { useState, useEffect } from 'react';
import {
  Play,
  Plus,
  Trash2,
  Save,
  Download,
  Upload,
  CheckCircle2,
  XCircle,
  Clock,
  ArrowDown,
  Layers,
  Settings2,
  Copy,
  ChevronRight,
} from 'lucide-react';
import {
  WorkflowDefinition,
  WorkflowStep,
  WorkflowExecutionResult,
  WorkflowStepResult,
  ServiceDescriptorModel,
  AssertionRule,
} from '../types';
import { api } from '../services/api';

interface WorkflowStudioProps {
  services: ServiceDescriptorModel[];
}

export const WorkflowStudio: React.FC<WorkflowStudioProps> = ({ services }) => {
  const [workflows, setWorkflows] = useState<WorkflowDefinition[]>([]);
  const [selectedWorkflow, setSelectedWorkflow] = useState<WorkflowDefinition | null>(null);
  const [selectedStepIndex, setSelectedStepIndex] = useState<number | null>(null);
  const [isExecuting, setIsExecuting] = useState(false);
  const [executionResult, setExecutionResult] = useState<WorkflowExecutionResult | null>(null);
  const [inspectedStepResult, setInspectedStepResult] = useState<WorkflowStepResult | null>(null);

  // Load available workflows from backend
  useEffect(() => {
    loadWorkflows();
  }, []);

  const loadWorkflows = async () => {
    try {
      const list = await api.getWorkflows();
      setWorkflows(list);
      if (list.length > 0 && !selectedWorkflow) {
        setSelectedWorkflow(list[0]);
        if (list[0].steps.length > 0) {
          setSelectedStepIndex(0);
        }
      }
    } catch (e) {
      console.error('Failed to load workflows:', e);
    }
  };

  const handleSaveWorkflow = async () => {
    if (!selectedWorkflow) return;
    try {
      const saved = await api.saveWorkflow(selectedWorkflow);
      setSelectedWorkflow(saved);
      loadWorkflows();
    } catch (e: any) {
      alert('Save failed: ' + e.message);
    }
  };

  const handleExecuteWorkflow = async () => {
    if (!selectedWorkflow) return;
    setIsExecuting(true);
    setExecutionResult(null);
    setInspectedStepResult(null);

    try {
      const result = await api.executeWorkflow(selectedWorkflow);
      setExecutionResult(result);
      if (result.stepResults.length > 0) {
        setInspectedStepResult(result.stepResults[0]);
      }
    } catch (e: any) {
      alert('Execution failed: ' + e.message);
    } finally {
      setIsExecuting(false);
    }
  };

  const handleAddStep = () => {
    if (!selectedWorkflow) return;
    const firstSvc = services[0];
    const firstMethod = firstSvc?.methods[0];

    const newStep: WorkflowStep = {
      id: 'step_' + (selectedWorkflow.steps.length + 1),
      name: `${firstMethod ? firstMethod.name : 'New RPC Call'}`,
      serviceId: firstSvc ? firstSvc.name : 'Service',
      methodId: firstMethod ? firstMethod.name : 'Method',
      requestJson: '{\n  \n}',
      metadata: {},
      timeoutSeconds: 30,
      assertions: [],
      failureStrategy: 'STOP_ON_FAILURE',
      retryCount: 0,
      retryBackoffMs: 500,
    };

    const updated = {
      ...selectedWorkflow,
      steps: [...selectedWorkflow.steps, newStep],
    };
    setSelectedWorkflow(updated);
    setSelectedStepIndex(updated.steps.length - 1);
  };

  const handleDeleteStep = (index: number) => {
    if (!selectedWorkflow) return;
    const updatedSteps = selectedWorkflow.steps.filter((_, i) => i !== index);
    setSelectedWorkflow({ ...selectedWorkflow, steps: updatedSteps });
    setSelectedStepIndex(updatedSteps.length > 0 ? 0 : null);
  };

  const currentStep =
    selectedWorkflow && selectedStepIndex !== null
      ? selectedWorkflow.steps[selectedStepIndex]
      : null;

  return (
    <div className="flex-1 flex h-[calc(100vh-3.5rem)] overflow-hidden bg-slate-900/30">
      {/* Workflow Canvas / List Panel */}
      <div className="w-80 border-r border-slate-800 bg-slate-950 flex flex-col shrink-0">
        <div className="p-4 border-b border-slate-800 flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <span className="font-bold text-xs uppercase tracking-wider text-slate-300">
              Workflows ({workflows.length})
            </span>
          </div>

          <button
            onClick={() => {
              const newWf: WorkflowDefinition = {
                id: 'wf_' + Date.now(),
                name: 'New Custom Workflow',
                description: 'Chained gRPC workflow',
                steps: [],
                variables: {},
              };
              setSelectedWorkflow(newWf);
              setSelectedStepIndex(null);
            }}
            className="text-xs bg-indigo-600/20 text-indigo-400 hover:bg-indigo-600/30 px-2 py-1 rounded border border-indigo-500/30 flex items-center space-x-1"
          >
            <Plus size={13} />
            <span>New</span>
          </button>
        </div>

        {/* Workflow items */}
        <div className="flex-1 overflow-y-auto p-2 space-y-1">
          {workflows.map((wf) => (
            <button
              key={wf.id}
              onClick={() => {
                setSelectedWorkflow(wf);
                setSelectedStepIndex(wf.steps.length > 0 ? 0 : null);
                setExecutionResult(null);
              }}
              className={`w-full text-left p-3 rounded-lg border text-xs transition-colors ${
                selectedWorkflow?.id === wf.id
                  ? 'bg-slate-900 border-indigo-500/40 text-white shadow-sm'
                  : 'bg-slate-950/40 border-slate-800/80 text-slate-400 hover:bg-slate-900/40 hover:text-slate-200'
              }`}
            >
              <div className="font-semibold truncate">{wf.name}</div>
              <div className="text-[11px] text-slate-400 mt-1 flex items-center justify-between">
                <span>{wf.steps.length} chained steps</span>
                <span className="font-mono text-[10px] text-indigo-400">{wf.id}</span>
              </div>
            </button>
          ))}
        </div>
      </div>

      {/* Main Studio View */}
      {selectedWorkflow ? (
        <div className="flex-1 flex flex-col overflow-hidden">
          {/* Workflow Header Action Bar */}
          <div className="px-6 py-3.5 border-b border-slate-800 bg-slate-950/40 flex items-center justify-between">
            <div className="space-y-0.5">
              <input
                type="text"
                value={selectedWorkflow.name}
                onChange={(e) =>
                  setSelectedWorkflow({ ...selectedWorkflow, name: e.target.value })
                }
                className="text-base font-bold text-white bg-transparent focus:outline-none focus:border-b border-indigo-500"
              />
              <div className="text-xs text-slate-400">
                {selectedWorkflow.description || 'Chained multi-step gRPC execution'}
              </div>
            </div>

            <div className="flex items-center space-x-2.5">
              <button
                onClick={handleSaveWorkflow}
                className="flex items-center space-x-1.5 bg-slate-800 hover:bg-slate-700 text-slate-200 px-3 py-1.5 rounded-lg border border-slate-700 text-xs font-semibold transition-colors"
              >
                <Save size={13} />
                <span>Save</span>
              </button>

              <button
                onClick={handleExecuteWorkflow}
                disabled={isExecuting || selectedWorkflow.steps.length === 0}
                className="flex items-center space-x-2 bg-indigo-600 hover:bg-indigo-500 disabled:bg-indigo-800 text-white px-4 py-1.5 rounded-lg text-xs font-semibold shadow-lg shadow-indigo-600/20 transition-all hover:scale-102 cursor-pointer"
              >
                {isExecuting ? (
                  <>
                    <div className="w-3.5 h-3.5 border-2 border-white border-t-transparent rounded-full animate-spin" />
                    <span>Running Flow...</span>
                  </>
                ) : (
                  <>
                    <Play size={13} />
                    <span>Run Workflow</span>
                  </>
                )}
              </button>
            </div>
          </div>

          {/* Workflow Visual Steps & Step Inspector */}
          <div className="flex-1 flex overflow-hidden">
            {/* Visual Steps Timeline Column */}
            <div className="w-96 border-r border-slate-800 overflow-y-auto p-6 space-y-4 bg-slate-950/20">
              <div className="flex items-center justify-between text-xs font-semibold text-slate-300 uppercase tracking-wider">
                <span>Execution Steps ({selectedWorkflow.steps.length})</span>
                <button
                  onClick={handleAddStep}
                  className="text-indigo-400 hover:text-indigo-300 flex items-center space-x-1"
                >
                  <Plus size={13} />
                  <span>Add Step</span>
                </button>
              </div>

              {/* Start Node */}
              <div className="flex items-center justify-center py-2 px-4 rounded-full bg-slate-800/60 border border-slate-700 text-[11px] font-mono text-slate-300 uppercase tracking-widest mx-auto w-28">
                START
              </div>

              <div className="flex justify-center text-slate-400">
                <ArrowDown size={14} />
              </div>

              {/* Steps Sequence */}
              {selectedWorkflow.steps.map((step, idx) => {
                const isSelected = selectedStepIndex === idx;
                const stepExec = executionResult?.stepResults.find((s) => s.stepId === step.id);

                return (
                  <React.Fragment key={step.id}>
                    <div
                      onClick={() => {
                        setSelectedStepIndex(idx);
                        if (stepExec) setInspectedStepResult(stepExec);
                      }}
                      className={`relative p-3.5 rounded-xl border transition-all cursor-pointer ${
                        isSelected
                          ? 'bg-slate-900 border-indigo-500 shadow-md shadow-indigo-500/10'
                          : 'bg-slate-950 border-slate-800 hover:border-slate-700'
                      }`}
                    >
                      {/* Status indicator badge */}
                      {stepExec && (
                        <div className="absolute right-3 top-3">
                          {stepExec.status === 'SUCCESS' && (
                            <span className="flex items-center space-x-1 text-[10px] text-emerald-400 font-mono bg-emerald-500/10 px-2 py-0.5 rounded border border-emerald-500/20">
                              <CheckCircle2 size={11} />
                              <span>{stepExec.durationMs}ms</span>
                            </span>
                          )}
                          {stepExec.status === 'FAILED' && (
                            <span className="flex items-center space-x-1 text-[10px] text-rose-400 font-mono bg-rose-500/10 px-2 py-0.5 rounded border border-rose-500/20">
                              <XCircle size={11} />
                              <span>Failed</span>
                            </span>
                          )}
                        </div>
                      )}

                      <div className="text-xs font-semibold text-white">{step.name}</div>
                      <div className="font-mono text-[11px] text-indigo-400 mt-1">
                        {step.serviceId}.{step.methodId}
                      </div>

                      {step.assertions && step.assertions.length > 0 && (
                        <div className="mt-2 text-[10px] text-slate-400 bg-slate-900/60 px-2 py-1 rounded border border-slate-800">
                          {step.assertions.length} assertion{step.assertions.length > 1 ? 's' : ''} defined
                        </div>
                      )}
                    </div>

                    <div className="flex justify-center text-slate-400">
                      <ArrowDown size={14} />
                    </div>
                  </React.Fragment>
                );
              })}

              {/* End Node */}
              <div className="flex items-center justify-center py-2 px-4 rounded-full bg-slate-800/60 border border-slate-700 text-[11px] font-mono text-slate-300 uppercase tracking-widest mx-auto w-24">
                END
              </div>
            </div>

            {/* Step Config / Inspector Panel */}
            <div className="flex-1 overflow-y-auto p-6 space-y-6">
              {currentStep ? (
                <div className="max-w-3xl space-y-5">
                  <div className="flex items-center justify-between pb-3 border-b border-slate-800">
                    <div>
                      <h3 className="text-base font-bold text-white">{currentStep.name}</h3>
                      <div className="text-xs text-slate-400 font-mono">ID: {currentStep.id}</div>
                    </div>

                    <button
                      onClick={() => selectedStepIndex !== null && handleDeleteStep(selectedStepIndex)}
                      className="text-xs text-rose-400 hover:text-rose-300 flex items-center space-x-1 bg-rose-500/10 px-2.5 py-1 rounded border border-rose-500/20"
                    >
                      <Trash2 size={13} />
                      <span>Remove Step</span>
                    </button>
                  </div>

                  {/* Service & Method Selectors */}
                  <div className="grid grid-cols-2 gap-4">
                    <div className="space-y-1.5">
                      <label className="text-xs font-medium text-slate-300">Target Service</label>
                      <select
                        value={currentStep.serviceId}
                        onChange={(e) => {
                          const updated = [...selectedWorkflow.steps];
                          updated[selectedStepIndex!].serviceId = e.target.value;
                          setSelectedWorkflow({ ...selectedWorkflow, steps: updated });
                        }}
                        className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-200 focus:outline-none focus:border-indigo-500"
                      >
                        {services.map((s) => (
                          <option key={s.id} value={s.name}>
                            {s.name}
                          </option>
                        ))}
                      </select>
                    </div>

                    <div className="space-y-1.5">
                      <label className="text-xs font-medium text-slate-300">Target Method</label>
                      <select
                        value={currentStep.methodId}
                        onChange={(e) => {
                          const updated = [...selectedWorkflow.steps];
                          updated[selectedStepIndex!].methodId = e.target.value;
                          setSelectedWorkflow({ ...selectedWorkflow, steps: updated });
                        }}
                        className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-200 focus:outline-none focus:border-indigo-500"
                      >
                        {services
                          .find((s) => s.name === currentStep.serviceId)
                          ?.methods.map((m) => (
                            <option key={m.name} value={m.name}>
                              {m.name} ({m.type})
                            </option>
                          ))}
                      </select>
                    </div>
                  </div>

                  {/* Request JSON Payload with variable interpolation support */}
                  <div className="space-y-2">
                    <div className="flex items-center justify-between">
                      <label className="text-xs font-medium text-slate-300">
                        Request Body (Supports <code className="text-indigo-400 font-mono">{'{{steps.stepId.response.path}}'}</code>)
                      </label>
                      <span className="text-[11px] text-slate-400 font-mono">JSON</span>
                    </div>

                    <textarea
                      value={currentStep.requestJson}
                      onChange={(e) => {
                        const updated = [...selectedWorkflow.steps];
                        updated[selectedStepIndex!].requestJson = e.target.value;
                        setSelectedWorkflow({ ...selectedWorkflow, steps: updated });
                      }}
                      className="w-full h-36 bg-slate-950 border border-slate-800 rounded-xl p-3 font-mono text-xs text-slate-200 focus:outline-none focus:border-indigo-500 leading-relaxed"
                    />
                  </div>

                  {/* Assertions Manager */}
                  <div className="space-y-3">
                    <div className="flex items-center justify-between">
                      <label className="text-xs font-medium text-slate-300">
                        Response Assertions & Conditions
                      </label>
                      <button
                        onClick={() => {
                          const updated = [...selectedWorkflow.steps];
                          const assertions = updated[selectedStepIndex!].assertions || [];
                          updated[selectedStepIndex!].assertions = [
                            ...assertions,
                            { target: 'response.status', operator: '==', expectedValue: 'OK' },
                          ];
                          setSelectedWorkflow({ ...selectedWorkflow, steps: updated });
                        }}
                        className="text-xs text-indigo-400 hover:text-indigo-300 flex items-center space-x-1"
                      >
                        <Plus size={13} />
                        <span>Add Assertion</span>
                      </button>
                    </div>

                    <div className="space-y-2">
                      {currentStep.assertions?.map((rule, ruleIdx) => (
                        <div key={ruleIdx} className="flex items-center space-x-2 bg-slate-950 p-2.5 rounded-lg border border-slate-800">
                          <input
                            type="text"
                            placeholder="response.field"
                            value={rule.target}
                            onChange={(e) => {
                              const updated = [...selectedWorkflow.steps];
                              updated[selectedStepIndex!].assertions![ruleIdx].target = e.target.value;
                              setSelectedWorkflow({ ...selectedWorkflow, steps: updated });
                            }}
                            className="w-1/3 bg-slate-900 border border-slate-800 rounded px-2.5 py-1 text-xs text-slate-200 font-mono focus:outline-none focus:border-indigo-500"
                          />
                          <select
                            value={rule.operator}
                            onChange={(e) => {
                              const updated = [...selectedWorkflow.steps];
                              updated[selectedStepIndex!].assertions![ruleIdx].operator = e.target.value;
                              setSelectedWorkflow({ ...selectedWorkflow, steps: updated });
                            }}
                            className="bg-slate-900 border border-slate-800 rounded px-2 py-1 text-xs text-slate-200 font-mono focus:outline-none"
                          >
                            <option value="==">==</option>
                            <option value="!=">!=</option>
                            <option value=">">&gt;</option>
                            <option value="<">&lt;</option>
                            <option value=">=">&gt;=</option>
                            <option value="<=">&lt;=</option>
                            <option value="contains">contains</option>
                            <option value="exists">exists</option>
                          </select>
                          <input
                            type="text"
                            placeholder="expected value"
                            value={rule.expectedValue}
                            onChange={(e) => {
                              const updated = [...selectedWorkflow.steps];
                              updated[selectedStepIndex!].assertions![ruleIdx].expectedValue = e.target.value;
                              setSelectedWorkflow({ ...selectedWorkflow, steps: updated });
                            }}
                            className="w-1/3 bg-slate-900 border border-slate-800 rounded px-2.5 py-1 text-xs text-slate-200 font-mono focus:outline-none focus:border-indigo-500"
                          />
                          <button
                            onClick={() => {
                              const updated = [...selectedWorkflow.steps];
                              updated[selectedStepIndex!].assertions = updated[selectedStepIndex!].assertions?.filter(
                                (_, idx) => idx !== ruleIdx
                              );
                              setSelectedWorkflow({ ...selectedWorkflow, steps: updated });
                            }}
                            className="text-slate-400 hover:text-rose-400 p-1"
                          >
                            <Trash2 size={13} />
                          </button>
                        </div>
                      ))}
                    </div>
                  </div>

                  {/* Step Result Details if executed */}
                  {inspectedStepResult && (
                    <div className="bg-slate-950 border border-slate-800 rounded-xl p-5 space-y-3">
                      <div className="flex items-center justify-between text-xs">
                        <span className="font-semibold text-white">Execution Result</span>
                        <span className={`font-mono font-bold ${inspectedStepResult.status === 'SUCCESS' ? 'text-emerald-400' : 'text-rose-400'}`}>
                          {inspectedStepResult.status} ({inspectedStepResult.durationMs}ms)
                        </span>
                      </div>

                      <div className="font-mono text-xs text-slate-200 bg-slate-900 p-3 rounded-lg overflow-x-auto max-h-48">
                        <pre>{inspectedStepResult.responseJson || '// No response body'}</pre>
                      </div>
                    </div>
                  )}
                </div>
              ) : (
                <div className="p-12 text-center text-xs text-slate-400 border border-dashed border-slate-800 rounded-xl">
                  Select a workflow step from the timeline to configure request parameters and assertions.
                </div>
              )}
            </div>
          </div>
        </div>
      ) : (
        <div className="flex-1 flex items-center justify-center text-xs text-slate-400">
          Select or create a workflow to begin visual chaining.
        </div>
      )}
    </div>
  );
};
