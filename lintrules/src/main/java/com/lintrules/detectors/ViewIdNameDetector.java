package com.lintrules.detectors;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.LayoutDetector;
import com.android.tools.lint.detector.api.Project;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.SourceCodeScanner;
import com.android.tools.lint.detector.api.XmlContext;

import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Attr;

import java.util.Collection;
import java.util.Collections;

import static com.android.SdkConstants.ANDROID_URI;
import static com.android.SdkConstants.ID_PREFIX;
import static com.android.SdkConstants.NEW_ID_PREFIX;

public class ViewIdNameDetector extends LayoutDetector implements SourceCodeScanner {

    public static final String ATTR_ID = "id";

    public static final Issue ISSUE = Issue.create(
            "ViewIdName",
            "ViewIdName命名规范",
            "ViewIdName命名规范",
            Category.SECURITY, 5, Severity.ERROR,
            new Implementation(ViewIdNameDetector.class, Scope.RESOURCE_FILE_SCOPE));

    @Nullable
    @Override
    public Collection<String> getApplicableAttributes() {
        return Collections.singletonList(ATTR_ID);
    }

    @Override
    public void visitAttribute(XmlContext context, Attr attribute) {
        Project mainProject = context.getMainProject();
        String value = attribute.getValue();
        String name = attribute.getLocalName();


        if (!ANDROID_URI.equals(attribute.getNamespaceURI())) {
            return;
        }

        if (name.equals(ATTR_ID) && value.startsWith(NEW_ID_PREFIX) ||
                value.startsWith(ID_PREFIX)) {
            String localName = attribute.getLocalName();

            System.out.println("localname=" + localName);
        }
    }
}
