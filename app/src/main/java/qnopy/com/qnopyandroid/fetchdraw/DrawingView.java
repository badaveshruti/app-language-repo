package qnopy.com.qnopyandroid.fetchdraw;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.util.SharedPref;


public class DrawingView extends View implements ScaleGestureDetector.OnScaleGestureListener {


    public static final int LINE = 1;
    public static final int RECTANGLE = 3;
    public static final int SQUARE = 4;
    public static final int CIRCLE = 5;
    public static final int TRIANGLE = 6;
    public static final int SMOOTHLINE = 2;
    public static final int DRAW = 7;

    public static final int TAG = 8;

    public static final int OBJECT = 11;

    public static final int UNDO = 9;

    public static final int ZOOM = 10;
    public static final float TOUCH_TOLERANCE = 4;
    public static final float TOUCH_STROKE_WIDTH = 5;
    public static boolean TEXT_DRAW = false;

    protected float mStrokeWidth = 3;

    public int textSize;

    public int mCurrentShape;
    protected Bitmap mBitmap;
    protected Canvas mCanvas;
    protected Path mPath;
    public static Paint mPaint;
    //    public static Paint textPaint;
    protected boolean isDrawing = false;
    protected float mStartX;
    protected float mStartY;
    protected String textValue = "";
    protected Bitmap object = null;
    protected float mx;
    protected float my;
    protected boolean checkText = false;
    protected ArrayList<Stroke> _allStrokes = new ArrayList<Stroke>();
    int color = 0xFFFF0000;
    ImageView bgImageView;

    private enum Mode {
        NONE,
        DRAG,
        ZOOM
    }

    protected Bitmap imageCanvas;

    private static final float MIN_ZOOM = 1.0f;
    private static final float MAX_ZOOM = 4.0f;

    private Mode mode = Mode.NONE;
    private float scale = 1.0f;
    private float lastScaleFactor = 0f;

    // Where the finger first touches the screen
    private float startX = 0f;
    private float startY = 0f;

    // How much to translate the canvas
    private float dx = 0f;
    private float dy = 0f;
    private float prevDx = 0f;
    private float prevDy = 0f;

    //Canvas width and height
    protected int canvasWidth = 0;
    protected int canvasHeight = 0;

//	SharedPreferences pref;

//	FetchDrawScreen fetchDraw = new FetchDrawScreen();

    public DrawingView(Context context) {
        super(context);
        init();
        init(context);
        //initforText();
//		pref = getSharedPrefrences(context);
        SharedPref.globalContext = context;
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        init(context);

        // initforText();
//		pref = getSharedPrefrences(context);
//		fetchDraw = new FetchDrawScreen();

        SharedPref.globalContext = context;
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        init(context);
        //initforText();
//		pref = getSharedPrefrences(context);
//		fetchDraw = new FetchDrawScreen();
        SharedPref.globalContext = context;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        canvasHeight = h;
        canvasWidth = w;

        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        } else {
            Bitmap temporary = Bitmap.createScaledBitmap(mBitmap, w, h, true);
            mBitmap = temporary;
        }
        mCanvas = new Canvas(mBitmap);

    }


    // Initialize the variables

    protected void init() {
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(this.color);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(TOUCH_STROKE_WIDTH);
    }

//    protected void initforText() {
//        textPaint = new Paint();
//        textPaint.setAntiAlias(true);
//        textPaint.setDither(true);
//        textPaint.setColor(this.color);
//        textPaint.setStyle(Paint.Style.FILL);
//        textPaint.setStrokeJoin(Paint.Join.ROUND);
//        textPaint.setStrokeCap(Paint.Cap.ROUND);
//        textPaint.setStrokeWidth(TOUCH_STROKE_WIDTH);
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (_allStrokes != null) {
            for (Stroke stroke : _allStrokes) {
                if (stroke != null) {
                    Path path = stroke.getPath();
                    int color = stroke.getColor();
                    float strokeWidth = stroke.getStroke();
                    float xValue = stroke.getX();
                    float yValue = stroke.getY();
                    String text = stroke.getText();
                    int text_size = stroke.getTextSize();
                    Bitmap bitmap = stroke.get_bitmap();
                    if ((path != null)) {
                        mPaint.setColor(color);
                        mPaint.setStrokeWidth(strokeWidth);
                        mPaint.setStyle(Paint.Style.STROKE);
                        canvas.drawPath(path, mPaint);

                    } else if (!(text.equalsIgnoreCase(""))) {
                        mPaint.setStyle(Paint.Style.FILL);
                        mPaint.setColor(color);
                        drawTextonCanvas(canvas, mPaint, text, xValue, yValue, text_size);
                        mPaint.setStyle(Paint.Style.STROKE);

                    } else if (bitmap != null) {
                        mPaint.setStyle(Paint.Style.STROKE);
                        drawObjectOnCanvas(canvas, bitmap, mPaint, xValue, yValue);
                    }
                }
            }
            TEXT_DRAW = false;
        }

    }


    //Set Paint Color

    public void colorChanged(int color) {
        mPaint.setColor(color);
        //textPaint.setColor(color);
        this.color = mPaint.getColor();
    }

    // Draw Image on View
    @SuppressLint("NewApi")
    public void setBackgroundV16Plus(View view, Bitmap bitmap) {
        view.setBackground(new BitmapDrawable(getResources(), bitmap));

    }

    // Draw Image on View
    @SuppressWarnings("deprecation")
    public void setBackgroundV16Minus(View view, Bitmap bitmap) {
        view.setBackgroundDrawable(new BitmapDrawable(bitmap));
    }

    //------------------------------------------------------------------
    // Undo
    //------------------------------------------------------------------
    public void onUndo() {
        if (_allStrokes.size() > 0) {
            _allStrokes.remove(_allStrokes.size() - 1);
            if (!textValue.equalsIgnoreCase(""))
                textValue = "";
            invalidate();
        }
    }


    //------------------------------------------------------------------
    // Line
    //------------------------------------------------------------------

    private void onDrawLine(Canvas canvas, boolean isDraw) {
        if (isDraw) {
            SharedPref.resetSaved();
            float dx = Math.abs(mx - mStartX);
            float dy = Math.abs(my - mStartY);

            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.lineTo(mx, my);
                _allStrokes.remove(_allStrokes.size() - 1);
                Stroke stroke = new Stroke();
                stroke.StrokePath(mPaint.getColor(), mPath, mPaint.getStrokeWidth());
                _allStrokes.add(stroke);
            }
        }
    }

    private void onTouchEventLine(MotionEvent event) {
        Stroke stroke = new Stroke();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mPath = new Path();
                mPaint.setStrokeWidth(mStrokeWidth);
                mStartX = mx;
                mStartY = my;
                mPath.moveTo(mStartX, mStartY);
                stroke.StrokePath(mPaint.getColor(), mPath, mPaint.getStrokeWidth());
                _allStrokes.add(stroke);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.reset();
                _allStrokes.remove(_allStrokes.size() - 1);
                mPath.moveTo(mStartX, mStartY);
                mPath.lineTo(mx, my);
                stroke.StrokePath(mPaint.getColor(), mPath, mPaint.getStrokeWidth());
                _allStrokes.add(stroke);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                onDrawLine(mCanvas, true);
                invalidate();
                break;
        }
    }


    //------------------------------------------------------------------
    // Free Draw
    //------------------------------------------------------------------

    private void onFreeDraw(Canvas canvas, boolean isDraw) {
        if (isDraw) {
            SharedPref.resetSaved();
            float dx = Math.abs(mx - mStartX);
            float dy = Math.abs(my - mStartY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.lineTo(mx, my);
                if (_allStrokes.size() > 0) {
                    _allStrokes.remove(_allStrokes.size() - 1);
                }
                Stroke stroke = new Stroke();
                stroke.StrokePath(mPaint.getColor(), mPath, mPaint.getStrokeWidth());
                _allStrokes.add(stroke);
            }
        }
    }

    private void onTouchEventFreeDraw(MotionEvent event) {
        Stroke stroke = new Stroke();
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mPath = new Path();
                mPaint.setStrokeWidth(mStrokeWidth);
                mStartX = mx;
                mStartY = my;
                mPath.moveTo(mStartX, mStartY);
                stroke.StrokePath(mPaint.getColor(), mPath, mPaint.getStrokeWidth());
                _allStrokes.add(stroke);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(mx, my);
                _allStrokes.remove(_allStrokes.size() - 1);
                stroke.StrokePath(mPaint.getColor(), mPath, mPaint.getStrokeWidth());
                _allStrokes.add(stroke);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                onFreeDraw(mCanvas, true);
                invalidate();
                break;
        }
    }

    //------------------------------------------------------------------
    // EditText
    //------------------------------------------------------------------

    private void drawText(Canvas canvas, boolean isDraw) {
        if (isDraw && !textValue.equalsIgnoreCase("")) {

            float left = mStartX > mx ? mx : mStartX;
            float top = mStartY > my ? my : mStartY;
            mPaint.setStrokeWidth(0);
            mPaint.setStyle(Paint.Style.FILL);

            mPaint.setTextSize(textSize);
            if (!checkText) {
                if (_allStrokes.size() > 0)
                    _allStrokes.remove(_allStrokes.size() - 1);
            }
            Stroke stroke = new Stroke();
            int offSet = 30;
            stroke.StrokeText(mPaint.getColor(), textValue, left, top + offSet, textSize);
            _allStrokes.add(stroke);
            canvas.drawText(textValue, left, top + offSet, mPaint);
            checkText = false;
            mPaint.setStrokeWidth(TOUCH_STROKE_WIDTH);
        }
    }


    private void onTouchEventEditText(MotionEvent event) {
        mStartX = mx;
        mStartY = my;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                drawText(mCanvas, true);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                drawText(mCanvas, true);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                drawText(mCanvas, true);
                invalidate();
                break;
        }
        ;
    }

    private void drawTextonCanvas(Canvas canvas, Paint paint, String text, float x, float y, int text_size) {
        SharedPref.resetSaved();
        paint.setStrokeWidth(0);
        paint.setTextSize(text_size);
        canvas.drawText(text, x, y, paint);
        paint.setStrokeWidth(TOUCH_STROKE_WIDTH);
    }


    public void drawTextFirst() {

        mPaint.setStrokeWidth(0);
        mPaint.setTextSize(textSize);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(color);
        int w = getWidth();
        int h = getHeight();
        if ((w == 0) || (h == 0)) {
            return;
        }
        float x = (float) w / 2.0f;
        float y = (float) h / 2.0f;
        float width = mPaint.measureText(textValue);
        Stroke stroke = new Stroke();
        stroke.StrokeText(mPaint.getColor(), textValue, x - (width / 2.0f), y, textSize);
        _allStrokes.add(stroke);
        mCanvas.drawText(textValue, x - (width / 2.0f), y, mPaint);
        checkText = false;
        invalidate();
        mPaint.setStrokeWidth(TOUCH_STROKE_WIDTH);

    }

    //------------------------------------------------------------------
    // Object
    //------------------------------------------------------------------


    private void onTouchEventObject(MotionEvent event) {
        mStartX = mx;
        mStartY = my;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                drawObject(mCanvas, true);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                drawObject(mCanvas, true);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                drawObject(mCanvas, true);
                invalidate();
                break;
        }
        ;
    }

    private void drawObject(Canvas canvas, boolean isDraw) {
        if (isDraw && (object != null)) {
            float left = mStartX > mx ? mx : mStartX;
            float top = mStartY > my ? my : mStartY;
            mPaint.setStrokeWidth(0);
            if (_allStrokes.size() > 0)
                _allStrokes.remove(_allStrokes.size() - 1);
            Stroke stroke = new Stroke();
            int offSet = 30;
            int w = object.getWidth();
            int h = object.getHeight();
            stroke.StrokeObject(mPaint.getColor(), object, left, top + offSet, h, w);
            _allStrokes.add(stroke);
            canvas.drawBitmap(object, left, top + offSet, mPaint);
            mPaint.setStrokeWidth(TOUCH_STROKE_WIDTH);
        }
    }

    public void drawObjectOnCanvas(Canvas canvas, Bitmap bitmap, Paint paint, float x, float y) {
        SharedPref.resetSaved();
        canvas.drawBitmap(bitmap, x, y, paint);
    }

    public void drawObjectFirst() {
        mPaint.setStrokeWidth(0);
        mPaint.setColor(color);
        int w = getWidth();
        int h = getHeight();
        if ((w == 0) || (h == 0)) {
            return;
        }
        float x = (float) w / 2.0f;
        float y = (float) h / 2.0f;
        float width = getWidth() * 1f;
        Stroke stroke = new Stroke();
        stroke.StrokeObject(mPaint.getColor(), object, x - (width / 2.0f), y, h, w);
        _allStrokes.add(stroke);

        mCanvas.drawBitmap(object, x - (width / 2.0f), y, mPaint);
        invalidate();
        mPaint.setStrokeWidth(TOUCH_STROKE_WIDTH);
    }


    //------------------------------------------------------------------
    // Square
    //------------------------------------------------------------------

    private void onTouchEventSquare(MotionEvent event) {
        Stroke stroke;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mStartX = mx;
                mStartY = my;
                mPath = new Path();
                adjustSquare(mx, my);
                drawRectangle(mCanvas, true);
                stroke = new Stroke();
                stroke.StrokePath(mPaint.getColor(), mPath, mPaint.getStrokeWidth());
                _allStrokes.add(stroke);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (_allStrokes.size()>0){
                    _allStrokes.remove(_allStrokes.size() - 1);
                }

                mPath = new Path();
                adjustSquare(mx, my);
                drawRectangle(mCanvas, true);
                stroke = new Stroke();
                stroke.StrokePath(mPaint.getColor(), mPath, mPaint.getStrokeWidth());
                _allStrokes.add(stroke);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                if (_allStrokes.size()>0){
                    _allStrokes.remove(_allStrokes.size() - 1);
                }
                mPath = new Path();
                adjustSquare(mx, my);
                drawRectangle(mCanvas, true);
                stroke = new Stroke();
                stroke.StrokePath(mPaint.getColor(), mPath, mPaint.getStrokeWidth());
                _allStrokes.add(stroke);
                invalidate();
                break;
        }
    }

    /**
     * Adjusts current coordinates to build a square
     *
     * @param x
     * @param y
     */
    protected void adjustSquare(float x, float y) {
        float deltaX = Math.abs(mStartX - x);
        float deltaY = Math.abs(mStartY - y);

        float max = Math.max(deltaX, deltaY);

        mx = mStartX - x < 0 ? mStartX + max : mStartX - max;
        my = mStartY - y < 0 ? mStartY + max : mStartY - max;
    }

    private void drawRectangle(Canvas canvas, boolean isDraw) {
        if (isDraw) {
            SharedPref.resetSaved();
            float right = mStartX > mx ? mStartX : mx;
            float left = mStartX > mx ? mx : mStartX;
            float bottom = mStartY > my ? mStartY : my;
            float top = mStartY > my ? my : mStartY;
            mPaint.setStrokeWidth(mStrokeWidth);
            mPaint.setColor(this.color);
            mPath.addRect(left, top, right, bottom, Direction.CW);
        }
    }

    //------------------------------------------------------------------
    // Circle
    //------------------------------------------------------------------

    private void onDrawCircle(Canvas canvas, boolean isDraw) {
        if (isDraw) {
            SharedPref.resetSaved();
            mPaint.setStrokeWidth(mStrokeWidth);
            mPaint.setColor(this.color);
            mPath.addCircle(mStartX, mStartY, calculateRadius(mStartX, mStartY, mx, my), Direction.CCW);
        }
    }

    private void onTouchEventCircle(MotionEvent event) {
        Stroke stroke;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mStartX = mx;
                mStartY = my;
                mPath = new Path();
                onDrawCircle(mCanvas, true);
                stroke = new Stroke();
                stroke.StrokePath(mPaint.getColor(), mPath, mPaint.getStrokeWidth());
                _allStrokes.add(stroke);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (_allStrokes.size()>0){
                    _allStrokes.remove(_allStrokes.size() - 1);
                }
                mPath = new Path();
                onDrawCircle(mCanvas, true);
                stroke = new Stroke();
                stroke.StrokePath(mPaint.getColor(), mPath, mPaint.getStrokeWidth());
                _allStrokes.add(stroke);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:

                try {
                    isDrawing = false;
                    if (_allStrokes.size()>0){
                        _allStrokes.remove(_allStrokes.size() - 1);
                    }
                    mPath = new Path();
                    onDrawCircle(mCanvas, true);
                    stroke = new Stroke();
                    stroke.StrokePath(mPaint.getColor(), mPath, mPaint.getStrokeWidth());
                    _allStrokes.add(stroke);
                    invalidate();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    /**
     * @return
     */
    protected float calculateRadius(float x1, float y1, float x2, float y2) {

        return (float) Math.sqrt(
                Math.pow(x1 - x2, 2) +
                        Math.pow(y1 - y2, 2)
        );
    }


    //----------------

    // All Touch Events


    private void init(Context context) {
        final ScaleGestureDetector scaleDetector = new ScaleGestureDetector(context, this);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (mCurrentShape) {
                    case ZOOM:
                        if (view != null) {
                            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                                case MotionEvent.ACTION_DOWN:
                                    if (scale > MIN_ZOOM) {
                                        mode = Mode.DRAG;
                                        startX = motionEvent.getX() - prevDx;
                                        startY = motionEvent.getY() - prevDy;
                                    }
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    if (mode == Mode.DRAG) {
                                        dx = motionEvent.getX() - startX;
                                        dy = motionEvent.getY() - startY;
                                    }
                                    break;
                                case MotionEvent.ACTION_POINTER_DOWN:
                                    mode = Mode.ZOOM;
                                    break;
                                case MotionEvent.ACTION_POINTER_UP:
                                    mode = Mode.DRAG;
                                    break;
                                case MotionEvent.ACTION_UP:
                                    mode = Mode.NONE;
                                    prevDx = dx;
                                    prevDy = dy;
                                    break;
                            }
                            scaleDetector.onTouchEvent(motionEvent);

                            if ((mode == Mode.DRAG && scale >= MIN_ZOOM) || mode == Mode.ZOOM) {
                                getParent().requestDisallowInterceptTouchEvent(true);
                                View sampleView = child();
                                if (sampleView != null) {
                                    float maxDx = (sampleView.getWidth() - (sampleView.getWidth() / scale)) / 2 * scale;
                                    float maxDy = (sampleView.getHeight() - (sampleView.getHeight() / scale)) / 2 * scale;
                                    dx = Math.min(Math.max(dx, -maxDx), maxDx);
                                    dy = Math.min(Math.max(dy, -maxDy), maxDy);
                                    Log.i(getClass().getSimpleName(), "Width: " + sampleView.getWidth() + ", scale " + scale + ", dx " + dx
                                            + ", max " + maxDx);
                                    applyScaleAndTranslation();
                                    applyBGScalingAndTranslation();
                                }
                            }
                        }
                        break;
                    default:
                        mx = motionEvent.getX();
                        my = motionEvent.getY();
                        if (mCurrentShape != ZOOM) {
                            switch (mCurrentShape) {
                                case LINE:
                                    onTouchEventLine(motionEvent);
                                    break;
                                case SQUARE:
                                    onTouchEventSquare(motionEvent);
                                    break;
                                case CIRCLE:
                                    onTouchEventCircle(motionEvent);
                                    break;
                                case DRAW:
                                    onTouchEventFreeDraw(motionEvent);
                                    break;
                                case TAG:
                                    onTouchEventEditText(motionEvent);
                                    break;
                                case OBJECT:
                                    onTouchEventObject(motionEvent);
                                    break;
                            }
                        }
                        break;
                }


                return true;
            }
        });
    }

    // ScaleGestureDetector

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleDetector) {
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector scaleDetector) {
        float scaleFactor = scaleDetector.getScaleFactor();
        if (lastScaleFactor == 0 || (Math.signum(scaleFactor) == Math.signum(lastScaleFactor))) {
            scale *= scaleFactor;
            scale = Math.max(MIN_ZOOM, Math.min(scale, MAX_ZOOM));
            lastScaleFactor = scaleFactor;
        } else {
            lastScaleFactor = 0;
        }
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleDetector) {
    }

    private void applyScaleAndTranslation() {
        child().setScaleX(scale);
        child().setScaleY(scale);
        child().setTranslationX(dx);
        child().setTranslationY(dy);
    }

    /**
     * Get the view object
     *
     * @return View
     */
    private View child() {
        return this;
    }

    /**
     * Apply BG Image Scaling and Translation
     */
    private void applyBGScalingAndTranslation() {
        bgImageView.setScaleX(scale);
        bgImageView.setScaleY(scale);
        bgImageView.setTranslationX(dx);
        bgImageView.setTranslationY(dy);
    }

    /**
     * Reset BG Image  and View Scaling and Translation
     */
    public void resetBGScalingAndTranslation() {
        scale = 1.0f;
        dx = dy = 0;
        applyBGScalingAndTranslation();
        applyScaleAndTranslation();

    }

    /**
     * Set ImageView for Zooming
     *
     * @param imageView
     */
    public void setImage(ImageView imageView) {
        bgImageView = imageView;
    }
}