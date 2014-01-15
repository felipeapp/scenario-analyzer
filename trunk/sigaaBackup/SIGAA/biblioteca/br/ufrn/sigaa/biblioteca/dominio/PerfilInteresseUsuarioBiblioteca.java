/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 29/11/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.dominio;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Autoridade;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;

/**
 *
 * <p>Cont�m as informa��es do perfil de intererrese do usu�rio na biblioteca.</p>
 *
 * <p> <i> At� o momento utilizada apenas para interesse na informa��es que ser�o disseminadas pelo sistema, num futuro pode conter outras informa��es </i> </p>
 * 
 * @author jadson
 *
 */
@Entity
@Table(name = "perfil_interesse_usuario_biblioteca", schema = "biblioteca")
public class PerfilInteresseUsuarioBiblioteca implements PersistDB{

	/** O id */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.resgistro_extras_sequence") })
	@Column(name = "id_perfil_interesse_usuario_biblioteca", nullable = false)
	private int id;
	
	/** A conta do usu�rio biblioteca desse perfil,  esse perfil s� � v�lido enquanto a conta n�o for quitada. */
	@ManyToOne(cascade  =  {}, fetch=FetchType.LAZY)
	@JoinColumn(name = "id_usuario_biblioteca", referencedColumnName ="id_usuario_biblioteca")
	private UsuarioBiblioteca usuario;
	
	
	/** Caso o usu�rio selecione esse campo, o sistema vai gerar mensalmente o relat�rio de novas aquisi��o (do �ltimo m�s) e enviar para quem tiver interesse. */
	@Column(name="receber_informativo_novas_aquisicoes", nullable=false)
	private boolean receberInformativoNovasAquisicoes = false;
	
	/** Guarda a grande �rea de conhecimento dos T�tulos que o usu�rio gostaria de receber.
	 *  Caso esse valor seja nulo, o utu�rio vai escolher o informativo com todas as �reas. 
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_area_conhecimento_informativo", referencedColumnName="id_area_conhecimento_cnpq")
	private AreaConhecimentoCnpq areaDoInformativo;
	
	
	/** A listagem dos assuntos autorizados da base de autoridades que o usu�rio escolheu receber informa��o. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name="biblioteca.perfil_interesse_x_autoridade_assunto", 
			joinColumns={@JoinColumn(name="id_perfil_interesse_usuario_biblioteca")}, 
			inverseJoinColumns={@JoinColumn(name="id_autoridade")})
	private Set<Autoridade> assuntosDeInteresse;
	
	
	/** A listagem dos autores autorizados da base de autoridades que o usu�rio escolheu receber informa��o. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name="biblioteca.perfil_interesse_x_autoridade_autor", 
			joinColumns={@JoinColumn(name="id_perfil_interesse_usuario_biblioteca")}, 
			inverseJoinColumns={@JoinColumn(name="id_autoridade")})
	private Set<Autoridade> autoresDeInteresse;
	
	
	/** <p> Indica as bibliotecas nas quais o usu�rio tem enteresse em receber informa��es. </p>
	 *  <p> Caso exista alguma, s� receber� informa��es provenientes dessa bibliotecas, sen�o receber informa��o de qualquer biblioteca. </p>
	 *  <p> No caso do usu�rio escolher todas as bibliotecas, esse relacionamento n�o ser� salvo, a cole��o estar� vazia.</p>
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name="biblioteca.perfil_interesse_x_biblioteca", 
			joinColumns={@JoinColumn(name="id_perfil_interesse_usuario_biblioteca")}, 
			inverseJoinColumns={@JoinColumn(name="id_biblioteca")})
	private Set<Biblioteca> bibliotecasDeInteresse;
	
	
	/**
	 * Adiciona um novo assunto que o usu�rio registrou interesse ao seu perfil
	 *  
	 * @param autoridade
	 */
	public void adicionaAssuntosDeInteresse(Autoridade autoridade){
		if(this.assuntosDeInteresse == null)
			this.assuntosDeInteresse = new HashSet<Autoridade>();
		
		this.assuntosDeInteresse.add(autoridade);
	}
	
	/**
	 * Adiciona um novo autor que o usu�rio registrou interesse ao seu perfil
	 *  
	 * @param autoridade
	 */
	public void adicionaAutoresDeInteresse(Autoridade autoridade){
		if(this.autoresDeInteresse == null)
			this.autoresDeInteresse = new HashSet<Autoridade>();
		
		this.autoresDeInteresse.add(autoridade);
	}
	
	
	/**
	 * Adiciona uma nova biblioteca �s bibliotecas que o usu�rio tem interesse em receber informa��o.
	 *  
	 * @param autoridade
	 */
	public void adicionaBibliotecaInteresse(Biblioteca biblioteca){
		if(this.bibliotecasDeInteresse == null)
			this.bibliotecasDeInteresse = new HashSet<Biblioteca>();
		
		this.bibliotecasDeInteresse.add(biblioteca);
	}
	
	
	
	///// sets e gets /////
	
	public UsuarioBiblioteca getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioBiblioteca usuario) {
		this.usuario = usuario;
	}

	public boolean isReceberInformativoNovasAquisicoes() {
		return receberInformativoNovasAquisicoes;
	}

	public void setReceberInformativoNovasAquisicoes(boolean receberInformativoNovasAquisicoes) {
		this.receberInformativoNovasAquisicoes = receberInformativoNovasAquisicoes;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Set<Autoridade> getAssuntosDeInteresse() {
		return assuntosDeInteresse;
	}

	public void setAssuntosDeInteresse(Set<Autoridade> assuntosDeInteresse) {
		this.assuntosDeInteresse = assuntosDeInteresse;
	}


	public Set<Biblioteca> getBibliotecasDeInteresse() {
		return bibliotecasDeInteresse;
	}

	public void setBibliotecasDeInteresse(Set<Biblioteca> bibliotecasDeInteresse) {
		this.bibliotecasDeInteresse = bibliotecasDeInteresse;
	}

	public int getId() {
		return id;
	}
	
	public AreaConhecimentoCnpq getAreaDoInformativo() {
		return areaDoInformativo;
	}

	public void setAreaDoInformativo(AreaConhecimentoCnpq areaDoInformativo) {
		this.areaDoInformativo = areaDoInformativo;
	}

	public Set<Autoridade> getAutoresDeInteresse() {
		return autoresDeInteresse;
	}

	public void setAutoresDeInteresse(Set<Autoridade> autoresDeInteresse) {
		this.autoresDeInteresse = autoresDeInteresse;
	}
	
	
}
