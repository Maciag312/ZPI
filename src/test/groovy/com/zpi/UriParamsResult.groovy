package com.zpi

import com.zpi.api.authCode.ticketRequest.RequestDTO
import org.springframework.test.web.servlet.ResultActions
import org.springframework.util.MultiValueMap
import org.springframework.web.util.UriComponents
import org.springframework.web.util.UriComponentsBuilder

import java.util.stream.Collectors

class UriParamsResult {
    private MultiValueMap<String, String> queryParams

    UriParamsResult(ResultActions result) {
        queryParams = uriComponentsFromResult(result).getQueryParams()
    }

    private static UriComponents uriComponentsFromResult(ResultActions result) {
        def uri = result.andReturn().getResponse().getHeader("Location")
        return UriComponentsBuilder.fromUriString(uri).build()
    }

    String getParam(String name) {
        return listToString(queryParams.get(name))
    }

    private static String listToString(List<String> list) {
        return list.stream()
                .map(s -> s)
                .collect(Collectors.joining(" "))
                .trim()
    }

    RequestDTO getRequest() {
        return RequestDTO.builder()
                .clientId(getParam("client_id"))
                .redirectUri(getParam("redirect_uri"))
                .responseType(getParam("response_type"))
                .scope(getParam("scope"))
                .state(getParam("state"))
                .build()
    }
}
