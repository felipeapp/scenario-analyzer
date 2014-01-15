/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 16/08/2010
 * 
 */
package br.ufrn.sigaa.arq.dao.agenda;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.agenda.dominio.Evento;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;

/**
 *
 * <p>Dao para busca de eventos persistidos da agenda </p>

 * 
 * @author jadson
 *
 */
public class EventoDao extends GenericSigaaDAO{
	
	
	/**
	 * <p>Método que retorna os eventos de uma agenda que ocorrem entre o período passado e com o status passado</p>
	 * 
	 * 
	 * @return
	 * @throws DAOException
	 * @throws ParseException 
	 */
	public List<Evento>  findEventosDaAgendaNoPeriodo(final int idAgenda, final Date dataInicio, final Date dataFim, final Short status) throws DAOException, ParseException {

		long tempo = System.currentTimeMillis();
		
		StringBuilder hql = new StringBuilder("SELECT e.id, e.titulo, e.dataInicio, e.dataFim, e.diaTodo, e.descricao, e.local, r.iCal, er.iCal ");
		
		
		hql.append(" FROM Evento e ");
		hql.append(" LEFT JOIN e.recorrencia r ");
		hql.append(" LEFT  JOIN e.excecaoRecorrencia er ");
		
		hql.append(" WHERE e.agenda.id = "+idAgenda+" ");
		
		if(status != null) {
			hql.append(" AND e.status = "+status);
		}
		
		// Busca todos os eventos que ocorren no  período do evento passado//
		hql.append(" AND ( (   (e.dataInicio between :dataInicio and :dataFim ) OR ( e.dataFim between  :dataInicio and :dataFim  )   ) ");
		hql.append(" OR (   (:dataInicio between  e.dataInicio and e.dataFim ) or (   :dataFim between  e.dataInicio and e.dataFim   ) )  ) ");
		
		Query q = getSession().createQuery(hql.toString());
		
		q.setTimestamp("dataInicio", dataInicio);
		q.setTimestamp("dataFim", dataFim);
		
		@SuppressWarnings("unchecked")
		List <Object> lista = q.list();
			
		List<Evento> resultado = new ArrayList<Evento>();
			
		int cont = 0;
		
		for (Object object : lista) {
			
			cont = 0;
			
			Object[] array = (Object[]) object;
			
			Evento e = new Evento ((Integer) array[cont++]
										, (String) array[cont++]
										, (Date)array[cont++]
										, (Date)  array[cont++]
										, (Boolean) array[cont++]
										, (String)array[cont++]
										, (String)array[cont++]
										, (String)array[cont++]  
										, (String)array[cont++] );
			
			/* ***************************************************************************************
			 *  Guarda as datas dos eventos salvos no banco, já que vão ser criados vários outros 
			 *
			 * eventos a partir das regras de recorrência com datas diferentes, é preciso saber a data
			 * original do evento em alguns casos
			 * 
			 * Só utilizado caso a agenda possua eventos recorrentes, caso contrário as datas originais
			 * vão sempre ser iguais as datas do eventos
			 * 
			 */ 
			e.setDataInicioOriginal(e.getDataInicio());
			e.setDataFimOriginal(e.getDataFim());
			
			resultado.add(e );
				
		}
			
		System.out.println(" $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  ");
		System.out.println("Consulta eventos pagina demorou: "+ (System.currentTimeMillis() - tempo) +" ms  com "+resultado.size()+" resultados");
		System.out.println("Parametros: data Inicio: "+dataInicio+"       dataFim:"+dataFim);
		return resultado;
	}
	
}
