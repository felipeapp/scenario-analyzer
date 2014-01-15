/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 18/11/2008 
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import br.ufrn.arq.negocio.FacadeDelegate;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Thread para processar o recalculo de cargas horárias
 * de estruturas curriculares.
 * 
 * @author David Pereira
 *
 */
public class RecalculoEstruturaCurricularThread extends Thread {

	private Usuario usuario;

	public RecalculoEstruturaCurricularThread(Usuario usuario) {
		this.usuario = usuario;
	}
	
	@Override
	public void run() {

		try {
			
			FacadeDelegate facade = new FacadeDelegate("ejb/SigaaFacade");
			MovimentoRecalculoEstruturaCurricular mov = new MovimentoRecalculoEstruturaCurricular(); 

			mov.setCodMovimento(SigaaListaComando.RECALCULAR_ESTRUTURA_CURRICULAR);
			mov.setUsuarioLogado(usuario);
			mov.setSistema(Sistema.SIGAA);
			mov.setRecalcularDiscentes(true);
			
			while(ListaEstruturasCalcular.possuiEstruturas()) {
				Integer estrutura = ListaEstruturasCalcular.getProximaEstrutura();
				mov.setId(estrutura);
				try {
					facade.prepare(SigaaListaComando.RECALCULAR_ESTRUTURA_CURRICULAR.getId(), usuario, Sistema.SIGAA);
					facade.execute(mov, usuario, Sistema.SIGAA);
				} catch(Exception e) { 
					e.printStackTrace();
				}
				ListaEstruturasCalcular.registraProcessada();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
