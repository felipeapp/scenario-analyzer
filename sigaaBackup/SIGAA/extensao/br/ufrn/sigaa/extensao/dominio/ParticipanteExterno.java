/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 07/02/2008
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/*******************************************************************************
 * <p>
 * O participante externo pode ser considerado um prestador de servicos na UFRN.
 * É uma pessoa de outra instituição que participa de ações de extensão.
 * </p>
 * 
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@SuppressWarnings("serial")
@Table(name = "participante_externo", schema = "extensao")
public class ParticipanteExterno implements Validatable {

	/** Chave principal */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name = "id_participante_externo", nullable = false)
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;

	/** Instituição onde esta lotado o participante externo. */
	@Column(name = "instituicao")
	private String instituicao;

	/** Informa se o participante externo esta ativo no sistema. */
	@Column(name = "ativo")
	private boolean ativo = true;

	/** Informa a titulação do participante externo. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_formacao")
	private Formacao formacao = new Formacao();

	/** Dados pessoais do participante externo. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pessoa")
	private Pessoa pessoa = new Pessoa();

	public ParticipanteExterno() {

	}

	public ParticipanteExterno(int id, String nome) {
		setId(id);
		this.pessoa.setNome(nome);
	}

	public ParticipanteExterno(int id) {
		this.id = id;
	}

	public Formacao getFormacao() {
		return formacao;
	}

	public void setFormacao(Formacao formacao) {
		this.formacao = formacao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(pessoa.getNome(), "Nome", erros);
		ValidatorUtil.validateRequired(instituicao, "Instituição", erros);
		ValidatorUtil.validateRequired(formacao, "Formação", erros);
		return erros;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public String getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(String instituicao) {
		this.instituicao = instituicao;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	
	public String getNomeCPF(){
		return pessoa.getNome() + " - " + pessoa.getCpfCnpjFormatado();
	}

}
