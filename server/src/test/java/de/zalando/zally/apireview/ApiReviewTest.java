package de.zalando.zally.apireview;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.zalando.zally.rule.Rule;
import de.zalando.zally.violation.Violation;
import de.zalando.zally.violation.ViolationType;
import io.swagger.models.Swagger;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class ApiReviewTest {

    private Rule dummyRule = new Rule() {

        @NotNull
        @Override
        public String getTitle() {
            return null;
        }

        @NotNull
        @Override
        public ViolationType getViolationType() {
            return null;
        }

        @Nullable
        @Override
        public String getUrl() {
            return null;
        }

        @NotNull
        @Override
        public String getCode() {
            return null;
        }

        @NotNull
        @Override
        public String getName() {
            return "dummyRule";
        }

        @Nullable
        @Override
        public Violation validate(@NotNull Swagger swagger) {
            return null;
        }
    };

    @Test
    public void shouldAggregateRuleTypeCount() {
        Violation mustViolation1 = new Violation(dummyRule, "", "", ViolationType.MUST, "", Collections.emptyList());
        Violation mustViolation2 = new Violation(dummyRule, "", "", ViolationType.MUST, "", Collections.emptyList());
        Violation shouldViolation = new Violation(dummyRule, "", "", ViolationType.SHOULD, "", Collections.emptyList());

        ApiReview apiReview = new ApiReview(emptyReviewRequest(), "", asList(mustViolation1, mustViolation2, shouldViolation));

        assertThat(apiReview.getMustViolations()).isEqualTo(2);
        assertThat(apiReview.getShouldViolations()).isEqualTo(1);
        assertThat(apiReview.getMayViolations()).isEqualTo(0);
        assertThat(apiReview.getHintViolations()).isEqualTo(0);
    }

    @Test
    public void shouldCalculateNumberOfEndpoints() throws URISyntaxException, IOException {
        Violation violation1 = new Violation(dummyRule, "", "", ViolationType.MUST, "", asList("1", "2"));
        Violation violation2 = new Violation(dummyRule, "", "", ViolationType.MUST, "", asList("3"));

        String apiDefinition = FileUtils.readFileToString(
            new File(this.getClass().getResource("/fixtures/limitNumberOfResourcesValid.json").toURI()),
            Charset.forName("UTF-8"));

        ApiReview apiReview = new ApiReview(emptyReviewRequest(), apiDefinition, asList(violation1, violation2));

        assertThat(apiReview.getNumberOfEndpoints()).isEqualTo(2);
    }

    private ObjectNode emptyReviewRequest() {
        return new ObjectMapper().createObjectNode();
    }
}
