package org.xjtuse.xiaoming.service.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.xjtuse.xiaoming.model.User
import org.xjtuse.xiaoming.service.LoginCodeService
import javax.mail.Message.RecipientType
import javax.mail.internet.InternetAddress

@Service
class MailService : LoginCodeService() {
    @Autowired
    private lateinit var mailSender: JavaMailSender

    @Value("\${spring.mail.username}")
    private lateinit var fromAddress: String

    override fun send(user: User) {
        val message = mailSender.createMimeMessage()
        message.setFrom(InternetAddress(fromAddress, "西交小明"))
        message.setRecipient(RecipientType.TO, InternetAddress(user.email))
        message.subject = "登录验证码"
        message.setText("验证码${generateAndSave(user)}，10分钟内有效。验证码提供给他人可能导致帐号被盗，请勿泄露，谨防被骗。如非本人操作请忽略。")
        mailSender.send(message)
    }
}