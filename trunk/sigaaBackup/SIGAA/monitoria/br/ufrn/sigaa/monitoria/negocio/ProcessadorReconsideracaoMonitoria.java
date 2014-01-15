/*
 * Universidade Federal do Rio Grande do Norte

 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 02/01/2012
 *
 */
package br.ufrn.sigaa.monitoria.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.monitoria.dominio.AvaliacaoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao;
import br.ufrn.sigaa.monitoria.dominio.TipoAvaliacaoMonitoria;
import br.ufrn.sigaa.negocio.ProjetoHelper;
import br.ufrn.sigaa.projetos.dominio.SolicitacaoReconsideracao;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * 
 * Classe respons�vel pela solicita��o de reconsidera��o feita pelo coordenador de projeto de monitoria.
 * <br>
 * <p>Ap�s o resultado da avalia��o dos projetos pelo comit� de monitoria, o coordenador do projeto poder� 
 * solicitar a reconsidera��o desta avalia��o em caso de reprova��o da sua proposta. Os passos s�o os seguintes:</p>
 * 
 * <ul>
 * 	<li>1. O coordenador preenche o formul�rio solicitando a reconsidera��o do projeto e envia a pr�-reitoria de gradua��o para aprova��o;</li>
 *	<li>2. O gestor de monitoria analisa a solicita��o e aceita ou nega a reconsidera��o baseado no texto do coordenador;</li>
 *	<li>3. Ao aceitar a solicita��o de reconsidera��o do coordenador a proposta passa para o status 'AGUARDANDO AVALIA��O' permitindo que sejam feitas novas avalia��o.</li>
 * </ul>
 * 
 *	@author ilueny santos
 *
 */
public class ProcessadorReconsideracaoMonitoria extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,    RemoteException {

		MovimentoCadastro movC = (MovimentoCadastro) mov; 
		ProjetoDao dao = getDAO(ProjetoDao.class, mov);
		validate(movC);
		SolicitacaoReconsideracao sr = movC.getObjMovimentado();
		ProjetoEnsino pm = dao.findByPrimaryKey(sr.getProjetoMonitoria().getId(), ProjetoEnsino.class);
		try {
		    
		    /** @negocio: Ao solicitar uma reconsidera��o de avalia��o o projeto de monitoria aguarda a an�lise da Pr�-Reitoria de Gradua��o. */
		    if( mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_RECONSIDERACAO_MONITORIA)) {
				sr.setDataSolicitacao(new Date());
				sr.setRegistroEntradaSolicitacao(mov.getUsuarioLogado().getRegistroEntrada());
				sr.setAtivo(true);
				dao.createOrUpdate(sr);					
	
				//alterando o status do projeto de monitoria;	
				pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_ANALISANDO_SOLICITACAO_DE_RECONSIDERACAO));
				dao.updateField(ProjetoEnsino.class, pm.getId(), "situacaoProjeto.id", TipoSituacaoProjeto.MON_ANALISANDO_SOLICITACAO_DE_RECONSIDERACAO);
				ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, pm);
				ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.MON_ANALISANDO_SOLICITACAO_DE_RECONSIDERACAO, sr.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());			
		    }
		    
		   /** Processando o resultado da an�lise da reconsidera��o.*/
		   if( mov.getCodMovimento().equals(SigaaListaComando.ANALISAR_RECONSIDERACAO_MONITORIA)){ 
				sr.setDataParecer(new Date());
				sr.setRegistroEntradaParecer(mov.getUsuarioLogado().getRegistroEntrada());
				dao.update(sr);
	
				//RECONSIDERA��O APROVADA
				if(sr.isAprovado()){
				    	
				    //Registrando a aprova��o da reconsidera��o.
					ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.MON_RECONSIDERACAO_APROVADA, sr.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
					
					//Libera o projeto para receber novas avalia��es
					dao.updateFields(ProjetoEnsino.class, pm.getId(), 
							new String[]{"mediaAnalise", "notaPrimeiraAvaliacao", "notaSegundaAvaliacao", "notaAvaliacaoFinal", "parecerAvaliacaoFinal"}, new Object[]{null, null, null, null, null});
					
					//Cancelando avalia��es antigas
					Collection<AvaliacaoMonitoria> avaliacoesAntigas = dao.findByExactField(AvaliacaoMonitoria.class, "projetoEnsino.id", pm.getId());
					for (AvaliacaoMonitoria am : avaliacoesAntigas) {
						if (am.getTipoAvaliacao().getId() == TipoAvaliacaoMonitoria.AVALIACAO_PROJETO_ENSINO) {
							dao.updateFields(AvaliacaoMonitoria.class, am.getId(), new String[]{"statusAvaliacao.id", "ativo"}, new Object[]{StatusAvaliacao.AVALIACAO_CANCELADA, false});
						}
					}
	
					//alterando o status do projeto
					pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_AGUARDANDO_AVALIACAO));
					dao.updateField(ProjetoEnsino.class, pm.getId(), "situacaoProjeto.id", TipoSituacaoProjeto.MON_AGUARDANDO_AVALIACAO);
					ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, pm);
					ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.MON_AGUARDANDO_AVALIACAO,	sr.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
					
					
				// RECONSIDERA��O REPROVADA
				}else{
	
					//Registrando a reprova��o da reconsidera��o.
					ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.MON_RECONSIDERACAO_NAO_APROVADA, sr.getProjetoMonitoria().getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
	
					/** @negocio: Em caso de reprova��o da reconsidera��o a a��o deve retornar para a situa��o antiga.	 */
					int situacaoAntesDaReconsideracao = dao.findLastSituacaoProjeto(sr.getProjeto().getId(), 
							new Integer[]{TipoSituacaoProjeto.MON_RECONSIDERACAO_NAO_APROVADA, TipoSituacaoProjeto.MON_ANALISANDO_SOLICITACAO_DE_RECONSIDERACAO});
					
					//alterando o status da a��o;
					pm.setSituacaoProjeto(new TipoSituacaoProjeto(situacaoAntesDaReconsideracao));
					dao.updateField(ProjetoEnsino.class, pm.getId(), "situacaoProjeto.id", situacaoAntesDaReconsideracao);
					ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, pm);
					ProjetoHelper.gravarHistoricoSituacaoProjeto(situacaoAntesDaReconsideracao,	sr.getProjetoMonitoria().getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
				}
		    } 
		    return null;
		}finally {
		    dao.close();
		}
	}

	/** Respons�vel por realizar valida��es finais. */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro cMov = (MovimentoCadastro) mov;

		Usuario usuario = (Usuario) mov.getUsuarioLogado();
		// Se o usu�rio n�o for servidor ou docente externo e estiver tentando realizar esta opera��o.
		if (!usuario.getVinculoAtivo().isVinculoServidor() && !usuario.getVinculoAtivo().isVinculoDocenteExterno()) {
			throw new NegocioException("Apenas Docentes ou T�cnicos Administrativos podem realizar esta opera��o.");
		}
		
		ListaMensagens erros = new ListaMensagens();
		SolicitacaoReconsideracao sr = cMov.getObjMovimentado();

		if( mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_RECONSIDERACAO_MONITORIA)){		
			if((sr.getProjetoMonitoria().getEditalMonitoria() != null) && (sr.getProjetoMonitoria().getEditalMonitoria().getEdital() != null) 
					&& ( CalendarUtils.descartarHoras(new Date()).after( sr.getProjetoMonitoria().getEditalMonitoria().getEdital().getDataFimReconsideracao()))) {
				erros.addErro("Prazo para reconsidera��o de avalia��o deste projeto expirou. Verifique o prazo estabelecido no edital.");
			}

			ValidatorUtil.validateRequired(sr.getJustificativa(), "Justificativa da Solicita��o", erros);	    
		}

		if( mov.getCodMovimento().equals(SigaaListaComando.ANALISAR_RECONSIDERACAO_MONITORIA)){
			ValidatorUtil.validateRequired(sr.getParecer(), "Parecer da Avalia��o", erros);
		}

		ValidatorUtil.validateRequired(sr.getProjetoMonitoria(), "Projeto de Monitoria Relaciondo ao Parecer", erros);
		checkValidation(erros);
	}

}
