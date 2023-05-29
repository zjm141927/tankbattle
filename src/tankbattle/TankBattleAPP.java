package tankbattle;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Scanner;

public class TankBattleAPP extends JFrame {
    MyPanel mp = null;
    static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {

        TankBattleAPP tankBattle01 = new TankBattleAPP();
    }
    public TankBattleAPP(){
        System.out.println("请输入选择1：新游戏 2：继续上局");
        String key = scanner.next();
        mp = new MyPanel(key);
        this.add(mp);
        new Thread(mp).start();//启动线程，因为myPanel实现了Runnable接口，并且重写了run方法，所以要开启线程
        this.setSize(1300,795);  //框架的大小
        this.addKeyListener(mp);//监听键盘的输入
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //点了关闭后，程序退出
        this.setVisible(true);//可视化
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Recorder.keepRecord();
                System.exit(0);
            }
        });



    }
}
