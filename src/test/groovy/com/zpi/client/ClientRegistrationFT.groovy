package com.zpi.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.zpi.CommonFixtures
import com.zpi.CommonHelpers
import com.zpi.client.api.ClientDTO
import com.zpi.client.domain.ClientRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class ClientRegistrationFT extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private ClientRepository repository;

    @Autowired
    private CommonHelpers commonHelpers

    private static final String url = "/api/client/register"

    def "should register new client"() {
        given:
            def client = CommonFixtures.clientDTO()

        when:
            def request = commonHelpers.postRequest(client, url)

        then:
            request.andExpect(status().isCreated())

        and:
            def domainClient = client.toDomain()
            def result = repository.getByKey(domainClient.getId()).get()

            result == domainClient
    }

    def "should return conflict on clientId crash"() {
        given:
            def clientA = CommonFixtures.clientDTO()

            def clientB = ClientDTO.builder()
                    .id(clientA.getId())
                    .availableRedirectUri(new ArrayList<String>())
                    .build()

        when:
            commonHelpers.postRequest(clientA, url)
            def request = commonHelpers.postRequest(clientB, url)

        then:
            request.andExpect(status().isConflict())

        and:
            def domainClientA = clientA.toDomain()
            def result = repository.getByKey(domainClientA.getId()).get()

            result == domainClientA
    }

    def "should return bad request on null client"() {
        given:
            def client = null

        when:
            def request = commonHelpers.postRequest(client, url)

        then:
            request.andExpect(status().isBadRequest())
    }

    def "should return bad request on malformed client"() {
        given:
            def client = ClientDTO.builder().id("").build()

        when:
            def request = commonHelpers.postRequest(client, url)

        then:
            request.andExpect(status().isBadRequest())

        and:
            def response = request.andReturn().getResponse().getContentAsString()
            response.contains("id must not be empty") && response.contains("availableRedirectUri must not be null")
    }
}
