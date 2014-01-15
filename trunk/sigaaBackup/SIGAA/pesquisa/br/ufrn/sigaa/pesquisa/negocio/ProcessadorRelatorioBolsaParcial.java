/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/02/2007
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.pesquisa.RelatorioBolsaParcialDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.RelatorioBolsaParcial;

/**
 * Processador respons�vel pela controle do envio e emiss�o de
 * parecer de relat�rios parciais de bolsas de pesquisa
 *
 * @author Ricardo Wendell
 *
 */
public class ProcessadorRelatorioBolsaParcial extends AbstractProcessador {

	/**
	 * Executa o processamento do movimento de acordo com o comando definido.
	 * 
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		RelatorioBolsaParcial relatorio = (RelatorioBolsaParcial) mov;

		// S� valida se for enviar ou gravar relat�rio parcial
		if(relatorio.getCodMovimento() == SigaaListaComando.ENVIAR_RELATORIO_PARCIAL_BOLSA_PESQUISA || 
				relatorio.getCodMovimento() == SigaaListaComando.GRAVAR_RELATORIO_PARCIAL_BOLSA_PESQUISA)
			validate(mov);
		
		GenericDAO dao = getGenericDAO(mov);

		RelatorioBolsaParcial relatorioGravado = null;

		try {
			// Envio de relat�rio
			if (SigaaListaComando.ENVIAR_RELATORIO_PARCIAL_BOLSA_PESQUISA.equals(mov.getCodMovimento())) {
				if (relatorio.getId() <= 0) {
					relatorio.setDataEnvio(new Date());
					relatorio.setEnviado(true);
					// Encoding Latin9
					relatorio.setAtividadesRealizadas(StringUtils.toLatin9(relatorio.getAtividadesRealizadas()));
					relatorio.setComparacaoOriginalExecutado(StringUtils.toLatin9(relatorio.getComparacaoOriginalExecutado()));
					relatorio.setOutrasAtividades(StringUtils.toLatin9(relatorio.getOutrasAtividades()));

					verificarRelatorioRemovido( mov, relatorio );
					
					dao.create(relatorio);
					relatorioGravado = relatorio;
				}
				else {
					relatorioGravado = dao.findByPrimaryKey(relatorio.getId(), RelatorioBolsaParcial.class);

					relatorioGravado.setDataEnvio(new Date());
					relatorioGravado.setEnviado(true);
					
					relatorioGravado.setAtividadesRealizadas(StringUtils.toLatin9(relatorio.getAtividadesRealizadas()));
					relatorioGravado.setComparacaoOriginalExecutado(StringUtils.toLatin9(relatorio.getComparacaoOriginalExecutado()));
					relatorioGravado.setOutrasAtividades(StringUtils.toLatin9(relatorio.getOutrasAtividades()));
					relatorioGravado.setResultadosPreliminares(StringUtils.toLatin9(relatorio.getResultadosPreliminares()));

					dao.update(relatorioGravado);
				}
			// Apenas grava, mas n�o submete ao orientador
			} else if (SigaaListaComando.GRAVAR_RELATORIO_PARCIAL_BOLSA_PESQUISA.equals(mov.getCodMovimento())) {
				if (relatorio.getId() <= 0) {
					relatorio.setDataEnvio(new Date());
					relatorio.setEnviado(false);
					
					// Encoding Latin9
					relatorio.setAtividadesRealizadas(StringUtils.toLatin9(relatorio.getAtividadesRealizadas()));
					relatorio.setComparacaoOriginalExecutado(StringUtils.toLatin9(relatorio.getComparacaoOriginalExecutado()));
					relatorio.setOutrasAtividades(StringUtils.toLatin9(relatorio.getOutrasAtividades()));
					relatorio.setResultadosPreliminares(StringUtils.toLatin9(relatorio.getResultadosPreliminares()));
					
					dao.create(relatorio);
					relatorioGravado = relatorio;
				}
				else {
					relatorioGravado = dao.findByPrimaryKey(relatorio.getId(), RelatorioBolsaParcial.class);

					relatorioGravado.setDataEnvio(new Date());
					relatorioGravado.setEnviado(false);
					
					relatorioGravado.setAtividadesRealizadas(StringUtils.toLatin9(relatorio.getAtividadesRealizadas()));
					relatorioGravado.setComparacaoOriginalExecutado(StringUtils.toLatin9(relatorio.getComparacaoOriginalExecutado()));
					relatorioGravado.setOutrasAtividades(StringUtils.toLatin9(relatorio.getOutrasAtividades()));
					relatorioGravado.setResultadosPreliminares(StringUtils.toLatin9(relatorio.getResultadosPreliminares()));

					dao.update(relatorioGravado);
				}
			}
			// Emiss�o do parecer
			else if (SigaaListaComando.PARECER_RELATORIO_PARCIAL_BOLSA_PESQUISA.equals(mov.getCodMovimento())) {
				relatorioGravado = dao.findByPrimaryKey(relatorio.getId(), RelatorioBolsaParcial.class);

				if (relatorioGravado.getPlanoTrabalho().getOrientador().getId() != ((Usuario) mov.getUsuarioLogado()).getServidor().getId()) {
					throw new NegocioException("Somente o orientador do plano de trabalho pode emitir o parecer do relat�rio parcial do bolsista");
				}

				relatorioGravado.setDataParecer(new Date());
				relatorioGravado.setParecerOrientador(relatorio.getParecerOrientador());
				dao.update(relatorioGravado);
			}
			// Remo��o do parecer
			else if( SigaaListaComando.REMOVER_PARECER_RELATORIO_PARCIAL_BOLSA_PESQUISA.equals(mov.getCodMovimento()) ){
				relatorioGravado = dao.findByPrimaryKey(relatorio.getId(), RelatorioBolsaParcial.class);
				
				relatorioGravado.setDataParecer(null);
				relatorioGravado.setParecerOrientador(null);
				dao.update(relatorioGravado);
			}
			relatorio = dao.findByPrimaryKey(relatorio.getId(), RelatorioBolsaParcial.class);
		} finally {
			dao.close();
		}

		return relatorioGravado;
	}

	/**
	 * Se o relat�rio j� tiver sido enviado o mesmo j� tiver sido removido, o id ser� reaproveitado.
	 * @param mov
	 * @param relatorio
	 * @throws DAOException
	 */
	private void verificarRelatorioRemovido( Movimento mov, RelatorioBolsaParcial relatorio ) throws DAOException {
		RelatorioBolsaParcialDao dao = getDAO(RelatorioBolsaParcialDao.class, mov);
		try {
			Integer id = dao.findIdByPlanoAndMembro(relatorio.getPlanoTrabalho().getId(), relatorio.getMembroDiscente().getId());
			if ( id != null && id > 0 )
				relatorio.setId(id);
		} finally {
			dao.close();
		}
	}

	/**
	 * Valida as informa��es contidas no movimento, verificando se � poss�vel prosseguir com a execu��o do mesmo.
	 * 
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
			
		RelatorioBolsaParcial relatorio = (RelatorioBolsaParcial) mov;
		
		ListaMensagens lista = new ListaMensagens();
		
		GenericDAO dao = getGenericDAO(mov);
		try {
			PlanoTrabalho plano = dao.findByPrimaryKey(relatorio.getPlanoTrabalho().getId(), PlanoTrabalho.class);
			
			if(!mov.getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_PESQUISA)){
				// Validar per�odo de envio
				RelatorioBolsaParcialValidator.validarPeriodoEnvio(plano, lista);
			}
			
			// Validar dados do relat�rio
			lista.addAll(relatorio.validate());
		} finally {
			dao.close();
		}
		
		checkValidation(lista);
	}

}
