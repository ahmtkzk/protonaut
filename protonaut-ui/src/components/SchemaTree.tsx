import React from 'react';
import { MessageDescriptorModel, FieldDescriptorModel } from '../types';
import { ChevronRight, ChevronDown, List, Hash, Tag, FileText } from 'lucide-react';

interface SchemaTreeProps {
  message: MessageDescriptorModel;
}

export const SchemaTree: React.FC<SchemaTreeProps> = ({ message }) => {
  if (!message) return <div className="text-xs text-slate-400">No schema descriptor available</div>;

  return (
    <div className="space-y-4">
      <div className="flex items-center space-x-2 text-xs text-slate-400 pb-2 border-b border-slate-800">
        <span className="font-semibold text-slate-200">{message.name}</span>
        <span className="font-mono text-[11px] text-slate-400">({message.fullName})</span>
      </div>

      <div className="space-y-2">
        {message.fields.map((field) => (
          <FieldRow key={field.number} field={field} />
        ))}
      </div>
    </div>
  );
};

const FieldRow: React.FC<{ field: FieldDescriptorModel }> = ({ field }) => {
  const [expanded, setExpanded] = React.useState(false);

  const hasNested = field.nestedType || (field.enumType && field.enumType.values.length > 0);

  return (
    <div className="bg-slate-900/60 border border-slate-800/80 rounded-lg p-2.5 text-xs">
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-2">
          {hasNested && (
            <button
              onClick={() => setExpanded(!expanded)}
              className="text-slate-400 hover:text-slate-200 transition-colors"
            >
              {expanded ? <ChevronDown size={14} /> : <ChevronRight size={14} />}
            </button>
          )}

          <span className="font-mono font-semibold text-slate-200">{field.jsonName || field.name}</span>

          <span className="font-mono text-[10px] text-slate-400 bg-slate-800 px-1.5 py-0.5 rounded">
            #{field.number}
          </span>

          {field.oneofName && (
            <span className="text-[10px] font-mono px-1.5 py-0.5 rounded bg-amber-500/10 text-amber-400 border border-amber-500/20">
              oneof {field.oneofName}
            </span>
          )}
        </div>

        <div className="flex items-center space-x-2 font-mono text-[11px]">
          {field.repeated && (
            <span className="px-1.5 py-0.5 rounded bg-purple-500/10 text-purple-400 border border-purple-500/20">
              repeated
            </span>
          )}

          {field.map && (
            <span className="px-1.5 py-0.5 rounded bg-cyan-500/10 text-cyan-400 border border-cyan-500/20">
              map&lt;{field.mapKeyType}, {field.mapValueType}&gt;
            </span>
          )}

          <span className="text-indigo-400 font-semibold">
            {field.typeName ? field.typeName.split('.').pop() : field.type.toLowerCase()}
          </span>
        </div>
      </div>

      {/* Nested Message or Enum expansion */}
      {expanded && (
        <div className="mt-3 pl-4 border-l-2 border-indigo-500/30 space-y-2">
          {field.nestedType && (
            <div>
              <div className="text-[11px] font-semibold text-slate-400 mb-1">
                Nested Message: {field.nestedType.name}
              </div>
              <div className="space-y-1.5">
                {field.nestedType.fields.map((nestedF) => (
                  <FieldRow key={nestedF.number} field={nestedF} />
                ))}
              </div>
            </div>
          )}

          {field.enumType && (
            <div className="space-y-1">
              <div className="text-[11px] font-semibold text-slate-400">
                Enum Values: {field.enumType.name}
              </div>
              <div className="flex flex-wrap gap-1.5">
                {field.enumType.values.map((v) => (
                  <span
                    key={v.number}
                    className="font-mono text-[10px] bg-slate-950 px-2 py-0.5 rounded border border-slate-800 text-slate-300"
                  >
                    {v.name} = {v.number}
                  </span>
                ))}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};
