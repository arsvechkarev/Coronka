package core

/**
 * Creates [DateTime] objects
 */
interface DateTimeCreator {
  
  /**
   * Parses given [string] in format **2020-10-13T14:02** to [DateTime] object
   */
  fun createFromString(string: String): DateTime
  
  /**
   * Creates [DateTime] object that represents current date and time
   */
  fun getCurrent(): DateTime
}