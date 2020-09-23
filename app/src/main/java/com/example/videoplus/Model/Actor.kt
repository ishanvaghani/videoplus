package com.example.videoplus.Model

class Actor {

    var actorId: String = ""
    var actorName: String = ""
    var actorImage: String = ""

    constructor(actorId: String, actorName: String, actorImage: String) {
        this.actorId = actorId
        this.actorName = actorName
        this.actorImage = actorImage
    }
}