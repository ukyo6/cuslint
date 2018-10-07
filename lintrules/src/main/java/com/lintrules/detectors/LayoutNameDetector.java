package com.lintrules.detectors;

import com.android.annotations.NonNull;
import com.android.tools.lint.client.api.JavaParser;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.intellij.psi.PsiMethod;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UCallExpression;
import org.jetbrains.uast.UExpression;

import java.util.Arrays;
import java.util.List;

public class LayoutNameDetector extends Detector implements Detector.UastScanner {

    public static final Issue ISSUE = Issue.create(
            "LayoutNamessss",
            "ViewIdName命名",
            "请勿直接调用android.util.Log，应该使用统一Log工具类",
            Category.SECURITY, 5, Severity.ERROR,
            new Implementation(LayoutNameDetector.class, Scope.JAVA_FILE_SCOPE));

    @Nullable
    @Override
    public List<String> getApplicableMethodNames() {
        String message = String.format(
                "ID definitions **must** be of the form `@+id/name`; try using `%1$s`",
                "ss");

        return Arrays.asList("setContentView", "inflate");
    }


    @Override
    public void visitMethod(JavaContext context, UCallExpression node, PsiMethod method) {
        String name = method.getName();

        if ("setContentView".equals(name)) {


        } else if ("inflate".equals(name)) {



        }
    }

    private boolean isSetContentViewOnThis_ForActivity(@NonNull UCallExpression node) {
        String argOwner = node.getMethodIdentifier().getSourcePsi().getParent().getText();
        if (argOwner.startsWith("setContentView")
                || argOwner.startsWith("this.setContentView")) {
            return true;
        } else {
            return false;
        }
    }

//    private boolean isThisInstanceOfActivity_ForActivity(@NonNull JavaContext context, @NonNull UCallExpression node) {
//        node.getPsi().getClass()
//
//        JavaParser.ResolvedNode resolved = context.resolve(JavaContext.findSurroundingClass(node));
//        JavaParser.ResolvedClass sorroundingClass = (JavaParser.ResolvedClass) resolved;
//        while (sorroundingClass != null) {
//            //System.out.println("sorroundingClass = " + sorroundingClass);
//            if ("android.app.Activity".equals(sorroundingClass.getName())) {
//                return true;
//            } else {
//                sorroundingClass = sorroundingClass.getSuperClass();
//            }
//        }
//        return false;
//    }

}
