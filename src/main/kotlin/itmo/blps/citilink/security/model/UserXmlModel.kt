package itmo.blps.citilink.security.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "users")
data class UsersList(
    @JacksonXmlProperty(localName = "user")
    @JacksonXmlElementWrapper(useWrapping = false)
    val users: MutableList<UserXmlModel> = mutableListOf(),
)

data class UserXmlModel(
    @JacksonXmlProperty(localName = "username")
    val username: String,

    @JacksonXmlProperty(localName = "password")
    val password: String,

    @JacksonXmlProperty(localName = "role")
    val role: String
)