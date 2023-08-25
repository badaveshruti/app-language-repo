package qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces

import qnopy.com.qnopyandroid.clientmodel.Location

interface OnLocationListener {
    fun onLocationPinClicked(location: Location, pos: Int)
    fun onLocationNameClicked(location: Location, pos: Int)
    fun onLocationDeleteClicked(location: Location, pos: Int, isFormDefault: Boolean)
    fun onLocationAssignClicked(location: Location, pos: Int)
}