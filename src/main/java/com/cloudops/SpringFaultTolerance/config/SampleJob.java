package com.cloudops.SpringFaultTolerance.config;

import com.cloudops.SpringFaultTolerance.listener.SkipListener;
import com.cloudops.SpringFaultTolerance.listener.SkipListenerImpl;
import com.cloudops.SpringFaultTolerance.model.StudentCsv;
import com.cloudops.SpringFaultTolerance.model.StudentJson;
import com.cloudops.SpringFaultTolerance.processor.FirstItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SampleJob {

    @Autowired
    JobRepository jobRepository;

    @Autowired
    PlatformTransactionManager platformTransactionManager;

    @Autowired
    FirstItemProcessor firstItemProcessor;

    @Autowired
    SkipListener skipListener;

    @Autowired
    SkipListenerImpl skipListenerImpl;

    @Bean
    public Job firstJob(){
        return new JobBuilder("firstJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(firstStep())
                .build();
    }

    public Step firstStep(){
        return new StepBuilder("firstStep", jobRepository)
                .<StudentCsv, StudentJson>chunk(3,platformTransactionManager)
                .reader(flatFileItemReader())
                .processor(firstItemProcessor)
                .writer(jsonFileItemWriter())
                .faultTolerant()
                .skip(Throwable.class) //TODO In case we don't know which exception is going to be thrown then best is to use throwable class
                //.skip(FlatFileParseException.class)
                .skipLimit(10) //TODO In case we don't know which exception is going to be thrown then best is to use throwable class
                //.skipPolicy(new AlwaysSkipItemSkipPolicy()) //TODO In case we want to skip all the bad records use this
                //.listener(skipListener)
                .retryLimit(3) //TODO retryLimit() can only be applied on ItemWriter and ItemProcessor if value is 3, then ItemWriter will retry 4 times total and ItemProcessor will retry value-1 which is 3 times total
                .retry(Throwable.class) //TODO retryLimit() is for how many times and retry() is for which error class it will retry
                .listener(skipListenerImpl)
                .build();
    }

    public FlatFileItemReader<StudentCsv> flatFileItemReader(){
        FlatFileItemReader<StudentCsv> fileItemReader = new FlatFileItemReader<StudentCsv>();
        FileSystemResource fileSystemResource = new FileSystemResource("D:\\Spring Projects\\Spring Batch Projects\\SpringFaultTolerance\\src\\main\\java\\com\\cloudops\\SpringFaultTolerance\\input\\student.csv");
        fileItemReader.setResource(fileSystemResource);
        fileItemReader.setLineMapper(new DefaultLineMapper<StudentCsv>(){
            {
                setLineTokenizer(new DelimitedLineTokenizer(){
                    {
                        setNames("ID","First Name","Last Name","Email");
                    }
                });

                setFieldSetMapper(new BeanWrapperFieldSetMapper<>(){
                    {
                        setTargetType(StudentCsv.class);
                    }
                });
            }
        });
        fileItemReader.setLinesToSkip(1);
        return fileItemReader;
    }

    public JsonFileItemWriter<StudentJson> jsonFileItemWriter(){
        FileSystemResource fileSystemResource = new FileSystemResource("D:\\Spring Projects\\Spring Batch Projects\\SpringFaultTolerance\\src\\main\\java\\com\\cloudops\\SpringFaultTolerance\\input\\student.json");
        JsonFileItemWriter<StudentJson> jsonItemWriter =new JsonFileItemWriter<>(fileSystemResource, new JacksonJsonObjectMarshaller<StudentJson>()){

            //TODO explicitly written to throw exception in 3rd record
            @Override
            public String doWrite(Chunk<? extends StudentJson> items) {
                items.getItems().forEach(item->{
                    if (item.getId()==3) {
                        System.out.println("Inside item writer");
                        throw new NullPointerException();
                    }
                });
                return super.doWrite(items);
            }
        };
        return jsonItemWriter;
    }
}
