package com.example.myapplication.bean;


import java.util.List;

/**
 *  查询用户得到的详细个人信息
 * */
public class User_Msg_Bean {

    private Integer errCode;
    private String errMsg;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private Integer id;
        private Integer unread_notify;
        private String name;
        private String phone;
        private String avatar;
        private Integer status;
        private Integer gender;
        private Integer age;
        private String created_at;
        private String updated_at;
        private List<Members_Bean> members;
        private Extends_Bean user_extends;

        public Extends_Bean getUser_extends() {
            return user_extends;
        }

        public void setUser_extends(Extends_Bean user_extends) {
            this.user_extends = user_extends;
        }

        public Integer getUnread_notify() {
            return unread_notify;
        }

        public void setUnread_notify(Integer unread_notify) {
            this.unread_notify = unread_notify;
        }

        public List<Members_Bean> getMembers() {
            return members;
        }

        public void setMembers(List<Members_Bean> members) {
            this.members = members;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public Integer getGender() {
            return gender;
        }

        public void setGender(Integer gender) {
            this.gender = gender;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }



        public class Members_Bean{
            private Integer member_id;
            private Integer experience;
            private String end_time;
            private String member_name;
            private String member_icon;
            private String level_id;
            private String level;
            private String level_name;
            private String lv_icon;

            public Integer getMember_id() {
                return member_id;
            }

            public void setMember_id(Integer member_id) {
                this.member_id = member_id;
            }

            public Integer getExperience() {
                return experience;
            }

            public void setExperience(Integer experience) {
                this.experience = experience;
            }

            public String getEnd_time() {
                return end_time;
            }

            public void setEnd_time(String end_time) {
                this.end_time = end_time;
            }

            public String getMember_name() {
                return member_name;
            }

            public void setMember_name(String member_name) {
                this.member_name = member_name;
            }

            public String getMember_icon() {
                return member_icon;
            }

            public void setMember_icon(String member_icon) {
                this.member_icon = member_icon;
            }

            public String getLevel_id() {
                return level_id;
            }

            public void setLevel_id(String level_id) {
                this.level_id = level_id;
            }

            public String getLevel() {
                return level;
            }

            public void setLevel(String level) {
                this.level = level;
            }

            public String getLevel_name() {
                return level_name;
            }

            public void setLevel_name(String level_name) {
                this.level_name = level_name;
            }

            public String getLv_icon() {
                return lv_icon;
            }

            public void setLv_icon(String lv_icon) {
                this.lv_icon = lv_icon;
            }
        }


        public class Extends_Bean {
            public String bedtime;
            public String awaken_time;
            public String sleep_monitoring;
            public String painless_arousal;
            public String timed_close;
            public String delay;
        }

    }
}
