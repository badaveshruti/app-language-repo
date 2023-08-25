package qnopy.com.qnopyandroid.androidjavamail;

import android.content.Context;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class GMail {

    private static final String TAG = "Gmail";
    final String emailPort ="465";//"587" ;// gmail's smtp port

    final String smtpAuth = "false";
    final String starttls = "true";
    final String emailHost = "smtp.gmail.com"; //"smtp.goaquablue.com";

    String fromEmail;
    String fromPassword;
    List<String> toEmailList;
    String emailSubject;
    String emailBody;
    List<String> attachFilename;

    Properties emailProperties;
    Session mailSession;
    MimeMessage emailMessage;
    private Multipart _multipart;

    public GMail() {

    }

    public GMail(String fromEmail, String fromPassword,
                 List<String> toEmailList, String emailSubject, String emailBody,
                 List<String> filename) {
        this.fromEmail = fromEmail;
        this.fromPassword = fromPassword;
        this.toEmailList = toEmailList;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;
        this.attachFilename = filename;

        _multipart = new MimeMultipart();

        emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", emailPort);
        emailProperties.put("mail.smtp.auth", smtpAuth);
        emailProperties.put("mail.smtp.starttls.enable", starttls);
        emailProperties.put("mail.smtp.debug", "true");
        Log.i(TAG, "Mail server properties set.");
    }

    public MimeMessage createEmailMessage() throws AddressException,
            MessagingException, UnsupportedEncodingException {

        mailSession = Session.getDefaultInstance(emailProperties, null);
        emailMessage = new MimeMessage(mailSession);

        emailMessage.setFrom(new InternetAddress(fromEmail, fromEmail));
        for (String toEmail : toEmailList) {
            Log.i(TAG, "toEmail: " + toEmail);
            emailMessage.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(toEmail));
        }

        emailMessage.setSubject(emailSubject);
        emailMessage.setSentDate(new Date());

//		emailMessage.setContent(emailBody, "text/html");// for a html email
        try {
// emailMessage.setText(emailBody);// for a text email
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(emailBody);
            _multipart.addBodyPart(messageBodyPart);
            if (attachFilename != null) {
                for (int i = 0; i < attachFilename.size(); i++) {
                    try {
                        System.out.println("ggg" + "Adding attachment" + this.attachFilename.get(i));
                        this.addAttachment(this.attachFilename.get(i));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            emailMessage.setContent(_multipart);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in createEmailMessage:" + e.getMessage());
        }


        Log.i(TAG, "Email Message created.");
        return emailMessage;
    }

    public void addAttachment(String filename) throws Exception {
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        String CurrentDate = this.getCurrentDateTime();

//		    int index = filename.indexOf('.');
//	    	if (filename.contains(".csv")) {
//	    		String fieldName = filename.substring(0, index)+CurrentDate+".csv";
//	    		messageBodyPart.setFileName(fieldName);
//	    	} else {

        String[] name = filename.split("/");
        if (name != null) {
            if (name.length != 0) {
                messageBodyPart.setFileName(name[name.length - 1]);
            } else {
                messageBodyPart.setFileName(filename);
            }
        } else {
            messageBodyPart.setFileName(filename);
        }
//	    	}
        _multipart.addBodyPart(messageBodyPart);
    }

    public String getCurrentDateTime() {
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        return currentDateTimeString;
    }

    public void sendEmail(Context context) throws AddressException, MessagingException {
        System.out.println("mmmm" + "sendEmail");
        try {
            Transport transport = mailSession.getTransport("smtps");
            transport.connect(emailHost, Integer.parseInt(emailPort), fromEmail, fromPassword);
            Log.i(TAG, "allrecipients: " + emailMessage.getAllRecipients());
            transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
            transport.close();
            Log.i(TAG, "Email sent successfully.");
        } catch (Exception e) {
            if (e!=null){
                e.printStackTrace();
            }
            Log.e(TAG, "SendEmail Exception-" + e.getLocalizedMessage());
            //Toast.makeText(context,"Error in send email:"+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }


}
