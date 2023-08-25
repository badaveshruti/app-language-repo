package qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces

import qnopy.com.qnopyandroid.clientmodel.EventData

interface OnRecentEventListener {
    fun onShowMoreClicked(event: EventData)
    fun onEventClicked(event: EventData)
}