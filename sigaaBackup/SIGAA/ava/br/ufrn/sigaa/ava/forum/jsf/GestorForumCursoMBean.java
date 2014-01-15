package br.ufrn.sigaa.ava.forum.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ava.forum.dao.GestorForumCursoDao;
import br.ufrn.sigaa.ava.forum.dominio.GestorForumCurso;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * MBean responsável por gerenciar o cadastro de gestores de fórum de cursos.
 * @author Victor Hugo
 *
 */
@Component("gestorForumCursoBean") @Scope("request")
public class GestorForumCursoMBean extends SigaaAbstractController<GestorForumCurso> {

	/** Lista de gestores de fórum para o curso informado. */
	private List<GestorForumCurso> lista;
	/** Define o id do gestor selecionado no formulário de alteração/cadastro */
	private Integer idDocente;
	/** Define o nome do gestor selecionado no formulário de alteração/cadastro */
	private String nomeDocente;
	/** Define o nome do gestor selecionado no formulário de alteração/cadastro */
	private String tipoBuscaDocente;
	/** Define se o gestor será para um docente. */
	private boolean gestorDocente;
	
	/** Construtor padrão. 
	 * @throws ArqException */
	public GestorForumCursoMBean() throws ArqException {
		
		obj = new GestorForumCurso();
		obj.setServidor(new Servidor());
		obj.setDocenteExterno(new DocenteExterno());
		obj.setCurso( getCursoAtualCoordenacao() );
		lista = (List<GestorForumCurso>) getGenericDAO().findByExactField(GestorForumCurso.class, new String[]{"curso.id", "ativo"}, new Object[]{getCursoAtualCoordenacao().getId(), true});
		idDocente = 0;
		nomeDocente = null;
		if( !isEmpty( getParameter("tipoAjaxDocente") ) )
			tipoBuscaDocente = getParameter("tipoAjaxDocente");
		
	}
	
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		setGestorDocente(true);
		return super.preCadastrar();
	}
	
	@Override
	public String listar() throws ArqException {
		lista = (List<GestorForumCurso>) getGenericDAO().findByExactField(GestorForumCurso.class, new String[]{"curso.id", "ativo"}, new Object[]{getCursoAtualCoordenacao().getId(), true});
		return super.listar();
	}
	
	/** Diretório Base do Fórum. */
	@Override
	public String getDirBase() {
		return "/ava/GestorForumCurso";
	}
	
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO);
	}
	

	@Override
	public void beforeCadastrarAndValidate() throws NegocioException,
			SegurancaException, DAOException {
		populaDadosDocente();
	}
	
	@Override
	protected void beforeAtualizar() throws ArqException {
		populaDadosDocente();
	}

	@Override
	public String forwardCadastrar() {
		return getListPage();
	}
	
	/**
	 * Popula os dados docente de acordo com o tipo da busca ajax do docente.
	 */
	private void populaDadosDocente(){
		if( tipoBuscaDocente != null && tipoBuscaDocente.equals("externo") ){
			obj.setDocenteExterno( new DocenteExterno(idDocente, null, nomeDocente) );
			obj.setServidor(null);
		}else {
			obj.setServidor( new Servidor(idDocente, nomeDocente) );
			obj.setDocenteExterno(null);
		}	
	}
	
	@Override
	protected String forwardInativar() {
		try {
			prepareMovimento(ArqListaComando.DESATIVAR);
		} catch (ArqException e) {
			e.printStackTrace();
		}
		return getListPage();
	}
	
	/**
	 * Retorna a lista de gestores
	 * @return
	 */
	public List<GestorForumCurso> getLista() {
		return lista;
	}
	
	@Override
	protected void doValidate() throws ArqException {
		
		boolean jaCadastrado = true;
		
		if( !isEmpty(getCursoAtualCoordenacao()) ){
			jaCadastrado = getDAO(GestorForumCursoDao.class).isGestorForumCurso( 
					obj.getServidor(),
					obj.getDocenteExterno(), getCursoAtualCoordenacao());
		}
		
		if( jaCadastrado ){
			addMensagemErro("O servidor/docente externo indicado já está cadastrado como gestor do fórum deste curso.");
		}
		super.doValidate();
	}

	/**
	 * seta a lista de gestores
	 * @param lista
	 */
	public void setLista(List<GestorForumCurso> lista) {
		this.lista = lista;
	}

	public Integer getIdDocente() {
		return idDocente;
	}

	public void setIdDocente(Integer idDocente) {
		this.idDocente = idDocente;
	}

	public String getNomeDocente() {
		return nomeDocente;
	}

	public void setNomeDocente(String nomeDocente) {
		this.nomeDocente = nomeDocente;
	}

	public String getTipoBuscaDocente() {
		return tipoBuscaDocente;
	}

	public void setTipoBuscaDocente(String tipoBuscaDocente) {
		this.tipoBuscaDocente = tipoBuscaDocente;
	}

	public boolean getGestorDocente() {
		return gestorDocente;
	}

	public void setGestorDocente(boolean gestorDocente) {
		this.gestorDocente = gestorDocente;
	}
	
}
