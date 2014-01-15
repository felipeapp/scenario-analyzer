/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Classe respons�vel pela solicita��o de reconsidera��o feita pelo coordenador da a��o de extens�o.
 * <br>
 * <p>Ap�s o resultado da avalia��o das a��es pelo comit� de extens�o, o coordenador do projeto poder� 
 * solicitar a reconsidera��o desta avalia��o em caso de reprova��o da sua proposta. Os passos s�o os seguintes:</p>
 * <ul>
 * 	<li>1. O coordenador preenche o formul�rio solicitando a reconsidera��o do projeto e envia a pr�-reitoria de extens�o para aprova��o;</li>
 *	<li>2. O operador da PROEX analisa a solicita��o e aceitar ou nega a reconsidera��o baseado no texto do coordenador;</li>
 *	<li>3. Ao aceitar a solicita��o de reconsidera��o do coordenador a proposta passa para o status
 *	 'CADASTRO EM ANDAMENTO' permitindo que sejam feitas as altera��es necess�rias.</li>
 *	<li>4. Ap�s realizar as altera��es, o coordenador envia a proposta para aprova��o do departamento;</li>
 *	<li>5. ap�s a aprova��o do departamento a proposta segue para PROEX pra ser distribu�da para nova avalia��o.</li>
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
		    
		    /** @negocio: Ao solicitar uma reconsidera��o de avalia��o a a��o de extens�o aguarda a analise da PROEX. */
		    if( mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_RECONSIDERACAO_EXTENSAO)) {
				sr.setDataSolicitacao(new Date());
				sr.setRegistroEntradaSolicitacao(mov.getUsuarioLogado().getRegistroEntrada());
				sr.setAtivo(true);
				dao.createOrUpdate(sr);					
	
				//alterando o status da a��o;	
				atv.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_ANALISANDO_SOLICITACAO_RECONSIDERACAO));
				dao.updateField(AtividadeExtensao.class, atv.getId(), "situacaoProjeto.id", TipoSituacaoProjeto.EXTENSAO_ANALISANDO_SOLICITACAO_RECONSIDERACAO);
				ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, atv);
				ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_ANALISANDO_SOLICITACAO_RECONSIDERACAO, 
					sr.getAtividade().getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());			
	
		    }
		    
		    //Processando o resultado da an�lise da reconsidera��o.
		    if( mov.getCodMovimento().equals(SigaaListaComando.ANALISAR_RECONSIDERACAO_EXTENSAO)){ 
				sr.setDataParecer(new Date());
				sr.setRegistroEntradaParecer(mov.getUsuarioLogado().getRegistroEntrada());
				dao.update(sr);
	
				//RECONSIDERA��O APROVADA
				if(sr.isAprovado()){
				    	
				    //Registrando a aprova��o da reconsidera��o.
					ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_RECONSIDERACAO_APROVADA,
							sr.getAtividade().getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
	
					//alterando o status da a��o	
					atv.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_AGUARDANDO_AVALIACAO));
					dao.updateField(AtividadeExtensao.class, atv.getId(), "situacaoProjeto.id", TipoSituacaoProjeto.EXTENSAO_AGUARDANDO_AVALIACAO);
					ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, atv);
					ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_AGUARDANDO_AVALIACAO,
						sr.getAtividade().getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
					
					
					
				// RECONSIDERA��O REPROVADA
				}else{
	
					//Registrando a reprova��o da reconsidera��o.
					ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_RECONSIDERACAO_NAO_APROVADA,
							sr.getAtividade().getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
	
					/** @negocio: Em caso de reprova��o da reconsidera��o a a��o deve retornar para a situa��o antiga.	 */
					int situacaoAntesDaReconsideracao = dao.findLastSituacaoProjeto(sr.getProjeto().getId(), 
							new Integer[]{TipoSituacaoProjeto.EXTENSAO_RECONSIDERACAO_NAO_APROVADA, TipoSituacaoProjeto.EXTENSAO_ANALISANDO_SOLICITACAO_RECONSIDERACAO});
					
					//alterando o status da a��o;
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
		// Se o usu�rio n�o for servidor ou docente externo e estiver tentando realizar esta opera��o.
		if (!usuario.getVinculoAtivo().isVinculoServidor() && !usuario.getVinculoAtivo().isVinculoDocenteExterno()) {
			throw new NegocioException("Apenas Docentes ou T�cnicos Administrativos podem realizar esta opera��o.");
		}
		
		ListaMensagens erros = new ListaMensagens();
		SolicitacaoReconsideracao sr = cMov.getObjMovimentado();

		if( mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_RECONSIDERACAO_EXTENSAO)){		
			if((sr.getAtividade().getEditalExtensao() != null) && (sr.getAtividade().getEditalExtensao().getEdital() != null) 
					&& ( CalendarUtils.descartarHoras(new Date()).after( sr.getAtividade().getEditalExtensao().getEdital().getDataFimReconsideracao()))) {
				erros.addErro("Prazo para reconsidera��o de avalia��o deste projeto expirou. Verifique o prazo estabelecido no edital.");
			}

			ValidatorUtil.validateRequired(sr.getJustificativa(), "Justificativa da Solicita��o", erros);	    
		}

		if( mov.getCodMovimento().equals(SigaaListaComando.ANALISAR_RECONSIDERACAO_EXTENSAO)){
			ValidatorUtil.validateRequired(sr.getParecer(), "Parecer da Avalia��o", erros);
		}

		ValidatorUtil.validateRequired(sr.getAtividade(), "Atividade Relacionda ao Parecer", erros);

		checkValidation(erros);
	}

}
