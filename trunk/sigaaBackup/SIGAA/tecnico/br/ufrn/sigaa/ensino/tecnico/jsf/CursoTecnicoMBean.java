/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 04/02/2010
 *
 */
package br.ufrn.sigaa.ensino.tecnico.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.SituacaoCursoHabil;
import br.ufrn.sigaa.ensino.dominio.SituacaoDiploma;
import br.ufrn.sigaa.ensino.dominio.TipoRegimeLetivo;
import br.ufrn.sigaa.ensino.dominio.TipoSistemaCurricular;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.tecnico.dao.CursoTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.CursoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ModalidadeCursoTecnico;

/**
 * Contém operações realizadas sobre os cursos técnicos, sempre referenciando apenas
 * os cursos da unidade do usuário logado. 
 * 
 * @author Leonardo Campos
 *
 */
@Component("cursoTecnicoMBean") @Scope("request")
public class CursoTecnicoMBean extends SigaaAbstractController<CursoTecnico> {

	/** Filtro da busca pelo código */
	private boolean filtroCodigo;
	/** Filtro da busca pelo Nome */
	private boolean filtroNome;
	/** Collection que irá armazenar a listagem dos cursos. */
	private Collection<br.ufrn.sigaa.dominio.Curso> listaCursos = new ArrayList<br.ufrn.sigaa.dominio.Curso>(); 
	
	/**
	 * Construtor padrão
	 */
	public CursoTecnicoMBean() {
		init();
	}
	
	/** Inicializador das variáveis utilizadas */
	private void init(){
		obj = new CursoTecnico();
		obj.setModalidadeEducacao(new ModalidadeEducacao());
		obj.setAtivo(false);
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
		return "/ensino/tecnico/curso";
	}
	
	/**
	 * Metodo que redireciona o usuário para a tela de detalhes do curso.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/tecnico/curso/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String view() throws ArqException {
		setId();
		setObj(getGenericDAO().findByPrimaryKey(obj.getId(), CursoTecnico.class));
		return forward(getViewPage());
	}
	
	@Override
	public String atualizar() throws ArqException {
		setId();
		setObj(getGenericDAO().findByPrimaryKey(obj.getId(), CursoTecnico.class));

		if(ValidatorUtil.isEmpty(obj.getModalidadeCursoTecnico()))
			obj.setModalidadeCursoTecnico(new ModalidadeCursoTecnico());
		if (ValidatorUtil.isEmpty(obj.getTipoRegimeLetivo())) 
			obj.setTipoRegimeLetivo(new TipoRegimeLetivo());
		if (ValidatorUtil.isEmpty(obj.getTipoSistemaCurricular())) 
			obj.setTipoSistemaCurricular(new TipoSistemaCurricular());
		if (ValidatorUtil.isEmpty(obj.getSituacaoCursoHabil())) 
			obj.setSituacaoCursoHabil(new SituacaoCursoHabil());
		if (ValidatorUtil.isEmpty(obj.getSituacaoDiploma())) 
			obj.setSituacaoDiploma(new SituacaoDiploma());
		if (ValidatorUtil.isEmpty(obj.getTurno())) 
			obj.setTurno(new Turno());
		if(ValidatorUtil.isEmpty(obj.getModalidadeEducacao()))
			obj.setModalidadeEducacao(new ModalidadeEducacao());
		
		setConfirmButton("Alterar");
		prepareMovimento(ArqListaComando.ALTERAR);
		
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		return forward(getFormPage());
	}
	
	/**
	 * Retorna o caminho da view do formulário.
	 * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Método não invocado por JSP</li>
	 * </ul>
	 */
	@Override
	public String getFormPage() {
		if(getNivelEnsino() == NivelEnsino.FORMACAO_COMPLEMENTAR)
			return "/ensino/formacao_complementar/curso/form.jsf";
		return super.getFormPage();
	}
	
	/**
	 * Retorna uma coleção de itens com todos os cursos da unidade gestora atual.
	 * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/coordenacao_curso/form.jsp</li>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/coordenacao_curso/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public Collection<SelectItem> getAllUnidadeCombo() throws DAOException, ArqException{
		return toSelectItems(getDAO(CursoTecnicoDao.class).findByUnidadeNivel(getUnidadeGestora(), getNivelEnsino()), "id", "nomeCompleto");
	}
	
	/**
	 * Retornar todo os curso que possuem módulo cadastrado.
	 * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/qualificacao/form.jsp</li>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/qualificacao/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public Collection<SelectItem> getAllCursoComModulo() throws DAOException, ArqException{
		return toSelectItems(getDAO(CursoTecnicoDao.class).findComModulos(getUnidadeGestora(), getNivelEnsino()), "id", "nomeCompleto");
	}
	
	/**
	 * Método responsável pelo cadastro de um Curso Tecnico.
	 * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/curso/form.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {

		checkOperacaoAtiva(ArqListaComando.CADASTRAR.getId(), ArqListaComando.ALTERAR.getId());
		
		obj.setNivel(getNivelEnsino());
		obj.setUnidade(new Unidade(getUnidadeGestora()));
		erros.addAll(obj.validate());
		
		if (hasErrors())
			return null;
		
		if (obj.getSituacaoCursoHabil().getId() == 0) 
			obj.setSituacaoCursoHabil(new SituacaoCursoHabil(-1));
		if (obj.getSituacaoDiploma().getId() == 0) 
			obj.setSituacaoDiploma(new SituacaoDiploma(-1));
		if (obj.getTipoRegimeLetivo().getId() == 0) 
			obj.setTipoRegimeLetivo(new TipoRegimeLetivo(-1));
		if (obj.getTipoSistemaCurricular().getId() == 0) 
			obj.setTipoSistemaCurricular(new TipoSistemaCurricular(-1));
		if(obj.getModalidadeCursoTecnico() != null && obj.getModalidadeCursoTecnico().getId() == 0)
			obj.setModalidadeCursoTecnico(null);
		if (obj.getCodigoInep() == null) 
			obj.setCodigoInep(0);

		super.cadastrar();
		removeOperacaoAtiva();
		return cancelar();  
	}

	/**
	 * Serve para Setar a operação ativa.
	 * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/formacao_complementar/menus/curso.jsp</li>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/curso.jsp</li>
	 * </ul>
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		return super.preCadastrar();
	}
	
	/**
	 * Método responável pela busca de curso Técnico.
	 * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/curso/lista.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String buscar() throws ArqException {
		String nome =null;
		String codigo = null;
		
		CursoDao dao = getDAO(CursoDao.class);
		
		if (filtroCodigo && obj.getCodigo().equals("")) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Código");
		
		if (filtroNome && obj.getNome().equals("")) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome");
		
		if (hasOnlyErrors()) {
			listaCursos.clear();
			return null;
		}
	
		if (filtroCodigo) 
			codigo = obj.getCodigo();

		if (filtroNome) 
			nome= obj.getNome();
		
		int unidade = 0;
		if (!getAcessoMenu().isPedagogico())
			unidade = getUnidadeGestora();

		listaCursos = dao.findByNomeOrCodigo(nome, codigo, getNivelEnsino(),unidade );
		if (listaCursos.isEmpty()) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		return forward(getListPage());
	}

	/**
	 * Efetua a remoção lógica do curso, desativando-o.
	 * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/curso/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String remover() throws ArqException {
		obj.setId(getParameterInt("id"));
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), CursoTecnico.class);
		if (obj == null) {
			addMensagemErro("O objeto acessado não existe mais.");
			return redirectJSF(getSubSistema().getLink());
		}
		prepareMovimento(ArqListaComando.DESATIVAR);
		try {
			super.inativar();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		return forward(getListPage());
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

	/**
	 * Realizar uma busca pelos curso caso, a lista esteja vazia.
	 * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Não invocado por jsp.</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public Collection<br.ufrn.sigaa.dominio.Curso> getListaCursos() throws ArqException {
		return listaCursos;
	}

	public void setListaCursos(Collection<br.ufrn.sigaa.dominio.Curso> listaCursos) {
		this.listaCursos = listaCursos;
	}

}