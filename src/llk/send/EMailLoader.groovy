package llk.send

import java.text.SimpleDateFormat
class EMailLoader {

    private static def email_subject
    private static def email_content

    private def hintWindow = new HintWindow()

    private static def xmlFilePath

    EMailLoader(def filePath){
        xmlFilePath = filePath
    }

    /**
     * 读取邮件内容
     * @param filePath
     * @param loadEmailFinish
     * @return
     */
    def launchLoader(def loadEmailFinish){
        if (xmlFilePath){
            loadEmailContent(xmlFilePath, loadEmailFinish)
        }else {
            println("EMailContent File is null, path=" + xmlFilePath)
        }
    }

    private def loadEmailContent(def filePath, def loadEmailFinish){
        if (checkFileModifiedTime(filePath)){ //文件已经修改过了
            Thread.start {
                def email = new XmlParser().parse(filePath)
                if (email){
                    email_subject = email.subject.text()
                    if (email_subject.indexOf("@yourname") != -1){
                        email_subject = email_subject.replace("@yourname", UserInfoLoader.getYourName())
                    }
                    if (email_subject.indexOf("@m") != -1){
                        email_subject = email_subject.replace("@m", Utils.currentMonth())
                    }
                    if (email_subject.indexOf("@w") != -1){
                        email_subject = email_subject.replace("@w", Utils.currentWeekInMonth())
                    }

                    def nameArray, itemArray
                    nameArray = email.content.progress.@name + email.content.plan.@name + email.content.risk.@name + email.content.bug.@name
                    itemArray = [email.content.progress.text(), email.content.plan.text(), email.content.risk.text(), email.content.bug.text()]
                    email_content = "${nameArray[0]} \n ${itemArray[0]} \n"
                    for (i in 1..3){
                        email_content += "${nameArray[i]} \n ${itemArray[i]} \n"
                    }
                }else {
                    println("xml is null, filePath=" + filePath)
                }

                println("email_subject=" + email_subject)
                println("email_content=" + email_content)
                if (loadEmailFinish != null){
                    loadEmailFinish.call([email_subject, email_content])
                }
            }
        }else { //文件修改日期 比 最后发送日期要小
            println("文件修改日期与实际不符")
            hintWindow.showEditWindow(this)
        }
    }

    /**
     * 从输入框填写进来的内容
     * 需要写入到xml里面
     * @param emailContent
     */
    def writeEmailContentToXml(def emailContent, def isWriteNull){
        try {
            def strXml = new StringWriter()
            def xmlMark  = new groovy.xml.MarkupBuilder(strXml)
            xmlMark.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
            xmlMark.email(){
                subject(name:'邮件主题', '@yourname @m月第@w周周报 (周报机器人自动发送)')
                content(name:'邮件内容'){
                    progress(name:'一、本周项目进度', isWriteNull ? "" : emailContent[0])
                    plan(name:'二、下周计划', isWriteNull ? "" : emailContent[1])
                    risk(name:'三、风险点', isWriteNull ? "" : emailContent[2])
                    bug(name:'四、遗留bug', isWriteNull ? "" : emailContent[3])
                }
            }

            PrintWriter pw = new PrintWriter(xmlFilePath)
            pw.write(strXml.toString())
            pw.close()
            if (!isWriteNull){
                println("成功更新邮件内容到WeeklyReport.xml")
            }else {
                println("成功写入空数据WeeklyReport.xml")
            }

            return true
        }catch (ex){
            println("写入WeeklyReport.xml失败, ex=$ex")
            return false
        }

    }

    /**
     * 校验文件修改日期是否是本周内修改的
     * @param file
     * @return
     */
    def checkFileModifiedTime(def filePath){
        File file = new File(filePath)
        if (!file) return false

        def fileModifiedTime = file.lastModified() //文件修改时间
        def cal = Calendar.getInstance()
        def formatter = new SimpleDateFormat("yyyy-MM-dd")
        cal.setTimeInMillis(fileModifiedTime)

        def fileModifiedDate = formatter.format(cal.getTime()).toString().replace("-", "")
        def lastSendDate = UserInfoLoader.getLastSendDate().toString().replace("-", "")
        println("修改文件日期=$fileModifiedDate, 最后发送邮件日期=$lastSendDate")
        if (fileModifiedDate >= lastSendDate){
            return true
        }else {
            return false
        }
    }

    static def getEmailSubject(){
        return email_subject
    }

    static def getEmailContent(){
        return email_content
    }
}
