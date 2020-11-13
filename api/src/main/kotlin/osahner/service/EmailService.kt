package osahner.service

import com.sun.istack.ByteArrayDataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.InputStreamResource
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import osahner.domain.Agendamento
import osahner.domain.Consulta
import osahner.enums.LogEnum
import osahner.filestorage.FileStorageImpl
import java.io.ByteArrayInputStream
import java.time.format.DateTimeFormatter
import javax.activation.DataHandler
import javax.activation.DataSource
import javax.activation.FileDataSource
import javax.mail.Part
import javax.mail.internet.MimeBodyPart


@Component
class EmailService(
  private val javaMailSender : JavaMailSender,
  private val fileStorage : FileStorageImpl
) {

  @Autowired
  private val emailSender: JavaMailSender? = null

  fun sendSimpleMessage(to: String, subject: String, text: String) {
    val mailMessage = SimpleMailMessage()
    mailMessage.setTo(to)
    mailMessage.subject = subject
    mailMessage.text = text
    mailMessage.from = "andre.dionisio@hdnit.com.br"
    javaMailSender.send(mailMessage)
  }

  fun recuperarSenha(to: String?, name: String?, password: String?) {
    val subject = "OPT Gestão - Recuperar Senha"
    val htmlMsg =
      "<p> Olá " + name + " </p>" +
      "<p> Você solicitou a <b> redefinição de sua senha de acesso no sistema OPT Gestão </b>. </p>" +
      "<p> Essa é sua senha temporária: <b>" + password + "</b>. Ao logar no sistema, você deverá definir a nova senha. </p>"

    sendEmail(to, subject, htmlMsg)
  }

  fun novoUsuario(to: String?, password: String?) {
    val subject = "OPT Gestão - Novo Usuário"
    val htmlMsg =
      "Bem-vindo(a) ao sistema <b>OPT Gestão</b>!" +
      "<p>É com muita alegria e de braços abertos que hoje recebemos você. <br/>" +
        "Desejamos que se sinta bem entre nós, que faça parte das nossas vivências, que encontre oportunidades e condições para crescer.</p>" +
      "<p> Para o seu seu primeiro acesso, informe a seguinte senha temporária: <b>" + password + "</b>. <br/>" +
        "Ao logar no sistema, você deverá definir a nova senha.</p>"

    sendEmail(to, subject, htmlMsg)
  }

  fun novoAgendamento(to: String?, agendamento: Agendamento) {
    val formatterData = DateTimeFormatter.ofPattern("dd/MM/uuuu")
    val dia = formatterData.format(agendamento.dia)

    val formatterHora = DateTimeFormatter.ofPattern("HH:mm")
    val inicio = formatterHora.format(agendamento.agendaSalas?.first()?.inicio)
    val fim = formatterHora.format(agendamento.agendaSalas?.last()?.fim)

    val nomeSala = agendamento.agendaSalas?.first()?.sala?.nome
    val nome = agendamento.medico?.user?.nome

    val subject = "OPT Gestão - Novo Agendamento"
    val htmlMsg =
            "<p> Olá "+ nome +" </p>"+
            "<p> A sala '"+ nomeSala +"' foi agendada para o dia "+ dia +" das "+ inicio +" às "+ fim +"</p>"

    sendEmail(to, subject, htmlMsg)
  }

  fun enviarTermoAceite(to: String?, name: String?, id: Long?, files: Array<ByteArrayInputStream?>) {
    val subject = "OPT Gestão - Termo de Aceite"
    val htmlMsg =
            "<p> Olá " + name + " </p>" +
            "<p> Em anexo está o Termo de Aceite que deve ser assinado por você e enviado na área de edição de seus dados no sistema OPT.DOC </p>"

    sendEmailWithAttachment(to, subject, htmlMsg, files, arrayOf("TermoDeAceite"+ id +".pdf", "TermoDeUso" + id + ".pdf"))
  }

  fun cancelamentoConsulta(to: String?, consulta: Consulta) {
    val formatterData = DateTimeFormatter.ofPattern("dd/MM/uuuu")
    val dia = formatterData.format(consulta.dataInicio)

    val formatterDataHora = DateTimeFormatter.ofPattern("dd/MM HH:mm")
    val data = formatterDataHora.format(consulta.dataInicio)

    val formatterHora = DateTimeFormatter.ofPattern("HH:mm")
    val inicio = formatterHora.format(consulta.dataInicio)
    val fim = formatterHora.format(consulta.dataFim)

    val nomeProfissional = consulta.refMedicoEntity?.user?.nome
    val nomePaciente = consulta.refPacienteEntity?.user?.nome

    val subject = "Cancelamento de consulta - "+ data +" - " + nomePaciente
    val htmlMsg =
            "<p> Prezado(a) Doutor(a) "+ nomeProfissional +" </p>"+
            "<p> Informamos que a consulta abaixo foi cancelada </p> <br/>"+
            "<p><b>Paciente:</b> " +nomePaciente +" </p>"+
            "<p><b>Data:</b> "+ dia +" </p>"+
            "<p><b>Horário:</b> "+ inicio +" às "+ fim +" </p>"

    sendEmail(to, subject, htmlMsg)
  }

  private fun sendEmailWithAttachment(to: String?, subject: String?, htmlMsg: String?, files: Array<ByteArrayInputStream?>, fileNames: Array<String>)  {
    val message = emailSender?.createMimeMessage()
    val helper = MimeMessageHelper(message, true)
    helper.setTo(to)
    helper.setFrom("contato@optdoc.com.br");
    helper.setSubject(subject)
    helper.setText(htmlMsg, true)

    for (i in 0..files.size - 1) {
      val file: ByteArrayInputStream? = files[i]
      val ds = ByteArrayDataSource(file?.readBytes(), "application/octet-stream")

      val anexoBody = MimeBodyPart()
      anexoBody.dataHandler = DataHandler(ds)
      anexoBody.fileName = fileNames[i]
      anexoBody.disposition = Part.ATTACHMENT

      helper.mimeMultipart.addBodyPart(anexoBody)
    }

    emailSender?.send(helper.mimeMessage);
  }

  fun sendEmail(to: String?, subject: String?, htmlMsg: String?)  {
    val message = emailSender?.createMimeMessage()
    val helper = MimeMessageHelper(message, true)
    helper.setTo(to)
    helper.setFrom("contato@optdoc.com.br");
    helper.setSubject(subject)
    helper.setText(htmlMsg, true)

    try{
      emailSender?.send(helper.mimeMessage)
    }catch (e: Exception){
      println(e.message)
    }
  }

  fun sendSimpleMessageTeste(to: String, subject: String, text: String) {
    val message = SimpleMailMessage().apply {
      setFrom("contato@optdoc.com.br");
      setTo(to);
      setSubject(subject);
      setText(text);
    }

    val result = emailSender?.send(message);

    println(result)
  }

  fun aprovacaoConsulta(to: String?, consulta: Consulta) {
    val formatterData = DateTimeFormatter.ofPattern("dd/MM/uuuu")
    val dia = formatterData.format(consulta.dataInicio)

    val formatterDataHora = DateTimeFormatter.ofPattern("dd/MM HH:mm")
    val data = formatterDataHora.format(consulta.dataInicio)

    val formatterHora = DateTimeFormatter.ofPattern("HH:mm")
    val inicio = formatterHora.format(consulta.dataInicio)
    val fim = formatterHora.format(consulta.dataFim)

    val nomeProfissional = consulta.refMedicoEntity?.user?.nome
    val nomePaciente = consulta.refPacienteEntity?.user?.nome

    val subject = "Consulta Aprovada - "+ data +" - Dr(a) " + nomeProfissional
    val htmlMsg =
            "<p> Prezado(a) "+ nomePaciente +" </p>"+
                    "<p> Informamos que a consulta solicitada foi aprovada. </p> <br/>"+
                    "<p><b>Dr(a):</b> " +nomeProfissional +" </p>"+
                    "<p><b>Data:</b> "+ dia +" </p>"+
                    "<p><b>Horário:</b> "+ inicio +" às "+ fim +" </p>"

    sendEmail(to, subject, htmlMsg)
  }

  fun reagendamentoConsulta(to: String?, consulta: Consulta) {
    val formatterData = DateTimeFormatter.ofPattern("dd/MM/uuuu")
    val dia = formatterData.format(consulta.dataInicio)

    val formatterDataHora = DateTimeFormatter.ofPattern("dd/MM HH:mm")
    val data = formatterDataHora.format(consulta.dataInicio)

    val formatterHora = DateTimeFormatter.ofPattern("HH:mm")
    val inicio = formatterHora.format(consulta.dataInicio)
    val fim = formatterHora.format(consulta.dataFim)

    val nomeProfissional = consulta.refMedicoEntity?.user?.nome
    val nomePaciente = consulta.refPacienteEntity?.user?.nome

    val subject = "Reagendamento de consulta - "+ data +" - Dr(a) " + nomeProfissional
    val htmlMsg =
            "<p> Prezado(a) "+ nomePaciente +" </p>"+
                    "<p> Informamos que a sua consulta foi reagendada para: </p> <br/>"+
                    "<p><b>Dr(a):</b> " +nomeProfissional +" </p>"+
                    "<p><b>Data:</b> "+ dia +" </p>"+
                    "<p><b>Horário:</b> "+ inicio +" às "+ fim +" </p>"

    sendEmail(to, subject, htmlMsg)
  }
}
