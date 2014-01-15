/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 26/10/2006
 *
 */
package br.ufrn.sigaa.monitoria.negocio;

import java.util.Collection;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.monitoria.dominio.ComponenteCurricularMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocenteComponente;
import br.ufrn.sigaa.monitoria.dominio.ProvaSelecaoComponenteCurricular;

/**
 * Movimento para cadastro de projetos de monitoria.
 *
 * @author David Ricardo
 * @author ilueny santos
 *
 */
public class ProjetoMonitoriaMov extends MovimentoCadastro {

	/** Atributo utilizado para representar a ação de ALTERAR_COORDENADOR no cadastro de projeto de monitoria */
	public static final int ACAO_ALTERAR_COORDENADOR = 11;
	/** Atributo utilizado para representar a ação de AVALIAR_PROJETO_POR_MEMBRO_COMISSAO no cadastro de projeto de monitoria */
	public static final int ACAO_AVALIAR_PROJETO_POR_MEMBRO_COMISSAO = 12;
	/** Atributo utilizado para representar a ação de AVALIAR_PROJETO_POR_MEMBRO_PROGRAD no cadastro de projeto de monitoria */
	public static final int ACAO_AVALIAR_PROJETO_POR_MEMBRO_PROGRAD = 13;
	/** Atributo utilizado para representar a ação de VALIDAR_CADASTRO_SELECAO no cadastro de projeto de monitoria */
	public static final int ACAO_VALIDAR_CADASTRO_SELECAO = 14;
	/** Atributo utilizado para representar a ação de ALTERAR_DOCENTE_PROGRAD no cadastro de projeto de monitoria */
	public static final int ACAO_ALTERAR_DOCENTE_PROGRAD = 15;
	/** Atributo utilizado para representar a ação de ALTERAR_COMPONENTES_OBRIGATORIOS no cadastro de projeto de monitoria */
	public static final int ACAO_ALTERAR_COMPONENTES_OBRIGATORIOS = 16;
	/** Atributo utilizado para representar a ação de ALTERAR_SITUACAO_PROJETO no cadastro de projeto de monitoria */
	public static final int ACAO_ALTERAR_SITUACAO_PROJETO = 17;
	/** Atributo utilizado para representar a ação de DESVALIDAR_SELECAO no cadastro de projeto de monitoria */
	public static final int ACAO_DESVALIDAR_SELECAO = 18;
	/** Atributo utilizado para representar a ação de CADASTRAR_PROVA_SELECAO no cadastro de projeto de monitoria */
	public static final int ACAO_CADASTRAR_PROVA_SELECAO = 19;
	/** Atributo utilizado para representar a ação de ENVIAR_PROPOSTA_AOS_DEPARTAMENTOS no cadastro de projeto de monitoria */
	public static final int ACAO_ENVIAR_PROPOSTA_AOS_DEPARTAMENTOS = 20;
	/** Atributo utilizado para representar a ação de GRAVAR_TEMPORARIAMENTE no cadastro de projeto de monitoria */
	public static final int ACAO_GRAVAR_TEMPORARIAMENTE = 21;
	/** Atributo utilizado para representar a ação de ENVIAR_PROPOSTA_ASSOCIADA_AOS_DEPARTAMENTOS no cadastro de projeto de monitoria */
	public static final int ACAO_ENVIAR_PROPOSTA_ASSOCIADA_AOS_DEPARTAMENTOS = 22;
	/** Atributo utilizado para representar a ação de DESATIVAR_PROJETO no cadastro de projeto de monitoria */
	public static final int ACAO_DESATIVAR_PROJETO = 23;
	/** Atributo utilizado para representar a ação de PUBLICAR_RESULTADO_AVALIACOES no cadastro de projeto de monitoria */
	public static final int ACAO_PUBLICAR_RESULTADO_AVALIACOES = 24;
	/** Atributo utilizado para representar a ação de CONCLUIR_PROJETO no cadastro de projeto de monitoria */
	public static final int ACAO_CONCLUIR_PROJETO = 25;
	
	/** Atributo utilizado para representar uma Collection de vínculos entre Docentes de Componentes  que foram removidos */
	private Collection<EquipeDocenteComponente> equipesDocenteComponenteRemovidos;
	/** Atributo utilizado para representar um Collection de de componentes curriculares específicos de monitoria removidos do projeto */
	private Collection<ComponenteCurricularMonitoria> componentesCurricularesRemovidos;	
	/** 
	 * Atributo utilizado para representar um Collection de vínculos entre, provas de seleção e todos os componentes
	 *  curriculares, removidos  
	 */
	private Collection<ProvaSelecaoComponenteCurricular> provaComponentesRemovidos;
	/** Atributo utilizado para representar o ID do projeto original reconsiderado */
	private int idProjetoOriginalReconsiderado;
	/** Atributo utilizado para representar o Id do novo coordenador */
	private Integer idNovoCoordenador;
	/** Atributo utilizado para informar se houve solicitação da PROGRAD */
	private boolean solicitacaoPrograd;
	
	
	

	public Integer getIdNovoCoordenador() {
		return idNovoCoordenador;
	}

	public void setIdNovoCoordenador(Integer idNovoCoordenador) {
		this.idNovoCoordenador = idNovoCoordenador;
	}

	public int getIdProjetoOriginalReconsiderado() {
		return idProjetoOriginalReconsiderado;
	}

	public void setIdProjetoOriginalReconsiderado(int idProjetoOriginalReconsiderado) {
		this.idProjetoOriginalReconsiderado = idProjetoOriginalReconsiderado;
	}

	public Collection<ComponenteCurricularMonitoria> getComponentesCurricularesRemovidos() {
		return componentesCurricularesRemovidos;
	}

	public void setComponentesCurricularesRemovidos(
			Collection<ComponenteCurricularMonitoria> componentesCurricularesRemovidos) {
		this.componentesCurricularesRemovidos = componentesCurricularesRemovidos;
	}

	public Collection<EquipeDocenteComponente> getEquipesDocenteComponenteRemovidos() {
		return equipesDocenteComponenteRemovidos;
	}

	public void setEquipesDocenteComponenteRemovidos(
			Collection<EquipeDocenteComponente> equipesDocenteComponenteRemovidos) {
		this.equipesDocenteComponenteRemovidos = equipesDocenteComponenteRemovidos;
	}

	/**
	 * Lista de componentes obrigatórios removidos da prova seletiva.
	 * 
	 * @return
	 */
	public Collection<ProvaSelecaoComponenteCurricular> getProvaComponentesRemovidos() {
		return provaComponentesRemovidos;
	}

	public void setProvaComponentesRemovidos(
			Collection<ProvaSelecaoComponenteCurricular> provaComponentesRemovidos) {
		this.provaComponentesRemovidos = provaComponentesRemovidos;
	}

	/**
	 * Informa que a solicitação do movimento está sendo realizada por
	 * membro da prograd.
	 * 
	 * @return
	 */
	public boolean isSolicitacaoPrograd() {
		return solicitacaoPrograd;
	}

	public void setSolicitacaoPrograd(boolean solicitacaoPrograd) {
		this.solicitacaoPrograd = solicitacaoPrograd;
	}

}