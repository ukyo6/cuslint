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

import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UAnonymousClass;
import org.jetbrains.uast.UClass;
import org.w3c.dom.Element;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author hewei
 * @desc 序列化内部类检查
 */
public class SerializableDetector extends Detector implements Detector.UastScanner {

    public static final String CLASS_SERIALIZABLE = "java.io.Serializable";

    public static final Issue ISSUE = Issue.create(
            "SerializableImpl",
            "SerializableImpl",
            "SerializableImpl",
            Category.SECURITY, 5, Severity.ERROR,
            new Implementation(SerializableDetector.class, Scope.JAVA_FILE_SCOPE));

    @Nullable
    @Override
    public List<String> applicableSuperClasses() {
        return Collections.singletonList(CLASS_SERIALIZABLE);
    }


    @Override
    public void visitClass(JavaContext context, UClass declaration) {
        if (declaration instanceof UAnonymousClass) { //只从外部开始检查,内部类忽略
            return;
        }

        declaration.getImplementsListTypes()[0].getClassName();
        for (UClass uClass : declaration.getInnerClasses()) {
            UClass superClass = uClass.getSuperClass();
        }
    }
}
