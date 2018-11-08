package com.mokee.imagnet.presenter

import bt.Bt
import bt.StandaloneClientBuilder
import bt.data.file.FileSystemStorage
import bt.dht.DHTConfig
import bt.dht.DHTModule
import timber.log.Timber
import java.io.File
import java.lang.Exception

class MagnetPresenter private constructor() {
    private var mStorage: FileSystemStorage
    private var mDHTMoudle: DHTModule
    private var mConfig: DHTConfig = object : DHTConfig() {
        override fun shouldUseRouterBootstrap(): Boolean {
            return true
        }
    }
    private var mClientBuilder: StandaloneClientBuilder

    init {
        mDHTMoudle = DHTModule(mConfig)
        mStorage = FileSystemStorage(File(FILE_PATH))

        mClientBuilder = Bt.client()
        mClientBuilder.storage(mStorage).autoLoadModules().module(mDHTMoudle)
    }

    @Synchronized
    public fun magnet(magnetUri: String) {
        try {
            val btClient = mClientBuilder.magnet(magnetUri).build()
            btClient.startAsync({
                if(it.piecesRemaining == 0) {
                    btClient.stop()
                }
                Timber.d("it.connectedPeers: ${it.connectedPeers}")
                Timber.d("it.piecesTotal: ${it.piecesTotal}")
                Timber.d("it.uploaded: ${it.uploaded}")
                Timber.d("it.downloaded: ${it.downloaded}")
                Timber.d("it.piecesComplete: ${it.piecesComplete}")
                Timber.d("it.piecesIncomplete: ${it.piecesIncomplete}")
                Timber.d("it.piecesNotSkipped: ${it.piecesNotSkipped}")
            }, 1000)
        } catch (e: Exception) {
            Timber.e("Process magnet happen exception: $e")
        }
    }

    companion object {
        const val FILE_PATH = "/sdcard/iMagent/"
        val instance: MagnetPresenter by lazy { Holder.INSTANCE }
    }

    private object Holder {val INSTANCE = MagnetPresenter()}

}