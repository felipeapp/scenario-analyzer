/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 03/04/2007
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.Curso;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.monitoria.DiscenteMonitoriaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.Orientacao;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.TipoVinculoDiscenteMonitoria;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * MBean usado para realizar consultas sobre monitor: visualização detalhada de
 * monitor, exclusão de monitor, emissão de certificado e declaração de monitor.
 * 
 * Prograd(Pró-Reitoria de Graduação)
 * 
 * @author Victor Hugo
 * @author Ilueny
 * 
 */
@Component("consultarMonitor")
@Scope("request")
public class ConsultarMonitorMBean extends SigaaAbstractController<DiscenteMonitoria> {

	/** Atributo utilizado para representar o Projeto de Ensino */
    private ProjetoEnsino projetoEnsino = new ProjetoEnsino();

    /** Atributo utilizado para representar o título do projeto */
    private String tituloProjeto = null;	

    /** Atributo utilizado para representar o servidor */
    private Servidor servidor = new Servidor();

    /** Atributo utilizado para representar a situação do monitor */
    private SituacaoDiscenteMonitoria situacaoMonitor = new SituacaoDiscenteMonitoria();

    /** utilizado na tela de busca dos discentes */
    private Curso curso = new Curso();

    /** Atributo utilizado para representar a Orientação feita para o monitor */
    private Orientacao orientacao;

    /** Atributo utilizado para representar as orientações localizadas */
    private Collection<Orientacao> orientacoesLocalizadas = new HashSet<Orientacao>();

    /** Atributo utilizado para representar o array de monitores */
    private Collection<DiscenteMonitoria> monitores = new ArrayList<DiscenteMonitoria>();

    /** campos para auxiliar na busca */
    private boolean checkBuscaProjeto;
    /** Atributo utilizado para auxiliar na busca por orientador */
    private boolean checkBuscaOrientador;
    /** Atributo utilizado para auxiliar na busca por Situação */
    private boolean checkBuscaSituacao;
    /** Atributo utilizado para auxiliar na busca por Tipo de Vínculo */
    private boolean checkBuscaTipoVinculo;
    /** Atributo utilizado para auxiliar na busca para gerar o relatório */
    private boolean checkGerarRelatorio;
    /** Atributo utilizado para auxiliar na busca por Discente */
    private boolean checkBuscaDiscente;
    /** Atributo utilizado para auxiliar na busca por Ano */
    private boolean checkBuscaAno;
    /** Atributo utilizado para auxiliar na busca por Monitor Ativo */
    private boolean checkBuscaMonitorAtivo;
    /** Atributo utilizado para auxiliar na busca por Curso */
    private boolean checkBuscaCurso;
    /** Atributo utilizado para auxiliar na busca por Período de Entrada */
    private boolean checkBuscaPeriodoEntrada;
    /** Atributo utilizado para auxiliar na busca por Vínculo */
    private Integer tipoVinculo;
    /** Atributo utilizado para auxiliar na busca por Discente */
    private Discente discente = new Discente();
    /** Atributo utilizado para auxiliar na busca por Ano de Referência */
    private Integer anoReferencia;
    /** Atributo utilizado para auxiliar na busca por Monitor ativo */
    private boolean monitorAtivo;
    /** Atributo utilizado para auxiliar na busca com paginação */
    private boolean comPaginacao = true;

    /** avaliação do projeto */
    private boolean mostarDocentes;
    /** Atributo utilizado para auxiliar na avaliação do projeto informando a data de Início de entrada */
    private Date buscaDataInicioEntrada;
    /** Atributo utilizado para auxiliar na avaliação do projeto informando a data de Fim de entrada */
    private Date buscaDataFimEntrada;
    /** Atributo utilizado para auxiliar na avaliação do projeto informando se a busca foi realizada */
    private boolean buscaRealizada = false;



    public ConsultarMonitorMBean() {
	obj = new DiscenteMonitoria();
	anoReferencia = CalendarUtils.getAnoAtual();
	buscaRealizada = false;
    }

    /** 
     * Localiza discente na tela de buscar discente.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>Chamado por: sigaa.war/monitoria/AlterarDiscenteMonitoria/lista.jsp</li>
     *  <li>Chamado por: sigaa.war/monitoria/ConsultarMonitor/lista.jsp</li>
     * </ul>
     */
    public void localizar() {

    	erros.getMensagens().clear();		
    	monitores.clear();
    	buscaRealizada = true;

    	/** Analisando filtros selecionados */
    	String  tituloProj = null;
    	Integer idSituacao = null;
    	Integer idOrientador = null;
    	Integer tipoMonitoria = null;
    	Integer idDiscente = null;
    	Integer anoProjeto = null;
    	Boolean monAtivo = null;
    	Integer idCurso = null;
    	Date dataInicioEntrada = null;
    	Date dataFimEntrada = null;

    	ListaMensagens mensagens = new ListaMensagens();

    	/** Definição dos filtros e validações */
    	if (checkBuscaProjeto) {
    	    tituloProj = getTituloProjeto();			
    	    ValidatorUtil.validateRequired(tituloProj, "Projeto", mensagens);
    	}			
    	if (checkBuscaSituacao) {
    		idSituacao = situacaoMonitor.getId();
    		ValidatorUtil.validateRequired(situacaoMonitor, "Situação do Monitor", mensagens);
    	}
    	if (checkBuscaOrientador) {
    		idOrientador = servidor.getId();
    		ValidatorUtil.validateRequired(servidor, "Orientador", mensagens);
    	}
    	if (checkBuscaTipoVinculo) {
    		tipoMonitoria = tipoVinculo;
    		ValidatorUtil.validateRequired(new TipoVinculoDiscenteMonitoria(tipoVinculo), "Tipo de Vínculo do Monitor", mensagens);
    	}
    	if (checkBuscaDiscente) {
    		idDiscente = discente.getId();
    		ValidatorUtil.validateRequired(discente, "Discente", mensagens);
    	}
    	if (checkBuscaAno) {
    		anoProjeto = anoReferencia;
    		ValidatorUtil.validateRequired(anoReferencia, "Ano do Projeto", mensagens);
    		ValidatorUtil.validateMinValue(anoReferencia, 1900, "Ano", mensagens);
    	}
    	if (checkBuscaMonitorAtivo) {
    		monAtivo = monitorAtivo;
    		ValidatorUtil.validateRequired(monitorAtivo, "Situação do Monitor", mensagens);
    	}
    	if (checkBuscaCurso) {
    		idCurso = curso.getId();
    		ValidatorUtil.validateRequired(curso, "Curso do Monitor", mensagens);
    	}

    	if (checkBuscaPeriodoEntrada) {
    		dataInicioEntrada = buscaDataInicioEntrada;
    		dataFimEntrada = buscaDataFimEntrada;
    		ValidatorUtil.validateRequired(buscaDataInicioEntrada, "Data Início Monitoria", mensagens);
    		ValidatorUtil.validateRequired(buscaDataFimEntrada, "Data Fim Monitoria", mensagens);

    		if( !ValidatorUtil.isEmpty(dataInicioEntrada) && dataInicioEntrada.compareTo(dataFimEntrada) > 0 ) {
    			mensagens.addErro("Data Início Monitoria deve ser menor ou igual à Data Fim Monitoria");
    		}


    	}

    	if ((!checkBuscaProjeto) && (!checkBuscaSituacao)
    			&& (!checkBuscaOrientador) && (!checkBuscaTipoVinculo)
    			&& (!checkBuscaDiscente) && (!checkBuscaAno)
    			&& (!checkBuscaCurso) && (!checkBuscaPeriodoEntrada)) {
    		addMensagemErro("Selecione pelo menos uma opção para buscar os monitores!");
    	} else {

    		DiscenteMonitoriaDao dao =  getDAO(DiscenteMonitoriaDao.class);
    		try {

    			if (!mensagens.isEmpty()) {
    				addMensagens(mensagens);
    			}else {
        			monitores = dao.filter(tituloProj, idOrientador, idDiscente,
        					idSituacao, tipoMonitoria, anoProjeto, monAtivo,
        					idCurso, dataInicioEntrada, dataFimEntrada);
    
        			if(monitores.isEmpty()) {
        				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
        			}
    			}


    		} catch (DAOException e) {
    			addMensagemErro(e.getMessage());
    			notifyError(e);
    		}

    	}

    }

    /**
     * Habilita ou desabilita a paginação dependendo do tipo de consulta
     * (relatório ou em tela)
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/monitoria/ConsultarMonitor/lista_dados_bancarios.jsp</li>
     *  <li>sigaa.war/monitoria/Relatorios/dados_bancarios_monitores_form.jsp</li>
     * </ul>
     * @return
     */
    public String localizarRelatorioDadosBancarios() {
    	localizar();

    	if (isCheckGerarRelatorio()) {
    		return forward(ConstantesNavegacaoMonitoria.RELATORIO_DADOS_BANCARIOS_MONITORES_REL);
    	} else {
    		return null;
    	}

    }

    /**
     * Exibe os dados do monitor
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>sigaa.war/monitoria/AlterarDiscenteMonitoria/lista.jsp</li>
     * <li>sigaa.war/monitoria/AvaliacaoRelatorioProjeto/form.jsp</li>
     * <li>sigaa.war/monitoria/CancelarBolsas/form.jsp</li>
     * <li>sigaa.war/monitoria/CancelarBolsas/lista.jsp</li>
     * <li>sigaa.war/monitoria/ConsultarMonitor/lista.jsp</li>
     * <li>sigaa.war/monitoria/DocumentosAutenticados/lista_discentes.jsp</li>
     * <li>sigaa.war/monitoria/RelatorioMonitor/busca.jsp</li>
     * <li>sigaa.war/monitoria/RelatorioMonitor/lista_coordenacao.jsp</li>
     * <li>sigaa.war/monitoria/RelatorioMonitor/lista_prograd.jsp</li>
     * <li>sigaa.war/monitoria/ValidacaoProgradAtividadeMonitor/lista.jsp</li>
     * <li>sigaa.war/monitoria/ValidaSelecaoMonitor/form.jsp</li>
     * </ul>
     * 
     * @return
     * @throws DAOException
     */
    public String view() throws DAOException {

    	Integer id = getParameterInt("id");

    	// por padrão mostra os docentes
    	setMostarDocentes(true);

    	// se for da comissão esconde os docentes, 
    	// mas se for da prograd(Pró-Reitoria de Graduação) também, então pode ver
    	if ((getAcessoMenu() != null) 
    			&& (getAcessoMenu().isComissaoMonitoria())
    			&& (!getAcessoMenu().isMonitoria())) {
    		setMostarDocentes(false);
    	}


    	GenericDAO dao = getGenericDAO();
    	obj = dao.findByPrimaryKey(id, DiscenteMonitoria.class);
    	obj.getOrientacoes().iterator();
    	obj.setDiscente(dao.findByPrimaryKey(obj.getDiscente().getId(), DiscenteGraduacao.class, 
    			"discente.matricula", "discente.id",
    			"discente.pessoa.nome",
    			"discente.curso.nome"));

    	return forward(ConstantesNavegacaoMonitoria.CONSULTARMONITOR_VIEW);

    }


    /**
     * Usado na busca de discentes
     * <br /><br />
     * Não é chamado por jsp
     * 
     * @return
     */
    
    public Collection<SelectItem> getSituacaoDiscenteMonitoriaCombo() {

    	DiscenteMonitoriaDao dao =  getDAO(DiscenteMonitoriaDao.class);

    	try {
    		return toSelectItems(dao.findAll(SituacaoDiscenteMonitoria.class), "id", "descricao");
    	} catch (Exception e) {
    		notifyError(e);
    		addMensagemErro(e.getMessage());
    		return null;
    	}

    }

    @Override
    public String getDirBase() {
	return "/monitoria/ConsultarMonitor";
    }

    @Override
    public String getFormPage() {
	return "/monitoria/ConsultarMonitor/form.jsp";
    }

    @Override
    public String getListPage() {
	return "/monitoria/ConsultarMonitor/lista.jsp";
    }

    /**
     * Permite que o coordenador do projeto de monitoria altere os monitores dos
     * seu projeto.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/portais/docente/menu_docente.jsp</li>
     * </ul>
     * @return
     * @throws SegurancaException 
     */
    public String coordenadorAlterarMonitor() throws SegurancaException {
	try {
	    if ((getUsuarioLogado().getServidor() != null) && (getAcessoMenu().isCoordenadorMonitoria() || getAcessoMenu().isMonitoria())) {
        	    DiscenteMonitoriaDao dao =  getDAO(DiscenteMonitoriaDao.class);
        	    setMonitores(dao.findByCoordenador(getUsuarioLogado().getServidor().getId()));
        	    return forward(ConstantesNavegacaoMonitoria.ALTERARDISCENTEMONITORIA_LISTA);
	    } else {
		throw new SegurancaException("Área restrita ao coordenadores e gestores de monitoria.");
	    }
	} catch (DAOException e) {
	    notifyError(e);
	    return null;
	}
    }

    public Collection<Orientacao> getOrientacoesLocalizadas() {
	return orientacoesLocalizadas;
    }

    public void setOrientacoesLocalizadas(
	    Collection<Orientacao> orientacoesLocalizadas) {
	this.orientacoesLocalizadas = orientacoesLocalizadas;
    }

    public Orientacao getOrientacao() {
	return orientacao;
    }

    public void setOrientacao(Orientacao orientacao) {
	this.orientacao = orientacao;
    }

    public boolean isCheckBuscaOrientador() {
	return checkBuscaOrientador;
    }

    public void setCheckBuscaOrientador(boolean checkBuscaOrientador) {
	this.checkBuscaOrientador = checkBuscaOrientador;
    }

    public boolean isCheckBuscaProjeto() {
	return checkBuscaProjeto;
    }

    public void setCheckBuscaProjeto(boolean checkBuscaProjeto) {
	this.checkBuscaProjeto = checkBuscaProjeto;
    }

    public boolean isCheckBuscaSituacao() {
	return checkBuscaSituacao;
    }

    public void setCheckBuscaSituacao(boolean checkBuscaSituacao) {
	this.checkBuscaSituacao = checkBuscaSituacao;
    }

    public boolean isCheckBuscaTipoVinculo() {
	return checkBuscaTipoVinculo;
    }

    public void setCheckBuscaTipoVinculo(boolean checkBuscaTipoVinculo) {
	this.checkBuscaTipoVinculo = checkBuscaTipoVinculo;
    }

    public ProjetoEnsino getProjetoEnsino() {
	return projetoEnsino;
    }

    public void setProjetoEnsino(ProjetoEnsino projetoEnsino) {
	this.projetoEnsino = projetoEnsino;
    }

    public Servidor getServidor() {
	return servidor;
    }

    public void setServidor(Servidor servidor) {
	this.servidor = servidor;
    }

    public SituacaoDiscenteMonitoria getSituacaoMonitor() {
	return situacaoMonitor;
    }

    public void setSituacaoMonitor(SituacaoDiscenteMonitoria situacaoMonitor) {
	this.situacaoMonitor = situacaoMonitor;
    }

    public Collection<DiscenteMonitoria> getMonitores() {
	return monitores;
    }

    public void setMonitores(Collection<DiscenteMonitoria> monitores) {
	this.monitores = monitores;
    }

    public Integer getTipoVinculo() {
	return tipoVinculo;
    }

    public void setTipoVinculo(Integer tipoVinculo) {
	this.tipoVinculo = tipoVinculo;
    }

    public boolean isCheckGerarRelatorio() {
	return checkGerarRelatorio;
    }

    public void setCheckGerarRelatorio(boolean checkGerarRelatorio) {
	this.checkGerarRelatorio = checkGerarRelatorio;
    }

    public boolean isCheckBuscaDiscente() {
	return checkBuscaDiscente;
    }

    public void setCheckBuscaDiscente(boolean checkBuscaDiscente) {
	this.checkBuscaDiscente = checkBuscaDiscente;
    }

    public Discente getDiscente() {
	return discente;
    }

    public void setDiscente(Discente discente) {
	this.discente = discente;
    }

    public boolean isCheckBuscaAno() {
	return checkBuscaAno;
    }

    public void setCheckBuscaAno(boolean checkBuscaAno) {
	this.checkBuscaAno = checkBuscaAno;
    }

    public Integer getAnoReferencia() {
	return anoReferencia;
    }

    public void setAnoReferencia(Integer anoReferencia) {
	this.anoReferencia = anoReferencia;
    }

    public boolean isCheckBuscaMonitorAtivo() {
	return checkBuscaMonitorAtivo;
    }

    public void setCheckBuscaMonitorAtivo(boolean checkBuscaMonitorAtivo) {
	this.checkBuscaMonitorAtivo = checkBuscaMonitorAtivo;
    }

    public boolean isMonitorAtivo() {
	return monitorAtivo;
    }

    public void setMonitorAtivo(boolean monitorAtivo) {
	this.monitorAtivo = monitorAtivo;
    }

    public boolean isComPaginacao() {
	return comPaginacao;
    }

    public void setComPaginacao(boolean comPaginacao) {
	this.comPaginacao = comPaginacao;
    }

    public boolean isCheckBuscaCurso() {
	return checkBuscaCurso;
    }

    public void setCheckBuscaCurso(boolean checkBuscaCurso) {
	this.checkBuscaCurso = checkBuscaCurso;
    }

    public Curso getCurso() {
	return curso;
    }

    public void setCurso(Curso curso) {
	this.curso = curso;
    }

    public boolean isMostarDocentes() {
	return mostarDocentes;
    }

    public void setMostarDocentes(boolean mostarDocentes) {
	this.mostarDocentes = mostarDocentes;
    }

    public String getTituloProjeto() {
	return tituloProjeto;
    }

    public void setTituloProjeto(String tituloProjeto) {
	this.tituloProjeto = tituloProjeto;
    }

    public boolean isCheckBuscaPeriodoEntrada() {
        return checkBuscaPeriodoEntrada;
    }

    public void setCheckBuscaPeriodoEntrada(boolean checkBuscaPeriodoEntrada) {
        this.checkBuscaPeriodoEntrada = checkBuscaPeriodoEntrada;
    }

    public Date getBuscaDataInicioEntrada() {
        return buscaDataInicioEntrada;
    }

    public void setBuscaDataInicioEntrada(Date buscaDataInicioEntrada) {
        this.buscaDataInicioEntrada = buscaDataInicioEntrada;
    }

    public Date getBuscaDataFimEntrada() {
        return buscaDataFimEntrada;
    }

    public void setBuscaDataFimEntrada(Date buscaDataFimEntrada) {
        this.buscaDataFimEntrada = buscaDataFimEntrada;
    }

	public void setBuscaRealizada(boolean buscaRealizada) {
		this.buscaRealizada = buscaRealizada;
	}

	public boolean isBuscaRealizada() {
		return buscaRealizada;
	}

}
