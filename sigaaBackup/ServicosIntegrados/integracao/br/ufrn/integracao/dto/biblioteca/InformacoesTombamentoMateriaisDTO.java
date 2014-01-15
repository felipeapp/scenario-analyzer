/*
 * InformacoesTombamentoMateriaisDTO.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendencia de Informatica
 * Diretoria de Sistemas
 * Campos Universitario Lagoa Nova
 * Natal - RN - Brasil
 *
 * Este software eh confidencial e de propriedade intelectual da
 * UFRN - Universidade Federal do Rio Grande no Norte
 * Nao se deve utilizar este produto em desacordo com as normas
 * da referida instituicao.
 */
package br.ufrn.integracao.dto.biblioteca;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *     <p>O objeto que tramite os dados dos materiais informacionais tombados no SIPAC para o SIGAA.</p>
 *     <p>Usado no caso de uso de cataloga��o por tombamento.</p>
 *
 *     <p>Observa��o: Como esse classe � um DTO os seus atributos podem ser p�blicos, contudo se eles foram ser acessados por JSPs
 *     devem ter os m�todos gets e sets e ser privados, para n�o dar erro no web service na hora de criar o servi�o remoto.</p>
 *
 * @author jadson
 * @since 12/03/2009
 * @version 1.0 criacao da classe
 *
 */
public class InformacoesTombamentoMateriaisDTO implements Serializable{
	
	/** O identificador da serializa��o*/
	private static final long serialVersionUID = 6688205673067589268L;
	
	
	/** Constante para indicar o tipo de tombamento dos materiais por compra */ 
	public static final short TIPO_TOMBAMENTO_COMPRA = 1;
	/** Constante para indicar o tipo de tombamento dos materiais por doa��o*/
	public static final short TIPO_TOMBAMENTO_DOACAO = 2;
	
	/** Aponta para qual titulo o livro se transformou no sigaa */
	public int idTituloCatalograficoSigaa;
	
	/** Faz refer�ncia ao livro no SIPAC.  � a unidade equivalente ao TitutoCatalografico no acervo do SIGAA */
	private int idLivro;             		
	
	////////////////////////////////////////////////////////////////////////////////////////////
	//informacoes do livro no sipac vao ser trasformados para campos no formato MARC no sigaa //
	////////////////////////////////////////////////////////////////////////////////////////////
	
	/** Equivamente ao campo 100$a do SIGAA */
	private String autor; 
	/** Equivamente ao campo 100$b do SIGAA */
	private String autor2;     
	/** Equivamente ao campo 100$c do SIGAA */
	private String autor3;    
	/** Equivamente ao campo 245$a do SIGAA */
	private String titulo;    
	/** Equivamente ao campo 245$b do SIGAA */
	private String subtitulo;
	/** Equivamente ao campo 260$a do SIGAA */
	private String local; 
	/** Equivamente ao campo 020$a do SIGAA */
	private String isbn;
	/** Equivamente ao campo 250$a do SIGAA */
	private int edicao; 
	/** Equivamente ao campo 260$c do SIGAA */
	private int ano;           
	/** Equivamente ao campo 300$a  do SIGAA */
	private String volume;     
	/** Equivamente ao campo 300$a do SIGAA */
	private Integer paginas;    
	/** Equivamente ao campo 490$a do SIGAA */
	private String serie;       

	//////////////////////////////////////////////////////////////////////////////////////////////////

	

	/** A descri��o do tipo de tombamento do bem */
	private String tipoTombamento;        
	
	/** A descri��o do termo de responsabilidade cadatrado no SIPAC sobre o qual os materiais foram tombados */
	private String descricaoTermoResponsabibliodade;
	
	/** Os materiais comprados daquele titulo no formato:  Map<IdBem, NumeroTombamento>*/
    public Map<Integer, Long> numerosPatrimonio = new HashMap<Integer, Long>();
    
    
    /** Guarda os n�meros de tombamento ainda n�o inseridos no acervo do siagaa  */
    public Map<Integer, Long> numerosPatrimonioNaoUsados = new HashMap<Integer, Long>() ;
    
    
    /** Utilizada para mostrar ao usu�rios quais s�o os n�meros de tombamentos recuperados que ainda n�o foram usados. */
    private List<Long> listaNumerosPatrimonioNaoUsados = new ArrayList<Long>();
    
    
    /** Utilizada para mostrar ao usu�rios quais s�o os n�meros de tombamentos recuperados. */
    private List<Long> listaNumerosPatrimonioRecuperados = new ArrayList<Long>();
    
    
    /**  Os bens comprados com as unidade para onde foram tombados  no formato:  Map<IdBem, IdUnidade>
     *   Utilizado para verifica no momento da inclus�o no acervo do SIGAA se os bens foram tombados para a mesma unidade da biblioteca 
     *   e validar se o bibliotec�rio est� inclu�ndo os Bens na biblioteca correta.
     */
    public Map<Integer, Integer> unidadesTombamento = new HashMap<Integer, Integer>();
    
    
    /** Os tipos dos tombamentos dos materiais comprados daquele titulo no formato: Map<IdBem, TipoTombamento> */
    public Map<Integer, Short> tiposTombamentos = new HashMap<Integer, Short>();

    
    
    /**
     * <p>Adiciona � esse classe um n�mero de tombamento que ainda n�o existe na acervo do SIGAA. Calculado no momento que o usu�rio faz a busca.</p>
     * 
     * <p>S�o esses n�meros que o usu�rio vai conseguir incluir no acervo.</p>
     *
     * @void
     */
    public void adicionaNumeroPatrimonioNaoUsado(Integer idBem, Long numeroPatrimonio){
    	if(numerosPatrimonioNaoUsados == null){
    		numerosPatrimonioNaoUsados = new HashMap<Integer, Long>();
    		listaNumerosPatrimonioNaoUsados = new ArrayList<Long>();
    	}
    	
    	numerosPatrimonioNaoUsados.put(idBem, numeroPatrimonio);
    	listaNumerosPatrimonioNaoUsados.add(numeroPatrimonio);
    }
    
    
    
    /**
     * Retorna a quantidade total de materiais buscados no SIPAC.
     *
     * @int
     */
    public int getNumeroTotalMateriaisInformacionaisCompra(){
    	return numerosPatrimonio.size();
    }
    
    /**
     * Retorna a quantidade de materiais buscados no SIPAC que j� foram inclu�do no acervo do SIGAA.
     *
     * @int
     */
    public int getNumeroMateriaisInformacionaisAcervo(){
    	return numerosPatrimonio.size() - numerosPatrimonioNaoUsados.size();
    }
    
    /**
     * Retorna a quantidade de materiais buscados no SIPAC que ainda n�o foram inclu�do no acervo do SIGAA.
     *
     * @int
     */
    public int getNumeroMateriaisInformacionaisNaoUsados(){
    	return numerosPatrimonioNaoUsados.size();
    }
    
    
    /**
     * Retorna o primeiro n�mero de patrim�nio retornado na consulta. (ordem num�rica)
     *
     * @int
     */
    public Long getPrimeiroNumeroPatrimonioRecuperado(){
    	Collection<Long> temp =  numerosPatrimonio.values();
    	
    	List<Long> numerosPatrimonioRetornados = new ArrayList<Long>(temp);
    	Collections.sort(numerosPatrimonioRetornados);
    	return numerosPatrimonioRetornados.get(0);
    }
    
    /**
     * Retorna o �ltimo n�mero de patrim�nio retornado na consulta. (ordem num�rica)
     *
     * @int
     */
    public Long getUltimoNumeroPatrimonioRecuperado(){
    	Collection<Long> temp =  numerosPatrimonio.values();
    	
    	List<Long> numerosPatrimonioRetornados = new ArrayList<Long>(temp);
    	Collections.sort(numerosPatrimonioRetornados);
    	return numerosPatrimonioRetornados.get(numerosPatrimonioRetornados.size()-1);
    }

    /**
     * Retorna a listagem com os  n�meros de patrim�nio ainda n�o inclu�dos no acervo.
     *
     * @int
     */
    public List<Long> getListaNumerosPatrimonioNaoUsados(){
    	Collections.sort(listaNumerosPatrimonioNaoUsados);
    	return listaNumerosPatrimonioNaoUsados;
    }
    
    
    /**
     *  Retorna a listagem com os  n�meros de patrim�nio recuperados no termo de responsabilidade.
     *
     * @int
     */
    public List<Long> getListaNumerosPatrimonioRecuperados(){
    	listaNumerosPatrimonioRecuperados = new ArrayList<Long>(numerosPatrimonio.values());
    	Collections.sort(listaNumerosPatrimonioRecuperados);
    	return listaNumerosPatrimonioRecuperados;
    }
    
    
    
    //  set e gets para as p�ginas JSF //
    
	public String getAutor() {
		return autor;
	}
	public void setAutor(String autor) {
		this.autor = autor;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getSubtitulo() {
		return subtitulo;
	}
	public void setSubtitulo(String subtitulo) {
		this.subtitulo = subtitulo;
	}
	public int getAno() {
		return ano;
	}
	
	public void setAno(int ano) {
		this.ano = ano;
	}

	public String getAutor2() {
		return autor2;
	}

	public void setAutor2(String autor2) {
		this.autor2 = autor2;
	}

	public String getAutor3() {
		return autor3;
	}

	public void setAutor3(String autor3) {
		this.autor3 = autor3;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public int getEdicao() {
		return edicao;
	}

	public void setEdicao(int edicao) {
		this.edicao = edicao;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public Integer getPaginas() {
		return paginas;
	}

	public void setPaginas(Integer paginas) {
		this.paginas = paginas;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public int getIdLivro() {
		return idLivro;
	}

	public void setIdLivro(int idLivro) {
		this.idLivro = idLivro;
	}

	public String getTipoTombamento() {
		return tipoTombamento;
	}

	public void setTipoTombamento(String tipoTombamento) {
		this.tipoTombamento = tipoTombamento;
	}

	public String getDescricaoTermoResponsabibliodade() {
		return descricaoTermoResponsabibliodade;
	}

	public void setDescricaoTermoResponsabibliodade(String descricaoTermoResponsabibliodade) {
		this.descricaoTermoResponsabibliodade = descricaoTermoResponsabibliodade;
	}

	public void setListaNumerosPatrimonioNaoUsados(List<Long> listaNumerosPatrimonioNaoUsados) {
		this.listaNumerosPatrimonioNaoUsados = listaNumerosPatrimonioNaoUsados;
	}
	
}
