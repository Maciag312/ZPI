package com.zpi.testUtils.wiremock

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.zpi.domain.rest.ams.Client
import com.zpi.infrastructure.rest.ams.AuthConfigurationDTO
import com.zpi.infrastructure.rest.ams.TokenConfigurationDTO
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

    static void clientTokenConfig(WireMockServer mockService) throws IOException {
        def client = new AuthConfigurationDTO(new TokenConfigurationDTO(1000L, "DJASFDNUS812DAMNXMANSDHQHW83183JD18JJ1HFG8JXJ12JSH1XCHBUJ28X2JH12J182XJH1F3H1JS81G7RESHD13H71"))
        def mapper = new ObjectMapper()
        mockService.stubFor(WireMock.get(WireMock.urlMatching("/api/authserver/config"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(mapper.writeValueAsString(client))
                )
        )
    }
}
