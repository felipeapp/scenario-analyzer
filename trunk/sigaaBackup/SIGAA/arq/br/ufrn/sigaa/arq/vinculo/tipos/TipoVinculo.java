/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 01/07/2011
 */

package br.ufrn.sigaa.arq.vinculo.tipos;

import java.io.Serializable;

import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculo;

/**
 * Interface que deve ser implementada por todos os v�nculos
 * 
 * 
 * @author Henrique Andr�
 *
 */
public interface TipoVinculo extends Serializable {
	
	/**
	 * Descri��o do tipo do v�nculo
	 * @return
	 */
	public String getTipo();
	
	/**
	 * Indica se o v�nculo � ativo ou inativo
	 * @return
	 */
	public boolean isAtivo();
	
	/**
	 * Um identificador �nico que representa o v�nculo
	 * @return
	 */
	public Object getIdentificador();
	
	/**
	 * Outras informa��es relevantes sobre o v�nculo
	 * 
	 * @return
	 */
	public String getOutrasInformacoes();
	
	/**
	 * Estrategia que ir� popular este v�nculo
	 * 
	 * @return
	 */
	public EstrategiaPopularVinculo getEstrategia();
	
	/**
	 * Ordem que ser� usada para definir a ordena��o
	 * 
	 * @return
	 */
	public int getOrdem();	
	
	/**
	 * Indica se � Discente
	 * 
	 * @return
	 */
	public boolean isDiscente();
	
	/**
	 * Indica se � Servidor
	 * 
	 * @return
	 */
	public boolean isServidor();
	
	/**
	 * Indica se � Secret�rio
	 * 
	 * @return
	 */
	public boolean isSecretario();
	
	/**
	 * Indica se � Respons�vel
	 * 
	 * @return
	 */
	public boolean isResponsavel();
	
	/**
	 * Indica se � Docente Externo
	 * 
	 * @return
	 */
	public boolean isDocenteExterno();
	
	/**
	 * Indica se � Tutor
	 * 
	 * @return
	 */
	public boolean isTutor();
	
	
	/**
	 * Indica se � tutor do IMD
	 * @return
	 */
	public boolean isTutorIMD();
	
	/**
	 * Indica se � Coordeandor de Polo
	 * 
	 * @return
	 */
	public boolean isCoordenacaoPolo();
	
	/**
	 * Indica se � Concedente de Est�gio
	 * 
	 * @return
	 */
	public boolean isConcedenteEstagio();	
	
	/**
	 * Indica se � Familiar de um discente m�dio
	 * 
	 * @return
	 */
	public boolean isFamiliar();		

	/**
	 * Indica se � o coordenador geral de um programa em rede
	 * 
	 * @return
	 */
	public boolean isCoordenadorGeralRede();
	
	/**
	 * Indica se � o coordenador geral de um programa em rede
	 * 
	 * @return
	 */
	public boolean isCoordenadorUnidadeRede();
}
