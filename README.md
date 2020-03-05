[![Build Status](https://dev.azure.com/validaria/openvalidation/_apis/build/status/openVALIDATION%20OpenAPI/openVALIDATION%20OpenAPI%20master?branchName=master)](https://dev.azure.com/validaria/openvalidation/_build/latest?definitionId=3&branchName=master)
![Azure DevOps tests (compact)](https://img.shields.io/azure-devops/tests/validaria/openvalidation/3?compact_message)
[![Maven Central](https://img.shields.io/maven-central/v/io.openvalidation/openvalidation-openapi-generator)](https://search.maven.org/search?q=g:io.openvalidation)
[![Follow us on Twitter](https://img.shields.io/twitter/follow/openVALIDATION?style=social)](https://twitter.com/openVALIDATION)


# OpenAPI Generator for openVALIDATION

This project provides an openapi-generator integration to generate services with validation logic provided by openVALIDATION.

## What's OpenAPI
The goal of OpenAPI is to define a standard, language-agnostic interface to REST APIs which allows both humans and computers to discover and understand the capabilities of the service without access to source code, documentation, or through network traffic inspection.
When properly described with OpenAPI, a consumer can understand and interact with the remote service with a minimal amount of implementation logic.
Similar to what interfaces have done for lower-level programming, OpenAPI removes the guesswork in calling the service.

Check out [OpenAPI-Spec](https://github.com/OAI/OpenAPI-Specification) for additional information about the OpenAPI project, including additional libraries with support for other languages and more. 

## What's openVALIDATION

Check out [openVALIDATION](https://github.com/openvalidation/openvalidation) to learn about human readable validation rules for software solutions.

## openVALIDATION OpenAPI Codegen Documentation

[Documentation](https://docs.openvalidation.io/openapi/openapi-specification)

[Tutorial](https://docs.openvalidation.io/openapi/openapi-tutorial)

## How do I use this?

Download the [openVALIDATION OpenAPI generator CLI](https://downloadarchive.blob.core.windows.net/openvalidation-openapi-generator/ov-openapi-generator-cli.jar) (requires the Java SE 8 runtime environment).
Use `ov-openapi-generator-cli.jar` as a drop-in replacement for the `openapi-generator-cli.jar`

Now you can use openVALIDATION rules in your service contract:
```yaml
paths:
  /: 
   post:
      requestBody:
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/applicant'
            x-ov-rules:
              culture: en
              rule: |
                the location of the applicant must be Dortmund
      responses:
        '200':
          description: success
components:
  schemas:
    applicant:
      type: object
      properties:
        name:
          type: string
        age:
          type: integer
        location:
          type: string
```
*(For further details check the above-mentioned documentation or the openVALIDATION project itself.)*

Alternatively, the `openvalidation-openapi-generator` jar itself can be called in combination with the OpenAPI generator.

For mac/linux:
```
java -cp /path/to/openapi-generator-cli.jar:/path/to/openvalidation-openapi-generator.jar org.openapitools.codegen.OpenAPIGenerator generate -g ov-java-spring-server -i /path/to/openapi.yaml -o ./test
```
(Do not forget to replace the values `/path/to/openapi-generator-cli.jar`, `/path/to/openvalidation-openapi-generator.jar` and `/path/to/openapi.yaml` in the previous command)

For Windows users, you will need to use `;` instead of `:` in the classpath, e.g.
```
java -cp /path/to/openapi-generator-cli.jar;/path/to/openvalidation-openapi-generator.jar org.openapitools.codegen.OpenAPIGenerator generate -g ov-java-spring-server -i /path/to/openapi.yaml -o ./test
```

Or use openVALIDATION Generator as Maven Plugin:
```xml
    <build>
        <plugins>
            <plugin>
                <groupId>org.openapitools</groupId>
                <artifactId>openapi-generator-maven-plugin</artifactId>
                <version>3.3.4</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>generate</goal>
                        </goals>
                        <configuration>
                            <inputSpec>${project.basedir}/my.spec.yaml</inputSpec>
                            <generatorName>ov-java-rules</generatorName> <!- rules only generation -->
                            <configOptions>
                                <invokerPackage>my.custom.package</invokerPackage>
                                <modelPackage>my.custom.package.model</modelPackage>
                            </configOptions>
                        </configuration>
                    </execution>
                </executions>
                <dependencies>
                    <dependency>
                        <groupId>io.openvalidation</groupId>
                        <artifactId>openvalidation-openapi-generator</artifactId>
                        <version>0.0.1</version>
                    </dependency>
                </dependencies>
            </plugin>
        </plugins>
    </build>
```


## Getting involved

Please refer to our [contribution guidelines](CONTRIBUTING.md).

## Contact

You can write an [E-Mail](mailto:validaria@openvalidation.io) or mention our twitter account [@openVALIDATION](https://twitter.com/openvalidation).
