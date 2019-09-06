# How to integrate generated Rules?


With "ov-xxx-rules" generator, only the corresponding set of rules in the respective programming language 
is generated. In this form of generation, any dependencies to any specific framework are waived. 
This code can be used in a service as well as in the client. 

Below you will find various implementation examples. 
 
 
## Service integration

 
Since the generated code does not contain any framework specifics, it needs a corresponding integration, 
depending on the used framework. In the following you will find different services Implementation examples.
 
### Java Spring Boot


The first thing we need is a Spring Adapter that acts as a connector between Spring Service and the generated 
set of rules. And this is what a Spring Adapter looks like:

OVSpringValdator.java


 
 
     package io.openvalidation.spring.validation;
     
     import org.springframework.stereotype.Component;
     import org.springframework.validation.Errors;
     import org.springframework.validation.Validator;
     import org.springframework.web.context.request.RequestAttributes;
     import org.springframework.web.context.request.RequestContextHolder;
     import org.springframework.web.context.request.ServletRequestAttributes;
     
     import javax.servlet.http.HttpServletRequest;
     
     @Component
     public class OVSpringValdator implements Validator {
     
         @Override
         public boolean supports(Class<?> aClass) { return true; }
     
         @Override
         public void validate(Object target, Errors errors) {
             OpenValidatorFactory validatorFactory = new OpenValidatorFactory();
     
             OVFramework.IOpenValidator validator = validatorFactory.create(getCurrentOperationID());
     
             if (validator != null){
                 OVFramework.OpenValidationSummary result = validator.validate(target);
                 if (result.hasErrors()){
                     for(OVFramework.OpenValidationSummaryError error :result.getErrors()){
     
                         if (error.getFields() != null && error.getFields().length > 0) {
                             for(String field : error.getFields()){
                                 if(errors.hasFieldErrors(field)){
                                     errors.rejectValue(field, null, error.getError());
                                 }
                                 else
                                 {
                                     errors.reject(null, field +" : " + error.getError());
                                 }
                             }
                         }
                         else
                         {
                             errors.reject(error.getError());
                         }
     
                     }
                 }
             }
         }
     
         public String getCurrentOperationID(){
             HttpServletRequest request = getCurrentRequest();
     
             return  (request.getServletPath() + "/" + request.getMethod()).toLowerCase();
         }
     
         public static HttpServletRequest getCurrentRequest() {
             RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
             HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
     
             return servletRequest;
         }
     }
 


Our adapter must now only be registered in the respective ApiController and be passed to the WebDataBinder:
 
    package io.openvalidation.spring.api;
    
    import org.springframework.stereotype.Controller;
    import org.springframework.web.bind.annotation.RequestMapping;
    import java.util.Optional;
     
    
    @Controller
    @RequestMapping()
    public class MyApiController implements MyApi {

        ...

        @org.springframework.beans.factory.annotation.Autowired
        io.openvalidation.spring.validation.OVSpringValdator ovSpringValdator;


        @org.springframework.web.bind.annotation.InitBinder
        public void setupBinder(org.springframework.web.bind.WebDataBinder binder) {
            binder.addValidators(ovSpringValdator);
        }
        
    }
 
 
 
## Client integration