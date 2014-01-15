/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
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
 * Processador responsável pela avaliação das Atividades de Extensão.
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
		 * @negocio: Definindo valores padrões da avaliação.
		 */
		avaliacao.setParecer(getGenericDAO(mov).findByPrimaryKey(avaliacao.getParecer().getId(), TipoParecerAvaliacaoExtensao.class));
		avaliacao.setDataAvaliacao(new Date());
		avaliacao.setRegistroEntradaAvaliacao(mov.getUsuarioLogado().getRegistroEntrada());
		avaliacao.setStatusAvaliacao(new StatusAvaliacao(StatusAvaliacao.AVALIADO));
		dao.createOrUpdate(avaliacao);
		
		/* @negocio: Avaliações do comitê de extensão definem o orçamento da proposta */
		if (avaliacao.isAvaliacaoComite()){
			//gravando o orçamento concedido.
			Collection<AvaliacaoOrcamentoProposto> orcs = avaliacao.getOrcamentoProposto();
			for (AvaliacaoOrcamentoProposto orcamento: orcs) {
				dao.update(orcamento);			
			}
		    
		    // @negocio: A Devolvolução da proposta para ajustes do o coordenador da ação só ocorre caso esta ação só tenha 1 avaliador.
		    if(avaliacao.getParecer().getId() == TipoParecerAvaliacaoExtensao.DEVOLVER_PARA_AJUSTES_COORDENADOR){
		    	
		    	// A ação é devolvida para ajustes caso seja validado(método validate) que tenha apenas 1 avaliador para a mesma
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
		 * 	Na avaliação do presidente da comissão o status da atividade é alterado e o processo de 
		 * 	avaliação é finalizado.
		 * 	A ação passa para a situação em execução ou reprovada.
		 */
		if (avaliacao.isAvaliacaoPresidenteComite()){		
				AtividadeExtensao atv = dao.findByPrimaryKey(avaliacao.getAtividade().getId(), AtividadeExtensao.class);
				dao.refresh(atv.getProjeto().getCoordenador());
				atv.getProjeto().getEquipe().iterator();
			
			boolean propostaAprovadaComRecursos = false;
			
			/* @negocio: Atualizando o orçamento proposto durante a avaliação. */
			Collection<AvaliacaoOrcamentoProposto> orcs = avaliacao.getOrcamentoProposto();
			for (AvaliacaoOrcamentoProposto op: orcs) {
		    	dao.createOrUpdate(op);					
				/* @negocio: Valores concedidos pelo comitê devem ser atualizados nos orçamentos consolidados da ação. */
				for (OrcamentoConsolidado oc : atv.getOrcamentosConsolidados()) {
				    if (oc.getElementoDespesa().getId() == op.getElementoDespesa().getId()){
				    	dao.updateField(OrcamentoConsolidado.class, oc.getId(), "fundoConcedido", op.getValorProposto()); //FAEX
				    }						
				}
				propostaAprovadaComRecursos = propostaAprovadaComRecursos || (op.getValorProposto() > 0);
			}
			/* @negocio: As bolsas concedidas a ação também são defidas na avaliação do presidente do comitê. */
			atv.setBolsasConcedidas(avaliacao.getBolsasPropostas());
			propostaAprovadaComRecursos = propostaAprovadaComRecursos || (avaliacao.getBolsasPropostas() > 0);
				
				
			/* @negocio: 
			 * 	Após a avaliação do presidente do comitê de extensão todas as avaliações pendentes ou em andamento devem ser canceladas.
			 * */
			Collection<AvaliacaoAtividade> avaliacoes = atv.getAvaliacoes();
			for (AvaliacaoAtividade av: avaliacoes) {
				if ( ((av.getTipoAvaliacao().getId() == TipoAvaliacao.AVALIACAO_ACAO_PARECERISTA) || (av.getTipoAvaliacao().getId() == TipoAvaliacao.AVALIACAO_ACAO_COMITE)) 
						&&	av.isAvaliacaoPendente()){
					//Cancelando avaliações não finalizadas
					dao.updateFields(AvaliacaoAtividade.class, av.getId(), 
							new String[] {"ativo", "statusAvaliacao.id", "justificativa" }, 
							new Object[] {false, StatusAvaliacao.AVALIACAO_CANCELADA, "[AVALIAÇÃO DO PRESIDENTE DO COMITÊ DE EXTENSÃO JÁ REALIZADA.]" });
				}					
			}
			
			/* @negocio: Propostas APROVADAS pelo presidente do comitê passam para a situação de em execução. */
			if (avaliacao.getParecer().getId() == TipoParecerAvaliacaoExtensao.APROVADO){
					
			    /* @negocio:  Caso a proposta seja de registro, gravamos essa informação no histórico de situação. */
				if (atv.isRegistro()) {
				    ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_REGISTRO_APROVADO, 
					    atv.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada() );
				}
				
				
				/* @negocio: A proposta passa para a situação de aprovado e aguarda a confirmação do coordenador quanto a execução.*/
				if (atv.isFinanciamentoInterno()) {
					if (propostaAprovadaComRecursos) {
					    atv.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_APROVADO_COM_RECURSOS));
					}else {
					    atv.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_APROVADO_SEM_RECURSOS));
					}
					atv.getProjeto().setRecebeuFinanciamentoInterno(propostaAprovadaComRecursos);

				/* @negocio: Ação de extensão aprovada que não solicitou financiamento interno entra automaticamente 'Em execução'. */ 
				} else {
					atv.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO));
					atv.getProjeto().setRecebeuFinanciamentoInterno(false);
				}
				
				
				/* @negocio: Se a ação for isolada, seus membros tornam-se ativos quando a ação é aprovada. */
				if (atv.isProjetoIsolado()) {
					for (MembroProjeto mp : atv.getMembrosEquipe()) {
						dao.updateFields(MembroProjeto.class, mp.getId(), 
								new String[] {"ativo", "dataInicio", "dataFim"}, 
								new Object[] {true, atv.getDataInicio(), atv.getDataFim()});
					}
				}
				
				/* @negocio: Ações aprovadas pelo presidente do comitê possuem umcódigo de identificação único. */					 
				int seq = AtividadeExtensaoHelper.gerarCodigoAcaoExtensao(atv, dao);
		        atv.setSequencia(seq);
				
				
				/* @negocio: Propostas APROVADAS COM RECOMENDAÇÃO passam para a situação de cadastro em andamento permitindo que o 
				 * coordenador ajuste a proposta e submeta novamente. */
			} else if(avaliacao.getParecer().getId() == TipoParecerAvaliacaoExtensao.APROVADO_COM_RECOMENDACAO) {
			    if (atv.isRegistro()) {
				    ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO , 
					    atv.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada() );
				}
			    
				atv.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO));				
				/* @negocio: Propostas não aprovadas ou aprovadas com recomendação não recebem código. */
				atv.setSequencia(0);
				
				/* Ultima opção, a ação foi REPROVADA pelo presidente do comitê. */
			} else{
					
				if (atv.isRegistro()) {
				    ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_REGISTRO_REPROVADO , 
					    atv.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada() );
				}
				atv.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_REPROVADO));
				

				/* @negocio: Se a ação for isolada, seus membros são finalizados. */
				if (atv.isProjetoIsolado()) {
				    for (MembroProjeto mp : atv.getMembrosEquipe()) {
						dao.updateFields(MembroProjeto.class, mp.getId(), 
								new String[] {"ativo", "dataInicio", "dataFim"}, 
								new Object[] {true, null, null});
				    }
				}
					
				/* @negocio: Propostas não aprovadas não recebem código. */
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
	 * Verifica permissões.
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// Checando os papeis de usuários permitidos
		int[] papeis = {SigaaPapeis.GESTOR_EXTENSAO, SigaaPapeis.MEMBRO_COMITE_EXTENSAO, 
				SigaaPapeis.PARECERISTA_EXTENSAO, SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO}; 
		checkRole(papeis, mov);

		ListaMensagens mensagens = new ListaMensagens();

		AvaliacaoAtividade avaliacao = (AvaliacaoAtividade) ((MovimentoCadastro)mov).getObjMovimentado();
		AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class, mov);
		try {
			if(avaliacao.getParecer().getId() == TipoParecerAvaliacaoExtensao.DEVOLVER_PARA_AJUSTES_COORDENADOR){
				int qtdAvaliadores = dao.countAvaliadoresComiteExtensaoAtividade(avaliacao.getAtividade().getId()).intValue();
				// Verifica qtos membros do comitê tem avaliando a ação
				if(qtdAvaliadores != 1){
					mensagens.addErro("Só é permitido devolver uma ação para ajustes do(a) coordenador(a) caso a mesma tenha apenas 1 avaliador(a).");
				}
			}
			checkValidation(mensagens);
		}finally {
			dao.close();
		}
	}

}