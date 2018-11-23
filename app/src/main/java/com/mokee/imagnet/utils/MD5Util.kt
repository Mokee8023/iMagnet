package com.mokee.imagnet.utils

import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import kotlin.experimental.and

object MD5Util {

    fun checkFileMD5(file: File, md5: String): Boolean {
        val digest = MessageDigest.getInstance("MD5")
        var fis: FileInputStream? = null
        try {
            fis = file.inputStream()
            val buffer = ByteArray(1024)
            var len = 0
            while (len != -1) {
                len = fis.read(buffer)
                if(len != -1) {
                    digest.update(buffer, 0, len)
                }
            }

            val generateMD5 = convertHashToString(digest.digest())
            Timber.i("The generate md5 by file is $generateMD5, the original md5 is $md5")

            generateMD5?.let {
                return generateMD5 == (md5.toUpperCase())
            }?: return false
        } catch (e: Exception) {
            Timber.e("Check MD5 exception: $e")
            return false
        } finally {
            fis?.close()
        }
    }

    /**
     * Check two md5 value is equal
     */
    fun checkMD5Equal(ori: String, dst: String): Boolean {
        return (ori.length == 32
                && dst.length == 32
                && (ori.toUpperCase() == dst.toUpperCase()))
    }

    /**
     * Create md5 value with character string
     * @return if generate fail, return null
     */
    fun generateMD5(character: String): String? {
        val digest = MessageDigest.getInstance("MD5")
        return try {
            digest.update(character.toByteArray())
            val generateMD5 = convertHashToString(digest.digest())
            Timber.i("The generate md5 by $character is $generateMD5")

            generateMD5?.toUpperCase()
        } catch (e: Exception) {
            Timber.e("Check MD5 exception: $e")
            null
        }
    }

    private fun convertHashToString(hashBytes: ByteArray): String? {
        val sb = StringBuilder()
        if (hashBytes.isEmpty()) {
            return null
        }

        hashBytes.forEach {it ->
            val hex = Integer.toString((it and 0xFF.toByte()) + 0x100, 16)
            if(hex.length > 2) {
                sb.append(Integer.toString((it and 0xFF.toByte()) + 0x100, 16).substring(1))
            } else {
                sb.append(Integer.toString((it and 0xFF.toByte()) + 0x100, 16))
            }
        }
        return sb.toString().toUpperCase()
    }
}