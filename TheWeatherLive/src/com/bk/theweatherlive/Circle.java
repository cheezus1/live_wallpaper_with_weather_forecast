package com.bk.theweatherlive;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Circle {
    private static final int VERTICES = 180;
    float coords[] = new float[VERTICES * 3];
    float theta = 0;

    private short[] indices = new short[VERTICES * 3];
    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;

    public Circle() {
        ByteBuffer vbb = ByteBuffer.allocateDirect(VERTICES * 3 * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();

        ByteBuffer ibb = ByteBuffer.allocateDirect(VERTICES * 3 * 2);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();
        
        for (int i = 0; i < VERTICES * 3; i += 3) {
            coords[i + 0] = (float) Math.cos(theta);
            coords[i + 1] = (float) Math.sin(theta);
            coords[i + 2] = 0;
            vertexBuffer.put(coords[i + 0]);
            vertexBuffer.put(coords[i + 1]);
            vertexBuffer.put(coords[i + 2]);
            
            indices[i + 0] = (short)(i + 0);
            indices[i + 1] = (short)(i + 1);
            indices[i + 2] = (short)(i + 2);
            indexBuffer.put(indices[i + 0]);
            indexBuffer.put(indices[i + 1]);
            indexBuffer.put(indices[i + 2]);
            
            theta += Math.PI / 90;
        }

        vertexBuffer.position(0);
        indexBuffer.position(0);
    }

    public void draw(GL10 gl) {
        gl.glFrontFace(GL10.GL_CCW);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glCullFace(GL10.GL_BACK);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

        //gl.glDrawElements(GL10.GL_LINE_LOOP, VERTICES, GL10.GL_UNSIGNED_SHORT, indexBuffer);
        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, VERTICES);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisable(GL10.GL_CULL_FACE);
    }
}