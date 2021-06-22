package org.upgrad.upstac.testrequests;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.testrequests.TestRequest;
import org.upgrad.upstac.testrequests.consultation.ConsultationController;
import org.upgrad.upstac.testrequests.consultation.CreateConsultationRequest;
import org.upgrad.upstac.testrequests.consultation.DoctorSuggestion;
import org.upgrad.upstac.testrequests.lab.CreateLabResult;
import org.upgrad.upstac.testrequests.lab.LabResult;
import org.upgrad.upstac.testrequests.lab.TestStatus;
import org.upgrad.upstac.testrequests.RequestStatus;
import org.upgrad.upstac.testrequests.TestRequestQueryService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Slf4j
class ConsultationControllerTest {


    @Autowired
    ConsultationController consultationController;


    @Autowired
    TestRequestQueryService testRequestQueryService;


    @Test
    @WithUserDetails(value = "doctor")
    public void calling_assignForConsultation_with_valid_test_request_id_should_update_the_request_status(){

        TestRequest testRequest = getTestRequestByStatus(RequestStatus.LAB_TEST_COMPLETED);

        //Implement this method

        //Create another object of the TestRequest method and explicitly assign this object for Consultation using assignForConsultation() method
        // from consultationController class. Pass the request id of testRequest object.
        TestRequest anotherRequest = consultationController.assignForConsultation(testRequest.getRequestId());

        //Use assertThat() methods to perform the following two comparisons
        //  1. the request ids of both the objects created should be same
        assertThat(testRequest.getRequestId(), equalTo(anotherRequest.getRequestId()));
        //  2. the status of the second object should be equal to 'DIAGNOSIS_IN_PROCESS'
        assertThat(anotherRequest.getStatus(), equalTo(RequestStatus.DIAGNOSIS_IN_PROCESS));
        // make use of assertNotNull() method to make sure that the consultation value of second object is not null
        // use getConsultation() method to get the lab result
        assertNotNull(anotherRequest.getConsultation());
    }

    public TestRequest getTestRequestByStatus(RequestStatus status) {
        return testRequestQueryService.findBy(status).stream().findFirst().get();
    }

    @Test
    @WithUserDetails(value = "doctor")
    public void calling_assignForConsultation_with_valid_test_request_id_should_throw_exception(){

        Long InvalidRequestId= -34L;

        //Implement this method


        // Create an object of ResponseStatusException . Use assertThrows() method and pass assignForConsultation() method
        // of consultationController with InvalidRequestId as Id
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> {
            consultationController.assignForConsultation(InvalidRequestId);
        });


        //Use assertThat() method to perform the following comparison
        //  the exception message should be contain the string "Invalid ID"
        assertThat(responseStatusException.getMessage(), containsString("Invalid ID"));

    }

    @Test
    @WithUserDetails(value = "doctor")
    public void calling_updateConsultation_with_valid_test_request_id_should_update_the_request_status_and_update_consultation_details(){

        TestRequest testRequest = getTestRequestByStatus(RequestStatus.DIAGNOSIS_IN_PROCESS);

        //Implement this method
        //Create an object of CreateConsultationRequest and call getCreateConsultationRequest() to create the object. Pass the above created object as the parameter
        CreateConsultationRequest createConsultationRequest = getCreateConsultationRequest(testRequest);
        //Create another object of the TestRequest method and explicitly update the status of this object
        // to be 'COMPLETED'. Make use of updateConsultation() method from consultationController class
        // (Pass the previously created two objects as parameters)
        // (for the object of TestRequest class, pass its ID using getRequestId())
        TestRequest anotherRequest =  consultationController.updateConsultation(testRequest.getRequestId(),createConsultationRequest);
        //anotherRequest.setStatus(RequestStatus.COMPLETED);

        //Use assertThat() methods to perform the following three comparisons
        //  1. the request ids of both the objects created should be same
        assertThat(anotherRequest.getRequestId(),equalTo(testRequest.getRequestId()));
        //  2. the status of the second object should be equal to 'COMPLETED'
        assertThat(anotherRequest.getStatus(),equalTo(RequestStatus.COMPLETED));
        // 3. the suggestion of both the objects created should be same. Make use of getSuggestion() method to get the results.
        assertThat(anotherRequest.getConsultation().getSuggestion(),equalTo(createConsultationRequest.getSuggestion()));

    }


    @Test
    @WithUserDetails(value = "doctor")
    public void calling_updateConsultation_with_invalid_test_request_id_should_throw_exception(){

        TestRequest testRequest = getTestRequestByStatus(RequestStatus.DIAGNOSIS_IN_PROCESS);

        //Implement this method

        //Create an object of CreateConsultationRequest and call getCreateConsultationRequest() to create the object. Pass the above created object as the parameter
        CreateConsultationRequest createConsultationRequest = getCreateConsultationRequest(testRequest);
        // Create an object of ResponseStatusException . Use assertThrows() method and pass updateConsultation() method
        // of consultationController with a negative long value as Id and the above created object as second parameter
        //Refer to the TestRequestControllerTest to check how to use assertThrows() method
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,()->{
            consultationController.updateConsultation(-98L,createConsultationRequest);

        });

        //Use assertThat() method to perform the following comparison
        //  the exception message should be contain the string "Invalid ID"
        assertThat(responseStatusException.getMessage(), containsString("Invalid ID"));

    }

    @Test
    @WithUserDetails(value = "doctor")
    public void calling_updateConsultation_with_invalid_empty_status_should_throw_exception(){

        TestRequest testRequest = getTestRequestByStatus(RequestStatus.DIAGNOSIS_IN_PROCESS);

        //Implement this method

        //Create an object of CreateConsultationRequest and call getCreateConsultationRequest() to create the object. Pass the above created object as the parameter
        // Set the suggestion of the above created object to null.
        CreateConsultationRequest createConsultationRequest = getCreateConsultationRequest(testRequest);
        createConsultationRequest.setSuggestion(null);
        // Create an object of ResponseStatusException . Use assertThrows() method and pass updateConsultation() method
        // of consultationController with request Id of the testRequest object and the above created object as second parameter
        //Refer to the TestRequestControllerTest to check how to use assertThrows() method
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,()->{
            consultationController.updateConsultation(testRequest.getRequestId(),createConsultationRequest);

        });

    }

    public CreateConsultationRequest getCreateConsultationRequest(TestRequest testRequest) {

        //Create an object of CreateLabResult and set all the values
        // if the lab result test status is Positive, set the doctor suggestion as "HOME_QUARANTINE" and comments accordingly
        // else if the lab result status is Negative, set the doctor suggestion as "NO_ISSUES" and comments as "Ok"
        // Return the object
        CreateConsultationRequest createConsultationRequest = new CreateConsultationRequest();
        LabResult labResult = testRequest.getLabResult();
        if(labResult.getResult() == TestStatus.POSITIVE){
            createConsultationRequest.setSuggestion(DoctorSuggestion.HOME_QUARANTINE);
            createConsultationRequest.setComments(labResult.getComments());
        } else {
            createConsultationRequest.setSuggestion(DoctorSuggestion.NO_ISSUES);
            createConsultationRequest.setComments("Ok");
        }

        return createConsultationRequest; // Replace this line with your code

    }

}