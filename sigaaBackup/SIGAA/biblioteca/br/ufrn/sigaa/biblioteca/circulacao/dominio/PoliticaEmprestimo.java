package br.ufrn.sigaa.biblioteca.circulacao.dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;

/**
 *   <p> Define as pol�ticas de empr�stimo do sistema de biblioteca.</p> 
 *   <p> Uma pol�tica de empr�stimo define os <b>prazos</b> e <b>quantidades</b> que um usu�rio ter� direito nos empr�stimos de materiais para as bibliotecas do sistema..</p> 
 *
 *   <p>
 *      As regras da pol�tica de empr�timo devem ser essas:  <br/> <br/>
 *      
 *   	NORMAL  -> REGULAR (qualquer tipo de material )  = quantidade e prazo  <br/>
 *	    ESPECIAL -> REGULAR e ESPECIAL  (qualquer tipo de material )  = quantidade e prazo <br/>
 *      FOTO_C�PIA -> REGULAR e ESPECIAL  (qualquer tipo de material )   = quantidade e prazo <br/>
 *      NOVO_TIPO ->  REGULAR e ESPECIAL (para o tipo de material disco) = quantidade e prazo  (usado apenas na biblioteca de m�sica) <br/>
 *   </p>
 *
 *   <p>
 *   	Ent�o as pol�ticas de empr�stimo v�o ser definidas pela:<br/>
 *   	1� - A Biblioteca <br/>
 *      2� - O V�nculo do Usu�rio <br/>
 *      3� - O Tipo do Empr�stimos <br/><br/>
 *      4� - 0 a N status  <i> ( Caso n�o tenha nenhum status, ent�o a pol�tica serve para material de qualquer status) </i> <br/>
 *      5� - 0 a N tipos de materiais  <i> ( Caso n�o tenha nenhum tipo de material, ent�o a pol�tica serve para material de qualquer tipo de material) </i> <br/>
 *   </p>
 *
 *
 *	<strong>
 *    <p> 
 *    - A entidade de pol�ticas de empr�stimo nunca vai sofrer atualiza��o, sempre que tiver uma atualiza��o 
 * o registro atual dever� ser desativado e um novo criado. <br/>
 *     - Os empr�stimos criados sempre devem apontar para a pol�tica ativa. <br/>
 *     - As pol�ticas v�o ter uma hierarquia dentro do sistema. Se uma biblioteca setorial tiver 
 * uma pol�tica vai usar a sua, se n�o tiver vai usar a pol�tica da biblioteca central. <br/>
 *     - A biblioteca central n�o pode ter pol�ticas apagadas (com valores zerados).  <br/>
 *     - A setorial pode apagar, nesse caso vai usar as pol�tica da central. <br/>
 *     </p> 
 *  </strong>
 *
 * @author Jadson
 * @since 02/06/2009
 * @version 1.0 cria��o da classe
 * @version 2.0 Jadson - 14/02/2013 - Altera��o das regras da pol�tica para atender a "Pol�tica de Empr�stimo do Sistema de Bibliotecas da UFRN" escrita no papel. 
 *      Desde que o sistema entrou em produ��o ele n�o atendia complemente o que estava escrito no papel. A partir de agora a pol�tica vai 
 *      ficar mais flex�vel, permitindo v�rios status e v�rios tipos de material.
 */
@Entity
@Table(name = "politica_emprestimo", schema = "biblioteca")
public class PoliticaEmprestimo implements Validatable{

	/**  O prazo dos empr�stimos dessa pol�tica v�o ser em dias.
	 *   Como vai ser muito dif�cil criar outro tipo de prazo, em minutos ou em meses, 
	 *   esses tipos est�o fixos. 
	 */
	public static final Short TIPO_PRAZO_EM_DIAS = 1;
	
	/** 
	 *   O prazo dos empr�stimos dessa pol�tica v�o ser em horas.
	 *   Como vai ser muito dif�cil criar outro tipo de prazo, em minutos ou em meses, 
	 *   esses tipos est�o fixos. 
	 */
	public static final Short TIPO_PRAZO_EM_HORAS = 2;
	
	
	/**
	 * O id
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column(name = "id_politica_emprestimo", nullable = false)
	private int id;

	
	
	
	
	/* *****************************************************************************************
	 *                   Dados que definem qual pol�tica ser� utilizada                        *
	 * *****************************************************************************************/
	
	
	/** a biblioteca onde a pol�tica est� sendo aplicada */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_biblioteca", nullable = true)
	private Biblioteca biblioteca = new Biblioteca(-1);  
 	
	
	
	/*********************************************************************************************** 
	 *   <p>O tipo do usu�rio no qual a pol�tica est� sendo aplicada.</p>                          *
	 *                                                                                             *
	 *   <p> A partir do tipo do usu�rio da pol�tica � poss�vel determinar qual o v�nculo que o    *
	 * usu�rio utilizou para realizar os empr�stimo. Esta informa��o � utilizada em alguns         * 
	 * relat�rios e para permitir o usu�rio cancelar um v�nculo.</p>                               *
	 *                                                                                             * 
	 *                                                                                             *
	 * <p> Pol�titicas de empr�stimo personalizadas n�o possuem tipo de usu�rio fixo , pois        *
	 * qualquer usu�rio pode utilizar esse tipo de empr�stimo.                                     * 
	 *                                                                                             *
	 ***********************************************************************************************/
	@Enumerated(EnumType.ORDINAL)
	@Column(name="vinculo", nullable=true)
	private VinculoUsuarioBiblioteca vinculo;
	
	
	/**
	 * O tipo de empr�stimo da pol�tica
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_emprestimo", nullable = false)
	private TipoEmprestimo tipoEmprestimo = new TipoEmprestimo(-1); 
	
	
	
	/**
	 * <p>A cole��o de status para os quais essa pol�tica � v�lida.</p>
	 * 
	 * <p>Caso n�o possua nenhum, a pol�tica � v�lida para todos os status para a trinca: 
	 * <b>[biblioteca, v�nculo usu�rio, tipo de empr�stimo] </b>.</p>
	 * 
	 * <p>O sistema n�o deve permitir adicionar o mesmo status para <b>[biblioteca, v�nculo usu�rio, tipo de empr�stimo]</b>. 
	 * Por exemplo, se j� exitir uma pol�tica para um determinado <b>[biblioteca, v�nculo usu�rio, tipo de empr�stimo]</b> com status "REGULAR", 
	 * n�o pode ser cadastrada outra pol�tica para os mesmos <b>[biblioteca, v�nculo usu�rio, tipo de empr�stimo]</b> que contenha o status "REGULAR". <br/>
	 * Se existir uma pol�tica <b>[biblioteca, v�nculo usu�rio, tipo de empr�stimo]</b> com status vazio, ou seja, para todos os status, n�o 
	 * pode ser cadatrado outra para os mesmos <b>[biblioteca, v�nculo usu�rio, tipo de empr�stimo]</b>. </p>
	 * 
	 * <p>Essas regras visam impedir ambiguidade, que para um determinado status exitam duas pol�ticas que possam ser usadas. SEMPRE DEVE EXISTIR S� UMA.</p>
	 * 
	 */
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="biblioteca.politica_emprestimo_status_material", 
			joinColumns={@JoinColumn(name="id_politica_emprestimo")}, inverseJoinColumns={@JoinColumn(name="id_status_material_informacional")})
	private List<StatusMaterialInformacional> statusMateriais;
	
	
	
	/**
	 * <p>A cole��o de tipos de materiais para os quais essa pol�tica � v�lida.</p>
	 * 
	 * <p>Caso n�o possua nenhum, a pol�tica � v�lida para todos os tipo de materiais para a trinca: 
	 * <b>[biblioteca, v�nculo usu�rio, tipo de empr�stimo] </b>.</p>
	 * 
	 * <p>O sistema n�o deve permitir adicionar o mesmo tipo de material para <b>[biblioteca, v�nculo usu�rio, tipo de empr�stimo]</b>. 
	 * Por exemplo, se j� exitir uma pol�tica para um determinado <b>[biblioteca, v�nculo usu�rio, tipo de empr�stimo]</b> com tipo de materia "livro", 
	 * n�o pode ser cadastrada outra pol�tica para os mesmos <b>[biblioteca, v�nculo usu�rio, tipo de empr�stimo]</b> que contenha o tipo de material "livro". <br/>
	 * Se existir uma pol�tica <b>[biblioteca, v�nculo usu�rio, tipo de empr�stimo]</b> com tipo de materiais vazio, ou seja, para todos os tipo de materiais, n�o 
	 * pode ser cadatrado outra para os mesmos <b>[biblioteca, v�nculo usu�rio, tipo de empr�stimo]</b>. </p>
	 * 
	 * <p>Essas regras visam impedir ambiguidade, que para um determinado tipo de material exitam duas pol�ticas que possam ser usadas. SEMPRE DEVE EXISTIR S� UMA.</p>
	 * 
	 */
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="biblioteca.politica_emprestimo_tipo_material", 
			joinColumns={@JoinColumn(name="id_politica_emprestimo")}, inverseJoinColumns={@JoinColumn(name="id_tipo_material")})
	private List<TipoMaterial> tiposMateriais;
	
	
	/* *****************************************************************************************/
	
	
	
	
	
	
	
	/* *****************************************************************************************
	 *                   Dados a serem usados na pol�tica do usu�rio                           *
	 * *****************************************************************************************/
	
	
	
	/** 
	 * <p>  A quantidade de materiais que pode ser levados pelo usu�rio. </p> 
	 * <p> Pol�tica personalizada n�o possui quantidade de materiais, mas que o usu�rio j� tenha a quantiade m�xima pode fazer.</p>
	 */
	@Column(name="quantidade_materiais", nullable = true)
	private Integer quantidadeMateriais;   
	
	
	 /** 
	  * <p>  O prazo do empr�stimo </p> 
	  *  <p> Pol�tica personalizada n�o possui prazo, � definido pelo usu�rio no momento do empr�stimo.</p>
	  */  
	@Column(name="prazo_emprestimo", nullable = true)
	private Integer prazoEmprestimo;  
	
	
	
	
	/** Como vai ser contado o prazo se em dias ou em horas */
	@Column(name="tipo_prazo", nullable = false)
	private Short tipoPrazo = TIPO_PRAZO_EM_DIAS;  
	
	
	/**
	 * Indica a quantidade de vezes que um empr�stimo dessa pol�tica pode ser renovado.
	 */
	@Column(name="quantidade_renovacoes", nullable = false)
	private Integer quantidadeRenovacoes = 0;

	
	
	
	/* *****************************************************************************************/
	
	
	
	
	/** 
	 * <p>Bloqueia a altera��o dos dados da pol�tica pelo usu�rio, s�o pol�tica pre-cadastradas que s�o 
	 * essenciais para o funcionamento do sistema.</p>
	 * 
	 * <p> <strong>Observa��o:</strong> S� existe no sistema uma pol�tica de empr�stios pr�-cadastrada que n�o pode ser alterada, que � a politica 
	 * para empr�stimos do tipo PERSONALIZ�VEL </p>
	 * */
	private boolean alteravel = true;
	
	

	/** 
	 * <p>Indica se esta pol�tica de empr�stimo � para o tipo de empr�tios PERSONALIZ�VEL.  S� deve existir uma pol�tica pre-cadastrada com esse valor verdadeiro no sistema.</p> 
	 *  
	 * <p>IMPORTANTE: A quantidade e o prazo dessa pol�tica s�o nulos, eles s�o definidos pelo usu�rio, com permiss�o para tal, no momento do empr�stios</p>
	 * 
	 * */
	private boolean personalizavel = false;
	
	
	

	/** 
	 * Qualquer altera��o na pol�tica em empr�stimo ele deve ser desativada para o usu�rio utilizar sempre a atualmente virgente.
	 * Ou seja, se o administrador alterar, a politica vai ser desabilitada e criada uma nova com as novas informa��es.
	 * 
	 * Os empr�stimos j� feitos v�o continuar usando as regras anteriores e apenas os novos empr�stimos v�o usar as novas regras. 
	 * Para isso que serve esse campo.
	 */
	@Column(nullable = false)
	private boolean ativo = true;

	
	
	
	//////////////////////////  Informa��es de Auditoria ////////////////////////
	/**
	 * Registro entrada do usu�rio que cadastrou
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/**
	 * Data de cadastro
	 */
	@CriadoEm
	@Column(name="data_cadastro")
	private Date dataCadastro;

	/**
	 * Registro entrada do usu�rio que alterou
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")	
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;
	
	
	/**
	 * Data da �ltima atualiza��o
	 */
	@AtualizadoEm
	@Column(name="data_atualizacao")
	private Date dataAtualizacao;

	//////////////////////////////////////////////////////////////////////////////
	
	/** Guarda temporariamente o valor inteiro que equivale ao V�nculo do usu�rio.
	 * 
	 *  Isso porque nas p�gina os combo box n�o pode atribuir valores � enums.
	 */
	@Transient
	private int valorVinculo = -1;
	
	
	/**
	 * Construtor default
	 */
	public PoliticaEmprestimo(){

	}

	/**
	 * Construtor para tipos que j� estejam persistidos
	 * 
	 * @param id
	 */
	public PoliticaEmprestimo(int id){
		this.id = id;
	}

	/**
	 * Cria uma nova pol�tica de empr�stimo ***ATIVA***.
	 * 
	 * @param biblioteca
	 * @param tipoUsuarioBiblioteca
	 * @param tipoEmprestimo
	 * @param status
	 */
	public PoliticaEmprestimo(Biblioteca biblioteca, VinculoUsuarioBiblioteca vinculo, TipoEmprestimo tipoEmprestimo
			            , Integer quantidadeMateriais, Integer prazoEmprestimo, Short tipoPrazo) {
		this(tipoPrazo);
		this.biblioteca = biblioteca;
		this.vinculo = vinculo;
		this.tipoEmprestimo = tipoEmprestimo;
		this.quantidadeMateriais = quantidadeMateriais;
		this.prazoEmprestimo = prazoEmprestimo;
		
	}
	
	/**
	 * Cria uma nova pol�tica de empr�stimo apenas com o tipo de prazo.
	 * 
	 * @param biblioteca
	 * @param tipoUsuarioBiblioteca
	 * @param tipoEmprestimo
	 * @param status
	 */
	public PoliticaEmprestimo(Short tipoPrazo) {
		this.tipoPrazo = tipoPrazo;
	}
	
	
	/**
	 *   Diz se o prazo da pol�tica � para ser contado em dias
	 */
	public boolean isPrazoContadoEmHoras(){
		return this.tipoPrazo.equals(TIPO_PRAZO_EM_HORAS);
	}
	
	/**
	 *  Dia se o prazo da pol�tica � para ser contado em horas 
	 */
	public boolean isPrazoContadoEmDias(){
		return this.tipoPrazo.equals(TIPO_PRAZO_EM_DIAS);
	}
	
	/**
	 *    Diz se a pol�tica foi apagada pelo usu�rio. Uma pol�tica � apagada quando
	 * o prazo e a quantidade s�o iguais a zero.
	 * 
	 */
	public boolean isPoliticaApagada(){
		if( new Integer(0).equals(this.quantidadeMateriais) && new Integer(0).equals(this.prazoEmprestimo))
			return true;
		else
			return false;
	}
	
	
	/**
	 *   Se o usu�rio preencher a quantidade tem que preencher o prazo e vice-versa.
	 *   Se ele deixar os dois igual a zero significa que a pol�tica vai ser "apagada".
	 */
	public ListaMensagens validate() {
		
		ListaMensagens mensagens = new ListaMensagens();

		if(biblioteca == null || biblioteca.getId() < 0){
			mensagens.addErro("A pol�tica de empr�stimos deve pertencer a uma biblioteca.");
			return mensagens;
		}
		
		if(tipoEmprestimo == null || tipoEmprestimo.getId() < 0){
			mensagens.addErro("A pol�tica de empr�stimos deve possuir um tipo de empr�stimo.");
			return mensagens;
		}
		
		if(vinculo == null || ! vinculo.isPodeRealizarEmprestimos()){
			mensagens.addErro("A pol�tica de empr�stimos deve possuir um v�nculo do usu�rio.");
			return mensagens;
		}
		
		if(quantidadeMateriais == null || quantidadeMateriais.compareTo(new Integer(0)) < 0){
			mensagens.addErro("A quantidade de materiais da pol�tica "+this.getDescricaoPolitica()+" deve ser igual ou maior que zero.");
		}
		
		if(prazoEmprestimo == null  || prazoEmprestimo.compareTo(new Integer(0)) < 0){
			mensagens.addErro("O prazo da pol�tica "+this.getDescricaoPolitica()+" deve ser igual ou maior que zero.");
		}
		
		if(quantidadeRenovacoes == null || quantidadeRenovacoes.compareTo(new Integer(0)) < 0){
			mensagens.addErro("A quantidade renova��es da pol�tica "+this.getDescricaoPolitica()+" deve ser igual ou maior que zero.");
		}
		
		if(! new Short(tipoPrazo).equals(TIPO_PRAZO_EM_DIAS)  &&  ! new Short(tipoPrazo).equals(TIPO_PRAZO_EM_HORAS) ){
			mensagens.addErro("A unidade do prazo da pol�tica: "+this.getDescricaoPolitica()+" n�o � v�lida. ");
		}
		
		return mensagens;
	}
	
	/** 
	 * <p>Uma pol�tica de empr�stimo � igual a outra se for da mesma biblioteca,  mesmo v�nculo do usu�rio, mesmo tipo de empr�stimo.</p>
	 * 
	 * <p> <strong>Observa��o:  </strong> A pol�tica personaliz�vel � fixa no sistema e n�o cont�m status nem v�nculo 
	 * do usu�rio. Qualquer usu�rio pode realiz�-la. Ent�o a compara��o de igualdade � realizada apenas pelo id.</p>
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(this.getId());
	}

	
	/**
	 * <p> Uma pol�tica de empr�stimo � igual a outra pelo id persitido..</p>
	 * 
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	
	/**
	 * Verifica se uma pol�tica � igual a outra pelos dados da pol�tica. Utilizando os daods que identificam a pol�tica  
	 * (biblioteca, vinculo do usu�rio, tipo de empr�stimo, status do material e tipos do material) 
	 * 
	 * N�o considera o id.
	 * @param other
	 * @return
	 *  
	 */
	public boolean equalByDadosPolitica(PoliticaEmprestimo other) {
		
		List<Integer> idsStatusPoliticaThis = new ArrayList<Integer>();
		List<Integer> idsTipoMaterialPoliticaThis = new ArrayList<Integer>();
		
		List<Integer> idsStatusPoliticaOther = new ArrayList<Integer>();
		List<Integer> idsTipoMaterialPoliticaOther = new ArrayList<Integer>();
		
		
		if(other.getStatusMateriais() != null)
		for (StatusMaterialInformacional x : other.getStatusMateriais()) {
			idsStatusPoliticaOther.add(x.getId());
		}
		if(other.getTiposMateriais() != null)
			for (TipoMaterial x :  other.getTiposMateriais()) {
				idsTipoMaterialPoliticaOther.add(x.getId());
		}
		
		if(this.getStatusMateriais() != null)
		for (StatusMaterialInformacional x : this.getStatusMateriais()) {
			idsStatusPoliticaThis.add(x.getId());
		}
		
		if(this.getTiposMateriais() != null)
		for (TipoMaterial x :  this.getTiposMateriais()) {
			idsTipoMaterialPoliticaThis.add(x.getId());
		}
		
		Collections.sort(idsStatusPoliticaOther);
		Collections.sort(idsStatusPoliticaThis);
		Collections.sort(idsTipoMaterialPoliticaOther);
		Collections.sort(idsTipoMaterialPoliticaThis);
		
		if(   this.getBiblioteca().getId() == other.getBiblioteca().getId() 
				&& this.getVinculo().equals(  other.getVinculo() )
				&& this.getTipoEmprestimo().getId() == other.getTipoEmprestimo().getId() 
				&&  idsStatusPoliticaThis.equals(idsStatusPoliticaOther)
				&&  idsTipoMaterialPoliticaThis.equals(idsTipoMaterialPoliticaOther)
				)
				return true;
			else
				return false;
		
	}
	
	/**
	 * Verifica se uma pol�tica � igual a outra pelos dados da pol�tica. N�o considera o id.
	 * Verifica Todos os dados, at� a quantidade de materias, tipo parazo, quantidade de renova��es.
	 * @param obj
	 * @return
	 *  
	 */
	public boolean strictlyEqualByDadosPolitica(PoliticaEmprestimo other) {
		
		if( this.equalByDadosPolitica(other) 
				&&  this.getQuantidadeMateriais().equals( other.getQuantidadeMateriais() )  
				&&  this.getPrazoEmprestimo().equals( other.getPrazoEmprestimo() )
				&&  this.getTipoPrazo().equals( other.getTipoPrazo() )
				&&  this.getQuantidadeRenovacoes().equals(other.getQuantidadeRenovacoes() )  
				&&  ( this.isAtivo() == other.isAtivo() )  )
				return true;
			else
				return false;
		
	}
	
	
	/**
	 * Adiciona um novo status de material a essa pol�tica, caso seja adicionado a pol�tica passa a ser espec�fica para materiais desse status.
	 * @param status
	 */
	public void adicionaStatusMaterial(StatusMaterialInformacional statusMaterial){
		if(statusMateriais == null){
			statusMateriais = new ArrayList<StatusMaterialInformacional>();
		}
		if(! statusMateriais.contains(statusMaterial))
			statusMateriais.add(statusMaterial);
	}
	
	
	/**
	 * Adiciona um novo status de material a essa pol�tica, caso seja adicionado a pol�tica passa a ser espec�fica para materiais desse status.
	 * @param status
	 */
	public void adicionaTipoMaterial(TipoMaterial tipoMaterial){
		if(tiposMateriais == null){
			tiposMateriais = new ArrayList<TipoMaterial>();
		}
		if(! tiposMateriais.contains(tipoMaterial))
			tiposMateriais.add(tipoMaterial);
	}
	
	/** Retorna a quantidade de tipos de materiais associados � pol�tica */
	public int getQuantidadeStatusMateriais(){
		if(statusMateriais == null){
			return 0;
		}
		
		return statusMateriais.size();
	}
	
	/** Retorna a quantidade de tipos de materiais associados � pol�tica */
	public int getQuantidadeTiposMateriais(){
		if(tiposMateriais == null){
			return 0;
		}
		
		return tiposMateriais.size();
	}
	
	
	
	/**  
	 * <p>Retorna os status associados com a pol�tica separados por v�rgual:  ESPECIAL, NORMAL, N�O CIRCULA, ETC...</p>
	 * 
	 *  <p>Usado na visualiza��o para o usu�rio.</p>
	 */
	public String getStatusMateriaisFormatados(){
		if(statusMateriais == null){
			return "TODOS";
		}
		List<String> descricoes = new ArrayList<String>();
		
		for (StatusMaterialInformacional status : statusMateriais) {
			descricoes.add(status.getDescricao());
		}
		
		return descricoes.toString();
	}
	
	/** 
	 * <p>Retorna os tipos de materias associados com a pol�tica separados por v�rgual:  LIVRO, DVD, CD, ETC ...</p>
	 * 
	 * <p>Usado na visualiza��o para o usu�rio.</p>
	 * 
	 */
	public String getTiposMateriaisFormatados(){
		if(tiposMateriais == null){
			return "TODOS";
		}
		
		List<String> descricoes = new ArrayList<String>();
		
		for (TipoMaterial tipo : tiposMateriais) {
			descricoes.add(tipo.getDescricao());
		}
		
		return descricoes.toString();
	}
	
	
	
	/**
	 *   Retorna a descri��o do tipo de prazo para mostrar nas p�ginas.
	 * 
	 * @return
	 */
	public String getDescricaoTipoPrazo(){
		
		if(tipoPrazo == null)
			return "DIAS";
		
		if(tipoPrazo.equals(TIPO_PRAZO_EM_DIAS))
			return "DIAS";
		
		if(tipoPrazo.equals(TIPO_PRAZO_EM_HORAS) )
			return "HORAS";
			
		return "";
	}
	
	
	/**
	 *   Retorna a descri��o da pol�tica para o usu�rio
	 * 
	 * @return
	 */
	public String getDescricaoPolitica(){
		return " ( "+biblioteca.getDescricao()+" ; "+vinculo.getDescricao()+" ; "+tipoEmprestimo.getDescricao()+" )";
	}
	
	
	// sets e gets

	@Override
	public String toString() {
		if(! isPersonalizavel() && biblioteca != null && vinculo != null && tipoEmprestimo != null ){
			return "["+ biblioteca.getIdentificador() +" - "+ vinculo.getDescricao() +" - "
					+ " Tipo Empr�stimo: "+tipoEmprestimo.getDescricao() 
					+ " Materiais = " + quantidadeMateriais+","
					+ " Prazo = " + prazoEmprestimo + " "+ getDescricaoTipoPrazo()+","
					+ " Renova��es = " + quantidadeRenovacoes+".";
		}else{
			if( biblioteca != null  && tipoEmprestimo != null )
				return "["+ biblioteca.getIdentificador() +" -  Tipo: "+ tipoEmprestimo.getDescricao() +"] ";
			else
				return "";
		}
	}

	/**
	 * Retorna as informa��es da pot�tica para mostrar ao usu�rio no momento do empr�stimo.
	 *
	 * @return
	 */
	public String getInformacoesPolitica() {
		
		StringBuilder retorno = new StringBuilder();
		if(tipoEmprestimo != null ){
			retorno.append("["+"Tipo de Empr�stimo: "+tipoEmprestimo.getDescricao());
			
			if( ! isPersonalizavel()  ){
				retorno.append(" ; Status: ");
				
				if(statusMateriais != null  && statusMateriais.size() > 0){
					
					if(statusMateriais.size() > 2){
						retorno.append(statusMateriais.get(0).getDescricao()+" e outros... "); 
					}else{
					
						for (StatusMaterialInformacional status : statusMateriais) {
							retorno.append(status.getDescricao()+", "); 
						}
					
						if ( retorno.charAt(retorno.length()-2) == ',' )
							retorno.deleteCharAt(retorno.length()-2) ;
					}
				}else{
					retorno.append("TODOS");
				}
				
				retorno.append(" ; Tipo de Material: ");
				
				if(tiposMateriais != null && tiposMateriais.size() > 0){
					
					if(tiposMateriais.size() > 2){
						retorno.append(tiposMateriais.get(0).getDescricao()+" e outros... "); 
					}else{
					
						for (TipoMaterial tipo : tiposMateriais) {
							retorno.append(tipo.getDescricao()+", "); 
						}
						
						if ( retorno.charAt(retorno.length()-2) == ',' )
							retorno.deleteCharAt(retorno.length()-2) ;
					}
					
				}else{
					retorno.append("TODOS");
				}
			}
			
			retorno.append( "] ");
		}
		
		
		
		if( ! isPersonalizavel()  ){
			
			retorno.append(
					  " Materiais = " + quantidadeMateriais+","
					+ " Prazo = " + prazoEmprestimo + " "+ getDescricaoTipoPrazo()+","
					+ " Renova��es = " + quantidadeRenovacoes+".");
		}
		
		return retorno.toString();
	}
	
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}

	public TipoEmprestimo getTipoEmprestimo() {
		return tipoEmprestimo;
	}

	public VinculoUsuarioBiblioteca getVinculo() {
		return vinculo;
	}

	public void setVinculo(VinculoUsuarioBiblioteca vinculo) {
		this.vinculo = vinculo;
	}

	public void setTipoEmprestimo(TipoEmprestimo tipoEmprestimo) {
		this.tipoEmprestimo = tipoEmprestimo;
	}

	public Integer getQuantidadeMateriais() {
		return quantidadeMateriais;
	}

	public void setQuantidadeMateriais(Integer quantidadeMateriais) {
		this.quantidadeMateriais = quantidadeMateriais;
	}

	public Integer getPrazoEmprestimo() {
		return prazoEmprestimo;
	}

	public void setPrazoEmprestimo(Integer prazoEmprestimo) {
		this.prazoEmprestimo = prazoEmprestimo;
	}

	public Short getTipoPrazo() {
		return tipoPrazo;
	}

	public void setTipoPrazo(Short tipoPrazo) {
		this.tipoPrazo = tipoPrazo;
	}

	public Integer getQuantidadeRenovacoes() {
		return quantidadeRenovacoes;
	}

	public void setQuantidadeRenovacoes(Integer quantidadeRenovacoes) {
		this.quantidadeRenovacoes = quantidadeRenovacoes;
	}

	public boolean isAlteravel() {
		return alteravel;
	}

	public void setAlteravel(boolean alteravel) {
		this.alteravel = alteravel;
	}

	public boolean isPersonalizavel() {
		return personalizavel;
	}

	public void setPersonalizavel(boolean personalizavel) {
		this.personalizavel = personalizavel;
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

	public List<StatusMaterialInformacional> getStatusMateriais() {
		return statusMateriais;
	}

	public void setStatusMateriais(List<StatusMaterialInformacional> statusMateriais) {
		this.statusMateriais = statusMateriais;
	}

	public List<TipoMaterial> getTiposMateriais() {
		return tiposMateriais;
	}

	public void setTiposMateriais(List<TipoMaterial> tiposMateriais) {
		this.tiposMateriais = tiposMateriais;
	}

	public int getValorVinculo() {
		return valorVinculo;
	}

	public void setValorVinculo(int valorVinculo) {
		this.valorVinculo = valorVinculo;
	}
	
	
	
}
