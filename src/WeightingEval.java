import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * No clue about this one yet. I had reweightings which seemed successfull empirically, however I had not reason why. The current versions poor AFAIK.
 */
public class WeightingEval
	implements Evaluator
{
	private final static Logger L = Logger.getLogger(WeightingEval.class);
	private double mixin;
	private int maxValue;

	public WeightingEval(double mixin)
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
		int[] iCount = new int[maxValue];
		for(int i = start; i < end; i++) {
			int h = seq[i];
			iCount[h]++;
		}
		double sum = 0.0;
		Arrays.sort(iCount);
		int mid = maxValue/2;
		double median = maxValue % 2 == 0 ? 0.5*(iCount[mid]+iCount[mid-1]) : iCount[mid];
		double[] count = new double[maxValue];
		for(int j = 0; j < maxValue; j++) {
			double h = iCount[j];
			// if ( j == 0 ) h += mixin;
			// if ( h < median ) h += mixin;
			// if ( j == 0 || h < median ) h += mixin;
			// else if ( h > median || j == maxValue - 1) h -= mixin;
			// h += mixin * (j+1);
			if ( j+1 == maxValue ) h -= mixin;
			count[j] = h;
			sum += h;
		}
		double f2 = 1.0/sum;
		double res = 0.0;
		for(int j = 0; j < maxValue; j++) {
			double pa = f2 * count[j];
			if ( pa == 0.0 ) continue;
			if(L.isTraceEnabled())L.trace(String.format(" %2d %5d %7.4f\n", j, count[j], pa));
			res -= count[j] * Math.log(pa);
		}
		return res;
	}

	public String toString()
	{
		return String.format("WeightingEval[%5.3f]", mixin);
	}
}
