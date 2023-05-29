package tankbattle;

import java.util.Vector;

//自己的坦克
public class MyTank extends Tank {
    //创建一个Shot对象
    Shot shot = null;
    Vector<Shot> shots = new Vector<>();

    public MyTank(int x, int y) {
        super(x, y);
    }

    public void shotEnemyTank() {
        if(shots.size() == 5){//允许场上最多存在多少颗子弹
            return;
        }
        switch (getDirect()) {//我的坦克的方向
            case 0:
                shot = new Shot(getX() + 20, getY(), 0);
                break;
            case 1:
                shot = new Shot(getX() + 60, getY() + 20, 1);
                break;
            case 2:
                shot = new Shot(getX() + 20, getY() + 60, 2);
                break;
            case 3:
                shot = new Shot(getX(), getY() + 20, 3);
                break;

        }
        shots.add(shot);
        new Thread(shot).start();
    }
}
