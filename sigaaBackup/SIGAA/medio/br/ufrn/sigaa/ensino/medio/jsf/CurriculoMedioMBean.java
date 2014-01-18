/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 02/06/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.UnidadeTempo;
import br.ufrn.sigaa.ensino.medio.dao.CurriculoMedioDao;
import br.ufrn.sigaa.ensino.medio.dao.SerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.CurriculoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.CurriculoMedioComponente;
import br.ufrn.sigaa.ensino.medio.dominio.CursoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;

/**
 * MBean responsável pelo controle das operações referentes ao currículo de ensino médio.
 * 
 * @author Rafael Gomes
 *
 */
@Component("curriculoMedio") @Scope("session")
public class CurriculoMedioMBean extends SigaaAbstractController<CurriculoMedio>{
	
	/** Filtro da busca pelo Código do Currículo */
	private boolean filtroCodigo;
	/** Filtro da busca pelo Curso */
	private boolean filtroCurso;
	/** Filtro da busca por Série */
	private boolean filtroSerie;
	/** Collection que irá armazenar a listagem dos Currículos de Ensino Médio. */
	private Collection<CurriculoMedio> listaCurriculos= new ArrayList<CurriculoMedio>(); 
	/** Objeto de relacionamento entre Currículo de Ensino Médio com uma Disciplina */
	private CurriculoMedioComponente curriculoDisciplina;
	/** Armazena uma coleção de Currículo Disciplina */
	private Collection<CurriculoMedioComponente> curriculoDisciplinas = new ArrayList<CurriculoMedioComponente>();
	/** Lista das séries por curso de ensino médio. */
	private List<SelectItem> seriesByCurso = new ArrayList<SelectItem>(0);
	/** atributos utilizados para realizar a exibição das carga horárias preenchidas e restantes do currículo.*/
	private int chPreenchida,chRestante ;
	
	/**
	 * Construtor padrão
	 */
	public CurriculoMedioMBean() {
		initObj();
	}
	
	/** Inicializando das variáveis utilizadas */
	private void initObj(){
		obj = new CurriculoMedio();
		obj.setSerie(new Serie());
		obj.setCursoMedio(new CursoMedio());
		curriculoDisciplina = new CurriculoMedioComponente();
		curriculoDisciplinas = new ArrayList<CurriculoMedioComponente>();
		chPreenchida = 0;
		chRestante = 0;
	}
	
	/**
	 * Diretório que se encontra as view's
	 * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Método não invocado por JSP</li>
	 * </ul>
	 */
	@Override
	public String getDirBase() {
		return "/medio/curriculo";
	}
	
	/**
	 * Redireciona para o formulário que mostra informações resumidas do
	 * currículo.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Nenhuma</li>
	 * </ul>
	 */
	public String telaResumo() {
		return forward(getDirBase() + "/resumo.jsf");
	}
	

	/**
	 * Redireciona para o usuário para a tela de formulário,
	 * quando a operação voltar é solicitada.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/medio/curriculo/resumo.jsp</li>
	 * </ul>
	 */
	public String voltar() {
		return forward(getFormPage());
	}
	
	@Override
	public String listar() throws ArqException {
		initObj();
		return super.listar();
	}
	
	@Override
	public String cancelar() {
		initObj();
		return super.cancelar();
	}
	
	/**
	 * Metodo que redireciona o usuário para a tela de detalhes do currículo.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/medio/curriculo/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String view() throws ArqException {
		setId();
		setObj(getGenericDAO().findByPrimaryKey(obj.getId(), CurriculoMedio.class));
		carregarSeriesByCurso();
		curriculoDisciplinas.clear();
		curriculoDisciplinas.addAll(obj.getCurriculoComponentes());
		return forward(getViewPage());
	}
	
	@Override
	public String atualizar() throws ArqException {
		checkChangeRole();
		setId();
		setObj(getGenericDAO().findByPrimaryKey(obj.getId(), CurriculoMedio.class));
		carregarSeriesByCurso();
		
		curriculoDisciplinas.clear();
		curriculoDisciplinas.addAll(obj.getCurriculoComponentes());
		
		setConfirmButton("Alterar");
		setOperacaoAtiva(SigaaListaComando.ALTERAR_CURRICULO_MEDIO.getId());
		prepareMovimento(SigaaListaComando.ALTERAR_CURRICULO_MEDIO);
		return forward(getFormPage());
	}
	
	/**
	 * Método responsável por Setar a operação ativa.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/medio/menus/curso.jsp</li>
	 * </ul>
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		initObj();
		checkChangeRole();
		setConfirmButton("Cadastrar");
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_CURRICULO_MEDIO.getId());
		prepareMovimento(SigaaListaComando.CADASTRAR_CURRICULO_MEDIO);
		return super.preCadastrar();
	}
	
	/**
	 * Método responsável por carregar  e enviar os dados do currículo em cadastro ou alteração para a tela de confirmação.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/medio/curriculo/form.jsp</li>
	 * </ul>
	 */
	public String submeterCurriculo() throws DAOException{
		
		erros.addAll(obj.validate());
		if (hasErrors()) return null;

		CurriculoMedioDao cmDao = getDAO(CurriculoMedioDao.class);
		
		// Verifica se existe outro currículo ativo cadastrado com mesmo código, série e curso
		if ( cmDao.existeCurriculoAtivoByCodigoSerieCurso(obj) )
			addMensagemErro("Favor Informar outro código, pois já existe Estrutura Curricular ativa com o código informado.");
		
		if (hasErrors()) return null;
		
		if ( obj.getId() == 0 )
			popularDadosCurriculo();
		
		
		//Remoção da collection das disciplinas removidas do currículo.
		for (CurriculoMedioComponente cmc : obj.getCurriculoComponentes()) {
			if (!curriculoDisciplinas.contains(cmc)) {
				getGenericDAO().remove(cmc);
			}
		}
		if (!obj.getCurriculoComponentes().isEmpty()) {
			for (CurriculoMedioComponente cm : curriculoDisciplinas) {
				if (!obj.getCurriculoComponentes().contains(cm)) 
					getGenericDAO().remove(cm);
			}
			obj.getCurriculoComponentes().clear();
		}
		if ( curriculoDisciplinas.isEmpty() )
			addMensagemErro("Não é possível "+getConfirmButton()+" Currículo sem haver disciplinas vinculadas ao mesmo.");	
		
		if (this.chPreenchida > obj.getCargaHoraria()) 
			addMensagemErro("A Carga horária das Disciplinas adicionadas não pode  ser maior do que a " +
					"carga horária total do currículo.");
		
		for (CurriculoMedioComponente disciplina : curriculoDisciplinas) {
			if (disciplina.getChAno() == null){
				addMensagemErro("Existe(m) Disciplina(s) sem o valor da Carga Horária Anual informado.");
				break;
			}	
		}
		
		if (hasErrors()) return null;
		
		obj.getCurriculoComponentes().addAll(curriculoDisciplinas);
		
		if( obj.getId() > 0 ){
			setConfirmButton("Alterar");
		}else{
			setConfirmButton("Cadastrar");
		}
		
		return telaResumo();
	}
	
	/**
	 * Método responsável por carregar os dados básicos do curículo para serem exibidos na tela de resumo.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>NENHUMA</li>
	 * </ul>
	 */
	private void popularDadosCurriculo() throws DAOException{
		GenericDAO dao = getGenericDAO();
		
		obj.setCursoMedio(dao.findByPrimaryKey(obj.getCursoMedio().getId(), CursoMedio.class, "id","nome"));
		obj.setSerie(dao.findByPrimaryKey(obj.getSerie().getId(), Serie.class, "id","descricao","numero"));
		obj.setUnidadeTempo(dao.findByPrimaryKey(obj.getUnidadeTempo().getId(), UnidadeTempo.class, "id","descricao"));
		
	}
	
	/**
	 * Método responsável pelo cadastro de um Currículo de Ensino Médio.
	 * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/curriculo/form.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		checkChangeRole();
		
		if (!confirmaSenha())
			return null;
			
		if (isOperacaoAtiva(SigaaListaComando.INATIVAR_ATIVAR_CURRICULO_MEDIO.getId())) {
			return inativarOuAtivarCurriculo();
		}		
		
		Comando comando = SigaaListaComando.CADASTRAR_CURRICULO_MEDIO;
		if (obj.getId() != 0)
			comando = SigaaListaComando.ALTERAR_CURRICULO_MEDIO;
		
	
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(comando);
		mov.setObjMovimentado(obj);
		
		try {
			execute(mov);
			if (comando.equals(SigaaListaComando.CADASTRAR_CURRICULO_MEDIO)) {
				addMessage("Estrutura Curricular cadastrada com sucesso!",
						TipoMensagemUFRN.INFORMATION);
			} else {
				addMessage("Estrutura Curricular alterada com sucesso!",
						TipoMensagemUFRN.INFORMATION);
			}
			redirectJSF(getSubSistema().getLink());
			return null;
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		} finally {
			initObj();
			removeOperacaoAtiva();
		}
	}
	
	/**
	 * Popula o currículo que vai sofrer alteração de situação para Ativo ou Inativo. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/medio/curriculo/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String preInativarOuAtivar() throws ArqException {
		setOperacaoAtiva(SigaaListaComando.INATIVAR_ATIVAR_CURRICULO_MEDIO.getId());
		prepareMovimento(SigaaListaComando.INATIVAR_ATIVAR_CURRICULO_MEDIO);
		setReadOnly(true);
		obj = new CurriculoMedio();
		setId();
		obj = getGenericDAO().refresh(obj);
		
		if (obj == null) {
			addMensagemErro("Registro não foi localizado.");
			return null;
		}
		
		if( obj.isAtivo() ){
			setConfirmButton("Inativar");
		}else{
			setConfirmButton("Ativar");
		}
		
		return telaResumo();
	}
	
	/**
	 * Coloca o currículo como inativo, não exclui. Não se
	 * deve cadastrar discentes em currículos desativados.
	 * 
	 * @return
	 * @throws ArqException
	 */
	private String inativarOuAtivarCurriculo() throws ArqException {

		if( obj.isAtivo() ){
			obj.setAtivo(false);
		}else{
			obj.setAtivo(true);
		}
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.INATIVAR_ATIVAR_CURRICULO_MEDIO);
		mov.setObjMovimentado(obj);
		try {
			execute(mov);
			addMensagemInformation("Currículo foi "+ (obj.isAtivo() ? "a" : "ina")+"tivado com sucesso.");
		} catch (NegocioException e) {
			tratamentoErroPadrao(e, "Não foi possivel "+ (obj.isAtivo() ? "a" : "ina")+"tivar este curriculo.");
			return null;
		}

		return cancelar();
	}
	
	/**
	 * Efetua a remoção lógica do Currículo de ensino Médio, desativando-o.
	 * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/curriculo/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String remover() throws ArqException {
		checkChangeRole();
		setId();
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), CurriculoMedio.class);
		if (obj == null) {
			addMensagemErro("O objeto selecionado não existe mais.");
			return redirectJSF(getSubSistema().getLink());
		}
		prepareMovimento(SigaaListaComando.REMOVER_CURRICULO_MEDIO);
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.REMOVER_CURRICULO_MEDIO);
		mov.setObjMovimentado(obj);
		try {
			execute(mov);
			addMensagemInformation("Estrutura Curricular Removida com sucesso.");
		} catch (NegocioException e) {
			tratamentoErroPadrao(e, "Não foi possivel Remover essa Estrutura Curricular.");
			return null;
		}

		return listar();
	}
	
	/**
	 * Método responsável pela busca de currículos de ensino Médio.
	 * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/curriculo/lista.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String buscar() throws ArqException {
		
		CurriculoMedioDao dao = getDAO(CurriculoMedioDao.class);
		
		if (filtroCodigo && isEmpty(obj.getCodigo())) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Código");
		
		if (filtroCurso && isEmpty(obj.getCursoMedio())) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Curso");
		
		if (filtroSerie && isEmpty(obj.getSerie())) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Série");
		
		if ( !filtroCurso ) obj.setCursoMedio(new CursoMedio());
		if ( !filtroSerie ) obj.setSerie(new Serie());
		
		if (hasOnlyErrors()) {
			listaCurriculos.clear();
			return null;
		}
	
		listaCurriculos = dao.findByCursoOrSerie(obj.getCursoMedio(), obj.getSerie(), obj.getCodigo(), null);
		if (listaCurriculos.isEmpty()) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		return forward(getListPage());
	}
	
	/** 
	 * Carrega as séries pertencentes ao curso de ensino médio selecionado na jsp..
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/medio/curriculo/form.jsp</li>
	 * </ul>
 	 * @param e
	 * @throws DAOException
	 */
	public void carregarSeriesByCurso(ValueChangeEvent e) throws DAOException {
		SerieDao dao = getDAO( SerieDao.class );
		
		CursoMedio cursoMedio = null;
		
		if( e != null && (Integer)e.getNewValue() > 0 )
			cursoMedio = dao.findByPrimaryKey((Integer)e.getNewValue(), CursoMedio.class);
		else {
			seriesByCurso = new ArrayList<SelectItem>(0);
			return;
		}	
		
		cursoMedio.setNivel(getNivelEnsino());
		if (cursoMedio != null)
			seriesByCurso = toSelectItems(dao.findByCurso(cursoMedio), "id", "descricaoCompleta");
	}
	
	/** 
	 * Carrega as séries pertencentes ao curso de ensino médio baseado no curso do objeto em questão.
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/medio/curriculo/form.jsp</li>
	 * </ul>
 	 * @param e
	 * @throws DAOException
	 */
	public void carregarSeriesByCurso() throws DAOException {
		SerieDao dao = getDAO( SerieDao.class );
		
		if( isNotEmpty(obj.getCursoMedio()) )
			seriesByCurso = toSelectItems(dao.findByCurso(obj.getCursoMedio()), "id", "descricaoCompleta");
		else {
			seriesByCurso = new ArrayList<SelectItem>(0);
			return;
		}	
	}
	
	/**
	 * Seleciona a disciplina escolhida pelo o usuário.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/medio/curriculo/form.js</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void selecionarDisciplina(ActionEvent e) throws DAOException {
		ComponenteCurricular componente = (ComponenteCurricular) e.getComponent().getAttributes().get("componente");
		if ( isNotEmpty(componente) ){
			curriculoDisciplina.setComponente( getGenericDAO().refresh(componente) );
		}	
	}
	
	/**
	 * Adiciona uma nova disciplina na lista de disciplina do Currículo.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/medio/curriculo/form.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public void adicionarDisciplina() throws DAOException {
		if (curriculoDisciplina.getComponente().getId() != 0) {
			curriculoDisciplina.setComponente((getGenericDAO().findByPrimaryKey(curriculoDisciplina.getComponente().getId(), ComponenteCurricular.class)));
			curriculoDisciplina.setCurriculoMedio(obj);
			curriculoDisciplina.setChAno(curriculoDisciplina.getComponente().getChTotal());
			curriculoDisciplina.setRegistroEntrada(getRegistroEntrada());
			curriculoDisciplina.setDataCadastro(new Date());
			
			addMensagens(obj.validate());
			
			if (curriculoDisciplinas.contains(curriculoDisciplina)) 
				addMensagemErro("A disciplina já está inserida no Currículo.");
			
			if (!hasOnlyErrors()) {
				this.curriculoDisciplinas.add(curriculoDisciplina);
				obj.getCurriculoComponentes().add(curriculoDisciplina);
				curriculoDisciplina = new CurriculoMedioComponente();
				curriculoDisciplina.setComponente(new ComponenteCurricular());
			}
		} else {
			addMensagemErro("Deve-se preencher um nome de disciplina válido e aguardar " +
					"que a disciplina seja exibida na lista de sugestões para a seleção.");
		}
	}
	
	/**
	 * Remoção de uma disciplina de um currículo.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/medio/curriculo/form.jsp</li>
	 * </ul>
	 */
	public void removerDisciplina(){
		int idDisciplina = getParameterInt("id");
		if (curriculoDisciplinas.size() > 1){
			for (CurriculoMedioComponente md : curriculoDisciplinas) {
				if (idDisciplina == md.getComponente().getId()) {
					curriculoDisciplinas.remove(md);
					obj.getDisciplinas().remove(md);
					break;
				}
			}
		} else {
			addMensagemErro("Não é possível manter uma Estrutura Curricular sem Disciplinas.");
			return;
		}
	}
	
	/**
	 * Retorna a carga horária total já adiciona ao currículo.
	 * 
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/medio/curriculo/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public void calcularChTotal(ActionEvent e){
		int totalPreenchida = 0;
		for (CurriculoMedioComponente cmc : curriculoDisciplinas) {
			totalPreenchida += cmc.getChAno() != null ? cmc.getChAno() : 0;
		}	
		this.chPreenchida = totalPreenchida;
		this.chRestante = obj.getCargaHoraria() - totalPreenchida;
	}
	
	/**
	 * Carrega as unidades de tempo cadastradas.
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *  <br>
	 * 		<li>/sigaa.war/medio/curriculo/form.jsp</li>
	 * </ul>
     *
     * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllUnidadeTempo() throws DAOException{
		return toSelectItems(getDAO(CursoDao.class).findAll(UnidadeTempo.class), "id", "descricao");
	}
	
	public String getLabelCombo() {
		return "codigo";
	}
	
	/**
	 * Verifica se o usuário pode alterar/remover a disciplina
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/medio/curriculo/lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isPodeAlterar() {
		return isUserInRole(SigaaPapeis.GESTOR_MEDIO);
	}
	
	
	/** Verifica os papéis: GESTOR_MEDIO, COORDENADOR_MEDIO.
	 * 
	 * Método responsável por carregar  e enviar os dados do currículo em cadastro ou alteração para a tela de confirmação.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>NENHUMA</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO);
	}
	
	
	// Getters and Setters
	
	/**  the filtroCodigo */
	public boolean isFiltroCodigo() {
		return filtroCodigo;
	}

	/**  filtroCodigo the filtroCodigo to set */
	public void setFiltroCodigo(boolean filtroCodigo) {
		this.filtroCodigo = filtroCodigo;
	}

	/**  the filtroCurso */
	public boolean isFiltroCurso() {
		return filtroCurso;
	}

	/**  filtroCurso the filtroCurso to set */
	public void setFiltroCurso(boolean filtroCurso) {
		this.filtroCurso = filtroCurso;
	}

	/**  the filtroSerie */
	public boolean isFiltroSerie() {
		return filtroSerie;
	}

	/**  filtroSerie the filtroSerie to set */
	public void setFiltroSerie(boolean filtroSerie) {
		this.filtroSerie = filtroSerie;
	}

	/**  the listaCurriculos */
	public Collection<CurriculoMedio> getListaCurriculos() {
		return listaCurriculos;
	}

	/**  listaCurriculos the listaCurriculos to set */
	public void setListaCurriculos(Collection<CurriculoMedio> listaCurriculos) {
		this.listaCurriculos = listaCurriculos;
	}
	
	/**  the curriculoDisciplina */
	public CurriculoMedioComponente getCurriculoDisciplina() {
		return curriculoDisciplina;
	}

	/**  curriculoDisciplina the curriculoDisciplina to set */
	public void setCurriculoDisciplina(CurriculoMedioComponente curriculoDisciplina) {
		this.curriculoDisciplina = curriculoDisciplina;
	}

	/**  the curriculoDisciplinas */
	public Collection<CurriculoMedioComponente> getCurriculoDisciplinas() {
		return this.curriculoDisciplinas;
	}

	/**  curriculoDisciplinas the curriculoDisciplinas to set */
	public void setCurriculoDisciplinas(
			Collection<CurriculoMedioComponente> curriculoDisciplinas) {
		this.curriculoDisciplinas = curriculoDisciplinas;
	}

	/**  the seriesByCurso */
	public List<SelectItem> getSeriesByCurso() {
		return seriesByCurso;
	}

	/**  seriesByCurso the seriesByCurso to set */
	public void setSeriesByCurso(List<SelectItem> seriesByCurso) {
		this.seriesByCurso = seriesByCurso;
	}

	/**  the chPreenchida */
	public int getChPreenchida() {
		int total = 0;
		for (CurriculoMedioComponente cmc : curriculoDisciplinas) {
			total += cmc.getChAno() != null ? cmc.getChAno() : 0;
		}		
		return total;
	}

	/**  chPreenchida the chPreenchida to set */
	public void setChPreenchida(int chPreenchida) {
		this.chPreenchida = chPreenchida;
	}

	/**  the chRestante */
	public int getChRestante() {
		this.chRestante = obj.getCargaHoraria() != null ? obj.getCargaHoraria() - getChPreenchida() : 0;
		return chRestante;
	}

	/** chRestante the chRestante to set */
	public void setChRestante(int chRestante) {
		this.chRestante = chRestante;
	}

}