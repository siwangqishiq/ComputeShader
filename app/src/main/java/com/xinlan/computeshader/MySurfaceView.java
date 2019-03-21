package com.xinlan.computeshader;

import java.io.IOException;
import java.io.InputStream;

import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;
import android.opengl.GLES31;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

class MySurfaceView extends GLSurfaceView {
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;//�Ƕ����ű���
    private SceneRenderer mRenderer;//������Ⱦ��

    private float mPreviousY;//�ϴεĴ���λ��Y����
    private float mPreviousX;//�ϴεĴ���λ��X����

    int textureId;//ϵͳ���������id

    public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //����ʹ��OPENGL ES3.1
        mRenderer = new SceneRenderer();    //����������Ⱦ��
        setRenderer(mRenderer);                //������Ⱦ��
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//������ȾģʽΪ������Ⱦ   
    }

    //�����¼��ص�����
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dy = y - mPreviousY;//���㴥�ر�Yλ��
                float dx = x - mPreviousX;//���㴥�ر�Xλ��
                //���ݴ����޸����������
                Constant.direction += dx * TOUCH_SCALE_FACTOR;
                Constant.direction = Constant.direction % 360;
                //���ݴ����޸����������
                Constant.yj += dy;
                if (Constant.yj > 90) {
                    Constant.yj = 90;
                }
                if (Constant.yj < 20) {
                    Constant.yj = 20;
                }
                //���������9����
                Constant.calCamera();
        }
        mPreviousY = y;//��¼����λ��
        mPreviousX = x;//��¼����λ��
        return true;
    }

    private class SceneRenderer implements Renderer {
        Water water;//ˮ��

        long start = System.nanoTime();
        int count = 0;

        public void onDrawFrame(GL10 gl) {
            //����FPS=========================================================
            count++;
            if (count == 150) {
                count = 0;
                long end = System.nanoTime();
                System.out.println("FPS:" + (1000000000.0 * 150 / (end - start)));
                start = end;
            }
            //����FPS=========================================================

            //�����Ȼ�������ɫ����
            GLES31.glClear(GLES31.GL_DEPTH_BUFFER_BIT | GLES31.GL_COLOR_BUFFER_BIT);

            //���ô˷������������9����λ�þ���
            MatrixState.setCamera
                    (
                            Constant.cx, Constant.cy, Constant.cz,
                            Constant.tx, Constant.ty, Constant.tz,
                            Constant.upx, Constant.upy, Constant.upz
                    );

            //����ˮ��
            water.drawSelf(textureId);
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //�����ӿڴ�С��λ�� 
            GLES31.glViewport(0, 0, width, height);
            //����GLSurfaceView�Ŀ�߱�
            float ratio = (float) width / height;
            //���ô˷����������͸��ͶӰ����
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1, 1000);
            //��ʼ���任����
            MatrixState.setInitStack();
            //��ʼ���ƹ�
            MatrixState.setLightLocation(400, 400, 400);
            //��ʼ�������
            Constant.calCamera();
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //������Ļ����ɫRGBA
            GLES31.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            //����ˮ����� 
            water = new Water(MySurfaceView.this);
            //����ȼ��
            GLES31.glEnable(GLES31.GL_DEPTH_TEST);
            //��ʼ������
            initTexture();
            //�򿪱������   
            GLES31.glEnable(GLES31.GL_CULL_FACE);
        }
    }

    public void initTexture()//textureId
    {
        //��������ID
        int[] textures = new int[1];
        GLES31.glGenTextures
                (
                        1,          //����������id������
                        textures,   //����id������
                        0           //ƫ����
                );
        textureId = textures[0];
        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D, textureId);
        GLES31.glTexParameterf(GLES31.GL_TEXTURE_2D, GLES31.GL_TEXTURE_MIN_FILTER, GLES31.GL_NEAREST);
        GLES31.glTexParameterf(GLES31.GL_TEXTURE_2D, GLES31.GL_TEXTURE_MAG_FILTER, GLES31.GL_LINEAR);
        GLES31.glTexParameterf(GLES31.GL_TEXTURE_2D, GLES31.GL_TEXTURE_WRAP_S, GLES31.GL_REPEAT);
        GLES31.glTexParameterf(GLES31.GL_TEXTURE_2D, GLES31.GL_TEXTURE_WRAP_T, GLES31.GL_REPEAT);


        //ͨ������������ͼƬ===============begin===================
        InputStream is = this.getResources().openRawResource(R.drawable.haimian);
        Bitmap bitmapTmp;
        try {
            bitmapTmp = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //ͨ������������ͼƬ===============end=====================  

        //ʵ�ʼ�������
        GLUtils.texImage2D
                (
                        GLES31.GL_TEXTURE_2D,   //�������ͣ���OpenGL ES�б���ΪGL10.GL_TEXTURE_2D
                        0,                      //����Ĳ�Σ�0��ʾ����ͼ��㣬�������Ϊֱ����ͼ
                        bitmapTmp,              //����ͼ��
                        0                      //����߿�ߴ�
                );
        bitmapTmp.recycle();          //������سɹ����ͷ�ͼƬ
    }
}
