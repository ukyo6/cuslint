package com.lintrules.detectors;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.SourceCodeScanner;
import com.intellij.lang.jvm.JvmAnnotation;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.psi.PsiMethod;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UCallExpression;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UExpression;
import org.jetbrains.uast.java.JavaUMethod;

import java.util.Arrays;
import java.util.List;

/**
 * @author hewei
 * @desc layoutId命名检查
 */
@SuppressWarnings("UnstableApiUsage")
public class CsLayoutNameDetector extends Detector implements SourceCodeScanner {

    private static final Class<? extends Detector> DETECTOR_CLASS = CsLayoutNameDetector.class;

    private static final String ISSUE_ACTIVITY_ID = "ActivityLayoutNamePrefixError";
    private static final String ISSUE_FRAGMENT_ID = "FragmentLayoutNamePrefixError";

    public static final Issue ACTIVITY_LAYOUT_NAME_ISSUE = Issue.create(
            ISSUE_ACTIVITY_ID,
            "Activity layout请使用前缀{activity_}",
            "Activity layout请使用前缀{activity_}. 例如 `activity_function.xml`",
            Category.CORRECTNESS, 9, Severity.ERROR,
            new Implementation(DETECTOR_CLASS, Scope.JAVA_FILE_SCOPE)
    );

    public static final Issue FRAGMENT_LAYOUT_NAME_ISSUE = Issue.create(
            ISSUE_FRAGMENT_ID,
            "You should name an fragment-layout file with prefix {fragment_}",
            "Fragment layout请使用前缀 {fragment_}. 例如 `fragment_function.xml`;",
            Category.CORRECTNESS, 9, Severity.ERROR,
            new Implementation(DETECTOR_CLASS, Scope.JAVA_FILE_SCOPE)
    );

    @Nullable
    @Override
    public List<String> getApplicableMethodNames() {
        return Arrays.asList("setContentView", "inflate");
    }


    @Override
    public void visitMethod(@NotNull JavaContext context, @NotNull UCallExpression node, PsiMethod method) {
        String name = method.getName();

        if ("setContentView".equals(name)) {
            if (isSetContentViewOnThis_ForActivity(node)
                    && isThisInstanceOfActivity_ForActivity(method)
                    && isThisMethodHasLayoutAnnotation_ForActivity(method)) {


                List<UExpression> valueArguments = node.getValueArguments();
                String layoutString = valueArguments.get(0).toString();
                if (isFileStringStartWithPrefix(layoutString, "activity_")) {
                    context.report(ACTIVITY_LAYOUT_NAME_ISSUE,
                            node,
                            context.getLocation(node),
                            "Activity layout请使用前缀{activity_}");
                }
            }
        } else if ("inflate".equals(name)) {

            if (isInflateCalledInOnCreateView_ForFragment(node)) {

                List<UExpression> valueArguments = node.getValueArguments();
                String layoutString = valueArguments.get(0).toString();
                if (isFileStringStartWithPrefix(layoutString, "fragment_")) {
                    context.report(FRAGMENT_LAYOUT_NAME_ISSUE,
                            node,
                            context.getLocation(node),
                            "Fragment layout请使用前缀 {fragment_}");
                }
            }

        }
    }

    private boolean isSetContentViewOnThis_ForActivity(@NotNull UCallExpression node) {
        String argOwner = node.getMethodIdentifier().getSourcePsi().getParent().getText();
        return argOwner.startsWith("setContentView")
                || argOwner.startsWith("this.setContentView");
    }

    private boolean isThisInstanceOfActivity_ForActivity(@NotNull PsiMethod method) {
        return "AppCompatActivity".equals(method.getContainingClass().getName());
    }

    private boolean isThisMethodHasLayoutAnnotation_ForActivity(@NotNull PsiMethod method) {
        JvmParameter[] parameters = method.getParameters();
        if (parameters.length < 1) {
            return false;
        }

        JvmAnnotation[] annotations = parameters[0].getAnnotations();
        for (JvmAnnotation annotation : annotations) {
            if ("androidx.annotation.LayoutRes".equals(annotation.getQualifiedName())) {
                return true;
            }
        }
        return false;
    }

    private boolean isInflateCalledInOnCreateView_ForFragment(@NotNull UCallExpression node) {
        UElement surroundingMethod = getSurroundingMethod(node);
        try {
            String resolvedNodeName = ((JavaUMethod) surroundingMethod).getName();
            if ("onCreateView".equals(resolvedNodeName)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private UElement getSurroundingMethod(UElement node) {
        while (node != null) {
            Class<? extends UElement> type = node.getClass();
            if (type == JavaUMethod.class) {
                return node;
            }
            node = node.getUastParent();
        }
        return null;
    }


    private boolean isFileStringStartWithPrefix(String layoutFileResourceString, String prefix) {
        int lastDotIndex = layoutFileResourceString.lastIndexOf(".");
        String fileName = layoutFileResourceString.substring(lastDotIndex + 1);
        return !fileName.startsWith(prefix);
    }
}
