package com.lintrules.detectors;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.intellij.psi.PsiClassType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UAnonymousClass;
import org.jetbrains.uast.UClass;
import java.util.Collections;
import java.util.List;


/**
 * @author hewei
 * @desc 序列化内部类检查
 */
@SuppressWarnings("UnstableApiUsage")
public class CsSerialDetector extends Detector implements Detector.UastScanner {

    private static final String CLASS_SERIALIZABLE = "java.io.Serializable";

    public static final Issue ISSUE = Issue.create(
            "InnerClassSerializable",
            "内部类需要实现Serializable接口",
            "内部类需要实现Serializable接口",
            Category.SECURITY, 5, Severity.ERROR,
            new Implementation(CsSerialDetector.class, Scope.JAVA_FILE_SCOPE));

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
    public void visitClass(@NotNull JavaContext context, @NotNull UClass declaration) {
        //只从最外部开始向内部类递归检查
        if (declaration instanceof UAnonymousClass) {
            return;
        }
        sortClass(context, declaration);
    }

    private void sortClass(JavaContext context, UClass declaration) {
        UClass[] innerClasses = declaration.getInnerClasses();
        if(innerClasses.length == 0){
            return;
        }
        for (UClass uClass : declaration.getInnerClasses()) {
            sortClass(context, uClass);
            //查找每个子类继承的接口,看他是否集成了序列化接口
            boolean hasImplement = false;
            for (PsiClassType psiClassType : uClass.getImplementsListTypes()) {
                if (CLASS_SERIALIZABLE.equals(psiClassType.getCanonicalText())) {
                    hasImplement = true;
                    break;
                }
            }

            if (!hasImplement) {
                context.report(ISSUE,
                        uClass.getNameIdentifier(),
                        context.getNameLocation(uClass),
                        String.format("内部类 `%1$s` 需要实现Serializable接口", uClass.getName()));
            }
        }
    }
}
