package com.example.meetapp;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RectShape;

class SlotBackgroundSetter {


        static int getColorPercentage(int colorStart, int colorEnd, int percent){
            return Color.rgb(
                    ColorPercentageCalculation(Color.red(colorStart), Color.red(colorEnd), percent),
                    ColorPercentageCalculation(Color.green(colorStart), Color.green(colorEnd), percent),
                    ColorPercentageCalculation(Color.blue(colorStart), Color.blue(colorEnd), percent)
            );
        }

        private static int ColorPercentageCalculation(int colorStart, int colorEnd, int percent){
            return ((Math.max(colorStart, colorEnd)*(100-percent)) + (Math.min(colorStart, colorEnd)*percent)) / 100;
        }


        static Drawable setBackGroundColorAndBorder(int color) {
            StateListDrawable states = new StateListDrawable();
            states.addState(new int[]{
                    android.R.attr.state_focused, -android.R.attr.state_pressed,
            }, getBackGroundAndBorder(color));
            states.addState(new int[]{
                    android.R.attr.state_focused, android.R.attr.state_pressed,
            }, getBackGroundAndBorder(color));
            states.addState(new int[]{
                    -android.R.attr.state_focused, android.R.attr.state_pressed,
            }, getBackGroundAndBorder(color));
            states.addState(new int[]{
                    android.R.attr.state_enabled
            }, getBackGroundAndBorder(color));

            return states;
        }

        private static Drawable getBackGroundAndBorder(int color) {
            Drawable[] drawablesForBackGroundAndBorder = new Drawable[2];
            drawablesForBackGroundAndBorder[0] = getBorder();
            drawablesForBackGroundAndBorder[1] = getBackGround(color);
            LayerDrawable layerDrawable = new LayerDrawable(drawablesForBackGroundAndBorder);
            layerDrawable.setLayerInset(1, 2, 2, 2, 2);
            return layerDrawable.mutate();
        }

        private static Drawable getBorder() {
            RectShape rectShape = new RectShape();
            ShapeDrawable shapeDrawable = new ShapeDrawable(rectShape);
            shapeDrawable.getPaint().setStyle(Paint.Style.STROKE);
            shapeDrawable.getPaint().setStrokeWidth(10f);
            shapeDrawable.getPaint().setAntiAlias(true);
            shapeDrawable.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
            return shapeDrawable.mutate();
        }

        private static Drawable getBackGround(int color) {
            RectShape rectShape = new RectShape();
            ShapeDrawable shapeDrawable = new ShapeDrawable(rectShape);
            shapeDrawable.getPaint().setColor(color);
            shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
            shapeDrawable.getPaint().setAntiAlias(true);
            shapeDrawable.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
            return shapeDrawable.mutate();
        }
}
