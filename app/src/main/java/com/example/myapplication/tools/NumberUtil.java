package com.example.myapplication.tools;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cheng on 2016/3/28.
 */
public class NumberUtil {

    // 用于匹配手机号码
    private final static String REGEX_MOBILEPHONE = "^0?1[34578]\\d{9}$";

    // 用于匹配固定电话号码
    private final static String REGEX_FIXEDPHONE = "^(010|02\\d|0[3-9]\\d{2})?\\d{6,8}$";

    // 用于获取固定电话中的区号
    private final static String REGEX_ZIPCODE = "^(010|02\\d|0[3-9]\\d{2})\\d{6,8}$";

    private static Pattern PATTERN_MOBILEPHONE;
    private static Pattern PATTERN_FIXEDPHONE;
    private static Pattern PATTERN_ZIPCODE;

    static {
        PATTERN_FIXEDPHONE = Pattern.compile(REGEX_FIXEDPHONE);
        PATTERN_MOBILEPHONE = Pattern.compile(REGEX_MOBILEPHONE);
        PATTERN_ZIPCODE = Pattern.compile(REGEX_ZIPCODE);
    }

    public static enum PhoneType {
        /**
         * 手机
         */
        CELLPHONE,

        /**
         * 固定电话
         */
        FIXEDPHONE,

        /**
         * 非法格式号码
         */
        INVALIDPHONE
    }

    public static class Numbers {
        private PhoneType type;
        /**
         * 如果是手机号码，则该字段存储的是手机号码 前七位；如果是固定电话，则该字段存储的是区号
         */
        private String code;
        private String number;

        public Numbers(PhoneType _type, String _code, String _number) {
            this.type = _type;
            this.code = _code;
            this.number = _number;
        }

        public PhoneType getType() {
            return type;
        }

        public String getCode() {
            return code;
        }

        public String getNumber() {
            return number;
        }

        public String toString() {
            return String.format("[number:%s, type:%s, code:%s]", number, type.name(), code);
        }
    }

    /**
     * 判断是否为手机号码
     *
     * @param number
     *            手机号码
     * @return
     */
    public static boolean isCellPhone(String number) {
        Matcher match = PATTERN_MOBILEPHONE.matcher(number);
        return match.matches();
    }

    /**
     * 判断是否为固定电话号码
     *
     * @param number
     *            固定电话号码
     * @return
     */
    public static boolean isFixedPhone(String number) {
        Matcher match = PATTERN_FIXEDPHONE.matcher(number);
        return match.matches();
    }

    /**
     * 获取固定号码号码中的区号
     *
     * @param strNumber
     * @return
     */
    public static String getZipFromHomephone(String strNumber) {
        Matcher matcher = PATTERN_ZIPCODE.matcher(strNumber);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }



    /**
     * 检查号码类型，并获取号码前缀，手机获取前7位，固话获取区号
     *
     * @param _number
     * @return
     */
    public static Numbers checkNumber(String _number) {
        String number = _number;
        Numbers rtNum = null;

        if (number != null && number.length() > 0) {
            if (isCellPhone(number)) {
                // 如果手机号码以0开始，则去掉0
                if (number.charAt(0) == '0') {
                    number = number.substring(1);
                }
                rtNum = new Numbers(PhoneType.CELLPHONE, number.substring(0, 7), _number);
            } else if (isFixedPhone(number)) {
                // 获取区号
                String zipCode = getZipFromHomephone(number);
                rtNum = new Numbers(PhoneType.FIXEDPHONE, zipCode, _number);
            } else {
                rtNum = new Numbers(PhoneType.INVALIDPHONE, null, _number);
            }
        }

        return rtNum;
    }


    //151 1111 1111 -> 151 **** 1111
    public static String dealPhoneNumber(String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {

            /*phoneNumber = phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7, 11);
            return phoneNumber;*/


            int len = phoneNumber.length();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < len; i++) {
                if (i > 2 && i < 7) {
                    builder.append("*");
                } else {
                    builder.append(phoneNumber.charAt(i));
                }

                if (i == 2 || i == 6) {
                    if (i != len - 1)
                        builder.append(" ");
                }
            }
            return builder.toString();
        }
        return null;
    }


}
