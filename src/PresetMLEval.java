import org.apache.log4j.Logger;

public class PresetMLEval
	implements Evaluator
{
	private final static Logger L = Logger.getLogger(PresetMLEval.class);
	private double mixin;
	private int maxValue;

	public PresetMLEval(double mixin)
	{
		this.mixin = mixin;
	}

	@Override
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	@Override
	public double eval(int[] seq, int start, int end)
	{
		int[] outer = new int[maxValue];
		int[] inner = new int[maxValue];
		for(int i = 0; i < seq.length; i++) {
			int h = seq[i];
			outer[h]++;
			if ( i >= start && i < end ) inner[h]++;
		}
		double[] p = new double[maxValue];
		double f1 = 1.0/seq.length;
		double f2 = 1.0/(mixin + end-start);
		if(L.isTraceEnabled())L.trace(String.format("*%5d %5d  %7.2e %7.2e\n", seq.length, end-start, f1, f2));
		for(int j = 0; j < maxValue; j++) {
			double pa = f1 * outer[j];
			p[j] = f2 * (mixin * pa + inner[j]);
		}
		// XXX could be one loop
		double res = 0.0;
		for(int j = 0; j < maxValue; j++) {
			if(L.isTraceEnabled())L.trace(String.format(" %2d %5d %5d %7.4f\n", j, outer[j], inner[j], p[j]));
			if ( inner[j] > 0 ) res -= inner[j] * Math.log(p[j]);
		}
		return res;
	}

	public String toString()
	{
		return String.format("PresetMLEval[%5.3f]", mixin);
	}
}
