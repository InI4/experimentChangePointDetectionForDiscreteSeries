import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * The idea to incorporate future results into ML did not yet workout.
 */
public class AverageMLEval
	implements Evaluator
{
	private final static Logger L = Logger.getLogger(AverageMLEval.class);
	private double mixin;
	private int maxValue;

	public AverageMLEval(double mixin)
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
		double[] iCount = new double[maxValue];
		double total = 0;
		for(int d = -1; d <= 1; d++) {
			int s1 = Math.max(0,start + d), e1 = Math.min(seq.length,end + d);
			double weight = d == 0 ? 1.0 : mixin;
			for(int i = s1; i < e1; i++) {
				int h = seq[i];
				iCount[h] += weight;
				total += weight;
			}
		}
		double res = 0.0;
		for(int i = 0; i < maxValue; i++) {
			double c = iCount[i];
			if ( c == 0 ) continue;
			res -= c * Math.log(c/total);
		}
// if ( Double.isInfinite(res) || Double.isNaN(res) || res == 0.0) { System.err.println("total="+total+",  iCount="+Arrays.toString(iCount)+", res="+res+"  "+start+":"+end);		}
		return res;
	}

	public String toString()
	{
		return String.format("AverageMLEval[%5.3f]", mixin);
	}
}
