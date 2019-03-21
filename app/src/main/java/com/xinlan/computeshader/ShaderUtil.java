package com.xinlan.computeshader;//������

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import android.content.res.Resources;
import android.opengl.GLES31;
import android.util.Log;

//������ɫ���Ĺ�����
public class ShaderUtil 
{
   //����ָ��shader�ķ���
   public static int loadShader
   (
		 int shaderType, //shader������  GLES31.GL_VERTEX_SHADER(����)   GLES31.GL_FRAGMENT_SHADER(ƬԪ)   GLES31.GL_COMPUTE_SHADER(����)
		 String source   //shader�Ľű��ַ���
   ) 
   {
	    //����һ����shader
        int shader = GLES31.glCreateShader(shaderType);
        //�������ɹ������shader
        if (shader != 0) 
        {
        	//����shader��Դ����
        	GLES31.glShaderSource(shader, source);
            //����shader
        	GLES31.glCompileShader(shader);
            //��ű���ɹ�shader����������
            int[] compiled = new int[1];
            //��ȡShader�ı������
            GLES31.glGetShaderiv(shader, GLES31.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) 
            {//������ʧ������ʾ������־��ɾ����shader
                Log.e("ES31_ERROR", "Could not compile shader " + shaderType + ":");
                Log.e("ES31_ERROR", GLES31.glGetShaderInfoLog(shader));
                GLES31.glDeleteShader(shader);
                shader = 0;      
            }  
        }
        return shader;
    }
   
   //����������ɫ������ķ���
   public static int createComputeProgram(String source) 
   {
	    //���ؼ�����ɫ��
        int computeShader = loadShader(GLES31.GL_COMPUTE_SHADER, source);
        if (computeShader == 0) 
        {//������ɫ������ʧ��ʱ
            return 0;//����0
        }

        //��������
        int program = GLES31.glCreateProgram();
        //�����򴴽��ɹ���������м��������ɫ��
        if (program != 0) 
        {
        	//������м��������ɫ��
            GLES31.glAttachShader(program, computeShader);
            checkGlError("glAttachShader");
            //���ӳ���
            GLES31.glLinkProgram(program);
            //������ӳɹ�program����������
            int[] linkStatus = new int[1];
            //��ȡprogram���������
            GLES31.glGetProgramiv(program, GLES31.GL_LINK_STATUS, linkStatus, 0);
            //������ʧ���򱨴�ɾ������
            if (linkStatus[0] != GLES31.GL_TRUE) 
            {
                Log.e("ES31_ERROR", "Could not link program: ");
                Log.e("ES31_ERROR", GLES31.glGetProgramInfoLog(program));
                GLES31.glDeleteProgram(program);//ɾ������
                program = 0;
            }
        }
        return program;//���ؽ��
    }
    
   //��������shader����ķ���
   public static int createRenderProgram(String vertexSource, String fragmentSource) 
   {
	    //���ض�����ɫ��
        int vertexShader = loadShader(GLES31.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) 
        {
            return 0;
        }
        
        //����ƬԪ��ɫ��
        int pixelShader = loadShader(GLES31.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) 
        {
            return 0;
        }

        //��������
        int program = GLES31.glCreateProgram();
        //�����򴴽��ɹ���������м��붥����ɫ����ƬԪ��ɫ��
        if (program != 0) 
        {
        	//������м��붥����ɫ��
            GLES31.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            //������м���ƬԪ��ɫ��
            GLES31.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            //���ӳ���
            GLES31.glLinkProgram(program);
            //������ӳɹ�program����������
            int[] linkStatus = new int[1];
            //��ȡprogram���������
            GLES31.glGetProgramiv(program, GLES31.GL_LINK_STATUS, linkStatus, 0);
            //������ʧ���򱨴�ɾ������
            if (linkStatus[0] != GLES31.GL_TRUE) 
            {
                Log.e("ES31_ERROR", "Could not link program: ");
                Log.e("ES31_ERROR", GLES31.glGetProgramInfoLog(program));
                GLES31.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }
    
   //���ÿһ�������Ƿ��д���ķ���
   public static void checkGlError(String op) 
   {
        int error;
        while ((error = GLES31.glGetError()) != GLES31.GL_NO_ERROR) 
        {
            Log.e("ES31_ERROR", op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
   }
   
   //��sh�ű��м���shader���ݵķ���
   public static String loadFromAssetsFile(String fname,Resources r)
   {
   	String result=null;    	
   	try
   	{
   		InputStream in=r.getAssets().open(fname);
			int ch=0;
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    while((ch=in.read())!=-1)
		    {
		      	baos.write(ch);
		    }      
		    byte[] buff=baos.toByteArray();
		    baos.close();
		    in.close();
   		result=new String(buff,"UTF-8"); 
   		result=result.replaceAll("\\r\\n","\n");
   	}
   	catch(Exception e)
   	{
   		e.printStackTrace();
   	}    	
   	return result;
   }
}
