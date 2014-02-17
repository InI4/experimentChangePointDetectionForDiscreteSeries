import java.util.Random;
import java.util.Arrays;
import java.security.SecureRandom;

public class SimpleGenerators
{
	private static long weightedSeed = 1;

	public static Generator weighted(final double... q) {
		final double[] p = new double[q.length];
		double sum = 0.0;
		for(int i = 0; i < q.length; i++) {
			if ( q[i] < 0.0 ) throw new IllegalArgumentException("Negative Input, not allowed.");
			sum += q[i];
		}
		p[0] = q[0] / sum;
		for(int i = 1; i < q.length; i++) p[i] = p[i-1] + q[i]/sum;
		p[q.length-1] = 1.0; // to avoid rounding issues
		final Random rand = new SecureRandom();
		rand.setSeed(weightedSeed);
		weightedSeed = 5 * weightedSeed + 1;
		return new Generator() {
			public int getMaxValue() { return p.length; }
			@Override
			public int[] generate(int len)
			{
				int[] res = new int[len];
				return generate(res, len);
			}
			@Override
			public int[] generate(int[] res, int len)
			{
				if ( len > res.length ) throw new IllegalArgumentException("len > length");
				return generate(res, 0, len);
			}
			@Override
			public int[] generate(int[] res, int from, int to) 
			{
				if ( to > res.length ) throw new IllegalArgumentException("to > length");
				for(int i = from; i < to; i++) {
					double d = rand.nextDouble();
					int j = 0; while ( d > p[j] ) j++;
					res[i] = j;
				}
				return res;
			}
			@Override 
			public String toString() {
				return "weighted"+Arrays.toString(q);
			}
		};
	}
}
