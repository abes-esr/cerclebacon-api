package fr.abes.cerclebaconapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class RenameFileRequestDto {
    private String forceOption;
    private String fileName;
}
