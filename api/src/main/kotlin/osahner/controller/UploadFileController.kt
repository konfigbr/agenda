package osahner.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.ui.Model
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import osahner.dto.UploadResponseDto
import osahner.dto.UrlFileDto
import osahner.filestorage.FileStorage
import osahner.utils.Utils
import java.text.SimpleDateFormat
import java.util.*
import javax.ws.rs.PathParam


@RestController
@RequestMapping("/files/upload")
class UploadFileController {
  @Autowired
  lateinit var fileStorage: FileStorage

  @GetMapping
  fun index(): String {
    return "multipartfile/uploadform.html"
  }

  @PostMapping
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_ASSISTENTE', 'ROLE_ATENDENTE','ROLE_PROFISSIONAL')")
  fun uploadMultipartFile(@RequestParam("uploadfile") file: MultipartFile): Any? {
      var filename = fileStorage.store(file)
      val response = UploadResponseDto(
              filename = filename,
              error = null
      )
      return ResponseEntity(response,HttpStatus.OK)
  }

    @GetMapping("/url/{filename}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_ASSISTENTE', 'ROLE_ATENDENTE','ROLE_PROFISSIONAL')")
    fun getUrlFile(@PathVariable filename: String): Any? {
        var url = fileStorage.getFileUrl(filename)
        val response = UrlFileDto(
                url = url
        )
        return ResponseEntity(response,HttpStatus.OK)
    }
}
