package br.ufrn.sigaa.biblioteca.circulacao.dominio;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * <p> Classe que guarda as informações dos usuários externos da biblioteca.</p>
 * <p> Observação 1:  Um usuário externo está cancelado se foi cancelado manualmente ou o seu prazo do vínculo expirou.</p>
 * <p> Observação 2:  Caso o usuário esteja cancelado, pode-se reativar seu vínculo com uma atualização na suas informações. </p>
 * <p> Observação 3:  Caso o usuário esteja desativado, deve-se criar um novo usuário biblioteca para ele. </p>
 * <p> Observação 4:  Só pode exitir um usuário externo ativo por Usuário Biblioteca.</p>
 */
@Entity
@Table (name="usuario_externo_biblioteca", schema="biblioteca")
public class UsuarioExternoBiblioteca implements Validatable {
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column(name = "id_usuario_externo_biblioteca")
	private int id;
	
	/**
	 *   A qual usuário biblioteca pertence esse usuario externo
	 */
	@OneToOne(fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	@JoinColumn (name="id_usuario_biblioteca")
	private UsuarioBiblioteca usuarioBiblioteca = new UsuarioBiblioteca();
	
	/** O documento autorizando o cadastro. */
	private String documento;
	
	/** A unidade que o usuário externo foi cadastrado  */
	@ManyToOne
	@JoinColumn(name="id_unidade", referencedColumnName="id_unidade")
	/** A unidade que autorizou o cadastro. */
	private Unidade unidade = new Unidade(-2);
	
	/** Convênio do usuário externo */
	@ManyToOne
	@JoinColumn(name="id_convenio", referencedColumnName="id_convenio")
	private Convenio convenio = new Convenio();


	/** Motivo pelo qual o usuário teve seu vínculo cancelado. */
	@Column(name="motivo_cancelamento")
	private String motivoCancelamento;
	
	
	/** O prazo de vigência para o vínculo deste usuário externo com a biblioteca. */
	@Column(name="prazo_vinculo")
	@Temporal(TemporalType.DATE)
	private Date prazoVinculo;
	
	
	/** 
	 * Indica se o vínculo do usuário externo foi cancelado manualmente pelo operador <br/>
	 * O vínculo do usuário externo também fica automaticamente cancelado quando o prazo expira. <br/>
	 * Pode ser reativado, caso o usuário alterar as suas informações com um novo prazo vínculo.<br/>
	 */
	private boolean cancelado = false;
	
	
	/**
	 * Removido do sistema por algum erro. Não pode ser atualizado, o operador vai ter que criar um novo vínculo.
	 */
	private boolean ativo = true;
	
	
	
	////////////////////////////  Dados para Auditoria ///////////////////////
	
	/** Data de cadastro. */
	@CriadoEm
	@Column(name="data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCadastro;

	/** Registro entrada do usuário que cadastrou. */
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	private RegistroEntrada registroCadastro;
	
	/** Data da última atualização do usuário externo. */
	@AtualizadoEm
	@Column(name="data_ultima_atualizacao")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataUltimaAtualizacao;

	/** Registro entrada do usuário que atualizou por último o usuário externo. */
	@AtualizadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_ultima_atualizacao")
	private RegistroEntrada registroUltimaAtualizacao;
	
	/** O usuário que cancelou o vínculo de usuário externo manualmente */
	@ManyToOne
	@JoinColumn(name="id_registro_entrada_cancelamento")
	private RegistroEntrada registroEntradaCancelamento;
	
	/** Quanto usuário externo foi cancelado manualmente */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cancelamento")
	private Date dataCancelamento;
	
	//////////////////////////////////////////////////////////////////////////
	
	
	
	
	public UsuarioExternoBiblioteca (){}
	
	
	
	
	
	public UsuarioExternoBiblioteca(int id, UsuarioBiblioteca usuarioBiblioteca,  Date prazoVinculo,
			String documento, Unidade unidade, Convenio convenio, boolean cancelado, String motivoCancelamento, boolean ativo) {
		super();
		this.id = id;
		this.usuarioBiblioteca = usuarioBiblioteca;
		this.documento = documento;
		this.unidade = unidade;
		this.convenio = convenio;
		this.prazoVinculo = prazoVinculo;
		this.cancelado = cancelado;
		this.motivoCancelamento = motivoCancelamento;
		this.ativo = ativo;
	}





	public UsuarioExternoBiblioteca (int id){
		this.id = id;
	}

	
	/**
	 *   Verifica se o vínculo de usuário externo continua ativo no sistema.
	 *
	 * @return
	 */
	public boolean isCancelado(){
		// se foi cancelado manualmente ou o prazo expirou //
		
		if(isCanceladoManualmenente() || isCanceladoPorPrazo() )
			return true;
		else
			return false;
	}
	
	
	/**
	 *   Verifica se o vínculo de usuário externo continua ativo no sistema.
	 *
	 * @return
	 */
	public boolean isCanceladoManualmenente(){
		// se foi cancelado manualment //
		
		if(cancelado == true)
			return true;
		else
			return false;
	}
	
	
	/**
	 *   Verifica se o vínculo de usuário externo continua ativo no sistema.
	 *
	 * @return
	 */
	public boolean isCanceladoPorPrazo(){
		// se o prazo expirou  do cadastro expirou //
		
		if(! isCanceladoManualmenente() && prazoVinculo != null && CalendarUtils.compareTo(prazoVinculo, new Date()) < 0)
			return true;
		else
			return false;
	}
	
	
	
	
	/**
	 * 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens mensagens = new ListaMensagens ();
		
		if(usuarioBiblioteca == null)
			mensagens.addErro("O usuário externo precisa está vinculado com um usuário da biblioteca.");		
		
		if(ativo == true)
			if( prazoVinculo == null || CalendarUtils.compareTo(prazoVinculo, new Date()) < 0 )
				mensagens.addErro("O prazo do vínculo de usuário externo precisa ser uma data futura.");		
		
		return mensagens;
	}

	
	@Override
	public boolean equals (Object obj){
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	@Override
	public int hashCode (){
		return HashCodeUtil.hashAll(id);
	}
	
	
	
	////////  Sets e Gets ////////
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UsuarioBiblioteca getUsuarioBiblioteca() {
		return usuarioBiblioteca;
	}

	public void setUsuarioBiblioteca(UsuarioBiblioteca usuarioBiblioteca) {
		this.usuarioBiblioteca = usuarioBiblioteca;
	}
	
	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public Convenio getConvenio() {
		return convenio;
	}

	public void setConvenio(Convenio convenio) {
		this.convenio = convenio;
	}

	
	
	public String getMotivoCancelamento() {
		return motivoCancelamento;
	}

	public void setMotivoCancelamento(String motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}

	public Date getDataCancelamento() {
		return dataCancelamento;
	}

	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}

	public RegistroEntrada getRegistroEntradaCancelamento() {
		return registroEntradaCancelamento;
	}

	public void setRegistroEntradaCancelamento(RegistroEntrada registroEntradaCancelamento) {
		this.registroEntradaCancelamento = registroEntradaCancelamento;
	}

	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Date getPrazoVinculo() {
		return prazoVinculo;
	}

	public void setPrazoVinculo(Date prazoVinculo) {
		this.prazoVinculo = prazoVinculo;
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

	public Date getDataUltimaAtualizacao() {
		return dataUltimaAtualizacao;
	}

	public void setDataUltimaAtualizacao(Date dataUltimaAtualizacao) {
		this.dataUltimaAtualizacao = dataUltimaAtualizacao;
	}

	public RegistroEntrada getRegistroUltimaAtualizacao() {
		return registroUltimaAtualizacao;
	}

	public void setRegistroUltimaAtualizacao(RegistroEntrada registroUltimaAtualizacao) {
		this.registroUltimaAtualizacao = registroUltimaAtualizacao;
	}

	public void setCancelado(boolean cancelado) {
		this.cancelado = cancelado;
	}	
}