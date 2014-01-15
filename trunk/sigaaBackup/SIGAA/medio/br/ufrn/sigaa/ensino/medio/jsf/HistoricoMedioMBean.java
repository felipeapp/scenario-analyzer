/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 30/08/2011
 *
 */
package br.ufrn.sigaa.ensino.medio.jsf;

import static br.ufrn.arq.util.JasperReportsUtil.getReportSIGAA;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.arquivos.AssinaturaDigitalService;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.DefaultExtensionPointFactory;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.seguranca.autenticacao.AutValidator;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.dao.graduacao.IndiceAcademicoDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.expressao.ExpressaoInvalidaException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.medio.dao.MatriculaDiscenteSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.medio.dominio.HistoricoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaComponenteDependencia;
import br.ufrn.sigaa.ensino.medio.dominio.NotaDisciplina;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacao;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacaoFactory;
import br.ufrn.sigaa.mensagens.MensagensGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Managed Bean para geração de histórico dos discentes do Ensino Médio. 
 * 
 * @author Arlindo Rodrigues
 * 
 */
@Component("historicoMedio") @Scope("request")
public class HistoricoMedioMBean extends SigaaAbstractController<HistoricoMedio> implements OperadorDiscente, AutValidator {

	/** Comprovante de autenticidade do histórico. */
	private EmissaoDocumentoAutenticado comprovante;
	/** Indica se o usuário está verificando a autenticidade do documento. */
	private boolean verificando;	

	/** Construtor padrão */
	public HistoricoMedioMBean() {
		obj = new HistoricoMedio();
	}
	
	/**
	 * Invoca o MBean responsável por realizar a busca de discente para emissão do histórico.
	 * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/medio/menus/discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String buscarDiscente() throws SegurancaException {

		if (!possuiAcessoHistorico()){
			addMensagem(MensagensGerais.USUARIO_NAO_AUTORIZADO);
			return null;
		}

		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.EMISSAO_HISTORICO_MEDIO);

		return buscaDiscenteMBean.popular();
	}	
	
	/**
	 * Verifica se o acesso está sendo realizado através do portal público do sistema
	 * @return
	 */
	private boolean isAcessoPublico(){
		
		String reqURL = getCurrentRequest().getRequestURI();
		reqURL = reqURL.substring( getCurrentRequest().getContextPath().length() );
		
		if (getUsuarioLogado() == null && reqURL.startsWith("/public/"))
				return true;
		
		return false;
		
	}	
	
	/**
	 * Verifica se o usuário que está tentando tirar o histórico tem permissão.
	 * @throws SegurancaException 
	 */	
	private boolean possuiAcessoHistorico() throws SegurancaException {
		
		// (PÚBLICO)
		// Se o acesso for realizado através do portal público do sistema, 
		// na autenticação de documentos, não será necessário verificar o papel do usuário. 
		if(isAcessoPublico() && verificando)
			return true;		

		// Permite o coordenador, secretaria e gestor visualizarem o histórico.
		if (isUserInRole(new int[] { SigaaPapeis.COORDENADOR_MEDIO, 
				SigaaPapeis.SECRETARIA_MEDIO, SigaaPapeis.GESTOR_MEDIO }))			
			return true;
		
		//(DOCENTE/DISCENTE) Permissão liberada para visualizar o próprio histórico. 
		if (getSubSistema().equals(SigaaSubsistemas.PORTAL_DISCENTE)
				|| getSubSistema().equals(SigaaSubsistemas.PORTAL_DOCENTE )) 
					return true;		
		
		return false;
		
	}
	
	/**
	 * Reúne todas as informações do discente e monta o histórico.
	 * 
	 * @return
	 */
	private String gerarHistorico() {
		try {
			if (!possuiAcessoHistorico()){
				addMensagem(MensagensGerais.USUARIO_NAO_AUTORIZADO);
				return null;
			}
			
			if (ValidatorUtil.isEmpty(obj))
				throw new NegocioException("Nenhum discente foi selecionado.");
			
			//Verifica se está cancelado ou excluído
			if (obj.getDiscente().getDiscente().isExcluido()) {
				addMensagem(MensagensGerais.DISCENTE_SEM_HISTORICO, StatusDiscente.getDescricao(obj.getDiscente().getStatus()));
				return null;
			}
			
			// Verificar bloqueio de emissão do documento
			if (!AutenticacaoUtil.isDocumentoLiberado(TipoDocumentoAutenticado.HISTORICO_MEDIO, NivelEnsino.MEDIO)) {
				addMensagem(MensagensGerais.HISTORICO_INDISPONIVEL);
				return redirectMesmaPagina();
			}
			
			MatriculaDiscenteSerieDao dao = getDAO(MatriculaDiscenteSerieDao.class);
			DiscenteDao discenteDao = getDAO(DiscenteDao.class);
			MovimentacaoAlunoDao movDao = getDAO(MovimentacaoAlunoDao.class);
			IndiceAcademicoDao indiceDao = getDAO(IndiceAcademicoDao.class);
			try {
				//Carrega as disciplinas do discente

				Collection<SituacaoMatricula> situacoesHistorico = SituacaoMatricula.getSituacoesAtivas();
				situacoesHistorico.addAll(SituacaoMatricula.getSituacoesAproveitadas());
				
				List<MatriculaComponente> disciplinas = dao.findDisciplinasByDiscente(obj.getDiscente(), situacoesHistorico);
				if (ValidatorUtil.isEmpty(disciplinas)){
					addMensagemErro("O discente selecionado não possui matrícula em disciplinas com situação ativa.");
					return null;
				}
				
				//Carrega as observações do discente na série
				obj.setObservacoesDiscente(discenteDao.findObservacoesDiscente(obj.getDiscente()));	
				
				//Carrega a Estratégia de consolidação
				EstrategiaConsolidacaoFactory factory = (EstrategiaConsolidacaoFactory) DefaultExtensionPointFactory.getImplementation(ParametrosGerais.IMPLEMENTACAO_ESTRATEGIA_CONSOLIDACAO_FACTORY);
				EstrategiaConsolidacao estrategia = factory.getEstrategia(obj.getDiscente(), getParametrosAcademicos());	
				
				//Agrupa as notas para cada disciplina 
				List<NotaDisciplina> lista = new ArrayList<NotaDisciplina>();
				List<MatriculaComponenteDependencia> dependencias = dao.findAllMatriculasDependenciaByDiscente(obj.getDiscente());
				for (MatriculaComponente mat : disciplinas){
					
					mat.setEstrategia(estrategia);
					NotaDisciplina notaDisc = new NotaDisciplina();
					notaDisc.setMatricula(mat);
					notaDisc.setDependencia(null);
					
					if (ValidatorUtil.isNotEmpty(dependencias)){
						for (MatriculaComponenteDependencia dep : dependencias){
							if (mat.equals(dep.getMatriculaDependencia())){
								notaDisc.setDependencia(dep);
								break;
							}
						}
					}
					
					/* Tratando os casos de implantação de dependência no histórico. */
					if (ValidatorUtil.isEmpty(dependencias) 
						&& mat.getSerie().getCursoMedio().getId() != obj.getDiscente().getDiscente().getCurso().getId()){
						for (MatriculaComponente disciplina : disciplinas) {
							if (disciplina.getSerie().getNumero().intValue() == mat.getSerie().getNumero().intValue() 
									&& disciplina.getComponente().getId() == mat.getComponente().getId() 
									&& disciplina.getId() != mat.getId() 
									&& SituacaoMatricula.getSituacoesReprovadas().contains(disciplina.getSituacaoMatricula())
									&& disciplina.getAno() < mat.getAno()) {
								MatriculaComponenteDependencia mcDependencia = new MatriculaComponenteDependencia();
								mcDependencia.setMatriculaRegular(disciplina);
								mcDependencia.setMatriculaDependencia(mat);
								mcDependencia.setMatriculaSerieRegular(dao.findSerieAtualDiscente(obj.getDiscente(), disciplina.getAno()));
								notaDisc.setDependencia(mcDependencia);
								notaDisc.getMatricula().setSerie(disciplina.getSerie());
								break;
							}
						}
					}
					
					lista.add(notaDisc);
				}
				
				obj.setDisciplinas(lista);
				
				//Carrega os dados pessoais do discente
				obj.getDiscente().setPessoa(discenteDao.findAndFetch(obj.getDiscente().getPessoa().getId(), Pessoa.class));
				obj.getDiscente().getPessoa().prepararDados();
				obj.getDiscente().getPessoa().getPais().getNome();
				obj.getDiscente().getPessoa().getMunicipioUf();
				if (obj.getDiscente().getPessoa().getEnderecoContato() != null) {
					obj.getDiscente().getPessoa().getEnderecoContato().getDescricao();
					if (obj.getDiscente().getPessoa().getEnderecoContato().getMunicipio() != null && obj.getDiscente().getPessoa().getEnderecoContato().getMunicipio().getId() != 0)
						obj.getDiscente().getPessoa().getEnderecoContato().getMunicipio().getNomeUF();
						
				}				
				// Verifica se o discente está concluído
				if (!StatusDiscente.getStatusComVinculo().contains(obj.getDiscente().getStatus()) && 
						obj.getDiscente().getStatus() != StatusDiscente.ATIVO_DEPENDENCIA) {
				
					if (obj.getDiscente().isConcluido()) {
						obj.getDiscente().setMovimentacaoSaida(movDao.findConclusaoByDiscente(obj.getDiscente().getId()));
					}
			
					if (obj.getDiscente().getMovimentacaoSaida() == null) {
						obj.getDiscente().setMovimentacaoSaida(movDao.findUltimoAfastamentoByDiscente(obj.getDiscente().getId(), true, false));
					}
				}
				
				//Calcula a média do discente
				float media = (float) indiceDao.calculaMediaGeralDiscente(obj.getDiscente().getId());
				obj.setMediaDiscente(media);
				
				
				if (comprovante == null && !verificando) {
					comprovante = geraEmissao(TipoDocumentoAutenticado.HISTORICO_MEDIO,
							obj.getDiscente().getMatricula().toString(),
							geraSemente(disciplinas), Integer
									.toString(obj.getDiscente().getId()), null, false);
				}			
				
				if (comprovante != null) 
					obj.setDataHistorico(comprovante.getHoraEmissao());
				else
					obj.setDataHistorico(new Date());
				
			} finally {
				if (dao != null)
					dao.close();
				if (discenteDao != null)
					discenteDao.close();
				if (movDao != null)
					movDao.close();
				if (indiceDao != null)
					indiceDao.close();
			}			
			
			return geraArquivo();
		} catch(ExpressaoInvalidaException e) {
			addMensagem(MensagensGerais.ERRO_EXPRESSOES_HISTORICO);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			notifyError(e);
			addMensagemErroPadrao();
			return null;
		}
	}	
	
	/** Gera um arquivo formatado com os dados do histórico do discente.
	 *  <br>Método não invocado por JSP´s.
	 * @param historicos
	 * @param nivel
	 * @return
	 */
	private String geraArquivo() throws DAOException, JRException, IOException {
		
		Map<String, Object> hs = new HashMap<String, Object>();
		hs.put("nomeSistema", RepositorioDadosInstitucionais.get("siglaSigaa") + " - " + RepositorioDadosInstitucionais.get("nomeSigaa") );
		hs.put("nomeInstituicao", RepositorioDadosInstitucionais.get("siglaInstituicao") + " - " + RepositorioDadosInstitucionais.get("nomeInstituicao") );
		hs.put("enderecoInstituicao", RepositorioDadosInstitucionais.get("enderecoInstituicao") );
		hs.put("logo_ifes", JasperReportsUtil.class.getResourceAsStream("/br/ufrn/sigaa/relatorios/fontes/ufrn.gif"));
		hs.put("logo_adm_sistema", JasperReportsUtil.class.getResourceAsStream("/br/ufrn/sigaa/relatorios/fontes/logosuper.gif"));

		hs.put("pathSubRelMatriculadas", getReportSIGAA("HistoricoDisciplinasMedio.jasper"));
		hs.put("pathSubRelObservacoes",	getReportSIGAA("HistoricoDiscenteObservacoes.jasper"));

		if (comprovante != null)
			hs.put("codigoSeguranca", comprovante.getCodigoSeguranca());
		
		hs.put("siteVerificacao", ParametroHelper.getInstance()
				.getParametro(ConstantesParametro.ENDERECO_AUTENTICIDADE));
		
		List<HistoricoMedio> historicos = new ArrayList<HistoricoMedio>();
		historicos.add(obj);

		JRDataSource jrds = new JRBeanCollectionDataSource(historicos);
		JasperPrint prt = JasperFillManager.fillReport(
				getReportSIGAA("HistoricoMedio.jasper"), hs, jrds);

		getCurrentResponse().setContentType("application/pdf");
		getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=historico_" + obj.getDiscente().getMatricula() + ".pdf");
		
		File tempFile = File.createTempFile("historico_" + obj.getDiscente().getMatricula(), ".pdf");
		JasperExportManager.exportReportToPdfFile(prt, tempFile.getAbsolutePath());
		
		File assinado = null;
		try {
			assinado = AssinaturaDigitalService.assinarPdf(tempFile);
			getCurrentResponse().getOutputStream().write(FileUtils.readFileToByteArray(assinado));
		} catch(Exception e) {
			e.printStackTrace();
			getCurrentResponse().getOutputStream().write(FileUtils.readFileToByteArray(tempFile));				
		}
			
		if (FacesContext.getCurrentInstance() != null)
			FacesContext.getCurrentInstance().responseComplete();

		return null;
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
	 * Emissão de um histórico de discente a partir de um link em uma página,
	 * passando como parâmetro o id do discente.<br>
	 * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/medio/menus/discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */	
	public String selecionaDiscenteForm() throws ArqException {
		Integer idDiscente = getParameterInt("id");
		if (idDiscente == null) {
			addMensagem(MensagensGerais.DISCENTE_INVALIDO_HISTORICO);
			return null;
		}
		DiscenteDao dao = getDAO(DiscenteDao.class);
		try {
			obj.setDiscente( (DiscenteMedio) dao.findByPK(idDiscente) );
		} finally {
			dao.close();
		}
		return selecionaDiscente();
	}	

	/**
	 * Gera o histórico de discente selecionado.
	 * <br>Método não invocado por JSP´s.
	 * @return
	 * @throws ArqException
	 */	
	@Override
	public String selecionaDiscente() throws ArqException {
		return gerarHistorico();
	}


	/** Seta o discente o qual será emitido o histórico.
	 * Método não invocado por JSP's.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.pessoa.dominio.Discente)
	 */	
	@Override
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		this.obj.setDiscente( (DiscenteMedio) discente );
	}

	/**
	 * Exibe o histórico depois de validar.
	 * <br>Método não invocado por JSP´s.
	 */
	@Override
	public void exibir(EmissaoDocumentoAutenticado comprovante,
			HttpServletRequest req, HttpServletResponse res) {
		DiscenteDao dao = getDAO(DiscenteDao.class);
		this.comprovante = comprovante;
		verificando = true;
		try {
			// Buscar discente
			obj.setDiscente( (DiscenteMedio) dao.findByPK(Integer.parseInt(comprovante.getDadosAuxiliares())) );
			selecionaDiscente();					
		} catch (Exception e) {
			addMensagemErroPadrao();
		}

	}

	/**
	 * Valida mudanças no Histórico.
	 * <br>Método não invocado por JSP´s.
	 */	
	@Override
	public boolean validaDigest(EmissaoDocumentoAutenticado comprovante) {
		String matricula = comprovante.getIdentificador();
		DiscenteDao dao = getDAO(DiscenteDao.class);
		MatriculaDiscenteSerieDao matriculaDao = getDAO(MatriculaDiscenteSerieDao.class);
		try {
			// Buscar discente
			DiscenteAdapter discente = null;
			try {
				discente = dao.findByMatricula(new Long(matricula), NivelEnsino.MEDIO);
			} catch (DAOException e) {
				if (comprovante.getDadosAuxiliares() != null) {
					discente = dao.findByPrimaryKey(Integer.parseInt(comprovante.getDadosAuxiliares()), DiscenteMedio.class);
				} else {
					discente = null;
				}
			}

			if (discente == null)
				return false;

			List<MatriculaComponente> disciplinas = matriculaDao.findDisciplinasByDiscente(new DiscenteMedio( discente.getId() ));
			String semente = geraSemente(disciplinas);

			String codigoVerificacao = AutenticacaoUtil.geraCodigoValidacao(comprovante, semente);
			if (codigoVerificacao.equals(comprovante.getCodigoSeguranca()))
				return true;

		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
		}
		return false;
	}

}
