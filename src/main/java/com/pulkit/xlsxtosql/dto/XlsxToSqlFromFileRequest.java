package com.pulkit.xlsxtosql.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class XlsxToSqlFromFileRequest {

    @JsonProperty("path")
    private String filePath;
}
