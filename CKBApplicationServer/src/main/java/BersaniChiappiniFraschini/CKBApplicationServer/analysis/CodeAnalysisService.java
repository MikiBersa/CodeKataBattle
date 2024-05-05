package BersaniChiappiniFraschini.CKBApplicationServer.analysis;

import BersaniChiappiniFraschini.CKBApplicationServer.battle.Battle;
import BersaniChiappiniFraschini.CKBApplicationServer.ecaRunners.ECARunner;
import BersaniChiappiniFraschini.CKBApplicationServer.ecaRunners.JavaECARunner;
import BersaniChiappiniFraschini.CKBApplicationServer.testRunners.JavaTestRunner;
import BersaniChiappiniFraschini.CKBApplicationServer.testRunners.TestRunner;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This service is responsible for analyzing students' solutions by running the tests and the static analysis
 * (if configured in the battle).
 */
@Service
@NoArgsConstructor
public class CodeAnalysisService {
    private record LanguageAnalysisTools(ProjectBuilder projectBuilder, ECARunner ecaRunner, TestRunner testRunner){}
    private final Map<String, LanguageAnalysisTools> languageAnalysisTools = new HashMap<>();

    // Register Analysis Tools
    {
        languageAnalysisTools.put("java 17", new LanguageAnalysisTools(
                new JavaProjectBuilder(),
                new JavaECARunner(),
                new JavaTestRunner())
        );
    }

    /**
     * Launch the automated assessment process of the code.
     * The assessment follows the steps: build -> test -> static analysis.
     * @param projectDirectory directory of the project to evaluate.
     * @param battle battle for which the solution has been provided.
     * @return EvaluationResult containing all the results to tests and static analysis.
     * @throws Exception if one of the automated steps fail (build -> test -> static analysis)
     */
    public EvaluationResult launchAutomatedAssessment(String projectDirectory, Battle battle) throws Exception {

        var language = battle.getProject_language();
        var testFileName = battle.getTests_file_name();
        var evaluationParameters = battle.getEvaluation_parameters();

        var analysisTools = languageAnalysisTools.get(language);
        // Build/compile project
        var compiledProjectPath = analysisTools.projectBuilder.buildProject(projectDirectory);
        // Run tests
        var testResults = analysisTools.testRunner.launchUnitTests(compiledProjectPath, testFileName);
        // Run Static Analysis
        var staticAnalysisResults = analysisTools.ecaRunner.launchExternalCodeAnalysis(projectDirectory, evaluationParameters);

        EvaluationResult results = new EvaluationResult();
        results.setTests_results(testResults);
        results.setStatic_analysis_results(staticAnalysisResults);

        // timeliness score calculation
        Date now = new Date(System.currentTimeMillis());
        long battleDurationMillis = battle.getSubmission_deadline().getTime() - battle.getEnrollment_deadline().getTime();
        long timeRemaining = battle.getSubmission_deadline().getTime() - now.getTime();

        // score calculated as rounded down percentage of time remaining over the total duration of the battle
        float timeRatio = (float)timeRemaining / (float)battleDurationMillis;
        Integer timelinessScore = (int) (timeRatio * 100);
        results.setTimeliness_score(timelinessScore);

        return results;
    }
}
