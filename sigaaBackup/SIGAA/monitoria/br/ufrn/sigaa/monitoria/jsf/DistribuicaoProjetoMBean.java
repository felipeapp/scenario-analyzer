/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 * 
 * Created on 04/04/2007
 * 
 */
package br.ufrn.sigaa.monitoria.jsf;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.monitoria.AvaliacaoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroComissaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.AvaliacaoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocente;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao;
import br.ufrn.sigaa.monitoria.dominio.TipoAvaliacaoMonitoria;
import br.ufrn.sigaa.monitoria.jsf.dominio.DistribuicaoProjeto;
import br.ufrn.sigaa.monitoria.negocio.DistribuicaoMonitoriaMov;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * MBean respons�vel pela distribui��o de projetos
 * @author ilueny santos
 *
 */
@Component("distribuicaoProjeto") @Scope("session")
public class DistribuicaoProjetoMBean extends SigaaAbstractController<DistribuicaoProjeto> {

	/** Atributo utilizado para representar o Tipos de Alavalia��o de Monitoria */
	private TipoAvaliacaoMonitoria tipoAvaliacao;

	public DistribuicaoProjetoMBean() {
		obj = new DistribuicaoProjeto();

		try {
			//seta o tipo de avalia��o que dever� ser feita ap�s a distribui��o
			//podem ser AVALIA��O DE PROJETO ou AVALIACAO DE RESUMO SID
			GenericDAO dao = getGenericDAO();
			setTipoAvaliacao(dao.findByPrimaryKey(TipoAvaliacaoMonitoria.AVALIACAO_PROJETO_ENSINO, TipoAvaliacaoMonitoria.class));

		} catch (DAOException e) {
			notifyError(e);
			addMensagemErro("Erro ao definir tipo de Avalia��o!");
		}

	}
	
	/**
	 * Redireciona para p�gina com lista de projetos
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war\monitoria\DistribuicaoProjeto\form.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String distribuirOutroProjeto() throws SegurancaException{
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		resetBean();
		return forward( ConstantesNavegacaoMonitoria.DISTRIBUICAOPROJETO_LISTA);		
	}

	
	/**
	 * Remove avalia��o do projeto.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li>sigaa.war\monitoria\DistribuicaoProjeto\form.jsp</li></ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String removerAvaliacao() throws SegurancaException{
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		
		try {
			
			AvaliacaoMonitoriaDao daoAM = getDAO(AvaliacaoMonitoriaDao.class);
		
			Integer idM = getParameterInt("idMembroComissao");
	
			List<AvaliacaoMonitoria> avaliacoes = obj.getProjetoEnsino().getAvaliacoes();
			for (AvaliacaoMonitoria am : avaliacoes) {
				if (am.getTipoAvaliacao().getId() == TipoAvaliacaoMonitoria.AVALIACAO_PROJETO_ENSINO) {
						if (am.getAvaliador().getId() == idM) {
							avaliacoes.remove(am);  //remove a avalia��o do projeto
							
							//verifica se esse docente j� avaliava esse projeto
							AvaliacaoMonitoria a = daoAM.findByProjetoResumoAvaliador(obj.getProjetoEnsino().getId(), 0, idM, TipoAvaliacaoMonitoria.AVALIACAO_PROJETO_ENSINO);
								if (( a != null ) && (a.getId() > 0))									
									if (obj.getAvaliadoresRemovidos().add(am.getAvaliador())) //s� coloca na lista de removidos os que j� avaliavam o projeto
										break;
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
	 * Adiciona uma avalia��o ao projeto
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\DistribuicaoProjeto\form.jsp</li>
	 * </ul> 
	 * @return
	 * @throws SegurancaException
	 */
	public String adicionarAvaliacao() throws SegurancaException{
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		
		try{
		
			int idM = getParameterInt("idMembro");
			
			if (idM > 0){
				
				AvaliacaoMonitoriaDao daoAM = getDAO(AvaliacaoMonitoriaDao.class);
				
	
				//pega o membro completo do banco
				MembroComissao membroComissao = daoAM.findByPrimaryKey(idM, MembroComissao.class);
				
				if (membroComissao.getDataFimMandato() == null){
					addMensagemErro("O per�odo do mandato do Membro da Comiss�o n�o est� definido!");
					addMensagemInformation("Configure as datas de in�cio e t�rmino do mandato deste membro da comiss�o em 'Editar Comiss�o' no menu principal");
					return null;
				}
						
				if( (membroComissao.getDataFimMandato().compareTo(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)) < 0)
				|| (membroComissao.getDataInicioMandato().compareTo(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)) > 0) ){
					addMensagemErro("O mandato do Membro da Comiss�o est� vencido!");
					addMensagemInformation("Renove o mantado deste membro de comiss�o no link Editar Comiss�o do menu principal.");
					return null;
				}
								
				
				StatusAvaliacao statusAvaliacao = daoAM.findByPrimaryKey(StatusAvaliacao.AGUARDANDO_AVALIACAO, StatusAvaliacao.class);
				
				// Criar AvaliacaoMonitoria 			
				AvaliacaoMonitoria avaliacaoMonitoria = new AvaliacaoMonitoria();	
								   avaliacaoMonitoria.setAvaliador(membroComissao);
								   avaliacaoMonitoria.setProjetoEnsino(obj.getProjetoEnsino());	
								   avaliacaoMonitoria.setDataDistribuicao(new Date());
								   avaliacaoMonitoria.setStatusAvaliacao(statusAvaliacao);
								   avaliacaoMonitoria.setTipoAvaliacao(new TipoAvaliacaoMonitoria(TipoAvaliacaoMonitoria.AVALIACAO_PROJETO_ENSINO));
				
				obj.getProjetoEnsino().getAvaliacoes().add(avaliacaoMonitoria);
				
			}else{
				addMensagemErro("Avaliador Selecionado � inv�lido!");
			}
		
		} catch (DAOException e) {
			notifyError(e);
			addMensagemErro("Erro ao Adicionar avaliador!");
		}
		
		return null;
		
	}
	
		
	
	
	/**
	 * Busca projeto selecionado.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\DistribuicaoProjeto\lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String selecionarProjeto() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		
		try {
			
			prepareMovimento(SigaaListaComando.DISTRIBUIR_MONITORIA);			
			int id = getParameterInt("id",0);

			if (id > 0){
				GenericDAO dao = getGenericDAO();
				obj.setProjetoEnsino(dao.findByPrimaryKey(id, ProjetoEnsino.class)); //seta o projeto escolhido na distribui��o		
				dao.refresh(obj.getProjetoEnsino().getUnidade());
				return forward( ConstantesNavegacaoMonitoria.DISTRIBUICAOPROJETO_FORM );
				
			}else{
				addMensagemErro("Projeto n�o selecionado.");
				return null;
			}
				
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro buscar projeto selecionado");
		}
		
		return null;

	}

	
	
	/**
	 *
	 * Lista todos os membros da comiss�o de monitoria que podem avaliar o
	 * projeto selecionado
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
			
			// Todos os membros ativos da comiss�o de monitoria
			obj.setAvaliadoresPossiveis( dao.findByComissao(new Integer(MembroComissao.MEMBRO_COMISSAO_MONITORIA)) );

			//remove avaliadores cancelados pra n�o contar com os 3...
			Collection<AvaliacaoMonitoria> avalicoesCanceladas = daoAM.findByProjetosStatusAvaliacao(obj.getProjetoEnsino().getId(), null, StatusAvaliacao.AVALIACAO_CANCELADA);
			obj.getProjetoEnsino().getAvaliacoes().removeAll(avalicoesCanceladas);
			
			//retira os membros que j� est�o avaliando o projeto
			obj.getProjetoEnsino().getAvaliacoes().iterator();
			for (AvaliacaoMonitoria avaProj : obj.getProjetoEnsino().getAvaliacoes()){
				
				//removendo avaliadores da lista de avaliadores poss�veis
				if (avaProj.getTipoAvaliacao().getId() == TipoAvaliacaoMonitoria.AVALIACAO_PROJETO_ENSINO)
					obj.getAvaliadoresPossiveis().remove( avaProj.getAvaliador() );				
				
			}
			
			//Remove coordenador do projeto, caso o mesmo fa�a parte a comiss�o de monitoria
			for (MembroComissao avaliador : obj.getAvaliadoresPossiveis()) {
					if (obj.getProjetoEnsino().getCoordenacao().getId() == avaliador.getServidor().getId()){
						obj.getAvaliadoresPossiveis().remove( avaliador );
						break;
					}
			}
			
			//Remove os docentes envolvidos no projeto, caso os mesmos fa�am parte da comiss�o de monitoria
			Collection<MembroComissao> membrosRemoverProjeto = new ArrayList<MembroComissao>();
			for(MembroComissao avaliador : obj.getAvaliadoresPossiveis()){
				if(obj.getProjetoEnsino().getEquipeDocentes().contains(new EquipeDocente(0, false, null, true, avaliador.getServidor())))
					membrosRemoverProjeto.add( avaliador );
			}
			obj.getAvaliadoresPossiveis().removeAll(membrosRemoverProjeto);
			
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
	 *  <li>sigaa.war\monitoria\DistribuicaoProjeto\form.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 *
	 */
	public String distribuir() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);

		try {
			DistribuicaoMonitoriaMov mov = new DistribuicaoMonitoriaMov();
			mov.setAcao(DistribuicaoMonitoriaMov.DISTRIBUIR_PROJETOS);
			mov.setCodMovimento(SigaaListaComando.DISTRIBUIR_MONITORIA);
			
			//setando a nova situa��o do projeto
			obj.getProjetoEnsino().setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_AGUARDANDO_AVALIACAO));

			mov.setObjMovimentado(obj.getProjetoEnsino());
			mov.setAvaliadoresRemovidos(obj.getAvaliadoresRemovidos());			
			execute(mov, getCurrentRequest());
			addMensagemInformation("Projeto Distribuido com sucesso");
			
			//atualiza lista de projetos
			((ProjetoMonitoriaMBean)getMBean("projetoMonitoria")).localizar();
			
			return distribuirOutroProjeto();			

		}catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		}

	}

	public TipoAvaliacaoMonitoria getTipoAvaliacao() {
		return tipoAvaliacao;
	}

	public void setTipoAvaliacao(TipoAvaliacaoMonitoria tipoAvaliacao) {
		this.tipoAvaliacao = tipoAvaliacao;
	}


	/**
	 * M�todo utilizado para checar os papeis do usu�rio e verificar se o mesmo tem permiss�o de acesso ao caso de uso
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamado por JSP(s)</li>
	 * </ul>
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
	}
	
}