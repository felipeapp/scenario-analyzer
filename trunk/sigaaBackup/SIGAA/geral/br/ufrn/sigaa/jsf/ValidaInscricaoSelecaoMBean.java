/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 02/09/2010
 *
 */
package br.ufrn.sigaa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.EditalProcessoSeletivoDao;
import br.ufrn.sigaa.arq.dao.ensino.InscricaoSelecaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.InscricaoSelecao;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;

/**
 * Classe respons�vel em validar as inscri��es.
 * @author mario
 *
 */
@Component("validaInscricaoSelecaoMBean") 
@Scope("request")
public class ValidaInscricaoSelecaoMBean extends SigaaAbstractController<ProcessoSeletivo> {
	
	/** Lista de inscri��es a validar.*/
	private Collection<InscricaoSelecao> inscricoes;
	
	/** Lista de Inscri��es de Candidatos que pagaram duas ou mais inscri��es.*/
	private Collection<InscricaoSelecao> inscricoesDuplicadas;
	
	/** Caractere separador utilizado na lista de inscri��es */
	private String separador;

	/** String contendo a lista, separado por v�rgula, tabula��o ou ponto-e-v�rgula, dos n�meros de refer�ncia da GRU pagas. */
	private String listaValidacao;

	/** Lista de inscri��es que foram pagas. */
	private Collection<InscricaoSelecao> inscricoesPagas;
	
	/**
	 * Valida as inscri��es dos candidato em processo seletivo que possui taxa de inscri��o.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String confirmaPagamento() throws ArqException {
		obj = new ProcessoSeletivo();
		setId();
		EditalProcessoSeletivoDao dao = getDAO(EditalProcessoSeletivoDao.class);
		obj = dao.refresh(obj);
		if (obj == null) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			obj = new ProcessoSeletivo();
			return null;
		}
		InscricaoSelecaoDao isDao = getDAO(InscricaoSelecaoDao.class);
		inscricoesPagas = isDao.verificaInscricaoGRUPaga(obj.getId());
		if (isEmpty(inscricoesPagas)) {
			addMensagemErro("N�o h� GRUs a validar no momento.");
			return null;
		}
		obj.getEditalProcessoSeletivo().setQtdInscritos(dao.countTotalInscricoesByEdital(obj.getEditalProcessoSeletivo().getId()));
		prepareMovimento(SigaaListaComando.VALIDAR_INSCRICAO_SELECAO_LOTE);
		setOperacaoAtiva(SigaaListaComando.VALIDAR_INSCRICAO_SELECAO_LOTE.getId());
		return forward(getDirBase() + "/confirma_pagamento.jsp");
	}
	
	/**
	 * M�todo que possuia todas valida��es associadas a inscri��o
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/confirma_pagamento.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public String validar() throws NegocioException, ArqException{
		if (isEmpty(inscricoesPagas)) 
			addMensagemErro("N�o h� inscri��es para validar o pagamento da GRU.");
		if (hasErrors())
			return null;
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setColObjMovimentado(inscricoesPagas);
		mov.setCodMovimento(SigaaListaComando.VALIDAR_INSCRICAO_SELECAO_LOTE);
		execute(mov);
		addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Inscri��es");
		return cancelar();
	}
	
	/** Valida os dados para a valida��o em lote das inscri��es.
	 * M�todo n�o invocado por JSP.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(br.ufrn.arq.negocio.validacao.ListaMensagens)
	 */
	@Override
	public boolean validacaoDados(ListaMensagens mensagens) {
		ValidatorUtil.validateRequired(obj, "Processo Seletivo", mensagens);
		ValidatorUtil.validateRequired(listaValidacao, "N�mero(s) de Refer�ncia da GRU", mensagens);
		if (!"\t".equals(separador)) 
			ValidatorUtil.validateRequired(separador, "Separador", mensagens);
		return mensagens.isErrorPresent();
	}
	
	/**
	 * JSP: M�todo n�o invocado por JSP's'.
	 */
	@Override
	public String getDirBase() {
		return "/administracao/cadastro/ProcessoSeletivo";
	}

	public Collection<InscricaoSelecao> getInscricoes() {
		return inscricoes;
	}

	public void setInscricoes(Collection<InscricaoSelecao> inscricoes) {
		this.inscricoes = inscricoes;
	}

	public String getSeparador() {
		return separador;
	}

	public void setSeparador(String separador) {
		this.separador = separador;
	}

	public Collection<InscricaoSelecao> getInscricoesDuplicadas() {
		return inscricoesDuplicadas;
	}

	public void setInscricoesDuplicadas(
			Collection<InscricaoSelecao> inscricoesDuplicadas) {
		this.inscricoesDuplicadas = inscricoesDuplicadas;
	}

	public String getListaValidacao() {
		return listaValidacao;
	}

	public void setListaValidacao(String listaValidacao) {
		this.listaValidacao = listaValidacao;
	}

	public Collection<InscricaoSelecao> getInscricoesPagas() {
		return inscricoesPagas;
	}

	public void setInscricoesPagas(Collection<InscricaoSelecao> inscricoesPagas) {
		this.inscricoesPagas = inscricoesPagas;
	}
}
