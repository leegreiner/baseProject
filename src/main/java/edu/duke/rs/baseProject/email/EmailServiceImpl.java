package edu.duke.rs.baseProject.email;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

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
      final Map<String, Object> content) {
    this.send(messageType, List.of(to), Collections.emptyList(), Collections.emptyList(), null, subject, content);  
  }
  
  @Override
  public void send(final MessageType messageType, final Collection<String> to, final Collection<String> cc,
      final Collection<String> bcc, final String from, final String subject, final Map<String, Object> content) {
    final MimeMessagePreparator messagePreparator = mimeMessage -> {
      MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
      messageHelper.setFrom(StringUtils.isBlank(from) ? defaultEmailFrom : from);
      messageHelper.setTo(to.stream().toArray(String[]::new));
      
      if (CollectionUtils.isNotEmpty(cc)) {
        messageHelper.setCc(cc.stream().toArray(String[]::new));
      }
      if (CollectionUtils.isNotEmpty(bcc)) {
        messageHelper.setBcc(bcc.stream().toArray(String[]::new));
      }
      messageHelper.setSubject(subject);
      messageHelper.setText(contentBuilder.build(messageType, content == null ? Collections.emptyMap() : content));
    };
    
    try {
      mailSender.send(messagePreparator);
    } catch (final MailException me) {
      throw new EmailException("error.unableToSendEmail", me, (Object[])null);
    }
  }
}
