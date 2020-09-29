package core.db

import android.database.Cursor
import core.model.CountryMetaInfo

class CountriesMetaInfoDao {
  
  fun getAll(cursor: Cursor): List<CountryMetaInfo> {
    val populations = ArrayList<CountryMetaInfo>()
    while (cursor.moveToNext()) {
      val info = CountryMetaInfo(
        cursor.getString(cursor.getColumnIndex(CountriesMetaInfoTable.COLUMN_ISO2)),
        cursor.getInt(cursor.getColumnIndex(CountriesMetaInfoTable.COLUMN_POPULATION)),
        cursor.getString(cursor.getColumnIndex(CountriesMetaInfoTable.COLUMN_REGION))
      )
      populations.add(info)
    }
    cursor.close()
    return populations
  }
}