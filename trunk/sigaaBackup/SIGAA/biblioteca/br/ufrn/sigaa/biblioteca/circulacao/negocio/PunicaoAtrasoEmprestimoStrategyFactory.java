/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p> Fábrica para retorna as estratégias de punição por atraso na biblioteca
 * dependendo de como o sistema está configurado para trabalhar
 * </p>
 *
 * 
 * @author jadson
 *
 */
public class PunicaoAtrasoEmprestimoStrategyFactory {

	
	/**
	 * 
	 * <p>Método que retorna as estragégias usadas para a punição do usuário.</p>  
	 * 
	 * <p><strong>Responsável por realizar a derivação do produto em tempo de execução.</strong></p>
	 * 
	 * <p>A partir da estratégias retornas o sistema saberá o que deve executar.</p> 
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
	 * Verifica se o sistema está utilizando a punição de multas da biblioteca.
	 *
	 * @boolean
	 */
	public static boolean sistemaTrabalhaComMultas(){
		return ParametroHelper.getInstance().getParametroBoolean(ParametrosBiblioteca.SISTEMA_TRABALHA_COM_MULTA);
	}
	
	/**
	 * Verifica se o sistema está utilizando a punição de suspensão da biblioteca.
	 *
	 * @boolean
	 */
	public static boolean sistemaTrabalhaComSuspensao(){
		return ParametroHelper.getInstance().getParametroBoolean(ParametrosBiblioteca.SISTEMA_TRABALHA_COM_SUSPENSAO);
	}

	
	/**
	 * Retorna a estratégia de multa utilizada
	 *
	 * @return
	 */
	public MultaStrategyDefault getEstrategiaMulta(){
		String nomeClasseImplementaResgrasMulta = ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.NOME_CLASSE_IMPLEMENTA_ESTRATEGIA_MULTA);
		MultaStrategyDefault estrategiaMulta = ReflectionUtils.newInstance( nomeClasseImplementaResgrasMulta );
		return estrategiaMulta;
	}
	
	
	/**
	 * Retorna a estratégia de suspensão utilizada
	 *
	 * @return
	 */
	public SuspensaoStrategyDefault getEstrategiaSuspensao(){
		String nomeClasseImplementaResgrasSuspensao = ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.NOME_CLASSE_IMPLEMENTA_ESTRATEGIA_SUSPENSAO);
		SuspensaoStrategyDefault estrategiaSuspensao = ReflectionUtils.newInstance( nomeClasseImplementaResgrasSuspensao );
		return estrategiaSuspensao;
	}
	
}
