/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/04/2008
 *
 */
package br.ufrn.sigaa.projetos.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.negocio.ProjetoHelper;
import br.ufrn.sigaa.projetos.dominio.HistoricoSituacaoProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.SolicitacaoReconsideracao;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;


/**
 * 
 * Classe responsável pela solicitação de reconsideração feita pelo coordenador da ação acadêmica.
 * <p>
 * Após o resultado da avaliação das ações pelo Comitê integrado de Ensino, Pesquisa e Extensão (CIEPE), 
 * o coordenador do projeto poderá solicitar a reconsideração desta avaliação caso não concorde com o resultado.
 * </p> 
 * Os passos são os seguintes:
 * <ul>
 *	<li>1. O coordenador preenche o formulário solicitando a reconsideração do projeto e envia o CIEPE;</li>
 *	<li>2. Um membro do CIEPE analisa a solicitação e aceitar ou nega a reconsideração baseado no texto do coordenador;</li>
 *	<li>3. Ao aceitar a solicitação de reconsideração do coordenador a proposta passa para o status
 *	 'AGUARDANDO_AVALIACAO' permitindo que sejam feitas as alterações necessárias na avaliação do projeto.</li>
 * </ul>
 *@author ilueny santos
 *
 */
public class ProcessadorReconsiderarAvaliacao extends AbstractProcessador {

    public Object execute(Movimento dmMov) throws NegocioException, ArqException,    RemoteException {

	MovimentoCadastro mov = (MovimentoCadastro) dmMov; 
	GenericDAO dao = getGenericDAO(mov);
	validate(mov);
	SolicitacaoReconsideracao sr = mov.getObjMovimentado();
	Projeto projeto = dao.findByPrimaryKey(sr.getProjeto().getId(), Projeto.class);
	projeto.getHistoricoSituacao().iterator();

	
	
	if( mov.getCodMovimento().equals(SigaaListaComando.SOLICITAR_RECONSIDERACAO_PROJETO_BASE)){ 

	    sr.setDataSolicitacao(new Date());
	    sr.setRegistroEntradaSolicitacao(mov.getUsuarioLogado().getRegistroEntrada());
	    dao.create(sr);
	    dao.detach(sr);

	    //gravando histórico do projeto
	    ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_ANALISANDO_PEDIDO_RECONSIDERACAO,
		    projeto.getId(), mov.getUsuarioLogado().getRegistroEntrada());			

	    //alterando o status do projeto;	
	    projeto.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_ANALISANDO_PEDIDO_RECONSIDERACAO));
	    dao.update(projeto);

	}
	
	

	if( mov.getCodMovimento().equals(SigaaListaComando.ANALISAR_RECONSIDERACAO_PROJETO_BASE)){ 

	    sr.setDataParecer(new Date());
	    sr.setRegistroEntradaParecer(mov.getUsuarioLogado().getRegistroEntrada());
	    dao.update(sr);
	    dao.detach(sr);

	    if(sr.isAprovado()){

			//gravando histórico da ação
			ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_PEDIDO_RECONSIDERACAO_APROVADO,
				projeto.getId(), mov.getUsuarioLogado().getRegistroEntrada());
	
			//gravando histórico da ação
			ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_AGUARDANDO_AVALIACAO,
				projeto.getId(), mov.getUsuarioLogado().getRegistroEntrada());
	
			//alterando o status da ação. Liberando para nova avaliação.
			projeto.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_AGUARDANDO_AVALIACAO));
			dao.update(projeto);

	    }else{

	    	//gravando histórico da ação
	    	ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_PEDIDO_RECONSIDERACAO_NEGADO,
	    			projeto.getId(), mov.getUsuarioLogado().getRegistroEntrada());

	    	int situacaoAntesDaReconsideracao = TipoSituacaoProjeto.PROJETO_BASE_REPROVADO;

	    	//lista todas as situações da ação pra verificar se ela já estava aprovada antes da solicitação de reconsideração
	    	//se ela já tiver sido aprovada antes então volta pra a situação de aprovada mesmo não tendo a reconsideração aceita
	    	for (HistoricoSituacaoProjeto hs : projeto.getHistoricoSituacao()){

	    		//alterando o status da ação, reestabelecendo a situação de aprovado/reprovado;
	    		if (hs.getSituacaoProjeto().getId() == TipoSituacaoProjeto.PROJETO_BASE_APROVADO_COM_RECURSOS){
	    			situacaoAntesDaReconsideracao = TipoSituacaoProjeto.PROJETO_BASE_APROVADO_COM_RECURSOS;
	    			break;							
	    		}
	    		if (hs.getSituacaoProjeto().getId() == TipoSituacaoProjeto.PROJETO_BASE_APROVADO_SEM_RECURSOS){
	    			situacaoAntesDaReconsideracao = TipoSituacaoProjeto.PROJETO_BASE_APROVADO_SEM_RECURSOS;
	    			break;							
	    		}

	    	}

	    	//alterando o status da ação;
	    	projeto.setSituacaoProjeto(new TipoSituacaoProjeto(situacaoAntesDaReconsideracao));
	    	dao.update(projeto);

	    	ProjetoHelper.gravarHistoricoSituacaoProjeto(situacaoAntesDaReconsideracao, 
	    			projeto.getId(), mov.getUsuarioLogado().getRegistroEntrada());
	    }

	} 


	return null;

    }

    /**
     * Realiza validações de relativas a data limite de solicitação de reconsideração
     * 
     */
    public void validate(Movimento mov) throws NegocioException, ArqException {
    	MovimentoCadastro mc = (MovimentoCadastro) mov;
    	ListaMensagens erros = new ListaMensagens();
    	SolicitacaoReconsideracao sr = mc.getObjMovimentado();

    	Usuario usuario = (Usuario) mov.getUsuarioLogado();
    	// Se o usuário não for servidor ou docente externo e estiver tentando realizar esta operação.
    	if (!usuario.getVinculoAtivo().isVinculoServidor() && !usuario.getVinculoAtivo().isVinculoDocenteExterno()) {
    		erros.addErro("Apenas Docentes ou Técnicos Administrativos podem realizar esta operação.");
    	}


    	if( mov.getCodMovimento().equals(SigaaListaComando.SOLICITAR_RECONSIDERACAO_PROJETO_BASE)){		
    		if((sr.getProjeto().getEdital() != null) && ( CalendarUtils.descartarHoras(new Date()).after( sr.getProjeto().getEdital().getDataFimReconsideracao()))) {
    			erros.addErro("Prazo para reconsideração de avaliação deste projeto expirou. Verifique o prazo estabelecido no edital.");
    		}

    		ValidatorUtil.validateRequired(sr.getJustificativa(), "Justificativa da Solicitação", erros);	    
    	}

    	if( mov.getCodMovimento().equals(SigaaListaComando.ANALISAR_RECONSIDERACAO_PROJETO_BASE)){
    		ValidatorUtil.validateRequired(sr.getParecer(), "Parecer da Avaliação", erros);
    	}

    	ValidatorUtil.validateRequired(sr.getProjeto(), "Projeto Relacionado ao Parecer", erros);
    	checkValidation(erros);
    }

}
