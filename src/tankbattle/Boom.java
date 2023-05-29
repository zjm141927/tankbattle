package tankbattle;

public class Boom {//将被打爆的坦克位置看成将会出现一个炸弹，并利用图片实现爆炸效果
    int x, y;
    int life = 9;//炸弹生命周期
    boolean isLive = true;

    public Boom(int x, int y) {//这个炸弹的位置就是坦克被打爆的位置(左上角)
        this.x = x;
        this.y = y;
    }

    public void lifeDown() {//为了让爆炸效果看起来更好
        if (life > 0) {
            life--;
        } else {
            isLive = false;//如果炸弹的life变为0了，代表这个炸弹所演示的爆炸效果结束了，所以将这个炸弹移除
        }
    }
}
