package com.zpi

import com.jayway.jsonpath.JsonPath
import com.zpi.api.authCode.ticketRequest.TicketRequestDTO
import org.springframework.test.web.servlet.ResultActions
import org.springframework.web.util.UriComponentsBuilder

class ResultHelpers {
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
