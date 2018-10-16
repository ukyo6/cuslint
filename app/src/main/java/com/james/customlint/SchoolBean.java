package com.james.customlint;

import java.util.List;

/**
 * @author hewei
 * @desc 检查内部类是否实现Serializable
 */
public class SchoolBean implements java.io.Serializable, Serializable {

    private String schoolName;
    private List<StudentBean> students;

    public static class StudentBean {
        private String name;
        private String age;

        class C implements java.io.Serializable {

        }
    }

    public static class A implements java.io.Serializable {

    }

    public static class B {

    }
}
