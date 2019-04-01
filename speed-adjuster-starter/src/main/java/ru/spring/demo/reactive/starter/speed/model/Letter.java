package ru.spring.demo.reactive.starter.speed.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Evgeny Borisov
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Letter {
    private String content;
    private String signature;
    private String location;
    private String _original;

    public String getSignature() {
        return getEncrypted();
    }

    private String getEncrypted() {
        return Integer.toBinaryString(signature.hashCode());
    }

    public String secretMethodForDecodeSignature() {
        return this._original;
    }
}
