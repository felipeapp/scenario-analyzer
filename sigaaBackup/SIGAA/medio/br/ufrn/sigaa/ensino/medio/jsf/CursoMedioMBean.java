/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 26/05/2011
 *
 */
package br.ufrn.sigaa.ensino.medio.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.NAO_HA_OBJETO_REMOCAO;

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
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.SituacaoCursoHabil;
import br.ufrn.sigaa.ensino.dominio.SituacaoDiploma;
import br.ufrn.sigaa.ensino.dominio.TipoRegimeLetivo;
import br.ufrn.sigaa.ensino.dominio.TipoSistemaCurricular;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.medio.dao.CursoMedioDao;
import br.ufrn.sigaa.ensino.medio.dominio.CursoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.ModalidadeCursoMedio;

/**
 * Contém operações realizadas sobre os cursos médios, sempre referenciando apenas
 * os cursos da unidade do usuário logado. 
 * 
 * @author Rafael Rodrigues
 *
 */
@Component("cursoMedio") @Scope("request")
public class CursoMedioMBean extends SigaaAbstractController<CursoMedio> {

	/** Filtro da busca pelo código */
	private boolean filtroCodigo;
	/** Filtro da busca pelo Nome */
	private boolean filtroNome;
	/** Collection que irá armazenar a listagem dos cursos. */
	private Collection<Curso> listaCursos = new ArrayList<Curso>(); 
	
	/**
	 * Construtor padrão
	 */
	public CursoMedioMBean() {
		init();
	}
	
	/** Inicializando das variáveis utilizadas */
	private void init(){
		obj = new CursoMedio();
		obj.setModalidadeEducacao(new ModalidadeEducacao());
		obj.setModalidadeCursoMedio(new ModalidadeCursoMedio());
		obj.setAtivo(true);
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
		return "/medio/curso";
	}
	
	/**
	 * Metodo que redireciona o usuário para a tela de detalhes do currículo.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/medio/curso/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String view() throws ArqException {
		setId();
		setObj(getGenericDAO().findByPrimaryKey(obj.getId(), CursoMedio.class));
		return forward(getViewPage());
	}
	
	@Override
	public String atualizar() throws ArqException {
		checkChangeRole();
		setId();
		setObj(getGenericDAO().findByPrimaryKey(obj.getId(), CursoMedio.class));
		setConfirmButton("Alterar");
		prepareMovimento(ArqListaComando.ALTERAR);
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());

		if (obj == null || obj.getId() == 0) {
			addMensagem(NAO_HA_OBJETO_REMOCAO);
			return null;
		}
		if(ValidatorUtil.isEmpty(obj.getModalidadeCursoMedio()))
			obj.setModalidadeCursoMedio(new ModalidadeCursoMedio());
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
		
		return forward(getFormPage());
	}
	
	/**
	 * Retorna uma coleção de itens com todos os cursos de Ensino Médio.
	 * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/serie/form.jsp</li>
	 *  <li>/sigaa.war/medio/serie/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public Collection<SelectItem> getAllCombo() throws DAOException, ArqException{
		CursoMedioDao dao = getDAO(CursoMedioDao.class);
		return toSelectItems(dao.findByUnidadeNivel(null, NivelEnsino.MEDIO), "id", "nomeCompleto");
	}
	
	/**
	 * Retorna uma coleção de itens com todos os cursos da unidade gestora atual.
	 * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/coordenacao_curso/form.jsp</li>
	 *  <li>/sigaa.war/medio/coordenacao_curso/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public Collection<SelectItem> getAllUnidadeCombo() throws DAOException, ArqException{
		return toSelectItems(getDAO(CursoMedioDao.class).findByUnidadeNivel(new Unidade(getUnidadeGestora()), getNivelEnsino()), "id", "nomeCompleto");
	}
	
//	/**
//	 * Método responsável pelo cadastro de um Curso de Ensino Médio.
//	 * 
//     * <br />
//     * Método chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 *  <li>/sigaa.war/medio/curso/form.jsp</li>
//	 * </ul>
//	 * 
//	 */
	
	@Override
	public void beforeCadastrarAndValidate() throws NegocioException, DAOException {
		
		obj.setNivel(getNivelEnsino());
		try {
			obj.setUnidade(new Unidade(getUnidadeGestora()));
		} catch (ArqException e) {
			e.printStackTrace();
		}
		
		if (obj.getSituacaoCursoHabil().getId() == 0) 
			obj.setSituacaoCursoHabil(new SituacaoCursoHabil(-1));
		if (obj.getSituacaoDiploma().getId() == 0) 
			obj.setSituacaoDiploma(new SituacaoDiploma(-1));
		if (obj.getTipoRegimeLetivo().getId() == 0) 
			obj.setTipoRegimeLetivo(new TipoRegimeLetivo(-1));
		if (obj.getTipoSistemaCurricular().getId() == 0) 
			obj.setTipoSistemaCurricular(new TipoSistemaCurricular(-1));
		if(obj.getModalidadeCursoMedio() != null && obj.getModalidadeCursoMedio().getId() == 0)
			obj.setModalidadeCursoMedio(new ModalidadeCursoMedio(-1));
		if (obj.getCodigoINEP() == null) 
			obj.setCodigoINEP(null);

		if (getDAO(CursoMedioDao.class).existeCursoByMesmoCodigo(obj))
			addMensagemErro("Código na "+RepositorioDadosInstitucionais.get("siglaInstituicao")+": Já utilizado por outro Curso.");
		
		if (hasErrors())
			return;
	}
	
	/**
	 * Método responsável por Setar a operação ativa.
	 * 
	 * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Não invocado por jsp.</li>
	 * </ul>
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		checkChangeRole();
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		return super.preCadastrar();
	}
	
	/**
	 * Método responsável pela busca de curso de ensino Médio.
	 * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/curso/lista.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String buscar() throws ArqException {
		String nome =null;
		String codigo = null;
		
		CursoMedioDao dao = getDAO(CursoMedioDao.class);
		
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

		listaCursos = dao.findByNomeOrCodigo(nome, codigo, getNivelEnsino());
		if (listaCursos.isEmpty()) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		return forward(getListPage());
	}

	@Override
	public void beforeRemover() throws DAOException {
		try {
			prepareMovimento(ArqListaComando.REMOVER);
			setOperacaoAtiva(ArqListaComando.REMOVER.getId());
			checkChangeRole();
			setId();
			obj = getGenericDAO().findByPrimaryKey(obj.getId(), CursoMedio.class);
		} catch (ArqException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public String forwardCadastrar() {
		return getSubSistema().getLink();
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
	public Collection<Curso> getListaCursos() throws ArqException {
		return listaCursos;
	}

	public void setListaCursos(Collection<Curso> listaCursos) {
		this.listaCursos = listaCursos;
	}
	
	/** Verifica os papéis: GESTOR_MEDIO, COORDENADOR_MEDIO.
	 * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Não invocado por jsp.</li>
	 * </ul>
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO);
	}

}