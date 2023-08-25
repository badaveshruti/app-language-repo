package qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces

import qnopy.com.qnopyandroid.clientmodel.EventData

interface OnFieldEventListener {
    fun onEventClicked(event: EventData, position: Int)
    fun onEventOptionsClicked(event: EventData, position: Int)
    fun onCloseEventClicked(status: Int, event: EventData, position: Int)
}