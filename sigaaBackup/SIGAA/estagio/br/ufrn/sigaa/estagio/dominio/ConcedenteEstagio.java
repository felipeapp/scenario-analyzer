/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 07/10/2010
 */
package br.ufrn.sigaa.estagio.dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Entidade que representa os Concedentes de Estágio (Empresas) Convêniados.
 * 
 * @author arlindo
 *
 */
@Entity
@Table(name = "concedente_estagio", schema = "estagio")
public class ConcedenteEstagio implements PersistDB {
	
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="estagio.hibernate_sequence") })	
	@Column(name = "id_concedente_estagio")	
	private int id;
	
	/** Convênio de Estágio ao qual o concedente está associado */
	@OneToOne
	@JoinColumn(name = "id_convenio_estagio")
	private ConvenioEstagio convenioEstagio;
	
	/** Empresa que será conveniada. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa")
	private Pessoa pessoa = new Pessoa();	
	
	/** Unidade, caso o tipo de convênio for "Agente Interno" */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_unidade")	
	private Unidade unidade;
	
	/** Código do Projeto, caso o tipo de convênio for "Agente Interno"  */
	@Column(name = "codigo_projeto")
	private Integer codigoProjeto;
	
	/** Indica se está ativo */
	@Column(name = "ativo")
	private boolean ativo;
	
	/** Data do cadastro. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;
	
	/** Registro entrada de quem cadastrou. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroCadastro;
	
	/**
	 * Responsáveis pelo Concedente de Estágio
	 */
	@OneToMany(mappedBy = "concedente", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<ConcedenteEstagioPessoa> concedenteEstagioPessoa = new ArrayList<ConcedenteEstagioPessoa>();	
	
	/**
	 * Retorna o Responsável do Concedente
	 */
	@Transient
	private ConcedenteEstagioPessoa responsavel;
	
	/**
	 * Retorna o Supervisor do Concedente de Estágio
	 */
	@Transient
	private ConcedenteEstagioPessoa supervisor;
	
	public ConcedenteEstagio() {
		this.ativo = true;
	}
	
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ConvenioEstagio getConvenioEstagio() {
		return convenioEstagio;
	}

	public void setConvenioEstagio(ConvenioEstagio convenioEstagio) {
		this.convenioEstagio = convenioEstagio;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public Integer getCodigoProjeto() {
		return codigoProjeto;
	}

	public void setCodigoProjeto(Integer codigoProjeto) {
		this.codigoProjeto = codigoProjeto;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}	

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public List<ConcedenteEstagioPessoa> getConcedenteEstagioPessoa() {
		return concedenteEstagioPessoa;
	}

	public void setConcedenteEstagioPessoa(
			List<ConcedenteEstagioPessoa> concedenteEstagioPessoa) {
		this.concedenteEstagioPessoa = concedenteEstagioPessoa;
	}
	
	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	
	/**
	 * Retorna a pessoa conforme a função passada
	 * @param funcao
	 * @return
	 */
	@Transient
	public ConcedenteEstagioPessoa getConcedentePessoa(int funcao){
		ConcedenteEstagioPessoa pep = null;
		if (concedenteEstagioPessoa != null && concedenteEstagioPessoa.size() > 0){
			for (ConcedenteEstagioPessoa pe : concedenteEstagioPessoa){
				if (pe.getFuncao().getId() == funcao)
					pep = pe;
			}						
		}
		return pep;
	}

	/**
	 * Retorna o Responsável do Concedente
	 * @return
	 */
	public ConcedenteEstagioPessoa getResponsavel() {
		if (responsavel == null && !ValidatorUtil.isEmpty(concedenteEstagioPessoa))
			return getConcedentePessoa(ConcedentePessoaFuncao.ADMINISTRADOR);
		return responsavel;
	}

	public void setResponsavel(ConcedenteEstagioPessoa responsavel) {
		this.responsavel = responsavel;
	}

	/**
	 * Retorna o supervisor do estágio
	 * @return
	 */
	public ConcedenteEstagioPessoa getSupervisor() {
		if (supervisor == null && !ValidatorUtil.isEmpty(concedenteEstagioPessoa))
			return getConcedentePessoa(ConcedentePessoaFuncao.SUPERVISOR);		
		return supervisor;
	}

	public void setSupervisor(ConcedenteEstagioPessoa supervisor) {
		this.supervisor = supervisor;
	}	
}
