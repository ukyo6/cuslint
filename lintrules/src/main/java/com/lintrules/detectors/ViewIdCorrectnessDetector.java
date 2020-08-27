package com.lintrules.detectors;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.LayoutDetector;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.SourceCodeScanner;
import com.android.tools.lint.detector.api.XmlContext;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import java.util.Arrays;
import java.util.Collection;

import static com.android.SdkConstants.ANDROID_URI;
import static com.android.SdkConstants.BUTTON;
import static com.android.SdkConstants.CHECKED_TEXT_VIEW;
import static com.android.SdkConstants.CHECK_BOX;
import static com.android.SdkConstants.EDIT_TEXT;
import static com.android.SdkConstants.IMAGE_VIEW;
import static com.android.SdkConstants.NEW_ID_PREFIX;
import static com.android.SdkConstants.RADIO_BUTTON;
import static com.android.SdkConstants.SWITCH;
import static com.android.SdkConstants.TEXT_VIEW;
import static com.android.SdkConstants.TOGGLE_BUTTON;
/**
 * @author hewei
 * @desc ViewIdName的命名建议使用 view的缩写_xxx,例如tv_username
 *
 */
public class ViewIdCorrectnessDetector extends LayoutDetector {

    private static final String ATTR_ID = "id";

    public static final Issue ISSUE = Issue.create(
            "ViewIdName",
            "ViewId命名不规范",
            "ViewIdName建议使用 view的缩写加上_xxx,例如tv_username",
            Category.CORRECTNESS, 5, Severity.ERROR,
            new Implementation(ViewIdCorrectnessDetector.class, Scope.RESOURCE_FILE_SCOPE));


    @Override
    public Collection<String> getApplicableElements() {
        return Arrays.asList(  //指定检查这几个控件的命名规范,可自行扩展
                TEXT_VIEW,
                IMAGE_VIEW,
                BUTTON,
                EDIT_TEXT,
                CHECK_BOX
        );
    }

    @Override
    public void visitElement(XmlContext context, Element element) {
        //这个detector只扫描android:id属性,其他属性跳过
        if (!element.hasAttributeNS(ANDROID_URI, ATTR_ID)) return;

        Attr attr = element.getAttributeNodeNS(ANDROID_URI, ATTR_ID);
        String value = attr.getValue();
        if (value.startsWith(NEW_ID_PREFIX)) {
            String idValue = value.substring(NEW_ID_PREFIX.length());
            boolean right = true;
            String expMsg = "";
            switch (element.getTagName()) {
                case TEXT_VIEW:
                    expMsg = "tv";
                    right = idValue.startsWith(expMsg);
                    break;
                case IMAGE_VIEW:
                    expMsg = "iv";
                    right = idValue.startsWith(expMsg);
                    break;
                case BUTTON:
                    expMsg = "btn";
                    right = idValue.startsWith(expMsg);
                    break;
                case EDIT_TEXT:
                    expMsg = "et";
                    right = idValue.startsWith(expMsg);
                    break;
                case CHECK_BOX:
                    expMsg = "cb";
                    right = idValue.startsWith(expMsg);
                    break;
            }

            if(!right) {
                context.report(ISSUE, attr, context.getLocation(attr),
                        String.format(
                                "ViewIdName建议使用 view的缩写_xxx; 建议使用 `%1$s_xxx`",
                                expMsg));
            }
        }
    }
}
