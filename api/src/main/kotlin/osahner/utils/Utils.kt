package osahner.utils

import org.springframework.stereotype.Component
import osahner.config.AppConfiguration
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.random.Random

class Utils {
  companion object {
    fun generatePassword(): String {
      val characterSet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

      val random = Random(System.nanoTime())
      val password = StringBuilder()

      for (i in 0 until 6) {
        val rIndex = random.nextInt(characterSet.length)
        password.append(characterSet[rIndex])
      }

      return password.toString()
    }

    fun generateRandom(x: Int): String {
      val characterSet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

      val random = Random(System.nanoTime())
      val password = StringBuilder()

      for (i in 0 until x) {
        val rIndex = random.nextInt(characterSet.length)
        password.append(characterSet[rIndex])
      }

      return password.toString()
    }

    fun checkCpf(et: String): Boolean{
      var str = et.replace("-", "").replace("/","").replace(".","")
      var calc: Int
      var num = 10
      var sum = 0
      for(x in 0..8) {
        calc = str[x].toString().toInt() * num
        sum += calc
        --num
      }
      var rest = sum % 11
      var test = 11 - rest
      if(test > 9) test = 0
      if(test != str[9].toString().toInt()) return false
      num = 11
      sum = 0
      for(x in 0..9) {
        calc = str[x].toString().toInt() * num
        sum += calc
        --num
      }
      rest = sum % 11
      test = 11 - rest
      if(test > 9) test = 0
      if(test != str[10].toString().toInt()) return false
      return true
    }
    fun checkCnpj(et: String): Boolean{
      var str = et.replace("-", "").replace("/","").replace(".","")
      var calc: Int
      var num = 5
      var sum = 0
      for(x in 0..11) {
        calc = str[x].toString().toInt() * num
        sum += calc
        --num
        if(num == 1) num = 9
      }
      var rest = sum % 11
      var test = 11 - rest
      if(test < 2) test = 0
      if(test != str[12].toString().toInt()) return false
      num = 6
      sum = 0
      for(x in 0..12) {
        calc = str[x].toString().toInt() * num
        sum += calc
        --num
        if(num == 1) num = 9
      }
      rest = sum % 11
      test = 11 - rest
      if(test < 2) test = 0
      if(test != str[13].toString().toInt()) return false
      return true
    }

    fun toLocalDate(date: LocalDateTime): LocalDate = LocalDate.of(date.year, date.monthValue, date.dayOfMonth)
  }
}
