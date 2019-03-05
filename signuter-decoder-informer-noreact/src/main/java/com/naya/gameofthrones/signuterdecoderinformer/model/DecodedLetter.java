package com.naya.gameofthrones.signuterdecoderinformer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Evgeny Borisov
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecodedLetter {
    private String author;
    private String content;
    private String location;

}
