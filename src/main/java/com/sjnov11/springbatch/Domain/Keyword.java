package com.sjnov11.springbatch.Domain;

import lombok.*;


/*
    Keyword: representation of keyword file data
        area: Area to capture (e.g x06, x08...)
        query: Search query
        type: Type of area (h1, keyword, list...)
        queryRank: Priority to process
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Keyword {
    private String area;
    private String query;
    private String type;
    private int queryRank;
}
