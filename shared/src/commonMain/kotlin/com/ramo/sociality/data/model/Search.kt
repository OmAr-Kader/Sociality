package com.ramo.sociality.data.model

open class Search(
    @io.realm.kotlin.types.annotations.PrimaryKey
    var _id: org.mongodb.kbson.ObjectId = org.mongodb.kbson.ObjectId.invoke(),
    @io.realm.kotlin.types.annotations.Index
    var searchText: String,
    @io.realm.kotlin.types.annotations.Index
    var date: Long,
) : io.realm.kotlin.types.RealmObject {
    constructor() : this(org.mongodb.kbson.ObjectId.invoke(), "", 0)
    constructor(searchText: String, date: Long) : this(org.mongodb.kbson.ObjectId.invoke(), searchText, date)
    constructor(search: SearchData) : this(if (search.id.isEmpty()) org.mongodb.kbson.ObjectId.invoke() else org.mongodb.kbson.ObjectId.invoke(search.id), search.searchText, search.date)
}

data class SearchData(
    val id: String,
    val searchText: String,
    val date: Long,
) {
    constructor(searchText: String, date: Long) : this("", searchText, date)
    constructor(search: Search) : this(search._id.toHexString(), search.searchText, search.date)
}