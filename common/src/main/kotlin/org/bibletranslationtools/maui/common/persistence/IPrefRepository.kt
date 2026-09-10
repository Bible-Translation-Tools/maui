package org.bibletranslationtools.maui.common.persistence

const val DEV_SERVER_NAME_KEY = "dev_server_name"
const val DEV_USER_NAME_KEY = "dev_user_name"
const val PROD_SERVER_NAME_KEY = "prod_server_name"
const val PROD_USER_NAME_KEY = "prod_user_name"

interface IPrefRepository {
    fun get(key: String, default: String = ""): String
    fun put(key: String, value: String)
}