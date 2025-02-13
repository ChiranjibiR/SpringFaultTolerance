package com.cloudops.SpringFaultTolerance.listener;

import com.cloudops.SpringFaultTolerance.model.StudentCsv;
import com.cloudops.SpringFaultTolerance.model.StudentJson;
import org.springframework.batch.core.annotation.OnSkipInProcess;
import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.batch.core.annotation.OnSkipInWrite;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

@Component
public class SkipListener {

    @OnSkipInRead
    public void skipReader(Throwable th){
        if(th instanceof FlatFileParseException){
            createFile("D:\\Spring Projects\\Spring Batch Projects\\SpringFaultTolerance\\src\\main\\java\\com\\cloudops\\SpringFaultTolerance\\input\\reader\\SkipInRead.txt",
                    ((FlatFileParseException) th).getInput());
        }
    }

    @OnSkipInProcess
    public void skipProcess(StudentCsv studentCsv, Throwable th){
        createFile("D:\\Spring Projects\\Spring Batch Projects\\SpringFaultTolerance\\src\\main\\java\\com\\cloudops\\SpringFaultTolerance\\input\\processor\\SkipInProcess.txt",
                studentCsv.toString());
    }

    @OnSkipInWrite
    public void skipWrite(StudentJson studentJson, Throwable th){
        createFile("D:\\Spring Projects\\Spring Batch Projects\\SpringFaultTolerance\\src\\main\\java\\com\\cloudops\\SpringFaultTolerance\\input\\writer\\SkipInWriter.txt",
                studentJson.toString());
    }

    public void createFile(String filePath, String data){
        try(FileWriter fileWriter = new FileWriter(new File(filePath), true)){
            fileWriter.write(data + new Date() + "\n");
        }catch (Exception e){

        }
    }
}
