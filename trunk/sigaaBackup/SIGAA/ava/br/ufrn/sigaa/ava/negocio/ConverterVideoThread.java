package br.ufrn.sigaa.ava.negocio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.ava.dao.VideoTurmaDao;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesVideoTurma;
import br.ufrn.sigaa.ava.dominio.VideoTurma;

/**
 * Thread que envia um vídeo a converter ou recebe o vídeo convertido pelo conversor de vídeos.
 * 
 * @author freddcs
 *
 */

public class ConverterVideoThread extends Thread implements Runnable {
	
	/** A porta na qual o conversor está escutando. */
	public static int porta;
	
	/** O endereço no qual o conversor está escutando. */
	public static String servidor;
	
	/** O vídeo a ser convertido. */
	private VideoTurma video;
	
	/** Configuração de conversão de um vídeo. */
	private ConfiguracoesVideoTurma videoConfig;
	
	/** O vetor de bytes do conteúdo do vídeo. */
	private byte [] bytes;
	
	/** Constante que sinaliza a operação de enviar o vídeo a converter. */
	public static final int OPERACAO_ENVIAR_VIDEO_A_CONVERTER = 1;
	
	/** Constante que sinaliza a operaçõ de receber o vídeo convertido. */
	public static final int OPERACAO_RECEBER_VIDEO_CONVERTIDO = 2;
	
	/** Constante que sinaliza o sucesso na última operação realizada. */
	public static final int OK = 10;
	
	/** Constante que sinaliza o erro na última operação realizada. */
	public static final int ERRO = 11;
	
	/** Constante que sinaliza que a conversão do vídeo ainda está ocorrento e deve ficar aguardando. */
	public static final int AGUARDANDO = 12;
	
	/**
	 * Construtor da classe.
	 * 
	 * @param video O Vídeo a converter ou solicitar ao conversor.
	 * @param bytes Os dados do vídeo a enviar ao conversor.
	 * @param enderecoConverter O endereço do conversor.
	 */
	public ConverterVideoThread (VideoTurma video, ConfiguracoesVideoTurma videoConfig , byte [] bytes, String enderecoConverter) {
		
		String [] auxEnderecoConverter = enderecoConverter.split(":");
		servidor = auxEnderecoConverter[0];
		porta = Integer.parseInt(auxEnderecoConverter[1]);
		
		this.video = video;
		this.videoConfig = videoConfig;
		this.bytes = bytes;
	}
	
	/**
	 * Método padrão da execução da thread.
	 * 
	 */
	public void run () {
		System.out.println("Conectando ...");
		Socket socket = null;  
		DataOutputStream os = null;
		DataInputStream is = null;
		VideoTurmaDao dao = null;

		try {

			dao = new VideoTurmaDao();
			
			try {
				socket = new Socket(servidor, porta);
				os = new DataOutputStream(socket.getOutputStream());
				is = new DataInputStream(socket.getInputStream());
			} catch (UnknownHostException e) {
				System.err.println("Não foi possível conectar ao servidor " + servidor);
				dao.informaProgressoConversao(video.getId(), "Não foi possível conectar ao servidor " + servidor, true);
			} catch (IOException e) {
				System.err.println("erro de I/O");
				dao.informaProgressoConversao(video.getId(), "Não foi possível conectar ao servidor " + servidor, true);
			}

			if (socket != null && os != null && is != null) {
							
				// Se foram passados os bytes do arquivo a converter, envia ao conversor.
				if (bytes != null){
					os.writeInt(OPERACAO_ENVIAR_VIDEO_A_CONVERTER);
					os.writeInt(video.getId());
					os.writeUTF(video.getNome());
					os.writeUTF(videoConfig != null ? videoConfig.getComando() : "");
					os.writeUTF(""); //Feito isso para enviar a extensão dos arquivos a serem convertidos.
					os.write(bytes);
					
					// inicia a thread para receber o vídeo convertido.
					new ConverterVideoThread (video, videoConfig, null, servidor + ":" + porta).start();
					
				// Se não foram passados os bytes do arquivo a converter, é porque o mesmo já foi enviado.
				} else {
					
					os.writeInt(OPERACAO_RECEBER_VIDEO_CONVERTIDO);
					os.writeInt(video.getId());
									
					// Descobre a situação do vídeo
					switch (is.readInt()){
						case OK:
							// Descobre o tamanho do arquivo convertido.
							int length = is.readInt();
							
							byte [] bytes = new byte[length];
							int offset = 0, numRead = 0;
							
							// Recebe o vídeo convertido.
							while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
								offset += numRead;
							
							int idArquivoConvertido = EnvioArquivoHelper.getNextIdArquivo();
							EnvioArquivoHelper.inserirArquivo(idArquivoConvertido, bytes, "video/x-flv", video.getNome());
							
							// Apaga o vídeo original
							VideoTurma v = dao.findByPrimaryKey(video.getId(), VideoTurma.class);
							if (v.getIdArquivo() != null)
								EnvioArquivoHelper.removeArquivo(v.getIdArquivo());
							
							// Atualiza o id do vídeo
							dao.atualizaVideoConvertido(video.getId(), idArquivoConvertido);
						break;
						
						case ERRO:
							dao.informaProgressoConversao(video.getId(), is.readUTF(), true);
						break;
						
						case AGUARDANDO:
							dao.informaProgressoConversao(video.getId(), is.readUTF(), false);
							
							// Como não houve erro e ainda está aguardando a conversão, tenta novamente em cinco segundos.
							sleep(5 * 1000);
							new ConverterVideoThread (video, videoConfig, null, servidor + ":" + porta).start();
							
						break;
					}
				}
				
				is.close();
				socket.close();   
			}
		} catch (UnknownHostException e) {
			System.err.println("UnknownHostException: " + e);
		} catch (IOException e) {
			System.err.println("IOException: " + e);
		} catch (DAOException e) {
			System.err.println("DAOException: " + e);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (dao != null)
				dao.close();
		}
	}
}