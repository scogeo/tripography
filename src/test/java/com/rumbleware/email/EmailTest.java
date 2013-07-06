package com.rumbleware.email;

import com.amazonaws.services.simpleemail.AWSJavaMailTransport;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @author gscott
 */
@Ignore
public class EmailTest {

    private static final Logger logger = LoggerFactory.getLogger(EmailTest.class);

    @Test
    public void testEmail() {
        MimeMessage msg;
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "aws");

        // Build server role
        //props.setProperty("mail.aws.user", "ASIAJWENGX7WMQ23Y36A");
        //props.setProperty("mail.aws.password", "GXuhaZoKo1FZI/HiGrEeduzQ7UQxMzo1lYNadxNB");

        logger.info("Using properties " + props);

        Session session = Session.getDefaultInstance(props, null);
        try {
            msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress("no-reply@tripography.com", "Tripography"));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress("george.scott@gmail.com"));
            msg.setSubject("test");
            msg.setText("Hello");

            // send the message by JavaMail
            Transport transport = new AWSJavaMailTransport(session, null);

            transport.connect();
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();
        }
        catch (Exception e) {
            // log i
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

}
