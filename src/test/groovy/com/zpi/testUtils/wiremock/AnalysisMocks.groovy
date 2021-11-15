package com.zpi.testUtils.wiremock

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.zpi.infrastructure.rest.analysis.LoginFailedDTO
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

import java.time.LocalDateTime

class AnalysisMocks {
    private static final String analysisUri = "/api/authserver/analyse"
    private static final String lockoutUri = "/api/authserver/login-fail"

    static void positiveAnalysis(WireMockServer mockService) throws IOException {
        def mapper = new ObjectMapper()
        mockService.stubFor(WireMock.post(WireMock.urlMatching(analysisUri))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(mapper.writeValueAsString(new Boolean(true)))
                )
        )
    }

    static void negativeAnalysis(WireMockServer mockService) throws IOException {
        def mapper = new ObjectMapper()
        mockService.stubFor(WireMock.post(WireMock.urlMatching(analysisUri))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(mapper.writeValueAsString(new Boolean(false)))
                )
        )
    }

    static void lockoutAllow(WireMockServer mockService) throws IOException {
        def mapper = new ObjectMapper()
        mockService.stubFor(WireMock.post(WireMock.urlMatching(lockoutUri))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(mapper.writeValueAsString(new LoginFailedDTO("ALLOW", LocalDateTime.now().toString())))
                )
        )
    }

    static void blockLockout(WireMockServer mockService) throws IOException {
        def mapper = new ObjectMapper()
        mockService.stubFor(WireMock.post(WireMock.urlMatching(lockoutUri))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(mapper.writeValueAsString(new LoginFailedDTO("BLOCK", LocalDateTime.now().toString())))
                )
        )
    }

    static void lockoutNotAvailable(WireMockServer mockService) throws IOException {
        def mapper = new ObjectMapper()
        mockService.stubFor(WireMock.post(WireMock.urlMatching(lockoutUri))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
//                        .withBody(mapper.writeValueAsString(new LoginFailedDTO("BLOCK", LocalDateTime.now().toString())))
                )
        )
    }
}
