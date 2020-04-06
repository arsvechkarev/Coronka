package com.arsvechkarev.storage.dao

import android.database.Cursor
import com.arsvechkarev.storage.PopulationsTable.COLUMN_ISO2
import com.arsvechkarev.storage.PopulationsTable.COLUMN_POPULATION
import core.model.Population

class PopulationsDao {
  
  fun getAll(cursor: Cursor): List<Population> {
    val populations = ArrayList<Population>()
    while (cursor.moveToNext()) {
      val info = Population(
        cursor.getString(cursor.getColumnIndex(COLUMN_ISO2)),
        cursor.getInt(cursor.getColumnIndex(COLUMN_POPULATION))
      )
      populations.add(info)
    }
    cursor.close()
    return populations
  }
}