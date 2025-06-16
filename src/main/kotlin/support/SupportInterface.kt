package com.rosana_diana.support

interface SupportInterface {
    fun allSupports(): List<Support>
    fun getSupportById(id: Int): Support?
    fun createSupport(support: Support): Support
    fun deleteSupport(id: Int): Boolean
}