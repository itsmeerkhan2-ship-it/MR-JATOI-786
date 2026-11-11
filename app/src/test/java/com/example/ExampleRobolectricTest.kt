package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.PaymentMethod
import com.example.data.repository.AppRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("MR JATOI VIDEO", appName)
    }

    @Test
    fun `test watch to earn anti fraud reward`() {
        val repo = AppRepository.instance
        // Less than 10 seconds should not earn watch coins
        val shortWatchEarned = repo.recordVideoView("v1", 5)
        assertFalse(shortWatchEarned)

        // 10+ seconds continuous watch should trigger reward
        val validWatchEarned = repo.recordVideoView("v1", 12)
        assertTrue(validWatchEarned)
    }

    @Test
    fun `test minimum withdrawal limit check`() {
        val repo = AppRepository.instance
        // Less than 500 PKR should fail
        val (success, message) = repo.requestWithdrawal(
            method = PaymentMethod.JAZZCASH,
            accountTitle = "Ali Raza",
            accountNumber = "03129876543",
            bankName = "",
            amountPkr = 100.0
        )
        assertFalse(success)
        assertTrue(message.contains("Minimum withdrawal limit is 500 PKR"))
    }
}
