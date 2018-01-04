package com.steven.hy.bridge;

/**
 * Created by steven on 18/1/2.
 */

public class HyEnv {
    private static HyEnv hyEnv;

    public boolean isDebug;

    private HyEnv(){
    }

    public static HyEnv getIns(){
        if(hyEnv==null){
            synchronized (HyEnv.class){
                if(hyEnv==null){
                    hyEnv=new HyEnv();
                }
            }
        }
        return hyEnv;
    }


}
