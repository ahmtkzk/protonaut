package io.protonaut.codegen.generators;

import io.protonaut.core.models.MethodDescriptorModel;
import io.protonaut.core.models.ServiceDescriptorModel;
import io.protonaut.core.spi.ClientCodeGenerator;

import java.util.Map;

public class CSharpClientCodeGenerator implements ClientCodeGenerator {

    @Override
    public String getLanguage() {
        return "csharp";
    }

    @Override
    public String getDisplayName() {
        return "C# (Grpc.Net.Client)";
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
        String address = "http://" + (host != null ? host : "localhost") + ":" + (port > 0 ? port : 6570);

        StringBuilder sb = new StringBuilder();
        sb.append("using System;\n");
        sb.append("using System.Threading.Tasks;\n");
        sb.append("using Grpc.Core;\n");
        sb.append("using Grpc.Net.Client;\n\n");

        sb.append("class Program {\n");
        sb.append("    static async Task Main(string[] args) {\n");
        sb.append("        // 1. Establish GrpcChannel\n");
        sb.append("        using var channel = GrpcChannel.ForAddress(\"").append(address).append("\");\n");
        sb.append("        var client = new ").append(serviceName).append(".").append(serviceName).append("Client(channel);\n\n");

        sb.append("        // 2. Attach Metadata Headers\n");
        sb.append("        var headers = new Metadata();\n");
        if (metadata != null && !metadata.isEmpty()) {
            for (Map.Entry<String, String> entry : metadata.entrySet()) {
                sb.append("        headers.Add(\"").append(entry.getKey()).append("\", \"").append(entry.getValue()).append("\");\n");
            }
        } else {
            sb.append("        headers.Add(\"authorization\", \"Bearer <TOKEN>\");\n");
        }
        sb.append("\n");

        sb.append("        // 3. Prepare Request and Call RPC\n");
        sb.append("        var request = new ").append(method.inputType().name()).append("();\n");
        sb.append("        try {\n");
        if (method.type().isServerStreaming()) {
            sb.append("            using var streamingCall = client.").append(methodName).append("(request, headers, deadline: DateTime.UtcNow.AddSeconds(30));\n");
            sb.append("            await foreach (var response in streamingCall.ResponseStream.ReadAllAsync()) {\n");
            sb.append("                Console.WriteLine($\"Streamed: {response}\");\n");
            sb.append("            }\n");
        } else {
            sb.append("            var response = await client.").append(methodName).append("Async(request, headers, deadline: DateTime.UtcNow.AddSeconds(30));\n");
            sb.append("            Console.WriteLine($\"Response: {response}\");\n");
        }
        sb.append("        } catch (RpcException ex) {\n");
        sb.append("            Console.WriteLine($\"RPC Error: {ex.Status.StatusCode} - {ex.Status.Detail}\");\n");
        sb.append("        }\n");
        sb.append("    }\n");
        sb.append("}\n");

        return sb.toString();
    }
}
