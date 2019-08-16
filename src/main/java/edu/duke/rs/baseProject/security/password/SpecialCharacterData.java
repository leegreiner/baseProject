package edu.duke.rs.baseProject.security.password;

import org.passay.CharacterData;

class SpecialCharacterData implements CharacterData {
  public static final String SPECIAL_CHARACTERS = "!\\\"#$%&'()*+,-./:;<=>?@[\\\\]^_`{|}~";
  
  @Override
  public String getErrorCode() {
    return "SPECIAL_CHARACTER";
  }

  @Override
  public String getCharacters() {
    return SPECIAL_CHARACTERS;
  }
}