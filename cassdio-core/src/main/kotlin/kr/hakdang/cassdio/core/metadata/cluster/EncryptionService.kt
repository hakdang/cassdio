package kr.hakdang.cassdio.core.metadata.cluster

import kr.hakdang.cassdio.core.metadata.config.MetadataBootstrapProperties
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

interface EncryptionService {
    fun encrypt(plaintext: String?): String?

    fun decrypt(ciphertext: String?): String?
}

@Service
class AesGcmEncryptionService(
    properties: MetadataBootstrapProperties,
    private val secureRandom: SecureRandom = SecureRandom(),
) : EncryptionService {
    private val key: SecretKeySpec = SecretKeySpec(resolveKey(properties), "AES")

    override fun encrypt(plaintext: String?): String? {
        if (plaintext == null) {
            return null
        }

        val iv = ByteArray(IV_BYTES)
        secureRandom.nextBytes(iv)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(TAG_BITS, iv))
        val encrypted = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))

        return listOf(
            VERSION,
            Base64.getEncoder().encodeToString(iv),
            Base64.getEncoder().encodeToString(encrypted),
        ).joinToString(":")
    }

    override fun decrypt(ciphertext: String?): String? {
        if (ciphertext == null) {
            return null
        }

        val parts = ciphertext.split(":")
        require(parts.size == 3 && parts[0] == VERSION) { "Unsupported encrypted value format." }

        val iv = Base64.getDecoder().decode(parts[1])
        val encrypted = Base64.getDecoder().decode(parts[2])
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_BITS, iv))

        return cipher.doFinal(encrypted).toString(Charsets.UTF_8)
    }

    companion object {
        private const val VERSION = "v1"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val IV_BYTES = 12
        private const val TAG_BITS = 128

        private fun resolveKey(properties: MetadataBootstrapProperties): ByteArray {
            val configured =
                properties.encryption.masterKey
                    ?: System.getenv("CASSDIO_METADATA_MASTER_KEY")
                    ?: "cassdio-local-development-master-key"

            val decoded =
                runCatching { Base64.getDecoder().decode(configured) }
                    .getOrNull()
                    ?.takeIf { it.size == 32 }

            return decoded ?: MessageDigest.getInstance("SHA-256").digest(configured.toByteArray(Charsets.UTF_8))
        }
    }
}
