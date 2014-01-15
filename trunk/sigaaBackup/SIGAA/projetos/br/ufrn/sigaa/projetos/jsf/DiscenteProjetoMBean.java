/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '05/01/2011'
 * 
 */
package br.ufrn.sigaa.projetos.jsf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.component.UIData;
import javax.faces.component.html.HtmlDataTable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.notificacoes.Destinatario;
import br.ufrn.rh.dominio.Servidor;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.projetos.DiscenteProjetoDao;
import br.ufrn.sigaa.arq.jsf.NotificacoesMBean;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.projetos.dominio.DiscenteProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoVinculoDiscente;

/**
 * MBean responsável pelo gerenciamento dos discentes de projetos.
 * 
 * @author Ilueny Santos
 *
 */
@Component("discenteProjetoBean")
@Scope("session")
public class DiscenteProjetoMBean extends SigaaAbstractController<DiscenteProjeto> {

	/** Auxiliar de tela de busca. */
	private UIData UIDiscentes = new HtmlDataTable();
	
	/** Resultado da busca dos discentes */
	private List<DiscenteProjeto> listDiscentes = new ArrayList<DiscenteProjeto>();
	
	/** Auxiliar de tela de busca. */
	private Curso curso = new Curso();

	/** Auxiliar de tela de busca. */
	private TipoVinculoDiscente vinculoDiscente = new TipoVinculoDiscente();
	
	/** Auxiliar de tela de busca. */ 
	private boolean checkBuscaAnoInicioBolsa;
	
	/** Auxiliar de tela de busca. */
	private boolean checkBuscaAnoInicioFinalizacao;	

	/** Auxiliar de tela de busca. */
	private boolean checkBuscaEdital;

	/** Auxiliar de tela de busca. */
	private boolean checkBuscaTitulo;

	/** Auxiliar de tela de busca. */
	private boolean checkBuscaVinculo;

	/** Auxiliar de tela de busca. */
	private boolean checkGerarRelatorio;

	/** Auxiliar de tela de busca. */
	private boolean checkBuscaDiscente;

	/** Auxiliar de tela de busca. */
	private boolean checkBuscaCoordenador;

	/** Auxiliar de tela de busca. */
	private boolean checkBuscaAno;
	
	/** Auxiliar de tela de busca. */
	private boolean checkBuscaAnoInicio;
	
	/** Usado para armazenar informação digitada na tela de busca. */
	private Date dataInicioBolsa = new Date();
	
	/** Usado para armazenar informação digitada na tela de busca. */
	private Date dataFimBolsa = new Date();
	
	/** Usado para armazenar informação digitada na tela de busca. */
	private Date dataInicioFinalizacao = new Date();
	
	/** Usado para armazenar informação digitada na tela de busca. */
	private Date dataFimFinalizacao = new Date();	

	/** Usado para armazenar informação digitada na tela de busca. */
	private Date dataInicio = new Date();

	/** Usado para armazenar informação digitada na tela de busca. */
	private Date dataFim = new Date();

	/** Usado para armazenar informação digitada na tela de busca. */
	private boolean checkBuscaCurso;

	/** Usado para armazenar informação digitada na tela de busca. */
	private Discente discente = new Discente();

	/** Usado para armazenar informação digitada na tela de busca. */
	private Servidor coordenador = new Servidor();

	/** Usado para armazenar informação digitada na tela de busca. */
	private String buscaTitulo;

	/** Usado para armazenar informação digitada na tela de busca. */
	private Integer buscaAno = CalendarUtils.getAnoAtual();

	/** Usado para armazenar informação digitada na tela de busca. */
	private int buscaEdital;

	/**
	 * Inicia a busca por discentes cadatrados no projeto.
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>sigaa.war/portais/docente/menu_docente.jsp</li>
     * </ul>
     * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarBusca() throws SegurancaException {
		return forward(ConstantesNavegacaoProjetos.DISCENTES_BUSCA);
	}
	
	/**
	 * Responsável por buscar discentes cadastrados em planos de trabalhos de projetos.
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>sigaa.war/projetos/DiscenteProjeto/busca.jsp</li>
     * </ul>
 	 */
	@Override
	public String buscar() throws Exception {
		
		if (UIDiscentes != null)
			UIDiscentes = new HtmlDataTable();

		/* Analisando filtros selecionados */
		String titulo = null;
		Integer idEdital = null;
		Integer idVinculo = null;
		Integer idDiscente = null;
		Integer idCoordenador = null;
		Integer ano = null;
		Integer idCurso = null;
		Date dataInicioBolsa = null;
		Date dataFimBolsa = null;
		Date dataInicioFinalizacao = null;
		Date dataFimFinalizacao = null;

		ListaMensagens mensagens = new ListaMensagens();

		// Definição dos filtros e validações
		if(checkBuscaAnoInicioBolsa) {
			dataInicioBolsa = this.dataInicioBolsa;
			dataFimBolsa = this.dataFimBolsa;
			ValidatorUtil.validateRequired(dataInicioBolsa,"Data Início Bolsa", mensagens);
			ValidatorUtil.validateRequired(dataFimBolsa,"Data Fim Bolsa", mensagens);
		}
		
		if(checkBuscaAnoInicioFinalizacao) {
			dataInicioFinalizacao = this.dataInicioFinalizacao;
			dataFimFinalizacao = this.dataFimFinalizacao;
			ValidatorUtil.validateRequired(dataInicioFinalizacao,"Data Início Finalização", mensagens);
			ValidatorUtil.validateRequired(dataFimFinalizacao,"Data Fim Finalização", mensagens);
		}
		
		if (checkBuscaTitulo) {
			titulo = buscaTitulo;
			ValidatorUtil.validateRequired(titulo, "Título", mensagens);
		}
		
		if (checkBuscaEdital) {
			idEdital = buscaEdital;
			if(idEdital == 0)
				mensagens.addErro("Edital: Campo obrigatório não informado.");
		}
		
		if (checkBuscaVinculo) {
			idVinculo = vinculoDiscente.getId();
			if(idVinculo == 0)
				mensagens.addErro("Vínculo: Campo obrigatório não informado.");
		}
		
		if (checkBuscaDiscente) {
			idDiscente = discente.getId();			
		}

		if (checkBuscaCoordenador) {
			idCoordenador = coordenador.getId();			
		}

		if (checkBuscaAno) {
			ano = buscaAno;
			ValidatorUtil.validateRequired(ano, "Ano do projeto", mensagens);
			
		}
		
		if (checkBuscaCurso) {
			idCurso = curso.getId();
			if(idCurso == 0)
				mensagens.addErro("Curso: Campo obrigatório não informado.");
		}
		
		if ((!checkBuscaTitulo) && (!checkBuscaDiscente) && (!checkBuscaAno)
				&& (!checkBuscaCurso) && (!checkBuscaEdital)
				&& (!checkBuscaVinculo) && (!checkBuscaAnoInicio) && (!checkBuscaAnoInicio) 
				&& (!checkBuscaAnoInicioBolsa) && (!checkBuscaAnoInicioFinalizacao) && (!checkBuscaCoordenador)) {
			addMensagemErro("Selecione pelo menos uma opção para buscar os discentes!");			
		} else {

			try{
				DiscenteProjetoDao dao = getDAO(DiscenteProjetoDao.class);
				if (mensagens.isEmpty()) {					
					listDiscentes = dao.filter(titulo, ano, dataInicioBolsa, dataFimBolsa, dataInicioFinalizacao, 
							dataFimFinalizacao, idVinculo, idEdital, idDiscente, idCurso, idCoordenador);
					
					if(listDiscentes.isEmpty())
						addMensagemWarning("Nenhum discente encontrado para os parâmetros de busca informados.");
				}
				else {
					addMensagens(mensagens);
				}
			    
			}catch (DAOException e) {
			    notifyError(e);
			}			
		}
		return super.buscar();
	}
	
	/**
	 * Carrega plano e prepara MBeans para visualização.
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projeto/DiscenteProjeto/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String view() {
		Integer id = getParameterInt("idDiscenteProjeto");
		try {
			obj = getGenericDAO().findByPrimaryKey(id, DiscenteProjeto.class);
			//Evitar erro de Lazy
			if (obj.getDiscente() != null && obj.getDiscente().getCurso() != null) {
				obj.getDiscente().getCurso().getNomeCompleto();
			}
			return forward(ConstantesNavegacaoProjetos.DISCENTES_VIEW);
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
			return null;
		}
	}
	
	
	/**
	 * Verifica permissões para apresentação do relatório com os dados bancários 
	 * dos discentes. 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
     *	 <li>sigaa.war/portais/docente/menu_docente.jsp</li>
     * </ul>
	 * 
	 * @return
	 */
	public String iniciarDadosBancariosDiscentes() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_BOLSAS_ACOES_ASSOCIADAS);
		return forward(ConstantesNavegacaoProjetos.RELATORIOS_DADOS_BANCARIOS_DISCENTES_FORM);		
	}

	/**
	 * Verifica permissões para apresentação do relatório com os dados bancários 
	 * dos discentes. 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
     *	 <li>sigaa.war/projetos/Relatorios/dados_bancarios_discentes_form.jsp</li>
     * </ul>
	 * 
	 * @return
	 */
	public String buscarDadosBancarios() throws Exception {
		checkRole(SigaaPapeis.GESTOR_BOLSAS_ACOES_ASSOCIADAS);
		buscar();
		if (checkGerarRelatorio) {
			if(!listDiscentes.isEmpty()){
				return forward(ConstantesNavegacaoProjetos.RELATORIOS_DADOS_BANCARIOS_DISCENTES_VIEW);
			}
		}
		return null;
	}

	public String enviarMensagem() throws NumberFormatException, ArqException{
		
		String nome = null;
		String email = null;
		int idUsuario = 0;
		DiscenteDao dao = getDAO(DiscenteDao.class);
		try {
			obj = new DiscenteProjeto();
			setId();
			setObj( dao.findByPrimaryKey(obj.getId(), DiscenteProjeto.class) );
			Usuario usuario = dao.findByUsuario(obj.getDiscente().getPessoa().getId());
			nome = obj.getDiscente().getPessoa().getNome();
			email = obj.getDiscente().getPessoa().getEmail();
			idUsuario = usuario.getId() ;

			String remetente =  getUsuarioLogado().getNome();
			NotificacoesMBean notificacao = getMBean("notificacoes");
			ArrayList<Destinatario> destinatarios = new ArrayList<Destinatario>();	
			Destinatario destinatario = new Destinatario(nome, email);
			destinatario.setIdusuario(idUsuario);			
			destinatarios.add(destinatario);
			notificacao.setDestinatarios(destinatarios);
			notificacao.setRemetente( remetente );
			notificacao.setTitulo("Enviar mensagem ao Discente");
			notificacao.clear();
			notificacao.prepararFormulario();
			notificacao.getObj().setContentType(MailBody.HTML);
			return forward(notificacao.getCaminhoFormulario());
			
		} finally {
			dao.close();
		}
	}

	public UIData getUIDiscentes() {
		return UIDiscentes;
	}



	public void setUIDiscentes(UIData uIDiscentes) {
		UIDiscentes = uIDiscentes;
	}



	public List<DiscenteProjeto> getListDiscentes() {
		return listDiscentes;
	}



	public void setListDiscentes(List<DiscenteProjeto> listDiscentes) {
		this.listDiscentes = listDiscentes;
	}



	public Curso getCurso() {
		return curso;
	}



	public void setCurso(Curso curso) {
		this.curso = curso;
	}



	public TipoVinculoDiscente getVinculoDiscente() {
		return vinculoDiscente;
	}



	public void setVinculoDiscente(TipoVinculoDiscente vinculoDiscente) {
		this.vinculoDiscente = vinculoDiscente;
	}



	public boolean isCheckBuscaAnoInicioBolsa() {
		return checkBuscaAnoInicioBolsa;
	}



	public void setCheckBuscaAnoInicioBolsa(boolean checkBuscaAnoInicioBolsa) {
		this.checkBuscaAnoInicioBolsa = checkBuscaAnoInicioBolsa;
	}



	public boolean isCheckBuscaAnoInicioFinalizacao() {
		return checkBuscaAnoInicioFinalizacao;
	}



	public void setCheckBuscaAnoInicioFinalizacao(
			boolean checkBuscaAnoInicioFinalizacao) {
		this.checkBuscaAnoInicioFinalizacao = checkBuscaAnoInicioFinalizacao;
	}



	public boolean isCheckBuscaEdital() {
		return checkBuscaEdital;
	}



	public void setCheckBuscaEdital(boolean checkBuscaEdital) {
		this.checkBuscaEdital = checkBuscaEdital;
	}



	public boolean isCheckBuscaTitulo() {
		return checkBuscaTitulo;
	}



	public void setCheckBuscaTitulo(boolean checkBuscaTitulo) {
		this.checkBuscaTitulo = checkBuscaTitulo;
	}



	public boolean isCheckBuscaVinculo() {
		return checkBuscaVinculo;
	}



	public void setCheckBuscaVinculo(boolean checkBuscaVinculo) {
		this.checkBuscaVinculo = checkBuscaVinculo;
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



	public boolean isCheckBuscaCoordenador() {
		return checkBuscaCoordenador;
	}



	public void setCheckBuscaCoordenador(boolean checkBuscaCoordenador) {
		this.checkBuscaCoordenador = checkBuscaCoordenador;
	}



	public boolean isCheckBuscaAno() {
		return checkBuscaAno;
	}



	public void setCheckBuscaAno(boolean checkBuscaAno) {
		this.checkBuscaAno = checkBuscaAno;
	}



	public boolean isCheckBuscaAnoInicio() {
		return checkBuscaAnoInicio;
	}



	public void setCheckBuscaAnoInicio(boolean checkBuscaAnoInicio) {
		this.checkBuscaAnoInicio = checkBuscaAnoInicio;
	}



	public Date getDataInicioBolsa() {
		return dataInicioBolsa;
	}



	public void setDataInicioBolsa(Date dataInicioBolsa) {
		this.dataInicioBolsa = dataInicioBolsa;
	}



	public Date getDataFimBolsa() {
		return dataFimBolsa;
	}



	public void setDataFimBolsa(Date dataFimBolsa) {
		this.dataFimBolsa = dataFimBolsa;
	}



	public Date getDataInicioFinalizacao() {
		return dataInicioFinalizacao;
	}



	public void setDataInicioFinalizacao(Date dataInicioFinalizacao) {
		this.dataInicioFinalizacao = dataInicioFinalizacao;
	}



	public Date getDataFimFinalizacao() {
		return dataFimFinalizacao;
	}



	public void setDataFimFinalizacao(Date dataFimFinalizacao) {
		this.dataFimFinalizacao = dataFimFinalizacao;
	}



	public Date getDataInicio() {
		return dataInicio;
	}



	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}



	public Date getDataFim() {
		return dataFim;
	}



	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}



	public boolean isCheckBuscaCurso() {
		return checkBuscaCurso;
	}



	public void setCheckBuscaCurso(boolean checkBuscaCurso) {
		this.checkBuscaCurso = checkBuscaCurso;
	}



	public Discente getDiscente() {
		return discente;
	}



	public void setDiscente(Discente discente) {
		this.discente = discente;
	}



	public Servidor getCoordenador() {
		return coordenador;
	}



	public void setCoordenador(Servidor coordenador) {
		this.coordenador = coordenador;
	}



	public String getBuscaTitulo() {
		return buscaTitulo;
	}



	public void setBuscaTitulo(String buscaTitulo) {
		this.buscaTitulo = buscaTitulo;
	}



	public Integer getBuscaAno() {
		return buscaAno;
	}



	public void setBuscaAno(Integer buscaAno) {
		this.buscaAno = buscaAno;
	}



	public int getBuscaEdital() {
		return buscaEdital;
	}



	public void setBuscaEdital(int buscaEdital) {
		this.buscaEdital = buscaEdital;
	}
}
