package com.example.myapplication.bean;

public class Oss_Bean {

    private Integer errCode;
    private String errMsg;
    private Oss_Bean_Data data;

    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public Oss_Bean_Data getData() {
        return data;
    }

    public void setData(Oss_Bean_Data data) {
        this.data = data;
    }

    public static class Oss_Bean_Data {
        public String AssumedRoleId;
        public String Bucket;
        public String OssRegion;
        public String AccessKeyId;
        public String AccessKeySecret;
        public String Expiration;
        public String SecurityToken;


        public String getAssumedRoleId() {
            return AssumedRoleId;
        }

        public void setAssumedRoleId(String assumedRoleId) {
            AssumedRoleId = assumedRoleId;
        }

        public String getBucket() {
            return Bucket;
        }

        public void setBucket(String bucket) {
            Bucket = bucket;
        }

        public String getOssRegion() {
            return OssRegion;
        }

        public void setOssRegion(String ossRegion) {
            OssRegion = ossRegion;
        }

        public String getAccessKeyId() {
            return AccessKeyId;
        }

        public void setAccessKeyId(String accessKeyId) {
            AccessKeyId = accessKeyId;
        }

        public String getAccessKeySecret() {
            return AccessKeySecret;
        }

        public void setAccessKeySecret(String accessKeySecret) {
            AccessKeySecret = accessKeySecret;
        }

        public String getExpiration() {
            return Expiration;
        }

        public void setExpiration(String expiration) {
            Expiration = expiration;
        }

        public String getSecurityToken() {
            return SecurityToken;
        }

        public void setSecurityToken(String securityToken) {
            SecurityToken = securityToken;
        }
    }

}
