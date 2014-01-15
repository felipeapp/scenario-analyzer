/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 */
package br.ufrn.sigaa.cv.dominio;

import java.util.Date;

import javax.persistence.Transient;

import br.ufrn.sigaa.ava.dominio.RepositorioIcones;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Classe m�e de todos os tipos de material disponibilizados para uma turma
 * 
 * @author David Pereira
 *
 */
public abstract class MaterialComunidade implements Comparable<MaterialComunidade> {

	public abstract Usuario getUsuarioCadastro();
	public abstract Date getDataCadastro();
	public abstract String getNome();
	public abstract String getTipoMaterial();
	
	/**
	 * Indica se os membros da comunidade ser�o notificados da inclus�o deste recurso
	 */
	@Transient
	private boolean notificarMembros;
	
	public int compareTo(MaterialComunidade o) {
		int result = this.getDataCadastro().compareTo(o.getDataCadastro());
		
		if (result == 0)
			return this.getNome().compareTo(o.getNome());
		return result;
	}
	
	public boolean isTipoArquivo() {
		return this instanceof ArquivoComunidade;
	}
	
	public boolean isTipoIndicacao() {
		return this instanceof IndicacaoReferenciaComunidade;
	}
	
	public boolean isTipoConteudo() {
		return this instanceof ConteudoComunidade;
	}
	
	public String getIcone() {
		String icone = null;
		if (isTipoArquivo()) {
			ArquivoComunidade arquivo = (ArquivoComunidade) this;
			String extensao = arquivo.getArquivo().getExtensao();
			
			icone = RepositorioIcones.getArquivo(extensao);
			
			if ( icone == null ) {
				icone = RepositorioIcones.getRecurso(RepositorioIcones.DESCONHECIDO);
			}
			
			return icone;
		} else if (isTipoIndicacao()) {
			return RepositorioIcones.getRecurso(RepositorioIcones.INDICACAO);
		} else if (isTipoConteudo()) {
			return RepositorioIcones.getRecurso(RepositorioIcones.CONTEUDO);
		} else {
			return RepositorioIcones.getRecurso(RepositorioIcones.DESCONHECIDO);
		}
	}

	public boolean isNotificarMembros() {
		return notificarMembros;
	}
	public void setNotificarMembros(boolean notificarMembros) {
		this.notificarMembros = notificarMembros;
	}	
}
