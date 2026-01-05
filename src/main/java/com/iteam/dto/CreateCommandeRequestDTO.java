package com.iteam.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class CreateCommandeRequestDTO {

    private Long userId;
    private List<Long> productsId;



}
