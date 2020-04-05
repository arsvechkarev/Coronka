package com.arsvechkarev.storage.dao

import android.database.Cursor
import com.arsvechkarev.storage.PopulationsTable.COLUMN_POPULATION

class PopulationsDao {
  
  fun getPopulation(cursor: Cursor): Int {
    return cursor.getInt(cursor.getColumnIndex(COLUMN_POPULATION))
  }
}