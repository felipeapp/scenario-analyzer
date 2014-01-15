/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 24/06/2010
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.vestibular.InscricaoVestibularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.parametros.dominio.ParametrosVestibular;
import br.ufrn.sigaa.vestibular.dominio.InscricaoVestibular;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

/**
 * Controller responsável pela exportação de dados dos candidatos, para
 * importação no sistema de processamento da Comissão Permanente do Vestibular -
 * COMPERVE.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
@Component("exportarDadosCandidatoBean")
@Scope("request")
public class ExportaDadosCandidatosMBean extends SigaaAbstractController<InscricaoVestibular> {

	// constantes que definem a operação atual do controller.
	/** Define que o controler irá exportar os dados dos candidatos. */
	public final int EXPORTAR_DADOS_CANDIDATOS = 1;
	/** Define que o controler irá exportar as fotos dos candidatos. */
	public final int EXPORTAR_FOTOS_CANDIDATOS = 2;
	
	// constantes que definem o formato de exportação de dados dos candidatos.
	/** Define que o controler irá exportar os dados dos candidatos no formato SQL. */
	private final int ARQUIVO_SQL = 1;
	/** Define que o controler irá exportar os dados dos candidatos no formato CSV. */
	private final int ARQUIVO_CSV = 2;
	
	/** Modo de operação atual do controller (exportar dados dos candidatos, ou as fotos). */
	private int operacao = 0;
	/** Formato do arquivo de dados dos candidatos a ser exportadas (SQL ou CSV). */
	private int formatoArquivo;
	/** Faixa inicial da inscrição para exportar fotos dos candidatos. */
	private boolean selecionaFaixaInscricao;
	/** Coleçao de selectitem de faixa de inscrição para exportar fotos dos candidatos. */
	private Collection<SelectItem> faixaInscricaoCombo;
	/** inicio da faixa de inscrição para exportar fotos dos candidatos. */
	private int faixaInscricao;
	/** Exporta somente os dados de inscrições validadas. */
	private boolean exportarSomenteValidados;
	
	/** Construtor padrão. */
	public ExportaDadosCandidatosMBean() {
	}
	
	/** Redireciona o usuário para a formulário de exportação de dados.<br/>Método não invocado por JSP´s.
	 * @return
	 */
	public String formExportarDados(){
		return forward("/vestibular/InscricaoVestibular/exportar_dados.jsp");
	}

	/**
	 * Inicia a exportação de dados dos candidatos do vestibular.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/menus/candidatos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarDadosInscritos() {
		init();
		operacao = EXPORTAR_DADOS_CANDIDATOS;
		selecionaFaixaInscricao = false;
		return formExportarDados();
	}
	
	/** Inicia a exportação de fotos dos candidatos do vestibular.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/menus/candidatos.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciarFotosCandidato(){
		init();
		operacao = EXPORTAR_FOTOS_CANDIDATOS;
		selecionaFaixaInscricao = true;
		return formExportarDados();
	}
	
	/** Exporta os dados ou as fotos dos candidatos do vestibular. Inicia a exportação de dados dos candidatos do vestibular.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/InscricaoVestibular/exportar_dados.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String exportar() throws ArqException{
		checkRole(SigaaPapeis.VESTIBULAR);
		ValidatorUtil.validateRequired(obj.getProcessoSeletivo(), "Processo Seletivo", erros);
		if (selecionaFaixaInscricao)
			ValidatorUtil.validateRequired(faixaInscricao, "Faixa de Inscrição", erros);
		if (hasErrors()) return null;
		switch (operacao) {
			case EXPORTAR_DADOS_CANDIDATOS: exportarDados(); break;
			case EXPORTAR_FOTOS_CANDIDATOS: exportarFotos(); break;
		}
		return null;
	}

	/** Cria um arquivo zip com as fotos dos candidatos.
	 * @return
	 * @throws DAOException 
	 */
	private void exportarFotos() throws DAOException {
		InscricaoVestibularDao dao = getDAO(InscricaoVestibularDao.class);
		ProcessoSeletivoVestibular ps = dao.refresh(obj.getProcessoSeletivo());
		int quantidadeExportar = ParametroHelper.getInstance().getParametroInt(ParametrosVestibular.QUANTIDADE_MAXIMA_FOTOS_EXPORTAR);
		Boolean validada = this.exportarSomenteValidados ? true : null;
		Collection<InscricaoVestibular> validadas = dao.findByVestibularFaixaInscricao(obj.getProcessoSeletivo().getId(), faixaInscricao, faixaInscricao + quantidadeExportar, validada);
		if (ValidatorUtil.isEmpty(validadas)) {
			addMensagemErro("Não há inscrições validadas");
			return;
		}
		String nomeArquivo =  "fotos_candidatos_" +ps.getSigla() +faixaInscricao +"_"+(faixaInscricao+quantidadeExportar)+ ".zip";
		nomeArquivo = nomeArquivo.replace(" ", "_");
		getCurrentResponse().setContentType("application/zip");
		getCurrentResponse().setCharacterEncoding("iso-8859-15");
		getCurrentResponse().setHeader("Content-disposition", "attachment; filename=\""+nomeArquivo+"\"");
		int inscrProcessada = 0;
		try {
			OutputStream out = getCurrentResponse().getOutputStream();
			ZipOutputStream zout = new ZipOutputStream(out);
			for (InscricaoVestibular inscricao : validadas) {
				if (inscricao.getPessoa().getIdFoto() != null) {
					inscrProcessada = inscricao.getNumeroInscricao();
					zout.putNextEntry(new ZipEntry(inscricao.getNumeroInscricao() + ".jpg"));
					EnvioArquivoHelper.recuperaArquivo(zout, inscricao.getPessoa().getIdFoto());
					zout.closeEntry();
				}
			}
			zout.close();
			FacesContext.getCurrentInstance().responseComplete();
		} catch (Exception e) {
			tratamentoErroPadrao(e, "Inscrição processada: " + inscrProcessada);
		}
	}

	/** Cria um arquivo csv ou sql com os dados dos candidatos.
	 * @return
	 * @throws ArqException 
	 */
	private void exportarDados() throws ArqException {
		InscricaoVestibularDao dao = getDAO(InscricaoVestibularDao.class);
		ProcessoSeletivoVestibular ps = dao.refresh(obj.getProcessoSeletivo());
		try {
			String dados;
			if (formatoArquivo == ARQUIVO_CSV)
				dados = dao.exportaValidadas(obj.getProcessoSeletivo().getId(), InscricaoVestibularDao.FORMATO_CSV);
			else
				dados = dao.exportaValidadas(obj.getProcessoSeletivo().getId(), InscricaoVestibularDao.FORMATO_SQL);
			if (ValidatorUtil.isEmpty(dados)) {
				addMensagemErro("Não há inscrições para o Processo Seletivo selecionado.");
				return;
			}
			
			String nomeArquivo =  "candidatos_" +ps.getSigla();
			nomeArquivo = nomeArquivo.replace(' ', '_');
			
			if (formatoArquivo == ARQUIVO_CSV) {
				nomeArquivo += ".csv";
				getCurrentResponse().setContentType("text/csv");
			} else {
				nomeArquivo += ".sql";
				getCurrentResponse().setContentType("text/sql");
			}
			getCurrentResponse().setCharacterEncoding("iso-8859-15");
			getCurrentResponse().setHeader("Content-disposition", "attachment; filename=\""+nomeArquivo+"\"");
			PrintWriter out = getCurrentResponse().getWriter();
			out.println(dados);
			FacesContext.getCurrentInstance().responseComplete();
		} catch (IOException e) {
			tratamentoErroPadrao(e);
		}
	}
	
	/** Inicializa os atributos do controller. */
	private void init() {
		obj = new InscricaoVestibular();
	}
	
	/** Selecionar o processo seletivo.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/InscricaoVestibular/exportar_dados.jsp</li>
	 * </ul>
	 * 
	 */
	public void selecionaProcessoSeletivo() {
		faixaInscricaoCombo = null;
		faixaInscricao = 0;
	}

	/**
	 * Retorna os possíveis formatos de arquivo para exportação de dados dos
	 * candidatos.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/InscricaoVestibular/exportar_dados.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Collection<SelectItem> getFormatoArquivoCombo() {
		Collection<SelectItem> formatos = new ArrayList<SelectItem>();
		formatos.add(new SelectItem(ARQUIVO_SQL, "Comandos SQL"));
		formatos.add(new SelectItem(ARQUIVO_CSV, "CSV (Comma-separated Values)"));
		return formatos;
	}
	
	/** Retorna uma coleção de selecitem de faixas de inscrições para gerar o lote de exportação de fotos.
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<SelectItem> getFaixaInscricaoCombo() throws HibernateException, DAOException{
		if (faixaInscricaoCombo == null) {
			InscricaoVestibularDao dao = getDAO(InscricaoVestibularDao.class);
			int quantidadeExportar = ParametroHelper.getInstance().getParametroInt(ParametrosVestibular.QUANTIDADE_MAXIMA_FOTOS_EXPORTAR);
			Map<Integer, Integer> mapa = dao.findFaixasInscricao(quantidadeExportar, obj.getProcessoSeletivo().getId());
			faixaInscricaoCombo = new ArrayList<SelectItem>();
			for (Integer key : mapa.keySet())
				faixaInscricaoCombo.add(new SelectItem(key, "De " + key + " à " + mapa.get(key)));
		}
		return faixaInscricaoCombo;
	}
	
	/** Indica se a operação atual do controller é a exportação de dados pessoais.
	 * @return
	 */
	public boolean isExportarDadosPessoais() {
		return operacao == EXPORTAR_DADOS_CANDIDATOS;
	}

	/** Retorna o formato do arquivo de dados dos candidatos a ser exportadas (SQL ou CSV). 
	 * @return
	 */
	public int getFormatoArquivo() {
		return formatoArquivo;
	}

	/** Seta o formato do arquivo de dados dos candidatos a ser exportadas (SQL ou CSV).
	 * @param formatoArquivo
	 */
	public void setFormatoArquivo(int formatoArquivo) {
		this.formatoArquivo = formatoArquivo;
	}

	/** Retorna modo de operação atual do controller (exportar dados dos candidatos, ou as fotos). 
	 * @return
	 */
	public int getOperacao() {
		return operacao;
	}

	/** Seta modo de operação atual do controller (exportar dados dos candidatos, ou as fotos).
	 * @param operacao
	 */
	public void setOperacao(int operacao) {
		this.operacao = operacao;
	}

	public boolean isSelecionaFaixaInscricao() {
		return selecionaFaixaInscricao;
	}

	public int getFaixaInscricao() {
		return faixaInscricao;
	}

	public void setFaixaInscricao(int faixaInscricao) {
		this.faixaInscricao = faixaInscricao;
	}

	public boolean isExportarSomenteValidados() {
		return exportarSomenteValidados;
	}

	public void setExportarSomenteValidados(boolean exportarSomenteValidados) {
		this.exportarSomenteValidados = exportarSomenteValidados;
	}
}
