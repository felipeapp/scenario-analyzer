/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 18/11/2008 
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.negocio.FacadeDelegate;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;

/**
 * Thread para processar o recalculo dos discentes de graduação.
 * 
 * @author David Pereira
 *
 */
public class RecalculoDiscenteThread extends Thread {

	private Usuario usuario;
	private volatile boolean finalizar;
	private ListaDiscentesCalcular reCalculo;
	private boolean zerarIntegralizacoes;
	
	public RecalculoDiscenteThread(Usuario usuario, boolean zerarIntegralizacoes, ListaDiscentesCalcular reCalculo) {
		this.usuario = usuario;
		this.reCalculo = reCalculo;
		this.zerarIntegralizacoes = zerarIntegralizacoes;
	}
	
	@Override
	public void run() {
		DiscenteDao discenteDao = DAOFactory.getInstance().getDAO(DiscenteDao.class);

		try {
			int i = 1;
			FacadeDelegate facade = new FacadeDelegate("ejb/SigaaFacade");
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setUsuarioLogado(usuario);
			mov.setSistema(Sistema.SIGAA);
			
			while(reCalculo.possuiDiscentes() && !finalizar) {
				Integer id = reCalculo.getProximoDiscente();
				DiscenteAdapter discente = discenteDao.findByPK(id);
				
				if (discente.isGraduacao()) {
					DiscenteGraduacao grad = (DiscenteGraduacao) discente;
					mov.setCodMovimento(SigaaListaComando.CALCULAR_INTEGRALIZACOES_DISCENTE);
					mov.setObjMovimentado(grad);
					mov.setObjAuxiliar(new Object[] { zerarIntegralizacoes, true });
					
					facade.prepare(SigaaListaComando.CALCULAR_INTEGRALIZACOES_DISCENTE.getId(), usuario, Sistema.SIGAA);
					facade.execute(mov, usuario, Sistema.SIGAA);
				} else if (discente.isStricto()) {
					DiscenteStricto stricto = (DiscenteStricto) discente;
					mov.setCodMovimento(SigaaListaComando.CALCULAR_INTEGRALIZACOES_DISCENTE_STRICTO);
					mov.setObjMovimentado(stricto);
					
					facade.prepare(SigaaListaComando.CALCULAR_INTEGRALIZACOES_DISCENTE_STRICTO.getId(), usuario, Sistema.SIGAA);
					facade.execute(mov, usuario, Sistema.SIGAA);					
				}
				
				reCalculo.registraProcessado();
				System.out.println(reCalculo.getTotalProcessados() + " - " + discente.getMatriculaNome());
				
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			discenteDao.close();
		}
	}

	public void finalizar() {
		this.finalizar = true;
	}
	
	
}
