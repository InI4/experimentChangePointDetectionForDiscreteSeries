import java.util.Random;
import java.util.Locale;
import java.security.SecureRandom;

import org.apache.log4j.Logger;

public class CPD
{
	private final static Logger L = Logger.getLogger(CPD.class);
	private final static Logger LDATA = Logger.getLogger("data");

	private final Random rand;
	private int sequenceLen;
	private Generator g1, g2;
	private int maxValue;

	public CPD(int sequenceLen, Generator g1, Generator g2)
	{
		if ( sequenceLen < 3 ) throw new IllegalArgumentException("sequenceLen < 3");
		LDATA.info("# sequenceLen="+sequenceLen+"  "+g1+"  "+g2);
		this.sequenceLen = sequenceLen;
		this.g1 = g1;
		this.g2 = g2;
		maxValue = Math.max(g1.getMaxValue(), g2.getMaxValue());
		rand = new SecureRandom();
		rand.setSeed(1L);
	}

	public void setSeed(long l)
	{
		rand.setSeed(l);
	}

	public void simulate(int trials, Evaluator... es)
	{
		LDATA.info("# trials="+trials);
		for(int i = 0; i < es.length; i++) LDATA.info("# Evaluator."+i+" "+es[i]);
		int[] seq = new int[sequenceLen];
		int[] minPos = new int[es.length];
		double[] minVal = new double[es.length];
		double[] norm = new double[es.length];
		int[] errAbs = new int[es.length];
		long[] errSq = new long[es.length];
		for(Evaluator e : es) e.setMaxValue(maxValue);
		for(int trial = 0; trial < trials; trial++) {
			int trueSplit = 1 + rand.nextInt(sequenceLen-1);
			g1.generate(seq, 0, trueSplit);
			g2.generate(seq, trueSplit, sequenceLen);
			for(int k = 0; k < es.length; k++) {
				Evaluator e = es[k];
				minPos[k] = -1;
				minVal[k] = Double.MAX_VALUE;
				norm[k] = e.eval(seq, 0, sequenceLen);
				L.debug(String.format("%s : %7.4f", e, norm[k]));
			}
			for(int split = 1; split < sequenceLen-1; split++) {
				L.debug("split="+split+" / "+trueSplit);
				for(int k = 0; k < es.length; k++) {
					Evaluator e = es[k];
					double ev1 = e.eval(seq, 0, split);
					double ev2 = e.eval(seq, split, sequenceLen);
					double ev = ev1 + ev2;
					LDATA.info(String.format(Locale.ENGLISH, "%4d:%-4d %4d %4d %7.4f %7.4f %7.4f %s", trial, k, trueSplit, split, ev1/norm[k], ev2/norm[k], ev/norm[k], e));
					if ( ev < minVal[k] ) {
						minVal[k] = ev;
						minPos[k] = split;
					}
				}
			}
			L.debug(String.format("split=%3d ", trueSplit));
			int minAllPos = Integer.MAX_VALUE;
			int maxAllPos = -Integer.MAX_VALUE;
			for(int k = 0; k < es.length; k++) {
				int delta = minPos[k] - trueSplit;
				maxAllPos = Math.max(maxAllPos, minPos[k]);
				minAllPos = Math.min(minAllPos, minPos[k]);
				errAbs[k] += Math.abs(delta);
				errSq[k] += delta * delta;
				L.debug(String.format(" %+5d %6.2f %6.2f", delta, (double)errAbs[k]/(trial+1), Math.sqrt((double)errSq[k]/(trial+1)) ));
			}
			L.info("trial="+trial+",  deltaPos="+(maxAllPos-minAllPos));
		}
		for(int k = 0; k < es.length; k++) {
			String out = String.format("%4d %9.3f %8.3f %s", k, (double)errAbs[k]/trials, Math.sqrt((double)errSq[k]/(trials)), es[k]);
			L.info(out);
			LDATA.info("## "+out);
		}
	}

	public static void main(String[] args)
	{
		Generator g1, g2;
		g1 = SimpleGenerators.weighted(0.16,0.16,0.17,0.17,0.17,0.17);
		g2 = SimpleGenerators.weighted(0.17,0.16,0.16,0.17,0.17,0.17);
		g1 = SimpleGenerators.weighted(1.0,1.0,1.0,1.0,1.0,1.0);
		g2 = SimpleGenerators.weighted(1.0,1.0,1.0,1.0,2.0,0.0);
		CPD cpd = new CPD(500, g1, g2);
		cpd.simulate(5000
				,new PresetMLEval(0.0)
				// ,new AverageMLEval(0.1)
				// ,new AverageMLEval(0.2)
				// ,new AverageMLEval(0.3)
				,new AverageMLEval(0.2)
				,new AverageMLEval(0.3)
				,new AverageMLEval(0.4)
				,new AverageMLEval(0.5)
				// ,new AverageMLEval(0.6)
				// ,new AverageMLEval(0.7)
				,new PresetMLEval(0.25)
				// ,new PresetMLEval(0.5)
				// ,new PresetMLEval(1.0)
				// ,new PresetMLEval(1.5)
				// ,new PresetMLEval(2.0)
				// ,new PresetMLEval(2.5)
				// ,new PresetMLEval(3.0)
				// ,new PresetMLEval(3.5)
				// ,new PresetMLEval(4.0)
				// ,new PresetMLEval(4.5)
				// ,new PresetMLEval(5.0)
				// ,new PresetMLEval(5.5)
				// ,new WeightingEval(0.1)
				// ,new WeightingEval(0.2)
				,new PredictingMLEval(0.025)
				,new PredictingMLEval(0.05)
				,new PredictingMLEval(0.1)
				// ,new PredictingMLEval(0.2)
				// ,new PredictingMLEval(0.3)
				// ,new PredictingMLEval(0.5)
				// ,new PredictingMLEval(1.0)
			);
	}
}
