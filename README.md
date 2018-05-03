    周报机器人_Groovy
    一、介绍
      不用再为每周要发周报而烦恼.
      不用再为每周忘记发周报而烦恼.
      不用再为打开邮箱编辑邮件整理周报格式而烦恼.
      
      电脑开机就能自动运行, 到指定时间会自动发送指定周报内容.
      如果没有提前编写好周报内容, 会弹出文本框提醒编辑并发送.
      发送成功会弹出提醒框, 发送失败会进行多次重试操作.
 
    
    二、使用介绍
      1.文件介绍
      ① launch_script文件夹里面包含了所有需要的东西
      ② WeeklyReportRobot.jar 就是要运行的周报机器人包(无需再下载安装配置groovy的环境, 有装Jdk就行)
      ③ launch_robot_script.bat 、 launch_robot_script.vbs是自动运行jar包脚本
        launch_robot_script.bat 、 launch_robot_script.vbs的区别：(看需求使用)
            .bat : 运行时会弹出cmd黑框(cmd命令框), 能看到输出的日志
            .vbs : 后台运行, 不会弹出cmd黑框
      
      2.配置脚本步骤
      ① WeeklyReportRobot.jar放到某个路径下 例如：E:\WeeklyReportRobot.jar
      ② 编辑launch_robot_script.bat 或 launch_robot_script.vbs 里面运行jar的路径 将"xxx\xxx\WeeklyReportRobot.jar" 替换成步骤①的路径(让脚本能找到jar)
      ③ 选中其中一个launch_robot_script运行脚本文件, 创建快捷方式
      ④ 将launch_robot_script快捷方式放到 C:\ProgramData\Microsoft\Windows\Start Menu\Programs\Startup (备注：window开机启动文件夹)
        (可以在 开始菜单->所有程序->启动->右键'打开所有用户'->也能进入 window开机启动文件夹)
        
      3.周报文件夹
      ① 运行周报机器人, 会检测D盘是否存在D:\A_WeeklyReportInfo, 如果不存在会创建
      ② D:\A_WeeklyReportInfo里面会有两个文件 UserInfo.xml 、WeeklyReport.xml
      ③ UserInfo.xml用户信息(必须要填写完整)
          <user>
            <username></username> //用户账号
            <password></password> //用户密码
            <yourname></yourname> //姓名 这个姓名是用在邮件主题的
            <from_address></from_address> //发件人 只能填一个
            <to_address></to_address> //收件人 只能填一个
            <cc_address></cc_address> //抄送人 例如：xxx@qq.com、xxx@qq.com (多联系人要用'、'隔开)
            <auto_send_week></auto_send_week> //星期几发送 例如 星期五 (星期一到星期天)
            <auto_send_time></auto_send_time> //自动发送的时间 例如17：00
            <last_send_date></last_send_date> //不用填, 自动生成
          </user>
      ④ WeeklyReport.xml周报内容填写 (最好提前编写好)
        <email>
          <subject name='邮件主题'>@yourname @m月第@w周周报 (周报机器人自动发送)</subject>
          <content name='邮件内容'>
            <progress name='一、本周项目进度'>
            </progress>
            <plan name='二、下周计划'>
            </plan>
            <risk name='三、风险点'>
            </risk>
            <bug name='四、遗留bug'>
            </bug>
          </content>
        </email>
        