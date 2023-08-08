package dev.mr3n.contestkt

import java.util.concurrent.ConcurrentHashMap

object Storage {
    val contests = ConcurrentHashMap<String, ContestInfo>()
}