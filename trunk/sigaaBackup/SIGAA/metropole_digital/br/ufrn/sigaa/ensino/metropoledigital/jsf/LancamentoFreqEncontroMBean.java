package br.ufrn.sigaa.ensino.metropoledigital.jsf;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.metropoledigital.dao.AcompanhamentoSemanalDiscenteDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.ConsolidacaoParcialIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.LancamentoFreqEncontroDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TurmaEntradaTecnicoDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.AcompanhamentoSemanalDiscente;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.PeriodoAvaliacao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.RelatorioFrequenciaTurmaIMD;
import br.ufrn.sigaa.ensino.metropoledigital.negocio.MovimentoCriacaoRegistrosAcompanhamento;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;

/**
 * Universidade Federal do Rio Grande do Norte Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * 
 * @author Rafael Barros
 * 
 */

@Scope("request")
@Component("lancamentoFrequenciaIMD")
public class LancamentoFreqEncontroMBean extends SigaaAbstractController<AcompanhamentoSemanalDiscente> {

	/**
	 * Entidade que ir� armazenar o ID da turma de entrada selecionada para se
	 * efetuar a frequ�ncia semanal do IMD
	 **/
	private int idTurmaEntradaSelecionada = 0;

	/**
	 * Entidade que ir� armazenar a turma de entrada selecionada para se efetuar
	 * a frequ�ncia semanal do IMD
	 **/
	private TurmaEntradaTecnico turmaEntradaSelecionada;

	/**
	 * Entidade que ir� armazenar o ID do discente selecionado para se efetuar a
	 * gera��o do relatorio da frequencia
	 **/
	private int idDiscenteSelecionado = 0;

	/**
	 * Entidade que ir� armazenar o objeto do discente selecionado para se
	 * efetuar a gera��o do relatorio da frequencia
	 **/
	private DiscenteTecnico discenteSelecionado;

	/**
	 * Entidade que ir� armazenar a listagem dos discentes do curso t�cnico que
	 * comp�em a turma de entrada selecionada para se efetuar a frequ�ncia
	 **/
	private Collection<DiscenteTecnico> listaDiscentesTurma;

	/**
	 * Entidade que ir� armazenar a listagem dos per�odos que comp�em a turma de
	 * entrada selecionada para se efetuar a frequ�ncia
	 **/
	private Collection<PeriodoAvaliacao> listaPeriodosTurma;

	/**
	 * Entidade que ir� armazenar a listagem dos discentes do curso t�cnico que
	 * comp�em a turma de entrada selecionada para se efetuar a frequ�ncia
	 **/
	private Collection<AcompanhamentoSemanalDiscente> listaFrequencias;

	/**
	 * Entidade que ir� armazenar a listagem dos discentes do curso t�cnico que
	 * comp�em a turma de entrada selecionada para se efetuar a frequ�ncia
	 **/
	private Collection<AcompanhamentoSemanalDiscente> listaGeralAcompanhamentos;

	/**
	 * Tabela respons�vel por armazenar os registros de acompanhamentos dos
	 * discentes em fun��o dos per�odos de avalia��o
	 **/
	private List<List<AcompanhamentoSemanalDiscente>> tabelaAcompanhamento;

	/**
	 * Entidade que ir� armazenar a listagem dos objetos correspondentes ao
	 * relat�rio de frequencia da turma
	 **/
	private Collection<RelatorioFrequenciaTurmaIMD> listaRelatorioFrequencia;

	/** Representa os discentes pertencentes a uma determinada turma de entrada */
	private Collection<SelectItem> discentesCombo = new ArrayList<SelectItem>();

	/**
	 * Vari�vel respons�vel por armazenar qual o tipo de op��o foi selecionada
	 * antes da tela de sele��o da turma
	 **/
	private String tipoSelecao = null;

	/**
	 * Vari�vel que armazenar� a data atual, servir� para o controle da
	 * frequencia para que o tutor n�o possa efetuar a frequencia de datas
	 * futuras
	 **/
	private Date dataAtual = new Date();

	// VARI�VEIS QUE SER�O UTILIZADAS PARA O RELAT�RIO DE FREQU�NCIA POR TURMA
	// CONFIGUR�VEL
	/**
	 * Corresponde a data inicio informada pelo usu�rio para gera��o do
	 * relat�rio de frequ�ncia configur�vel
	 */
	private boolean relatorioFiltrado = false;

	/**
	 * Corresponde a data inicio informada pelo usu�rio para gera��o do
	 * relat�rio de frequ�ncia configur�vel
	 */
	private Date dataInicioInformada = null;

	/**
	 * Corresponde a data fim informada pelo usu�rio para gera��o do relat�rio
	 * de frequ�ncia configur�vel
	 */
	private Date dataFimInformada = null;

	/**
	 * Corresponde a quantidade m�nima de faltas informada pelo usu�rio para
	 * gera��o do relat�rio de frequ�ncia configur�vel
	 */
	private Integer qtdFaltasMinimaInformada = null;

	/**
	 * Corresponde a quantidade m�xima de faltas informada pelo usu�rio para
	 * gera��o do relat�rio de frequ�ncia configur�vel
	 */
	private Integer qtdFaltasMaximaInformada = null;

	/**
	 * Corresponde ao percentual m�nimo de faltas informado pelo usu�rio para
	 * gera��o do relat�rio de frequ�ncia configur�vel
	 */
	private Integer percFaltasMinimaInformada = null;

	/**
	 * Corresponde ao percentual m�ximo de faltas informado pelo usu�rio para
	 * gera��o do relat�rio de frequ�ncia configur�vel
	 */
	private Integer percFaltasMaximaInformada = null;

	public LancamentoFreqEncontroMBean() {
		this.turmaEntradaSelecionada = new TurmaEntradaTecnico();
		this.discenteSelecionado = new DiscenteTecnico();
		this.listaDiscentesTurma = new ArrayList<DiscenteTecnico>();
		this.listaFrequencias = Collections.emptyList();
		this.listaPeriodosTurma = Collections.emptyList();
		this.listaRelatorioFrequencia = new ArrayList<RelatorioFrequenciaTurmaIMD>();
		this.discentesCombo = new ArrayList<SelectItem>();
		this.tipoSelecao = null;
		this.dataAtual = new Date();
	}

	public int getIdTurmaEntradaSelecionada() {
		return idTurmaEntradaSelecionada;
	}

	public void setIdTurmaEntradaSelecionada(int idTurmaEntradaSelecionada) {
		this.idTurmaEntradaSelecionada = idTurmaEntradaSelecionada;
	}

	/**
	 * Fun��o get do atributo turmaEntradaSelecionada que tamb�m efetua o
	 * preenchimento desse atributo caso ele esteja vazio
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * <li>
	 * /sigaa.war/metropole_digital/lancar_frequencia/lista_frequencia_discente
	 * .jsp</li>
	 * <li>
	 * /sigaa.war/metropole_digital/lancar_frequencia/lista_frequencia_turma.jsp
	 * </li>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/lista.jsp</li>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/selecao_discente.jsp</li>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/selecao_turma.jsp</li>
	 * <li>
	 * /sigaa.war/metropole_digital/lancar_frequencia/selecao_filtros_relatorio
	 * .jsp</li>
	 * </ul>
	 * 
	 * @param
	 * @return P�gina referente � opera��o.
	 * @throws
	 */
	public TurmaEntradaTecnico getTurmaEntradaSelecionada() throws DAOException {
		try {
			if (turmaEntradaSelecionada == null) {
				turmaEntradaSelecionada = getGenericDAO().findByPrimaryKey(idTurmaEntradaSelecionada,
						TurmaEntradaTecnico.class);
			}
			return turmaEntradaSelecionada;
		} finally {
			getGenericDAO().close();
		}
	}

	public void setTurmaEntradaSelecionada(TurmaEntradaTecnico turmaEntradaSelecionada) {
		this.turmaEntradaSelecionada = turmaEntradaSelecionada;
	}

	/**
	 * Fun��o get do atributo listaDiscentesTurma que tamb�m efetua o
	 * preenchimento desse atributo caso ele esteja vazio
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * <li>
	 * /sigaa.war/metropole_digital/lancar_frequencia/lista_frequencia_discente
	 * .jsp</li>
	 * <li>
	 * /sigaa.war/metropole_digital/lancar_frequencia/lista_frequencia_turma.jsp
	 * </li>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/lista.jsp</li>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/selecao_discente.jsp</li>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/selecao_turma.jsp</li>
	 * <li>
	 * /sigaa.war/metropole_digital/lancar_frequencia/selecao_filtros_relatorio
	 * .jsp</li>
	 * </ul>
	 * 
	 * @param
	 * @return P�gina referente � opera��o.
	 * @throws
	 */
	public Collection<DiscenteTecnico> getListaDiscentesTurma() throws DAOException {
		/*
		 * if (this.listaDiscentesTurma.isEmpty()) {
		 * findDiscentesPeriodosTurma(); }
		 */
		return listaDiscentesTurma;
	}

	public void setListaDiscentesTurma(Collection<DiscenteTecnico> listaDiscentesTurma) {
		this.listaDiscentesTurma = listaDiscentesTurma;
	}

	public Collection<AcompanhamentoSemanalDiscente> getListaFrequencias() {
		return listaFrequencias;
	}

	public void setListaFrequencias(Collection<AcompanhamentoSemanalDiscente> listaFrequencias) {
		this.listaFrequencias = listaFrequencias;
	}

	/**
	 * Fun��o get do atributo listaPeriodosTurma que tamb�m efetua o
	 * preenchimento desse atributo caso ele esteja vazio
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * <li>
	 * /sigaa.war/metropole_digital/lancar_frequencia/lista_frequencia_discente
	 * .jsp</li>
	 * <li>
	 * /sigaa.war/metropole_digital/lancar_frequencia/lista_frequencia_turma.jsp
	 * </li>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/lista.jsp</li>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/selecao_discente.jsp</li>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/selecao_turma.jsp</li>
	 * <li>
	 * /sigaa.war/metropole_digital/lancar_frequencia/selecao_filtros_relatorio
	 * .jsp</li>
	 * </ul>
	 * 
	 * @param
	 * @return P�gina referente � opera��o.
	 * @throws
	 */
	public Collection<PeriodoAvaliacao> getListaPeriodosTurma() throws DAOException {
		/*
		 * if (this.listaPeriodosTurma.isEmpty()) {
		 * findDiscentesPeriodosTurma(); }
		 */
		return listaPeriodosTurma;
	}

	public void setListaPeriodosTurma(Collection<PeriodoAvaliacao> listaPeriodosTurma) {
		this.listaPeriodosTurma = listaPeriodosTurma;
	}

	public List<List<AcompanhamentoSemanalDiscente>> getTabelaAcompanhamento() {
		return tabelaAcompanhamento;
	}

	public void setTabelaAcompanhamento(List<List<AcompanhamentoSemanalDiscente>> tabelaAcompanhamento) {
		this.tabelaAcompanhamento = tabelaAcompanhamento;
	}

	public boolean isRelatorioFiltrado() {
		return relatorioFiltrado;
	}

	public void setRelatorioFiltrado(boolean relatorioFiltrado) {
		this.relatorioFiltrado = relatorioFiltrado;
	}

	/**
	 * Fun��o get do atributo listaRelatorioFrequencia que tamb�m efetua o
	 * preenchimento desse atributo caso ele esteja vazio
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * <li>
	 * /sigaa.war/metropole_digital/lancar_frequencia/lista_frequencia_discente
	 * .jsp</li>
	 * <li>
	 * /sigaa.war/metropole_digital/lancar_frequencia/lista_frequencia_turma.jsp
	 * </li>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/lista.jsp</li>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/selecao_discente.jsp</li>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/selecao_turma.jsp</li>
	 * <li>
	 * /sigaa.war/metropole_digital/lancar_frequencia/selecao_filtros_relatorio
	 * .jsp</li>
	 * </ul>
	 * 
	 * @param
	 * @return P�gina referente � opera��o.
	 * @throws
	 */
	public Collection<RelatorioFrequenciaTurmaIMD> getListaRelatorioFrequencia() throws DAOException {
		try {
			
			turmaEntradaSelecionada = getGenericDAO().findByPrimaryKey(idTurmaEntradaSelecionada,
					TurmaEntradaTecnico.class);

			if (listaRelatorioFrequencia.isEmpty()) {
				if (!relatorioFiltrado) {
					gerarRelatorioFrequenciaTurma();
				} else {
					String retorno = gerarRelatorioFrequenciaTurmaFiltrado();
				}

			}

			if (idDiscenteSelecionado > 0) {
				gerarRelatorioFrequenciaDiscente();
			}

			return listaRelatorioFrequencia;
		} finally {
			getGenericDAO().close();
		}
	}

	public void setListaRelatorioFrequencia(Collection<RelatorioFrequenciaTurmaIMD> listaRelatorioFrequencia) {
		this.listaRelatorioFrequencia = listaRelatorioFrequencia;
	}

	public String getTipoSelecao() {
		return tipoSelecao;
	}

	public void setTipoSelecao(String tipoSelecao) {
		this.tipoSelecao = tipoSelecao;
	}

	public Collection<SelectItem> getDiscentesCombo() {
		return discentesCombo;
	}

	public void setDiscentesCombo(Collection<SelectItem> discentesCombo) {
		this.discentesCombo = discentesCombo;
	}

	public int getIdDiscenteSelecionado() {
		return idDiscenteSelecionado;
	}

	public void setIdDiscenteSelecionado(int idDiscenteSelecionado) {
		this.idDiscenteSelecionado = idDiscenteSelecionado;
	}

	public DiscenteTecnico getDiscenteSelecionado() {
		return discenteSelecionado;
	}

	public void setDiscenteSelecionado(DiscenteTecnico discenteSelecionado) {
		this.discenteSelecionado = discenteSelecionado;
	}

	public Collection<AcompanhamentoSemanalDiscente> getListaGeralAcompanhamentos() {
		return listaGeralAcompanhamentos;
	}

	public void setListaGeralAcompanhamentos(Collection<AcompanhamentoSemanalDiscente> listaGeralAcompanhamentos) {
		this.listaGeralAcompanhamentos = listaGeralAcompanhamentos;
	}

	public Date getDataInicioInformada() {
		return dataInicioInformada;
	}

	public void setDataInicioInformada(Date dataInicioInformada) {
		this.dataInicioInformada = dataInicioInformada;
	}

	public Date getDataFimInformada() {
		return dataFimInformada;
	}

	public void setDataFimInformada(Date dataFimInformada) {
		this.dataFimInformada = dataFimInformada;
	}

	public Integer getQtdFaltasMinimaInformada() {
		return qtdFaltasMinimaInformada;
	}

	public void setQtdFaltasMinimaInformada(Integer qtdFaltasMinimaInformada) {
		this.qtdFaltasMinimaInformada = qtdFaltasMinimaInformada;
	}

	public Integer getQtdFaltasMaximaInformada() {
		return qtdFaltasMaximaInformada;
	}

	public void setQtdFaltasMaximaInformada(Integer qtdFaltasMaximaInformada) {
		this.qtdFaltasMaximaInformada = qtdFaltasMaximaInformada;
	}

	public Integer getPercFaltasMinimaInformada() {
		return percFaltasMinimaInformada;
	}

	public void setPercFaltasMinimaInformada(Integer percFaltasMinimaInformada) {
		this.percFaltasMinimaInformada = percFaltasMinimaInformada;
	}

	public Integer getPercFaltasMaximaInformada() {
		return percFaltasMaximaInformada;
	}

	public void setPercFaltasMaximaInformada(Integer percFaltasMaximaInformada) {
		this.percFaltasMaximaInformada = percFaltasMaximaInformada;
	}

	public Date getDataAtual() {
		return dataAtual;
	}

	public void setDataAtual(Date dataAtual) {
		this.dataAtual = dataAtual;
	}

	/**
	 * Fun��o respons�vel por redirecionar para a p�gina inicial do Portal do
	 * tutor do IMD
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * <li>
	 * /sigaa.war/metropole_digital/lancar_frequencia/lista_frequencia_discente
	 * .jsp</li>
	 * <li>
	 * /sigaa.war/metropole_digital/lancar_frequencia/lista_frequencia_turma.jsp
	 * </li>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/lista.jsp</li>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/selecao_discente.jsp</li>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/selecao_turma.jsp</li>
	 * </ul>
	 * 
	 * @param
	 * @return P�gina referente � opera��o.
	 */
	public String voltarTelaPortal() {
		return forward("/metropole_digital/principal.jsp");
	}

	/**
	 * Fun��o chamada a partir da op��o Lan�ar Frequencia, respons�vel por
	 * redirecionar para a p�gina de sele��o de turma
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * <li>/sigaa.war/metropole_digital/principal.jsp</li>
	 * </ul>
	 * 
	 * @param
	 * @return P�gina referente � opera��o.
	 */
	public String redirecionarSelecaoTurmaLancarFreq() {
		tipoSelecao = null;
		return forward("/metropole_digital/lancar_frequencia/selecao_turma.jsp");
	}

	/**
	 * Fun��o chamada a partir da op��o Relatorio de Frequencia, respons�vel por
	 * redirecionar para a p�gina de sele��o de turma
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * <li>/sigaa.war/metropole_digital/principal.jsp</li>
	 * </ul>
	 * 
	 * @param
	 * @return P�gina referente � opera��o.
	 */
	public String redirecionarSelecaoTurmaRelatorioFreq() {
		tipoSelecao = "RELATORIO";
		return forward("/metropole_digital/lancar_frequencia/selecao_turma.jsp");
	}

	/**
	 * Fun��o que carrega os dados e redireciona para o relatorio de frequencia
	 * da turma
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * <li>/sigaa.war/metropole_digital/principal.jsp</li>
	 * </ul>
	 * 
	 * @param
	 * @return P�gina referente � opera��o.
	 */
	public String redirecionarRelatorioFrequencia() throws DAOException {
		int id = 0;

		if (getParameter("id") != null) {
			id = getParameterInt("id");
		}

		if (id > 0) {
			idTurmaEntradaSelecionada = id;
		}

		listaRelatorioFrequencia = new ArrayList<RelatorioFrequenciaTurmaIMD>();
		relatorioFiltrado = false;

		turmaEntradaSelecionada = getGenericDAO()
				.findByPrimaryKey(idTurmaEntradaSelecionada, TurmaEntradaTecnico.class);

		return forward("/metropole_digital/lancar_frequencia/lista_frequencia_turma.jsp");
	}

	/**
	 * Fun��o que carrega os dados e redireciona para o relatorio de frequencia
	 * da turma com filtros
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * <li>/sigaa.war/metropole_digital/principal.jsp</li>
	 * </ul>
	 * 
	 * @param
	 * @return P�gina referente � opera��o.
	 */
	public String redirecionarRelatorioFrequenciaFiltrado() throws DAOException {
		int id = 0;

		if (getParameter("id") != null) {
			id = getParameterInt("id");
		}

		if (id > 0) {
			idTurmaEntradaSelecionada = id;
		}

		relatorioFiltrado = true;

		turmaEntradaSelecionada = getGenericDAO()
				.findByPrimaryKey(idTurmaEntradaSelecionada, TurmaEntradaTecnico.class);

		return gerarRelatorioFrequenciaTurmaFiltrado();

	}

	/**
	 * Fun��o que carrega os dados e redireciona para o relatorio de frequencia
	 * da turma
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * <li>/sigaa.war/metropole_digital/principal.jsp</li>
	 * </ul>
	 * 
	 * @param
	 * @return P�gina referente � opera��o.
	 */
	public String redirecionarSelecaoFiltrosRelatorioFrequencia() throws DAOException {
		int id = getParameterInt("id");

		if (id > 0) {
			idTurmaEntradaSelecionada = id;
		}

		turmaEntradaSelecionada = getGenericDAO()
				.findByPrimaryKey(idTurmaEntradaSelecionada, TurmaEntradaTecnico.class);

		return forward("/metropole_digital/lancar_frequencia/selecao_filtros_relatorio.jsp");
	}

	/**
	 * Fun��o que carrega os dados e redireciona para a sele��o do discente para
	 * o relatorio de frequencia
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * <li>/sigaa.war/metropole_digital/principal.jsp</li>
	 * </ul>
	 * 
	 * @param
	 * @return P�gina referente � opera��o.
	 */
	public String redicionarSelecaoDiscente() throws DAOException {
		try {
			int id = getParameterInt("id");

			if (id > 0) {
				idTurmaEntradaSelecionada = id;
			}

			turmaEntradaSelecionada = getGenericDAO().findByPrimaryKey(idTurmaEntradaSelecionada,
					TurmaEntradaTecnico.class);
			listaDiscentesTurma = getDAO(TurmaEntradaTecnicoDao.class).findDiscentesByTurmaEntrada(
					idTurmaEntradaSelecionada);
			listaPeriodosTurma = getDAO(TurmaEntradaTecnicoDao.class).findPeriodosByTurmaEntrada(
					idTurmaEntradaSelecionada);

			return forward("/metropole_digital/lancar_frequencia/selecao_discente.jsp");
		} finally {
			getDAO(TurmaEntradaTecnicoDao.class).close();
		}
	}

	/**
	 * Fun��o que preenche a listagem dos discentes, per�odos de avalia��o e os
	 * registros de frequencia da Turma de Entrada selecionada. A fun��o tamb�m
	 * verifica se os registros da frequencia da turma j� foram criados, caso
	 * n�o tenham sido criados, a fun��o chama uma nova fun��o que ir� efetuar o
	 * procedimento de criar os registros da frequencia da turma
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/form.jsp</li>
	 * </ul>
	 * 
	 * @param
	 * @return P�gina referente � opera��o.
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public void constroiDiscentesPeriodosTurma() throws NegocioException, ArqException {

		try {
			listaDiscentesTurma = getDAO(TurmaEntradaTecnicoDao.class).findDiscentesByTurmaEntrada(
					idTurmaEntradaSelecionada);
			listaPeriodosTurma = getDAO(TurmaEntradaTecnicoDao.class).findPeriodosByTurmaEntrada(
					idTurmaEntradaSelecionada);

			MovimentoCriacaoRegistrosAcompanhamento movAcomp = new MovimentoCriacaoRegistrosAcompanhamento();
			movAcomp.setListaDiscentesTurma(listaDiscentesTurma);
			movAcomp.setListaGeralAcompanhamentos(listaGeralAcompanhamentos);
			movAcomp.setListaPeriodosTurma(listaPeriodosTurma);
			movAcomp.setTurmaSelecionada(turmaEntradaSelecionada);
			movAcomp.setCodMovimento(SigaaListaComando.CRIACAO_REGISTROS_ACOMPANHAMENTO_DISCENTE);

			executeWithoutClosingSession(movAcomp);

			// verificarCriaRegistrosFrequencia();

			preencheTabelaAcompanhamento();

		} finally {
			getDAO(TurmaEntradaTecnicoDao.class).close();
		}
	}

	/**
	 * Fun��o que efetua o redirecionamento para a p�gina da frequencia
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * <li>/sigaa.war/metropole_digital/principal.jsp</li>
	 * </ul>
	 * 
	 * @param
	 * @return P�gina referente � opera��o.
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String redirecionarFrequencia() throws NegocioException, ArqException {
		int id = getParameterInt("id");
		
		if (getDAO(ConsolidacaoParcialIMDDao.class).isConsolidadoParcialmente(id)) {			
			addMessage("O lan�amento da frequencia semanal n�o pode ser realizado, pois a turma j� foi consolidada.", TipoMensagemUFRN.ERROR);
			return redirect("/metropole_digital/principal.jsf");
		}

		if (id > 0) {
			idTurmaEntradaSelecionada = id;
		}

		turmaEntradaSelecionada = getGenericDAO()
				.findByPrimaryKey(idTurmaEntradaSelecionada, TurmaEntradaTecnico.class);

		prepareMovimento(SigaaListaComando.CRIACAO_REGISTROS_ACOMPANHAMENTO_DISCENTE);
		
		constroiDiscentesPeriodosTurma();

		return forward("/metropole_digital/lancar_frequencia/form.jsp");
	}

	/**
	 * Fun��o que cria os registros da frequencia da turma, essa fun��o s� �
	 * chamada se os registros supracitados n�o tenham sido criados
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/form.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 * @return
	 **/
	public void criarRegistrosFrequencia(int idTurmaEntrada) throws DAOException {

		AcompanhamentoSemanalDiscenteDao acompDao = new AcompanhamentoSemanalDiscenteDao();

		try {
			for (DiscenteTecnico discente : listaDiscentesTurma) {
				for (PeriodoAvaliacao periodo : listaPeriodosTurma) {
					AcompanhamentoSemanalDiscente acomp = new AcompanhamentoSemanalDiscente();
					acomp.setPeriodoAvaliacao(periodo);
					acomp.setDiscente(discente);
					acompDao.createOrUpdate(acomp);
				}
			}
		} finally {
			acompDao.close();
		}
	}

	/**
	 * Fun��o que cria os registros da frequencia da turma por periodo
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/form.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 * @return
	 **/
	public void criarRegistrosFrequenciaPorPeriodo(PeriodoAvaliacao periodo) throws DAOException {

		AcompanhamentoSemanalDiscenteDao acompDao = new AcompanhamentoSemanalDiscenteDao();

		try {
			for (DiscenteTecnico discente : listaDiscentesTurma) {
				AcompanhamentoSemanalDiscente acomp = new AcompanhamentoSemanalDiscente();
				acomp.setPeriodoAvaliacao(periodo);
				acomp.setDiscente(discente);
				acompDao.createOrUpdate(acomp);
			}
		} finally {
			acompDao.close();
		}
	}

	/**
	 * Fun��o que efetua a verifica��o se os registros da frequencia da turma j�
	 * foram criados.
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/form.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 * @return: FALSE: Se os registros da frequencia nao tiverem sido criados
	 *          para um determinado periodo. TRUE: Se os registros ja tiverem
	 *          sido criados.
	 **/
	public boolean buscaRegistrosFrequencia() throws DAOException {
		LancamentoFreqEncontroDao lancamentoFreqDao = new LancamentoFreqEncontroDao();
		try {

			for (PeriodoAvaliacao periodo : listaPeriodosTurma) {
				if (!lancamentoFreqDao.existeRegistrosFrequencia(periodo.getId())) {
					return false;
				} else {
					return true;
				}
			}
			return false;
		} finally {
			lancamentoFreqDao.close();
		}
	}

	/**
	 * Fun��o que efetua a verifica��o se os registros da frequencia da turma j�
	 * foram criados, se n�o, efetua a cria��o dos mesmos.
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/form.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 * @return
	 ** 
	 *         public void verificarCriaRegistrosFrequencia() throws
	 *         DAOException { AcompanhamentoSemanalDiscenteDao acompDao = new
	 *         AcompanhamentoSemanalDiscenteDao(); try {
	 * 
	 *         listaGeralAcompanhamentos = acompDao
	 *         .findAcompanhamentosByTurmaEntradaProjetado
	 *         (turmaEntradaSelecionada .getId()); if
	 *         (!(listaDiscentesTurma.size() * listaPeriodosTurma.size() ==
	 *         listaGeralAcompanhamentos .size())) {
	 * 
	 *         boolean existeAcomp = false; for (DiscenteTecnico discente :
	 *         listaDiscentesTurma) {
	 * 
	 *         for (AcompanhamentoSemanalDiscente acomp :
	 *         listaGeralAcompanhamentos) { if (acomp.getDiscente().getId() ==
	 *         discente.getId()) { existeAcomp = true; break; } }
	 * 
	 *         if (!existeAcomp) {
	 *         criarRegistrosFrequenciaPorDiscente(discente); } existeAcomp =
	 *         false; } }
	 * 
	 *         } finally { acompDao.close(); } }
	 */

	/**
	 * Fun��o que efetua o preenchimento dos registros da tabela de
	 * acompanhamentos
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/form.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 * @return
	 **/
	public void preencheTabelaAcompanhamento() throws DAOException {
		AcompanhamentoSemanalDiscenteDao acompDao = new AcompanhamentoSemanalDiscenteDao();
		try {

			tabelaAcompanhamento = new ArrayList<List<AcompanhamentoSemanalDiscente>>();
			listaGeralAcompanhamentos = (ArrayList) acompDao
					.findAcompanhamentosByTurmaEntradaProjetado(turmaEntradaSelecionada.getId());

			int contadorLista = 0;
			List<AcompanhamentoSemanalDiscente> listaAuxiliarAcompanhamentos = new ArrayList<AcompanhamentoSemanalDiscente>();

			for (AcompanhamentoSemanalDiscente acomp : listaGeralAcompanhamentos) {
				if (contadorLista == listaPeriodosTurma.size()) {
					tabelaAcompanhamento.add(listaAuxiliarAcompanhamentos);
					listaAuxiliarAcompanhamentos = new ArrayList<AcompanhamentoSemanalDiscente>();
					contadorLista = 0;
				}
				listaAuxiliarAcompanhamentos.add(acomp);
				contadorLista++;
			}

			if (!listaAuxiliarAcompanhamentos.isEmpty()) {
				tabelaAcompanhamento.add(listaAuxiliarAcompanhamentos);
			}

		} finally {
			acompDao.close();
		}
	}

	/**
	 * Fun��o que efetua o salvamento dos dados
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/form.jsp</li>
	 * </ul>
	 * 
	 * @param
	 * @return P�gina referente � opera��o.
	 */
	public String salvar() throws DAOException {
		AcompanhamentoSemanalDiscenteDao acompDao = new AcompanhamentoSemanalDiscenteDao();
		try {
			if (validarFrequencia()) {

				for (List<AcompanhamentoSemanalDiscente> linha : tabelaAcompanhamento) {

					for (AcompanhamentoSemanalDiscente acomp : linha) {
						acompDao.createOrUpdate(acomp);
					}
				}
				// addMessage("Dados armazenados com sucesso.",TipoMensagemUFRN.INFORMATION);
				listaRelatorioFrequencia = new ArrayList<RelatorioFrequenciaTurmaIMD>();
				gerarRelatorioFrequenciaTurma();
				return forward("/metropole_digital/lancar_frequencia/lista_frequencia_turma.jsp");

			} else {
				addMessage(
						"Os registros referentes as frequ�ncias devem ser VAZIOS, ZERO (Falta), 0.5 (Meia Falta) e 1 (Presente).",
						TipoMensagemUFRN.ERROR);
				return forward("/metropole_digital/lancar_frequencia/form.jsp");
			}
		} finally {
			acompDao.close();
		}
	}

	/**
	 * Fun��o respons�vel por validar se todas as frequencias informadas se
	 * enquadram nos valores VAZIO, ZERO, 1 ou 0.5
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/form.jsp</li>
	 * </ul>
	 * 
	 * 
	 * @return: FALSE = Se a frequ�ncia informada for inv�lida; TRUE = Se a
	 *          frequ�ncia informada for v�lida.
	 **/
	public boolean validarFrequencia() {
		for (List<AcompanhamentoSemanalDiscente> linha : tabelaAcompanhamento) {
			for (AcompanhamentoSemanalDiscente acomp : linha) {
				if (acomp.getFrequencia() != null && acomp.getFrequencia() != 0 && acomp.getFrequencia() != 1
						&& acomp.getFrequencia() != 0.5) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Fun��o respons�vel por salvar os dados da turma selecionada na tela
	 * intermedi�ria de sele��o de turma
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * <li>/sigaa.war/metropole_digital/principal.jsp</li>
	 * </ul>
	 * 
	 * @return P�gina referente � opera��o.
	 * @throws ArqException
	 * @throws NegocioException
	 **/
	public String salvarTurma() throws NegocioException, ArqException {
		try {
			if (idTurmaEntradaSelecionada <= 0) {
				addMessage("A turma deve ser informada.", TipoMensagemUFRN.ERROR);
				return forward("/metropole_digital/lancar_frequencia/selecao_turma.jsp");
			} else {
				turmaEntradaSelecionada = getGenericDAO().findByPrimaryKey(idTurmaEntradaSelecionada,
						TurmaEntradaTecnico.class);
				if (tipoSelecao == "") {
					constroiDiscentesPeriodosTurma();
					return forward("/metropole_digital/lancar_frequencia/form.jsp");
				} else if (tipoSelecao == "RELATORIO") {
					return forward("/metropole_digital/lancar_frequencia/lista_frequencia_turma.jsp");
				}
			}
			return forward("/metropole_digital/lancar_frequencia/selecao_turma.jsp");

		} finally {
			getGenericDAO().close();
		}

	}

	/**
	 * Fun��o respons�vel por salvar os dados do discente selecionado na tela
	 * intermedi�ria de sele��o do discente
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/form.jsp</li>
	 * </ul>
	 * 
	 * 
	 * @return P�gina referente � opera��o.
	 **/
	public String salvarDiscente() throws DAOException {
		try {
			idDiscenteSelecionado = getParameterInt("id");
			if (idDiscenteSelecionado <= 0) {

				idDiscenteSelecionado = getParameterInt("id");

				if (idDiscenteSelecionado <= 0) {
					addMessage("O discente deve ser informado.", TipoMensagemUFRN.ERROR);
					return forward("/metropole_digital/lancar_frequencia/selecao_discente.jsp");
				} else {
					discenteSelecionado = getGenericDAO()
							.findByPrimaryKey(idDiscenteSelecionado, DiscenteTecnico.class);

					turmaEntradaSelecionada = getGenericDAO().findByPrimaryKey(idTurmaEntradaSelecionada,
							TurmaEntradaTecnico.class);

					if (listaRelatorioFrequencia.isEmpty()) {
						gerarRelatorioFrequenciaTurma();
					}

					if (idDiscenteSelecionado > 0) {
						gerarRelatorioFrequenciaDiscente();
					}

					return forward("/metropole_digital/lancar_frequencia/lista_frequencia_discente.jsp");
				}
			} else {
				discenteSelecionado = getGenericDAO().findByPrimaryKey(idDiscenteSelecionado, DiscenteTecnico.class);

				turmaEntradaSelecionada = getGenericDAO().findByPrimaryKey(idTurmaEntradaSelecionada,
						TurmaEntradaTecnico.class);

				if (listaRelatorioFrequencia.isEmpty()) {
					gerarRelatorioFrequenciaTurma();
				}

				if (idDiscenteSelecionado > 0) {
					gerarRelatorioFrequenciaDiscente();
				}

				return forward("/metropole_digital/lancar_frequencia/lista_frequencia_discente.jsp");
			}

		} finally {
			getGenericDAO().close();
		}

	}

	/**
	 * Fun��o respons�vel por salvar os dados do discente selecionado na tela
	 * intermedi�ria de sele��o do discente
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/form.jsp</li>
	 * </ul>
	 * 
	 * 
	 * @return P�gina referente � opera��o.
	 **/
	public String frequenciaPorDiscente() throws DAOException {

		discenteSelecionado = (DiscenteTecnico) getUsuarioLogado().getDiscenteAtivo();
		idDiscenteSelecionado = discenteSelecionado.getId();
		turmaEntradaSelecionada = discenteSelecionado.getTurmaEntradaTecnico();
		idTurmaEntradaSelecionada = turmaEntradaSelecionada.getId();

		return forward("/metropole_digital/lancar_frequencia/lista_frequencia_discente.jsp");

	}

	/**
	 * Fun��o que efetua a gera��o dos valores do relatorio de frequencia da
	 * turma
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/form.jsp</li>
	 * </ul>
	 * 
	 * 
	 * @throws DAOException
	 * @return
	 **/
	public void gerarRelatorioFrequenciaTurma() throws DAOException {
		AcompanhamentoSemanalDiscenteDao acompDao = new AcompanhamentoSemanalDiscenteDao();
		try {

			if (this.listaPeriodosTurma.isEmpty() || this.listaDiscentesTurma.isEmpty()) {
				listaDiscentesTurma = getDAO(TurmaEntradaTecnicoDao.class).findDiscentesByTurmaEntrada(
						idTurmaEntradaSelecionada);
				listaPeriodosTurma = getDAO(TurmaEntradaTecnicoDao.class).findPeriodosByTurmaEntrada(
						idTurmaEntradaSelecionada);
			}

			listaGeralAcompanhamentos = acompDao
					.findAcompanhamentosByTurmaEntradaProjetadoOrdenado(turmaEntradaSelecionada.getId());

			DiscenteTecnico discente = new DiscenteTecnico();
			int contador = 0;
			int idDiscenteAuxiliar = 0;
			Double qtdFaltas = (double) 0;
			Double chExecutada = (double) 0;
			Double chPresenca = (double) 0;
			Double chFaltas = (double) 0;
			Double chTotal = (double) 0;
			Double percentualFaltas = (double) 0;
			DecimalFormat df = new DecimalFormat("0.00");
			String percentualFaltasTexto = "0";

			for (AcompanhamentoSemanalDiscente acomp : listaGeralAcompanhamentos) {

				if (contador == 0) {
					discente = acomp.getDiscente();
				}

				if (idDiscenteAuxiliar != acomp.getDiscente().getId()) {
					if (contador > 0) {
						chFaltas = chExecutada - chPresenca;
						if (chExecutada > 0) {
							percentualFaltas = (double) ((chFaltas * 100) / chExecutada);

							percentualFaltasTexto = df.format((double) ((chFaltas * 100) / chExecutada));
						}

						RelatorioFrequenciaTurmaIMD relatorio = new RelatorioFrequenciaTurmaIMD();
						relatorio.setDiscente(discente);
						relatorio.setChExecutada(chExecutada);
						relatorio.setChFaltas(chFaltas);
						relatorio.setChTotal(chTotal);
						relatorio.setPercentualFaltas(percentualFaltas);
						relatorio.setPercentualFaltasTexto(percentualFaltasTexto);
						relatorio.setQtdFaltas(qtdFaltas);

						listaRelatorioFrequencia.add(relatorio);

						chExecutada = (double) 0;
						chPresenca = (double) 0;
						chFaltas = (double) 0;
						percentualFaltas = (double) 0;
						chTotal = (double) 0;
						percentualFaltasTexto = "0";
						qtdFaltas = (double) 0;

					}

					idDiscenteAuxiliar = acomp.getDiscente().getId();
					discente = acomp.getDiscente();
				}

				if (acomp.getFrequencia() != null) {
					chExecutada += (double) (acomp.getPeriodoAvaliacao().getChTotalPeriodo());
					chPresenca += (double) ((acomp.getPeriodoAvaliacao().getChTotalPeriodo() * acomp.getFrequencia()));
					if (acomp.getFrequencia() == 0) {
						qtdFaltas++;
					} else if (acomp.getFrequencia() == 0.5) {
						qtdFaltas += 0.5;
					}
				}

				chTotal += (double) (acomp.getPeriodoAvaliacao().getChTotalPeriodo());
				contador++;

			}

			if (contador > 0) {
				chFaltas = chExecutada - chPresenca;
				if (chExecutada > 0) {
					percentualFaltas = (double) ((chFaltas * 100) / chExecutada);

					percentualFaltasTexto = df.format((double) ((chFaltas * 100) / chExecutada));
				}

				RelatorioFrequenciaTurmaIMD relatorio = new RelatorioFrequenciaTurmaIMD();
				relatorio.setDiscente(discente);
				relatorio.setChExecutada(chExecutada);
				relatorio.setChFaltas(chFaltas);
				relatorio.setChTotal(chTotal);
				relatorio.setPercentualFaltas(percentualFaltas);
				relatorio.setPercentualFaltasTexto(percentualFaltasTexto);
				relatorio.setQtdFaltas(qtdFaltas);

				listaRelatorioFrequencia.add(relatorio);

				chExecutada = (double) 0;
				chPresenca = (double) 0;
				chFaltas = (double) 0;
				percentualFaltas = (double) 0;
				chTotal = (double) 0;
				percentualFaltasTexto = "0";
				qtdFaltas = (double) 0;
			}

		} finally {
			acompDao.close();
		}
	}

	/**
	 * Fun��o que efetua a gera��o dos valores do relatorio de frequencia da
	 * turma com os filtros
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/form.jsp</li>
	 * </ul>
	 * 
	 * 
	 * @throws DAOException
	 * @return
	 **/
	public String gerarRelatorioFrequenciaTurmaFiltrado() throws DAOException {
		AcompanhamentoSemanalDiscenteDao acompDao = new AcompanhamentoSemanalDiscenteDao();
		try {

			boolean retornouResultadosFiltros = false;
			listaRelatorioFrequencia = new ArrayList<RelatorioFrequenciaTurmaIMD>();

			if (this.listaPeriodosTurma.isEmpty() || this.listaDiscentesTurma.isEmpty()) {
				listaDiscentesTurma = getDAO(TurmaEntradaTecnicoDao.class).findDiscentesByTurmaEntrada(
						idTurmaEntradaSelecionada);
				listaPeriodosTurma = getDAO(TurmaEntradaTecnicoDao.class).findPeriodosByTurmaEntrada(
						idTurmaEntradaSelecionada);
			}
			listaGeralAcompanhamentos = new ArrayList<AcompanhamentoSemanalDiscente>();
			listaGeralAcompanhamentos = acompDao
					.findAcompanhamentosByTurmaEntradaProjetadoOrdenado(turmaEntradaSelecionada.getId());

			// VERIFICA��O SE O RELAT�RIO � CONFIGUR�VEL E SE UMA DAS DATAS FOI
			// INFORMADA
			if (dataInicioInformada != null || dataFimInformada != null) {

				if (dataInicioInformada != null && dataFimInformada != null) {
					if (!dataFimInformada.before(dataInicioInformada) && !dataFimInformada.after(dataInicioInformada)) {
						addMessage("A data in�cio � igual que a data fim.", TipoMensagemUFRN.ERROR);
						return forward("/metropole_digital/lancar_frequencia/selecao_filtros_relatorio.jsp");
					} else if (dataFimInformada.before(dataInicioInformada)) {
						addMessage("A data in�cio � maior que a data fim.", TipoMensagemUFRN.ERROR);
						return forward("/metropole_digital/lancar_frequencia/selecao_filtros_relatorio.jsp");
					}
				}

				List<AcompanhamentoSemanalDiscente> listaAcompFiltrada = new ArrayList<AcompanhamentoSemanalDiscente>();
				for (AcompanhamentoSemanalDiscente ac : listaGeralAcompanhamentos) {

					if (dataInicioInformada != null && dataFimInformada != null) {
						if (((!ac.getPeriodoAvaliacao().getDataInicio().before(dataInicioInformada) && !ac
								.getPeriodoAvaliacao().getDataInicio().after(dataInicioInformada)) || ac
								.getPeriodoAvaliacao().getDataInicio().after(dataInicioInformada))
								&& (((!ac.getPeriodoAvaliacao().getDatafim().before(dataFimInformada) && !ac
										.getPeriodoAvaliacao().getDatafim().after(dataFimInformada)) || ac
										.getPeriodoAvaliacao().getDatafim().before(dataFimInformada)))) {
							listaAcompFiltrada.add(ac);
							if (qtdFaltasMinimaInformada == null && qtdFaltasMaximaInformada == null
									&& percFaltasMaximaInformada == null && percFaltasMinimaInformada == null) {
								retornouResultadosFiltros = true;
							}
						}
					} else if (dataInicioInformada != null) {
						if (((!ac.getPeriodoAvaliacao().getDataInicio().before(dataInicioInformada) && !ac
								.getPeriodoAvaliacao().getDataInicio().after(dataInicioInformada)) || ac
								.getPeriodoAvaliacao().getDataInicio().after(dataInicioInformada))) {
							listaAcompFiltrada.add(ac);
							if (qtdFaltasMinimaInformada == null && qtdFaltasMaximaInformada == null
									&& percFaltasMaximaInformada == null && percFaltasMinimaInformada == null) {
								retornouResultadosFiltros = true;
							}
						}
					} else if (dataFimInformada != null) {
						if (((!ac.getPeriodoAvaliacao().getDatafim().before(dataFimInformada) && !ac
								.getPeriodoAvaliacao().getDatafim().after(dataFimInformada)) || ac
								.getPeriodoAvaliacao().getDatafim().before(dataFimInformada))) {
							listaAcompFiltrada.add(ac);
							if (qtdFaltasMinimaInformada == null && qtdFaltasMaximaInformada == null
									&& percFaltasMaximaInformada == null && percFaltasMinimaInformada == null) {
								retornouResultadosFiltros = true;
							}
						}
					}
				}
				listaGeralAcompanhamentos = listaAcompFiltrada;

				DiscenteTecnico discente = new DiscenteTecnico();
				int contador = 0;
				int idDiscenteAuxiliar = 0;
				Double qtdFaltas = (double) 0;
				Double chExecutada = (double) 0;
				Double chPresenca = (double) 0;
				Double chFaltas = (double) 0;
				Double chTotal = (double) 0;
				Double percentualFaltas = (double) 0;
				DecimalFormat df = new DecimalFormat("0.00");
				String percentualFaltasTexto = "0";

				for (AcompanhamentoSemanalDiscente acomp : listaGeralAcompanhamentos) {

					if (contador == 0) {
						discente = acomp.getDiscente();
					}

					if (idDiscenteAuxiliar != acomp.getDiscente().getId()) {
						if (contador > 0) {
							chFaltas = chExecutada - chPresenca;
							if (chExecutada > 0) {
								percentualFaltas = (double) ((chFaltas * 100) / chExecutada);

								percentualFaltasTexto = df.format((double) ((chFaltas * 100) / chExecutada));
							}

							RelatorioFrequenciaTurmaIMD relatorio = new RelatorioFrequenciaTurmaIMD();
							relatorio.setDiscente(discente);
							relatorio.setChExecutada(chExecutada);
							relatorio.setChFaltas(chFaltas);
							relatorio.setChTotal(chTotal);
							relatorio.setPercentualFaltas(percentualFaltas);
							relatorio.setPercentualFaltasTexto(percentualFaltasTexto);
							relatorio.setQtdFaltas(qtdFaltas);

							listaRelatorioFrequencia.add(relatorio);

							chExecutada = (double) 0;
							chPresenca = (double) 0;
							chFaltas = (double) 0;
							percentualFaltas = (double) 0;
							chTotal = (double) 0;
							percentualFaltasTexto = "0";
							qtdFaltas = (double) 0;

						}

						idDiscenteAuxiliar = acomp.getDiscente().getId();
						discente = acomp.getDiscente();
					}

					if (acomp.getFrequencia() != null) {
						chExecutada += (double) (acomp.getPeriodoAvaliacao().getChTotalPeriodo());
						chPresenca += (double) ((acomp.getPeriodoAvaliacao().getChTotalPeriodo() * acomp
								.getFrequencia()));
						if (acomp.getFrequencia() == 0) {
							qtdFaltas++;
						} else if (acomp.getFrequencia() == 0.5) {
							qtdFaltas += 0.5;
						}
					}

					chTotal += (double) (acomp.getPeriodoAvaliacao().getChTotalPeriodo());
					contador++;

				}

				if (contador > 0) {
					chFaltas = chExecutada - chPresenca;
					if (chExecutada > 0) {
						percentualFaltas = (double) ((chFaltas * 100) / chExecutada);

						percentualFaltasTexto = df.format((double) ((chFaltas * 100) / chExecutada));
					}

					RelatorioFrequenciaTurmaIMD relatorio = new RelatorioFrequenciaTurmaIMD();
					relatorio.setDiscente(discente);
					relatorio.setChExecutada(chExecutada);
					relatorio.setChFaltas(chFaltas);
					relatorio.setChTotal(chTotal);
					relatorio.setPercentualFaltas(percentualFaltas);
					relatorio.setPercentualFaltasTexto(percentualFaltasTexto);
					relatorio.setQtdFaltas(qtdFaltas);

					listaRelatorioFrequencia.add(relatorio);

					chExecutada = (double) 0;
					chPresenca = (double) 0;
					chFaltas = (double) 0;
					percentualFaltas = (double) 0;
					chTotal = (double) 0;
					percentualFaltasTexto = "0";
					qtdFaltas = (double) 0;
				}

				// VERIFICA��O SE O RELAT�RIO � CONFIGUR�VEL E SE O FILTRO SOBRE
				// A QUANTIDADE DE FALTAS FOI INFORMADO
				if (qtdFaltasMinimaInformada != null || qtdFaltasMaximaInformada != null) {

					if (qtdFaltasMinimaInformada != null && qtdFaltasMaximaInformada != null) {
						if (qtdFaltasMinimaInformada > qtdFaltasMaximaInformada) {
							addMessage("A quantidade m�nima de faltas � maior que a quantidade m�xima de faltas.",
									TipoMensagemUFRN.ERROR);
							return forward("/metropole_digital/lancar_frequencia/selecao_filtros_relatorio.jsp");
						}
					}

					List<RelatorioFrequenciaTurmaIMD> listaRelatorioFiltrada = new ArrayList<RelatorioFrequenciaTurmaIMD>();
					for (RelatorioFrequenciaTurmaIMD reg : listaRelatorioFrequencia) {
						if (qtdFaltasMinimaInformada != null && qtdFaltasMaximaInformada != null) {
							if (qtdFaltasMinimaInformada <= reg.getQtdFaltas()
									&& qtdFaltasMaximaInformada >= reg.getQtdFaltas()) {
								listaRelatorioFiltrada.add(reg);
								retornouResultadosFiltros = true;
							}
						} else if (qtdFaltasMinimaInformada != null) {
							if (qtdFaltasMinimaInformada <= reg.getQtdFaltas()) {
								listaRelatorioFiltrada.add(reg);
								retornouResultadosFiltros = true;
							}
						} else if (qtdFaltasMaximaInformada != null) {
							if (qtdFaltasMaximaInformada >= reg.getQtdFaltas()) {
								listaRelatorioFiltrada.add(reg);
								retornouResultadosFiltros = true;
							}
						}
					}
					listaRelatorioFrequencia = listaRelatorioFiltrada;
				}

				// VERIFICA��O SE O RELAT�RIO � CONFIGUR�VEL E SE O FILTRO SOBRE
				// A PERCENTUAL DE FALTAS FOI INFORMADO
				if (percFaltasMinimaInformada != null || percFaltasMaximaInformada != null) {

					if (percFaltasMinimaInformada != null && percFaltasMaximaInformada != null) {
						if (percFaltasMinimaInformada > percFaltasMaximaInformada) {
							addMessage("O percentual m�nimo de faltas � maior que o percentual m�ximo de faltas.",
									TipoMensagemUFRN.ERROR);
							return forward("/metropole_digital/lancar_frequencia/selecao_filtros_relatorio.jsp");
						}
					}

					List<RelatorioFrequenciaTurmaIMD> listaRelatorioFiltrada = new ArrayList<RelatorioFrequenciaTurmaIMD>();
					for (RelatorioFrequenciaTurmaIMD reg : listaRelatorioFrequencia) {
						if (percFaltasMinimaInformada != null && percFaltasMaximaInformada != null) {
							if (percFaltasMinimaInformada <= reg.getPercentualFaltas()
									&& percFaltasMaximaInformada >= reg.getPercentualFaltas()) {
								listaRelatorioFiltrada.add(reg);
								retornouResultadosFiltros = true;
							}
						} else if (percFaltasMinimaInformada != null) {
							if (percFaltasMinimaInformada <= reg.getPercentualFaltas()) {
								listaRelatorioFiltrada.add(reg);
								retornouResultadosFiltros = true;
							}
						} else if (percFaltasMaximaInformada != null) {
							if (percFaltasMaximaInformada >= reg.getPercentualFaltas()) {
								listaRelatorioFiltrada.add(reg);
								retornouResultadosFiltros = true;
							}
						}
					}
					listaRelatorioFrequencia = listaRelatorioFiltrada;
				}

				relatorioFiltrado = false;
				if (retornouResultadosFiltros) {
					return forward("/metropole_digital/lancar_frequencia/lista_frequencia_turma.jsp");
				} else {
					addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
					return null;
				}

			} else {
				addMessage("Para gerar o relat�rio com os filtros, a data in�cio ou a data fim deve ser informada.",
						TipoMensagemUFRN.ERROR);
				return forward("/metropole_digital/lancar_frequencia/selecao_filtros_relatorio.jsp");
			}

		} finally {
			acompDao.close();
		}
	}

	/**
	 * Fun��o que organiza os objetos do MBean para o relat�rio de frequencia
	 * por discente
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * <li>/sigaa.war/metropole_digital/principal.jsp</li>
	 * </ul>
	 * 
	 * 
	 * @throws DAOException
	 * @return
	 **/
	public void gerarRelatorioFrequenciaDiscente() throws DAOException {
		try {
			List<RelatorioFrequenciaTurmaIMD> novaLista = new ArrayList<RelatorioFrequenciaTurmaIMD>();
			for (RelatorioFrequenciaTurmaIMD item : listaRelatorioFrequencia) {
				if (item.getDiscente().getId() == discenteSelecionado.getId()) {
					novaLista.add(item);
					if (turmaEntradaSelecionada == null) {
						idTurmaEntradaSelecionada = item.getDiscente().getTurmaEntradaTecnico().getId();
					}
					break;
				}
			}
			listaRelatorioFrequencia = new ArrayList<RelatorioFrequenciaTurmaIMD>();
			listaRelatorioFrequencia = novaLista;

			List<AcompanhamentoSemanalDiscente> novaListaAcomp = new ArrayList<AcompanhamentoSemanalDiscente>();
			for (AcompanhamentoSemanalDiscente itemAcomp : listaGeralAcompanhamentos) {
				if (itemAcomp.getDiscente().getId() == discenteSelecionado.getId()) {
					novaListaAcomp.add(itemAcomp);
				}
			}

			List<AcompanhamentoSemanalDiscente> novaListaOrdenada = new ArrayList<AcompanhamentoSemanalDiscente>();

			for (int i = 1; i <= novaListaAcomp.size(); i++) {
				for (AcompanhamentoSemanalDiscente ac : novaListaAcomp) {
					if (ac.getPeriodoAvaliacao().getNumeroPeriodo() == i) {
						novaListaOrdenada.add(ac);
					}
				}
			}

			listaFrequencias = new ArrayList<AcompanhamentoSemanalDiscente>();
			listaFrequencias = novaListaOrdenada;

		} finally {
			getGenericDAO().close();
		}

	}

}
