package core.model.mappers

import core.Mapper
import core.model.data.CountryEntity
import core.model.domain.Country

class CountryEntitiesToCountriesMapper : Mapper<List<CountryEntity>, List<Country>> {
  
  override fun map(value: List<CountryEntity>): List<Country> = ArrayList<Country>().apply {
    for (countryEntity in value) {
      add(Country(
        id = countryEntity.id,
        name = countryEntity.name,
        slug = countryEntity.slug,
        iso2 = countryEntity.iso2,
        confirmed = countryEntity.confirmed,
        deaths = countryEntity.deaths,
        recovered = countryEntity.recovered,
        newConfirmed = countryEntity.newConfirmed,
        newDeaths = countryEntity.newDeaths,
      ))
    }
  }
}