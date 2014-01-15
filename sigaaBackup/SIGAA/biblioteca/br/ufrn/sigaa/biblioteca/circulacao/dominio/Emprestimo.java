/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em 16/09/2008
 */

package br.ufrn.sigaa.biblioteca.circulacao.dominio;

import java.util.ArrayList;
import java.util.Calendar;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.dominio.Usuario;

/**
 *    Entidade que representa os empréstimos de Materiais Informacionais do sistema de biblioteca.
 * 
 * @author Fred
 * 
 * @see {@link UsuarioBiblioteca}
 * @see {@link MaterialInformacional}
 */
@Entity
@Table(name = "emprestimo", schema = "biblioteca")
public class Emprestimo implements Validatable{

	// Usado em comparações dentro de  JSPs para saber a situação do empréstimo:
	
	/** Indica um empréstimo ativo. (O material está com o usuário )*/
	public static final int EMPRESTADO = 1;
	/** Indica um empréstimo no qual o material já foi devolvido.  (continua ativo)*/
	public static final int DEVOLVIDO = 2;
	/** Indica um empréstimo cancelado. (o empréstimo vai possuir data de estorno e vai está desativado) */
	public static final int CANCELADO = 3;
	
	/**
	 * O id
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.emprestimo_sequence") })
	@Column(name = "id_emprestimo", nullable = false)
	private int id;

	/**
	 * Prazo do empréstimo, a data até a qual o usuário pode ficar com o material sem ser penalizado
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "prazo")
	private Date prazo;

	/**
	 * A data em que foi feito o empréstimo
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_emprestimo")
	private Date dataEmprestimo;

	
	/**
	 * A data de devolução do material, se for <code>NULL<code>, ou o empréstimo ainda está ativo, ou
	 * foi estornado.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_devolucao")
	private Date dataDevolucao;
	
	
	/**
	 * A data do cancelamento do empréstimo, caso ele seja cancelado. Empréstimos cancelados não aparecem no histórico de empréstimos do usuário.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_estorno")
	private Date dataEstorno;
	

	/**  
	 * Informação do usuário que realizou o empréstimo.  
	 */
	@ManyToOne(cascade  =  {}, fetch=FetchType.LAZY)
	@JoinColumn(name = "id_usuario_biblioteca", referencedColumnName ="id_usuario_biblioteca")
	private UsuarioBiblioteca usuarioBiblioteca;
	
	/** O material que foi emprestado. */
	@ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST}, fetch=FetchType.LAZY)
	@JoinColumn(name = "id_material", referencedColumnName = "id_material_informacional", nullable = false)
	private MaterialInformacional material;

	
	/**
	 *  Possui a política de empréstimo do empréstimo. Através dela é calculado os prazos e quantidades de dias que 
	 *  o usuário tem direito. 
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_politica_emprestimo", referencedColumnName = "id_politica_emprestimo")
	protected PoliticaEmprestimo politicaEmprestimo;

	
	/** <p>A situação do empréstimo, ver constantes nesta classe. </p> 
	 *  <p>Utilizado para otimizar as consultas, já que fica mais rápido comprar inteiros do que data no banco, apesar dessa informação está redumbante,
	 *  é possível obtê-la atraves das data de devolução e estorno</p>
	 */
	private int situacao = EMPRESTADO;
	
	
	/** Indica se o empréstimo está ativo ou não, só fica ativo = 'false' quando é estornado, mesmo 
	 * devolvido vai ficar ativo 
	 */
	private boolean ativo = true;
	
	
	/** se o empréstimo foi migrado do sistema antigo ou não */
	private String codmerg;
	
//	/**************************************************************************************************
//	 *   <p>Guarda informações sobre o vínculo que o usuário utilizou para realizar um empréstimo</p> 
//	 *   <p>Dependendo da política em préstimo este campo pode contém: </p>                         
//	 *   <ul>                                                                                         
//	 *   	<li>idDiscente</li>:                                  
//	 *   	<li>isServidor</li>
//	 *   	<li>idUsuarioExterno</li>
//	 *   	<li>idDocenteExterno</li>
//	 *   	<li>idBiblioteca</li>
//	 *   </ul>
//	 *   <p><strong>Observação: </strong><i> Este campo sempre deve conter o vínculo preferencial
//	 *   que o usuário possuía no momento do empréstimo.</i> </p>     
//	 *************************************************************************************************/
//	@Column(name = "identificacao_usuario")
//	private Integer identificacaoUsuario;
	
	////////////////////////////INFORMAÇÕES AUDITORIA  ///////////////////////////////////////

	/**
	 * Guarda o operador que realizou o emprestimo
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_realizou_emprestimo", referencedColumnName ="id_usuario")
	private Usuario usuarioRealizouEmprestimo;

	/**
	 * Guarda o operador que realizou a devolução do emprestimo
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_realizou_devolucao", referencedColumnName ="id_usuario")
	private Usuario usuarioRealizouDevolucao;

	/**
	 * Guarda o operador que estornou o empréstimo
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_realizou_estorno", referencedColumnName ="id_usuario")
	private Usuario usuarioRealizouEstorno;
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	
	/** Usado apenas na view para saber se um empréstimo foi selecionado ou não. */
	@Transient
	private boolean selecionado = false;
	
	/**
	 * Guarda as prorrogações desse empréstimo.
	 */
	@Transient
	private List <ProrrogacaoEmprestimo> prorrogacoes = new ArrayList <ProrrogacaoEmprestimo> ();
	
	

	
	/** Guarda a data da última renovação do empréstimo. */
	@Transient
	private Date dataRenovacao;
	
	/** Guarda a quantidade de renovações do empréstimo. */
	@Transient
	private int quantidadeRenovacoes;
	
	
	/**
	 * Construtor padrão para empréstimos.
	 */
	public Emprestimo() {
		super();
	}
	
	
	/**
	 * Cria um empréstimo já persistido.
	 */
	public Emprestimo(int id){
		this.id = id;
	}
	
	
	/**
	 * Verifica se um empréstimo ativo está atrasado ou não.<br>
	 * Se foi devolvido, não está atrasado.
	 * Realiza isso verificado se a data atual já passou do prazo.
	 */
	public boolean isAtrasado() throws NegocioException{

			if (dataDevolucao != null || dataEstorno != null)
				return false;
		
			// Data atuação com hora e tudo.  
			Date dataAtual = new Date();

			if (politicaEmprestimo.isPrazoContadoEmDias())
				return CalendarUtils.estorouPrazo(this.prazo, dataAtual);
			else if (politicaEmprestimo.isPrazoContadoEmHoras())
				return CalendarUtils.estorouPrazoConsiderandoHoras(this.prazo, dataAtual);	

			throw new NegocioException("Não foi possível verificar se o empréstimo está atrasado");

	}
	

	/**
	 * Testa se o empréstimo já foi finalizado.
	 */
	public boolean isFinalizado(){
		return !isAtivo() || dataDevolucao != null;
	}

	
	
	/**
	 * Indica se o empréstimo foi finalizado em dia ou não.
	 */
	public boolean isFinalizadoComAtraso() throws NegocioException{

		if (dataDevolucao == null)// Não foi finalizado ainda.
			return false;

		if (politicaEmprestimo.isPrazoContadoEmDias())
			return CalendarUtils.estorouPrazo(this.prazo, dataDevolucao);			
		else if (politicaEmprestimo.isPrazoContadoEmHoras())
			return CalendarUtils.estorouPrazoConsiderandoHoras(this.prazo, dataDevolucao);	

		throw new NegocioException("Não foi possível verificar se o empréstimo foi finalizado com atrazo");
	}

	
	
	/**
	 * Adiciona a quantidade de dias passados ao prazo do empréstimo.
	 */
	public void addDiasAoPrazo(int quantidadeDias){
		if(quantidadeDias > 0){

			Calendar calendar = Calendar.getInstance();  
			calendar.setTime(this.getPrazo());

			calendar.add(Calendar.DAY_OF_MONTH, quantidadeDias);  

			CalendarUtils.adicionaDiasFimDeSemana(calendar);

			this.prazo = calendar.getTime();
		}
	}
	
	/**
	 * Indica se este empréstimo pode ser renovado.
	 */
	public boolean podeRenovar (){
		if (politicaEmprestimo != null && politicaEmprestimo.getQuantidadeRenovacoes() > quantidadeRenovacoes)
			return true;

		return false;
	}
	
	/** Indica se é renovável. (Para acessar das página JSP). */
	public boolean isPodeRenovar (){
		return podeRenovar();
	}
	
	/**
	 *  Valida os dados para salvar novos empréstimos
	 *  
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		
		if (dataDevolucao.getTime() < dataEmprestimo.getTime()){
			erros.addErro("A Data de Devolução Prescisa ser Maior que a Data de Empréstimo");
		}
		
		if (prazo.getTime() < dataEmprestimo.getTime()){
			erros.addErro("O Prazo para Devolução Prescisa ser Maior que a Data de Empréstimo");
		}
		
		return erros;
	}
	
	/**
	 * Retorna a última data de renovação. Caso o empréstimo tenha sido renovado mais de uma vez.
	 */
	public Date getDataRenovacao (){
		return dataRenovacao;
	}

	public void setDataRenovacao(Date dataRenovacao){
		this.dataRenovacao = dataRenovacao;
	}

	

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(this.getId());
	}


	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	
	
	// sets e gets 


	@Override
	public int getId() {
		return id;
	}
	@Override
	public void setId(int id) {
		this.id = id;
	}

	public Usuario getUsuarioRealizouEmprestimo() {
		return usuarioRealizouEmprestimo;
	}

	public void setUsuarioRealizouEmprestimo(Usuario usuarioRealizouEmprestimo) {
		this.usuarioRealizouEmprestimo = usuarioRealizouEmprestimo;
	}

	public Usuario getUsuarioRealizouDevolucao() {
		return usuarioRealizouDevolucao;
	}

	public void setUsuarioRealizouDevolucao(Usuario usuarioRealizouDevolucao) {
		this.usuarioRealizouDevolucao = usuarioRealizouDevolucao;
	}

	public Date getDataEmprestimo() {
		return dataEmprestimo;
	}
	
	public void setDataEmprestimo(Date dataEmprestimo) {
		this.dataEmprestimo = dataEmprestimo;
	}
	
	public Date getPrazo() {
		return prazo;
	}
	
	public void setPrazo(Date prazo) {
		this.prazo = prazo;
	}

	public boolean isAtivo() {
		return ativo;
	}
	
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Date getDataDevolucao() {
		return dataDevolucao;
	}
	public void setDataDevolucao(Date dataDevolucao) {
		this.dataDevolucao = dataDevolucao;
	}

	public boolean getSelecionado(){
		return selecionado;
	}

	public void setSelecionado(boolean selecionado){
		this.selecionado = selecionado;
	}
	public MaterialInformacional getMaterial() {
		return material;
	}

	public void setMaterial(MaterialInformacional material) {
		this.material = material;
	}
	
	public Usuario getUsuarioRealizouEstorno() {
		return usuarioRealizouEstorno;
	}

	public void setUsuarioRealizouEstorno(Usuario usuarioRealizouEstorno) {
		this.usuarioRealizouEstorno = usuarioRealizouEstorno;
	}

	public Date getDataEstorno() {
		return dataEstorno;
	}

	public void setDataEstorno(Date dataEstorno) {
		this.dataEstorno = dataEstorno;
	}

	public UsuarioBiblioteca getUsuarioBiblioteca() {
		return usuarioBiblioteca;
	}

	public void setUsuarioBiblioteca(UsuarioBiblioteca usuarioBiblioteca) {
		this.usuarioBiblioteca = usuarioBiblioteca;
	}

	public PoliticaEmprestimo getPoliticaEmprestimo() {
		return politicaEmprestimo;
	}

	public void setPoliticaEmprestimo(PoliticaEmprestimo politicaEmprestimo) {
		this.politicaEmprestimo = politicaEmprestimo;
	}

	public int getSituacao() {
		return situacao;
	}

	public void setSituacao(int situacao) {
		this.situacao = situacao;
	}

	public String getCodmerg() {
		return codmerg;
	}

	public void setCodmerg(String codmerg) {
		this.codmerg = codmerg;
	}

	public int getQuantidadeRenovacoes() {
		return quantidadeRenovacoes;
	}

	public void setQuantidadeRenovacoes(int quantidadeRenovacoes) {
		this.quantidadeRenovacoes = quantidadeRenovacoes;
	}

	public List<ProrrogacaoEmprestimo> getProrrogacoes() {
		return prorrogacoes;
	}

	public void setProrrogacoes(List<ProrrogacaoEmprestimo> prorrogacoes) {
		this.prorrogacoes = prorrogacoes;
	}
	
//	public Integer getIdentificacaoUsuario() {
//		return identificacaoUsuario;
//	}
//
//	public void setIdentificacaoUsuario(Integer identificacaoUsuario) {
//		this.identificacaoUsuario = identificacaoUsuario;
//	}
	
}