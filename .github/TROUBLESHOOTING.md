# GitHub Actions Troubleshooting & FAQ

## 🆘 Common Issues & Solutions

### ❌ Issue: Workflow Not Found

**Symptom**: Can't see "SentinelQA Test Automation" in Actions tab

**Cause**: Workflow file not committed to `main` branch

**Solution**:
```bash
# Ensure workflow file is committed and pushed
git add .github/workflows/automation.yml
git commit -m "Add GitHub Actions workflow"
git push origin main

# Wait 1-2 minutes for GitHub to detect the workflow
# Then refresh the Actions tab
```

---

### ❌ Issue: Tests Pass Locally but Fail in CI

**Symptom**: `mvn test` works on local machine but fails on GitHub Actions

**Possible Causes**:
1. Environment variable not set
2. Configuration file missing
3. Network/API connectivity issue
4. OS-specific behavior (Windows vs Linux)

**Debug Steps**:
```yaml
# Add this step in workflow to debug
- name: Debug Environment
  run: |
    echo "Current directory: $(pwd)"
    echo "Environment variables:"
    env | grep -i "openai\|api\|key" | head -5
    echo "File structure:"
    find . -name "config.properties" -o -name "log4j2.xml"
```

**Common Fixes**:
```yaml
# Ensure OPENAI_API_KEY is passed to tests
- name: 🧪 Run Tests
  run: mvn test -B -V
  env:
    OPENAI_API_KEY: ${{ env.OPENAI_API_KEY }}
```

---

### ❌ Issue: OPENAI_API_KEY Not Accessible in Java

**Symptom**: `System.getenv("OPENAI_API_KEY")` returns null

**Cause**: Secret not configured in GitHub or not exported to environment

**Solution**:

1. **Verify Secret Exists**:
   - GitHub Settings → Secrets and variables → Actions
   - Should see `OPENAI_API_KEY` listed

2. **Verify Workflow Exports It**:
   ```yaml
   - name: 🔑 Configure Environment Variables
     run: echo "OPENAI_API_KEY=${{ secrets.OPENAI_API_KEY }}" >> $GITHUB_ENV
   ```

3. **Verify Java Can Access It**:
   ```java
   public class ApiKeyTest {
       public static void main(String[] args) {
           String key = System.getenv("OPENAI_API_KEY");
           if (key == null) {
               System.out.println("❌ Key is null");
           } else {
               System.out.println("✓ Key found (length: " + key.length() + ")");
           }
       }
   }
   ```

4. **Test Step Should Show Success**:
   ```bash
   echo "OPENAI_API_KEY=${{ secrets.OPENAI_API_KEY }}" >> $GITHUB_ENV
   echo "✓ Environment variables configured successfully"
   ```

---

### ❌ Issue: Maven Build Fails - Java Version Mismatch

**Symptom**: 
```
ERROR] COMPILATION ERROR : error: invalid source release: 17
```

**Cause**: Workflow Java version doesn't match `pom.xml` version

**Solution**:

Check `pom.xml`:
```xml
<properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
</properties>
```

Verify `automation.yml`:
```yaml
env:
  JAVA_VERSION: '17'
```

Both should match. If not:

**Option A**: Update workflow to match pom.xml
```yaml
# If pom.xml says Java 21
env:
  JAVA_VERSION: '21'
```

**Option B**: Update pom.xml to match workflow
```xml
<!-- If workflow specifies Java 17 -->
<maven.compiler.source>17</maven.compiler.source>
<maven.compiler.target>17</maven.compiler.target>
```

---

### ❌ Issue: Maven Cache Not Being Used

**Symptom**: Every build takes 2-3 minutes (downloading dependencies)

**Cause**: 
- First run (expected)
- `pom.xml` changed (forces cache invalidation)
- Cache hit miss (GitHub action cache limits)

**Solution**:

```yaml
# Cache is automatically configured:
- name: 🔧 Set up Java 17 (Temurin)
  uses: actions/setup-java@v4
  with:
    java-version: 17
    distribution: temurin
    cache: maven  # ← This enables Maven cache
```

**To verify cache is working**:
1. Run workflow first time (no cache, ~2-3 min)
2. Make a non-dependency change (e.g., Java code)
3. Push and run again (should use cache, ~30-60 sec)
4. If still slow, check workflow logs for cache messages

---

### ❌ Issue: Artifacts Not Uploading

**Symptom**: No artifacts available to download after workflow

**Cause**: 
- Artifact paths don't exist
- Permissions issue
- Artifact retention expired

**Solution**:

1. **Verify artifact paths exist**:
   ```yaml
   - name: 📊 Upload Test Reports
     if: always()
     uses: actions/upload-artifact@v4
     with:
       name: test-reports-${{ github.run_id }}
       path: |
         test-output/
         reports/
         target/surefire-reports/
       if-no-files-found: warn  # ← Don't fail if paths missing
   ```

2. **Check what directories exist**:
   ```yaml
   - name: List Artifacts
     if: always()
     run: |
       echo "=== Test Output Directories ==="
       ls -la test-output/ 2>/dev/null || echo "test-output/ not found"
       ls -la reports/ 2>/dev/null || echo "reports/ not found"
       ls -la target/surefire-reports/ 2>/dev/null || echo "target/surefire-reports/ not found"
   ```

3. **Use `if: always()`**:
   Ensures artifacts are uploaded even if tests fail

---

### ❌ Issue: Workflow Shows Success but Tests Actually Failed

**Symptom**: Green checkmark in Actions but tests failed

**Cause**: `continue-on-error: true` on test step allows workflow to continue

**This is intentional** - allows us to collect test reports even on failure

**To Make Workflow Fail on Test Failure**:
```yaml
- name: 🧪 Run Tests
  run: mvn test -B -V
  # Remove this line:
  # continue-on-error: true
```

**To Check if Tests Actually Failed**:
1. Click workflow run
2. Look for red ❌ in the "Run Tests" step
3. Or check the test reports artifact

---

### ❌ Issue: Hardcoded Secret Accidentally Exposed

**Symptom**: Realized you pasted API key directly in code or workflow

**Solution** (Immediately):
1. **Rotate the key**: Generate a new API key in Google AI Studio
2. **Update GitHub Secret**: Settings → Secrets → Update `OPENAI_API_KEY`
3. **Never commit the exposed key**: Git history keeps it forever

**To Remove from Git History** (advanced):
```bash
# Use git-filter-branch or BFG Repo-Cleaner
# This is complex - best to just rotate the key
```

**Prevention**:
- Always use `${{ secrets.SECRET_NAME }}`
- Never echo secrets: `echo ${{ secrets.API_KEY }}`
- Use `.gitignore` for local `.env` files

---

## ❓ Frequently Asked Questions

### Q: Can I run tests on multiple Java versions?

**A**: Yes! Create a build matrix:
```yaml
jobs:
  test-automation:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        java-version: [11, 17, 21]
    steps:
      - uses: actions/setup-java@v4
        with:
          java-version: ${{ matrix.java-version }}
```

---

### Q: Can I run tests in parallel?

**A**: Yes! Update `testng.xml`:
```xml
<suite name="SentinelQA" parallel="tests" thread-count="4">
```

Also update workflow to allow parallel execution:
```yaml
# GitHub Actions already supports parallel jobs
jobs:
  ui-tests:
    # UI test job
  api-tests:
    # API test job (runs in parallel)
```

---

### Q: Can I trigger workflow on schedule?

**A**: Yes! Add schedule trigger:
```yaml
on:
  push:
    branches: [main]
  pull_request:
    branches: [main]
  schedule:
    # Run daily at 2 AM UTC
    - cron: '0 2 * * *'
  workflow_dispatch:
```

---

### Q: How do I add Slack notifications on failure?

**A**: Add Slack action:
```yaml
- name: 📢 Notify Slack on Failure
  if: failure()
  uses: slackapi/slack-github-action@v1
  with:
    webhook-url: ${{ secrets.SLACK_WEBHOOK }}
    payload: |
      {
        "text": "❌ SentinelQA tests failed",
        "blocks": [
          {
            "type": "section",
            "text": {
              "type": "mrkdwn",
              "text": "*Test Automation Failed*\nRun: ${{ github.run_id }}\nBranch: ${{ github.ref }}"
            }
          }
        ]
      }
```

First, add `SLACK_WEBHOOK` secret from your Slack workspace.

---

### Q: Can I upload artifacts to AWS S3?

**A**: Yes! Use AWS action:
```yaml
- name: 📤 Upload to S3
  uses: aws-actions/configure-aws-credentials@v1
  with:
    aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
    aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
    aws-region: us-east-1

- name: 📦 Push to S3
  run: |
    aws s3 cp target/allure-report/ s3://my-bucket/allure-reports/ --recursive
```

---

### Q: How do I handle credentials for API tests?

**A**: Use GitHub Secrets:
```yaml
# In workflow
env:
  API_USER: ${{ secrets.API_USER }}
  API_PASSWORD: ${{ secrets.API_PASSWORD }}

# In Java
String user = System.getenv("API_USER");
String password = System.getenv("API_PASSWORD");
```

Or use `.properties` file (excluded from git):
```properties
# src/main/resources/config.properties
api.user=${API_USER}
api.password=${API_PASSWORD}
```

---

### Q: How do I skip workflow on certain commits?

**A**: Use commit message to skip:
```bash
git commit -m "Update docs [skip ci]"
# This will skip the workflow
```

Or skip by file changes:
```yaml
on:
  push:
    branches: [main]
    paths-ignore:
      - 'docs/**'
      - '**.md'
      - '.gitignore'
```

---

### Q: Can I run workflow on PR approval?

**A**: Use pull_request_review trigger:
```yaml
on:
  pull_request_review:
    types: [submitted]
```

With condition:
```yaml
if: github.event.review.state == 'approved'
```

---

### Q: How do I fail the workflow on test failures?

**A**: Remove `continue-on-error: true`:
```yaml
- name: 🧪 Run Tests
  run: mvn test -B -V
  # Don't add continue-on-error: true
  # Workflow will fail if tests fail
```

---

### Q: Can I require GitHub Actions to pass before merging?

**A**: Yes! Set branch protection:
1. Settings → Branches → Add rule
2. Branch name pattern: `main`
3. Check "Require status checks to pass"
4. Select "SentinelQA Test Automation"
5. Save

---

## 🔍 How to Debug Workflow

### View Detailed Logs

1. Go to Actions tab
2. Click on the workflow run
3. Click on "Run Tests" step
4. See full command output

### Enable Debug Logging

```yaml
- name: 🧪 Run Tests
  run: mvn test -B -V -X  # -X for debug output
  env:
    OPENAI_API_KEY: ${{ env.OPENAI_API_KEY }}
```

### Check Environment Variables

```yaml
- name: Debug Env
  run: |
    echo "OPENAI_API_KEY length: ${#OPENAI_API_KEY}"
    echo "Java home: $JAVA_HOME"
    echo "Maven home: $MAVEN_HOME"
```

### Download Logs

1. Click workflow run
2. Click "⋯" (more options)
3. Select "Download logs"
4. Extract and review

---

## 📊 Workflow Performance Tips

| Optimization | Impact | Effort |
|--------------|--------|--------|
| Maven cache | 50-70% faster | Already configured ✓ |
| Skip tests on compile | 30% faster | Added with `-DskipTests` |
| Parallel test execution | 40-60% faster | Requires testng.xml update |
| Java matrix removed | 50% faster per run | Only if not needed |
| Skip artifact upload | 10% faster | Use if not needed |

---

## 🔒 Security Reminders

✅ **Always use GitHub Secrets** for sensitive data  
✅ **Never echo or print secrets** in logs  
✅ **Rotate keys regularly** (every 3-6 months)  
✅ **Use environment variables** to pass secrets to code  
✅ **Review permissions** on secrets (min required)  
✅ **Audit secret access** in GitHub logs  

---

## 📚 Resources

- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [Workflow Syntax](https://docs.github.com/en/actions/using-workflows/workflow-syntax-for-github-actions)
- [Secrets Management](https://docs.github.com/en/actions/security-guides/encrypted-secrets)
- [setup-java Action](https://github.com/actions/setup-java)
- [Maven Documentation](https://maven.apache.org/)

---

## 💬 Need More Help?

1. **Check the logs**: Most answers are in workflow logs
2. **Read GITHUB_ACTIONS_SETUP.md**: Detailed configuration guide
3. **Review workflow comments**: Each step is documented
4. **Check GitHub Actions docs**: Comprehensive documentation
5. **Create an issue**: GitHub will help troubleshoot

---

**Last Updated**: 2024  
**Workflow Version**: 1.0
