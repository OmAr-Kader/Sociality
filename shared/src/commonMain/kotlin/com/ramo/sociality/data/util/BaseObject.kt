package com.ramo.sociality.data.util

import kotlinx.serialization.json.JsonObject

abstract class BaseObject : Any() {
    abstract fun json(): JsonObject
}