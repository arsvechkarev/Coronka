package core

/** Represent a date and a time*/
interface DateTime {
  
  val year: Int
  val monthName: String
  val monthValue: Int
  val dayOfMonth: Int
  val dayOfYear: Int
  val hour: Int
  val minute: Int
  
  /**
   * Returns this date and time in format **YYYY-MM-DDTHH:mm**, for example: 2020-10-13T14:02
   */
  override fun toString(): String
}