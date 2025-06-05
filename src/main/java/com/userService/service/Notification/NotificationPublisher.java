package com.userService.service.Notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userService.dto.Notification.NotificationRequestRecover;
import com.userService.dto.Notification.NotificationRequestRegister;
import com.userService.dto.Notification.NotificationRequestRegisterCode;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationPublisher {

    private static final String NOTIFICATION_QUEUE_RECOVER = "notifications_service_recover.queue";
    private static final String NOTIFICATION_QUEUE_REGISTER = "notifications_service_register.queue";
    private static final String NOTIFICATION_QUEUE_REGISTER_CODE = "notifications_service_register_code.queue";

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;


    public void publishNotificationRecover(String email, String subject, String code){
        try{
            NotificationRequestRecover request = new NotificationRequestRecover();
            request.setTo(email);
            request.setSubject(subject);
            request.setCode(code);

            String message = objectMapper.writeValueAsString(request);
            rabbitTemplate.convertAndSend(NOTIFICATION_QUEUE_RECOVER, message);

        } catch (Exception e){
            System.err.println("❌ Error al enviar notificación: " + e.getMessage());
        }
    }

    public void publishNotificationRegister(String email, String subject){
        try{
            NotificationRequestRegister request = new NotificationRequestRegister();
            request.setTo(email);
            request.setSubject(subject);

            String message = objectMapper.writeValueAsString(request);
            rabbitTemplate.convertAndSend(NOTIFICATION_QUEUE_REGISTER, message);

        } catch (Exception e){
            System.err.println("❌ Error al enviar notificación: " + e.getMessage());
        }
    }

    public void publishNotificationRegisterCode(String email, String subject, String code){
        try{
            NotificationRequestRegisterCode request = new NotificationRequestRegisterCode();
            request.setTo(email);
            request.setSubject(subject);
            request.setCode(code);

            String message = objectMapper.writeValueAsString(request);
            rabbitTemplate.convertAndSend(NOTIFICATION_QUEUE_REGISTER_CODE, message);

        } catch (Exception e){
            System.err.println("❌ Error al enviar notificación: " + e.getMessage());
        }
    }
}
