/*
 * FormatoMaterial.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
 * Natal - RN - Brasil
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
 *  <p> Classe que guarda as informa��es dos formatos de materiais definidos do padr�o 
 *  <a href="http://www.loc.gov/marc/bibliographic/ecbdhome.html">MARC</a> </p> 
 *
 *  <p> <strong>Observa��o 1: </strong> Esses formatos definem o conjunto de valores poss�veis para alguns campos de controle 
 *  MARC, como por exemplo os campos 006 e 008.</p>
 *  
 *  <p> <strong>Observa��o 2: </strong>  Esses formatos tamb�m definem como vai ser gerado o formato de refer�ncia e a ficha 
 *  catalogr�fica referentes � cataloga��o que contiver este formato.</p>
 *
 * @author jadson
 * @since 06/08/2008
 * @version 1.0 criacao da classe
 *
 */
@Entity
@Table(name = "formato_material", schema = "biblioteca")
public class FormatoMaterial implements Validatable{

	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })	
	@Column(name="id_formato_material")
	private int id;

	
	/* BK para livro, MU para m�sica, SE para peri�dico, etc... */
	@Column(name = "sigla", nullable=false)
	private String sigla;


	/* Informa a descricao do formato. LIVRO, MUSICA, PERIODICO, etc */
	@Column(name = "descricao", nullable=false)
	private String descricao;


	/* 
	 *    Para cada formato material vai existir um conjunto de valores padrao para o campo lider bibliografico. 
	 *    Apesar do campo lider no formato MARC n�o depender do formato do material, o valores padrao 
	 * que s�o mostrados na pagina de edicao desse campo variam dependendo do formato do material 
	 * (isso foi copiado do ALEPH) 
	 * */
	@SuppressWarnings("unused") // � usado sim, s� n�o atrav�s de gets. buscado direto no banco.
	@OneToMany(mappedBy="formatoMaterial", cascade={CascadeType.REMOVE})
	private List<ValorPadraoCampoControle> valoresPadraoControle;
	

	/**
	 * Construtor sem argumentos para o JSF
	 */
	public FormatoMaterial(){
	}

	public FormatoMaterial(int id){
		this.id = id;
	}

	/**
	 * Construtor default
	 * 
	 * @param descricao
	 */
	public FormatoMaterial(String descricao){
		this.descricao = descricao;
	}

	
	/**
	 * Construtor default
	 * 
	 * @param descricao
	 */
	public FormatoMaterial(String sigla, String descricao){
		this(descricao);
		this.sigla = sigla;
	}

	/**
	 * 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {

		ListaMensagens mensagens = new ListaMensagens();

		if(descricao == null || "".equals(descricao.trim())){
			mensagens.addErro("� preciso informar a descri��o do formato do t�tulo catalogr�fico");
		}

		if(StringUtils.isEmpty(sigla)){
			mensagens.addErro("� preciso informar a sigla do formato do t�tulo catalogr�fico ");
		}

		return mensagens;
	}


	/**
	 * Diz se um formato de material � do tipo peri�dico. 
	 * Importante para saber se deve permitir a inclus�o de um exemplar ou fasc�culo ao T�tulo
	 *
	 * @return
	 * @throws DAOException
	 */
	public boolean isFormatoPeriodico(){
		if(this.id == ParametroHelper.getInstance().getParametroInt(ParametrosBiblioteca.FORMATO_MATERIAL_PERIODICO)) 
			return true;
		else 
			return false;
	}
	
	
	
	/**
	 * Diz se um formato de material � do tipo livro.
	 *
	 * @return
	 * @throws DAOException
	 */
	public boolean isFormatoLivro(){
		if(this.id == ParametroHelper.getInstance().getParametroInt(ParametrosBiblioteca.FORMATO_MATERIAL_LIVRO)) 
			return true;
		else 
			return false;
	}
	
	
	



	
	
	// os sets e gets

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getDescricao() {
		return descricao;
	}

	/**
	 * 
	 * Retorna a descri��o completa para ser mostrada na p�gina
	 *
	 * @return
	 */
	public String getDescricaoCompleta(){
		return sigla+" - "+descricao;
	}

	// GETS e SETS
	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
