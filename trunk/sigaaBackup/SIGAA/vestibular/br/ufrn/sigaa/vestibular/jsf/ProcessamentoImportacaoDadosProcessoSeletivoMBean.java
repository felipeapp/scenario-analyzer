/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 09/12/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.vestibular.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.graduacao.ConvocacaoVestibularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.processamento.batch.ListaInscritosVestibular;
import br.ufrn.sigaa.processamento.batch.ProcessamentoImportacaoVestibularThread;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

/**
 * Controlador responsável por gerar a importação dos candidatos inscritos no processo seletivos
 * por meio de arquivo xml.
 * 
 * @author Rafael Gomes
 *
 */
@Component("processoImportacaDadosProcessoSeletivo")
@Scope("session")
public class ProcessamentoImportacaoDadosProcessoSeletivoMBean extends SigaaAbstractController<Object>{

	/** XML com os dados a importar. */
	private UploadedFile xml;
	/** Processo Seletivo para o qual os dados serão importados. */
	private ProcessoSeletivoVestibular processoSeletivo = new ProcessoSeletivoVestibular();
	/** Hora inicial do processamento. */
	private Date inicioProcessamento;
	/** Lista de processos seletivos para o usuário escolher em qual será importado os dados. */
	private List<SelectItem> processosCombo;
	
	/** Inicia a importação dos dados do Processo Seletivo.
	 * @return
	 */
	public String importacaoCandidatosVestibular(){
		processosCombo = null;
		setOperacaoAtiva(SigaaListaComando.PROCESSAR_IMPORTACAO_DADOS_PROCESSO_SELETIVO.getId());
		return forward("/vestibular/importacaoDadosProcessoSeletivo/form.jsp");
	}
	
	/** Importa os dados dos aprovados.
	 * <br/>Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/importacaoDadosProcessoSeletivo/form.jsp</li>
	 * </ul>
	 * @throws NegocioException 
	 */
	@Override
	public String cadastrar() throws ArqException, NegocioException {
		if (!checkOperacaoAtiva(SigaaListaComando.PROCESSAR_IMPORTACAO_DADOS_PROCESSO_SELETIVO.getId()))
			return null;
		validateRequired(processoSeletivo, "Processo Seletivo Vestibular", erros);
		if ( isEmpty(xml) ){
			addMensagemErro("Favor informar o arquivo XML para importação de candidatos.");
		} 
		if (hasErrors()) {
			return null;
		}
		File file = popFile(xml);
		if ( ValidatorUtil.isEmpty(processoSeletivo) )
			processoSeletivo = getGenericDAO().findByPrimaryKey(processoSeletivo.getId(), ProcessoSeletivoVestibular.class);
		// carrega a lista somente se não houver processamento em andamento.
		if (ListaInscritosVestibular.isBloqueada()) {
			addMensagemWarning("Há um processamento não concluído da importação de dados.");
		} else {
			ListaInscritosVestibular.carregarInscritos(processoSeletivo, file);
		}
		return forward("/vestibular/importacaoDadosProcessoSeletivo/progresso.jsp");
	}
	
	@Override
	public String cancelar() {
		ListaInscritosVestibular.reset();
		return super.cancelar();
	}
	
	/**
	 * Método responsável por gerar um arquivo xml, através do arquivo em upload.
	 * @param xml
	 * @return
	 */
	private File popFile(UploadedFile xml) {
		try{
			File f = new File("vestibular.xml");
			InputStream inputStream = xml.getInputStream();
		    OutputStream out = new FileOutputStream(f);
		    byte buf[] = new byte[1024];
		    int len;
		    while( (len = inputStream.read(buf)) > 0 )
		    	out.write(buf,0,len);
		    out.close();
		    inputStream.close();
		   
		    return f;
	    }
	    catch (IOException e){
	    }
	    return null;
	}
	
	/**
	 * @return the xml
	 */
	public UploadedFile getXml() {
		return xml;
	}

	/**
	 * @param xml the xml to set
	 */
	public void setXml(UploadedFile xml) {
		this.xml = xml;
	}

	/**
	 * @return the processoSeletivo
	 */
	public ProcessoSeletivoVestibular getProcessoSeletivo() {
		return processoSeletivo;
	}

	/**
	 * @param processoSeletivo the processoSeletivo to set
	 */
	public void setProcessoSeletivo(ProcessoSeletivoVestibular processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}

	/** Retorna o percentual do processamento. 
	 * @return Percentual do processamento. Ao fim do processamento, retorna o valor 101. 
	 */
	public int getPercentualProcessado() {
		if (!ListaInscritosVestibular.possuiInscritos() || ListaInscritosVestibular.getErro() != null) {
			removeOperacaoAtiva();
			return 101;
		} else if (ListaInscritosVestibular.getTotalProcessados() == 0 || ListaInscritosVestibular.getTotalInscritos() == 0) 
			return 0;
		else 
			return 1 + 100 * ListaInscritosVestibular.getTotalProcessados() / ListaInscritosVestibular.getTotalInscritos();
	}
	
	/** Retorna uma estimativa do tempo restante para o fim do processamento. 
	 * @return Estimativa do tempo restante para o fim do processamento.
	 */
	public String getMensagemProgresso() {
		if (inicioProcessamento == null || ListaInscritosVestibular.getTotalProcessados() == 0)
			return "Aguarde...";
		Date agora = new Date();
		long decorrido = agora.getTime() - inicioProcessamento.getTime();
		long previsao = decorrido * ListaInscritosVestibular.getTotalInscritos() / ListaInscritosVestibular.getTotalProcessados();
		long restante = (previsao - decorrido) / 1000;
		int horas = (int) (restante / 60 / 60);
		restante = (restante - horas * 60 * 60);
		int minutos = (int) (restante / 60);
		restante = (restante - minutos * 60);
		int segundos = (int) (restante);
		String estimativaRestante = String.format("%02d:%02d:%02d", horas, minutos, segundos);
		StringBuilder msg = new StringBuilder("Importando ");
		msg.append(ListaInscritosVestibular.getTotalProcessados())
		.append(" de ")
		.append(ListaInscritosVestibular.getTotalInscritos())
		.append(". Tempo extimado para conclusão: ")
		.append(estimativaRestante);
		return msg.toString();
	}
	
	/** Retorna possíveis erros encontrados no processamento.
	 * @return
	 */
	public Exception getErro() {
		return ListaInscritosVestibular.getErro();
	}
	
	/** Lista de SelectItem de Processos Seletivos do Vestibular.
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getProcessoSeletivoVestibularCombo() throws DAOException{
		if (processosCombo == null){
			processosCombo = toSelectItems(getDAO(ConvocacaoVestibularDao.class).findProcessosSeletivos(), "id", "nome");
		}
		return processosCombo;
	}
	
	/** Inicia o processamento da importação. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/importacaoDadosProcessoSeletivo/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public String getIniciaProcessamento(){
		ProcessamentoImportacaoVestibularThread pThread = new ProcessamentoImportacaoVestibularThread(processoSeletivo, getUsuarioLogado());
		pThread.start();
		inicioProcessamento = new Date();
		return "Por favor, aguarde enquanto importamos o resultado do Processo Seletivo...";
	}
}
