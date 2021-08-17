package com.zpi.client

import com.zpi.CommonFixtures
import com.zpi.domain.client.ClientRepository
import com.zpi.domain.client.ClientService
import spock.lang.Specification
import spock.lang.Subject

class ClientRegistrationUT extends Specification {
    def clientRepository = Mock(ClientRepository)

    @Subject
    private ClientService clientService = new ClientService(clientRepository)

    def "should return true for new client"() {
        given:
            def client = CommonFixtures.client()

            clientRepository.getByKey(client.getId()) >> Optional.empty()
        when:
            def isSuccess = clientService.saveClient(client)

        then:
            isSuccess

        and:
            1 * clientRepository.save(client.getId(), client)

    }

    def "should return false when registering existing client"() {
        given:
            def client = CommonFixtures.client()

            clientRepository.getByKey(client.getId()) >> Optional.of(client)
        when:
            def isSuccess = clientService.saveClient(client)

        then:
            !isSuccess

        and:
            0 * clientRepository.save(_, _)
    }
}
