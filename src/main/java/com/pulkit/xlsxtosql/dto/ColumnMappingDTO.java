package com.pulkit.xlsxtosql.dto;

import lombok.Data;

@Data
public class ColumnMappingDTO {

    private String srcCol;

    private String tgtCol;

    private String tgtDataType;

}
