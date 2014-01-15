/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 06/11/2007
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateEmailRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.latosensu.jsf.ConstantesNavegacao;
import br.ufrn.sigaa.ensino.negocio.dominio.CoordenacaoCursoMov;
import br.ufrn.sigaa.estagio.dominio.StatusEstagio;
import br.ufrn.sigaa.jsf.UsuarioMBean;
import br.ufrn.sigaa.mensagens.MensagensGerais;

/**
 * MBean para identificação/substituição de coordenadores de curso de graduação
 * e pós-graduação
 * @author leonardo
 * @author Victor Hugo
 */
@Component("coordenacaoCurso")
@Scope("session")
public class CoordenacaoCursoMBean extends SigaaAbstractController<CoordenacaoCurso> {

	/** Constantes de visualização do formulário  */
	public static final String JSP_FORM = "/ensino/coordenacao_curso/form.jsp";

	/** Constantes de visualização da listagem*/
	public static final String JSP_LISTA = "/ensino/coordenacao_curso/lista.jsp";

	/** Constantes de visualização da substituição  */
	public static final String JSP_SUBSTITUICAO = "/ensino/coordenacao_curso/substituicao.jsp";

	/** Constantes de visualização de contato  */
	public static final String JSP_CONTATO = "/ensino/coordenacao_curso/contato.jsp";

	/** Constantes de visualização da lista de contato  */
	public static final String JSP_LISTA_COORDENADORES_GRADUACAO = "/ensino/coordenacao_curso/lista_contato.jsp";
	/** Constantes de visualização dos coordenadores do programa  */
	public static final String JSP_LISTA_COORDENADORES_STRICTO = "/ensino/coordenacao_curso/coordenadores_programa.jsp";
	/** Constantes de visualização dos coordenadores Técnico  */
	public static final String JSP_LISTA_COORDENADORES_TECNICO = "/ensino/coordenacao_curso/coordenadores_tecnico.jsp";
	/** Constantes de visualização dos coordenadores Lato Sensu */
	public static final String JSP_LISTA_COORDENADORES_LATO = "/ensino/coordenacao_curso/coordenadores_lato.jsp";
	/** Constantes de visualização dos coordenadores de Residência Médica */
	public static final String JSP_LISTA_COORDENADORES_RESIDENCIA = "/ensino/coordenacao_curso/coordenadores_residencia.jsp";
	/** Coordenador antigo do Curso */
	private CoordenacaoCurso coordenadorAntigo;

	private Collection<CoordenacaoCurso> coordenadores;
	
	private boolean opcaoLogarComo = false;
	
	public CoordenacaoCursoMBean(){
		initObj();
	}

	/**
	 * Inicializa objeto
	 */
	private void initObj() {
		obj = new CoordenacaoCurso();
		obj.setUnidade(new Unidade());
		coordenadorAntigo = new CoordenacaoCurso();
		coordenadores = new ArrayList<CoordenacaoCurso>();
	}

	
	/**
	 * Volta para a tela de listagem de coordenadores de um curso.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *	      <li>sigaa.war/ensino/coordenacao_curso/substituicao.jsp</li>
	 *		  <li>sigaa.war/ensino/coordenacao_curso/form.jsp</li>
	 *	</ul>
	 * 
	 * @return {@value #JSP_LISTA}
	 * @throws SegurancaException
	 */
	public String voltar() throws SegurancaException {
			return forward(JSP_LISTA);
	}
	
	/**
	 * Inicia o caso de uso de substituição de coordenador
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/graduacao/menus/administracao.jsp</li>
	 *	</ul>
	 * 
	 * @return {@value #JSP_LISTA}
	 * @throws SegurancaException
	 */
	public String iniciarSubstituicao() throws SegurancaException {
		setReadOnly(false);
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.PPG);
		setResultadosBusca(null);
		return forward(JSP_LISTA);
	}

	/**
	 * Método que inicia a operação de alteração dos dados da coordenação selecionada.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino/coordenacao_curso/lista.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarAlterar() throws ArqException {
		initObj();
		coordenadorAntigo = null;
		setConfirmButton("Alterar");
		setReadOnly(true);
		
		setOperacaoAtiva(SigaaListaComando.ALTERAR_COORDENADOR.getId());
		prepareMovimento(SigaaListaComando.ALTERAR_COORDENADOR);
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.PPG, SigaaPapeis.GESTOR_LATO, SigaaPapeis.GESTOR_TECNICO,
				SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, SigaaPapeis.SECRETARIA_RESIDENCIA);
		
		
		Integer id = getParameterInt("id");
		obj = getGenericDAO().findByPrimaryKey(id, CoordenacaoCurso.class);
		
		if (obj == null) {
			//É necessário a criação da mensagem padrão em arquitetura -> 'Objeto não encontrado'. 
			addMensagemErro("Não foi possível localizar o registro.");
			return cancelar();
		}
		
		return forward(JSP_FORM);
	}
	
	/**
	 * Inicia o caso de uso de cadastro de coordenador de curso.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/administracao.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/turma.jsp</li>
	 *		<li>sigaa.war/graduacao/menus/administracao.jsp</li>
	 *		<li>sigaa.war/stricto/menus/permissao.jsp</li>
	 *		<li>sigaa.war/complexo_hospitalar/index.jsp</li>
	 *	</ul>
	 *  
	 * @return {@value #JSP_FORM}
	 * @throws ArqException 
	 */
	public String iniciar() throws ArqException{
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.PPG, SigaaPapeis.GESTOR_LATO, 
				SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, SigaaPapeis.SECRETARIA_RESIDENCIA);
		initObj();
		setConfirmButton("Confirmar");
		setOperacaoAtiva(SigaaListaComando.IDENTIFICAR_COORDENADOR.getId());
		prepareMovimento(SigaaListaComando.IDENTIFICAR_COORDENADOR);
		setReadOnly(false);
		return forward(JSP_FORM);
	}

	/**
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino/coordenacao_curso/form.jsp</li>
	 *		<li>sigaa.war/ensino/coordenacao_curso/substituicao.jsp</li>
	 *	</ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws NegocioException, ArqException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.PPG, SigaaPapeis.GESTOR_LATO, SigaaPapeis.GESTOR_TECNICO,
				SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, SigaaPapeis.SECRETARIA_RESIDENCIA);
		
		if ( getUltimoComando().equals(SigaaListaComando.IDENTIFICAR_COORDENADOR)) {
			if (!checkOperacaoAtiva(SigaaListaComando.IDENTIFICAR_COORDENADOR.getId()))
				return null;
		} else if (getUltimoComando().equals(SigaaListaComando.SUBSTITUIR_COORDENADOR)) {
			if ( !checkOperacaoAtiva(SigaaListaComando.SUBSTITUIR_COORDENADOR.getId()))
				return null;
		} else if (getUltimoComando().equals(SigaaListaComando.ALTERAR_COORDENADOR)) {
			if ( !checkOperacaoAtiva(SigaaListaComando.ALTERAR_COORDENADOR.getId()))
				return null;
		}
		
		erros = new ListaMensagens();
		erros.addAll(obj.validate().getMensagens());
		
		if( getNivelEnsino() == NivelEnsino.GRADUACAO || getNivelEnsino() == NivelEnsino.LATO || getNivelEnsino() == NivelEnsino.TECNICO){
			obj.setUnidade(null);
			ValidatorUtil.validateRequired(obj.getCurso(), "Curso", erros);
		} else{
			obj.setCurso(null);
			ValidatorUtil.validateRequired(obj.getUnidade(), "Programa", erros);
		}

		if (hasErrors())
			return null;
		
		//Valida se já existe coordenação ativa somente para operação idenficar coordenação
		if( getUltimoComando().equals( SigaaListaComando.IDENTIFICAR_COORDENADOR ) && hasCoordenacaoAtiva() ){
			getGenericDAO().initialize( obj.getCargoAcademico() );
			addMensagem( MensagensGerais.CURSO_UNIDADE_JA_POSSUI_COORDENACAO, 
					obj.getCargoAcademico().getDescricao().toLowerCase(), ( !isEmpty( obj.getUnidade() ) ? "programa" : "curso" ) );
		}
		
		// Se for substituição não valida email
		if (!getUltimoComando().equals(SigaaListaComando.SUBSTITUIR_COORDENADOR))
			ValidatorUtil.validateEmail(obj.getEmailContato(),"Email", erros);
		
		if(hasErrors())
			return null;

		CoordenacaoCursoMov mov = new CoordenacaoCursoMov();
		
		if ( getUltimoComando().equals(SigaaListaComando.IDENTIFICAR_COORDENADOR)) 
			mov.setCodMovimento(SigaaListaComando.IDENTIFICAR_COORDENADOR);
		else if (getUltimoComando().equals(SigaaListaComando.SUBSTITUIR_COORDENADOR)) 
			mov.setCodMovimento(SigaaListaComando.SUBSTITUIR_COORDENADOR);
		else if (getUltimoComando().equals(SigaaListaComando.ALTERAR_COORDENADOR))
			mov.setCodMovimento(SigaaListaComando.ALTERAR_COORDENADOR);
				
		mov.setCoordenador(obj);
		mov.setCoordenadorAntigo(coordenadorAntigo);

		try {
			execute(mov, getCurrentRequest());
		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens(e.getListaMensagens());
			return null;
		}catch (Exception e) {
			return tratamentoErroPadrao(e);
		}
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		initObj();
		return cancelar();
	}
	
	/**
	 * Método que verifica se existe uma coordenação ativa para o curso ou unidade
	 * selecionada
	 * Método não invocado por JSP's.
	 * @param unidade
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	private boolean hasCoordenacaoAtiva() throws DAOException{
		
		CoordenacaoCursoDao dao = getDAO( CoordenacaoCursoDao.class );
		CoordenacaoCurso coordenacaoAtiva = null; 
		coordenacaoAtiva = dao.findUltima( obj.getCurso(), obj.getUnidade(), obj.getCargoAcademico().getId() );
		
		return !isEmpty( coordenacaoAtiva ); 
		
	}

	/**
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino/coordenacao_curso/lista.jsp</li>
	 *	</ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#buscar()
	 */
	@Override
	public String buscar() throws DAOException {

		CoordenacaoCursoDao dao = getDAO(CoordenacaoCursoDao.class);
		if( getNivelEnsino() == NivelEnsino.GRADUACAO || getNivelEnsino() == NivelEnsino.LATO || getNivelEnsino() == NivelEnsino.TECNICO){
			if(obj.getCurso().getId() > 0){
				setResultadosBusca(dao.findByCurso(obj.getCurso().getId(), -1, getNivelEnsino(), null));
				initObj();
			} else {
				setResultadosBusca(null);
				addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
			}
		} else {
			if(obj.getUnidade().getId() > 0){
				if(getNivelEnsino() == NivelEnsino.STRICTO)
					setResultadosBusca(dao.findByPrograma(obj.getUnidade().getId(), TipoUnidadeAcademica.PROGRAMA_POS, true, null));
				else if(getNivelEnsino() == NivelEnsino.RESIDENCIA)
					setResultadosBusca(dao.findByPrograma(obj.getUnidade().getId(), TipoUnidadeAcademica.PROGRAMA_RESIDENCIA, true, null));
				initObj();
			} else {
				setResultadosBusca(null);
				addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
			}
		}

		return null;
	}
	
	/**
	 * Lista os coordenadores de curso de acordo com o nível de ensino atual. 
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/graduacao/menus/administracao.jsp</li>
	 *		<li>sigaa.war/stricto/menus/permissao.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/administracao.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/turma.jsp</li>
	 *	</ul>
	 */
	public String listar() throws ArqException {
		switch(getNivelEnsino()){
		case NivelEnsino.GRADUACAO:
			return forward(JSP_LISTA_COORDENADORES_GRADUACAO);
		case NivelEnsino.STRICTO:
			setResultadosBusca(null);
			getCoordenadoresPrograma();	
			return forward(JSP_LISTA_COORDENADORES_STRICTO);
		case NivelEnsino.TECNICO:
			return forward(JSP_LISTA_COORDENADORES_TECNICO);
		case NivelEnsino.LATO:
			initObj();
			if (getParameterBoolean("logarComo"))
				opcaoLogarComo = true;
			return forward(JSP_LISTA_COORDENADORES_LATO);
		case NivelEnsino.RESIDENCIA:
			setResultadosBusca(null);
			getCoordenadoresProgramaResidencia();
			return forward(JSP_LISTA_COORDENADORES_STRICTO);
		default:
			addMensagemErro("O nível de ensino não está definido ou não possui coordenadores de curso a listar.");
			return cancelar();
		}
	}
	/**
	 * Permite um gestor de Lato Sensu, logar com outro usuário, desde que o mesmo seja um
	 * Coordenador de um Curso Lato Sensu.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/administracao.jsp</li>
	 *	</ul>
	 * 
	 */
	public String logarComo() throws ArqException, RemoteException{
		String login = null;
		UsuarioMBean mBean = getMBean("userBean");

		if (getParameter("login") == null) {
			addMensagemErro("Não foi possível logar como o coordenador, por favor, reinicie os procedimentos.");
			return null;
		
		} else {
			login = getParameter("login");
		
			mBean.getObj().setLogin( login );
			return mBean.logarComo();
		}
	}
	
	/**
	 * 
	 * Faz a busca de coordenador por um curso especificado. 
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/administracao.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 */
	public String iniciarBuscaCoordenadorPorCurso() {
		setResultadosBusca(null);
		return forward(ConstantesNavegacao.LISTA_COORDENADOR);
	}

	/** Substitui o coordenador de curso por outro.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino/coordenacao_curso/lista.jsp</li>
	 *	</ul>
	 * 
	 * @return {@value #JSP_SUBSTITUICAO}
	 * @throws ArqException
	 */
	public String substituirCoordenador() throws ArqException {
		setReadOnly(false);
		initObj();
		int id = getParameterInt("id");
		GenericDAO dao = getGenericDAO();
		coordenadorAntigo = dao.findByPrimaryKey(id, CoordenacaoCurso.class);
		
		if( !coordenadorAntigo.isAtivo() ){
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return cancelar();
		}
		
		obj.setEmailContato( coordenadorAntigo.getEmailContato() );
		obj.setTelefoneContato1( coordenadorAntigo.getTelefoneContato1() );
		obj.setTelefoneContato2( coordenadorAntigo.getTelefoneContato2() );
		obj.setRamalTelefone1( coordenadorAntigo.getRamalTelefone1() );
		obj.setRamalTelefone2( coordenadorAntigo.getRamalTelefone2() );
		obj.setCargoAcademico( coordenadorAntigo.getCargoAcademico() );
		obj.setPaginaOficialCoordenacao( coordenadorAntigo.getPaginaOficialCoordenacao() );

		if(coordenadorAntigo.getUnidade() != null)
			obj.setUnidade(coordenadorAntigo.getUnidade());
		else if(coordenadorAntigo.getCurso() != null)
			obj.setCurso(coordenadorAntigo.getCurso());

		setOperacaoAtiva(SigaaListaComando.SUBSTITUIR_COORDENADOR.getId());
		prepareMovimento(SigaaListaComando.SUBSTITUIR_COORDENADOR);
		return forward(JSP_SUBSTITUICAO);
	}

	/**
	 * Cancela uma coordenação de curso. 
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino/coordenacao_curso/lista_contato.jsp</li>
	 *		<li>sigaa.war/ensino/coordenacao_curso/lista.jsp</li>
	 *	</ul>
	 * 
	 * @return @see #buscar()
	 * @throws ArqException
	 */
	public String cancelarCoordenador() throws ArqException {
		initObj();
		setId();
		GenericDAO dao = getGenericDAO();
		obj = dao.findByPrimaryKey(obj.getId(), CoordenacaoCurso.class);
		
		if( !obj.isAtivo() ){
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return cancelar();
		}
		
		prepareMovimento(ArqListaComando.DESATIVAR);

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(ArqListaComando.DESATIVAR);

		try {
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}

		return buscar();
	}

	/** Retorna o coordenador antigo do curso.
	 * @return
	 */
	public CoordenacaoCurso getCoordenadorAntigo() {
		return coordenadorAntigo;
	}

	/** Seta o coordenador antigo do curso.
	 * @param coordenadorAntigo
	 */
	public void setCoordenadorAntigo(CoordenacaoCurso coordenadorAntigo) {
		this.coordenadorAntigo = coordenadorAntigo;
	}

	/** Retorna uma lista de coordenação de curso do programa.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @return Lista de Coordenação de curso do programa.
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public Collection<CoordenacaoCurso> getCoordenadoresPrograma() throws DAOException, SegurancaException {
		
		if ( isEmpty(getResultadosBusca()) ) {
			checkRole(SigaaPapeis.PPG, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO);
			Integer idPrograma = null;
			if( isUserInRole( SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS ) && isPortalCoordenadorStricto() )
				idPrograma = getProgramaStricto().getId();
			
			CoordenacaoCursoDao dao = getDAO(CoordenacaoCursoDao.class);
			//setResultadosBusca( dao.findByPrograma(idPrograma, TipoUnidadeAcademica.PROGRAMA_POS, true, null) );
			List<Integer> idsPrograma = null;
			if( !isEmpty(idPrograma) ){
				idsPrograma = new ArrayList<Integer>();
				idsPrograma.add(idPrograma);
			}
			setResultadosBusca( dao.findProgramaECoordenacoes(idsPrograma, TipoUnidadeAcademica.PROGRAMA_POS, true, null) );
		}
		return getResultadosBusca();
	}
	
	/** Retorna uma lista de coordenação de curso do programa de residência.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @return Lista de Coordenação de curso do programa.
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public Collection<CoordenacaoCurso> getCoordenadoresProgramaResidencia() throws DAOException, SegurancaException {
		
		if ( isEmpty(getResultadosBusca()) ) {
			checkRole(SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, SigaaPapeis.SECRETARIA_RESIDENCIA, SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA);
		
			List<Integer> idsPrograma = new ArrayList<Integer>();
			
			if( isUserInRole( SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA ) ){
				idsPrograma.add(getProgramaResidencia().getId());
			}else if( isUserInRole( SigaaPapeis.SECRETARIA_RESIDENCIA) ){
				for (Unidade u : getAcessoMenu().getResidencias()) {
					idsPrograma.add(u.getId());
				}
			}
			
			if( isEmpty(idsPrograma.size() == 0 ))
				idsPrograma = null;
			
			CoordenacaoCursoDao dao = getDAO(CoordenacaoCursoDao.class);
			//setResultadosBusca( dao.findByProgramas(idsPrograma, TipoUnidadeAcademica.PROGRAMA_RESIDENCIA, true, null) );
			setResultadosBusca( dao.findProgramaECoordenacoes(idsPrograma, TipoUnidadeAcademica.PROGRAMA_RESIDENCIA, true, null) );
			
		}
		return getResultadosBusca();
	}
	
	/** Retorna uma lista de coordenadores Lato Senso.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public Collection<CoordenacaoCurso> getCoordenadoresLato() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.GESTOR_LATO);
		CoordenacaoCursoDao dao = getDAO(CoordenacaoCursoDao.class);

		if (coordenadores == null || coordenadores.isEmpty())
			coordenadores = dao.findAllLatoSensu(true, null);
		
		return coordenadores; 
	}
	
	/** Retorna uma lista de coordenadores Lato Senso.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @throws ArqException
	 */
	public Collection<CoordenacaoCurso> getCoordenadoresTecnico() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_TECNICO);
		
		CoordenacaoCursoDao dao = getDAO(CoordenacaoCursoDao.class);
		return dao.findAllTecnico(getUnidadeGestora(), true, null);
	}
	
	/** 
	 * Retorna a lista de coordenadores de cursos, ordenado por curso e nome.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @return Lista de coordenadores de curso.
	 * @throws DAOException
	 */
	public Collection <CoordenacaoCurso> getCoordenadoresCursos() throws DAOException {
		CoordenacaoCursoDao dao = getDAO(CoordenacaoCursoDao.class);
		return dao.findAllAtivosOrdenadoCursoNome();
	}
	/**
	 * Inicia a operação de alterar email e telefone de contato da coordenação
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/graduacao/menu_coordenador.jsp</li>
	 *		<li>sigaa.war/stricto/menu_coordenador.jsp</li>
	 *	</ul>
	 * 
	 * @return {@value #JSP_CONTATO}
	 * 
	 * @throws ArqException
	 */
	public String iniciarAlterarContatos() throws ArqException{
		checkRole( SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO,
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS);
		setReadOnly(false);
		CoordenacaoCursoDao dao = getDAO(CoordenacaoCursoDao.class);
		
		if( getCursoAtualCoordenacao() != null )
			obj = dao.findUltimaByServidorCurso(getServidorUsuario(), getCursoAtualCoordenacao());
		else if( getProgramaStricto() != null )
			obj = dao.findUltimaByServidorPrograma(getServidorUsuario(), getProgramaStricto());
		
		if( isEmpty(obj) ){
			addMensagemErro("Apenas coordenadores de curso podem realizar esta operação");
			return null;
		}
		
		prepareMovimento(SigaaListaComando.ALTERAR_CONTATOS_COORDENADOR);
		return forward(JSP_CONTATO) ;
	}

	/**
	 * Grava as informações de contato da coordenação no banco.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino/coordenacao_curso/contato.jsp</li>
	 *	</ul>
	 * 
	 * @return @see #cancelar()
	 * 
	 * @throws ArqException
	 */
	public String gravarContatos() throws ArqException{

		ListaMensagens lista = new ListaMensagens();
		if (("").equals(obj.getEmailContato()) || obj.getEmailContato() == null) {
			validateEmailRequired(null, "E-mail", lista);
		}
		if (("").equals(obj.getTelefoneContato1()) || obj.getTelefoneContato1() == null) {
			validateRequired(null, "Telefone 1", lista);
		}
		
		if(lista.size()>0){
			addMensagens(lista);
			return null;
		}

		CoordenacaoCursoMov mov = new CoordenacaoCursoMov();
		mov.setCodMovimento( SigaaListaComando.ALTERAR_CONTATOS_COORDENADOR );
		mov.setCoordenador(obj);

		try {
			execute(mov);
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		} catch (NegocioException e) {
			addMensagens( e.getListaMensagens() );
			return null;
		}

		return cancelar();
	}

	/** Listener que carrega os dados do contato do coordenador quando muda o curso.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino/coordenacao_curso/form.jsp</li>
	 *	</ul>
	 * 
	 * @param evt Valor do novo idCurso
	 * 
	 * @throws DAOException
	 */
	public void carregarDadosContato(ValueChangeEvent evt) throws DAOException {
		Integer id = (Integer)evt.getNewValue();

		if( isEmpty(id) )
			return;

		CoordenacaoCursoDao dao = getDAO( CoordenacaoCursoDao.class );
		List<CoordenacaoCurso> coordenacoes = (List<CoordenacaoCurso>) dao.findByCurso(id, 0, (char) 0, null);
		if( !isEmpty(coordenacoes) ){

			Collections.sort( coordenacoes, new Comparator<CoordenacaoCurso>(){
				public int compare(CoordenacaoCurso cc1, CoordenacaoCurso cc2) {
					return cc2.getDataInicioMandato().compareTo(cc1.getDataInicioMandato());
				}
			});

			CoordenacaoCurso cc = coordenacoes.iterator().next();

			obj.setEmailContato(cc.getEmailContato());
			obj.setTelefoneContato1(cc.getTelefoneContato1());
			obj.setTelefoneContato2(cc.getTelefoneContato2());
			obj.setRamalTelefone1( cc.getRamalTelefone1() );
			obj.setRamalTelefone2( cc.getRamalTelefone2() );
			obj.setPaginaOficialCoordenacao(cc.getPaginaOficialCoordenacao());
		}
	}
	
	/** Retorna o conteúdo utilizado no formulário de acordo com o subsistema vigente.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino/coordenacao_curso/form.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 */
	public String getRotuloFormulario(){
		StringBuilder sb = new StringBuilder();
		sb.append("Identificar coordenador de ");
		switch (getNivelEnsino()){
		case NivelEnsino.GRADUACAO:
			sb.append("curso de graduação");
			break;
		case NivelEnsino.STRICTO:
			sb.append("programa de pós-graduação");
			break;
		case NivelEnsino.LATO:
			sb.append("curso de especialização");
			break;
		case NivelEnsino.TECNICO:
			sb.append("curso técnico");
			break;
		case NivelEnsino.RESIDENCIA:
			sb.append("programa de residência");
			break;
		default:
			sb.append("curso");
		}
		return sb.toString();
	}

	public boolean isOpcaoLogarComo() {
		return opcaoLogarComo;
	}

	public void setOpcaoLogarComo(boolean opcaoLogarComo) {
		this.opcaoLogarComo = opcaoLogarComo;
	}

	/** Retorna quantidade de convênios pendente de análise.
	 * @return
	 */
	public String getQtdConveniosPendentesAnalise() {
		GenericDAO dao = getGenericDAO();
		Integer status[] = {StatusEstagio.EM_ANALISE, StatusEstagio.SOLICITADO_CANCELAMENTO};
		int pendente = dao.count("select count(*) from estagio.estagiario" +
				" inner join discente using (id_discente)" +
				" where id_curso = " + getCursoAtualCoordenacao().getId()+
				" and estagiario.status in " + UFRNUtils.gerarStringIn(status));
		if (pendente > 0)
			return "(" + pendente + " pend. análise)";
		else
			return "";
	}
}