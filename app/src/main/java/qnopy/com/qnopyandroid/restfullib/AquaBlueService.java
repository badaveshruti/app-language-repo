package qnopy.com.qnopyandroid.restfullib;


import org.json.JSONObject;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.util.List;

import qnopy.com.qnopyandroid.requestmodel.DEvent;
import qnopy.com.qnopyandroid.requestmodel.RUnitConverter;
import qnopy.com.qnopyandroid.requestmodel.SLocation;
import qnopy.com.qnopyandroid.requestmodel.SSite;
import qnopy.com.qnopyandroid.responsemodel.EventResponseModel;
import qnopy.com.qnopyandroid.responsemodel.LoginResponseModelV4;


public interface AquaBlueService {

    public LoginResponseModelV4 checkLogin(String resBaseUrl, String resource, String strName, String strPwd);

    public String changePassword(String resBaseUrl, String resource, String strName,
                                 String oldPwd, String newPwd);

    public SSite[] getMetaSite(String strBaseUrl, String strResource, String strName,
                               String strPwd, String strTime);

    public SLocation[] getMetaLocation(String strBaseUrl, String strResource,
                                       String strName, String strPwd, String strTime);

    public RUnitConverter[] getMetaDataSync(String strBaseUrl, String strResource,
                                            String strName, String strPwd, String strTime);

    public Boolean setFieldEventData(String strBaseUrl, String strResource, List<DEvent> eEvent, String strName, String strPwd);

    public Boolean SetFieldEventFile(String strBaseUrl, String strResource, MultiValueMap<String, Object> eEventData);

    public EventResponseModel generateEventIDFromServer(String strBaseUrl, String strResource,
                                                        DEvent event, String strName, JSONObject jsonRequest);

    public Boolean closeEventID(String strBaseUrl, String strResource, Integer eventID, String strName, String strPwd);

    public File readFile();

}
