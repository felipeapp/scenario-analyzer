/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */
package br.ufrn.sigaa.ensino.tecnico.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CSVUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dominio.TabelaCSV;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.graduacao.dao.ImportacaoDiscenteOutrosConcursosDao;
import br.ufrn.sigaa.ensino.tecnico.dao.PessoaTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.AtributoClasseMapeavelTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ImportacaoDiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.LeiauteArquivoImportacaoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.MapeamentoAtributoCampoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.PessoaTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ProcessoSeletivoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ResultadoClassificacaoCandidatoTecnico;
import br.ufrn.sigaa.negocio.PessoaValidator;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Controller respons�vel pela importa��o de dados e cadastramento de discentes aprovados no curso t�cnico.
 * 
 * @author �dipo Elder F. de Melo
 * @author Fred_castro
 *
 */
@Component("importaAprovadosTecnicoMBean")
@Scope("session")
public class ImportaAprovadosTecnicoMBean extends SigaaAbstractController<ImportacaoDiscenteTecnico> {
	
	/** Encapsula o resultado da consulta para o suggestionBox
	 * @author �dipo Elder F. de Melo
	 */
	public class AutoCompleteEncapsulator {
		/** Objeto a ser selecionado. */
		private PersistDB obj;
		/** M�todo invocado para descrever o objeto. Exemplo: nome, descricao, descricaoCompleta, etc. */
		private String metodoDescritor;
		/** Construtor parametrizado.
		 * @param obj
		 * @param metodoDescritor
		 */
		public AutoCompleteEncapsulator(PersistDB obj, String metodoDescritor){
			this.obj = obj;
			this.metodoDescritor = metodoDescritor;
		}
		public PersistDB getObjeto(){ return obj; }
		public String getDescritor(){  
			return ReflectionUtils.evalProperty(obj, metodoDescritor); }
	}
	
	/** Codifica��o utilizada no arquivo. */
	private String codificacao;
	/** Arquivo de origem dos dados para importa��o das inscri��es do Vestibular. */
	private UploadedFile arquivoUpload;
	/** Nome dos items constantes no cabe�alho do arquivo a ser importado. */
	private String[] cabecalhos;
	/** Separador usado nos arquivo de dados (CSV) */ 
	private String separador;
	/** Campo a ser mapeado em discente. */
	private String campo;
	/** Valor do campo a ser mapeado na equival�ncia entre valores e entidades. */
	private String valor;
	/** Atributo a ser mapeado em discente. */
	private String atributo;
	/** Mapa de dados a serem mapeados nos atributos da classe Discente, no par cabe�alho do arquivo de origem/atributo mape�vel. */
	private Map<String, AtributoClasseMapeavelTecnico> atributosMapeados;
	/** Cole��o de atributos a serem mapeados em discente. */
	private Collection<AtributoClasseMapeavelTecnico> atributosMapeaveis;
	/** Cole��o de tabelas de equival�ncia para os atributos que precisam de equival�ncia. A equival�ncia � 
	 * entre o valor de um campo do arquivo importado para um ID de um atributo. */
	private Map<AtributoClasseMapeavelTecnico, Map<String, PersistDB>> tabelasEquivalencia;
	/** Tabela de equival�ncia de um atributo. */
	private Map<String, PersistDB> tabelaEquivalencia;
	/** Cole��o de SelectItens que podem ser atribu�do em uma equivalencia entre valores importados e valores no banco de dados. */
	private Collection<SelectItem> valoresEquivalenciaCombo;
	/** Aba selecionada na view, para exibir a tabela de equival�ncia. */
	private String abaSelecionada;
	/** Atributo de classe mape�vel de equival�ncia utilizado no formul�rio. */
	private AtributoClasseMapeavelTecnico atributoMapeavelEquivalencia;
	/** Hist�rico de mapeamento de atributos. */
	private Collection<MapeamentoAtributoCampoTecnico> historicoMapeamento;
	/** Define se o modo de opera��o do controller � de defini��o de leiaute do arquivo. */
	private boolean definicaoLeiaute;
	/** Leiaute do arquivo a ser definido/utilizado na importa��o de dados. */
	private LeiauteArquivoImportacaoTecnico leiauteArquivoImportacao;
	/** Cole��o de selecItem de leiautes cadastrados. */
	private Collection<SelectItem> leiauteImportacaoCombo;
	/** Lista de leiautes cadastrados. */
	private List<LeiauteArquivoImportacaoTecnico> listaLeiautes;
	/** Cole��o de valores n�o mapeados. */
	private List<SelectItem> valoresNaoMapeados;
	/** Dados do arquivo CSV para importa��o de candidatos aprovados. */
	private TabelaCSV tabelaCSV;
	/** Indica que o usu�rio est� editando a �ltima equival�ncia de valores. */
	private boolean ultimaEquivalencia;
	/** Indica que o usu�rio est� editando a primeira equival�ncia de valores. */
	private boolean primeiraEquivalencia;
	/** Entidade equivalente ao valor a ser importado. */
	private PersistDB entidadeEquivalente;
	
	/** Construtor padr�o. */
	public ImportaAprovadosTecnicoMBean() {
		try {
			init();
		} catch (DAOException e) {
			notifyError(e);
		}
	}
	
	/**
	 * Inicia o processo de configura��o do leiaute do arquivo com os dados a serem importados. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/menus/importacao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarDefinicaoLeiaute() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO);
		init();
		prepareMovimento(SigaaListaComando.DEFINICAO_LEIAUTE_IMPORTACAO_TECNICO);
		setOperacaoAtiva(SigaaListaComando.DEFINICAO_LEIAUTE_IMPORTACAO_TECNICO.getId());
		definicaoLeiaute = true;
		return formUpload();
	}
	
	/**
	 * Inicia o processo de configura��o do leiaute do arquivo com os dados a serem importados. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/menus/importacao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String listarLeiautes() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO);
		
		listaLeiautes = (List<LeiauteArquivoImportacaoTecnico>) getGenericDAO().findAllAtivos(LeiauteArquivoImportacaoTecnico.class, "descricao");
		if (isEmpty(listaLeiautes)) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		// cira um alista �nica
		Set<LeiauteArquivoImportacaoTecnico> unico = new HashSet<LeiauteArquivoImportacaoTecnico>();
		unico.addAll(listaLeiautes);
		listaLeiautes = new ArrayList<LeiauteArquivoImportacaoTecnico>();
		listaLeiautes.addAll(unico);
		Collections.sort(listaLeiautes, new Comparator<LeiauteArquivoImportacaoTecnico>() {
			@Override
			public int compare(LeiauteArquivoImportacaoTecnico l1, LeiauteArquivoImportacaoTecnico l2) {
				int cmp = l1.getFormaIngresso().getDescricao().compareTo(l2.getFormaIngresso().getDescricao());
				if (cmp == 0)
					cmp = l1.getDescricao().compareTo(l2.getDescricao());
				return cmp;
			}
		});
		return forward("/tecnico/lista_leiaute.jsp");
	}
	
	/**
	 * Inicia o processo de configura��o do leiaute do arquivo com os dados a serem importados. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/tecnico/lista_leiaute.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String view() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO);
		
		int id = getParameterInt("id", 0);
		leiauteArquivoImportacao = getGenericDAO().findByPrimaryKey(id, LeiauteArquivoImportacaoTecnico.class);
		if (isEmpty(leiauteArquivoImportacao)) {
			addMensagem(MensagensArquitetura.OBJETO_NAO_FOI_SELECIONADO);
			return null;
		}
		return forward("/tecnico/view_leiaute.jsp");
	}
	
	/**
	 * Inicia o processo de importa��o de dados e cadastramento de discentes. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/menus/importacao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarImportacao() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO);
		
		ProcessoSeletivoTecnicoMBean psMBean = getMBean("processoSeletivoTecnico");
		if (isEmpty(psMBean.getAllCombo())) 
			addMensagemErro("N�o h� processos seletivos cadastrados.");

		if (hasErrors())
			return null;
		
		init();
		prepareMovimento(SigaaListaComando.IMPORTAR_APROVADOS_TECNICO);
		setOperacaoAtiva(SigaaListaComando.IMPORTAR_APROVADOS_TECNICO.getId());
		definicaoLeiaute = false;
		return formUpload();
	}
	
	/** Listener respons�vel por montar o combo de sele��o de leiaute de arquivo.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/menus/form.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws DAOException 
	 */
	public void processoSeletivoListener(ValueChangeEvent evt) throws DAOException {
		int id = (Integer) evt.getNewValue();
		obj.getProcessoSeletivo().setId(id);
		obj.setProcessoSeletivo(getGenericDAO().refresh(obj.getProcessoSeletivo()));
		if (obj.getProcessoSeletivo() == null)
			obj.setProcessoSeletivo(new ProcessoSeletivoTecnico());
		leiauteImportacaoCombo = null;
	}
	
	/** Inicializa o controller.
	 * @throws DAOException
	 */
	private void init() throws DAOException {
		atributosMapeados = new LinkedHashMap<String, AtributoClasseMapeavelTecnico>();
		cabecalhos = new String[0];
		obj = new ImportacaoDiscenteTecnico();
		separador = ";";
		GenericDAO dao = getGenericDAO();
		atributosMapeaveis = dao.findAll(AtributoClasseMapeavelTecnico.class, "descricao", "asc");
		tabelasEquivalencia = null;
		resultadosBusca = null;
		historicoMapeamento = new ArrayList<MapeamentoAtributoCampoTecnico>();
		leiauteArquivoImportacao = new LeiauteArquivoImportacaoTecnico();
		codificacao = Charset.defaultCharset().name();
		tabelaCSV = new TabelaCSV();
		this.valoresEquivalenciaCombo = new ArrayList<SelectItem>();
		this.valoresNaoMapeados = new ArrayList<SelectItem>();
		setReadOnly(false);
	}
	
	/** Retorna o link para o formul�rio de upload do arquivo com os dados para importa��o.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/tecnico/mapeamento.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String formUpload() {
		return forward("/tecnico/formimportacao.jsp");
	}
	
	/** Carrega o arquivo CSV com os dados a serem importados e trata-os.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/tecnico/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * @throws NegocioException 
	 */
	public String carregarArquivo() throws NegocioException, ClassNotFoundException, InstantiationException, IllegalAccessException, ArqException {
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO);
		
		if (!checkOperacaoAtiva(SigaaListaComando.IMPORTAR_APROVADOS_TECNICO.getId(), SigaaListaComando.DEFINICAO_LEIAUTE_IMPORTACAO_TECNICO.getId()))
			return cancelar();
		if (definicaoLeiaute) {
			validateRequired(leiauteArquivoImportacao.getFormaIngresso(), "Forma de Ingresso", erros);
			validateRequired(leiauteArquivoImportacao.getDescricao(), "Descri��o", erros);
		} else {
			validateRequired(obj.getProcessoSeletivo(), "Processo Seletivo", erros);
		}
		validateRequired(arquivoUpload, "Arquivo", erros);
		if (hasErrors())
			return null;
		GenericDAO dao = getGenericDAO();
		if (!definicaoLeiaute) {
			leiauteArquivoImportacao = dao.refresh(leiauteArquivoImportacao);
		} else {
			Collection<LeiauteArquivoImportacaoTecnico> leiautes = dao.findByExactField(LeiauteArquivoImportacaoTecnico.class, "descricao", leiauteArquivoImportacao.getDescricao());
			if (!isEmpty(leiautes)) {
				for (LeiauteArquivoImportacaoTecnico leiaute : leiautes)
					if (leiaute.getId() != this.leiauteArquivoImportacao.getId() && leiaute.isAtivo()) {
						addMensagemErro("Existe um leiaute cadastrado com esta descri��o. Por favor, informe outra descri��o.");
						return null;
					}
			}
		}
		String[] cabecalhosNovos = new String[0];
		tabelaCSV = new TabelaCSV();
		tabelasEquivalencia = null;
		resultadosBusca = null;
		
		if (arquivoUpload != null) {
			try{
				tabelaCSV = CSVUtils.readInputStream(arquivoUpload.getInputStream(), codificacao, separador);
				// cabe�alho
				if (leiauteArquivoImportacao.isPossuiCabecalho()) {
					cabecalhosNovos = (String[]) tabelaCSV.getCabecalho();
				} else {
					cabecalhosNovos = new String[tabelaCSV.getNumColunas()];
					for (int i = 0; i < tabelaCSV.getNumColunas(); i++)
						cabecalhosNovos[i] = "CAMPO " + (i+1);
				}
			} catch (Exception e) {
				addMensagemErro(e.getMessage());
				e.printStackTrace();
				return null;
			}
		} else {
			addMensagemErro("Selecione um arquivo CSV para enviar.");
		}
		if (tabelaCSV.isEmpty()) {
			addMensagemErro("O arquivo de dados n�o contem informa��es.");
			return null;
		} else {
			// verifica se no arquivo enviado constam todos mapeamentos anteriores.
			LinkedList<String> removidos = new LinkedList<String>();
			for (String cabecalho : cabecalhos) {
				boolean contem = false;
				for (String cabecalhoNovo : cabecalhosNovos) {
					if (cabecalhoNovo.equals(cabecalho))
						contem = true;
				}
				if (!contem) {
					removidos.add(cabecalho);
					if (definicaoLeiaute) {
						Iterator<MapeamentoAtributoCampoTecnico> iterator = leiauteArquivoImportacao.getMapeamentoAtributos().iterator();
						while (iterator.hasNext())
							if (iterator.next().getCampo().equals(cabecalho))
								iterator.remove();
						atributosMapeados.remove(cabecalho);
					}
				}
			}
			if (!isEmpty(removidos)) {
				addMensagemWarning("Os seguintes cabe�alhos foram removidos do leiaute: " + StringUtils.transformaEmLista(removidos));
			}
			cabecalhos = cabecalhosNovos;
			if (definicaoLeiaute)
				leiauteArquivoImportacao.setFormaIngresso(dao.refresh(leiauteArquivoImportacao.getFormaIngresso()));
			else
				leiauteArquivoImportacao = dao.refresh(leiauteArquivoImportacao);
			realizaMapeamentoHistorico();
			if (definicaoLeiaute)
				return forward("/tecnico/mapeamento.jsp");
			else {
				return submeterMapeamento();
			}
		}
	}
	
	/**
	 * Realiza um mapeamento pr�vio entre os campos e os atributos, baseado em
	 * hist�rico de mapeamento anteriores.
	 * @throws DAOException 
	 * @throws SegurancaException 
	 */
	private void realizaMapeamentoHistorico() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO);
		
		if (!definicaoLeiaute)
			historicoMapeamento.addAll(leiauteArquivoImportacao.getMapeamentoAtributos());
		for (MapeamentoAtributoCampoTecnico historico : historicoMapeamento) {
			for (String campo : cabecalhos) {
				if (historico.getCampo().equals(campo)) {
					for (AtributoClasseMapeavelTecnico obj : atributosMapeaveis) {
						if (historico.getAtributoMapeavel().getId() == obj.getId()) {
							atributosMapeados.put(campo, obj);
						}
					}
				}
			}
		}
	}

	/** Retorna o link para o formul�rio de mapeamento de dados em atributos nas classes.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/tecnico/equivalencia.jsp</li>
	 * </ul>
	 * @return
	 */
	public String formMapeamento() {
		if (definicaoLeiaute && leiauteArquivoImportacao.getId() > 0)
			return forward("/tecnico/edit_leiaute.jsp");
		else
			return forward("/tecnico/mapeamento.jsp");
	}
	
	/** Adiciona um mapeamento de um campo do arquivo CSV a um atributo na classe Discente.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/tecnico/mapeamento.jsp</li>
	 * </ul> 
	 * @return
	 * @throws SegurancaException 
	 */
	public String adicionarMapeamento() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO);
		
		if (!checkOperacaoAtiva(SigaaListaComando.IMPORTAR_APROVADOS_TECNICO.getId(),
				SigaaListaComando.DEFINICAO_LEIAUTE_IMPORTACAO_TECNICO.getId()))
			return cancelar();
		validateRequired(campo, "Coluna de Dados do Arquivo", erros);
		validateRequired(atributo, "Atributo Mapeado", erros);
		if (hasErrors()) 
			return null;
		for (AtributoClasseMapeavelTecnico attMap : atributosMapeaveis) {
			if (attMap.getAtributo().equals(atributo)) {
				int linha = leiauteArquivoImportacao.isPossuiCabecalho() ? 1 : 0;
				while (linha < tabelaCSV.getNumLinhas()) {
					// verifica se o tipo de dado � compat�vel
					String dado="";
					try {
						if (leiauteArquivoImportacao.isPossuiCabecalho())
							dado = (String) tabelaCSV.get(linha, campo);
						else {
							dado = (String) tabelaCSV.get(linha, Integer.parseInt(campo.substring(6))-1);
						}
						if (!attMap.isUsaTabelaEquivalencia()) {
								attMap.getTipoDado().parse(dado);
						}
					} catch (IndexOutOfBoundsException e) {
						addMensagemErro("O arquivo enviado n�o est� completo. H� colunas faltando na linha " + (linha + 1) + ". Verifique se o delimitador informado � o correto.");
						return null;
					} catch (Exception e) {
						addMensagemErro("O dado \""+dado+"\" (linha "+(linha + 1)+") n�o � compat�vel com o tipo do atributo ("+attMap.getTipoDado()+").");
						return null;
					}
					if (attMap.isObrigatorio() && isEmpty(dado)) {
						addMensagemErro("O atributo "+attMap.getDescricao()+" � obrigat�rio e o arquivo apresenha o campo \""+campo+"\" em branco na linha "+linha+".");
						return null;
					}
					linha++;
				}
				atributosMapeados.put(campo, attMap);
				MapeamentoAtributoCampoTecnico historico = new MapeamentoAtributoCampoTecnico(campo, attMap);
				if (!historicoMapeamento.contains(historico)) {
					historicoMapeamento.add(historico);
				}
				return null;
			}
		}
		// se chegar neste ponto, houve erro e n�o conseguiu achar o atributo mape�vel 
		addMensagemErro("N�o foi poss�vel adicionar o mapeamento do campo informado.");
		return null;
	}
	
	/** Remove um mapeamento de um campo do arquivo CSV a um atributo na classe Discente.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/tecnico/mapeamento.jsp</li>
	 * </ul> 
	 * @return
	 * @throws SegurancaException 
	 */
	public String removerMapeamento() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO);
		
		if (!checkOperacaoAtiva(SigaaListaComando.IMPORTAR_APROVADOS_TECNICO.getId(),
				SigaaListaComando.DEFINICAO_LEIAUTE_IMPORTACAO_TECNICO.getId()))
			return cancelar();
		String atributo = getParameter("atributo");
		if (atributo != null) {
			for (String campo : atributosMapeados.keySet()) {
				AtributoClasseMapeavelTecnico acm = atributosMapeados.get(campo);
				if (acm.getAtributo().equals(atributo)) {
					Iterator<MapeamentoAtributoCampoTecnico> iterator = historicoMapeamento.iterator();
					while (iterator.hasNext()) {
						if (iterator.next().getAtributoMapeavel().equals(acm)) {
							iterator.remove();
							break;
						}
					}
					atributosMapeados.remove(campo);
					return null;
				}
			}
		}
		// se chegar neste ponto, houve erro e n�o conseguiu achar o atributo mape�vel 
		addMensagemErro("N�o foi poss�vel remover o mapeamento do campo informado.");
		return null;
	}
	
	/** Submete e valida os mapeamentos de campos do arquivo CSV a atributos na classe Discente.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/tecnico/mapeamento.jsp</li>
	 * </ul> 
	 * @return
	 * @throws NegocioException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ArqException 
	 */
	public String submeterMapeamento() throws NegocioException, ClassNotFoundException, InstantiationException, IllegalAccessException, ArqException {
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO);
		
		primeiraEquivalencia = true;
		if (!checkOperacaoAtiva(SigaaListaComando.IMPORTAR_APROVADOS_TECNICO.getId(),
				SigaaListaComando.DEFINICAO_LEIAUTE_IMPORTACAO_TECNICO.getId()))
			return cancelar();
		// valida se todos campos obrigat�rios foram mapeados
		boolean informado = false;
		for (AtributoClasseMapeavelTecnico attMapeavel : atributosMapeaveis) {
			if (attMapeavel.isObrigatorio()) {
				informado = false;
				for (String campo : atributosMapeados.keySet()) {
					if (atributosMapeados.get(campo).equals(attMapeavel)) {
						informado = true;
						break;
					}
				}
				if (!informado) {
					addMensagemErro("N�o foi encontrando o atributo "+attMapeavel.getDescricao()+".");
				}
			}
		}
		if (hasErrors())
			return null;
		// uma vez validado, tenta setar os atributos dos discentes para validar os dados.
		criarListaResultadoClassificacao(false);
		if (hasErrors())
			return null;
		// exibe o formul�rio para definir equival�ncias
		tabelasEquivalencia = new HashMap<AtributoClasseMapeavelTecnico, Map<String,PersistDB>>();
		for (AtributoClasseMapeavelTecnico att : atributosMapeados.values()) {
			if (att.isUsaTabelaEquivalencia()) {
				carregaTabelasEquivalencia(att);
				atributoMapeavelEquivalencia = att; 
				abaSelecionada = atributoMapeavelEquivalencia.getAtributo();
				break;
			}
		}
		if (definicaoLeiaute)
			return forward("/tecnico/confirma_leiaute.jsp");
		else
			return formEquivalencia();
	}
	
	/** Retorna o formul�rio de equival�ncia de dados. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/tecnico/confirmacao.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String formEquivalencia() {
		return forward("/tecnico/equivalencia.jsp");
	}
	
	/** Chama o processador respons�vel pelo cadastramento dos discentes.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/tecnico/confirmacao.jsp</li>
	 * </ul> 
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public String confirmaImportacao() throws NegocioException, ArqException {
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO);
		
		if (!checkOperacaoAtiva(SigaaListaComando.IMPORTAR_APROVADOS_TECNICO.getId(),
				SigaaListaComando.DEFINICAO_LEIAUTE_IMPORTACAO_TECNICO.getId())) {
			return cancelar();
		}
		for (ResultadoClassificacaoCandidatoTecnico resultado : obj.getResultadosImportados()){
			resultado.getInscricaoProcessoSeletivo().setProcessoSeletivo(obj.getProcessoSeletivo());
			resultado.getInscricaoProcessoSeletivo().getPessoa().getEnderecoContato().setTipoLogradouro(null);
		}

				MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setColObjMovimentado(historicoMapeamento);
		mov.setCodMovimento(SigaaListaComando.IMPORTAR_APROVADOS_TECNICO);
		execute(mov);
		addMensagem(OPERACAO_SUCESSO);
		removeOperacaoAtiva();
		return cancelar();
	}
	
	/** Chama o processador respons�vel pelo cadastramento dos discentes.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/tecnico/equivalencia.jsp</li>
	 * </ul> 
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public String cadastraLeiaute() throws NegocioException, ArqException {
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO);
		
		if (!checkOperacaoAtiva(SigaaListaComando.IMPORTAR_APROVADOS_TECNICO.getId(),
				SigaaListaComando.DEFINICAO_LEIAUTE_IMPORTACAO_TECNICO.getId()))
			return cancelar();
		leiauteArquivoImportacao.setMapeamentoAtributos(historicoMapeamento);
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(leiauteArquivoImportacao);
		mov.setCodMovimento(SigaaListaComando.DEFINICAO_LEIAUTE_IMPORTACAO_TECNICO);
		execute(mov);
		addMensagem(OPERACAO_SUCESSO);
		removeOperacaoAtiva();
		return cancelar();
	}
	
	/** Altera um leiaute de arquivo de importa��o cadastrado.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/tecnico/equivalencia.jsp</li>
	 * </ul> 
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	@Override
	public String atualizar() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO);
		
		init();
		populaLeiauteArquivoImportacao();
		if (hasErrors()) return null;
		prepareMovimento(SigaaListaComando.DEFINICAO_LEIAUTE_IMPORTACAO_TECNICO);
		setOperacaoAtiva(SigaaListaComando.DEFINICAO_LEIAUTE_IMPORTACAO_TECNICO.getId());
		definicaoLeiaute = true;
		historicoMapeamento = new ArrayList<MapeamentoAtributoCampoTecnico>();
		historicoMapeamento.addAll(leiauteArquivoImportacao.getMapeamentoAtributos());
		atributosMapeados = new LinkedHashMap<String, AtributoClasseMapeavelTecnico>();
		cabecalhos = new String[leiauteArquivoImportacao.getMapeamentoAtributos().size()];
		int i = 0;
		for (MapeamentoAtributoCampoTecnico mapeamento : leiauteArquivoImportacao.getMapeamentoAtributos()) {
			atributosMapeados.put(mapeamento.getCampo(), mapeamento.getAtributoMapeavel());
			cabecalhos[i++] = mapeamento.getCampo();
		}
		setReadOnly(true);
		return forward("/tecnico/edit_leiaute.jsp");
	}
	
	/** Altera um leiaute de arquivo de importa��o cadastrado.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/tecnico/equivalencia.jsp</li>
	 * </ul> 
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public String duplicar() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO);
		
		init();
		populaLeiauteArquivoImportacao();
		if (hasErrors()) return null;
		// redefine o ID do objeto para for�ar o novo cadastro.
		leiauteArquivoImportacao.setId(0);
		for (MapeamentoAtributoCampoTecnico mapeamento : leiauteArquivoImportacao.getMapeamentoAtributos()) {
			mapeamento.setId(0);
		}
		prepareMovimento(SigaaListaComando.DEFINICAO_LEIAUTE_IMPORTACAO_TECNICO);
		setOperacaoAtiva(SigaaListaComando.DEFINICAO_LEIAUTE_IMPORTACAO_TECNICO.getId());
		definicaoLeiaute = true;
		atributosMapeados = new LinkedHashMap<String, AtributoClasseMapeavelTecnico>();
		realizaMapeamentoHistorico();
		return formMapeamento();
	}
	
	/**
	 * M�todo chamado para remover uma entidade.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/tecnico/equivalencia.jsp</li>
	 * </ul> 
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String inativar() throws ArqException, NegocioException {
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO);
		
		init();
		populaLeiauteArquivoImportacao();
		if (!leiauteArquivoImportacao.isAtivo()) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return listarLeiautes();
		}
		if (hasErrors()) return null;
		// redefine o ID do objeto para for�ar o novo cadastro.
		prepareMovimento(ArqListaComando.DESATIVAR);
		MovimentoCadastro mov = new MovimentoCadastro(leiauteArquivoImportacao, ArqListaComando.DESATIVAR);
		try {
			execute(mov);
			addMensagem(OPERACAO_SUCESSO);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
		}
		removeOperacaoAtiva();
		return listarLeiautes();
	}

	/**
	 * Popula o objeto leiauteArquivoImporta��o com os dados persistidos em banco.
	 * @throws SegurancaException 
	 */
	private void populaLeiauteArquivoImportacao() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO);
		
		try {
			GenericDAO dao = getGenericDAO();
			leiauteArquivoImportacao = dao.findByPrimaryKey(getParameterInt("id"), LeiauteArquivoImportacaoTecnico.class);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
		}
		if (leiauteArquivoImportacao == null) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			leiauteArquivoImportacao = new LeiauteArquivoImportacaoTecnico();
		}
	}
	
	/** Removeuma equival�ncia entre um valor a ser importado e o correspondente no banco de dados.<br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/tecnico/equivalencia.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 */
	public String removerEquivalencia() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO);
		
		String valor = getParameter("valor");
		if (isEmpty(valor)) {
			addMensagemErro("Selecione o valor equivalente a ser removido.");
			return null;
		}
		if (tabelaEquivalencia.get(valor) == null) {
			addMensagemErro("O valor selecionado j� foi removido.");
			return null;
		}
		tabelaEquivalencia.put(valor, null);
		valoresNaoMapeados.add(new SelectItem(valor, valor));
		this.valor = null;
		entidadeEquivalente = null;
		return redirectMesmaPagina();
	}
	
	/** Adiciona uma equival�ncia entre um valor a ser importado e o correspondente no banco de dados.<br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/tecnico/equivalencia.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 */
	public String adicionarEquivalencia() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO);
		
		if (isEmpty(entidadeEquivalente)) {
			addMensagemErro("Selecione um valor da lista de sugest�es.");
			return null;
		}
		tabelaEquivalencia.put(campo, entidadeEquivalente);
		Iterator<SelectItem> iterator = valoresNaoMapeados.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().getValue().equals(campo)) {
				iterator.remove();
				break;
			}
		}
		valor = null;
		entidadeEquivalente = null;
		return redirectMesmaPagina();
	}
	
	/**
	 * Este m�todo n�o faz nada, mas � necess�rio para que a JSP possa seta o objeto corretamente 
	 * quando o usu�rio seleciona da suggetionBox. Se n�o chamar este m�todo, d� uma lazy exception.<br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/tecnico/equivalencia.jsp</li>
	 * </ul>
	 */
	public void autoCompleteSelect() {
		// evita lazy exception
		if (entidadeEquivalente != null)
			entidadeEquivalente.toString();
	}
	
	/** Sugere valores para o que o usu�rio possa escolher qual objeto corresponde ao valor a ser importado.<br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/tecnico/equivalencia.jsp</li>
	 * </ul>
	 * @param valor
	 * @return
	 * @throws DAOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public List<AutoCompleteEncapsulator> autoComplete(Object valor) throws DAOException, ClassNotFoundException {
		List<AutoCompleteEncapsulator> itens = new LinkedList<AutoCompleteEncapsulator>();
		// consulta os poss�veis valores que a equival�ncia pode assumir;
		Collection<PersistDB> lista = null;
		valor = StringUtils.toAsciiAndUpperCase((String) valor) + "%";
		ImportacaoDiscenteOutrosConcursosDao dao = getDAO(ImportacaoDiscenteOutrosConcursosDao.class);
		Class<PersistDB> classe = (Class<PersistDB>) Class.forName(atributoMapeavelEquivalencia.getClasseEquivalente());
		String field = atributoMapeavelEquivalencia.getAtributoBuscaAutoComplete();
		boolean somenteAtivos = atributoMapeavelEquivalencia.isApenasAtivos();
		lista = dao.findSuggestionEquivalencia(classe, field, (String) valor, somenteAtivos);
		if (!isEmpty(lista)) {
			for (PersistDB obj : lista) {
				AutoCompleteEncapsulator capsula = new AutoCompleteEncapsulator(obj, atributoMapeavelEquivalencia.getMetodoDescricao());
				itens.add(capsula);
			}
		}
		return itens;
	}
	
	/** Carrega uma tabela de equival�ncia para um atributo espec�fico. O usu�rio dever� preencher
	 * os dados desta tabela indicando a qual ID de curso refere-se um valor de nome de curso, por exemplo.<br>
	 * M�todo n�o invocado por JSP's.
	 * @throws DAOException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SegurancaException 
	 */
	public void equivalenciaAnterior() throws DAOException, ClassNotFoundException, InstantiationException, IllegalAccessException, SegurancaException {
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO);
		
		int id = atributoMapeavelEquivalencia.getId();
		Iterator<AtributoClasseMapeavelTecnico> iterator = getAtributosComEquivalencia().iterator();
		AtributoClasseMapeavelTecnico anterior = null;
		if (iterator.hasNext()) 
			anterior = iterator.next();
		int idPrimeiro = anterior != null ? anterior.getId() : 0;
		while (iterator.hasNext()) {
			AtributoClasseMapeavelTecnico attr = iterator.next();
			if (attr.getId() == id) {
				carregaTabelasEquivalencia(anterior);
				break;
			}
			anterior = attr;
		}
		if (atributoMapeavelEquivalencia.getId() == idPrimeiro)
			primeiraEquivalencia = true;
		else
			primeiraEquivalencia = false;
		redirectMesmaPagina();
	}
	
	/** Carrega uma tabela de equival�ncia para um atributo espec�fico. O usu�rio dever� preencher
	 * os dados desta tabela indicando a qual ID de curso refere-se um valor de nome de curso, por exemplo.<br>
	 * M�todo n�o invocado por JSP's.
	 * @throws DAOException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NegocioException 
	 * @throws SegurancaException 
	 */
	public String proximaEquivalencia() throws DAOException, ClassNotFoundException, InstantiationException, IllegalAccessException, NegocioException, SegurancaException {
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO);
		
		if (isEmpty(tabelaEquivalencia)) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return cancelar();
		}
		primeiraEquivalencia = false;
		// verifica se todos valores foram setados
		StringBuilder valores = new StringBuilder();
		for (String key : tabelaEquivalencia.keySet()) {
			PersistDB equivalente = tabelaEquivalencia.get(key);
			if (equivalente.getId() == 0)
				valores.append(key).append(", ");
		}
		if (valores.length() > 0) {
			addMensagemErro("N�o foi definido a equival�ncia para o(s) valore(s): " + valores.substring(0, valores.lastIndexOf(", ")));
			return null;
		}
		int id = atributoMapeavelEquivalencia.getId();
		boolean confirmacao = true;
		Iterator<AtributoClasseMapeavelTecnico> iterator = getAtributosComEquivalencia().iterator();
		while (iterator.hasNext()) {
			AtributoClasseMapeavelTecnico attr = iterator.next();
			if (attr.getId() == id && iterator.hasNext()) {
				carregaTabelasEquivalencia(iterator.next());
				confirmacao = false;
				break;
			}
		}
		ultimaEquivalencia = !iterator.hasNext();
		if (confirmacao)
			return submeterEquivalencias();
		else
			return redirectMesmaPagina();
	}
	
	/**Carrega uma tabela de equival�ncia para um atributo espec�fico. O usu�rio dever� preencher
	 * os dados desta tabela indicando a qual ID de curso refere-se um valor de nome de curso, por exemplo.<br>
	 * M�todo n�o invocado por JSP's.
	 * @param atributoMapeavelEquivalencia
	 * @throws DAOException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SegurancaException 
	 */
	public void carregaTabelasEquivalencia(AtributoClasseMapeavelTecnico atributoMapeavelEquivalencia) throws DAOException, ClassNotFoundException, InstantiationException, IllegalAccessException, SegurancaException {
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO);
		
		this.atributoMapeavelEquivalencia = atributoMapeavelEquivalencia;
		campo = null;
		for (String campoMapeado : atributosMapeados.keySet()) {
			if (atributosMapeados.get(campoMapeado).equals(atributoMapeavelEquivalencia)) {
				campo = campoMapeado;
				break;
			}
		}
		// carrega a tabela de equival�ncias
		tabelaEquivalencia = tabelasEquivalencia.get(atributoMapeavelEquivalencia);
		if (isEmpty(tabelaEquivalencia)) {
			tabelaEquivalencia = new TreeMap<String, PersistDB>();
			tabelasEquivalencia.put(atributoMapeavelEquivalencia, tabelaEquivalencia);
			int linha = leiauteArquivoImportacao.isPossuiCabecalho() ? 1 : 0;
			// cria uma inst�ncia da classe equivalente para cada valor a ser mapeado
			for (; linha < tabelaCSV.getNumLinhas(); linha++) {
				tabelaEquivalencia.put((String) tabelaCSV.get(linha, campo), (PersistDB) Class.forName(atributoMapeavelEquivalencia.getClasseEquivalente()).newInstance());
			}
		}
		// busca os valores cadastrados na class para tentar setar automaticamente
		Collection<?> lista = null;
		if (atributoMapeavelEquivalencia.isApenasAtivos())
			lista = getGenericDAO().findAllAtivos(Class.forName(atributoMapeavelEquivalencia.getClasseEquivalente()));
		else
			lista = getGenericDAO().findAll(Class.forName(atributoMapeavelEquivalencia.getClasseEquivalente()));
		// pre-define os atributos baseados pelo nome/descri��o;
		valoresNaoMapeados = new LinkedList<SelectItem>();
		boolean setado ;
		for (String valor : tabelaEquivalencia.keySet()) {
			if (tabelaEquivalencia.get(valor).getId() == 0) {
				Iterator<?> iterator = lista.iterator();
				setado = false;
				String valorNormalizado = StringUtils.removeEspacosRepetidos(StringUtils.toAscii(valor));
				while (iterator.hasNext()) {
					PersistDB persistDB = (PersistDB) iterator.next();
					String atributoDB = ReflectionUtils.evalProperty(persistDB, atributoMapeavelEquivalencia.getMetodoDescricao());
					atributoDB = StringUtils.removeEspacosRepetidos(StringUtils.toAscii(atributoDB));
					if (atributoDB.equalsIgnoreCase(valorNormalizado)) {
						tabelaEquivalencia.put(valor, persistDB);
						setado = true;
						break;
					}
				}
				if (!setado)
					valoresNaoMapeados.add(new SelectItem(valor, valor));
			}
		}
		entidadeEquivalente = null;
		valor = null;
		// caso n�o use suggestionBox, monta o combo
		// carrega o comboBox com os poss�veis valores de equival�ncia
		if (!this.atributoMapeavelEquivalencia.isUsaSuggestionBox()) {
			valoresEquivalenciaCombo = new TreeSet<SelectItem>();
			// consulta os poss�veis valores que a equival�ncia pode assumir;
			valoresEquivalenciaCombo = toSelectItems(lista, "id", atributoMapeavelEquivalencia.getMetodoDescricao());
		}
	}
	
	public Collection<String> getValoresEquivalencia() {
		return tabelaEquivalencia.keySet();
	}
	
	/** Submete as equival�ncias cadastradas.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/tecnico/equivalencia.jsp</li>
	 * </ul> 
	 * @return
	 * @throws NegocioException
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	public String submeterEquivalencias() throws NegocioException, DAOException, SegurancaException {
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO);
		
		if (!checkOperacaoAtiva(SigaaListaComando.IMPORTAR_APROVADOS_TECNICO.getId(),
				SigaaListaComando.DEFINICAO_LEIAUTE_IMPORTACAO_TECNICO.getId()))
			return cancelar();
		for (AtributoClasseMapeavelTecnico atributoMapeaval : tabelasEquivalencia.keySet()) {
			Map<String, PersistDB> tabelaAtributo = tabelasEquivalencia.get(atributoMapeaval);
			if (tabelaAtributo != null) {
				for (String valor : tabelaAtributo.keySet()) {
					validateRequired(tabelaAtributo.get(valor), valor, erros);
				}
			} else {
				validateRequired(null, atributoMapeaval.getDescricao(), erros);
			}
		}
		if (hasErrors())
			return null;
		criarListaResultadoClassificacao(true);
		if (hasErrors())
			return null;
		int linha = leiauteArquivoImportacao.isPossuiCabecalho() ? 1 : 0;
		Map<String, StringBuilder> mapaErros = new TreeMap<String, StringBuilder>();
		for (ResultadoClassificacaoCandidatoTecnico resultado : obj.getResultadosImportados()) {
			linha++;
			ListaMensagens lista = new ListaMensagens();
			Pessoa pessoa = new Pessoa();
			try {
				BeanUtils.copyProperties(pessoa, resultado.getInscricaoProcessoSeletivo().getPessoa());
			} catch (Exception e) {
				tratamentoErroPadrao(e);
				notifyError(e);
				lista.addErro("Erro de verifica��o de dados: " + e.getMessage());
			}
			// caso sejam dados novos, valida.
			if (pessoa.getId() == 0)
				PessoaValidator.validarDadosPessoais(pessoa, null, PessoaValidator.DISCENTE, lista);
			if (lista.isErrorPresent()) {
				for (MensagemAviso msg : lista.getMensagens()){
					StringBuilder linhas = mapaErros.get(msg.getMensagem());
					if (linhas == null) {
						linhas = new StringBuilder();
						mapaErros.put(msg.getMensagem(), linhas);
					}
					if (linhas.length() > 0)
						linhas.append(", ");
					linhas.append(linha);
				}
			} 
		}
		for (String msg : mapaErros.keySet()) {
			erros.addErro(msg + ". O erro ocorre na(s) linha(s) " + mapaErros.get(msg));
		}
		if (hasErrors())
			return null;
		
		// reordena para exibi��o
		java.util.Collections.sort((List<ResultadoClassificacaoCandidatoTecnico>) obj.getResultadosImportados(), new Comparator<ResultadoClassificacaoCandidatoTecnico>() {
			@Override
			public int compare(ResultadoClassificacaoCandidatoTecnico o1, ResultadoClassificacaoCandidatoTecnico o2) {
				int cmp = o1.getClassificacaoAprovado().compareTo(o2.getClassificacaoAprovado());
				if (cmp == 0)
					cmp = o1.getInscricaoProcessoSeletivo().getPessoa().getNomeAscii().compareTo(o2.getInscricaoProcessoSeletivo().getPessoa().getNomeAscii());
				return cmp;
			}
		});
		
		return forward("/tecnico/confirmacao.jsp");
	}
	
	/** Cria uma lista de discentes utilizando os dados de equival�ncia e mapeamento informados.
	 * @param usaEquivalencia indica se deve utilizar as tabelas de equival�ncias de dados ou n�o. 
	 * @throws NegocioException
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	private void criarListaResultadoClassificacao(boolean usaEquivalencia) throws NegocioException, DAOException, SegurancaException {
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO);
		
		// cria uma proje��o e de resultados de uma consulta para utilizar o HibernateUtils.parseTo()
		GenericDAO dao = getGenericDAO();
		StringBuilder projecao = new StringBuilder();
		int qtdAtributos = 0;
		for (String campo : atributosMapeados.keySet()) {
			if (usaEquivalencia || !atributosMapeados.get(campo).isUsaTabelaEquivalencia()) {
				if (projecao.length() > 1)
					projecao.append(", ");
				projecao.append(atributosMapeados.get(campo).getAtributo());
				qtdAtributos++;
			}
		}
		// cache de dados das equival�ncias
		Map<String, Map<Integer, PersistDB>> cache = new HashMap<String, Map<Integer,PersistDB>>();
		List<Object[]> lista = new ArrayList<Object[]>();
		int linha = leiauteArquivoImportacao.isPossuiCabecalho() ? 1 : 0;
		for (; linha < tabelaCSV.getNumLinhas(); linha++) {
			Object[] objects = new Object[qtdAtributos];
			int i = 0;
			for (String campo : atributosMapeados.keySet()) {
				if (!atributosMapeados.get(campo).isUsaTabelaEquivalencia()) {
					AtributoClasseMapeavelTecnico atributoMapeavel = atributosMapeados.get(campo);
					if (tabelaCSV.get(linha, campo) != null) {
						try {
							objects[i] = atributoMapeavel.getTipoDado().parse((String) tabelaCSV.get(linha, campo));
							if (atributoMapeavel.isObrigatorio() && isEmpty(objects[i])){
								addMensagemErro("O valor informado para o atributo " + atributoMapeavel.getDescricao() + " � obrigat�rio (erro na linha "+linha+").");
								return;
							}
							i++;
						} catch (ParseException e) {
							throw new NegocioException(e);
						}
					}
				} else {
//					if (isEmpty(tabelaCSV.get(linha, campo))) {
//						addMensagemErro("O campo \"" + campo + "\" n�o pode conter valores em branco (linha " + linha + ").");
//						return;
//					}
					if (usaEquivalencia) {
						for (AtributoClasseMapeavelTecnico atributoMapeaval : tabelasEquivalencia.keySet()) { 
							if (atributosMapeados.get(campo).equals(atributoMapeaval)) {
								Map<String, PersistDB> tabelaAtributo = tabelasEquivalencia.get(atributoMapeaval);
								if (tabelaAtributo != null) {
									Map<Integer, PersistDB> cacheAtributo = cache.get(atributosMapeados.get(campo).getAtributo());
									if (cacheAtributo == null) {
										cacheAtributo = new HashMap<Integer, PersistDB>();
										cache.put(atributosMapeados.get(campo).getAtributo(), cacheAtributo);
									}
									PersistDB persistDB = cacheAtributo.get(tabelaAtributo.get(tabelaCSV.get(linha, campo)).getId());
									if (persistDB == null) {
										persistDB = dao.refresh(tabelaAtributo.get(tabelaCSV.get(linha, campo)));
										cacheAtributo.put(tabelaAtributo.get(tabelaCSV.get(linha, campo)).getId(), persistDB);
									}
									objects[i++] = persistDB;
								}
							}
						}
					}
				}
			}
			lista.add(objects);
		}
		// tenta setar todos valores que n�o utilizam equival�ncia
		Collection<ResultadoClassificacaoCandidatoTecnico> resultados = null;
		try {
			resultados = HibernateUtils.parseTo(lista, projecao.toString(), ResultadoClassificacaoCandidatoTecnico.class);
		} catch (Exception e) {
			notifyError(e);
		}
		if (resultados == null) {
			addMensagemErro("N�o foi poss�vel criar uma lista de discentes com os dados importados. Revise se o mapeamento e as equival�ncias entre os dados est�o corretos.");
			return;
		}
		// utiliza dados de pessoa que j� est�o cadastrado no banco e verifica o ano de ingresso
		Collection<Long> cpfs = new ArrayList<Long>(1);
		for (ResultadoClassificacaoCandidatoTecnico resultado : resultados) {
			Long cpf = resultado.getInscricaoProcessoSeletivo().getPessoa().getCpf_cnpj();
			cpfs.add(cpf);
		}
		
		// Caso exista cadastro pr�vio dos dados pessoais, utiliza-os.
		PessoaTecnicoDao pDao = getDAO(PessoaTecnicoDao.class);
		Map<Long, PessoaTecnico> mapaPessoa = pDao.findByCpfCnpj(cpfs);
		for (ResultadoClassificacaoCandidatoTecnico resultado : resultados) {
			Long cpf = resultado.getInscricaoProcessoSeletivo().getPessoa().getCpf_cnpj();
			PessoaTecnico pessoa = mapaPessoa.get(cpf);
			if (pessoa != null)
				resultado.getInscricaoProcessoSeletivo().setPessoa(pessoa);
		}
		obj.setResultadosImportados(resultados);
	}
	
	/** Retorna uma cole��o de atributos mape�veis.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/tecnico/mapeamento.jsp</li>
	 * </ul>
	 * @return
	 */
	public Collection<SelectItem> getAtributosCombo() {
		Collection<SelectItem> combo = new ArrayList<SelectItem>();
		// remove do combo os itens j� inclu�dos no mapeamento
		Collection<AtributoClasseMapeavelTecnico> values = atributosMapeados.values();
		for (AtributoClasseMapeavelTecnico atributo : atributosMapeaveis) {
			if (!values.contains(atributo))
				combo.add(new SelectItem(atributo.getAtributo(), atributo.getDescricao()));
		}
		return combo;
	}
	
	/** Retorna uma cole��o de campos do arquivo com dados a serem importados.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/tecnico/mapeamento.jsp</li>
	 * </ul>
	 * @return
	 */
	public Collection<SelectItem> getCamposCombo() {
		Collection<SelectItem> combo = new ArrayList<SelectItem>();
		Set<String> keys = atributosMapeados.keySet();
		for (String item : cabecalhos) {
			if (!keys.contains(item))
				combo.add(new SelectItem(item,item));
		}
		return combo;
	}
	
	/** Retorna uma cole��o de atributos de classe que s�o mape�veis.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/tecnico/mapeamento.jsp</li>
	 * </ul>
	 * @return
	 */
	public Collection<AtributoClasseMapeavelTecnico> getAtributosComEquivalencia() {
		Collection<AtributoClasseMapeavelTecnico> lista = new ArrayList<AtributoClasseMapeavelTecnico>();
		for (AtributoClasseMapeavelTecnico att : atributosMapeados.values()) {
			if (att.isUsaTabelaEquivalencia()) {
				lista.add(att);
			}
		}
		return lista;
	}
	
	public UploadedFile getArquivoUpload() {
		return arquivoUpload;
	}

	public void setArquivoUpload(UploadedFile arquivoUpload) {
		this.arquivoUpload = arquivoUpload;
	}

	public String getSeparador() {
		return separador;
	}

	public void setSeparador(String separador) {
		this.separador = separador;
	}

	public String getAtributo() {
		return atributo;
	}

	public void setAtributo(String atributo) {
		this.atributo = atributo;
	}

	/** Retorna uma cole��o de separadores da lista de inscritos.<br> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/vestibular/InscricaoVestibular/lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public Collection<SelectItem> getPossiveisSeparadores() {
		Collection<SelectItem> listaSeparadores = new ArrayList<SelectItem>();
		listaSeparadores.add(new SelectItem(String.valueOf(","), "V�rgula"));
		listaSeparadores.add(new SelectItem(String.valueOf(";"), "Ponto e V�rgula"));
		listaSeparadores.add(new SelectItem(String.valueOf("\t"), "Tabula��o"));
		return listaSeparadores;
	}
	
	public String[] getCabecalhos() {
		return cabecalhos;
	}

	public void setCabecalhos(String[] cabecalhos) {
		this.cabecalhos = cabecalhos;
	}

	public String getCampo() {
		return campo;
	}

	public void setCampo(String campo) {
		this.campo = campo;
	}

	public Collection<AtributoClasseMapeavelTecnico> getAtributosMapeaveis() {
		return atributosMapeaveis;
	}
	
	/** Retorna uma cole��o com os atributos mape�veis obrigat�rios.
	 * @return
	 */
	public Collection<AtributoClasseMapeavelTecnico> getAtributosMapeaveisObrigatorios() {
		Collection<AtributoClasseMapeavelTecnico> lista = new ArrayList<AtributoClasseMapeavelTecnico>();
		for (AtributoClasseMapeavelTecnico att : atributosMapeaveis)
			if (att.isObrigatorio())
				lista.add(att);
		return lista;
	}

	public void setAtributosMapeaveis(
			Collection<AtributoClasseMapeavelTecnico> atributosMapeaveis) {
		this.atributosMapeaveis = atributosMapeaveis;
	}

	public Map<String, AtributoClasseMapeavelTecnico> getAtributosMapeados() {
		return atributosMapeados;
	}

	public void setAtributosMapeados(
			Map<String, AtributoClasseMapeavelTecnico> atributosMapeados) {
		this.atributosMapeados = atributosMapeados;
	}

	public Set<Entry<String,PersistDB>> getTabelaEquivalencia() {
		return tabelaEquivalencia.entrySet();
	}
	
	public String getAbaSelecionada() {
		return abaSelecionada;
	}

	public void setAbaSelecionada(String abaSelecionada) {
		this.abaSelecionada = abaSelecionada;
	}

	public AtributoClasseMapeavelTecnico getAtributoMapeavelEquivalencia() {
		return atributoMapeavelEquivalencia;
	}

	public boolean isDefinicaoLeiaute() {
		return definicaoLeiaute;
	}

	public LeiauteArquivoImportacaoTecnico getLeiauteArquivoImportacao() {
		return leiauteArquivoImportacao;
	}

	public void setLeiauteArquivoImportacao(
			LeiauteArquivoImportacaoTecnico leiauteArquivoImportacao) {
		this.leiauteArquivoImportacao = leiauteArquivoImportacao;
	}
	
	/** Retorna uma cole��o de poss�veis leiautes de arquivos para importa��o.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getLeiauteImportacaoCombo() throws DAOException{
		if (leiauteImportacaoCombo == null && obj != null) {
			Collection<LeiauteArquivoImportacaoTecnico> lista = getGenericDAO().findByExactField(LeiauteArquivoImportacaoTecnico.class, "formaIngresso.id", obj.getProcessoSeletivo().getFormaIngresso().getId());
			leiauteImportacaoCombo = toSelectItems(lista, "id", "descricao");
		}
		return leiauteImportacaoCombo;
	}
	
	/** Lista de selectItens com as codifica��es do arquivo.
	 * @return
	 */
	public Collection<SelectItem> getCodificacaoArquivoCombo() {
		Collection<SelectItem> lista = new ArrayList<SelectItem>();
		for (String cod : Charset.availableCharsets().keySet())
			lista.add(new SelectItem(cod, cod));
		return lista;
	}

	public String getCodificacao() {
		return codificacao;
	}

	public void setCodificacao(String codificacao) {
		this.codificacao = codificacao;
	}

	public List<LeiauteArquivoImportacaoTecnico> getListaLeiautes() {
		return listaLeiautes;
	}

	public void setListaLeiautes(List<LeiauteArquivoImportacaoTecnico> listaLeiautes) {
		this.listaLeiautes = listaLeiautes;
	}

	public List<SelectItem> getValoresNaoMapeadosCombo() {
		return valoresNaoMapeados;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
	
	public boolean isUltimaEquivalencia() {
		return ultimaEquivalencia;
	}
	
	public boolean isPrimeiraEquivalencia() {
		return primeiraEquivalencia;
	}

	public PersistDB getEntidadeEquivalente() {
		return entidadeEquivalente;
	}

	public void setEntidadeEquivalente(PersistDB entidadeEquivalente) {
		this.entidadeEquivalente = entidadeEquivalente;
	}
	
	public Collection<SelectItem> getValoresEquivalenciaCombo() {
		return valoresEquivalenciaCombo;
	}
}
