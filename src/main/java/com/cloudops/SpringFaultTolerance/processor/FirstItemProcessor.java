package com.cloudops.SpringFaultTolerance.processor;

import com.cloudops.SpringFaultTolerance.model.StudentCsv;
import com.cloudops.SpringFaultTolerance.model.StudentJson;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class FirstItemProcessor implements ItemProcessor<StudentCsv, StudentJson> {

    @Override
    public StudentJson process(StudentCsv item) {
        //TODO explicitly written to throw exception in 6th record
        if(item.getId() == 6) {
            System.out.println("Inside Item Processor");
            throw new NullPointerException();
        }
        StudentJson studentJson = new StudentJson();
        studentJson.setId(item.getId());
        studentJson.setFirstName(item.getFirstName());
        studentJson.setLastName(item.getLastName());
        studentJson.setEmail(item.getEmail());
        return studentJson;
    }
}
