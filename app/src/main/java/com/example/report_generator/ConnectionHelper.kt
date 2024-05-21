package com.example.report_generator

import android.os.StrictMode
import android.util.Log
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class ConnectionHelper {
    private val ip = "192.163.100.7:49682"
    private val dbName = "FoxGuard"
    private val username = "Android_ADMIN"
    private val psswrd = "1234"


    fun connection(): Connection?{
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var conn : Connection? = null
        val connString : String

        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance()
            connString= "jdbc:jtds:sqlserver://$ip;databaseName=$dbName;user=$username;password=$psswrd"
            conn = DriverManager.getConnection(connString)

        }catch (ex: SQLException){
            Log.e("Error: ", ex.message.toString())
        }
        return conn
    }
}