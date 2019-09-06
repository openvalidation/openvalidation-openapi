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

Download the [openVALIDATION OpenAPI generator CLI](https://repo1.maven.org/maven2/io/openvalidation/openvalidation-openapi-generator/0.0.1/ov-openapi-generator.jar) (requires the Java SE 8 runtime environment).
Use `ov-openapi-generator-cli.jar` is a drop-in replacement for the `openapi-generator-cli.jar`

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
java -cp /path/to/openapi-generator-cli.jar:/path/to/openvalidation-openapi-generator.jar org.openapitools.codegen.OpenAPIGenerator generate -g my-codegen -i /path/to/openapi.yaml -o ./test
```
(Do not forget to replace the values `/path/to/openapi-generator-cli.jar`, `/path/to/openvalidation-openapi-generator.jar` and `/path/to/openapi.yaml` in the previous command)

For Windows users, you will need to use `;` instead of `:` in the classpath, e.g.
```
java -cp /path/to/openapi-generator-cli.jar;/path/to/openvalidation-openapi-generator.jar org.openapitools.codegen.OpenAPIGenerator generate -g my-codegen -i /path/to/openapi.yaml -o ./test
```

## Getting involved

Please refer to our [contribution guidelines](CONTRIBUTING.md).

## Contact

You can write an [E-Mail](mailto:validaria@openvalidation.io) or mention our twitter account [@Validaria_](https://twitter.com/validaria_).
