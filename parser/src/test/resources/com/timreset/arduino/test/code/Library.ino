#include <LiquidCrystal.h>
#include <TestLibrary.h>

LiquidCrystal lcd(12,11,5,4,3,2);
LiquidCrystal lcd2(12,11,5,4,3,2);

void setup(){
  lcd.begin(16,2);
  lcd.print("hello, world!");
  lcd2.begin(16,2);
  lcd2.print("hello, world!");
}

void loop(){
  lcd.noBlink();
  delay(3000);
  lcd.blink();
  delay(3000);
  OtherClassInLibrary a1;
  OtherClassInLibrary a2(2);
  
   ClassLibrary l;
   l.methodWithParameter(&a1);
   l.methodWithParameter(&a2);
}