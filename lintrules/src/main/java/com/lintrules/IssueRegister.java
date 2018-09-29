package com.lintrules;

import com.android.tools.lint.checks.LogDetector;
import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.Issue;
import com.lintrules.detectors.SelfLogDetector;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class IssueRegister extends IssueRegistry{
    @NotNull
    @Override
    public List<Issue> getIssues() {
        System.out.println("==== my lint start ====");
        return new ArrayList<Issue>(){{
            add(SelfLogDetector.ISSUE);
        }};
    }
}
