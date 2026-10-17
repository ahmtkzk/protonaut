package io.protonaut.grpc.dummy;

import io.protonaut.core.models.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DummyDataGeneratorTest {

    @Test
    void testGenerateDummyDataForUserRequest() {
        MessageDescriptorModel requestModel = new MessageDescriptorModel(
                "ExampleRequest",
                "example.ExampleRequest",
                List.of(
                        FieldDescriptorModel.builder()
                                .name("first_name")
                                .jsonName("firstName")
                                .number(1)
                                .type(FieldValueType.STRING)
                                .build(),
                        FieldDescriptorModel.builder()
                                .name("last_name")
                                .jsonName("lastName")
                                .number(2)
                                .type(FieldValueType.STRING)
                                .build(),
                        FieldDescriptorModel.builder()
                                .name("age")
                                .jsonName("age")
                                .number(3)
                                .type(FieldValueType.INT32)
                                .build(),
                        FieldDescriptorModel.builder()
                                .name("active")
                                .jsonName("active")
                                .number(4)
                                .type(FieldValueType.BOOL)
                                .build()
                ),
                List.of(),
                List.of(),
                List.of(),
                null,
                "example.proto"
        );

        String json = DummyDataGenerator.generateJson(requestModel);

        assertThat(json).contains("\"firstName\" : \"Selam\"");
        assertThat(json).contains("\"lastName\" : \"Kazak\"");
        assertThat(json).contains("\"age\" : 28");
        assertThat(json).contains("\"active\" : true");
    }

    @Test
    void testOneofSelectionOnlyPicksFirst() {
        MessageDescriptorModel model = new MessageDescriptorModel(
                "AuthRequest",
                "auth.AuthRequest",
                List.of(
                        FieldDescriptorModel.builder()
                                .name("email")
                                .number(1)
                                .type(FieldValueType.STRING)
                                .oneofName("credential")
                                .build(),
                        FieldDescriptorModel.builder()
                                .name("phone")
                                .number(2)
                                .type(FieldValueType.STRING)
                                .oneofName("credential")
                                .build()
                ),
                List.of(),
                List.of(),
                List.of("credential"),
                null,
                "auth.proto"
        );

        String json = DummyDataGenerator.generateJson(model);

        assertThat(json).contains("email");
        assertThat(json).doesNotContain("phone");
    }
}
