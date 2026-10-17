package io.protonaut.codegen.generators;

import io.protonaut.core.models.MethodDescriptorModel;
import io.protonaut.core.models.ServiceDescriptorModel;
import io.protonaut.core.spi.ClientCodeGenerator;

import java.util.Map;

public class GoClientCodeGenerator implements ClientCodeGenerator {

    @Override
    public String getLanguage() {
        return "go";
    }

    @Override
    public String getDisplayName() {
        return "Go (google.golang.org/grpc)";
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
        String inputType = method.inputType() != null ? method.inputType().name() : "Request";

        StringBuilder sb = new StringBuilder();
        sb.append("package main\n\n");
        sb.append("import (\n");
        sb.append("\t\"context\"\n");
        sb.append("\t\"fmt\"\n");
        sb.append("\t\"log\"\n");
        sb.append("\t\"time\"\n\n");
        sb.append("\t\"google.golang.org/grpc\"\n");
        sb.append("\t\"google.golang.org/grpc/credentials/insecure\"\n");
        sb.append("\t\"google.golang.org/grpc/metadata\"\n");
        sb.append(")\n\n");

        sb.append("func main() {\n");
        sb.append("\t// 1. Establish gRPC Client Connection\n");
        sb.append("\taddress := \"").append(host != null ? host : "localhost").append(":")
                .append(port > 0 ? port : 6570).append("\"\n");
        sb.append("\tconn, err := grpc.NewClient(address, grpc.WithTransportCredentials(insecure.NewCredentials()))\n");
        sb.append("\tif err != nil {\n");
        sb.append("\t\tlog.Fatalf(\"Failed to connect: %v\", err)\n");
        sb.append("\t}\n");
        sb.append("\tdefer conn.Close()\n\n");

        sb.append("\t// 2. Attach Custom Metadata and Timeout Context\n");
        sb.append("\tctx, cancel := context.WithTimeout(context.Background(), 30*time.Second)\n");
        sb.append("\tdefer cancel()\n\n");

        sb.append("\tmd := metadata.New(map[string]string{\n");
        if (metadata != null && !metadata.isEmpty()) {
            for (Map.Entry<String, String> entry : metadata.entrySet()) {
                sb.append("\t\t\"").append(entry.getKey()).append("\": \"").append(entry.getValue()).append("\",\n");
            }
        } else {
            sb.append("\t\t\"authorization\": \"Bearer <TOKEN>\",\n");
        }
        sb.append("\t})\n");
        sb.append("\tctx = metadata.NewOutgoingContext(ctx, md)\n\n");

        sb.append("\t// 3. Initialize Client Stub and Execute RPC\n");
        sb.append("\tclient := pb.New").append(serviceName).append("Client(conn)\n");
        sb.append("\treq := &pb.").append(inputType).append("{}\n\n");

        if (method.type().isServerStreaming()) {
            sb.append("\tstream, err := client.").append(methodName).append("(ctx, req)\n");
            sb.append("\tif err != nil {\n");
            sb.append("\t\tlog.Fatalf(\"RPC error: %v\", err)\n");
            sb.append("\t}\n");
            sb.append("\tfor {\n");
            sb.append("\t\tresp, err := stream.Recv()\n");
            sb.append("\t\tif err != nil { break }\n");
            sb.append("\t\tfmt.Printf(\"Stream response: %v\\n\", resp)\n");
            sb.append("\t}\n");
        } else {
            sb.append("\tresp, err := client.").append(methodName).append("(ctx, req)\n");
            sb.append("\tif err != nil {\n");
            sb.append("\t\tlog.Fatalf(\"RPC invocation error: %v\", err)\n");
            sb.append("\t}\n");
            sb.append("\tfmt.Printf(\"Response: %v\\n\", resp)\n");
        }

        sb.append("}\n");
        return sb.toString();
    }
}
