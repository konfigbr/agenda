import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*


class JavaMailSenderImpl() {

  fun getJavaMailSender(): JavaMailSender? {
    val mailSender = JavaMailSenderImpl()
    mailSender.host = "smtp.gmail.com"
    mailSender.port = 587
    mailSender.username = "andre.dionisio@hdnit.com.br"
    mailSender.password = "password"
    val props: Properties = mailSender.javaMailProperties
    props.put("mail.transport.protocol", "smtp")
    props.put("mail.smtp.auth", "true")
    props.put("mail.smtp.starttls.enable", "true")
    props.put("mail.debug", "true")
    return mailSender
  }
}
