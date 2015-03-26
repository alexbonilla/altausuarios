/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.concesiones.utils;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author Alex
 */
public class SendFileEmail implements Runnable {

    private String to;
    private String from;
    private String user;
    private String pwd;
    private String host;
    private String port;
    private String messageSubject;
    private String messageBody;
    private DataSource source = null;
    private String filename;
    private String starttls="false";
    private String ssl="false";
    private String auth="false";

    /**
     *
     * @return
     */
    public String getTo() {
        return to;
    }

    /**
     *
     * @param to
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     *
     * @return
     */
    public String getFrom() {
        return from;
    }

    /**
     *
     * @param from
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     *
     * @return
     */
    public String getUser() {
        return user;
    }

    /**
     *
     * @param user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     *
     * @return
     */
    public String getPwd() {
        return pwd;
    }

    /**
     *
     * @param pwd
     */
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    /**
     *
     * @return
     */
    public String getHost() {
        return host;
    }

    /**
     *
     * @param host
     */
    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    /**
     *
     * @return
     */
    public String getMessageSubject() {
        return messageSubject;
    }

    /**
     *
     * @param messageSubject
     */
    public void setMessageSubject(String messageSubject) {
        this.messageSubject = messageSubject;
    }

    /**
     *
     * @return
     */
    public String getMessageBody() {
        return messageBody;
    }

    /**
     *
     * @param messageBody
     */
    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    /**
     *
     * @return
     */
    public DataSource getSource() {
        return source;
    }

    /**
     *
     * @param source
     */
    public void setSource(DataSource source) {
        this.source = source;
    }

    /**
     *
     * @return
     */
    public String getFilename() {
        return filename;
    }

    /**
     *
     * @param filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getStarttls() {
        return starttls;
    }

    public void setStarttls(String starttls) {
        this.starttls = starttls;
    }

    public String getSsl() {
        return ssl;
    }

    public void setSsl(String ssl) {
        this.ssl = ssl;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    @Override
    public void run() {
        Logger.getLogger(SendFileEmail.class.getName()).log(Level.INFO, "Sending message....");
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.user", user);
        properties.setProperty("mail.password", pwd);
        properties.setProperty("mail.smtp.user", user);
        properties.setProperty("mail.smtp.password", pwd);
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", port);
        properties.setProperty("mail.smtp.starttls.enable", starttls);
        properties.setProperty("mail.smtp.ssl.enable", ssl);
        properties.setProperty("mail.smtp.auth", auth);
        properties.setProperty("mail.debug", "false");

        Session session = Session.getDefaultInstance(properties);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));

            // Set Subject: header field
            message.setSubject(messageSubject);

            // Create the message part 
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
//            messageBodyPart.setText(messageBody);
            messageBodyPart.setContent(messageBody, "text/html; charset=utf-8");

            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            if (source != null) {
                // Part two is attachment
                messageBodyPart = new MimeBodyPart();

                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(filename);
                multipart.addBodyPart(messageBodyPart);

            }
            // Send the complete message parts
            message.setContent(multipart);
            // Send message
            Transport.send(message);
            Logger.getLogger(SendFileEmail.class.getName()).log(Level.INFO, "Sent message successfully....");
        } catch (MessagingException mex) {
            Logger.getLogger(SendFileEmail.class.getName()).log(Level.SEVERE, null, mex);
        }
    }

}
