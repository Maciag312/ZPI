package com.zpi

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import com.zpi.api.authCode.ticketRequest.TicketRequestDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.web.util.UriComponentsBuilder

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@Component
class CommonHelpers {
    private final MockMvc mockMvc

    @Autowired
    private ObjectMapper mapper

    CommonHelpers(MockMvc mockMvc) {
        this.mockMvc = mockMvc
    }

    def <T> ResultActions postRequest(T payload, String url) throws Exception {
        def value = mapper.writeValueAsString(payload);
        def result = mockMvc.perform(
                post(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(value)
        )
        return result
    }

    def <T> ResultActions postRequest(String url) throws Exception {
        return mockMvc.perform(
                post(url)        )
    }

    def <T> ResultActions getRequest(String url) throws Exception {
        return mockMvc.perform(
                get(url)
        )
    }


    static String authParametersToUrl(TicketRequestDTO request, String uri) {
        return UriComponentsBuilder.fromUriString(uri)
                .queryParam("client_id", request.getClientId())
                .queryParam("redirect_uri", request.getRedirectUri())
                .queryParam("response_type", request.getResponseType())
                .queryParam("scope", request.getScope())
                .queryParam("state", request.getState())
                .toUriString()
    }

    static String attributeFromResult(String attribute, ResultActions result) throws UnsupportedEncodingException {
        def response = result.andReturn().getResponse()
        def value = JsonPath.read(response.getContentAsString(), String.format("\$.%s", attribute))
        return value.toString()
    }
}
