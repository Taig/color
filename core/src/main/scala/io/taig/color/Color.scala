package io.taig.color

final case class Color(
    red: Channel,
    green: Channel,
    blue: Channel,
    alpha: Option[Channel]
) {

  /** Set the alpha channel to its min value */
  def transparent: Color = alpha(Channel.MinValue)

  /** Set the alpha channel to its min value */
  def opaque: Color = alpha(Channel.MaxValue)

  def alpha(value: Channel): Color = copy(alpha = Some(value))

  /** Remove the alpha channel information */
  def dropAlpha: Color = copy(alpha = None)

  /** Print the color as a hex string */
  def toHex: String = alpha match {
    case Some(alpha) =>
      f"#${red.value}%02x${green.value}%02x${blue.value}%02x${alpha.value}%02x"
    case None =>
      f"#${red.value}%02x${green.value}%02x${blue.value}%02x"
  }

  /** Print the color a rgb or rgba string */
  def toRgb: String = toRgbX(color => String.valueOf(color.value))
  def toRgbScaled: String = toRgbX(color => String.valueOf(color.scaled))
  def toRgb_% : String = toRgbX(color => String.valueOf(color.%))

  private def toRgbX(render: Channel => String): String = alpha match {
    case Some(alpha) =>
      "rgba(" + render(red) + ", " +
        render(green) + ", " +
        render(blue) + ", " +
        render(alpha) + ")"
    case None =>
      "rgb(" + render(red) + ", " +
        render(green) + ", " +
        render(blue) + ")"
  }
}

object Color {

  /** Convert a (hexadecimal) number to a `Color`
    *
    * Input values that are not within the `0x00000000 - 0xFFFFFFFF` range
    * will be rejected.
    *
    * This method should be used with hexadecimal number representations, e.g.:
    *
    * {{{
    * fromHex(0xFFF, digits = 3)
    * fromHex(0x000000, digits = 6)
    * fromHex(0xFF00, digits = 4)
    * fromHex(0xFF0000FF, digits = 8)
    * }}}
    *
    * This method requires the amount of digits because hexadecimal color values
    * should generally be represented as Strings, not numbers. This method will,
    * for instance, accept `0xF` as input value but might misinterpret it
    * without the explicit digits hint as it could mean `0x00F`, `0x000F`,
    * `0x00000F` or even `0x0000000F`.
    */
  def fromHex(value: Long, digits: Int): Either[String, Color] = {
    val channel: Long => Channel = value =>
      Channel.unsafeFromUnsignedShort(value.toShort)

    if (value < 0x00 || value > 0XFFFFFFFFL)
      Left("Color value must be between 0x00000000 and 0xFFFFFFFF")
    else if (digits == 3) {
      val red = channel(((value & 0xFFF) >> 8) * 17)
      val green = channel(((value & 0xFF) >> 4) * 17)
      val blue = channel((value & 0xF) * 17)
      Right(Color(red, green, blue, None))
    } else if (digits == 4) {
      val red = channel(((value & 0xFFFF) >> 12) * 17)
      val green = channel(((value & 0xFFF) >> 8) * 17)
      val blue = channel(((value & 0xFF) >> 4) * 17)
      val alpha = channel((value & 0xF) * 17)
      Right(Color(red, green, blue, Some(alpha)))
    } else if (digits == 6) {
      val red = channel((value & 0xFFFFFF) >> 16)
      val green = channel((value & 0xFFFF) >> 8)
      val blue = channel(value & 0xFF)
      Right(Color(red, green, blue, None))
    } else if (digits == 8) {
      val red = channel((value & 0xFFFFFFFF) >> 24)
      val green = channel((value & 0xFFFFFF) >> 16)
      val blue = channel((value & 0xFFFF) >> 8)
      val alpha = channel(value & 0xFF)
      Right(Color(red, green, blue, Some(alpha)))
    } else {
      val message = "Color value can only have 6 (rgba), 8 (rgba), 3 (rgb " +
        "shorthand) or 4 (rgba shorthand) digits"
      Left(message)
    }
  }

  /**
    * Parse a hexadecimal `String` to a `Color`
    *
    * The input `String` may start with a `#`.
    */
  def parseHex(value: String): Either[String, Color] = {
    val hex = if (value.startsWith("#")) value.substring(1) else value
    val digits = hex.length
    parseLong16(hex)
      .toRight("Invalid number format")
      .flatMap(fromHex(_, digits))
  }

  def unsafeParseHex(value: String): Color =
    parseHex(value).getOrElse(throw new IllegalArgumentException)

  val Black: Color =
    Color(Channel.MinValue, Channel.MinValue, Channel.MinValue, None)

  val White: Color =
    Color(Channel.MaxValue, Channel.MaxValue, Channel.MaxValue, None)
}
