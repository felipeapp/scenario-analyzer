/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '09/10/2009'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.jsf;

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
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CursoLatoDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.DiscenteLatoDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.TurmaEntradaLatoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.TipoPeriodicidadeAula;
import br.ufrn.sigaa.ensino.latosensu.dominio.TurmaEntradaLato;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 * Controlador respons�vel pelas opera��es de Turmas de Entrada Lato.
 * 
 * @author Igor
 *
 */

@Scope("session")
@Component("turmaEntrada")
public class TurmaEntradaMBean extends SigaaAbstractController<TurmaEntradaLato> {
	
	/** Respons�vel por armazenar turmas de entrada lato para sua manipula��o. */
	Collection<TurmaEntradaLato> turmasEntrada = new ArrayList<TurmaEntradaLato>();
	
	/**
	 * Construtor
	 */
	public TurmaEntradaMBean() {
		this.obj = new TurmaEntradaLato();
	}
	
	/**
	 * Retorna uma cole��o todos os cursos de lato sensu coordenados pelo usu�rio.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/lato/TurmaEntrada/turmas_entrada.jsp</li>
	 *	</ul>
	 * @return
	 */
	public Collection<SelectItem> getAllCursosCoordenadorCombo() {
		CursoLatoDao dao = getDAO(CursoLatoDao.class);
		try {
			return toSelectItems(dao.findAllCoordenadoPor(getUsuarioLogado().getServidor().getId()), "id", "nome");
		} catch (Exception e) {
			notifyError(e);
			return new ArrayList<SelectItem>();
		}
	}
	
	/**
	 * Retorna uma cole��o de todos os turnos ativos.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/lato/TurmaEntrada/turmas_entrada.jsp</li>
	 *	</ul>
	 * @return
	 */
	public Collection<SelectItem> getAllTurnoAtivosCombo() {
		return getAllAtivo(Turno.class, "id", "descricao");
	}
	
	/**
	 * Retorna uma cole��o de todos os tipos de periodicidade de aulas utilizadas nos cursos lato sensu.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/lato/TurmaEntrada/turmas_entrada.jsp</li>
	 *	</ul>
	 * @return
	 */
	public Collection<SelectItem> getAllTiposPeriodicidadeAulaCombo() {
		return getAll(TipoPeriodicidadeAula.class, "id", "descricao");
	}
	
	/**
	 * Retorna uma cole��o de todos os munic�pios ativos.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/lato/TurmaEntrada/turmas_entrada.jsp</li>
	 *	</ul>
	 * @return
	 */
	public Collection<SelectItem> getAllMunicipiosAtivosCombo() {
		return getAllAtivo(Municipio.class, "id", "nome");
	}
	
	/**
	 * Retorna uma cole��o de todos os Campus da Institui��o de Ensino Superior.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/lato/TurmaEntrada/turmas_entrada.jsp</li>
	 *	</ul>
	 * @return
	 */
	public Collection<SelectItem> getAllCampusIesCombo() {
		return getAll(CampusIes.class, "id", "nome");
	}	
	
	/**
	 * Inicia o caso de uso de cadastro de Turmas de Entrada.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/lato/menu_coordenador.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String preCadastrarTurmaEntrada() throws ArqException {		
		prepareMovimento(SigaaListaComando.CADASTRAR_TURMA_ENTRADA_LATO);		
		TurmaEntradaLatoDao dao = getDAO(TurmaEntradaLatoDao.class);
		try {
			turmasEntrada = dao.findByCursoLato(getCursoAtualCoordenacao().getId(), true);
			setConfirmButton("Cadastrar");
			setReadOnly(false);
			this.obj = new TurmaEntradaLato();
			obj.setCodigo(UFRNUtils.completaZeros((dao.findByCursoLato(getCursoAtualCoordenacao().getId(), false).size() + 1), 3));
		} finally {
			dao.close();
		}
		return forward(ConstantesNavegacao.CADASTRAR_TURMA_ENTRADA);
	}
	
	/**
	 * Inicia o caso de uso de altera��o de uma Turma de Entrada.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/lato/TurmaEntrada/turmas_entrada.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public String preAlterar() throws DAOException {
		TurmaEntradaLatoDao dao = getDAO(TurmaEntradaLatoDao.class);
		/*
		 * OBS. Eu tentei usar findByPrimaryKey e depois getGenericDao.inicialize no turno,
		 * em tipoPeriodicidadeAula e em campusIes mas dizia que eles nao estavam mapeados,
		 * mas est�o no hibernate.cfg. Logo criei este m�todo(findTurmaEntradaLato)
		 * pois estava perdendo tempo.
		 *  
		 */
		obj = dao.findTurmaEntradaLato(getParameterInt("id",0));		
		setConfirmButton("Alterar");
		return forward(ConstantesNavegacao.CADASTRAR_TURMA_ENTRADA);
	} 
	
	/**
	 * Respons�vel por cadastrar uma Turma de Entrada.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/lato/TurmaEntrada/turmas_entrada.jsp</li>
	 *	</ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		obj.setCursoLato( (CursoLato) getCursoAtualCoordenacao());
		
		if(obj.getCursoLato() == null) {
			addMensagemErro("Curso n�o informado.");
			return null;
		}
		
		ListaMensagens lista = obj.validate();
		if(!lista.isEmpty()){
			addMensagens(lista);
			return null;
		}
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_TURMA_ENTRADA_LATO);
		mov.setObjMovimentado(obj);
		mov.setUsuarioLogado(getUsuarioLogado());
		
		execute(mov);
		addMensagemInformation("Opera��o realizada com sucesso!");
		return forwardCadastrar();				
	}
	
	/**
	 * Respons�vel por remover uma Turma de Entrada.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/lato/TurmaEntrada/turmas_entrada.jsp</li>
	 *	</ul>
	 */
	@Override
	public String remover() throws ArqException {
		prepareMovimento(ArqListaComando.DESATIVAR);
		setOperacaoAtiva(ArqListaComando.DESATIVAR.getId());
		obj = getGenericDAO().findByPrimaryKey(getParameterInt("id",0), TurmaEntradaLato.class);
		
		if ( haDiscenteAssociado() ) {
			addMensagemErro("N�o � poss�vel remover essa turma de entrada, pois existe pelo menos um discente associado a ela.");
			return preCadastrarTurmaEntrada();
		}
		
		try {
			super.inativar();
			return forwardCadastrar();
		} catch (NegocioException e) {			
			e.printStackTrace();
		}
		return null;
	}

	private boolean haDiscenteAssociado() throws DAOException {
		DiscenteLatoDao dao = getDAO(DiscenteLatoDao.class);
		try {
			 return dao.haDiscenteTurmaEntrada(obj.getId());
		} finally {
			dao.close();
		}
	}
	
	@Override
	public String forwardCadastrar() {
		TurmaEntradaLatoDao dao = getDAO(TurmaEntradaLatoDao.class);
		try {
			turmasEntrada = dao.findByCursoLato(getCursoAtualCoordenacao().getId(), true);
			prepareMovimento(SigaaListaComando.CADASTRAR_TURMA_ENTRADA_LATO);
			obj = new TurmaEntradaLato();
			setConfirmButton("Cadastrar");
			obj.setCodigo(UFRNUtils.completaZeros((turmasEntrada.size() + 1), 3));
		} catch (ArqException e) {			
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return null;
	}
	
	/**
	 * Redireciona para a tela de inativa��o da turma de entrada.
	 */
	protected String forwardInativar() {
		TurmaEntradaLatoDao dao = getDAO(TurmaEntradaLatoDao.class);
		try {
			turmasEntrada = dao.findByCursoLato(getCursoAtualCoordenacao().getId(), true);			
			obj = new TurmaEntradaLato();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			dao.close();
		}
		setConfirmButton("Cadastrar");
		return ConstantesNavegacao.CADASTRAR_TURMA_ENTRADA;
	}

	public Collection<TurmaEntradaLato> getTurmasEntrada() {
		return turmasEntrada;
	}

	public void setTurmasEntrada(Collection<TurmaEntradaLato> turmasEntrada) {
		this.turmasEntrada = turmasEntrada;
	}	
}