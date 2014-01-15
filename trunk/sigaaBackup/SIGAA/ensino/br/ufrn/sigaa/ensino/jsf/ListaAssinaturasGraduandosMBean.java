/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 04/04/2008
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.ead.PoloDao;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;

/** 
 * Controller responsável por gerar a lista de assinaturas para colação de grau. 
 * @author Édipo Elder F. Melo
 *
 */
@Component("listaAssinaturasGraduandos")
@Scope("request")
public class ListaAssinaturasGraduandosMBean extends SigaaAbstractController<Curso> {

	/** Etapas do processamento da lista de graduação. */
	private enum EtapaProcessamento { INICIAL, BUSCANDO_DISCENTES, CALCULANDO_HISTORICO, CONCLUIDO}
	
	/** Etapa atual do processamento da lista de graduação. */
	private EtapaProcessamento etapaProcessamento; 
	
	/** Ano de graduação dos discentes. */
	private int ano;
	/** Período de graduação dos discentes. */
	private int periodo;
	/** Lista de SelectITens de cursos de graduação. */
	private Collection<SelectItem> listaCurso;
	/** Coleção de discentes graduandos para colação de grau. */
	private Collection<DiscenteGraduacao> graduandos;
	/** Indica se o formulário de busca está habilitado ou desabilitado. */
	private boolean disable;
	/** Polo do curso de EAD para o qual a lista de discentes será gerada. */
	private Polo polo;
	/** Quantidade de discentes que tiveram o histórico recalculado. */
	private int quantidadeProcessado;
	/** Discentes que terão o histórico recalculado. */
	private List<DiscenteGraduacao> discentes;
	/** Discentes que não estão aptos a colar grau e o motivo. */
	private Map<DiscenteGraduacao, String> inaptos;
	/** Registra a hora inicial do recálculo dos históricos. */ 
	private Date inicioProcessamento;
	/** Lista de SelectITens de polos EAD. */
	private Collection<SelectItem> listaPolo;
	
	
	/** Construtor padrão. */
	public ListaAssinaturasGraduandosMBean() {
		initObj();
	}

	/**
	 * Gera a lista de Graduandos.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/relatorios/lista_concluintes/busca_curso.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public String gerarListaGraduandos() throws SegurancaException{
		checkChangeRole();
		validacaoDados(erros);
		if (hasErrors())
			return null;
		
		return forward( "/graduacao/relatorios/lista_concluintes/lista_assinatura_colacao.jsf" );
	}
	
	/** Realiza uma busca por discentes graduandos.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/relatorios/lista_concluintes/busca_curso.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public void buscarGraduandos() throws ArqException {
		checkChangeRole();
		setDisable(true);
		DiscenteGraduacaoDao dao = getDAO(DiscenteGraduacaoDao.class);
		obj = dao.refresh(obj);
		polo = dao.refresh(polo);
		if (polo != null) polo.getDescricao();
		else polo = new Polo();
		validacaoDados(erros);
		if (hasErrors()) {
			setDisable(false);
			redirectMesmaPagina();
			return;
		}
		etapaProcessamento = EtapaProcessamento.BUSCANDO_DISCENTES;
		int anoPeriodoNormal = new Integer(ano + "" + periodo);
		int anoFerias, periodoFerias;
		anoFerias = ano;
		periodoFerias = periodo + 2;
		int anoPeriodoFerias = new Integer(anoFerias + "" + periodoFerias);
		
		discentes = (List<DiscenteGraduacao>) dao.findIdsByCursoParaRecalculo(obj.getId());
		inaptos = new LinkedHashMap<DiscenteGraduacao, String>();
		etapaProcessamento = EtapaProcessamento.CALCULANDO_HISTORICO;
		quantidadeProcessado = 0;
		inicioProcessamento = new Date();
		try {
			for( DiscenteGraduacao dg : discentes){
				prepareMovimento(SigaaListaComando.CALCULAR_INTEGRALIZACOES_DISCENTE );
				MovimentoCadastro movimento = new MovimentoCadastro();
				movimento.setCodMovimento(SigaaListaComando.CALCULAR_INTEGRALIZACOES_DISCENTE);
				movimento.setObjAuxiliar(new Object[] { true, true });
				movimento.setObjMovimentado(dg);
				execute(movimento);
				quantidadeProcessado++;
			}
		} catch (NegocioException e) {
			e.printStackTrace();
			notifyError(e);
			addMensagens(e.getListaMensagens());
			quantidadeProcessado = -1;
			discentes = null;
			setDisable(false);
			etapaProcessamento = EtapaProcessamento.INICIAL;
			redirectMesmaPagina();
		}
		etapaProcessamento = EtapaProcessamento.BUSCANDO_DISCENTES;
		// Se não for curso à distância, não utilizar o pólo
		if (!obj.isADistancia())
			polo.setId(0);
		
		// busca por graduandos que possuam matrículas no semestre informado, e
		// no semestre de férias anterior a ele
		graduandos = new ArrayList<DiscenteGraduacao>();
		Map<DiscenteGraduacao, String> todosGraduandos = dao.findGraduandosParaListaAssinaturaColacao(obj.getId(), polo.getId(), anoPeriodoNormal, anoPeriodoFerias);
		for (DiscenteGraduacao dg : todosGraduandos.keySet()) {
			if (dg.isSelecionado())
				graduandos.add(dg);
			else
				inaptos.put(dg, todosGraduandos.get(dg));
		}
		etapaProcessamento = EtapaProcessamento.CONCLUIDO;
		if (isEmpty(graduandos)) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		}
		discentes = new ArrayList<DiscenteGraduacao>();
		quantidadeProcessado = -1;
		discentes = null;
		setDisable(false);
	}

	/** Redireciona o usuário para o formulário de busca.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/relatorios/lista_concluintes/lista_assinatura_colacao.jsp</li>
	 * </ul>
	 * @return
	 */
	public String formListaAssinatura() {
		if (isEmpty(graduandos)) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return cancelar();
		}
		etapaProcessamento = EtapaProcessamento.INICIAL;
		quantidadeProcessado = -1;
		setDisable(false);
		return forward("/graduacao/relatorios/lista_concluintes/lista_assinatura_colacao.jsp");
	}
	
	/** Listenner que observa a mudança de curso<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/relatorios/lista_concluintes/busca_curso.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws DAOException 
	 */
	public void cursoListener() throws DAOException {
		this.obj = getGenericDAO().findByPrimaryKey(obj.getId(), Curso.class);
		if (this.obj == null ) this.obj = new Curso();
		listaPolo = null;
	}
	
	/** Retorna uma coleção de SelecItem de cursos de graduação.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getCursoCombo() throws DAOException {
		if (listaCurso == null) {
			listaCurso = new ArrayList<SelectItem>();
			CursoDao dao = getDAO(CursoDao.class);
			Collection<Curso> cursos = dao.findByNivel(NivelEnsino.GRADUACAO, true);
			listaCurso = toSelectItems(cursos, "id", "descricaoCompleta");
		}
		return listaCurso;
	}
	
	/**
	 * Retorna uma lista de SelectItem para montar um ComboBox com todos os pólos
	 */
	public Collection<SelectItem> getPoloCombo() throws ArqException {
		if (listaPolo == null) {
			listaPolo = new ArrayList<SelectItem>();
			PoloDao dao = getDAO(PoloDao.class);
			Collection<Polo> polos = dao.findAllPolos(obj);
			listaPolo = toSelectItems(polos, "id", "descricao");
		}
		return listaPolo;
	}

	/**
	 * Inicia a geração da lista de assinaturas para colação de grau.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/menus/diplomas.jsp</li>
	 * <li>/graduacao/menus/relatorios_dae.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public String iniciar() throws SegurancaException {
		checkChangeRole();
		if (!etapaProcessamento.equals(EtapaProcessamento.CONCLUIDO) && !etapaProcessamento.equals(EtapaProcessamento.INICIAL)) {
			addMensagemErro("Havia um processamento em andamento que foi cancelado. Evite usar duas janelas abertas, ou o botão \"Voltar\".");
			addMensagemErro("Tente novamente em " + getEstimativaTempoRestante());
			return null;
		} else 
			initObj();
		return formBusca();
	}
	
	/** Redireciona o usuário para o formulário de busca.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/relatorios/lista_concluintes/lista_assinatura_colacao.jsp</li>
	 * </ul>
	 * @return
	 */
	public String formBusca() {
		return forward("/graduacao/relatorios/lista_concluintes/busca_curso.jsp");
	}

	/** Inicializa os atributos do controller. */
	private void initObj() {
		ano = getCalendarioVigente().getAnoAnterior();
		periodo = getCalendarioVigente().getPeriodoAnterior();
		graduandos = new ArrayList<DiscenteGraduacao>();
		obj = new Curso();
		polo = new Polo();
		discentes = null;
		quantidadeProcessado = -1;
		inicioProcessamento = null;
		listaCurso = null;
		etapaProcessamento = EtapaProcessamento.INICIAL;
	}

	/** Retorna o ano de graduação dos discentes. 
	 * @return
	 */
	public int getAno() {
		return ano;
	}

	/** Seta o ano de graduação dos discentes.
	 * @param ano
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Retorna o período de graduação dos discentes. 
	 * @return
	 */
	public int getPeriodo() {
		return periodo;
	}

	/** Seta o período de graduação dos discentes.
	 * @param periodo
	 */
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	/** Valida os dados: curso, ano, período.</br>
	 * Método não invocado por JSP's
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(br.ufrn.arq.negocio.validacao.ListaMensagens)
	 */
	@Override
	public boolean validacaoDados(ListaMensagens lista) {
		ValidatorUtil.validateRequired(obj, "Curso", lista);
		ValidatorUtil.validateMinValue(ano, 1950, "Ano", lista);
		ValidatorUtil.validateRange(periodo, 1, 2, "Período", lista);
		if (obj.isADistancia())
			ValidatorUtil.validateRequired(polo, "Pólo", lista);
		return lista.isErrorPresent();
	}

	/** Retorna a coleção de discentes graduandos para colação de grau. 
	 * @return
	 */
	public Collection<DiscenteGraduacao> getGraduandos() {
		return graduandos;
	}

	public boolean isDisable() {
		return disable;
	}

	public void setDisable(boolean disable) {
		this.disable = disable;
	}

	public Polo getPolo() {
		return polo;
	}

	public void setPolo(Polo polo) {
		this.polo = polo;
	}

	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.GESTOR_DIPLOMAS_GRADUACAO);
	}
	
	/** Retorna o percentual do processamento. 
	 * @return Percentual do processamento. Ao fim do processamento, retorna o valor 101. 
	 */
	public int getPercentualProcessado() {
		int percentual = -1;
		if (etapaProcessamento.equals(EtapaProcessamento.INICIAL)) {
			percentual = -1;
		} else if (etapaProcessamento.equals(EtapaProcessamento.CONCLUIDO)) {
			percentual = 101;
		} else if (etapaProcessamento.equals(EtapaProcessamento.BUSCANDO_DISCENTES)) {
			percentual = 100;
		} else if (etapaProcessamento.equals(EtapaProcessamento.CALCULANDO_HISTORICO)) {
			if (quantidadeProcessado == 0 || discentes.size() == 0) percentual = 1;
			else percentual = 100 * (quantidadeProcessado + 1) / discentes.size();
		}
		return percentual;
	}
	
	/** Retorna uma estimativa do tempo restante para o fim do processamento. 
	 * @return Estimativa do tempo restante para o fim do processamento.
	 */
	public String getMensagemProgresso() {
		if (etapaProcessamento.equals(EtapaProcessamento.BUSCANDO_DISCENTES)) {
			return "Buscando possíveis concluintes. Por favor, aguarde...";
		} else if (discentes != null && quantidadeProcessado < discentes.size()) {
			if (quantidadeProcessado == 0) return "Estimando o tempo para o término do cálculo de históricos...";
			String estimativaRestante = getEstimativaTempoRestante();

			StringBuilder msg = new StringBuilder("Calculando o histórico ")
			.append(discentes.get(quantidadeProcessado).getMatricula())
			.append(" (")
			.append(quantidadeProcessado + 1)
			.append(" de ")
			.append(discentes.size())
			.append("). Tempo estimado para conclusão: ")
			.append(estimativaRestante);
			return msg.toString();
		} else
			return null;
	}

	/** Retorna uma estimativa de tempo restante até a conclusão do processamento.
	 * @return
	 */
	private String getEstimativaTempoRestante() {
		Date agora = new Date();
		long decorrido = agora.getTime() - inicioProcessamento.getTime();
		long media = decorrido / quantidadeProcessado;
		long previsao = discentes.size() * media + inicioProcessamento.getTime(); 
		long restante = (previsao - agora.getTime()) / 1000;
		int horas = (int) (restante / 60 / 60);
		restante = (restante - horas * 60 * 60);
		int minutos = (int) (restante / 60);
		restante = (restante - minutos * 60);
		int segundos = (int) (restante);
		String estimativaRestante = String.format("%02d:%02d:%02d", horas, minutos, segundos);
		return estimativaRestante;
	}
	
	public boolean isInicial() {
		return etapaProcessamento == EtapaProcessamento.INICIAL;
	}

	public Map<DiscenteGraduacao, String> getInaptos() {
		return inaptos;
	}

	public void setInaptos(Map<DiscenteGraduacao, String> inaptos) {
		this.inaptos = inaptos;
	}
}
