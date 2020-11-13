package osahner.filestorage

import com.amazonaws.AmazonServiceException
import com.amazonaws.SdkClientException
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.ObjectMetadata
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import osahner.utils.Utils
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Stream


@Service
class S3Storage: FileStorage {
    @Autowired
    lateinit var amazonS3: AmazonS3

    override fun store(file: MultipartFile): String {
        try {
            val currentDate = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
            val sufixo = Utils.generateRandom(4)
            var filename = StringUtils.cleanPath(file.originalFilename)
            filename = sufixo + currentDate + "-" + filename.toLowerCase().replace(" ".toRegex(), "-")

            val metaData = ObjectMetadata()
            val randomName = filename

            metaData.contentType = file.contentType

            amazonS3.putObject("optdoc-bucket", randomName, file.inputStream, metaData)
            amazonS3.setObjectAcl("optdoc-bucket", randomName, CannedAccessControlList.PublicRead)

            return filename
        } catch (ex: AmazonServiceException) {
            throw ex
        } catch (ex: SdkClientException) {
            throw ex
        }
    }

    override fun getFileUrl(fileName: String): String = amazonS3.getUrl("optdoc-bucket", fileName).toString()

    override fun storeCustomFileName(file: MultipartFile, fileName: String){
        throw RuntimeException("")
    }


    override fun loadFile(filename: String): Resource {
        throw RuntimeException("")
    }

    fun loadDocument(filename: String): Resource {
        throw RuntimeException("")
    }

    override fun deleteAll(){
        throw RuntimeException("")
    }

    override fun init(){
        throw RuntimeException("")
    }

    override fun loadFiles(): Stream<Path> {
        throw RuntimeException("")
    }

    override fun getRootLocation() : String {
        throw RuntimeException("")
    }
}