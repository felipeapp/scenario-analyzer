/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 10/09/2010
 * 
 */
package br.ufrn.sigaa.agenda.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import br.ufrn.sigaa.agenda.dominio.Evento;

/**
 * <p>Classe utilitária para realizar manipulações padrão nos eventos de uma agenda </p>
 * 
 * @author jadson
 *
 */
public class EventosUtils {

	/**
	 *  Método que cria todos os eventos virtuais de um espaço físico. Virtuais porque eles não são 
	 *  salvos no banco, são criados apartir das suas regras de recorrência.
	 *
	 * @param eventosReais
	 * @return
	 * @throws ParseException
	 */
	public static List<Evento> criaEventosVirtuais(List<Evento> eventosReais) throws ParseException{
	
		List<Evento> eventosVirtuais = new ArrayList<Evento>(); 
		
		/* ******************************************************************
		 *  Para cada evento, cria eventos virtuais que são suas recorrências
		 *  
		 *  São virtuais, porque são mostrados para o usuário como se fosse 
		 *  um evento novo, mas não é salvo no banco, a única coisa salva é
		 *  a regra de recorrência.
		 *  
		 * ******************************************************************/
		for (Evento eventoReal : eventosReais) {
			if(! eventoReal.isEventoRecorrente()){
				eventosVirtuais.add(eventoReal);
			}else{
				eventosVirtuais.addAll( eventoReal.getRecorrencia().getOcorrencias() );
			}
		}
		
		return eventosVirtuais;
	
	}
	
	
}
