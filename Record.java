
/**
 *
 * Code Created and designed by students of CSIT 5110 course @ HKUST
 * [ Marmik Shah, Aninda Choudhary, Vladislav Raznoschikov ]
 * The source code is available at GitHub -->
 * https://github.com/marmikshah/Data-Transfer-Via-Sound/tree/master
 *
 * Comments Legend
 * [ ] --> Description of objects
 * Area comment --> a section of objects, functions, etc of the same type
 * // --> A specific description for the next line or next few lines of code, either inside a function or while declaration of an instance, var, etc.
 *
 *  **/


/** Importing modules to record and manage Audio **/

/** Utility Imports **/
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 * Class Name -> Record
 * The purpose of this class is to enable the recorder and save recorded data into an array
 * Methods ->
 * 1. playTone() --> Creates a Thread t which handles the audio player.
 * 2. stopTone() --> Destroys the music player thread.
 * 3. generateSineInTimeDomain() --> Generates a sine wave based on the frequency that has been passed.
 *
 * **/

public class Record {

    /** General Data Types [Boolean, StringBuffer, ArrayList, Double] **/
    //Boolean
    private boolean hasAppended = false;
    private Boolean isRecording = false;


    //StringBuffer
    private StringBuffer tempString = new StringBuffer();
    private  StringBuffer message = new StringBuffer();


    //ArrayList <Type String>
    private ArrayList<String> frequencyToNumber = new ArrayList<String>();

    //Double
    private double frequency;

    /** Custom Classes [ASCIIBreaker , FrequencyScanner] **/
    private ASCIIBreaker builder = new ASCIIBreaker();
    private FrequencyScanner frequencyScanner = new FrequencyScanner();

    /** Thread  **/
    private Thread r;
    TargetDataLine line;


    /**
     *
     * The main function of this class that does the recording part
     * Flow of control
     * 1. Allocate memory to Thread r
     * ------Inside the thread run() definition function-----
     * 2. Create AudioRecorder object with respected parameters [Source Type, Sampling Rate, Channel Type, Encoding Type, Buffer Size (Sample Size)] and call startRecording() to let the MIC start recording
     * 3. Create a data array to store all the recorded samples
     * 4. Set an infinite while loop till user stops the MIC by clicking the "Stop Record" button.
     * 5. recorder.read() -> Get the recorded data into "data" array.
     * 6. If the data array holds values, extract the frequency from that array
     * 7. Check range of the frequency. If it is between [896, 5770] then convert that frequency to a number using the ASCIIBreaker class. It will convert every frequency to its specific digit value and return an Integer.
     * 8. Check range of the frequency. If it is between [6900, 7100] then it denotes that all the digits of the ASCII value of one character have been played. Convert that number to its character value and append it to the main message string.
     * 9. Check range of the frequency. If it is between [8970, 9030] then it denotes that one specific digit has finished playing and switch to the next digit.
     * 10. Otherwise, print the temporary string.
     * 11. Call the recorder.stop() function to disable the MIC.
     * 12. Release recorder object.
     * ------Exited the thread run() definition function()
     * 10. start the thread r.start()
     *
     * **/
    AudioFormat getAudioFormat() {
    	float sampleRate = 4410;
    	int sampleSizeInBits = 16;
    	// 1 for mono and 2 for stereo  
    	int channels = 1;
    	boolean signed = true;
    	boolean bigEndian = false;
    	AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,channels, signed, bigEndian);
    	return format;
    }
    void startRecording(){
        message = new StringBuffer();
        isRecording = true;

        r = new Thread(){

            public void run(){

                setPriority(Thread.MAX_PRIORITY);

//                int minBufferSize = AudioRecord.getMinBufferSize(44100,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
//
//                AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
//               
//                recorder.startRecording();
//                TargetDataLine line;
            	System.out.println(" in loop ");

                DataLine.Info info = new DataLine.Info(TargetDataLine.class, 
                		getAudioFormat() ); // format is an AudioFormat object
                	if (!AudioSystem.isLineSupported(info)) { 
                		System.out.println("not supported");
                	}
                	// Obtain and open the line.
                	try {
                	    line = (TargetDataLine) AudioSystem.getLine(info);
                	    line.open(getAudioFormat());
                	} catch (LineUnavailableException ex) {
                	   ex.printStackTrace();
                	}

//                short data[] = new short[minBufferSize];
                	byte[] data = new byte[line.getBufferSize() / 5];

                while(isRecording) {
                    // byte array not short array 
//                	System.out.println(isRecording + " in loop ");

                    line.read(data, 0, data.length);
                    // change from byte array to short array 
                    
                    System.out.println(Arrays.toString(data));
                    short[] shorts = new short[data.length/2];
                    // to turn bytes to shorts as either big endian or little endian. 
                    ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
                    if(shorts[0] != 0 ) {
//                    	System.out.println("if condition ");
                        frequency = frequencyScanner.extractFrequency(shorts, 44100);

                        if(frequency >= 896 && frequency <= 5770 && !hasAppended ) {
                            int num = builder.frequencyToNumber((int) frequency);
                            System.out.println("Num : " + num);
                            tempString.append(num);
                            hasAppended = true;
                        } else if (frequency > 6900 && frequency < 7100){

                            //End of a specific character
                            if(!tempString.toString().equals("")) { 
                            	message.append((char)Integer.parseInt(tempString.toString())); }
                            	System.out.println(message);

                            //Add converted character to ArrayList
                            	frequencyToNumber.add(tempString.toString());

                            //Remove reference of the tempString from its orginal value and re-allocate a new reference to an instance
                            	tempString = new StringBuffer();

                            	System.out.println("Fre --> Num : " + tempString);
                            
                        } else if(frequency >= 8970 && frequency <= 9030) {
                            // End of one digit
                            hasAppended = false;
                        } else {
                            System.out.println(tempString);
                        }
                    }

                }

                line.stop();
                line.close();
            }
        };
        r.start();
    }


    /**
     *
     * This method will be called when the user clicks on "Stop Recording" button
     * try to join the thread.
     * If it fails, do default stack trace printing.
     * Remove thread reference
     * @return String -> message will be returned to the Main Activity which is then displayed on the screen.
     *
     * **/

    String stopRecording(){
        isRecording = false;
        try {
            r.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        r = null;
        return message.toString();
    }


}
