package tankbattle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Vector;

//为了让坦克动起来，需要实现KeyListener来监听键盘的输入
public class MyPanel extends JPanel implements KeyListener, Runnable {
    //定义我的坦克
    MyTank myTank = null;
    Vector<EnemyTank> enemyTanks = new Vector();//敌人 坦克的集合，Vector线程安全
    //定义一个Vector,用于存放炸弹
    //定义一个存放Node对象的Vector，用于恢复敌人坦克的坐标和方向
    Vector<Node> nodes = new Vector<>();

    Vector<Boom> booms = new Vector<>();
    int enemyTankSize = 7;
    //定义3张炸弹的图片，显示爆炸效果
    Image image1 = null;
    Image image2 = null;
    Image image3 = null;

    public MyPanel(String key) {
        //先判断文件是否存在
        //如果文件不存在，只能开始新游戏
        File file = new File(Recorder.getRecordFile());
        if(file.exists()){
        nodes = Recorder.getNodesAndEnemyTankNum();}else {
            System.out.println("文件不存在，只能开始新游戏");
            key = "1";
        }
        //初始化自己的坦克的出现位置
        myTank = new MyTank(800, 650);
        //myTank.setSpeed(8);//默认为7
        //初始化敌人的坦克
        switch (key){
            case "1":
                Recorder.setAllEnemyTankNum(0);
                for (int i = 0; i < enemyTankSize; i++) {
                    EnemyTank enemyTank = new EnemyTank(100 * (i + 1), 0);
                    //将enemyTanks设置给enemyTank
                    enemyTank.setEnemyTanks(enemyTanks);
                    Recorder.setEnemyTanks(enemyTanks);
                    enemyTank.setDirect(2);//设置方向
                    //启动坦克
                    new Thread(enemyTank).start();
                    //给enemy加入一颗子弹
                    Shot shot = new Shot(enemyTank.getX() + 20, enemyTank.getY() + 60, enemyTank.getDirect());
                    //加入enemyTank的Vector成员
                    enemyTank.shots.add(shot);//将子弹加入到Vector集合中
                    new Thread(shot).start();//启动子弹的线程
                    enemyTanks.add(enemyTank);//敌人的坦克

                }
                break;
            case "2"://继续上局游戏
                for (int i = 0; i < nodes.size(); i++) {
                    Node node = nodes.get(i);
                    EnemyTank enemyTank = new EnemyTank(node.getX(), node.getY());
                    //将enemyTanks设置给enemyTank
                    enemyTank.setEnemyTanks(enemyTanks);
                    Recorder.setEnemyTanks(enemyTanks);
                    enemyTank.setDirect(node.getDirect());//设置方向
                    //启动坦克
                    new Thread(enemyTank).start();
                    //给enemy加入一颗子弹
                    Shot shot = new Shot(enemyTank.getX() + 20, enemyTank.getY() + 60, enemyTank.getDirect());
                    //加入enemyTank的Vector成员
                    enemyTank.shots.add(shot);//将子弹加入到Vector集合中
                    new Thread(shot).start();//启动子弹的线程
                    enemyTanks.add(enemyTank);//敌人的坦克

                }

                break;
        }

        //初始化图片对象
        image1 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_1.gif"));
        image2 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_2.gif"));
        image3 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_3.gif"));
        //这里在播放音乐
        new AePlayWave("src\\111.wav").start();
    }
    //编写方法，显示我方击毁敌方坦克的信息
    public void showInfo(Graphics g){
        g.setColor(Color.black);
        Font font  = new Font("宋体",Font.BOLD,25);
        g.setFont(font);
        g.drawString("您累计击毁敌方坦克",1020,30);
        drawTank(1020,60,g,0,0);
        g.setColor(Color.black);
        g.drawString(Recorder.getAllEnemyTankNum()+"",1080,100);
    }

    @Override
    @SuppressWarnings("all")
    public void paint(Graphics g) {
        super.paint(g);
        g.fillRect(0, 0, 1000, 750);
        showInfo(g);
        //画出自己的坦克
        if (myTank != null && myTank.isLive) {
            drawTank(myTank.getX(), myTank.getY(), g, myTank.getDirect(), 1);
        }
        //画出敌人的坦克
        for (int i = 0; i < enemyTanks.size(); i++) {
            //从集合中取出坦克
            EnemyTank enemyTank = enemyTanks.get(i);
            //判断当前坦克是否还活着
            if (enemyTank.isLive) {
                drawTank(enemyTank.getX(), enemyTank.getY(), g, enemyTank.getDirect(), 0);
                //画出敌人的坦克的所有子弹
                for (int j = 0; j < enemyTank.shots.size(); j++) {
                    //取出子弹
                    Shot shot = enemyTank.shots.get(j);
                    if (shot.isLive) {//子弹还存活的时候
                        g.draw3DRect(shot.x, shot.y, 5, 5, false);
                    } else {
                        enemyTank.shots.remove(shot);
                    }
                }
            }

        }
        //画出自己的子弹
        for (int i = 0; i < myTank.shots.size(); i++) {
            Shot shot = myTank.shots.get(i);
            if (shot != null && shot.isLive == true) {
                drawShot(shot.x, shot.y, g);
            } else {
                myTank.shots.remove(shot);//拿掉已经消耗的子弹
            }
        }

        //如果booms中有炸弹，就画出
        for (int i = 0; i < booms.size(); i++) {
            Boom boom = booms.get(i);
            //根据当前对象的life值去画出对应的图片
            if (boom.life > 6) {
                g.drawImage(image1, boom.x, boom.y, 60, 60, this);
            } else if (boom.life > 3) {
                g.drawImage(image2, boom.x, boom.y, 60, 60, this);
            } else {
                g.drawImage(image3, boom.x, boom.y, 60, 60, this);
            }
            boom.lifeDown();
            if (boom.life == 0) {
                booms.remove(boom);
            }
        }
    }

    /**
     * 画出子弹
     *
     * @param x
     * @param y
     * @param g
     */
    public void drawShot(int x, int y, Graphics g) {
        g.setColor(Color.yellow);
        g.draw3DRect(x, y, 5, 5, false);
    }

    //编写放大画出坦克

    /**
     * @param x      坦克的x坐标
     * @param y      坦克的y坐标
     * @param g      画笔
     * @param direct 坦克的方向
     * @param type   坦克类型
     */
    @SuppressWarnings("all")
    public void drawTank(int x, int y, Graphics g, int direct, int type) {
        switch (type) {//坦克的类型
            case 0://自己的坦克
                g.setColor(Color.cyan);
                break;
            case 1://地方坦克
                g.setColor(Color.yellow);
        }
        //根据坦克方向，来绘制对应的坦克形状
        //direct表示不同的方向(0:上，1：右，2：下，3：左)

        switch (direct) {
            case 0://表示向上
                g.fill3DRect(x, y, 10, 60, false);
                g.fill3DRect(x + 30, y, 10, 60, false);
                g.fill3DRect(x + 10, y + 10, 20, 40, false);
                g.fillOval(x + 10, y + 20, 20, 20);
                g.drawLine(x + 20, y + 30, x + 20, y);
                break;
            case 1://右
                g.fill3DRect(x, y, 60, 10, false);
                g.fill3DRect(x, y + 30, 60, 10, false);
                g.fill3DRect(x + 10, y + 10, 40, 20, false);
                g.fillOval(x + 20, y + 10, 20, 20);
                g.drawLine(x + 30, y + 20, x + 60, y + 20);
                break;
            case 2://下
                g.fill3DRect(x, y, 10, 60, false);
                g.fill3DRect(x + 30, y, 10, 60, false);
                g.fill3DRect(x + 10, y + 10, 20, 40, false);
                g.fillOval(x + 10, y + 20, 20, 20);
                g.drawLine(x + 20, y + 30, x + 20, y + 60);

                break;
            case 3://左
                g.fill3DRect(x, y, 60, 10, false);
                g.fill3DRect(x, y + 30, 60, 10, false);
                g.fill3DRect(x + 10, y + 10, 40, 20, false);
                g.fillOval(x + 20, y + 10, 20, 20);
                g.drawLine(x + 30, y + 20, x, y + 20);
                break;

//            default:
//                System.out.println("没有处理");
        }

    }

    public void hitMyTank() {
        //遍历所以的敌人坦克
        for (int i = 0; i < enemyTanks.size(); i++) {
            //取出敌人坦克
            EnemyTank enemyTank = enemyTanks.get(i);
            //取出敌人子弹的
            for (int j = 0; j < enemyTank.shots.size(); j++) {
                Shot shot = enemyTank.shots.get(j);
                //判断子弹是否击中我的坦克
                if (myTank.isLive && shot.isLive) {
                    hitTank(shot, myTank);
                }
            }
        }
    }

    public void hitEnemyTank() {
        for (int i = 0; i < myTank.shots.size(); i++) {
            Shot shot = myTank.shots.get(i);
            //判断是否击中了敌人坦克
            if (shot != null && shot.isLive) {//我的子弹还活着
                //遍历敌人的所有坦克
                for (int j = 0; j < enemyTanks.size(); j++) {
                    EnemyTank enemyTank = enemyTanks.get(j);
                    hitTank(shot, enemyTank);
                }
            }
        }
    }

    //      判断我的子弹是否击中了坦克
    public void hitTank(Shot s, Tank tank) {
        //判断s，击中坦克
        switch (tank.getDirect()) {
            case 0://坦克向上
            case 2://坦克向下
                if (s.x > tank.getX() && s.x < tank.getX() + 40
                        && s.y > tank.getY() && s.y < tank.getY() + 60) {
                    s.isLive = false;   //击中了坦克后，子弹设置为false，被击中的坦克也设置为false
                    tank.isLive = false;
                    enemyTanks.remove(tank);//当坦克被击中时，从集合中去除坦克
                    //当我发击毁一个敌人坦克时，就对数据allEnemyTankNum++
                    if(tank instanceof EnemyTank){
                        Recorder.addAllEnemyTankNum();
                    }
                    //创建boom对象,加入集合中
                    Boom boom = new Boom(tank.getX(), tank.getY());
                    booms.add(boom);

                }
                break;
            case 1://坦克向右
            case 3://坦克向左
                if (s.x > tank.getX() && s.x < tank.getX() + 60
                        && s.y > tank.getY() && s.y < tank.getY() + 40) {
                    s.isLive = false;
                    tank.isLive = false;
                    enemyTanks.remove(tank);//当坦克被击中时，从集合中去除坦克
                    //创建boom对象,加入集合中
                    //当我发击毁一个敌人坦克时，就对数据allEnemyTankNum++
                    if(tank instanceof EnemyTank){
                        Recorder.addAllEnemyTankNum();
                    }
                    Boom boom = new Boom(tank.getX(), tank.getY());
                    booms.add(boom);
                }
                break;


        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    //处理键按下的情况
    @Override
    @SuppressWarnings("all")
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            //改变方向
            myTank.setDirect(0);//方向改变了
            if (myTank.getY() > 0) {
                myTank.moveUp();
            }

        } else if (e.getKeyCode() == KeyEvent.VK_D) {
            myTank.setDirect(1);//向右
            if (myTank.getX() + 60 < 1000) {
                myTank.moveRight();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            myTank.setDirect(2);//向下
            if (myTank.getY() + 60 < 750) {
                myTank.moveDown();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_A) {
            myTank.setDirect(3);//向左
            if (myTank.getX() > 0) {
                myTank.moveLeft();
            }
        }
        /**
         * 发射一颗子弹
         */
//        if (e.getKeyCode() == KeyEvent.VK_J) {
//            if (myTank.shot == null || !myTank.shot.isLive) {
//                myTank.shotEnemyTank();
//            }
//        }

        /**
         * 发射多课子弹
         */
        if (e.getKeyCode() == KeyEvent.VK_J) {
            if (myTank.isLive) {
                myTank.shotEnemyTank();
            }
        }
        this.repaint();//让面板重绘

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            hitEnemyTank();
            hitMyTank();

            this.repaint();//每隔100毫秒重绘面板
        }
    }
}
