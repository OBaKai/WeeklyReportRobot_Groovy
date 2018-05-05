package llk.send

class AutoSendRebot {

    private static def userInfoLoader
    private static def emailLoader

    static def WORKSPACE_PATH = "D:\\A_WeeklyReportInfo"
    static def USER_FILE_NAME = "UserInfo.xml"
    static def WEEKLY_REPORT_FILE_NAME = "WeeklyReport.xml"

    //轮询间隔时间
    private static def keed_in_check_interval = 10 * 60 * 1000

    /**
     * 入口方法
     * @param args
     */
    static void main(){
        userInfoLoader = new UserInfoLoader("$WORKSPACE_PATH\\$USER_FILE_NAME")
        emailLoader = new EMailLoader("$WORKSPACE_PATH\\$WEEKLY_REPORT_FILE_NAME")

        checkLegalTiem()
    }

    /**
     * 检验工作区域是否正常
     */
    static def checkWorkspace(){
        File rootFolder = new File(WORKSPACE_PATH)
        def fileFlag = -1

        if (rootFolder?.exists() && rootFolder?.isDirectory()){
            def isHaveUserInfoFile = false
            def isHaveWeeklyReportFile = false

            rootFolder.eachFile {
                childFile ->
                    if (childFile?.isFile()){
                        if (childFile.name == USER_FILE_NAME){
                            isHaveUserInfoFile = true
                        }

                        if (childFile.name == WEEKLY_REPORT_FILE_NAME){
                            isHaveWeeklyReportFile = true
                        }
                    }
            }

            if (!isHaveUserInfoFile && !isHaveWeeklyReportFile){
                fileFlag = 2
            }else {
                if (!isHaveUserInfoFile){
                    fileFlag = 3
                }

                if (!isHaveWeeklyReportFile){
                   fileFlag = 4
                }

                if (isHaveUserInfoFile && isHaveUserInfoFile){
                    fileFlag = -1
                }
            }
        }else {
            rootFolder.mkdir() //创建文件夹
            fileFlag = 1
        }

        if (fileFlag != -1){
            makeWorkFile(fileFlag)
            return true
        }else {
            return false
        }

    }

    /**
     * 创建工作区文件
     * 1：创建文件夹 + 两个文件
     * 2：创建两个xml文件
     * 3：创建UserInfo.xml
     * 4：创建WeeklyReport.xml
     * @param needMakeFileFlag
     */
    static def makeWorkFile(def needMakeFileFlag){
        def user_file = new File("$WORKSPACE_PATH\\$USER_FILE_NAME")
        def weeklyReport_file = new File("$WORKSPACE_PATH\\$WEEKLY_REPORT_FILE_NAME")

        switch (needMakeFileFlag){
            case 1:
                println("工作区域 $WORKSPACE_PATH 不存在, 执行创建")
                user_file?.createNewFile()
                weeklyReport_file?.createNewFile()
                userInfoLoader.writeLastSendDate(null, true)
                emailLoader.writeEmailContentToXml(null, true)
                break
            case 2:
                println("$USER_FILE_NAME 和 $WEEKLY_REPORT_FILE_NAME 都不存在, 执行创建")
                user_file?.createNewFile()
                weeklyReport_file?.createNewFile()
                userInfoLoader.writeLastSendDate(null, true)
                emailLoader.writeEmailContentToXml(null, true)
                break
            case 3:
                println("$USER_FILE_NAME 不存在, 执行创建")
                user_file?.createNewFile()
                userInfoLoader.writeLastSendDate(null, true)
                break
            case 4:
                println("$WEEKLY_REPORT_FILE_NAME 不存在, 执行创建")
                weeklyReport_file?.createNewFile()
                emailLoader.writeEmailContentToXml(null, true)
                break
        }
    }

    /**
     * 校验合法时间
     * @return
     */
    static def checkLegalTiem(){
        Thread.start {
            def is_In_Legal_Tiem = false
            def is_show_hint = false

            while (!is_In_Legal_Tiem){
                //检验本地工作区
                //如果没穿件工作区, 创建完之后提醒用户去填写userinfo.xml
                if (checkWorkspace()){
                    new HintWindow().showDialog("已为你创建本地文件, 请完善${AutoSendRebot.WORKSPACE_PATH}\\${AutoSendRebot.USER_FILE_NAME}",
                            true)
                }else {
                    userInfoLoader.loadUserInfo()
                }

                def date = new Date()
                def currentDate = date.toString().split(" ")
                def currentWeek = currentDate[0] //当前星期几
                def currentTime = currentDate[3].replace(":", "").substring(0, 4)

                def targetWeek = Utils.weekChange(userInfoLoader?.getAutoSendWeek())
                def targetTime = userInfoLoader?.getAutoSendTime().toString().replace(":", "")
                println("currentWeek= $currentWeek, currentTime= $currentTime, targetWeek=$targetWeek, targetTime=$targetTime")

                if (currentWeek == targetWeek){ //星期几校验
                    if (currentTime >= targetTime && currentTime <= Utils.jointTime(targetTime, 15)){ //发送时间校验
                        is_In_Legal_Tiem = true
                    }else {
                        println("检验星期成功, 未到发送邮件时间继续轮询, 每${keed_in_check_interval}ms轮询一次")
                        if (!is_show_hint){
                            new HintWindow().showDialog("今天是${userInfoLoader?.getAutoSendWeek()}啦, 周报机器人会在${userInfoLoader?.getAutoSendTime()}左右为你发周报", false)
                            is_show_hint = true
                        }
                    }
                }else { //今天不是有效星期, 返回不做轮询
                    println("校验星期失败, 退出轮询")
                    break
                }

                if (is_In_Legal_Tiem){
                    println("当前星期、时间校验成功, 执行发送邮件操作")
                    executeSendEmail()
                }

                if (Utils.alignAtMinute(currentTime, targetTime)[0]){
                    long sleep_time = Utils.alignAtMinute(currentTime, targetTime)[1] * 60 * 1000
                    println("当前时间需要对齐目标时间, 本次sleep时间为${sleep_time}ms")
                    Thread.sleep(sleep_time)
                }else {
                    Thread.sleep(keed_in_check_interval)
                }
            }
        }
    }

    /**
     * 执行读取用户信息、读取邮件信息、发送邮件操作
     * @return
     */
    static def executeSendEmail(){
        def emailSend = new EMailSend()

        //闭包
        def loadEmailFinish = {
            email_datas ->
                //？== 判空
                emailSend?.sendEMailToFoxmail(userInfoLoader.getToAddress(),
                        userInfoLoader.getCCAddress().toString().indexOf("、") != -1 ? userInfoLoader.getCCAddress().toString().split("、") : userInfoLoader.getCCAddress(),
                        email_datas[0],
                        email_datas[1],
                        userInfoLoader)
        }

        emailLoader.launchLoader(loadEmailFinish)
    }
}



