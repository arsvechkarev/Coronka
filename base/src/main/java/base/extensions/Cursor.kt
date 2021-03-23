package base.extensions

import android.database.Cursor

fun Cursor.stringOfColumn(columnName: String): String = getString(getColumnIndex(columnName))

fun Cursor.intOfColumn(columnName: String): Int = getInt(getColumnIndex(columnName))

inline fun Cursor.iterate(block: Cursor.() -> Unit) {
  while (moveToNext()) {
    block(this)
  }
}

inline fun <T> Cursor.collectToList(block: Cursor.() -> T): List<T> {
  val list = ArrayList<T>()
  while (moveToNext()) {
    val t = block(this)
    list.add(t)
  }
  return list
}

inline fun <K, V> Cursor.collectToMap(builder: MapBuilder<K, V>.() -> Unit): Map<K, V> {
  val map = HashMap<K, V>()
  val mapBuilder = MapBuilder<K, V>().apply(builder)
  while (moveToNext()) {
    val key = mapBuilder.keyFunction!!.invoke(this)
    val value = mapBuilder.valueFunction!!.invoke(this)
    map[key] = value
  }
  return map
}

class MapBuilder<K, V> {
  
  @PublishedApi
  internal var keyFunction: (Cursor.() -> K)? = null
  
  @PublishedApi
  internal var valueFunction: (Cursor.() -> V)? = null
  
  fun key(function: Cursor.() -> K) {
    this.keyFunction = function
  }
  
  fun value(function: Cursor.() -> V) {
    this.valueFunction = function
  }
}
