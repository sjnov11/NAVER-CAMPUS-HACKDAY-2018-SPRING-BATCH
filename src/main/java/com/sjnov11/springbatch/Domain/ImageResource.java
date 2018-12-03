package com.sjnov11.springbatch.Domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/*
    ImageResource: Represent of data to write on DB
        area: Area to capture (e.g x06, x08...)
        query: Search query
        type: Type of area (h1, keyword, list...)
        queryRank: Priority to process
        imgPath: List of image path from crawling result
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageResource {
    private String area;
    private String query;
    private String type;
    private int queryRank;
    private List<String> imgPath;

}
