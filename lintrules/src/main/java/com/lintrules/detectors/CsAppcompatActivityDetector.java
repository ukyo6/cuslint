package com.lintrules.detectors;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.SourceCodeScanner;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiReferenceList;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UClass;

import java.util.Collections;
import java.util.List;

import static com.android.SdkConstants.CLASS_ACTIVITY;

/**
 * @author hewei
 * @desc Activity请继承AppcompatActivity
 *
 */
@SuppressWarnings("UnstableApiUsage")
public class CsAppcompatActivityDetector extends Detector implements SourceCodeScanner {

    public static final Issue ISSUE = Issue.create(
            "AppcompatActivityUsage",
            "请继承AppcompatActivity",
            "使用了compat主题, 请继承AppcompatActivity",
            Category.SECURITY, 5, Severity.ERROR,
            new Implementation(CsAppcompatActivityDetector.class, Scope.JAVA_FILE_SCOPE));

    @Nullable
    @Override
    public List<String> applicableSuperClasses() {

        return Collections.singletonList(CLASS_ACTIVITY);
    }

    @Override
    public void visitClass(@NotNull JavaContext context, @NotNull UClass declaration) {
        super.visitClass(context, declaration);

        PsiReferenceList extendsList = declaration.getExtendsList();
        if(extendsList == null){
            return;
        }
        PsiJavaCodeReferenceElement[] referenceElements = extendsList.getReferenceElements();
        for (PsiJavaCodeReferenceElement referenceElement : referenceElements) {
            if(CLASS_ACTIVITY.equals(referenceElement.getQualifiedName())) {
                String name = declaration.getName();
                context.report(ISSUE,
                        referenceElement,
                        context.getNameLocation(referenceElement),
                        String.format("类 `%1$s` 请继承AppcompatActivity", name));
            }
        }
    }
}
