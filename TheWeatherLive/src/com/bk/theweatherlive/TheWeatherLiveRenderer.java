package com.bk.theweatherlive;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import net.rbgrn.android.glwallpaperservice.GLWallpaperService;
import android.hardware.SensorEvent;
import android.opengl.GLU;
import android.view.MotionEvent;

public class TheWeatherLiveRenderer implements GLWallpaperService.Renderer {

	private Circle circle;
	private Triangle triangle;
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		
		gl.glEnable(GL10.GL_TEXTURE_2D);			//Enable Texture Mapping ( NEW )
		gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
		gl.glClearColor(0.53f, 0.81f, 0.92f, 1.0f); 	//Black Background
		gl.glClearDepthf(1.0f); 					//Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do
		
		//Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); 
		
	}
	
	public void onSensorChanged(SensorEvent event) {
	
	}

	public TheWeatherLiveRenderer() {
		this.circle = new Circle();
		this.triangle = new Triangle();
	}
		
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if (height == 0) {
			height = 1;
		}
		
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		
		GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100.0f);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
		gl.glTranslatef(1.7f, 3.0f, -12.5f);
		circle.draw(gl);
		
	}

	public void release() {
		
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		
		return true;
	}
}
