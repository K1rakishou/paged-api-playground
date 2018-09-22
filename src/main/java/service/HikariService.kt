package service

import java.sql.SQLException
import com.zaxxer.hikari.HikariDataSource
import com.zaxxer.hikari.HikariConfig
import java.sql.Connection


class HikariService {

  private val config = HikariConfig()
  private val ds: HikariDataSource

  init {
    Class.forName("org.h2.Driver")

    config.jdbcUrl = "jdbc:h2:mem:test"
    config.username = "sa"
    config.password = ""
    ds = HikariDataSource(config)
  }

  @Throws(SQLException::class)
  fun getConnection(): Connection {
    return ds.connection
  }

}