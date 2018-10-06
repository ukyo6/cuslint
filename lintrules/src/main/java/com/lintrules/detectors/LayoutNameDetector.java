package com.lintrules.detectors;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;

public class LayoutNameDetector extends Detector implements Detector.UastScanner {

    public static final Issue ISSUE = Issue.create(
            "ViewIdName",
            "ViewIdName命名",
            "请勿直接调用android.util.Log，应该使用统一Log工具类",
            Category.SECURITY, 5, Severity.ERROR,
            new Implementation(SelfLogDetector.class, Scope.RESOURCE_FILE_SCOPE));



}
