/*
* Universidade Federal do Rio Grande do Norte
* Superintend�ncia de Inform�tica
* Diretoria de Sistemas
*
* Created on 25/05/2008
*
*/
package br.ufrn.sigaa.extensao.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.AvaliacaoAtividade;
import br.ufrn.sigaa.extensao.dominio.AvaliacaoOrcamentoProposto;
import br.ufrn.sigaa.extensao.dominio.OrcamentoConsolidado;
import br.ufrn.sigaa.extensao.dominio.TipoAvaliacao;
import br.ufrn.sigaa.extensao.dominio.TipoParecerAvaliacaoExtensao;
import br.ufrn.sigaa.extensao.jsf.helper.AtividadeExtensaoHelper;
import br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao;
import br.ufrn.sigaa.negocio.ProjetoHelper;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Processador respons�vel pela avalia��o das Atividades de Extens�o.
 * 
 * @author Ilueny Santos
 * 
 */
public class ProcessadorAvaliarAtividade extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {		
	    validate(mov);
	    AvaliacaoAtividade avaliacao = (AvaliacaoAtividade) ((MovimentoCadastro)mov).getObjMovimentado();
	    AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class, mov);
	    
	    try {
		/*
		 * @negocio: Definindo valores padr�es da avalia��o.
		 */
		avaliacao.setParecer(getGenericDAO(mov).findByPrimaryKey(avaliacao.getParecer().getId(), TipoParecerAvaliacaoExtensao.class));
		avaliacao.setDataAvaliacao(new Date());
		avaliacao.setRegistroEntradaAvaliacao(mov.getUsuarioLogado().getRegistroEntrada());
		avaliacao.setStatusAvaliacao(new StatusAvaliacao(StatusAvaliacao.AVALIADO));
		dao.createOrUpdate(avaliacao);
		
		/* @negocio: Avalia��es do comit� de extens�o definem o or�amento da proposta */
		if (avaliacao.isAvaliacaoComite()){
			//gravando o or�amento concedido.
			Collection<AvaliacaoOrcamentoProposto> orcs = avaliacao.getOrcamentoProposto();
			for (AvaliacaoOrcamentoProposto orcamento: orcs) {
				dao.update(orcamento);			
			}
		    
		    // @negocio: A Devolvolu��o da proposta para ajustes do o coordenador da a��o s� ocorre caso esta a��o s� tenha 1 avaliador.
		    if(avaliacao.getParecer().getId() == TipoParecerAvaliacaoExtensao.DEVOLVER_PARA_AJUSTES_COORDENADOR){
		    	
		    	// A a��o � devolvida para ajustes caso seja validado(m�todo validate) que tenha apenas 1 avaliador para a mesma
	    		AtividadeExtensao atv = dao.findByPrimaryKey(avaliacao.getAtividade().getId(), AtividadeExtensao.class);
	    		dao.refresh(atv.getProjeto().getCoordenador());
	    		ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_PROPOSTA_DEVOLVIDA_PARA_COORDENADOR, atv.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
			    atv.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO));
				atv.setAtivo(true);
				dao.updateFields(AtividadeExtensao.class, atv.getId(), new String[] {"situacaoProjeto.id","ativo"},	new Object[] {atv.getSituacaoProjeto().getId(), atv.isAtivo()});
				ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, atv);
				ProjetoHelper.gravarHistoricoSituacaoProjeto(atv.getSituacaoProjeto().getId(),	atv.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
				
		    }
		}
		
		/* @negocio: 
		 * 	Na avalia��o do presidente da comiss�o o status da atividade � alterado e o processo de 
		 * 	avalia��o � finalizado.
		 * 	A a��o passa para a situa��o em execu��o ou reprovada.
		 */
		if (avaliacao.isAvaliacaoPresidenteComite()){		
				AtividadeExtensao atv = dao.findByPrimaryKey(avaliacao.getAtividade().getId(), AtividadeExtensao.class);
				dao.refresh(atv.getProjeto().getCoordenador());
				atv.getProjeto().getEquipe().iterator();
			
			boolean propostaAprovadaComRecursos = false;
			
			/* @negocio: Atualizando o or�amento proposto durante a avalia��o. */
			Collection<AvaliacaoOrcamentoProposto> orcs = avaliacao.getOrcamentoProposto();
			for (AvaliacaoOrcamentoProposto op: orcs) {
		    	dao.createOrUpdate(op);					
				/* @negocio: Valores concedidos pelo comit� devem ser atualizados nos or�amentos consolidados da a��o. */
				for (OrcamentoConsolidado oc : atv.getOrcamentosConsolidados()) {
				    if (oc.getElementoDespesa().getId() == op.getElementoDespesa().getId()){
				    	dao.updateField(OrcamentoConsolidado.class, oc.getId(), "fundoConcedido", op.getValorProposto()); //FAEX
				    }						
				}
				propostaAprovadaComRecursos = propostaAprovadaComRecursos || (op.getValorProposto() > 0);
			}
			/* @negocio: As bolsas concedidas a a��o tamb�m s�o defidas na avalia��o do presidente do comit�. */
			atv.setBolsasConcedidas(avaliacao.getBolsasPropostas());
			propostaAprovadaComRecursos = propostaAprovadaComRecursos || (avaliacao.getBolsasPropostas() > 0);
				
				
			/* @negocio: 
			 * 	Ap�s a avalia��o do presidente do comit� de extens�o todas as avalia��es pendentes ou em andamento devem ser canceladas.
			 * */
			Collection<AvaliacaoAtividade> avaliacoes = atv.getAvaliacoes();
			for (AvaliacaoAtividade av: avaliacoes) {
				if ( ((av.getTipoAvaliacao().getId() == TipoAvaliacao.AVALIACAO_ACAO_PARECERISTA) || (av.getTipoAvaliacao().getId() == TipoAvaliacao.AVALIACAO_ACAO_COMITE)) 
						&&	av.isAvaliacaoPendente()){
					//Cancelando avalia��es n�o finalizadas
					dao.updateFields(AvaliacaoAtividade.class, av.getId(), 
							new String[] {"ativo", "statusAvaliacao.id", "justificativa" }, 
							new Object[] {false, StatusAvaliacao.AVALIACAO_CANCELADA, "[AVALIA��O DO PRESIDENTE DO COMIT� DE EXTENS�O J� REALIZADA.]" });
				}					
			}
			
			/* @negocio: Propostas APROVADAS pelo presidente do comit� passam para a situa��o de em execu��o. */
			if (avaliacao.getParecer().getId() == TipoParecerAvaliacaoExtensao.APROVADO){
					
			    /* @negocio:  Caso a proposta seja de registro, gravamos essa informa��o no hist�rico de situa��o. */
				if (atv.isRegistro()) {
				    ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_REGISTRO_APROVADO, 
					    atv.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada() );
				}
				
				
				/* @negocio: A proposta passa para a situa��o de aprovado e aguarda a confirma��o do coordenador quanto a execu��o.*/
				if (atv.isFinanciamentoInterno()) {
					if (propostaAprovadaComRecursos) {
					    atv.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_APROVADO_COM_RECURSOS));
					}else {
					    atv.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_APROVADO_SEM_RECURSOS));
					}
					atv.getProjeto().setRecebeuFinanciamentoInterno(propostaAprovadaComRecursos);

				/* @negocio: A��o de extens�o aprovada que n�o solicitou financiamento interno entra automaticamente 'Em execu��o'. */ 
				} else {
					atv.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO));
					atv.getProjeto().setRecebeuFinanciamentoInterno(false);
				}
				
				
				/* @negocio: Se a a��o for isolada, seus membros tornam-se ativos quando a a��o � aprovada. */
				if (atv.isProjetoIsolado()) {
					for (MembroProjeto mp : atv.getMembrosEquipe()) {
						dao.updateFields(MembroProjeto.class, mp.getId(), 
								new String[] {"ativo", "dataInicio", "dataFim"}, 
								new Object[] {true, atv.getDataInicio(), atv.getDataFim()});
					}
				}
				
				/* @negocio: A��es aprovadas pelo presidente do comit� possuem umc�digo de identifica��o �nico. */					 
				int seq = AtividadeExtensaoHelper.gerarCodigoAcaoExtensao(atv, dao);
		        atv.setSequencia(seq);
				
				
				/* @negocio: Propostas APROVADAS COM RECOMENDA��O passam para a situa��o de cadastro em andamento permitindo que o 
				 * coordenador ajuste a proposta e submeta novamente. */
			} else if(avaliacao.getParecer().getId() == TipoParecerAvaliacaoExtensao.APROVADO_COM_RECOMENDACAO) {
			    if (atv.isRegistro()) {
				    ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO , 
					    atv.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada() );
				}
			    
				atv.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO));				
				/* @negocio: Propostas n�o aprovadas ou aprovadas com recomenda��o n�o recebem c�digo. */
				atv.setSequencia(0);
				
				/* Ultima op��o, a a��o foi REPROVADA pelo presidente do comit�. */
			} else{
					
				if (atv.isRegistro()) {
				    ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_REGISTRO_REPROVADO , 
					    atv.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada() );
				}
				atv.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_REPROVADO));
				

				/* @negocio: Se a a��o for isolada, seus membros s�o finalizados. */
				if (atv.isProjetoIsolado()) {
				    for (MembroProjeto mp : atv.getMembrosEquipe()) {
						dao.updateFields(MembroProjeto.class, mp.getId(), 
								new String[] {"ativo", "dataInicio", "dataFim"}, 
								new Object[] {true, null, null});
				    }
				}
					
				/* @negocio: Propostas n�o aprovadas n�o recebem c�digo. */
				atv.setSequencia(0);
			}
				
			atv.setAtivo(true);
			dao.update(atv);
			ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, atv);
			ProjetoHelper.gravarHistoricoSituacaoProjeto(atv.getSituacaoProjeto().getId(),	atv.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada() );
				
		}
	    }finally {	
	    	dao.close();
	    }
	    
	    return null;
	}

	/**
	 * Verifica permiss�es.
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// Checando os papeis de usu�rios permitidos
		int[] papeis = {SigaaPapeis.GESTOR_EXTENSAO, SigaaPapeis.MEMBRO_COMITE_EXTENSAO, 
				SigaaPapeis.PARECERISTA_EXTENSAO, SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO}; 
		checkRole(papeis, mov);

		ListaMensagens mensagens = new ListaMensagens();

		AvaliacaoAtividade avaliacao = (AvaliacaoAtividade) ((MovimentoCadastro)mov).getObjMovimentado();
		AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class, mov);
		try {
			if(avaliacao.getParecer().getId() == TipoParecerAvaliacaoExtensao.DEVOLVER_PARA_AJUSTES_COORDENADOR){
				int qtdAvaliadores = dao.countAvaliadoresComiteExtensaoAtividade(avaliacao.getAtividade().getId()).intValue();
				// Verifica qtos membros do comit� tem avaliando a a��o
				if(qtdAvaliadores != 1){
					mensagens.addErro("S� � permitido devolver uma a��o para ajustes do(a) coordenador(a) caso a mesma tenha apenas 1 avaliador(a).");
				}
			}
			checkValidation(mensagens);
		}finally {
			dao.close();
		}
	}

}