# Simple Logical Coupling

## Overview
This Kotlin application calculates the pairs of developers who most frequently contribute to the same files/modules in a GitHub repository.

## Instructions
1. Clone or download the repository to your local machine.

2. Ensure you have Kotlin installed on your system. If not, you can download it from https://kotlinlang.org/docs/command-line.html.

3. Open a terminal or command prompt and navigate to the directory where you cloned/downloaded the repository.

4. Compile the Kotlin code using the following Gradle command:


```bash
# Example installation commands
git clone https://github.com/mmangliyeva/LogCoupling.git
cd LogCoupling
./gradlew build
```

5. Run the application by executing the following command:
```bash
java -jar build/libs/LogCoupling-1.0-standalone.jar <owner> <repository> <token>
```
Replace <owner> <repository> <token> with owner of the repository, repository name and your Github token



