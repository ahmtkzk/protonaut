package io.protonaut.example.services;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.protonaut.example.user.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    private final Map<String, User> usersById = new ConcurrentHashMap<>();
    private final Map<String, String> usernameToId = new ConcurrentHashMap<>();
    private final Map<String, String> emailToId = new ConcurrentHashMap<>();

    public UserServiceImpl() {
        seedSampleUsers();
    }

    private void seedSampleUsers() {
        User alice = User.newBuilder()
                .setId("usr-001")
                .setUsername("alice")
                .setEmail("alice@protonaut.io")
                .setFullName("Alice Vance")
                .setRole(UserRole.ROLE_ADMIN)
                .setStatus(UserStatus.STATUS_ACTIVE)
                .setAddress(Address.newBuilder()
                        .setStreet("123 Cloud Way")
                        .setCity("San Francisco")
                        .setState("CA")
                        .setZipCode("94105")
                        .setCountry("USA")
                        .build())
                .setProfile(UserProfile.newBuilder()
                        .setBio("Lead Platform Engineer")
                        .setAvatarUrl("https://avatars.githubusercontent.com/u/1")
                        .putPreferences("theme", "dark")
                        .putPreferences("notifications", "email")
                        .build())
                .addTags("platform")
                .addTags("admin")
                .setCreatedAt(Instant.now().minusSeconds(86400 * 30).toEpochMilli())
                .setUpdatedAt(Instant.now().minusSeconds(86400 * 2).toEpochMilli())
                .build();

        User bob = User.newBuilder()
                .setId("usr-002")
                .setUsername("bob")
                .setEmail("bob@protonaut.io")
                .setFullName("Bob Martin")
                .setRole(UserRole.ROLE_USER)
                .setStatus(UserStatus.STATUS_ACTIVE)
                .setAddress(Address.newBuilder()
                        .setStreet("456 Microservices Ave")
                        .setCity("Seattle")
                        .setState("WA")
                        .setZipCode("98101")
                        .setCountry("USA")
                        .build())
                .setProfile(UserProfile.newBuilder()
                        .setBio("Senior Backend Developer")
                        .putPreferences("theme", "system")
                        .build())
                .addTags("backend")
                .addTags("developer")
                .setCreatedAt(Instant.now().minusSeconds(86400 * 10).toEpochMilli())
                .setUpdatedAt(Instant.now().minusSeconds(86400).toEpochMilli())
                .build();

        usersById.put(alice.getId(), alice);
        usernameToId.put(alice.getUsername(), alice.getId());
        emailToId.put(alice.getEmail(), alice.getId());

        usersById.put(bob.getId(), bob);
        usernameToId.put(bob.getUsername(), bob.getId());
        emailToId.put(bob.getEmail(), bob.getId());
    }

    @Override
    public void getUser(GetUserRequest request, StreamObserver<User> responseObserver) {
        User user = null;
        if (request.hasId()) {
            user = usersById.get(request.getId());
        } else if (request.hasUsername()) {
            String id = usernameToId.get(request.getUsername());
            if (id != null) user = usersById.get(id);
        } else if (request.hasEmail()) {
            String id = emailToId.get(request.getEmail());
            if (id != null) user = usersById.get(id);
        }

        if (user != null) {
            responseObserver.onNext(user);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("User not found matching specified criteria")
                    .asRuntimeException());
        }
    }

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<User> responseObserver) {
        if (request.getUsername().isBlank() || request.getEmail().isBlank()) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Username and email are required")
                    .asRuntimeException());
            return;
        }

        if (usernameToId.containsKey(request.getUsername())) {
            responseObserver.onError(Status.ALREADY_EXISTS
                    .withDescription("Username already exists: " + request.getUsername())
                    .asRuntimeException());
            return;
        }

        String id = "usr-" + UUID.randomUUID().toString().substring(0, 8);
        long now = Instant.now().toEpochMilli();

        User newUser = User.newBuilder()
                .setId(id)
                .setUsername(request.getUsername())
                .setEmail(request.getEmail())
                .setFullName(request.getFullName())
                .setRole(request.getRole() != UserRole.ROLE_UNSPECIFIED ? request.getRole() : UserRole.ROLE_USER)
                .setStatus(UserStatus.STATUS_ACTIVE)
                .setAddress(request.getAddress())
                .setProfile(request.getProfile())
                .addAllTags(request.getTagsList())
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .build();

        usersById.put(id, newUser);
        usernameToId.put(newUser.getUsername(), id);
        emailToId.put(newUser.getEmail(), id);

        responseObserver.onNext(newUser);
        responseObserver.onCompleted();
    }

    @Override
    public void updateUser(UpdateUserRequest request, StreamObserver<User> responseObserver) {
        User existing = usersById.get(request.getId());
        if (existing == null) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("User with id " + request.getId() + " not found")
                    .asRuntimeException());
            return;
        }

        User.Builder builder = existing.toBuilder()
                .setUpdatedAt(Instant.now().toEpochMilli());

        if (!request.getEmail().isBlank()) {
            emailToId.remove(existing.getEmail());
            builder.setEmail(request.getEmail());
            emailToId.put(request.getEmail(), existing.getId());
        }
        if (!request.getFullName().isBlank()) {
            builder.setFullName(request.getFullName());
        }
        if (request.getStatus() != UserStatus.STATUS_UNSPECIFIED) {
            builder.setStatus(request.getStatus());
        }
        if (request.hasAddress()) {
            builder.setAddress(request.getAddress());
        }

        User updated = builder.build();
        usersById.put(updated.getId(), updated);

        responseObserver.onNext(updated);
        responseObserver.onCompleted();
    }

    @Override
    public void listUsers(ListUsersRequest request, StreamObserver<User> responseObserver) {
        int limit = request.getLimit() > 0 ? request.getLimit() : 50;
        int offset = Math.max(0, request.getOffset());

        List<User> filtered = usersById.values().stream()
                .filter(u -> request.getRole() == UserRole.ROLE_UNSPECIFIED || u.getRole() == request.getRole())
                .filter(u -> request.getStatus() == UserStatus.STATUS_UNSPECIFIED || u.getStatus() == request.getStatus())
                .skip(offset)
                .limit(limit)
                .toList();

        for (User user : filtered) {
            responseObserver.onNext(user);
        }
        responseObserver.onCompleted();
    }
}
