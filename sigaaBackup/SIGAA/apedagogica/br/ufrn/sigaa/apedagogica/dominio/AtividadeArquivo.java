package br.ufrn.sigaa.apedagogica.dominio;

import org.apache.myfaces.custom.fileupload.UploadedFile;

/**
 * Classe n�o persistida utilizada para armazenar a 
 * associa��o da atividade com arquivo no formul�rio de cadastro 
 * do grupo de atividades. Essas informa��es s�o adicionadas
 * a uma lista e depois processadas em lote.
 * @author M�rio Rizzi
 *
 */
public class AtividadeArquivo {

	/** Atividade de Atualiza��o Pedag�gica */
	private AtividadeAtualizacaoPedagogica atividade;
	
	/** Arquivo da ementa da atividade */
	private UploadedFile arquivo;

	public AtividadeArquivo(){
		this.atividade = new AtividadeAtualizacaoPedagogica();
		this.arquivo = null;
	}
	
	public AtividadeArquivo(AtividadeAtualizacaoPedagogica atividade, UploadedFile arquivo){
		this.atividade = atividade;
		this.arquivo = arquivo;
	}
	
	public AtividadeAtualizacaoPedagogica getAtividade() {
		return atividade;
	}

	public void setAtividade(AtividadeAtualizacaoPedagogica atividade) {
		this.atividade = atividade;
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}
	
}
