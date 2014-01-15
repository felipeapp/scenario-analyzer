/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 27/04/2009
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import java.util.ArrayList;
import java.util.Collections;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.jsf.ConfiguraPerfilInteresseUsuarioBibliotecaMBean;
import br.ufrn.sigaa.biblioteca.util.CampoControleByEtiquetaComparator;
import br.ufrn.sigaa.biblioteca.util.CampoDadosByEtiquetaPosicaoComparator;

/**
 *
 *      <p>Classe que guarda as informa��es das autoridades cadastradas no sistema. Autoridades s�o
 * autores, assuntos, s�ries, qualquer coisa que se queira e o MARC permita buscar por nomes
 * alternativos dentro do sistema.</p>
 * 
 *     <p>Cadastro de autoridades praticamente possui a entrada autorizada (nome oficial) com suas
 * entradas remissivas (nomes alternativos).</p>
 * 
 *     <p>A entrada principal deve ser usada para atribuir o nome oficial na cataloga��o de T�tulos.
 * Os nome alternativos s�o usados nas buscas do sistema.</p>
 *
 *     <p>A base de autoridades tamb�m � usada como temas para os usu�rios cadastrarem interesse da parte de DSI.</p>
 *
 * @author jadson
 * @since 27/04/2009
 * @version 1.0 cria��o da classe
 * @see ConfiguraPerfilInteresseUsuarioBibliotecaMBean
 */
@Entity
@Table(name = "autoridade", schema = "biblioteca")
public class Autoridade implements PersistDB{
	
	/*
	 *    Os tipos das autoridades existentes no sistema, at� agora o sistema suporta autoridades de
	 * autor e assunto.
	 */
	
	/** Indica que a autoridade � do tipo autor.  */
	public static final int TIPO_AUTOR = 1;
	
	/** Indica que a autoridade � do tipo assunto. */
	public static final int TIPO_ASSUNTO = 2;
	
	/** O id da autoridade. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column (name="id_autoridade")
	private int id;
	
	/**
	 * Guarda um n�mero que identifica a autoridade no sistema.
	 */
	@Column(name="numero_do_sistema")
	private int numeroDoSistema;
	
	/**
	 * Autoridades, assim como t�tulos, s�o salvos no formato MARC.
	 */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY,  mappedBy="autoridade")
	@IndexColumn(name="posicao", base=0, nullable=false)
	private List<CampoControle> camposControle;

	/**
	 * O t�tulo catalogr�fico no formato MARC � formado por v�rios registros de dados.
	 */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY,  mappedBy="autoridade")
	@IndexColumn(name="posicao", base=0, nullable=false)
	private List<CampoDados> camposDados;
	
	/**
	 * Se a autoridade for catalogada por meio de importa��o vai ser salvo esse valor como  true
	 * Esse informa��es serve para os relat�rios do setor de coopera��o t�cnica.
	 * OBs.: A data da importa��o vai ser a data da cria��o da autoridade.
	 */
	@Column(name="importada", nullable=false)
	private boolean importada = false;
	
	/**
	 *  Guarda a biblioteca de quem importou a autoridade. Como um t�tulo n�o est� em uma
	 * biblioteca espec�fica � preciso guardar de onde o t�tulo foi exportado  */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_biblioteca_importacao", referencedColumnName="id_biblioteca")
	private Biblioteca biblotecaImportacao;
	
	/**
	 *   Informa��o usada para saber se uma Autoridade foi catalogada por completo ou n�o.
	 *   Se uma Autoridade n�o foi catalogada por completo n�o vai poder ser exportada.
	 * 
	 *   Se um arquivo de importa��o tiver v�rias Autoridades essas Autoridades tamb�m ser�o
	 *   salvas no sistema como n�o catalogadas.
	 */
	@Column(name="catalogada", nullable=false)
	private boolean catalogada = false;
	
	/**
	 *    O tipo da autoridade, se o autor ou assunto ou qualquer tipo que o sistema venha a suportar
	 * no futuro.
	 */
	@Column(name="tipo", nullable=false)
	private int tipo;
	
	/**
	 *   Autoridades podem ser apagadas do sistema, nesse caso elas s�o desativadas e seus
	 *   caches removidos para n�o aparecerem mais nas pesquisas.
	 */
	private boolean ativo = true;
	
	
	/** C�digo preenchido para manter alguma informa��o sobre dados
	 * migrados de outros sistemas. */
	private String codmerg;
	
	////////////////////////////  INFORMA��ES DE AUDITORIA  ///////////////////////////////////////

	/** Registro de entrada de cria��o da autoridade. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_criacao")
	@CriadoPor
	private RegistroEntrada registroCriacao;

	/**
	 * Data de cadastro.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@CriadoEm
	@Column(name="data_criacao")
	private Date dataCriacao;

	/**
	 * registro entrada  do usu�rio que realizou a �ltima atualiza��o
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_ultima_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroUltimaAtualizacao;

	/**
	 * data da �ltima atualiza��o
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_ultima_atualizacao")
	@AtualizadoEm
	private Date dataUltimaAtualizacao;

	//////////////////////////////////////////////////////////////////////////////////////////
	
	public Autoridade(){
		// Construtor padr�o
	}
	
	public Autoridade(int id){
		this.id = id;
	}
	
	/**
	 * Adiciona um novo campo de controle.
	 */
	public void addCampoControle(CampoControle c){
		if(camposControle == null){
			camposControle = new ArrayList<CampoControle>();
		}
		camposControle.add(c);
	}

	/**
	 * Adiciona um novo campo de dados.
	 */
	public void addCampoDados(CampoDados d){
		if(camposDados == null){
			camposDados = new ArrayList<CampoDados>();
		}
		camposDados.add(d);
	}
	
	
	
	/**
	 * Retorna a lista de campos de controle ordenados pela etiqueta LDR -> 001 -> 003 ...
	 */
	public List<CampoControle> getCamposControleOrdenadosByEtiqueta() {
		
		if(camposControle == null) return new ArrayList<CampoControle>();
		
		Collections.sort(camposControle, new CampoControleByEtiquetaComparator());
		return camposControle;
	}


	/**
	 * Retorna a lista de campos de dados ordenados pela etiqueta 010 -> 022 -> 940 ...
	 */
	public List<CampoDados> getCamposDadosOrdenadosByEtiqueta() {
		
		if(camposDados == null) return new ArrayList<CampoDados>();
			
		Collections.sort(camposDados, new CampoDadosByEtiquetaPosicaoComparator());
		return camposDados;
	}
	
	
	/**
	 * Duas autoridades s�o iguais quando t�m o mesmo n�mero do sistema.
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(numeroDoSistema);
	}

	/**
	 * Duas autoridades s�o iguais quando t�m o mesmo n�mero do sistema.
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "numeroDoSistema");
	}
	
	
	/**
	 * Imprime as informa��es de um autoridade no formato MARC.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder autoridade = new StringBuilder();
		
		if(this.getCamposControleOrdenadosByEtiqueta() != null)
			for (CampoControle cc : this.getCamposControleOrdenadosByEtiqueta()) {
				autoridade.append(cc.getEtiqueta().getTag()+" "+cc.getDado()+" \n");
			}
		
		if(this.getCamposDadosOrdenadosByEtiqueta() != null)
			for (CampoDados cd : this.getCamposDadosOrdenadosByEtiqueta()) {
				
				autoridade.append(cd.getEtiqueta().getTag()+" "+(new Character(' ').equals(cd.getIndicador1()) ? '_' : cd.getIndicador1())
						+" "+(new Character(' ').equals(cd.getIndicador2()) ? '_' : cd.getIndicador2()));
				
				for (SubCampo  sc : cd.getSubCampos()) {
					autoridade.append(" $"+sc.getCodigo()+" "+sc.getDado());
				}
				
				autoridade.append(" \n");
			}
		
		return autoridade.toString();
	}

	
	/**
	 *    Verifica se cont�m alguns dos campos autorizados de autor
	 */
	public boolean contemEntraPrincipalAutor() {
		
		if(this.getCamposDados() != null ){
	
			for (CampoDados d : this.getCamposDados()) {
				
				if(d.getEtiqueta().equals(Etiqueta.NOME_PESSOAL))
					return true;
				
				if(d.getEtiqueta().equals(Etiqueta.NOME_CORPORATIVO))
					return true;
				
				if(d.getEtiqueta().equals(Etiqueta.NOME_EVENTO))
					return true;
				
			}
		}
		
		return false;
	}


	
	/**
	 *    Verifica se cont�m alguns dos campos autorizados de assunto
	 */
	public boolean contemEntraPrincipalAssunto() {
		
		if(this.getCamposDados() != null ){
		
			for (CampoDados d : this.getCamposDados()) {

				if(d.getEtiqueta().equals(Etiqueta.CABECALHO_TOPICOS))
					return true;
				
				if(d.getEtiqueta().equals(Etiqueta.CABECALHO_NOME_GEOGRAFICO))
					return true;
				
				if(d.getEtiqueta().equals(Etiqueta.CABECALHO_GERAL_SUBDIVISAO))
					return true;
				
			}
		}
		
		return false;
		
	}
	
	/////// set e gets ///////

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public List<CampoControle> getCamposControle() {
		return camposControle;
	}

	public List<CampoDados> getCamposDados() {
		return camposDados;
	}

	public RegistroEntrada getRegistroCriacao() {
		return registroCriacao;
	}

	public void setRegistroCriacao(RegistroEntrada registroCriacao) {
		this.registroCriacao = registroCriacao;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public RegistroEntrada getRegistroUltimaAtualizacao() {
		return registroUltimaAtualizacao;
	}

	public void setRegistroUltimaAtualizacao(
			RegistroEntrada registroUltimaAtualizacao) {
		this.registroUltimaAtualizacao = registroUltimaAtualizacao;
	}

	public Date getDataUltimaAtualizacao() {
		return dataUltimaAtualizacao;
	}

	public void setDataUltimaAtualizacao(Date dataUltimaAtualizacao) {
		this.dataUltimaAtualizacao = dataUltimaAtualizacao;
	}

	public void setCamposControle(List<CampoControle> camposControle) {
		this.camposControle = camposControle;
	}

	public void setCamposDados(List<CampoDados> camposDados) {
		this.camposDados = camposDados;
	}

	public int getNumeroDoSistema() {
		return numeroDoSistema;
	}

	public void setNumeroDoSistema(int numeroDoSistema) {
		this.numeroDoSistema = numeroDoSistema;
	}

	public boolean isImportada() {
		return importada;
	}

	public void setImportada(boolean importada) {
		this.importada = importada;
	}

	public Biblioteca getBiblotecaImportacao() {
		return biblotecaImportacao;
	}

	public void setBiblotecaImportacao(Biblioteca biblotecaImportacao) {
		this.biblotecaImportacao = biblotecaImportacao;
	}

	public boolean isCatalogada() {
		return catalogada;
	}

	public void setCatalogada(boolean catalogada) {
		this.catalogada = catalogada;
	}
	
	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public String getCodmerg() {
		return codmerg;
	}

	public void setCodmerg(String codmerg) {
		this.codmerg = codmerg;
	}
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
}
