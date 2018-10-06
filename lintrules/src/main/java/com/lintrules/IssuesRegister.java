package com.lintrules;

import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.ApiKt;
import com.android.tools.lint.detector.api.Issue;
import com.lintrules.detectors.MessageObtainDetector;
import com.lintrules.detectors.MyToastDetector;
import com.lintrules.detectors.NewThreadDetector;
import com.lintrules.detectors.SelfLogDetector;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IssuesRegister extends IssueRegistry {
    @NotNull
    @Override
    public List<Issue> getIssues() {
        System.out.println("==== my lint start ====");
        System.out.println("api=" + getApi() + ",minApi=" + getMinApi()+",CurrentApi="+ ApiKt.CURRENT_API);

        return new ArrayList<Issue>() {{
            add(SelfLogDetector.ISSUE);
            add(NewThreadDetector.ISSUE);
            add(MessageObtainDetector.ISSUE);
        }};
    }

    @Override
    public int getApi() {
        return ApiKt.CURRENT_API;
    }

    @Override
    public int getMinApi() {
        return 1;
    }
}
