/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 11/11/2008
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConstantesErro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.sigaa.arq.dao.vestibular.InscricaoFiscalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.vestibular.dominio.Fiscal;
import br.ufrn.sigaa.vestibular.dominio.InscricaoFiscal;
import br.ufrn.sigaa.vestibular.dominio.ObjectSeletor;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;
import br.ufrn.sigaa.vestibular.negocio.MovimentoSelecaoManualFiscal;

/** Controller responsável pela seleção manual de fiscais.
 * @author Édipo Elder F. Melo
 *
 */
@Component("selecaoManualFiscal")
@Scope("session")
public class SelecaoManualFiscalMBean extends SigaaAbstractController<Fiscal> {

	/** Indica se esta operação foi cancelada. */
	private boolean operacaoCancelada = false;
	
	/** ID do processo seletivo para o qual se está selecionando fiscais. */
	private int idProcessoSeletivo;
	
	/** Nome para realizar a busca de inscritos. */
	private String nome;
	
	/** Lista de fiscais encontrado com o nome buscado. */
	private Collection<ObjectSeletor<InscricaoFiscal>> listaInscricoesFiscal;
	

	/**
	 * Carrega a lista de Inscrições não selecionados como Fiscais.
	 * 
	 * @throws DAOException
	 */
	private void carregaListaInscricoesFiscal() throws DAOException {
		InscricaoFiscalDao dao = getDAO(InscricaoFiscalDao.class);
		Collection<InscricaoFiscal> lista = dao
				.findInscritosNaoFiscalByPessoaProcessoSeletivo(nome,
						idProcessoSeletivo);
		this.listaInscricoesFiscal = new ArrayList<ObjectSeletor<InscricaoFiscal>>();
		for (InscricaoFiscal inscricao : lista) {
			ObjectSeletor<InscricaoFiscal> sif = new ObjectSeletor<InscricaoFiscal>();
			sif.setObjeto(inscricao);
			sif.setSelecionado(false);
			listaInscricoesFiscal.add(sif);
		}
		operacaoCancelada = false;
	}

	/** Retorna o ID do processo seletivo para o qual se está selecionando fiscais. 
	 * @return
	 */
	public int getIdProcessoSeletivo() {
		return idProcessoSeletivo;
	}

	/** Retorna uma coleção de seletor de inscrições de fiscais.
	 * @return
	 */
	public Collection<ObjectSeletor<InscricaoFiscal>> getListaInscricoesFiscal() {
		return listaInscricoesFiscal;
	}

	/** Retorna o nome para realizar a busca de inscritos. 
	 * @return
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * Entrada do MBean: inicializa as propriedades que serão utilizadas na sua
	 * execução e retorna o formulário para o usuário
	 * 
	 * @return sigaa/vestibular/Fiscal/selecao_manual.jsp
	 */
	public String iniciar() {
		operacaoCancelada = false;
		idProcessoSeletivo = 0;
		nome = "";
		listaInscricoesFiscal = new ArrayList<ObjectSeletor<InscricaoFiscal>>();
		return forward("/vestibular/Fiscal/selecao_manual.jsp");
	}

	/** Seta o ID do processo seletivo para o qual se está selecionando fiscais. 
	 * @param idProcessoSeletivo
	 */
	public void setIdProcessoSeletivo(int idProcessoSeletivo) {
		this.idProcessoSeletivo = idProcessoSeletivo;
	}

	/** Seta a lista de fiscais encontrado com o nome buscado. 
	 * @param listaInscricoesFiscal
	 */
	public void setListaInscricoesFiscal(
			Collection<ObjectSeletor<InscricaoFiscal>> listaInscricoesFiscal) {
		this.listaInscricoesFiscal = listaInscricoesFiscal;
	}

	/** Seta o nome para realizar a busca de inscritos. 
	 * @param nome
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * Realiza a busca de inscritos para fiscais que não fora selecionados
	 *
	 * <br />
	 * chamado por 
	 * <ul>
	 * <li>/sigaa/vestibular/Fiscal/selecao_manual.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#buscar()
	 */
	public String buscar() throws DAOException {
		validacaoDados(erros);
		if (hasErrors())
			return null;
		carregaListaInscricoesFiscal();
		if (this.listaInscricoesFiscal.size() == 0)
			addMensagemWarning("Não foram encontradas inscrições não selecionadas para fiscais com o critério informado");
		return null;
	}

	/** Cadastra o fiscal.
	 * 
	 * <br />
	 * chamado por 
	 * <ul>
	 * <li>/sigaa/vestibular/Fiscal/selecao_manual.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String cadastrar() throws ArqException {
		if (operacaoCancelada)
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA, "");
		validacaoDados(erros);
		if (hasErrors())
			return null;
		MovimentoSelecaoManualFiscal movimento = new MovimentoSelecaoManualFiscal();
		movimento.setInscricoesFiscais(this.listaInscricoesFiscal);
		movimento.setProcessoSeletivo(new ProcessoSeletivoVestibular(
				idProcessoSeletivo));
		movimento.setCodMovimento(SigaaListaComando.SELECAO_MANUAL_FISCAL);
		List<Fiscal> fiscaisTransferidos = new ArrayList<Fiscal>();
		try {
			fiscaisTransferidos = (List<Fiscal>) executeWithoutClosingSession(movimento);
			prepareMovimento(SigaaListaComando.SELECAO_MANUAL_FISCAL);
			carregaListaInscricoesFiscal();
		} catch (NegocioException e) {
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}
		if (fiscaisTransferidos.size() > 0) {
			addMensagemInformation(fiscaisTransferidos.size()
					+ " Fiscais associados com sucesso!");
		} else {
			addMensagemErro("Selecione pelo menos um fiscal a ser transferido");
		}
		return getSubSistema().getForward();
	}

	/** Valida os dados.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(java.util.Collection)
	 */
	@Override
	public boolean validacaoDados(ListaMensagens lista) {
		Collection<MensagemAviso> msgs = new ArrayList<MensagemAviso>();
		if (idProcessoSeletivo == 0) {
			msgs.add(new MensagemAviso("Selecione um Processo Seletivo válido",
					TipoMensagemUFRN.ERROR));
		}
		if (this.nome == null || nome.trim().length() < 3)
			msgs.add(new MensagemAviso(
					"Digite um nome válido com pelo menos três caracteres",
					TipoMensagemUFRN.ERROR));
		lista.addAll(msgs);
		return lista.size() > 0;
	}

	/** Cancela a operação.
	 * @see br.ufrn.arq.web.jsf.AbstractController#cancelar()
	 */
	@Override
	public String cancelar() {
		operacaoCancelada = true;
		return redirectJSF(getSubSistema().getLink());
	}

}
