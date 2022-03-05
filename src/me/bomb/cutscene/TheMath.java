package me.bomb.cutscene;

class TheMath {
	private static final float[] base = new float[65536];

	protected static float sin(float sin) {
		return TheMath.base[(int) (sin * 10430.378F) & '\uffff'];
	}

	protected static float cos(float cos) {
		return TheMath.base[(int) (cos * 10430.378F + 16384.0F) & '\uffff'];
	}

	static {
		int i;
		for (i = 0; i < 65536; ++i) {
			TheMath.base[i] = (float) Math.sin((double) i * 3.141592653589793D * 2.0D / 65536.0D);
		}
	}
}
