/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/09/2011
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Classe que armazena os poss�veis status do grupos de pesquisa.
 * 
 * @author Leonardo Campos
 *
 */
public class StatusGrupoPesquisa {

	/** Constantes dos status */
	/** Status final do grupo de pesquisa */
	public static final int CONSOLIDADO = 1;
	/** Status intermedi�rio do Grupo de Pesquisa */
	public static final int EM_CONSOLIDACAO = 2;
	/** Status inicial de um grupo de pesquisa */
	public static final int JUNIOR = 3;
	/** Status quando um docente est� preenchendo a proposta */
	public static final int CADASTRO_EM_ANDAMENTO = 4;
	/** Quando a proposta foi aceitao pelo departamento do coordenador */
	public static final int APROVADO = 10;
	/** Status apresentado quando � encontrado erro na proposta */
	public static final int NECESSITA_CORRECAO = 6;
	/** Status quando a proposta est� submetida para a aprova��o do chefe de departamento */
	public static final int APROVACAO_DEPARTAMENTO = 7;
	/** Status quando a proposta est� submetida para a aprova��o do diretor de centro */
	public static final int APROVACAO_CENTRO = 8;
	/** Status quando a proposta est� submetida para a aprova��o do diretor de centro */
	public static final int APROVACAO_COMISSAO_PESQUISA = 9;
	
	/** Tipos de status que o Grupo de pesquisa pode apresentar */
	private static Map<Integer, String> tiposStatus = new TreeMap<Integer, String>();
	
	static{
		tiposStatus.put(CONSOLIDADO, "Consolidado");
		tiposStatus.put(EM_CONSOLIDACAO, "Em Consolida��o");
		tiposStatus.put(JUNIOR, "J�nior");
		tiposStatus.put(CADASTRO_EM_ANDAMENTO, "Cadastro em Andamento");
		tiposStatus.put(APROVADO, "Aprovado");
		tiposStatus.put(NECESSITA_CORRECAO, "Necessita Corre��o");
		tiposStatus.put(APROVACAO_DEPARTAMENTO, "Aguardando Aprova��o do Departamento");
		tiposStatus.put(APROVACAO_CENTRO, "Aguardando Aprova��o do Centro");
		tiposStatus.put(APROVACAO_COMISSAO_PESQUISA, "Aguardando Aprova��o da Comiss�o de Pesquisa");
	}
	
	public static Map<Integer, String> getTiposStatus() {
		return tiposStatus;
	}
	
	/**
	 * Retorna todos os status v�lidos (certificados)
	 * @return
	 */
	public static List<Integer> getAllCertificados(){
		List<Integer> statusCertificados =  new ArrayList<Integer>();
		statusCertificados.add( CONSOLIDADO );
		statusCertificados.add( EM_CONSOLIDACAO );
		statusCertificados.add( JUNIOR );
		return statusCertificados; 
	}

}