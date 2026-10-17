export type MethodType = 'UNARY' | 'SERVER_STREAMING' | 'CLIENT_STREAMING' | 'BIDI_STREAMING';

export type FieldValueType =
  | 'DOUBLE'
  | 'FLOAT'
  | 'INT64'
  | 'UINT64'
  | 'INT32'
  | 'FIXED64'
  | 'FIXED32'
  | 'BOOL'
  | 'STRING'
  | 'GROUP'
  | 'MESSAGE'
  | 'BYTES'
  | 'UINT32'
  | 'ENUM'
  | 'SFIXED32'
  | 'SFIXED64'
  | 'SINT32'
  | 'SINT64';

export interface EnumValueModel {
  name: string;
  number: number;
  description?: string;
}

export interface EnumDescriptorModel {
  name: string;
  fullName: string;
  values: EnumValueModel[];
  description?: string;
}

export interface FieldDescriptorModel {
  name: string;
  jsonName: string;
  number: number;
  type: FieldValueType;
  typeName?: string;
  repeated: boolean;
  map: boolean;
  optional: boolean;
  required: boolean;
  oneofIndex?: number;
  oneofName?: string;
  description?: string;
  defaultValue?: any;
  mapKeyType?: FieldValueType;
  mapValueType?: FieldValueType;
  mapValueTypeName?: string;
  nestedType?: MessageDescriptorModel;
  enumType?: EnumDescriptorModel;
}

export interface MessageDescriptorModel {
  name: string;
  fullName: string;
  fields: FieldDescriptorModel[];
  enums: EnumDescriptorModel[];
  nestedMessages: MessageDescriptorModel[];
  oneofs: string[];
  description?: string;
  protoFile?: string;
}

export interface MethodDescriptorModel {
  id: string;
  name: string;
  fullMethodName: string;
  serviceName: string;
  packageName: string;
  type: MethodType;
  inputType: MessageDescriptorModel;
  outputType: MessageDescriptorModel;
  description?: string;
  protoFile?: string;
}

export interface ServiceDescriptorModel {
  id: string;
  name: string;
  fullName: string;
  packageName: string;
  methods: MethodDescriptorModel[];
  description?: string;
  protoFile?: string;
}

export interface GrpcStatusModel {
  code: number;
  name: string;
  description?: string;
  cause?: string;
}

export interface ExecutionRequest {
  serviceId: string;
  methodId: string;
  requestJson: string;
  metadata?: Record<string, string>;
  timeoutSeconds?: number;
  environment?: string;
}

export interface ExecutionResponse {
  requestId: string;
  serviceId: string;
  methodId: string;
  responseJson?: string;
  grpcStatus: GrpcStatusModel;
  durationMs: number;
  responseSizeBytes: number;
  responseMetadata: Record<string, string>;
  trailingMetadata: Record<string, string>;
  streamingResponses: string[];
  success: boolean;
  errorMessage?: string;
}

export interface ProtoSourceModel {
  filename: string;
  packageName: string;
  content: string;
  reconstructed: boolean;
  syntax: string;
}

export interface AssertionRule {
  target: string;
  operator: string;
  expectedValue: string;
  description?: string;
}

export type FailureStrategy = 'STOP_ON_FAILURE' | 'CONTINUE_ON_FAILURE' | 'RETRY' | 'SKIP' | 'ABORT';
export type WorkflowStatus = 'PENDING' | 'WAITING' | 'RUNNING' | 'SUCCESS' | 'FAILED' | 'SKIPPED' | 'CANCELLED';

export interface WorkflowStep {
  id: string;
  name: string;
  serviceId: string;
  methodId: string;
  requestJson: string;
  metadata?: Record<string, string>;
  timeoutSeconds?: number;
  condition?: string;
  assertions?: AssertionRule[];
  failureStrategy?: FailureStrategy;
  retryCount?: number;
  retryBackoffMs?: number;
  nodePosition?: { x: number; y: number };
}

export interface WorkflowStepResult {
  stepId: string;
  stepName: string;
  status: WorkflowStatus;
  requestJson?: string;
  responseJson?: string;
  grpcStatus?: GrpcStatusModel;
  durationMs: number;
  assertionsPassed: boolean;
  assertionErrors: string[];
  errorMessage?: string;
  attempts: number;
}

export interface WorkflowExecutionResult {
  executionId: string;
  workflowId: string;
  workflowName: string;
  status: WorkflowStatus;
  durationMs: number;
  stepResults: WorkflowStepResult[];
  contextVariables: Record<string, any>;
  startedAt: number;
  completedAt: number;
  errorMessage?: string;
}

export interface WorkflowDefinition {
  id: string;
  name: string;
  description?: string;
  steps: WorkflowStep[];
  variables?: Record<string, string>;
  createdAt?: number;
  updatedAt?: number;
}

export interface EnvironmentInfo {
  projectName: string;
  teamName: string;
  environment: string;
  version: string;
  description: string;
  grpcPort: number;
  restPort: number;
  restEnabled: boolean;
  host: string;
  status: string;
  usefulLinks?: Record<string, string>;
}

export interface HistoryItem {
  id: string;
  serviceId: string;
  methodId: string;
  timestamp: number;
  durationMs: number;
  status: GrpcStatusModel;
  requestJson: string;
  responseJson?: string;
  environment: string;
}
