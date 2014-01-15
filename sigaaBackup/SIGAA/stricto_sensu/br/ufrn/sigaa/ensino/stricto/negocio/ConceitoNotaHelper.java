/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 23/07/2010
 */
package br.ufrn.sigaa.ensino.stricto.negocio;

import java.util.HashMap;
import java.util.Map;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.ConfiguracaoAmbienteException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.ensino.stricto.ConceitoNotaDao;

/**
 * Classe com m�todos utilit�rios para notas representadas
 * por conceitos.
 * 
 * @author David Pereira
 *
 */
public class ConceitoNotaHelper {

	/** Mapeia a descri��o de um conceito ao seu valor, utilizado por quest�es de efici�ncia */
	private static Map <String, Double> mapaConceitoValor = new HashMap <String, Double> ();

	/** Mapeia o valor de um conceito a sua descri��o, utilizado por quest�es de efici�ncia */
	private static Map<Double, String> mapaValorConceito = new HashMap <Double, String> ();
	
	/**
	 * Busca o valor de um conceito pela sua descri��o.
	 * @param descricao
	 * @return
	 */
	public static Double getValorConceito(String conceito) {
		ConceitoNotaDao dao = null;
		
		Double nota = mapaConceitoValor.get(conceito);
		
		if (nota == null)
			try {
				dao = DAOFactory.getInstance().getDAO(ConceitoNotaDao.class);
				nota = dao.findValorByDescricao(conceito);
				mapaConceitoValor.put(conceito, nota);
				mapaValorConceito.put(nota, conceito);
			}catch(Exception e) {
				//os valores dos conceitos devem estar configurados na tabela stricto_sensu.conceito_nota
				throw new ConfiguracaoAmbienteException("O valor para o conceito '" + conceito + "' n�o foi devidamente configurado.");
			} finally {
				if (dao != null)
					dao.close();
			}
			
			return nota;
	}
	
	/**
	 * Busca a descri��o de um conceito pelo seu valor.
	 * @param descricao
	 * @return
	 */
	public static String getDescricaoConceito( Double valor ) {
		ConceitoNotaDao dao = null;
		
		String conceito = mapaValorConceito.get(valor);
		
		if (conceito == null)
			try {
				dao = DAOFactory.getInstance().getDAO(ConceitoNotaDao.class);
				conceito = dao.findDescricaoByValor(valor);
				mapaValorConceito.put(valor,conceito);
				mapaConceitoValor.put(conceito, valor);
			} catch (Exception e) {			
				throw new ConfiguracaoAmbienteException("A descri��o do conceito n�o foi devidamente configurada.");
			} 
		
		finally {
				if (dao != null)
					dao.close();
			}
		
		return conceito;
	}

}
