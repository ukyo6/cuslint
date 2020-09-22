package com.lintrules.detectors;

import com.android.SdkConstants;
import com.android.annotations.NonNull;
import com.android.annotations.Nullable;
import com.android.builder.model.Variant;
import com.android.ide.common.gradle.model.IdeAndroidProject;
import com.android.ide.common.rendering.api.ResourceNamespace;
import com.android.ide.common.repository.GradleVersion;
import com.android.ide.common.resources.ResourceItem;
import com.android.ide.common.resources.ResourceRepository;
import com.android.ide.common.util.PathString;
import com.android.resources.ResourceFolderType;
import com.android.resources.ResourceType;
import com.android.resources.ResourceUrl;
import com.android.tools.lint.client.api.LintClient;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Context;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.Lint;
import com.android.tools.lint.detector.api.Location;
import com.android.tools.lint.detector.api.ResourceXmlDetector;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.XmlContext;
import com.android.utils.XmlUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static com.android.SdkConstants.ANDROID_URI;
import static com.android.SdkConstants.ATTR_SRC;
import static com.android.SdkConstants.ATTR_SRC_COMPAT;
import static com.android.SdkConstants.AUTO_URI;
import static com.android.SdkConstants.TAG_ANIMATED_VECTOR;
import static com.android.SdkConstants.TAG_VECTOR;

/**
 * @author hewei
 * @desc 对FireBase上SVG的一些异常处理, 推荐使用app:srcCompat属性
 */
public class CsSvgDetector extends ResourceXmlDetector {

    /** The main issue discovered by this detector */
    public static final Issue ISSUE =
            Issue.create(
                    "CsSvgDetector",
                    "Using srcCompat for vectorDrawable",
                    "针对firebase上SVG的一些崩溃, 推荐使用`srcCompat`属性",
                    Category.CORRECTNESS,
                    5,
                    Severity.ERROR,
                    new Implementation(
                            CsSvgDetector.class,
                            Scope.ALL_RESOURCES_SCOPE,
                            Scope.RESOURCE_FILE_SCOPE))
                    .addMoreInfo(
                            "https://www.jianshu.com/p/8c7690070c17");

    /** Whether to skip the checks altogether. */
    private boolean mSkipChecks;

    /** All vector drawables in the project. */
    private final Set<String> mVectors = Sets.newHashSet();

    /** Whether the project uses AppCompat for vectors. */
    private boolean mUseSupportLibrary;

    @Override
    public void beforeCheckRootProject(@NonNull Context context) {
        IdeAndroidProject model = context.getProject().getGradleProjectModel();

        if (model == null) {
            mSkipChecks = true;
            return;
        }

//        if (context.getProject().getMinSdk() >= 24) {
//            mSkipChecks = true;
//            return;
//        }

        GradleVersion version = context.getProject().getGradleModelVersion();
        if (version == null || version.getMajor() < 2) {
            mSkipChecks = true;
            return;
        }

        Variant currentVariant = context.getProject().getCurrentVariant();

        if (currentVariant == null) {
            mSkipChecks = true;
            return;
        }

        if (Boolean.TRUE.equals(
                currentVariant.getMergedFlavor().getVectorDrawables().getUseSupportLibrary())) {
            mUseSupportLibrary = true;
        }
    }

    @Override
    public boolean appliesTo(@NonNull ResourceFolderType folderType) {
        //noinspection SimplifiableIfStatement
        if (mSkipChecks) {
            return false;
        }

        return folderType == ResourceFolderType.DRAWABLE || folderType == ResourceFolderType.LAYOUT;
    }

    /**
     * Saves names of all vector resources encountered. Because "drawable" is before "layout" in
     * alphabetical order, Lint will first call this on every vector, before calling {@link
     * #visitAttribute(XmlContext, Attr)} on every attribute.
     */
    @Override
    public void visitElement(@NonNull XmlContext context, @NonNull Element element) {
        if (mSkipChecks) {
            return;
        }
        String resourceName = Lint.getBaseName(context.file.getName());
        mVectors.add(resourceName);
    }

    @Nullable
    @Override
    public Collection<String> getApplicableElements() {
        return mSkipChecks ? null : Arrays.asList(TAG_VECTOR, TAG_ANIMATED_VECTOR);
    }

    @Nullable
    @Override
    public Collection<String> getApplicableAttributes() {
        return mSkipChecks ? null : ImmutableList.of(ATTR_SRC, ATTR_SRC_COMPAT);
    }

    @Override
    public void visitAttribute(@NonNull XmlContext context, @NonNull Attr attribute) {
        if (mSkipChecks) {
            return;
        }

        boolean incrementalMode =
                !context.getDriver().getScope().contains(Scope.ALL_RESOURCE_FILES);

        if (!incrementalMode && mVectors.isEmpty()) {
            return;
        }

        Predicate<String> isVector;
        if (!incrementalMode) {
            // TODO: Always use resources, once command-line client supports it.
            isVector = mVectors::contains;
        } else {
            LintClient client = context.getClient();
            ResourceRepository resources =
                    client.getResourceRepository(context.getMainProject(), true, false);
            if (resources == null) {
                // We only run on a single layout file, but have no access to the resources
                // database, there's no way we can perform the check.
                return;
            }

            isVector = name -> checkResourceRepository(resources, name);
        }

        String name = attribute.getLocalName();
        String namespace = attribute.getNamespaceURI();
        if ((ATTR_SRC.equals(name) && !ANDROID_URI.equals(namespace)
                || (ATTR_SRC_COMPAT.equals(name) && !AUTO_URI.equals(namespace)))) {
            // Not the attribute we are looking for.
            return;
        }

        ResourceUrl resourceUrl = ResourceUrl.parse(attribute.getValue());
        if (resourceUrl == null) {
            return;
        }

        if (mUseSupportLibrary && ATTR_SRC.equals(name) && isVector.test(resourceUrl.name)) {
            Location location = context.getNameLocation(attribute);
            String message = "针对firebase上使用SVG的崩溃,请直接使用 AppcompatImageView 和`app:srcCompat`属性";
            context.report(ISSUE, attribute, location, message);
        }
    }

    private static boolean checkResourceRepository(
            @NonNull ResourceRepository resources, @NonNull String name) {
        List<ResourceItem> items =
                resources.getResources(ResourceNamespace.TODO(), ResourceType.DRAWABLE, name);

        // Check if at least one drawable with this name is a vector.
        for (ResourceItem item : items) {
            PathString source = item.getSource();
            if (source == null) {
                return false;
            }
            File file = source.toFile();
            if (file == null) {
                return false;
            }

            if (!source.getFileName().endsWith(SdkConstants.DOT_XML)) {
                continue;
            }

            String rootTagName = XmlUtils.getRootTagName(file);
            return TAG_VECTOR.equals(rootTagName) || TAG_ANIMATED_VECTOR.equals(rootTagName);
        }

        return false;
    }
}
