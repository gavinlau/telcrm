package org.eredlab.g4.telcrm.service.util;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
public class MiscUtils {
    /* MD5加密算法。 */
    public static String consertMd5(String md5) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                'e', 'f' };
        try {
            byte[] strTemp = md5.getBytes();
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char number[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                number[k++] = hexDigits[byte0 >>> 4 & 0xf];
                number[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(number);
        } catch (Exception e) {
            return null;
        }
    }
    
    public static List<String> shuffleArrayToList(String[] array)
    {
    	 List<String> list=Arrays.asList(array);
         Collections.shuffle(list);
         return list;
    }
    
//    boolean flag=(custstatus==5 && days>=30) ||
//    (custstatus==4 && days>=60) ||
//    (custstatus==3 && days>=90) || 
//    (custstatus==1 && days>=180); 
      
      
      public static final int CUSTOMER_STATUS_CLUE=5;
      public static final int CUSTOMER_STATUS_CLUE_RECICLE_DAY=30;
      
      public static final int CUSTOMER_STATUS_INTENTION=4;
      public static final int CUSTOMER_STATUS_INTENTION_RECICLE_DAY=60;
      
      public static final int CUSTOMER_STATUS_TOSALE=3;
      public static final int CUSTOMER_STATUS_TOSALE_RECICLE_DAY=90;
      
      public static final int CUSTOMER_STATUS_CONTRACTED=2;
      public static final int CUSTOMER_STATUS_CONTRACTED_RECICLE_DAY=0;
      
      public static final int CUSTOMER_STATUS_CONTRACTOVER=1;
      public static final int CUSTOMER_STATUS_CONTRACTOVER_RECICLE_DAY=180;
      
    
}
