package com.zpi.api.authCode;

import com.zpi.api.authCode.authenticationRequest.AuthenticationResponseDTO;
import com.zpi.api.authCode.consentRequest.ConsentRequestDTO;
import com.zpi.api.authCode.consentRequest.ConsentResponseDTO;
import com.zpi.api.authCode.ticketRequest.TicketRequestDTO;
import com.zpi.api.authCode.ticketRequest.TicketResponseDTO;
import com.zpi.api.common.dto.UserDTO;
import com.zpi.api.common.exception.ErrorResponseException;
import com.zpi.domain.authCode.AuthCodeService;
import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequest;
import com.zpi.domain.authCode.consentRequest.ErrorConsentResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthCodeController {
    private final AuthCodeService authCodeService;

    private static final String AUTH_PAGE_URI = "/signin";

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
            var response = new AuthenticationResponseDTO(authCodeService.validateAndFillRequest(request));
            location = response.toUrl(AUTH_PAGE_URI);
        } catch (ErrorResponseException e) {
            var response = e.getErrorResponse();
            location = response.toUrl(AUTH_PAGE_URI);
        }

        return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, location).body(null);
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

        try {
            var user = userDTO.toHashedDomain();
            var body = new TicketResponseDTO(authCodeService.authenticationTicket(user, request));
            return ResponseEntity.ok(body);
        } catch (ErrorResponseException e) {
            return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Empty userDTO", HttpStatus.BAD_REQUEST);
        }
    }

    private static final String ALLOW_PAGE_URI = "/allow";

    @PostMapping("/consent")
    public ResponseEntity<?> consent(@RequestBody ConsentRequestDTO requestDTO) {
        var request = requestDTO.toDomain();

        try {
            var response = new ConsentResponseDTO(authCodeService.consentRequest(request));
            var location = response.toUrl();
            return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, location).body(null);
        } catch (ErrorConsentResponseException e) {
            var location = e.toUrl(AUTH_PAGE_URI, request.getState());
            return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, location).body(null);
        }
    }
}
