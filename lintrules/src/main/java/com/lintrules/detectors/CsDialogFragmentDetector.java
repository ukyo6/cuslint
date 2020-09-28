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

/**
 * @author hewei
 * @desc 不要直接继承DialogFragment, 清使用封装过的
 *
 * Todo: 处理内部类
 */
@SuppressWarnings("UnstableApiUsage")
public class CsDialogFragmentDetector extends Detector implements SourceCodeScanner {


    private static final String CLASS_DIALOG_FRAGMENT = "androidx.fragment.app.DialogFragment";

    public static final Issue ISSUE = Issue.create(
            "BaseDialogFragmentUsage",
            "请继承BaseDialogFragment",
            "为了统一处理一些DialogFragment的问题, 请继承BaseDialogFragment",
            Category.SECURITY, 5, Severity.ERROR,
            new Implementation(CsDialogFragmentDetector.class, Scope.JAVA_FILE_SCOPE));

    @Nullable
    @Override
    public List<String> applicableSuperClasses() {
        return Collections.singletonList(CLASS_DIALOG_FRAGMENT);
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
            if(CLASS_DIALOG_FRAGMENT.equals(referenceElement.getQualifiedName())) {
                String name = declaration.getName();
                context.report(ISSUE,
                        referenceElement,
                        context.getNameLocation(referenceElement),
                        String.format("类 `%1$s` 请继承BaseDialogFragment", name));
            }
        }
    }
}
