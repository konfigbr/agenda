package osahner.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.Workbook
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


@Component
class PoiExportService(
  @Qualifier("objectMapper") private val mapper: ObjectMapper
) {
  fun buildExcelDocument(
    title: String? = "Export",
    nome: String?, cpf: String?, dataInicio: LocalDateTime?, dataFim: LocalDateTime?,
    header: Collection<String>,
    headerName: Collection<String>,
    result: Collection<Any>
  ): Workbook = HSSFWorkbook().apply {
    val sheet = createSheet(title).apply {
      defaultColumnWidth = 20

    }
    val headerFont = createFont().apply {
      bold = true
    }
    val headerFontTitle = createFont().apply {
      bold = true
      fontHeightInPoints = 25

    }
    val headerCellStyle = createCellStyle().apply {
      setFont(headerFont)
      borderBottom = BorderStyle.MEDIUM
      borderLeft = BorderStyle.MEDIUM
      borderRight = BorderStyle.MEDIUM
      borderTop = BorderStyle.MEDIUM
    }

    val headerCellStyleBorderRow = createCellStyle().apply {
      setFont(headerFont)
      borderBottom = BorderStyle.THIN
      borderLeft = BorderStyle.THIN
      borderRight = BorderStyle.THIN
      borderTop = BorderStyle.THIN
      alignment = HorizontalAlignment.CENTER
    }
    val headerCellStyleNoBorder = createCellStyle().apply {
      setFont(headerFont)

    }

    val headerCellStyleTitle = createCellStyle().apply {
      setFont(headerFontTitle)
    }


    val dataInicioF = dataInicio?.dayOfMonth.toString() +"/"+ dataInicio?.monthValue?.toString()+"/"+ dataInicio?.year.toString()
    val dataFimF = dataFim?.dayOfMonth.toString() +"/"+ dataFim?.monthValue?.toString()+"/"+ dataFim?.year.toString()
    val createHelper = creationHelper
    var rowNo = 12
    var colNo = 0
    var row = sheet.createRow(rowNo++)
    var col = sheet.createRow(colNo++)

    val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")


    sheet.createRow(1).createCell(1).apply {
     setCellValue(createHelper.createRichTextString("Relatório de Agendamentos Profissional"))
      setCellStyle(headerCellStyleTitle)
    }

    /*sheet.createRow(4).createCell(0).apply {
      setCellValue(createHelper.createRichTextString("Filtros:"))
      setCellStyle(headerCellStyleNoBorder)
    }*/

    if (nome != "") {
      sheet.createRow(5).createCell(0).apply {
        setCellValue(createHelper.createRichTextString("Nome: " + nome))
        setCellStyle(headerCellStyleNoBorder)
      }
    }
    if (cpf != "") {

      sheet.createRow(6).createCell(0).apply {
        setCellValue(createHelper.createRichTextString("CPF: " + cpf))
        setCellStyle(headerCellStyleNoBorder)
      }
    }


    sheet.createRow(7).createCell(0).apply {
      setCellValue(createHelper.createRichTextString("Data Início: "+ dataInicioF))
      setCellStyle(headerCellStyleNoBorder)
    }

    sheet.createRow(8).createCell(0).apply {
      setCellValue(createHelper.createRichTextString("Data Fim: "+ dataFimF))
      setCellStyle(headerCellStyleNoBorder)
    }



    headerName.withIndex().forEach { (cellNo, it) ->
      row.createCell(cellNo).apply {
        setCellValue(createHelper.createRichTextString(it.capitalize()))
        setCellStyle(headerCellStyle)
      }
    }

    result.forEach { entity ->
      val map = mapper.convertValue<Map<String, Any>>(entity)
      val list = header.map { map[it] }
      row = sheet.createRow(rowNo++)
      list.withIndex().forEach { (cellNo, entity) ->
        row.createCell(cellNo).apply {
          when (entity) {
            is Number -> setCellValue(entity.toDouble())
            is String -> setCellValue(entity as String?)
            is Boolean -> setCellValue((entity as Boolean?)!!)
            is Collection<*> -> setCellValue(entity.joinToString("; "))
            is Any -> setCellValue(mapper.writeValueAsString(entity))
            else -> setCellValue("") // null -> empty text field

          }
          setCellStyle(headerCellStyleBorderRow)
        }
      }
    }
  }



  fun buildExcelDocumentAgendamentosDoDia(
    title: String? = "Export",
    header: Collection<String>,
    headerName: Collection<String>,
    result: Collection<Any>
  ): Workbook = HSSFWorkbook().apply {
    val sheet = createSheet(title).apply {
      defaultColumnWidth = 30

    }
    val headerFont = createFont().apply {
      bold = true
    }
    val headerFontTitle = createFont().apply {
      bold = true
      fontHeightInPoints = 25

    }
    val headerCellStyle = createCellStyle().apply {
      setFont(headerFont)
      borderBottom = BorderStyle.MEDIUM
      borderLeft = BorderStyle.MEDIUM
      borderRight = BorderStyle.MEDIUM
      borderTop = BorderStyle.MEDIUM
    }

    val headerCellStyleBorderRow = createCellStyle().apply {
      setFont(headerFont)
      borderBottom = BorderStyle.THIN
      borderLeft = BorderStyle.THIN
      borderRight = BorderStyle.THIN
      borderTop = BorderStyle.THIN
      alignment = HorizontalAlignment.CENTER
    }
    val headerCellStyleNoBorder = createCellStyle().apply {
      setFont(headerFont)

    }

    val headerCellStyleTitle = createCellStyle().apply {
      setFont(headerFontTitle)
    }


    val createHelper = creationHelper
    var rowNo = 12
    var colNo = 0
    var row = sheet.createRow(rowNo++)
    var col = sheet.createRow(colNo++)


    sheet.createRow(1).createCell(1).apply {
      setCellValue(createHelper.createRichTextString("Agendamentos do dia"))
      setCellStyle(headerCellStyleTitle)
    }

    /*sheet.createRow(4).createCell(0).apply {
      setCellValue(createHelper.createRichTextString("Filtros:"))
      setCellStyle(headerCellStyleNoBorder)
    }

    sheet.createRow(5).createCell(0).apply {
      setCellValue(createHelper.createRichTextString("Especialidade: "+especialidade))
      setCellStyle(headerCellStyleNoBorder)
    }*/

    headerName.withIndex().forEach { (cellNo, it) ->
      row.createCell(cellNo).apply {
        setCellValue(createHelper.createRichTextString(it.capitalize()))
        setCellStyle(headerCellStyle)
      }
    }

    result.forEach { entity ->
      val map = mapper.convertValue<Map<String, Any>>(entity)
      val list = header.map { map[it] }
      row = sheet.createRow(rowNo++)
      list.withIndex().forEach { (cellNo, entity) ->
        row.createCell(cellNo).apply {
          when (entity) {
            is Number -> setCellValue(entity.toDouble())
            is String -> setCellValue(entity as String?)
            is Boolean -> setCellValue((entity as Boolean?)!!)
            is Collection<*> -> setCellValue(entity.joinToString("; "))
            is Any -> setCellValue(mapper.writeValueAsString(entity))
            else -> setCellValue("") // null -> empty text field

          }
          setCellStyle(headerCellStyleBorderRow)
        }
      }
    }
  }


  fun buildExcelDocumentProfissionais(
          title: String? = "Export",
          especialidade: String?,
          header: Collection<String>,
          headerName: Collection<String>,
          result: Collection<Any>
  ): Workbook = HSSFWorkbook().apply {
    val sheet = createSheet(title).apply {
      defaultColumnWidth = 30

    }
    val headerFont = createFont().apply {
      bold = true
    }
    val headerFontTitle = createFont().apply {
      bold = true
      fontHeightInPoints = 25

    }
    val headerCellStyle = createCellStyle().apply {
      setFont(headerFont)
      borderBottom = BorderStyle.MEDIUM
      borderLeft = BorderStyle.MEDIUM
      borderRight = BorderStyle.MEDIUM
      borderTop = BorderStyle.MEDIUM
    }

    val headerCellStyleBorderRow = createCellStyle().apply {
      setFont(headerFont)
      borderBottom = BorderStyle.THIN
      borderLeft = BorderStyle.THIN
      borderRight = BorderStyle.THIN
      borderTop = BorderStyle.THIN
      alignment = HorizontalAlignment.CENTER
    }
    val headerCellStyleNoBorder = createCellStyle().apply {
      setFont(headerFont)

    }

    val headerCellStyleTitle = createCellStyle().apply {
      setFont(headerFontTitle)
    }


    val createHelper = creationHelper
    var rowNo = 12
    var colNo = 0
    var row = sheet.createRow(rowNo++)
    var col = sheet.createRow(colNo++)


    sheet.createRow(1).createCell(1).apply {
      setCellValue(createHelper.createRichTextString("Relatório Profissionais"))
      setCellStyle(headerCellStyleTitle)
    }

    sheet.createRow(4).createCell(0).apply {
      setCellValue(createHelper.createRichTextString("Filtros:"))
      setCellStyle(headerCellStyleNoBorder)
    }

    sheet.createRow(5).createCell(0).apply {
      setCellValue(createHelper.createRichTextString("Especialidade: "+especialidade))
      setCellStyle(headerCellStyleNoBorder)
    }

    headerName.withIndex().forEach { (cellNo, it) ->
      row.createCell(cellNo).apply {
        setCellValue(createHelper.createRichTextString(it.capitalize()))
        setCellStyle(headerCellStyle)
      }
    }

    result.forEach { entity ->
      val map = mapper.convertValue<Map<String, Any>>(entity)
      val list = header.map { map[it] }
      row = sheet.createRow(rowNo++)
      list.withIndex().forEach { (cellNo, entity) ->
        row.createCell(cellNo).apply {
          when (entity) {
            is Number -> setCellValue(entity.toDouble())
            is String -> setCellValue(entity as String?)
            is Boolean -> setCellValue((entity as Boolean?)!!)
            is Collection<*> -> setCellValue(entity.joinToString("; "))
            is Any -> setCellValue(mapper.writeValueAsString(entity))
            else -> setCellValue("") // null -> empty text field

          }
          setCellStyle(headerCellStyleBorderRow)
        }
      }
    }
  }
}
