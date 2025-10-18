package com.ondot.network.base

interface Mapper<RESPONSE, MODEL> {
    fun responseToModel(response: RESPONSE?): MODEL
}