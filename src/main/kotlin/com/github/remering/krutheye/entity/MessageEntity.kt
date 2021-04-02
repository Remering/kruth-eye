package com.github.remering.krutheye.entity

import com.github.remering.krutheye.message.EntityMessage

interface MessageEntity<E : MessageEntity<E, M>, M: EntityMessage<E, M>> {
    fun toMessage(): M
}
