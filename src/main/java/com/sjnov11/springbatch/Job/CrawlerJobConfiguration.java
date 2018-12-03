package com.sjnov11.springbatch.Job;


import com.sjnov11.springbatch.Domain.ImageResource;
import com.sjnov11.springbatch.Domain.Keyword;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableBatchProcessing
public class CrawlerJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Resource
    Environment env;

    @Bean
    public Job crawlerJob(Step crawlerStep1) {
        return jobBuilderFactory.get("crawlerJob")
                .start(crawlerStep1)
                .build();
    }

    @Bean
    public Step crawlerStep1(ItemReader reader, CrawlingProcessor processor, ItemWriter writer) {
        return stepBuilderFactory.get("crawlerStep1")
                .<Keyword, ImageResource>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }


    // Reader
    @Bean
    public FlatFileItemReader <Keyword> reader() {
        return new FlatFileItemReaderBuilder<Keyword>()
                .name("keywordItemReader")
                .resource(new ClassPathResource(env.getProperty("path.csv.hackday")))
                .linesToSkip(1)
                .delimited()
                .names(new String[]{"area", "query", "type", "queryRank"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Keyword>() {{
                    setTargetType(Keyword.class);
                }})
                .build();
    }

    // Processor
    @Bean
    public CrawlingProcessor processor(Map<String, Boolean> checker) {
        return new CrawlingProcessor(checker);
    }

    // Writer
    @Bean
    public CustomWriter writer(JdbcTemplate jdbcTemplate){
        return new CustomWriter(jdbcTemplate);
    }


    // Testwriter
    /*@Bean
    public ItemWriter<ImageResource> writer() {
        return new ItemWriter<ImageResource>() {
            @Override
            public void write(List<? extends ImageResource> items) throws Exception {
                for (ImageResource item : items) {
                    System.out.println(item);
                }
            }
        };
    }*/


        @Bean
        public JdbcTemplate jdbcTemplate(){
            BasicDataSource source = new BasicDataSource();
            source.setUrl(env.getProperty("spring.profiles.datasource.hikari.jdbc-url"));
            source.setUsername(env.getProperty("spring.profiles.datasource.hikari.username"));
            source.setPassword(env.getProperty("spring.profiles.datasource.hikari.password"));
            source.setDriverClassName(env.getProperty("spring.profiles.datasource.hikari.driver-class-name"));
           // DB INFO

            return new JdbcTemplate(source);
        }


    // To check whether (area + type) is processed or not
    @Bean
    public Map<String, Boolean> checker() {
        return new HashMap<>();
    }


}

