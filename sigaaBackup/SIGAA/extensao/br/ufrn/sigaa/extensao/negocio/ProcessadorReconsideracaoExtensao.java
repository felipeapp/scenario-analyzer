/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/04/2008
 *
 */
package br.ufrn.sigaa.extensao.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.negocio.ProjetoHelper;
import br.ufrn.sigaa.projetos.dominio.SolicitacaoReconsideracao;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * 
 * Classe responsável pela solicitação de reconsideração feita pelo coordenador da ação de extensão.
 * <br>
 * <p>Após o resultado da avaliação das ações pelo comitê de extensão, o coordenador do projeto poderá 
 * solicitar a reconsideração desta avaliação em caso de reprovação da sua proposta. Os passos são os seguintes:</p>
 * <ul>
 * 	<li>1. O coordenador preenche o formulário solicitando a reconsideração do projeto e envia a pró-reitoria de extensão para aprovação;</li>
 *	<li>2. O operador da PROEX analisa a solicitação e aceitar ou nega a reconsideração baseado no texto do coordenador;</li>
 *	<li>3. Ao aceitar a solicitação de reconsideração do coordenador a proposta passa para o status
 *	 'CADASTRO EM ANDAMENTO' permitindo que sejam feitas as alterações necessárias.</li>
 *	<li>4. Após realizar as alterações, o coordenador envia a proposta para aprovação do departamento;</li>
 *	<li>5. após a aprovação do departamento a proposta segue para PROEX pra ser distribuída para nova avaliação.</li>
 * </ul>
 *@author ilueny santos
 *
 */
public class ProcessadorReconsideracaoExtensao extends AbstractProcessador {

	public Object execute(Movimento dmMov) throws NegocioException, ArqException,    RemoteException {

		CadastroExtensaoMov mov = (CadastroExtensaoMov) dmMov; 
		ProjetoDao dao = getDAO(ProjetoDao.class, mov);
		validate(mov);
		SolicitacaoReconsideracao sr = mov.getObjMovimentado();
		
		if(ValidatorUtil.isEmpty(sr.getProjetoMonitoria())) {
			sr.setProjetoMonitoria(null);
		}
		
		AtividadeExtensao atv = dao.findByPrimaryKey(sr.getAtividade().getId(), AtividadeExtensao.class);
		try {
		    
		    /** @negocio: Ao solicitar uma reconsideração de avaliação a ação de extensão aguarda a analise da PROEX. */
		    if( mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_RECONSIDERACAO_EXTENSAO)) {
				sr.setDataSolicitacao(new Date());
				sr.setRegistroEntradaSolicitacao(mov.getUsuarioLogado().getRegistroEntrada());
				sr.setAtivo(true);
				dao.createOrUpdate(sr);					
	
				//alterando o status da ação;	
				atv.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_ANALISANDO_SOLICITACAO_RECONSIDERACAO));
				dao.updateField(AtividadeExtensao.class, atv.getId(), "situacaoProjeto.id", TipoSituacaoProjeto.EXTENSAO_ANALISANDO_SOLICITACAO_RECONSIDERACAO);
				ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, atv);
				ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_ANALISANDO_SOLICITACAO_RECONSIDERACAO, 
					sr.getAtividade().getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());			
	
		    }
		    
		    //Processando o resultado da análise da reconsideração.
		    if( mov.getCodMovimento().equals(SigaaListaComando.ANALISAR_RECONSIDERACAO_EXTENSAO)){ 
				sr.setDataParecer(new Date());
				sr.setRegistroEntradaParecer(mov.getUsuarioLogado().getRegistroEntrada());
				dao.update(sr);
	
				//RECONSIDERAÇÃO APROVADA
				if(sr.isAprovado()){
				    	
				    //Registrando a aprovação da reconsideração.
					ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_RECONSIDERACAO_APROVADA,
							sr.getAtividade().getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
	
					//alterando o status da ação	
					atv.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_AGUARDANDO_AVALIACAO));
					dao.updateField(AtividadeExtensao.class, atv.getId(), "situacaoProjeto.id", TipoSituacaoProjeto.EXTENSAO_AGUARDANDO_AVALIACAO);
					ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, atv);
					ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_AGUARDANDO_AVALIACAO,
						sr.getAtividade().getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
					
					
					
				// RECONSIDERAÇÃO REPROVADA
				}else{
	
					//Registrando a reprovação da reconsideração.
					ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_RECONSIDERACAO_NAO_APROVADA,
							sr.getAtividade().getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
	
					/** @negocio: Em caso de reprovação da reconsideração a ação deve retornar para a situação antiga.	 */
					int situacaoAntesDaReconsideracao = dao.findLastSituacaoProjeto(sr.getProjeto().getId(), 
							new Integer[]{TipoSituacaoProjeto.EXTENSAO_RECONSIDERACAO_NAO_APROVADA, TipoSituacaoProjeto.EXTENSAO_ANALISANDO_SOLICITACAO_RECONSIDERACAO});
					
					//alterando o status da ação;
					atv.setSituacaoProjeto(new TipoSituacaoProjeto(situacaoAntesDaReconsideracao));
					dao.updateField(AtividadeExtensao.class, atv.getId(), "situacaoProjeto.id", situacaoAntesDaReconsideracao);
					ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, atv);
					ProjetoHelper.gravarHistoricoSituacaoProjeto(situacaoAntesDaReconsideracao, 
							sr.getAtividade().getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
				}
		    } 
		    return null;
		}finally {
		    dao.close();
		}
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		CadastroExtensaoMov cMov = (CadastroExtensaoMov) mov;

		Usuario usuario = (Usuario) mov.getUsuarioLogado();
		// Se o usuário não for servidor ou docente externo e estiver tentando realizar esta operação.
		if (!usuario.getVinculoAtivo().isVinculoServidor() && !usuario.getVinculoAtivo().isVinculoDocenteExterno()) {
			throw new NegocioException("Apenas Docentes ou Técnicos Administrativos podem realizar esta operação.");
		}
		
		ListaMensagens erros = new ListaMensagens();
		SolicitacaoReconsideracao sr = cMov.getObjMovimentado();

		if( mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_RECONSIDERACAO_EXTENSAO)){		
			if((sr.getAtividade().getEditalExtensao() != null) && (sr.getAtividade().getEditalExtensao().getEdital() != null) 
					&& ( CalendarUtils.descartarHoras(new Date()).after( sr.getAtividade().getEditalExtensao().getEdital().getDataFimReconsideracao()))) {
				erros.addErro("Prazo para reconsideração de avaliação deste projeto expirou. Verifique o prazo estabelecido no edital.");
			}

			ValidatorUtil.validateRequired(sr.getJustificativa(), "Justificativa da Solicitação", erros);	    
		}

		if( mov.getCodMovimento().equals(SigaaListaComando.ANALISAR_RECONSIDERACAO_EXTENSAO)){
			ValidatorUtil.validateRequired(sr.getParecer(), "Parecer da Avaliação", erros);
		}

		ValidatorUtil.validateRequired(sr.getAtividade(), "Atividade Relacionda ao Parecer", erros);

		checkValidation(erros);
	}

}
