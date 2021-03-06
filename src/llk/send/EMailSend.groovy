package llk.send

import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.SimpleEmail

class EMailSend {
    private def emailController
    private def fail_index = 0

    EMailSend(){
        emailController = new SimpleEmail()
    }

    //发送邮件动作
    def sendEMailToFoxmail(def toAddress,
                           def ccAddressList,
                           def email_subject,
                           def email_content,
                           def userInfoLoader){
        if (!UserInfoLoader.getUserName() && !toAddress){
            new HintWindow().showDialog("用户信息相关信息为空, 请完善${AutoSendRebot.WORKSPACE_PATH}\\${AutoSendRebot.USER_FILE_NAME}",
                    true)

        }
        println("sendEMailToFoxmail"
        + " toAddress=" + toAddress
        + " ccAddressList=" + ccAddressList
        + " email_subject=" + email_subject
        + " email_content=" + email_content)

        if (emailController){
            emailController.setHostName("smtp.qq.com")
            emailController.setSmtpPort(465)
            emailController.setCharset("UTF-8") // 必须放在前面，否则乱码
            emailController.setSSLOnConnect(true)
            //登录认证账号
            emailController.setAuthenticator(new DefaultAuthenticator(UserInfoLoader.getUserName(), UserInfoLoader.getPassword()))
            emailController.setFrom(UserInfoLoader.getUserName())
            //收件人
            emailController.addTo(toAddress)
            //抄送人(CC:抄送, BCC:暗抄送)
            emailController.addCc(ccAddressList)

            //邮件主题
            emailController.setSubject(email_subject)
            //邮件内容
            emailController.setMsg(email_content)

            try {
                emailController.send()
            }catch (ex){
                println("Send Email Fail, ex=$ex")

                if (fail_index <= 5){
                    println("again send Email, fail_index=$fail_index")
                    sendEMailToFoxmail(toAddress,
                            UserInfoLoader.getCCAddress().toString().indexOf("、") != -1 ? UserInfoLoader.getCCAddress().toString().split("、") : UserInfoLoader.getCCAddress(),
                            email_subject,
                            email_content,
                            userInfoLoader)
                    fail_index++
                }else {
                    new HintWindow().showDialog("周报多次发送失败, 麻烦手动发送", true)
                }

            }finally {
                println("Send Email Success")
                //发送成功之后 记录一下最后发送的时间
                userInfoLoader?.writeLastSendDate(Utils.currentDate(), false)

                new HintWindow().showDialog("自动发周报成功!!!", false)
            }
        }else {
            println("emailController is null")
        }
    }
}
