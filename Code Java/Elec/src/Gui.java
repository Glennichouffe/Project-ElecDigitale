import java.awt.event.ActionListener;
import java.io.OutputStream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JFrame;

import gnu.io.SerialPort;

public class Gui extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	private JPanel panel, buttonPanel;
	private JButton  it,fermer;
	private JLabel label1, label2,label3;
	private Font police = new Font ("Times New Roman", Font.PLAIN,20);
	private JTextField  text1;
	SerialPort sp;
	int seuil=50;
  	
  public Gui(SerialPort spa){
	  
	  	sp=spa;
	  	
		
			  	
	  	setSize(500,400);
		setVisible(true);
	  	panel =new JPanel();
		panel=new JPanel(new GridLayout(5,1));
		panel.setVisible(true);
		panel.setBackground(Color.GRAY);
		setTitle("Gestion de la sonde");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		label3 = new JLabel();
		if(seuil>=100){
			   label3.setText("Limite: "+seuil/100+","+seuil%100+" m");
		   }
		else{
			   label3.setText("Limite: "+seuil+" cm");
		}
		label3.setFont(police);
		label3.setForeground(Color.black);
		panel.add(label3);
		add(panel,BorderLayout.CENTER);
		
	   label1 = new JLabel();
	   label1.setText("Modifiez la limite ici: (en cm)");
	   label1.setFont(police);
	   label1.setForeground(Color.BLACK);
	   text1 = new JTextField(15);
	   panel.add(label1);
	   panel.add(text1);
	   add(panel,BorderLayout.CENTER);
	   
	   label2 = new JLabel();
	   panel.add(label2);
	   add(panel,BorderLayout.CENTER);
	   
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1,3));
		panel.add(buttonPanel);
		
		it = new JButton();
		it.setText("Envoyer");
		it.setSize(4, 4);
		it.setForeground(Color.black);
		it.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
		             envoiDonnee(e);
			}
		});  
		buttonPanel.add(it);
		
		fermer = new JButton();
		fermer.setText("FERMER");
		fermer.setSize(4, 4);
		fermer.setForeground(Color.black);
		fermer.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
		             fermerConnexion(e);
			}
		});  
		buttonPanel.add(fermer);
  	}
	  public void envoiDonnee(ActionEvent e) {
		  String donnee = text1.getText();
		  try{
			  if(Integer.parseInt(donnee)>0 && Integer.parseInt(donnee)<1000){
				  seuil = Integer.parseInt(donnee);
					if(seuil>=100){
						if(seuil%100<10){
							label3.setText("Limite: "+seuil/100+",0"+seuil%100+" m");
						}
						else{
							label3.setText("Limite: "+seuil/100+","+seuil%100+" m");
						}
					   }
					else{
						   label3.setText("Limite: "+seuil+" cm");
					}
					try{
						OutputStream out=sp.getOutputStream();
						if(seuil>=10 && seuil<100){
							out.write(("!0"+seuil).getBytes());
						}
						else{
							if(seuil>=100 && seuil<1000){
								out.write(("!"+seuil).getBytes());
							}
							else{
								if(seuil>0 && seuil<10){
									out.write(("!00"+seuil).getBytes());
								}
							}
						}
						
					}
					catch(Exception en){
						en.printStackTrace();
					}
			  }
		  }
		  catch(Exception ex){}
	  }
	  public void miseDonnee(String donnee){
		   int d=Integer.parseInt(donnee);
		   if(d>=100){
			   if(d%100<10){
				   label2.setText("Distance mesurée: "+d/100+",0"+d%100+" m");
			   }
			   else{
				   label2.setText("Distance mesurée: "+d/100+","+d%100+" m");
			   }   
		   }
		   else{
			   label2.setText("Distance mesurée: "+donnee+" cm");
		   }
		   label2.setFont(police);
		   label2.setForeground(Color.black);
		   if(d>seuil){
			   panel.setBackground(Color.green);
		   }
		   else{
			   panel.setBackground(Color.red);
		   }
	  }
	  
	  public void fermerConnexion(ActionEvent e){
		  sp.close();
		  setVisible(false);
	  }
	  
	@Override
	
		public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
		
		public static void main ( String[] args )
		    {
		        try
		        {
		            TwoWaySerialComm a=new TwoWaySerialComm();
		            a.connect("COM4");
		        }
		        catch ( Exception e )
		        {
		            // TODO Auto-generated catch block
		            e.printStackTrace();
		        }
		    }
	}
