package com.tool;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

/** Class to store objects from survey.xml tagged as Answer*/
class Answer
{
    public String[] Text;
    public String[] Score;
    public String[] NextQuestionID;
    
    
    /** Answer Class constructor
     * @param Text - Answer text, to appear on the answer button.
     * @param Score - The score of selecting this Answer, used in determining results.
     * @param NextQuestionID - ID of the next question to be asked when selecting this answer. */
    public Answer(String[] Text, String[] Score, String[] NextQuestionID)
    {
    	this.Text = Text;
    	this.Score = Score;
    	this.NextQuestionID = NextQuestionID;
    }
}

/** Class to store objects from suvey.xml tagged as Question */
class Question
{
    public String ID;
    public String Text;

    /** Question class constructor.
     * @param ID - ID of this question.
     * @param Text - Text of this question. */
    public Question(String ID, String Text)
    {
    	this.ID = ID;
    	this.Text = Text;
    }
}

/** Class to store objects from suvey.xml tagged as Help*/
class Help
{
	public String Text;
	
	/** Class to store help text of a question.
	 * @param Text - Help text to be displayed when help button is pushed.	 */
	public Help(String Text)
	{
		this.Text = Text;	
	}
	
}

/** Class to store the question and it's associated answers and help text*/
class QuestionAnswer
{
	public Question question;
	public Answer answer;
	public Help help;
	
	/**
	 * Default constructor of QuestionAnswer class, nothing is done.
	 */
	public QuestionAnswer()
	{
		
	}
	
	/**
	 * Constructor of QuestionAnswer class.
	 * @param question - Question object
	 * @param answer - Answer object
	 * @param help - Help object
	 */
	public QuestionAnswer(Question question, Answer answer, Help help)
	{
		this.question = question;
		this.answer = answer;
		this.help = help;
		
	}
}

/** Class to store from the survey.xml objects tagged as Result*/
class Result
{
   // public String ID;
    public String FromScore;
    public String ToScore;
    public String Text;
    
    public Result()
    {
    	
    }
    
    public Result(String FromScore, String ToScore, String Text)
    {
    	this.FromScore = FromScore;
    	this.ToScore = ToScore;
    	this.Text = Text;
    }
}


/** Main method of the app.*/
public class RiskToolActivity extends Activity {
	Button button1, button2, restart_button, back_button, button3, button4, continue_button, help_button;
	
	// object for button push feedback
	private Vibrator vibrate;
	
    TextView myXmlContent, initial;
    ScrollView scroll_view;
    
    // variable to hold the current question object
	 static Question qs;
	 
	// variable to hold the current answer object
	 static Answer ans;
	 
	// current app supports up to 4 answers per question, 'ans_al' variable holds the array of answers
	static Answer[] ans_al = new Answer[4];	  
	
	// the app first reads the xml file and stores all the question/answers into an array list stored here in 'qa'
	static ArrayList<QuestionAnswer> qa =new ArrayList<QuestionAnswer>();
	
	int qa_size;
	StringBuffer strbf;
	
	static int score, qs_count = 0;
	private static final String TAG = "MyActivity";
	// string to store the next question's ID
	String nextqsid = new String();
	// string to store the current question's ID
	String thisqsid = new String();
	
	static ArrayList<Result> result  =new ArrayList<Result>();;
	
  // variable to hold the current QuestionAnswer object	
  QuestionAnswer qa_obj;
  
  String stringXmlContent;
  
  // variable to hold the current Result object
  Result res_obj ;
  
  String text_disp = new String();
  static String splash = new String();
  static String title = new String();
  static String initial_id = new String();

  // alert dialog boxes
  static AlertDialog back_exit_alert;
  static AlertDialog help_text_alert;
  
	    
	/** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
	   
       super.onCreate(savedInstanceState);
       setContentView(R.layout.main);    
     
       this.myXmlContent = (TextView)findViewById(R.id.my_xml);
       this.initial = (TextView)findViewById(R.id.initial);
       


       
       myXmlContent.setVisibility(View.INVISIBLE);
  	 
      this.scroll_view = (ScrollView)findViewById(R.id.scroller);
       initial.setTextSize(18);
       this.button1 = (Button)this.findViewById(R.id.yes);
       this.button2 = (Button)this.findViewById(R.id.no);
       this.restart_button = (Button)this.findViewById(R.id.restart);
       this.button3 = (Button)this.findViewById(R.id.button3);
       this.button4 = (Button)this.findViewById(R.id.button4);
       this.back_button = (Button)this.findViewById(R.id.back);
       this.continue_button = (Button)this.findViewById(R.id.continu);
       this.help_button = (Button)this.findViewById(R.id.help);
       
             
  try {
	   // parse and retrieve data from the xml
	   getEventsFromAnXML(this); 
	   
	   // initialize the app's screen
	   Initialize_screen();
	   
	   //the number of questions there are
	   qa_size = qa.size();    
	   
	   
	   // button1's click listener
       button1.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
       	         	 
        	 // button1's functionality
        	 answerButton1();
      	
         }
       }); 
       
       // button1's on touch listener (button vibration on click)     
       button1.setOnTouchListener(new View.OnTouchListener() {		
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
		    
			
			if (arg1.getAction() == MotionEvent.ACTION_DOWN){
	 		    // vibrate on touch
	 		    vibrate.vibrate(50);
			}
			
						
			return false;
		}
	});
           
     
     // button2's click listener
       button2.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
        	 
        	// button2's functionality
        	answerButton2();        	 
       	
         }
       });  
       
       // button2's on touch listener (button vibration on click)     
       button2.setOnTouchListener(new View.OnTouchListener() {		
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
		
			if (arg1.getAction() == MotionEvent.ACTION_DOWN){			
	 		    // vibrate on touch
	 		    vibrate.vibrate(50);
			}
						
			return false;
		}
	});
       
     
     // button3's click listener
     button3.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {  
        	 
        	// button3's functionality
        	answerButton3();
       	
         }
     });
     
     // button3's on touch listener (button vibration on click)     
     button3.setOnTouchListener(new View.OnTouchListener() {		
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			
			if (arg1.getAction() == MotionEvent.ACTION_DOWN){			
	 		    // vibrate on touch
	 		    vibrate.vibrate(50);
			}
				
			return false;
		}
	});
   
     // button4's click listener
     button4.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
        	 
        	// button4's functionality
        	answerButton4();
       	
         }
     });
     
     // button4's on touch listener (button vibration on click)     
     button4.setOnTouchListener(new View.OnTouchListener() {		
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			
			if (arg1.getAction() == MotionEvent.ACTION_DOWN){			
	 		    // vibrate on touch
	 		    vibrate.vibrate(50);
			}
						
			return false;
		}
	});
     
     
     // back button's click listener
     back_button.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
        	 
        	// back button's functionality
        	backButton();
       	
         }
         
     });
     
     // back button's on touch listener (button vibration on click)     
     back_button.setOnTouchListener(new View.OnTouchListener() {		
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			
			if (arg1.getAction() == MotionEvent.ACTION_DOWN){			
	 		    // vibrate on touch
	 		    vibrate.vibrate(50);
			}
						
			return false;
		}
	});
     
     
     // restart button's click listener
     restart_button.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
        	 
  		    // restart button's functionality
        	restartButton();
        	 
         }
     });
     
     // back button's on touch listener (button vibration on click)     
     back_button.setOnTouchListener(new View.OnTouchListener() {		
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {		    
			
			if (arg1.getAction() == MotionEvent.ACTION_DOWN){			
	 		    // vibrate on touch
	 		    vibrate.vibrate(50);
			}
						
			return false;
		}
	});
     
     // restart button's on touch listener (button vibration on click)     
     restart_button.setOnTouchListener(new View.OnTouchListener() {		
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			
			if (arg1.getAction() == MotionEvent.ACTION_DOWN){			
	 		    // vibrate on touch
	 		    vibrate.vibrate(50);
			}
						
			return false;
		}
	});
     
     // help button's click listener
     help_button.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
        	 
  		    // help text button functionality
        	helpTextButton();

         }
     }); 
     
     
     // help button's on touch listener (button vibration on click)     
     help_button.setOnTouchListener(new View.OnTouchListener() {		
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			
			if (arg1.getAction() == MotionEvent.ACTION_DOWN){			
	 		    // vibrate on touch
	 		    vibrate.vibrate(50);
			}
						
			return false;
		}
	});
     
     // continue button's click listener
     continue_button.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {       	 
        	
  		    // continue button functionality
        	continueButton();
        	 
        	}
     });   
     
     // continue button's on touch listener (button vibration on click)     
     continue_button.setOnTouchListener(new View.OnTouchListener() {		
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			
			if (arg1.getAction() == MotionEvent.ACTION_DOWN){			
	 		    // vibrate on touch
	 		    vibrate.vibrate(50);
			}
						
			return false;
		}
	});
     
     
  } catch (XmlPullParserException e) {
   // TODO Auto-generated catch block
   e.printStackTrace();
  } catch (IOException e) {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }        
          	
 }
   
   
   
   
  /** This method overrides the hardware back button on android to mimic functionality of the on screen back button. */
   @Override
   public void onBackPressed() {
     	 // check to make sure we're not on disclaimer page
	   if(!(continue_button.getVisibility()==View.VISIBLE))
	   {
		   
		   thisqsid = qa_obj.question.ID;
		   // if not on the first question, mimic back button
		   if (!thisqsid.equals(initial_id)){
			   backButton();
		   }
		   // else, prompt to exit out of app
		   else{
			   back_exit_alert.show();
		   }
		   
	   }
	   	   
	   // hardware back button pushed on disclaimer page, exit
	 else{
	  	this.finish();
	 }
	 
    	
   }

   
/** This method is called at the beginning to hide the buttons from view and initialize various other variables.*/
 public void Initialize_screen()
 {	
	 initial.setText(splash);
	 button1.setVisibility(View.INVISIBLE);
	 button2.setVisibility(View.INVISIBLE);
	 button3.setVisibility(View.INVISIBLE);
	 button4.setVisibility(View.INVISIBLE);
	 restart_button.setVisibility(View.INVISIBLE);
	 back_button.setVisibility(View.INVISIBLE);
	 help_button.setVisibility(View.INVISIBLE);
	 
     restart_button.setText("RESTART");
     back_button.setText("BACK");
     continue_button.setText("CONTINUE");
	 
     // feedback object initialization
     vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
     
     // alert message for exiting out of the app using the back button
     AlertDialog.Builder builder = new AlertDialog.Builder(this);
     builder.setMessage("Are you sure you want to exit?")
            .setCancelable(false)
            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
              	  RiskToolActivity.this.finish();
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
              	  dialog.cancel();
                }
            });
     back_exit_alert = builder.create();
	 
 }
 
 /** Method called after the continue button is pushed on the splash screen, retrieves the first question marked as "initial question" */
 public void getFirstQs()
 {
	 back_button.setVisibility(View.INVISIBLE);
	 restart_button.setVisibility(View.INVISIBLE);

	 QuestionAnswer qa_obj = reallyGetNextQs(initial_id); 
	 
	  myXmlContent.setText(qa_obj.question.Text);	  		 
	  	  
   // check xml for answer 1
   if(!(qa_obj.answer.Text[0] == null))
   {
  	 button1.setVisibility(View.VISIBLE);
  	 button1.setText(qa_obj.answer.Text[0]);

   }
   else
   {
  	 button1.setVisibility(View.INVISIBLE);
   }
   
   // check xml for answer 2    
   if(!(qa_obj.answer.Text[1] == null))
   {
  	 button2.setVisibility(View.VISIBLE);
  	 button2.setText(qa_obj.answer.Text[1]);

   }
   else
   {
 	  button2.setVisibility(View.INVISIBLE);
   }
    
   // check xml for answer 3
   if(!(qa_obj.answer.Text[2] == null))
   {
  	 button3.setVisibility(View.VISIBLE);
  	 button3.setText(qa_obj.answer.Text[2]);

   }
   else
   {
  	 button3.setVisibility(View.INVISIBLE);
   }
   
   // check xml for answer 4    
   if(!(qa_obj.answer.Text[3] == null))
   {
  	 button4.setVisibility(View.VISIBLE);
  	 button4.setText(qa_obj.answer.Text[3]);

   }
   else
   {
 	  button4.setVisibility(View.INVISIBLE);
   }
   
   // set help text
   if((qa_obj.help.Text == ""))
   { 	
 	   help_button.setText("");
	  	help_button.setVisibility(View.INVISIBLE);

   }
   
 }

/** After an answer has been selected, gets the next question.
 * @param nextqsid - ID of the next question to be retrieved. */
 public void getNextQuestion(String nextqsid){
		 
	 // set back and restart buttons to visible
	 back_button.setVisibility(View.VISIBLE);
	 restart_button.setVisibility(View.VISIBLE);
	 
	 // set the current question variables to the next question
	 QuestionAnswer qa_obj = reallyGetNextQs(nextqsid); 	  
	 
	  myXmlContent.setText(qa_obj.question.Text);	  
		 
	   // check xml for answer 1
	   if(!(qa_obj.answer.Text[0] == null))
	   {
	  	 button1.setVisibility(View.VISIBLE);
	  	 button1.setText(qa_obj.answer.Text[0]);

	   }
	   else
	   {
	  	 button1.setVisibility(View.INVISIBLE);
	   }
	   
	   // check xml for answer 2    
	   if(!(qa_obj.answer.Text[1] == null))
	   {
	  	 button2.setVisibility(View.VISIBLE);
	  	 button2.setText(qa_obj.answer.Text[1]);

	   }
	   else
	   {
	 	  button2.setVisibility(View.INVISIBLE);
	   }
     
     // check xml for answer 3
     if(!(qa_obj.answer.Text[2] == null))
     {
    	 button3.setVisibility(View.VISIBLE);
    	 button3.setText(qa_obj.answer.Text[2]);

     }
     else
     {
    	 button3.setVisibility(View.INVISIBLE);
     }
     
     // check xml for answer 4    
     if(!(qa_obj.answer.Text[3] == null))
     {
    	 button4.setVisibility(View.VISIBLE);
    	 button4.setText(qa_obj.answer.Text[3]);

     }
     else
     {
   	  button4.setVisibility(View.INVISIBLE);
     }
     
     // set help text
     if(!(qa_obj.help.Text == ""))
     {
    	 	help_button.setVisibility(View.VISIBLE);
    	 	help_button.setText("Help");

     }
     else
     {
  	   help_button.setText("");
   	  	help_button.setVisibility(View.INVISIBLE);
     }
	  
 }
 
 /** This method sets the QuestionAnswer object variables visible to the rest of the app.
  * @param nextqsid - ID of the next question to be retrieved.
  * @return QuestionAnswer - Contains the next question. */
 public QuestionAnswer reallyGetNextQs(String nextqsid)
 {
	  for(int i =0; i< qa_size; i++)
	  {
		  qa_obj = new QuestionAnswer();
	      qa_obj = qa.get(i);
	      
		  if(qa_obj.question.ID.equals(nextqsid))
		  {
			break;			  
		  }
			  
	  }//end for
	  
	  return qa_obj;
	  
 }
 
 /** This method is called when hitting the 'back' button, retrieves the previous question.
  * @param thisqsqsid - ID of the current question.*/
 public void getPreviousQuestion(String thisqsqsid)
 {
	 
	 QuestionAnswer new_qa_obj ;
	 
	 // If the app is not on the initial ID and there is a current question, retrieve the previous question
	  if(text_disp.equals("") && !(thisqsid.equals(initial_id)))
		  
		  // retrieve and set the previous question
		  new_qa_obj = reallyPreviousQs(thisqsqsid); 
	  
	  // set the returning question answer object to itself if no previous question is available
	  else 
		  {
		  text_disp = new String();
		  new_qa_obj = qa_obj;
		  }
	  
	  // if on initial question, hide the restart and back buttons
	  if (new_qa_obj.question.ID.equals(initial_id)){
			  back_button.setVisibility(View.INVISIBLE); 
			  restart_button.setVisibility(View.INVISIBLE);
	  }
	 
	 
	  myXmlContent.setText(new_qa_obj.question.Text);	  
	  
	   // check xml for answer 1
	   if(!(qa_obj.answer.Text[0] == null))
	   {
	  	 button1.setVisibility(View.VISIBLE);
	  	 button1.setText(qa_obj.answer.Text[0]);

	   }
	   else
	   {
	  	 button1.setVisibility(View.INVISIBLE);
	   }
	   
	   // check xml for answer 2    
	   if(!(qa_obj.answer.Text[1] == null))
	   {
	  	 button2.setVisibility(View.VISIBLE);
	  	 button2.setText(qa_obj.answer.Text[1]);

	   }
	   else
	   {
	 	  button2.setVisibility(View.INVISIBLE);
	   }
   
	   // set answer 3
	   if(!(new_qa_obj.answer.Text[2] == null))
	   {
		   button3.setVisibility(View.VISIBLE);
		   button3.setText(new_qa_obj.answer.Text[2]);
		  
	   }
	   else
	   {
		   button3.setVisibility(View.INVISIBLE);
	   }
   
	   // set answer 4
	   if(!(new_qa_obj.answer.Text[3] == null))
	   {
		   button4.setVisibility(View.VISIBLE);
		   button4.setText(new_qa_obj.answer.Text[3]);
	   }
	   else
	   {
		   button4.setVisibility(View.INVISIBLE);
	   }
   
	   // set help text
	   if(!(qa_obj.help.Text == ""))
	   {
		   help_button.setVisibility(View.VISIBLE);
		   help_button.setText("Help");

	   }
	   else
	   {
		   help_button.setText("");
		   help_button.setVisibility(View.INVISIBLE);
	   }
	  
 }
 
 /** This method sets the QuestionAnswer object visible to the rest of the app and returns the previous question.
  * @param thisqsid - ID of the question the app is currently on.
  * @return - The QuestionAnswer object previous of the current question. */
 public QuestionAnswer reallyPreviousQs(String thisqsid)
 {
	 outerloop:
	  for(int i =0; i< qa_size; i++)
	  {
		  qa_obj = new QuestionAnswer();
	      qa_obj = qa.get(i);
	      
	      
	      for(int j = 0; j < 4; j++)
	      {
	    	  if(!(qa_obj.answer.NextQuestionID[j] == null))
	    	  {
	    		  if(qa_obj.answer.NextQuestionID[j].equals(thisqsid))
	    		  {
	    			  break outerloop;			  
	    		  }
	    	  }
	      }//end for j  
	            
			  
	  }//end for
	  
 return qa_obj;
	  
 }
 
 /** At the end of the question line, this method is called to retrieve the result.
  * @param score - Score of the app, the result retrieved is based on the score. */
 public void getResult(int score)
 {
		  
	  for(int i=0; i < result.size(); i++)
	  {
		  res_obj = new Result();
		  res_obj.FromScore = result.get(i).FromScore;
		  res_obj.ToScore = result.get(i).ToScore;
		  
		  if(score >= Integer.parseInt(res_obj.FromScore))
		  {
			  if(res_obj.ToScore.equals("*"))
			  {
				  text_disp = new String(result.get(i).Text);
				  break;  
			  }
			  else if( score <=  Integer.parseInt(res_obj.ToScore))
			  {
			  text_disp = new String(result.get(i).Text);
			  break;
			  }
			 
		  }  
		   		  
	  }//end for
	  
 
	  myXmlContent.setVisibility(View.INVISIBLE);
	  scroll_view.setVisibility(View.VISIBLE);
	  initial.setVisibility(View.VISIBLE);
	  initial.setText(text_disp);
	  Linkify.addLinks(initial, Linkify.ALL);
	  
	  help_button.setVisibility(View.INVISIBLE);
	  
		  
 }
 
 /** This method parses through the input survey.xml and stores the question/answers/splash etc. into objects and arrays to be read by the app.
  * @param activity - the source activity.
  * @throws XmlPullParserException
  * @throws IOException*/
   public static void getEventsFromAnXML(Activity activity)
           throws XmlPullParserException, IOException
           {
   		
       	    Resources res = activity.getResources();
            XmlResourceParser xpp = res.getXml(R.xml.survey);
            String str = new String();
            String qsid = new String();
            String qstext = new String();
            String[] text = new String[4];
            String[] score = new String[4];
            String[] nextqsid = new String[4];
            boolean found_splash = false;
            boolean found_result = false;
            String helptext = "";
                
            String result_text = new String();
            String result_to = new String();
            String result_from = new String();
            int ans_cnt =0, qa_cnt = 0, res_cnt = 0;
            
            while (xpp.getEventType()!=XmlPullParser.END_DOCUMENT)        	 
           		
            {            	
                if (xpp.getEventType()==XmlPullParser.START_TAG)
                {
                  if (xpp.getName().equals("Question"))
                  {
               	    qsid = xpp.getAttributeValue(0);
               	    qstext = xpp.getAttributeValue(1);
               	                  	   
                  }
                  if (xpp.getName().equals("Answer"))
                  {
               	   
               	    text[ans_cnt] = xpp.getAttributeValue(0);                	  
               	    score[ans_cnt] =  xpp.getAttributeValue(1);
               	    nextqsid[ans_cnt] =  xpp.getAttributeValue(2);
               	    
               	    ans_cnt++;                    
                  }
                  if (xpp.getName().equals("Help"))
                  {
               	    helptext = xpp.getAttributeValue(0);                	                  	   
                  }
                  
                if (xpp.getName().equals("Result"))
                  {
                	result_from = xpp.getAttributeValue(0);
                	result_to =xpp.getAttributeValue(1);
                	found_result = true;
               	  
               	                  	   
                  }
                if (xpp.getName().equals("Splash"))
                {
                	found_splash = true;
                	
                }
                
                if (xpp.getName().equals("Survey"))
                {
                	title = xpp.getAttributeValue(0);
                	initial_id = xpp.getAttributeValue(1);
                }
                
                  
               }//end if start_tag
                
                if (xpp.getEventType()==XmlPullParser.TEXT)
                {
                	if(found_splash == true)
                	{
                	splash = xpp.getText();
                	Log.d(TAG, "splash is "+splash);
                	}
                	
                	if(found_result == true)
                	{
                	result_text = xpp.getText();
                	}
                }
            	
                if (xpp.getEventType()==XmlPullParser.END_TAG)    
                {
               	 if(xpp.getName().equals("Question"))
               	 {
               //	(new Answer(text[ans_cnt], score[ans_cnt], nextqsid[ans_cnt]));
               		 qa.add(new QuestionAnswer(new Question(qsid, qstext), new Answer(text, score, nextqsid), new Help(helptext)));
               		 
               		
               		 //reset all values
               		 
               		 qsid = ""; qstext = ""; helptext = "";
               		 text = new String[4]; score =new String[4];	nextqsid = new String[4];
               		 
               		 ans_cnt = 0; 
               		 qa_cnt ++;            
               	 } // end if question
               	 
               	if(xpp.getName().equals("Splash"))
               	{
               		found_splash = false;
               	}
               	
            	if(xpp.getName().equals("Result"))
               	{
            		 result.add(new Result(result_from, result_to, result_text)) ;
            		 result_to = new String();
            		 result_from = new String();
            		 result_text = new String();
            		 
            		 res_cnt++;
               		found_result = false;
               	}
                }                               
                xpp.next();
              }//endwhile           
          }//end geteventsFromAnXML
   
   
   /* button functionality methods, functionality was defined outside of the
      onClick methods so we can have multiple buttons do the same thing and with
      a common method for easier modification  
   */ 
   
   /** Method to handle when the 1st answer button is pushed.   */
   public void answerButton1(){	      
	   	   
		score = new Integer (qa_obj.answer.Score[0]);
		qs_count ++;
    	  
		if(qs_count < qa_size)
		{
    		  
			nextqsid = qa_obj.answer.NextQuestionID[0];
			if(nextqsid.equals(""))
			{
				button1.setVisibility(View.INVISIBLE) ;
				button2.setVisibility(View.INVISIBLE) ;
				button3.setVisibility(View.INVISIBLE) ;
				button4.setVisibility(View.INVISIBLE) ;
    			 
    			  
				getResult(score);
   
			}        	  
			else
			{
				getNextQuestion(nextqsid);
    			  
			}
		}//end if(qs_count < qa_size)    	      
   
   }
   
   /** Method to handle when the 2nd answer button is pushed.   */
   public void answerButton2(){	   
     	  
     	  score = new Integer (qa_obj.answer.Score[1]);
     	  qs_count ++;
     	  
     	  if(qs_count < qa_size)
     	  {
     		  nextqsid = qa_obj.answer.NextQuestionID[1]; 
     		  
     		// if the next question is blank, the app has reached the results
     		  if(nextqsid.equals(""))
         	  {
         		button1.setVisibility(View.INVISIBLE) ;
         		button2.setVisibility(View.INVISIBLE) ;
         		button3.setVisibility(View.INVISIBLE) ;
         		button4.setVisibility(View.INVISIBLE) ;
         		
         		 //displaying the result
         		 getResult(score);
         	
         	  }
     		  else
     		  {        	        		
     			  getNextQuestion(nextqsid);
     			 
     		  }
     	  }//if(qs_count < qa_size)
   }
   
   /** Method to handle when the 3rd answer button is pushed.   */
   public void answerButton3(){
  	        	 
 	  score =  new Integer (qa_obj.answer.Score[2]);
 	  qs_count ++;
 	  
 	  if(qs_count < qa_size)
 	  {
 		  
 		  nextqsid = qa_obj.answer.NextQuestionID[2];
 		  
 		  // if the next question is blank, the app has reached the results
 		  if(nextqsid.equals(""))
 		  {
 			  button1.setVisibility(View.INVISIBLE) ;
 			  button2.setVisibility(View.INVISIBLE) ;
 			  button3.setVisibility(View.INVISIBLE) ;
 			  button4.setVisibility(View.INVISIBLE) ;

      		 //displaying the result
 			  getResult(score);
 			 
 		  }        	  
 		  else
 		  {
 			  getNextQuestion(nextqsid);
 			
 		  }
 	  }//end if(qs_count < qa_size)
   }
   
   /** Method to handle when the 4th answer button is pushed.   */
   public void answerButton4(){
  	 
 	  score = new Integer (qa_obj.answer.Score[3]);
 	  qs_count ++;
 	  
 	  if(qs_count < qa_size)
 	  {
 		  
 		  nextqsid = qa_obj.answer.NextQuestionID[3];
 		  
 		  // if the next question is blank, the app has reached the results
 		  if(nextqsid.equals(""))
 		  {
 			  button1.setVisibility(View.INVISIBLE) ;
 			  button2.setVisibility(View.INVISIBLE) ;
 			  button3.setVisibility(View.INVISIBLE) ;
 			  button4.setVisibility(View.INVISIBLE) ;
 			  
 			  getResult(score);
 			 
 		  }        	  
 		  else
 		  {
 			  getNextQuestion(nextqsid);
 			  nextqsid = "";
 		  }
 	  }//end if(qs_count < qa_size)
	   
   }
   
   /** Method to handle when the restart button is pushed.   */
   public void restartButton(){
  	 
  	 myXmlContent.setVisibility(View.VISIBLE);
  	 scroll_view.setVisibility(View.INVISIBLE);
  	 initial.setVisibility(View.INVISIBLE);
  	 
  	 
 	  button1.setVisibility(View.VISIBLE) ;
		button2.setVisibility(View.VISIBLE) ;
 	  
 	 getFirstQs();
 	  score = 0;
 	  qs_count = 0;
 	  
 	 scroll_view.fullScroll(ScrollView.FOCUS_UP);
   }
   
   /** Method to handle when the back button is pushed.   */
   public void backButton(){
	    	 
  	             
       myXmlContent.setVisibility(View.VISIBLE);
       scroll_view.setVisibility(View.INVISIBLE);
       initial.setVisibility(View.INVISIBLE);
  	 

 	   qs_count --;
   	  
   	  if(qs_count < qa_size)
   	  {
   		  
   		  thisqsid = qa_obj.question.ID;
   		   	    		
   		  getPreviousQuestion(thisqsid);
   			  
   	  }//end if(qs_count < qa_size)
   	   	  
   	  text_disp = "";
   	  scroll_view.fullScroll(ScrollView.FOCUS_UP);
   }
   
   /** Method to handle when the text button is pushed.   */
   public void helpTextButton(){
  	 
       // alert dialog setup
       AlertDialog.Builder builder = new AlertDialog.Builder(RiskToolActivity.this);
       
       // set alert dialog text
       builder.setMessage(qa_obj.help.Text)
              .setCancelable(false)
              .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int id) {
                  	dialog.cancel();
                  }
              });   
       help_text_alert = builder.create();
       
       help_text_alert.show();
   }
   
   /** Method to handle when the continue button is pushed.   */
   public void continueButton(){
  	 
       myXmlContent.setVisibility(View.VISIBLE);
       initial.setVisibility(View.INVISIBLE);
  	 
       myXmlContent.setTextSize(22);
       continue_button.setVisibility(View.INVISIBLE);
       restart_button.setVisibility(View.VISIBLE);
       back_button.setVisibility(View.VISIBLE);
       button1.setVisibility(View.VISIBLE);
       button2.setVisibility(View.VISIBLE);
    
       // get the first question
       getFirstQs();
   	   scroll_view.fullScroll(ScrollView.FOCUS_UP);
       scroll_view.setVisibility(View.INVISIBLE);
   }
   
}
        
    

  
   
