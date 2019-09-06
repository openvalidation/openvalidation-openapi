# Development Setup

### Build Requirements:
jdk 1.8.*  
Maven 3.4 or newer

### Installation

```bash
# clone the repo:
git clone https://github.com/openvalidation/openvalidation-openapi.git
# switch to the checkout
cd openvalidation-openapi
# compile and test the sources
mvn clean package
```

# Building and Testing
Build the project via Maven

```bash
mvn clean install
```

or

```bash
mvn clean package
```

# Coding Guidelines

## Logging
This project uses [`java.util.logging.Logger`](https://docs.oracle.com/javase/7/docs/api/java/util/logging/Logger.html) for displaying runtime information.

### Levels
- `Level.FINE`: Exception Stacktraces, Background Information for Exceptions in Tests
- `Level.SEVERE`: Parameter Informaton that caused exception, Exception Error Message
 
## Code Documentation
The project uses [these guidelines](/docs/javaDoc_guidelines.md) for *JavaDoc* comments in source files.

## Formatting

We use the [format-maven-plugin](https://github.com/coveooss/fmt-maven-plugin) during build which makes use of google-java-format to enforce consisten formatting across the codebase.

To trigger autoformat manually run `mvn com.coveo:fmt-maven-plugin:format` in the project root directory.

## Checkstyle

The [Checkstyle Maven Plugin](https://maven.apache.org/plugins/maven-checkstyle-plugin/index.html) can be run via `mvn checkstyle:check`.
It uses a [modified](build-tools/src/main/resources/google_checks.xml) [google java style](https://google.github.io/styleguide/javaguide.html) configuration.

## Currently Supported targets

- ov-java-spring-server
- ov-java-client
- ov-csharp-client
- ov-java-rules

## Additional Debug Options
```
# The following additional debug options are available for all codegen targets:
# -DdebugOpenAPI prints the OpenAPI Specification as interpreted by the codegen
# -DdebugModels prints models passed to the template engine
# -DdebugOperations prints operations passed to the template engine
# -DdebugSupportingFiles prints additional data passed to the template engine

java -DdebugOperations -cp /path/to/openapi-generator-cli.jar:/path/to/your.jar org.openapitools.codegen.OpenAPIGenerator generate -g my-codegen -i /path/to/openapi.yaml -o ./test
```

The above example will output the debug info for operations.
You can use this info in the `api.mustache` file.
