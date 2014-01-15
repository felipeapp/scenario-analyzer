/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ava.dominio;

import java.util.Date;

import javax.persistence.Transient;

import br.ufrn.sigaa.ava.forum.relacionamentos.dominio.ForumTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.QuestionarioTurma;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Classe mãe de todos os tipos de material disponibilizados para uma turma
 * 
 * @author David Pereira
 *
 */
public abstract class AbstractMaterialTurma implements Comparable<AbstractMaterialTurma> {

	/** Usuário que realizou o cadastro do material. */
	public abstract Usuario getUsuarioCadastro();
	
	/** Data de cadastro do material. */
	public abstract Date getDataCadastro();
	
	/** Nome do material. */
	public abstract String getNome();
	
	/** Material cadastrado. */
	public abstract MaterialTurma getMaterial();
	
	/** Aula a qual o material está vinculado. */
	public abstract TopicoAula getAula();
	
	/** Identificador do material. */
	public abstract int getId();
	
	/** Permite verificação da descrição do material na view. */
	public abstract String getDescricaoGeral();
	
	/** Indica se os alunos serão notificados da inclusão deste recurso */
	@Transient
	private boolean notificarAlunos;
	
	/** Utilizado na ordenação do material quando exibido em listas. */
	public int compareTo(AbstractMaterialTurma o) {
		int result = this.getMaterial().getOrdem().compareTo(o.getMaterial().getOrdem());
		
		if (result == 0){
			result = this.getDataCadastro().compareTo(o.getDataCadastro());
		}
		
		if (result == 0){
			result = this.getNome().compareTo(o.getNome());
		}
		
		return result;
	}
	
	public boolean isTipoArquivo() {
		return this.getClass().isAssignableFrom(ArquivoTurma.class);
	}
	
	public boolean isTipoIndicacao() {
		return this.getClass().isAssignableFrom(IndicacaoReferencia.class);
	}
	
	public boolean isTipoTarefa() {
		return this.getClass().isAssignableFrom(TarefaTurma.class);
	}
	
	public boolean isTipoQuestionario() {
		return this.getClass().isAssignableFrom(QuestionarioTurma.class);
	}
	
	public boolean isTipoVideo() {
		return this.getClass().isAssignableFrom(VideoTurma.class);
	}
	
	public boolean isTipoConteudo() {
		return this.getClass().isAssignableFrom(ConteudoTurma.class);
	}

	public boolean isTipoRotulo() {
		return this.getClass().isAssignableFrom(RotuloTurma.class);
	}

	public boolean isTipoForum() {
		return this.getClass().isAssignableFrom(ForumTurma.class);
	}

	public boolean isTipoEnquete() {
		return this.getClass().isAssignableFrom(Enquete.class);
	}

	public boolean isTipoChat() {
		return this.getClass().isAssignableFrom(ChatTurma.class);
	}

	/** Exibe o caminho do ícone que caracteriza o tipo de material. */
	public String getIcone() {
		String icone = null;
		if (isTipoArquivo()) {
			ArquivoTurma arquivo = (ArquivoTurma) this;
			String extensao = arquivo.getArquivo().getExtensao();
			
			icone = RepositorioIcones.getArquivo(extensao);
			
			if ( icone == null ) {
				icone = RepositorioIcones.getRecurso(RepositorioIcones.DESCONHECIDO);
			}
			
			return icone;
		} else if (isTipoIndicacao()) {
			return RepositorioIcones.getRecurso(RepositorioIcones.INDICACAO);
		} else if (isTipoTarefa()) {
			return RepositorioIcones.getRecurso(RepositorioIcones.TAREFA);
		} else if (isTipoConteudo()) {
			return RepositorioIcones.getRecurso(RepositorioIcones.CONTEUDO);
		} else if (isTipoQuestionario()){
			return RepositorioIcones.getRecurso(RepositorioIcones.QUESTIONARIO);
		} else if (isTipoChat()){
			return RepositorioIcones.getRecurso(RepositorioIcones.CHAT);
		} else if (isTipoForum()){
			return RepositorioIcones.getRecurso(RepositorioIcones.FORUM);
		} else if (isTipoEnquete()){
			return RepositorioIcones.getRecurso(RepositorioIcones.ENQUETE);
		} else if (isTipoVideo()){
			return RepositorioIcones.getRecurso(RepositorioIcones.VIDEO);
		} else {
			return RepositorioIcones.getRecurso(RepositorioIcones.DESCONHECIDO);
		}
		
	}

	public boolean isNotificarAlunos() {
		return notificarAlunos;
	}

	public void setNotificarAlunos(boolean notificarAlunos) {
		this.notificarAlunos = notificarAlunos;
	}
	
	public boolean isSite() {
		return false;
	}
}
