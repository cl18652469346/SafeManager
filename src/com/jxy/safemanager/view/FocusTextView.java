package com.jxy.safemanager.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class FocusTextView extends TextView {

	/**
	 * Java代码中构建控件的方法
	 * @param context
	 */
	public FocusTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 系统调用(带属性+上下文的构造)
	 * @param context
	 * @param attrs
	 */
	public FocusTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 系统调用(带属性+上下文+自定义样式的构造)
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public FocusTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	// 能够获取焦点的自定义TextView - 重写获焦点的方法[系统调用，默认就能获取焦点]
	@Override
	public boolean isFocused() {
		// TODO Auto-generated method stub
		return true;
	}
}
