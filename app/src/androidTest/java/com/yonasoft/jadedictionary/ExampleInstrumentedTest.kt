package com.yonasoft.jadedictionary

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yonasoft.jadedictionary.features.word.data.local.cc.CCWordDatabase
import kotlinx.coroutines.runBlocking

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.yonasoft.jadedictionary", appContext.packageName)
    }

    @RunWith(AndroidJUnit4::class)
    class CCWordDatabaseTest {

        @Test
        fun testDatabaseLoadsData() = runBlocking {
            // Obtain the application context
            val context: Context = ApplicationProvider.getApplicationContext()

            // Build the database using your asset
            val db = CCWordDatabase.getDatabase(context)
            val dao = db.ccWordDao()

            // Query all words from the database
            val words = dao.getAllWords()

            // Print the result size for debugging
            println("Found ${words.size} words in the database.")

            // Assert that the database contains at least one record
            assertTrue("Database should contain some words", words.isNotEmpty())
        }
    }
}