package com.zpi.e2e

import com.zpi.CommonFixtures
import com.zpi.MvcRequestHelpers
import com.zpi.ResultHelpers
import com.zpi.api.token.TokenRequestDTO
import com.zpi.domain.authCode.consentRequest.AuthCode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationCodeE2E extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private MvcRequestHelpers mvcHelpers

    private static final String tokenRequestUrl = "/api/token"

    def "should get token based on auth code obtained previously"() {
//        given:
//            def authCode = obtainAuthCode()
//            def redirectUri = "asdf"
//            def client = CommonFixtures.client()
//
//        and:
//            def request = TokenRequestDTO.builder()
//                    .grant_type("authorization_code")
//                    .code(authCode.getValue())
//                    .redirect_uri(redirectUri)
//                    .client_id(client.getId())
//                    .build()
//
//        when:
//            def response = mvcHelpers.postRequest(request, tokenRequestUrl)
//
//        then:
//            !ResultHelpers.attributeFromResult("access_code", response).isEmpty()
    }

//    private AuthCode obtainAuthCode() {
//    }
}
