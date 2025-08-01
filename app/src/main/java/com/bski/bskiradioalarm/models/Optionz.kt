package com.bski.bskiradioalarm.models

class Optionz {
    // Instance variables
    var name: String
    var age: Int

    companion object {
        const val MUTE_STORAGE_PREF_KEY = "mute"
        const val SNOOZE_STORAGE_PREF_KEY = "snooze"
        const val VOLUME_STORAGE_PREF_KEY = "volume"
    }

    constructor(name: String, age: Int) {
        this.name = name
        this.age = age
    }

    fun greet(): String {
        return "Hello, my name is $name and I am $age years old."
    }
}