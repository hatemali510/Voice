
public class ChatWindow {

	static String message;
//	private static boolean isRecording = false;


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Record recorder = new Record();

//		if (isRecording) {
//			isRecording = false;
//			message = new String();
//			try {
//				// 
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			message = recorder.stopRecording();
//		} else {
//			isRecording = true;
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		recorder.startRecording();

	}

}
