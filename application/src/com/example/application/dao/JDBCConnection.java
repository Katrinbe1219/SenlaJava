package com.example.application.dao;

import com.example.custom_annotations.ConfigurableClass;
import com.example.custom_annotations.ConfigurationProperty;
import com.example.custom_annotations.Inject;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Inject
@ConfigurableClass
public class JDBCConnection {
    // так как используется DI то, класс будет и так синглтон


    @ConfigurationProperty(type="String", propertyName = "jdbc")
    private String url;

    @ConfigurationProperty(type="String", propertyName = "user")
    private String user;

    @ConfigurationProperty(type="String", propertyName = "password")
    private String password;

    private  static Connection connection;

//    JDBCConnection() throws SQLException {
//        this.connection = DriverManager.getConnection(this.url, this.user, this.password);
//    }

    public Connection getConnection() throws SQLException {
        if (connection == null) {
            this.connection = DriverManager.getConnection(this.url, this.user, this.password);

        }
        return this.connection;
    }


}
