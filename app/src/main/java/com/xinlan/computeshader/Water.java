package com.xinlan.computeshader;//������

import static com.xinlan.computeshader.ShaderUtil.createRenderProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import android.opengl.GLES31;

//����ˮ������ˮ��
public class Water
{	
	int mProgram;//�Զ�����Ⱦ���߳���id
    int muMVPMatrixHandle;//�ܱ任��������id
    int muMMatrixHandle;//λ�á���ת�任��������id
    int muLightLocationHandle;//��Դλ����������id
    int muCameraHandle; //�����λ���������� id
    int mutexCoorOffsetHandle;//ÿ֡����������ƫ������id
    
    int mComputeProgramBD;//�Զ�����Ⱦ���߼������id--����
	int mComputeProgramNormal;//�Զ�����Ⱦ���߼������id--������
    //��1�����Ҳ��ĸ�����������id
    int muBx1Handle;//��1�����Ҳ��Ĳ�������id
    int muBc1Handle;//��1�����Ҳ��Ĳ�������id
    int muzf1Handle;//��1�����Ҳ����������id
    int muqsj1Handle;//��1�����Ҳ�����ʼ������id
    
    //��2�����Ҳ��ĸ�����������id
    int muBx2Handle;
    int muBc2Handle;
    int muzf2Handle;
    int muqsj2Handle;
    
    //��3�����Ҳ��ĸ�����������id
    int muBx3Handle;
    int muBc3Handle;
    int muzf3Handle;
    int muqsj3Handle;
    
    int maPositionHandle; 	//����λ����������id  
    int maNormalHandle; 	//���㷨������������id  
    int maTexCoorHandle; 	//��������������������id  
    String mVertexShader;	//������ɫ��    	 
    String mFragmentShader;	//ƬԪ��ɫ��
    String mComputeShaderBD;//������ɫ��--����
    String mComputeShaderNormal;//������ɫ��--������
	
	FloatBuffer  mVertexBuffer;//�����������ݻ���
	FloatBuffer  mNormalBuffer;//���㷨�������ݻ���
	FloatBuffer  mTexCoorBuffer;//���������������ݻ���
	IntBuffer    mIndexBuffer;//��װ��������
	
	int vertexDataBufferId;//�������ݻ���id
	int normalDataBufferId;//���������ݻ���id
	
	//������
    int vCount=0;  
    //������
    int iCount=0;
    
    //�������Ҳ��ε���ʼ��
    float qsj1=0;
    float qsj2=90;
    float qsj3=45;
    
    //ÿ֡����������ƫ����
    float texCoorOffset=0.0f;
    
    public Water(MySurfaceView mv)
    {    	
    	//��ʼ��������������ɫ����
    	initVertexData();
    	//��ʼ����ɫ��        
    	initShader(mv);
    }
    
    //��ʼ������������ݵķ���
    public void initVertexData()
    {
    	//�����������ݵĳ�ʼ��================begin============================
    	vCount=(Constant.WATER_WIDTH+1)*(Constant.WATER_HEIGHT+1);
    	float vertices[]=new float[vCount*4];
    	int tempCount=0;
    	
    	for(int j=0;j<=Constant.WATER_HEIGHT;j++)
    	{
    		for(int i=0;i<=Constant.WATER_WIDTH;i++)
    		{
    			float x=Constant.WATER_UNIT_SIZE*i;
    			float z=Constant.WATER_UNIT_SIZE*j;
    			float y=0;
    			vertices[tempCount*4]=x;
    			vertices[tempCount*4+1]=y;
    			vertices[tempCount*4+2]=z;
    			vertices[tempCount*4+3]=1;
    			tempCount++; 
    		}
    	} 	
    	
        //���������������ݻ���
        //vertices.length*4����Ϊһ�������ĸ��ֽ�
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mVertexBuffer = vbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mVertexBuffer.put(vertices);//�򻺳����з��붥����������
        mVertexBuffer.position(0);//���û�������ʼλ��
        //�ر���ʾ�����ڲ�ͬƽ̨�ֽ�˳��ͬ���ݵ�Ԫ�����ֽڵ�һ��Ҫ����ByteBuffer
        //ת�����ؼ���Ҫͨ��ByteOrder����nativeOrder()�������п��ܻ������
        //�����������ݵĳ�ʼ��================end============================
        
        //���㷨�������ݵĳ�ʼ��================begin============================
        float normals[]=new float[vCount*4];
    	tempCount=0;
    	
    	for(int j=0;j<=Constant.WATER_HEIGHT;j++)
    	{
    		for(int i=0;i<=Constant.WATER_WIDTH;i++)
    		{    			
    			normals[tempCount*4]=0;
    			normals[tempCount*4+1]=1;
    			normals[tempCount*4+2]=0;
    			normals[tempCount*4+3]=1;
    			tempCount++; 
    		}
    	} 	
    	
        //���������������ݻ���
        //vertices.length*4����Ϊһ�������ĸ��ֽ�
        ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length*4);
        nbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mNormalBuffer = nbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mNormalBuffer.put(normals);//�򻺳����з��붥����������
        mNormalBuffer.position(0);//���û�������ʼλ��        
        //���㷨�������ݵĳ�ʼ��================end============================
        
        
        //���������������ݵĳ�ʼ��================begin============================
        float texCoor[]=new float[vCount*2];
        tempCount=0;
        
        for(int j=0;j<=Constant.WATER_HEIGHT;j++)
    	{
        	for(int i=0;i<=Constant.WATER_WIDTH;i++)
    		{
    			float s=(3.0f/Constant.WATER_WIDTH)*i;
    			float t=(3.0f/Constant.WATER_HEIGHT)*j;

    			texCoor[tempCount*2]=s;
    			texCoor[tempCount*2+1]=t;
    			tempCount++;
    		}
    	}
          
        //�������������������ݻ���
        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
        cbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mTexCoorBuffer = cbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mTexCoorBuffer.put(texCoor);//�򻺳����з��붥����ɫ����
        mTexCoorBuffer.position(0);//���û�������ʼλ��
        //�ر���ʾ�����ڲ�ͬƽ̨�ֽ�˳��ͬ���ݵ�Ԫ�����ֽڵ�һ��Ҫ����ByteBuffer
        //ת�����ؼ���Ҫͨ��ByteOrder����nativeOrder()�������п��ܻ������
        //���������������ݵĳ�ʼ��================end============================
        
        //������װ�������ݵĳ�ʼ��================begin============================
        iCount=Constant.WATER_WIDTH*Constant.WATER_HEIGHT*6;
        int[] indexs=new int[iCount];
        tempCount=0;
        for(int i=0;i<Constant.WATER_WIDTH;i++)
    	{
    		for(int j=0;j<Constant.WATER_HEIGHT;j++)
    		{
    			//0---1
    			//| / |
    			//3---2
    			int widthTemp=Constant.WATER_WIDTH+1;
    			int index0=j*widthTemp+i;   
    			int index1=index0+1; 
    			int index2=index0+1+widthTemp;
    			int index3=index0+widthTemp;
    			
    			//0-3-1
    			indexs[tempCount*6]=index0;
    			indexs[tempCount*6+1]=index3;
    			indexs[tempCount*6+2]=index1;
    			
    			//1-3-2
				indexs[tempCount*6+3]=index1;
    			indexs[tempCount*6+4]=index3;
    			indexs[tempCount*6+5]=index2;
    			
    			tempCount++;
    		}
    	}
        
        //�����������ݻ���
        ByteBuffer ibb = ByteBuffer.allocateDirect(indexs.length*4);
        ibb.order(ByteOrder.nativeOrder());//�����ֽ�˳��        
        mIndexBuffer=ibb.asIntBuffer();
        mIndexBuffer.put(indexs);
        mIndexBuffer.position(0);

        //���ɶ������ݻ���id�뷨�������ݻ���id
        int[] bufferIds=new int[2];//��Ŷ������ݻ���id�ͷ��������ݻ���id������      
        GLES31.glGenBuffers(2,bufferIds,0);//���ɻ���id
        vertexDataBufferId=bufferIds[0];//��ȡ�������ݻ���id
        normalDataBufferId=bufferIds[1];//��ȡ���������ݻ���id
        //�󶨶������ݻ���
        GLES31.glBindBuffer(GLES31.GL_SHADER_STORAGE_BUFFER, vertexDataBufferId);        
        //Ϊ�������ݻ����������
        GLES31.glBufferData(GLES31.GL_SHADER_STORAGE_BUFFER, vCount*4*4, mVertexBuffer, GLES31.GL_STATIC_DRAW);
        //�󶨷��������ݻ���
        GLES31.glBindBuffer(GLES31.GL_SHADER_STORAGE_BUFFER, normalDataBufferId);        
        //Ϊ���������ݻ����������
        GLES31.glBufferData(GLES31.GL_SHADER_STORAGE_BUFFER, vCount*4*4, mNormalBuffer, GLES31.GL_STATIC_DRAW);
    }

    //��ʼ����ɫ��
    public void initShader(MySurfaceView mv)
    {
    	//���ػ�����ɫ��-------------------------------------------------------------------------
    	//���ض�����ɫ���Ľű�����
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        //����ƬԪ��ɫ���Ľű�����
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());  
        //���ڶ�����ɫ����ƬԪ��ɫ����������
        mProgram = createRenderProgram(mVertexShader, mFragmentShader);
        //��ȡ�����ж���λ����������id  
        maPositionHandle = GLES31.glGetAttribLocation(mProgram, "aPosition");
        //��ȡ�����ж��㷨������������id  
        maNormalHandle = GLES31.glGetAttribLocation(mProgram, "aNormal");
        //��ȡ�����ж�������������������id  
        maTexCoorHandle= GLES31.glGetAttribLocation(mProgram, "aTexCoor");
        //��ȡ�������ܱ任��������id
        muMVPMatrixHandle = GLES31.glGetUniformLocation(mProgram, "uMVPMatrix");  
        //��ȡ������λ�á���ת�任��������id
        muMMatrixHandle = GLES31.glGetUniformLocation(mProgram, "uMMatrix");
        //��ȡ�����й�Դλ����������id
        muLightLocationHandle = GLES31.glGetUniformLocation(mProgram, "uLightLocation");
        //��ȡ�����������λ���������� id
        muCameraHandle = GLES31.glGetUniformLocation(mProgram, "uCamera"); 
        //��ȡ��������������ƫ���������� id
        mutexCoorOffsetHandle = GLES31.glGetUniformLocation(mProgram, "utexCoorOffset"); 
        
        //���ؼ�����ɫ��--����-------------------------------------------------------------------
        mComputeShaderBD=ShaderUtil.loadFromAssetsFile("computeBD.sh", mv.getResources());
        mComputeProgramBD=ShaderUtil.createComputeProgram(mComputeShaderBD);
        
        muBx1Handle=GLES31.glGetUniformLocation(mComputeProgramBD, "bx1"); 
        muBc1Handle=GLES31.glGetUniformLocation(mComputeProgramBD, "bc1"); 
        muzf1Handle=GLES31.glGetUniformLocation(mComputeProgramBD, "zf1");
        muqsj1Handle=GLES31.glGetUniformLocation(mComputeProgramBD, "qsj1");
        
        muBx2Handle=GLES31.glGetUniformLocation(mComputeProgramBD, "bx2"); 
        muBc2Handle=GLES31.glGetUniformLocation(mComputeProgramBD, "bc2"); 
        muzf2Handle=GLES31.glGetUniformLocation(mComputeProgramBD, "zf2");
        muqsj2Handle=GLES31.glGetUniformLocation(mComputeProgramBD, "qsj2");
        
        muBx3Handle=GLES31.glGetUniformLocation(mComputeProgramBD, "bx3"); 
        muBc3Handle=GLES31.glGetUniformLocation(mComputeProgramBD, "bc3"); 
        muzf3Handle=GLES31.glGetUniformLocation(mComputeProgramBD, "zf3");
        muqsj3Handle=GLES31.glGetUniformLocation(mComputeProgramBD, "qsj3");
        
        //���ؼ�����ɫ��--������--------------------------------------------------------------------
        mComputeShaderNormal=ShaderUtil.loadFromAssetsFile("computeNormal.sh", mv.getResources());
        mComputeProgramNormal=ShaderUtil.createComputeProgram(mComputeShaderNormal);
    }
    //���Ʒ���     
    public void drawSelf(int texId)
    {        
    	 //������ɫ��---��������FFT---------------------------------------begin---------------
    	
    	 //�󶨶������ݻ���
    	 GLES31.glBindBufferBase(GLES31.GL_SHADER_STORAGE_BUFFER,4,vertexDataBufferId);
    	 //�󶨷��������ݻ���
    	 GLES31.glBindBufferBase(GLES31.GL_SHADER_STORAGE_BUFFER,5,normalDataBufferId);
    	 //ʹ�ö����Ŷ�������ɫ������
   	     GLES31.glUseProgram(mComputeProgramBD);   
   	     
   	     //����1�����Ĳ��Ĵ�����Ⱦ����
   	     GLES31.glUniform2f(muBx1Handle, 50, 150);
   	     //����1�����Ĳ���������Ⱦ����
   	     GLES31.glUniform1f(muBc1Handle, 32);
   	     //����1���������������Ⱦ����
   	     GLES31.glUniform1f(muzf1Handle, 0.8f);
   	     //��1��������ʼ��
   	     qsj1=(qsj1+9)%360;
   	     //����1��������ʼ�Ǵ�����Ⱦ����
   	     GLES31.glUniform1f(muqsj1Handle,(float)Math.toRadians(qsj1));
   	     
   	     //��2�����Ĳ���
   	     GLES31.glUniform2f(muBx2Handle, 10, 40);
   	     //��2�����Ĳ���
   	     GLES31.glUniform1f(muBc2Handle, 24);
   	     //��2���������
   	     GLES31.glUniform1f(muzf2Handle, 1f);
   	     //��2��������ʼ��
   	     qsj2=(qsj2+9)%360;
   	     GLES31.glUniform1f(muqsj2Handle,(float)Math.toRadians(qsj2));
   	     
   	     //��3�����Ĳ���
   	     GLES31.glUniform2f(muBx3Handle, 200, 200);
   	     //��3�����Ĳ���
   	     GLES31.glUniform1f(muBc3Handle, 60);
   	     //��3���������
   	     GLES31.glUniform1f(muzf3Handle, 2.0f);
   	     //��3��������ʼ��
   	     qsj3=(qsj3+4.0f)%360;
   	     GLES31.glUniform1f(muqsj3Handle,(float)Math.toRadians(qsj3));
   	     
   	     //ִ������FFT������ɫ��
   	     GLES31.glDispatchCompute
   	     (
    		(Constant.WATER_WIDTH+1),	//���㹤����Xά�ȳߴ�
    		(Constant.WATER_HEIGHT+1),	//���㹤����Yά�ȳߴ�
    		1							//���㹤����Zά�ȳߴ�
   	     );
   	     //ͬ���ڴ�--�ڴ�����
   	     GLES31.glMemoryBarrier(GLES31.GL_SHADER_STORAGE_BARRIER_BIT);
   	     //������ɫ��---��������FFT---------------------------------------end------------------
    	
   	     //������ɫ��---���㷨����---------------------------------------begin---------------
   	     //�󶨶������ݻ���
    	 GLES31.glBindBufferBase(GLES31.GL_SHADER_STORAGE_BUFFER,4,vertexDataBufferId);
    	 //�󶨷��������ݻ���
    	 GLES31.glBindBufferBase(GLES31.GL_SHADER_STORAGE_BUFFER,5,normalDataBufferId);
    	 //ʹ�÷�����������ɫ������
   	     GLES31.glUseProgram(mComputeProgramNormal);   
   	     //ִ�м��㷨����������ɫ��   	     
   	     GLES31.glDispatchCompute
	     (
	    	(Constant.WATER_WIDTH+1),	//���㹤����Xά�ȳߴ�
	    	(Constant.WATER_HEIGHT+1),	//���㹤����Yά�ȳߴ�
	    	1							//���㹤����Zά�ȳߴ�
	     );
   	     //ͬ���ڴ�
	     GLES31.glMemoryBarrier(GLES31.GL_SHADER_STORAGE_BARRIER_BIT);
   	     //������ɫ��---���㷨����---------------------------------------end-----------------
    	
    	 //ָ��ʹ��ĳ��shader����
    	 GLES31.glUseProgram(mProgram);  
         //�����ձ任������shader����
         GLES31.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
         //��λ�á���ת�任��������ɫ������
         GLES31.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
         //����Դλ�ô�����ɫ������   
         GLES31.glUniform3fv(muLightLocationHandle, 1, MatrixState.lightPositionFB);
         //�������λ�ô�����ɫ������   
         GLES31.glUniform3fv(muCameraHandle, 1, MatrixState.cameraFB);    
         //����������ƫ�ƴ�����ɫ������
         texCoorOffset=(texCoorOffset+0.001f)%10.0f;
         GLES31.glUniform1f(mutexCoorOffsetHandle,texCoorOffset);         
         
         //��������������������Ⱦ����
         GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER,vertexDataBufferId);
         GLES31.glVertexAttribPointer(maPositionHandle, 4, GLES31.GL_FLOAT, false, 4*4, 0);
         
         //�����㷨��������������Ⱦ����
         GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER,normalDataBufferId);
         GLES31.glVertexAttribPointer(maNormalHandle, 4, GLES31.GL_FLOAT, false, 4*4, 0);
         GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER,0);
                
         //������������������������Ⱦ����
         GLES31.glVertexAttribPointer  
         (
        		maTexCoorHandle, 
         		2, 
         		GLES31.GL_FLOAT, 
         		false,
                2*4,   
                mTexCoorBuffer
         );
         //���������������
         GLES31.glEnableVertexAttribArray(maPositionHandle);  
         GLES31.glEnableVertexAttribArray(maNormalHandle);  
         GLES31.glEnableVertexAttribArray(maTexCoorHandle);  
         
         //������
         GLES31.glActiveTexture(GLES31.GL_TEXTURE0);
         GLES31.glBindTexture(GLES31.GL_TEXTURE_2D, texId);

         //ִ�л���
         GLES31.glDrawElements(GLES31.GL_TRIANGLES, iCount, GLES31.GL_UNSIGNED_INT, mIndexBuffer);
    }
}
