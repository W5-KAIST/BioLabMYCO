#include <avr/sleep.h>
#include <Wire.h>
#include <Adafruit_TCS34725.h>

// Global Variables
int power_3v_pin = 10;
int motor_pin = 9;
int motor_run_time =  3500;
int motor_run_tick = 2;

int temp_sensor_port = A1;

int batt_voltage_port = A0;

/*
int red = 0;
int green = 0;
int blue = 0;
Adafruit_TCS34725 tcs = Adafruit_TCS34725(TCS34725_INTEGRATIONTIME_50MS, TCS34725_GAIN_4X);
*/
int freqcolorsensorPin = 6;

int wakePin = 2;
//volatile int interrupt_counter = 0;
int interrupt_counter = 0;
int timer_threshold = 2;
int motor_flag = 0;
//

// Function Prototypes
void wakeTask(void);
void wakeUp(void);
void sleepTask(void);
void sleepNow(void);

void BLE_setup(void);
void run_motor(void);
int read_temp_sensor(void);
int get_batt_voltage_x10(void);
//void read_color_sensor(int &red_value, int &green_value, int &blue_value);
float read_freq_color_sensor(void);
void update_beacon(void);
//

// Main Program
void setup()
{
  // put your setup code here, to run once:
  Serial.begin(9600);
  
  pinMode(wakePin, INPUT);
  pinMode(power_3v_pin, OUTPUT);
  pinMode(motor_pin, OUTPUT);
  pinMode(freqcolorsensorPin, INPUT);

  BLE_setup();
  update_beacon();
}

void loop()
{
  // put your main code here, to run repeatedly:
  sleepNow();
  delay(100);
}
//

// SLEEP MODE CONTROL
void sleepNow()
{
  sei();
  attachInterrupt(0,wakeUp, RISING);
  delay(100);
  set_sleep_mode(SLEEP_MODE_PWR_DOWN);
  
  sleepTask();
  
  sleep_enable();
  sleep_mode();
  //Wake here
  
  sleep_disable();
  wakeTask();
  attachInterrupt(0,wakeUp, RISING);
}

void sleepTask()
{
  digitalWrite(power_3v_pin, LOW);
  digitalWrite(motor_pin, LOW);
}

void wakeUp()
{
  detachInterrupt(0);
  cli();
  //interrupt_counter = interrupt_counter + 1;
}

void wakeTask()
{
  interrupt_counter++;
  
  if(interrupt_counter == timer_threshold)
  {
    motor_flag = 1;
    delay(100);
    run_motor();
    delay(100);
    update_beacon();
  }else{
    update_beacon();
  }
}
//

// Function CONTROL
void BLE_setup()
{
  digitalWrite(power_3v_pin, HIGH);
  delay(100);
  Serial.println("AT+MANUF=BM-W5001"); // Beacon ID
  while(Serial.available()){Serial.read();}
  delay(500);
  Serial.println("AT+TXPWR=1"); // Tx Power 0 (-18dBm)~7(8dBm)
  while(Serial.available()){Serial.read();}
  delay(500);
  Serial.println("AT+ADVDATA=HZMTBC"); // Data Format
  while(Serial.available()){Serial.read();}
  delay(500);
  digitalWrite(power_3v_pin, LOW);
}

void run_motor()
{
  for(int i=0; i< motor_run_tick; i++)
  {
    digitalWrite(motor_pin, HIGH);
    delay(motor_run_time);
    digitalWrite(motor_pin, LOW);
    delay(100);
  }
}

int read_temp_sensor()
{
  float raw_temp = analogRead(temp_sensor_port);
  float real_temp = (raw_temp/1024) * 500;
  int temp = static_cast<int>(real_temp);
  
  return temp;
}

int get_batt_voltage_x10()
{
  float raw_voltage = analogRead(batt_voltage_port);
  float real_voltage = ((raw_voltage/1023) * 50) * 2;
  int volt = static_cast<int>(real_voltage);
  
  return volt;
}

float read_freq_color_sensor()
{
  int top = 1023;
  int cnt = 0;
  
  digitalWrite(power_3v_pin, HIGH);
  delay(500);
  
  unsigned long start = millis();
  
  while(cnt < top)
  {
    pulseIn(freqcolorsensorPin, HIGH);
    cnt++;  
  }
  
  unsigned long t = millis() - start;
  float frequency =  1/(t/1024.0);

  return frequency; //kHz scale
}

/*
void read_color_sensor(int &red_value, int &green_value, int &blue_value)
{
  uint16_t clr;
  uint16_t red_raw;
  uint16_t green_raw;
  uint16_t blue_raw;
  uint32_t sum;
  
  float r,g,b;
  
  digitalWrite(power_3v_pin, HIGH);
  delay(1000);
  tcs.begin();
  tcs.setInterrupt(false);
  delay(60);
  tcs.getRawData(&red_raw, &green_raw, &blue_raw, &clr);
  tcs.setInterrupt(true);
  delay(1000);
  digitalWrite(power_3v_pin, LOW);
  
  sum = clr;
  r = red_raw; r /= sum;
  g = green_raw; g /= sum;
  b = blue_raw; b /= sum;
  r *= 256; g *= 256; b *= 256;
  
  red_value = static_cast<int>(r);
  green_value = static_cast<int>(g);
  blue_value = static_cast<int>(b);
}
*/

void update_beacon()
{ 
  String msg_header = "AT+ADVDATA=";
  
  int temp = read_temp_sensor();
  String temp_str = String(static_cast<char>(temp));
  
  int batt = get_batt_voltage_x10();
  String batt_str = String(static_cast<char>(batt));
  
  String motor_run_flag_str = String(motor_flag);
  
  /*
  read_color_sensor(red, green, blue);
  String red_str = String(static_cast<char>(red));
  String green_str = String(static_cast<char>(green));
  String blue_str = String(static_cast<char>(blue));
  */
  float Hz = read_freq_color_sensor();
  int red_freq =  static_cast<int>(Hz);
  String red_freq_str;
  if(red_freq < 10)
  {
    String front_zero = "0";
    red_freq_str = front_zero + String(red_freq);
  }else{
     red_freq_str = String(red_freq); 
  }
  //Serial.println(Hz);
  //Serial.println(red_freq_str);
  
  String date_counter = String(static_cast<char>(interrupt_counter));
  //Serial.print(motor_flag);
  //Serial.print(",");
  //Serial.println(interrupt_counter);
  
  digitalWrite(power_3v_pin, HIGH);
  delay(100);
  
  //String adv_msg = msg_header + red_str + green_str + blue_str + motor_run_flag_str + temp_str + batt_str + date_counter;
  String adv_msg = msg_header + red_freq_str + motor_run_flag_str + temp_str + batt_str + date_counter;
  Serial.println(adv_msg);
  while(Serial.available()){Serial.read();}
  delay(500);
  
  digitalWrite(power_3v_pin, LOW);
}
//
