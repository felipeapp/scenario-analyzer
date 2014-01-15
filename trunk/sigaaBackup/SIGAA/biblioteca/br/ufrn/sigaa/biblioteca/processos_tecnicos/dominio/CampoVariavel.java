/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 08/08/2008
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.StringUtils;

/**
 *
 * Classe que representa um campo gen�rico do padr�o MARC seja ele Campo de Controle ou de Dados
 *
 * Essa classe n�o � para ser salva no banco � apenas para criar uma abstra��o.
 * 
 * @author jadson
 * @since 08/08/2008
 * @version 1.0 cria��o da classe
 *
 */
@MappedSuperclass
public abstract class CampoVariavel implements Serializable{

	/**
	 * Todo campo deve ter uma etiqueta.
	 */
	@ManyToOne(cascade={}, fetch = FetchType.LAZY )
	@JoinColumn(name = "id_etiqueta", referencedColumnName = "id_etiqueta", nullable=false)
	protected Etiqueta etiqueta;

	
	/**
	 *   Se o campo for de um t�tulo vai possuir essa vari�vel.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_titulo_catalografico", referencedColumnName = "id_titulo_catalografico")
	protected TituloCatalografico tituloCatalografico;

	
	/** Se o campo for de uma autoridade vai possuir essa vari�vel. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_autoridade", referencedColumnName = "id_autoridade")
	protected Autoridade autoridade;

	
	
	/**  Se o campo for de um artigo de peri�dico. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_artigo_de_periodico", referencedColumnName = "id_artigo_de_periodico")
	protected ArtigoDePeriodico artigo;
	
	
	
	/**
	 * A posi��o do campo na cataloga��o, na planilha os campos devem ser exibidos na ordem crescente de acordo com este atributo. 
	 * O Hibernate usa para manter a posi��o dos campos.
	 * � iniciado com -1 para campos n�o persistidos, nesse caso devem ficar por �ltimo na ordena��o 
	 * quando comparados com campos de dados persistidos com n�o persistidos ainda. 
	 **/
	@Column(name = "posicao", nullable= false)
	private int posicao = -1;
	
	
	/**
	 * <p> Guarda temporariamente o id da planilha de cataloga��o a partir da qual esse campo foi gerado.</p>
	 * 
	 * <p> Quando � usado a cataloga��o simplificada o sistema adiciona todos os campos presentes na 
	 * planilha escolhida pelo usu�rio, caso o usu�rio escolha outra planilha os campos adicionados pela planilha anterior devem ser removidos.</p>
	 */
	@Transient
	private int idPlanilhaGerado;
	
	/** Guarda um identificador tempor�rio para pode recuperar informa��es dos campos ainda n�o persistidos. */
	@Transient
	private int identificadorTemp;
	
	
	/**
	 *    Checa se um campo possui etiqueta v�lida ou n�o.
	 *    Serve para saber se o campo pode ser validado ou salvo por exemplo. 
	 *
	 * @return
	 */
	public final boolean possuiEtiquetaValida() {
		if(etiqueta != null){
			
			if(StringUtils.notEmpty( etiqueta.getTag()))
				return true;
			else
				return false;
		}else{
			return false;
		}
	}

	
	
	/**
	 * 
	 *    Checa se um campo � um campo de controle ou de dados.
	 *
	 * @return
	 * @throws NegocioException 
	 */
	public final boolean isCampoControle(){
		if(etiqueta != null && etiqueta.isEtiquetaControle() )
			return true;
		return false;
	}
	
	
	
	
	// sets e gets
	
	public TituloCatalografico getTituloCatalografico() {
		return tituloCatalografico;
	}

	public void setTituloCatalografico(TituloCatalografico tituloCatalografico) {
		this.tituloCatalografico = tituloCatalografico;
	}

	public Etiqueta getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(Etiqueta etiqueta) {
		this.etiqueta = etiqueta;
	}

	public void setAutoridade(Autoridade autoridade) {
		this.autoridade = autoridade;
	}

	public ArtigoDePeriodico getArtigo() {
		return artigo;
	}

	public void setArtigo(ArtigoDePeriodico artigo) {
		this.artigo = artigo;
	}

	public int getPosicao() {
		return posicao;
	}

	public void setPosicao(int posicao) {
		this.posicao = posicao;
	}
	
	public int getIdPlanilhaGerado() {
		return idPlanilhaGerado;
	}

	public void setIdPlanilhaGerado(int idPlanilhaGerado) {
		this.idPlanilhaGerado = idPlanilhaGerado;
	}

	public int getIdentificadorTemp() {
		return identificadorTemp;
	}

	public void setIdentificadorTemp(int identificadorTemp) {
		this.identificadorTemp = identificadorTemp;
	}
	
	
	
}
