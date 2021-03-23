package core

/** Formats time to readable string */
interface DateTimeFormatter {
  
  /**
   * Formats [stringDate] in to readable sting, like "1 hour ago", "2 minutes ago"
   *
   * Format of the date: 2020-10-13T14:02:07+0000
   */
  fun formatPublishedDate(stringDate: String): String
}