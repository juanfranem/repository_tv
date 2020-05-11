package es.jfechevarria.spaintv

import es.jfechevarria.domain.Channel

interface Actions {
    fun onClick(channel: Channel)
}