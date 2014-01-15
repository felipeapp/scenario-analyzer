/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Classe que representa um campo genérico do padrão MARC seja ele Campo de Controle ou de Dados
 *
 * Essa classe não é para ser salva no banco é apenas para criar uma abstração.
 * 
 * @author jadson
 * @since 08/08/2008
 * @version 1.0 criação da classe
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
	 *   Se o campo for de um título vai possuir essa variável.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_titulo_catalografico", referencedColumnName = "id_titulo_catalografico")
	protected TituloCatalografico tituloCatalografico;

	
	/** Se o campo for de uma autoridade vai possuir essa variável. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_autoridade", referencedColumnName = "id_autoridade")
	protected Autoridade autoridade;

	
	
	/**  Se o campo for de um artigo de periódico. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_artigo_de_periodico", referencedColumnName = "id_artigo_de_periodico")
	protected ArtigoDePeriodico artigo;
	
	
	
	/**
	 * A posição do campo na catalogação, na planilha os campos devem ser exibidos na ordem crescente de acordo com este atributo. 
	 * O Hibernate usa para manter a posição dos campos.
	 * É iniciado com -1 para campos não persistidos, nesse caso devem ficar por último na ordenação 
	 * quando comparados com campos de dados persistidos com não persistidos ainda. 
	 **/
	@Column(name = "posicao", nullable= false)
	private int posicao = -1;
	
	
	/**
	 * <p> Guarda temporariamente o id da planilha de catalogação a partir da qual esse campo foi gerado.</p>
	 * 
	 * <p> Quando é usado a catalogação simplificada o sistema adiciona todos os campos presentes na 
	 * planilha escolhida pelo usuário, caso o usuário escolha outra planilha os campos adicionados pela planilha anterior devem ser removidos.</p>
	 */
	@Transient
	private int idPlanilhaGerado;
	
	/** Guarda um identificador temporário para pode recuperar informações dos campos ainda não persistidos. */
	@Transient
	private int identificadorTemp;
	
	
	/**
	 *    Checa se um campo possui etiqueta válida ou não.
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
	 *    Checa se um campo é um campo de controle ou de dados.
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
