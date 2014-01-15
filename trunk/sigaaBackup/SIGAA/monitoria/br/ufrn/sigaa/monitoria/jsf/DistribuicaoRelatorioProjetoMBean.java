/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 * 
 * Created on 25/06/2007
 * 
 */
package br.ufrn.sigaa.monitoria.jsf;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.monitoria.AvaliacaoRelatorioProjetoDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroComissaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.AvaliacaoRelatorioProjeto;
import br.ufrn.sigaa.monitoria.dominio.RelatorioProjetoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao;
import br.ufrn.sigaa.monitoria.jsf.dominio.DistribuicaoProjeto;
import br.ufrn.sigaa.monitoria.negocio.DistribuicaoMonitoriaMov;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * 
 * Classe respons�vel pela distribui��o dos relat�rios parciais e finais
 * dos projeto de monitoria.
 *
 * @author ilueny santos
 *
 */
@Component("distribuicaoRelatorio") @Scope("session")
public class DistribuicaoRelatorioProjetoMBean extends SigaaAbstractController<DistribuicaoProjeto> {

	private Collection<RelatorioProjetoMonitoria> relatoriosProjeto;
	
	public DistribuicaoRelatorioProjetoMBean() {
		obj = new DistribuicaoProjeto();
		relatoriosProjeto = new ArrayList<RelatorioProjetoMonitoria>();
	}
	
	/**
	 * Redireciona para p�gina com lista de relat�rios.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\DistribuicaoRelatorioProjeto\form.jsp</li>
	 * </ul> 
	 * @return
	 * @throws SegurancaException
	 */
	public String distribuirOutroRelatorio() throws SegurancaException{
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		resetBean();
		return forward( ConstantesNavegacaoMonitoria.DISTRIBUICAORELATORIOPROJETO_LISTA);		
	}
	
	/**
	 * Remove avalia��o do relat�rio
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\DistribuicaoRelatorioProjeto\form.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String removerAvaliacao() throws SegurancaException{
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		
		try {
			
			AvaliacaoRelatorioProjetoDao daoAR = getDAO(AvaliacaoRelatorioProjetoDao.class);
			Integer idM = getParameterInt("idMembroComissao");
			Set<AvaliacaoRelatorioProjeto> avaliacoes = obj.getRelatorio().getAvaliacoes();
			
			for (AvaliacaoRelatorioProjeto ar : avaliacoes) {
						if (ar.getAvaliador().getId() == idM) {
							avaliacoes.remove(ar);  //remove a avalia��o do relat�rio
							addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
							
							//verifica se esse docente j� avaliava esse relat�rio
							AvaliacaoRelatorioProjeto a =  daoAR.findByRelatorioAvaliador(obj.getRelatorio().getId(), idM);
								if (( a != null ) && (a.getId() > 0))									
									if (obj.getAvaliadoresRemovidos().add(ar.getAvaliador())) //s� coloca na lista de removidos os que j� avaliavam o relat�rio
										break;
									
							break;
								
						}
				}
			
		} catch (DAOException e) {
			notifyError(e);
		} 
		
		return null;
		
	}

	
	
	/**
	 * Adiciona avalia��o a um relat�rio
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\DistribuicaoRelatorioProjeto\form.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String adicionarAvaliacao() throws SegurancaException{
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		
		try{
		
			int idM = getParameterInt("idMembro");
			
			if (idM > 0){
				
				AvaliacaoRelatorioProjetoDao daoAM = getDAO(AvaliacaoRelatorioProjetoDao.class);	
				//pega o membro completo do banco
				MembroComissao membroComissao = daoAM.findByPrimaryKey(idM, MembroComissao.class);				
				if (membroComissao.getDataFimMandato() == null){
					addMensagemErro("O per�odo do mandato do membro da comiss�o n�o est� definido!");
					addMensagemInformation("Configure as datas de in�cio e t�rmino do mandato deste membro da comiss�o em 'Editar Comiss�o' no menu principal");
					return null;
				}
				
				if( (membroComissao.getDataFimMandato().compareTo(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)) < 0)
						|| (membroComissao.getDataInicioMandato().compareTo(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)) > 0) ){
					
					addMensagemErro("O mandato do membro da comiss�o est� vencido!");
					addMensagemErro("Renove o mandato deste membro de comiss�o no link Editar Comiss�o do menu principal.");
					return null;
				}
				
				
				if ((obj.getRelatorio().getTotalAvaliadoresRelatorioAtivos() + 1) > 1){
				    	addMensagemErro("O Relat�rio deve ser avaliado por somente 1(um) membro da comiss�o de monitoria!");
					return null;
				}

				StatusAvaliacao statusAvaliacao = daoAM.findByPrimaryKey(StatusAvaliacao.AGUARDANDO_AVALIACAO, StatusAvaliacao.class);
				
				// Criar AvaliacaoRelatorioProjeto 			
				AvaliacaoRelatorioProjeto avaliacaoRelatorio = new AvaliacaoRelatorioProjeto();
				avaliacaoRelatorio.setAvaliador(membroComissao);
				avaliacaoRelatorio.setRelatorioProjetoMonitoria(obj.getRelatorio());
				avaliacaoRelatorio.setDataDistribuicao	(new Date());
				avaliacaoRelatorio.setStatusAvaliacao	(statusAvaliacao);
				//n�o recomenda e mant�m por padr�o....
				avaliacaoRelatorio.setRecomendaRenovacao(false);
				avaliacaoRelatorio.setMantemQuantidadeBolsas(true);
						
				if ( !relatoriosProjeto.isEmpty() ) {
					for (RelatorioProjetoMonitoria rel : relatoriosProjeto) {
						avaliacaoRelatorio.setRelatorioProjetoMonitoria(rel);
						rel.getAvaliacoes().add(avaliacaoRelatorio);
					}
				}
				obj.getRelatorio().getAvaliacoes().add(avaliacaoRelatorio);
						
			}else{
				addMensagemErro("Avaliador n�o selecionado!");
			}
		
		} catch (DAOException e) {
			notifyError(e);
			addMensagemErro("Erro ao Adicionar avaliador!");
		}
		
		return null;
		
	}
	
		
	
	
	/**
	 * Busca relat�rio selecionado.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\DistribuicaoRelatorioProjeto\lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String selecionarRelatorio() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		try {
			prepareMovimento(SigaaListaComando.DISTRIBUIR_MONITORIA);
			int id = getParameterInt("id");
			GenericDAO dao = getGenericDAO();
			obj.setRelatorio(dao.findByPrimaryKey(id, RelatorioProjetoMonitoria.class)); //seta o relat�rio escolhido na distribui��o		
			return forward( ConstantesNavegacaoMonitoria.DISTRIBUICAORELATORIOPROJETO_FORM );
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro buscar relat�rio selecionado");
		}
		return null;
	}

	public String distribuirRelatorio() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		relatoriosProjeto = new ArrayList<RelatorioProjetoMonitoria>();
		prepareMovimento(SigaaListaComando.DISTRIBUIR_MONITORIA);
		RelatorioProjetoMonitoriaMBean mBean = getMBean("relatorioProjetoMonitoria");
		for (RelatorioProjetoMonitoria relatorio : mBean.getRelatoriosLocalizados()) {
			if (relatorio.isSelecionado())
				relatoriosProjeto.add(relatorio);
		}
		
		return forward( ConstantesNavegacaoMonitoria.DISTRIBUICAORELATORIOPROJETO_FORM );
	}
	
	/**
	 *
	 * Lista todos os membros da comiss�o de monitoria que podem avaliar o
	 * relat�rio selecionado
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/DistribuicaoProjeto/form.jsp</li>
	 * </ul>
	 */
	public Collection<MembroComissao> getMembrosComissaoDisponiveis() {

		try {
		    	MembroComissaoDao dao =  getDAO(MembroComissaoDao.class);
			AvaliacaoRelatorioProjetoDao daoRE = getDAO(AvaliacaoRelatorioProjetoDao.class);
			
			// Todos os membros da comiss�o de monitoria
			obj.setAvaliadoresPossiveis( dao.findByComissao(new Integer(MembroComissao.MEMBRO_COMISSAO_MONITORIA)) );

			//remove avaliadores cancelados pra n�o contar com os 3...
			obj.getRelatorio().getAvaliacoes().removeAll(
					daoRE.findByRelatorioStatusAvaliacao(obj.getRelatorio().getId(), true, StatusAvaliacao.AVALIACAO_CANCELADA)
					);
			
			//retira os membros que j� est�o no projeto
			obj.getRelatorio().getAvaliacoes().iterator();
			for (AvaliacaoRelatorioProjeto avaRel : obj.getRelatorio().getAvaliacoes()){
				
				//removendo avaliadores da lista de avaliadores poss�veis
				obj.getAvaliadoresPossiveis().remove( avaRel.getAvaliador() );
				
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
	 *  <li>sigaa.war\monitoria\DistribuicaoRelatorioProjeto\form.jsp</li>
	 * </ul>
	 * @throws SegurancaException 
	 *
	 */
	public String distribuir() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);

		try {
			DistribuicaoMonitoriaMov mov = new DistribuicaoMonitoriaMov();
			mov.setAcao(DistribuicaoMonitoriaMov.DISTRIBUIR_RELATORIOS);
			mov.setCodMovimento(SigaaListaComando.DISTRIBUIR_MONITORIA);			
			
			//estabelecendo a nova situa��o do projeto
			obj.getRelatorio().getProjetoEnsino().setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_AGUARDANDO_AVALIACAO));
			
			mov.setObjMovimentado(obj.getRelatorio());
			mov.setAvaliadoresRemovidos(obj.getAvaliadoresRemovidos());
			mov.setObjAuxiliar(relatoriosProjeto);
			
			execute(mov, getCurrentRequest());
			addMensagemInformation("Relat�rio Distribu�do com sucesso");
			
			RelatorioProjetoMonitoriaMBean mBean = getMBean("relatorioProjetoMonitoria");
			mBean.setRelatoriosLocalizados(null);
			
			return forward( ConstantesNavegacaoMonitoria.DISTRIBUICAORELATORIOPROJETO_LISTA );			
			

		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
		} catch (Exception e) {
			notifyError(e);
		}
		return null;

	}

	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
	}

	public Collection<RelatorioProjetoMonitoria> getRelatoriosProjeto() {
		return relatoriosProjeto;
	}

	public void setRelatoriosProjeto(
			Collection<RelatorioProjetoMonitoria> relatoriosProjeto) {
		this.relatoriosProjeto = relatoriosProjeto;
	}

}