package osahner.filestorage

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.stream.Stream


@Service
class FileStorageImpl {

  private val logger: Logger = LogManager.getLogger(FileStorageImpl::class.java)

  val rootLocation = Paths.get("/opt/tomcat/images").toAbsolutePath().normalize();

  val rootLocationDocs = Paths.get("documentFile")

  /*override fun store(file: MultipartFile){
    Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()))
  }*/

  fun storeCustomFileName(file: MultipartFile, fileName: String){

    //try { // Check if the file's name contains invalid characters
      this.init()
      if (fileName.contains("..")) {
        throw RuntimeException("Sorry! Filename contains invalid path sequence $fileName")
      }
      // Copy file to the target location (Replacing existing file with the same name)
      val targetLocation: Path = this.rootLocation.resolve(fileName)
      Files.copy(file.inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING)
    //} catch (ex: IOException) {
      //print(ex.printStackTrace())/
      //logger.error(ex.message,ex)
      //throw RuntimeException("Could not store file ${ex.printStackTrace()} Please try again!", ex)
    //}
  }


  fun loadFile(filename: String): Resource{
    val file = rootLocation.resolve(filename).normalize()
    val resource = UrlResource(file.toUri())

    if(resource.exists()) {
      return resource
    }else{
      throw RuntimeException("FAIL!")
    }
  }

  fun loadDocument(filename: String): Resource{
    val file = rootLocationDocs.resolve(filename).normalize()
    val resource = UrlResource(file.toUri())

    if(resource.exists()) {
      return resource
    }else{
      throw RuntimeException("FAIL!")
    }
  }

  fun deleteAll(){
   // FileSystemUtils.deleteRecursively(rootLocation.toFile())
  }

  fun init(){
    if (!Files.exists(rootLocation)) {
      Files.createDirectory(rootLocation)
    }
  }

  fun loadFiles(): Stream<Path>{
    return Files.walk(this.rootLocation, 1)
      .filter{path -> !path.equals(this.rootLocation)}
      .map(this.rootLocation::relativize)
  }

  fun getRootLocation() : String {
    return rootLocation.toString()
  }
}
