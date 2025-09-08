package com.dh.ondot.data.base

interface Mapper<RESPONSE, MODEL> {
    fun responseToModel(response: RESPONSE?): MODEL
}