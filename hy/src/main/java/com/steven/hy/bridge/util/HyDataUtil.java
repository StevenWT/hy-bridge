package com.steven.hy.bridge.util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by steven on 17/12/20.
 */

public class HyDataUtil {
    public static final String OUTCOME="Outcome";
    public static final String MESSAGE="Message";
    public static final String DATA="Data";
    public static final String OUTCOME_SUCCESS="Success";
    public static final String OUTCOME_FAIL="Fail";

    /**
     * callback 的成功数据结构
     * @return
     */
    public static JSONObject getHySuccessData(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(OUTCOME,OUTCOME_SUCCESS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * callback 的失败数据结构
     * @return
     */
    public static JSONObject getHyFailData(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(OUTCOME,OUTCOME_FAIL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
