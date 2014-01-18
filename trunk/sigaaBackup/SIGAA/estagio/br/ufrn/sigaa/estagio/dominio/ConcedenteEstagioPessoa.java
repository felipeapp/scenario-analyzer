/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 07/10/2010
 */
package br.ufrn.sigaa.estagio.dominio;

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

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;


/**
 * Representa a(s) Pessoa(s) Relacionada(s) aos Concedentees de Estágio Convêniados.
 * 
 * @author arlindo
 *
 */
@Entity
@Table(name = "concedente_estagio_pessoa", schema = "estagio")
public class ConcedenteEstagioPessoa implements PersistDB, Validatable {
	
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="estagio.hibernate_sequence") })	
	@Column(name = "id_concedente_estagio_pessoa")		
	private int id;
	
	/** Concedente de Estágio que a pessoa está vinculada */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_concedente_estagio")		
	private ConcedenteEstagio concedente;
	
	/** Dados da Pessoa responsável pelo concedente */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa")	
	private Pessoa pessoa;
	
	/** Função que a pessoa do Concedente de Estágio tem para Operar o Estágio. */	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_funcao_concedente")	
	private ConcedentePessoaFuncao funcao;
	
	/** Cargo Exercido pela Pessoa na Empresa Concedentea */
	@Column(name = "cargo")	
	private String cargo;
	
	/**
	 * Compara o ID e do estágio com o passado por parâmetro.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}


	/** 
	 * Calcula e retorna o código hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}	
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public ConcedenteEstagio getConcedente() {
		return concedente;
	}

	public void setConcedente(ConcedenteEstagio concedente) {
		this.concedente = concedente;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public ConcedentePessoaFuncao getFuncao() {
		return funcao;
	}

	public void setFuncao(ConcedentePessoaFuncao funcao) {
		this.funcao = funcao;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	/**
	 * Valida os atributos do concedente de estágio. 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate() 
	 */	
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		ValidatorUtil.validateCPF_CNPJ(pessoa.getCpf_cnpj(), "CPF do Responsável", lista);		
		ValidatorUtil.validateRequired(pessoa.getNome(), "Nome do Responsável", lista);
		ValidatorUtil.validateRequired(pessoa.getIdentidade().getNumero(), "RG do Responsável", lista);
		ValidatorUtil.validateRequired(pessoa.getIdentidade().getOrgaoExpedicao(), "Orgão de Expedição", lista);
		ValidatorUtil.validateRequired(pessoa.getIdentidade().getUnidadeFederativa(), "UF do RG do Responsável", lista);
		ValidatorUtil.validateRequired(cargo, "Cargo", lista);
		ValidatorUtil.validateEmail(pessoa.getEmail(), "Email", lista);	
		
		return lista;
	}
}
