package com.egs.survivalrogue.util;


public class Noise {

	public Noise(){
		
	}
	
	public double noise(int x, int y, long seed){
		x = x + y * 57;
		x = ((x << 13) ^ x);
		double t = (x * (x * x * seed + 789221) + 1376312589) & 0x7fffffff;
		return 1.0 - t * 0.000000000931322574615478515625;
	}
	
	public double sNoise(int x, int y, long seed){
		double corners = (noise(x - 1, y - 1, seed) + noise(x + 1, y - 1, seed) + noise(x - 1, y + 1, seed) + noise(x + 1, y + 1, seed)) * 0.0625;
	    double sides = (noise(x - 1, y, seed)  + noise(x + 1, y, seed) + noise(x, y - 1, seed) + noise(x, y + 1, seed))  * 0.125;
	    double center = noise(x, y, seed) * 0.25;
		return corners + sides + center;		
	}

	public double lInterpoleLin(double a, double b, double x){
		return a * (1 - x) + b * x;		
	}
	
	public double lInterpoleCos(double a, double b, double x){
		double ft = x * 3.1415927;
		double f = (1 - Math.cos(ft)) * 0.5;
		return a * f + b * (1 - f);
	}
	
	public double iNoise(double x, double y, long seed){
		int iX = (int) x;
		int iY = (int) y;
		double dX = x - iX;
		double dY = y - iY;
		double p1 = sNoise(iX, iY, seed);
		double p2 = sNoise(iX + 1, iY, seed);
		double p3 = sNoise(iX, iY + 1, seed);
		double p4 = sNoise(iX + 1, iY + 1, seed);
		double i1 = lInterpoleCos(p1 ,p2 ,dX);
		double i2 = lInterpoleCos(p3, p4, dX);
		return lInterpoleCos(i1, i2, dY);	
	} 	
	
	public double pNoise(double x, double y, long seed, double persistence, int octave, double amplitude){
		double result;
		int frequence = 1;
		result = 0;
		for(int n = 0; n < octave; n++){
			frequence <<= 1;
			amplitude *= persistence;
			result += iNoise(x * frequence, y * frequence, seed) * amplitude;
		}
		return result * (persistence - 1) / (Math.pow(persistence, octave + 1) - 1);	
	}

	public int[][] startNoise(int w, int h, int xa, int ya, long seed, double featureSize, double percistence, int octave, double amplitude){		
		//System.out.println("Generating noise map");
		
		//System.out.println("X: " + x + ", Y: " + y + seed);
		
		//System.out.println("Generating noise: " + w + "x" + h + ", @ x: " + xa + ", y: " + ya);
		
		int result[][] = new int[16][16];
		
		for(int y = 0; y < 16; y++){
			for(int x = 0; x < 16; x++){
				double c = pNoise((double) (x + xa) * featureSize - w, (double) (y + ya) * featureSize - h, seed, percistence, octave, amplitude);
				
				
				c *= 128.0;
				c += 127.0;
				if(c > 255.0){
					c = 255.0;
				}
				if(c < 0.0){
					c = 0.0;
				}

				result[x][y] = (int) c;
			}
		}
		
		return result;
			
	}
		
//	private double normalize(double a, double max){
//		return Math.abs(a / max);
//	}
//	
//	private void calcPercent(){
//		percent = (current * 100) / total;
//	}
//	
//	private void printPercent(){
//		if(oldPercent != percent){
//			System.out.println(percent + "%");
//			oldPercent = percent;
//		}
//		
//	}
	
	}
