package core

/**
 * Converter from json to type [T]
 */
interface JsonConverter<T> {
  
  /** Covert [json] to [T] */
  fun convert(json: String): T
}