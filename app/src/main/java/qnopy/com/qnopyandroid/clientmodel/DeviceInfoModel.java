package qnopy.com.qnopyandroid.clientmodel;

/**
 * Created by Yogendra on 05-Mar-16.
 */
public class DeviceInfoModel {

    String deviceType;
    String device_name;
    String deviceId;
    String userId;
    String user_guid;
    String service_provider;
    String model_number;
    String screen_resolution;
    String phone_number;
    String battery_percentage;
    String imei_no;
    String os_version;
    String mac_address;//
    String ip_address;//network ip
    String user_location;//Lat-Long , seperated

    public String getImei_no() {
        return imei_no;
    }

    public void setImei_no(String imei_no) {
        this.imei_no = imei_no;
    }

    public String getOs_version() {
        return os_version;
    }

    public void setOs_version(String os_version) {
        this.os_version = os_version;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUser_guid() {
        return user_guid;
    }

    public void setUser_guid(String user_guid) {
        this.user_guid = user_guid;
    }

    public String getService_provider() {
        return service_provider;
    }

    public void setService_provider(String service_provider) {
        this.service_provider = service_provider;
    }

    public String getModel_number() {
        return model_number;
    }

    public void setModel_number(String model_number) {
        this.model_number = model_number;
    }

    public String getScreen_resolution() {
        return screen_resolution;
    }

    public void setScreen_resolution(String screen_resolution) {
        this.screen_resolution = screen_resolution;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getBattery_percentage() {
        return battery_percentage;
    }

    public void setBattery_percentage(String battery_percentage) {
        this.battery_percentage = battery_percentage;
    }


    public String getMac_address() {
        return mac_address;
    }

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getUser_location() {
        return user_location;
    }

    public void setUser_location(String user_location) {
        this.user_location = user_location;
    }
}
