/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 27/04/2007
 *
 */
package br.ufrn.sigaa.ensino.jsf;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.sigaa.arq.dao.ensino.SecretariaUnidadeDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.SecretariaUnidade;
import br.ufrn.sigaa.ensino.negocio.dominio.SecretariaUnidadeMov;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * MBean para realizar manutenção de coordenadores e secretários
 *
 * @author leonardo
 *
 */
@Component("secretariaUnidade")
@Scope("session")
public class SecretariaUnidadeMBean extends SigaaAbstractController<SecretariaUnidade> {

	/** Constante que define o caminho do formulário de identificação de secretário. */
	public static final String JSP_FORM = "/ensino/secretaria_unidade/form.jsp";
	
	/** Constante que define o caminho do formulário de substituição de secretário. */
	public static final String JSP_SUBSTITUICAO = "/ensino/secretaria_unidade/substituicao.jsp";

	/** Atributo que define o antigo secretário no formulário de substituição. */
	private SecretariaUnidade secretarioAntigo;

	/** Atributo que possui as secretarias resultantes da consulta através do componente autocomplete.  */
	private Collection<SelectItem> secretarias = new ArrayList<SelectItem>();
	
	/** Campo para controlar se somente servidores serão aceitos como secretario */
	private boolean somenteServidores = true;

	/** Atributo que indica o tipo acadêmico da unidade. */
	private int tipoAcademico;
	
	/** Atributo utilizado nos caso de uso de substituição de para indicar que é substituição */
	private boolean substituicao = false;

	/**
	 * Retorna o tipo acadêmico da unidade.
	 * Método não invocado por JSP's.
	 * @return the tipoAcademico
	 */
	public int getTipoAcademico() {
		return tipoAcademico;
	}

	/**
	 * Seta o tipo acadêmico da unidade.
	 * Método não invocado por JSP's.
	 */
	public void setTipoAcademico(int tipoAcademico) {
		this.tipoAcademico = tipoAcademico;
	}

	/**
	 * Construtor da classe.
	 * Método não invocado por JSP's.
	 */
	public SecretariaUnidadeMBean(){
		initObj();
	}

	/**
	 * Método que inicializa os atributo envolvidos nos casos de uso gerenciados pela classe.
	 * Método não invocado por JSP's.
	 */
	private void initObj() {
		obj = new SecretariaUnidade();
		obj.setUsuario(new Usuario());
		somenteServidores = true;
	}

	/**
	 * Redireciona para página do formulário de substituição de secretário.
	 * Método não invocado por JSP's.
	 * @return
	 * @throws ArqException
	 */
	public String iniciarSubstituicao() throws ArqException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO,
				SigaaPapeis.PPG, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.COORDENADOR_TECNICO);
		substituicao = true;
		prepareMovimento(SigaaListaComando.SUBSTITUIR_SECRETARIO);
		secretarioAntigo = new SecretariaUnidade();
		secretarioAntigo.setFim( new Date() );
		return forward(JSP_SUBSTITUICAO);
	}

	/**
	 * Método que prepara o formulário de substituição de secretário dos departamentos
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/menus/administracao.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarSubstituicaoDepartamento() throws ArqException {
		initObj();
		tipoAcademico = TipoUnidadeAcademica.DEPARTAMENTO;
		obj.setUnidade(new Unidade());
		obj.setCurso(null);
		obj.setTipo(SecretariaUnidade.DEPARTAMENTO);
		return iniciarSubstituicao();
	}
	
	/**
	 * Método que prepara o formulário de substituição da coordenação de graduação.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/menus/administracao.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarSubstituicaoCoordenacao() throws ArqException {
		initObj();
		tipoAcademico = TipoUnidadeAcademica.COORDENACAO_CURSO;
		obj.setCurso(new Curso());
		obj.setUnidade(null);
		obj.setTipo(SecretariaUnidade.CURSO);
		return iniciarSubstituicao();
	}
	
	/**
	 * Método que prepara o formulário de substituição da secretaria de lato sensu.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/lato/menu_coordenador.jsp</li>
	 * <li>/WEB-INF/jsp/ensino/latosensu/menu/administracao.jsp</li>
	 * <li>/WEB-INF/jsp/menus/menu_lato_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarSubstituicaoCoordenacaoLato() throws ArqException {
		initObj();
		tipoAcademico = TipoUnidadeAcademica.COORDENACAO_CURSO_LATO;
		obj.setCurso(new Curso());
		obj.setUnidade(null);
		obj.setTipo(SecretariaUnidade.CURSO);
		return iniciarSubstituicao();
	}

	/**
	 * Método que prepara o formulário de substituição do secretário de centro.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <br />
	 * <ul>
	 * <li>/graduacao/menus/administracao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarSubstituicaoCentro() throws ArqException {
		initObj();
		tipoAcademico = TipoUnidadeAcademica.CENTRO;
		obj.setUnidade( new Unidade() );
		obj.setCurso( null );
		obj.setTipo(SecretariaUnidade.CENTRO);
		return iniciarSubstituicao();
	}

	/**
	 * Método que prepara o formulário de substituição do secretário do programa.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <br />
	 * <ul>
	 * <li>/stricto/menus/permissao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarSubstituicaoProgramaPos() throws ArqException {
		initObj();
		tipoAcademico = TipoUnidadeAcademica.PROGRAMA_POS;
		obj.setUnidade( new Unidade() );
		obj.setCurso( null );
		obj.setTipo(SecretariaUnidade.PROGRAMA);
		return iniciarSubstituicao();
	}
	
	/**
	 * Método que prepara o formulário de substituição da secretaria de ténico.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/turma.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarSubstituicaoCoordenacaoTecnico() throws ArqException{
		initObj();
		tipoAcademico = TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA;
		obj.setUnidade(new Unidade());
		obj.setTipo(SecretariaUnidade.UNID_ACADEMICA_ESPECIALIZADA);
		return iniciarSubstituicao();
	}
	
	/**
	 * Método que prepara o formulário de substituição do secretário de unidade acadêmica especializada.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <br />
	 * <ul>
	 * <li>/graduacao/menus/administracao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarSubstituicaoUnidadeEspecializada() throws ArqException {
		initObj();
		tipoAcademico = TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA;
		obj.setUnidade( new Unidade() );
		obj.setCurso( null );
		obj.setTipo(SecretariaUnidade.UNID_ACADEMICA_ESPECIALIZADA);
		return iniciarSubstituicao();
	}

	/**
	 * Prepara os atributos para o formulário de identificação de secretário.
	 * Método não invocado por JSP's.
	 * @return
	 */
	private String iniciar(){
		setConfirmButton("Confirmar");
		substituicao = false;
		secretarioAntigo = null;
		try {
			prepareMovimento(SigaaListaComando.IDENTIFICAR_SECRETARIO);
		} catch (ArqException e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro("Ocorreu um erro durante a preparação para esta operação.");
			return null;
		}
		return forward(JSP_FORM);
	}

	/**
	 * Prepara os atributos para o formulário de identificação de secretário de curso de graduação.
	 * Chamado por:
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/administracao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarCoordenacao() {
		initObj();
		substituicao = false;
		tipoAcademico = TipoUnidadeAcademica.COORDENACAO_CURSO;
		obj.setCurso(new Curso());
		obj.setTipo(SecretariaUnidade.CURSO);
		somenteServidores = false;
		return iniciar();
	}
	
	/**
	 * Prepara os atributos para o formulário de identificação de secretário de curso de lato sensu.
	 * Chamado por:
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/menu_coordenador.jsp</li>
	 * <li>/WEB-INF/jsp/ensino/latosensu/menu/administracao.jsp</li>
	 * <li>/WEB-INF/jsp/menus/menu_lato_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarCoordenacaoLato() {
		initObj();
		substituicao = false;
		tipoAcademico = TipoUnidadeAcademica.COORDENACAO_CURSO_LATO;
		obj.setCurso(new Curso());
		obj.setTipo(SecretariaUnidade.CURSO);
		return iniciar();
	}

	/**
	 * Prepara os atributos para o formulário de identificação de secretário de curso de stricto sensu.
	 * Chamado por:
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/menus/permissao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarProgramaPos() {
		initObj();
		substituicao = false;
		tipoAcademico = TipoUnidadeAcademica.PROGRAMA_POS;
		obj.setUnidade(new Unidade());
		obj.setTipo(SecretariaUnidade.PROGRAMA);
		return iniciar();
	}
	
	/**
	 * Prepara os atributos para o formulário de identificação de secretário de curso de técnico.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/turma.jsp</li>
	 *	</ul>
	 * @return
	 */
	public String iniciarCoordenacaoTecnico() {
		initObj();
		tipoAcademico = TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA;
		obj.setUnidade(new Unidade());
		obj.setTipo(SecretariaUnidade.UNID_ACADEMICA_ESPECIALIZADA);
		return iniciar();
	}

	/**
	 * Prepara os atributos para o formulário de identificação de secretário de curso de departamento.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/administracao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarDepartamento() {
		initObj();
		tipoAcademico = TipoUnidadeAcademica.DEPARTAMENTO;
		obj.setUnidade(new Unidade());
		obj.setTipo(SecretariaUnidade.DEPARTAMENTO);
		return iniciar();
	}

	/**
	 * Prepara os atributos para o formulário de identificação de secretário de curso de centro.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/administracao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarCentro() {
		initObj();
		tipoAcademico = TipoUnidadeAcademica.CENTRO;
		obj.setUnidade(new Unidade());
		obj.setTipo(SecretariaUnidade.CENTRO);
		return iniciar();
	}

	/**
	 * Prepara os atributos para o formulário de identificação de secretário de unidade acadêmica especializada.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/administracao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarUnidadeEspecializada() {
		initObj();
		tipoAcademico = TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA;
		obj.setUnidade(new Unidade());
		obj.setTipo(SecretariaUnidade.UNID_ACADEMICA_ESPECIALIZADA);
		return iniciar();
	}
	
	/**
	 * Retorna a descrição do tipo de secretaria nos formulário de identificação e substituição de secretário.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/secretaria_unidade/form.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/secretaria_unidade/substituicao.jsp</li>
	 * </ul>
	 * @return
	 */
	public String getTipoSecretaria() {
		if (isCoordenacao() || isCoordenacaoLato())
			return "Coordenação";
		if (isDepartamento())
			return "Departamento";
		if (isCentro())
			return "Centro";
		if (isProgramaPos())
			return "Programa de Pós";
		if (isCoordenacaoTecnico())
			return "Unidade Especializada";
		return "";
	}

	/**
	 * Verifica se a secretaria é de coordenação do curso de graduação
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/secretaria_unidade/form.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/secretaria_unidade/substituicao.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isCoordenacao() {
		return tipoAcademico == TipoUnidadeAcademica.COORDENACAO_CURSO;
	}

	/**
	 * Verifica se a secretaria é de coordenação do curso de lato
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li> /sigaa.war/ensino/secretaria_unidade/form.jsp</li>
	 * <li>/sigaa.war/ensino/secretaria_unidade/substituicao.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isCoordenacaoLato() {
		return tipoAcademico == TipoUnidadeAcademica.COORDENACAO_CURSO_LATO;
	}

	/**
	 * Verifica se a secretaria é de coordenação do curso de departamento
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li> /sigaa.war/ensino/secretaria_unidade/form.jsp</li>
	 * <li>/sigaa.war/ensino/secretaria_unidade/substituicao.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isDepartamento() {
		return tipoAcademico == TipoUnidadeAcademica.DEPARTAMENTO;
	}

	/**
	 * Verifica se a secretaria é de coordenação do curso de centro
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li> /sigaa.war/ensino/secretaria_unidade/form.jsp</li>
	 * <li>/sigaa.war/ensino/secretaria_unidade/substituicao.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isCentro() {
		return tipoAcademico == TipoUnidadeAcademica.CENTRO;
	}

	/**
	 * Verifica se a secretaria é de coordenação do curso de stricto sensu
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li> /sigaa.war/ensino/secretaria_unidade/form.jsp</li>
	 * <li>/sigaa.war/ensino/secretaria_unidade/substituicao.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isProgramaPos() {
		return tipoAcademico == TipoUnidadeAcademica.PROGRAMA_POS;
	}
	
	/**
	 * Verifica se a secretaria é de coordenação do curso de técnico
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li> /sigaa.war/ensino/secretaria_unidade/form.jsp</li>
	 * <li>/sigaa.war/ensino/secretaria_unidade/substituicao.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isCoordenacaoTecnico() {
		return tipoAcademico == TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA;
	}

	/**
	 * Cadastrar ou atualiza os dados preenchidos nos formulário de substituição ou identificação de secretário.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/secretaria_unidade/substituicao.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws NegocioException, ArqException {
		erros = new ListaMensagens();
		erros.addAll(obj.validate().getMensagens());


		
		
		/** VALIDAÇÃO */
		if ((isCoordenacao() || isCoordenacaoLato()) && obj.getCurso().getId() == 0)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Curso");
		else if (isCoordenacaoTecnico() && obj.getUnidade().getId() == 0)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Escola");
		else if (!isCoordenacao() && !isCoordenacaoLato() && obj.getUnidade().getId() == 0){
			if( obj.isCentro() )
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Centro");
			else
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Departamento");
		}
		else if (isCoordenacaoTecnico() && obj.getUnidade().getId() == 0)
			addMensagemErro("Informe a Unidade");

		if( isSubstituicao() ){
			if( secretarioAntigo == null || secretarioAntigo.getId() == 0 )
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Secretário(a) Atual");

			ValidatorUtil.validateRequired(secretarioAntigo.getFim(), "Date de Fim", erros);
		}

		if(hasErrors())
			return null;

		
		ServidorDao dao = getDAO(ServidorDao.class);
		
		Usuario usuario = dao.findByPrimaryKey(obj.getUsuario().getId(), Usuario.class);
		
		Collection<Servidor> servidores = dao.findByPessoaAndVinculos(usuario.getPessoa().getId(), Ativo.SERVIDOR_ATIVO);
		
		for (Servidor servidor : servidores) {
			obj.setServidor(servidor);
			break;
		}
		
		SecretariaUnidadeMov mov = new SecretariaUnidadeMov();
		String msgSucesso = null;
		if( isSubstituicao() ){
			mov.setCodMovimento(SigaaListaComando.SUBSTITUIR_SECRETARIO);
			msgSucesso = "Substituição de secretário realizada com sucesso.";
		}else{
			mov.setCodMovimento(SigaaListaComando.IDENTIFICAR_SECRETARIO);
			msgSucesso = "Identificação de secretário realizada com sucesso.";
		}
		mov.setSecretario(obj);
		mov.setSecretarioAntigo(secretarioAntigo);

		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
		} catch(NegocioException e){
			addMensagens(e.getListaMensagens());
			return null;
		}
		addMessage(msgSucesso, TipoMensagemUFRN.INFORMATION);

		if( mov.getCodMovimento() == SigaaListaComando.IDENTIFICAR_SECRETARIO ){
			if (isCoordenacao())
				return iniciarCoordenacao();
			else if( isCoordenacaoLato() )
				return iniciarCoordenacaoLato();
			else if( isCentro() )
				return iniciarCentro();
			else if (isDepartamento())
				return iniciarDepartamento();
			else if( isProgramaPos() )
				return iniciarProgramaPos();
			else if(isCoordenacaoTecnico())
				return redirect(getSubSistema().getLink());
			else
				return cancelar();
		} else{
			if (isCoordenacao())
				return iniciarSubstituicaoCoordenacao();
			else if(isCoordenacaoLato())
				return iniciarSubstituicaoCoordenacaoLato();
			else if( isCentro() )
				return iniciarSubstituicaoCentro();
			else if (isDepartamento())
				return iniciarSubstituicaoDepartamento();
			else if ( isProgramaPos() )
				return iniciarSubstituicaoProgramaPos();
			else if(isCoordenacaoTecnico())
				return redirect(getSubSistema().getLink());
			else
				return cancelar();
		}
	}

	/**
	 * Chama o processador para cancelar uma secretaria cadastrada errada
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/secretaria_unidade/secretarios_centro.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/secretaria_unidade/secretarios_curso.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/secretaria_unidade/secretarios_departamento.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/secretaria_unidade/secretarios_programa.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String cancelarSecretario() throws ArqException {
		Integer id = getParameterInt("id");
		if( id == null ){
			addMensagemErro("Seleciona a secretaria para cancelar");
			return null;
		}

		prepareMovimento(SigaaListaComando.CANCELAR_SECRETARIO);

		SecretariaUnidade secretaria = new SecretariaUnidade(id);
		SecretariaUnidadeMov mov = new SecretariaUnidadeMov();
		mov.setCodMovimento(SigaaListaComando.CANCELAR_SECRETARIO);
		mov.setSecretario(secretaria);

		try {
			execute(mov, getCurrentRequest());
			addMessage("Secretaria cancelada com sucesso!", TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens(e.getListaMensagens());
		} catch(Exception e){
			tratamentoErroPadrao(e);
		}

		return null;
	}

	/**
	 * Busca de secretários utilizada na busca do discente.
	 * Método não invocado por JSP's.
	 */
	@Override
	public String buscar() throws DAOException {
		String param = getParameter("paramBusca");
		if (param == null) {
			addMensagemErro("Selecione um tipo de busca e digite o parâmetro de busca");
			return null;
		}

		SecretariaUnidadeDao dao = getDAO(SecretariaUnidadeDao.class);
		if ("nome".equalsIgnoreCase(param))
			setResultadosBusca(dao.findByNomeUsuario(obj.getUsuario().getPessoa().getNome()));
		else if ("login".equalsIgnoreCase(param))
			setResultadosBusca(dao.findByLoginUsuario(obj.getUsuario().getLogin()));
		else
			setResultadosBusca(null);

		//initObj();
		return null;
	}

	/**
	 * Retorna todas as secretarias do curso de acordo com nível ensino.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/secretaria_unidade/secretarios_curso.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<SecretariaUnidade> getSecretariosCurso() throws DAOException {
		SecretariaUnidadeDao secretariaDao = getDAO(SecretariaUnidadeDao.class);
		return secretariaDao.findSecretariosCursoByNivel(getNivelEnsino());
	}

	/**
	 * Retorna todas as secretarias de departamento
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>SIGAA/app/sigaa.ear/sigaa.war/ensino/secretaria_unidade/secretarios_curso.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<SecretariaUnidade> getSecretariosDepartamento() throws DAOException {
		SecretariaUnidadeDao secretariaDao = getDAO(SecretariaUnidadeDao.class);
		return secretariaDao.findSecreatariosUnidade(TipoUnidadeAcademica.DEPARTAMENTO);
	}

	/**
	 * Retorna todas as secretarias de centros.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/secretaria_unidade/secretarios_curso.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<SecretariaUnidade> getSecretariosCentro() throws DAOException {
		SecretariaUnidadeDao secretariaDao = getDAO(SecretariaUnidadeDao.class);
		return secretariaDao.findSecreatariosUnidade(TipoUnidadeAcademica.CENTRO);
	}

	/**
	 * Retorna todas as secretarias dos programas.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/secretaria_unidade/secretarios_curso.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<SecretariaUnidade> getSecretariosPrograma() throws DAOException {
		SecretariaUnidadeDao secretariaDao = getDAO(SecretariaUnidadeDao.class);
		return secretariaDao.findSecreatariosUnidade(TipoUnidadeAcademica.PROGRAMA_POS);
	}
	
	/**
	 * Retorna todas as secretarias dos curso de técnico.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/secretaria_unidade/secretarios_curso.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<SecretariaUnidade> getSecretariosTecnico() throws DAOException {
		SecretariaUnidadeDao secretariaDao = getDAO(SecretariaUnidadeDao.class);
		return secretariaDao.findByUnidade(getUsuarioLogado().getVinculoAtivo().getUnidade().getId(),null);
	}
	
	/**
	 * Retorna todas as secretarias de centros.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/secretaria_unidade/secretarios_curso.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<SecretariaUnidade> getSecretariosUnidadeEspecializada() throws DAOException {
		SecretariaUnidadeDao secretariaDao = getDAO(SecretariaUnidadeDao.class);
		return secretariaDao.findSecreatariosUnidade(TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA);
	}

	/**
	 * Método disparado por evento que atualiza lista de SelectItem na JSP 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/secretaria_unidade/substituicao.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void carregarSecretarios(ValueChangeEvent e)  throws DAOException {
		SecretariaUnidadeDao dao = getDAO(SecretariaUnidadeDao.class);
		if (isCoordenacao() || isCoordenacaoLato()) {
			secretarias = toSelectItems(dao.findByCurso((Integer) e.getNewValue()), "id", "nome") ;
		} else {
			secretarias = toSelectItems(dao.findByUnidade((Integer) e.getNewValue(),null), "id", "nome") ;
		}
	}

	/**
	 * Método disparado por evento que atualiza lista de SelectItem na JSP com o secretário escolhido.
	 * Método não invocado por JSP's.
	 * @param e
	 * @throws DAOException
	 */
	public void carregarSecretarioEscolhido(ValueChangeEvent e) throws DAOException {
		secretarioAntigo = getGenericDAO().findByPrimaryKey(secretarioAntigo.getId()
				, SecretariaUnidade.class);
	}

	/**
	 * Retorna o secretário escolhido
	 */
	public SecretariaUnidade getSecretarioEscolhido() {
		return secretarioAntigo;
	}

	/**
	 * Seta o secretário escolhido 
	 */
	public void setSecretarioEscolhido(SecretariaUnidade secretarioEscolhido) {
		this.secretarioAntigo = secretarioEscolhido;
	}

	/**
	 * Retorna as secretarias de acordo com o que foi preenchido no auto complete do formulário de identificação e substituição de secretário.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 */
	public Collection<SelectItem> getSecretarias() {
		return secretarias;
	}

	/**
	 * Define as secretarias de acordo com o que foi preenchido no auto complete do formulário de identificação e substituição de secretário.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 */
	public void setSecretarias(Collection<SelectItem> secretarias) {
		this.secretarias = secretarias;
	}

	public SecretariaUnidade getSecretarioAntigo() {
		return secretarioAntigo;
	}

	public void setSecretarioAntigo(SecretariaUnidade secretarioAntigo) {
		this.secretarioAntigo = secretarioAntigo;
	}

	public boolean isSubstituicao() {
		return substituicao;
	}

	public void setSubstituicao(boolean substituicao) {
		this.substituicao = substituicao;
	}

	public boolean isSomenteServidores() {
		return somenteServidores;
	}

	public void setSomenteServidores(boolean somenteServidores) {
		this.somenteServidores = somenteServidores;
	}
	
}
