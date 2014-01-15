/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on '30/04/2010'
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.AvisoFaltaDocenteDao;
import br.ufrn.sigaa.arq.dao.ensino.DadosAvisoFaltaDao;
import br.ufrn.sigaa.arq.dao.ensino.DocenteExternoDao;
import br.ufrn.sigaa.arq.dao.ensino.DocenteTurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.TopicoAulaDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.jsf.TurmaVirtualMBean;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.AvisoFaltaDocente;
import br.ufrn.sigaa.ensino.dominio.DadosAvisoFalta;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.MovimentoAvisoFaltaDocente;
import br.ufrn.sigaa.ensino.util.TurmaUtil;
import br.ufrn.sigaa.mensagens.MensagensGraduacao;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * MBean responsável por gerenciar os avisos de falta dos professores
 * 
 * @author Henrique André
 */
@SuppressWarnings("serial")
@Component("avisoFalta") @Scope("session")
public class AvisoFaltaDocenteMBean extends SigaaAbstractController<AvisoFaltaDocente> {
	
	/** Coleção de {@link SelectItem} referente aos docentes de uma determinada turma. */
	private Collection<SelectItem> docenteCombo = new ArrayList<SelectItem>(0);
	/** Coleção de {@link SelectItem} contendo as datas de aula que não foram feriados. */
	private Collection<SelectItem> aulasCombo = new ArrayList<SelectItem>(0);

	/**
	 * Objeto que encapsula os campos que foram populados na busca de aviso falta docente
	 */
	private BuscaAvisoFaltaCampos buscaCampos;
	
	/**
	 *  Objeto que encapsula os checkbox que foram marcados na busca de aviso falta docente
	 */
	private BuscaAvisoFaltaChecks buscaChecks;
	
	/**
	 * Lista com todos os docentes do departamento (servidores e externos)
	 */
	private List<CorpoDocenteDepartamento> corpoDocente;
	
	/**
	 * Resultado da busca de avisos de falta
	 */
	private List<DadosAvisoFalta> resultado;
	
	/** Lista contendo os {@link AvisoFaltaDocente} para visualização das observações dos discente. */
	private List<AvisoFaltaDocente> avisosFalta;
	
	/** Coleção de departamentos ligados ao centro. */
	private Collection <UnidadeGeral> departamentos;
	
	/** Chave primária da turma ond eestá sendo cadastrada a falta. */
	private Integer idTurma;
	
	/** Atributo utilizado para armazenar a data da aula selecionada, quando houver aulas registradas para o docente.*/
	private String dataAulaStr;
	
	/**
	 * Redireciona para página
	 * JSP: /sigaa.war/ensino/aviso_falta/form.jsp
	 * @return
	 */
	private String telaForm() {
		return forward("/ensino/aviso_falta/discente/form.jsp");
	}

	/**
	 * Executado antes de ir pra tela de busca
	 * 
	 * JSP: /sigaa.war/graduacao/menus/relatorios_dae.jsp
	 * /sigaa.war/portais/docente/menu_docente.jsp
	 * /sigaa.war/portais/rh_plan/abas/graduacao.jsp
	 * @return
	 * @throws DAOException 
	 */
	public String iniciarBusca() throws DAOException {
		initBusca();
		
		if(!getAcessoMenu().isAdministradorDAE()){
			buscaChecks.setCheckUnidade(true);
			if(getAcessoMenu().isDiretorCentro() || getAcessoMenu().isChefeDepartamento() || getAcessoMenu().isPlanejamento()) {
				buscaCampos.setUnidade(getAcessoMenu().getSecretariaCentro());
				buscaChecks.setCheckCentro(true);
				buscaCampos.setCentro(getAcessoMenu().getSecretariaCentro() == null ? getUsuarioLogado().getVinculoAtivo().getUnidade().getGestora().getId() : getAcessoMenu().getSecretariaCentro().getId());
				if(getAcessoMenu().isChefeDepartamento()) {
					buscaCampos.setDepartamento(getServidorUsuario().getUnidade().getId());
					buscaCampos.setUnidade(new Unidade(buscaCampos.getDepartamento()));
				}
				carregarDepartamentos();
			}
			else {
				buscaCampos.setUnidade(getServidorUsuario().getUnidade());
				carregarCorpoDocente();
			}
		}
		
		return forward("/ensino/aviso_falta/docente/busca.jsp");
	}

	/**
	 * Carrega os departamentos de acordo com o id do centro definido no parâmetro buscaCampos.centro
	 * @throws DAOException
	 */
	private void carregarDepartamentos() throws DAOException {
		UnidadeDao dao = getDAO(UnidadeDao.class);
		departamentos = dao.findBySubUnidades(new Unidade(buscaCampos.centro),TipoUnidadeAcademica.DEPARTAMENTO);
	}
	
	/**
	 * Inicializa os atributos que serão usados na busca de aviso de falta
	 */
	private void initBusca() {
		init();
		buscaCampos = new BuscaAvisoFaltaCampos();
		buscaChecks = new BuscaAvisoFaltaChecks();
		
		buscaCampos.setAno(getCalendarioVigente().getAno());
		buscaCampos.setPeriodo(getCalendarioVigente().getPeriodo());
		resultado = null;
	}
	
	/**
	 * Carrega docentes de um departamento
	 * 
	 * @throws DAOException
	 */
	private void carregarCorpoDocente() throws DAOException {
		
		corpoDocente = new ArrayList<CorpoDocenteDepartamento>();
		
		ServidorDao servidorDao = getDAO(ServidorDao.class);
		DocenteExternoDao docExtenoDao = getDAO(DocenteExternoDao.class);
		
		int idUnidade = 0;
		
		if(getAcessoMenu().isAdministradorDAE() || getAcessoMenu().isDiretorCentro() || getAcessoMenu().isPlanejamento())
		
			idUnidade = buscaCampos.departamento;
		else
			idUnidade = getUnidadeResponsabilidade().getId();
		 
		
		Collection<Servidor> docentesQuadro = servidorDao.findByDocente(idUnidade);
		Collection<DocenteExterno> docentesExternos = docExtenoDao.findByUnidade(idUnidade);
		
		int id = 1;
		for (DocenteExterno docenteExterno : docentesExternos) {
			CorpoDocenteDepartamento corpo = new CorpoDocenteDepartamento();
			corpo.setId(id++);
			corpo.setDocenteExterno(docenteExterno);
			corpoDocente.add(corpo);
		}
		
		for (Servidor docente : docentesQuadro) {
			CorpoDocenteDepartamento corpo = new CorpoDocenteDepartamento();
			corpo.setId(id++);
			corpo.setDocente(docente);
			corpoDocente.add(corpo);
		}
	}

	/**
	 * Inicia o caso de uso pra o aluno avisar a falta do professor
	 * JSP: /sigaa.war/portais/discente/menu_discente.jsp
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
		init();
		prepareMovimento(SigaaListaComando.AVISAR_FALTA_DOCENTE);
		aulasCombo.clear(); 
		return telaForm();
	}
	
	/** Listener responsável por atualizar a coleção de departamentos ligados ao centro, quando há mudança no valor do centro.
	 * @param evt
	 * @throws DAOException
	 */
	public void centroListener(ValueChangeEvent evt) throws DAOException {
		buscaCampos.centro = (Integer) evt.getNewValue();
		UnidadeDao dao = getDAO(UnidadeDao.class);
		departamentos = dao.findBySubUnidades(new Unidade(buscaCampos.centro),TipoUnidadeAcademica.DEPARTAMENTO);
	}
	
	/** Listener responsável por atualizar a coleção de docentes ligados ao departamento, quando há mudança no valor do departamento.
	 * @param evt
	 * @throws DAOException
	 */
	public void departamentoListener(ValueChangeEvent evt) throws DAOException {
		buscaCampos.departamento = (Integer) evt.getNewValue();
		carregarCorpoDocente();
	}
	
	/**
	 * Executa a busca por avisos de falta
	 * JSP: /sigaa.war/ensino/aviso_falta/busca.jsp
	 * @throws DAOException 
	 */
	public String buscar() throws DAOException {
		validarBusca();
		
		if(hasErrors()){
			return null;
		}
		
		DadosAvisoFaltaDao dao = getDAO(DadosAvisoFaltaDao.class);
		
		if(buscaChecks.isCheckDocente())
			capturarDocenteSelecionado();
		
		resultado = dao.findGeral(buscaCampos, buscaChecks);
		
		if (resultado.isEmpty())
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		
		return forward("/ensino/aviso_falta/docente/busca.jsp");
	}
	
	/**
	 * Valida os dados da busca.
	 */
	private void validarBusca(){
		if(!buscaChecks.isCheckAnoPeriodo() && !buscaChecks.isCheckDocente() && !buscaChecks.isCheckDepartamento()
				&& !buscaChecks.isCheckCentro() && !buscaChecks.isCheckUnidade()){
			addMensagemErro("Selecione um Parâmetro de Busca.");
		}
		
		if(buscaChecks.isCheckCentro() && ValidatorUtil.isEmpty(buscaCampos.centro)){
			addMensagemErro("Selecione um Centro");
		}
		
		if(buscaChecks.isCheckDepartamento() && ValidatorUtil.isEmpty(buscaCampos.departamento)){
			addMensagemErro("Selecione um Departamento");
		}
		
		if(buscaChecks.isCheckDocente() && ValidatorUtil.isEmpty(buscaCampos.docente.getId())){
			addMensagemErro("Selecione um Docente.");
		}
		
		if(buscaChecks.isCheckAnoPeriodo() && ValidatorUtil.isEmpty(buscaCampos.getAno())){
			addMensagemErro("Ano: Campo Obrigatório Não Informado.");
			if(buscaChecks.isCheckAnoPeriodo() && ValidatorUtil.isEmpty(buscaCampos.getPeriodo())){
				addMensagemErro("Período: Campo Obrigatório Não Informado.");
			}
			return;
		}
		
		if(buscaChecks.isCheckAnoPeriodo() && ValidatorUtil.isEmpty(buscaCampos.getPeriodo())){
			addMensagemErro("Período: Campo Obrigatório Não Informado.");
		}
	}

	/**
	 * Seleciona o docente que foi selecionado na view
	 */
	private void capturarDocenteSelecionado() {
		
		
		for (CorpoDocenteDepartamento c : corpoDocente) {
			if (c.getId() == buscaCampos.getDocente().getId()) {
				
				CorpoDocenteDepartamento tmp = null;
				try {
					tmp = UFRNUtils.deepCopy(c);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				buscaCampos.setDocente(tmp);
				return;
			}
		}
	}
	
	/**
	 * Cria obj
	 */
	private void init() {
		obj = new AvisoFaltaDocente();
		obj.setDadosAvisoFalta(new DadosAvisoFalta());
		obj.getDadosAvisoFalta().setDocente(new Servidor());
		obj.getDadosAvisoFalta().setTurma(new Turma());
	}

	/**
	 * Carrega as datas das aulas
	 * 
	 * JSP: /sigaa.war/ensino/aviso_falta/form.jsp
	 * @param evt
	 * @throws DAOException
	 */
	public void carregarCombos(ValueChangeEvent evt) throws DAOException {
		
		TurmaDao dao = null;
		TurmaVirtualDao daoTurmaVirtual = null;
		TopicoAulaDao topDao = null;
		DocenteTurmaDao dDao = null;
		
		try {
			
			dao = getDAO(TurmaDao.class);
			daoTurmaVirtual = getDAO(TurmaVirtualDao.class);
			topDao = getDAO(TopicoAulaDao.class);
			dDao = getDAO(DocenteTurmaDao.class);
			
			idTurma = (Integer) evt.getNewValue();
			
			List<Date> aulasCanceladas = TurmaUtil.getDatasCanceladas(idTurma);
			Turma turma = dao.findByPrimaryKey(idTurma, Turma.class);
			List<Date> feriados = TurmaUtil.getFeriados(turma);
			
			TurmaVirtualMBean turmaBean = getMBean("turmaVirtual");
			
			List<DocenteTurma> dt = turmaBean.getDocentesTurma(turma, getDAO(UsuarioDao.class), dDao);
			docenteCombo = toSelectItems(dt, "docente.id", "docente.pessoa.nome");

			CalendarioAcademico c = CalendarioAcademicoHelper.getCalendario(turma);
			Set<Date> datasAulas = TurmaUtil.getDatasAulas(turma, c, false);
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd 'de' MMMM 'de' yyyy");
			SimpleDateFormat sdfDataId = new SimpleDateFormat("dd'/'MM'/'yyyy");

			aulasCombo.clear(); 
			dataAulaStr = null;
			Date hoje = new Date();
			for (Date data : datasAulas) {
				if ( data.before(hoje) && !feriados.contains(data) && !aulasCanceladas.contains(data) ) /** não adiciona dias de feriados no combo */			
					aulasCombo.add(new SelectItem(sdfDataId.format(data).toString(), sdf.format(data)));
			}
		} finally {
			if ( dao != null )
				dao.close();
			if ( daoTurmaVirtual != null )
				daoTurmaVirtual.close();
			if ( topDao != null )
				topDao.close();
			if ( dDao != null )
				dDao.close();
		}
	}

	/**
	 * Cadastra a notificação
	 * JSP: /sigaa.war/ensino/aviso_falta/form.jsp
	 * @throws ArqException
	 */
	@Override
	public String cadastrar() throws ArqException {
		if ( ValidatorUtil.isEmpty(obj.getDadosAvisoFalta().getDataAula()))
			obj.getDadosAvisoFalta().setDataAula(Formatador.getInstance().parseDate(dataAulaStr));
		validarAvisoFalta();		
		
		if (hasErrors())
			return null;
		
		verificaDocentes();
		
		DiscenteAdapter discente = getUsuarioLogado().getDiscenteAtivo();
		
		MovimentoAvisoFaltaDocente mov = new MovimentoAvisoFaltaDocente();
		mov.setAvisoFalta(obj);
		mov.setCodMovimento(SigaaListaComando.AVISAR_FALTA_DOCENTE);		
		mov.setDiscente(discente.getDiscente());
		
		try {
			mov = (MovimentoAvisoFaltaDocente) execute(mov);
			
			if (!mov.isTemChefe())
				addMensagem(MensagensGraduacao.AVISO_FALTA_DOCENTE_DEPARTAMENTO_SEM_CHEFE);
			
			addMensagemInformation("Sua notificação foi feita com sucesso.");
		} catch (NegocioException e) {
			return tratamentoErroPadrao(e, e.getMessage());
		}

		return cancelar();
	}

	/**
	 * Metódo identifica se é um servidor ou docente externo
	 * 
	 * @throws DAOException
	 */
	private void verificaDocentes() throws DAOException {
		
		int id = obj.getDadosAvisoFalta().getDocente().getId();
		
		ServidorDao dao = getDAO(ServidorDao.class);
		Servidor servidor = dao.findByPrimaryKey(id, Servidor.class);
		if (servidor == null) {
			obj.getDadosAvisoFalta().setDocenteExterno(new DocenteExterno(id));
			obj.getDadosAvisoFalta().setDocente(null);
		} else {
			obj.getDadosAvisoFalta().setDocenteExterno(null);
			obj.getDadosAvisoFalta().setDocente(servidor);
		}
	}

	/**
	 * Carrega as observações que os docentes fizeram no aviso de falta
	 * @param evt
	 * @throws DAOException
	 */
	public void carregarObservacoesDiscentes(ActionEvent evt) throws DAOException {
		
		Integer id = getParameterInt("idDadosFalta");
		
		AvisoFaltaDocenteDao dao = getDAO(AvisoFaltaDocenteDao.class);
		avisosFalta = dao.findByDadosAviso(id);
	}
	
	/**
	 * Valida o formulário de notificação
	 * @throws DAOException 
	 */
	private void validarAvisoFalta() throws DAOException {
		
		if (ValidatorUtil.isEmpty(idTurma))
			erros.addErro("Nenhuma turma selecionada.");
		
		if ( obj.getDadosAvisoFalta().getDataAula() != null && 
				obj.getDadosAvisoFalta().getDataAula().after(new Date()) )
			erros.addErro("Não é possível cadastrar aviso de falta com data posterior a atual.");
		
		if (erros.isErrorPresent())
			return;		
		
		List<Date> aulasCanceladas = TurmaUtil.getDatasCanceladas(idTurma);
		if ( aulasCanceladas.contains(obj.getDadosAvisoFalta().getDataAula()) )
			erros.addErro("Esta aula foi cancelada pelo professor.");
		
		erros.addAll( obj.validate() );
	}
	
	public BuscaAvisoFaltaCampos getBuscaCampos() {
		return buscaCampos;
	}

	public void setBuscaCampos(BuscaAvisoFaltaCampos buscaCampos) {
		this.buscaCampos = buscaCampos;
	}

	public BuscaAvisoFaltaChecks getBuscaChecks() {
		return buscaChecks;
	}

	public void setBuscaChecks(BuscaAvisoFaltaChecks buscaChecks) {
		this.buscaChecks = buscaChecks;
	}

	public Collection<SelectItem> getDocenteCombo() {
		return docenteCombo;
	}

	public void setDocenteCombo(Collection<SelectItem> docenteCombo) {
		this.docenteCombo = docenteCombo;
	}

	public Collection<SelectItem> getAulasCombo() {
		return aulasCombo;
	}

	public void setAulasCombo(Collection<SelectItem> aulasCombo) {
		this.aulasCombo = aulasCombo;
	}

	public List<CorpoDocenteDepartamento> getCorpoDocente() {
		return corpoDocente;
	}

	public List<DadosAvisoFalta> getResultado() {
		return resultado;
	}

	public void setResultado(List<DadosAvisoFalta> resultado) {
		this.resultado = resultado;
	}

	public void setCorpoDocente(List<CorpoDocenteDepartamento> corpoDocente) {
		this.corpoDocente = corpoDocente;
	}

	public List<SelectItem> getCorpoProgramaCombo() throws DAOException {
		if(isEmpty(corpoDocente))
			carregarCorpoDocente();
	
		return toSelectItems(corpoDocente, "id", "nome");
	}
	
	public List<AvisoFaltaDocente> getAvisosFalta() {
		return avisosFalta;
	}

	public void setAvisosFalta(List<AvisoFaltaDocente> avisosFalta) {
		this.avisosFalta = avisosFalta;
	}

	public Collection<SelectItem> getDepartamentos() {
		return toSelectItems(departamentos, "id", "nome");
	}

	public class BuscaAvisoFaltaCampos implements Serializable {
		private Integer ano;
		private Integer periodo;
		private CorpoDocenteDepartamento docente = new CorpoDocenteDepartamento();
		private Unidade unidade = new Unidade( getAcessoMenu().isChefeDepartamento() ? getServidorUsuario().getUnidade().getId() : 0 );
		private int departamento;
		private int centro;
		
		public Integer getAno() {
			return ano;
		}
		public void setAno(Integer ano) {
			this.ano = ano;
		}
		public Integer getPeriodo() {
			return periodo;
		}
		public void setPeriodo(Integer periodo) {
			this.periodo = periodo;
		}
		public CorpoDocenteDepartamento getDocente() {
			return docente;
		}
		public void setDocente(CorpoDocenteDepartamento docente) {
			this.docente = docente;
		}
		public Unidade getUnidade() {
			return unidade;
		}
		public void setUnidade(Unidade unidade) {
			this.unidade = unidade;
		}
		public int getDepartamento() {
			return departamento;
		}
		public void setDepartamento(int departamento) {
			this.departamento = departamento;
		}
		public int getCentro() {
			return centro;
		}
		public void setCentro(int centro) {
			this.centro = centro;
		}
	}
	
	public class BuscaAvisoFaltaChecks implements Serializable {
		private boolean checkAnoPeriodo;
		private boolean checkDocente;
		private boolean checkUnidade;
		private boolean checkDepartamento;
		private boolean checkCentro;
	
		public boolean isCheckAnoPeriodo() {
			return checkAnoPeriodo;
		}
		public void setCheckAnoPeriodo(boolean checkAnoPeriodo) {
			this.checkAnoPeriodo = checkAnoPeriodo;
		}
		public boolean isCheckDocente() {
			return checkDocente;
		}
		public void setCheckDocente(boolean checkDocente) {
			this.checkDocente = checkDocente;
		}
		public boolean isCheckUnidade() {
			return checkUnidade;
		}
		public void setCheckUnidade(boolean checkUnidade) {
			this.checkUnidade = checkUnidade;
		}
		public boolean isCheckDepartamento() {
			return checkDepartamento;
		}
		public void setCheckDepartamento(boolean checkDepartamento) {
			this.checkDepartamento = checkDepartamento;
		}
		public boolean isCheckCentro() {
			return checkCentro;
		}
		public void setCheckCentro(boolean checkCentro) {
			this.checkCentro = checkCentro;
		}
	}
	
	public class CorpoDocenteDepartamento implements Comparable<CorpoDocenteDepartamento>, Serializable {
		
		private int id;
		private Servidor docente = null;
		private DocenteExterno docenteExterno = null;
		
		/**
		 * Nome do docente
		 * @return
		 */
		public String getNome() {
			return isServidor() ? docente.getNome() : docenteExterno.getNome();
		}
		
		public boolean isServidor() {
			return docente != null && docente.getId() != 0;
		}		
		
		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public Servidor getDocente() {
			return docente;
		}
		public void setDocente(Servidor docente) {
			this.docente = docente;
		}
		public DocenteExterno getDocenteExterno() {
			return docenteExterno;
		}
		public void setDocenteExterno(DocenteExterno docenteExterno) {
			this.docenteExterno = docenteExterno;
		}

		@Override
		public int compareTo(CorpoDocenteDepartamento o) {
			return getNome().compareTo(o.getNome());
		}
		
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + id;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CorpoDocenteDepartamento other = (CorpoDocenteDepartamento) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (id != other.id)
				return false;
			return true;
		}

		public int getIdProfessor() {
			if (isServidor())
				return docente.getId();
			else
				return docenteExterno.getId();
		}

		private AvisoFaltaDocenteMBean getOuterType() {
			return AvisoFaltaDocenteMBean.this;
		}
	}
	
	/** 
	 * Método responsável por retornar o nome do centro por meio do secretaria ou gestora acadêmica.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/aviso_falta/docente/busca.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public String getNomeCentro() {
		String nome = null;
		
		try {
			nome = getAcessoMenu().getSecretariaCentro().getNome();
		} catch(NullPointerException ex) {
				nome = getUsuarioLogado().getVinculoAtivo().getUnidade().getGestora().getNome();
		}
		
		return nome;
	}
	
	public boolean isPossuiDatasDeAulas(){
		return aulasCombo.size() > 0;
	}

	public void setIdTurma(Integer idTurma) {
		this.idTurma = idTurma;
	}

	public Integer getIdTurma() {
		return idTurma;
	}

	public String getDataAulaStr() {
		return dataAulaStr;
	}

	public void setDataAulaStr(String dataAulaStr) {
		this.dataAulaStr = dataAulaStr;
	}
}