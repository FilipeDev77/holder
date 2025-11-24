package com.example.holder

import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.holder.kms.KmsSimulator
import com.example.holder.utils.CryptoUtils
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import java.io.File

class MainActivity : AppCompatActivity() {

    private val TAG = "HolderApp"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "==================== DEMO SECURE HOLDER ====================")

        // ----------------------
        // Demander KEK au KMS
        // ----------------------
        val kms = KmsSimulator()
        val keyInfo = kms.generateKey()
        Log.d(TAG, "KEK reçu du KMS")
        Log.d(TAG, "   -> keyId: ${keyInfo.keyId}")
        Log.d(TAG, "   -> KEK (Base64): ${Base64.encodeToString(keyInfo.key.encoded, Base64.DEFAULT)}")

        // ----------------------
        // Générer DEK classique en mémoire (pour CryptoUtils)
        // ----------------------
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        val dek = keyGen.generateKey()
        Log.d(TAG, "DEK générée en mémoire (non-Keystore, exportable)")
        Log.d(TAG, "   -> DEK (Base64): ${Base64.encodeToString(dek.encoded, Base64.DEFAULT)}")

        // ----------------------
        // Chiffrement des credentials
        // ----------------------
        val credentials = "user:password"
        val (iv, encryptedData) = CryptoUtils.encryptAES(credentials.toByteArray(), dek)
        Log.d(TAG, "Credentials chiffrés")
        Log.d(TAG, "   -> IV (Base64): ${Base64.encodeToString(iv, Base64.DEFAULT)}")
        Log.d(TAG, "   -> EncryptedData (Base64): ${Base64.encodeToString(encryptedData, Base64.DEFAULT)}")

        // ----------------------
        // Encapsulation DEK avec KEK du KMS
        // ----------------------
        val wrapCipher = javax.crypto.Cipher.getInstance("AESWrap")
        wrapCipher.init(javax.crypto.Cipher.WRAP_MODE, keyInfo.key)
        val wrappedKey = wrapCipher.wrap(dek)
        Log.d(TAG, "DEK encapsulée avec KEK du KMS")
        Log.d(TAG, "   -> Wrapped DEK (Base64): ${Base64.encodeToString(wrappedKey, Base64.DEFAULT)}")

        // ----------------------
        // Stockage credentials chiffrés + wrappedKey + keyId sur fichier interne
        // ----------------------
        val storedString = Base64.encodeToString(iv + encryptedData, Base64.DEFAULT) + ":" +
                keyInfo.keyId + ":" +
                Base64.encodeToString(wrappedKey, Base64.DEFAULT)

        val file = File(filesDir, "holder_data.enc")
        file.writeText(storedString)
        Log.d(TAG, "Données stockées sur disque interne")
        Log.d(TAG, "   -> File path: ${file.absolutePath}")

        // ----------------------
        // Déchiffrement pour vérification
        // ----------------------
        val storedParts = file.readText().split(":")
        val storedBytes = Base64.decode(storedParts[0], Base64.DEFAULT)
        val ivStored = storedBytes.copyOfRange(0, 16)
        val encryptedStored = storedBytes.copyOfRange(16, storedBytes.size)

        val decrypted = CryptoUtils.decryptAES(encryptedStored, dek, ivStored)
        Log.d(TAG, "Credentials déchiffrés pour vérification")
        Log.d(TAG, "   -> ${String(decrypted)}")

        Log.d(TAG, "==================== FIN DU FLUX ====================")
    }
}