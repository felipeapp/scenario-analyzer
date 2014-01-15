/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 *
 * Criado em 11/03/2010
 * Autor: Jean Guerethes
 * 
 */
package br.ufrn.sigaa.ava.dominio;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jfree.data.category.DefaultCategoryDataset;

import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.ava.jsf.ControllerTurmaVirtual;
import de.laures.cewolf.DatasetProduceException;
import de.laures.cewolf.DatasetProducer;

/**
 * Classe responsável pela geração do gráfico com o acesso dos discente à turma virtual. 
 * 
 * @author Jean Guerethes
 */
public class GraficoAcessoTurma extends ControllerTurmaVirtual implements DatasetProducer, Serializable {

	/**Constante que representar um segundo em milisegundos.*/
	public static final int UM_SEGUNDO = 1000;
	/**Tempo de time out para geracao do grafico.*/
	public static final int TIMEOUT = 10 * UM_SEGUNDO;

	public String getProducerId() {
		return "AcessoTurma";
	}

	/**
	 * Verifica o Timeout
	 */
	@SuppressWarnings("unchecked")
	public boolean hasExpired(Map arg0, Date data) {
		return (System.currentTimeMillis() - data.getTime()) > TIMEOUT;
	}
	/**
	 * Gera o grafico de acessos da dos docente turma virtual de acordo com os acessos passados no paramtro.
	 */
	@SuppressWarnings("unchecked")
	public Object produceDataset(Map params) throws DatasetProduceException {
		
		List<LogLeituraSigaaTurmaVirtual> logLeituraTurmaVirtual = (List<LogLeituraSigaaTurmaVirtual>) params.get("lista");
		
		DefaultCategoryDataset ds = new DefaultCategoryDataset();
		
		int total=0, idAtual = 0, idSemanaAtual=0, count=0, idMesAtual=0,quantidadeTotalAcessos = 0 ;
		
		for (LogLeituraSigaaTurmaVirtual l : logLeituraTurmaVirtual){
				quantidadeTotalAcessos += l.getQntEntrouTurmaVirtual();
		}
		
		
		
		for (LogLeituraSigaaTurmaVirtual lista : logLeituraTurmaVirtual){
			// Detalhamento Diário
			if ((lista.isDetalharSemana() || quantidadeTotalAcessos < 60) && !lista.isDetalharMes() ) {
				if ((idAtual == 0 && idMesAtual == 0) || (idAtual == lista.getDia() && idMesAtual == lista.getMes())){ 
					total = total + lista.getQntEntrouTurmaVirtual();
				 	idAtual = lista.getDia();
				 	idMesAtual = lista.getMes();
				 	count++;
				}else{
					ds.addValue(total, "Acesso Dia/Mês", new String(String.valueOf(idAtual +"/"+ idMesAtual)));
					idAtual = lista.getDia();
					idMesAtual = lista.getMes();
					total = 0;
					total = total +  lista.getQntEntrouTurmaVirtual();
					count++;
				}
				if (logLeituraTurmaVirtual.size() == count) {
					ds.addValue(total, "Acesso Dia/Mês", new String(String.valueOf(idAtual +"/"+ idMesAtual)));
				}
			}
			// Detalhamento Semanal			
			else if (lista.isDetalharMes() || (quantidadeTotalAcessos >= 60 && quantidadeTotalAcessos < 120 )) {
				Calendar c = Calendar.getInstance();
				c.set(getAnoAtual(), lista.getMes(), lista.getDia());
				if ((lista.getMes() == idAtual && c.get(Calendar.DAY_OF_WEEK_IN_MONTH) == idSemanaAtual) || idAtual==0 || idSemanaAtual==0) {
					total = total + lista.getQntEntrouTurmaVirtual();
					idAtual = lista.getMes();
					idSemanaAtual = c.get(Calendar.DAY_OF_WEEK_IN_MONTH);
					count++;
				} else {
					ds.addValue(total == 0 ? lista.getQntEntrouTurmaVirtual() : total, "Semana/Mês", new String( 
								String.valueOf( idSemanaAtual + "° Sem./" + CalendarUtils.getMesAbreviado(idAtual) )));
					total = 0;
					total = total + lista.getQntEntrouTurmaVirtual();
					count++;
					idAtual = lista.getMes();
					idSemanaAtual = c.get(Calendar.DAY_OF_WEEK_IN_MONTH);

				}
				
				if (logLeituraTurmaVirtual.size() == count ) {
					ds.addValue(total == 0 ? lista.getQntEntrouTurmaVirtual() : total, "Semana/Mês", new String( 
							String.valueOf( idSemanaAtual + "° Sem/" + CalendarUtils.getMesAbreviado(idAtual) )));
				}

			} //Detalhamento Mensal
			if (quantidadeTotalAcessos >= 120) {
				if (lista.getMes() == idAtual || idAtual == 0) {
					total = total + lista.getQntEntrouTurmaVirtual();
					idAtual = lista.getMes();
					count++;
				} else {
					ds.addValue(total == 0 ? lista.getQntEntrouTurmaVirtual() : total, "Acesso Mensal", new String(
								String.valueOf( CalendarUtils.getNomeMes(idAtual))));
					total = 0;
					total = total + lista.getQntEntrouTurmaVirtual();
					idAtual = lista.getMes();
					count++;
				}
				
				if (logLeituraTurmaVirtual.size() == count) {
					ds.addValue(total == 0 ? lista.getQntEntrouTurmaVirtual() : total, "Acesso Mensal", new String(
							String.valueOf( CalendarUtils.getNomeMes(idAtual))));
				}

			}
		} 
		return ds;
	}

}