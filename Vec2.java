import java.lang.Math;

public class Vec2
{
	private double x, y, magnitude;
	
	public Vec2()
	{
		this.x = 0.0;
		this.y = 0.0;
	}
	public Vec2(double theX, double theY)
	{
		this.x = theX;
		this.y = theY;
	}
	public double[] getValues()
	{
		double[] values = {x, y};
		return values;
	}
	public double getX()
	{
		return this.x;
	}
	public double getY()
	{
		return this.y;
	}
	public double magnitude()
	{
		magnitude = Math.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0));
		
		return magnitude;
	}
	public Vec2 normalize()
	{
		double newX, newY, magnitude;
		magnitude = this.magnitude();
		
		if(magnitude != 0.0)
		{
			newX = (this.x / magnitude);
			newY = (this.y / magnitude);
		}
		else
		{
			newX = this.x;
			newY = this.y;
		}
		
		Vec2 newVec2 = new Vec2(newX, newY);
		
		return newVec2;
	}
	public void set(Vec2 otherVec2)
	{
		this.x = otherVec2.getX();
		this.y = otherVec2.getY();
	}
	public void set(double theX, double theY)
	{
		this.x = theX;
		this.y = theY;
	}
	public Vec2 add(Vec2 otherVec2)
	{
		double[] newValues = new double[2];
		
		for(int i = 0; i < 2; i++)
		{
			newValues[i] = (this.getValues()[i] + otherVec2.getValues()[i]);
		}
		
		Vec2 newVec2 = new Vec2(newValues[0], newValues[1]);
		
		return newVec2;
	}
	public Vec2 add(double theX, double theY)
	{
		Vec2 newVec2 = new Vec2(this.x + theX, this.y + theY);
		
		return newVec2;
	}
	public Vec2 subtract(Vec2 otherVec2)
	{
		double[] newValues = new double[2];
		
		for(int i = 0; i < 2; i++)
		{
			newValues[i] = (this.getValues()[i] - otherVec2.getValues()[i]);
		}
		
		Vec2 newVec2 = new Vec2(newValues[0], newValues[1]);
		
		return newVec2;
	}
	public Vec2 subtract(double theX, double theY)
	{
		Vec2 newVec2 = new Vec2(this.x - theX, this.y - theY);
		
		return newVec2;
	}
	public Vec2 multiply(double scalar)
	{
		double newX, newY;
		
		newX = (this.x * scalar);
		newY = (this.y * scalar);
		
		Vec2 newVec2 = new Vec2(newX, newY);
		
		return newVec2;
	}
	public double dot(Vec2 otherVec2)
	{
		double[] newValues = new double[3];
		double dotProduct = 0.0;
		
		for(int i = 0; i < 2; i++)
		{
			newValues[i] = (this.getValues()[i] * otherVec2.getValues()[i]);
		}
		
		for(int i = 0; i < 2; i++)
		{
			dotProduct += newValues[i];
		}
		
		return dotProduct;
	}
}