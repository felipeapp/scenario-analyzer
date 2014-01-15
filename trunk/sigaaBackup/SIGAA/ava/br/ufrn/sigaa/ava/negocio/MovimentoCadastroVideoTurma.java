/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 20/02/2011
 *
 */
package br.ufrn.sigaa.ava.negocio;

import java.util.List;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dominio.VideoTurma;

/**
 * Classe de negócio que auxilia na gerência de vídeos nas turmas virtuais.
 * 
 * @author Fred_Castro
 *
 */
public class MovimentoCadastroVideoTurma extends AbstractMovimentoAdapter {

	/** Objeto vídeo que será salvo. */
	private VideoTurma video;
	
	/** Arquivo do vídeo que será salvo. */
	private UploadedFile arquivoEnviado;
	
	/** Se é pra cadastrar em mais de uma turma. */
	private boolean cadastrarEmVariasTurmas;
	
	/** Turmas em que o vídeo será cadastrado. */
	private List<String> cadastrarEm;
	
	/** Id de um arquivo do porta-arquivos. */
	private Integer idArquivoPA;
	
	/** Forma de envio do arquivo. */
	private Character enviar;
	
	/** Construtor padrão. */
	public MovimentoCadastroVideoTurma() {		
	}
	
	public MovimentoCadastroVideoTurma (VideoTurma video, UploadedFile arquivoEnviado){
		this.video = video;
		this.arquivoEnviado = arquivoEnviado;
		
		setCodMovimento(SigaaListaComando.CADASTRAR_VIDEO_TURMA);
	}

	public VideoTurma getVideo() {
		return video;
	}

	public void setVideo(VideoTurma video) {
		this.video = video;
	}

	public UploadedFile getArquivoEnviado() {
		return arquivoEnviado;
	}

	public void setArquivoEnviado(UploadedFile arquivoEnviado) {
		this.arquivoEnviado = arquivoEnviado;
	}

	public void setCadastrarEmVariasTurmas(boolean cadastrarEmVariasTurmas) {
		this.cadastrarEmVariasTurmas = cadastrarEmVariasTurmas;
	}

	public boolean isCadastrarEmVariasTurmas() {
		return cadastrarEmVariasTurmas;
	}

	public void setCadastrarEm(List<String> cadastrarEm) {
		this.cadastrarEm = cadastrarEm;
	}

	public List<String> getCadastrarEm() {
		return cadastrarEm;
	}

	public void setEnviar(Character enviar) {
		this.enviar = enviar;
	}

	public Character getEnviar() {
		return enviar;
	}

	public void setIdArquivoPA(Integer idArquivoPA) {
		this.idArquivoPA = idArquivoPA;
	}

	public Integer getIdArquivoPA() {
		return idArquivoPA;
	}
}
