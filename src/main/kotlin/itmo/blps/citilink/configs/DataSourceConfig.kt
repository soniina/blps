package itmo.blps.citilink.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup
import javax.sql.DataSource

@Configuration
class DataSourceConfig {

    @Bean
    fun dataSource(): DataSource {
        val lookup = JndiDataSourceLookup()
        return lookup.getDataSource("java:jboss/datasources/CitilinkDS")
    }
}