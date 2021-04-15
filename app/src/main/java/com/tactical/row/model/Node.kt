package com.tactical.row.model

class Node(
    var col: Int = 0,
    var root: Boolean = false,
    var estimation: Int = 0,
    var depth: Int = 0,
    val parent: Node? = null,
    var nodes: ArrayList<Node> = arrayListOf()
)