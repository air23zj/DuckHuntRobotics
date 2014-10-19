/*
  Blink
  Turns on an LED on for one second, then off for one second, repeatedly.
 
  This example code is in the public domain.
 */
 
// Pin 13 has an LED connected on most Arduino boards.
// give it a name:
int led = 13;
int inPin = 9;   // pushbutton connected to digital pin 9(PB5)
int val = 0;     // variable to store the read value

// the setup routine runs once when you press reset:
void setup() {                
  // initialize the digital pin as an output.
  pinMode(led, OUTPUT); 
  pinMode(inPin , INPUT);    // sets the digital pin 9 as input
}

// the loop routine runs over and over again forever:
void loop() {
  val = digitalRead(inPin);   // read the input pin
  digitalWrite(led, val);    // sets the LED to the button's value
}
