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
 * Entidade que define tipos de empréstimos
 * 
 * Tipos existentes quando a classe foi criada: Normal ou Fotocópia, Institucional, Prazo Configurável
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
	 * descrição
	 */
	@Column(name = "descricao")
	private String descricao;
	
	
	/** Variável que diz ao sistema se o usuário pode alterar esse tipo de empréstimo.
	 *  Serve para o usuário não conseguir alterar tipos de empréstimos pre-cadastrados que são essenciais para o funcionamento do sistema.
	 *  
	 *  Empréstimo INSTITUCIONAL e PERSONALIZADO são os tipos de empréstimo que existem no sistema mais o usuário não pode alterá-lo.
	 */
	private boolean alteravel = true;

	
	/** Empréstimo especial utilizando para realizar empréstimos entre bibliotecas.  Não pode ser alterado pelo usuário.
	 * 
	 * Só deve existir 1 tipo de emprestimos pré-cadastrado no sistema com essa variável com valor verdadeiro
	 */  
	@Column(name="institucional", nullable = false)
	private boolean institucional = false;
	
	
	/** Empréstimo especial no qual o usuário ( chefe da seção) pode definir os prazos. A quantidade é livre
	 * Como o empréstimo institucional o usuário não pode alterar esse tipo de empréstimo, 
	 * logo alteravel = false obrigatoriamente.
	 * 
	 * Só deve existir 1 tipo de emprestimos pré-cadastrado no sistema com essa variável com valor verdadeiro. Não pode ser alterado pelo usuário.
	 */  
	@Column(name="sem_politica_emprestimo", nullable = false)
	private boolean semPoliticaEmprestimo = false;

	/**
	 * ativo
	 */
	private boolean ativo = true;
	
	
	////////////////////////////  Dados para a auditoria ////////////////////
	
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
	 * registro entrada do usuário que realizou a última atualizacão
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/**
	 * data da última atualizacão
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;
	
	//////////////////////////////////////////////////////////////////////////
	
	
	/** Transiente que indica se o tipo de empréstimos foi selecionada ou não. */
	@Transient
	private boolean selecionado;
	
	
	/**
	 * Construtor default, para o hibernate e JSF
	 */
	public TipoEmprestimo(){

	}

	/**
	 * construtor com id para quando o objeto já altera no banco
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
	 * construtor com id para quando o objeto já altera no banco
	 */
	public TipoEmprestimo(int id, String descricao){
		this(id);
		this.descricao = descricao;
	}
	
	
	/**
	 * Validar se as variáveis estão preenchidas corretamente
	 */
	public ListaMensagens validate() {
		ListaMensagens mensagens = new ListaMensagens();

		if (StringUtils.isEmpty(descricao))
			mensagens.addErro("A descrição deve ser preenchida.");

		return mensagens;
	}
	
	
	/**
	 * Indica se a biblioteca é a biblioteca central do sistema ou não.
	 *
	 * @return
	 * @throws DAOException 
	 */
	public boolean isTipoEmprestimoInstitucional(){
		return isInstitucional();
	}
	
	
	
	/**
	 * Indica se o tipo de empréstimo é um tipo de empréstimo especial que não possui política associada,
	 * nesse caso o usuário deve informar o quantidade de dias do empréstimo. 
	 *
	 * @return
	 * @throws DAOException 
	 */
	public boolean isTipoEmprestimoPersonalizavel(){
		return isSemPoliticaEmprestimo();
	}
	
	/**
	 * Compara dois tipos de emprétimos pela descrição.<br/>
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
	 * Ver comentários da classe pai.<br/>
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