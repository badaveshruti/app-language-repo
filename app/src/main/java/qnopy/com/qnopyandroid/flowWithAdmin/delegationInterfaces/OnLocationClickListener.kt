package qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces

import qnopy.com.qnopyandroid.clientmodel.Location

interface OnLocationClickListener{
    fun onLocationPinClicked(location: Location, pos:Int)
    fun onLocationNameClicked(location: Location, pos:Int)
    fun onNextClcikListner(location: Location,  pos:Int)
}