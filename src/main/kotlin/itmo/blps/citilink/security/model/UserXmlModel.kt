package itmo.blps.citilink.security.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "users")
data class UsersList(
    @JacksonXmlProperty(localName = "user")
    @JacksonXmlElementWrapper(useWrapping = false)
    val users: List<UserXmlModel> = mutableListOf()
)

data class UserXmlModel(
    val username: String = "",
    val password: String = "",
    val role: String = ""
)