#include <projetElec.h>
#include <stdio.h>
#fuses HS, NOPROTECT, NOLVP, NOWDT
#use delay (clock=20000000)
#use rs232(baud=9600,xmit=PIN_C6,rcv=PIN_C7,bits=8)


#byte PORTC = 0xF82


char buffer[4];
int flag=0;
int i=0;

#int_RDA
void  RDA_isr(void) 
{
   buffer[i]=getc();
   if(buffer[0]=='!' && flag==0){
      i++;
      if(i>=4){
         i=0;
         flag=1;
      }
   }
   
}

void main()
{

   enable_interrupts(INT_RDA);
   enable_interrupts(GLOBAL);
   setup_low_volt_detect(FALSE);
   
   set_tris_c(0b10000010);//parametrage des entrees
   setup_timer_0(RTCC_INTERNAL);//choix du timer
   int bitfort, bitfaible;
   int16  distance,seuil,c,d,u;
   long time;

   while(TRUE)
   {  
      if(flag==1){
         flag=0;
         c=buffer[1]-48;
         d=buffer[2]-48;
         u=buffer[3]-48;
         seuil=(int16)(c*100+d*10+u);
      }  
      else{
         if(seuil==NULL){
            seuil=50;
            //while(flag!=1){} on peut aussi attendre que le seuil soit envoyer
         }
      } 
      //on s'assure de bien demarrer avec le trigger a 0
      output_low(PIN_C0);
      delay_us(2);
      
      //activation du trigger
      output_high(PIN_C0);
      delay_us(10);
      output_low(PIN_C0);
      
      //on attend d'avoir un signal sur l'echo
      while(!input(PIN_C1)){}
      
      //signal arrive. On met en marche le timer et on calcule le temps a l'etat haut
      set_timer0(0);
      while(input(PIN_C1)){}
      time=get_timer0()*(1.6);//recupere le timer qu'on multiplie par son temps ici 1.6 microseconds

      distance=(int16)time*0.017;//on multiplie par la vitesse du son divise par 2
      
      //envoi du resultat sur le connecteur rs232
      printf("%ld",distance);
      printf("\n");
      delay_ms(500);
      
      
      //on passe aux tests pour l'affichage
      if(distance<10 && distance>=1){
         bitfort=0;
         bitfaible=distance;
         output_bit(PIN_E0, 0);
      }
      else{ 
            if(distance>=10 && distance <100){
                  bitfort=distance/10;
                  bitfaible=distance%10;
                  output_bit(PIN_E0, 0);
            }
            else {
                  if(distance>=100 && distance<1000){
                     bitfort=distance/100;
                     bitfaible=(distance%100)/10;
                     output_bit(PIN_E0, 1);
                 }
                 else{
                     bitfort=14;
                     bitfaible=14;
                     output_bit(PIN_E0, 0);
                 }
           }
       }
       
      //sortie pour les decodeurs
      output_d(bitfaible+(bitfort<<4)); 
      
      //test pour les leds
      if(distance<=seuil){
         output_bit(PIN_B5, 1);
         output_bit(PIN_B4, 0);
      }
      if(distance>seuil){
         output_bit(PIN_B4, 1);
         output_bit(PIN_B5, 0);
      }
      
      
   }

}


