import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.test.context.junit4.SpringRunner
import osahner.Application

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = arrayOf(Application::class))
class MailServiceTest {

    @Autowired
    private val emailSender: JavaMailSender? = null

    fun sendSimpleMessage(to: String, subject: String, text: String) {
        val message = SimpleMailMessage().apply {
            setFrom("contato@optdoc.com.br");
            setTo(to);
            setSubject(subject);
            setText(text);
        }

        emailSender?.send(message);
    }

    fun sendEmail(to: String?, subject: String?, htmlMsg: String?)  {
        val message = emailSender?.createMimeMessage()
        val helper = MimeMessageHelper(message, true)
        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(htmlMsg, true)
        emailSender?.send(message)
    }

    fun sendSimpleMessageMime(to: String, subject: String, text: String) {
        val message = emailSender?.createMimeMessage()

        val helper = MimeMessageHelper(message, true)
        helper.setTo(to)
        helper.setFrom("contato@optdoc.com.br");
        helper.setSubject(subject)
        helper.setText(text, true)

        emailSender?.send(helper.mimeMessage);
    }

    @Test
    fun send() {
        sendSimpleMessage("andredionisio114@gmail.com", "teste ruim", "xpto222 123456")
        sendSimpleMessageMime("andredionisio114@gmail.com", "TESTE DOS DEUSES", "xpto222 123456")
    }
}