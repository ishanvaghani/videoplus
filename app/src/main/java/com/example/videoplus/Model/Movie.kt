package com.example.videoplus.Model

class Movie() {

    var name: String = ""
    var thumbnail: String = ""
    var description: String = ""
    var video: String = ""
    val carousel: Boolean = false
    val carouselImage: String = ""
    val type: String = ""
    var id: String = ""

    constructor(movieImage: String, id: String) : this() {
        this.thumbnail = movieImage
        this.id = id
    }

    constructor(name:String, movieImage: String, id: String) : this() {
        this.name = name
        this.thumbnail = movieImage
        this.id = id
    }

    constructor(movieName: String, movieImage: String, movieDescription: String, movieVideo: String) : this() {
        this.name = movieName
        this.thumbnail = movieImage
        this.description = movieDescription
        this.video = movieVideo
    }

}