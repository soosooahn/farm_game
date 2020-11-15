/*===============================================================================
Copyright (c) 2016 PTC Inc. All Rights Reserved.

Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.vuforia.samples.VuforiaSamples.app.VirtualButtons;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.service.media.CameraPrewarmService;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vuforia.Device;
import com.vuforia.ImageTargetResult;
import com.vuforia.Rectangle;
import com.vuforia.Renderer;
import com.vuforia.State;
import com.vuforia.Tool;
import com.vuforia.TrackableResult;
import com.vuforia.VirtualButton;
import com.vuforia.VirtualButtonResult;
import com.vuforia.Vuforia;
import com.vuforia.samples.SampleApplication.SampleAppRenderer;
import com.vuforia.samples.SampleApplication.SampleAppRendererControl;
import com.vuforia.samples.SampleApplication.SampleApplicationSession;
import com.vuforia.samples.SampleApplication.utils.CubeObject;
import com.vuforia.samples.SampleApplication.utils.CubeShaders;
import com.vuforia.samples.SampleApplication.utils.LineShaders;
import com.vuforia.samples.SampleApplication.utils.OBJLoader;
import com.vuforia.samples.SampleApplication.utils.OBJParser.ObjParser;
import com.vuforia.samples.SampleApplication.utils.SampleUtils;
import com.vuforia.samples.SampleApplication.utils.Teapot;
import com.vuforia.samples.SampleApplication.utils.Texture;
import com.vuforia.samples.VuforiaSamples.R;

public class VirtualButtonRenderer implements GLSurfaceView.Renderer, SampleAppRendererControl {
    private static final String LOGTAG = "VirtualButtonRenderer";
    // Constants:
    static private float kTeapotScale = 0.003f, kCubeScale = 0.002f, kObjScale = 0.0005f;
    // Define the coordinates of the virtual buttons to render the area of action,
    // this values are the same as the wood dataset
    static private float RED_VB_BUTTON[] = {-0.0695f, -0.0571f, -0.0593f, -0.0457f};
    static private float BLUE_VB_BUTTON[] = {-0.0497f, -0.0571f, -0.0383f, -0.0457f};
    static private float YELLOW_VB_BUTTON[] = {-0.0271f, -0.0571f, -0.0165f, -0.0457f};
    static private float CUSTOM_OBJECT_VB_BUTTON[] = {-0.0045f, -0.0571f, 0.0061f, -0.0457f};
    static private float ROT_VB_BUTTON[] = {0.0169f, -0.0571f, 0.0469f, -0.0457f};
    static private float REG_VB_BUTTON[] = {0.0533f, -0.0607f, 0.0687f, -0.0459f};
    Random ran = new Random();
    float T_x_position = ran.nextInt(260);
    float T_y_position = ran.nextInt(170);
    float B_x_position = ran.nextInt(260);
    float B_y_position = ran.nextInt(170);
    float P_x_position = ran.nextInt(260);
    float P_y_position = ran.nextInt(170);
    float B1_x_position = ran.nextInt(260);
    float B1_y_position = ran.nextInt(170);
    float B2_x_position = ran.nextInt(260);
    float B2_y_position = ran.nextInt(170);
    float B3_x_position = ran.nextInt(260);
    float B3_y_position = ran.nextInt(170);
    float S_x_position = ran.nextInt(260);
    float S_y_position = ran.nextInt(170);
    float H_x_position = ran.nextInt(260);
    float H_y_position = ran.nextInt(170);
    private SampleApplicationSession vuforiaAppSession;
    private SampleAppRenderer mSampleAppRenderer;
    private boolean mIsActive = false;
    private VirtualButtons mActivity;
    private Vector<Texture> mTextures;
    private Teapot mTeapot = new Teapot();
    private CubeObject mCube = new CubeObject();
    private OBJLoader mObj;
    private OBJLoader mObj_to;
    private OBJLoader mObj_ba;
    private OBJLoader mObj_pe;
    private OBJLoader mObj_bo_1;
    private OBJLoader mObj_bo_2;
    private OBJLoader mObj_bo_3;
    private OBJLoader mObj_sh;
    private OBJLoader mObj_he;
    // OpenGL ES 2.0 specific (3D model):
    private int shaderProgramID = 0;
    private int vertexHandle = 0;
    private int textureCoordHandle = 0;
    private int mvpMatrixHandle = 0;
    private int texSampler2DHandle = 0;
    private int lineOpacityHandle = 0;
    private int lineColorHandle = 0;
    private int mvpMatrixButtonsHandle = 0;
    // OpenGL ES 2.0 specific (Virtual Buttons):
    private int vbShaderProgramID = 0;
    private int vbVertexHandle = 0;
    //rotate
    private float angle = 0.0f;
    private boolean rotate = false;

    private int time_o = 30;
    private int score = 0;
    private int h_score = 0;


    //trans
    private float step_h = 0.0f;
    private float step_w = 0.0f;
    private float step_alpa = 0.0f;
    private boolean trans = false;
    private int textureIndex = 3;

    private boolean tomato = true;
    private boolean banana = true;
    private boolean pear = true;
    private boolean game = true;
    //lego
    private int objects;
    private ArrayList verticeBuffers;
    private ArrayList textureBuffers;
    //tomato
    private int objects_to;
    private ArrayList verticeBuffers_to;
    private ArrayList textureBuffers_to;
    //banana
    private int objects_ba;
    private ArrayList verticeBuffers_ba;
    private ArrayList textureBuffers_ba;
    //pear
    private int objects_pe;
    private ArrayList verticeBuffers_pe;
    private ArrayList textureBuffers_pe;
    //bomb_1
    private int objects_bo_1;
    private ArrayList verticeBuffers_bo_1;
    private ArrayList textureBuffers_bo_1;
    //bomb_2
    private int objects_bo_2;
    private ArrayList verticeBuffers_bo_2;
    private ArrayList textureBuffers_bo_2;
    //bomb_3
    private int objects_bo_3;
    private ArrayList verticeBuffers_bo_3;
    private ArrayList textureBuffers_bo_3;
    //boots
    private int objects_sh;
    private ArrayList verticeBuffers_sh;
    private ArrayList textureBuffers_sh;
    //heart
    private int objects_he;
    private ArrayList verticeBuffers_he;
    private ArrayList textureBuffers_he;


    public VirtualButtonRenderer(VirtualButtons activity,
                                 SampleApplicationSession session) {
        mActivity = activity;
        vuforiaAppSession = session;

        // SampleAppRenderer used to encapsulate the use of RenderingPrimitives setting
        // the device mode AR/VR and stereo mode
        mSampleAppRenderer = new SampleAppRenderer(this, mActivity, Device.MODE.MODE_AR, false, 0.01f, 5f);
    }


    // Called when the surface is created or recreated.
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(LOGTAG, "GLRenderer.onSurfaceCreated");

        // Call Vuforia function to (re)initialize rendering after first use
        // or after OpenGL ES context was lost (e.g. after onPause/onResume):
        vuforiaAppSession.onSurfaceCreated();

        mSampleAppRenderer.onSurfaceCreated();
    }

    // Called when the surface changed size.
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(LOGTAG, "GLRenderer.onSurfaceChanged");

        // Call Vuforia function to handle render surface size changes:
        vuforiaAppSession.onSurfaceChanged(width, height);

        // RenderingPrimitives to be updated when some rendering change is done
        mSampleAppRenderer.onConfigurationChanged(mIsActive);

        // Call function to initialize rendering:
        initRendering();
    }


    public void setActive(boolean active) {
        mIsActive = active;

        if (mIsActive)
            mSampleAppRenderer.configureVideoBackground();

        //Timer textView
        Timer m_timer = new Timer();
        TimerTask m_task = new TimerTask() {
            @Override
            public void run() {
                if (time_o > 0) time_o--;
            }
        };
        m_timer.schedule(m_task, 0, 1000);
    }


    // Called to draw the current frame.
    @Override
    public void onDrawFrame(GL10 gl) {
        if (!mIsActive)
            return;

        // Call our function to render content from SampleAppRenderer class
        mSampleAppRenderer.render();
    }


    private void initRendering() {
        Log.d(LOGTAG, "initRendering");

        // Define clear color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f
                : 1.0f);

        // Now generate the OpenGL texture objects and add settings
        for (Texture t : mTextures) {
            GLES20.glGenTextures(1, t.mTextureID, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t.mTextureID[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
                    t.mWidth, t.mHeight, 0, GLES20.GL_RGBA,
                    GLES20.GL_UNSIGNED_BYTE, t.mData);
        }

        shaderProgramID = SampleUtils.createProgramFromShaderSrc(
                CubeShaders.CUBE_MESH_VERTEX_SHADER,
                CubeShaders.CUBE_MESH_FRAGMENT_SHADER);

        vertexHandle = GLES20.glGetAttribLocation(shaderProgramID,
                "vertexPosition");
        textureCoordHandle = GLES20.glGetAttribLocation(shaderProgramID,
                "vertexTexCoord");
        mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramID,
                "modelViewProjectionMatrix");
        texSampler2DHandle = GLES20.glGetUniformLocation(shaderProgramID,
                "texSampler2D");

        // OpenGL setup for Virtual Buttons
        vbShaderProgramID = SampleUtils.createProgramFromShaderSrc(
                LineShaders.LINE_VERTEX_SHADER, LineShaders.LINE_FRAGMENT_SHADER);

        mvpMatrixButtonsHandle = GLES20.glGetUniformLocation(vbShaderProgramID,
                "modelViewProjectionMatrix");
        vbVertexHandle = GLES20.glGetAttribLocation(vbShaderProgramID,
                "vertexPosition");
        lineOpacityHandle = GLES20.glGetUniformLocation(vbShaderProgramID,
                "opacity");
        lineColorHandle = GLES20.glGetUniformLocation(vbShaderProgramID,
                "color");

        //lego
        ObjParser objParser = new ObjParser(mActivity);
        try {
            objParser.parse(R.raw.lego_t);
        } catch (IOException e) {
        }
        objects = objParser.getObjectIds().size();
        mObj = new OBJLoader(objParser);
        verticeBuffers = mObj.getBuffers(0);
        textureBuffers = mObj.getBuffers(2);

        //tomato
        ObjParser objParser_to = new ObjParser(mActivity);
        try {
            objParser_to.parse(R.raw.tomato_t);
            T_x_position -= 130;
            T_y_position -= 70;
        } catch (IOException e) {
        }
        objects_to = objParser_to.getObjectIds().size();
        mObj_to = new OBJLoader(objParser_to);
        verticeBuffers_to = mObj_to.getBuffers(0);
        textureBuffers_to = mObj_to.getBuffers(2);

        //banana
        ObjParser objParser_ba = new ObjParser(mActivity);
        try {
            objParser_ba.parse(R.raw.banana_t);
            B_x_position -= 130;
            B_y_position -= 70;
        } catch (IOException e) {
        }
        objects_ba = objParser_ba.getObjectIds().size();
        mObj_ba = new OBJLoader(objParser_ba);
        verticeBuffers_ba = mObj_ba.getBuffers(0);
        textureBuffers_ba = mObj_ba.getBuffers(2);

        //pear
        ObjParser objParser_pe = new ObjParser(mActivity);
        try {
            objParser_pe.parse(R.raw.pear_t);
            P_x_position -= 130;
            P_y_position -= 70;
        } catch (IOException e) {
        }
        objects_pe = objParser_pe.getObjectIds().size();
        mObj_pe = new OBJLoader(objParser_pe);
        verticeBuffers_pe = mObj_pe.getBuffers(0);
        textureBuffers_pe = mObj_pe.getBuffers(2);

        //bomb1
        ObjParser objParser_bo1 = new ObjParser(mActivity);
        try {
            objParser_bo1.parse(R.raw.bomb_t);
            B1_x_position -= 130;
            B1_y_position -= 70;
        } catch (IOException e) {
        }
        objects_bo_1 = objParser_bo1.getObjectIds().size();
        mObj_bo_1 = new OBJLoader(objParser_bo1);
        verticeBuffers_bo_1 = mObj_bo_1.getBuffers(0);
        textureBuffers_bo_1 = mObj_bo_1.getBuffers(2);

        //bomb2
        ObjParser objParser_bo2 = new ObjParser(mActivity);
        try {
            objParser_bo2.parse(R.raw.bomb_t);
            B2_x_position -= 130;
            B2_y_position -= 70;
        } catch (IOException e) {
        }
        objects_bo_2 = objParser_bo2.getObjectIds().size();
        mObj_bo_2 = new OBJLoader(objParser_bo2);
        verticeBuffers_bo_2 = mObj_bo_2.getBuffers(0);
        textureBuffers_bo_2 = mObj_bo_2.getBuffers(2);

        //bomb3
        ObjParser objParser_bo3 = new ObjParser(mActivity);
        try {
            objParser_bo3.parse(R.raw.bomb_t);
            B3_x_position -= 130;
            B3_y_position -= 70;
        } catch (IOException e) {
        }
        objects_bo_3 = objParser_bo3.getObjectIds().size();
        mObj_bo_3 = new OBJLoader(objParser_bo3);
        verticeBuffers_bo_3 = mObj_bo_3.getBuffers(0);
        textureBuffers_bo_3 = mObj_bo_3.getBuffers(2);

        //shoes
        ObjParser objParser_sh = new ObjParser(mActivity);
        try {
            objParser_sh.parse(R.raw.boots_t);
            S_x_position -= 130;
            S_y_position -= 70;
        } catch (IOException e) {
        }
        objects_sh = objParser_sh.getObjectIds().size();
        mObj_sh = new OBJLoader(objParser_sh);
        verticeBuffers_sh = mObj_sh.getBuffers(0);
        textureBuffers_sh = mObj_sh.getBuffers(2);

        //heart
        ObjParser objParser_he = new ObjParser(mActivity);
        try {
            objParser_he.parse(R.raw.heart_t);
            H_x_position -= 130;
            H_y_position -= 70;
        } catch (IOException e) {
        }
        objects_he = objParser_he.getObjectIds().size();
        mObj_he = new OBJLoader(objParser_he);
        verticeBuffers_he = mObj_he.getBuffers(0);
        textureBuffers_he = mObj_he.getBuffers(2);

    }


    public void updateRenderingPrimitives() {
        mSampleAppRenderer.updateRenderingPrimitives();
    }


    // The render function called from SampleAppRendering by using RenderingPrimitives views.
    // The state is owned by SampleAppRenderer which is controlling it's lifecycle.
    // State should not be cached outside this method.
    public void renderFrame(State state, float[] projectionMatrix) {
        // Renders video background replacing Renderer.DrawVideoBackground()
        mSampleAppRenderer.renderVideoBackground();

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        /*GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);*/

        // Did we find any trackables this frame?
        if (state.getNumTrackableResults() > 0) {
            // Get the trackable:
            TrackableResult trackableResult = state.getTrackableResult(0);
            float[] modelViewMatrix = Tool.convertPose2GLMatrix(
                    trackableResult.getPose()).getData();

            float[] modelViewMatrix_to = Tool.convertPose2GLMatrix(
                    trackableResult.getPose()).getData();

            float[] modelViewMatrix_ba = Tool.convertPose2GLMatrix(
                    trackableResult.getPose()).getData();

            float[] modelViewMatrix_pr = Tool.convertPose2GLMatrix(
                    trackableResult.getPose()).getData();

            float[] modelViewMatrix_bo1 = Tool.convertPose2GLMatrix(
                    trackableResult.getPose()).getData();

            float[] modelViewMatrix_bo2 = Tool.convertPose2GLMatrix(
                    trackableResult.getPose()).getData();

            float[] modelViewMatrix_bo3 = Tool.convertPose2GLMatrix(
                    trackableResult.getPose()).getData();

            float[] modelViewMatrix_sh = Tool.convertPose2GLMatrix(
                    trackableResult.getPose()).getData();

            float[] modelViewMatrix_he = Tool.convertPose2GLMatrix(
                    trackableResult.getPose()).getData();

            // The image target specific result:
            ImageTargetResult imageTargetResult = (ImageTargetResult) trackableResult;

            // Set transformations:
            float[] modelViewProjection = new float[16];
            Matrix.multiplyMM(modelViewProjection, 0, projectionMatrix, 0, modelViewMatrix, 0);

            float[] modelViewProjection_to = new float[16];
            Matrix.multiplyMM(modelViewProjection_to, 0, projectionMatrix, 0, modelViewMatrix_to, 0);

            float[] modelViewProjection_ba = new float[16];
            Matrix.multiplyMM(modelViewProjection_ba, 0, projectionMatrix, 0, modelViewMatrix_ba, 0);

            float[] modelViewProjection_pr = new float[16];
            Matrix.multiplyMM(modelViewProjection_pr, 0, projectionMatrix, 0, modelViewMatrix_pr, 0);

            float[] modelViewProjection_bo_1 = new float[16];
            Matrix.multiplyMM(modelViewProjection_bo_1, 0, projectionMatrix, 0, modelViewMatrix_bo1, 0);

            float[] modelViewProjection_bo_2 = new float[16];
            Matrix.multiplyMM(modelViewProjection_bo_2, 0, projectionMatrix, 0, modelViewMatrix_bo2, 0);

            float[] modelViewProjection_bo_3 = new float[16];
            Matrix.multiplyMM(modelViewProjection_bo_3, 0, projectionMatrix, 0, modelViewMatrix_bo3, 0);

            float[] modelViewProjection_sh = new float[16];
            Matrix.multiplyMM(modelViewProjection_sh, 0, projectionMatrix, 0, modelViewMatrix_sh, 0);

            float[] modelViewProjection_he = new float[16];
            Matrix.multiplyMM(modelViewProjection_he, 0, projectionMatrix, 0, modelViewMatrix_he, 0);

            /*TimerTask count_down = new TimerTask() {
                @Override
                public void run() {
                    //Log.i("test", "timer is start!");
                    Runnable update = new Runnable() {
                        @Override
                        public void run() {
                            timer.setText("TIME OUT : " + String.valueOf(time_o));
                        }
                    };
                    time_o--;
                }
            };
            Timer timer_1 = new Timer();
            timer_1.schedule(count_down, 0, 1000);*/

            /*
            //Timer textView
            Timer m_timer = new Timer();
            TimerTask m_task = new TimerTask() {
                @Override
                public void run() {
                    if(time_o>0) time_o--;
                }
            };
            m_timer.schedule(m_task,0,1000);
            */

            //score textView
            final TextView score_view = (TextView) mActivity.findViewById(R.id.textView_score);
            final TextView timer_vew = (TextView) mActivity.findViewById(R.id.textView_timer);
            final TextView h_score_vew = (TextView) mActivity.findViewById(R.id.textView_h_score);
            score_view.post(new Runnable() {
                @Override
                public void run() {
                    score_view.setText("SCORE : " + String.valueOf(score));
                    timer_vew.setText("TIME OUT : " + String.valueOf(time_o));
                    h_score_vew.setText("HIGHEST SCORE : " + String.valueOf(h_score));
                }
            });


            //button clicked
            Button button_res = (Button) mActivity.findViewById(R.id.button_restart);
            button_res.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(LOGTAG, "buttons is clicked");
                    T_x_position = ran.nextInt(260) - 130;
                    T_y_position = ran.nextInt(170) - 70;
                    B_x_position = ran.nextInt(260) - 130;
                    B_y_position = ran.nextInt(170) - 70;
                    P_x_position = ran.nextInt(260) - 130;
                    P_y_position = ran.nextInt(170) - 70;

                    B1_x_position = ran.nextInt(260) - 130;
                    B1_y_position = ran.nextInt(170) - 70;
                    B2_x_position = ran.nextInt(260) - 130;
                    B2_y_position = ran.nextInt(170) - 70;
                    B3_x_position = ran.nextInt(260) - 130;
                    B3_y_position = ran.nextInt(170) - 70;

                    S_x_position = ran.nextInt(260) - 130;
                    S_y_position = ran.nextInt(170) - 70;
                    H_x_position = ran.nextInt(260) - 130;
                    H_y_position = ran.nextInt(170) - 70;

                    step_h = step_w = 0.0f;
                    if (h_score < score) h_score = score;
                    score = 0;
                    time_o = 30;
                    game = banana = tomato = pear = true;
                    step_alpa = 0.0f;

                }
            });


            // Set the texture used for the teapot model:
            //int textureIndex = 0;

            float vbVertices[] = new float[imageTargetResult
                    .getNumVirtualButtons() * 24];
            short vbCounter = 0;

            // Iterate through this targets virtual buttons:
            for (int i = 0; i < imageTargetResult.getNumVirtualButtons(); ++i) {
                VirtualButtonResult buttonResult = imageTargetResult
                        .getVirtualButtonResult(i);
                VirtualButton button = buttonResult.getVirtualButton();

                int buttonIndex = 0;
                rotate = false;
                // Run through button name array to find button index
                for (int j = 0; j < VirtualButtons.NUM_BUTTONS; ++j) {
                    if (button.getName().compareTo(mActivity.virtualButtons[j]) == 0) {
                        buttonIndex = j;
                        break;
                    }
                }
                // If the button is pressed, than use this texture:
                if (buttonResult.isPressed()) {
                    /*if (buttonIndex >= 0 && buttonIndex < 4) {
                        textureIndex = buttonIndex;
                    } else {
                        rotate = false;
                    }*/
                    Log.d(LOGTAG, "button is pressed" + T_x_position + " " + T_y_position);
                    MediaPlayer m2 = MediaPlayer.create(mActivity.getBaseContext(), R.raw.battle003);
                    if ((game == true) && (B1_x_position - 20 <= step_w) && (step_w <= B1_x_position + 20) && (B1_y_position - 20 <= step_h) && (step_h <= B1_y_position + 20)) {
                        m2.start();
                        game = false;
                        time_o = 0;
                        step_alpa = 0;
                    }
                    if ((game == true) && (B2_x_position - 20 <= step_w) && (step_w <= B2_x_position + 20) && (B2_y_position - 20 <= step_h) && (step_h <= B2_y_position + 20)) {
                        m2.start();
                        game = false;
                        time_o = 0;
                        step_alpa = 0;
                    }
                    if ((game == true) && (B3_x_position - 20 <= step_w) && (step_w <= B3_x_position + 20) && (B3_y_position - 20 <= step_h) && (step_h <= B3_y_position + 20)) {
                        m2.start();
                        game = false;
                        time_o = 0;
                        step_alpa = 0;
                    }

                    if ((game == true) && (time_o <= 0)) {
                        m2.start();
                        game = false;
                        step_alpa = 0;
                    }
                    if ((S_x_position - 20 <= step_w) && (step_w <= S_x_position + 20) && (S_y_position - 20 <= step_h) && (step_h <= S_y_position + 20)) {
                        if (step_alpa <= 3) step_alpa += 1.0f;
                        S_x_position = ran.nextInt(260) - 130;
                        S_y_position = ran.nextInt(170) - 70;
                    }

                    if ((H_x_position - 20 <= step_w) && (step_w <= H_x_position + 20) && (H_y_position - 20 <= step_h) && (step_h <= H_y_position + 20)) {
                        time_o += 5;
                        H_x_position = ran.nextInt(260) - 130;
                        H_y_position = ran.nextInt(170) - 70;
                    }
                    //m2.stop();

                    if ((tomato == false) && (pear == false) && (banana == false)) {
                        T_x_position = ran.nextInt(260) - 130;
                        T_y_position = ran.nextInt(170) - 70;
                        B_x_position = ran.nextInt(260) - 130;
                        B_y_position = ran.nextInt(170) - 70;
                        P_x_position = ran.nextInt(260) - 130;
                        P_y_position = ran.nextInt(170) - 70;
                        tomato = true;
                        pear = true;
                        banana = true;
                    }
                    textureIndex = 3;
                    if (buttonIndex == 0) {
                        step_w -= 1.5f + step_alpa;
                        angle = 270;
                    } else if (buttonIndex == 1) {
                        step_h += 1.5f + step_alpa;
                        angle = 180;

                    } else if (buttonIndex == 2) {
                        step_w += 1.5f + step_alpa;
                        angle = 90;
                    } else if (buttonIndex == 3) {
                        step_h -= 1.5f + step_alpa;
                        angle = 0;

                    } else if (buttonIndex == 4) {
                        Log.d(LOGTAG, "im button 4" + T_x_position + " " + T_y_position);

                    } else if (buttonIndex == 5) {

                        MediaPlayer m = MediaPlayer.create(mActivity.getBaseContext(), R.raw.cartoon130);
                        if ((tomato == true) && (T_x_position - 20 <= step_w) && (step_w <= T_x_position + 20) && (T_y_position - 20 <= step_h) && (step_h <= T_y_position + 20)) {
                            tomato = false;
                            m.start();
                            score += 10;
                            //score_view.setText("SCORE : " + String.valueOf(score));
                            Log.d(LOGTAG, "score : " + score);
                            //score_view.setText("TIME score : " + score);
                        }
                        if ((banana == true) && (B_x_position - 30 <= step_w) && (step_w <= B_x_position + 30) && (B_y_position - 30 <= step_h) && (step_h <= B_y_position + 30)) {
                            banana = false;
                            m.start();
                            score += 10;
                            //score_view.setText("SCORE : " + String.valueOf(score));
                            Log.d(LOGTAG, "score : " + score);
                            //score_view.setText("TIME score : " + score);
                        }
                        if ((pear == true) && (P_x_position - 20 <= step_w) && (step_w <= P_x_position + 20) && (P_y_position - 20 <= step_h) && (step_h <= P_y_position + 20)) {
                            pear = false;
                            m.start();
                            score += 10;
                            //score_view.setText("SCORE : " + String.valueOf(score));
                            Log.d(LOGTAG, "score : " + score);
                            //score_view.setText("TIME score : " + score);
                        }


                    }
                }

                // Define the four virtual buttons as Rectangle using the same values as the dataset
                Rectangle vbRectangle[] = new Rectangle[6];
                vbRectangle[0] = new Rectangle(RED_VB_BUTTON[0], RED_VB_BUTTON[1],
                        RED_VB_BUTTON[2], RED_VB_BUTTON[3]);
                vbRectangle[1] = new Rectangle(BLUE_VB_BUTTON[0], BLUE_VB_BUTTON[1],
                        BLUE_VB_BUTTON[2], BLUE_VB_BUTTON[3]);
                vbRectangle[2] = new Rectangle(YELLOW_VB_BUTTON[0], YELLOW_VB_BUTTON[1],
                        YELLOW_VB_BUTTON[2], YELLOW_VB_BUTTON[3]);
                vbRectangle[3] = new Rectangle(CUSTOM_OBJECT_VB_BUTTON[0], CUSTOM_OBJECT_VB_BUTTON[1],
                        CUSTOM_OBJECT_VB_BUTTON[2], CUSTOM_OBJECT_VB_BUTTON[3]);
                vbRectangle[4] = new Rectangle(ROT_VB_BUTTON[0], ROT_VB_BUTTON[1],
                        ROT_VB_BUTTON[2], ROT_VB_BUTTON[3]);
                vbRectangle[5] = new Rectangle(REG_VB_BUTTON[0], REG_VB_BUTTON[1],
                        REG_VB_BUTTON[2], REG_VB_BUTTON[3]);


                // We add the vertices to a common array in order to have one
                // single
                // draw call. This is more efficient than having multiple
                // glDrawArray calls
                vbVertices[vbCounter] = vbRectangle[buttonIndex].getLeftTopX();
                vbVertices[vbCounter + 1] = vbRectangle[buttonIndex]
                        .getLeftTopY();
                vbVertices[vbCounter + 2] = 0.0f;
                vbVertices[vbCounter + 3] = vbRectangle[buttonIndex]
                        .getRightBottomX();
                vbVertices[vbCounter + 4] = vbRectangle[buttonIndex]
                        .getLeftTopY();
                vbVertices[vbCounter + 5] = 0.0f;
                vbVertices[vbCounter + 6] = vbRectangle[buttonIndex]
                        .getRightBottomX();
                vbVertices[vbCounter + 7] = vbRectangle[buttonIndex]
                        .getLeftTopY();
                vbVertices[vbCounter + 8] = 0.0f;
                vbVertices[vbCounter + 9] = vbRectangle[buttonIndex]
                        .getRightBottomX();
                vbVertices[vbCounter + 10] = vbRectangle[buttonIndex]
                        .getRightBottomY();
                vbVertices[vbCounter + 11] = 0.0f;
                vbVertices[vbCounter + 12] = vbRectangle[buttonIndex]
                        .getRightBottomX();
                vbVertices[vbCounter + 13] = vbRectangle[buttonIndex]
                        .getRightBottomY();
                vbVertices[vbCounter + 14] = 0.0f;
                vbVertices[vbCounter + 15] = vbRectangle[buttonIndex]
                        .getLeftTopX();
                vbVertices[vbCounter + 16] = vbRectangle[buttonIndex]
                        .getRightBottomY();
                vbVertices[vbCounter + 17] = 0.0f;
                vbVertices[vbCounter + 18] = vbRectangle[buttonIndex]
                        .getLeftTopX();
                vbVertices[vbCounter + 19] = vbRectangle[buttonIndex]
                        .getRightBottomY();
                vbVertices[vbCounter + 20] = 0.0f;
                vbVertices[vbCounter + 21] = vbRectangle[buttonIndex]
                        .getLeftTopX();
                vbVertices[vbCounter + 22] = vbRectangle[buttonIndex]
                        .getLeftTopY();
                vbVertices[vbCounter + 23] = 0.0f;
                vbCounter += 24;

            }

            // We only render if there is something on the array
            if (vbCounter > 0) {
                // Render frame around button
                GLES20.glUseProgram(vbShaderProgramID);

                GLES20.glVertexAttribPointer(vbVertexHandle, 3,
                        GLES20.GL_FLOAT, false, 0, fillBuffer(vbVertices));

                GLES20.glEnableVertexAttribArray(vbVertexHandle);

                GLES20.glUniform1f(lineOpacityHandle, 1.0f);
                GLES20.glUniform3f(lineColorHandle, 1.0f, 1.0f, 1.0f);

                GLES20.glUniformMatrix4fv(mvpMatrixButtonsHandle, 1, false,
                        modelViewProjection, 0);

                // We multiply by 8 because that's the number of vertices per
                // button
                // The reason is that GL_LINES considers only pairs. So some
                // vertices
                // must be repeated.
                GLES20.glDrawArrays(GLES20.GL_LINES, 0,
                        imageTargetResult.getNumVirtualButtons() * 8);

                SampleUtils.checkGLError("VirtualButtons drawButton");

                GLES20.glDisableVertexAttribArray(vbVertexHandle);
            }

            // Assumptions:
            Texture thisTexture = mTextures.get(textureIndex);
            Texture toTexture = mTextures.get(textureIndex + 1);
            Texture baTexture = mTextures.get(textureIndex + 2);
            Texture peTexture = mTextures.get(textureIndex + 3);
            Texture boTexture = mTextures.get(textureIndex + 4);
            Texture shTexture = mTextures.get(textureIndex + 5);
            Texture heTexture = mTextures.get(textureIndex + 6);

            float rotationMatrix[] = new float[16];
            float[] modelViewProjectionScaled = new float[16];

            float rotationMatrix_to[] = new float[16];
            float[] modelViewProjectionScaled_to = new float[16];

            float rotationMatrix_ba[] = new float[16];
            float[] modelViewProjectionScaled_ba = new float[16];

            float rotationMatrix_pr[] = new float[16];
            float[] modelViewProjectionScaled_pr = new float[16];

            float rotationMatrix_bo1[] = new float[16];
            float[] modelViewProjectionScaled_bo1 = new float[16];

            float rotationMatrix_bo2[] = new float[16];
            float[] modelViewProjectionScaled_bo2 = new float[16];

            float rotationMatrix_bo3[] = new float[16];
            float[] modelViewProjectionScaled_bo3 = new float[16];

            float rotationMatrix_sh[] = new float[16];
            float[] modelViewProjectionScaled_sh = new float[16];

            float rotationMatrix_he[] = new float[16];
            float[] modelViewProjectionScaled_he = new float[16];

            // Scale 3D model
            if (textureIndex == 0 || textureIndex == 1) {
                Matrix.scaleM(modelViewMatrix, 0, kTeapotScale, kTeapotScale, kTeapotScale);
            } else if (textureIndex == 2) {
                Matrix.scaleM(modelViewMatrix, 0, kCubeScale, kCubeScale, kCubeScale);
            } else if (textureIndex == 3) {
                Matrix.scaleM(modelViewMatrix, 0, kObjScale, kObjScale, kObjScale);
                Matrix.scaleM(modelViewMatrix_to, 0, kObjScale, kObjScale, kObjScale);
                Matrix.scaleM(modelViewMatrix_ba, 0, kObjScale, kObjScale, kObjScale);
                Matrix.scaleM(modelViewMatrix_pr, 0, kObjScale, kObjScale, kObjScale);
                Matrix.scaleM(modelViewMatrix_bo1, 0, kObjScale, kObjScale, kObjScale);
                Matrix.scaleM(modelViewMatrix_bo2, 0, kObjScale, kObjScale, kObjScale);
                Matrix.scaleM(modelViewMatrix_bo3, 0, kObjScale, kObjScale, kObjScale);
                Matrix.scaleM(modelViewMatrix_sh, 0, kObjScale, kObjScale, kObjScale);
                Matrix.scaleM(modelViewMatrix_he, 0, kObjScale, kObjScale, kObjScale);
            }

            GLES20.glUseProgram(shaderProgramID);

            GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
                    false, 0, mTeapot.getVertices());
            GLES20.glVertexAttribPointer(textureCoordHandle, 2,
                    GLES20.GL_FLOAT, false, 0, mTeapot.getTexCoords());

            GLES20.glEnableVertexAttribArray(vertexHandle);
            GLES20.glEnableVertexAttribArray(textureCoordHandle);


            /*
            if (textureIndex == 0 || textureIndex == 1) {
                Matrix.setRotateM(rotationMatrix, 0, angle, 0.0f, 0.0f, 1.0f);
                Matrix.multiplyMM(modelViewMatrix, 0, modelViewMatrix, 0, rotationMatrix, 0);
                Matrix.multiplyMM(modelViewProjectionScaled, 0, projectionMatrix, 0, modelViewMatrix, 0);
                GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
                        false, 0, mTeapot.getVertices());
                GLES20.glVertexAttribPointer(textureCoordHandle, 2,
                        GLES20.GL_FLOAT, false, 0, mTeapot.getTexCoords());
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                        thisTexture.mTextureID[0]);
                GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false,
                        modelViewProjectionScaled, 0);
                GLES20.glUniform1i(texSampler2DHandle, 0);
                GLES20.glDrawElements(GLES20.GL_TRIANGLES,
                        mTeapot.getNumObjectIndex(), GLES20.GL_UNSIGNED_SHORT,
                        mTeapot.getIndices());
            }

            else if (textureIndex == 2) {
                Matrix.setRotateM(rotationMatrix, 0, angle, 0.0f, 0.0f, 1.0f);
                Matrix.multiplyMM(modelViewMatrix, 0, modelViewMatrix, 0, rotationMatrix, 0);
                Matrix.multiplyMM(modelViewProjectionScaled, 0, projectionMatrix, 0, modelViewMatrix, 0);
                GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
                        false, 0, mCube.getVertices());
                GLES20.glVertexAttribPointer(textureCoordHandle, 2,
                        GLES20.GL_FLOAT, false, 0, mCube.getTexCoords());
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                        thisTexture.mTextureID[0]);
                GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false,
                        modelViewProjectionScaled, 0);
                GLES20.glUniform1i(texSampler2DHandle, 0);
                GLES20.glDrawElements(GLES20.GL_TRIANGLES,
                        mCube.getNumObjectIndex(), GLES20.GL_UNSIGNED_SHORT,
                        mCube.getIndices());
            }

            else if (textureIndex == 3) {

            }

            if (rotate) {
                angle += 1.0f;
                if (angle >= 360.0f) {
                    angle -= 360.0f;
                }

            }*/
            if (game) {
                //lego
                Matrix.translateM(modelViewMatrix, 0, step_w, step_h, 0);
                Matrix.setRotateM(rotationMatrix, 0, angle, 0.0f, 0.0f, 1.0f);
                Matrix.multiplyMM(modelViewMatrix, 0, modelViewMatrix, 0, rotationMatrix, 0);
                Matrix.setRotateM(rotationMatrix, 0, 90.0f, 1.0f, 0.0f, 0.0f);
                Matrix.multiplyMM(modelViewMatrix, 0, modelViewMatrix, 0, rotationMatrix, 0);
                Matrix.multiplyMM(modelViewProjectionScaled, 0, projectionMatrix, 0, modelViewMatrix, 0);
                GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, (FloatBuffer) verticeBuffers.get(0));
                GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, (FloatBuffer) textureBuffers.get(0));
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, thisTexture.mTextureID[0]);
                GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjectionScaled, 0);
                GLES20.glUniform1i(texSampler2DHandle, 0);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mObj.getNumObjectVertex(0));

                //bomb1
                Matrix.translateM(modelViewMatrix_bo1, 0, B1_x_position, B1_y_position, 0);
                Matrix.setRotateM(rotationMatrix_bo1, 0, 90, 0.0f, 0.0f, 1.0f);
                Matrix.multiplyMM(modelViewMatrix_bo1, 0, modelViewMatrix_bo1, 0, rotationMatrix_bo1, 0);
                Matrix.setRotateM(rotationMatrix_bo1, 0, 90.0f, 1.0f, 0.0f, 0.0f);
                Matrix.multiplyMM(modelViewMatrix_bo1, 0, modelViewMatrix_bo1, 0, rotationMatrix_bo1, 0);
                Matrix.multiplyMM(modelViewProjectionScaled_bo1, 0, projectionMatrix, 0, modelViewMatrix_bo1, 0);
                GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, (FloatBuffer) verticeBuffers_bo_1.get(0));
                GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, (FloatBuffer) textureBuffers_bo_1.get(0));
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, boTexture.mTextureID[0]);
                GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjectionScaled_bo1, 0);
                GLES20.glUniform1i(texSampler2DHandle, 0);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mObj_bo_1.getNumObjectVertex(0));

                //bomb2
                Matrix.translateM(modelViewMatrix_bo2, 0, B2_x_position, B2_y_position, 0);
                Matrix.setRotateM(rotationMatrix_bo2, 0, 90, 0.0f, 0.0f, 1.0f);
                Matrix.multiplyMM(modelViewMatrix_bo2, 0, modelViewMatrix_bo2, 0, rotationMatrix_bo2, 0);
                Matrix.setRotateM(rotationMatrix_bo2, 0, 90.0f, 1.0f, 0.0f, 0.0f);
                Matrix.multiplyMM(modelViewMatrix_bo2, 0, modelViewMatrix_bo2, 0, rotationMatrix_bo2, 0);
                Matrix.multiplyMM(modelViewProjectionScaled_bo2, 0, projectionMatrix, 0, modelViewMatrix_bo2, 0);
                GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, (FloatBuffer) verticeBuffers_bo_2.get(0));
                GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, (FloatBuffer) textureBuffers_bo_2.get(0));
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, boTexture.mTextureID[0]);
                GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjectionScaled_bo2, 0);
                GLES20.glUniform1i(texSampler2DHandle, 0);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mObj_bo_2.getNumObjectVertex(0));

                //bomb3
                Matrix.translateM(modelViewMatrix_bo3, 0, B3_x_position, B3_y_position, 0);
                Matrix.setRotateM(rotationMatrix_bo3, 0, 90, 0.0f, 0.0f, 1.0f);
                Matrix.multiplyMM(modelViewMatrix_bo3, 0, modelViewMatrix_bo3, 0, rotationMatrix_bo3, 0);
                Matrix.setRotateM(rotationMatrix_bo3, 0, 90.0f, 1.0f, 0.0f, 0.0f);
                Matrix.multiplyMM(modelViewMatrix_bo3, 0, modelViewMatrix_bo3, 0, rotationMatrix_bo3, 0);
                Matrix.multiplyMM(modelViewProjectionScaled_bo3, 0, projectionMatrix, 0, modelViewMatrix_bo3, 0);
                GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, (FloatBuffer) verticeBuffers_bo_3.get(0));
                GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, (FloatBuffer) textureBuffers_bo_3.get(0));
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, boTexture.mTextureID[0]);
                GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjectionScaled_bo3, 0);
                GLES20.glUniform1i(texSampler2DHandle, 0);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mObj_bo_3.getNumObjectVertex(0));

                //shoes
                Matrix.translateM(modelViewMatrix_sh, 0, S_x_position, S_y_position, 0);
                Matrix.setRotateM(rotationMatrix_sh, 0, 90, 0.0f, 0.0f, 1.0f);
                Matrix.multiplyMM(modelViewMatrix_sh, 0, modelViewMatrix_sh, 0, rotationMatrix_sh, 0);
                Matrix.setRotateM(rotationMatrix_sh, 0, 90.0f, 1.0f, 0.0f, 0.0f);
                Matrix.multiplyMM(modelViewMatrix_sh, 0, modelViewMatrix_sh, 0, rotationMatrix_sh, 0);
                Matrix.multiplyMM(modelViewProjectionScaled_sh, 0, projectionMatrix, 0, modelViewMatrix_sh, 0);
                GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, (FloatBuffer) verticeBuffers_sh.get(0));
                GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, (FloatBuffer) textureBuffers_sh.get(0));
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, shTexture.mTextureID[0]);
                GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjectionScaled_sh, 0);
                GLES20.glUniform1i(texSampler2DHandle, 0);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mObj_sh.getNumObjectVertex(0));

                //heart
                Matrix.translateM(modelViewMatrix_he, 0, H_x_position, H_y_position, 0);
                Matrix.setRotateM(rotationMatrix_he, 0, 180, 0.0f, 0.0f, 1.0f);
                Matrix.multiplyMM(modelViewMatrix_he, 0, modelViewMatrix_he, 0, rotationMatrix_he, 0);
                Matrix.setRotateM(rotationMatrix_he, 0, 90.0f, 1.0f, 0.0f, 0.0f);
                Matrix.multiplyMM(modelViewMatrix_he, 0, modelViewMatrix_he, 0, rotationMatrix_he, 0);
                Matrix.multiplyMM(modelViewProjectionScaled_he, 0, projectionMatrix, 0, modelViewMatrix_he, 0);
                GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, (FloatBuffer) verticeBuffers_he.get(0));
                GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, (FloatBuffer) textureBuffers_he.get(0));
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, heTexture.mTextureID[0]);
                GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjectionScaled_he, 0);
                GLES20.glUniform1i(texSampler2DHandle, 0);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mObj_he.getNumObjectVertex(0));


                if (tomato == true) {
                    //tomato
                    Matrix.translateM(modelViewMatrix_to, 0, T_x_position, T_y_position, 0);
                    Matrix.setRotateM(rotationMatrix_to, 0, 90, 0.0f, 0.0f, 1.0f);
                    Matrix.multiplyMM(modelViewMatrix_to, 0, modelViewMatrix_to, 0, rotationMatrix_to, 0);
                    Matrix.setRotateM(rotationMatrix_to, 0, 90.0f, 1.0f, 0.0f, 0.0f);
                    Matrix.multiplyMM(modelViewMatrix_to, 0, modelViewMatrix_to, 0, rotationMatrix_to, 0);
                    Matrix.multiplyMM(modelViewProjectionScaled_to, 0, projectionMatrix, 0, modelViewMatrix_to, 0);
                    GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
                            false, 0, (FloatBuffer) verticeBuffers_to.get(0));
                    GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT,
                            false, 0, (FloatBuffer) textureBuffers_to.get(0));
                    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, toTexture.mTextureID[0]);
                    GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjectionScaled_to, 0);
                    GLES20.glUniform1i(texSampler2DHandle, 0);
                    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mObj_to.getNumObjectVertex(0));
                }

                if (banana == true) {
                    //banana
                    Matrix.translateM(modelViewMatrix_ba, 0, B_x_position, B_y_position, 0);
                    Matrix.setRotateM(rotationMatrix_ba, 0, 90, 0.0f, 0.0f, 1.0f);
                    Matrix.multiplyMM(modelViewMatrix_ba, 0, modelViewMatrix_ba, 0, rotationMatrix_ba, 0);
                    Matrix.setRotateM(rotationMatrix_ba, 0, 90.0f, 1.0f, 0.0f, 0.0f);
                    Matrix.multiplyMM(modelViewMatrix_ba, 0, modelViewMatrix_ba, 0, rotationMatrix_ba, 0);
                    Matrix.multiplyMM(modelViewProjectionScaled_ba, 0, projectionMatrix, 0, modelViewMatrix_ba, 0);
                    GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
                            false, 0, (FloatBuffer) verticeBuffers_ba.get(0));
                    GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT,
                            false, 0, (FloatBuffer) textureBuffers_ba.get(0));
                    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                            baTexture.mTextureID[0]);
                    GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false,
                            modelViewProjectionScaled_ba, 0);
                    GLES20.glUniform1i(texSampler2DHandle, 0);
                    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0,
                            mObj_ba.getNumObjectVertex(0));
                }

                if (pear == true) {
                    //pear
                    Matrix.translateM(modelViewMatrix_pr, 0, P_x_position, P_y_position, 0);
                    Matrix.setRotateM(rotationMatrix_pr, 0, 90, 0.0f, 0.0f, 1.0f);
                    Matrix.multiplyMM(modelViewMatrix_pr, 0, modelViewMatrix_pr, 0, rotationMatrix_pr, 0);
                    Matrix.setRotateM(rotationMatrix_pr, 0, 90.0f, 1.0f, 0.0f, 0.0f);
                    Matrix.multiplyMM(modelViewMatrix_pr, 0, modelViewMatrix_pr, 0, rotationMatrix_pr, 0);
                    Matrix.multiplyMM(modelViewProjectionScaled_pr, 0, projectionMatrix, 0, modelViewMatrix_pr, 0);
                    GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
                            false, 0, (FloatBuffer) verticeBuffers_pe.get(0));
                    GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT,
                            false, 0, (FloatBuffer) textureBuffers_pe.get(0));
                    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                            peTexture.mTextureID[0]);
                    GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false,
                            modelViewProjectionScaled_pr, 0);
                    GLES20.glUniform1i(texSampler2DHandle, 0);
                    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0,
                            mObj_pe.getNumObjectVertex(0));

                }
            }
            /*GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                thisTexture.mTextureID[0]);
            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false,
                modelViewProjectionScaled, 0);
            GLES20.glUniform1i(texSampler2DHandle, 0);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES,
                mTeapot.getNumObjectIndex(), GLES20.GL_UNSIGNED_SHORT,
                mTeapot.getIndices());*/

            GLES20.glDisableVertexAttribArray(vertexHandle);
            GLES20.glDisableVertexAttribArray(textureCoordHandle);

            SampleUtils.checkGLError("VirtualButtons renderFrame");
        }

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        Renderer.getInstance().end();

    }


    private Buffer fillBuffer(float[] array) {
        // Convert to floats because OpenGL doesnt work on doubles, and manually
        // casting each input value would take too much time.
        ByteBuffer bb = ByteBuffer.allocateDirect(4 * array.length); // each
        // float
        // takes 4
        // bytes
        bb.order(ByteOrder.LITTLE_ENDIAN);
        for (float d : array)
            bb.putFloat(d);
        bb.rewind();

        return bb;

    }


    public void setTextures(Vector<Texture> textures) {
        mTextures = textures;

    }

}
