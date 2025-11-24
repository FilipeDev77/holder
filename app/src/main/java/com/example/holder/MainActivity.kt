package com.example.holder

import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.holder.kms.KmsSimulator
import com.example.holder.utils.CryptoUtils
import javax.crypto.KeyGenerator
import java.io.File

class MainActivity : AppCompatActivity() {

    private val TAG = "HolderApp"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "==================== DEMO SECURE HOLDER ====================")

        val kms = KmsSimulator()

        // ---------------------------
        // 1️⃣ Holder demande un keyId
        // ---------------------------
        val keyInfo = kms.generateKey()
        Log.d(TAG, "KEK générée côté KMS (Android ne la voit pas)")
        Log.d(TAG, " -> keyId: ${keyInfo.keyId}")

        // ---------------------------
        // 2️⃣ Holder génère DEK local
        // ---------------------------
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        val dek = keyGen.generateKey()
        Log.d(TAG, "DEK générée (locale, exportable)")
        Log.d(TAG, " -> DEK: ${Base64.encodeToString(dek.encoded, Base64.DEFAULT)}")

        // ---------------------------
        // 3️⃣ Holder chiffre credentials
        // ---------------------------
        val credentials = "user:password"
        val (iv, encryptedData) = CryptoUtils.encryptAES(credentials.toByteArray(), dek)
        Log.d(TAG, "Credentials chiffrés")
        Log.d(TAG, " -> IV: ${Base64.encodeToString(iv, Base64.DEFAULT)}")
        Log.d(TAG, " -> Cipher: ${Base64.encodeToString(encryptedData, Base64.DEFAULT)}")

        // ---------------------------
        // 4️⃣ Holder envoie DEK + keyId au KMS → wrap
        // ---------------------------
        val wrappedKey = kms.wrapKey(keyInfo.keyId, dek)
        Log.d(TAG, "KMS a encapsulé la DEK")
        Log.d(TAG, " -> wrappedKey: ${Base64.encodeToString(wrappedKey, Base64.DEFAULT)}")

        // ---------------------------
        // 5️⃣ Holder stocke tout
        // ---------------------------
        val storedString =
            Base64.encodeToString(iv + encryptedData, Base64.DEFAULT) + ":" +
                    keyInfo.keyId + ":" +
                    Base64.encodeToString(wrappedKey, Base64.DEFAULT)

        val file = File(filesDir, "holder_data.enc")
        file.writeText(storedString)
        Log.d(TAG, "Données stockées")
        Log.d(TAG, " -> ${file.absolutePath}")

        // ---------------------------
        // 6️⃣ DÉCHIFFREMENT via KMS
        // ---------------------------
        val (storedBytes, keyIdStored, wrappedStored) = file.readText().split(":").let {
            Triple(
                Base64.decode(it[0], Base64.DEFAULT),
                it[1],
                Base64.decode(it[2], Base64.DEFAULT)
            )
        }

        val ivStored = storedBytes.copyOfRange(0, 16)
        val ciphStored = storedBytes.copyOfRange(16, storedBytes.size)

        // KMS UNWRAP (Holder envoie keyId + wrappedKey)
        val dekRecovered = kms.unwrapKey(keyIdStored, wrappedStored)
        Log.d(TAG, "KMS a UNWRAP la DEK")
        Log.d(TAG, " -> DEK: ${Base64.encodeToString(dekRecovered.encoded, Base64.DEFAULT)}")

        // Déchiffrement final
        val decrypted = CryptoUtils.decryptAES(ciphStored, dekRecovered, ivStored)
        Log.d(TAG, "Credentials déchiffrés via DEK unwrappée par KMS")
        Log.d(TAG, " -> ${String(decrypted)}")

        Log.d(TAG, "==================== FIN ====================")
    }
}