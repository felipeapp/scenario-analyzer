/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 09/04/2007
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.ReconsideracaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.AutorizacaoReconsideracao;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;

/**
 * Managed-Bean para autorização dos projetos de monitoria que solicitaram
 * reconsideração quanto a formalização do projeto.
 * 
 * @author ilueny santos
 * 
 */
@Component("autorizacaoReconsideracao") @Scope("session")
public class AutorizacaoReconsideracaoMBean extends
		SigaaAbstractController<AutorizacaoReconsideracao> {

	private Collection<AutorizacaoReconsideracao> autorizacoes;
	
	private Collection<ProjetoEnsino> projetosReconsideraveis;


	public AutorizacaoReconsideracaoMBean() {
		obj = new AutorizacaoReconsideracao();
	}

	/**
	 * Prepara o ambiente para listar todas as solcitações de reconsideração
	 * de projetos de monitoria.
	 *
	 * Chamado por: sigaa/monitoria/index.jsp
	 * 
	 * @return
	 */
	public String listarSolicitacoes() {
		try {
			ReconsideracaoDao dao = getDAO(ReconsideracaoDao.class);			
			autorizacoes = dao.findAllAutorizacoesAtivas();			
			return forward(ConstantesNavegacaoMonitoria.AUTORIZACAORECONSIDERACAO_LISTA);
		} catch (DAOException e) {
			notifyError(e);	
			return null;
		}   
	}

	/**
	 * Lista de projetos pendentes de autorização
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public Collection<ProjetoEnsino> getAllProjetosParaAutorizacaoReconsideracao() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		try {
			ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class);
			return dao.findByAutorizacaoReconsideracaoPendente();
		} catch (DAOException e) {
			notifyError(e);
			return new ArrayList<ProjetoEnsino>();
		}
	}


	/**
	 * Lista todos os projetos passíveis de reconsideração do docente.
	 * 
	 * Chamado por: /sigaa/portais/docente/menu_docente.jsp
	 * 
	 * @return
	 */
	public String listarMeusProjetosReconsideraveis() {
		try {
			ReconsideracaoDao dao = getDAO(ReconsideracaoDao.class);
			projetosReconsideraveis = dao.findProjetosReconsideraveis(getUsuarioLogado().getServidor().getId(), getUsuarioLogado().getId());
			return forward(ConstantesNavegacaoMonitoria.SOLICITACAORECONSIDERACAOCADASTRO_LISTA);
			
		} catch (DAOException e) {
			notifyError(e);
			return null;
		}
	}
	
	
	/**
	 * Lista de projetos pendentes de autorização de reconsideração do cadastro da proposta
	 * Coordenador do projeto vai listar todos os projetos que cadastrou que podem ser re-considerados 
	 * 
	 * @return
	 */
	public Collection<ProjetoEnsino> getProjetosReconsideraveisUsuarioLogado() {
	    return projetosReconsideraveis;
	}

	/**
	 * Coordenador solicita reconsideração da avaliação 
	 * do projeto
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\SolicitacaoReconsideracaoCadastro\form.jsp</li>
	 * </ul>
	 * @return
	 */
	public String solicitarReconsideracao(){
		
		obj.setDataSolicitacao(new Date());
		obj.setRegistroEntradaSolicitante(getUsuarioLogado().getRegistroEntrada());
		
		try {

			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setAcao(MovimentoCadastro.ACAO_CRIAR);
			mov.setCodMovimento(SigaaListaComando.AUTORIZAR_RECONSIDERACAO);
			
			execute(mov, getCurrentRequest());
			addMensagemInformation("Solicitação de Reconsideração por Requisitos Formais realizada com sucesso!");


		} catch (Exception e) {
			addMensagemErro("Erro no Cadastro da Solicitação da Reconsideração!");
			notifyError(e);
			return forward(ConstantesNavegacaoMonitoria.SOLICITACAORECONSIDERACAOCADASTRO_FORM);
		}

		resetBean();
		return forward(ConstantesNavegacaoMonitoria.SOLICITACAORECONSIDERACAOCADASTRO_LISTA);		
		
		
	}
	
	
	
	/**
	 * Lista de todas autorizações pendentes.
	 * 
	 * @return
	 */
	public Collection<AutorizacaoReconsideracao> getAutorizacoes() {
	    return autorizacoes;
	}

	
	
	
	/**
	 * Escolhe uma autorização que ainda não foi analisada
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\AutorizacaoReconsideracao\lista.jsp</li>
	 * </ul>
	 * @return
	 * 
	 */
	public String escolherAutorizacao() {

		GenericDAO dao = getGenericDAO();

		try {

			obj = dao.findByPrimaryKey(getParameterInt("idAutorizacao"),
					AutorizacaoReconsideracao.class);

			//carrega as autorizações para excluir depois, no processador...
			ProjetoEnsino pm = obj.getProjetoEnsino();
			pm.getAutorizacoesProjeto().iterator();
			
			prepareMovimento(SigaaListaComando.AUTORIZAR_RECONSIDERACAO);
			setConfirmButton("Confirmar");

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}

		return forward(ConstantesNavegacaoMonitoria.AUTORIZACAORECONSIDERACAO_FORM);

	}

	
	
	
	/**
	 * Escolhe uma autorização que ainda não foi analisada
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\ProjetoMonitoria\meus_projetos.jsp</li>
	 *  <li>sigaa.war\monitoria\SolicitacaoReconsideracaoCadastro\lista.jsp</li>
	 * </ul>  
	 * @return
	 * 
	 */
	public String escolherProjeto() {

		GenericDAO dao = getGenericDAO();

		try {

			ProjetoEnsino pm = dao.findByPrimaryKey(getParameterInt("id"), ProjetoEnsino.class);
			
			obj.setProjetoEnsino(pm);			
			
			prepareMovimento(SigaaListaComando.AUTORIZAR_RECONSIDERACAO);
			setConfirmButton("Cadastrar Solicitação");

		} catch (Exception e) {
			addMensagemErro("Erro ao Buscar Projeto!");
			notifyError(e);
			return forward(ConstantesNavegacaoMonitoria.SOLICITACAORECONSIDERACAOCADASTRO_LISTA);
		}

		return forward(ConstantesNavegacaoMonitoria.SOLICITACAORECONSIDERACAOCADASTRO_FORM);

	}

	
	
	
	
	/**
	 * Autorizar um projeto de monitoria
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s): 
	 * JSP: sigaa.war\monitoria\AutorizacaoReconsideracao\form.jsp
	 * @return
	 */
	public String autorizar() {

		obj.setDataAutorizacao(new Date());
		obj.setRegistroEntradaAutorizador(getUsuarioLogado().getRegistroEntrada());
		
		ListaMensagens lista = obj.validate();
		if (lista != null && lista.getMensagens().size() > 0) {
			addMensagens(lista);
			return forward(ConstantesNavegacaoMonitoria.AUTORIZACAORECONSIDERACAO_FORM);
		}

		try {

			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setAcao(MovimentoCadastro.ACAO_CRIAR);
			mov.setCodMovimento(SigaaListaComando.AUTORIZAR_RECONSIDERACAO);			
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			return listarSolicitacoes(); 

		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			notifyError(e);
			return null;
		}
	}

	
	@Override
	public String cancelar() {

		resetBean();
		if (getConfirmButton().equalsIgnoreCase("Cadastrar Solicitação")){
			return forward(ConstantesNavegacaoMonitoria.SOLICITACAORECONSIDERACAOCADASTRO_LISTA);
		}else{
			return forward(ConstantesNavegacaoMonitoria.AUTORIZACAORECONSIDERACAO_LISTA);
		}
	}


	public Collection<ProjetoEnsino> getProjetosReconsideraveis() {
	    return projetosReconsideraveis;
	}


	public void setProjetosReconsideraveis(
		Collection<ProjetoEnsino> projetosReconsideraveis) {
	    this.projetosReconsideraveis = projetosReconsideraveis;
	}


	public void setAutorizacoes(Collection<AutorizacaoReconsideracao> autorizacoes) {
	    this.autorizacoes = autorizacoes;
	}	

}