package core

/**
 * Represents a storage that allows to validate cache
 */
interface CacheValidationStorage : KeyValueStorage {
  
  /**
   * Saves current time in milliseconds by [key] to later be requested when
   * cache is needed to be checked
   */
  fun saveTime(key: String, currentMillis: Long) = putLong(key, currentMillis)
  
  /**
   * Returns true if time in millis by [key] has been in cache no more
   * than [maxMinutesInCache] minutes
   */
  fun isUpToDate(key: String, maxMinutesInCache: Int): Boolean
}