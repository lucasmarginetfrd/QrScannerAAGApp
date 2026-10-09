package com.epicjugador.qrscanneraagapp.data.repository

import com.epicjugador.qrscanneraagapp.data.model.DeviceRegister
import com.epicjugador.qrscanneraagapp.data.model.ScanEvent
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun registerDevice(deviceRegister: DeviceRegister) {

        firestore.collection("devices")
            .document(deviceRegister.uuid)
            .set(deviceRegister)
            .await()
    }

    suspend fun saveScanEvent(scanEvent: ScanEvent) {

        firestore.collection("scanEvents")
            .add(scanEvent)
            .await()
    }

    suspend fun updateLastOpen(uuid: String, lastOpen: String) {

        firestore.collection("devices")
            .document(uuid)
            .update("lastOpenTime", lastOpen)
            .await()
    }

}