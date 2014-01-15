/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Sep 19, 2008
 *
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.StringUtils;

/**
 *	<p>Esta entidade registra uma solicitação normalização.</p>
 * 
 *	@author Felipe Rivas
 */
@Entity
@Table(name = "solicitacao_normalizacao", schema = "biblioteca")
public class SolicitacaoNormalizacao extends SolicitacaoServicoDocumento {

	/** Id da solicitação */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.solicitacoes_usuario_sequence") })
	@Column(name = "id_solicitacao_normalizacao", nullable = false)
	private int id;

	// Aspectos a serem normalizados.

	/** Se verdadeiro, todo o trabalho será normalizado */
	@Column(name="trabalho_todo")
	private boolean trabalhoTodo;

	/** Se verdadeiro, as referências do trabalho serão normalizadas */
	@Column(name="referencias")
	private boolean referencias;

	/** Se verdadeiro, as citações do trabalho serão normalizadas */
	@Column(name="citacoes")
	private boolean citacoes;

	/** Se verdadeiro, a estrutura do trabalho será normalizada */
	@Column(name="estrutura_do_trabalho")
	private boolean estruturaDoTrabalho;

	/** Se verdadeiro, os itens pré-textuais serão normalizados */
	@Column(name="pre_textuais")
	private boolean preTextuais;

	/** Se verdadeiro, os itens pró-textuais serão normalizados */
	@Column(name="pro_textuais")
	private boolean proTextuais;

	/** Se verdadeiro, outras seções do trabalho serão normalizadas */
	@Column(name="outros_aspectos_normalizacao")
	private boolean outrosAspectosNormalizacao;

	/** descrição dos outros aspectos a serem normalizados, caso selecionado */
	@Column(name="descricao_outros_aspectos_normalizacao")
	private String descricaoOutrosAspectosNormalizacao; //Utilizado quando o aspecto a ser normalizado seja outros.

	/////////////////////////// sets e gets ////////////////////////////
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isTrabalhoTodo() {
		return trabalhoTodo;
	}

	public void setTrabalhoTodo(boolean trabalhoTodo) {
		this.trabalhoTodo = trabalhoTodo;
	}

	public boolean isReferencias() {
		return referencias;
	}

	public void setReferencias(boolean referencias) {
		this.referencias = referencias;
	}
	public boolean isCitacoes() {
		return citacoes;
	}

	public void setCitacoes(boolean citacoes) {
		this.citacoes = citacoes;
	}

	public boolean isEstruturaDoTrabalho() {
		return estruturaDoTrabalho;
	}

	public void setEstruturaDoTrabalho(boolean estruturaDoTrabalho) {
		this.estruturaDoTrabalho = estruturaDoTrabalho;
	}

	public boolean isPreTextuais() {
		return preTextuais;
	}

	public void setPreTextuais(boolean preTextuais) {
		this.preTextuais = preTextuais;
	}

	public boolean isProTextuais() {
		return proTextuais;
	}

	public void setProTextuais(boolean proTextuais) {
		this.proTextuais = proTextuais;
	}

	public boolean isOutrosAspectosNormalizacao() {
		return outrosAspectosNormalizacao;
	}

	public void setOutrosAspectosNormalizacao(boolean outrosAspectosNormalizacao) {
		this.outrosAspectosNormalizacao = outrosAspectosNormalizacao;
	}

	public String getDescricaoOutrosAspectosNormalizacao() {
		return descricaoOutrosAspectosNormalizacao;
	}

	public void setDescricaoOutrosAspectosNormalizacao(String descricaoOutrosAspectosNormalizacao) {
		this.descricaoOutrosAspectosNormalizacao = descricaoOutrosAspectosNormalizacao;
	}
	
	/**
	 * Valida o preenchimento dos campos do objeto.
	 * 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens erros = super.validate();
		
		if(!trabalhoTodo && !referencias && !citacoes && !estruturaDoTrabalho &&
				!preTextuais && !proTextuais && !outrosAspectosNormalizacao) {
			erros.addErro("Pelo menos um aspecto a ser normalizado deve ser selecionado");
		}
		
		if(outrosAspectosNormalizacao && StringUtils.isEmpty(descricaoOutrosAspectosNormalizacao)) {
			erros.addErro("Os aspectos a serem normalizados devem ser informados no campo OUTROS.");
		}
		
		return erros;
	}

	@Override
	public String getTipoServico() {
		return "Normalização";
	}
		
}