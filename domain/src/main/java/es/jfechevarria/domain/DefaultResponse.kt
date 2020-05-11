package es.jfechevarria.domain

data class DefaultResponse<T>(
    val message: String,
    val data: T,
    val meta: MetaDefault?
)

data class MetaDefault(
    val current_page: Int,
    val from: Int,
    val last_page: Int,
    val per_page: Int,
    val to: Int,
    val total: Int
)