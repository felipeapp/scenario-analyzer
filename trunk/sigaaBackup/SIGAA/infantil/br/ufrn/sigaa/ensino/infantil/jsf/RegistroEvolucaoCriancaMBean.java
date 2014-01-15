/**
 * 
 */
package br.ufrn.sigaa.ensino.infantil.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.infantil.TurmaInfantilDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.infantil.dao.FormularioEvolucaoCriancaDao;
import br.ufrn.sigaa.ensino.infantil.dominio.FormularioEvolucaoCrianca;
import br.ufrn.sigaa.ensino.infantil.dominio.ItemInfantilFormulario;
import br.ufrn.sigaa.mensagens.MensagensInfantil;
import br.ufrn.sigaa.parametros.dominio.ParametrosInfantil;

/**
 * Controlador respons�vel pelas opera��es do registro da evolu��o da crian�a
 * no ensino infantil
 * 
 * @author Leonardo Campos
 *
 */
@Component @Scope("session")
public class RegistroEvolucaoCriancaMBean extends SigaaAbstractController<MatriculaComponente> implements OperadorDiscente {

	/** Formul�rio de evolu��o movimentado */
	private FormularioEvolucaoCrianca formulario;
	/** itens do formul�rio de evolu��o */
	private DataModel itens;
	/** Se est� apenas visualizando os registros de evolu��o */
	private boolean verRegistros;
	/** Matr�culas das crian�as */
	private Collection<MatriculaComponente> matriculas;
	/** Turma na qual pertence o formul�rio de exibi��o */
	private Turma turma;
	/** Se o acesso est� sendo feito atrav�s da turma virtual */
	private boolean acessoTurmaVirtual = false;
	
	public RegistroEvolucaoCriancaMBean() {
		clear();
	}

	/**
	 * Limpa os atributos que ser�o utilizados na opera��o
	 */
	private void clear() {
		obj = new MatriculaComponente();
		turma = new Turma();
		acessoTurmaVirtual = false;
		formulario = new FormularioEvolucaoCrianca();
		formulario.setItens( new LinkedList<ItemInfantilFormulario>() );
		formulario.setPeriodo(FormularioEvolucaoCrianca.BIMESTRE_INICIAL);
		matriculas = new ArrayList<MatriculaComponente>();
	}
	
	/**
	 * Verifica as permiss�es de acesso, popula as informa��es necess�rias e inicia o caso de uso
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/menu.jsp</li>
	 *	</ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciar() throws SegurancaException{
		checkRole(SigaaPapeis.GESTOR_INFANTIL);
		clear();
		verRegistros = false;
		return buscarDiscente();
	}

	/**
	 * Acessa o registro de evolu��o das crian�as pela turma virtual.
	 * <br>
	 * M�todo n�o invocado por JSPs
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	public String iniciarTurmaVirtual(int idTurma) throws SegurancaException, DAOException{
		clear();
		verRegistros = false;
		acessoTurmaVirtual = true;
		
		TurmaInfantilDao tDao = null;
		FormularioEvolucaoCriancaDao fDao = null;
		try {
			tDao = getDAO(TurmaInfantilDao.class);
			fDao = getDAO(FormularioEvolucaoCriancaDao.class);
			
			formulario = fDao.findByTurma(idTurma, obj.getId());
			if ( formulario != null && formulario.getItens() != null )
				itens = new ListDataModel(formulario.getItensPeriodo(formulario, true));
			
			if ( formulario == null || itens.getRowCount() == 0 ) {
				addMensagemErro("O formul�rio da turma n�o foi definido.");
				return null;
			}
			
			turma = tDao.findByPrimaryKey(idTurma, Turma.class);
			matriculas = tDao.findMatriculasAConsolidar(turma);
			
			return forward(ConstantesNavegacaoInfantil.LISTA_CRIANCAS_TURMA);
			
		}finally {
			if (tDao != null)
				tDao.close();
			if (fDao != null)
				fDao.close();
		}
	}
	
	/**
	 * Redireciona o usu�rio para a p�gina do registro de evolu��o da crian�a em formato de impress�o 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/lista_criancas_turma.jsp</li>
	 *	</ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String selecionaDiscenteTurma() throws ArqException {
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class);
		try {
			Integer id = getParameterInt("id", 0);
			obj = dao.findByPrimaryKey(id, MatriculaComponente.class);
			
			if(obj == null){
				addMensagem(MensagensInfantil.DISCENTE_NAO_MATRICULADO);
				return null;
			}
			
			prepareMovimento(SigaaListaComando.REGISTRAR_EVOLUCAO_CRIANCA);
			popularFormulario();
			setOperacaoAtiva(SigaaListaComando.REGISTRAR_EVOLUCAO_CRIANCA.getId());
			
			return forward(ConstantesNavegacaoInfantil.REGISTRO_EVOLUCAO_FORM);
				
		} finally {
			dao.close();
		}
	
	}
	
	/**
	 * Inicia a visualiza��o dos registros anteriormente cadastrados
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/RegistroEvolucaoCrianca/form_registro_evolucao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException 
	 * @throws SegurancaException
	 */
	public String imprimir () throws ArqException {
			gravar(false);
			obj.setTurma(getGenericDAO().refresh(obj.getTurma()));
			return forward(ConstantesNavegacaoInfantil.REGISTRO_EVOLUCAO_VIEW);
	}
	
	/**
	 * Inicia a visualiza��o dos registros anteriormente cadastrados
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/menu.jsp</li>
	 *	</ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarVisualizacaoRegistros() throws SegurancaException{
		checkRole(SigaaPapeis.GESTOR_INFANTIL);
		clear();
		verRegistros = true;
		return buscarDiscente();
	}
	
	/**
	 * Verifica as permiss�es de acesso, popula as informa��es necess�rias e inicia o caso de uso para
	 * emiss�o do Rel�torio do Registro de Evolu��o da Crian�a.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/menu.jsp</li>
	 *	</ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarRelatorioRegistroEvolucao() throws SegurancaException{
		checkRole(SigaaPapeis.GESTOR_INFANTIL);
		verRegistros = false;
		clear();
		return buscarDiscente();
	}
	
	/**
	 * Redirecionar para o Managed Bean para a busca de discentes do ensino infantil
	 *
	 * @return
	 */
	private String buscarDiscente() throws SegurancaException {
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.REGISTRO_EVOLUCAO_CRIANCA);
		return buscaDiscenteMBean.popular();
	}
	
	/**
	 * Popula as informa��es do discente selecionado e encaminha para a tela do registro da evolu��o da crian�a
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 */
	public String selecionaDiscente() throws ArqException {
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class);
		try {
			setOperacaoAtiva(SigaaListaComando.REGISTRAR_EVOLUCAO_CRIANCA.getId());
			if ( verRegistros ) {
				matriculas = dao.findByDiscente(obj.getDiscente(), SituacaoMatricula.APROVADO, SituacaoMatricula.REPROVADO );
				
				if ( matriculas.isEmpty() ) {
					addMensagemErro("N�o foi encontrada nenhuma turma anterior para o discente.");
					return null;
				}
				
				return forward(ConstantesNavegacaoInfantil.REGISTROS_EVOLUCAO_PREENCHIDOS); 
				
			} else {
				matriculas = dao.findByDiscente(obj.getDiscente(), SituacaoMatricula.MATRICULADO);
				
				if(matriculas == null || matriculas.isEmpty()){
					addMensagem(MensagensInfantil.DISCENTE_NAO_MATRICULADO);
					return null;
				}
				
				obj = matriculas.iterator().next();
				prepareMovimento(SigaaListaComando.REGISTRAR_EVOLUCAO_CRIANCA);
				
				return popularFormulario();
			}
			
		} finally {
			dao.close();
		}
	
	}
	
	/**
	 * Redireciona para tela dos formul�rios preenchidos
	 * N�o invocada por JSPs
	 * @return
	 */
	public String formPreenchidos(){
		return forward(ConstantesNavegacaoInfantil.REGISTROS_EVOLUCAO_PREENCHIDOS);
	}
	
	/**
	 * Seleciona a matr�cula do objeto movimentado e popula o formul�rio 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/RegistroEvolucaoCrianca/registros_evolucao_preenchidos.jsp</li>
	 *	</ul>
	 * @return
	 */
	public String selecionarTurma() throws DAOException, SegurancaException {
		obj.setId( getParameterInt("id", 0) );
		setObj( getGenericDAO().findByPrimaryKey( obj.getId(), MatriculaComponente.class) );
		return popularFormulario();
	}
	
	/**
	 * Invoca o processador para persistir as informa��es do formul�rio atual.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/RegistroEvolucaoCrianca/form_registro_evolucao.jsp</li>
	 *	</ul>
	 * @throws ArqException
	 */
	public String gravarFinalizar() throws ArqException {
		gravar(true);
		if (hasErrors())
			return null;
		else if (acessoTurmaVirtual) {
			removeOperacaoAtiva();
			return iniciarTurmaVirtual(turma.getId());
		} else {
			removeOperacaoAtiva();
			return iniciar();
		}
	}
	
	
	/**
	 * Invoca o processador para persistir as informa��es do formul�rio atual.
	 *  <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/RegistroEvolucaoCrianca/form_registro_evolucao.jsp</li>
	 *	</ul>
	 * @throws ArqException
	 */
	public String gravar() throws ArqException {
		return gravar(true);
	}
	
	/**
	 * Invoca o processador para persistir as informa��es do formul�rio atual.
	 *  <br>
	 * M�todo n�o invocado por JSP(s):
	 * @throws ArqException
	 */
	public String gravar(boolean msg) throws ArqException {
		if( !checkOperacaoAtiva(SigaaListaComando.REGISTRAR_EVOLUCAO_CRIANCA.getId()) )
			return cancelar();

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.REGISTRAR_EVOLUCAO_CRIANCA);
		mov.setObjMovimentado(obj);
		mov.setObjAuxiliar( formulario );
		
		try {
			execute(mov);
			if (msg)
				addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Registro da Evolu��o da Crian�a");
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		
		int periodo = formulario.getPeriodo();
		popularFormulario();
		formulario.setPeriodo( periodo );
		itens = new ListDataModel(formulario.getItensPeriodo(formulario, true));
		
		prepareMovimento(SigaaListaComando.REGISTRAR_EVOLUCAO_CRIANCA);
		return null;
	}

	/**
	 * Popula as informa��es do formul�rio de avalia��o para serem preenchidas/alteradas.
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	private String popularFormulario() throws DAOException, SegurancaException {
		FormularioEvolucaoCriancaDao formEvolCriDao = getDAO(FormularioEvolucaoCriancaDao.class);
		try {
			formulario = formEvolCriDao.findByTurma( obj.getTurma().getId(), obj.getId() );
			if ( formulario != null && formulario.getItens() != null )
				itens = new ListDataModel(formulario.getItensPeriodo(formulario, true));
		} finally {
			formEvolCriDao.close();
		}
			
		if ( formulario == null || itens.getRowCount() == 0 ) {
			addMensagemErro("O formul�rio da turma n�o foi definido.");
			return null;
		}
		
		return forward(ConstantesNavegacaoInfantil.REGISTRO_EVOLUCAO_FORM);
	}
	
	public Collection<SelectItem> getBimestres() {
		int quantPeriodo = ParametroHelper.getInstance().getParametroInt(ParametrosInfantil.QNT_BIMESTRES);
		Collection<SelectItem> bimestres = new ArrayList<SelectItem>();
		for (int i = 1; i <= quantPeriodo; i++) {
			SelectItem bimestre = new SelectItem(i, i + "� Bimestre");
			bimestres.add(bimestre);
		}
		return bimestres;
	}
	
	/**
	 * Altera os crit�rios do formul�rio.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/RegistroEvolucaoCrianca/form_registro_evolucao.jsp</li>
	 *	</ul>
	 */
	public void alterarCriterios(ValueChangeEvent evt) throws DAOException, SegurancaException {
		if( !checkOperacaoAtiva(SigaaListaComando.REGISTRAR_EVOLUCAO_CRIANCA.getId()) )
			redirectJSF(getSubSistema().getLink());
		formulario.setPeriodo( (Integer) evt.getNewValue() );
		itens = new ListDataModel(formulario.getItensPeriodo(formulario, true));
	}
	
	/**
	 * Seta o discente selecionado.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.pessoa.dominio.Discente)
	 */
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		obj.setDiscente(discente.getDiscente());
	}

	public FormularioEvolucaoCrianca getFormulario() {
		return formulario;
	}

	public void setFormulario(FormularioEvolucaoCrianca formulario) {
		this.formulario = formulario;
	}

	public DataModel getItens() {
		return itens;
	}

	public void setItens(DataModel itens) {
		this.itens = itens;
	}

	public boolean isVerRegistros() {
		return verRegistros;
	}

	public void setVerRegistros(boolean verRegistros) {
		this.verRegistros = verRegistros;
	}

	public Collection<MatriculaComponente> getMatriculas() {
		return matriculas;
	}

	public void setMatriculas(Collection<MatriculaComponente> matriculas) {
		this.matriculas = matriculas;
	}
	
	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public void setAcessoTurmaVirtual(boolean acessoTurmaVirtual) {
		this.acessoTurmaVirtual = acessoTurmaVirtual;
	}

	public boolean isAcessoTurmaVirtual() {
		return acessoTurmaVirtual;
	}
	
}