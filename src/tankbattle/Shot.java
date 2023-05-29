package tankbattle;

//射击子弹
public class Shot implements Runnable {//子弹类实现了Runnable接口，作为线程
    int x;//子弹的2坐标
    int y;//子弹的y坐标
    int direct;//子弹的方向
    int speed = 10;//子弹的速度
    boolean isLive = true;//子弹是否开活着

    public Shot(int x, int y, int direct) {//定义子弹出现的位置和方向
        this.x = x;
        this.y = y;
        this.direct = direct;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(50);//每隔50毫秒移动一次
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            switch (direct) {//子弹的移动，单开了线程
                case 0:
                    y -= speed;
                    break;
                case 1:
                    x += speed;
                    break;
                case 2:
                    y += speed;
                    break;
                case 3:
                    x -= speed;
                    break;
            }
            //测试
//            System.out.println("子弹的坐标" + x + "\t" + y);
            //当子弹碰到敌人坦克时和碰到边界时，子弹销毁
            if (!(x >= 0 && x <= 1000 && y >= 0 && y <= 750 && isLive)) {
                isLive = false;
                break;
            }
        }
    }
}
