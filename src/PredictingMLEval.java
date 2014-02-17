import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * The idea to incorporate future results into ML did not yet workout.
 */
public class PredictingMLEval
	implements Evaluator
{
	private final static Logger L = Logger.getLogger(PredictingMLEval.class);
	private double mixin;
	private int maxValue;

	public PredictingMLEval(double mixin)
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
		int total = 0;
		for(int i = start; i < end; i++) {
			int h = seq[i];
			iCount[h]++;
			total++;
		}
		double n1 = total + mixin;
		double res = 0.0;
		for(int l = 0; l < maxValue; l++) {
			// double weight = Math.pow((0.5 + iCount[l])/n1, mixin);
			double weight = (mixin/3.0 + iCount[l])/n1;
			for(int i = 0; i < maxValue; i++) {
				double c = iCount[i];
				if ( i == l ) c += mixin;
				if ( c == 0 ) continue;
				res -= weight * c * Math.log(c/n1);
			}
		}
		return res;
	}

	public String toString()
	{
		return String.format("PredictingMLEval[%5.3f]", mixin);
	}
}
