/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 14/01/2010
 */
package br.ufrn.arq.tasks;

import java.util.Map.Entry;

import br.ufrn.arq.aop.ProfilingAspect;
import br.ufrn.arq.dao.ProfilingDao;

/**
 * Timer para gravar, de tempos em tempos, informações de profiling
 * capturadas pelo aspecto {@link ProfilingAspect}.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class TimerProfiling extends TarefaTimer {

	@Override
	public void run() {
		
		ProfilingDao dao = new ProfilingDao();
		
		for(Entry<String, String> entry : ProfilingAspect.COUNT_CALL_MAP.entrySet()) {
			String classe = getClasse(entry.getKey());
			String metodo = getMetodo(entry.getKey());
			String countStr = entry.getValue();
			int countCall = Integer.parseInt(countStr);	
			dao.atualizaCountCall(classe, metodo, countCall);
			
			ProfilingAspect.COUNT_CALL_MAP.put(entry.getKey(), "0");
		}
		
		for(Entry<String, String> entry : ProfilingAspect.MAX_TIME_MAP.entrySet()) {
			String classe = getClasse(entry.getKey());
			String metodo = getMetodo(entry.getKey());
			String timeStr = entry.getValue();
			long maxTime = Long.parseLong(timeStr);	
			dao.atualizaMaxTime(classe, metodo, maxTime);
			
			ProfilingAspect.MAX_TIME_MAP.put(entry.getKey(), "0");
		}
		
		for(Entry<String, String> entry : ProfilingAspect.TIME_MAP.entrySet()) {
			String classe = getClasse(entry.getKey());
			String metodo = getMetodo(entry.getKey());
			String timeStr = entry.getValue();
			String[] timeArray = timeStr.split("\\|");
			long meanTime = Long.parseLong(timeArray[0]);
			int count = Integer.parseInt(timeArray[1]);
			dao.atualizaMeanTime(classe, metodo, count, meanTime);
			
			ProfilingAspect.TIME_MAP.put(entry.getKey(), "0|0");
		}
		
	}

	private String getMetodo(String key) {
		return key.substring(key.indexOf("#") + 1);
	}

	private String getClasse(String key) {
		return key.substring(0, key.indexOf("#"));
	}
	
}

