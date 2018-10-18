package com.lintrules.detectors;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.XmlContext;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiReferenceList;
import com.intellij.psi.PsiType;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UAnonymousClass;
import org.jetbrains.uast.UClass;
import org.jetbrains.uast.UTypeReferenceExpression;
import org.w3c.dom.Element;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author hewei
 * @desc 序列化内部类检查
 */
public class SerializableDetector extends Detector implements Detector.UastScanner {

    private static final String CLASS_SERIALIZABLE = "java.io.Serializable";

    public static final Issue ISSUE = Issue.create(
            "InnerClassSerializable",
            "内部类需要实现Serializable接口",
            "内部类需要实现Serializable接口",
            Category.SECURITY, 5, Severity.ERROR,
            new Implementation(SerializableDetector.class, Scope.JAVA_FILE_SCOPE));

    @Nullable
    @Override
    public List<String> applicableSuperClasses() {
        //父类是"java.io.Serializable"
        return Collections.singletonList(CLASS_SERIALIZABLE);
    }

    /**
     * 扫描到applicableSuperClasses()指定的list时,回调该方法
     */
    @Override
    public void visitClass(JavaContext context, UClass declaration) {
        //只从最外部开始向内部类递归检查
        if (declaration instanceof UAnonymousClass) {
            return;
        }
        sortClass(context, declaration);
    }

    private void sortClass(JavaContext context, UClass declaration) {
        for (UClass uClass : declaration.getInnerClasses()) {
            sortClass(context, uClass);

            //判断是否继承了Serializable并提示
            boolean hasImpled = false;
            for (PsiClassType psiClassType : uClass.getImplementsListTypes()) {
                if (CLASS_SERIALIZABLE.equals(psiClassType.getCanonicalText())) {
                    hasImpled = true;
                    break;
                }
            }
            if (!hasImpled) {
                context.report(ISSUE,
                        uClass.getNameIdentifier(),
                        context.getLocation(uClass.getNameIdentifier()),
                        String.format("内部类 `%1$s` 需要实现Serializable接口", uClass.getName()));
            }

        }
    }
}
