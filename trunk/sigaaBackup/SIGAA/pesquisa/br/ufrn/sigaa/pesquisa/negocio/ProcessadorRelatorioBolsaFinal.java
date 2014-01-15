/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/06/2007
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.pesquisa.RelatorioBolsaFinalDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.RelatorioBolsaFinal;

/**
 * Processador respons�vel pelo controle do envio e emiss�o de
 * parecer de relat�rios finais de bolsas de pesquisa
 *
 * @author Ricardo Wendell
 *
 */
public class ProcessadorRelatorioBolsaFinal extends AbstractProcessador {

	/** 
	 * Executa o processamento do movimento de acordo com o comando definido.
	 * @see br.ufrn.arq.ejb.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		RelatorioBolsaFinal relatorio = (RelatorioBolsaFinal) mov;
		
		// S� valida se for enviar ou gravar relat�rio final
		if(relatorio.getCodMovimento() == SigaaListaComando.ENVIAR_RELATORIO_FINAL_BOLSA_PESQUISA || 
				relatorio.getCodMovimento() == SigaaListaComando.GRAVAR_RELATORIO_FINAL_BOLSA_PESQUISA)
			validate(mov);
		
		GenericDAO dao = getGenericDAO(mov);
		RelatorioBolsaFinalDao relDao = getDAO(RelatorioBolsaFinalDao.class, mov);
		
		RelatorioBolsaFinal relatorioGravado = null;
		
		try {
			// Envio de relat�rio
			if (SigaaListaComando.ENVIAR_RELATORIO_FINAL_BOLSA_PESQUISA.equals(mov.getCodMovimento())) {
				if (relatorio.getId() <= 0) {
					RelatorioBolsaFinal rel = relDao.findByPlanoTrabalho(relatorio.getPlanoTrabalho().getId());
					if ( rel != null && rel.getId() > 0 )
						relatorio.setId(rel.getId());
					
					relatorio.setDataEnvio(new Date());
					relatorio.setEnviado(Boolean.TRUE);
					relatorio.setAtivo(Boolean.TRUE);
					dao.create(relatorio);
					relatorioGravado = relatorio;
				}
				else {
					relatorioGravado = dao.findByPrimaryKey(relatorio.getId(), RelatorioBolsaFinal.class);
					dao.detach(relatorioGravado);

					relatorio.setDataEnvio(new Date());
					relatorio.setEnviado(Boolean.TRUE);
					
					dao.update(relatorio);
				}
			}
			// Gravar relat�rio			
			else if (SigaaListaComando.GRAVAR_RELATORIO_FINAL_BOLSA_PESQUISA.equals(mov.getCodMovimento())) {
				if (relatorio.getId() <= 0) {
					relatorio.setDataEnvio(new Date());
					relatorio.setEnviado(Boolean.FALSE);
					
					dao.create(relatorio);
					relatorioGravado = relatorio;
				}
				else {
					relatorioGravado = dao.findByPrimaryKey(relatorio.getId(), RelatorioBolsaFinal.class);
					dao.detach(relatorioGravado);

					relatorio.setDataEnvio(new Date());
					relatorio.setEnviado(Boolean.FALSE);
					dao.update(relatorio);
				}
			}			
			// Emiss�o do parecer
			else if (SigaaListaComando.PARECER_RELATORIO_FINAL_BOLSA_PESQUISA.equals(mov.getCodMovimento())) {
				relatorioGravado = dao.findByPrimaryKey(relatorio.getId(), RelatorioBolsaFinal.class);
				relatorioGravado.setDataParecer(new Date());
				relatorioGravado.setParecerOrientador(relatorio.getParecerOrientador());
				dao.update(relatorioGravado);
			}
			// Remo��o do parecer
			else if( SigaaListaComando.REMOVER_PARECER_RELATORIO_FINAL_BOLSA_PESQUISA.equals(mov.getCodMovimento()) ){
				relatorioGravado = dao.findByPrimaryKey(relatorio.getId(), RelatorioBolsaFinal.class);

				relatorioGravado.setDataParecer(null);
				relatorioGravado.setParecerOrientador(null);
				dao.update(relatorioGravado);
			}
			relatorio = dao.findByPrimaryKey(relatorio.getId(), RelatorioBolsaFinal.class);
		} finally {
			dao.close();
		}

		return relatorioGravado;
	}

	/** 
	 * Valida as informa��es contidas no movimento, verificando se � poss�vel prosseguir com a execu��o do mesmo.
	 * @see br.ufrn.arq.ejb.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		RelatorioBolsaFinal relatorio = (RelatorioBolsaFinal) mov;
		
		// Validar se o relat�rio j� foi enviado
		if(relatorio.getId() > 0){
			RelatorioBolsaFinal relBD = getGenericDAO(mov).findByPrimaryKey(relatorio.getId(), RelatorioBolsaFinal.class);
			if(relBD.isEnviado() && !mov.getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_PESQUISA))
				throw new NegocioException("O relat�rio j� foi enviado.");
		}

		ListaMensagens lista = new ListaMensagens();
		
		if(!mov.getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_PESQUISA)){
			GenericDAO dao = getGenericDAO(mov);
			try {
				PlanoTrabalho plano = dao.findByPrimaryKey(relatorio.getPlanoTrabalho().getId(), PlanoTrabalho.class);
	
				// Validar per�odo de envio do relat�rio
				RelatorioBolsaFinalValidator.validarPeriodoEnvio(plano, lista);
	
			} finally {
				dao.close();
			}
		}
		
		if(relatorio.getCodMovimento() != SigaaListaComando.GRAVAR_RELATORIO_FINAL_BOLSA_PESQUISA){
			lista.addAll(relatorio.validate());
		}
		checkValidation( lista );
	}

}
