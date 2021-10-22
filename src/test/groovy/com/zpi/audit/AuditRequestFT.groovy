package com.zpi.audit

import com.fasterxml.jackson.databind.ObjectMapper
import com.zpi.domain.audit.AuditLog
import com.zpi.domain.audit.AuditMetadata
import com.zpi.domain.audit.AuditRepository
import com.zpi.testUtils.CommonHelpers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content

@SpringBootTest
@AutoConfigureMockMvc
class AuditRequestFT extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private AuditRepository auditRepository

    @Autowired
    private CommonHelpers helpers

    @Autowired
    private ObjectMapper mapper

    private static final String baseUri = "/api/audit/users/"

    def setup() {
        auditRepository.clear()
    }

    def "should return audit data for user"() {
        given:
            def date = new Date()
            def metadata = new AuditMetadata("fasd", "asdf")
            def username = "sdghadalksj"
            def log = new AuditLog(date, metadata, username)
            auditRepository.save(date, log)

        when:
            def response = helpers.getRequest(baseUri + username)

        then:
            response.andExpect(content().json(mapper.writeValueAsString(List.of(log))))
    }

    def "should return empty response for user with no audit data"() {
        when:
            def response = helpers.getRequest(baseUri + "asdf")

        then:
            response.andExpect(content().json(mapper.writeValueAsString(List.of())))
    }
}
