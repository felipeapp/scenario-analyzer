/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '20/11/2007'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.prodocente.CHDedicadaResidenciaMedicaDao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.CHDedicadaResidenciaMedica;
import br.ufrn.sigaa.prodocente.atividades.dominio.ProgramaResidenciaMedica;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

/**
 * MBean para gerenciar {@link CHDedicadaResidenciaMedica}.
 *
 * @author Ricardo Wendell
 *
 */
public class CHDedicadaResidenciaMedicaMBean extends AbstractControllerAtividades<CHDedicadaResidenciaMedica> {

	// Constantes das Views 
	/** Link para a lista de CH dedicada de residência médica. **/
	private final String JSP_RELATORIO = "/complexo_hospitalar/ResidenciaMedica/list.jsf";
	/** Link para a busca de CH dedicada de residência médica. **/
	private final String JSP_BUSCA = "/complexo_hospitalar/ResidenciaMedica/busca.jsf";
	
	/** Indica se restringe a busca por ano. */
	private boolean checkBuscaAno =  false;
	/** Indica se restringe a busca por programa. */
	private boolean checkBuscaPrograma =  false;
	/** Indica se o resultado da busca será no formato de relatório. */
	private boolean checkBuscaRelatorio =  false;
	
	/** Dados para o relatório da busca por residência médica. */
	private List<HashMap<String, Object>> lista = new ArrayList<HashMap<String, Object>>();
	
	/** Ano referente à carga horária. */
	private int ano = CalendarUtils.getAnoAtual();
	
	/** Período referente à carga horária. */
	private int periodo = getPeriodoAtual();
	
	/** ID do programa de residência médica. */
	private int id=0;
	
	/** Total de elementos do resultado da busca.  */
	private int total=0;
	
	/** Lista de programas de residência médica. */
	private String[] programa;
	
	/** Nome do programa de residência médica.*/
	private String nome;

	/** Construtor padrão. */
	public CHDedicadaResidenciaMedicaMBean() {
		clear();
	}

	/** Inicializa os atributos do controler. */
	private void clear() {
		this.obj = new CHDedicadaResidenciaMedica();
		this.obj.setServidor( new Servidor() );
		this.obj.setProgramaResidenciaMedica( new ProgramaResidenciaMedica() );
		this.obj.setAno( getCalendarioVigente().getAno() );
		this.obj.setSemestre( getCalendarioVigente().getPeriodo() );
		checkBuscaAno = checkBuscaPrograma = checkBuscaRelatorio =  false;
		lista = new ArrayList<HashMap<String, Object>>();
		total = lista.size();
	}

	/**
	 * Usado quando acessado do subsistema Complexo_hospitalar
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/complexo_hospitalar/index.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String direcionar() throws SegurancaException{
		clear();
		return forward("/complexo_hospitalar/ResidenciaMedica/form.jsf");
	}

	/**
 	 * Usado quando acessado do subsistema Complexo_hospitalar
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/complexo_hospitalar/index.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String telaBusca() throws SegurancaException{
		clear();
		return forward("/complexo_hospitalar/ResidenciaMedica/busca.jsf");
	}

	/**
	 * Usado quando acessado do subsistema Complexo_hospitalar
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/complexo_hospitalar/index.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String listagem() throws SegurancaException{
		return forward("/complexo_hospitalar/ResidenciaMedica/lista.jsf");
	}

	/**
	 * Usada quando se quer visualizar detalhes da residência Médica.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/complexo_hospitalar/index.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String visualizar() throws DAOException, SegurancaException{
		id = getParameterInt("id");
		CHDedicadaResidenciaMedicaDao dao = getDAO(CHDedicadaResidenciaMedicaDao.class);
		lista = dao.findResidenciaMedicaDetalhada(id);
		return listagem();
	}
	
	/**
	 * Usada para fazer uma busca pelos servidores
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/complexo_hospitalar/ResidenciaMedica/busca.jsp</li>
	 *	</ul>
	 *
	 * @return
	 */
	public String getEscopoBuscaServidores() {
		if (isUserInRole(SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, SigaaPapeis.SECRETARIA_RESIDENCIA)){
			return "todos";
		} else {
			return "D";
		} 
	}

	/** Retorna o link para o formulário de busca. 
	 * <br/> Método não invocado por JSP´s
	 * @return
	 */
	public String getbusca() {
		return getDirBase() + "/busca.jsf";
	}
	
    /**
	* De acordo como subsistema o getDirBase assume um valor diferente.
	* <br/> Método não invocado por JSP´s
    */
	@Override
	public String getDirBase() {
		String str;
		if (getSubSistema().getNome().equals("Portal do Docente")) {
			str = "/prodocente/atividades/ResidenciaMedica";
		}else {
			str = "/complexo_hospitalar/ResidenciaMedica";
		}
		return str;
	}
	
	/** Atualiza a CH dedicada da residência médica.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/complexo_hospitalar/ResidenciaMedica/form.jsp</li>
	 *	</ul>
	 * @see br.ufrn.sigaa.prodocente.producao.jsf.AbstractControllerProdocente#atualizar()
	 */
	@Override
	public String atualizar() throws ArqException {
		super.atualizar();
		return getbusca(); 
	}

	/**
	 * Serve para direcionar logo após o cadastro para o diretório específico.
	 * <br/> Método não invocado por JSP´s 
	 */
	@Override
	public String forwardCadastrar() {
		if(getUltimoComando() == ArqListaComando.ALTERAR)
			return getDirBase() + "/busca.jsf";
		else
			return getDirBase() + "/form.jsf";
	}
		
	/** Remove a CH dedicada da residência médica.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/complexo_hospitalar/ResidenciaMedica/lista.jsp</li>
	 *	</ul>
	 * @see br.ufrn.sigaa.prodocente.producao.jsf.AbstractControllerProdocente#remover()
	 */
	@Override
	public String remover(){
		setId();
		try {
			obj = getGenericDAO().findByPrimaryKey(obj.getId(), obj.getClass());
		} catch (DAOException e) {
			e.printStackTrace();
		}
		
		if (obj == null || obj.getId() == 0) {
			addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
			return forward(JSP_BUSCA);
		} else {
			try {
				prepareMovimento(ArqListaComando.DESATIVAR);
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setObjMovimentado(obj);
				mov.setCodMovimento(ArqListaComando.DESATIVAR);
				
				execute(mov);
				
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			} catch (ArqException e) {
				e.printStackTrace();
			} catch (NegocioException e) {
				e.printStackTrace();
			}
		}
		afterRemover();
		return forward(JSP_BUSCA);
	}
	
	/** Atualiza a busca após remover a CH de residência médica.
	 * <br/> Método não invocado por JSP´s
	 * @see br.ufrn.sigaa.prodocente.producao.jsf.AbstractControllerProdocente#afterRemover()
	 */
	@Override
	public void afterRemover(){
		try {
			relatorio();
		} catch (ArqException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Serve para determinar qual o relatório selecionado pelo usuário.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/complexo_hospitalar/busca.jsp</li>
	 *	</ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String relatorio() throws ArqException {
		if (!checkBuscaAno && !checkBuscaPrograma) {
			addMensagemErro("Por favor, escolha algum critério de busca.");
		}
		if (checkBuscaAno && checkBuscaPrograma) {
			id = obj.getProgramaResidenciaMedica().getId();
			CHDedicadaResidenciaMedicaDao dao = getDAO(CHDedicadaResidenciaMedicaDao.class);
			programa = dao.findNome(id);
			nome = programa[0];
			relatorioResidenciaMedica();
			total = lista.size();
			return null;
		}
		if (checkBuscaAno) {
			relatorioResidenciaMedicaPorPeriodo();
			total = lista.size();
			return null;
		}
		if (checkBuscaPrograma) {
			id = obj.getProgramaResidenciaMedica().getId();
			CHDedicadaResidenciaMedicaDao dao = getDAO(CHDedicadaResidenciaMedicaDao.class);
			programa = dao.findNome(id);
			nome = programa[0];
			relatorioResidenciaMedicaPorPrograma();
			total = lista.size();
			return null;
		}
		return null;
	}
	
	/**
	 * Faz a busca pelo programa, ano e período.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/complexo_hospitalar/busca.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String relatorioResidenciaMedica() throws DAOException, ArqException {
		CHDedicadaResidenciaMedicaDao dao = getDAO(CHDedicadaResidenciaMedicaDao.class);
		lista = dao.findResidenciaMedica(id,ano, periodo);
		if (checkBuscaRelatorio) {
			return forward(JSP_RELATORIO); 
		}else{
			return getbusca();
		}
	}

	/**
	 * Faz a busca pelo ano e período.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/complexo_hospitalar/busca.jsp</li>
	 *	</ul>
	 *
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String relatorioResidenciaMedicaPorPeriodo() throws DAOException, ArqException {
		CHDedicadaResidenciaMedicaDao dao = getDAO(CHDedicadaResidenciaMedicaDao.class);
		lista = dao.findResidenciaMedicaPorAno(ano, periodo);
		if (checkBuscaRelatorio) {
			return forward(JSP_RELATORIO); 
		}else{
			return getbusca();
		}
	}

	/**
	 * Faz a busca pelo programa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/complexo_hospitalar/busca.jsp</li>
	 *	</ul>
	 *
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String relatorioResidenciaMedicaPorPrograma() throws DAOException, ArqException {
		CHDedicadaResidenciaMedicaDao dao = getDAO(CHDedicadaResidenciaMedicaDao.class);
		lista = dao.findResidenciaMedicaPorPrograma(id);
		if (checkBuscaRelatorio) {
			return forward(JSP_RELATORIO); 
		}else{
			return getbusca();
		}
	}

	/**
	 * Serve para inativar o usuário passando o ativo para false. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/complexo_hospitalar/ResidenciaMedica/lista.jsp</li>
	 *	</ul>
	 */
	@Override
	public String inativar() throws ArqException, NegocioException {
		Integer id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, CHDedicadaResidenciaMedica.class);
		return super.inativar();
	}
	
	/** Inicia o cadastro da CH dedicada da residência médica.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/complexo_hospitalar/index.jsp</li>
	 *	</ul>
	 * @see br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades#preCadastrar()
	 */
	@Override
	public String preCadastrar() {
		clear();
		try {
			prepareMovimento(ArqListaComando.CADASTRAR);
		} catch (ArqException e) {
			e.printStackTrace();
		}
		setConfirmButton("Cadastrar");
		return forward(getFormPage());
	}

	/**
	 * Testa se o período está dentro dos padrões da UFRN, ou seja, o maior perído possível
	 * na intituição é o 4.  <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/complexo_hospitalar/ResidenciaMedica/form.jsp</li>
	 *	</ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		if (obj.getSemestre() > 4) {
			addMensagemErro("Informe um período válido.");
			return null;
		}
		return super.cadastrar();
	}
	
	/**
	 * Serve para redirecionar o usuário para a tela de busca, de acordo com o seu gerDirBase.
	 * <br/> Método não invocado por JSP´s
	 */
	@Override
	public String getUrlRedirecRemover() {
		if (getSubSistema().getNome().equals("Portal do Docente")) {
			return "prodocente/atividades/ResidenciaMedica/lista.jsf";
		}else {
			return "/sigaa" + getDirBase() + "/busca.jsf";	
		}
	}

    /**
	* Limpa os dados logo após o cadastro.
	* <br/> Método não invocado por JSP´s
	*/
	@Override
	protected void afterCadastrar() {
		if(getUltimoComando() == ArqListaComando.ALTERAR){
			try {
				relatorio();
			} catch (ArqException e) {
				e.printStackTrace();
			}
		}
		else
			clear();
	}

	/**
	 * Serve pra visualização de todas as informações contidas na CHDedicadaResidenciaMedica.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/atividades/residenciaMedica/lista.jsp</li>
	 *	</ul>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public String detalhar() throws DAOException{
		id = getParameterInt("id");
		CHDedicadaResidenciaMedicaDao dao = getDAO(CHDedicadaResidenciaMedicaDao.class);
		lista = dao.detalhes(id);
		return forward("/prodocente/atividades/ResidenciaMedica/view.jsf");
	}
	
	/**
	 * Quando cancelar o mesmo será redirecionado para a tela principal do complexo_hospitalar, desde
	 * que o mesmo tenha vindo de lá.
	 * <br/> Método não invocado por JSP´s
	 * @return
	 */
	public String cancelamento(){
		if(getUltimoComando() == ArqListaComando.CADASTRAR || getUltimoComando() == null)
			return forward("/complexo_hospitalar/index.jsf");
		else if(getUltimoComando() == ArqListaComando.ALTERAR)
			return forward(getbusca());
		return null;
	}
	
	/**
	 * Nesse caso ele vai retornar para a tela principal do portal docente.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/complexo_hospitalar/ResidenciaMedica/form.jsp</li>
	 *	</ul>
	 */
	@Override
	public String cancelar() {
		
		if (getUsuarioLogado().isUserInRole(SigaaPapeis.SECRETARIA_POS , SigaaPapeis.PPG)) 
			return	redirectJSF(getSubSistema().getLink());

		if (validar != null && validar) {

			return forward("/portais/docente/docente.jsf");

		} else {

			if (getConfirmButton() != null) {
				if (getConfirmButton().equals("Alterar")
						|| getConfirmButton().equals("Remover")) {
					String url = "/sigaa/" + getListPage();
					url = url.substring(0, url.length() - 1);
					return redirectJSF(url + "f");

				} else if (getConfirmButton().equals("Cadastrar")) {
					return forward("/portais/docente/docente.jsf");
				}
			}
			return super.cancelar();
		}

	}
	
	/** Retorna uma coleção de selectItem de carga horária dedicada à residência médica.
	 * <br/> Método não invocado por JSP´s
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllCombo()
	 */
	public Collection<SelectItem> getAllCombo() throws DAOException {
		return toSelectItems(getGenericDAO().findAll(CHDedicadaResidenciaMedica.class, "observacoes", "asc"),
				"id", "servidor.pessoa.nome");
	}

	@Override
	public Collection<CHDedicadaResidenciaMedica> getAllPaginado() throws ArqException {
		return super.getAllPaginado();
	}

	public boolean isCheckBuscaAno() {
		return checkBuscaAno;
	}

	public void setCheckBuscaAno(boolean checkBuscaAno) {
		this.checkBuscaAno = checkBuscaAno;
	}

	public boolean isCheckBuscaPrograma() {
		return checkBuscaPrograma;
	}

	public void setCheckBuscaPrograma(boolean checkBuscaPrograma) {
		this.checkBuscaPrograma = checkBuscaPrograma;
	}

	public boolean isCheckBuscaRelatorio() {
		return checkBuscaRelatorio;
	}

	public void setCheckBuscaRelatorio(boolean checkBuscaRelatorio) {
		this.checkBuscaRelatorio = checkBuscaRelatorio;
	}

	public List<HashMap<String, Object>> getLista() {
		return lista;
	}

	public void setLista(List<HashMap<String, Object>> lista) {
		this.lista = lista;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String[] getPrograma() {
		return programa;
	}

	public void setPrograma(String[] programa) {
		this.programa = programa;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
	
}