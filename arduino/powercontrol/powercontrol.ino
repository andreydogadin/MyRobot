#include <Servo.h>
#include <SoftwareSerial.h>

const int GET_POWER_PIN = 2;
const int SET_POWER_PIN = 6;
const int SERVO_X_PIN = 10;
const int SERVO_Y_PIN = 9;
const int SERIAL_RX_PIN = 11;
const int SERIAL_TX_PIN = 3;
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

Servo servoX;
Servo servoY;
int currentServo = SERVO_X_BYTE;

int currentCommandType = 'Q';

SoftwareSerial mySerial(SERIAL_RX_PIN, SERIAL_TX_PIN);

void setup(){
  // start serial port at 9600 bps:
  Serial.begin(9600);
  mySerial.begin(57600);
  pinMode(GET_POWER_PIN, INPUT);   
  pinMode(SET_POWER_PIN, OUTPUT);
  pinMode(LED_PIN, OUTPUT);
  //servoX.attach(SERVO_X_PIN);
  //servoY.attach(SERVO_Y_PIN);
}

void loop(){
  if (Serial.available() > 0) {
    commandByte = Serial.read();

    switch (commandByte){
      // Power control
      case 'P':
        currentCommandType = commandByte;
        break;
      // Robot control
      case 'R':
        currentCommandType = commandByte;
        break;
      // Camera control
      case 'C':
        currentCommandType = commandByte;
        break;
      // Reset command type flag
      case 'Q':
        currentCommandType = commandByte;
      // Perform internal test
      case 'T':
        performTest();
        break;
      default:
        processCommand(commandByte);
    }
    delay(100);
  }
}

void processCommand(int commandByte){
  switch (currentCommandType){
      case 'P':
        processPowerCommand(commandByte);
        break;
      // Robot control
      case 'R':
        processRobotCommand(commandByte);
        break;
      // Camera control
      case 'C':
        processCameraCommand(commandByte);
        break;
    }
}

void processPowerCommand(int commandByte){
  switch (commandByte) {
    case GET_POWER_BYTE:
      sendPowerStatus();
      break;
    case SET_POWER_BYTE:
       switchPower();
      break;
  }
}

void processRobotCommand(int commandByte){
  mySerial.write(commandByte);
}

void processCameraCommand(int commandByte){
  switch (commandByte) {
    case SERVO_X_BYTE:
      currentServo = SERVO_X_BYTE;
      break;
    case SERVO_Y_BYTE:
      currentServo = SERVO_Y_BYTE;
      break;
    default:
      if (currentServo == SERVO_X_BYTE){
        servoX.attach(SERVO_X_PIN);
        moveServo(servoX, commandByte);
        servoX.detach();
      }
        
      if (currentServo == SERVO_Y_BYTE){
        servoY.attach(SERVO_Y_PIN);
        moveServo(servoY, commandByte);
        servoY.detach();
      }
  }
}

void moveServo(Servo s, int commandByte){
  int currentPos = s.read();
  int nextPos = 90;
  switch (commandByte){
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

void performTest(){
  
  Serial.println("Arduino blink 2 times");
  blink();
  blink();

  Serial.println("Test Camera movement");
  currentCommandType = 'C';  
    processCommand('X');  
    processCommand('+');
    processCommand('Y');
    processCommand('+');
    processCommand('X');  
    processCommand('+');
    processCommand('Y');
    processCommand('+');
    processCommand('X');  
    processCommand('0');
    processCommand('Y');
    processCommand('0');
  currentCommandType = 'Q';

  Serial.println("Test power control - turn Robot On");
  currentCommandType = 'P';
    processCommand('G');
    processCommand('S');
    delay(4000);
  currentCommandType = 'Q';

  Serial.println("Test robot control");
  currentCommandType = 'R';
    processCommand(128);
    processCommand(132);
    Serial.println("Robot Start");
    delay(500);
    Serial.println("Set ADV LED to On");
    processCommand(139);
    processCommand(8);
    processCommand(0);
    processCommand(0);
    delay(1000);
    Serial.println("Set Play LED to On");
    processCommand(139);
    processCommand(2);
    processCommand(0);
    processCommand(0);
    Serial.println("Set PWR LED to On");
    delay(1000);
    processCommand(139);
    processCommand(0);
    processCommand(255);
    processCommand(255);
    delay(1000);
    Serial.println("Set ALL LEDs to Off");
    delay(1000);
    processCommand(139);
    processCommand(0);
    processCommand(0);
    processCommand(0);
    delay(1000);
  currentCommandType = 'Q';

  Serial.println("Test power control - turn Robot Off");
  currentCommandType = 'P';      
    processCommand('G');
    processCommand('S');
  currentCommandType = 'Q';

  Serial.println("Arduino blink 3 times");
  blink();
  blink();
  blink();

  Serial.println("Test Complete");
}
