public interface Generator
{
	public int getMaxValue();
	public int[] generate(int len);
	public int[] generate(int[] res, int len);
	public int[] generate(int[] res, int from, int to);
}
