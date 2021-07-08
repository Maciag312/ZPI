package com.zpi

import com.zpi.Greeter
import com.zpi.MessageProvider
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Subject

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class GreeterUTGroovy extends Specification {

    def messageProvider = Mock(MessageProvider);

    @Subject
    Greeter greeter = new Greeter(messageProvider);

    def "should check if greeter welcomes"() {
        given:
        messageProvider.getMessage() >> "Welcome!"

        when:
        def message = greeter.hello()

        then:
        message == "Welcome!"
    }
}
