/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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

	/** Nome do atributo de sessão que guarda os parâmetros da unidade gestora. */
	public static final String PARAMETROS_SESSAO = "paramGestora";

	/** Nome do atributo de sessão que guarda o calendário acadêmico. */
	public static final String CALENDARIO_SESSAO = "calendarioAcademico";

	/** Nome do atributo de sessão que guarda o curso atual do coordenador  de graduação */
	public static final String CURSO_ATUAL = "cursoAtual";

	/** Nome do atributo de sessão que guarda o programa atual do coordenador  de pós */
	public static final String PROGRAMA_ATUAL = "programaAtual";
	
	/** Nome do atributo de sessão que guarda o curso atual do coordenador de pós que está selecionado no portal do coordenador.
	 * Este atributo só existe quando o curso que o usuário coordena possui mais de um CalendarioAcademico ou ParametrosGestoraAcademica.
	 * Dessa forma o calendário ou parâmetro que será utilizado será o que está associado ao curso selecionado no portal */
	public static final String CURSO_ATUAL_COORDENADOR_STRICTO = "cursoAtualCoordStricto";
	
	/**
	 * Retorna o usuário logado
	 *
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Usuario getUsuarioLogado() {
		return super.getUsuarioLogado();
	}

	/**
	 * Retorna se o usuário está em um ambiente mobile
	 * @return
	 */
	public boolean isMobile(HttpServletRequest req) {
		return req.getSession(true).getAttribute("mobile") != null;
	}

	/**
	 * Retorna o servidor do usuário logado
	 * @return
	 */
	public Servidor getServidorUsuario() {
		return getUsuarioLogado().getServidorAtivo();
	}
	
	/**
	 * Retorna o docente externo do usuário logado
	 * @return
	 */
	public DocenteExterno getDocenteExternoUsuario() {
		return getUsuarioLogado().getDocenteExternoAtivo();
	}


	/**
	 * Retorna o aluno do usuário logado
	 * @return
	 * @throws DAOException
	 */
	public DiscenteAdapter getDiscenteUsuario() {
		return getUsuarioLogado().getDiscenteAtivo();
	}

	/**
	 * Retorna o nível de ensino em uso no subsistema atual
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
	
	/** Retorna a descrição do nível de ensino em uso no subsistema atual
	 * @return
	 */
	public String getNivelDescricao() {
		return NivelEnsino.getDescricao(getNivelEnsino());
	}
	
	/**
	 * Retorna os níveis de ensino que o usuário pode operar quando registra diplomas/certificado.
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
	 * Retorna os níveis de ensino que o usuário pode operar quando registra diplomas/certificado.
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
	 * Retorna uma coleção de selectItem de níveis de ensino que o usuário pode operar.
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
	 * Retorna o id da unidade gestora acadêmica atual
	 * @return
	 * @throws ArqException
	 */
	public int getUnidadeGestora() throws ArqException {

		try {
			return getParametrosAcademicos().getUnidade().getId();
		} catch (Exception e) {
			throw new ArqException("É necessário unidade acadêmica definida para acessar essa operação");
		}
	}

	/**
	 * Retorna a unidade que o usuário logado possui algum nível de responsabilidade
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
	 * Retorna o calendário vigente para as operações
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
			throw new RuntimeNegocioException("É necessário que o calendário acadêmico esteja definido para realizar esta operação.");
		}
		return cal;
	}

	/**
	 * Checa se o usuário é docente
	 * 
	 * @throws SegurancaException
	 */
	public void checkDocenteRole() throws SegurancaException {
		if (getServidorUsuario() != null && getServidorUsuario().getCategoria().getId() != Categoria.DOCENTE)
			throw new SegurancaException("Somente docentes podem realizar esta operação");
		else if(getServidorUsuario() == null && getDocenteExternoUsuario() == null)
			throw new SegurancaException("Somente docentes podem realizar esta operação");
	}

	

	/**
	 * Retorna os parâmetros da gestora acadêmica em questão
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
	 * Retorna a entidade DadosAcesso, que contém as informações da sessão do usuário, com informações de contexto
	 * @return
	 */
	public DadosAcesso getAcessoMenu() {
		return (DadosAcesso) getCurrentSession().getAttribute("acesso");
	}

	/**
	 * Método a ser reescrito para verificar os papéis do usuário logado.
	 * @return
	 * @throws SegurancaException
	 */
	public String getCheckRole() throws SegurancaException {
		return "";
	}

	/**
	 * Verifica se os dados do usuário bate com do usuário logado.
	 * Deve ser chamado em casos de uso que precisam de confirmação dos dados pessoais após a sua execução
	 * @return
	 * @throws ArqException 
	 */
	public boolean confirmaSenha() throws ArqException{

		ConfirmaSenhaMBean confirma = (ConfirmaSenhaMBean) getMBean("confirmaSenha");
		if( confirma == null )
			return false;
		String msgDadosIncorretos = "Dados Incorretos, a operação não foi realizada.";
		String param = getCurrentRequest().getParameter("apenasSenha");
		confirma.setApenasSenha( Boolean.valueOf(param) ) ;

		if( isEmpty( confirma.getSenha() ) ) {
			if( confirma.isApenasSenha() )
				addMensagemErro("Informe a Senha");
			else
				addMensagemErro("Informe os dados de confirmação.");
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
				addMensagemErro("A identidade não está informada em seu cadastro. Procure a coordenação de curso para atualização.");
			}

			if ( confirma.isShowDataNascimento() && getUsuarioLogado().getPessoa().getDataNascimento() == null ) {
				addMensagemErro("A data de nascimento não está informada em seu cadastro. Procure a coordenação de curso para atualização.");
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
			msgDadosIncorretos = "Senha Incorreta, a operação não foi realizada.";
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
	 * Retorna todos os cursos do nível de ensino atual (definido pelo portal no qual o usuário 
	 * logado se encontra no momento) coordenados pelo usuário logado
	 * (coordenador ou secretário do curso).
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
	 * Retorna o Programa de Pós que o Usuário logado coordena
	 * @return
	 */
	public Unidade getProgramaStricto() {
		return SigaaHelper.getProgramaStricto();
	}
	
	
	
	
	/**
	 * Retorna o curso atual do coordenador de pós que está selecionado no portal do coordenador.
	 * Este atributo só existe quando o curso que o usuário coordena possui mais de um CalendarioAcademico ou ParametrosGestoraAcademica.
	 * Dessa forma o calendário ou parâmetro que será utilizado será o que está associado ao curso selecionado no portal 
	 * @return
	 */
	public Curso getCursoAtualCoordenadroStricto() {
		if (getCurrentSession().getAttribute(CURSO_ATUAL_COORDENADOR_STRICTO)!= null)
			return (Curso) getCurrentSession().getAttribute(CURSO_ATUAL_COORDENADOR_STRICTO);
		else
			return null;
	}
	
	/**
	 * Retorna o Programa de Residência Médica que o Usuário logado coordena
	 * @return
	 */
	public Unidade getProgramaResidencia() {
		if (getCurrentSession().getAttribute(PROGRAMA_ATUAL)!= null)
			return (Unidade) getCurrentSession().getAttribute(PROGRAMA_ATUAL);
		else
			return null;
	}

	/**
	 * Remove o elemento da posição indicada da coleção
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
	 * Retorna o tutor do usuário logado 
	 * 
	 * @return
	 * @throws DAOException
	 */
	public TutorOrientador getTutorUsuario() {
		return getUsuarioLogado().getVinculoAtivo().getTutor();
	}

	/**
	 * Limpa a sessão web
	 */
	public void clearSessionWEB() {
		SigaaUtils.clearSessionWEB(getCurrentRequest());
	}

	/**
	 * Diz se o usuário encontra-se no portal do discente
	 * 
	 * @return
	 */
	public boolean isPortalDiscente(){
		return SigaaSubsistemas.PORTAL_DISCENTE.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usuário encontra-se na turma Virtual
	 * 
	 * @return
	 */
	public boolean isTurmaVirtual(){
		return SigaaSubsistemas.PORTAL_TURMA.getId() == getSubSistema().getId();
	}


	/**
	 * Diz se o usuário encontra-se no portal do docente
	 * 
	 * @return
	 */
	public boolean isPortalDocente(){
		return SigaaSubsistemas.PORTAL_DOCENTE.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usuário encontra-se no portal de coordenação de graduação 
	 * @return
	 */
	public boolean isPortalCoordenadorGraduacao(){
		return SigaaSubsistemas.PORTAL_COORDENADOR.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usuário encontra-se no portal de coordenação de programa de pós STRICTO
	 * @return
	 */
	public boolean isPortalCoordenadorStricto(){
		return SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usuário encontra-se no portal de coordenação de programa de pós LATO SENSU
	 * @return
	 */
	public boolean isPortalCoordenadorLato(){
		return SigaaSubsistemas.PORTAL_COORDENADOR_LATO.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usuário encontra-se no portal de coordenação de curso de rede 
	 * @return
	 */
	public boolean isPortalCoordenadorEnsinoRede(){
		return SigaaSubsistemas.PORTAL_ENSINO_REDE.getId() == getSubSistema().getId();
	}

	/**
	 * Diz se o usuário encontra-se no módulo de curso de rede 
	 * @return
	 */
	public boolean isPortalEnsinoRede() {
		return SigaaSubsistemas.ENSINO_REDE.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usuário encontra-se no portal da PPG
	 * @return
	 */
	public boolean isPortalPpg(){
		return SigaaSubsistemas.STRICTO_SENSU.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usuário encontra-se no portal do Complexo Hospitalar
	 * @return
	 */
	public boolean isPortalComplexoHospitalar(){
		return SigaaSubsistemas.COMPLEXO_HOSPITALAR.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usuário encontra-se no portal do Lato Sensu.
	 * @return
	 */
	public boolean isPortalLatoSensu(){
		return SigaaSubsistemas.LATO_SENSU.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usuário encontra-se no portal do Lato Sensu
	 * @return
	 */
	public boolean isLatoSensu(){
		return SigaaSubsistemas.LATO_SENSU.getId() == getSubSistema().getId() ||
			SigaaSubsistemas.PORTAL_COORDENADOR_LATO.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usuário encontra-se no portal Técnico
	 * @return
	 */
	public boolean isTecnico(){
		return SigaaSubsistemas.TECNICO.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usuário encontra-se no portal Formação Complementar
	 * @return
	 */
	public boolean isFormacaoComplementar(){
		return SigaaSubsistemas.FORMACAO_COMPLEMENTAR.getId() == getSubSistema().getId();
	}

	/**
	 * Diz se o usuário encontra-se no portal administrativo da graduação
	 * @return
	 */
	public boolean isPortalGraduacao(){
		return SigaaSubsistemas.GRADUACAO.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usuário encontra-se no portal do menu técnico
	 * @return
	 */
	public boolean isMenuTecnico(){
		return SigaaSubsistemas.TECNICO.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usuário encontra-se no portal da Avaliação Institucional
	 * @return
	 */
	public boolean isPortalAvaliacaoInstitucional(){
		return SigaaSubsistemas.PORTAL_AVALIACAO_INSTITUCIONAL.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usuário encontra-se no portal de Pesquisa
	 * @return
	 */
	public boolean isPesquisa(){
		return SigaaSubsistemas.PESQUISA.getId() == getSubSistema().getId();
	}

	/**
	 * Diz se o usuário encontra-se no portal de Monitoria
	 * @return
	 */
	public boolean isMonitoria(){
		return SigaaSubsistemas.MONITORIA.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usuário encontra-se no portal de Pesquisa
	 * @return
	 */
	public boolean isExtensao(){
		return SigaaSubsistemas.EXTENSAO.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usuário encontra-se no portal de Pesquisa
	 * @return
	 */
	public boolean isAcoesIntegradas(){
		return SigaaSubsistemas.ACOES_ASSOCIADAS.getId() == getSubSistema().getId();
	}

	/**
	 * Diz se o usuário encontra-se no portal da Reitoria
	 * @return
	 */
	public boolean isPortalReitoria(){
		return SigaaSubsistemas.PORTAL_PLANEJAMENTO.getId() == getSubSistema().getId();
	}	
	
	/**
	 * Diz se o usuário encontra-se no portal de Estágio da Empresa
	 * @return
	 */
	public boolean isPortalConcedenteEstagio(){
		return SigaaSubsistemas.PORTAL_CONCEDENTE_ESTAGIO.getId() == getSubSistema().getId();
	}		
	
	/**
	 * Diz se o usuário encontra-se no módulo de Convênios de Estágio
	 * @return
	 */
	public boolean isConvenioEstagio(){
		return SigaaSubsistemas.CONVENIOS_ESTAGIO.getId() == getSubSistema().getId();
	}

	/**
	 * Diz se o usuário encontra-se no portal das Escolas Especializadas
	 * @return
	 */
	public boolean isPortalEscolasEspecializadas(){
		return SigaaSubsistemas.FORMACAO_COMPLEMENTAR.getId() == getSubSistema().getId();
	}		
	
	/**
	 * Diz se o usuário encontra-se no módulo do ensino médio.
	 * @return
	 */
	public boolean isMedio(){
		return SigaaSubsistemas.MEDIO.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usuário encontra-se no portal Assistência ao Estudante
	 * @return
	 */
	public boolean isProae(){
		
		if (getSubSistema() == null)
			return false;
		
		return SigaaSubsistemas.SAE.getId() == getSubSistema().getId();
	}		
	
	/**
	 * Carrega em sessão tanto os parâmetros como o calendário atual, baseados
	 * na unidade e nível de ensino do usuário
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
			// Se o usuário tem gestora acadêmica E (se ainda não foram carregados em sessão
			// os parâmetros e calendário da unidade acadêmica OU os parâmetros e calendário carregados são de um nível de ensino
			// diferente do setado no usuário)
			// E o nível de ensino do usuário é valido !
			if( usr.getUnidade() != null && usr.getUnidade().getGestoraAcademica() != null &&
					((param == null &&  cal == null) || (param != null && param.getNivel() != usr.getNivelEnsino() && cal.getNivel() != usr.getNivelEnsino())) &&
					NivelEnsino.isValido(usr.getNivelEnsino()) ) {
				request.getSession().setAttribute(PARAMETROS_SESSAO, ParametrosGestoraAcademicaHelper.getParametros(usr));
				request.getSession().setAttribute( CALENDARIO_SESSAO, CalendarioAcademicoHelper.getCalendario(usr));

			} else if ( usr.isUserInRole( new int[] { SigaaPapeis.CDP, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE } )  &&
					request.getSession().getAttribute(PARAMETROS_SESSAO) == null &&
					request.getSession().getAttribute(CALENDARIO_SESSAO) == null)  {

				// Caso o usuário seja habilitado no Sistema de Graduação
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
	 * Indica se o discente realizou adesão ao cadastro único
	 * 
	 * @return
	 * @throws DAOException
	 */
	public boolean isCadastroUnico() throws DAOException {
		AdesaoCadastroUnicoBolsaDao dao = getDAO(AdesaoCadastroUnicoBolsaDao.class);

		return dao.isAdesaoCadastroUnico(getDiscenteUsuario().getId(), getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo());
	}
	
	/**
	 * Indica se o aluno é prioritário no cadastro único
	 * 
	 * @return
	 * @throws DAOException
	 */
	public boolean isPrioritario(DiscenteAdapter d) throws DAOException {
		AdesaoCadastroUnicoBolsaDao dao = getDAO(AdesaoCadastroUnicoBolsaDao.class);
		
		return dao.isDiscentePrioritario(d, getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo());
	}
	
	/**
	 * Indica se o discente é prioritário (carente ou não) de acordo com o Ano-Período informado 
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