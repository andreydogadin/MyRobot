const int GET_POWER_PIN = 8;
const int SET_POWER_PIN = 12;
const int LED_PIN = 13;

const int GET_POWER_BYTE = 'G';
const int SET_POWER_BYTE = 'S';

const int POWER_STATUS_OFF = 'F';
const int POWER_STATUS_ON = 'N';

int inByte = 0;         // incoming serial byte

void setup(){
  // start serial port at 9600 bps:
  Serial.begin(9600);
  pinMode(GET_POWER_PIN, INPUT);   
  pinMode(SET_POWER_PIN, OUTPUT);
  pinMode(LED_PIN, OUTPUT);
}

void loop(){
  if (Serial.available() > 0) {
    inByte = Serial.read();
    switch (inByte) {
      case GET_POWER_BYTE:
        sendPowerStatus();
        break;
      case SET_POWER_BYTE:
        switchPower();
        break;
      default: 
        sendByteBack(inByte);
    }
  }
  delay(100);
}

void sendByteBack(int inByte){
  Serial.write(inByte);
}

void sendPowerStatus(){
  int result = 0; 
  if (digitalRead(GET_POWER_PIN) == LOW)
    result = POWER_STATUS_OFF;
  else 
    result = POWER_STATUS_ON;
  sendByteBack(result);
}

void switchPower(){
  blink();
  digitalWrite(SET_POWER_PIN, LOW);
  digitalWrite(SET_POWER_PIN, HIGH); // switch power on
  digitalWrite(SET_POWER_PIN, LOW);
  delay(3500);
}

void blink(){
  digitalWrite(LED_PIN, HIGH);   // set the LED on
  delay(500);              // wait for a second
  digitalWrite(LED_PIN, LOW);    // set the LED off
  delay(500);              // wait for a second
}
