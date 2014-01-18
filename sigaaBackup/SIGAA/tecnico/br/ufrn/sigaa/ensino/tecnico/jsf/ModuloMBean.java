package br.ufrn.sigaa.ensino.tecnico.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.tecnico.dao.EstruturaCurricularTecDao;
import br.ufrn.sigaa.ensino.tecnico.dao.ModuloDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.EstruturaCurricularTecnica;
import br.ufrn.sigaa.ensino.tecnico.dominio.Modulo;
import br.ufrn.sigaa.ensino.tecnico.dominio.ModuloDisciplina;
import br.ufrn.sigaa.ensino.tecnico.negocio.ModuloMov;

/**
 * Managed Bean responsável pelas operações básicas 
 * de Módulo Técnico Técnica.
 *  
 * @author guerethes
 */
@SuppressWarnings("serial")
@Component @Scope("request")
public class ModuloMBean extends SigaaAbstractController<Modulo> {

	/** Constante utilizada para auxiliar na busca por código  */
	private boolean filtroCodigo;
	/** Constante utilizada para auxiliar na busca por nome  */
	private boolean filtroNome;
	/** Constante utilizada para auxiliar na busca por curso  */
	private boolean filtroCurso;
	/**Descrição do modulo, usada na busca*/
	private String descricao;	
	/**Codigo do modulo, usado na busca*/
	private String codigo;
	/**Identificador do curso que o modulo está vinculado, usado na busca*/
	private Integer idCurso;
	
	/** E a represetação de um módulo com uma Disciplina */
	private ModuloDisciplina modulodisciplina;
	/** Armazena uma coleção de Modulo Disciplina */
	private Collection<ModuloDisciplina> moduloDisciplinas = new ArrayList<ModuloDisciplina>();
	/** Retorna uma coleção de Modulo */
	private Collection<Modulo> listaModulos = new ArrayList<Modulo>();
	/** Serve pra auxiliar na edição ou noa do nome do Módulo */
	private boolean bloquearEdicao;
	
	/**
	 * Construtor padrão
	 */
	public ModuloMBean() {
		init();
	}
	
	/**
	 * Inicializa os atributos que serão utilizados no decorrer dos Casos de Uso.
	 */
	private void init() {
		obj = new Modulo();
		modulodisciplina = new ModuloDisciplina();
		modulodisciplina.setDisciplina(new ComponenteCurricular());
		bloquearEdicao = false;
	}
	
	/**
	 * Adiciona uma nova disciplina na lista de disciplina do módulo.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/modulo/form.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public void adicionarDisciplina() throws DAOException, NegocioException {
		if(moduloDisciplinas.isEmpty() && obj.getDescricao() != null && !obj.getDescricao().equals("")){
			ModuloDao dao = getDAO(ModuloDao.class);
			
			Collection<Modulo> modulosMesmoNome = dao.findByCodCursoNome(obj.getDescricao(), null, null, getNivelEnsino(), getUsuarioLogado().getVinculoAtivo().getUnidade().getId());
			if(!modulosMesmoNome.isEmpty() && !obj.equals(modulosMesmoNome.iterator().next())){
				addMensagemErro("Já existe um módulo com a descrição " + obj.getDescricao()+".");
				return;
			}
				
		}
		if (modulodisciplina.getDisciplina().getId() == 0) {
			addMensagemErro("Disciplina: Campo obrigatório não informado.");
			return;
		}
			modulodisciplina.setDisciplina((getGenericDAO().findByPrimaryKey(modulodisciplina.getDisciplina().getId(), ComponenteCurricular.class)));
			modulodisciplina.setModulo(obj);
			obj.setModuloDisciplina(modulodisciplina);
			addMensagens(obj.validate());
			
			if (moduloDisciplinas.contains(modulodisciplina)) 
				addMensagemErro("A disciplina já está contida no Módulo.");
			
			if (obj.getCargaHoraria() != null && obj.getCargaHoraria() < getChTotal() + modulodisciplina.getDisciplina().getChTotal())  
				addMensagemErro("Não é possível adicionar a disciplina, pois ultrapassa a carga horária total do Módulo.");
			
			if (!hasOnlyErrors()) {
				moduloDisciplinas.add(modulodisciplina);
				obj.getModuloDisciplinas().add(modulodisciplina);
				modulodisciplina = new ModuloDisciplina();
				modulodisciplina.setDisciplina(new ComponenteCurricular());
				bloquearEdicao = true;
				modulodisciplina = new ModuloDisciplina();
				modulodisciplina.setDisciplina(new ComponenteCurricular());
			}
		
		
	}

	
	/**
	 * Utilizada na validação dos dados do Módulo
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Método não invocado por JSP.</li>
	 * </ul>
	 */
	@Override
	public boolean validacaoDados(ListaMensagens mensagens) {
		ListaMensagens lista = new ListaMensagens();
		//Validação da carga horária total do módulo.
		int chTotal = 0;
		for (ModuloDisciplina md : moduloDisciplinas) {
			chTotal += md.getDisciplina().getChAula() + md.getDisciplina().getChEstagio() + md.getDisciplina().getChLaboratorio();
		}
		chTotal += modulodisciplina.getDisciplina().getChAula() + 
		modulodisciplina.getDisciplina().getChEstagio() + modulodisciplina.getDisciplina().getChLaboratorio();

		if (chTotal > obj.getCargaHoraria()) 
			lista.addErro("A carga horária total do módulo foi excedida.");

		return super.validacaoDados(mensagens);
	}
	
	/**
	 * Remoção de uma disciplina de um módulo.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/modulo/form.jsp</li>
	 * </ul>
	 */
	public void removerDisciplina(){
		int idDisciplina = getParameterInt("id");
		for (ModuloDisciplina md : moduloDisciplinas) {
			if (idDisciplina == md.getDisciplina().getId()) {
				moduloDisciplinas.remove(md);
				obj.getDisciplinas().remove(md);
				if (moduloDisciplinas.size() == 0) 
					bloquearEdicao = false;
				break;
			}
		}
	}
	
	/**
	 * Retorna a carga horária total já adiciona ao módulo.
	 * 
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/modulo/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public int getChTotal(){
		int total = 0;
		for (ModuloDisciplina md : moduloDisciplinas) {
			total += md.getDisciplina().getChTotal();
		}		
		return total;
	}
	
	/**
	 * Método utilizado para a realização das busca referente aos módulos. 
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/modulo/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 * @throws DAOException 
	 */
	@Override
	public String buscar() throws DAOException, ArqException{
		ModuloDao dao = getDAO(ModuloDao.class);
		
		try {
			
			if (filtroCodigo){
				ValidatorUtil.validateRequired(codigo,"Código",erros);
			}else
				codigo = null;
			if (filtroNome){
				ValidatorUtil.validateRequired(descricao,"Nome",erros);
			}else
				descricao = null;
			if (filtroCurso){
				ValidatorUtil.validateRequiredId(idCurso, "Curso", erros);
			}else
				idCurso = null;
			if(!filtroCodigo && !filtroCurso && !filtroNome)
				addMensagemErro("Selecione uma opção para efetuar a busca.");
				
			
			if(!hasErrors()){
				listaModulos.clear();
				listaModulos = dao.findByCodCursoNome(descricao, codigo, idCurso, getNivelEnsino(), getUnidadeGestora());
				if(listaModulos == null || listaModulos.isEmpty()){
					addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				}
			}else 
				return null;
		} finally {
			dao.close();
		}
		return forward(getListPage());
	}
	
	/**
	 * Método cuja responsabilidade é atualizar os dados do módulo
	 * 
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/modulo/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String atualizar() throws ArqException {
		super.atualizar();
		moduloDisciplinas.clear();
		moduloDisciplinas.addAll(obj.getModuloDisciplinas());
		if (moduloDisciplinas.size() == 0) 
			bloquearEdicao = false;
		else
			bloquearEdicao = true;
		return getFormPage();
	}

	/**
	 * Ação realizada antes de entrar no cadastrar
	 * 
	 *  * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/menu/curso.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String preCadastrar() throws ArqException, NegocioException {
		checkChangeRole();
		prepareMovimento(ArqListaComando.CADASTRAR);
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		setConfirmButton("Cadastrar");
		return forward(getFormPage());
	}

	/**
	 * Método cuja responsábilidade é cadastrar um módulo técnico. 
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/modulo/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		if (!isOperacaoAtiva(ArqListaComando.CADASTRAR.getId(),ArqListaComando.ALTERAR.getId())) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return cancelar();
		}
		erros.addAll(obj.validate());
		if (hasErrors()) 
			return null;
		
		//Remoção dos não mais usados.
		for (ModuloDisciplina md : obj.getModuloDisciplinas()) {
			if (!moduloDisciplinas.contains(md)) {
				getGenericDAO().remove(md);
			}
		}
	
		if (getChTotal() != obj.getCargaHoraria()) {
			addMensagemErro("A Carga horária das Disciplinas adicionadas é diferente a " +
					"carga horária do módulo.");
			return null;
		}
		
		if (obj.getId() == 0 ) {
			obj.setNivel(getNivelEnsino());
			obj.setUnidade(getUsuarioLogado().getVinculoAtivo().getUnidade());
		}
		if (!obj.getModuloDisciplinas().isEmpty()) {
			for (ModuloDisciplina mod : moduloDisciplinas) {
				if (!obj.getModuloDisciplinas().contains(mod)) 
					getGenericDAO().remove(mod);
			}
			obj.getModuloDisciplinas().clear();
			obj.getModuloDisciplinas().addAll(moduloDisciplinas);
		}else{
			obj.getModuloDisciplinas().addAll(moduloDisciplinas);
		}

		try {
			ModuloMov mov;
			if (obj.getId() > 0){ 
				atualizaTotalCargaHorariaEstruturas(obj);
				prepareMovimento(SigaaListaComando.ALTERAR_MODULO_TECNICO);
				mov = new ModuloMov(obj, SigaaListaComando.ALTERAR_MODULO_TECNICO);
				execute(mov);
				addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Módulo");
			}else{
				prepareMovimento(SigaaListaComando.CADASTRAR_MODULO_TECNICO);
				mov = new ModuloMov(obj, SigaaListaComando.CADASTRAR_MODULO_TECNICO);
				execute(mov);
				addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Módulo");
			}
		} catch (NegocioException ne){
			addMensagemErro(ne.getMessage());
			ne.printStackTrace();
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
			return forward(getCurrentURL());
		}
		return cancelar();
	}

	/**
	 * Define se é uma operação de cadastro ou de atualização.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/modulo/form.jsp</li>
	 * </ul>
	 * */
	public String getDescricaoOperacao(){
		return getConfirmButton().equals("Cadastrar")? "Cadastro de":"Atualizar";
	}
	
	/**
	 * Método não invocado por JSP(s):
	 * @param modulo
	 * @throws DAOException 
	 */
	private void atualizaTotalCargaHorariaEstruturas(Modulo modulo) throws DAOException{
		EstruturaCurricularTecDao dao = getDAO(EstruturaCurricularTecDao.class);
		Collection<EstruturaCurricularTecnica> estruturas = dao.findByModulo(modulo.getId());
		Modulo moduloPersistido =  dao.findByPrimaryKey(modulo.getId(), Modulo.class, "cargaHoraria");
		int diferencaCH = 0;
		if(moduloPersistido != null && estruturas!= null){
			diferencaCH = modulo.getCargaHoraria() - moduloPersistido.getCargaHoraria() ; 
			if(diferencaCH != 0);
				for(EstruturaCurricularTecnica ect:estruturas){
					dao.updateField(EstruturaCurricularTecnica.class, ect.getId(), "chTotalModulos",ect.getChTotalModulos() + diferencaCH );
				}
		}
	}
	
	/**
	 * Método responsável pela remoção dos módulos técnicos.
	 * 
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/modulo/lista.jsp</li>
	 * </ul>
	 * 
	 * ModuloMBean.remover
	 */
	@Override
	public String remover() throws ArqException {
		prepareMovimento(ArqListaComando.REMOVER);
		Integer id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, Modulo.class);
		if (obj == null) {
			addMensagemErro("O objeto acessado não existe mais.");
			return forward(getListPage());
		}
		
		MovimentoCadastro mov = new MovimentoCadastro(obj, ArqListaComando.REMOVER);
		try {
			execute(mov);
			addMensagem(OPERACAO_SUCESSO);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		} 
		listaModulos.remove(obj);
		return null;
	}
	
	/**
	 * Visualiza os dados do módulo selecionado na view.
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/modulo/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String view() throws DAOException {
		setId();

		this.obj = getGenericDAO().findByPrimaryKey(obj.getId(), obj.getClass());
		
		return forward(getViewPage());
	}
	
	/**
	 * Retorna todos os Módulos Tecnicos encontrados da unidade e do nível de ensino.
	 * 
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/estrutura/modulo.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getAllModulosTecnico() throws ArqException{
		return toSelectItems(getDAO(ModuloDao.class).findAll(getUnidadeGestora(), getNivelEnsino(), null), "id", "fullDesc");
	}
	
	@Override
	public String getDirBase() {
		return "/ensino/tecnico/modulo";
	}
	
	public ModuloDisciplina getModulodisciplina() {
		return modulodisciplina;
	}

	public void setModulodisciplina(ModuloDisciplina modulodisciplina) {
		this.modulodisciplina = modulodisciplina;
	}

	public Collection<ModuloDisciplina> getModuloDisciplinas() {
		return moduloDisciplinas;
	}

	public void setModuloDisciplinas(Collection<ModuloDisciplina> moduloDisciplinas) {
		this.moduloDisciplinas = moduloDisciplinas;
	}

	public boolean isBloquearEdicao() {
		return bloquearEdicao;
	}

	public void setBloquearEdicao(boolean bloquearEdicao) {
		this.bloquearEdicao = bloquearEdicao;
	}

	public boolean isFiltroCodigo() {
		return filtroCodigo;
	}

	public void setFiltroCodigo(boolean filtroCodigo) {
		this.filtroCodigo = filtroCodigo;
	}

	public boolean isFiltroNome() {
		return filtroNome;
	}

	public void setFiltroNome(boolean filtroNome) {
		this.filtroNome = filtroNome;
	}

	public boolean isFiltroCurso() {
		return filtroCurso;
	}

	public void setFiltroCurso(boolean filtroCurso) {
		this.filtroCurso = filtroCurso;
	}

	public Collection<Modulo> getListaModulos() {
		return listaModulos;
	}

	public void setListaModulos(Collection<Modulo> listaModulos) {
		this.listaModulos = listaModulos;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public Integer getIdCurso() {
		return idCurso;
	}

	public void setIdCurso(Integer idCurso) {
		this.idCurso = idCurso;
	}

}