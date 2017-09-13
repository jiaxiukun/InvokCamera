package baway.com.invokcamera.utils;

import android.os.Environment;

/**
 * Created by 贾秀坤 on 2017/9/12.
 */

public class Tools {
    public static boolean hasSdcard(){
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }
    }
}
