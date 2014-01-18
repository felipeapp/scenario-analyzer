/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 09/11/2007
 *
 */

package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.JasperReportsUtil.getReportSIGAA;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;

import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.arquivos.AssinaturaDigitalService;
import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.seguranca.autenticacao.AutValidator;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.IndiceAcademicoDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.expressao.ExpressaoInvalidaException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Historico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;
import br.ufrn.sigaa.ead.negocio.EADHelper;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.CurriculoComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.IdiomasEnum;
import br.ufrn.sigaa.ensino.internacionalizacao.jsf.HistoricoTraducaoMBean;
import br.ufrn.sigaa.ensino.latosensu.dominio.DiscenteLato;
import br.ufrn.sigaa.ensino.metropoledigital.negocio.MetropoleDigitalHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.negocio.ProcessadorCalculaHistorico;
import br.ufrn.sigaa.ensino.stricto.dominio.AproveitamentoCredito;
import br.ufrn.sigaa.ensino.stricto.dominio.AreaConcentracao;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.mensagens.MensagensGerais;
import br.ufrn.sigaa.negocio.MovimentoCalculoHistorico;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Managed Bean para geração de histórico dos discentes. O Histórico é emitido
 * para todos os níveis de ensino.
 * 
 * @author David Pereira
 * 
 */
@Component("historico") @Scope("session")
public class HistoricoMBean extends SigaaAbstractController<DiscenteAdapter> implements OperadorDiscente, AutValidator {

	/** Comprovante de autenticidade do histórico. */
	private EmissaoDocumentoAutenticado comprovante;

	/** Indica se o usuário está verificando a autenticidade do documento. */
	private boolean verificando;

	/** Indica se o histórico é de discente EAD. */
	private boolean ead;

	/** Indica se o histórico será emitido no formato excel. */
	private boolean excel;
	
	/** Objeto da requisição do usuário. */  
	private HttpServletRequest request;
	/** Objeto de resposta do usuário. */
	private HttpServletResponse response;

	/** Usuário que está emitindo o diploma. */
	private Usuario usuarioLogado;

	/** 
	 * Indica se o usuário já está autenticado para emissão de Histórico de discente.
	 * Usuário redirecionado de outro sistema que não seja o SIGAA.
	 * Solicitado na #37969.
	**/
	private boolean autenticado;
	
	/** Idioma selecionado para ser utilizado na emissão do histórico com internacionalização.*/
	private String idioma;
	
	/** Atributo boleano para controlar a emissão de históricos com a listagem das ementas dos componentes.*/
	private boolean historicoComEmenta = false;
	
	/**
	 * Redireciona para a consulta geral de alunos, mas busca somente alunos de
	 * cursos de Ensino à Distância (EAD).<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String buscarDiscenteEad() throws SegurancaException {
		ead = true;
		return buscarDiscente();
	}

	/**
	 * Inicia a emissão do histórico no formato Excel.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/menus/administracao.jsp</li>
	 * </ul>
	 */
	public String buscarDiscenteExcel() throws SegurancaException {
		getCurrentSession().setAttribute("nivel", NivelEnsino.GRADUACAO);
		return buscarDiscente(true);
	}

	/**
	 * Redireciona para o Managed Bean para a busca de discentes de graduação.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * <li>/sigaa.war/stricto/menus/discente.jsp</li>
	 * <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * <li>/sigaa.war/lato/menu_coordenador.jsp</li>
	 * <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * <li>/sigaa.war/graduacao/menus/aluno.jsp</li>
	 * <li>/sigaa.war/graduacao/menus/cdp.jsp</li>
	 * <li>/sigaa.war/graduacao/menus/coordenacao.jsp</li>
	 * <li>/sigaa.war/graduacao/menus/diplomas.jsp</li>
	 * <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 */
	public String buscarDiscente() throws SegurancaException {
		this.historicoComEmenta = false;
		return buscarDiscente(false);
	}
	
	/**
	 * Inicia a emissão do histórico com a listagem com ementas dos componentes traduzidos.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/relacoes_internacionais/menu.jsp</li>
	 * </ul>
	 */
	public String buscarDiscenteHistoricoEmenta() throws SegurancaException {
		this.historicoComEmenta = true;
		return buscarDiscente(false);
	}
	
	/**
	 * Invoca o Mbean responsável por realizar a busca de discente.
	 * @param excel
	 * @return
	 * @throws SegurancaException
	 */
	private String buscarDiscente(boolean excel) throws SegurancaException {
		this.request = null;
		this.response = null;
		
		verificaAcesso();

		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.EMISSAO_HISTORICO);
		buscaDiscenteMBean.setEad(ead);

		this.excel = excel;		
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Verifica se o usuário que está buscando discente tem permissão.
	 * para isso.
	 */
	private void verificaAcesso() throws SegurancaException {
		if (getUsuarioLogado() != null && getUsuarioLogado().getDiscenteAtivo() == null && getSubSistema() != null) {
			
			if (getSubSistema().equals(SigaaSubsistemas.GRADUACAO)) {
				checkRole(new int[] { SigaaPapeis.COORDENADOR_CURSO,
						SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.DAE, SigaaPapeis.SECRETARIA_DEPARTAMENTO,
						SigaaPapeis.SECRETARIA_CENTRO, SigaaPapeis.CDP, SigaaPapeis.SECRETARIA_RESIDENCIA });
			} else if (getSubSistema().equals(SigaaSubsistemas.STRICTO_SENSU)) {
				checkRole(new int[] { SigaaPapeis.COORDENADOR_CURSO_STRICTO,
						SigaaPapeis.PPG, SigaaPapeis.SECRETARIA_POS });
			} else if (getSubSistema().equals(SigaaSubsistemas.SEDIS)) {
				checkRole(SigaaPapeis.COORDENADOR_GERAL_EAD, SigaaPapeis.SEDIS);
			} else if (getSubSistema().equals(SigaaSubsistemas.COMPLEXO_HOSPITALAR)){
				checkRole(SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, SigaaPapeis.SECRETARIA_RESIDENCIA,
						SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA);
			}
		}

	}

	/**
	 * Verifica se o acesso está sendo realizado através do portal público do sistema
	 * @return
	 */
	private boolean isAcessoPublico(){
		
		String reqURL = getCurrentRequest().getRequestURI();
		reqURL = reqURL.substring( getCurrentRequest().getContextPath().length() );
		
		if (getUsuarioLogado() == null && (reqURL.startsWith("/public/") || reqURL.startsWith("/mobile/touch/public")))
				return true;
		
		return false;
		
	}
	
	/**
	 * Verifica se o usuário que está tentando tirar o histórico tem permissão.
	 * para isso.
	 * @throws DAOException 
	 */
	private boolean verificaAcessoHistorico() throws DAOException {	
		
		//Solicitação da #37969: Permitir a geração de histórico quando chamado por GerarHistoricoAcessoExternoAction 
		//mesmo que nao tenha nenhum papel no SIGAA. Essa action é usada para a geração de históricos a partir do SIPAC.
		if (autenticado)
			return true;
		
		// (PÚBLICO)
		// Se o acesso for realizado através do portal público do sistema, na autenticação de documentos, não será necessário verificar o papel do usuário. 
		if(isAcessoPublico())
			return true;
		
		// (GRADUAÇÃO)
		// Permissão de Coordenador ou de Secretario: visualizar apenas históricos de discentes do curso relacionado.
		if (getSubSistema().equals(SigaaSubsistemas.GRADUACAO) || getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR)) {
			// Permissão da SECRETARIA DO CENTRO: visualizar apenas históricos ao centro que é vinculado. Para este caso
			// a busca de discentes já retorna apenas alunos dos respectivo centro de vinculação do usuário logado. 
			// Permissão de DAE ou CDP: visualizar qualquer histórico.
			if ((isUserInRole(new int[] { SigaaPapeis.CDP, SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE, 
					SigaaPapeis.CONSULTA_DADOS_GRADUACAO,SigaaPapeis.SECRETARIA_CENTRO,SigaaPapeis.SECRETARIA_DEPARTAMENTO  }))){
				return true;
			}
			else if (isUserInRole( new int[] { SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO })) {
				 if (!obj.isRegular()) //Se aluno ESPECIAL apenas DAE ou CDP pode visualizar. 
					 return false;	 
				 else if ( getCursoAtualCoordenacao() != null && getCursoAtualCoordenacao().getId() == obj.getCurso().getId() ) 
					 return true;
			 }
		}
		
		// (STRICTO)
		// Permissão de Coordenador ou de Secretario: visualizar apenas históricos de discentes do programa relacionado.
		else if (getSubSistema().equals(SigaaSubsistemas.STRICTO_SENSU) || getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO)){
			 	if (isUserInRole( new int[] { SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS })
			 			&& obj.getUnidade().equals(getProgramaStricto()) )
			 		return true;
			// Permissão PPG: visualizar qualquer histórico.
			 	else if ( (isUserInRole(new int[] { SigaaPapeis.PPG })))
				 return true;
		}
		
		// (LATO)
		// Permissão de Coordenador ou de Secretario: visualizar apenas históricos de discentes do programa relacionado.
		else if (getSubSistema().equals(SigaaSubsistemas.LATO_SENSU) || getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO)){
			if ( isUserInRole( new int[] { SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO })  
				 && getAllCursosCoordenacaoNivel().contains( obj.getCurso() ) ) 
				 return true;
			// Permissão PPG: visualizar qualquer histórico.
			else if ( (isUserInRole(new int[] { SigaaPapeis.PPG, SigaaPapeis.GESTOR_LATO })))
				 return true;
		}
		
		//(GESTOR TECNICO) Permissão liberada para visualizar qualquer histórico do TECNICO. 
		else if (getSubSistema().equals(SigaaSubsistemas.TECNICO)
				&& (isUserInRole( new int[] {SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO, SigaaPapeis.COORDENADOR_TECNICO, SigaaPapeis.PEDAGOGICO_TECNICO})))
			return true;
		
		//(GESTOR FORMAÇÃO COMPLEMENTAR) Permissão liberada para visualizar qualquer histórico do nível FORMAÇÃO COMPLEMENTAR. 
		else if (getSubSistema().equals(SigaaSubsistemas.FORMACAO_COMPLEMENTAR)
				&& (isUserInRole( new int[] {SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR})))
				return true;
				
		//(EAD)Permissão de Coordenador Geral: visualizar qualquer históricos de discentes de EAD.
		else if (getSubSistema().equals(SigaaSubsistemas.SEDIS) 
			&& (isUserInRole( new int[] {SigaaPapeis.COORDENADOR_GERAL_EAD, SigaaPapeis.SEDIS})))
				return true;
		
		//(EAD)Permissão de Coordenador de Pólo: visualizar históricos de discentes do seu pólo.
		// A própria listagem dos discentes já filtra por pólo.
		else if (getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_POLO))
				return true;

		//(EAD)Tutor EAD pode acessar o histórico somente dos alunos que exerce tutoria
		else if (getSubSistema().equals(SigaaSubsistemas.PORTAL_TUTOR)) {
			//DiscenteGraduacao dg = (DiscenteGraduacao) obj;
			ArrayList<TutorOrientador> tutores = EADHelper.findTutoresByAluno(obj);
			if (tutores != null && getTutorUsuario() != null && tutores.contains(getTutorUsuario()))
				return true;
		}
		
		// (PLANEJAMENTO) Permissão liberada para visualizar qualquer histórico.
		else if (getSubSistema().equals(SigaaSubsistemas.PORTAL_PLANEJAMENTO) 
			&& (isUserInRole( new int[] {SigaaPapeis.PORTAL_PLANEJAMENTO })) )
				return true;
		
		//(COMPLEXO HOSPITALAR) Permissão liberada para visualizar qualquer histórico de residência. 
		else if (getSubSistema().equals(SigaaSubsistemas.COMPLEXO_HOSPITALAR) 
				&& (isUserInRole( new int[] {SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, SigaaPapeis.SECRETARIA_RESIDENCIA,
						SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA}) ))
				return true;
		
		//(DOCENTE/DISCENTE) Permissão liberada para visualizar o próprio histórico. 
		else if (getSubSistema().equals(SigaaSubsistemas.PORTAL_DISCENTE)
				|| getSubSistema().equals(SigaaSubsistemas.PORTAL_DOCENTE )) 
					return true;
		
		//(CONSULTADOR ACEDÊMICO) Permissão liberada para visualizar históricos.
		else if (getSubSistema().equals(SigaaSubsistemas.CONSULTA) 
				&& (isUserInRole( new int[] {SigaaPapeis.CONSULTADOR_ACADEMICO}) ))
				return true;
		
		// (PORTAL RELATORIOS DE GESTÃO) - Relatórios de Taxa de Conclusão 
		// Permissão liberada para visualizar qualquer histórico.
		else if (getSubSistema().equals(SigaaSubsistemas.PORTAL_RELATORIOS) 
			&& (isUserInRole( new int[] {SigaaPapeis.PORTAL_RELATORIOS })) )
				return true;		

		//  (MÓDULO DE REGISTRO DE DIPLOMAS) - Consulta de Históricos
		else if (getSubSistema().equals(SigaaSubsistemas.REGISTRO_DIPLOMAS)
				&& isUserInRole(SigaaPapeis.GESTOR_DIPLOMAS_GRADUACAO, SigaaPapeis.GESTOR_DIPLOMAS_LATO, SigaaPapeis.GESTOR_DIPLOMAS_STRICTO))
				return true;
		
		//(COORDENADOR DE AÇÕES DE EXTENSAO) Permissão liberada para visualizar histórico dos candidatos a bolsistas. 
		else if (getSubSistema().equals(SigaaSubsistemas.EXTENSAO)
				&& (getAcessoMenu().isCoordenadorExtensao()))
				return true;
		
		//(NEE)Permissão de Usuário da Comissão de Apoio ao Discente com Necessidades Especiais: visualizar históricos de discente com necessidades educacionais especiais.
		else if ( getSubSistema().equals(SigaaSubsistemas.NEE) && isUserInRole(SigaaPapeis.GESTOR_NEE) )
				return true;
		
		//(Mobile Web) Discentes e Docentes logados
		else if(getSubSistema().equals(SigaaSubsistemas.SIGAA_MOBILE) && getUsuarioLogado() != null && getUsuarioLogado().getVinculoAtivo() != null && 
				(getUsuarioLogado().getVinculoAtivo().isVinculoDiscente() || (getUsuarioLogado().getVinculoAtivo().isVinculoServidor() && getUsuarioLogado().getServidor() != null && getUsuarioLogado().getServidor().isDocente())))
			return true;
		
		//(INTERNACIONALIZAÇÃO DE DOCUMENTOS) Gestores de Tradução de Documentos
		else if(getSubSistema().equals(SigaaSubsistemas.RELACOES_INTERNACIONAIS) && isUserInRole(SigaaPapeis.GESTOR_TRADUCAO_DOCUMENTOS, SigaaPapeis.TRADUTOR_DADOS_ACADEMICOS))
			return true;

		//(INTERNACIONALIZAÇÃO DE DOCUMENTOS) Gestores de Tradução de Documentos
		else if(getSubSistema().equals(SigaaSubsistemas.SAE))
			return true;

		//(Área Administrativa) Permissão liberada para área administrativa.
		else if(getSubSistema().equals(SigaaSubsistemas.ADMINISTRACAO))
			return true;
		
		//OBS: Secretário de Departamento não tem permissão alguma para visualização.
		
		return false;
	}		

	/**
	 * Emissão de um histórico de discente a partir de um link em uma página,
	 * passando como parâmetro o id do discente.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/trancamento_matricula/atendimento_solicitacoes/solicitacao_aluno.jsp</li>
	 * <li>/sigaa.war/extensao/PlanoTrabalho/form.jsp</li>
	 * <li>/sigaa.war/extensao/PlanoTrabalho/indicar_discente.jsp</li>
	 * <li>/sigaa.war/graduacao/discente/solicitacoes_matricula.jsp</li>
	 * <li>/sigaa.war/graduacao/matricula/_info_discente.jsp</li>
	 * <li>/sigaa.war/graduacao/orientacao_academica/lista.jsp</li>
	 * <li>/sigaa.war/graduacao/solicitacao_matricula/solicitacoes.jsp</li>
	 * <li>/sigaa.war/graduacao/solicitacao_turma/solicitacoes_ensino_individual.jsp</li>
	 * <li>/sigaa.war/stricto/orientacao/orientandos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String selecionaDiscenteForm() throws ArqException {
		Integer idDiscente = getParameterInt("id");
		if (idDiscente == null) {
			addMensagem(MensagensGerais.DISCENTE_INVALIDO_HISTORICO);
			return null;
		}
		
		obj = getGenericDAO().findByPrimaryKey(idDiscente, Discente.class);
		return selecionaDiscente();
	}

	/**
	 * Gera o histórico de discente a partir de uma action ou servlet.
	 * <br>Método não invocado por JSP´s.
	 * @param req
	 * @param res
	 * @return
	 * @throws ArqException
	 */
	public String selecionaDiscente(HttpServletRequest req, HttpServletResponse res) throws ArqException {
		this.request = req;
		this.response = res;
		return gerarHistorico();
	}
	
	/**
	 * Gera o histórico a partir de uma JSP.
	 * <br> Método chamado pela(s) seguinte(s) JSP(s): <ul>
	 * <li>/sigaa.war/ead/FichaAvaliacaoEad/discentes.jsp</li>
	 * <li>/sigaa.war/ensino/consolidacao/consolidacao_individual.jsp</li>
	 * <li>/sigaa.war/ensino/trancamento_matricula/atendimento_solicitacoes/solicitacao_aluno.jsp</li>
	 * <li>/sigaa.war/ensino/trancamento_matricula/trancamento_tutor.jsp</li>
	 * <li>/sigaa.war/extensao/PlanoTrabalho/form.jsp</li>
	 * <li>/sigaa.war/extensao/PlanoTrabalho/indicar_discente.jsp</li>
	 * <li>/sigaa.war/graduacao/discente/solicitacoes_matricula.jsp</li>
	 * </ul>
	 */
	public String selecionaDiscente() throws ArqException {
		this.request = null;
		this.response = null;
		this.usuarioLogado = null;
		this.comprovante = null;
		if (isModuloRelacoesInternacionais()){
			idioma = null;
			return forward("/relacoes_internacionais/historico/selecao_idioma.jsp");
		}
		return gerarHistorico();
	}
	
	/**
	 * Gera o histórico a partir de uma JSP.
	 * <br> Método chamado pela(s) seguinte(s) JSP(s): <ul>
	 * <li>/sigaa.war/relacoes_internacionais/historico/selecao_idioma.jsp</li>
	 * </ul>
	 */
	public String selecionaIdioma() throws ArqException {
		ValidatorUtil.validateRequired(idioma, "Idioma", erros);
		if (hasErrors()) return null;
		return gerarHistorico();
	}

	/**
	 * Método responsável por retornar o usuário para a lista de discentes
	 * <br> Método chamado pela(s) seguinte(s) JSP(s): <ul>
	 * <li>/sigaa.war/relacoes_internacionais/historico/selecao_idioma.jsp</li>
	 * </ul>
	 */
	public String voltar() throws ArqException {
		return forward("/graduacao/busca_discente.jsp");
	}
	
	/**
	 * Reúne todas as informações do discente e monta o histórico.
	 * 
	 * @return
	 */
	private String gerarHistorico() {
		try {
			if (verificaAcessoHistorico() == false){
				addMensagem(MensagensGerais.USUARIO_NAO_AUTORIZADO);
				return null;
			}
			
			if (getDiscente() == null)
				throw new NegocioException("Nenhum discente foi selecionado.");
			
			if (getDiscente().getStatus() == StatusDiscente.EXCLUIDO) {
				addMensagem(MensagensGerais.DISCENTE_SEM_HISTORICO, StatusDiscente.getDescricao(StatusDiscente.EXCLUIDO));
				return null;
			}
			
			// Verificar bloqueio de emissão do documento
			if ( ( getAcessoMenu() == null || !getAcessoMenu().isDae() ) 
					&& !AutenticacaoUtil.isDocumentoLiberado(TipoDocumentoAutenticado.HISTORICO, getDiscente().getNivel())) {
				addMensagem(MensagensGerais.HISTORICO_INDISPONIVEL);
				return redirectMesmaPagina();
			}

			if (!ValidatorUtil.isEmpty(getDiscente().getIdHistoricoDigital()))
				return recuperaHistoricoDigitalizado();
			else
				return computaHistoricoDiscente();
			
		} catch(ExpressaoInvalidaException e) {
			addMensagem(MensagensGerais.ERRO_EXPRESSOES_HISTORICO);
			return null;
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			notifyError(e);
			addMensagemErroPadrao();
			return null;
		}
	}
	
	/** Recupera o histórico digitalizado de um discente antigo.
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws JRException
	 * @throws IOException
	 */
	private String recuperaHistoricoDigitalizado() throws IOException {
		String nomeArquivo = EnvioArquivoHelper.recuperaNomeArquivo(getDiscente().getIdHistoricoDigital());
		getCurrentResponse().setContentType("application/pdf");
		getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=" + nomeArquivo);
		ServletOutputStream os = getCurrentResponse().getOutputStream();
		EnvioArquivoHelper.recuperaArquivo(os, getDiscente().getIdHistoricoDigital());
		if (FacesContext.getCurrentInstance() != null)
			FacesContext.getCurrentInstance().responseComplete();
		return null;
	}
	
	/** Calcula e gera o arquivo PDF com o histórico do discente.
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws JRException
	 * @throws IOException
	 */
	private String computaHistoricoDiscente() throws ArqException, NegocioException, JRException, IOException {
		MovimentoCalculoHistorico mov = new MovimentoCalculoHistorico();
		mov.setUsuarioLogado(getUsuarioLogado());
		mov.setRecalculaCurriculo(!verificando);
		mov.setDiscente(getDiscente());
		mov.setCodMovimento(SigaaListaComando.CALCULAR_HISTORICO_DISCENTE);
		
		prepareMovimento(SigaaListaComando.CALCULAR_HISTORICO_DISCENTE);
		
		Historico historico = null;
		try {
			historico = execute(mov);
		} catch(NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		} catch(Exception e) {
			tratamentoErroPadrao(e);
			return null;
		}
		
		if (comprovante != null) {
			historico.setDataHistorico(comprovante.getHoraEmissao());
		} 
		if (comprovante == null && !verificando) {
			comprovante = geraEmissao(TipoDocumentoAutenticado.HISTORICO,
				getDiscente().getMatricula().toString(),
				geraSemente(historico.getMatriculasDiscente()), Integer
				.toString(getDiscente().getId()), null, false);
		}
		
		historico.getDiscente().getDiscente().setIndices(getDAO(IndiceAcademicoDao.class).findIndicesAcademicoDiscente(historico.getDiscente().getDiscente()));
		if ( historico.getDiscente().isGraduacao() )
			populaNotasFrequenciaPorSituacao(historico);
		
		/* Solicitação de tradução do histórico, quando o idioma for diferente do português.*/
		if (ValidatorUtil.isNotEmpty(idioma) && !IdiomasEnum.PORTUGUES.getId().equals(idioma)){
			traduzirElementosHistorico(historico);
			if (hasErrors()) return null;
		} else {
			if (historico.getDiscente().isGraduacao()){
				ReflectionUtils.setProperty(historico.getDiscente(), "rendimentoAcademino", "Índices Acadêmicos");
			}
		}
		historico.setStatusDiscenteI18n(ValidatorUtil.isEmpty(historico.getStatusDiscenteI18n()) ? getDiscente().getStatusString() : historico.getStatusDiscenteI18n());
		List<Historico> historicos = new ArrayList<Historico>();
		historicos.add(historico);

		String nivel = historico.getNivel();
		
		return geraArquivo(historicos, nivel);
	}

	/** Gera um arquivo formatado com os dados do histórico do discente.
	 *  <br>Método não invocado por JSP´s.
	 * @param historicos
	 * @param nivel
	 * @return
	 */
	public String geraArquivo(List<Historico> historicos, String nivel) throws DAOException, JRException, IOException {
		
		Historico historico = historicos.iterator().next();
		DiscenteAdapter discente = historico.getDiscente(); 
		int totalPendentes = historico.getDisciplinasPendentesDiscente().size();
		boolean autenticidadeAutomatica = true;
		
		Map<String, Object> hs = new HashMap<String, Object>();
		
		hs.put("nomeSistema", RepositorioDadosInstitucionais.get("siglaSigaa") + " - " + RepositorioDadosInstitucionais.get("nomeSigaa") );
		hs.put("nomeInstituicao", RepositorioDadosInstitucionais.get("siglaInstituicao") + " - " + RepositorioDadosInstitucionais.get("nomeInstituicao") );
		hs.put("enderecoInstituicao", RepositorioDadosInstitucionais.get("enderecoInstituicao") );		
		hs.put("logo_ifes", new URL(RepositorioDadosInstitucionais.get("logoInstituicao") ));
		hs.put("logo_adm_sistema", new URL(RepositorioDadosInstitucionais.get("logoInformatica") ) ); 
		hs.put("verificacaoAutomaticaAutenticidade", (discente.isStricto() ? ParametroHelper.getInstance().getParametro(ParametrosStrictoSensu.SIGLA_PRO_REITORIA_POS) : ParametroHelper.getInstance().getParametro(ParametrosGraduacao.SIGLA_DEPARTAMENTO_ADM_ESCOLAR) ) );

		
		hs.put("pathSubRelCurso", getReportSIGAA("HistoricoCurso" + nivel + ".jasper"));
		if(MetropoleDigitalHelper.isMetropoleDigital(discente))
			hs.put("pathSubRelMatriculadas", getReportSIGAA("HistoricoComponentesMatriculadosMetropole.jasper"));
		else if(historicoComEmenta)
			hs.put("pathSubRelMatriculadas", getReportSIGAA("HistoricoComponentesMatriculadosEmenta" + nivel + ".jasper"));
		else
			hs.put("pathSubRelMatriculadas", getReportSIGAA("HistoricoComponentesMatriculados" + nivel + ".jasper"));

		if (discente.isGraduacao()) {
			hs.put("pathSubRelGradeCH", getReportSIGAA("HistoricoGradeCargasHorariasGraduacao.jasper"));
			hs.put("pathSubRelEquivalencias", getReportSIGAA("HistoricoComponentesEquivalencias.jasper"));
			hs.put("pathSubRelMobilidadeEstudantil", getReportSIGAA("HistoricoMobilidadeEstudantil.jasper"));		
		}
		if (discente.isTecnico()) {
			hs.put("pathSubRelGradeCH", getReportSIGAA("HistoricoGradeCargasHorariasTecnico.jasper"));
		}
		if (discente.isStricto()) { 
			DiscenteStricto ds = (DiscenteStricto) discente; 
			if (ds.getBancaDefesa() != null) { 
				hs.put("pathSubRelDadosBancaStricto", getReportSIGAA("HistoricoDadosBancaStricto.jasper"));			
			} 
			calcularTotalCreditos(ds);			
			hs.put("pathSubRelGradeCH", getReportSIGAA("HistoricoGradeCargasHorariasStricto.jasper"));		
		}
		if(discente.isLato()){
			DiscenteLato dl = (DiscenteLato) discente;
			if(dl.getTrabalhoFinal() != null){
				hs.put("pathSubRelDadosTrabalhoFinalLato", getReportSIGAA("HistoricoDadosTrabalhoFinalLato.jasper"));
			}
		}

		if(historicoComEmenta)
			hs.put("pathSubRelPendentes", getReportSIGAA("HistoricoComponentesPendentesEmenta.jasper"));
		else
			hs.put("pathSubRelPendentes", getReportSIGAA("HistoricoComponentesPendentes.jasper"));
		hs.put("pathSubRelObservacoes", getReportSIGAA("HistoricoDiscenteObservacoes.jasper"));

		if (comprovante != null)
			hs.put("codigoSeguranca", comprovante.getCodigoSeguranca());
		hs.put("siteVerificacao", ParametroHelper.getInstance()
				.getParametro(ConstantesParametro.ENDERECO_AUTENTICIDADE));
		hs.put("totalPendentes", String.valueOf(totalPendentes));
		hs.put("stricto", String.valueOf(historico.isStricto()));
		hs.put("exigeNotaAproveitamento", String.valueOf(ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalGraduacao().isExigeNotaAproveitamento()));

		if (historico.isGraduacao()) {
			ParametroHelper.getInstance().getParametro(ParametrosGraduacao.SIGLA_NOME_GESTAO_GRADUACAO);
			hs.put("cabecalhoLinha1", ParametroHelper.getInstance().getParametro(ParametrosGraduacao.SIGLA_NOME_GESTAO_GRADUACAO));
			hs.put("cabecalhoLinha2", ParametroHelper.getInstance().getParametro(ParametrosGraduacao.SIGLA_NOME_ADM_ESCOLAR));
		} else if (historico.isTecnico()) {
			hs.put("cabecalhoLinha1", "");
			hs.put("cabecalhoLinha2", "");
		} else if (historico.isStricto()) {
			hs.put("cabecalhoLinha1", ParametroHelper.getInstance().getParametro(ParametrosStrictoSensu.SIGLA_NOME_PRO_REITORIA_POS));
			hs.put("cabecalhoLinha2", "");
		} else if (historico.isResidencia()) {
			hs.put("cabecalhoLinha1", "Residências em Saúde");
			hs.put("cabecalhoLinha2", "");
		} else if (historico.isLato()) {
			hs.put("cabecalhoLinha1", ParametroHelper.getInstance().getParametro(ParametrosStrictoSensu.SIGLA_NOME_PRO_REITORIA_POS));
			hs.put("cabecalhoLinha2", "");
		}
		
		
		if (ValidatorUtil.isNotEmpty(idioma) && !IdiomasEnum.PORTUGUES.getId().equals(idioma)){
			Locale locale = new Locale(idioma);
			hs.put(JRParameter.REPORT_LOCALE, locale);
			autenticidadeAutomatica = false;
		}	
		hs.put("autenticidadeAutomatica", autenticidadeAutomatica);
		
		JRDataSource jrds = new JRBeanCollectionDataSource(historicos);
		JasperPrint prt = JasperFillManager.fillReport(
				getReportSIGAA("HistoricoDiscente.jasper"), hs, jrds);

		if (excel) {
			getCurrentResponse().setContentType("application/xls");
			getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=historico_" + discente.getMatricula() + ".xls");

			JRXlsExporter exporter = new JRXlsExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, prt);  
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, getCurrentResponse().getOutputStream());
			exporter.exportReport();
		} else {
			getCurrentResponse().setContentType("application/pdf");
			getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=\"historico_" + discente.getMatricula() + ".pdf\"");
			
			File tempFile = File.createTempFile("historico_" + discente.getMatricula(), ".pdf");
			JasperExportManager.exportReportToPdfFile(prt, tempFile.getAbsolutePath());
			
			File assinado = null;
			try {
				assinado = AssinaturaDigitalService.assinarPdf(tempFile);
				getCurrentResponse().getOutputStream().write(FileUtils.readFileToByteArray(assinado));
			} catch(Exception e) {
				e.printStackTrace();
				getCurrentResponse().getOutputStream().write(FileUtils.readFileToByteArray(tempFile));				
			}
			
		}
		
		if (FacesContext.getCurrentInstance() != null)
			FacesContext.getCurrentInstance().responseComplete();

		return null;
	}	
	
	
	/**
	 * Calcula o total de créditos exigidos no currículo do curso.
	 * Considera os componentes específicos da área de concentração do discente e 
	 * os componentes comuns à todas as áreas. 
	 * 
	 * @param d
	 * @throws DAOException
	 */
	private void calcularTotalCreditos(DiscenteStricto d) throws DAOException { 
		//Adicionar validação para contabilizar em chNaoAtividadeObrig apenas as disciplinas da área de concentração do aluno, 
		//caso não tenha área de concentração específica, realiza a soma todos componentes do currículo. 
		DiscenteStricto ds = getGenericDAO().findByPrimaryKey(d.getId(),DiscenteStricto.class); 
		if(!ValidatorUtil.isEmpty(ds) && !ValidatorUtil.isEmpty(ds.getArea()) && !ValidatorUtil.isEmpty(ds.getDiscente()) && 
				!ValidatorUtil.isEmpty(ds.getDiscente().getCurriculo())) { 
			Discente discente= ds.getDiscente(); 
			Curriculo curriculo = discente.getCurriculo(); 
			AreaConcentracao area = ds.getArea(); 
			int chAtividadeObrig = 0; 
			int chNaoAtividadeObrig = 0; 
			int crNaoAtividadeObrig = 0; 
			
			for (CurriculoComponente cc : curriculo.getCurriculoComponentes()) { 
				ComponenteCurricular comp = getGenericDAO().findByPrimaryKey(cc.getComponente().getId(), ComponenteCurricular.class); 
				if (cc.getObrigatoria() && (comp.isAtividade() || comp.isAtividadeColetiva())) { 
					chAtividadeObrig += comp.getChTotal();					
				} else if (cc.getObrigatoria() && !comp.isAtividade() && !comp.isAtividadeColetiva() && 
						(ValidatorUtil.isEmpty(cc.getAreaConcentracao()) || cc.getAreaConcentracao().getId() == area.getId()) ) { 
					chNaoAtividadeObrig += comp.getChTotal(); 
					crNaoAtividadeObrig += comp.getCrTotal();				
				} 
				getGenericDAO().detach(comp); 
			
			} 
			curriculo.setChTotalMinima(curriculo.getChOptativasMinima() + chAtividadeObrig + chNaoAtividadeObrig); 
			int crOptativasMinima = curriculo.getChOptativasMinima() / ParametrosGestoraAcademicaHelper.getParametros(ds.getDiscente()).getHorasCreditosAula(); 
			d.setTotalCreditoCalculado(crNaoAtividadeObrig + crOptativasMinima); 
		
		} 
	
	}


	
	
	
	
	
	
	
	

	/**
	 * Retorna o discente para o qual será emitido o atestado de matrícula.
	 * <br>Método não invocado por JSP´s.
	 */
	public DiscenteAdapter getDiscente() {
		if (getUsuarioLogado() != null
				&& getUsuarioLogado().getDiscenteAtivo() != null
				&& (SigaaSubsistemas.PORTAL_DISCENTE.equals(getSubSistema())
						|| SigaaSubsistemas.PORTAL_TURMA.equals(getSubSistema())
						|| SigaaSubsistemas.SIGAA_MOBILE.equals(getSubSistema())
						|| getSubSistema() == null)) {
			return getUsuarioLogado().getDiscenteAtivo();
		} else {
			return obj;
		}
	}

	/** Seta o discente o qual será emitido o histórico.
	 * Método não invocado por JSP's.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.pessoa.dominio.Discente)
	 */
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		this.obj = discente;
	}

	/**
	 * Exibe o histórico depois de validar.
	 * <br>Método não invocado por JSP´s.
	 */
	public void exibir(EmissaoDocumentoAutenticado comprovante,
			HttpServletRequest req, HttpServletResponse res) {
		String matricula = comprovante.getIdentificador();

		DiscenteDao dao = getDAO(DiscenteDao.class);
		this.comprovante = comprovante;
		verificando = true;

		try {
			// Buscar discente
			try {
				obj = dao.findByMatricula(new Long(matricula));
			} catch (DAOException e) {
				if (comprovante.getDadosAuxiliares() != null) {
					obj = dao.findByPrimaryKey(Integer.parseInt(comprovante
							.getDadosAuxiliares()), Discente.class);
				} else {
					obj = null;
				}
			}
			selecionaDiscente();
		} catch (Exception e) {
			addMensagemErroPadrao();
		}

	}

	/**
	 * Valida mudanças no Histórico.
	 * <br>Método não invocado por JSP´s.
	 */
	public boolean validaDigest(EmissaoDocumentoAutenticado comprovante) {
		String matricula = comprovante.getIdentificador();
		DiscenteDao dao = getDAO(DiscenteDao.class);

		try {
			// Buscar discente
			try {
				obj = dao.findByMatricula(new Long(matricula));
			} catch (DAOException e) {
				if (comprovante.getDadosAuxiliares() != null) {
					obj = dao.findByPrimaryKey(Integer.parseInt(comprovante.getDadosAuxiliares()), Discente.class);
				} else {
					obj = null;
				}
			}

			if (obj == null)
				return false;

			List<MatriculaComponente> matriculas = dao.findDisciplinasConcluidasMatriculadas(obj.getId(), true);
			if (obj.isGraduacao()){
				// ENADE -> É listado no Histórico como Componente Pendente
				ProcessadorCalculaHistorico processadorHistorico =  new ProcessadorCalculaHistorico();
				Collection<ComponenteCurricular> componentesPendentes = new ArrayList<ComponenteCurricular>();
				setObj(dao.findByPK(obj.getId()));
				processadorHistorico.participacaoEnade((DiscenteGraduacao) obj, matriculas, componentesPendentes);
			}
			if (obj.isStricto()) {
				List<AproveitamentoCredito> aproveitamentos = (List<AproveitamentoCredito>) getGenericDAO().findByExactField(AproveitamentoCredito.class, "discente.id", obj.getId());
				ParametrosGestoraAcademica parametros = ParametrosGestoraAcademicaHelper.getParametros(obj);
				for (AproveitamentoCredito aproveitamento : aproveitamentos) {
					if (aproveitamento.isAtivo()) {
						MatriculaComponente mc = new MatriculaComponente();
						ComponenteCurricular cc = new ComponenteCurricular();
						cc.setNome("APROVEITAMENTO DE CRÉDITOS");
						cc.setChTotal(aproveitamento.getCreditos() * parametros.getHorasCreditosAula());
						mc.setSituacaoMatricula(SituacaoMatricula.APROVEITADO_CUMPRIU);
		
						mc.setComponente(cc);
						matriculas.add(mc);
					}
				}
			}
			
			String semente = geraSemente(matriculas);

			String codigoVerificacao = AutenticacaoUtil.geraCodigoValidacao(comprovante, semente);
			if (codigoVerificacao.equals(comprovante.getCodigoSeguranca()))
				return true;

		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
		}
		return false;
	}

	/**
	 * Gera a semente de verificação utilizada na identificação única do documento
	 * autenticado emitido pelo sistema. No caso do histórico é composta pelas
	 * matrículas nos componentes curriculares, suas médias finais e situações respectivas.
	 */
	private String geraSemente(Collection<MatriculaComponente> matriculas) {
		StringBuffer bufferDigest = new StringBuffer();
		for (MatriculaComponente comp : matriculas)
			bufferDigest.append(comp.getId() + "_" + comp.getMediaFinal() + "_"
					+ comp.getSituacaoMatricula().getId() + "_");

		return bufferDigest.toString();
	}

	/**
	 * Representação das situações de matrícula.
	 */
			
	/**
	 * Método responsável por popular as notas e frequências das matrículas, 
	 * de acordo com a regra de visualização conforme a situação da matrícula.
	 * @param historico
	 * @throws DAOException 
	 */
	private void populaNotasFrequenciaPorSituacao(Historico historico) throws DAOException{
		List<MatriculaComponente> matriculasDiscente = new ArrayList<MatriculaComponente>();
		matriculasDiscente.addAll(historico.getMatriculasDiscente());
		
		String mesAno = ParametroHelper.getInstance().getParametro(ParametrosGraduacao.MES_ANO_LIMITE_ALUNOS_ANTIGOS_INATIVOS);
		Calendar dataBase = CalendarUtils.getInstance("MM/yyyy", mesAno);
		
		boolean exigeNotaAproveitamento = ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalGraduacao().isExigeNotaAproveitamento();
		
		DiscenteAdapter discente = historico.getDiscente();
		boolean discenteAntigo = false;
		/* Exibe apenas os valores de notas e frequência para discente antigos ao ano registrado 
		 * no parâmetro: ParametrosGraduacao.MES_ANO_LIMITE_ALUNOS_ANTIGOS_INATIVOS. */
		if(discente.getDiscente().getMovimentacaoSaida() != null){
			if(discente.getDiscente().getMovimentacaoSaida().getTipoMovimentacaoAluno().isPermanente() 
					&& discente.getDiscente().getMovimentacaoSaida().getDataRetorno() == null 
					&& discente.getDiscente().getMovimentacaoSaida().getDataOcorrencia().before(dataBase.getTime())){
				discenteAntigo = true;
			}
		}

		for (MatriculaComponente mc : matriculasDiscente) {
			int situacao = mc.getSituacaoMatricula().getId();
			
			if (situacao == SituacaoMatricula.APROVEITADO_DISPENSADO.getId()) {
				mc.setMediaFinal(null);
			}
			else if (situacao == SituacaoMatricula.APROVEITADO_CUMPRIU.getId()) {
				if (!exigeNotaAproveitamento){  
					mc.setAno(null);
					mc.setPeriodo(null);
					if (!discenteAntigo){
						mc.setMediaFinal(null);
					}
				}
			}
			else if (situacao == SituacaoMatricula.APROVEITADO_TRANSFERIDO.getId()) {
				if (!exigeNotaAproveitamento){  
					mc.setAno(null);
					mc.setPeriodo(null);
					if (!discenteAntigo){
						mc.setMediaFinal(null);
					}
				}
			}
			else if (situacao == SituacaoMatricula.APROVADO.getId()) {
				if (!discenteAntigo && !exigeNotaAproveitamento && mc.getMediaFinal() == null)  
					mc.setMediaFinal(null);
			}
			else if (situacao == SituacaoMatricula.TRANCADO.getId() ) {
				mc.setMediaFinal(null);
			}
			else if (situacao == SituacaoMatricula.MATRICULADO.getId()) {
				mc.setMediaFinal(null);
			}
			else if (situacao == SituacaoMatricula.CANCELADO.getId()) {
				mc.setMediaFinal(null);
			}
			else if (situacao == SituacaoMatricula.NAO_CONCLUIDO.getId()) {
				mc.setMediaFinal(null);
			}
			else if (situacao == SituacaoMatricula.EM_ESPERA.getId()) {
				mc.setMediaFinal(null);
			}
			else if (situacao == SituacaoMatricula.EXCLUIDA.getId()) { 
				mc.setMediaFinal(null);
			}
			else if (situacao == SituacaoMatricula.INDEFERIDA.getId()) { 
				mc.setMediaFinal(null);
			}
			else if (situacao == SituacaoMatricula.DESISTENCIA.getId()) { 
				mc.setMediaFinal(null);
			}
			else {
				if ( mc.getComponente().isAtividade() 
						&& !mc.getComponente().isNecessitaMediaFinal()
						|| (exigeNotaAproveitamento == false && mc.getMediaFinal() == null) )
					mc.setMediaFinal(null);
			}
			
		}
		
		if (!exigeNotaAproveitamento){  
			Comparator<MatriculaComponente> matriculaComparator = new Comparator<MatriculaComponente>() {
				public int compare(MatriculaComponente mc1, MatriculaComponente mc2) {
					Integer ano1 = new Integer((mc1.getAno() != null ? mc1.getAno() : 0));
					Integer ano2 = new Integer((mc2.getAno() != null ? mc2.getAno() : 0));
					return ano1.compareTo(ano2);
					
				}
			};
			Collections.sort((List<MatriculaComponente>)historico.getMatriculasDiscente(), matriculaComparator);
		}
	}
	
	/** Indica se o histórico será emitido no formato excel. 
	 * Método não invocado por JSP's.
	 * @return
	 */
	public boolean isExcel() {
		return excel;
	}

	/**
	 * Retorna uma instância do HttpServletRequest.
	 * Método não invocado por JSP's.
	 * @see br.ufrn.arq.web.jsf.AbstractController#getCurrentRequest()
	 */
	@Override
	public HttpServletRequest getCurrentRequest() {
		if (request == null)
			return super.getCurrentRequest();
		else
			return request;
	}
	
	/**
	 * Retorna uma instância do HttpServletResponse.
	 * Método não invocado por JSP's.
	 * @see br.ufrn.arq.web.jsf.AbstractController#getCurrentResponse()
	 */
	@Override
	public HttpServletResponse getCurrentResponse() {
		if (response == null)
			return super.getCurrentResponse();
		else
			return response;
	}

	/**
	 * Retorna o usuário logado
	 * Método não invocado por JSP's
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getUsuarioLogado()
	 */
	@Override
	public Usuario getUsuarioLogado() {
		if (request == null) {
			return super.getUsuarioLogado();
		} else {
			return usuarioLogado;
		}
	}

	/**
	 * Seleciona o usuário logado que está acessando o histórico
	 * @param usuarioLogado
	 * @throws DAOException
	 */
	public void setUsuarioLogado(Usuario usuarioLogado) throws DAOException {
		this.usuarioLogado = usuarioLogado;
	}

	/** Método não invocado por JSP.
	 * @see br.ufrn.arq.web.jsf.AbstractController#prepareMovimento(br.ufrn.arq.dominio.Comando)
	 */
	@Override
	public void prepareMovimento(Comando comando) throws ArqException {
		if (request == null)
			super.prepareMovimento(comando);
		else
			super.prepareMovimento(comando, getUserDelegate(request), usuarioLogado, Sistema.SIGAA);
	}

	/**
	 * Método responsável pela tradução dos elementos dinâmicos pertencentes ao histórico.  
	 * @param historico
	 * @throws DAOException 
	 */
	//TODO
	private void traduzirElementosHistorico(Historico historico) throws DAOException{
		HistoricoTraducaoMBean historicoTraducaoMBean = (HistoricoTraducaoMBean) getMBean("historicoTraducaoMBean");
		historicoTraducaoMBean.traduzirElementosHistorico(historico, idioma, historicoComEmenta);
	}
	
	/**
	 * Verifica se o usuário está acessando o módulo de Internacionalização de documentos.
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/busca_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isModuloRelacoesInternacionais() {
		return SigaaSubsistemas.RELACOES_INTERNACIONAIS.equals(getSubSistema());
	}
	
	/** Indica se o usuário está verificando a autenticidade do documento. 
	 * Método não invocado por JSP's.
	 * @return
	 */
	public boolean isVerificando() {
		return verificando;
	}

	/** 
	 * Indica se o usuário redirecionado de outro sistema já está autenticado
	 * para emissão de Histórico de discente.
	 * Método não invocado por JSP's.
	 * @return
	 */
	public boolean isAutenticado() {
		return autenticado;
	}

	/**
	 * Seta se o usuário redirecionado de outro sistema já está autenticado
	 * para emissão de Histórico de discente.
	 * Método não invocado por JSP's.
	 * @param verificando
	 */
	public void setAutenticado(boolean autenticado) {
		this.autenticado = autenticado;
	}

	/** Seta se o usuário está verificando a autenticidade do documento.
	 * 	Método não invocado por JSP's.
	 * @param verificando
	 */
	public void setVerificando(boolean verificando) {
		this.verificando = verificando;
	}

	public String getIdioma() {
		return idioma;
	}

	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}
	
}
