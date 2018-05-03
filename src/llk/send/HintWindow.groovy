package llk.send

import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JTextArea
import java.awt.Button
import java.awt.Color
import java.awt.event.ActionListener
import javax.swing.JOptionPane

class HintWindow {

    private def mWindow
    private def hintLabel, projectLabel, planLabel, riskLabel, bugLabel
    private def projectTextArea, planTextArea, riskTextArea, bugTextArea
    private def sendButton, cancelButton

    private def projectText, planText, riskText, bugText

    HintWindow(){
        mWindow = new JFrame()
        if (mWindow){
            mWindow.setTitle("周报机器人")
            mWindow.setSize(600, 650) //窗口大小
            mWindow.setLocationRelativeTo(null) //屏幕居中
            mWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
        }
    }

    def showEditWindow(def emailLoader){
        if (mWindow){
            mWindow.setLayout(null) //使用绝对布局
            hintLabel = new JLabel("检测到邮件内容文件WeeklyReport.xml没有更新, 可以在这里编写完后发送")
            hintLabel.setBounds(50, 10, 500, 15)
            hintLabel.setForeground(Color.RED)

            projectLabel = new JLabel("本周项目进度:")
            projectLabel.setBounds(50, 30, 500, 15)

            planLabel = new JLabel("下周计划:")
            planLabel.setBounds(50, 160, 500, 15)

            riskLabel = new JLabel("风险点:")
            riskLabel.setBounds(50, 290, 500, 15)

            bugLabel = new JLabel("遗留bug:")
            bugLabel.setBounds(50, 420, 500, 15)

            projectTextArea = new JTextArea()
            projectTextArea.setBounds(50, 50, 500, 100)
            planTextArea = new JTextArea()
            planTextArea.setBounds(50, 180, 500, 100)
            riskTextArea = new JTextArea()
            riskTextArea.setBounds(50, 310, 500, 100)
            bugTextArea = new JTextArea()
            bugTextArea.setBounds(50, 440, 500, 100)
            projectTextArea.setLineWrap(true)
            planTextArea.setLineWrap(true)
            riskTextArea.setLineWrap(true)
            bugTextArea.setLineWrap(true)

            sendButton = new Button("Send EMail")
            sendButton.setBounds(50, 550, 250, 40)
            sendButton.setBackground(Color.YELLOW)
            cancelButton = new Button("Close")
            cancelButton.setBounds(310, 550, 250, 40)
            cancelButton.setBackground(Color.YELLOW)

            sendButton.addActionListener({
                projectText = projectTextArea.getText()
                planText = planTextArea.getText()
                riskText = riskTextArea.getText()
                bugText = bugTextArea.getText()

                if (!projectText && !planText && !riskText && !bugText){
                    showDialog("所有的框都是空的, 别忽悠我发邮件!!", true)
                }else {
                    if (emailLoader){
                        if (emailLoader.writeEmailContentToXml([projectText, planText, riskText, bugText], false)){ //写入xml成功
                            AutoSendRebot.executeSendEmail()
                        }
                    }
                }
            } as ActionListener)

            //关闭窗口
            cancelButton.addActionListener({
                System.exit(0)
            } as ActionListener)

            mWindow.add(hintLabel)
            mWindow.add(projectLabel)
            mWindow.add(projectTextArea)
            mWindow.add(planLabel)
            mWindow.add(planTextArea)
            mWindow.add(riskLabel)
            mWindow.add(riskTextArea)
            mWindow.add(bugLabel)
            mWindow.add(bugTextArea)
            mWindow.add(sendButton)
            mWindow.add(cancelButton)

            mWindow.setVisible(true)
        }
    }

    def showDialog(def msg, def isNeedErrorDialog){
        if(mWindow){

            if (isNeedErrorDialog){
                JOptionPane.showMessageDialog(mWindow, msg, "周报机器人, 提示你", JOptionPane.ERROR_MESSAGE)
            }else {
                JOptionPane.showMessageDialog(mWindow, msg, "周报机器人, 提示你", JOptionPane.INFORMATION_MESSAGE)
            }
        }
    }
}
