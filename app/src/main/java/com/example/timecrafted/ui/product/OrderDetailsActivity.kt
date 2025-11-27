package com.example.timecrafted.ui.product

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.timecrafted.R
import com.example.timecrafted.data.model.Order
import com.example.timecrafted.data.repository.OrderRepository
import com.example.timecrafted.ui.product.adapter.OrderItemAdapter
import com.example.timecrafted.ui.product.model.ReviewProductOption
import com.example.timecrafted.ui.product.WriteReviewActivity
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
class OrderDetailsActivity : AppCompatActivity() {

    private lateinit var tvOrderId: TextView
    private lateinit var tvOrderDate: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvTracking: TextView
    private lateinit var tvShippingAddress: TextView
    private lateinit var tvPaymentDetails: TextView
    private lateinit var subtotalValueView: TextView
    private lateinit var shippingValueView: TextView
    private lateinit var taxesValueView: TextView
    private lateinit var platformFeeValueView: TextView
    private lateinit var totalValueView: TextView
    private lateinit var platformFeeRow: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderItemAdapter
    private lateinit var writeReviewBtn: Button
    private lateinit var downloadInvoiceBtn: Button

    private val orderRepository by lazy { OrderRepository() }
    private var orderId: String = ""
    private var currentOrder: Order? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        orderId = intent.getStringExtra("orderId") ?: ""
        if (orderId.isEmpty()) {
            finish()
            return
        }

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        tvOrderId = findViewById(R.id.tvOrderId)
        tvOrderDate = findViewById(R.id.tvOrderDate)
        tvStatus = findViewById(R.id.tvStatus)
        tvTracking = findViewById(R.id.tvTracking)
        tvShippingAddress = findViewById(R.id.tvShippingAddress)
        tvPaymentDetails = findViewById(R.id.tvPaymentDetails)
        platformFeeRow = findViewById(R.id.platform_fee_row)
        subtotalValueView = setupRow(R.id.subtotal_row, getString(R.string.subtotal_label))
        shippingValueView = setupRow(R.id.shipping_row, getString(R.string.shipping_label))
        taxesValueView = setupRow(R.id.taxes_row, getString(R.string.taxes_label))
        platformFeeValueView = setupRow(R.id.platform_fee_row, getString(R.string.platform_fee_label))
        totalValueView = setupRow(R.id.total_row, getString(R.string.total_label))
        recyclerView = findViewById(R.id.rvOrderItems)
        writeReviewBtn = findViewById(R.id.writeReviewBtn)
        downloadInvoiceBtn = findViewById(R.id.downloadInvoiceBtn)

        adapter = OrderItemAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        backBtn.setOnClickListener { finish() }
        downloadInvoiceBtn.setOnClickListener {
            currentOrder?.let { downloadInvoice(it) }
        }
        writeReviewBtn.setOnClickListener {
            currentOrder?.let { launchReview(it) }
        }

        loadOrderDetails()
    }

    private fun setupRow(rowId: Int, label: String): TextView {
        val row = findViewById<View>(rowId)
        row.findViewById<TextView>(R.id.keyText).text = label
        return row.findViewById(R.id.valueText)
    }

    private fun loadOrderDetails() {
        orderRepository.getOrderById(
            orderId = orderId,
            onSuccess = { order -> displayOrder(order) },
            onError = {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                finish()
            }
        )
    }

    private fun displayOrder(order: Order) {
        currentOrder = order
        tvOrderId.text = getString(R.string.order_id_display, order.id.take(8))

        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        val date = Date(order.createdAt)
        tvOrderDate.text = getString(R.string.order_placed_on, dateFormat.format(date))

        tvStatus.text = getString(
            R.string.order_status_format,
            order.orderStatus.replaceFirstChar { it.uppercaseChar() }
        )

        val trackingNumber = order.id.replace("-", "").take(10)
        tvTracking.text = getString(R.string.tracking_number_format, trackingNumber)

        order.shippingAddress?.let { address ->
            val addressText = buildString {
                append(address.fullName)
                if (address.phoneNumber.isNotEmpty()) {
                    append("\n${getString(R.string.phone_label, address.phoneNumber)}")
                }
                append("\n${address.addressLine1}")
                if (address.addressLine2.isNotEmpty()) {
                    append(", ${address.addressLine2}")
                }
                append("\n${address.city}, ${address.state} ${address.zipCode}")
                if (address.country.isNotEmpty()) {
                    append(", ${address.country}")
                }
            }
            tvShippingAddress.text = addressText
        }

        val format = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 2
        }
        val currency = order.currency

        subtotalValueView.text = currencyValue(currency, format.format(order.subtotal))
        shippingValueView.text =
            if (order.shipping == 0.0) getString(R.string.free_text)
            else currencyValue(currency, format.format(order.shipping))
        taxesValueView.text = currencyValue(currency, format.format(order.taxes))

        if (order.platformFee > 0) {
            platformFeeRow.isVisible = true
            platformFeeValueView.text = currencyValue(currency, format.format(order.platformFee))
        } else {
            platformFeeRow.isVisible = false
        }

        totalValueView.text = currencyValue(currency, format.format(order.total))

        val paymentText = when (order.paymentMethod.lowercase(Locale.getDefault())) {
            "cod" -> getString(R.string.payment_cod)
            "razorpay" -> getString(R.string.payment_razorpay)
            else -> order.paymentMethod
        }
        tvPaymentDetails.text = paymentText

        writeReviewBtn.isVisible =
            order.orderStatus.equals("delivered", ignoreCase = true) && order.items.isNotEmpty()

        adapter.submitList(order.items)
    }

    private fun launchReview(order: Order) {
        val reviewOptions = order.items
            .filter { it.productId.isNotBlank() }
            .map { ReviewProductOption(it.productId, it.name) }

        if (reviewOptions.isEmpty()) {
            Toast.makeText(this, R.string.review_missing_product, Toast.LENGTH_SHORT).show()
            return
        }

        startActivity(
            WriteReviewActivity.createIntent(
                context = this,
                orderId = order.id,
                products = ArrayList(reviewOptions)
            )
        )
    }

    private fun downloadInvoice(order: Order) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val titlePaint = Paint().apply {
            textSize = 18f
            isFakeBoldText = true
        }
        val bodyPaint = Paint().apply {
            textSize = 12f
        }

        var y = 40f
        canvas.drawText(getString(R.string.invoice_title), 20f, y, titlePaint)
        y += 24f
        canvas.drawText(getString(R.string.order_id_display, order.id.take(8)), 20f, y, bodyPaint)
        y += 18f
        canvas.drawText(
            getString(
                R.string.order_placed_on,
                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(order.createdAt))
            ),
            20f,
            y,
            bodyPaint
        )
        y += 24f
        canvas.drawText(getString(R.string.invoice_billing_to), 20f, y, titlePaint)
        tvShippingAddress.text.split("\n").forEach { line ->
            if (line.isNotBlank()) {
                y += 16f
                canvas.drawText(line, 20f, y, bodyPaint)
            }
        }
        y += 24f
        canvas.drawText(getString(R.string.order_items_title), 20f, y, titlePaint)

        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 2
        }

        order.items.forEach { item ->
            y += 18f
            val line = "${item.name} x${item.quantity} - ${
                currencyValue(order.currency, numberFormat.format(item.price * item.quantity))
            }"
            canvas.drawText(line, 20f, y, bodyPaint)
        }

        y += 24f
        canvas.drawText(
            getString(R.string.invoice_total_line, totalValueView.text.toString()),
            20f,
            y,
            titlePaint
        )

        document.finishPage(page)

        val downloadsDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        if (downloadsDir?.exists() == false) downloadsDir.mkdirs()
        val file = File(downloadsDir, "Invoice_${order.id.take(8)}.pdf")

        runCatching {
            FileOutputStream(file).use { document.writeTo(it) }
        }.onSuccess {
            Toast.makeText(
                this,
                getString(R.string.invoice_saved, file.absolutePath),
                Toast.LENGTH_LONG
            ).show()
        }.onFailure {
            Toast.makeText(
                this,
                getString(R.string.invoice_failed, it.localizedMessage ?: "Error"),
                Toast.LENGTH_LONG
            ).show()
        }.also {
            document.close()
        }
    }

    private fun currencyValue(symbol: String, amount: String): String = "$symbol$amount"
}