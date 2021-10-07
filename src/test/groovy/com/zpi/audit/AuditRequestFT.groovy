package com.zpi.audit

import com.fasterxml.jackson.databind.ObjectMapper
import com.zpi.CommonHelpers
import com.zpi.domain.audit.AuditLog
import com.zpi.domain.audit.AuditMetadata
import com.zpi.domain.audit.AuditRepository
import com.zpi.domain.organization.Organization
import com.zpi.domain.organization.OrganizationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class AuditRequestFT extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private AuditRepository auditRepository

    @Autowired
    private OrganizationRepository organizationRepository

    @Autowired
    private CommonHelpers helpers

    @Autowired
    private ObjectMapper mapper

    private static final Organization organization = new Organization("sdaafsd")
    private static final String baseUri = "/api/audit/" + organization.getName()

    def setup() {
        auditRepository.clear()
        organizationRepository.clear()
    }

    def "should return audit data for registered organization"() {
        given:
            def date = new Date()
            def metadata = new AuditMetadata("fasd", "asdf")
            def username = "sdghadal;ksj"
            def log = new AuditLog(date, metadata, organization.getName(), username)
            auditRepository.save(date, log)
            organizationRepository.save(organization)

        when:
            def response = helpers.getRequest(baseUri)

        then:
            response.andExpect(content().json(mapper.writeValueAsString(List.of(log))))
    }

    def "should return empty response for registered organization with no audit data"() {
        given:
            organizationRepository.save(organization)

        when:
            def response = helpers.getRequest(baseUri)

        then:
            response.andExpect(content().json(mapper.writeValueAsString(List.of())))
    }

    def "should return 404 for non existing organization"() {
        when:
            def response = helpers.getRequest(baseUri)

        then:
            response.andExpect(status().isNotFound())
            CommonHelpers.attributeFromResult("error", response) == "UNKNOWN_ORGANIZATION"
            !CommonHelpers.attributeFromResult("error_description", response).isEmpty()
    }
}
