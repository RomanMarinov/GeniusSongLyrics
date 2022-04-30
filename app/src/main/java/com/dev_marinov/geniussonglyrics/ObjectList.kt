package com.dev_marinov.geniussonglyrics

class ObjectList {

    var nameArtist: String? = null
    var urlPictureSong: String? = null
    var title: String? = null
    var urlPageSong: String? = null
    var urlPageArtist: String? = null

    constructor(
        nameArtist: String?,
        urlPictureSong: String?,
        title: String?,
        urlPageSong: String?,
        urlPageArtist: String?
    ) {
        this.nameArtist = nameArtist
        this.urlPictureSong = urlPictureSong
        this.title = title
        this.urlPageSong = urlPageSong
        this.urlPageArtist = urlPageArtist
    }
}