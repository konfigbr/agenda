package osahner.service

import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfWriter
import org.springframework.stereotype.Component
import osahner.domain.AgendaAssistente
import osahner.domain.Medico
import osahner.domain.TermoDeUso
import osahner.dto.AgendaAssistenteDto
import osahner.enums.TipoDocumento
import osahner.enums.TipoTermoDeUso
import osahner.repository.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.*





@Component
class ExportPdfService(
  private val medicoRepository: MedicoRepository,
  private val especialidadeRepository: EspecialidadeRepository,
  private val userRepository: UserRepository,
  private val termoDeUsoRepository: TermoDeUsoRepository
) {

  fun exportTerms(profissional: Medico): ByteArrayInputStream?{
    var especialidade = especialidadeRepository.findById(profissional.refEspecialidadeEntity?.id).get()

    if (especialidade.tipoAceite?.name == "MEDICINA" && profissional.tipoDocumento?.name == "CPF"){
      return this.doctorCpfPdfExportTerms(profissional)
    }else if (especialidade.tipoAceite?.name == "MEDICINA" && profissional.tipoDocumento?.name == "CNPJ"){
      return this.outrosPdfExportTerms(profissional)
    }else if(especialidade.tipoAceite?.name == "ODONTOLOGIA" && profissional.tipoDocumento?.name == "CPF"){
      return this.dentistCpfPdfExportTerms(profissional)
    }else if(especialidade.tipoAceite?.name == "ODONTOLOGIA" && profissional.tipoDocumento?.name == "CNPJ"){
      return this.outrosPdfExportTerms(profissional)
    }else if(especialidade.tipoAceite?.name == "FONOAUDIOLOGIA" && profissional.tipoDocumento?.name == "CPF"){
      return this.fonoCpfPdfExportTerms(profissional)
    }else if(especialidade.tipoAceite?.name == "FONOAUDIOLOGIA" && profissional.tipoDocumento?.name == "CNPJ"){
      return this.outrosPdfExportTerms(profissional)
    }else {
      return this.outrosPdfExportTerms(profissional)
    }
  }

  fun exportTermsOfUse(profissional: Medico): ByteArrayInputStream? {
    var termoDeUso: TermoDeUso? = null;
    val especialidade = especialidadeRepository.findById(profissional.refEspecialidadeEntity?.id).get()

    if (especialidade.nome !== "Odontologia" && especialidade.nome !== "Otorrino") {
      termoDeUso = this.termoDeUsoRepository.findByTipo(TipoTermoDeUso.PF_ODONTO).get()
    } else if (profissional.tipoDocumento === TipoDocumento.CPF) {
      if (especialidade.nome === "Odontologia")
        termoDeUso = this.termoDeUsoRepository.findByTipo(TipoTermoDeUso.PF_ODONTO).get()
      else if (especialidade.nome === "Otorrino")
        termoDeUso = this.termoDeUsoRepository.findByTipo(TipoTermoDeUso.PF_MEDICINA).get()
      else if (especialidade.nome === "Fonoaudiologia")
        termoDeUso = this.termoDeUsoRepository.findByTipo(TipoTermoDeUso.PF_FONO).get()
    } else if (profissional.tipoDocumento === TipoDocumento.CNPJ) {
      if (especialidade.nome === "Odontologia")
        termoDeUso = this.termoDeUsoRepository.findByTipo(TipoTermoDeUso.PJ_ODONTO).get()
      else if (especialidade.nome === "Otorrino")
        termoDeUso = this.termoDeUsoRepository.findByTipo(TipoTermoDeUso.PJ_MEDICINA).get()
      else if (especialidade.nome === "Fonoaudiologia")
        termoDeUso = this.termoDeUsoRepository.findByTipo(TipoTermoDeUso.PJ_FONO).get()
    } else {
      termoDeUso = this.termoDeUsoRepository.findByTipo(TipoTermoDeUso.PJ_MEDICINA).get()
    }

    var document: Document? = Document()
    var out: ByteArrayOutputStream? = ByteArrayOutputStream()
    var writer: PdfWriter =  PdfWriter.getInstance(document, out)
    val open = document?.open()

    var font: com.itextpdf.text.Font = FontFactory.getFont("COURIER")
    var paragraph:Paragraph = Paragraph("TERMO DE USO"+"\n"+
            "\n" , font)
    paragraph.setAlignment(Element.ALIGN_CENTER)

    var body: Paragraph = Paragraph(termoDeUso?.texto)

    var footer = Phrase(String.format("Page %d of", writer.getPageNumber()), font)
    document?.add(paragraph)
    document?.add(body)
    document?.add(Chunk.NEWLINE)
    document?.close()

    return ByteArrayInputStream(out?.toByteArray())
  }

  fun dentistCpfPdfExportTerms(profissional:Medico) : ByteArrayInputStream? {

    var user = userRepository.findById(profissional.user?.id).get()
    val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
    val date = LocalDateTime.now(saoPauloZone)
    var document: Document? = Document()
    var out: ByteArrayOutputStream? = ByteArrayOutputStream()
    var writer: PdfWriter =  PdfWriter.getInstance(document, out)
    val open = document?.open()

    var font: com.itextpdf.text.Font = FontFactory.getFont("COURIER")
    var paragraph:Paragraph = Paragraph("TERMO DE CONSENTIMENTO / RESPONSABILIDADE"+"\n"+
            "\n" , font)
    paragraph.setAlignment(Element.ALIGN_CENTER)

    var body: Paragraph = Paragraph( ""+user.nome+", " +profissional.nacionalidade+", odontologista, "+ profissional.estadoCivil+", com endereço situado na "+profissional.logradouro+", nº "+profissional.numero+", "+profissional.bairro+", "+profissional.cidade+"/"+profissional.uf+", CEP "+profissional.cep+", portador da cédula de identidade RG n.º "+profissional.rg+", inscrito no CPF/MF sob n.º "+profissional.cpf+", declara para todos os fins, que: \n" +
            "\n" +
            "A. Pessoalmente realizou o Aceite nos Termos de Uso do Coworking de Saúde, por meio de acesso à Plataforma OPT.DOC. com a utilização de login e senha, que são pessoais e intransferíveis, aperfeiçoando-se assim, a relação contratual entre as Partes; \n" +
            "\n" +
            "B. Está ciente dos direitos e obrigações decorrentes da Contratação, dos Termos aplicáveis à relação contratual, em especial quanto à Política de Preços e Prazos, Política de Cancelamento, Política de Privacidade de Dados e a Limitação de Uso do Espaço; \n" +
            "\n" +
            "C. Está ciente que o meio para interação com a OPT.DOC, tanto para o aluguel de salas/equipamentos, como para a contratação de serviços, se dá exclusivamente através do acesso à Plataforma OPT.DOC. com a utilização de login e senha, que são pessoais e intransferíveis;\n" +
            "\n" +
            "D. É o(a) responsável por todos os aspectos técnicos e regulamentares relacionados ao atendimento e a realização dos procedimentos nas dependências do Coworking de Saúde, ficando a OPT.DOC isenta de quaisquer responsabilidades por todos os resultados decorrentes destas atuações;\n" +
            "\n" +
            "E. Conhece a Limitação de Uso do Espaço, relacionado à infraestrutura do Consultório de Atendimento e Consultório de Ortodontia, nos limites da legislação e regulamentações aplicáveis;\n" +
            " \n" +
            "F. Está apto à prática da odontologia em todo o território nacional, em especial, dentro do Estado de São Paulo, com sua situação profissional regular perante o Conselho Regional de Odontologia, bem como tem ciências de seus direitos e deveres éticos, obrigando-se a conduzir suas práticas em conformidade com os preceitos legais e regulamentares aplicáveis;\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "São Paulo, "+date.dayOfMonth+" de "+date.month.getDisplayName(TextStyle.FULL,  Locale("pt"))+" de "+date.year+"\n" +
            "\n" +
            "\n" +

            "______________________________________\n" +
            ""+user.nome+"\n",font)
    var footer = Phrase(String.format("Page %d of", writer.getPageNumber()), font)
    document?.add(paragraph)
    document?.add(body)
   // document?.add(footer)
    document?.add(Chunk.NEWLINE)

    document?.close()

   // document?.add(new Paragraph("Exemplo Criptografia"));
    return  ByteArrayInputStream(out?.toByteArray())
  }


  fun doctorCpfPdfExportTerms(profissional:Medico) : ByteArrayInputStream? {

    var user = userRepository.findById(profissional.user?.id).get()
    val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
    val date = LocalDateTime.now(saoPauloZone)
    var document: Document? = Document()
    var out: ByteArrayOutputStream? = ByteArrayOutputStream()
    var writer: PdfWriter =  PdfWriter.getInstance(document, out)
    val open = document?.open()

    var font: com.itextpdf.text.Font = FontFactory.getFont("COURIER")
    var paragraph:Paragraph = Paragraph("TERMO DE CONSENTIMENTO / RESPONSABILIDADE"+"\n"+
            "\n" , font)
    paragraph.setAlignment(Element.ALIGN_CENTER)

    var body: Paragraph = Paragraph( ""+user.nome+", " +profissional.nacionalidade+", médico(a), "+ profissional.estadoCivil+", com endereço situado na "+profissional.logradouro+", nº "+profissional.numero+", "+profissional.bairro+", "+profissional.cidade+"/"+profissional.uf+", CEP "+profissional.cep+", portador da cédula de identidade RG n.º "+profissional.rg+", inscrito no CPF/MF sob n.º "+profissional.cpf+", declara para todos os fins, que: \n" +
            "\n" +
            "A. Pessoalmente realizou o Aceite nos Termos de Uso do Coworking de Saúde, por meio de acesso à Plataforma OPT.DOC. com a utilização de login e senha, que são pessoais e intransferíveis, aperfeiçoando-se assim, a relação contratual entre as Partes; \n" +
            "\n" +
            "B. Está ciente dos direitos e obrigações decorrentes da Contratação, dos Termos aplicáveis à relação contratual, em especial quanto à Política de Preços e Prazos, Política de Cancelamento, Política de Privacidade de Dados e a Limitação de Uso do Espaço; \n" +
            "\n" +
            "C. Está ciente que o meio para interação com a OPT.DOC, tanto para o aluguel de salas/equipamentos, como para a contratação de serviços, se dá exclusivamente através do acesso à Plataforma OPT.DOC. com a utilização de login e senha, que são pessoais e intransferíveis;\n" +
            "\n" +
            "D. É o(a) responsável por todos os aspectos técnicos e regulamentares relacionados ao atendimento e a realização dos procedimentos nas dependências do Coworking de Saúde, ficando a OPT.DOC isenta de quaisquer responsabilidades por todos os resultados decorrentes destas atuações;\n" +
            "\n" +
            "E. Conhece a Limitação de Uso do Espaço, relacionado à infraestrutura do Consultório de Atendimento e Consultório Médico, nos limites da legislação e regulamentações aplicáveis;\n" +
            " \n" +
            "F. Está apto à prática da medicina em todo o território nacional, em especial, dentro do Estado de São Paulo, com sua situação profissional regular perante o Conselho Regional de Medicina, bem como tem ciências de seus direitos e deveres éticos, obrigando-se a conduzir suas práticas em conformidade com os preceitos legais e regulamentares aplicáveis;\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "São Paulo, "+date.dayOfMonth+" de "+date.month.getDisplayName(TextStyle.FULL,  Locale("pt"))+" de "+date.year+"\n" +
            "\n" +
            "\n" +

            "______________________________________\n" +
            ""+user.nome+"\n",font)
    var footer = Phrase(String.format("Page %d of", writer.getPageNumber()), font)
    document?.add(paragraph)
    document?.add(body)
    // document?.add(footer)
    document?.add(Chunk.NEWLINE)

    document?.close()

    // document?.add(new Paragraph("Exemplo Criptografia"));
    return  ByteArrayInputStream(out?.toByteArray())
  }


  fun fonoCpfPdfExportTerms(profissional:Medico) : ByteArrayInputStream? {

    var user = userRepository.findById(profissional.user?.id).get()
    val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
    val date = LocalDateTime.now(saoPauloZone)
    var document: Document? = Document()
    var out: ByteArrayOutputStream? = ByteArrayOutputStream()
    var writer: PdfWriter =  PdfWriter.getInstance(document, out)
    val open = document?.open()

    var font: com.itextpdf.text.Font = FontFactory.getFont("COURIER")
    var paragraph:Paragraph = Paragraph("TERMO DE CONSENTIMENTO / RESPONSABILIDADE"+"\n"+
            "\n" , font)
    paragraph.setAlignment(Element.ALIGN_CENTER)

    var body: Paragraph = Paragraph( ""+user.nome+", " +profissional.nacionalidade+", fonoaudiólogo(a), "+ profissional.estadoCivil+", com endereço situado na "+profissional.logradouro+", nº "+profissional.numero+", "+profissional.bairro+", "+profissional.cidade+"/"+profissional.uf+", CEP "+profissional.cep+", portador da cédula de identidade RG n.º "+profissional.rg+", inscrito no CPF/MF sob n.º "+profissional.cpf+", declara para todos os fins, que: \n" +
            "\n" +
            "A. Pessoalmente realizou o Aceite nos Termos de Uso do Coworking de Saúde, por meio de acesso à Plataforma OPT.DOC. com a utilização de login e senha, que são pessoais e intransferíveis, aperfeiçoando-se assim, a relação contratual entre as Partes; \n" +
            "\n" +
            "B. Está ciente dos direitos e obrigações decorrentes da Contratação, dos Termos aplicáveis à relação contratual, em especial quanto à Política de Preços e Prazos, Política de Cancelamento, Política de Privacidade de Dados e a Limitação de Uso do Espaço; \n" +
            "\n" +
            "C. Está ciente que o meio para interação com a OPT.DOC, tanto para o aluguel de salas/equipamentos, como para a contratação de serviços, se dá exclusivamente através do acesso à Plataforma OPT.DOC. com a utilização de login e senha, que são pessoais e intransferíveis;\n" +
            "\n" +
            "D. É o(a) responsável por todos os aspectos técnicos e regulamentares relacionados ao atendimento e a realização dos procedimentos nas dependências do Coworking de Saúde, ficando a OPT.DOC isenta de quaisquer responsabilidades por todos os resultados decorrentes destas atuações;\n" +
            "\n" +
            "E. Conhece a Limitação de Uso do Espaço, relacionado à infraestrutura do Consultório de Atendimento e Consultório de Fonoaudiologia, nos limites da legislação e regulamentações aplicáveis;\n" +
            " \n" +
            "F. Está apto à prática da fonoaudiologia em todo o território nacional, em especial, dentro do Estado de São Paulo, com sua situação profissional regular perante o Conselho Regional de Fonoaudiologia, bem como tem ciências de seus direitos e deveres éticos, obrigando-se a conduzir suas práticas em conformidade com os preceitos legais e regulamentares aplicáveis;\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "São Paulo, "+date.dayOfMonth+" de "+date.month.getDisplayName(TextStyle.FULL,  Locale("pt"))+" de "+date.year+"\n" +
            "\n" +
            "\n" +

            "______________________________________\n" +
            ""+user.nome+"\n",font)
    var footer = Phrase(String.format("Page %d of", writer.getPageNumber()), font)
    document?.add(paragraph)
    document?.add(body)
    // document?.add(footer)
    document?.add(Chunk.NEWLINE)

    document?.close()

    // document?.add(new Paragraph("Exemplo Criptografia"));
    return  ByteArrayInputStream(out?.toByteArray())
  }


  fun outrosPdfExportTerms(profissional:Medico) : ByteArrayInputStream? {

    var user = userRepository.findById(profissional.user?.id).get()
    val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
    val date = LocalDateTime.now(saoPauloZone)
    var document: Document? = Document()
    var out: ByteArrayOutputStream? = ByteArrayOutputStream()
    var writer: PdfWriter =  PdfWriter.getInstance(document, out)
    val open = document?.open()

    var font: com.itextpdf.text.Font = FontFactory.getFont("COURIER")
    var paragraph:Paragraph = Paragraph("TERMO DE CONSENTIMENTO / RESPONSABILIDADE"+"\n"+
            "\n" , font)
    paragraph.setAlignment(Element.ALIGN_CENTER)

    var body: Paragraph = Paragraph( ""+user.nome+", " +profissional.nacionalidade+", odontologista, "+ profissional.estadoCivil+", com endereço situado na "+profissional.logradouro+", nº "+profissional.numero+", "+profissional.bairro+", "+profissional.cidade+"/"+profissional.uf+", CEP "+profissional.cep+", portador da cédula de identidade RG n.º "+profissional.rg+", inscrito no CPF/MF sob n.º "+profissional.cpf+", declara para todos os fins, que: \n" +
            "\n" +
            "A. Pessoalmente realizou o Aceite nos Termos de Uso do Coworking de Saúde, por meio de acesso à Plataforma OPT.DOC. com a utilização de login e senha, que são pessoais e intransferíveis, aperfeiçoando-se assim, a relação contratual entre as Partes; \n" +
            "\n" +
            "B. Está ciente dos direitos e obrigações decorrentes da Contratação, dos Termos aplicáveis à relação contratual, em especial quanto à Política de Preços e Prazos, Política de Cancelamento, Política de Privacidade de Dados e a Limitação de Uso do Espaço; \n" +
            "\n" +
            "C. Está ciente que o meio para interação com a OPT.DOC, tanto para o aluguel de salas/equipamentos, como para a contratação de serviços, se dá exclusivamente através do acesso à Plataforma OPT.DOC. com a utilização de login e senha, que são pessoais e intransferíveis;\n" +
            "\n" +
            "D. É o(a) responsável por todos os aspectos técnicos e regulamentares relacionados ao atendimento e a realização dos procedimentos nas dependências do Coworking de Saúde, ficando a OPT.DOC isenta de quaisquer responsabilidades por todos os resultados decorrentes destas atuações;\n" +
            "\n" +
            "E. Conhece a Limitação de Uso do Espaço, relacionado à infraestrutura do Consultório de Atendimento e Consultório de Ortodontia, nos limites da legislação e regulamentações aplicáveis;\n" +
            " \n" +
            "F. Está apto à prática da odontologia em todo o território nacional, em especial, dentro do Estado de São Paulo, com sua situação profissional regular perante o Conselho Regional de Odontologia, bem como tem ciências de seus direitos e deveres éticos, obrigando-se a conduzir suas práticas em conformidade com os preceitos legais e regulamentares aplicáveis;\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "São Paulo, "+date.dayOfMonth+" de "+date.month.getDisplayName(TextStyle.FULL,  Locale("pt"))+" de "+date.year+"\n" +
            "\n" +
            "\n" +

            "______________________________________\n" +
            ""+user.nome+"\n",font)
    var footer = Phrase(String.format("Page %d of", writer.getPageNumber()), font)
    document?.add(paragraph)
    //document?.add(body)
    // document?.add(footer)
    document?.add(Chunk.NEWLINE)

    document?.close()

    // document?.add(new Paragraph("Exemplo Criptografia"));
    return  ByteArrayInputStream(out?.toByteArray())
  }
}
