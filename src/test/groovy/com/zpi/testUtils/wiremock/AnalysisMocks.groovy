package com.zpi.testUtils.wiremock

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

class AnalysisMocks {
    private static final String baseUri = "/api/authserver/analyse"

    static void setupMockPositiveAnalysisResponse(WireMockServer mockService) throws IOException {
        def mapper = new ObjectMapper()
        mockService.stubFor(WireMock.post(WireMock.urlMatching(baseUri))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(mapper.writeValueAsString(new Boolean(true)))
                )
        )
    }

    static void setupMockNegativeAnalysisResponse(WireMockServer mockService) throws IOException {
        def mapper = new ObjectMapper()
        mockService.stubFor(WireMock.post(WireMock.urlMatching(baseUri))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(mapper.writeValueAsString(new Boolean(false)))
                )
        )
    }
}
