import React, { useState, useEffect } from 'react';
import {
  Play,
  RotateCcw,
  Sparkles,
  Copy,
  Download,
  Clock,
  HardDrive,
  Check,
  AlertCircle,
  FileCode2,
  Code,
  Shield,
  Plus,
  Trash2,
  Send,
} from 'lucide-react';
import {
  MethodDescriptorModel,
  ServiceDescriptorModel,
  ExecutionResponse,
  ProtoSourceModel,
} from '../types';
import { api } from '../services/api';
import { useProtonautStore } from '../store/useProtonautStore';
import { SchemaTree } from './SchemaTree';

interface MethodExplorerProps {
  service: ServiceDescriptorModel;
  method: MethodDescriptorModel;
}

type TabType = 'overview' | 'request' | 'response' | 'schema' | 'proto' | 'code';

function generateDefaultPayload(message?: any, depth = 0): Record<string, any> {
  if (!message || !message.fields || depth > 3) return {};
  const result: Record<string, any> = {};
  const chosenOneofs = new Set<string>();

  for (const field of message.fields) {
    if (field.oneofName) {
      if (chosenOneofs.has(field.oneofName)) continue;
      chosenOneofs.add(field.oneofName);
    }
    const key = field.jsonName || field.name;
    result[key] = generateFieldValue(field, depth);
  }
  return result;
}

function generateFieldValue(field: any, depth: number): any {
  if (field.map) {
    return { key1: field.nestedType ? generateDefaultPayload(field.nestedType, depth + 1) : 'value' };
  }
  if (field.repeated) {
    const single = generateSingleValue(field, depth);
    return single !== undefined ? [single] : [];
  }
  return generateSingleValue(field, depth);
}

function generateSingleValue(field: any, depth: number): any {
  if (field.type === 'MESSAGE') {
    if (field.typeName === 'google.protobuf.Timestamp') return '2026-10-17T12:00:00Z';
    if (field.typeName === 'google.protobuf.Duration') return '60s';
    if (field.typeName === 'google.protobuf.StringValue') return 'sample text';
    if (field.typeName === 'google.protobuf.Int32Value' || field.typeName === 'google.protobuf.Int64Value') return 42;
    if (field.typeName === 'google.protobuf.BoolValue') return true;
    if (field.nestedType) return generateDefaultPayload(field.nestedType, depth + 1);
    return {};
  }
  if (field.type === 'ENUM') {
    if (field.enumType && field.enumType.values && field.enumType.values.length > 0) {
      const vals = field.enumType.values;
      if (vals.length > 1 && vals[0].number === 0) {
        return vals[1].name;
      }
      return vals[0].name;
    }
    return 'UNSPECIFIED';
  }
  const name = (field.name || '').toLowerCase();
  switch (field.type) {
    case 'STRING':
      if (name.includes('sku')) return 'SKU-LAPTOP-01';
      if (name.includes('order')) return 'ORD-' + Math.floor(1000 + Math.random() * 9000);
      if (name.includes('customer') || name.includes('user')) return 'CUST-1001';
      if (name.includes('email')) return 'user@example.com';
      if (name.includes('currency')) return 'USD';
      if (name.includes('id')) return name + '-101';
      if (name.includes('name')) return 'Sample Name';
      return 'sample_' + field.name;
    case 'DOUBLE':
    case 'FLOAT':
      if (name.includes('amount') || name.includes('price')) return 99.99;
      return 19.95;
    case 'INT32':
    case 'SINT32':
    case 'UINT32':
    case 'FIXED32':
    case 'SFIXED32':
      if (name.includes('quantity') || name.includes('count') || name.includes('qty')) return 2;
      return 10;
    case 'INT64':
    case 'SINT64':
    case 'UINT64':
    case 'FIXED64':
    case 'SFIXED64':
      if (name.includes('time') || name.includes('date')) return Date.now();
      return 1000;
    case 'BOOL':
      return true;
    case 'BYTES':
      return 'SGVsbG8gUHJvdG9uYXV0';
    default:
      return 'sample_value';
  }
}

export const MethodExplorer: React.FC<MethodExplorerProps> = ({ service, method }) => {
  const [activeTab, setActiveTab] = useState<TabType>('request');
  const [requestJson, setRequestJson] = useState('{\n  \n}');
  const [timeoutSeconds, setTimeoutSeconds] = useState(30);
  const [isExecuting, setIsExecuting] = useState(false);
  const [response, setResponse] = useState<ExecutionResponse | null>(null);
  const [protoSource, setProtoSource] = useState<ProtoSourceModel | null>(null);
  const [generatedCode, setGeneratedCode] = useState<string>('');
  const [selectedLanguage, setSelectedLanguage] = useState('java');
  const [copied, setCopied] = useState(false);

  // Metadata headers state
  const { savedHeaders, auth, addHistoryItem, environment } = useProtonautStore();
  const [headers, setHeaders] = useState<Array<{ key: string; value: string; enabled: boolean }>>(
    savedHeaders
  );

  // Auto-populate request payload whenever service or method changes
  useEffect(() => {
    // 1. Immediately calculate schema-based default payload
    if (method.inputType && method.inputType.fields && method.inputType.fields.length > 0) {
      const defaultPayload = generateDefaultPayload(method.inputType);
      setRequestJson(JSON.stringify(defaultPayload, null, 2));
    } else {
      setRequestJson('{\n  \n}');
    }

    // 2. Fetch server-generated dummy data with realistic backend heuristics
    api.generateDummyData(service.id, method.name)
      .then((res) => {
        if (res && res.json && res.json.trim() !== '{}' && res.json.trim() !== '') {
          setRequestJson(res.json);
        }
      })
      .catch((err) => {
        console.warn('Using schema-based payload default (server dummy offline):', err);
      });
  }, [service.id, method.name, method.fullMethodName]);

  // Load proto source on mount or method change
  useEffect(() => {
    api.getProto(service.id)
      .then(setProtoSource)
      .catch((err) => console.error('Error fetching proto:', err));
  }, [service.id]);

  // Load code generation when code tab active or language changed
  useEffect(() => {
    if (activeTab === 'code') {
      const headerMap: Record<string, string> = {};
      headers.filter((h) => h.enabled).forEach((h) => (headerMap[h.key] = h.value));

      api.generateCode({
        language: selectedLanguage,
        serviceId: service.id,
        methodId: method.name,
        sampleJson: requestJson,
        metadata: headerMap,
      })
        .then((res) => setGeneratedCode(res.code))
        .catch((err) => setGeneratedCode('// Error generating code: ' + err.message));
    }
  }, [activeTab, selectedLanguage, service.id, method.name, requestJson, headers]);

  // Handle Generate Dummy Data
  const handleGenerateDummy = async () => {
    try {
      const res = await api.generateDummyData(service.id, method.name);
      if (res && res.json && res.json.trim() !== '{}') {
        setRequestJson(res.json);
      } else if (method.inputType) {
        const defaultPayload = generateDefaultPayload(method.inputType);
        setRequestJson(JSON.stringify(defaultPayload, null, 2));
      }
    } catch (e: any) {
      if (method.inputType) {
        const defaultPayload = generateDefaultPayload(method.inputType);
        setRequestJson(JSON.stringify(defaultPayload, null, 2));
      }
    }
  };

  // Format Request JSON
  const handleFormatJson = () => {
    try {
      const parsed = JSON.parse(requestJson);
      setRequestJson(JSON.stringify(parsed, null, 2));
    } catch (_) {}
  };

  // Execute gRPC Call
  const handleExecute = async () => {
    setIsExecuting(true);
    const headerMap: Record<string, string> = {};

    // Auth injection
    if (auth.type === 'bearer' && auth.token) {
      headerMap['authorization'] = `Bearer ${auth.token}`;
    } else if (auth.type === 'basic' && auth.username) {
      const credentials = btoa(`${auth.username}:${auth.password || ''}`);
      headerMap['authorization'] = `Basic ${credentials}`;
    } else if (auth.type === 'apiKey' && auth.key && auth.value) {
      headerMap[auth.key] = auth.value;
    }

    // Custom headers
    headers.filter((h) => h.enabled && h.key).forEach((h) => {
      headerMap[h.key] = h.value;
    });

    try {
      const res = await api.executeMethod({
        serviceId: service.id,
        methodId: method.name,
        requestJson,
        metadata: headerMap,
        timeoutSeconds,
        environment,
      });

      setResponse(res);
      setActiveTab('response');

      addHistoryItem({
        id: res.requestId,
        serviceId: service.name,
        methodId: method.name,
        timestamp: Date.now(),
        durationMs: res.durationMs,
        status: res.grpcStatus,
        requestJson,
        responseJson: res.responseJson,
        environment,
      });
    } catch (err: any) {
      setResponse({
        requestId: 'err-' + Date.now(),
        serviceId: service.id,
        methodId: method.name,
        grpcStatus: { code: 2, name: 'UNKNOWN', description: err.message },
        durationMs: 0,
        responseSizeBytes: 0,
        responseMetadata: {},
        trailingMetadata: {},
        streamingResponses: [],
        success: false,
        errorMessage: err.message,
      });
      setActiveTab('response');
    } finally {
      setIsExecuting(false);
    }
  };

  const copyToClipboard = (text: string) => {
    navigator.clipboard.writeText(text);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const downloadText = (filename: string, content: string) => {
    const blob = new Blob([content], { type: 'text/plain;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    link.click();
    URL.revokeObjectURL(url);
  };

  // Support Tab key inside Request Editor
  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === 'Tab') {
      e.preventDefault();
      const target = e.currentTarget;
      const start = target.selectionStart;
      const end = target.selectionEnd;
      const value = target.value;
      const nextValue = value.substring(0, start) + '  ' + value.substring(end);
      setRequestJson(nextValue);
      setTimeout(() => {
        target.selectionStart = target.selectionEnd = start + 2;
      }, 0);
    }
  };

  const tabs: Array<{ id: TabType; label: string }> = [
    { id: 'overview', label: 'Overview' },
    { id: 'request', label: 'Request' },
    { id: 'response', label: 'Response' },
    { id: 'schema', label: 'Schema' },
    { id: 'proto', label: 'Proto' },
    { id: 'code', label: 'Client Code' },
  ];

  return (
    <div className="flex-1 flex flex-col h-[calc(100vh-3.5rem)] overflow-hidden bg-slate-900/30">
      {/* Method Header Bar */}
      <div className="px-6 py-4 border-b border-slate-800 bg-slate-950/40 backdrop-blur flex items-center justify-between">
        <div className="space-y-1">
          <div className="flex items-center space-x-2 text-xs">
            <span className="font-mono text-indigo-400 font-semibold">{service.packageName}</span>
            <span className="text-slate-400">/</span>
            <span className="text-slate-300 font-medium">{service.name}</span>
          </div>

          <div className="flex items-center space-x-3">
            <h2 className="text-xl font-bold text-white tracking-tight">{method.name}</h2>
            <span className="text-xs font-mono px-2 py-0.5 rounded bg-indigo-500/10 text-indigo-400 border border-indigo-500/20 font-semibold">
              {method.type}
            </span>
          </div>
        </div>

        {/* Execution Action Button */}
        <div className="flex items-center space-x-3">
          <div className="flex items-center space-x-2 bg-slate-900 border border-slate-800 rounded-lg px-2.5 py-1 text-xs">
            <Clock size={13} className="text-slate-400" />
            <span className="text-slate-400">Timeout:</span>
            <input
              type="number"
              value={timeoutSeconds}
              onChange={(e) => setTimeoutSeconds(Number(e.target.value))}
              className="w-12 bg-transparent text-slate-200 focus:outline-none font-mono text-center"
              min={1}
              max={300}
            />
            <span className="text-slate-400">s</span>
          </div>

          <button
            onClick={handleExecute}
            disabled={isExecuting}
            className="flex items-center space-x-2 bg-indigo-600 hover:bg-indigo-500 disabled:bg-indigo-800 text-white font-semibold text-xs px-4 py-2 rounded-lg shadow-lg shadow-indigo-600/20 transition-all hover:scale-102 cursor-pointer"
          >
            {isExecuting ? (
              <>
                <div className="w-3.5 h-3.5 border-2 border-white border-t-transparent rounded-full animate-spin" />
                <span>Executing...</span>
              </>
            ) : (
              <>
                <Send size={14} />
                <span>Execute Method</span>
              </>
            )}
          </button>
        </div>
      </div>

      {/* Tabs Bar */}
      <div className="px-6 border-b border-slate-800 bg-slate-950 flex items-center justify-between">
        <div className="flex space-x-1">
          {tabs.map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`px-4 py-2.5 text-xs font-medium border-b-2 transition-colors cursor-pointer ${
                activeTab === tab.id
                  ? 'border-indigo-500 text-indigo-400 bg-slate-900/40'
                  : 'border-transparent text-slate-400 hover:text-slate-200 hover:bg-slate-900/20'
              }`}
            >
              {tab.label}
              {tab.id === 'response' && response && (
                <span
                  className={`ml-2 text-[10px] font-mono px-1.5 py-0.2 rounded ${
                    response.success ? 'bg-emerald-500/20 text-emerald-400' : 'bg-rose-500/20 text-rose-400'
                  }`}
                >
                  {response.grpcStatus.name}
                </span>
              )}
            </button>
          ))}
        </div>
      </div>

      {/* Main Tab Content */}
      <div className="flex-1 overflow-y-auto p-6">
        {/* OVERVIEW TAB */}
        {activeTab === 'overview' && (
          <div className="max-w-4xl space-y-6">
            <div className="bg-slate-950 border border-slate-800 rounded-xl p-5 space-y-3">
              <h3 className="text-sm font-semibold text-white">Full Method Signature</h3>
              <div className="bg-slate-900 p-3 rounded-lg border border-slate-800 font-mono text-xs text-indigo-300">
                rpc {method.name}({method.type.includes('CLIENT') ? 'stream ' : ''}
                {method.inputType?.name}) returns ({method.type.includes('SERVER') ? 'stream ' : ''}
                {method.outputType?.name});
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="bg-slate-950 border border-slate-800 rounded-xl p-5 space-y-2">
                <span className="text-xs text-slate-400">Input Message Contract</span>
                <div className="text-sm font-semibold text-white font-mono">{method.inputType?.name}</div>
                <div className="text-xs text-slate-400">{method.inputType?.fields.length} fields defined</div>
              </div>

              <div className="bg-slate-950 border border-slate-800 rounded-xl p-5 space-y-2">
                <span className="text-xs text-slate-400">Output Message Contract</span>
                <div className="text-sm font-semibold text-white font-mono">{method.outputType?.name}</div>
                <div className="text-xs text-slate-400">{method.outputType?.fields.length} fields defined</div>
              </div>
            </div>
          </div>
        )}

        {/* REQUEST TAB */}
        {activeTab === 'request' && (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 h-full">
            {/* Left 2 Cols: JSON Payload Editor */}
            <div className="lg:col-span-2 flex flex-col space-y-3 h-full">
              <div className="flex items-center justify-between">
                <span className="text-xs font-semibold text-slate-300 uppercase tracking-wider">
                  Request Payload (JSON Protobuf)
                </span>
                <div className="flex items-center space-x-2">
                  <button
                    onClick={handleGenerateDummy}
                    className="flex items-center space-x-1.5 bg-indigo-600/10 hover:bg-indigo-600/20 text-indigo-400 border border-indigo-500/30 px-3 py-1 rounded text-xs transition-colors"
                  >
                    <Sparkles size={13} />
                    <span>Generate Dummy Data</span>
                  </button>
                  <button
                    onClick={handleFormatJson}
                    className="bg-slate-800 hover:bg-slate-700 text-slate-300 px-3 py-1 rounded text-xs border border-slate-700 transition-colors"
                  >
                    Format JSON
                  </button>
                </div>
              </div>

              <div className="relative flex-1 min-h-[360px] bg-slate-950 border border-slate-800 rounded-xl overflow-hidden focus-within:border-indigo-500 transition-colors">
                <textarea
                  value={requestJson}
                  onChange={(e) => setRequestJson(e.target.value)}
                  onKeyDown={handleKeyDown}
                  spellCheck={false}
                  className="w-full h-full p-4 bg-transparent font-mono text-xs text-slate-200 focus:outline-none resize-none leading-relaxed code-font"
                  placeholder="Enter JSON request payload..."
                />
              </div>
            </div>

            {/* Right Col: Metadata and Headers */}
            <div className="flex flex-col space-y-3">
              <div className="flex items-center justify-between">
                <span className="text-xs font-semibold text-slate-300 uppercase tracking-wider">
                  gRPC Metadata ({headers.length})
                </span>
                <button
                  onClick={() => setHeaders([...headers, { key: '', value: '', enabled: true }])}
                  className="text-xs text-indigo-400 hover:text-indigo-300 flex items-center space-x-1"
                >
                  <Plus size={13} />
                  <span>Add Row</span>
                </button>
              </div>

              {/* Dynamic Generator helper pills */}
              <div className="bg-slate-950 border border-slate-800 p-2.5 rounded-lg space-y-1.5">
                <span className="text-[11px] text-slate-400 font-medium">Quick Dynamic Tokens:</span>
                <div className="flex flex-wrap gap-1.5">
                  {['{{uuid}}', '{{timestamp}}', '{{isoTimestamp}}', '{{randomString}}'].map((tok) => (
                    <button
                      key={tok}
                      onClick={() => setHeaders([...headers, { key: 'x-' + tok.replace(/[{}]/g, ''), value: tok, enabled: true }])}
                      className="font-mono text-[10px] bg-slate-900 border border-slate-700/60 px-2 py-0.5 rounded text-indigo-300 hover:bg-slate-800 transition-colors"
                    >
                      {tok}
                    </button>
                  ))}
                </div>
              </div>

              {/* Metadata rows */}
              <div className="space-y-2 overflow-y-auto max-h-[380px] pr-1">
                {headers.map((h, i) => (
                  <div key={i} className="flex items-center space-x-2 bg-slate-950 p-2 rounded-lg border border-slate-800">
                    <input
                      type="checkbox"
                      checked={h.enabled}
                      onChange={(e) => {
                        const copy = [...headers];
                        copy[i].enabled = e.target.checked;
                        setHeaders(copy);
                      }}
                      className="rounded bg-slate-900 border-slate-700 text-indigo-600 focus:ring-0"
                    />
                    <input
                      type="text"
                      placeholder="header-name"
                      value={h.key}
                      onChange={(e) => {
                        const copy = [...headers];
                        copy[i].key = e.target.value;
                        setHeaders(copy);
                      }}
                      className="w-1/2 bg-slate-900 border border-slate-800 rounded px-2 py-1 text-xs text-slate-200 font-mono focus:outline-none focus:border-indigo-500"
                    />
                    <input
                      type="text"
                      placeholder="value or {{token}}"
                      value={h.value}
                      onChange={(e) => {
                        const copy = [...headers];
                        copy[i].value = e.target.value;
                        setHeaders(copy);
                      }}
                      className="w-1/2 bg-slate-900 border border-slate-800 rounded px-2 py-1 text-xs text-slate-200 font-mono focus:outline-none focus:border-indigo-500"
                    />
                    <button
                      onClick={() => setHeaders(headers.filter((_, idx) => idx !== i))}
                      className="text-slate-400 hover:text-rose-400 p-1"
                    >
                      <Trash2 size={13} />
                    </button>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {/* RESPONSE TAB */}
        {activeTab === 'response' && (
          <div className="space-y-4 max-w-5xl">
            {response ? (
              <div className="space-y-4">
                {/* Status Bar */}
                <div className="flex items-center justify-between bg-slate-950 p-4 rounded-xl border border-slate-800">
                  <div className="flex items-center space-x-4">
                    <div
                      className={`flex items-center space-x-2 px-3 py-1.5 rounded-lg text-xs font-mono font-bold ${
                        response.success
                          ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/30'
                          : 'bg-rose-500/10 text-rose-400 border border-rose-500/30'
                      }`}
                    >
                      {response.success ? <Check size={14} /> : <AlertCircle size={14} />}
                      <span>
                        {response.grpcStatus.code} : {response.grpcStatus.name}
                      </span>
                    </div>

                    <div className="flex items-center space-x-2 text-xs text-slate-400 font-mono">
                      <Clock size={13} />
                      <span>{response.durationMs} ms</span>
                    </div>

                    <div className="flex items-center space-x-2 text-xs text-slate-400 font-mono">
                      <HardDrive size={13} />
                      <span>{response.responseSizeBytes} bytes</span>
                    </div>
                  </div>

                  <div className="flex items-center space-x-2">
                    <button
                      onClick={() => copyToClipboard(response.responseJson || '')}
                      className="flex items-center space-x-1.5 bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs px-3 py-1.5 rounded-lg border border-slate-700 transition-colors"
                    >
                      <Copy size={13} />
                      <span>{copied ? 'Copied!' : 'Copy Response'}</span>
                    </button>

                    <button
                      onClick={() => downloadText(`${method.name}-response.json`, response.responseJson || '')}
                      className="flex items-center space-x-1.5 bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs px-3 py-1.5 rounded-lg border border-slate-700 transition-colors"
                    >
                      <Download size={13} />
                      <span>Download</span>
                    </button>
                  </div>
                </div>

                {/* Error Banner if RPC Failed */}
                {!response.success && (
                  <div className="bg-rose-500/10 border border-rose-500/30 p-4 rounded-xl text-xs space-y-1">
                    <div className="font-semibold text-rose-400">gRPC Invocation Failed:</div>
                    <div className="text-slate-300 font-mono">{response.errorMessage || response.grpcStatus.description}</div>
                  </div>
                )}

                {/* Response Body JSON */}
                <div className="bg-slate-950 border border-slate-800 rounded-xl p-4 font-mono text-xs text-slate-200 overflow-x-auto max-h-[500px]">
                  <pre>{response.responseJson || '// No response payload received'}</pre>
                </div>
              </div>
            ) : (
              <div className="p-12 text-center text-xs text-slate-400 border border-dashed border-slate-800 rounded-xl space-y-2">
                <Send size={24} className="mx-auto text-slate-400" />
                <p>No response yet. Click "Execute Method" to invoke {method.name}.</p>
              </div>
            )}
          </div>
        )}

        {/* SCHEMA TAB */}
        {activeTab === 'schema' && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 max-w-6xl">
            <div className="bg-slate-950 border border-slate-800 rounded-xl p-5 space-y-3">
              <h3 className="text-sm font-semibold text-white">Input Schema Contract</h3>
              <SchemaTree message={method.inputType} />
            </div>

            <div className="bg-slate-950 border border-slate-800 rounded-xl p-5 space-y-3">
              <h3 className="text-sm font-semibold text-white">Output Schema Contract</h3>
              <SchemaTree message={method.outputType} />
            </div>
          </div>
        )}

        {/* PROTO TAB */}
        {activeTab === 'proto' && (
          <div className="max-w-5xl space-y-3">
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-2">
                <span className="font-mono text-xs text-slate-300 font-semibold">{protoSource?.filename || 'schema.proto'}</span>
                {protoSource?.reconstructed ? (
                  <span className="text-[10px] font-mono px-2 py-0.5 rounded bg-amber-500/10 text-amber-400 border border-amber-500/20 font-semibold">
                    Reconstructed from Descriptors
                  </span>
                ) : (
                  <span className="text-[10px] font-mono px-2 py-0.5 rounded bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 font-semibold">
                    Original Source File
                  </span>
                )}
              </div>

              <div className="flex items-center space-x-2">
                <button
                  onClick={() => copyToClipboard(protoSource?.content || '')}
                  className="bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs px-3 py-1.5 rounded-lg border border-slate-700 flex items-center space-x-1.5"
                >
                  <Copy size={13} />
                  <span>{copied ? 'Copied' : 'Copy Proto'}</span>
                </button>
                <button
                  onClick={() => downloadText(protoSource?.filename || 'schema.proto', protoSource?.content || '')}
                  className="bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs px-3 py-1.5 rounded-lg border border-slate-700 flex items-center space-x-1.5"
                >
                  <Download size={13} />
                  <span>Download .proto</span>
                </button>
              </div>
            </div>

            <div className="bg-slate-950 border border-slate-800 rounded-xl p-4 font-mono text-xs text-indigo-200 overflow-x-auto max-h-[550px] leading-relaxed">
              <pre>{protoSource?.content || '// Loading proto source...'}</pre>
            </div>
          </div>
        )}

        {/* CODE GENERATION TAB */}
        {activeTab === 'code' && (
          <div className="max-w-5xl space-y-4">
            <div className="flex items-center justify-between">
              {/* Language selection pills */}
              <div className="flex items-center space-x-2">
                {[
                  { id: 'java', label: 'Java' },
                  { id: 'go', label: 'Go' },
                  { id: 'typescript', label: 'TypeScript' },
                  { id: 'python', label: 'Python' },
                  { id: 'csharp', label: 'C#' },
                ].map((lang) => (
                  <button
                    key={lang.id}
                    onClick={() => setSelectedLanguage(lang.id)}
                    className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-colors cursor-pointer ${
                      selectedLanguage === lang.id
                        ? 'bg-indigo-600 text-white shadow-sm'
                        : 'bg-slate-800 text-slate-400 hover:text-slate-200'
                    }`}
                  >
                    {lang.label}
                  </button>
                ))}
              </div>

              <button
                onClick={() => copyToClipboard(generatedCode)}
                className="bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs px-3 py-1.5 rounded-lg border border-slate-700 flex items-center space-x-1.5 cursor-pointer"
              >
                <Copy size={13} />
                <span>{copied ? 'Copied' : 'Copy Snippet'}</span>
              </button>
            </div>

            <div className="bg-slate-950 border border-slate-800 rounded-xl p-5 font-mono text-xs text-slate-200 overflow-x-auto max-h-[550px] leading-relaxed">
              <pre>{generatedCode || '// Generating client code...'}</pre>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
