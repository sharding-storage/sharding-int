package sharding.storage.itmo.integration.model

data class SchemaResponse(
    val nodes: List<String>,
    val virtualNodes: Int,
    val version: Int
)