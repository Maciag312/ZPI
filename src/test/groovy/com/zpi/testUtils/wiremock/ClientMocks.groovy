package com.zpi.testUtils.wiremock

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.zpi.domain.rest.ams.Client
import com.zpi.testUtils.CommonFixtures
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

class ClientMocks {
    static void clientDetails(WireMockServer mockService) throws IOException {
        def client = new Client(List.of(CommonFixtures.redirectUri), CommonFixtures.clientId)
        def mapper = new ObjectMapper()
        mockService.stubFor(WireMock.get(WireMock.urlMatching("/api/authserver/client/.*"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(mapper.writeValueAsString(client))
                )
        )
    }
}
