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
 * Managed Bean respons�vel pelas opera��es b�sicas 
 * de M�dulo T�cnico T�cnica.
 *  
 * @author guerethes
 */
@SuppressWarnings("serial")
@Component @Scope("request")
public class ModuloMBean extends SigaaAbstractController<Modulo> {

	/** Constante utilizada para auxiliar na busca por c�digo  */
	private boolean filtroCodigo;
	/** Constante utilizada para auxiliar na busca por nome  */
	private boolean filtroNome;
	/** Constante utilizada para auxiliar na busca por curso  */
	private boolean filtroCurso;
	/**Descri��o do modulo, usada na busca*/
	private String descricao;	
	/**Codigo do modulo, usado na busca*/
	private String codigo;
	/**Identificador do curso que o modulo est� vinculado, usado na busca*/
	private Integer idCurso;
	
	/** E a represeta��o de um m�dulo com uma Disciplina */
	private ModuloDisciplina modulodisciplina;
	/** Armazena uma cole��o de Modulo Disciplina */
	private Collection<ModuloDisciplina> moduloDisciplinas = new ArrayList<ModuloDisciplina>();
	/** Retorna uma cole��o de Modulo */
	private Collection<Modulo> listaModulos = new ArrayList<Modulo>();
	/** Serve pra auxiliar na edi��o ou noa do nome do M�dulo */
	private boolean bloquearEdicao;
	
	/**
	 * Construtor padr�o
	 */
	public ModuloMBean() {
		init();
	}
	
	/**
	 * Inicializa os atributos que ser�o utilizados no decorrer dos Casos de Uso.
	 */
	private void init() {
		obj = new Modulo();
		modulodisciplina = new ModuloDisciplina();
		modulodisciplina.setDisciplina(new ComponenteCurricular());
		bloquearEdicao = false;
	}
	
	/**
	 * Adiciona uma nova disciplina na lista de disciplina do m�dulo.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
				addMensagemErro("J� existe um m�dulo com a descri��o " + obj.getDescricao()+".");
				return;
			}
				
		}
		if (modulodisciplina.getDisciplina().getId() == 0) {
			addMensagemErro("Disciplina: Campo obrigat�rio n�o informado.");
			return;
		}
			modulodisciplina.setDisciplina((getGenericDAO().findByPrimaryKey(modulodisciplina.getDisciplina().getId(), ComponenteCurricular.class)));
			modulodisciplina.setModulo(obj);
			obj.setModuloDisciplina(modulodisciplina);
			addMensagens(obj.validate());
			
			if (moduloDisciplinas.contains(modulodisciplina)) 
				addMensagemErro("A disciplina j� est� contida no M�dulo.");
			
			if (obj.getCargaHoraria() != null && obj.getCargaHoraria() < getChTotal() + modulodisciplina.getDisciplina().getChTotal())  
				addMensagemErro("N�o � poss�vel adicionar a disciplina, pois ultrapassa a carga hor�ria total do M�dulo.");
			
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
	 * Utilizada na valida��o dos dados do M�dulo
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>M�todo n�o invocado por JSP.</li>
	 * </ul>
	 */
	@Override
	public boolean validacaoDados(ListaMensagens mensagens) {
		ListaMensagens lista = new ListaMensagens();
		//Valida��o da carga hor�ria total do m�dulo.
		int chTotal = 0;
		for (ModuloDisciplina md : moduloDisciplinas) {
			chTotal += md.getDisciplina().getChAula() + md.getDisciplina().getChEstagio() + md.getDisciplina().getChLaboratorio();
		}
		chTotal += modulodisciplina.getDisciplina().getChAula() + 
		modulodisciplina.getDisciplina().getChEstagio() + modulodisciplina.getDisciplina().getChLaboratorio();

		if (chTotal > obj.getCargaHoraria()) 
			lista.addErro("A carga hor�ria total do m�dulo foi excedida.");

		return super.validacaoDados(mensagens);
	}
	
	/**
	 * Remo��o de uma disciplina de um m�dulo.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Retorna a carga hor�ria total j� adiciona ao m�dulo.
	 * 
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * M�todo utilizado para a realiza��o das busca referente aos m�dulos. 
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
				ValidatorUtil.validateRequired(codigo,"C�digo",erros);
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
				addMensagemErro("Selecione uma op��o para efetuar a busca.");
				
			
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
	 * M�todo cuja responsabilidade � atualizar os dados do m�dulo
	 * 
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * A��o realizada antes de entrar no cadastrar
	 * 
	 *  * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * M�todo cuja respons�bilidade � cadastrar um m�dulo t�cnico. 
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
		
		//Remo��o dos n�o mais usados.
		for (ModuloDisciplina md : obj.getModuloDisciplinas()) {
			if (!moduloDisciplinas.contains(md)) {
				getGenericDAO().remove(md);
			}
		}
	
		if (getChTotal() != obj.getCargaHoraria()) {
			addMensagemErro("A Carga hor�ria das Disciplinas adicionadas � diferente a " +
					"carga hor�ria do m�dulo.");
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
				addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "M�dulo");
			}else{
				prepareMovimento(SigaaListaComando.CADASTRAR_MODULO_TECNICO);
				mov = new ModuloMov(obj, SigaaListaComando.CADASTRAR_MODULO_TECNICO);
				execute(mov);
				addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "M�dulo");
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
	 * Define se � uma opera��o de cadastro ou de atualiza��o.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/modulo/form.jsp</li>
	 * </ul>
	 * */
	public String getDescricaoOperacao(){
		return getConfirmButton().equals("Cadastrar")? "Cadastro de":"Atualizar";
	}
	
	/**
	 * M�todo n�o invocado por JSP(s):
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
	 * M�todo respons�vel pela remo��o dos m�dulos t�cnicos.
	 * 
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
			addMensagemErro("O objeto acessado n�o existe mais.");
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
	 * Visualiza os dados do m�dulo selecionado na view.
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
	 * Retorna todos os M�dulos Tecnicos encontrados da unidade e do n�vel de ensino.
	 * 
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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