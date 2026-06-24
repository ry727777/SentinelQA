# GitHub Actions CI/CD Setup Documentation

## Overview

This document explains the GitHub Actions workflow implementation for the **SentinelQA** test automation framework.

---

## 🚀 Workflow File Location

**File**: `.github/workflows/automation.yml`

This file is automatically detected and executed by GitHub Actions when you push to the `main` branch or create a pull request.

---

## 📋 Workflow Triggers

The workflow automatically executes under these conditions:

### 1. **Push to Main Branch**
```yaml
on:
  push:
    branches:
      - main
```
- Triggers when you push commits to the `main` branch
- Useful for continuous integration of merged code

### 2. **Pull Request to Main Branch**
```yaml
pull_request:
  branches:
    - main
```
- Triggers when a PR is opened, updated, or reopened targeting `main`
- Validates code before merging
- Helps prevent broken code from entering `main`

### 3. **Manual Execution (workflow_dispatch)**
```yaml
workflow_dispatch:
```
- Allows manual triggering from GitHub Actions UI
- Useful for:
  - Running tests on-demand
  - Debugging CI issues
  - Testing specific code without making commits

**How to manually trigger**:
1. Go to your repository on GitHub
2. Click **Actions** tab
3. Select **SentinelQA Test Automation** workflow
4. Click **Run workflow** > **Run workflow**

---

## 🔐 Secret Management

### Setting up OPENAI_API_KEY Secret

**GitHub Secrets** provide secure storage for sensitive data without exposing them in logs or code.

#### Step-by-Step Setup:

1. **Navigate to Repository Settings**
   - Go to your GitHub repository
   - Click **Settings** (top right)
   - Left sidebar: Click **Secrets and variables** > **Actions**

2. **Create New Secret**
   - Click **New repository secret**
   - Name: `OPENAI_API_KEY`
   - Value: Your actual API key (e.g., `sk-...`)
   - Click **Add secret**

3. **Verify in Workflow**
   - The workflow file references: `${{ secrets.OPENAI_API_KEY }}`
   - GitHub securely injects this value at runtime
   - The key is **never exposed** in logs or output

#### How It's Used in Java:

```java
// In your Java code, access it like any environment variable:
String apiKey = System.getenv("OPENAI_API_KEY");

// Example: Using it with GeminiClient
public class GeminiClient {
    private String apiKey = System.getenv("OPENAI_API_KEY");
    
    public GeminiClient() {
        if (apiKey == null || apiKey.isEmpty()) {
            logger.warn("API key not set");
        }
    }
}
```

#### Security Best Practices:

✅ **DO**:
- Store secrets in GitHub Secrets
- Use `${{ secrets.SECRET_NAME }}` in workflows
- Rotate API keys periodically
- Use environment variables to access secrets in code

❌ **DON'T**:
- Hardcode API keys in code or config files
- Print secrets to logs
- Commit `.env` files with secrets
- Share API keys in messages or documentation

---

## 🔧 Workflow Steps Explained

### Step 1: Checkout Repository
```yaml
- name: 📋 Checkout Repository
  uses: actions/checkout@v4
  with:
    fetch-depth: 0
```
- Downloads your repository code to the runner
- `fetch-depth: 0` fetches full git history (useful for git operations)

### Step 2: Set up Java 17 (Temurin)
```yaml
- name: 🔧 Set up Java 17 (Temurin)
  uses: actions/setup-java@v4
  with:
    java-version: 17
    distribution: temurin
    cache: maven
```
- Installs Java 17 using Temurin JDK (open-source, reliable)
- `cache: maven` automatically caches Maven dependencies
- Matches your `pom.xml` configuration (Java 17)

### Step 3: Cache Maven Dependencies
```yaml
- name: 📦 Cache Maven Dependencies
  uses: actions/cache@v4
```
- Caches `~/.m2/repository/` to speed up builds
- Cache key: Based on `pom.xml` hash
- If `pom.xml` changes, cache is invalidated automatically
- Significantly reduces build time on subsequent runs

### Step 4: Configure Environment Variables
```yaml
- name: 🔑 Configure Environment Variables
  run: |
    echo "OPENAI_API_KEY=${{ secrets.OPENAI_API_KEY }}" >> $GITHUB_ENV
```
- Exports `OPENAI_API_KEY` secret as environment variable
- Makes it available to all subsequent steps
- Java can access it via: `System.getenv("OPENAI_API_KEY")`

### Step 5: Display Java and Maven Versions
```yaml
- name: ℹ️ Display Java and Maven Versions
  run: |
    java -version
    mvn -version
```
- Diagnostic step for debugging CI issues
- Confirms correct Java/Maven are installed
- Helps troubleshoot version conflicts

### Step 6: Clean and Compile Project
```yaml
- name: 🔨 Clean and Compile Project
  run: mvn clean compile -DskipTests -B -V
```
- Cleans previous build artifacts
- Compiles Java code (validates syntax)
- `-DskipTests`: Skip tests during compilation
- `-B`: Batch mode (no interactive input)
- `-V`: Verbose output

### Step 7: Run Tests
```yaml
- name: 🧪 Run Tests
  run: mvn test -B -V
  continue-on-error: true
  env:
    OPENAI_API_KEY: ${{ env.OPENAI_API_KEY }}
```
- Executes TestNG tests configured in `testng.xml`
- `continue-on-error: true`: Continues even if tests fail (so we can collect reports)
- Exports `OPENAI_API_KEY` to test environment
- Tests can now access the API key via `System.getenv()`

### Step 8-10: Upload Test Reports
```yaml
- name: 📊 Upload Test Reports and Artifacts
  if: always()
  uses: actions/upload-artifact@v4
```
- `if: always()`: Executes even if tests fail (crucial!)
- Uploads test reports, logs, and Allure results
- Artifacts retain for 30 days
- Can be downloaded from GitHub Actions UI

### Step 11: Generate Workflow Summary
```yaml
- name: 📝 Generate Workflow Summary
  if: always()
  run: |
    echo "## Test Automation Workflow Summary" >> $GITHUB_STEP_SUMMARY
```
- Creates a summary visible in GitHub Actions UI
- Displays environment details and artifact locations
- Helps with quick debugging

---

## 📊 Accessing Test Results

### Download Artifacts from GitHub

1. **Go to Actions**
   - Your repository → **Actions** tab

2. **Select Workflow Run**
   - Click the latest workflow run

3. **Download Artifacts**
   - Scroll down to **Artifacts** section
   - Download:
     - `test-reports-and-logs-*`: TestNG reports, test-output, logs
     - `allure-report-*`: Detailed Allure test report

### View Reports Locally

After downloading:

```bash
# Extract Allure report
unzip allure-report-*.zip
cd target/allure-report

# Open in browser
open index.html  # macOS
start index.html # Windows
xdg-open index.html # Linux
```

---

## 🔄 Environment Variable Access in Java

Your Java code can access the `OPENAI_API_KEY` environment variable:

```java
package com.rahul.framework.ai;

public class GeminiClient {
    private static final String API_KEY_ENV_VAR = "OPENAI_API_KEY";
    private final String apiKey;

    public GeminiClient() {
        // This will be set when running in GitHub Actions CI
        this.apiKey = System.getenv(API_KEY_ENV_VAR);
        
        if (apiKey == null || apiKey.isEmpty()) {
            logger.warn("API key not set. Set OPENAI_API_KEY environment variable.");
        } else {
            logger.info("API key successfully loaded from environment");
        }
    }

    public boolean isApiKeySet() {
        return apiKey != null && !apiKey.isEmpty();
    }
}
```

---

## 💾 Cache Configuration

### How Maven Caching Works

```yaml
- name: 📦 Cache Maven Dependencies
  uses: actions/cache@v4
  with:
    path: ~/.m2/repository
    key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
    restore-keys: |
      ${{ runner.os }}-maven-
```

**Key Benefits**:
- First run: Downloads and caches dependencies (~2-3 minutes)
- Subsequent runs: Uses cached dependencies (~30 seconds)
- **Hash-based invalidation**: When you update `pom.xml`, cache is automatically refreshed

**Cache Key Explained**:
- `${{ runner.os }}`: Ubuntu-latest (prevents cross-OS cache conflicts)
- `maven-`: Prefix for clarity
- `${{ hashFiles('**/pom.xml') }}`: Hash of pom.xml content
  - If pom.xml changes → new cache created
  - If pom.xml unchanged → existing cache reused

---

## 🐛 Troubleshooting

### Issue: Workflow Not Triggering on Push

**Solution**:
1. Ensure the workflow file is in `.github/workflows/` directory
2. Commit and push the workflow file to `main` branch
3. Workflows only execute after they're committed to the branch

```bash
git add .github/workflows/automation.yml
git commit -m "Add GitHub Actions CI workflow"
git push origin main
```

### Issue: Tests Fail but Workflow Shows as Successful

**Cause**: `continue-on-error: true` in the test step allows workflow to proceed even if tests fail.

**How to Fix**:
1. Check the **Artifacts** section for test reports
2. Look for failures in the **Test Reports** artifact
3. The workflow summary will indicate if tests failed

**To make workflow fail on test failure** (optional):
```yaml
- name: 🧪 Run Tests
  run: mvn test -B -V
  # Remove continue-on-error to fail on test failure
```

### Issue: API Key Not Accessible in Tests

**Check**:
1. Is `OPENAI_API_KEY` secret configured in GitHub Settings?
2. Is the environment variable exported in the workflow?
3. Can your Java code access it?

**Debug**:
```yaml
- name: Debug Environment
  run: |
    # Check if variable is set (won't print the actual key)
    if [ -z "$OPENAI_API_KEY" ]; then
      echo "❌ OPENAI_API_KEY is not set"
    else
      echo "✓ OPENAI_API_KEY is set (length: ${#OPENAI_API_KEY})"
    fi
```

### Issue: Maven Compilation Fails

**Check**:
1. Java version matches `pom.xml`: Currently set to Java 17
2. All dependencies are available (check Maven Central)
3. Code has no syntax errors

**Update Java version** (if needed):
```yaml
env:
  JAVA_VERSION: '21'  # Change from 17 to 21
```

And update `pom.xml`:
```xml
<properties>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
</properties>
```

---

## 📈 Performance Optimization

### Cache Hit Rate
- First run: ~2-3 minutes (downloads dependencies)
- Subsequent runs: ~30-60 seconds (uses cache)
- When `pom.xml` changes: Full download again (~2-3 minutes)

### Reduce Build Time:
1. **Use Maven cache** ✓ Already configured
2. **Skip tests during compile**: ✓ `-DskipTests` already used
3. **Use parallel test execution** (optional):
   ```xml
   <suiteXmlFiles>
       <suiteXmlFile>testng.xml</suiteXmlFile>
   </suiteXmlFiles>
   <parallel>methods</parallel>
   <threadCount>4</threadCount>
   ```

---

## 🔒 Security Best Practices

### For GitHub Actions Workflows:

1. **Never log secrets**
   ```yaml
   # ❌ WRONG
   run: echo "API Key: ${{ secrets.API_KEY }}"
   
   # ✅ CORRECT
   run: echo "API key is set"
   ```

2. **Use GitHub Secrets, not hardcoded values**
   ```yaml
   # ✅ CORRECT
   env:
     API_KEY: ${{ secrets.OPENAI_API_KEY }}
   
   # ❌ WRONG
   env:
     API_KEY: sk-1234567890
   ```

3. **Minimize secret permissions**
   ```yaml
   permissions:
     contents: read  # Only read permission
   ```

4. **Rotate secrets periodically**
   - Regenerate API keys every 3-6 months
   - Update GitHub secret with new key

---

## 📝 Customization Guide

### Change Java Version

In `.github/workflows/automation.yml`:
```yaml
env:
  JAVA_VERSION: '21'  # Change from 17 to 21
```

Also update `pom.xml`:
```xml
<properties>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
</properties>
```

### Add More Environment Variables

```yaml
- name: 🔑 Configure Environment Variables
  run: |
    echo "OPENAI_API_KEY=${{ secrets.OPENAI_API_KEY }}" >> $GITHUB_ENV
    echo "API_URL=${{ secrets.API_URL }}" >> $GITHUB_ENV
    echo "DB_PASSWORD=${{ secrets.DB_PASSWORD }}" >> $GITHUB_ENV
```

### Run Only Specific Tests

```yaml
- name: 🧪 Run Tests
  run: mvn test -Dtest=LoginTest,CartPageSortingTest
```

### Run Tests with Specific TestNG Groups

```yaml
- name: 🧪 Run Tests
  run: mvn test -Dgroups=ui
```

---

## 📚 Useful Links

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [GitHub Secrets Best Practices](https://docs.github.com/en/actions/security-guides/encrypted-secrets)
- [setup-java Action](https://github.com/actions/setup-java)
- [Cache Action](https://github.com/actions/cache)
- [Upload Artifacts Action](https://github.com/actions/upload-artifact)
- [Maven Documentation](https://maven.apache.org/)

---

## ✅ Verification Checklist

Before pushing to `main`, verify:

- [ ] `.github/workflows/automation.yml` is created
- [ ] `OPENAI_API_KEY` secret is configured in GitHub Settings
- [ ] Project runs successfully locally: `mvn clean test`
- [ ] `pom.xml` has correct Java version (17)
- [ ] No secrets are hardcoded in code
- [ ] `.gitignore` includes sensitive files
- [ ] testng.xml is properly configured
- [ ] All dependencies are available in Maven Central

---

## 🎯 Next Steps

1. **Commit and push the workflow file**:
   ```bash
   git add .github/workflows/automation.yml
   git commit -m "Add GitHub Actions CI workflow for test automation"
   git push origin main
   ```

2. **Verify workflow runs**:
   - Go to **Actions** tab on GitHub
   - Should see "SentinelQA Test Automation" workflow running

3. **Download and review test reports**:
   - After workflow completes, download artifacts
   - Review test results and logs

4. **Set up branch protection** (optional):
   - Settings → Branches → Add branch protection rule
   - Require GitHub Actions workflow to pass before merging

---

## 📞 Support

For issues or questions about the GitHub Actions workflow:
1. Check the **Workflow Logs** in GitHub Actions UI
2. Review this documentation
3. Check GitHub Actions official documentation
4. Create an issue in your repository

---

**Workflow Version**: 1.0  
**Last Updated**: 2024  
**Maintainer**: SentinelQA Team
