package br.ufrn.arq.negocio;

import br.ufrn.arq.erros.ConfiguracaoAmbienteException;
import br.ufrn.arq.parametrizacao.ParametroHelper;


/**
 * Classe padrão de pontos de extensão para parametrizações de regra de negócio 
 * através de plug-ins de código.
 * 
 * @author Gleydson Lima
 *
 */
public class DefaultExtensionPointFactory {

	/**
	 * Retorna a implementação do ponto de extensão parametrizado.
	 * @param parametro
	 * @return
	 * @throws ConfiguracaoAmbienteException
	 */
	public static Object getImplementation(String parametro) throws ConfiguracaoAmbienteException {
		
		String className = ParametroHelper.getInstance().getParametro(parametro);
		try {
			return Class.forName(className).newInstance();
		} catch (Exception e) {
			throw new ConfiguracaoAmbienteException(e);
		}
	}
	
}
