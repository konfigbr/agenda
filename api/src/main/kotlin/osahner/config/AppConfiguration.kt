package osahner.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import osahner.filestorage.FileStorage
import osahner.filestorage.S3Storage


@Configuration
@EnableConfigurationProperties(
  SecurityProperties::class
)
class AppConfiguration(
  val securityProperties: SecurityProperties
) {
  @Bean
  fun bCryptPasswordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder(securityProperties.strength)

  @Bean
  fun amazonS3Client(): AmazonS3 {
    // considerar o uso de variáveis de ambiente
    val awsCredentials = BasicAWSCredentials("AKIAYZTX6HV44N3EEKC3", "wksS10iLU94tgzqcIExWIlHCno+ieiGicwibo+78")
    return AmazonS3ClientBuilder.standard()
            .withRegion(Regions.fromName("sa-east-1")).withCredentials(AWSStaticCredentialsProvider(awsCredentials)).build()
  }

  @Bean
  fun fileStorage(): FileStorage = S3Storage()
}
