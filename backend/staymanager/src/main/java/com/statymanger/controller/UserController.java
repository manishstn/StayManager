package com.statymanger.controller;

import com.statymanger.dto.Response;
import com.statymanger.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "User Controller", description = "Endpoints for user management and profile retrieval")
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get all users", description = "Admin only access to retrieve all registered users.")
    public ResponseEntity<Response> getAllUsers() {
        Response response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-by-id/{userId}")
    @PreAuthorize("hasAuthority('ADMIN') or #userId == authentication.principal.id")
    @Operation(summary = "Get user by ID", description = "Retrieve specific user details. Restricted to Admin or the owner.")
    public ResponseEntity<Response> getUserById(@PathVariable("userId") String userId) {
        Response response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete user", description = "Admin only. Permanently removes a user from the system.")
    public ResponseEntity<Response> deleteUser(@PathVariable("userId") String userId) {
        Response response = userService.deleteUser(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-logged-in-profile-info")
    @Operation(summary = "Get current profile", description = "Retrieve profile information for the currently authenticated user.")
    public ResponseEntity<Response> getLoggedInUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Response response = userService.getUserInfo(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-user-bookings/{userId}")
    @PreAuthorize("hasAuthority('ADMIN') or #userId == authentication.principal.id")
    @Operation(summary = "Get user booking history")
    public ResponseEntity<Response> getUserBookingHistory(@PathVariable("userId") String userId) {
        Response response = userService.getUserBookingHistory(userId);
        return ResponseEntity.ok(response);
    }
}