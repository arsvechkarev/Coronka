package core.extenstions

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