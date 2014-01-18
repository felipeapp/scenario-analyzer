/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 03/03/2008
 *
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import static br.ufrn.arq.util.JasperReportsUtil.getReportSIGAA;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dao.ProcessoDAO;
import br.ufrn.comum.dao.ProcessoDAOImpl;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.integracao.dto.ProcessoDTO;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.BancaPosDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.HomologacaoTrabalhoFinalDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoGenerico;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.metropoledigital.negocio.MetropoleDigitalHelper;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.negocio.ProcessadorCalculaHistorico;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.HomologacaoTrabalhoFinal;
import br.ufrn.sigaa.ensino.stricto.negocio.MovimentoHomologacao;
import br.ufrn.sigaa.negocio.MovimentoCalculoHistorico;
import br.ufrn.sigaa.parametros.dominio.ParametrosAdministrativos;

/**
 * Managed bean para registrar a homologa��o de trabalhos
 * finais em cursos de p�s-gradua��o stricto-sensu.
 * @author David Pereira
 */
@Component("homologacaoTrabalhoFinal") @Scope("request")
public class HomologacaoTrabalhoFinalMBean extends SigaaAbstractController<HomologacaoTrabalhoFinal> implements OperadorDiscente {

	/** Discente que ter� o trabalho de final de curso homologado. */
	private DiscenteStricto discente;

	/** Arquivo de trabalho de final de curso. */
	private UploadedFile arquivo;
	
	/** N�mero do processo de homologa��o do trabalho de final de curso. */
	private Integer numProcesso;
	
	/** Ano do processo de homologa��o do trabalho de final de curso. */
	private Integer anoProcesso;
	
	/** Lista de trabalhos finais de curso pendentes de homologa��o. */
	private List<HomologacaoTrabalhoFinal> lista = new ArrayList<HomologacaoTrabalhoFinal>();

	/** Construtor padr�o. */
	public HomologacaoTrabalhoFinalMBean() {
		obj = new HomologacaoTrabalhoFinal();
	}

	/**
	 * Redireciona para tela de selecionar discente
	 * 
	 * @return
	 * @throws ArqException
	 */
	private String iniciar(int operacao) throws ArqException {
		
		checkRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.PPG);

		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(operacao);

		return buscaDiscenteMBean.popular();
	}

	/**
	 * Inicia o caso de uso de gera��o de documentos da solicita��o de homologa��o de trabalho final de discentes Stricto
	 * chamado a partir de: 
	 * /SIGAA/app/sigaa.ear/sigaa.war/stricto/menu_coordenador.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarGerarDocumentos() throws ArqException {
		prepareMovimento(SigaaListaComando.GERAR_DOCUMENTO_HOMOLOGACAO_TRABALHO_FINAL);
		return iniciar(OperacaoDiscente.DOCUMENTOS_HOMOLOGACAO_DIPLOMA);
	}	
	
	/**
	 * Inicia o caso de uso de solicita��o de homologa��o de trabalho final de discentes Stricto
	 * chamado a partir de: 
	 * /SIGAA/app/sigaa.ear/sigaa.war/stricto/menu_coordenador.jsp
	 * /SIGAA/app/sigaa.ear/sigaa.war/stricto/menus/discente.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarHomologacao() throws ArqException {
		prepareMovimento(SigaaListaComando.HOMOLOGAR_TRABALHO_FINAL_STRICTO);
		return iniciar(OperacaoDiscente.HOMOLOGACAO_TRABALHO_FINAL_STRICTO);
	}
	
	/**
	 * Seleciona, da lista de resultados da busca, o discente que
	 * se deseja solicitar a homologa��o do diploma.
	 * 
	 * M�todo n�o invocado por JSP
	 */
	public String selecionaDiscente() throws ArqException {
		if (getUltimoComando().equals(SigaaListaComando.HOMOLOGAR_TRABALHO_FINAL_STRICTO))
			try {
				return redirecionarCriarHomologacao();
			} catch (NegocioException e) {
				return tratamentoErroPadrao(e);
			}
		if (getUltimoComando().equals(SigaaListaComando.GERAR_DOCUMENTO_HOMOLOGACAO_TRABALHO_FINAL))
			return redirecionarGerarDocumentos();
		
		return null;
	}

	/**
	 * Redireciona para pagina onde � poss�vel gerar os documentos da homologa��o
	 * 
	 * @return
	 * @throws ArqException 
	 */
	private String redirecionarGerarDocumentos() throws ArqException {
		
		HomologacaoTrabalhoFinalDao dao = getDAO(HomologacaoTrabalhoFinalDao.class);
		HomologacaoTrabalhoFinal homologacao = dao.findUltimoByDiscente(discente.getId());
		
		if (homologacao == null)
			addMensagemErro("N�o foi localizado nenhum Solicita��o de Homola��o para o discente selecionado.");
		else
			obj = homologacao;		

		if (hasErrors())
			return null;
		
		gerarDocumentoRequisicaoDiploma(null);
		
		return null;
	}

	/**
	 * Redireciona para o formulario de cria��o da homologa��o
	 * 
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	private String redirecionarCriarHomologacao() throws NegocioException, ArqException {
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class);
		BancaPosDao bancaDao = getDAO(BancaPosDao.class);
		
//		EmprestimoDao emprestimoDao = getDAO(EmprestimoDao.class);
//		
//		// verifica empr�stimos na biblioteca sem devolu��o
//		Collection<Emprestimo> pendencias = emprestimoDao.findPendenciasByDiscente(discente.getId());
//		if (!ValidatorUtil.isEmpty(pendencias)) {
//			List<String> bibliotecas = new ArrayList<String>();
//			StringBuffer listaBiblioteca = new StringBuffer();
//			for (Emprestimo emprestimo : pendencias) {
//				if (!bibliotecas.contains(emprestimo.getMaterial().getBiblioteca().getDescricao())) {
//					bibliotecas.add(emprestimo.getMaterial().getBiblioteca().getDescricao());
//					listaBiblioteca.append(
//							(listaBiblioteca.length() > 0 ? ", " : "") + 
//							emprestimo.getMaterial().getBiblioteca().getDescricao());
//				}
//			}
//			addMensagemErro("N�o � poss�vel concluir o discente "+discente.getMatriculaNome()+" pois o discente tem empr�stimo sem devolu��o na(s) biblioteca(s): "+ listaBiblioteca.toString() + ".");
//			return null;
//		}

		
		ListaMensagens erros = VerificaSituacaoUsuarioBibliotecaUtil.verificaEmprestimoPendenteDiscente(discente.getDiscente());
		if(erros.size() > 0){
			addMensagemWarning("N�o � poss�vel concluir o discente "+discente.getDiscente().getMatriculaNome()+" pois o ele possui empr�stimos pendentes nas bibliotecas.");
			return null;
		}
		
		Collection<MatriculaComponente> matriculadas = dao.findAtividades(discente, new TipoAtividade(TipoAtividade.TESE), SituacaoMatricula.APROVADO);
		BancaPos banca = bancaDao.findMaisRecenteByTipo(discente, BancaPos.BANCA_DEFESA);

		// S� � poss�vel realizar a homologa��o se o aluno tiver uma defesa cadastrada
		if (isEmpty(matriculadas)) {
			addMensagemErro("S� � poss�vel realizar a homologa��o se o aluno tiver uma defesa cadastrada e a atividade de defesa estiver consolidada.");
			return null;
		}
		if( banca == null ){
			addMensagemErro("S� � poss�vel realizar a homologa��o se o aluno tiver uma banca de defesa cadastrada.");
			return null;
		}

		// Verifica se o aluno possui orientador
		OrientacaoAcademicaDao orientacaoDao = getDAO( OrientacaoAcademicaDao.class );
		Collection<OrientacaoAcademica> orientacoes = orientacaoDao.findByDiscente(discente.getId(), OrientacaoAcademica.ORIENTADOR, false);
		if( isEmpty(orientacoes) ){
			addMensagemErro("S� � poss�vel realizar a homologa��o se o aluno tiver um orientador cadastrado.");
			return null;
		}

		// Validar se est� passando do per�odo de homologa��o (6 meses)
		int tempoMaximo = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.TEMPO_MAXIMO_HOMOLOGACAO_STRICTO);
		if (!getAcessoMenu().isPpg() && discente.getAnoEntrada() >= 2005 && CalendarUtils.diferencaMeses(banca.getData(), new Date()) > tempoMaximo) {
			addMensagemErro("Conforme o par�grafo 4 do artigo 43 da resolu��o 072/2004 do CONSEPE, n�o � poss�vel realizar "
					+ "a homologa��o porque o prazo de " + tempoMaximo + " meses se esgotou.");
			return null;
		}
		
		if (!isAllObrigatoriosPagos(discente, erros)){
			addMensagemErro("N�o � poss�vel realizar a homologa��o, quando o aluno possuir componentes pendentes no seu curr�culo.");
			return null;
		}
		
		if (getDAO(MatriculaComponenteDao.class).countMatriculasByDiscente(discente, SituacaoMatricula.MATRICULADO) > 0){
			addMensagemErro("N�o � poss�vel realizar a homologa��o, quando o aluno possuir componentes matriculados.");
			return null;
		}
		
		obj.setBanca(banca);

		return forward("/stricto/homologacao_trabalho_final/info_defesa.jsp");
	}

	/** Cadastra a homologa��o do trabalho. 
	 * Chamado a partir de /stricto/homologacao_trabalho_final/info_defesa.jsp
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws ArqException {
		
		validacaoDados(erros);
		
		if (anoProcesso != null && numProcesso != null){
			obj.setAnoProcesso(anoProcesso);
			obj.setNumProcesso(numProcesso);			
			
			validaProcesso(erros);
		}
		
		if( hasErrors() )
			return null;
		
		try {
			MovimentoHomologacao mov = new MovimentoHomologacao();
			mov.setHomologacao(obj);
			mov.setArquivo(arquivo);
			mov.setCodMovimento(SigaaListaComando.HOMOLOGAR_TRABALHO_FINAL_STRICTO);
			execute(mov);

			addMessage("Homologa��o realizada com sucesso!", TipoMensagemUFRN.INFORMATION);
			
			setConfirmButton("Cadastrar");
			return forward("/stricto/homologacao_trabalho_final/confirmacao_homologacao.jsp");
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());			
			return null;
		}
	}
	
	/**
	 * Utilizado para verificar se o sipac e o sistema de protocolos est�o ativos.
	 * A partir da� exige-se ou n�o a inser��o de dados referentes ao processo de homologacao, como
	 * numero do processo e ano.
	 * �til as Ifes que n�o tem o sipac com o m�dulo de protocolos ativos.
	 * 
	 * Chamado por:
	 * sigaa.war/stricto/homologacao_trabalho_final/info_defesa.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public boolean isExigirDadosProcessoHomologacao() throws DAOException {		
		return ParametroHelper.getInstance().getParametroBoolean(ParametrosAdministrativos.UTILIZA_PROTOCOLO_SIPAC);
	}

	/**
	 * Valida se o processo existe no SIPAC
	 * 
	 * @param erros
	 * @throws DAOException 
	 */
	private void validaProcesso(ListaMensagens erros) throws DAOException {
		if(isExigirDadosProcessoHomologacao()
				&& Sistema.isSipacAtivo() && Sistema.isProtocoloAtivo()) {
			ProcessoDAO procDao = getDAO(ProcessoDAOImpl.class);
			try {			
				ProcessoDTO processoHomologacao = procDao.findByIdentificador(obj.getNumProcesso(), obj.getAnoProcesso(), -1);
				if (processoHomologacao == null) {
					erros.addErro("O processo de homologa��o informado � inv�lido.");
				}
			}finally{
				procDao.close();
			}
		}
	}

	/**
	 * Gera o documento contendo a requisi��o para confec��o de diploma e o formul�rio para cadastro de disserta��o
	 * @throws DAOException 
	 * 
	 * @throws JRException
	 * @throws IOException
	 * @throws DAOException
	 */
	public void gerarDocumentoRequisicaoDiploma(ActionEvent evt) throws DAOException {
		
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), HomologacaoTrabalhoFinal.class);
		
		List<HomologacaoTrabalhoFinal> lista = new ArrayList<HomologacaoTrabalhoFinal>();
		lista.add(obj);
		
		OrientacaoAcademica orientadorAtivo = DiscenteHelper.getUltimoOrientador(obj.getBanca().getDadosDefesa().getDiscente().getDiscente());
		obj.getBanca().getDadosDefesa().getDiscente().setOrientacao(orientadorAtivo);

		// Evitar Lazy
		obj.getBanca().getDadosDefesa().getDiscente().getPessoa();
		
		String matriculaNome = obj.getBanca().getDadosDefesa().getDiscente().getMatriculaNome();
		try {
			
			Map<String, Object> hs = new HashMap<String, Object>();			
			hs.put("nomeSistema", RepositorioDadosInstitucionais.get("siglaSigaa") + " - " + RepositorioDadosInstitucionais.get("nomeSigaa") );
			hs.put("nomeInstituicao", RepositorioDadosInstitucionais.get("siglaInstituicao") + " - " + RepositorioDadosInstitucionais.get("nomeInstituicao") );			
			hs.put("municipioData",RepositorioDadosInstitucionais.get("cidadeInstituicao") + ", " + new SimpleDateFormat("dd 'de' MMMMM 'de' yyyy ").format(new Date()));
			hs.put("logo_ifes", new URL(RepositorioDadosInstitucionais.get("logoInstituicao") ));
			hs.put("logo_adm_sistema", new URL(RepositorioDadosInstitucionais.get("logoInformatica") ));
			
			JRDataSource ds = new JRBeanCollectionDataSource(lista);
			JasperPrint prt = JasperFillManager.fillReport(getReportSIGAA("requisicao_diploma.jasper"), hs, ds);
			JasperReportsUtil.exportarPdf(prt, "requisicao_homologacao_" + matriculaNome + ".pdf", getCurrentResponse());
			
			if (FacesContext.getCurrentInstance() != null)
				FacesContext.getCurrentInstance().responseComplete();			
			
		} catch (JRException e) {
			tratamentoErroPadrao(e, "N�o foi poss�vel montar o Documento de Requisi��o de Diploma");
		} catch (IOException e) {
			tratamentoErroPadrao(e, "N�o foi poss�vel montar o Documento de Requisi��o de Diploma");
		}
	}

	
	/**
	 * Seta o discente a homologar o trabalho final de curso.
	 * M�todo n�o invocado por JSP. 
	 * Chamado a partir de:
	 * BuscaDiscenteMBean.redirecionarDiscente(Discente discente)
	 */
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		try {
			this.discente = (DiscenteStricto) getDAO(DiscenteDao.class).findByPK(discente.getId());
			this.discente.setOrientacao(DiscenteHelper.getOrientadorAtivo(this.discente.getDiscente()));
		} catch (Exception e) {
			discente = null;
			e.printStackTrace();
		}
	}

	/** Atualiza os dados da homologa��o de trabalho de final de curso.
	 * Chamado a partir de: /stricto/homologacao_trabalho_final/lista.jsp
	 */
	@Override
	public String atualizar() throws ArqException {
		anoProcesso = 0;
		numProcesso = 0;
		prepareMovimento(SigaaListaComando.HOMOLOGAR_TRABALHO_FINAL_STRICTO);
		String forward = super.atualizar();
		
		if( obj != null ){
			numProcesso = obj.getNumProcesso();
			anoProcesso = obj.getAnoProcesso();
		}
		
		return forward;
	}
	
	/** Lista as homologa��es de trabalhos de final de cursos pendentes.
	 * Chamado a partir de :/stricto/menu_coordenador.jsp
	 */
	@Override
	public String listar() throws ArqException {
		HomologacaoTrabalhoFinalDao dao = getDAO(HomologacaoTrabalhoFinalDao.class);
		lista = dao.findByUnidadeStatusDiscente(getProgramaStricto().getId(), StatusDiscente.EM_HOMOLOGACAO);
		if( isEmpty(lista) ){
			addMensagemErro("N�o h� solicita��es de homologa��o pendentes.");
			return null;
		}
		return super.listar();
	}
	
	/** Retorna o diret�rio base dos formul�rios utilizados neste controller.
	 * M�todo n�o invocado por JSP.
	 */
	@Override
	public String getDirBase() {
		return "/stricto/homologacao_trabalho_final";
	}
	
	/** Retorna o link para o formul�rio de dados da defesa do trabalho.
	 * M�todo n�o invocado por JSP.
	 */
	@Override
	public String getFormPage() {
		return getDirBase() + "/info_defesa.jsf";
	}

	/** Retorna a lista de trabalhos finais de curso pendentes de homologa��o. 
	 * @return
	 */
	public List<HomologacaoTrabalhoFinal> getLista() {
		return lista;
	}

	/** Seta a lista de trabalhos finais de curso pendentes de homologa��o.
	 * @param lista
	 */
	public void setLista(List<HomologacaoTrabalhoFinal> lista) {
		this.lista = lista;
	}
	
	/** Retorna o arquivo de trabalho de final de curso. 
	 * @return
	 */
	public UploadedFile getArquivo() {
		return arquivo;
	}

	/** Seta o arquivo de trabalho de final de curso.
	 * @param arquivo
	 */
	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	/** Retorna o n�mero do processo de homologa��o do trabalho de final de curso. 
	 * @return
	 */
	public Integer getNumProcesso() {
		return numProcesso;
	}

	/** Seta o n�mero do processo de homologa��o do trabalho de final de curso.
	 * @param numProcesso
	 */
	public void setNumProcesso(Integer numProcesso) {
		this.numProcesso = numProcesso;
	}

	/** Retorna o ano do processo de homologa��o do trabalho de final de curso. 
	 * @return
	 */
	public Integer getAnoProcesso() {
		return anoProcesso;
	}

	/** Seta o ano do processo de homologa��o do trabalho de final de curso.
	 * @param anoProcesso
	 */
	public void setAnoProcesso(Integer anoProcesso) {
		this.anoProcesso = anoProcesso;
	}
	

	/** Valida os dados: n�mero e ano do processo, data da defesa, arquivo com o trabalho de final de curso.
	 * M�todo n�o invocado por JSP.
	 */
	public boolean validacaoDados(ListaMensagens erros) {
		try {
			
			if(isExigirDadosProcessoHomologacao()) {			
				ValidatorUtil.validateRequired(numProcesso, "N�mero do processo", erros);
				ValidatorUtil.validateRequired(anoProcesso, "Ano do processo", erros);
				if (anoProcesso != null && anoProcesso < CalendarUtils.getAno(obj.getBanca().getData()))
					ValidatorUtil.validateRequired(anoProcesso, "O Ano do processo n�o pode ser anterior ao ano da data da defesa", erros);
				
			}			
		} catch (DAOException e) {
			erros.addErro("Erro ao verificar se o SIPAC est� ativo.");			
		}		
		
		if (anoProcesso != null && anoProcesso < CalendarUtils.getAno(obj.getBanca().getData()))
			ValidatorUtil.validateRequired(anoProcesso, "O Ano do processo n�o pode ser anterior ao ano da data da defesa", erros);	
		if(arquivo == null)	
			ValidatorUtil.validateRequired(arquivo, "Arquivo", erros);
		return erros.size() < 0;
	}
	
	/**
	 * Valida os componentes obrigat�rios do curr�culo do discentes que n�o foram pagos. 
	 * Impossibilitando a solicita��o da homologa��o de diploma.
	 * @param d
	 * @return
	 * @throws ArqException 
	 */
	public boolean isAllObrigatoriosPagos(DiscenteStricto d, ListaMensagens erros) throws ArqException{
		//Valida os componentes obrigat�rios do curr�culo do discentes que n�o foram pagos.
		//Impossibilitando a solicita��o da homologa��o de diploma.
		ProcessadorCalculaHistorico proc = new ProcessadorCalculaHistorico();
		
		Collection<ComponenteCurricular> componentesPendentes = new ArrayList<ComponenteCurricular>();
		List<TipoGenerico> equivalenciasDiscente = new ArrayList<TipoGenerico>();
		
		DiscenteDao discenteDao = getDAO(DiscenteDao.class);
		
		Curso curso = discenteDao.findByPrimaryKey(discente.getCurso().getId(), Curso.class);
		
		List<MatriculaComponente> disciplinas = discenteDao.findDisciplinasConcluidasMatriculadas(discente.getId(), true, !MetropoleDigitalHelper.isMetropoleDigital(discente));
		
		
		MovimentoCalculoHistorico mov = new MovimentoCalculoHistorico();
		mov.setUsuarioLogado(getUsuarioLogado());
		mov.setDiscente(discente);
		mov.setSistema(Sistema.SIGAA);
		
		proc.verificarComponentesPendentes(mov, discenteDao, discente, curso, disciplinas, componentesPendentes, equivalenciasDiscente);
		
		if (componentesPendentes.isEmpty())
			return true;
		return false;
		
	}
}
