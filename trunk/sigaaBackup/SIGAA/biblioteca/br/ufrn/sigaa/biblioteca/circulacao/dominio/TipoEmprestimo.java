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

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Entidade que define tipos de empr�stimos
 * 
 * Tipos existentes quando a classe foi criada: Normal ou Fotoc�pia, Institucional, Prazo Configur�vel
 * 
 * @author Fred_Castro
 */
@Entity
@Table(name = "tipo_emprestimo", schema = "biblioteca")
public class TipoEmprestimo implements Validatable, Comparable<TipoEmprestimo>{
	

	/**
	 * id
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column(name = "id_tipo_emprestimo")
	private int id;

	/**
	 * descri��o
	 */
	@Column(name = "descricao")
	private String descricao;
	
	
	/** Vari�vel que diz ao sistema se o usu�rio pode alterar esse tipo de empr�stimo.
	 *  Serve para o usu�rio n�o conseguir alterar tipos de empr�stimos pre-cadastrados que s�o essenciais para o funcionamento do sistema.
	 *  
	 *  Empr�stimo INSTITUCIONAL e PERSONALIZADO s�o os tipos de empr�stimo que existem no sistema mais o usu�rio n�o pode alter�-lo.
	 */
	private boolean alteravel = true;

	
	/** Empr�stimo especial utilizando para realizar empr�stimos entre bibliotecas.  N�o pode ser alterado pelo usu�rio.
	 * 
	 * S� deve existir 1 tipo de emprestimos pr�-cadastrado no sistema com essa vari�vel com valor verdadeiro
	 */  
	@Column(name="institucional", nullable = false)
	private boolean institucional = false;
	
	
	/** Empr�stimo especial no qual o usu�rio ( chefe da se��o) pode definir os prazos. A quantidade � livre
	 * Como o empr�stimo institucional o usu�rio n�o pode alterar esse tipo de empr�stimo, 
	 * logo alteravel = false obrigatoriamente.
	 * 
	 * S� deve existir 1 tipo de emprestimos pr�-cadastrado no sistema com essa vari�vel com valor verdadeiro. N�o pode ser alterado pelo usu�rio.
	 */  
	@Column(name="sem_politica_emprestimo", nullable = false)
	private boolean semPoliticaEmprestimo = false;

	/**
	 * ativo
	 */
	private boolean ativo = true;
	
	
	////////////////////////////  Dados para a auditoria ////////////////////
	
	/**
	 * registro entrada do usu�rio que cadastrou
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
	 * registro entrada do usu�rio que realizou a �ltima atualizac�o
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/**
	 * data da �ltima atualizac�o
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;
	
	//////////////////////////////////////////////////////////////////////////
	
	
	/** Transiente que indica se o tipo de empr�stimos foi selecionada ou n�o. */
	@Transient
	private boolean selecionado;
	
	
	/**
	 * Construtor default, para o hibernate e JSF
	 */
	public TipoEmprestimo(){

	}

	/**
	 * construtor com id para quando o objeto j� altera no banco
	 */
	public TipoEmprestimo(int id){
		this.id = id;
	}


	/**
	 * 
	 * Construtor para cadastro
	 * 
	 * @param descricao
	 * @param prazoHoras
	 */
	public TipoEmprestimo(String descricao){
		this.descricao = descricao;
	}

	/**
	 * construtor com id para quando o objeto j� altera no banco
	 */
	public TipoEmprestimo(int id, String descricao){
		this(id);
		this.descricao = descricao;
	}
	
	
	/**
	 * Validar se as vari�veis est�o preenchidas corretamente
	 */
	public ListaMensagens validate() {
		ListaMensagens mensagens = new ListaMensagens();

		if (StringUtils.isEmpty(descricao))
			mensagens.addErro("A descri��o deve ser preenchida.");

		return mensagens;
	}
	
	
	/**
	 * Indica se a biblioteca � a biblioteca central do sistema ou n�o.
	 *
	 * @return
	 * @throws DAOException 
	 */
	public boolean isTipoEmprestimoInstitucional(){
		return isInstitucional();
	}
	
	
	
	/**
	 * Indica se o tipo de empr�stimo � um tipo de empr�stimo especial que n�o possui pol�tica associada,
	 * nesse caso o usu�rio deve informar o quantidade de dias do empr�stimo. 
	 *
	 * @return
	 * @throws DAOException 
	 */
	public boolean isTipoEmprestimoPersonalizavel(){
		return isSemPoliticaEmprestimo();
	}
	
	/**
	 * Compara dois tipos de empr�timos pela descri��o.<br/>
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(TipoEmprestimo otherTypeLoan) {
		if(this.descricao != null && otherTypeLoan.getDescricao() != null)
			return this.descricao.compareTo(otherTypeLoan.getDescricao());
		else
			return 0;
	}
	

	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TipoEmprestimo [descricao=" + descricao + "]";
	}
	
	
	/// sets e gets
	
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

	public boolean isAlteravel() {
		return alteravel;
	}

	public void setSemPoliticaEmprestimo(boolean semPoliticaEmprestimo) {
		this.semPoliticaEmprestimo = semPoliticaEmprestimo;
	}
	
	public boolean isInstitucional() {
		return institucional;
	}

	public boolean isSemPoliticaEmprestimo() {
		return semPoliticaEmprestimo;
	}

	public void setInstitucional(boolean institucional) {
		this.institucional = institucional;
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll("id");
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	public void setAlteravel(boolean alteravel) {
		this.alteravel = alteravel;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}
	
	
}