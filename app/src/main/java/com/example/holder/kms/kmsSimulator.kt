package com.example.holder.kms

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import java.util.UUID

data class KeyInfo(val keyId: String, val key: SecretKey)

class KmsSimulator {

    private val keyStore = mutableMapOf<String, SecretKey>()

    fun generateKey(): KeyInfo {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        val kek = keyGen.generateKey()
        val keyId = UUID.randomUUID().toString()
        keyStore[keyId] = kek
        return KeyInfo(keyId, kek)
    }

    fun unwrapKey(keyId: String, wrappedKey: ByteArray): SecretKey {
        val kek = keyStore[keyId] ?: throw IllegalArgumentException("Invalid keyId")
        val cipher = Cipher.getInstance("AESWrap")
        cipher.init(Cipher.UNWRAP_MODE, kek)
        return cipher.unwrap(wrappedKey, "AES", Cipher.SECRET_KEY) as SecretKey
    }
}