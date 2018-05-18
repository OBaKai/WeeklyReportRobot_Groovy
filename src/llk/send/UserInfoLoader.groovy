package llk.send

class UserInfoLoader {
    private static def username, password,yourname,
            to_address, cc_address, lastSendDate,
            auto_send_week, auto_send_time

    private def xmlFilePath

    UserInfoLoader(def filePath){
        xmlFilePath = filePath
    }

    def loadUserInfo(){
        def userData = new XmlParser().parse(xmlFilePath)
        if (userData){
            username = userData.username.text()
            password = userData.password.text()
            yourname = userData.yourname.text()
            to_address = userData.to_address.text()
            cc_address = userData.cc_address.text()
            auto_send_week = userData.auto_send_week.text()
            auto_send_time = userData.auto_send_time.text()
            lastSendDate = userData.last_send_date.text()
        }else {
            println("UserInfoLoader xmlData is null")
        }
    }

    def writeLastSendDate(def date, def isWriteNull){
        lastSendDate = date

        def strXml = new StringWriter()
        def xmlMark  = new groovy.xml.MarkupBuilder(strXml)
        xmlMark.mkp.xmlDeclaration(version: "1.0", encoding: "GBK")
        xmlMark.user(){
            username(isWriteNull ? "" : getUserName())
            password(isWriteNull ? "" : getPassword())
            yourname(isWriteNull ? "" : getYourName())
            to_address(isWriteNull ? "" : getToAddress())
            cc_address(isWriteNull ? "" : getCCAddress())
            auto_send_week(isWriteNull ? "" : getAutoSendWeek())
            auto_send_time(isWriteNull ? "" : getAutoSendTime())
            last_send_date(isWriteNull ? "" : getLastSendDate())
        }

        PrintWriter pw = new PrintWriter(xmlFilePath)
        pw.write(strXml.toString())
        pw.close()

        if (!isWriteNull){
            println("成功更新发送邮件日期到UserInfo.xml, 时间=$date")
        }else {
            println("成功写入空数据到UserInfo.xml")
        }
    }

    static def getUserName(){
        return username
    }

    static def getPassword(){
        return password
    }

    static def getYourName(){
        return yourname
    }

    static def getToAddress(){
        return to_address
    }

    static def getCCAddress(){
        return cc_address
    }

    static def getLastSendDate(){
        return lastSendDate
    }

    static def getAutoSendWeek(){
        return auto_send_week
    }

    static def getAutoSendTime(){
        return auto_send_time
    }
}
