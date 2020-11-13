package osahner.service

import org.apache.poi.ss.usermodel.Workbook
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import osahner.domain.Address
import osahner.dto.AddressDto
import osahner.repository.AddressRepository
import java.util.*

@Component
class AddressService(
  private val addressRepository: AddressRepository,
  private val csvImportService: CsvImportService,
  private val poiExportService: PoiExportService
) {
  fun list(): Collection<Address> = addressRepository.findAll()

  fun findById(id: Int): Optional<Address> = addressRepository.findById(id)

 // fun save(dto: AddressDto): Address = addressRepository.saveAndFlush(Address.fromDTO(dto))

  fun delete(id: Int) = addressRepository.deleteById(id)

  fun import(file: MultipartFile): Collection<Address> =
    csvImportService.importAddress(file).also { addressRepository.saveAll(it) }

  /*fun toWorkbook(): Workbook {
    val result = addressRepository.findAll().map { it.toDTO() }
    return poiExportService.buildExcelDocument(
      "Export Address List",
      listOf("Data", "Hora Início", "Hora Fim", "Valor Pago/Hora", "Assistente", "Profissional", "Sala Pronta","Sala Desocupada"),
      listOf("id","data", "Hora Iníciosss", "Hora Fimsss", "Valor Pago/Hora", "Assistente", "Profissional", "Sala Pronta","Sala Desocupada"),
      result
    )
  }*/
}
