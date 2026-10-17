package io.protonaut.codegen.generators;

import io.protonaut.core.models.MethodDescriptorModel;
import io.protonaut.core.models.ServiceDescriptorModel;
import io.protonaut.core.spi.ClientCodeGenerator;

import java.util.Map;

public class PythonClientCodeGenerator implements ClientCodeGenerator {

    @Override
    public String getLanguage() {
        return "python";
    }

    @Override
    public String getDisplayName() {
        return "Python (grpcio)";
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
        String methodName = method.name();
        String target = (host != null ? host : "localhost") + ":" + (port > 0 ? port : 6570);

        StringBuilder sb = new StringBuilder();
        sb.append("import grpc\n");
        sb.append("from google.protobuf import json_format\n");
        sb.append("# Import compiled proto modules (e.g. user_pb2, user_pb2_grpc)\n\n");

        sb.append("def run():\n");
        sb.append("    # 1. Establish insecure channel\n");
        sb.append("    channel = grpc.insecure_channel('").append(target).append("')\n");
        sb.append("    stub = ").append(serviceName.toLowerCase()).append("_pb2_grpc.").append(serviceName).append("Stub(channel)\n\n");

        sb.append("    # 2. Metadata Headers\n");
        sb.append("    metadata = (\n");
        if (metadata != null && !metadata.isEmpty()) {
            for (Map.Entry<String, String> entry : metadata.entrySet()) {
                sb.append("        ('").append(entry.getKey()).append("', '").append(entry.getValue()).append("'),\n");
            }
        } else {
            sb.append("        ('authorization', 'Bearer <TOKEN>'),\n");
        }
        sb.append("    )\n\n");

        sb.append("    # 3. Create Request\n");
        sb.append("    request = ").append(serviceName.toLowerCase()).append("_pb2.").append(method.inputType().name()).append("()\n");
        if (sampleJson != null && !sampleJson.isBlank()) {
            sb.append("    json_format.Parse('").append(sampleJson.replace("'", "\\'").replace("\n", "").trim()).append("', request)\n\n");
        }

        sb.append("    # 4. Invoke RPC with Timeout\n");
        sb.append("    try:\n");
        if (method.type().isServerStreaming()) {
            sb.append("        responses = stub.").append(methodName).append("(request, metadata=metadata, timeout=30)\n");
            sb.append("        for response in responses:\n");
            sb.append("            print('Streamed item:', response)\n");
        } else {
            sb.append("        response = stub.").append(methodName).append("(request, metadata=metadata, timeout=30)\n");
            sb.append("        print('Success:', response)\n");
        }
        sb.append("    except grpc.RpcError as e:\n");
        sb.append("        print(f'RPC failed: {e.code()} - {e.details()}')\n\n");

        sb.append("if __name__ == '__main__':\n");
        sb.append("    run()\n");

        return sb.toString();
    }
}
