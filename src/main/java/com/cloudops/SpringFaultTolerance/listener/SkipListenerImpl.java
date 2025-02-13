package com.cloudops.SpringFaultTolerance.listener;

import com.cloudops.SpringFaultTolerance.model.StudentCsv;
import com.cloudops.SpringFaultTolerance.model.StudentJson;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

@Component
public class SkipListenerImpl implements SkipListener<StudentCsv, StudentJson> {

    @Override
    public void onSkipInRead(Throwable th) {
        if(th instanceof FlatFileParseException){
            createFile("D:\\Spring Projects\\Spring Batch Projects\\SpringFaultTolerance\\src\\main\\java\\com\\cloudops\\SpringFaultTolerance\\input\\reader\\SkipInRead.txt",
                    ((FlatFileParseException) th).getInput());
        }
    }

    @Override
    public void onSkipInWrite(StudentJson item,Throwable th) {
        createFile("D:\\Spring Projects\\Spring Batch Projects\\SpringFaultTolerance\\src\\main\\java\\com\\cloudops\\SpringFaultTolerance\\input\\writer\\SkipInWriter.txt",
                item.toString());
    }

    @Override
    public void onSkipInProcess(StudentCsv item, Throwable th) {
        createFile("D:\\Spring Projects\\Spring Batch Projects\\SpringFaultTolerance\\src\\main\\java\\com\\cloudops\\SpringFaultTolerance\\input\\processor\\SkipInProcess.txt",
                item.toString());
    }

    public void createFile(String filePath, String data){
        try(FileWriter fileWriter = new FileWriter(new File(filePath), true)){
            fileWriter.write(data + new Date() + "\n");
        }catch (Exception e){

        }
    }
}
