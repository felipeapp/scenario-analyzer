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
 *   <p> Define as políticas de empréstimo do sistema de biblioteca.</p> 
 *   <p> Uma política de empréstimo define os <b>prazos</b> e <b>quantidades</b> que um usuário terá direito nos empréstimos de materiais para as bibliotecas do sistema..</p> 
 *
 *   <p>
 *      As regras da política de emprétimo devem ser essas:  <br/> <br/>
 *      
 *   	NORMAL  -> REGULAR (qualquer tipo de material )  = quantidade e prazo  <br/>
 *	    ESPECIAL -> REGULAR e ESPECIAL  (qualquer tipo de material )  = quantidade e prazo <br/>
 *      FOTO_CÓPIA -> REGULAR e ESPECIAL  (qualquer tipo de material )   = quantidade e prazo <br/>
 *      NOVO_TIPO ->  REGULAR e ESPECIAL (para o tipo de material disco) = quantidade e prazo  (usado apenas na biblioteca de música) <br/>
 *   </p>
 *
 *   <p>
 *   	Então as políticas de empréstimo vão ser definidas pela:<br/>
 *   	1º - A Biblioteca <br/>
 *      2º - O Vínculo do Usuário <br/>
 *      3º - O Tipo do Empréstimos <br/><br/>
 *      4º - 0 a N status  <i> ( Caso não tenha nenhum status, então a política serve para material de qualquer status) </i> <br/>
 *      5º - 0 a N tipos de materiais  <i> ( Caso não tenha nenhum tipo de material, então a política serve para material de qualquer tipo de material) </i> <br/>
 *   </p>
 *
 *
 *	<strong>
 *    <p> 
 *    - A entidade de políticas de empréstimo nunca vai sofrer atualização, sempre que tiver uma atualização 
 * o registro atual deverá ser desativado e um novo criado. <br/>
 *     - Os empréstimos criados sempre devem apontar para a política ativa. <br/>
 *     - As políticas vão ter uma hierarquia dentro do sistema. Se uma biblioteca setorial tiver 
 * uma política vai usar a sua, se não tiver vai usar a política da biblioteca central. <br/>
 *     - A biblioteca central não pode ter políticas apagadas (com valores zerados).  <br/>
 *     - A setorial pode apagar, nesse caso vai usar as política da central. <br/>
 *     </p> 
 *  </strong>
 *
 * @author Jadson
 * @since 02/06/2009
 * @version 1.0 criação da classe
 * @version 2.0 Jadson - 14/02/2013 - Alteração das regras da política para atender a "Política de Empréstimo do Sistema de Bibliotecas da UFRN" escrita no papel. 
 *      Desde que o sistema entrou em produção ele não atendia complemente o que estava escrito no papel. A partir de agora a política vai 
 *      ficar mais flexível, permitindo vários status e vários tipos de material.
 */
@Entity
@Table(name = "politica_emprestimo", schema = "biblioteca")
public class PoliticaEmprestimo implements Validatable{

	/**  O prazo dos empréstimos dessa política vão ser em dias.
	 *   Como vai ser muito difícil criar outro tipo de prazo, em minutos ou em meses, 
	 *   esses tipos estão fixos. 
	 */
	public static final Short TIPO_PRAZO_EM_DIAS = 1;
	
	/** 
	 *   O prazo dos empréstimos dessa política vão ser em horas.
	 *   Como vai ser muito difícil criar outro tipo de prazo, em minutos ou em meses, 
	 *   esses tipos estão fixos. 
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
	 *                   Dados que definem qual política será utilizada                        *
	 * *****************************************************************************************/
	
	
	/** a biblioteca onde a política está sendo aplicada */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_biblioteca", nullable = true)
	private Biblioteca biblioteca = new Biblioteca(-1);  
 	
	
	
	/*********************************************************************************************** 
	 *   <p>O tipo do usuário no qual a política está sendo aplicada.</p>                          *
	 *                                                                                             *
	 *   <p> A partir do tipo do usuário da política é possível determinar qual o vínculo que o    *
	 * usuário utilizou para realizar os empréstimo. Esta informação é utilizada em alguns         * 
	 * relatórios e para permitir o usuário cancelar um vínculo.</p>                               *
	 *                                                                                             * 
	 *                                                                                             *
	 * <p> Polítiticas de empréstimo personalizadas não possuem tipo de usuário fixo , pois        *
	 * qualquer usuário pode utilizar esse tipo de empréstimo.                                     * 
	 *                                                                                             *
	 ***********************************************************************************************/
	@Enumerated(EnumType.ORDINAL)
	@Column(name="vinculo", nullable=true)
	private VinculoUsuarioBiblioteca vinculo;
	
	
	/**
	 * O tipo de empréstimo da política
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_emprestimo", nullable = false)
	private TipoEmprestimo tipoEmprestimo = new TipoEmprestimo(-1); 
	
	
	
	/**
	 * <p>A coleção de status para os quais essa política é válida.</p>
	 * 
	 * <p>Caso não possua nenhum, a política é válida para todos os status para a trinca: 
	 * <b>[biblioteca, vínculo usuário, tipo de empréstimo] </b>.</p>
	 * 
	 * <p>O sistema não deve permitir adicionar o mesmo status para <b>[biblioteca, vínculo usuário, tipo de empréstimo]</b>. 
	 * Por exemplo, se já exitir uma política para um determinado <b>[biblioteca, vínculo usuário, tipo de empréstimo]</b> com status "REGULAR", 
	 * não pode ser cadastrada outra política para os mesmos <b>[biblioteca, vínculo usuário, tipo de empréstimo]</b> que contenha o status "REGULAR". <br/>
	 * Se existir uma política <b>[biblioteca, vínculo usuário, tipo de empréstimo]</b> com status vazio, ou seja, para todos os status, não 
	 * pode ser cadatrado outra para os mesmos <b>[biblioteca, vínculo usuário, tipo de empréstimo]</b>. </p>
	 * 
	 * <p>Essas regras visam impedir ambiguidade, que para um determinado status exitam duas políticas que possam ser usadas. SEMPRE DEVE EXISTIR SÓ UMA.</p>
	 * 
	 */
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="biblioteca.politica_emprestimo_status_material", 
			joinColumns={@JoinColumn(name="id_politica_emprestimo")}, inverseJoinColumns={@JoinColumn(name="id_status_material_informacional")})
	private List<StatusMaterialInformacional> statusMateriais;
	
	
	
	/**
	 * <p>A coleção de tipos de materiais para os quais essa política é válida.</p>
	 * 
	 * <p>Caso não possua nenhum, a política é válida para todos os tipo de materiais para a trinca: 
	 * <b>[biblioteca, vínculo usuário, tipo de empréstimo] </b>.</p>
	 * 
	 * <p>O sistema não deve permitir adicionar o mesmo tipo de material para <b>[biblioteca, vínculo usuário, tipo de empréstimo]</b>. 
	 * Por exemplo, se já exitir uma política para um determinado <b>[biblioteca, vínculo usuário, tipo de empréstimo]</b> com tipo de materia "livro", 
	 * não pode ser cadastrada outra política para os mesmos <b>[biblioteca, vínculo usuário, tipo de empréstimo]</b> que contenha o tipo de material "livro". <br/>
	 * Se existir uma política <b>[biblioteca, vínculo usuário, tipo de empréstimo]</b> com tipo de materiais vazio, ou seja, para todos os tipo de materiais, não 
	 * pode ser cadatrado outra para os mesmos <b>[biblioteca, vínculo usuário, tipo de empréstimo]</b>. </p>
	 * 
	 * <p>Essas regras visam impedir ambiguidade, que para um determinado tipo de material exitam duas políticas que possam ser usadas. SEMPRE DEVE EXISTIR SÓ UMA.</p>
	 * 
	 */
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="biblioteca.politica_emprestimo_tipo_material", 
			joinColumns={@JoinColumn(name="id_politica_emprestimo")}, inverseJoinColumns={@JoinColumn(name="id_tipo_material")})
	private List<TipoMaterial> tiposMateriais;
	
	
	/* *****************************************************************************************/
	
	
	
	
	
	
	
	/* *****************************************************************************************
	 *                   Dados a serem usados na política do usuário                           *
	 * *****************************************************************************************/
	
	
	
	/** 
	 * <p>  A quantidade de materiais que pode ser levados pelo usuário. </p> 
	 * <p> Política personalizada não possui quantidade de materiais, mas que o usuário já tenha a quantiade máxima pode fazer.</p>
	 */
	@Column(name="quantidade_materiais", nullable = true)
	private Integer quantidadeMateriais;   
	
	
	 /** 
	  * <p>  O prazo do empréstimo </p> 
	  *  <p> Política personalizada não possui prazo, é definido pelo usuário no momento do empréstimo.</p>
	  */  
	@Column(name="prazo_emprestimo", nullable = true)
	private Integer prazoEmprestimo;  
	
	
	
	
	/** Como vai ser contado o prazo se em dias ou em horas */
	@Column(name="tipo_prazo", nullable = false)
	private Short tipoPrazo = TIPO_PRAZO_EM_DIAS;  
	
	
	/**
	 * Indica a quantidade de vezes que um empréstimo dessa política pode ser renovado.
	 */
	@Column(name="quantidade_renovacoes", nullable = false)
	private Integer quantidadeRenovacoes = 0;

	
	
	
	/* *****************************************************************************************/
	
	
	
	
	/** 
	 * <p>Bloqueia a alteração dos dados da política pelo usuário, sáo política pre-cadastradas que são 
	 * essenciais para o funcionamento do sistema.</p>
	 * 
	 * <p> <strong>Observação:</strong> Só existe no sistema uma política de empréstios pré-cadastrada que não pode ser alterada, que é a politica 
	 * para empréstimos do tipo PERSONALIZÁVEL </p>
	 * */
	private boolean alteravel = true;
	
	

	/** 
	 * <p>Indica se esta política de empréstimo é para o tipo de emprétios PERSONALIZÁVEL.  Só deve existir uma política pre-cadastrada com esse valor verdadeiro no sistema.</p> 
	 *  
	 * <p>IMPORTANTE: A quantidade e o prazo dessa política são nulos, eles são definidos pelo usuário, com permissão para tal, no momento do empréstios</p>
	 * 
	 * */
	private boolean personalizavel = false;
	
	
	

	/** 
	 * Qualquer alteração na política em empréstimo ele deve ser desativada para o usuário utilizar sempre a atualmente virgente.
	 * Ou seja, se o administrador alterar, a politica vai ser desabilitada e criada uma nova com as novas informações.
	 * 
	 * Os empréstimos já feitos vão continuar usando as regras anteriores e apenas os novos empréstimos vão usar as novas regras. 
	 * Para isso que serve esse campo.
	 */
	@Column(nullable = false)
	private boolean ativo = true;

	
	
	
	//////////////////////////  Informações de Auditoria ////////////////////////
	/**
	 * Registro entrada do usuário que cadastrou
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
	 * Registro entrada do usuário que alterou
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")	
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;
	
	
	/**
	 * Data da última atualização
	 */
	@AtualizadoEm
	@Column(name="data_atualizacao")
	private Date dataAtualizacao;

	//////////////////////////////////////////////////////////////////////////////
	
	/** Guarda temporariamente o valor inteiro que equivale ao Vínculo do usuário.
	 * 
	 *  Isso porque nas página os combo box não pode atribuir valores à enums.
	 */
	@Transient
	private int valorVinculo = -1;
	
	
	/**
	 * Construtor default
	 */
	public PoliticaEmprestimo(){

	}

	/**
	 * Construtor para tipos que já estejam persistidos
	 * 
	 * @param id
	 */
	public PoliticaEmprestimo(int id){
		this.id = id;
	}

	/**
	 * Cria uma nova política de empréstimo ***ATIVA***.
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
	 * Cria uma nova política de empréstimo apenas com o tipo de prazo.
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
	 *   Diz se o prazo da política é para ser contado em dias
	 */
	public boolean isPrazoContadoEmHoras(){
		return this.tipoPrazo.equals(TIPO_PRAZO_EM_HORAS);
	}
	
	/**
	 *  Dia se o prazo da política é para ser contado em horas 
	 */
	public boolean isPrazoContadoEmDias(){
		return this.tipoPrazo.equals(TIPO_PRAZO_EM_DIAS);
	}
	
	/**
	 *    Diz se a política foi apagada pelo usuário. Uma política é apagada quando
	 * o prazo e a quantidade são iguais a zero.
	 * 
	 */
	public boolean isPoliticaApagada(){
		if( new Integer(0).equals(this.quantidadeMateriais) && new Integer(0).equals(this.prazoEmprestimo))
			return true;
		else
			return false;
	}
	
	
	/**
	 *   Se o usuário preencher a quantidade tem que preencher o prazo e vice-versa.
	 *   Se ele deixar os dois igual a zero significa que a política vai ser "apagada".
	 */
	public ListaMensagens validate() {
		
		ListaMensagens mensagens = new ListaMensagens();

		if(biblioteca == null || biblioteca.getId() < 0){
			mensagens.addErro("A política de empréstimos deve pertencer a uma biblioteca.");
			return mensagens;
		}
		
		if(tipoEmprestimo == null || tipoEmprestimo.getId() < 0){
			mensagens.addErro("A política de empréstimos deve possuir um tipo de empréstimo.");
			return mensagens;
		}
		
		if(vinculo == null || ! vinculo.isPodeRealizarEmprestimos()){
			mensagens.addErro("A política de empréstimos deve possuir um vínculo do usuário.");
			return mensagens;
		}
		
		if(quantidadeMateriais == null || quantidadeMateriais.compareTo(new Integer(0)) < 0){
			mensagens.addErro("A quantidade de materiais da política "+this.getDescricaoPolitica()+" deve ser igual ou maior que zero.");
		}
		
		if(prazoEmprestimo == null  || prazoEmprestimo.compareTo(new Integer(0)) < 0){
			mensagens.addErro("O prazo da política "+this.getDescricaoPolitica()+" deve ser igual ou maior que zero.");
		}
		
		if(quantidadeRenovacoes == null || quantidadeRenovacoes.compareTo(new Integer(0)) < 0){
			mensagens.addErro("A quantidade renovações da política "+this.getDescricaoPolitica()+" deve ser igual ou maior que zero.");
		}
		
		if(! new Short(tipoPrazo).equals(TIPO_PRAZO_EM_DIAS)  &&  ! new Short(tipoPrazo).equals(TIPO_PRAZO_EM_HORAS) ){
			mensagens.addErro("A unidade do prazo da política: "+this.getDescricaoPolitica()+" não é válida. ");
		}
		
		return mensagens;
	}
	
	/** 
	 * <p>Uma política de empréstimo é igual a outra se for da mesma biblioteca,  mesmo vínculo do usuário, mesmo tipo de empréstimo.</p>
	 * 
	 * <p> <strong>Observação:  </strong> A política personalizável é fixa no sistema e não contém status nem vínculo 
	 * do usuário. Qualquer usuário pode realizá-la. Então a comparação de igualdade é realizada apenas pelo id.</p>
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(this.getId());
	}

	
	/**
	 * <p> Uma política de empréstimo é igual a outra pelo id persitido..</p>
	 * 
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	
	/**
	 * Verifica se uma política é igual a outra pelos dados da política. Utilizando os daods que identificam a política  
	 * (biblioteca, vinculo do usuário, tipo de empréstimo, status do material e tipos do material) 
	 * 
	 * Não considera o id.
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
	 * Verifica se uma política é igual a outra pelos dados da política. Não considera o id.
	 * Verifica Todos os dados, até a quantidade de materias, tipo parazo, quantidade de renovações.
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
	 * Adiciona um novo status de material a essa política, caso seja adicionado a política passa a ser específica para materiais desse status.
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
	 * Adiciona um novo status de material a essa política, caso seja adicionado a política passa a ser específica para materiais desse status.
	 * @param status
	 */
	public void adicionaTipoMaterial(TipoMaterial tipoMaterial){
		if(tiposMateriais == null){
			tiposMateriais = new ArrayList<TipoMaterial>();
		}
		if(! tiposMateriais.contains(tipoMaterial))
			tiposMateriais.add(tipoMaterial);
	}
	
	/** Retorna a quantidade de tipos de materiais associados à política */
	public int getQuantidadeStatusMateriais(){
		if(statusMateriais == null){
			return 0;
		}
		
		return statusMateriais.size();
	}
	
	/** Retorna a quantidade de tipos de materiais associados à política */
	public int getQuantidadeTiposMateriais(){
		if(tiposMateriais == null){
			return 0;
		}
		
		return tiposMateriais.size();
	}
	
	
	
	/**  
	 * <p>Retorna os status associados com a política separados por vírgual:  ESPECIAL, NORMAL, NÃO CIRCULA, ETC...</p>
	 * 
	 *  <p>Usado na visualização para o usuário.</p>
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
	 * <p>Retorna os tipos de materias associados com a política separados por vírgual:  LIVRO, DVD, CD, ETC ...</p>
	 * 
	 * <p>Usado na visualização para o usuário.</p>
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
	 *   Retorna a descrição do tipo de prazo para mostrar nas páginas.
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
	 *   Retorna a descrição da política para o usuário
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
					+ " Tipo Empréstimo: "+tipoEmprestimo.getDescricao() 
					+ " Materiais = " + quantidadeMateriais+","
					+ " Prazo = " + prazoEmprestimo + " "+ getDescricaoTipoPrazo()+","
					+ " Renovações = " + quantidadeRenovacoes+".";
		}else{
			if( biblioteca != null  && tipoEmprestimo != null )
				return "["+ biblioteca.getIdentificador() +" -  Tipo: "+ tipoEmprestimo.getDescricao() +"] ";
			else
				return "";
		}
	}

	/**
	 * Retorna as informações da potítica para mostrar ao usuário no momento do empréstimo.
	 *
	 * @return
	 */
	public String getInformacoesPolitica() {
		
		StringBuilder retorno = new StringBuilder();
		if(tipoEmprestimo != null ){
			retorno.append("["+"Tipo de Empréstimo: "+tipoEmprestimo.getDescricao());
			
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
					+ " Renovações = " + quantidadeRenovacoes+".");
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
