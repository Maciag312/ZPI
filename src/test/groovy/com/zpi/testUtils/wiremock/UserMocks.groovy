package com.zpi.testUtils.wiremock

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

class UserMocks {
    static void userRegister(WireMockServer mockService) throws IOException {
        def mapper = new ObjectMapper()
        mockService.stubFor(WireMock.post(WireMock.urlMatching("/api/authserver/user/register"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(mapper.writeValueAsString(new Boolean(true)))
                )
        )
    }

    static void userAuthenticate(WireMockServer mockService) throws IOException {
        def mapper = new ObjectMapper()
        mockService.stubFor(WireMock.post(WireMock.urlMatching("/api/users/authenticate"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(mapper.writeValueAsString(new Boolean(true)))
                )
        )
    }
}
