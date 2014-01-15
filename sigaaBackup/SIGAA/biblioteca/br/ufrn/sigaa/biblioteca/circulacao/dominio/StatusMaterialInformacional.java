package br.ufrn.sigaa.biblioteca.circulacao.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;

/**
 *   Os status dos materias informacionais do sistema. Dependendo do status algumas regras 
 * especiais são aplicadas na hora de realizar o empréstimo. 
 * 
 * @author Fred
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "status_material_informacional", schema = "biblioteca")
public class StatusMaterialInformacional implements Validatable{

	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column(name = "id_status_material_informacional")
	private int id;

	/** A descricao mostrada ao usuário */
	@Column(name="descricao" , nullable=false)
	private String descricao;
	
	/**
	 * Configura no sistema quais status aceitam reservas de materiais
	 */
	@Column(name="aceita_reserva" , nullable=false)
	private boolean aceitaReserva = true;
	
	
	/**
	 * Diz se o status do material vai permitir emprétimos ou não.
	 */
	@Column(name="permite_emprestimo" , nullable=false)
	private boolean permiteEmprestimo = true;
	
	
	/** Campo utilizado para verificar se está ativo ou não */
	private boolean ativo = true;
	
	
	//////////////////////// informações de auditoria ///////////////
	/**
	 * registro entrada do usuário que cadastrou
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/**
	 * data de cadastro
	 */
	@CriadoEm
	@Column(name="data_cadastro")
	private Date dataCadastro;

	/**
	 * registro entrada do usuário que realizou a última atualização
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/**
	 * data da última atualização
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;

	//////////////////////////////////////////////////////////////////
	
	
	/**
	 * Guarda se esse objeto foi selecionado (usados nas páginas JSP)
	 */
	@Transient
	protected boolean selecionado;
	
	
	/**
	 * Construtor para hibernate e jsf
	 */
	public StatusMaterialInformacional(){

	}

	/**
	 * Construtor para hibernate e jsf
	 */
	public StatusMaterialInformacional(int id){
		this.id = id;
	}
	
	
	/**
	 * Contrutor para o cadastro
	 * 
	 * @param status
	 * @param descricao
	 * @param circula
	 * @param limitadorPrazoHoras
	 */
	public StatusMaterialInformacional(String descricao) {
		this.descricao = descricao;
	}

	
	
	/**
	 * 
	 * @param status
	 * @param descricao
	 * @param circula
	 * @param limitadorPrazoHoras
	 */
	public StatusMaterialInformacional(int id, String descricao) {
		this(id);
		this.descricao = descricao;
	}

	
	/**
	 * 
	 * @param status
	 * @param descricao
	 * @param circula
	 * @param limitadorPrazoHoras
	 */
	public StatusMaterialInformacional(int id, String descricao, boolean permiteEmprestimo) {
		this(id, descricao);
		this.permiteEmprestimo = permiteEmprestimo;
	}


	
	/**
	 * Validar se as variáveis estão preenchidas corretamente
	 */
	public ListaMensagens validate() {

		ListaMensagens mensagens = new ListaMensagens();

		if (StringUtils.isEmpty(descricao))
			mensagens.addErro("É preciso informar a descrição do Status do Material Informacional");

		return mensagens;
	}

	
	
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(this.getId());
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	
	@Override
	public String toString() {
		return "Status: "+descricao+"("+id+") ";
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public boolean isAceitaReserva() {
		return aceitaReserva;
	}

	public void setAceitaReserva(boolean aceitaReserva) {
		this.aceitaReserva = aceitaReserva;
	}

	public boolean isPermiteEmprestimo() {
		return permiteEmprestimo;
	}

	public void setPermiteEmprestimo(boolean permiteEmprestimo) {
		this.permiteEmprestimo = permiteEmprestimo;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}
	
}
