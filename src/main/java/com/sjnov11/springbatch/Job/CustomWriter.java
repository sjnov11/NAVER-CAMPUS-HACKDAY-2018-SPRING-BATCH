package com.sjnov11.springbatch.Job;

import com.sjnov11.springbatch.Domain.ImageResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Slf4j
public class CustomWriter implements ItemWriter<ImageResource> {
    private JdbcTemplate jdbcTemplate;

    public CustomWriter(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void write(List<? extends ImageResource> items) throws Exception {

        for (ImageResource item : items) {
            jdbcTemplate.update("INSERT INTO img_resource (area, query, type, queryRank) VALUES (?, ?, ?, ?)",
                                    item.getArea(), item.getQuery(), item.getType(), item.getQueryRank());
            Integer pk = jdbcTemplate.queryForObject("SELECT (id) FROM img_resource " +
                                                            "WHERE area = '" + item.getArea() + "' AND type = '" + item.getType() +"'", Integer.class);

            for (String imgPath : item.getImgPath()) {
                jdbcTemplate.update("INSERT INTO img_path (fk, imgPath) VALUES (?, ?)", pk, imgPath);
            }
        }
    }
}
