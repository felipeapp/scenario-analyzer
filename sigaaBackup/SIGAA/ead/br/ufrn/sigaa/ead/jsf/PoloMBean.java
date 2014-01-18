
package br.ufrn.sigaa.ead.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.arq.dao.ead.PoloDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ead.dominio.PoloCurso;
import br.ufrn.sigaa.ead.negocio.MovimentoPolo;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 * Managed bean para cadastro de p�los
 * @author David Ricardo
 */
@Component("poloBean") @Scope("session")
public class PoloMBean extends SigaaAbstractController<Polo> {

	/** DataModel respons�vel pelas opera��es dos polos */
	private DataModel cursos;

	/** Curso o qual o polo est� vinculado. */
	private Curso curso;
	
	private int idUf;
	
	private int idCidade;
	
	/** Construtor padr�o. */
	public PoloMBean() {
		clear();
	}	
	
	private void clear() {
		obj = new Polo();
		obj.setCidade(new Municipio());
	}
	
	/**
	 * Atualiza os dados do p�lo
	 * 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/ead/CursoPolo/lista.jsp</li>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/ead/Polo/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String atualizar() throws ArqException {
		prepareMovimento(ArqListaComando.ALTERAR);
		GenericDAO dao = getGenericDAO();
		setId();
		setReadOnly(false);
		this.obj = dao.findAndFetch(obj.getId(), Polo.class, "polosCursos", "cidade", "cidade.unidadeFederativa");
		setConfirmButton("Alterar");
		return forward("/ead/Polo/formCoordenador.jsf");
	}
	
	/**
	 * Atualizar Polo que o usu�rio � coordenador
	 * 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/portais/cpolo/menu_cpolo.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String atualizarPolo() throws ArqException {
		prepareMovimento(ArqListaComando.ALTERAR);
		
		obj = getUsuarioLogado().getVinculoAtivo().getCoordenacaoPolo().getPolo();
		
		GenericDAO dao = getGenericDAO();
		setReadOnly(false);
		this.obj = dao.findByPrimaryKey(obj.getId(), Polo.class);
		setConfirmButton("Alterar");
		return forward("/ead/Polo/formCoordenador.jsf");
	}
	
	/**
	 * Cadastrar um P�lo e suas associa��es com cursos
	 * 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/ead/CursoPolo/form.jsp</li>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/ead/Polo/form.jsp</li>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/ead/Polo/formCoordenador.jsp</li>
	 * </ul>
     * @throws ArqException 
	 */
	@Override
	public String cadastrar() throws ArqException {
		if (cadastro()) {
			if (isUserInRole(SigaaPapeis.COORDENADOR_GERAL_EAD))
				return redirect(getContextPath() + "/ead/Polo/lista.jsf");
			return cancelar();
		}
		else
			return forward("/ead/Polo/form.jsf?aba=cdp-cadastros");
	}
	
	/**
	 * Retorna para listagem.
	 * 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/ead/Polo/formCoordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String voltar() throws ArqException{
		return redirect(getContextPath() + "/ead/Polo/lista.jsf");
	}
	
	/**
	 * Cadastro realizado pela coordena��o de p�lo, redireciona para a p�gina dos portais de p�lo
	 * 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>N�o invocado por JSP.</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String cadastrarParaCoordenadorPolo() throws ArqException {
		cadastro();
		return redirect(getContextPath() + "/portais/cpolo/cpolo.jsf");
	}

	/**
	 * Realizada a opera��o de cadastro de p�lo
	 * 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>N�o invocado por JSP.</li>
	 * </ul>
	 * @throws ArqException
	 */
	@SuppressWarnings("unchecked")
	private boolean cadastro() throws ArqException {
		obj.getCidade().getUnidadeFederativa().setId(idUf);
		obj.getCidade().setId(idCidade);
		
		boolean nenhumCursoSelecionado = true;
		List<Curso> cursosSelecionados = (List<Curso>) cursos.getWrappedData();
		for (Iterator<Curso> it = cursosSelecionados.iterator(); it.hasNext(); ) {
			Curso c = it.next();
			if (c.isSelecionado())
				nenhumCursoSelecionado = false;
		}
		
		erros = obj.validate();
		
		if ( obj.getCidade() != null ){
			Polo poloExistente = getGenericDAO().findByExactField(Polo.class, "cidade.id",obj.getCidade().getId(),true);
			if (poloExistente != null && poloExistente.getId() != obj.getId())
				erros.addErro("J� existe p�lo cadastrado para este munic�pio.");
		}
		
		if (nenhumCursoSelecionado)
			erros.addErro("Pelo menos um Curso � obrigat�rio.");
			
		if (hasErrors()){
			return false;
		}
		else{
			
			for (Iterator<Curso> it = cursosSelecionados.iterator(); it.hasNext(); ) {
				Curso c = it.next();
				if (!c.isSelecionado())
					it.remove();
			}
								
			MovimentoPolo mov = new MovimentoPolo();
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_POLO);
			mov.setCursos(cursosSelecionados);
			mov.setPolo(obj);
	
			try {
				executeWithoutClosingSession(mov, getCurrentRequest());
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return false;
			}
			
			if (obj.getId() == 0) {
				addMessage("P�lo cadastrado com sucesso!", TipoMensagemUFRN.INFORMATION);	
			}
			else {
				addMessage("P�lo atualizado com sucesso!", TipoMensagemUFRN.INFORMATION);	
			}
		}
		return true; 
	}

	/**
	 * Remove um p�lo
	 * 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/ead/Polo/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String remover() throws ArqException {
		setId();
		obj.setCidade(null);

		if (obj.getId() == 0) {
			addMensagemErro("N�o h� objeto informado para remo��o");
			return null;
		} else {

			MovimentoPolo mov = new MovimentoPolo();
			mov.setPolo(obj);
			prepareMovimento(SigaaListaComando.REMOVER_POLO);
			mov.setCodMovimento(SigaaListaComando.REMOVER_POLO);

			try {
				executeWithoutClosingSession(mov, getCurrentRequest());
				redirect(getContextPath() + "/ead/Polo/lista.jsf");
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return forward("/ead/Polo/lista.jsp");
			}
			addMessage("Opera��o realizada com sucesso!", TipoMensagemUFRN.INFORMATION);

			return null;
		}
	}

	/**
	 * Retorna todos uma lista com todos os p�los existentes
	 */
	@Override
	public Collection<Polo> getAll() throws ArqException {
		return getDAO(PoloDao.class).findAllPolos();
	}
	
	/**
	 * Retorna uma lista de SelectItem para montar um ComboBox com todos os p�los
	 */
	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		return toSelectItems(getAll(), "id", "descricao");
	}

	/**
	 * Redireciona para a listagem de p�los
	 * 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>N�o invocado por JSP.</li>
	 * </ul>
	 */
	@Override
	public String forwardCadastrar() {
		return forward("/ead/Polo/lista.jsp");
	}

	/**
	 * Exibe a p�gina de detalhes (view) do p�lo
	 *
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/ead/Polo/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String detalhes() throws DAOException {
		obj = getGenericDAO().findByPrimaryKey(getParameterInt("id"), Polo.class);
		return forward("/ead/Polo/view.jsp");
	}

	/**
	 * Retorna um DataModel com todos os cursos
	 * 
  	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/ead/Polo/form.jsp</li>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/ead/Polo/formCoordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public DataModel getCursos() throws ArqException {
		prepareMovimento(SigaaListaComando.CADASTRAR_POLO);

		if (cursos == null) {
			CursoDao dao = getDAO(CursoDao.class);
			List<Curso> cursosDistancia = dao.findAllCursosADistancia();

			if (obj.getId() != 0) {
				Collection<PoloCurso> pcs = obj.getPolosCursos();
				for (PoloCurso pc : pcs) {
					Integer index = cursosDistancia.indexOf(pc.getCurso());
					if(index != -1) {
						cursosDistancia.get(index).setSelecionado(true);
					}
				}
			}

			cursos = new ListDataModel(cursosDistancia);
		}
		return cursos;
	}

	/**
	 * Retorna uma cole��o de {@link SelectItem} referentes aos munic�pios cuja unidade federativa seja o RN.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllMunicipiosUf() throws DAOException {
		List<SelectItem> itens = new ArrayList<SelectItem>();
		if ( idUf != 0 ) {
			MunicipioDao dao = getDAO(MunicipioDao.class);
			try {
				itens = toSelectItems(dao.findByUF(idUf, "id", "nome"), "id", "nome");
			} finally {
				dao.close();
			}
		}
		
//		clear();
		return itens;
	}
	
	public void setCursos(DataModel cursos) {
		this.cursos = cursos;
	}

	public Curso getCurso() {
		return this.curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public int getIdUf() {
		return idUf;
	}

	public void setIdUf(int idUf) {
		this.idUf = idUf;
	}

	public int getIdCidade() {
		return idCidade;
	}

	public void setIdCidade(int idCidade) {
		this.idCidade = idCidade;
	}
	
}