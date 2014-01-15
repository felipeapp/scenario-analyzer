/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 28/10/2004
 */
package br.ufrn.arq.parametrizacao;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import br.ufrn.arq.dominio.Parametro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConfiguracaoAmbienteException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Classe Utilitária para buscar parâmetros.
 *
 * @author Gleydson Lima
 * @author David Pereira
 *
 */
public class ParametroHelper {

	/* Instância da classe */
	private static ParametroHelper singleton = new ParametroHelper();

	/* Cache com os valores dos parâmetros */
	private static Map<String, String> cache = new HashMap<String, String>();

	/* Tempo da última vez em que o valor do parâmetro foi buscado */
	private static Map<String, Long> cacheTime = new HashMap<String, Long>();
	
	/* Cache com o tempo máximo em que o cache de parâmetros é válido */
	private static Map<String, Integer> cacheMaxTime = new HashMap<String, Integer>();

	/* Tempo máximo default para o cache dos parâmetros. */
	private static final int CACHE_TIME = 5*60*1000;

	private ParametroHelper() {
	}

	public static ParametroHelper getInstance() {
		return singleton;
	}

	/**
	 * Busca um parâmetro com o código informado para o sistema informado.
	 *
	 * @param codigo
	 * @param sistema
	 * @return
	 */
	public String getParametro(String codigo) {
		// verifica tempo do cache

		if (cache.get(codigo) != null) {
			Long tempo = cacheTime.get(codigo);
			Integer tempoMaximo = cacheMaxTime.get(codigo);
			
			if ( System.currentTimeMillis() - tempo > tempoMaximo ) {
				cache.put(codigo, null);
			}
		}

		if (cache.get(codigo) == null) {
			ParametroDao dao = new ParametroDao();
			try {
				Map<String, Object> mapa = dao.getParametroAsMap(codigo);
				if (mapa != null) { 
					String parametro = (String) mapa.get("valor");
					Integer tempoMaximo = (Integer) mapa.get("tempo_maximo");
					
					if (parametro == null)
						throw new ConfiguracaoAmbienteException("Parâmetro não definido. É necessário ter esse parâmetro para que o sistema funcione corretamente. Código do parâmetro: " + codigo + ".");
					if (tempoMaximo == null)
						tempoMaximo = CACHE_TIME;
					
					cache.put(codigo, parametro);
					cacheTime.put(codigo, System.currentTimeMillis());
					cacheMaxTime.put(codigo, tempoMaximo);
					
					return parametro;
				} else {
					throw new ConfiguracaoAmbienteException("Parâmetro não definido. É necessário ter esse parâmetro para que o sistema funcione corretamente. Código do parâmetro: " + codigo + ".");
				}
			} finally {
				dao.close();
			}
		} else {
			return cache.get(codigo);
		}
	}

	/**
	 * Esse método retorna o parâmetro que possui o código passado, convertido
	 * para int.
	 *
	 * @param codigo
	 * @return
	 */
	public int getParametroInt(String codigo) {
		String param = getParametro(codigo);
		
		try {
			return Integer.parseInt(param.trim());			
		} catch(NumberFormatException e) {
			cache.remove(codigo);
			throw new ConfiguracaoAmbienteException("O parâmetro de código \"" + codigo + "\" deveria ser numérico, mas o seu valor está \"" + param + "\".");			
		}
	}

	/**
	 * Esse método retorna o parâmetro que possui o código passado, convertido
	 * para um array de ints.
	 * @param codigo
	 * @return
	 */
	public int[] getParametroIntArray(String codigo) {
		String[] partes = getParametroStringArray(codigo);
		int[] result = new int[partes.length];

		try {
			int i = 0;
			for (String p : partes)
				result[i++] = Integer.parseInt(p.trim());
			return result;	
		} catch(NumberFormatException e) {
			String valor = getParametro(codigo);
			cache.remove(codigo);
			throw new ConfiguracaoAmbienteException("O parâmetro de código \"" + codigo + "\" deveria ser uma lista de números separados por vírgulas, mas o seu valor está \"" + valor + "\".");			
		}
	}

	/**
	 * Esse método retorna o parâmetro que possui o código passado, convertido
	 * para um array de Integer.
	 * @param codigo
	 * @return
	 */
	public Integer[] getParametroIntegerArray(String codigo) {
		String[] partes = getParametroStringArray(codigo);
		Integer[] result = new Integer[partes.length];
		
		try {
			int i = 0;
			for (String p : partes)
				result[i++] = Integer.parseInt(p.trim());
			return result;	
		} catch(NumberFormatException e) {
			String valor = getParametro(codigo);
			cache.remove(codigo);
			throw new ConfiguracaoAmbienteException("O parâmetro de código \"" + codigo + "\" deveria ser uma lista de números separados por vírgulas, mas o seu valor está \"" + valor + "\".");			
		}
	}

	/**
	 * Esse método retorna o parâmetro que possui o código passado, convertido
	 * para um array de Strings.
	 * @param codigo
	 * @return
	 */
	public String[] getParametroStringArray(String codigo) {
		String param = getParametro(codigo);
		return param.split(",");
	}

	/**
	 * Esse método retorna o parâmetro que possui o cádigo passado, convertido
	 * para int.
	 *
	 * @param codigo
	 * @return
	 */
	public long getParametroLong(String codigo) {
		String param = getParametro(codigo);
		
		try {
			return Long.parseLong(param);	
		} catch(NumberFormatException e) {
			cache.remove(codigo);
			throw new ConfiguracaoAmbienteException("O parâmetro de código \"" + codigo + "\" deveria ser numérico, mas o seu valor está \"" + param + "\".");			
		}
	}

	/**
	 * Esse método retorna o parâmetro que possui o código passado no formato
	 * double.
	 *
	 * @param codigo
	 * @return
	 */
	public double getParametroDouble(String codigo) {
		String param = getParametro(codigo);
		
		try {
			return Double.parseDouble(param);	
		} catch(NumberFormatException e) {
			cache.remove(codigo);
			throw new ConfiguracaoAmbienteException("O parâmetro de código \"" + codigo + "\" deveria ser numérico decimal, mas o seu valor está \"" + param + "\".");			
		}
		
	}

	/**
	 * Esse método retorna o parâmetro que possui o código passado no formato
	 * double.
	 *
	 * @param codigo
	 * @return
	 */
	public Date getParametroDate(String codigo) {
		String param = getParametro(codigo);
		
		try {
			return new Date(Long.parseLong(param));	
		} catch(NumberFormatException e) {
			cache.remove(codigo);
			throw new ConfiguracaoAmbienteException("O parâmetro de código \"" + codigo + "\" deveria ser um valor numérico, mas o seu valor está \"" + param + "\".");			
		}
	}

	/**
	 * Retorna o parâmetro que possui o código informado no formato boolean.
	 *
	 * @param bloqueioConsolidacaoAvaliacao
	 * @return
	 */
	public boolean getParametroBoolean(String codigo) {
		String param = getParametro(codigo);
		return new Boolean(param);
	}

	/**
	 * Retorna o parâmetro que possui o código informado no formato Map.
	 * @param codigo
	 * @return
	 */
	public Map<String, String> getParametroMap(String codigo) {
		String param = getParametro(codigo);
		Map<String, String> result = new HashMap<String, String>();
		
		for (String linha : param.split("\n")) {
			String[] partes = linha.split("=");
			result.put(partes[0].trim(), partes[1].trim());
		}
		
		return result;
	}
	
	/**
	 * Atualiza um parâmetro no banco de dados e o remove do cache.
	 * @param usuario
	 * @param sistema
	 * @param codigo
	 * @param valor
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public void atualizaParametro(UsuarioGeral usuario, int sistema, String codigo, String valor) throws NegocioException, ArqException {
		ParametroDao dao = new ParametroDao();
		try {
			Parametro parametro = dao.findByPrimaryKey(codigo);
			parametro.setValor(valor);
			dao.salvar(parametro);

			cache.remove(codigo);
		} finally {
			dao.close();
		}
	}

	/**
	 * Apaga todo o cache de parâmetros do sistema informado.
	 *
	 * @param sistema
	 */
	public void resetCache(Integer sistema) {
		cache = new HashMap<String, String>();
	}

}