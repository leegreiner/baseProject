package edu.duke.rs.baseProject.email;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;

@Service
public class EmailServiceImpl implements EmailService {
  private transient final JavaMailSender mailSender;
  private transient MessageContentBuilder contentBuilder;
  @Value("${app.defaultEmailFrom:DoNotReply@nowhere.com}")
  private String defaultEmailFrom;
  
  public EmailServiceImpl(final JavaMailSender mailSender, final MessageContentBuilder contentBuilder) {
    this.mailSender = mailSender;
    this.contentBuilder = contentBuilder;
  }
  
  @Override
  public void send(final MessageType messageType, final String to, final String subject,
      final Map<String, Object> content, final Collection<File> attachments) {
    this.send(messageType, List.of(to), Collections.emptyList(), Collections.emptyList(), null, subject, content,
        attachments);  
  }
  
  @Override
  public void send(final MessageType messageType, final Collection<String> to, final Collection<String> cc,
      final Collection<String> bcc, final String from, final String subject, final Map<String, Object> content,
      final Collection<File> attachments) {
    final MimeMessagePreparator messagePreparator = mimeMessage -> {
      MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,
          attachments == null || attachments.size() == 0 ? false : true);
      messageHelper.setFrom(StringUtils.isBlank(from) ? defaultEmailFrom : from);
      messageHelper.setTo(to.stream().toArray(String[]::new));
      
      if (cc != null && cc.size() > 0) {
        messageHelper.setCc(cc.stream().toArray(String[]::new));
      }
      if (bcc != null && bcc.size() > 0) {
        messageHelper.setBcc(bcc.stream().toArray(String[]::new));
      }
      messageHelper.setSubject(subject);
      messageHelper.setText(contentBuilder.build(messageType, content == null ? Collections.emptyMap() : content), true);
      
      if (attachments != null) {
        attachments.forEach(attachment -> {
          try {
            messageHelper.addAttachment(attachment.getName(), attachment);
          } catch (final MessagingException messagingException) {
            throw new EmailException("Unable to add attachment " + attachment.getAbsolutePath(), messagingException, (Object[]) null);
          }
        });
      }
    };
    
    try {
      mailSender.send(messagePreparator);
    } catch (final MailException me) {
      throw new EmailException("error.unableToSendEmail", me, (Object[])null);
    }
  }
}
