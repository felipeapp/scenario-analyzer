/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 01/07/2011
 */

package br.ufrn.sigaa.arq.vinculo.tipos;

import java.io.Serializable;

import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculo;

/**
 * Interface que deve ser implementada por todos os vínculos
 * 
 * 
 * @author Henrique André
 *
 */
public interface TipoVinculo extends Serializable {
	
	/**
	 * Descrição do tipo do vínculo
	 * @return
	 */
	public String getTipo();
	
	/**
	 * Indica se o vínculo é ativo ou inativo
	 * @return
	 */
	public boolean isAtivo();
	
	/**
	 * Um identificador único que representa o vínculo
	 * @return
	 */
	public Object getIdentificador();
	
	/**
	 * Outras informações relevantes sobre o vínculo
	 * 
	 * @return
	 */
	public String getOutrasInformacoes();
	
	/**
	 * Estrategia que irá popular este vínculo
	 * 
	 * @return
	 */
	public EstrategiaPopularVinculo getEstrategia();
	
	/**
	 * Ordem que será usada para definir a ordenação
	 * 
	 * @return
	 */
	public int getOrdem();	
	
	/**
	 * Indica se é Discente
	 * 
	 * @return
	 */
	public boolean isDiscente();
	
	/**
	 * Indica se é Servidor
	 * 
	 * @return
	 */
	public boolean isServidor();
	
	/**
	 * Indica se é Secretário
	 * 
	 * @return
	 */
	public boolean isSecretario();
	
	/**
	 * Indica se é Responsável
	 * 
	 * @return
	 */
	public boolean isResponsavel();
	
	/**
	 * Indica se é Docente Externo
	 * 
	 * @return
	 */
	public boolean isDocenteExterno();
	
	/**
	 * Indica se é Tutor
	 * 
	 * @return
	 */
	public boolean isTutor();
	
	
	/**
	 * Indica se é tutor do IMD
	 * @return
	 */
	public boolean isTutorIMD();
	
	/**
	 * Indica se é Coordeandor de Polo
	 * 
	 * @return
	 */
	public boolean isCoordenacaoPolo();
	
	/**
	 * Indica se é Concedente de Estágio
	 * 
	 * @return
	 */
	public boolean isConcedenteEstagio();	
	
	/**
	 * Indica se é Familiar de um discente médio
	 * 
	 * @return
	 */
	public boolean isFamiliar();		

	/**
	 * Indica se é o coordenador geral de um programa em rede
	 * 
	 * @return
	 */
	public boolean isCoordenadorGeralRede();
	
	/**
	 * Indica se é o coordenador geral de um programa em rede
	 * 
	 * @return
	 */
	public boolean isCoordenadorUnidadeRede();
}
