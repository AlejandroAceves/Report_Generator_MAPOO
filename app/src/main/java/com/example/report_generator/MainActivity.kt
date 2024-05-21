package com.example.report_generator

import android.Manifest
import android.util.Log
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Xml
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.xmlpull.v1.XmlSerializer
import java.io.File
import java.io.FileWriter
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 1
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnGenerateXML: Button = findViewById(R.id.btnGenerateXML)
        btnGenerateXML.setOnClickListener {
            val btnGenerateXML: Button = findViewById(R.id.btnGenerateXML)
            btnGenerateXML.setOnClickListener {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
                } else {
                    generateXMLFile()
                }
            }
        }
    }

    private fun generateXMLFile() {
        thread {
            val connectionHelper = ConnectionHelper()
            val connection: Connection? = connectionHelper.connection()

            connection?.let {
                try {
                    Log.d(TAG, "Connected to database")
                    val statement: Statement = it.createStatement()
                    val resultSet: ResultSet = statement.executeQuery("SELECT * FROM ATTENDANCE")

                    // Create XML file
                    val file = File(getExternalFilesDir(null), "REPORT.xml")
                    val writer = FileWriter(file)

                    // XML Serializer
                    val xmlSerializer: XmlSerializer = Xml.newSerializer()
                    xmlSerializer.setOutput(writer)

                    // Start Document
                    xmlSerializer.startDocument("UTF-8", true)
                    xmlSerializer.startTag(null, "Data")

                    // Write data
                    while (resultSet.next()) {
                        xmlSerializer.startTag(null, "Row")
                        // Attencance and Atendee
                        xmlSerializer.startTag(null, "attendantId")
                        xmlSerializer.text(resultSet.getString("attendantId"))
                        xmlSerializer.endTag(null, "attendantId")

                        xmlSerializer.startTag(null, "atendeeId")
                        xmlSerializer.text(resultSet.getString("atendeeId"))
                        xmlSerializer.endTag(null, "atendeeId")


                        //eventid, entrance and exit
                        xmlSerializer.startTag(null, "eventId")
                        xmlSerializer.text(resultSet.getString("eventId"))
                        xmlSerializer.endTag(null, "eventId")

                        xmlSerializer.startTag(null, "entryTime")
                        xmlSerializer.text(resultSet.getString("entryTime"))
                        xmlSerializer.endTag(null, "entryTime")

                        xmlSerializer.startTag(null, "exitTime")
                        xmlSerializer.text(resultSet.getString("exitTime"))
                        xmlSerializer.endTag(null, "exitTime")

                        xmlSerializer.endTag(null, "Row")
                    }

                    // End Document
                    xmlSerializer.endTag(null, "Data")
                    xmlSerializer.endDocument()

                    writer.close()
                    resultSet.close()
                    statement.close()
                    it.close()

                    runOnUiThread {
                        Toast.makeText(this, "XML file created successfully", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "XML file created successfully")
                    }

                } catch (e: Exception) {
                    Log.e(TAG, "Error creating XML file", e)
                    runOnUiThread {
                        Toast.makeText(this, "Error creating XML file", Toast.LENGTH_SHORT).show()
                    }
                }
            } ?: runOnUiThread {
                Toast.makeText(this, "Error connecting to database", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error connecting to database")
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                generateXMLFile()
            } else {
                Toast.makeText(this, "Permission denied to write to external storage", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
