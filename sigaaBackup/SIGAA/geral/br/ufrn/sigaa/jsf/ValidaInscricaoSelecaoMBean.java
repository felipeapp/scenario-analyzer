/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Classe responsável em validar as inscrições.
 * @author mario
 *
 */
@Component("validaInscricaoSelecaoMBean") 
@Scope("request")
public class ValidaInscricaoSelecaoMBean extends SigaaAbstractController<ProcessoSeletivo> {
	
	/** Lista de inscrições a validar.*/
	private Collection<InscricaoSelecao> inscricoes;
	
	/** Lista de Inscrições de Candidatos que pagaram duas ou mais inscrições.*/
	private Collection<InscricaoSelecao> inscricoesDuplicadas;
	
	/** Caractere separador utilizado na lista de inscrições */
	private String separador;

	/** String contendo a lista, separado por vírgula, tabulação ou ponto-e-vírgula, dos números de referência da GRU pagas. */
	private String listaValidacao;

	/** Lista de inscrições que foram pagas. */
	private Collection<InscricaoSelecao> inscricoesPagas;
	
	/**
	 * Valida as inscrições dos candidato em processo seletivo que possui taxa de inscrição.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
			addMensagemErro("Não há GRUs a validar no momento.");
			return null;
		}
		obj.getEditalProcessoSeletivo().setQtdInscritos(dao.countTotalInscricoesByEdital(obj.getEditalProcessoSeletivo().getId()));
		prepareMovimento(SigaaListaComando.VALIDAR_INSCRICAO_SELECAO_LOTE);
		setOperacaoAtiva(SigaaListaComando.VALIDAR_INSCRICAO_SELECAO_LOTE.getId());
		return forward(getDirBase() + "/confirma_pagamento.jsp");
	}
	
	/**
	 * Método que possuia todas validações associadas a inscrição
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
			addMensagemErro("Não há inscrições para validar o pagamento da GRU.");
		if (hasErrors())
			return null;
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setColObjMovimentado(inscricoesPagas);
		mov.setCodMovimento(SigaaListaComando.VALIDAR_INSCRICAO_SELECAO_LOTE);
		execute(mov);
		addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Inscrições");
		return cancelar();
	}
	
	/** Valida os dados para a validação em lote das inscrições.
	 * Método não invocado por JSP.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(br.ufrn.arq.negocio.validacao.ListaMensagens)
	 */
	@Override
	public boolean validacaoDados(ListaMensagens mensagens) {
		ValidatorUtil.validateRequired(obj, "Processo Seletivo", mensagens);
		ValidatorUtil.validateRequired(listaValidacao, "Número(s) de Referência da GRU", mensagens);
		if (!"\t".equals(separador)) 
			ValidatorUtil.validateRequired(separador, "Separador", mensagens);
		return mensagens.isErrorPresent();
	}
	
	/**
	 * JSP: Método não invocado por JSP's'.
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
