package com.alanc.springboot.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import javax.imageio.ImageIO;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * 问我V415版图片处理方式：
 * 1》 把图片（最多5张）合成为一张大图片，模板暂定为5行1列。
 * 2》 把这张大图片存到mongodb里。
 * 3》 考虑适当的压缩。
 * 
 * @author Diego
 *
 */

public class ImageUtils {

	public static byte[][] scaleImage(byte[] imageInput, int[] targetWidthList, String formatName) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(imageInput);
		BufferedImage bi = ImageIO.read(bais);
		int oriWidth = bi.getWidth();
		byte[][] targetImage = new byte[targetWidthList.length][]; 
		for(int i=0; i<targetWidthList.length; i++) {
			int maxWidth = targetWidthList[i];
			if(oriWidth<=maxWidth) {
				targetImage[i] = imageInput;
				continue;
			} 
			float scale = (float)maxWidth/(float)oriWidth;
			int newHeight = (int)(bi.getHeight()*scale);
			Image itemp = bi.getScaledInstance(maxWidth, newHeight, BufferedImage.SCALE_SMOOTH); 
			BufferedImage newImage = new BufferedImage(itemp.getWidth(null), itemp.getHeight(null), BufferedImage.TYPE_INT_RGB);	
			Graphics g = newImage.createGraphics();

		    // Paint the image onto the buffered image
		    try {
		    	g.drawImage(itemp, 0, 0, null);
		    } finally{
		    	g.dispose();
		    }
		    
		    ByteArrayOutputStream baos = new ByteArrayOutputStream((int)(imageInput.length*scale*scale));
		    
		    JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(baos);   
            encoder.encode(newImage); 
			ImageIO.write((BufferedImage)newImage, formatName, baos);
			targetImage[i] = baos.toByteArray();
		}
		return targetImage;
	}
	
    
    public static final MediaTracker tracker = new MediaTracker(
    		new Component() {
        private static final long serialVersionUID = 1234162663955668507L;
        } 
    );
    
    public static void resize(List<byte[]> dlist,OutputStream os,int fix_width, int fix_height,int padding_height, String format)throws IOException{
    	
    	int back_height =0;		//背景图的高度
    	BufferedImage[] images = new BufferedImage[dlist.size()];
    	ScaleInfo[] scaleInfos = new ScaleInfo[dlist.size()];
    	for(int i=0,s= dlist.size(); i < s ; i++){
    		ByteArrayInputStream bis = new ByteArrayInputStream(dlist.get(i));			
    		images[i] = ImageIO.read(bis);
    		//images[i] =  Toolkit.getDefaultToolkit().createImage( dlist.get(i) );
    		scaleInfos[i] = getScaleInfo(images[i],fix_width,fix_height);
    		back_height += scaleInfos[i].getH() + padding_height;
    	}

    	//创建背景图
    	BufferedImage destImage = new BufferedImage(fix_width, back_height, BufferedImage.TYPE_INT_RGB);  
    	Graphics2D g2d = destImage.createGraphics();
    	g2d.setBackground(Color.lightGray);
    	g2d.fillRect(0, 0, fix_width, back_height);

    	int x=0,y=0;
    	//循环打印图片
    	for(int i=0,s=images.length; i<s; i++){
    		Image image = images[i];
    		ScaleInfo scaleInfo = scaleInfos[i];
    		drawImageToBackground(g2d, image, scaleInfo.getW(),scaleInfo.getH(), x, y);
    		y += scaleInfo.getH() + padding_height;
    	}
    	
    	g2d.dispose();
    	ImageIO.write(destImage, format, os); 
    }
    
    /**
     * 封装到一个方法里，以便作出各种渲染效果。
     * @param backImg
     * @param image
     * @param scaleInfo
     * @param x
     * @param y
     */
    public static void drawImageToBackground(Graphics2D backImg,Image image,int width,int height,int x, int y){

		backImg.drawImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH)
				,x,y,width,height,null);
    }
    
    private static int getWidth(Image img){
    	int imageWidth = img.getWidth( null );
        if ( imageWidth < 1 ) {
           throw new IllegalArgumentException( "image width " + imageWidth + " is out of range" );
        }
        return imageWidth;
    }
    private static int getHeight(Image img){
    	int imageHeight = img.getHeight( null );
        if ( imageHeight < 1 ) {
           throw new IllegalArgumentException( "image height " + imageHeight + " is out of range" );
        }
        return imageHeight;
    }
    
    public static class ScaleInfo{
    	int w;
    	int h;
    	public ScaleInfo(int w,int h){
    		this.w = w;
    		this.h = h;
    	}
		public int getW() {
			return w;
		}
		public void setW(int w) {
			this.w = w;
		}
		public int getH() {
			return h;
		}
		public void setH(int h) {
			this.h = h;
		}
    }
        
    public static ScaleInfo getScaleInfo(Image inputImage,int fix_width,int fix_height) throws IOException {
    
//        waitForImage( inputImage );
        
        int input_width = getWidth(inputImage);
        int input_height = getHeight(inputImage);
        
        //放大或缩小比例
        double factor_w,factor_h = 1.0;
        int new_w , new_h = 0;        
        
        if(input_width >= fix_width){
        	//宽超过指定宽，缩小图片情况
        	new_w = fix_width;
    		factor_w =  (double) fix_width / (double) input_width;
        	if(input_height < fix_height ){
        		//此时高度应该缩小
        		new_h = (int)Math.round(input_height * factor_w);
        	}
        	else{
        		//此时高度应该缩小，如果缩小之后还是超过fix_height， 就必须重新计算new_w.所以可判断宽或高
        		//哪个参数缩小的比例更大。
                factor_h = (double) fix_height / (double) input_height;
        		if(factor_w > factor_h){
            		new_h = (int)Math.round(input_height * factor_w);        			
        		}else{
        			new_h = fix_height;
        			new_w = (int)Math.round(input_width * factor_h);
        			if(new_w > fix_width){
        				new_w = fix_width;
        				new_h = (int)Math.round(input_height * factor_w);
        			}
        		}
        	}        	
        }else{
        	//宽小于指定宽，要看height的情况决定是放大还是缩小图片
        	if(input_height < fix_height ){
        		//放大图片
        		factor_w =  (double) fix_width / (double) input_width;
                factor_h = (double) fix_height / (double) input_height;
                if( factor_w > factor_h ){
                	new_h = fix_height;
                	new_w = (int)Math.round(input_width * factor_h);   
                }else{
                	new_w = fix_width;
                	new_h = (int)Math.round(input_height * factor_w);  
                }
        	}
        	else{
        		//高度超过指定高，缩小图片
        		new_h = fix_height;
                factor_h = (double) fix_height / (double) input_height;
                new_w = (int)Math.round(input_width * factor_h);          		
        	}
        }

        return new ScaleInfo(new_w, new_h);
    }
}

