package com.zpi.testUtils.wiremock

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.zpi.infrastructure.rest.analysis.AnalysisResponseDTO
import com.zpi.infrastructure.rest.analysis.LockoutDTO
import com.zpi.infrastructure.rest.analysis.TwoFactorDTO
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

import java.time.LocalDateTime

class AnalysisMocks {
    private static final String analysisUri = "/api/authserver/analyse"
    private static final String loginFailUri = "/api/authserver/login-fail"
    private static final String lockoutUri = "/api/authserver/lockout.*"

    private static final LockoutDTO allowLogin = new LockoutDTO("ALLOW", LocalDateTime.now() as String)
    private static final LockoutDTO blockLogin = new LockoutDTO("BLOCK", LocalDateTime.now() as String)


    static void blockLogin2faNotRequired(WireMockServer mockService) throws IOException {
        def mapper = new ObjectMapper()
        mockService.stubFor(WireMock.post(WireMock.urlMatching(analysisUri))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(mapper.writeValueAsString(new AnalysisResponseDTO(blockLogin, new TwoFactorDTO(false))))
                )
        )
    }

    static void allowLogin2faNotRequired(WireMockServer mockService) throws IOException {
        def mapper = new ObjectMapper()
        mockService.stubFor(WireMock.post(WireMock.urlMatching(analysisUri))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(mapper.writeValueAsString(new AnalysisResponseDTO(allowLogin, new TwoFactorDTO(false))))
                )
        )
    }

    static void allowLogin2faRequired(WireMockServer mockService) throws IOException {
        def mapper = new ObjectMapper()
        mockService.stubFor(WireMock.post(WireMock.urlMatching(analysisUri))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(mapper.writeValueAsString(new AnalysisResponseDTO(allowLogin, new TwoFactorDTO(true))))
                )
        )
    }

    static void reportLoginFailure(WireMockServer mockService) throws IOException {
        mockService.stubFor(WireMock.post(WireMock.urlMatching(loginFailUri))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                )
        )
    }

    static void noLockout(WireMockServer mockService) {
        def mapper = new ObjectMapper()
        mockService.stubFor(WireMock.get(WireMock.urlMatching(lockoutUri))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(mapper.writeValueAsString(new LockoutDTO("ALLOW", LocalDateTime.now().toString())))
                )
        )
    }

    static void lockout(WireMockServer mockService) {
        def mapper = new ObjectMapper()
        mockService.stubFor(WireMock.get(WireMock.urlMatching(lockoutUri))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(mapper.writeValueAsString(new LockoutDTO("BLOCK", LocalDateTime.now().toString())))
                )
        )
    }
}
