package dev.asiglesias.domain;

import lombok.Value;

@Value
public class MeasureUnit {

   private static final String PIECE_NAME = "piece";

   String unitName;

   public static MeasureUnit piece() {
      return new MeasureUnit(PIECE_NAME);
   }
}
