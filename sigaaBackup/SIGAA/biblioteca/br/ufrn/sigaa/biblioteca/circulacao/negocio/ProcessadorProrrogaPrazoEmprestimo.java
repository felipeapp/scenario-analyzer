/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on 23/10/2008
 *
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;

/**
 *       Processador para prorrogar os prazos dos empr�stimos
 *   
 *       Prorroga todos os empr�stimos cujos prazos caem dentro do per�odo passado no movimento. Os
 * novos prazos ser�o para um dia depois do final do prazo passado, se o prazo cair no s�bado ou 
 * domingo ainda � prorrogado para a segunda-feira automaticamente. 
 *
 * @author jadson
 * @since 23/10/2008
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorProrrogaPrazoEmprestimo  extends ProcessadorCadastro{

	/**
	 * Executa a opera��o para prorroga��o de todos os empr�stimos com prazos informado
	 */
	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {

		EmprestimoDao emprestimoDao = null;
		
		try{
			
			validate(movimento);
			
			
			MovimentoProrrogaPrazoEmprestimo myMov = (MovimentoProrrogaPrazoEmprestimo) movimento;
			
			emprestimoDao = getDAO(EmprestimoDao.class, movimento);
			
		
			Date inicio = CalendarUtils.configuraTempoDaData(myMov.getDataInicioPeriodo(), 0, 0, 0, 0);
			Date fim = CalendarUtils.configuraTempoDaData(myMov.getDataFinalPeriodo(), 23, 59, 59, 99);
			
			// obs.: Tem que realizar a pesquisa do dia in�cio 00:00:00 ate o dia fim 23:59:59
			
			List<Emprestimo> emprestimosAtivos = emprestimoDao.findEmprestimosAtivosByUsuarioMaterialBiblioteca(null, null, null, false, null, null, inicio, fim, null);
			
			if(emprestimosAtivos.size() == 0)
				throw new NegocioException("N�o existem empr�stimos ativos com o prazo vencendo no per�odo informado!");
			
			// Adiciona Um dia � data Fim e livra os finais de semana
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(CalendarUtils.adicionaUmDia(fim));  
			CalendarUtils.adicionaDiasFimDeSemana(calendar);
			Date novoPrazo = calendar.getTime();
			
			// Todos os empr�stimos que vencem entre as duas datas devem ficar com prazo final igual � data final mais um dia.
			for (Emprestimo emprestimo : emprestimosAtivos) {
				emprestimo.setPrazo(novoPrazo);
				emprestimoDao.update(emprestimo);	
			}
		
		}catch (NegocioException ne) {
			throw ne;
		}catch(Exception e){
			throw new ArqException(e);
		}finally{
			if(emprestimoDao != null){
				emprestimoDao.close();
			}
		}
		
		
		
		
		
		return null;
	}

	/**
	 * Valida os dados do movimento
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoProrrogaPrazoEmprestimo myMov = (MovimentoProrrogaPrazoEmprestimo) mov;
		
		Date inicio = myMov.getDataInicioPeriodo();
		Date fim = myMov.getDataFinalPeriodo();
		
		// a data fim tem que ser maior que a data de in�cio e a data de in�cio tem que ser >= a de hoje
		
		
		if(inicio == null) throw new NegocioException("Digite a data de in�cio do per�odo");
		
		if(fim == null) throw new NegocioException("Digite a data de final do per�odo");
		
		
		
		if(inicio.compareTo( CalendarUtils.descartarHoras(new Date()) ) < 0){
			throw new NegocioException("A data de in�cio precisa ser maior ou igual a data de hoje");
		}
		
		if(fim.compareTo(inicio) < 0){
			throw new NegocioException("A data final precisa ser maior ou igual a data de in�cio do per�odo");
		}
		
		
	}

	
}
