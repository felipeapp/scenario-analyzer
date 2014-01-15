package br.ufrn.sigaa.pesquisa.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.autenticacao.AutValidator;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.SubTipoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.MembroProjetoDiscenteDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.parametros.dominio.ParametrosPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;

/**
 * MBean responsável por emitir as declarações de docentes e discentes de
 * pesquisa com JasperReports
 * 
 * @author julio
 */
@Component("declaracoesPesquisa")
@Scope("session")
public class DeclaracoesPesquisaMBean extends
		SigaaAbstractController<MembroProjetoDiscente> implements AutValidator {

	/** Atributo que representa o nome do documento */
	public static final String DECLARACAO = "declaracao_bolsista_propesq";
	public static final String DECLARACAO_COORDENACAO = "declaracao_coordenador_propesq";
	/** Atributo que representa o comprovante de autenticidade do documento */
	private EmissaoDocumentoAutenticado comprovante;
	/** Atributo que verifica se a declaração já existe */
	private boolean verificando = false;
	/** Atributo que guarda as declarações do bolsista */
	private Collection<MembroProjetoDiscente> declaracoesBolsista = new ArrayList<MembroProjetoDiscente>();
	/** Atributo que guarda as declarações do coordenador de projetos */
	private Collection<MembroProjeto> declaracoesCoordenador = new ArrayList<MembroProjeto>();
	/**
	 * Atributo que guarda os projetos no qual o servidor pode retirar
	 * declarações como orientador de plano de trabalho
	 */
	private Collection<ProjetoPesquisa> desclaracoesOrientador = new ArrayList<ProjetoPesquisa>();
	/**
	 * Atributo que representa o docente para que possa ser retirado declaração
	 * do mesmo
	 */
	private MembroProjeto docente = new MembroProjeto();

	/**
	 * Método que seta o objeto correto para emissão de declaração dentro os
	 * objetos contidos na lista da busca <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa/pesquisa/declaracoesPesquisa/
	 * lista_declaracao_bolsista_plano_trabalho.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 */
	public void preEmitirDeclaracaoBolsista() throws DAOException {
		int id = getParameterInt("id");
		setObj( getGenericDAO().findByPrimaryKey(id, MembroProjetoDiscente.class) );
		emitirDeclaracaoBolsista();
	}

	/**
	 * Método que recupera o membro projeto correto da lista de declarações de
	 * coordenadores de pesquisa <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa/pesquisa/declaracoesPesquisa/
	 * lista_declaracao_coordenador_projetos.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public void preEmitirDeclaracaoCoordenadorProjeto() throws ArqException,
			NegocioException {
		int id = getParameterInt("id");
		GenericDAO dao = getGenericDAO();
		for (Iterator<MembroProjeto> it = declaracoesCoordenador.iterator(); it
				.hasNext();) {
			MembroProjeto m = (MembroProjeto) it.next();
			if (m.getId() == id) {
				this.docente = dao.findByPrimaryKey(id, MembroProjeto.class);
				break;
			}
		}
		emitirDeclaracaoCoordenador();
	}

	/**
	 * Método que emite a declaração de participação do bolsista <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Não é chamado por JSP(s).</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String emitirDeclaracaoBolsista() {
		try {
			if (validarEmissaoDeclaracaoBolsista()) {

				// texto da declaração
				String texto = getTextoDeclaracaoBolsista();

				// Verifica se a declaração já existe no banco
				if (!verificando) {
					comprovante = geraEmissao(
							TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO,
							((Integer) obj.getId()).toString(),
							gerarSementeDeclaracaoBolsista(),
							null,
							SubTipoDocumentoAutenticado.DECLARACAO_BOLSISTA_PESQUISA,
							true);
				}

				// Setando os parametros
				HashMap<Object, Object> parametros = new HashMap<Object, Object>();
				parametros.put("codigo_seguranca",
						comprovante.getCodigoSeguranca());
				parametros.put("numero_documento",
						comprovante.getNumeroDocumento());
				parametros.put(
						"site_verificacao",
						ParametroHelper.getInstance().getParametro(
								ConstantesParametro.ENDERECO_AUTENTICIDADE));
				parametros.put("texto", texto);
				parametros.put(
						"cidade",
						"Natal, "
								+ Formatador.getInstance()
										.formatarDataDiaMesAno(
												comprovante.getDataEmissao()));
				parametros.put(
						"pro_reitor",
						ParametroHelper.getInstance().getParametro(
								ParametrosPesquisa.NOME_PRO_REITOR_PESQUISA));
				// parametros.put("coordenacao",
				// obj.getPlanoTrabalho().getProjetoPesquisa().getCoordenador().getPessoa().getNome());

				// Preencher declaração
				Collection<MembroProjetoDiscente> lista = new ArrayList<MembroProjetoDiscente>();
				lista.add(obj);
				JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(
						lista);
				JasperPrint prt = JasperFillManager.fillReport(
						JasperReportsUtil
								.getReportSIGAA(DECLARACAO + ".jasper"),
						parametros, jrds);

				if (prt.getPages().size() == 0) {
					addMensagemErro("Não foi encontrado nenhum vínculo com plano passível de emissão de declaração.");
					return null;
				}

				getCurrentResponse().setContentType("application/pdf");
				getCurrentResponse().addHeader(
						"Content-Disposition",
						"attachment; filename=DECLARACAO_BOLSISTA_PROPESQ_"
								+ obj.getDiscente().getPessoa().getNomeAscii()
								+ ".pdf");
				JasperExportManager.exportReportToPdfStream(prt,
						getCurrentResponse().getOutputStream());

				FacesContext.getCurrentInstance().responseComplete();
			}

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro ao buscar as informações da declaração.");
			return null;
		}
		return null;
	}

	/**
	 * Método que emite a declaração do coordenador de projeto de pesquisa <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Não é chamado por JSP(s).</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String emitirDeclaracaoCoordenador() throws ArqException,
			NegocioException {
		try {
			if (validaEmissaoDeclaracaoCoordenador()) {

				// texto da declaração
				String texto = getTextoDeclaracaoCoordenador();

				HashMap<Object, Object> parametros = new HashMap<Object, Object>();

				// Verifica se a declaração já existe
				if (!verificando) {
					comprovante = geraEmissao(
							TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO,
							((Integer) docente.getId()).toString(),
							gerarSementeDeclaracaoCoordenacao(),
							null,
							SubTipoDocumentoAutenticado.DECLARACAO_DOCENTE_COORDENADOR_PROJETO_PESQUISA,
							true);
				}

				// Seta os parametros do relatório
				parametros.put("codigo_seguranca",
						comprovante.getCodigoSeguranca());
				parametros.put("numero_documento",
						comprovante.getNumeroDocumento());
				parametros.put(
						"site_verificacao",
						ParametroHelper.getInstance().getParametro(
								ConstantesParametro.ENDERECO_AUTENTICIDADE));
				parametros.put("texto", texto);
				parametros.put(
						"cidade",
						"Natal, "
								+ Formatador.getInstance()
										.formatarDataDiaMesAno(
												comprovante.getDataEmissao()));
				parametros.put(
						"pro_reitor",
						ParametroHelper.getInstance().getParametro(
								ParametrosPesquisa.NOME_PRO_REITOR_PESQUISA));
				// parametros.put("coordenacao",
				// obj.getPlanoTrabalho().getProjetoPesquisa().getCoordenador().getPessoa().getNome());

				// Preencher declaração
				Collection<MembroProjeto> lista = new ArrayList<MembroProjeto>();
				lista.add(docente);
				JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(
						lista);
				JasperPrint prt = JasperFillManager.fillReport(
						JasperReportsUtil.getReportSIGAA(DECLARACAO_COORDENACAO
								+ ".jasper"), parametros, jrds);

				if (prt.getPages().size() == 0) {
					addMensagemErro("Não foi encontrado nenhum vínculo com o projeto passível de emissão de declaração.");
					return null;
				}

				getCurrentResponse().setContentType("application/pdf");
				getCurrentResponse().addHeader(
						"Content-Disposition",
						"attachment; filename=DECLARACAO_COORDENACAO_PROJETO_PESQUISA"
								+ docente.getNomeMembroProjeto() + ".pdf");
				JasperExportManager.exportReportToPdfStream(prt,
						getCurrentResponse().getOutputStream());
				FacesContext.getCurrentInstance().responseComplete();

				return null;
			}
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro ao buscar as informações da declaração.");
			return null;
		}

		return null;
	}

	public String emitirDeclaracaoOrientacaoPlanoTrabalho() {
		return null;
	}

	/**
	 * Utilizado na validação da autenticidade da declaração a partir do portal
	 * público. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Não é chamado por JSP(s)</li>
	 * </ul>
	 */
	@Override
	public void exibir(EmissaoDocumentoAutenticado comprovante,
			HttpServletRequest req, HttpServletResponse res) {
		MembroProjetoDiscenteDao dao = getDAO(MembroProjetoDiscenteDao.class);
		this.comprovante = comprovante;

		try {
			obj = dao.findByPrimaryKey(
					Integer.parseInt(comprovante.getIdentificador()),
					MembroProjetoDiscente.class);
			verificando = true;
			if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO)
				emitirDeclaracaoBolsista();
		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
		}
	}

	/**
	 * Utilizado na validação da autenticidade da declaração a partir do portal
	 * público. Valida o código de autenticação e o número do documento. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 */
	@Override
	public boolean validaDigest(EmissaoDocumentoAutenticado comprovante) {
 		MembroProjetoDiscenteDao dao = getDAO(MembroProjetoDiscenteDao.class);
		try {
			String codVerificacao = "";

			if (comprovante.getSubTipoDocumento().equals(SubTipoDocumentoAutenticado.DECLARACAO_BOLSISTA_PESQUISA) ) {
				obj = dao.findByPrimaryKey(Integer.parseInt(comprovante.getIdentificador()), MembroProjetoDiscente.class);
				if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO) {
					codVerificacao = AutenticacaoUtil.geraCodigoValidacao(comprovante, gerarSementeDeclaracaoBolsista());
				}
			}
				
			else if (comprovante.getSubTipoDocumento().equals(SubTipoDocumentoAutenticado.DECLARACAO_DOCENTE_COORDENADOR_PROJETO_PESQUISA) ) {
				this.docente = dao.findByPrimaryKey(Integer.parseInt(comprovante.getIdentificador()), MembroProjeto.class);
				if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO) {
					codVerificacao = AutenticacaoUtil.geraCodigoValidacao(comprovante, gerarSementeDeclaracaoCoordenacao());
				}
			}

			if (codVerificacao.equals(comprovante.getCodigoSeguranca()))
				return true;

		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
		}
		return false;
	}

	/**
	 * Utilizado na validação para emissão da declaração. Verifica se o usuário
	 * atual pode imprimir a declaração. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @return <code>true</code> se o usuário puder emitir.
	 */
	private boolean validarEmissaoDeclaracaoBolsista() {

		if (obj == null) {
			addMensagemErro("Declaração não encontrada.");
			return false;
		}
		if ((obj.getDataInicio() != null) && (obj.getDataFim() != null)) {
			ValidatorUtil.validaOrdemTemporalDatas(obj.getDataInicio(),
					obj.getDataFim(), true, "Período", erros);
			ValidatorUtil.validaOrdemTemporalDatas(obj.getDataFim(),
					new Date(), false, "Data fim", erros);
			if (!erros.isEmpty()) {
				addMensagens(erros);
				if (obj != null) {
					clearMensagens();
					addMensagemWarning("Solicite ao Coordenador do Projeto que altere suas datas de início e fim, colocando-as dentro do período do Projeto.");
				}
				return false;
			}
		}

		return true;
	}

	/**
	 * Método que valida se é possível emitir a declaração de coordenador de
	 * projeto de pesquisa <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @return <code>true</code> se o usuário puder emitir a declaração
	 */
	private boolean validaEmissaoDeclaracaoCoordenador() {
		if (docente == null) {
			addMensagemErro("Declaração não encontrada.");
			return false;
		}
		if ((docente.getDataInicio() != null) && (docente.getDataFim() != null)) {
			ValidatorUtil.validaOrdemTemporalDatas(docente.getDataInicio(),
					docente.getDataFim(), true, "Data do Coordenador", erros);
			if (!erros.isEmpty()) {
				addMensagens(erros);
				if (docente != null) {
					clearMensagens();
					addMensagemWarning("Por favor, altere suas datas de início e fim, colocando-as dentro do período do Projeto.");
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * Método que gera o texto da declaração de bolsista
	 * 
	 * @return
	 */
	private String getTextoDeclaracaoBolsista() {

		StringBuilder texto = new StringBuilder();
		texto.append(" Declaramos que o(a) aluno(a) "
				+ obj.getDiscente().getNome() + " mat. "
				+ obj.getDiscente().getMatricula());
		texto.append(" atua(ou) como aluno(a) de iniciação científica, com bolsa "
				+ obj.getTipoBolsaString());
		texto.append(" no projeto \"" + obj.getPlanoTrabalho().getTitulo()
				+ "\"");
		texto.append(" sob a orientação do(a) professor(a) "
				+ obj.getPlanoTrabalho().getOrientador().getNome());
		texto.append(" no período de "
				+ Formatador.getInstance().formatarDataDiaMesAno(
						obj.getDataInicio()));
		if (obj.getDataFim() != null)
			texto.append(" a "
					+ Formatador.getInstance().formatarDataDiaMesAno(
							obj.getDataFim()) + ".");
		else
			texto.append(" até a presente data.");

		return texto.toString();
	}

	/**
	 * Método que retorna o texto da declaração do coordenador de projeto de
	 * pesquisa
	 * 
	 * @return
	 */
	private String getTextoDeclaracaoCoordenador() {
		StringBuilder texto = new StringBuilder();
		texto.append("Declaramos que o professor(a) "
				+ docente.getPessoa().getNome() + ", mat. "
				+ docente.getServidor().getSiape());
		texto.append(", lotado no "
				+ docente.getServidor().getUnidade().getNome()
				+ ", atuou como " + docente.getFuncaoMembro().getDescricao()
				+ " da Pesquisa \"" + docente.getProjeto().getTitulo() + "\",");
		texto.append("no período de "
				+ Formatador.getInstance().formatarDataDiaMesAno(
						docente.getDataInicio()));
		if (docente.getDataFim() != null)
			texto.append(" a " + Formatador.getInstance().formatarDataDiaMesAno(
						docente.getDataFim()) + ".");
		else
			texto.append(" até a presente data.");

		return texto.toString();
	}

	/**
	 * Método que gera a semente para validação da declaração
	 * 
	 * @return
	 */
	private String gerarSementeDeclaracaoBolsista() {
		StringBuilder semente = new StringBuilder();
		semente.append(obj.getId());
		semente.append(obj.getPlanoTrabalho().getProjetoPesquisa().getId());
		semente.append(getTextoDeclaracaoBolsista().hashCode());
		semente.append(TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO);

		return semente.toString();
	}

	/**
	 * Método que gera a semente para validação da declaração
	 * 
	 * @return
	 */
	private String gerarSementeDeclaracaoCoordenacao() {
		StringBuilder semente = new StringBuilder();
		semente.append(docente.getId());
		semente.append(docente.getProjeto().getId());
		semente.append(getTextoDeclaracaoCoordenador().hashCode());
		semente.append(TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO);

		return semente.toString();
	}

	/**
	 * Método que retorna a lista de participações em planos de trabalho <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String listarDeclararoesPlanoTrabalhoBolsista() {
		
		addMensagemWarning("Os certificados e declarações só poderão ser emitidos junto à PROPESQ.");
		return null;
		
//		MembroProjetoDiscenteDao dao = getDAO(MembroProjetoDiscenteDao.class);
//		try {
//			declaracoesBolsista = dao
//					.findMesmoMembroComCadaPlanoTrabalho(getUsuarioLogado()
//							.getDiscente().getId());
//			if (ValidatorUtil.isEmpty(declaracoesBolsista)) {
//				addMensagemErro("Não há nenhuma declaração à ser retirada.");
//				return null;
//			}
//			return redirect("/pesquisa/declaracoesPesquisa/lista_declaracao_bolsista_plano_trabalho.jsf");
//		} catch (DAOException e) {
//			tratamentoErroPadrao(e);
//			return null;
//		}
	}

	/**
	 * Método que retorna para o fomulário de busca das declarações das
	 * Coordenações de Projetos de Pesquisa <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String listarDeclaracoesCoordenadorProjetos() {
		MembroProjetoDao dao = new MembroProjetoDao();
		try {
			declaracoesCoordenador = dao.findCoordenacaoProjetoPesquisaByPessoa(getUsuarioLogado().getPessoa().getId());
			return redirect("/pesquisa/declaracoesPesquisa/lista_declaracao_coordenador_projetos.jsf");
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
			return null;
		}
	}

	/**
	 * Método que retorna o formulário de busca das declarações de orientações
	 * do docente <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String listarDeclararoesOrientacoes() {
		MembroProjetoDao dao = new MembroProjetoDao();
		try {
			desclaracoesOrientador = dao
					.findOrientacoesPlanoTrabalhoDoMembroProjetoByServidor(getUsuarioLogado()
							.getServidor().getId());
			return redirect("/pesquisa/declaracoesPesquisa/lista_declaracao_orientador_plano_trabalho.jsf");
		} catch (Exception e) {
			tratamentoErroPadrao(e);
		}
		return null;
	}

	public Collection<MembroProjetoDiscente> getDeclaracoesBolsista() {
		return declaracoesBolsista;
	}

	public void setDeclaracoesBolsista(
			Collection<MembroProjetoDiscente> declaracoesBolsista) {
		this.declaracoesBolsista = declaracoesBolsista;
	}

	public Collection<MembroProjeto> getDeclaracoesCoordenador() {
		return declaracoesCoordenador;
	}

	public void setDeclaracoesCoordenador(
			Collection<MembroProjeto> declaracoesCoordenador) {
		this.declaracoesCoordenador = declaracoesCoordenador;
	}

	public MembroProjeto getDocente() {
		return docente;
	}

	public void setDocente(MembroProjeto docente) {
		this.docente = docente;
	}

	public Collection<ProjetoPesquisa> getDesclaracoesOrientador() {
		return desclaracoesOrientador;
	}

	public void setDesclaracoesOrientador(
			Collection<ProjetoPesquisa> desclaracoesOrientador) {
		this.desclaracoesOrientador = desclaracoesOrientador;
	}
}
