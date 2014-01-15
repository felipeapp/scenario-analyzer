/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p>Classe utilit�ria para realizar manipula��es padr�o nos eventos de uma agenda </p>
 * 
 * @author jadson
 *
 */
public class EventosUtils {

	/**
	 *  M�todo que cria todos os eventos virtuais de um espa�o f�sico. Virtuais porque eles n�o s�o 
	 *  salvos no banco, s�o criados apartir das suas regras de recorr�ncia.
	 *
	 * @param eventosReais
	 * @return
	 * @throws ParseException
	 */
	public static List<Evento> criaEventosVirtuais(List<Evento> eventosReais) throws ParseException{
	
		List<Evento> eventosVirtuais = new ArrayList<Evento>(); 
		
		/* ******************************************************************
		 *  Para cada evento, cria eventos virtuais que s�o suas recorr�ncias
		 *  
		 *  S�o virtuais, porque s�o mostrados para o usu�rio como se fosse 
		 *  um evento novo, mas n�o � salvo no banco, a �nica coisa salva �
		 *  a regra de recorr�ncia.
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
