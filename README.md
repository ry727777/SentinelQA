# 🔍 SentinelQA - AI-Powered Test Automation Framework

> Intelligent Sentinel for Quality Assurance with AI-Driven Root Cause Analysis

![Java](https://img.shields.io/badge/Java-17-orange?logo=java)
![Maven](https://img.shields.io/badge/Maven-3.8+-blue?logo=apache-maven)
![Selenium](https://img.shields.io/badge/Selenium-4.15.0-brightgreen?logo=selenium)
![TestNG](https://img.shields.io/badge/TestNG-7.11.0-lightblue)
![Allure Reports](https://img.shields.io/badge/Allure-Reports-yellow?logo=allure)
![AI Powered](https://img.shields.io/badge/AI%20Powered-Gemini-red)

## 📋 Overview

**SentinelQA** is a comprehensive test automation framework that combines traditional Selenium UI testing and REST API testing with **AI-powered intelligent failure analysis** using Google Gemini. 

When tests fail, instead of spending hours debugging, SentinelQA automatically analyzes the failure and provides:
- 🤖 **Root Cause Analysis (RCA)** powered by AI
- 📊 **Failure Categorization** (Locator Issues, Timing Issues, API Auth Issues, etc.)
- 💡 **Actionable Recommendations** for fixing the issue
- 📸 **Visual Evidence** with screenshots attached to Allure reports

### Key Features

✅ **Hybrid Testing Approach**
- UI Testing using Selenium WebDriver 4.15.0
- REST API Testing using REST Assured 5.3.2
- Cross-browser support (Chrome, Firefox, Edge)

✅ **AI-Powered Failure Intelligence**
- Automatic root cause analysis using Google Gemini API
- Graceful fallback analysis when AI is unavailable
- Pattern-based failure categorization
- Confidence scoring for accuracy

✅ **Enterprise-Grade Reporting**
- Allure Reports with detailed failure insights
- Screenshots and logs attached to every failure
- AI analysis embedded in test reports
- Real-time test execution dashboard

✅ **Production-Ready Framework**
- Comprehensive logging using Log4j2
- WebDriver Manager for automatic driver management
- TestNG test execution with listener support
- Configurable properties for different environments

---

## 🚀 Quick Start

### Prerequisites

- **Java 17 or higher**
- **Maven 3.8+**
- **Google Gemini API Key** (for AI analysis - optional but recommended)
- **Chrome/Firefox/Edge Browser** (with appropriate driver)

### Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/ry727777/SentinelQA.git
   cd SentinelQA
   ```

2. **Install Dependencies**
   ```bash
   mvn clean install
   ```

3. **Set Up Gemini API Key** (for AI-powered analysis)
   ```bash
   # Windows (PowerShell)
   $env:GEMINI_API_KEY = "your_google_gemini_api_key_here"
   
   # macOS/Linux
   export GEMINI_API_KEY=your_google_gemini_api_key_here
   ```
   
   > Get your free Gemini API key from [Google AI Studio](https://aistudio.google.com/app/apikey)

4. **Run Tests**
   ```bash
   # Run all tests
   mvn clean test
   
   # Run specific test suite
   mvn clean test -Dgroups=ui
   mvn clean test -Dgroups=api
   ```

5. **View Allure Report**
   ```bash
   # Generate report
   mvn allure:report
   
   # Serve report locally
   mvn allure:serve
   ```

---

## 📁 Project Structure

```
SentinelQA/
├── src/
│   ├── main/
│   │   ├── java/com/rahul/
│   │   │   └── framework/
│   │   │       ├── ai/
│   │   │       │   ├── FailureAnalyzer.java      # AI-powered root cause analysis
│   │   │       │   ├── GeminiClient.java         # Google Gemini API integration
│   │   │       │   └── AIPromptTemplates.java    # AI prompt templates
│   │   │       ├── base/
│   │   │       │   ├── BaseTest.java             # UI test base class
│   │   │       │   └── BaseApiTest.java          # API test base class
│   │   │       ├── config/
│   │   │       │   ├── DriverManager.java        # Selenium WebDriver management
│   │   │       │   └── ConfigReader.java         # Configuration properties reader
│   │   │       ├── constants/
│   │   │       │   └── AppConstants.java         # Application constants
│   │   │       ├── pages/
│   │   │       │   ├── LoginPage.java            # Page Object Model for login
│   │   │       │   ├── CartPage.java             # Page Object Model for cart
│   │   │       │   ├── LogoutPage.java           # Page Object Model for logout
│   │   │       │   └── SortingCartPageItems.java # Page Object Model for sorting
│   │   │       ├── listeners/
│   │   │       │   └── TestListener.java         # TestNG listener for AI analysis
│   │   │       └── utils/
│   │   │           ├── LoggerUtil.java           # Logging utility
│   │   │           └── ScreenshotUtil.java       # Screenshot capture utility
│   │   └── resources/
│   │       ├── config.properties                 # Configuration file
│   │       └── log4j2.xml                        # Log4j2 configuration
│   └── test/
│       ├── java/com/rahul/tests/
│       │   ├── ui/
│       │   │   ├── LoginTest.java                # UI test for login
│       │   │   ├── LogoutTest.java               # UI test for logout
│       │   │   ├── AddToCartTest.java            # UI test for add to cart
│       │   │   ├── RemoveItemsTest.java          # UI test for remove items
│       │   │   └── CartPageSortingTest.java      # UI test for sorting
│       │   ├── api/
│       │   │   ├── AuthApiTest.java              # API test for authentication
│       │   │   └── UserApiTest.java              # API test for user operations
│       │   ├── base/
│       │   │   ├── BaseTest.java                 # Test base class
│       │   │   └── BaseApiTest.java              # API test base class
│       │   └── listeners/
│       │       └── TestListener.java             # Test event listener
├── pom.xml                                       # Maven configuration
├── testng.xml                                    # TestNG test suite configuration
├── allure-results/                               # Allure test results (auto-generated)
├── target/                                       # Maven build output
└── README.md                                     # This file
```

---

## 🤖 AI-Powered Root Cause Analysis

### How It Works

When a test fails, SentinelQA automatically:

1. **Captures Failure Details**: Test name, stack trace, and screenshot
2. **Invokes AI Analysis**: Sends failure context to Google Gemini API
3. **Receives Insights**: AI returns probable root cause and recommended fixes
4. **Reports Results**: Embeds AI analysis in Allure report

### Failure Categories

The framework intelligently categorizes failures into:

| Category | Description | Example |
|----------|-------------|---------|
| **LOCATOR ISSUE** | Element not found / selector changed | `NoSuchElementException` |
| **TIMING ISSUE** | Element not ready / async operation incomplete | `TimeoutException`, `Wait.until` |
| **API AUTH/CONFIG** | API authentication or configuration issue | `401 Unauthorized`, Missing API key |
| **ASSERTION MISMATCH** | Expected value doesn't match actual | Assertion failures |
| **ENVIRONMENT ISSUE** | Network, service, or browser connectivity problem | `Connection refused`, `UnknownHostException` |
| **TEST BUG** | Issue in test code, not application | `NullPointerException`, `IllegalStateException` |

### Example AI Analysis in Report

```
AI ROOT CAUSE ANALYSIS
Analysis Mode: Gemini API
AI Service Status: Success
Category: LOCATOR ISSUE
Confidence: High

Probable Root Cause:
The failure is most likely caused by a changed, incorrect, or stale Selenium 
locator. Selenium could not find the target element while executing 'LoginTest'. 
Start by checking the locator used in LoginPage.

Recommended Fix:
1. Open LoginPage and verify the locator against the current DOM
2. Prefer stable attributes such as id, data-test, or data-testid
3. Rerun the failed test and confirm the element is found
```

### Graceful Fallback

If the Gemini API is unavailable or misconfigured, the framework automatically:
- Falls back to **local pattern-based analysis**
- Extracts exception types and stack traces
- Provides category-based recommendations
- Never blocks test execution

```java
// No setup needed for basic framework functionality
// AI analysis is completely optional
mvn test  // Works even without GEMINI_API_KEY set
```

---

## 🔧 Configuration

### config.properties

Located at `src/main/resources/config.properties`

```properties
# SauceDemo URLs and Configuration
base.url=https://www.saucedemo.com
ui.timeout=10

# REST API Configuration
api.base.url=https://reqres.in
api.timeout=5
api.key=your_api_key_here

# Browser Configuration
browser.name=chrome          # Options: chrome, firefox, edge
headless.mode=false          # Set to true for headless execution

# Logging
log.level=INFO               # Options: DEBUG, INFO, WARN, ERROR

# Credentials
username=standard_user
password=secret_sauce
```

### Environment Variables

```bash
# For AI-powered analysis (recommended)
export GEMINI_API_KEY=your_google_gemini_api_key

# For other configurations (optional)
export BROWSER=chrome
export HEADLESS=false
export LOG_LEVEL=INFO
```

---

## 📊 Running Tests

### Run All Tests

```bash
mvn clean test
```

### Run Specific Test Classes

```bash
# Run UI tests only
mvn test -Dtest=LoginTest,AddToCartTest

# Run API tests only
mvn test -Dtest=AuthApiTest,UserApiTest
```

### Run with Custom Configuration

```bash
# Run with Firefox browser
mvn test -Dbrowser=firefox

# Run in headless mode
mvn test -Dheadless=true

# Run with DEBUG logging
mvn test -Dlog.level=DEBUG
```

### TestNG Configuration

Tests are configured in `testng.xml`:

```xml
<suite name="SentinelQA Test Suite" parallel="false" thread-count="1">
    <listeners>
        <listener class-name="com.rahul.tests.listeners.TestListener" />
    </listeners>
    
    <test name="UI Tests">
        <classes>
            <class name="com.rahul.tests.ui.LoginTest"/>
            <!-- More UI tests -->
        </classes>
    </test>
    
    <test name="API Tests">
        <classes>
            <class name="com.rahul.tests.api.AuthApiTest"/>
            <!-- More API tests -->
        </classes>
    </test>
</suite>
```

---

## 📈 Allure Reports

### Generate Allure Report

```bash
# Clean previous results
mvn clean

# Run tests (results go to target/allure-results)
mvn test

# Generate report
mvn allure:report

# Serve report locally (opens browser at http://localhost:8080)
mvn allure:serve
```

### What You'll See in Reports

✅ **Test Execution Timeline** - Visual timeline of test execution  
✅ **AI Root Cause Analysis** - Every failed test has AI-powered insights  
✅ **Screenshots** - Visual evidence of failures  
✅ **Detailed Logs** - Full execution logs for debugging  
✅ **Categorized Failures** - Tests grouped by failure type  
✅ **Test Statistics** - Pass/fail rates and trends  

---

## 🛠️ Architecture & Design Patterns

### Page Object Model (POM)

The framework follows the Page Object Model pattern for maintainability:

```java
// LoginPage.java
public class LoginPage {
    private WebDriver driver;
    private By usernameField = By.id("user-name");
    private By passwordField = By.id("password");
    private By loginButton = By.id("login-button");
    
    public void login(String username, String password) {
        driver.findElement(usernameField).sendKeys(username);
        driver.findElement(passwordField).sendKeys(password);
        driver.findElement(loginButton).click();
    }
}
```

### Base Test Classes

- **BaseTest**: Handles UI test setup/teardown, driver initialization
- **BaseApiTest**: Handles REST API client initialization and common assertions

### Listener Pattern

TestNG listeners automatically trigger AI analysis on test failures:

```java
@Override
public void onTestFailure(ITestResult result) {
    String testName = result.getMethod().getMethodName();
    String stackTrace = Arrays.toString(result.getThrowable().getStackTrace());
    String analysis = failureAnalyzer.analyzeFailure(testName, stackTrace);
    // Embed analysis in Allure report
}
```

---

## 🧪 Sample Test Cases

### UI Test Example

```java
@Test
public void testLoginWithValidCredentials() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.login("standard_user", "secret_sauce");
    
    CartPage cartPage = new CartPage(driver);
    Assert.assertTrue(cartPage.isInventoryDisplayed(), 
        "Inventory should be displayed after login");
}
```

### API Test Example

```java
@Test
public void testUserLogin() {
    Response response = given()
        .contentType(ContentType.JSON)
        .body(loginPayload)
    .when()
        .post(Endpoints.LOGIN);
    
    Assert.assertEquals(response.getStatusCode(), 200);
    String token = response.jsonPath().getString("token");
    Assert.assertNotNull(token, "Token should not be null");
}
```

---

## 📝 Logging

The framework uses **Log4j2** for comprehensive logging. Configuration is in `src/main/resources/log4j2.xml`.

### Log Levels

| Level | Use Case |
|-------|----------|
| **DEBUG** | Detailed diagnostic information |
| **INFO** | General informational messages |
| **WARN** | Warning messages (potential issues) |
| **ERROR** | Error messages (failures) |

### Enable Debug Logging

```bash
# In config.properties
log.level=DEBUG

# Or via Maven
mvn test -Dlog.level=DEBUG
```

---

## 🔐 Security

- **API Keys**: Store in environment variables, not in code
- **Credentials**: Use config.properties or environment variables
- **Sensitive Data**: Never log passwords or tokens
- **HTTPS Only**: All API endpoints use HTTPS

### Example: Safe Configuration

```bash
# ✅ CORRECT - Use environment variables
export API_KEY=secret_key_123
export GEMINI_API_KEY=google_key_here

# ❌ WRONG - Never hardcode secrets in code or config files
api.key=secret_key_123  # NEVER DO THIS
```

---

## 🐛 Troubleshooting

### Issue: Tests not running

```bash
# Verify Java version
java -version  # Should be 17+

# Clean and rebuild
mvn clean compile

# Check TestNG configuration
cat testng.xml
```

### Issue: WebDriver not found

```bash
# WebDriverManager automatically downloads drivers
# If issues persist, ensure Chrome/Firefox/Edge is installed

# Run with verbose output
mvn test -X
```

### Issue: AI Analysis not working

```bash
# Verify Gemini API key is set
echo $GEMINI_API_KEY  # Should print your API key

# Framework works without AI key (fallback to local analysis)
# Set log level to DEBUG to see what's happening
mvn test -Dlog.level=DEBUG
```

### Issue: Allure report not generating

```bash
# Ensure Allure is installed
allure --version

# Clear old results
rm -rf target/allure-results/

# Run tests and generate report
mvn clean test allure:report
```

---

## 🤝 Contributing

Contributions are welcome! To contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📚 Technology Stack

| Technology | Version | Purpose |
|-----------|---------|---------|
| **Java** | 17 | Primary language |
| **Selenium WebDriver** | 4.15.0 | UI automation |
| **REST Assured** | 5.3.2 | API testing |
| **TestNG** | 7.11.0 | Test framework |
| **Allure** | 2.21.0 | Reporting |
| **Log4j2** | 2.21.0 | Logging |
| **WebDriverManager** | 5.6.3 | Driver management |
| **Google Gemini** | 2.5 Flash | AI analysis |
| **GSON** | 2.10.1 | JSON parsing |

---

## 📖 Resources

- [Selenium Documentation](https://www.selenium.dev/documentation/)
- [TestNG Documentation](https://testng.org/)
- [REST Assured Documentation](https://rest-assured.io/)
- [Allure Reports](https://docs.qameta.io/allure/)
- [Google Gemini API](https://ai.google.dev/)
- [Maven Documentation](https://maven.apache.org/)

---

## 🎯 Roadmap

- [ ] Integration with CI/CD pipelines (Jenkins, GitHub Actions)
- [ ] Database testing support
- [ ] Performance testing modules
- [ ] Multi-language support for AI analysis
- [ ] Real-time dashboard with test metrics
- [ ] Parallel test execution optimization
- [ ] Integration with Slack/Teams notifications

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👤 Author

**Rahul** - [GitHub Profile](https://github.com/ry727777)

---

## 📞 Support & Contact

For issues, questions, or suggestions:

- **GitHub Issues**: [Create an issue](https://github.com/ry727777/SentinelQA/issues)
- **Email**: Contact via GitHub profile
- **Documentation**: Check README and code comments

---

## 🙏 Acknowledgments

- Inspired by modern QA frameworks and AI-driven testing methodologies
- Thanks to the open-source community for excellent libraries
- Special thanks to Google for Gemini API

---

**⭐ If you find this project helpful, please give it a star! ⭐**

---

## Summary

**SentinelQA** combines the power of:
- 🌐 **Robust Test Automation** (Selenium + REST Assured)
- 📊 **Comprehensive Reporting** (Allure)
- 🤖 **AI Intelligence** (Google Gemini)

To deliver **faster root cause analysis** and **smarter debugging** for your QA team.

Start automating with intelligence today! 🚀