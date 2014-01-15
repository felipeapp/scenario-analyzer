/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 29/07/2008 
 *
 */

package br.ufrn.sigaa.processamento.batch;

import javax.naming.InitialContext;

import br.ufrn.arq.negocio.Processador;
import br.ufrn.arq.negocio.ProcessadorHome;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dominio.MovimentoAcademico;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.processamento.jsf.ProcessamentoMatriculaMBean;

/**
 * Thread que consome o pós processamento, realizando o controle dos Movimentos de cada processo.
 * 
 * @author David Pereira
 *
 */
public class PosProcessamentoThread extends Thread {

	private int tipo;
	
	private boolean rematricula;
	
	private int ano;
	
	private int periodo;
	
	/**
	 * @param tipo
	 * @param periodo 
	 * @param ano 
	 */
	public PosProcessamentoThread(int tipo, int ano, int periodo, boolean rematricula) {
		this.tipo = tipo;
		this.rematricula = rematricula;
		this.ano = ano;
		this.periodo = periodo;
	}

	@Override
	public void run() {
		try {
			InitialContext ic = new InitialContext();
			ProcessadorHome home = (ProcessadorHome) ic.lookup("ejb/SigaaFacade");
			Processador remote = home.create();

			MovimentoAcademico mov = new MovimentoAcademico();
			mov.setCalendarioAcademicoAtual(CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad());

			if (tipo == ProcessamentoMatriculaMBean.POS_PROCESSAMENTO_BLOCOS)
				while (ListaBlocosProcessar.possuiDiscentes()) {

					Integer discente = ListaBlocosProcessar.getProximoDiscente();
	
					mov.setId(discente);
					mov.setCodMovimento(SigaaListaComando.POS_PROCESSAR_MATRICULA);
					mov.setAcao(tipo);
					mov.setUsuarioLogado(new Usuario(Usuario.USUARIO_SISTEMA));
					mov.setSistema(Sistema.SIGAA);
					mov.setAno(ano);
					mov.setPeriodo(periodo);
					mov.setRematricula(rematricula);
	
					remote.execute(mov);
	
					ListaBlocosProcessar.registraProcessada();
	
					System.out.println(ListaBlocosProcessar.totalProcessados);
	
				}
			else {
				while (ListaCoRequisitosProcessar.possuiDiscentes()) {

					Integer discente = ListaCoRequisitosProcessar.getProximoDiscente();
	
					mov.setId(discente);
					mov.setCodMovimento(SigaaListaComando.POS_PROCESSAR_MATRICULA);
					mov.setAcao(tipo);
					mov.setUsuarioLogado(new Usuario(Usuario.USUARIO_SISTEMA));
					mov.setSistema(Sistema.SIGAA);
					mov.setAno(ano);
					mov.setPeriodo(periodo);
					mov.setRematricula(rematricula);
	
					remote.execute(mov);
	
					ListaCoRequisitosProcessar.registraProcessada();
	
					System.out.println(ListaCoRequisitosProcessar.totalProcessados);
	
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
