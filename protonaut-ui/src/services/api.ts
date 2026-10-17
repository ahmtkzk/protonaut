import {
  EnvironmentInfo,
  ExecutionRequest,
  ExecutionResponse,
  MethodDescriptorModel,
  ProtoSourceModel,
  ServiceDescriptorModel,
  WorkflowDefinition,
  WorkflowExecutionResult,
} from '../types';

// Use relative API path to support deployment behind Ingress, Nginx, or any reverse proxy without hardcoded localhost!
const API_BASE = './api';

async function handleResponse<T>(res: Response): Promise<T> {
  if (!res.ok) {
    const errorText = await res.text();
    let message = errorText;
    try {
      const parsed = JSON.parse(errorText);
      message = parsed.message || parsed.error || errorText;
    } catch (_) {}
    throw new Error(message || `HTTP ${res.status}: ${res.statusText}`);
  }
  return res.json();
}

export const api = {
  async getInfo(): Promise<EnvironmentInfo> {
    const res = await fetch(`${API_BASE}/info`);
    return handleResponse<EnvironmentInfo>(res);
  },

  async getServices(): Promise<ServiceDescriptorModel[]> {
    const res = await fetch(`${API_BASE}/services`);
    return handleResponse<ServiceDescriptorModel[]>(res);
  },

  async getService(id: string): Promise<ServiceDescriptorModel> {
    const res = await fetch(`${API_BASE}/services/${encodeURIComponent(id)}`);
    return handleResponse<ServiceDescriptorModel>(res);
  },

  async getMethod(id: string): Promise<MethodDescriptorModel> {
    const res = await fetch(`${API_BASE}/methods/${encodeURIComponent(id)}`);
    return handleResponse<MethodDescriptorModel>(res);
  },

  async getProtos(): Promise<ProtoSourceModel[]> {
    const res = await fetch(`${API_BASE}/protos`);
    return handleResponse<ProtoSourceModel[]>(res);
  },

  async getProto(serviceId: string): Promise<ProtoSourceModel> {
    const res = await fetch(`${API_BASE}/protos/${encodeURIComponent(serviceId)}`);
    return handleResponse<ProtoSourceModel>(res);
  },

  async executeMethod(req: ExecutionRequest): Promise<ExecutionResponse> {
    const res = await fetch(`${API_BASE}/execute`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(req),
    });
    return handleResponse<ExecutionResponse>(res);
  },

  async generateDummyData(serviceId: string, methodId: string): Promise<{ json: string }> {
    try {
      const res = await fetch(`${API_BASE}/dummy`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ serviceId, methodId }),
      });
      if (!res.ok) {
        const fallbackRes = await fetch(
          `${API_BASE}/services/${encodeURIComponent(serviceId)}/methods/${encodeURIComponent(methodId)}/dummy`
        );
        if (fallbackRes.ok) {
          const data = await fallbackRes.json();
          if (data && typeof data.json === 'string') return data;
          return { json: JSON.stringify(data, null, 2) };
        }
      }
      const data = await res.json();
      if (data && typeof data.json === 'string') {
        return data;
      }
      return { json: JSON.stringify(data, null, 2) };
    } catch (_) {
      return { json: '{}' };
    }
  },

  async generateCode(params: {
    language: string;
    serviceId: string;
    methodId: string;
    sampleJson?: string;
    metadata?: Record<string, string>;
  }): Promise<{ code: string }> {
    const res = await fetch(`${API_BASE}/codegen`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(params),
    });
    return handleResponse<{ code: string }>(res);
  },

  async getWorkflows(): Promise<WorkflowDefinition[]> {
    const res = await fetch(`${API_BASE}/workflows`);
    return handleResponse<WorkflowDefinition[]>(res);
  },

  async saveWorkflow(wf: WorkflowDefinition): Promise<WorkflowDefinition> {
    const res = await fetch(`${API_BASE}/workflows`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(wf),
    });
    return handleResponse<WorkflowDefinition>(res);
  },

  async deleteWorkflow(id: string): Promise<{ success: boolean }> {
    const res = await fetch(`${API_BASE}/workflows/${encodeURIComponent(id)}`, {
      method: 'DELETE',
    });
    return handleResponse<{ success: boolean }>(res);
  },

  async executeWorkflow(wf: WorkflowDefinition): Promise<WorkflowExecutionResult> {
    const res = await fetch(`${API_BASE}/workflows/execute`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(wf),
    });
    return handleResponse<WorkflowExecutionResult>(res);
  },

  async getPublicConfig(): Promise<any> {
    const res = await fetch(`${API_BASE}/config/public`);
    return handleResponse<any>(res);
  },
};
