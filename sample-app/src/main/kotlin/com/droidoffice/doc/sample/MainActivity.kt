package com.droidoffice.doc.sample

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var logView: TextView
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val demos = Demos(this)

        val demoList = listOf(
            "Basic Read/Write" to demos::basicReadWrite,
            "Styles" to demos::styles,
            "Tables" to demos::tables,
            "Lists" to demos::lists,
            "Header/Footer" to demos::headerFooter,
            "Images" to demos::images,
            "Password" to demos::passwordProtection,
            "HTML/Text Export" to demos::htmlTextExport,
            "Full Report" to demos::fullReport,
        )

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        for ((name, demo) in demoList) {
            layout.addView(Button(this).apply {
                text = name
                setOnClickListener { runDemo(name, demo) }
            })
        }

        layout.addView(Button(this).apply {
            text = "Run All"
            setOnClickListener {
                scope.launch {
                    var pass = 0; var fail = 0
                    for ((name, demo) in demoList) {
                        log("=== $name ===")
                        try { log(demo()); log("PASS"); pass++ }
                        catch (e: Exception) { log("FAIL: ${e.message}"); fail++ }
                    }
                    log("\n=== Results: $pass passed, $fail failed ===")
                }
            }
        })

        logView = TextView(this).apply {
            textSize = 12f
            setPadding(0, 16, 0, 0)
        }
        layout.addView(logView)

        val scrollView = ScrollView(this)
        scrollView.addView(layout)
        setContentView(scrollView)

        // Auto-run if launched with intent extra
        if (intent.hasExtra("autorun")) {
            layout.post {
                scope.launch {
                    var pass = 0; var fail = 0
                    for ((name, demo) in demoList) {
                        log("=== $name ===")
                        try { log(demo()); log("PASS"); pass++ }
                        catch (e: Exception) { log("FAIL: ${e.message}"); fail++ }
                    }
                    log("\n=== Results: $pass passed, $fail failed ===")
                }
            }
        }
    }

    private fun runDemo(name: String, demo: suspend () -> String) {
        scope.launch {
            log("=== $name ===")
            try { log(demo()); log("PASS") }
            catch (e: Exception) { log("FAIL: ${e.message}") }
        }
    }

    private fun log(msg: String) {
        logView.append("$msg\n")
    }
}
