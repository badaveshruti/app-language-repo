package qnopy.com.qnopyandroid.fetchdraw;

import android.graphics.Bitmap;
import android.graphics.Path;

import java.io.Serializable;

public class  Stroke implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Path _path;
	private String _text="";
	private Bitmap _bitmap = null;
	private float _x;
	private float _y;
	private int _textSize;
	int _color;
	float _strokeWidth;
	private int _h;
	private int _w;

	public synchronized void StrokePath (int color, Path path, float StrokeWidth) {
		_color = color;
		_path = path;
		_strokeWidth = StrokeWidth;
	}
	public synchronized void StrokeText (int color, String text, float x, float y, int textSize) {
		_color = color;
		_text = text;
		_x = x;
		_y = y;
		_textSize = textSize;
	}
	
	public synchronized void StrokeObject (int color, Bitmap bitmap, float x, float y, int h, int w) {
		_color = color;
		set_bitmap(bitmap);
		_x = x;
		_y = y;
		set_h(h);
		set_w(w);
	}

	public void setTextSize(int textSize) {
		_textSize = textSize;
	}

	public int getTextSize() {
		return _textSize;
	}

	public void setStroke(float strokeWidht) {
		_strokeWidth = strokeWidht;
	}

	public float getStroke() {
		return _strokeWidth;
	}

	public void setPath(Path path) {
		_path = path;
	}
	public Path getPath() {
		return _path;
	}

	public void setColor(int color) {
		_color = color;
	}

	public int getColor() {
		return _color;
	}

	public void setX(float xValue) {
		_x = xValue;
	}

	public float getX() {
		return _x;
	}

	public void setY(float yValue) {
		_y = yValue;
	}

	public float getY() {
		return _y;
	}
	
	public void setText(String text) {
		_text = text;
	}
	public String getText() {
		return _text;
	}
	Bitmap get_bitmap() {
		return _bitmap;
	}
	public void set_bitmap(Bitmap _bitmap) {
		this._bitmap = _bitmap;
	}
	int get_h() {
		return _h;
	}
	void set_h(int _h) {
		this._h = _h;
	}
	int get_w() {
		return _w;
	}
	void set_w(int _w) {
		this._w = _w;
	}
}