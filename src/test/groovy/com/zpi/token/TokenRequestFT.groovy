package com.zpi.token

import com.zpi.CommonFixtures
import com.zpi.MvcRequestHelpers
import com.zpi.ResultHelpers
import com.zpi.api.token.TokenRequestDTO
import com.zpi.domain.authCode.consentRequest.AuthCode
import com.zpi.domain.authCode.consentRequest.authCodePersister.AuthCodeRepository
import com.zpi.domain.client.ClientRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

@SpringBootTest
@AutoConfigureMockMvc
class TokenRequestFT extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private MvcRequestHelpers mvcRequestHelpers

    @Autowired
    private ClientRepository clientRepository

    @Autowired
    private AuthCodeRepository authCodeRepository

    private static final String baseUrl = "/api/token"

    def cleanup() {
        clientRepository.clear()
        authCodeRepository.clear()
    }

    def "should return token when provided data is correct"() {
        given:
            def authCode = UUID.randomUUID().toString()
            def redirectUri = CommonFixtures.redirectUri
            def client = CommonFixtures.client()

            def request = TokenRequestDTO.builder()
                    .grant_type(CommonFixtures.grantType)
                    .code(authCode)
                    .redirect_uri(redirectUri)
                    .client_id(client.getId())
                    .build()

        and:
            clientRepository.save(client.getId(), client)
            authCodeRepository.save(authCode, new AuthCode(authCode))

        when:
            def response = mvcRequestHelpers.postRequest(request, baseUrl)

        then:
            !ResultHelpers.attributeFromResult("access_token", response).isEmpty()
    }

    def "should return error on incorrect data"() {
        given:
            def authCode = "codeasdffdsa"
            def redirectUri = "asdgsdfgasdfalk"

            def client = CommonFixtures.client()
            def request = TokenRequestDTO.builder()
                    .grant_type(null)
                    .code(authCode)
                    .redirect_uri(redirectUri)
                    .client_id(client.getId())
                    .build()

        and:
            clientRepository.save(client.getId(), client)

        when:
            def response = mvcRequestHelpers.postRequest(request, baseUrl)

        then:
            !ResultHelpers.attributeFromResult("error", response).isEmpty()
            !ResultHelpers.attributeFromResult("error_description", response).isEmpty()
    }
}
