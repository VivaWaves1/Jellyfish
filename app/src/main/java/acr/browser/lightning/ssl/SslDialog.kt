package acr.browser.lightning.ssl

import acr.browser.lightning.BrowserApp
import acr.browser.lightning.R
import acr.browser.lightning.di.HiltEntryPoint
import acr.browser.lightning.dialog.BrowserDialog
import acr.browser.lightning.dialog.DialogItem
import acr.browser.lightning.dialog.DialogTab
import acr.browser.lightning.extensions.copyToClipboard
import acr.browser.lightning.extensions.snackbar
import android.app.Activity
import android.net.http.SslCertificate
import android.text.format.DateFormat
import dagger.hilt.android.EntryPointAccessors

/**
 * Shows an informative dialog with the provided [SslCertificate] information.
 */
fun Activity.showSslDialog(sslCertificate: SslCertificate, sslState: SslState) {
    val by = sslCertificate.issuedBy
    val to = sslCertificate.issuedTo
    val toName = to.dName?.takeIf(String::isNotBlank) ?: to.cName
    val issueDate = sslCertificate.validNotBeforeDate
    val expireDate = sslCertificate.validNotAfterDate
    val dateFormat = DateFormat.getDateFormat(applicationContext)
    val cm = EntryPointAccessors.fromApplication(BrowserApp.instance.applicationContext, HiltEntryPoint::class.java).clipboardManager

    var showAlgorithm = false
    var algoName = ""
    var oid = ""
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        showAlgorithm = true
        val cert = sslCertificate.x509Certificate
        algoName = cert?.sigAlgName ?: ""
        oid = cert?.sigAlgOID ?: ""
    }

    val icon = createSslDrawableForState(sslState)

    BrowserDialog.show(this, icon, to.cName, true,
        DialogTab(show=true, items=arrayOf(
            DialogItem(title = R.string.ssl_info_issued_by, text = by.dName) {
                cm.copyToClipboard(by.dName)
                snackbar(R.string.message_text_copied)
            },
            DialogItem(title = R.string.ssl_info_issued_to, text = toName) {
                cm.copyToClipboard(toName)
                snackbar(R.string.message_text_copied)
            },
            DialogItem(title = R.string.ssl_info_issued_on, text = dateFormat.format(issueDate)) {
                cm.copyToClipboard(dateFormat.format(issueDate))
                snackbar(R.string.message_text_copied)
            },
            DialogItem(title = R.string.ssl_info_expires_on, text = dateFormat.format(expireDate)) {
                cm.copyToClipboard(dateFormat.format(expireDate))
                snackbar(R.string.message_text_copied)
            },
            DialogItem(title = R.string.algorithm, text = algoName, show = showAlgorithm && algoName.isNotEmpty()) {
                cm.copyToClipboard(algoName)
                snackbar(R.string.message_text_copied)
            },
            DialogItem(title = R.string.oid, text = oid, show = showAlgorithm && oid.isNotEmpty()) {
                cm.copyToClipboard(oid)
                snackbar(R.string.message_text_copied)
            }
        ))
    )
}
