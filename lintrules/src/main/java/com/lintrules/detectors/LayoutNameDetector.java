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
import com.intellij.lang.jvm.JvmAnnotation;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UCallExpression;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UExpression;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class LayoutNameDetector extends Detector implements Detector.UastScanner {

    private static final Class<? extends Detector> DETECTOR_CLASS = LayoutNameDetector.class;
    private static final EnumSet<Scope> DETECTOR_SCOPE = Scope.JAVA_FILE_SCOPE;
    private static final Implementation IMPLEMENTATION = new Implementation(
            DETECTOR_CLASS,
            DETECTOR_SCOPE
    );

    private static final String ISSUE_ACTIVITY_ID = "LayoutNamePrefixError";
    private static final String ISSUE_ACTIVITY_DESCRIPTION = "FBI WARING!:You should name an activity-layout file with prefix {activity_}";
    private static final String ISSUE_ACTIVITY_EXPLANATION = "FBI WARING!:You should name an activity-layout file with prefix {activity_}. For example, `activity_function.xml`.";
    private static final String ISSUE_FRAGMENT_ID = "LayoutNamePrefixError";
    private static final String ISSUE_FRAGMENT_DESCRIPTION = "FBI WARING!:You should name an fragment-layout file with prefix {fragment_}";
    private static final String ISSUE_FRAGMENT_EXPLANATION = "FBI WARING!:You should name an fragment-layout file with prefix {fragment_}. For example, `fragment_function.xml`.";

    private static final Category ISSUE_CATEGORY = Category.SECURITY;
    private static final int ISSUE_PRIORITY = 9;
    private static final Severity ISSUE_SEVERITY = Severity.ERROR;

    public static final Issue ISSUE = Issue.create(
            ISSUE_ACTIVITY_ID,
            ISSUE_ACTIVITY_DESCRIPTION,
            ISSUE_ACTIVITY_EXPLANATION,
            ISSUE_CATEGORY,
            ISSUE_PRIORITY,
            ISSUE_SEVERITY,
            IMPLEMENTATION
    );

    public static final Issue FRAGMENT_LAYOUT_NAME_ISSUE = Issue.create(
            ISSUE_FRAGMENT_ID,
            ISSUE_FRAGMENT_DESCRIPTION,
            ISSUE_FRAGMENT_EXPLANATION,
            ISSUE_CATEGORY,
            ISSUE_PRIORITY,
            ISSUE_SEVERITY,
            IMPLEMENTATION
    );

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
            if(isSetContentViewOnThis_ForActivity(node)
                    && isThisInstanceOfActivity_ForActivity(context, method)
                    && isThisMethodHasLayoutAnnotation_ForActivity(context, method)) {

                JvmParameter[] parameters = method.getParameters();
                String layoutString = parameters[0].getName();
                if (!isFileStringStartWithPrefix(layoutString, "activity_")) {
                    context.report(ISSUE,
                            node,
                            context.getLocation(node),
                            ISSUE_ACTIVITY_DESCRIPTION);
                }
            }
        } else if ("inflate".equals(name)) {

//            if (isInflateCalledInOnCreateView_ForFragment(context, node)) {
//                String layoutString = getParamWithLayoutAnnotation_ForFragment(context, node);
//
//                if (layoutString == null) {
//                    return;
//                }
//
//                if (!isFileStringStartWithPrefix(layoutString, "fragment_")) {
//                    context.report(FRAGMENT_LAYOUT_NAME_ISSUE,
//                            node,
//                            context.getLocation(node),
//                            ISSUE_FRAGMENT_DESCRIPTION);
//                }
//            }

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

    private boolean isThisInstanceOfActivity_ForActivity(@NonNull JavaContext context, @NonNull PsiMethod method) {
        if("AppCompatActivity".equals(method.getContainingClass().getName())){
            return true;
        } else {
            return false;
        }
    }

    private boolean isThisMethodHasLayoutAnnotation_ForActivity(@NonNull JavaContext context, @NonNull PsiMethod method) {
        if (method.getParameters().length != 1) {
            return false;
        }
        JvmAnnotation[] annotations = method.getParameters()[0].getAnnotations();
        for (JvmAnnotation annotation : annotations) {
            annotation.getQualifiedName();
            if ("android.support.annotation.LayoutRes".equals(annotation.getQualifiedName())) {
                return true;
            }
        }
        return false;
    }

    private boolean isInflateCalledInOnCreateView_ForFragment(@NonNull JavaContext context, @NonNull PsiMethod method) {
        method.getParent().getText();
//        try {
//            String resolvedNodeName = resolvedNode.getName();
//            if ("onCreateView".equals(resolvedNodeName)) {
//                return true;
//            }
//        } catch (Exception e) {
//            return false;
//        }
        return false;
    }


    private boolean isFileStringStartWithPrefix(String layoutFileResourceString, String prefix) {
        int lastDotIndex = layoutFileResourceString.lastIndexOf(".");
        String fileName = layoutFileResourceString.substring(lastDotIndex + 1);
        if (fileName.startsWith(prefix)) {
            return true;
        } else {
            return false;
        }
    }




}
