/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 18/11/2008 
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.negocio.FacadeDelegate;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;

/**
 * Thread para processar o recalculo dos discentes de graduação.
 * 
 * @author David Pereira
 *
 */
public class RecalculoDiscenteThread extends Thread {

	private Usuario usuario;
	private String opcao;
	private boolean zerarIntegralizacoes;
	private volatile boolean finalizar;

	public RecalculoDiscenteThread(Usuario usuario, String opcao, boolean zerarIntegralizacoes) {
		this.usuario = usuario;
		this.opcao = opcao;
		this.zerarIntegralizacoes = zerarIntegralizacoes;
	}
	
	@Override
	public void run() {

		GenericDAO dao = new GenericSigaaDAO();
		
		try {
			
			Comando comando = null;
			
			if ("todos".equalsIgnoreCase(opcao)) {
				comando = SigaaListaComando.CALCULAR_INTEGRALIZACOES_DISCENTE;
			} else if ("statusTipos".equalsIgnoreCase(opcao)) {
				comando = SigaaListaComando.CALCULAR_TIPOS_STATUS;
			} else if ("consolidacao".equalsIgnoreCase(opcao)) {
				comando = SigaaListaComando.CALCULAR_CONSOLIDACAO;
			}
			
			FacadeDelegate facade = new FacadeDelegate("ejb/SigaaFacade");
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(comando);
			mov.setUsuarioLogado(usuario);
			mov.setSistema(Sistema.SIGAA);
			
			while(ListaDiscentesCalcular.possuiDiscentes() && !finalizar) {
				Integer id = ListaDiscentesCalcular.getProximoDiscente();
				DiscenteGraduacao discente = dao.findByPrimaryKey(id, DiscenteGraduacao.class);
				try {
					mov.setObjMovimentado(discente);
					mov.setObjAuxiliar(new Object[] { zerarIntegralizacoes, true });
					facade.prepare(comando.getId(), usuario, Sistema.SIGAA);
					facade.execute(mov, usuario, Sistema.SIGAA);
				} catch(Exception e) { 
					e.printStackTrace();
				}
				ListaDiscentesCalcular.registraProcessado();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		
	}

	public void finalizar() {
		this.finalizar = true;
	}
	
}
