/*
 * MYCO-PollingSystem.c
 *
 * Created: 2015-10-28 PM 4:50:16
 * Author : MR
 */ 

#include <avr/io.h>
#include <avr/interrupt.h>
#include <avr/sleep.h>
const long BLINK_PERIOD = 8*60*60;
volatile long count = 0 ;
volatile char initial = 1;
ISR(INT0_vect){
	cli();
	if(count == BLINK_PERIOD){
		PORTD |= (1<<5);
		count = 0;
	}
}

int main(void)
{
	if(initial){
		DDRD = (1<<DDD5);
		PORTD = 0x00;
		GIMSK=(1<<INT0);
		MCUCR= (0<<ISC11) | (0<<ISC10) | (0<<ISC01) | (0<<ISC00);
		sei();
		sleep_enable();
		set_sleep_mode(SLEEP_MODE_PWR_DOWN);
		sleep_cpu();
		initial = 0;
		count = 0;
	}
	
	while (1)
	{
		if((PIND & (1<<PIND2))>>PIND2!=0 ){
			//if high detected
			count++;
			PORTD &= ~(1<<5);
			sei();
			sleep_cpu();
		}
	}
}