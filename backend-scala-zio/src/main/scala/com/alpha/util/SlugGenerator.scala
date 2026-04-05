package com.alpha.util

import java.text.Normalizer
import java.util.regex.Pattern

object SlugGenerator:

  private val nonWordPattern    = Pattern.compile("[^\\w\\s-]")
  private val whitespacePattern = Pattern.compile("\\s+")
  private val dashPattern       = Pattern.compile("-+")

  def generate(name: String): String =
    val normalized       = Normalizer.normalize(name, Normalizer.Form.NFD)
      .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
      .toLowerCase
    val noSpecialChars   = nonWordPattern.matcher(normalized).replaceAll("")
    val noWhitespace     = whitespacePattern.matcher(noSpecialChars).replaceAll("-")
    val noMultipleDashes = dashPattern.matcher(noWhitespace).replaceAll("-")
    noMultipleDashes.stripPrefix("-").stripSuffix("-")

  def generateUnique(base: String, exists: String => Boolean, maxAttempts: Int = 10): String =
    val slug = generate(base)
    if !exists(slug) then slug
    else
      (1 to maxAttempts).find(i => !exists(s"$slug-$i")) match
        case Some(i) => s"$slug-$i"
        case None    => s"$slug-${System.currentTimeMillis()}"
