/*
 * RegistroMovimentacaoMaterialInformacional.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import java.util.Date;

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

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.dominio.Usuario;

/**
 *        Classe usada para registrar as movimentações de materiais de uma biblioteca para a outra.<br/>
 *        Quando um material precisa de autorização para ser transferido, é criado apenas o registro 
 *     com a variável <code>pendente = true</code>, depois o usuário da biblioteca destino tem que autorizar a 
 *     transferência, quando é colocado  <code>pendente = false</code> no registro da transferência e
 *     a biblioteca do materia é realmente mudada para a transferência ocorrer.        
 *
 * @author jadson
 * @since 23/09/2009
 * @version 1.0 criacao da classe
 *
 */
@Entity
@Table(name = "registro_movimentacao_material_informacional", schema = "biblioteca")
public class RegistroMovimentacaoMaterialInformacional implements PersistDB{

	/**
	 * id
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column(name="id_registra_movimentacao_material", nullable=false)
	private int id;
	
	/** O material que foi transferido de biblioteca */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_material_informacional", referencedColumnName = "id_material_informacional")
	private MaterialInformacional material;
	
	/** Onde o material estava */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_biblioteca_origem", referencedColumnName ="id_biblioteca")
	private Biblioteca bibliotecaOrigem;
	
	/** Para onde  o material foi */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_biblioteca_destino", referencedColumnName ="id_biblioteca")
	private Biblioteca bibliotecaDestino;

	/** Em qual assinatura o fascículos estava (para os casos de tranferência de fascículos)  */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_assinatura_origem", referencedColumnName ="id_assinatura")
	private Assinatura assinaturaOrigem;
	
	
	/** Para qual assinatura o fascículos foi transferido (para os casos de tranferência fascículos)
	 * <br/>
	 * <p>IMPORTANTE: Caso a biblioteca destino não possua uma assintura para o título escolhido, a
	 * transferência pode ser feita, neste caso, a assintura Destino vai possuir o valor "nulo".
	 * <br/>
	 * A criação e atribuíção da assinatura deve ser feita pelo bibliotecário que vai autorizar a transferênica.
	 * </p>
	 * 
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_assinatura_destino", referencedColumnName ="id_assinatura")
	private Assinatura assinturaDestino;
	
	
	/** Quem foi que realizou a movimentação */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_movimentou_material", referencedColumnName ="id_usuario")
	private Usuario usuarioMovimentouMaterial;
	
	
	/** Quem foi que autorizou a movimentacao, para fascículos a movimentação só é realizada se for 
	 * autorizada por um bibliotecário na biblioteca de destino  */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_autorizou_movimentacao_material", referencedColumnName ="id_usuario")
	private Usuario usuarioAutorizouMovimentacaoMaterial;
	
	
	
	/** Quando a movimentação foi feita  */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_movimentacao")
	private Date dataMovimentacao;
	
	
	/** Quando a autorização da tranferência foi feita */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_autorizacao")
	private Date dataAutorizacao;
	
	
	/** Se a movimentação está pendente de autorização ou não */
	@Column(name = "pendente")
	private boolean pendente;
	
	/** <p>Guarda se a movimentação gerou um chamado patrimonial no SIPAC</p> 
	 *  <p>Vai possuir 3 valores:</p>
	 *  <p>
	 *  	<ul>
	 *  		<li>NULL = Não se sabe (tranferência ocorridas antes dessa funcionalidade entrar)</li>
	 *  	 	<li>TRUE = sim </li>
	 *  	 	<li>FALSE = não</li>
	 *   	</ul>
	 *  </p> 
	 */
	@Column(name = "gerou_chamado_patrimonial", nullable=true)
	private Boolean gerouChamadoPatrimonial;
	
	
	/**
	 * Construtor padrão para o hibernate e jsf.
	 */
	public RegistroMovimentacaoMaterialInformacional(){
		
	}
	
	/**
	 * Construtor padrão para o hibernate e jsf.
	 */
	public RegistroMovimentacaoMaterialInformacional(int id){
		this.id = id;
	}
	
	/**
	 *     Construtor para criar um revistro de movimentação para exemplares. A princípio
	 *  exemplares são transferidos diretamente. Sem precisar de autorização do bibliotecário da 
	 *  biblioteca destino.
	 * 
	 * 
	 * @param material
	 * @param usuarioMovimentouMaterial
	 * @param dataMovimentacao
	 * @param bibliotecaOrigem
	 * @param bibliotecaDestino
	 */
	public RegistroMovimentacaoMaterialInformacional(Exemplar exemplar, Usuario usuarioMovimentouMaterial, 
			Biblioteca bibliotecaOrigem, Biblioteca bibliotecaDestino) {
		
		this.material = exemplar;
		this.usuarioMovimentouMaterial = usuarioMovimentouMaterial;
		this.dataMovimentacao = new Date();
		this.bibliotecaOrigem = bibliotecaOrigem;
		this.bibliotecaDestino = bibliotecaDestino;
		this.pendente = false;
	}
	
	
	/**
	 *     Construtor para criar um revistro de movimentação para fascículos. Todas as transferência 
	 * de fascículos são feitas em duas etapas. Na primeira é criada somente um registro pedente 
	 * da transferência, somente depois que um bibliotecário da biblioteca destino autorizar, os 
	 * fascículos são realmente transferidos e o registro é alterado para não constar mais como pendente.
	 * 
	 * @param material
	 * @param usuarioMovimentouMaterial
	 * @param dataMovimentacao
	 * @param bibliotecaOrigem
	 * @param bibliotecaDestino
	 */
	public RegistroMovimentacaoMaterialInformacional(Fasciculo f, Usuario usuarioMovimentouMaterial, 
			Biblioteca bibliotecaOrigem, Biblioteca bibliotecaDestino, 
			Assinatura assinaturaOrigem, Assinatura assinaturaDestino) {
		
		this.material = f;
		this.usuarioMovimentouMaterial = usuarioMovimentouMaterial;
		this.dataMovimentacao = new Date();
		this.bibliotecaOrigem = bibliotecaOrigem;
		this.bibliotecaDestino = bibliotecaDestino;
		this.assinaturaOrigem = assinaturaOrigem;
		this.assinturaDestino = assinaturaDestino;
		this.pendente = true;
	}
	
	
	

	////////////  Sets e Gets //////////
	
	public int getId() {
		return id;
	}

	public MaterialInformacional getMaterial() {
		return material;
	}

	public Usuario getUsuarioMovimentouMaterial() {
		return usuarioMovimentouMaterial;
	}

	public Date getDataMovimentacao() {
		return dataMovimentacao;
	}

	public Biblioteca getBibliotecaOrigem() {
		return bibliotecaOrigem;
	}

	public Biblioteca getBibliotecaDestino() {
		return bibliotecaDestino;
	}

	public void setId(int id) {
		this.id = id;	
	}

	public Assinatura getAssinaturaOrigem() {
		return assinaturaOrigem;
	}

	public Assinatura getAssinturaDestino() {
		return assinturaDestino;
	}

	public Usuario getUsuarioAutorizouMovimentacaoMaterial() {
		return usuarioAutorizouMovimentacaoMaterial;
	}

	public Date getDataAutorizacao() {
		return dataAutorizacao;
	}

	public boolean isPendente() {
		return pendente;
	}

	public void setUsuarioAutorizouMovimentacaoMaterial(Usuario usuarioAutorizouMovimentacaoMaterial) {
		this.usuarioAutorizouMovimentacaoMaterial = usuarioAutorizouMovimentacaoMaterial;
	}

	public void setDataAutorizacao(Date dataAutorizacao) {
		this.dataAutorizacao = dataAutorizacao;
	}

	public void setPendente(boolean pendente) {
		this.pendente = pendente;
	}

	public void setAssinturaDestino(Assinatura assinturaDestino) {
		this.assinturaDestino = assinturaDestino;
	}

	public Boolean isGerouChamadoPatrimonial() {
		return gerouChamadoPatrimonial;
	}

	public void setGerouChamadoPatrimonial(Boolean gerouChamadoPatrimonial) {
		this.gerouChamadoPatrimonial = gerouChamadoPatrimonial;
	}
	
	
	
}
