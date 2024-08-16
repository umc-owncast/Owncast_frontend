package kr.dori.android.own_cast.player

import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.TransferListener
import java.io.IOException
import java.io.InputStream

class InputStreamDataSource(private val inputStream: InputStream) : DataSource {

    private var opened: Boolean = false

    @OptIn(UnstableApi::class)
    override fun open(dataSpec: DataSpec): Long {
        opened = true
        val availableBytes = inputStream.available().toLong()
        Log.d("InputStreamDataSource", "Stream opened with $availableBytes bytes available")
        return availableBytes.takeIf { it > 0 } ?: C.LENGTH_UNSET.toLong()
    }

    @UnstableApi
    override fun read(buffer: ByteArray, offset: Int, readLength: Int): Int {
        return if (opened) {
            val bytesRead = inputStream.read(buffer, offset, readLength)
            Log.d("InputStreamDataSource", "Read $bytesRead bytes")
            bytesRead
        } else {
            throw IOException("DataSource not opened.")
        }
    }

    @OptIn(UnstableApi::class)
    override fun addTransferListener(transferListener: TransferListener) {
        TODO("Not yet implemented")
    }

    @UnstableApi
    override fun close() {
        if (opened) {
            opened = false
            inputStream.close()
        }
    }

    @UnstableApi
    override fun getUri(): Uri? {
        return null
    }

    @UnstableApi
    override fun getResponseHeaders(): Map<String, List<String>> {
        return emptyMap()
    }
}
