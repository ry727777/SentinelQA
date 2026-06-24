# ⚡ Quick Start: GitHub Actions CI Setup

## 📋 What Was Created

| File | Purpose |
|------|---------|
| `.github/workflows/automation.yml` | Main GitHub Actions workflow file |
| `.github/GITHUB_ACTIONS_SETUP.md` | Detailed setup and troubleshooting guide |

---

## 🚀 Quick Setup (5 Steps)

### 1️⃣ **Commit the Workflow File**
```bash
git add .github/workflows/automation.yml
git add .github/GITHUB_ACTIONS_SETUP.md
git commit -m "Add GitHub Actions CI workflow"
git push origin main
```

### 2️⃣ **Configure GitHub Secret**
1. Go to GitHub repository → **Settings**
2. Left sidebar: **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Name: `OPENAI_API_KEY`
5. Value: Your API key (from Google AI Studio)
6. Click **Add secret**

### 3️⃣ **Verify Workflow**
1. Go to **Actions** tab on GitHub
2. You should see "SentinelQA Test Automation" workflow
3. Click on it to view the runs

### 4️⃣ **Download Test Reports**
After workflow runs:
1. Click on the workflow run
2. Scroll to **Artifacts** section
3. Download the test reports

### 5️⃣ **View Test Results**
```bash
# Extract and view Allure report
unzip allure-report-*.zip
cd target/allure-report
open index.html
```

---

## 🔐 Environment Variable Access

Your Java code can access the API key:

```java
// In your Java code
String apiKey = System.getenv("OPENAI_API_KEY");
if (apiKey != null && !apiKey.isEmpty()) {
    System.out.println("✓ API Key loaded from environment");
}
```

---

## ✅ Workflow Features

✅ **Automatically Triggers On**:
- Push to `main` branch
- Pull requests targeting `main`
- Manual execution via GitHub Actions UI

✅ **Steps Included**:
1. Checkout code
2. Install Java 17 (Temurin)
3. Cache Maven dependencies
4. Configure environment variables
5. Compile project
6. Run tests
7. Upload test reports as artifacts
8. Generate Allure report
9. Create workflow summary

✅ **Security**:
- No hardcoded secrets
- Uses GitHub Secrets for API keys
- Secrets never exposed in logs
- Environment variable passing via `System.getenv()`

✅ **Reports Uploaded**:
- TestNG reports (`target/surefire-reports/`)
- Test output (`test-output/`)
- Allure results (`allure-results/`)
- Detailed Allure report (`target/allure-report/`)

---

## 🔄 Workflow Triggers

### Push to Main
```bash
git push origin main
# Workflow automatically triggers
```

### Pull Request
```bash
git push origin feature-branch
# Create PR on GitHub targeting main
# Workflow automatically runs
```

### Manual Trigger
1. Go to **Actions** tab
2. Select **SentinelQA Test Automation**
3. Click **Run workflow**
4. Click **Run workflow** button

---

## 📊 Accessing Results

### From GitHub UI
1. Actions → SentinelQA Test Automation
2. Click latest run
3. Scroll to **Artifacts** section
4. Download reports

### Locally After Download
```bash
# TestNG reports
cat target/surefire-reports/index.html

# Allure report
unzip allure-report-*.zip
cd target/allure-report
python -m http.server 8000  # Open http://localhost:8000
```

---

## 🐛 Quick Troubleshooting

| Issue | Solution |
|-------|----------|
| Workflow not running | Push workflow file to `main` branch first |
| API key not found | Configure `OPENAI_API_KEY` secret in GitHub Settings |
| Tests fail in CI but pass locally | Check logs in GitHub Actions UI → download artifacts |
| Java version mismatch | Update `JAVA_VERSION` in workflow and `pom.xml` |
| Maven cache not working | Cache is rebuilt when `pom.xml` changes (expected) |

---

## 📁 Project Structure

```
SentinelQA/
├── .github/
│   ├── workflows/
│   │   └── automation.yml ✨ NEW
│   └── GITHUB_ACTIONS_SETUP.md ✨ NEW
├── src/
├── target/
├── pom.xml
└── README.md
```

---

## 🔍 What Happens in the Workflow

```
Push to main branch
    ↓
GitHub detects push
    ↓
Workflow starts (automation.yml)
    ↓
Step 1: Checkout code
Step 2: Install Java 17 (Temurin)
Step 3: Cache Maven dependencies
Step 4: Configure OPENAI_API_KEY environment variable
Step 5: Display Java/Maven versions
Step 6: mvn clean compile
Step 7: mvn test (with API key available)
Step 8: Upload test reports
Step 9: Generate Allure report
Step 10: Upload Allure report
Step 11: Generate summary
    ↓
Artifacts available for download
    ↓
View results in GitHub Actions UI
```

---

## 🎯 Next Steps

1. ✅ Push workflow files to repository
2. ✅ Configure `OPENAI_API_KEY` secret
3. ✅ Make a commit to `main` to trigger workflow
4. ✅ Check Actions tab to verify workflow runs
5. ✅ Download and review test reports

---

## 📚 Important Files

### Workflow Configuration
- **File**: `.github/workflows/automation.yml`
- **Description**: Main GitHub Actions workflow
- **Contains**: All CI/CD steps and configurations

### Setup Documentation
- **File**: `.github/GITHUB_ACTIONS_SETUP.md`
- **Description**: Detailed setup guide with troubleshooting
- **Contains**: Step-by-step instructions, security best practices, customization guide

### Project Configuration
- **File**: `pom.xml`
- **Status**: Already configured ✓
- **Note**: Uses Java 17, Maven 3.8.7

---

## 💡 Pro Tips

1. **Faster builds**: Maven cache kicks in after first run (~2-3 min → ~30 sec)
2. **Manual test**: Go to Actions → Run workflow → Run workflow
3. **Custom secrets**: Add more secrets in GitHub Settings
4. **Multi-branch CI**: Duplicate workflow for other branches (e.g., `develop`)
5. **Slack notifications**: Add Slack action for notifications on failure

---

## 🔒 Security Checklist

- ✅ No secrets hardcoded in workflow file
- ✅ Using GitHub Secrets for API keys
- ✅ Environment variable passed securely to tests
- ✅ Secrets never logged or printed
- ✅ Limited permissions in workflow
- ✅ `.gitignore` configured properly

---

## 📞 Getting Help

1. **Workflow logs**: Actions → Click run → View logs
2. **Detailed guide**: Read `.github/GITHUB_ACTIONS_SETUP.md`
3. **GitHub docs**: https://docs.github.com/en/actions
4. **Maven docs**: https://maven.apache.org/

---

## ✨ Summary

Your SentinelQA framework now has **production-ready GitHub Actions CI** that:
- Automatically builds and tests on every push/PR
- Securely manages API keys via GitHub Secrets
- Collects comprehensive test reports
- Provides detailed Allure test dashboards
- Follows GitHub Actions best practices

**Status**: ✅ Ready to use!

Next: Push to GitHub and watch the magic happen! 🚀
