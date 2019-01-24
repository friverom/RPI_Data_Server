/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rpi_data_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpio_client.Net_RPI_IO;
import rpio_data.RPI_Data;

/**
 *
 * @author Federico
 */
public class RPI_Data_Server {
    
    Net_RPI_IO rpio = null;
    RPI_Data data = null; 
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        
        new RPI_Data_Server().serverRun();
        
        }
    public void serverRun() throws IOException, ClassNotFoundException{
        
        ServerSocket serversocket = new ServerSocket(30005);
        data = new RPI_Data();
        data.set_Rpi_name("RPI RD3");
        data.set_Rpi_address("192.168.1.56");
        rpio = new Net_RPI_IO("localhost",30000);
        
        while(true){
            Socket socket = serversocket.accept();
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            
            String command = (String) ois.readObject();
            
            oos.writeObject(this.proccessCommand(command));
            oos.close();
            socket.close();
            
        }
        
    }
    
    private Object proccessCommand(String command){
        
        Object reply = null;
        switch(command){
            case"get data":
                data.setInputs(this.getInputPort(rpio));
                data.setOutputs(this.getOutputPort(rpio));
                for(int i=1;i<9;i++){
                data.setAnalog(i, this.getAnalogInput(rpio, i));
            }
                reply = data;
                break;
                
            default:
                reply="Invalid command";
               
        }
        return reply;
    }
    private int getInputPort(Net_RPI_IO rpio){
        
        String data = rpio.getInputPort(11,1);
        String parts[] = data.split(",");
        
        if(parts.length == 3){
            return Integer.parseInt(parts[2]);
        } else{
            return 255;
        }
        
    }
    
    private int getOutputPort(Net_RPI_IO rpio){
     
        String data = rpio.getRelayPort(11, 1);
        String parts[] = data.split(",");
        
        if(parts.length == 3){
            return Integer.parseInt(parts[2]);
        } else{
            return 255;
        }
    }
    
    private double getAnalogInput(Net_RPI_IO rpio, int chn){
        
        String data = rpio.readAnalogChannel(11, 1, chn);
        String parts[] = data.split(",");
        
        if(parts.length == 3){
            return Double.parseDouble(parts[2]);
        } else{
            return 20.0;
        }
    }
        
    }


