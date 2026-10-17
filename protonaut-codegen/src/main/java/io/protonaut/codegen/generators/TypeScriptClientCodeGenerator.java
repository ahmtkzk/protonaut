package io.protonaut.codegen.generators;

import io.protonaut.core.models.MethodDescriptorModel;
import io.protonaut.core.models.ServiceDescriptorModel;
import io.protonaut.core.spi.ClientCodeGenerator;

import java.util.Map;

public class TypeScriptClientCodeGenerator implements ClientCodeGenerator {

    @Override
    public String getLanguage() {
        return "typescript";
    }

    @Override
    public String getDisplayName() {
        return "TypeScript (@grpc/grpc-js)";
    }

    @Override
    public String generate(
            ServiceDescriptorModel service,
            MethodDescriptorModel method,
            String sampleJson,
            Map<String, String> metadata,
            String host,
            int port
    ) {
        String serviceName = service.name();
        String methodName = Character.toLowerCase(method.name().charAt(0)) + method.name().substring(1);
        String target = (host != null ? host : "localhost") + ":" + (port > 0 ? port : 6570);

        StringBuilder sb = new StringBuilder();
        sb.append("import * as grpc from '@grpc/grpc-js';\n");
        sb.append("import * as protoLoader from '@grpc/proto-loader';\n\n");
        sb.append("// 1. Setup client connection\n");
        sb.append("const target = '").append(target).append("';\n");
        sb.append("const client = new proto.").append(serviceName).append("Client(\n");
        sb.append("  target,\n");
        sb.append("  grpc.credentials.createInsecure()\n");
        sb.append(");\n\n");

        sb.append("// 2. Setup Metadata\n");
        sb.append("const metadata = new grpc.Metadata();\n");
        if (metadata != null && !metadata.isEmpty()) {
            for (Map.Entry<String, String> entry : metadata.entrySet()) {
                sb.append("metadata.set('").append(entry.getKey()).append("', '").append(entry.getValue()).append("');\n");
            }
        } else {
            sb.append("metadata.set('authorization', 'Bearer <TOKEN>');\n");
        }
        sb.append("\n");

        sb.append("// 3. Construct Request\n");
        sb.append("const requestPayload = ").append(sampleJson != null && !sampleJson.isBlank() ? sampleJson.trim() : "{}").append(";\n\n");

        sb.append("// 4. Invoke Method\n");
        if (method.type().isServerStreaming()) {
            sb.append("const stream = client.").append(methodName).append("(requestPayload, metadata);\n");
            sb.append("stream.on('data', (response) => console.log('Received: ', response));\n");
            sb.append("stream.on('error', (err) => console.error('Stream Error: ', err));\n");
            sb.append("stream.on('end', () => console.log('Stream ended.'));\n");
        } else {
            sb.append("client.").append(methodName).append("(requestPayload, metadata, { deadline: Date.now() + 30000 }, (error, response) => {\n");
            sb.append("  if (error) {\n");
            sb.append("    console.error('RPC Error:', error.message, 'Code:', error.code);\n");
            sb.append("    return;\n");
            sb.append("  }\n");
            sb.append("  console.log('Success:', response);\n");
            sb.append("});\n");
        }

        return sb.toString();
    }
}
