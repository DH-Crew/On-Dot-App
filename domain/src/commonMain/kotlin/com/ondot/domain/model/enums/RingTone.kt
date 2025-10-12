package com.dh.ondot.domain.model.enums

enum class RingTone(val id: String) {
    DANCING_IN_THE_STARDUST("dancing_in_the_stardust"),
    IN_THE_CITY_LIGHTS_MIST("in_the_city_lights_mist"),
    FRACTURED_LOVE("fractured_love"),
    CHASING_LIGHTS("chasing_lights"),
    ASHES_OF_US("ashes_of_us"),
    HEATING_SUN("heating_sun"),
    MEDAL("medal"),
    EXCITING_SPORTS_COMPETITIONS("exciting_sports_competitions"),
    POSITIVE_WAY("positive_way"),
    ENERGETIC_HAPPY_UPBEAT_ROCK_MUSIC("energetic_happy_upbeat_rock_music"),
    ENERGY_CATCHER("energy_catcher");

    companion object {
        fun getNameById(id: String): RingTone {
            return entries.find { it.id == id }?: DANCING_IN_THE_STARDUST
        }
    }
}