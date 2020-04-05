package com.arsvechkarev.storage.queries

import com.arsvechkarev.storage.PopulationsTable.COLUMN_ISO2 as ISO2
import com.arsvechkarev.storage.PopulationsTable.COLUMN_POPULATION as POPULATION
import com.arsvechkarev.storage.PopulationsTable.TABLE_NAME as POPULATIONS

object PopulationsQueries {
  
  fun populationQuery(iso2: String) = "SELECT $POPULATION FROM $POPULATIONS WHERE $ISO2 = $iso2"
}
