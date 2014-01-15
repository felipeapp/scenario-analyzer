/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Classe respons�vel pela solicita��o de reconsidera��o feita pelo coordenador da a��o acad�mica.
 * <p>
 * Ap�s o resultado da avalia��o das a��es pelo Comit� integrado de Ensino, Pesquisa e Extens�o (CIEPE), 
 * o coordenador do projeto poder� solicitar a reconsidera��o desta avalia��o caso n�o concorde com o resultado.
 * </p> 
 * Os passos s�o os seguintes:
 * <ul>
 *	<li>1. O coordenador preenche o formul�rio solicitando a reconsidera��o do projeto e envia o CIEPE;</li>
 *	<li>2. Um membro do CIEPE analisa a solicita��o e aceitar ou nega a reconsidera��o baseado no texto do coordenador;</li>
 *	<li>3. Ao aceitar a solicita��o de reconsidera��o do coordenador a proposta passa para o status
 *	 'AGUARDANDO_AVALIACAO' permitindo que sejam feitas as altera��es necess�rias na avalia��o do projeto.</li>
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

	    //gravando hist�rico do projeto
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

			//gravando hist�rico da a��o
			ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_PEDIDO_RECONSIDERACAO_APROVADO,
				projeto.getId(), mov.getUsuarioLogado().getRegistroEntrada());
	
			//gravando hist�rico da a��o
			ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_AGUARDANDO_AVALIACAO,
				projeto.getId(), mov.getUsuarioLogado().getRegistroEntrada());
	
			//alterando o status da a��o. Liberando para nova avalia��o.
			projeto.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_AGUARDANDO_AVALIACAO));
			dao.update(projeto);

	    }else{

	    	//gravando hist�rico da a��o
	    	ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_PEDIDO_RECONSIDERACAO_NEGADO,
	    			projeto.getId(), mov.getUsuarioLogado().getRegistroEntrada());

	    	int situacaoAntesDaReconsideracao = TipoSituacaoProjeto.PROJETO_BASE_REPROVADO;

	    	//lista todas as situa��es da a��o pra verificar se ela j� estava aprovada antes da solicita��o de reconsidera��o
	    	//se ela j� tiver sido aprovada antes ent�o volta pra a situa��o de aprovada mesmo n�o tendo a reconsidera��o aceita
	    	for (HistoricoSituacaoProjeto hs : projeto.getHistoricoSituacao()){

	    		//alterando o status da a��o, reestabelecendo a situa��o de aprovado/reprovado;
	    		if (hs.getSituacaoProjeto().getId() == TipoSituacaoProjeto.PROJETO_BASE_APROVADO_COM_RECURSOS){
	    			situacaoAntesDaReconsideracao = TipoSituacaoProjeto.PROJETO_BASE_APROVADO_COM_RECURSOS;
	    			break;							
	    		}
	    		if (hs.getSituacaoProjeto().getId() == TipoSituacaoProjeto.PROJETO_BASE_APROVADO_SEM_RECURSOS){
	    			situacaoAntesDaReconsideracao = TipoSituacaoProjeto.PROJETO_BASE_APROVADO_SEM_RECURSOS;
	    			break;							
	    		}

	    	}

	    	//alterando o status da a��o;
	    	projeto.setSituacaoProjeto(new TipoSituacaoProjeto(situacaoAntesDaReconsideracao));
	    	dao.update(projeto);

	    	ProjetoHelper.gravarHistoricoSituacaoProjeto(situacaoAntesDaReconsideracao, 
	    			projeto.getId(), mov.getUsuarioLogado().getRegistroEntrada());
	    }

	} 


	return null;

    }

    /**
     * Realiza valida��es de relativas a data limite de solicita��o de reconsidera��o
     * 
     */
    public void validate(Movimento mov) throws NegocioException, ArqException {
    	MovimentoCadastro mc = (MovimentoCadastro) mov;
    	ListaMensagens erros = new ListaMensagens();
    	SolicitacaoReconsideracao sr = mc.getObjMovimentado();

    	Usuario usuario = (Usuario) mov.getUsuarioLogado();
    	// Se o usu�rio n�o for servidor ou docente externo e estiver tentando realizar esta opera��o.
    	if (!usuario.getVinculoAtivo().isVinculoServidor() && !usuario.getVinculoAtivo().isVinculoDocenteExterno()) {
    		erros.addErro("Apenas Docentes ou T�cnicos Administrativos podem realizar esta opera��o.");
    	}


    	if( mov.getCodMovimento().equals(SigaaListaComando.SOLICITAR_RECONSIDERACAO_PROJETO_BASE)){		
    		if((sr.getProjeto().getEdital() != null) && ( CalendarUtils.descartarHoras(new Date()).after( sr.getProjeto().getEdital().getDataFimReconsideracao()))) {
    			erros.addErro("Prazo para reconsidera��o de avalia��o deste projeto expirou. Verifique o prazo estabelecido no edital.");
    		}

    		ValidatorUtil.validateRequired(sr.getJustificativa(), "Justificativa da Solicita��o", erros);	    
    	}

    	if( mov.getCodMovimento().equals(SigaaListaComando.ANALISAR_RECONSIDERACAO_PROJETO_BASE)){
    		ValidatorUtil.validateRequired(sr.getParecer(), "Parecer da Avalia��o", erros);
    	}

    	ValidatorUtil.validateRequired(sr.getProjeto(), "Projeto Relacionado ao Parecer", erros);
    	checkValidation(erros);
    }

}
