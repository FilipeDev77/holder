package com.example.holder.kms

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import java.util.UUID

data class KeyIdInfo(val keyId: String)

class KmsSimulator {

    private val keyStore = mutableMapOf<String, SecretKey>()

    // Génère KEK et renvoie uniquement le keyId
    fun generateKey(): KeyIdInfo {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        val kek = keyGen.generateKey()
        val keyId = UUID.randomUUID().toString()
        keyStore[keyId] = kek
        return KeyIdInfo(keyId)
    }

    // Holder envoie (keyId + DEK) -> KMS wrap
    fun wrapKey(keyId: String, dek: SecretKey): ByteArray {
        val kek = keyStore[keyId] ?: throw IllegalArgumentException("Invalid keyId")
        val cipher = Cipher.getInstance("AESWrap")
        cipher.init(Cipher.WRAP_MODE, kek)
        return cipher.wrap(dek)
    }

    // Plus tard : Holder envoie (keyId + wrappedKey) -> KMS unwrap
    fun unwrapKey(keyId: String, wrapped: ByteArray): SecretKey {
        val kek = keyStore[keyId] ?: throw IllegalArgumentException("Invalid keyId")
        val cipher = Cipher.getInstance("AESWrap")
        cipher.init(Cipher.UNWRAP_MODE, kek)
        return cipher.unwrap(wrapped, "AES", Cipher.SECRET_KEY) as SecretKey
    }
}