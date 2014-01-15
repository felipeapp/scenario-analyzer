package br.ufrn.sigaa.ava.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.ava.dao.VideoTurmaDao;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesVideoTurma;

/**
 * M�todos utilit�rios para opera��es sobre MimeType. 
 * 
 * @author Diego J�come
 *
 */
public class MimeTypeVideoUtil {

	/** MimeTypes que podem ser convertidos pelo conversor de v�deos da turma virtual. */
	private static Map<String, String> mimeTypesVideo;
	
	static {
		mimeTypesVideo = new HashMap<String, String>();
		mimeTypesVideo.put("FLV", "video/x-flv");
		mimeTypesVideo.put(".RMF", "video/vnd.rn-realmedia");
		mimeTypesVideo.put("FWS", "application/x-shockwave-flash");
		mimeTypesVideo.put("ftyp", "video/mp4");
	}

	/**
	 * Retorna o mimetype dos bytes caso esse esteja presente no mapa de mimetypes de v�deos da turma virtual
	 * @param bytes
	 * @return
	 */
	public static String getMimeTypeVideo( byte[] bytes ) {
		
		for ( Entry<String, String> entry : mimeTypesVideo.entrySet() ){
			String key = entry.getKey();
			String value = entry.getValue();
			String sub = "";
			
			if (bytes.length >= key.length()){	
				
				if (!value.equals("video/mp4"))
					for ( int i = 0 ; i < key.length() ; i++ )
						sub += (char) bytes[i];
				else
					for ( int i = 4 ; i < key.length()+4 ; i++ )
						sub += (char) bytes[i];
			}
			if ( key.equals(sub) )
				return value;
		}
		return null;
	}
	
	/**
	 * Verifica se o mimetype dos bytes est� presente no mapa dos mimetypes dos v�deos da turma virtual
	 * @param bytes
	 * @return
	 */
	public static boolean isFormatoVideoOk ( byte[] bytes ) {
		
		for ( Entry<String, String> entry : mimeTypesVideo.entrySet() ){
			String key = entry.getKey();
			String value = entry.getValue();
			String sub = "";
			
			if (bytes.length >= key.length()){	
				
				if (!value.equals("video/mp4"))
					for ( int i = 0 ; i < key.length() ; i++ )
						sub += (char) bytes[i];
				else
					for ( int i = 4 ; i < key.length()+4 ; i++ )
						sub += (char) bytes[i];
			}
			if ( key.equals(sub) )
				return true;
		}
		return false;
	}
	
	/**
	 * Verifica se o v�deo � do formato SWF e foi comprimido.
	 * @param bytes
	 * @return
	 */
	public static boolean isCWS ( byte[] bytes ){
		if ( bytes != null && bytes.length > 3 )
			if ( bytes[0] == 'C' && bytes[1] == 'W' && bytes[2] == 'S')
				return true;
		return false;	
	}
	
	/**
	 * Retorna as configura��es de convers�o do v�deo de acordo com seu mime type.
	 * @param bytes
	 * @return
	 */
	public static ConfiguracoesVideoTurma getConfiguracoesVideoByMimeType ( byte[] bytes ) throws DAOException {
		
		String mimeType = MimeTypeVideoUtil.getMimeTypeVideo(bytes);
		ConfiguracoesVideoTurma videoConfig;
		VideoTurmaDao dao = null;

		try {
			
			dao = DAOFactory.getInstance().getDAO(VideoTurmaDao.class);
			
			if (mimeType != null && mimeType.equals("FLV")){
				videoConfig = dao.findByPrimaryKey(ConfiguracoesVideoTurma.FLV, ConfiguracoesVideoTurma.class);
			} else {
				videoConfig = dao.findByPrimaryKey(ConfiguracoesVideoTurma.DEMAIS_MIME_TYPES, ConfiguracoesVideoTurma.class);
			}
			
		} finally {
			if (dao != null)
				dao.close();
		}
		return videoConfig;	
	}
}
