package fr.abes.cerclebaconapi.dto;

public class RenameFileRequestDto {
    private String forceOption;
    private String fileName;

    public String getForceOption() {
        return forceOption;
    }

    public void setForceOption(String forceOption) {
        this.forceOption = forceOption;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
