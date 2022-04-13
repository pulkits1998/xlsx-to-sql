package com.pulkit.xlsxtosql.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class XlsxToSqlFromUrlRequest {

    @JsonProperty("url")
    private String fileUrl;
}
