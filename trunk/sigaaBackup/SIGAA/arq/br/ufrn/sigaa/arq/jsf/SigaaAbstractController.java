/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 02/02/2009
 *
 */
package br.ufrn.sigaa.arq.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.RuntimeNegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.usuarios.UserAutenticacao;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.sigaa.arq.acesso.DadosAcesso;
import br.ufrn.sigaa.arq.dao.sae.AdesaoCadastroUnicoBolsaDao;
import br.ufrn.sigaa.arq.negocio.SigaaHelper;
import br.ufrn.sigaa.arq.util.SigaaUtils;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Classe base para todos os managed-bens do SIGAA.
 *
 * @author Gleydson
 *
 * @param <T>
 */
public class SigaaAbstractController<T> extends AbstractControllerCadastro<T> {

	/** Nome do atributo de sess�o que guarda os par�metros da unidade gestora. */
	public static final String PARAMETROS_SESSAO = "paramGestora";

	/** Nome do atributo de sess�o que guarda o calend�rio acad�mico. */
	public static final String CALENDARIO_SESSAO = "calendarioAcademico";

	/** Nome do atributo de sess�o que guarda o curso atual do coordenador  de gradua��o */
	public static final String CURSO_ATUAL = "cursoAtual";

	/** Nome do atributo de sess�o que guarda o programa atual do coordenador  de p�s */
	public static final String PROGRAMA_ATUAL = "programaAtual";
	
	/** Nome do atributo de sess�o que guarda o curso atual do coordenador de p�s que est� selecionado no portal do coordenador.
	 * Este atributo s� existe quando o curso que o usu�rio coordena possui mais de um CalendarioAcademico ou ParametrosGestoraAcademica.
	 * Dessa forma o calend�rio ou par�metro que ser� utilizado ser� o que est� associado ao curso selecionado no portal */
	public static final String CURSO_ATUAL_COORDENADOR_STRICTO = "cursoAtualCoordStricto";
	
	/**
	 * Retorna o usu�rio logado
	 *
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Usuario getUsuarioLogado() {
		return super.getUsuarioLogado();
	}

	/**
	 * Retorna se o usu�rio est� em um ambiente mobile
	 * @return
	 */
	public boolean isMobile(HttpServletRequest req) {
		return req.getSession(true).getAttribute("mobile") != null;
	}

	/**
	 * Retorna o servidor do usu�rio logado
	 * @return
	 */
	public Servidor getServidorUsuario() {
		return getUsuarioLogado().getServidorAtivo();
	}
	
	/**
	 * Retorna o docente externo do usu�rio logado
	 * @return
	 */
	public DocenteExterno getDocenteExternoUsuario() {
		return getUsuarioLogado().getDocenteExternoAtivo();
	}


	/**
	 * Retorna o aluno do usu�rio logado
	 * @return
	 * @throws DAOException
	 */
	public DiscenteAdapter getDiscenteUsuario() {
		return getUsuarioLogado().getDiscenteAtivo();
	}

	/**
	 * Retorna o n�vel de ensino em uso no subsistema atual
	 * @return
	 */
	public char getNivelEnsino() {
		char nivel =  SigaaSubsistemas.getNivelEnsino(getSubSistema());
		if (nivel == ' ') {
			Character nivelSession = (Character) getCurrentSession().getAttribute(
					"nivel");
			if (nivelSession != null)
				return nivelSession;
			else return ' ';
		}
		return nivel;
	}
	
	/**
	 * Retorna o tipo de edital em uso no subsistema atual
	 * @return
	 */
	public char getTipoEdital() {
		char tipo =  SigaaSubsistemas.getTipoEdital(getSubSistema());
		if (tipo == ' ') {
			Character tipoEditalSession = (Character) getCurrentSession().getAttribute(
					"tipoEdital");
			if (tipoEditalSession != null)
				return tipoEditalSession;
			else return ' ';
		}
		return tipo;
	}
	
	/** Retorna a descri��o do n�vel de ensino em uso no subsistema atual
	 * @return
	 */
	public String getNivelDescricao() {
		return NivelEnsino.getDescricao(getNivelEnsino());
	}
	
	/**
	 * Retorna os n�veis de ensino que o usu�rio pode operar quando registra diplomas/certificado.
	 * @return
	 */
	public char[] getNiveisHabilitados() {
		StringBuilder niveis = new StringBuilder();
		if (getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_DIPLOMAS_GRADUACAO))
			niveis.append(NivelEnsino.GRADUACAO);
		if (getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_DIPLOMAS_STRICTO))
			niveis.append(NivelEnsino.STRICTO);
		if (getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_DIPLOMAS_LATO))
			niveis.append(NivelEnsino.LATO);
		return niveis.toString().toCharArray();
	}
	
	/**
	 * Retorna os n�veis de ensino que o usu�rio pode operar quando registra diplomas/certificado.
	 * @return
	 */
	public String[] getNiveisHabilitadosDesc() {
		List<String> niveis = new ArrayList<String>();
		if (getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_DIPLOMAS_GRADUACAO))
			niveis.add(NivelEnsino.getDescricao(NivelEnsino.GRADUACAO));
		if (getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_DIPLOMAS_STRICTO))
			niveis.add(NivelEnsino.getDescricao(NivelEnsino.STRICTO));
		if (getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_DIPLOMAS_LATO))
			niveis.add(NivelEnsino.getDescricao(NivelEnsino.LATO));
		return niveis.toArray(new String[niveis.size()]);
	}
	
	/**
	 * Retorna uma cole��o de selectItem de n�veis de ensino que o usu�rio pode operar.
	 * @return
	 */
	public Collection<SelectItem> getNiveisHabilitadosCombo() {
		Collection<SelectItem> combo = new ArrayList<SelectItem>();
		for (Character c : getNiveisHabilitados()) {
			combo.add(new SelectItem(c, NivelEnsino.getDescricao(c)));
		}
		return combo;
	}
	
	/**
	 * Retorna o id da unidade gestora acad�mica atual
	 * @return
	 * @throws ArqException
	 */
	public int getUnidadeGestora() throws ArqException {

		try {
			return getParametrosAcademicos().getUnidade().getId();
		} catch (Exception e) {
			throw new ArqException("� necess�rio unidade acad�mica definida para acessar essa opera��o");
		}
	}

	/**
	 * Retorna a unidade que o usu�rio logado possui algum n�vel de responsabilidade
	 * @return
	 */
	public Unidade getUnidadeResponsabilidade() {
		if ( isPortalCoordenadorGraduacao() && isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO) )
			return getCursoAtualCoordenacao().getUnidadeCoordenacao();
		if ( ( isPortalGraduacao() || isPortalDocente() || isTurmaVirtual() ) && isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO) ){
			return getUsuarioLogado().getVinculoServidor().getUnidade();
			
		}	
		return null;
	}	
	
	/**
	 * Retorna o calend�rio vigente para as opera��es
	 *
	 * @return
	 * @throws DAOException 
	 */
	public CalendarioAcademico getCalendarioVigente() {
		CalendarioAcademico cal = (CalendarioAcademico) getCurrentSession().getAttribute("calendarioAcademico");
		if (cal == null)
			try {
				cal = CalendarioAcademicoHelper.getCalendario(getUsuarioLogado());
			} catch (Exception e) {
				e.printStackTrace();
			}

		if (cal == null) {
			throw new RuntimeNegocioException("� necess�rio que o calend�rio acad�mico esteja definido para realizar esta opera��o.");
		}
		return cal;
	}

	/**
	 * Checa se o usu�rio � docente
	 * 
	 * @throws SegurancaException
	 */
	public void checkDocenteRole() throws SegurancaException {
		if (getServidorUsuario() != null && getServidorUsuario().getCategoria().getId() != Categoria.DOCENTE)
			throw new SegurancaException("Somente docentes podem realizar esta opera��o");
		else if(getServidorUsuario() == null && getDocenteExternoUsuario() == null)
			throw new SegurancaException("Somente docentes podem realizar esta opera��o");
	}

	

	/**
	 * Retorna os par�metros da gestora acad�mica em quest�o
	 *
	 * @return
	 * @throws DAOException
	 */
	public ParametrosGestoraAcademica getParametrosAcademicos() throws DAOException {

		if ( getCurrentSession().getAttribute(PARAMETROS_SESSAO) == null ) {
			return ParametrosGestoraAcademicaHelper.getParametros(getUsuarioLogado());
		} else {
			return (ParametrosGestoraAcademica) getCurrentSession().getAttribute(PARAMETROS_SESSAO);
		}
	}

	/**
	 * Retorna a entidade DadosAcesso, que cont�m as informa��es da sess�o do usu�rio, com informa��es de contexto
	 * @return
	 */
	public DadosAcesso getAcessoMenu() {
		return (DadosAcesso) getCurrentSession().getAttribute("acesso");
	}

	/**
	 * M�todo a ser reescrito para verificar os pap�is do usu�rio logado.
	 * @return
	 * @throws SegurancaException
	 */
	public String getCheckRole() throws SegurancaException {
		return "";
	}

	/**
	 * Verifica se os dados do usu�rio bate com do usu�rio logado.
	 * Deve ser chamado em casos de uso que precisam de confirma��o dos dados pessoais ap�s a sua execu��o
	 * @return
	 * @throws ArqException 
	 */
	public boolean confirmaSenha() throws ArqException{

		ConfirmaSenhaMBean confirma = (ConfirmaSenhaMBean) getMBean("confirmaSenha");
		if( confirma == null )
			return false;
		String msgDadosIncorretos = "Dados Incorretos, a opera��o n�o foi realizada.";
		String param = getCurrentRequest().getParameter("apenasSenha");
		confirma.setApenasSenha( Boolean.valueOf(param) ) ;

		if( isEmpty( confirma.getSenha() ) ) {
			if( confirma.isApenasSenha() )
				addMensagemErro("Informe a Senha");
			else
				addMensagemErro("Informe os dados de confirma��o.");
			return false;
		}

		if( !confirma.isApenasSenha() ){
			if( confirma.isShowDataNascimento() && confirma.getDataNascimento() == null ){
				addMensagemErro("Informe a Data de Nascimento.");
				return false;
			}

			if( confirma.isShowIdentidade() && confirma.getIdentidade().length() <= 0 ){
				addMensagemErro("Informe a Identidade.");
				return false;
			}

			if ( confirma.isShowIdentidade() && getUsuarioLogado().getPessoa().getIdentidade() == null ) {
				addMensagemErro("A identidade n�o est� informada em seu cadastro. Procure a coordena��o de curso para atualiza��o.");
			}

			if ( confirma.isShowDataNascimento() && getUsuarioLogado().getPessoa().getDataNascimento() == null ) {
				addMensagemErro("A data de nascimento n�o est� informada em seu cadastro. Procure a coordena��o de curso para atualiza��o.");
			}

			if( confirma.isShowIdentidade() &&
					getUsuarioLogado().getPessoa().getIdentidade() != null &&
					!confirma.getIdentidade().equals( getUsuarioLogado().getPessoa().getIdentidade().getNumero() ) ){
				addMensagemErro(msgDadosIncorretos);
				return false;
			}

			if( confirma.isShowDataNascimento() && getUsuarioLogado().getPessoa().getDataNascimento() != null && confirma.getDataNascimento().compareTo( getUsuarioLogado().getPessoa().getDataNascimento() ) != 0 ){
				addMensagemErro(msgDadosIncorretos);
				return false;
			}

		}else{
			msgDadosIncorretos = "Senha Incorreta, a opera��o n�o foi realizada.";
		}


		if( !UserAutenticacao.autenticaUsuario(getCurrentRequest(), getUsuarioLogado().getLogin(), confirma.getSenha()) ){
			addMensagemErro(msgDadosIncorretos);
			return false;
		}

		return true;

	}

	/**
	 * Retorna Curso atual manipulado pelo coordenador logado
	 * @return
	 */
	public Curso getCursoAtualCoordenacao() {
		
		return SigaaHelper.getCursoAtualCoordenacao();
		
	}
	
	/**
	 * Retorna todos os cursos do n�vel de ensino atual (definido pelo portal no qual o usu�rio 
	 * logado se encontra no momento) coordenados pelo usu�rio logado
	 * (coordenador ou secret�rio do curso).
	 * @return
	 */
	public Collection<Curso> getAllCursosCoordenacaoNivel() {
		
		char nivel = getNivelEnsino();
		
		Collection<Curso> cursos = new  ArrayList<Curso>();
		if( getAcessoMenu().getCursos() != null ) {
			for(Curso c : getAcessoMenu().getCursos() )
				if( c.getNivel() == nivel )	cursos.add(c);
		}
		return cursos;
	}

	/**
	 * Retorna o Programa de P�s que o Usu�rio logado coordena
	 * @return
	 */
	public Unidade getProgramaStricto() {
		return SigaaHelper.getProgramaStricto();
	}
	
	
	
	
	/**
	 * Retorna o curso atual do coordenador de p�s que est� selecionado no portal do coordenador.
	 * Este atributo s� existe quando o curso que o usu�rio coordena possui mais de um CalendarioAcademico ou ParametrosGestoraAcademica.
	 * Dessa forma o calend�rio ou par�metro que ser� utilizado ser� o que est� associado ao curso selecionado no portal 
	 * @return
	 */
	public Curso getCursoAtualCoordenadroStricto() {
		if (getCurrentSession().getAttribute(CURSO_ATUAL_COORDENADOR_STRICTO)!= null)
			return (Curso) getCurrentSession().getAttribute(CURSO_ATUAL_COORDENADOR_STRICTO);
		else
			return null;
	}
	
	/**
	 * Retorna o Programa de Resid�ncia M�dica que o Usu�rio logado coordena
	 * @return
	 */
	public Unidade getProgramaResidencia() {
		if (getCurrentSession().getAttribute(PROGRAMA_ATUAL)!= null)
			return (Unidade) getCurrentSession().getAttribute(PROGRAMA_ATUAL);
		else
			return null;
	}

	/**
	 * Remove o elemento da posi��o indicada da cole��o
	 * Deprecated. Usar UFRNUtils.removePorPosicao.
	 * @param col
	 * @param posicao
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public void removePorPosicao(Collection col, int posicao) {
		CollectionUtils.removePorPosicao(col, posicao);
	}

	/**
	 * Retorna o tutor do usu�rio logado 
	 * 
	 * @return
	 * @throws DAOException
	 */
	public TutorOrientador getTutorUsuario() {
		return getUsuarioLogado().getVinculoAtivo().getTutor();
	}

	/**
	 * Limpa a sess�o web
	 */
	public void clearSessionWEB() {
		SigaaUtils.clearSessionWEB(getCurrentRequest());
	}

	/**
	 * Diz se o usu�rio encontra-se no portal do discente
	 * 
	 * @return
	 */
	public boolean isPortalDiscente(){
		return SigaaSubsistemas.PORTAL_DISCENTE.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usu�rio encontra-se na turma Virtual
	 * 
	 * @return
	 */
	public boolean isTurmaVirtual(){
		return SigaaSubsistemas.PORTAL_TURMA.getId() == getSubSistema().getId();
	}


	/**
	 * Diz se o usu�rio encontra-se no portal do docente
	 * 
	 * @return
	 */
	public boolean isPortalDocente(){
		return SigaaSubsistemas.PORTAL_DOCENTE.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usu�rio encontra-se no portal de coordena��o de gradua��o 
	 * @return
	 */
	public boolean isPortalCoordenadorGraduacao(){
		return SigaaSubsistemas.PORTAL_COORDENADOR.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usu�rio encontra-se no portal de coordena��o de programa de p�s STRICTO
	 * @return
	 */
	public boolean isPortalCoordenadorStricto(){
		return SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usu�rio encontra-se no portal de coordena��o de programa de p�s LATO SENSU
	 * @return
	 */
	public boolean isPortalCoordenadorLato(){
		return SigaaSubsistemas.PORTAL_COORDENADOR_LATO.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usu�rio encontra-se no portal de coordena��o de curso de rede 
	 * @return
	 */
	public boolean isPortalCoordenadorEnsinoRede(){
		return SigaaSubsistemas.PORTAL_ENSINO_REDE.getId() == getSubSistema().getId();
	}

	/**
	 * Diz se o usu�rio encontra-se no m�dulo de curso de rede 
	 * @return
	 */
	public boolean isPortalEnsinoRede() {
		return SigaaSubsistemas.ENSINO_REDE.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usu�rio encontra-se no portal da PPG
	 * @return
	 */
	public boolean isPortalPpg(){
		return SigaaSubsistemas.STRICTO_SENSU.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usu�rio encontra-se no portal do Complexo Hospitalar
	 * @return
	 */
	public boolean isPortalComplexoHospitalar(){
		return SigaaSubsistemas.COMPLEXO_HOSPITALAR.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usu�rio encontra-se no portal do Lato Sensu.
	 * @return
	 */
	public boolean isPortalLatoSensu(){
		return SigaaSubsistemas.LATO_SENSU.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usu�rio encontra-se no portal do Lato Sensu
	 * @return
	 */
	public boolean isLatoSensu(){
		return SigaaSubsistemas.LATO_SENSU.getId() == getSubSistema().getId() ||
			SigaaSubsistemas.PORTAL_COORDENADOR_LATO.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usu�rio encontra-se no portal T�cnico
	 * @return
	 */
	public boolean isTecnico(){
		return SigaaSubsistemas.TECNICO.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usu�rio encontra-se no portal Forma��o Complementar
	 * @return
	 */
	public boolean isFormacaoComplementar(){
		return SigaaSubsistemas.FORMACAO_COMPLEMENTAR.getId() == getSubSistema().getId();
	}

	/**
	 * Diz se o usu�rio encontra-se no portal administrativo da gradua��o
	 * @return
	 */
	public boolean isPortalGraduacao(){
		return SigaaSubsistemas.GRADUACAO.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usu�rio encontra-se no portal do menu t�cnico
	 * @return
	 */
	public boolean isMenuTecnico(){
		return SigaaSubsistemas.TECNICO.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usu�rio encontra-se no portal da Avalia��o Institucional
	 * @return
	 */
	public boolean isPortalAvaliacaoInstitucional(){
		return SigaaSubsistemas.PORTAL_AVALIACAO_INSTITUCIONAL.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usu�rio encontra-se no portal de Pesquisa
	 * @return
	 */
	public boolean isPesquisa(){
		return SigaaSubsistemas.PESQUISA.getId() == getSubSistema().getId();
	}

	/**
	 * Diz se o usu�rio encontra-se no portal de Monitoria
	 * @return
	 */
	public boolean isMonitoria(){
		return SigaaSubsistemas.MONITORIA.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usu�rio encontra-se no portal de Pesquisa
	 * @return
	 */
	public boolean isExtensao(){
		return SigaaSubsistemas.EXTENSAO.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usu�rio encontra-se no portal de Pesquisa
	 * @return
	 */
	public boolean isAcoesIntegradas(){
		return SigaaSubsistemas.ACOES_ASSOCIADAS.getId() == getSubSistema().getId();
	}

	/**
	 * Diz se o usu�rio encontra-se no portal da Reitoria
	 * @return
	 */
	public boolean isPortalReitoria(){
		return SigaaSubsistemas.PORTAL_PLANEJAMENTO.getId() == getSubSistema().getId();
	}	
	
	/**
	 * Diz se o usu�rio encontra-se no portal de Est�gio da Empresa
	 * @return
	 */
	public boolean isPortalConcedenteEstagio(){
		return SigaaSubsistemas.PORTAL_CONCEDENTE_ESTAGIO.getId() == getSubSistema().getId();
	}		
	
	/**
	 * Diz se o usu�rio encontra-se no m�dulo de Conv�nios de Est�gio
	 * @return
	 */
	public boolean isConvenioEstagio(){
		return SigaaSubsistemas.CONVENIOS_ESTAGIO.getId() == getSubSistema().getId();
	}

	/**
	 * Diz se o usu�rio encontra-se no portal das Escolas Especializadas
	 * @return
	 */
	public boolean isPortalEscolasEspecializadas(){
		return SigaaSubsistemas.FORMACAO_COMPLEMENTAR.getId() == getSubSistema().getId();
	}		
	
	/**
	 * Diz se o usu�rio encontra-se no m�dulo do ensino m�dio.
	 * @return
	 */
	public boolean isMedio(){
		return SigaaSubsistemas.MEDIO.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usu�rio encontra-se no portal Assist�ncia ao Estudante
	 * @return
	 */
	public boolean isProae(){
		
		if (getSubSistema() == null)
			return false;
		
		return SigaaSubsistemas.SAE.getId() == getSubSistema().getId();
	}		
	
	/**
	 * Carrega em sess�o tanto os par�metros como o calend�rio atual, baseados
	 * na unidade e n�vel de ensino do usu�rio
	 *
	 * @param request
	 */
	public void carregarParametrosCalendarioAtual() {
		try {
			HttpServletRequest request = getCurrentRequest();
			Usuario usr = getUsuarioLogado();
			
			ParametrosGestoraAcademica param = (ParametrosGestoraAcademica) request.getSession().getAttribute(PARAMETROS_SESSAO);
			CalendarioAcademico cal = (CalendarioAcademico) request.getSession().getAttribute(CALENDARIO_SESSAO);
			
			if ( !NivelEnsino.isValido(usr.getNivelEnsino()) )
				usr.setNivelEnsino( NivelEnsino.GRADUACAO );
			// Se o usu�rio tem gestora acad�mica E (se ainda n�o foram carregados em sess�o
			// os par�metros e calend�rio da unidade acad�mica OU os par�metros e calend�rio carregados s�o de um n�vel de ensino
			// diferente do setado no usu�rio)
			// E o n�vel de ensino do usu�rio � valido !
			if( usr.getUnidade() != null && usr.getUnidade().getGestoraAcademica() != null &&
					((param == null &&  cal == null) || (param != null && param.getNivel() != usr.getNivelEnsino() && cal.getNivel() != usr.getNivelEnsino())) &&
					NivelEnsino.isValido(usr.getNivelEnsino()) ) {
				request.getSession().setAttribute(PARAMETROS_SESSAO, ParametrosGestoraAcademicaHelper.getParametros(usr));
				request.getSession().setAttribute( CALENDARIO_SESSAO, CalendarioAcademicoHelper.getCalendario(usr));

			} else if ( usr.isUserInRole( new int[] { SigaaPapeis.CDP, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE } )  &&
					request.getSession().getAttribute(PARAMETROS_SESSAO) == null &&
					request.getSession().getAttribute(CALENDARIO_SESSAO) == null)  {

				// Caso o usu�rio seja habilitado no Sistema de Gradua��o
				request.getSession().setAttribute(PARAMETROS_SESSAO, ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalGraduacao());
				request.getSession().setAttribute( CALENDARIO_SESSAO, CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad());

			} else if (usr.isUserInRole( new int[] { SigaaPapeis.SEDIS, SigaaPapeis.COORDENADOR_PEDAGOGICO_EAD, SigaaPapeis.COORDENADOR_TUTORIA_EAD } )  &&
				request.getSession().getAttribute(PARAMETROS_SESSAO) == null &&
				request.getSession().getAttribute(CALENDARIO_SESSAO) == null) {

				request.getSession().setAttribute(PARAMETROS_SESSAO, ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalGraduacao());
				request.getSession().setAttribute( CALENDARIO_SESSAO, CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad());
			} else if (usr.isUserInRole( new int[] { SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO }) &&
					request.getSession().getAttribute(PARAMETROS_SESSAO) == null){
				request.getSession().setAttribute(PARAMETROS_SESSAO, ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalLato());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Indica se o discente realizou ades�o ao cadastro �nico
	 * 
	 * @return
	 * @throws DAOException
	 */
	public boolean isCadastroUnico() throws DAOException {
		AdesaoCadastroUnicoBolsaDao dao = getDAO(AdesaoCadastroUnicoBolsaDao.class);

		return dao.isAdesaoCadastroUnico(getDiscenteUsuario().getId(), getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo());
	}
	
	/**
	 * Indica se o aluno � priorit�rio no cadastro �nico
	 * 
	 * @return
	 * @throws DAOException
	 */
	public boolean isPrioritario(DiscenteAdapter d) throws DAOException {
		AdesaoCadastroUnicoBolsaDao dao = getDAO(AdesaoCadastroUnicoBolsaDao.class);
		
		return dao.isDiscentePrioritario(d, getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo());
	}
	
	/**
	 * Indica se o discente � priorit�rio (carente ou n�o) de acordo com o Ano-Per�odo informado 
	 * @param d
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public boolean isPrioritario(Discente d, int ano, int periodo) throws DAOException {
		AdesaoCadastroUnicoBolsaDao dao = getDAO(AdesaoCadastroUnicoBolsaDao.class);
		return dao.isDiscentePrioritario(d, ano, periodo);
	}
	
}