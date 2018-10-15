package com.james.customlint;

import java.io.Serializable;
import java.util.List;

public class SchoolBean implements Serializable {

    private String schoolName;
    private List<StudentBean> students;

    public static class StudentBean {
        private String name;
        private String age;
    }
}
