package com.lintrules;

import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.ApiKt;
import com.android.tools.lint.detector.api.Issue;
import com.lintrules.detectors.CsSvgDetector;
import com.lintrules.detectors.CsLayoutNameDetector;
import com.lintrules.detectors.CsMsgObtainDetector;
import com.lintrules.detectors.CsNewThreadDetector;
import com.lintrules.detectors.CsLogDetector;
import com.lintrules.detectors.CsSerialDetector;
import com.lintrules.detectors.CsViewIdDetector;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class IssuesRegister extends IssueRegistry {

    @NotNull
    @Override
    public List<Issue> getIssues() {
        System.out.println("==== my lint start ====");
        System.out.println("api=" + getApi() + ",minApi=" + getMinApi()+",CurrentApi="+ ApiKt.CURRENT_API);

        return new ArrayList<Issue>() {{
            add(CsLogDetector.ISSUE);
            add(CsNewThreadDetector.ISSUE_NEW_THREAD);
            add(CsMsgObtainDetector.ISSUE);
            add(CsViewIdDetector.ISSUE);
            add(CsLayoutNameDetector.ACTIVITY_LAYOUT_NAME_ISSUE);
            add(CsLayoutNameDetector.FRAGMENT_LAYOUT_NAME_ISSUE);
            add(CsSerialDetector.ISSUE);
            add(CsSvgDetector.ISSUE);
        }};
    }

    @Override
    public int getApi() {
        return ApiKt.CURRENT_API;
    }

    @Override
    public int getMinApi() {  //兼容3.1
        return 1;
    }
}
