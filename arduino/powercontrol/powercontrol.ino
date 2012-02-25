#include <Servo.h> 

const int GET_POWER_PIN = 2;
const int SET_POWER_PIN = 6;
const int SERVO_X_PIN = 9;
const int SERVO_Y_PIN = 10;
const int LED_PIN = 13;

const int GET_POWER_BYTE = 'G';
const int SET_POWER_BYTE = 'S';

const int SERVO_X_BYTE = 'X';
const int SERVO_Y_BYTE = 'Y';

const int SERVO_PLUS_BYTE = '+';
const int SERVO_MINUS_BYTE = '-';
const int SERVO_RESET_BYTE = '0';
const int SERVO_MOVE_STEP = 30;

const int POWER_STATUS_OFF = 'F';
const int POWER_STATUS_ON = 'N';

int commandByte = 0;         // incoming serial byte
int paramByte = 0;
int wasServoCommandFlag = 'n';

Servo servoX;
Servo servoY;
int currentServo = SERVO_X_BYTE;

void setup(){
  // start serial port at 9600 bps:
  Serial.begin(9600);
  pinMode(GET_POWER_PIN, INPUT);   
  pinMode(SET_POWER_PIN, OUTPUT);
  pinMode(LED_PIN, OUTPUT);
  servoX.attach(SERVO_X_PIN);
  servoY.attach(SERVO_Y_PIN);
}

void loop(){
  if (Serial.available() > 0) {
    commandByte = Serial.read();

    if (wasServoCommandFlag == 'y'){
      processServoCommand(commandByte);
      wasServoCommandFlag = 'n';  
    }
    else {
      processCommonCommand(commandByte);  
    }
  }
  delay(100);
}

void processCommonCommand(int commandByte){
  switch (commandByte) {
        case GET_POWER_BYTE:
          sendPowerStatus();
          break;
        case SET_POWER_BYTE:
           switchPower();
          break;
        case SERVO_X_BYTE:
          currentServo = SERVO_X_BYTE;
          wasServoCommandFlag = 'y';
          break;
        case SERVO_Y_BYTE:
          currentServo = SERVO_Y_BYTE;
          wasServoCommandFlag = 'y';
          break;
        default:
          sendByteBack(commandByte);
  }
}
void processServoCommand(int commandByte){
  int command = commandByte;
  if (currentServo == SERVO_X_BYTE){
    //servoX.attach(SERVO_X_PIN);
    moveServo(servoX, command);
    //servoX.detach();
  }
    
  if (currentServo == SERVO_Y_BYTE){
    //servoY.attach(SERVO_Y_PIN);
    moveServo(servoY, command);
    //servoY.detach();
  }
    

}

void moveServo(Servo s, int command){
  int currentPos = s.read();
  int nextPos = 90;
  switch (command){
    case SERVO_PLUS_BYTE:
      nextPos = currentPos + SERVO_MOVE_STEP;
      if (nextPos > 180) nextPos = 180;
      break;
    case SERVO_MINUS_BYTE:
      nextPos = currentPos - SERVO_MOVE_STEP;
      if (nextPos < 0) nextPos = 0;
      break;
  }
  s.write(nextPos);
  delay(300);
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

void sendByteBack(int inByte){
  Serial.write(inByte);
}
