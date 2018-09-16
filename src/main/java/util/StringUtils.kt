package util

import java.util.*

object StringUtils {
  private val random = Random()
  private val numericAlphabetic = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
  private val loremIpsum = ("Cras vitae tellus mattis, fringilla nunc et, luctus felis.\n" +
    "Nullam in lectus venenatis, aliquam arcu in, sollicitudin erat.\n" +
    "In at tellus et risus posuere maximus a ut lectus.\n" +
    "Etiam ac urna auctor, aliquam nisi et, fringilla velit.\n" +
    "Quisque pretium enim sit amet ligula imperdiet, in convallis metus porttitor.\n" +
    "Suspendisse hendrerit mi non neque condimentum, ac auctor tellus rutrum.\n" +
    "Vestibulum molestie neque nec consequat dapibus.\n" +
    "Fusce sed lacus a erat ultricies placerat et id magna.\n" +
    "Mauris at ipsum bibendum, maximus turpis ac, imperdiet nibh.\n" +
    "Phasellus vitae enim aliquam, auctor dolor ac, auctor risus.\n" +
    "Cras ut neque sed nisl sagittis elementum.\n" +
    "In et est sodales, aliquet dolor in, consequat magna.\n" +
    "Nam in est bibendum, bibendum tortor ac, rutrum odio.\n" +
    "Donec ut erat feugiat, ornare arcu non, feugiat elit.\n" +
    "Morbi dignissim lorem at nunc porttitor, vitae convallis magna placerat.\n" +
    "Vivamus eu orci vitae libero luctus vehicula ut nec quam.\n" +
    "Proin in turpis eget dui condimentum vulputate.\n" +
    "Suspendisse viverra ex eget turpis scelerisque, id pharetra ligula eleifend.\n" +
    "Ut hendrerit eros et ullamcorper lobortis.\n" +
    "Suspendisse vulputate massa eleifend sapien condimentum elementum.\n" +
    "Morbi dictum est non dui molestie aliquet.\n" +
    "Phasellus ac elit ac velit vulputate vestibulum non in turpis.\n" +
    "Phasellus sed enim eget eros semper rhoncus.\n" +
    "Quisque ultricies mi id eros pellentesque, ac consectetur nisi accumsan.\n" +
    "Aliquam finibus nulla quis urna hendrerit porta.\n" +
    "Curabitur id leo dictum, ullamcorper nisl vitae, sollicitudin neque.\n" +
    "Fusce consectetur augue et purus blandit pulvinar.\n" +
    "Maecenas porta lacus a lobortis tincidunt.\n" +
    "Proin accumsan sapien id tortor pharetra faucibus.\n" +
    "Morbi eget orci non quam efficitur molestie.\n" +
    "Duis malesuada mauris sed quam euismod, id euismod augue pulvinar.\n" +
    "Ut iaculis nisi ut augue fringilla, vel mattis nibh efficitur.\n" +
    "In a lectus cursus, sodales nunc eu, fringilla arcu.\n" +
    "Mauris elementum libero semper mauris accumsan volutpat.\n" +
    "Curabitur commodo mi at varius consectetur.\n" +
    "Ut non justo ac justo ornare posuere vel in enim.\n" +
    "Mauris semper lorem ut eleifend mollis.\n" +
    "Nunc sed tellus porta, vulputate orci sit amet, viverra nunc.\n" +
    "Aenean malesuada tortor pharetra posuere tempor.\n" +
    "Vivamus in neque sed mi ultricies ullamcorper et non metus.\n" +
    "Ut suscipit orci sed ultricies volutpat.\n" +
    "Vestibulum a diam aliquam, porttitor ante ac, faucibus leo.\n" +
    "Curabitur tincidunt massa vitae arcu accumsan, id aliquet nunc auctor.\n" +
    "Curabitur et lacus ac erat euismod dictum a a nunc.\n" +
    "Nulla at purus non neque egestas eleifend lacinia a odio.\n" +
    "Donec sit amet lorem sit amet erat fringilla rhoncus.\n" +
    "Proin laoreet libero eget nisl tempor tristique.\n" +
    "Quisque luctus tellus sodales, posuere orci ac, viverra urna.\n" +
    "Duis rutrum lacus quis orci rutrum, vitae bibendum velit laoreet.\n" +
    "Sed nec nibh in purus hendrerit sagittis.\n" +
    "Fusce quis sem maximus, placerat odio vitae, ultricies neque.\n" +
    "Cras quis nisi sodales, faucibus mi at, mattis nulla.\n" +
    "Praesent a sem sodales massa ornare rhoncus.\n" +
    "Phasellus ultrices turpis nec dui mollis, eget sollicitudin felis semper.\n" +
    "Quisque non urna vitae sapien dignissim aliquam.\n" +
    "Maecenas ut eros auctor, auctor magna ut, sodales lectus.\n" +
    "Mauris semper massa at pharetra lobortis.\n" +
    "Integer a sem at lectus rutrum interdum at et tellus.").split("\n")

  fun generateRandomString(len: Int): String {
    val bytes = ByteArray(len)
    random.nextBytes(bytes)

    val sb = StringBuilder()
    val alphabetLen = numericAlphabetic.length

    for (i in 0 until len) {
      sb.append(numericAlphabetic[Math.abs(bytes[i] % alphabetLen)])
    }

    return sb.toString()
  }

  fun randomLoremIpsumLine(): String {
    return loremIpsum[Math.abs(random.nextInt(loremIpsum.size))]
  }

}