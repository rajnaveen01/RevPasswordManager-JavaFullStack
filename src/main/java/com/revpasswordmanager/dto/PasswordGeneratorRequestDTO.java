//package com.revpasswordmanager.dto;
//
//public class PasswordGeneratorRequestDTO {
//
//    private int length;
//    private boolean includeUppercase;
//    private boolean includeLowercase;
//    private boolean includeNumbers;
//    private boolean includeSpecialChars;
//
//    public PasswordGeneratorRequestDTO() {}
//
//    public int getLength() { return length; }
//    public void setLength(int length) { this.length = length; }
//
//    public boolean isIncludeUppercase() { return includeUppercase; }
//    public void setIncludeUppercase(boolean includeUppercase) {
//        this.includeUppercase = includeUppercase;
//    }
//
//    public boolean isIncludeLowercase() { return includeLowercase; }
//    public void setIncludeLowercase(boolean includeLowercase) {
//        this.includeLowercase = includeLowercase;
//    }
//
//    public boolean isIncludeNumbers() { return includeNumbers; }
//    public void setIncludeNumbers(boolean includeNumbers) {
//        this.includeNumbers = includeNumbers;
//    }
//
//    public boolean isIncludeSpecialChars() { return includeSpecialChars; }
//    public void setIncludeSpecialChars(boolean includeSpecialChars) {
//        this.includeSpecialChars = includeSpecialChars;
//    }
//}




package com.revpasswordmanager.dto;

public class PasswordGeneratorRequestDTO {

    private int length;
    private boolean includeUppercase;
    private boolean includeLowercase;
    private boolean includeNumbers;
    private boolean includeSpecialChars;
    private boolean excludeSimilarChars;

    public PasswordGeneratorRequestDTO() {}

    public int getLength() { return length; }
    public void setLength(int length) { this.length = length; }

    public boolean isIncludeUppercase() { return includeUppercase; }
    public void setIncludeUppercase(boolean includeUppercase) {
        this.includeUppercase = includeUppercase;
    }

    public boolean isIncludeLowercase() { return includeLowercase; }
    public void setIncludeLowercase(boolean includeLowercase) {
        this.includeLowercase = includeLowercase;
    }

    public boolean isIncludeNumbers() { return includeNumbers; }
    public void setIncludeNumbers(boolean includeNumbers) {
        this.includeNumbers = includeNumbers;
    }

    public boolean isIncludeSpecialChars() { return includeSpecialChars; }
    public void setIncludeSpecialChars(boolean includeSpecialChars) {
        this.includeSpecialChars = includeSpecialChars;
    }

    public boolean isExcludeSimilarChars() { return excludeSimilarChars; }
    public void setExcludeSimilarChars(boolean excludeSimilarChars) {
        this.excludeSimilarChars = excludeSimilarChars;
    }
}