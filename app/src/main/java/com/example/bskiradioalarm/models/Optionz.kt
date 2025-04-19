package com.example.bskiradioalarm.models

class Optionz {
    // Instance variables
    var name: String
    var age: Int

    companion object {
        const val MUTE_STORAGE_PREF_KEY = "mute"
        const val DEFAULT_AGE = 30
    }

    constructor(name: String, age: Int) {
        this.name = name
        this.age = age
    }

    fun greet(): String {
        return "Hello, my name is $name and I am $age years old."
    }
}