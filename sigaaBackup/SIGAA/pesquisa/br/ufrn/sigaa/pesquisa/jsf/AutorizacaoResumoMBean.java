/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 22/12/2009
 *
 */
package br.ufrn.sigaa.pesquisa.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.pesquisa.AvaliacaoResumoDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ResumoCongressoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.pesquisa.dominio.AvaliacaoResumo;
import br.ufrn.sigaa.pesquisa.dominio.ResumoCongresso;

/**
 * Controlador para as operações de autorização de resumos do CIC.
 * 
 * @author Daniel Augusto
 *
 */
@Component("autorizacaoResumo") @Scope("session")
public class AutorizacaoResumoMBean extends SigaaAbstractController<ResumoCongresso> {
	
	/** Armazena os resumos pendentes de avaliação */
	private Collection<ResumoCongresso> resumosPendentes;
	
	private Collection<AvaliacaoResumo> avaliacoes;
	
	private AvaliacaoResumo avaliacao;
	
	/**
	 * Exibe todos os resumos submetidos por discentes.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/autorizacao_resumo/autorizar_resumo.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/autorizacao_resumo/correcoes.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String listarResumos() throws DAOException {
		carregarResumos();
		if (resumosPendentes.isEmpty()) {
			addMensagemErro("Não há resumos pendentes de autorização.");
			return null;
		}
		return forward("/pesquisa/autorizacao_resumo/lista.jsp");
	}
	
	/**
	 * Recupera todos os resumos do CIC para avaliação, nos quais tenham como coordenador o docente logado.
	 *
 	 * Método não invocado por JSP.
	 * 
	 * @throws DAOException
	 */
	private void carregarResumos() throws DAOException {
		resumosPendentes = getDAO(ResumoCongressoDao.class).findAllPendentesByOrientador(
				getUsuarioLogado().getServidorAtivo().getId());
	}
	
	public String listarResumosComissao() throws DAOException{
		AvaliacaoResumoDao dao = getDAO(AvaliacaoResumoDao.class);
		try {
			avaliacoes = dao.findByAllResumoCongresso();
		} finally {
			dao.close();
		}
	
		return forward("/pesquisa/autorizacao_resumo/lista_comissao.jsp");
	}
	
	/**
	 * Prepara a avaliação do resumo.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/autorizacao_resumo/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String preAutorizarResumo() throws ArqException {
		
		obj = getGenericDAO().findByPrimaryKey(getParameterInt("idResumo"), ResumoCongresso.class);
		prepareMovimento(SigaaListaComando.AUTORIZAR_RESUMO_CONGRESSO_IC);
		prepareMovimento(SigaaListaComando.RECUSAR_RESUMO_CONGRESSO_IC);
		prepareMovimento(SigaaListaComando.RECUSADO_NECESSITA_CORRECOES);
		return forward("/pesquisa/autorizacao_resumo/autorizar_resumo.jsp");
	}
	
	/**
	 * Prepara a avaliação do resumo.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/autorizacao_resumo/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String preAutorizarResumoComissao() throws ArqException {
		avaliacao = getGenericDAO().findAndFetch(getParameterInt("idAvaliacao"), AvaliacaoResumo.class, "avaliador", "resumo");
		avaliacao.getResumo().setIdAvaliacao(avaliacao.getId());
		setObj(avaliacao.getResumo());
		prepareMovimento(SigaaListaComando.AUTORIZAR_RESUMO_CONGRESSO_IC);
		prepareMovimento(SigaaListaComando.RECUSAR_RESUMO_CONGRESSO_IC);
		prepareMovimento(SigaaListaComando.RECUSADO_NECESSITA_CORRECOES);
		return forward("/pesquisa/autorizacao_resumo/autorizar_resumo_comissao.jsp");
	}
	
	/**
	 * Prepara a avaliação do resumo.
	 * 
	 * Método não invocado por JSP.
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String retornarResumo() throws ArqException {
		
		obj = getGenericDAO().findByPrimaryKey(getParameterInt("idResumo"), ResumoCongresso.class);
		prepareMovimento(SigaaListaComando.AUTORIZAR_RESUMO_CONGRESSO_IC);
		prepareMovimento(SigaaListaComando.RECUSAR_RESUMO_CONGRESSO_IC);
		return forward("/pesquisa/autorizacao_resumo/autorizar_resumo.jsp");
	}
	
	/**
	 * Autoriza o resumo pelo coordenador (docente).
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/autorizacao_resumo/autorizar_resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String autorizarResumo() throws ArqException {
		obj.setCodMovimento(SigaaListaComando.AUTORIZAR_RESUMO_CONGRESSO_IC);
		return modificarStatusResumo();
	}

	/**
	 * Autoriza o resumo pelo coordenador (docente).
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/autorizacao_resumo/autorizar_resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String corrigirResumo() throws ArqException {
		obj.setCodMovimento(SigaaListaComando.RECUSADO_NECESSITA_CORRECOES);
		return modificarStatusResumo();
	}
	
	/**
	 * Recusa o resumo informando as correções necessárias.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/autorizacao_resumo/autorizar_resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String resumoRecusadoNecessitaCorrecoes() throws ArqException {
		obj.setCodMovimento(SigaaListaComando.RECUSADO_NECESSITA_CORRECOES);
		return forward("/pesquisa/autorizacao_resumo/correcoes.jsp");
	}

	/**
	 * Tem como finalidade realizar a modificação do Status do Resumo e 
	 * realiza o envio de um email com as correções solicitadas pelo orientador 
	 * do autor resumo.     
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/autorizacao_resumo/correcoes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String retornarResumoRecusado() throws ArqException {
		if (obj.getCorrecao().equals("") || obj.getCorrecao() == null) {
			erros.addErro("Correção: Campo Obrigatório não informado.");
		}
		if (!hasErrors()) {
			modificarStatusResumo();
			envioEmailPrograma();
		}
		return null; 
	}

	/**
	 * Tem como finalidade enviar um email informando ao autor do 
	 * resumo as correções solicitadas pelo orientador.
	 * 
	 * @throws DAOException
	 */
	private void envioEmailPrograma() throws DAOException {
		MailBody email = new MailBody();
		email.setAssunto("[SIGAA] " + obj.getCodigo() + " - " + obj.getTitulo());
		email.setContentType(MailBody.TEXT_PLAN);
		email.setEmail(obj.getAutor().getEmail());
		email.setNome(obj.getAutor().getNome());
		email.setMensagem("O orientador " + obj.getOrientador().getNome() + 
				" inseriu algumas correções a serem realizadas no resumo: \r\n" + obj.getCorrecao());
			
		Mail.send(email);
	}
	
	/**
	 * Recusa o resumo pelo coordenador (docente).
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/autorizacao_resumo/autorizar_resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String recusarResumo() throws ArqException {
		obj.setCodMovimento(SigaaListaComando.RECUSAR_RESUMO_CONGRESSO_IC);
		return modificarStatusResumo();
	}
	
	/**
	 * Executa a operação para a modificação do estado do resumo.
	 * 
	 * @return
	 * @throws ArqException
	 */
	private String modificarStatusResumo() throws ArqException {
		
		try {
			execute(obj);
			addMensagem(OPERACAO_SUCESSO);
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		carregarResumos();
		
		if (isPortalDocente()) {
			return resumosPendentes.isEmpty() ? redirect("/verPortalDocente.do") 
					: forward("/pesquisa/autorizacao_resumo/lista.jsp");
		} else {
			return listarResumosComissao();
		}

	}

	public void setResumosPendentes(Collection<ResumoCongresso> resumosPendentes) {
		this.resumosPendentes = resumosPendentes;
	}

	public Collection<ResumoCongresso> getResumosPendentes() {
		return resumosPendentes;
	}

	public Collection<AvaliacaoResumo> getAvaliacoes() {
		return avaliacoes;
	}

	public void setAvaliacoes(Collection<AvaliacaoResumo> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}

	public AvaliacaoResumo getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(AvaliacaoResumo avaliacao) {
		this.avaliacao = avaliacao;
	}
	
}