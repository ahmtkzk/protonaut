package io.protonaut.grpc.descriptors;

import com.google.protobuf.Descriptors;
import io.grpc.ServerMethodDefinition;
import io.grpc.ServerServiceDefinition;
import io.grpc.protobuf.ProtoFileDescriptorSupplier;
import io.grpc.protobuf.ProtoMethodDescriptorSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceDescriptorExtractor {
    private static final Logger log = LoggerFactory.getLogger(ServiceDescriptorExtractor.class);

    public static Descriptors.ServiceDescriptor extract(ServerServiceDefinition serviceDefinition) {
        if (serviceDefinition == null) return null;

        Object schemaDesc = serviceDefinition.getServiceDescriptor().getSchemaDescriptor();
        String serviceFullName = serviceDefinition.getServiceDescriptor().getName();

        // 1. Check if schema descriptor is a ProtoFileDescriptorSupplier
        if (schemaDesc instanceof ProtoFileDescriptorSupplier fileSupplier) {
            Descriptors.FileDescriptor fileDesc = fileSupplier.getFileDescriptor();
            if (fileDesc != null) {
                for (Descriptors.ServiceDescriptor service : fileDesc.getServices()) {
                    if (service.getFullName().equals(serviceFullName) || service.getName().equals(serviceFullName)) {
                        return service;
                    }
                }
            }
        }

        // 2. Check individual methods for ProtoMethodDescriptorSupplier
        for (ServerMethodDefinition<?, ?> methodDef : serviceDefinition.getMethods()) {
            Object methodSchema = methodDef.getMethodDescriptor().getSchemaDescriptor();
            if (methodSchema instanceof ProtoMethodDescriptorSupplier methodSupplier) {
                Descriptors.MethodDescriptor protoMethod = methodSupplier.getMethodDescriptor();
                if (protoMethod != null && protoMethod.getService() != null) {
                    return protoMethod.getService();
                }
            }
        }

        log.debug("Could not extract protobuf Descriptors.ServiceDescriptor for service {}", serviceFullName);
        return null;
    }
}
