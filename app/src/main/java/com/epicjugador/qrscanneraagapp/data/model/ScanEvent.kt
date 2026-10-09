package com.epicjugador.qrscanneraagapp.data.model

data class ScanEvent(
    val qrUrl: String = "",
    val timestamp: String = "",
    val deviceUuid: String = ""
)