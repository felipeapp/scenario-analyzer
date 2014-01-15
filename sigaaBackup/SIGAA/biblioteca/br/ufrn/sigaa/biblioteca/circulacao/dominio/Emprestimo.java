/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 *    Entidade que representa os empr�stimos de Materiais Informacionais do sistema de biblioteca.
 * 
 * @author Fred
 * 
 * @see {@link UsuarioBiblioteca}
 * @see {@link MaterialInformacional}
 */
@Entity
@Table(name = "emprestimo", schema = "biblioteca")
public class Emprestimo implements Validatable{

	// Usado em compara��es dentro de  JSPs para saber a situa��o do empr�stimo:
	
	/** Indica um empr�stimo ativo. (O material est� com o usu�rio )*/
	public static final int EMPRESTADO = 1;
	/** Indica um empr�stimo no qual o material j� foi devolvido.  (continua ativo)*/
	public static final int DEVOLVIDO = 2;
	/** Indica um empr�stimo cancelado. (o empr�stimo vai possuir data de estorno e vai est� desativado) */
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
	 * Prazo do empr�stimo, a data at� a qual o usu�rio pode ficar com o material sem ser penalizado
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "prazo")
	private Date prazo;

	/**
	 * A data em que foi feito o empr�stimo
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_emprestimo")
	private Date dataEmprestimo;

	
	/**
	 * A data de devolu��o do material, se for <code>NULL<code>, ou o empr�stimo ainda est� ativo, ou
	 * foi estornado.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_devolucao")
	private Date dataDevolucao;
	
	
	/**
	 * A data do cancelamento do empr�stimo, caso ele seja cancelado. Empr�stimos cancelados n�o aparecem no hist�rico de empr�stimos do usu�rio.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_estorno")
	private Date dataEstorno;
	

	/**  
	 * Informa��o do usu�rio que realizou o empr�stimo.  
	 */
	@ManyToOne(cascade  =  {}, fetch=FetchType.LAZY)
	@JoinColumn(name = "id_usuario_biblioteca", referencedColumnName ="id_usuario_biblioteca")
	private UsuarioBiblioteca usuarioBiblioteca;
	
	/** O material que foi emprestado. */
	@ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST}, fetch=FetchType.LAZY)
	@JoinColumn(name = "id_material", referencedColumnName = "id_material_informacional", nullable = false)
	private MaterialInformacional material;

	
	/**
	 *  Possui a pol�tica de empr�stimo do empr�stimo. Atrav�s dela � calculado os prazos e quantidades de dias que 
	 *  o usu�rio tem direito. 
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_politica_emprestimo", referencedColumnName = "id_politica_emprestimo")
	protected PoliticaEmprestimo politicaEmprestimo;

	
	/** <p>A situa��o do empr�stimo, ver constantes nesta classe. </p> 
	 *  <p>Utilizado para otimizar as consultas, j� que fica mais r�pido comprar inteiros do que data no banco, apesar dessa informa��o est� redumbante,
	 *  � poss�vel obt�-la atraves das data de devolu��o e estorno</p>
	 */
	private int situacao = EMPRESTADO;
	
	
	/** Indica se o empr�stimo est� ativo ou n�o, s� fica ativo = 'false' quando � estornado, mesmo 
	 * devolvido vai ficar ativo 
	 */
	private boolean ativo = true;
	
	
	/** se o empr�stimo foi migrado do sistema antigo ou n�o */
	private String codmerg;
	
//	/**************************************************************************************************
//	 *   <p>Guarda informa��es sobre o v�nculo que o usu�rio utilizou para realizar um empr�stimo</p> 
//	 *   <p>Dependendo da pol�tica em pr�stimo este campo pode cont�m: </p>                         
//	 *   <ul>                                                                                         
//	 *   	<li>idDiscente</li>:                                  
//	 *   	<li>isServidor</li>
//	 *   	<li>idUsuarioExterno</li>
//	 *   	<li>idDocenteExterno</li>
//	 *   	<li>idBiblioteca</li>
//	 *   </ul>
//	 *   <p><strong>Observa��o: </strong><i> Este campo sempre deve conter o v�nculo preferencial
//	 *   que o usu�rio possu�a no momento do empr�stimo.</i> </p>     
//	 *************************************************************************************************/
//	@Column(name = "identificacao_usuario")
//	private Integer identificacaoUsuario;
	
	////////////////////////////INFORMA��ES AUDITORIA  ///////////////////////////////////////

	/**
	 * Guarda o operador que realizou o emprestimo
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_realizou_emprestimo", referencedColumnName ="id_usuario")
	private Usuario usuarioRealizouEmprestimo;

	/**
	 * Guarda o operador que realizou a devolu��o do emprestimo
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_realizou_devolucao", referencedColumnName ="id_usuario")
	private Usuario usuarioRealizouDevolucao;

	/**
	 * Guarda o operador que estornou o empr�stimo
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_realizou_estorno", referencedColumnName ="id_usuario")
	private Usuario usuarioRealizouEstorno;
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	
	/** Usado apenas na view para saber se um empr�stimo foi selecionado ou n�o. */
	@Transient
	private boolean selecionado = false;
	
	/**
	 * Guarda as prorroga��es desse empr�stimo.
	 */
	@Transient
	private List <ProrrogacaoEmprestimo> prorrogacoes = new ArrayList <ProrrogacaoEmprestimo> ();
	
	

	
	/** Guarda a data da �ltima renova��o do empr�stimo. */
	@Transient
	private Date dataRenovacao;
	
	/** Guarda a quantidade de renova��es do empr�stimo. */
	@Transient
	private int quantidadeRenovacoes;
	
	
	/**
	 * Construtor padr�o para empr�stimos.
	 */
	public Emprestimo() {
		super();
	}
	
	
	/**
	 * Cria um empr�stimo j� persistido.
	 */
	public Emprestimo(int id){
		this.id = id;
	}
	
	
	/**
	 * Verifica se um empr�stimo ativo est� atrasado ou n�o.<br>
	 * Se foi devolvido, n�o est� atrasado.
	 * Realiza isso verificado se a data atual j� passou do prazo.
	 */
	public boolean isAtrasado() throws NegocioException{

			if (dataDevolucao != null || dataEstorno != null)
				return false;
		
			// Data atua��o com hora e tudo.  
			Date dataAtual = new Date();

			if (politicaEmprestimo.isPrazoContadoEmDias())
				return CalendarUtils.estorouPrazo(this.prazo, dataAtual);
			else if (politicaEmprestimo.isPrazoContadoEmHoras())
				return CalendarUtils.estorouPrazoConsiderandoHoras(this.prazo, dataAtual);	

			throw new NegocioException("N�o foi poss�vel verificar se o empr�stimo est� atrasado");

	}
	

	/**
	 * Testa se o empr�stimo j� foi finalizado.
	 */
	public boolean isFinalizado(){
		return !isAtivo() || dataDevolucao != null;
	}

	
	
	/**
	 * Indica se o empr�stimo foi finalizado em dia ou n�o.
	 */
	public boolean isFinalizadoComAtraso() throws NegocioException{

		if (dataDevolucao == null)// N�o foi finalizado ainda.
			return false;

		if (politicaEmprestimo.isPrazoContadoEmDias())
			return CalendarUtils.estorouPrazo(this.prazo, dataDevolucao);			
		else if (politicaEmprestimo.isPrazoContadoEmHoras())
			return CalendarUtils.estorouPrazoConsiderandoHoras(this.prazo, dataDevolucao);	

		throw new NegocioException("N�o foi poss�vel verificar se o empr�stimo foi finalizado com atrazo");
	}

	
	
	/**
	 * Adiciona a quantidade de dias passados ao prazo do empr�stimo.
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
	 * Indica se este empr�stimo pode ser renovado.
	 */
	public boolean podeRenovar (){
		if (politicaEmprestimo != null && politicaEmprestimo.getQuantidadeRenovacoes() > quantidadeRenovacoes)
			return true;

		return false;
	}
	
	/** Indica se � renov�vel. (Para acessar das p�gina JSP). */
	public boolean isPodeRenovar (){
		return podeRenovar();
	}
	
	/**
	 *  Valida os dados para salvar novos empr�stimos
	 *  
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		
		if (dataDevolucao.getTime() < dataEmprestimo.getTime()){
			erros.addErro("A Data de Devolu��o Prescisa ser Maior que a Data de Empr�stimo");
		}
		
		if (prazo.getTime() < dataEmprestimo.getTime()){
			erros.addErro("O Prazo para Devolu��o Prescisa ser Maior que a Data de Empr�stimo");
		}
		
		return erros;
	}
	
	/**
	 * Retorna a �ltima data de renova��o. Caso o empr�stimo tenha sido renovado mais de uma vez.
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