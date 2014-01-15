package br.ufrn.sigaa.apedagogica.dominio;

import org.apache.myfaces.custom.fileupload.UploadedFile;

/**
 * Classe não persistida utilizada para armazenar a 
 * associação da atividade com arquivo no formulário de cadastro 
 * do grupo de atividades. Essas informações são adicionadas
 * a uma lista e depois processadas em lote.
 * @author Mário Rizzi
 *
 */
public class AtividadeArquivo {

	/** Atividade de Atualização Pedagógica */
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
