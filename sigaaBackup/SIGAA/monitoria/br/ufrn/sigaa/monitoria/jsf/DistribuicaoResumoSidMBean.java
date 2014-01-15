/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 * 
 * Created on 23/02/2007
 * 
 */
package br.ufrn.sigaa.monitoria.jsf;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.monitoria.AvaliacaoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroComissaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.AvaliacaoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ResumoSid;
import br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao;
import br.ufrn.sigaa.monitoria.dominio.TipoAvaliacaoMonitoria;
import br.ufrn.sigaa.monitoria.jsf.dominio.DistribuicaoProjeto;
import br.ufrn.sigaa.monitoria.negocio.DistribuicaoMonitoriaMov;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;

/**
 * Respons�vel por distribuir resumos do semin�rio de inicia��o � doc�ncia
 * para os avaliadores da comiss�o cient�fica.
 *
 * @author Ilueny santos
 *
 */
@Component("distribuicaoResumoSid") @Scope("session")
public class DistribuicaoResumoSidMBean extends SigaaAbstractController<DistribuicaoProjeto> {

	private TipoAvaliacaoMonitoria tipoAvaliacao;
	
	public DistribuicaoResumoSidMBean() {
	    obj = new DistribuicaoProjeto();
	    try {
		GenericDAO dao = getGenericDAO();
		setTipoAvaliacao( dao.findByPrimaryKey(TipoAvaliacaoMonitoria.AVALIACAO_RESUMO_SID, TipoAvaliacaoMonitoria.class));
	    } catch (DAOException e) {
		addMensagemErro("Erro ao definir tipo de Avalia��o!");
	    }
	}
	
	
	
	/**
	 * remove avalia��o de um resumo do semin�rio de inicia��o � doc�ncia
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\DistribuicaoResumoSid\form.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String removerAvaliacao() throws SegurancaException{
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		
		try {
			
			AvaliacaoMonitoriaDao daoAM = getDAO(AvaliacaoMonitoriaDao.class);		
			Integer idM = getParameterInt("idMembroComissao");
	
			Set<AvaliacaoMonitoria> avaliacoes = obj.getResumo().getAvaliacoes();
			for (AvaliacaoMonitoria am : avaliacoes) {
				if (am.getTipoAvaliacao().getId() == TipoAvaliacaoMonitoria.AVALIACAO_RESUMO_SID) {
						if (am.getAvaliador().getId() == idM) {
							avaliacoes.remove(am);  //remove a avalia��o do projeto
							
							//verifica se esse docente j� avaliava esse projeto
							AvaliacaoMonitoria a = daoAM.findByProjetoResumoAvaliador(0, obj.getResumo().getId(), idM, TipoAvaliacaoMonitoria.AVALIACAO_RESUMO_SID);
								if (( a != null ) && (a.getId() > 0)) {									
								    	//s� coloca na lista de removidos os que j� avaliavam o projeto
									if (obj.getAvaliadoresRemovidos().add(am.getAvaliador())) { 
										break;
									}
								}
							break;								
						}
					}
				}
			
		} catch (DAOException e) {
			notifyError(e);
		} 
		
		return null;
		
	}

	
	/**
	 * Adiciona avalia��o a um resumo do semin�rio de inicia��o � doc�ncia
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\DistribuicaoResumoSid\form.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String adicionarAvaliacao() throws SegurancaException{
	    checkRole(SigaaPapeis.GESTOR_MONITORIA);

	    try{

		int idM = getParameterInt("idMembro", 0);
		if (idM == 0){
		    addMensagemErro("Avaliador Selecionado � inv�lido!");
		} else{
		    AvaliacaoMonitoriaDao daoAM = getDAO(AvaliacaoMonitoriaDao.class);
		    MembroComissao membroComissao = daoAM.findByPrimaryKey(idM, MembroComissao.class);

		    if (membroComissao.getDataInicioMandato() == null || membroComissao.getDataFimMandato() == null){
			addMensagemErro("O per�odo do mandato do Membro da Comiss�o n�o est� definido!");
			addMensagemInformation("Configure as datas de in�cio e t�rmino do mandato deste membro da comiss�o em 'Editar Comiss�o' no menu principal");
			return null;
		    }

		    if (membroComissao.isMandatoVigente()) {
			// Criar AvaliacaoMonitoria 			
			StatusAvaliacao statusAvaliacao = daoAM.findByPrimaryKey(StatusAvaliacao.AGUARDANDO_AVALIACAO, StatusAvaliacao.class);
			AvaliacaoMonitoria avaliacaoMonitoria = new AvaliacaoMonitoria();
			avaliacaoMonitoria.setAvaliador(membroComissao);
			avaliacaoMonitoria.setResumoSid(obj.getResumo());
			avaliacaoMonitoria.setProjetoEnsino(obj.getResumo().getProjetoEnsino());
			avaliacaoMonitoria.setDataDistribuicao(new Date());
			avaliacaoMonitoria.setStatusAvaliacao(statusAvaliacao);
			avaliacaoMonitoria.setTipoAvaliacao	(new TipoAvaliacaoMonitoria(TipoAvaliacaoMonitoria.AVALIACAO_RESUMO_SID));
			obj.getResumo().getAvaliacoes().add(avaliacaoMonitoria);
		    } else {
			addMensagemErro("O mandato do Membro da Comiss�o est� vencido!");
			addMensagemInformation("Renove o Mantado deste membro de comiss�o no link Editar Comiss�o do menu principal.");
		    }
		    return null;
		}

	    } catch (DAOException e) {
		notifyError(e);
		addMensagemErro("Erro ao Adicionar avaliador!");
	    }

	    return null;
	}
	
	
		
	
	/**
	 * Busca resumo do semin�rio de inicia��o � doc�ncia selecionado
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\DistribuicaoResumoSid\lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String selecionarResumoSid() throws ArqException {
	    try {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		prepareMovimento(SigaaListaComando.DISTRIBUIR_MONITORIA);			
		int id = getParameterInt("id",0);
		GenericDAO dao = getGenericDAO();
		obj.setResumo(dao.findByPrimaryKey(id, ResumoSid.class)); //seta o resumo escolhido na distribui��o		
		return forward( ConstantesNavegacaoMonitoria.DISTRIBUICAORESUMO_FORM );
	    } catch (DAOException e) {
		notifyError(e);
	    }
	    return null;	
	}

	
	/**
	 *
	 * Lista todos os membros da comiss�o de monitoria e cient�fica que podem avaliar o
	 * resumo selecionado
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/DistribuicaoProjeto/form.jsp</li>
	 * </ul>
	 *
	 */
	public Collection<MembroComissao> getMembrosComissaoDisponiveis() {

		try {
		    	MembroComissaoDao dao = getDAO(MembroComissaoDao.class);
			AvaliacaoMonitoriaDao daoAM = getDAO(AvaliacaoMonitoriaDao.class);

			// Todos os membros da comiss�o de monitoria e cient�fica
			obj.setAvaliadoresPossiveis( dao.findByComissao(new Integer(MembroComissao.MEMBRO_COMISSAO_MONITORIA)) );
			obj.getAvaliadoresPossiveis().addAll( dao.findByComissao(new Integer(MembroComissao.MEMBRO_COMISSAO_CIENTIFICA)) );

			
			//remove avaliadores cancelados pra n�o contar com os 3 (m�ximos permitidos)...
			Collection<AvaliacaoMonitoria> avalicoesCanceladas = daoAM.findByProjetosStatusAvaliacao(null, obj.getResumo().getId(), StatusAvaliacao.AVALIACAO_CANCELADA);
			obj.getResumo().getAvaliacoes().removeAll(avalicoesCanceladas);
			
			//retira os membros que j� est�o avaliando o resumo
			obj.getResumo().getAvaliacoes().iterator();
			for (AvaliacaoMonitoria avaProj : obj.getResumo().getAvaliacoes()){
				
				//removendo avaliadores da lista de avaliadores poss�veis
				if (avaProj.getTipoAvaliacao().getId() == TipoAvaliacaoMonitoria.AVALIACAO_RESUMO_SID)
					obj.getAvaliadoresPossiveis().remove( avaProj.getAvaliador() );				
				
			}
			
			return obj.getAvaliadoresPossiveis();

		} catch (DAOException e) {
			notifyError(e);
		}
		
		return null;

	}
	
	
	
	
	
	/**
	 *
	 * Confere as regras e distribui os projetos para os avaliadores. Verifica a
	 * situa��o de cada avaliador da lista e muda sua situa��o junto ao projeto
	 * atual
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li> sigaa.war\monitoria\DistribuicaoResumoSid\form.jsp</li>
	 * </ul> 
	 * @throws SegurancaException
	 *
	 *
	 */
	public String distribuir() throws SegurancaException {
	    checkRole(SigaaPapeis.GESTOR_MONITORIA);
	    if (!ValidatorUtil.isEmpty(obj.getResumo().getAvaliacoes())){
		try {
		    DistribuicaoMonitoriaMov mov = new DistribuicaoMonitoriaMov();
		    mov.setAcao(DistribuicaoMonitoriaMov.DISTRIBUIR_RESUMOS);
		    mov.setCodMovimento(SigaaListaComando.DISTRIBUIR_MONITORIA);					
		    mov.setObjMovimentado(obj.getResumo());
		    mov.setAvaliadoresRemovidos(obj.getAvaliadoresRemovidos());					
		    execute(mov, getCurrentRequest());		
		    addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		    return forward( ConstantesNavegacaoMonitoria.DISTRIBUICAORESUMO_LISTA );
		} catch (Exception e) {
		    notifyError(e);
		    addMensagemErro(e.getMessage());
		    return null;
		}

	    }else{
		addMensagemErro("N�o h� Avaliadores selecionados  para distribuir o resumo!");
		return null;
	    }
	}	

	
	public TipoAvaliacaoMonitoria getTipoAvaliacao() {
		return tipoAvaliacao;
	}

	public void setTipoAvaliacao(TipoAvaliacaoMonitoria tipoAvaliacao) {
		this.tipoAvaliacao = tipoAvaliacao;
	}


	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
	}

	/**
	 * 
	 * verifica se usu�rio logado tem papel de gestor de monitoria e redireciona para lista de distribui��o de resumos
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/DistribuicaoResumoSid/form.jsp</li>
	 * </ul> 
	 * @return
	 * @throws SegurancaException
	 */
	public String distribuirOutroResumo() throws SegurancaException{
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		resetBean();
		return forward( ConstantesNavegacaoMonitoria.DISTRIBUICAORESUMO_LISTA);		
	}

	
	
}