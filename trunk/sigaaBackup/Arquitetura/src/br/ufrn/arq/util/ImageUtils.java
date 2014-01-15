/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 25/03/2008
 */
package br.ufrn.arq.util;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.RenderingHints.Key;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import sun.awt.image.BufferedImageGraphicsConfig;

/**
 * Métodos auxiliares para tratamento de imagens
 * 
 * @author Ricardo Wendell
 * @author Gleydson Lima
 *
 */
public class ImageUtils {

	public static final int IMAGE_JPEG = 0;
	public static final int IMAGE_PNG = 1;

	/**
	 * Redimensiona uma imagem para uma dimensao especificada,
	 * efetuando o crop se necessário.
	 * 
	 * @param image
	 * @param width
	 * @param height
	 * @return
	 */
	public static BufferedImage resizeImage(BufferedImage image, int width,	int height) {
		return resizeImage(image, true, width,	height) ;
	}
	
	/**
	 * Redimensiona uma imagem para uma dimensao especificada,
	 * se a resolução dela for superior a desejada
	 * 
	 * @param image
	 * @param width
	 * @param height
	 * @return
	 */
	public static BufferedImage resizeImageProportional(BufferedImage image, int width,	int height) {
		
		double propAtual = new Double(image.getWidth()) * new Double(image.getHeight());
		double propDesejada = new Double(width) * new Double(height);
		
		int w = image.getWidth(),
			h = image.getHeight();
		
		if (propAtual >= propDesejada) {
			if(w >= h){
				h = ((width * h)/w);
				w = width;
			}else{
				w =  ((height * w)/h);
				h = height;
			}
			return resize(image, w, h);
		}
		
		return image;
		
	}
	
	/**
	 * Redimensiona uma imagem
	 *
	 * @param imagem
	 * @param destWidth
	 * @param destHeight
	 * @return
	 * @throws IOException
	 */
	public static byte[] redimensionaImagem(byte[] imagem, int width, int height) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(imagem);
		BufferedImage image = ImageIO.read(in);

		image = resizeImage(image, IMAGE_PNG, width, height);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(image, "png", out);

		return out.toByteArray();
	}
	
	/**
	 * Resizes an image
	 *
	 * @param imgName The image name to resize. Must be the complet path to the file
	 * @param maxWidth The image's max width
	 * @param maxHeight The image's max height
	 * @return A resized <code>BufferedImage</code>
	 * @throws IOException If the file is not found
	 */
	public static BufferedImage resizeImage(String imgName, int type, int maxWidth, int maxHeight) throws IOException {
		return resizeImage(ImageIO.read(new File(imgName)), type, maxWidth, maxHeight);
	}

	/**
	 * Resizes an image.
	 *
	 * @param image The image to resize
	 * @param maxWidth The image's max width
	 * @param maxHeight The image's max height
	 * @return A resized <code>BufferedImage</code>
	 */
	public static BufferedImage resizeImage(Image image, int type, int maxWidth, int maxHeight) {
		Dimension largestDimension = new Dimension(maxWidth, maxHeight);

		// Original size
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);

		float aspectRation = (float)imageWidth / imageHeight;

		if (imageWidth > maxWidth || imageHeight > maxHeight) {
			if ((float)largestDimension.width / largestDimension.height > aspectRation) {
				largestDimension.width = (int)Math.ceil(largestDimension.height * aspectRation);
			}
			else {
				largestDimension.height = (int)Math.ceil(largestDimension.width / aspectRation);
			}

			imageWidth = largestDimension.width;
			imageHeight = largestDimension.height;
		}

		return createBufferedImage(image, type, imageWidth, imageHeight);
	}
	
	/**
	 * Creates a <code>BufferedImage</code> from an <code>Image</code>.
	 *
	 * @param image The image to convert
	 * @param w The desired image width
	 * @param h The desired image height
	 * @return The converted image
	 */
	public static BufferedImage createBufferedImage(Image image, int type, int w, int h) {
		if (type == IMAGE_PNG && hasAlpha(image)) {
			type = BufferedImage.TYPE_INT_ARGB;
		}
		else {
			type = BufferedImage.TYPE_INT_RGB;
		}

		BufferedImage bi = new BufferedImage(w, h, type);

		Graphics g = bi.createGraphics();
		g.drawImage(image, 0, 0, w, h, null);
		g.dispose();

		return bi;
	}
	
	/**
	 * Determines if the image has transparent pixels.
	 *
	 * @param image The image to check for transparent pixel.s
	 * @return <code>true</code> of <code>false</code>, according to the result
	 * @throws InterruptedException
	 */
	public static boolean hasAlpha(Image image) {
		try {
			PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
			pg.grabPixels();

			return pg.getColorModel().hasAlpha();
		}
		catch (InterruptedException e) {
			return false;
		}
	}
	
	/**
	 * Redimensiona uma imagem para uma dimensao especificada,
	 * efetuando as operações necessárias para evitar a perda de qualidade.
	 * 
	 * @param image
	 * @param width
	 * @param height
	 * @return
	 */
	public static BufferedImage resizeImage(BufferedImage image, boolean crop, int width,	int height) {
		image = createCompatibleImage(image);
		
		if(crop)
			image = crop(image, new Double(width)/new Double(height) );
		
		image = resize(image, 5*width, 5*height);
		image = blurImage(image);
		image = resize(image, width, height);
		
		return image;
	}
	
	/**
	 * Criar uma imagem com tipo compativel com as operacoes a serem realizadas
	 * 
	 * @param image
	 * @return
	 */
	private static BufferedImage createCompatibleImage(BufferedImage image) {
		GraphicsConfiguration gc = BufferedImageGraphicsConfig.getConfig(image);
		int w = image.getWidth();
		int h = image.getHeight();
		BufferedImage result = gc.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
		Graphics2D g2 = result.createGraphics();
		g2.drawRenderedImage(image, null);
		g2.dispose();
		return result;
	}
	
	/**
	 * Recorta a imagem para que a proporcao desejada para a imagem resultante seja mantida
	 * sem que sejam causadas deformacoes no redimensionamento
	 * 
	 * @param image
	 * @param proporcaoDesejada
	 * @return
	 */
	public static BufferedImage crop(BufferedImage image, double proporcaoDesejada) {
		double proporcao = new Double(image.getWidth())/ new Double(image.getHeight());
		
		int x = 0, 
			y = 0, 
			w = image.getWidth(),
			h = image.getHeight();
		
		// Realizar os calculos necessários de acordo com a proporcao desejada
		if (proporcao > proporcaoDesejada) {
			w = (int) (proporcaoDesejada * image.getHeight());
			x = (image.getWidth() - w )/2;
			return image.getSubimage(x, y, w, h);
		} else {
			h = (int) (image.getWidth()/proporcaoDesejada);
			y = (image.getHeight() - h)/2;
			return image.getSubimage(x, y, w, h);

		}
		
		// Retornar a subImagem
	}

	/**
	 * Redimensionar imagem, definindo as configuracoes necessárias para evitar
	 * a perda de qualidade
	 * 
	 * @param image
	 * @param width
	 * @param height
	 * @return
	 */
	private static BufferedImage resize(BufferedImage image, int width, int height) {
		int type = image.getType() == 0? BufferedImage.TYPE_INT_ARGB : image.getType();
		BufferedImage resizedImage = new BufferedImage(width, height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.setComposite(AlphaComposite.Src);
		
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		return resizedImage;
	}

	/**
	 * Aplica um filtro passa-baixa na imagem
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage blurImage(BufferedImage image) {
		float ninth = 1.0f/9.0f;
		float[] blurKernel = {
			ninth, ninth, ninth,
			ninth, ninth, ninth,
			ninth, ninth, ninth
		};

		Map<Key, Object> map = new HashMap<Key, Object>();

		map.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		map.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		map.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		RenderingHints hints = new RenderingHints(map);
		BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, blurKernel), ConvolveOp.EDGE_NO_OP, hints);
		return op.filter(image, null);
	}

	
}
