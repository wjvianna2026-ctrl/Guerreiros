package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object WhatsAppHelper {

    fun generateBrotherhoodMessage(
        nickname: String,
        description: String,
        amount: Double,
        dueDate: String,
        pixKey: String,
        pixHolder: String
    ): String {
        return """
            ⚔️ *GUERREIROS DO BEM MOTO CLUBE* 🏍️💨
            
            Saudações fraternas, meu irmão *$nickname*!
            
            Passando no espírito de respeito e irmandade estradeira para lembrar da nossa contribuição do clube:
            
            📌 *Referência:* $description
            💰 *Valor:* R$ ${"%.2f".format(amount)}
            🗓️ *Vencimento:* $dueDate
            
            🔑 *Chave PIX do Moto Clube:*
            `$pixKey`
            Titular: $pixHolder
            
            Assim que fizer o lançamento, nos envie o comprovante por aqui para darmos a baixa no caixa do clube.
            
            *Honra, Respeito e Irmandade nas Estradas!* 🤜🤛
        """.trimIndent()
    }

    fun openWhatsApp(context: Context, phone: String, message: String) {
        try {
            // Clean phone digits
            val cleanPhone = phone.replace(Regex("[^0-9]"), "")
            val fullPhone = if (!cleanPhone.startsWith("55") && cleanPhone.length in 10..11) {
                "55$cleanPhone"
            } else cleanPhone

            val encodedMsg = Uri.encode(message)
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$fullPhone&text=$encodedMsg")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp não instalado ou link indisponível. Mensagem copiada!", Toast.LENGTH_LONG).show()
            copyToClipboard(context, "Mensagem Moto Clube", message)
        }
    }

    fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copiado para a área de transferência!", Toast.LENGTH_SHORT).show()
    }
}
