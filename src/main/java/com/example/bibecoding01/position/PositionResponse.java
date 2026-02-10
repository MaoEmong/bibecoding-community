package com.example.bibecoding01.position;

public class PositionResponse {

    public record OptionDTO(
            Long id,
            String name,
            boolean selected
    ) {
    }
}
