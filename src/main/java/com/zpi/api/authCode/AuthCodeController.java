package com.zpi.api.authCode;

import com.zpi.api.authCode.consentRequest.ConsentRequestDTO;
import com.zpi.api.authCode.ticketRequest.TicketRequestDTO;
import com.zpi.api.common.dto.ErrorResponseDTO;
import com.zpi.api.common.dto.UserDTO;
import com.zpi.api.common.exception.ErrorResponseException;
import com.zpi.domain.authCode.AuthCodeService;
import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequest;
import com.zpi.domain.authCode.consentRequest.ErrorConsentResponseException;
import com.zpi.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthCodeController {
    private final AuthCodeService authCodeService;

    private static final String AUTH_PAGE_URI = "/signin";
    private static final String ALLOW_PAGE_URI = "/allow";

    @GetMapping("/authorize")
    public ResponseEntity<?> authorize(@RequestParam String client_id,
                                       @RequestParam(required = false) String redirect_uri,
                                       @RequestParam String response_type,
                                       @RequestParam(required = false) String scope,
                                       @RequestParam(required = false) String state) {

        var requestDTO = new TicketRequestDTO(client_id,
                redirect_uri,
                response_type,
                scope,
                state);
        var request = requestDTO.toDomain();
        return getRedirectInfo(request);
    }

    private ResponseEntity<?> getRedirectInfo(AuthenticationRequest request) {
        String location;

        try {
            request = authCodeService.validateAndFillRequest(request);

            location = createLocation(request);
        } catch (ErrorResponseException e) {
            var response = e.getErrorResponse();

            location = createErrorLocation(response);
        }

        return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, location).body(null);
    }

    private String createErrorLocation(ErrorResponseDTO response) {
        String location;
        location = UriComponentsBuilder.fromUriString(AUTH_PAGE_URI)
                .queryParam("error", response.getError())
                .queryParam("error_description", response.getError_description())
                .queryParam("state", response.getState())
                .toUriString();
        return location;
    }

    private String createLocation(AuthenticationRequest request) {
        String location;
        location = UriComponentsBuilder.fromUriString(AUTH_PAGE_URI)
                .queryParam("client_id", request.getClientId())
                .queryParam("redirect_uri", request.getRedirectUri())
                .queryParam("response_type", request.getResponseType())
                .queryParam("scope", request.getScope())
                .queryParam("state", request.getState())
                .toUriString();
        return location;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody UserDTO userDTO,
                                          @RequestParam String client_id,
                                          @RequestParam(required = false) String redirect_uri,
                                          @RequestParam String response_type,
                                          @RequestParam(required = false) String scope,
                                          @RequestParam String state) {

        var requestDTO = new TicketRequestDTO(client_id,
                redirect_uri,
                response_type,
                scope,
                state);
        var request = requestDTO.toDomain();

        var user = userToDomain(userDTO);

        return getAuthenticationTicket(request, user);
    }

    private User userToDomain(UserDTO userDTO) {
        return userDTO.toHashedDomain();
    }

    private ResponseEntity<?> getAuthenticationTicket(AuthenticationRequest request, User user) {
        try {
            var body = authCodeService.authenticationTicket(user, request);
            return ResponseEntity.ok(body);
        } catch (ErrorResponseException e) {
            return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/consent")
    public ResponseEntity<?> consent(@RequestBody ConsentRequestDTO requestDTO) {
        var request = requestDTO.toDomain();

        try {
            var response = authCodeService.consentRequest(request);
            var location = response.toUrl(response.getRedirectUri());
            return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, location).body(null);
        } catch (ErrorConsentResponseException e) {
            var location = e.toUrl(AUTH_PAGE_URI, request.getState());
            return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, location).body(null);
        }
    }
}
