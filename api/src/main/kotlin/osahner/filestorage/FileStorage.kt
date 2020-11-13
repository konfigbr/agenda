package osahner.filestorage

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException

interface FileStorage {
  fun store(file: MultipartFile): String
  fun loadFile(filename: String): Resource
  fun deleteAll()
  fun init()
  fun loadFiles(): Stream<Path>
  fun getRootLocation(): String
  fun getFileUrl(filename: String): String
  fun storeCustomFileName(file: MultipartFile, filename: String)
}
