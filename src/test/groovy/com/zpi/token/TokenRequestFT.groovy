package com.zpi.token

import com.zpi.CommonFixtures
import com.zpi.MvcRequestHelpers
import com.zpi.ResultHelpers
import com.zpi.api.token.TokenRequestDTO
import com.zpi.domain.authCode.consentRequest.AuthCode
import com.zpi.domain.authCode.consentRequest.authCodePersister.AuthCodeRepository
import com.zpi.domain.organization.client.ClientRepository
import com.zpi.domain.token.tokenRequest.tokenIssuer.configProvider.TokenIssuerConfigProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

@SpringBootTest(properties = "security.jwt.token.secret-key=AJASFDNUS812DAMNXMANSDHQHW83183JD18JJ1HFG8JXJ12JSH1XCHBUJ28X2JH12J182XJH1F3H1JS81G7RESHD13H71")
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

    @Autowired
    private TokenIssuerConfigProvider configProvider

    private static final String baseUrl = "/api/token"

    def cleanup() {
        clientRepository.clear()
        authCodeRepository.clear()
    }

    def "should return correct response when provided data is correct"() {
        given:
            def authCode = UUID.randomUUID().toString()
            def redirectUri = CommonFixtures.redirectUri
            def client = CommonFixtures.client()

            def request = new TokenRequestDTO(CommonFixtures.grantType, authCode, redirectUri, client.getId())

        and:
            clientRepository.save(client.getId(), client)
            authCodeRepository.save(authCode, new AuthCode(authCode, ""))

        when:
            def response = mvcRequestHelpers.postRequest(request, baseUrl)

        then:
            !ResultHelpers.attributeFromResult("access_token", response).isEmpty()
            ResultHelpers.attributeFromResult("token_type", response) == "Bearer"
            !ResultHelpers.attributeFromResult("refresh_token", response).isEmpty()
            ResultHelpers.attributeFromResult("expires_in", response) == "3600"

        and:
            ResultHelpers.headerFromResult("Cache-Control", response).contains("no-store")
            ResultHelpers.headerFromResult("Pragma", response).contains("no-cache")
    }

    def "should return error on incorrect data"() {
        given:
            def authCode = "codeasdffdsa"
            def redirectUri = "asdgsdfgasdfalk"

            def client = CommonFixtures.client()
            def request = new TokenRequestDTO(null, authCode, redirectUri, client.getId())

        and:
            clientRepository.save(client.getId(), client)

        when:
            def response = mvcRequestHelpers.postRequest(request, baseUrl)

        then:
            !ResultHelpers.attributeFromResult("error", response).isEmpty()
            !ResultHelpers.attributeFromResult("error_description", response).isEmpty()
    }
}
