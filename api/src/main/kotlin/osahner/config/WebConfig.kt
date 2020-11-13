package osahner.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpMethod
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import osahner.security.JWTAuthenticationFilter
import osahner.security.JWTAuthorizationFilter
import osahner.service.*

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebConfig(
  val bCryptPasswordEncoder: BCryptPasswordEncoder,
  val userDetailsService: AppUserDetailsService,
  val securityProperties: SecurityProperties,
  val userService: UserService,
  val assistenteService: AssistenteService,
  val medicoService: MedicoService,
  val auditLogService: AuditLogService
) : WebSecurityConfigurerAdapter() {
  override fun configure(http: HttpSecurity) {
    http
      .cors().and()
      .csrf().disable()
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no sessions
      .and()
      .authorizeRequests()
      .antMatchers("/api/**").permitAll()
      .antMatchers("/error/**").permitAll()
      .antMatchers(HttpMethod.POST, "/login").permitAll()
      .antMatchers(HttpMethod.POST, "/users/recover-password").permitAll()
      .antMatchers(HttpMethod.POST, "/users/is-password-recovered").permitAll()
      .antMatchers(HttpMethod.POST, "/professionals/sign-up").permitAll()
      .antMatchers(HttpMethod.POST, "/justa/pay-schedule").permitAll()
      .antMatchers(HttpMethod.POST, "/justa/push-payment").permitAll()
      .antMatchers(HttpMethod.GET, "/professionals/terms-of-use").permitAll()
      .antMatchers(HttpMethod.GET, "/professionals/url/{url}").permitAll()
      .antMatchers(HttpMethod.GET, "/professionals/findByCfm").permitAll()
      .antMatchers(HttpMethod.GET, "/professionals/findByCfo").permitAll()
      .antMatchers(HttpMethod.GET, "/files/download/{filename}").permitAll()
      .antMatchers(HttpMethod.POST, "/patient").permitAll()
      .antMatchers(HttpMethod.GET, "/specialties").permitAll()
      .antMatchers(HttpMethod.GET, "/specialties/professionals").permitAll()
      .antMatchers(HttpMethod.GET, "/calls/listener").permitAll()
      .antMatchers(HttpMethod.POST, "/calls/new").permitAll()
      .antMatchers(HttpMethod.POST, "/company").permitAll()
      .anyRequest().authenticated()
      .and()
      .addFilter(JWTAuthenticationFilter(authenticationManager(), securityProperties, userService, medicoService,assistenteService,auditLogService ))
      .addFilter(JWTAuthorizationFilter(authenticationManager(), securityProperties))
  }

  @Throws(Exception::class)
  override fun configure(auth: AuthenticationManagerBuilder) {
    auth.userDetailsService(userDetailsService)
      .passwordEncoder(bCryptPasswordEncoder)
  }

  @Bean
  fun authProvider(): DaoAuthenticationProvider = DaoAuthenticationProvider().apply {
    setUserDetailsService(userDetailsService)
    setPasswordEncoder(bCryptPasswordEncoder)
  }

  @Bean
  fun corsConfigurationSource(): CorsConfigurationSource = UrlBasedCorsConfigurationSource().also { cors ->
    CorsConfiguration().apply {
      allowedOrigins = listOf("*")
      allowedMethods = listOf("POST", "PUT", "DELETE", "GET", "OPTIONS", "HEAD")
      allowedHeaders = listOf(
        "Authorization",
        "Content-Type",
        "X-Requested-With",
        "Accept",
        "Origin",
        "Access-Control-Request-Method",
        "Access-Control-Request-Headers",
        "Access-Control-Allow-Origin"
      )
      exposedHeaders =
        listOf(
          "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials", "Authorization", "Content-Disposition"
        )
      allowCredentials = true
      maxAge = 3600

      cors.registerCorsConfiguration("/**", this)
    }
  }

  @Bean
  @Primary
  fun objectMapper(builder: Jackson2ObjectMapperBuilder): ObjectMapper = builder.build<ObjectMapper>().apply {
    registerModule(JavaTimeModule())
    configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
  }
}
