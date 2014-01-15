/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 02/03/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
 *
 * <p> F�brica para retorna as estrat�gias de puni��o por atraso na biblioteca
 * dependendo de como o sistema est� configurado para trabalhar
 * </p>
 *
 * 
 * @author jadson
 *
 */
public class PunicaoAtrasoEmprestimoStrategyFactory {

	
	/**
	 * 
	 * <p>M�todo que retorna as estrag�gias usadas para a puni��o do usu�rio.</p>  
	 * 
	 * <p><strong>Respons�vel por realizar a deriva��o do produto em tempo de execu��o.</strong></p>
	 * 
	 * <p>A partir da estrat�gias retornas o sistema saber� o que deve executar.</p> 
	 *
	 * @List<PunicaoAtrasoEmprestimoStrategy>
	 */
	public List<PunicaoAtrasoEmprestimoStrategy> getEstrategiasPunicao(){
		
		List<PunicaoAtrasoEmprestimoStrategy> estrategias = new ArrayList<PunicaoAtrasoEmprestimoStrategy>();
	
		if(sistemaTrabalhaComSuspensao()){
			estrategias.add( getEstrategiaSuspensao() );
		}
		if(sistemaTrabalhaComMultas()){
			estrategias.add( getEstrategiaMulta() );
		}
		
		return estrategias;
		
	}
	
	/**
	 * Verifica se o sistema est� utilizando a puni��o de multas da biblioteca.
	 *
	 * @boolean
	 */
	public static boolean sistemaTrabalhaComMultas(){
		return ParametroHelper.getInstance().getParametroBoolean(ParametrosBiblioteca.SISTEMA_TRABALHA_COM_MULTA);
	}
	
	/**
	 * Verifica se o sistema est� utilizando a puni��o de suspens�o da biblioteca.
	 *
	 * @boolean
	 */
	public static boolean sistemaTrabalhaComSuspensao(){
		return ParametroHelper.getInstance().getParametroBoolean(ParametrosBiblioteca.SISTEMA_TRABALHA_COM_SUSPENSAO);
	}

	
	/**
	 * Retorna a estrat�gia de multa utilizada
	 *
	 * @return
	 */
	public MultaStrategyDefault getEstrategiaMulta(){
		String nomeClasseImplementaResgrasMulta = ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.NOME_CLASSE_IMPLEMENTA_ESTRATEGIA_MULTA);
		MultaStrategyDefault estrategiaMulta = ReflectionUtils.newInstance( nomeClasseImplementaResgrasMulta );
		return estrategiaMulta;
	}
	
	
	/**
	 * Retorna a estrat�gia de suspens�o utilizada
	 *
	 * @return
	 */
	public SuspensaoStrategyDefault getEstrategiaSuspensao(){
		String nomeClasseImplementaResgrasSuspensao = ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.NOME_CLASSE_IMPLEMENTA_ESTRATEGIA_SUSPENSAO);
		SuspensaoStrategyDefault estrategiaSuspensao = ReflectionUtils.newInstance( nomeClasseImplementaResgrasSuspensao );
		return estrategiaSuspensao;
	}
	
}
