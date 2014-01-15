/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Criado em 18/01/2010
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.vestibular.IsentoTaxaInscricaoDao;
import br.ufrn.sigaa.arq.dao.vestibular.PessoaVestibularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.vestibular.dominio.IsentoTaxaVestibular;
import br.ufrn.sigaa.vestibular.dominio.PessoaVestibular;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

/**
 * Controller respons�vel pelo cadastro de CPFs de candidatos
 * isentos/parcialmente isentos do pagamento da taxa de inscri��o do vestibular.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
@Component("isencaoTaxaInscricao")
@Scope("request")
public class IsencaoTaxaInscricaoMBean extends	SigaaAbstractController<IsentoTaxaVestibular> {

	/** Lista de CPFs de candidatos isentos, separados por v�rgula. */
	private String listaCPF;

	/** Lista de isentos da taxa do vestibular.*/
	private Collection<IsentoTaxaVestibular> isentos;
	
	/** Caractere separador utilizado na lista de CPFs */
	private String separador;

	/** Lista de poss�veis separadores para a lista de CPF. */
	private Collection<SelectItem> listaSeparadores;
	
	/** CPF para o qual a busca por isento se restringe. */
	private long cpf;
	
	/** Construtor padr�o. */
	public IsencaoTaxaInscricaoMBean() {
		init();
	}

	/** Inicializa os atributos do controller. */
	private void init() {
		obj = new IsentoTaxaVestibular();
		obj.setProcessoSeletivoVestibular(new ProcessoSeletivoVestibular());
		obj.setValor(new Double(0));
	}
	
	/** Cadastrar uma lista de CPFs de candidatos que ter�o isen��o
	 * da taxa de inscri��o do Vestibular. <br> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/vestibular/IsencaoTaxaInscricao/confirma.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		checkRole(SigaaPapeis.VESTIBULAR);
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setColObjMovimentado(isentos);
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_ISENTO_VESTIBULAR);
		execute(mov);
		addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Lista de CPFs");
		return cancelar();
	}
	
	/** Valida os CPFs informados.<br> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/vestibular/IsencaoTaxaInscricao/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	public String validaListaCPF() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.VESTIBULAR);
		validacaoDados(erros);
		if (hasErrors()) return null;
		PessoaVestibularDao dao = getDAO(PessoaVestibularDao.class);
		IsentoTaxaInscricaoDao isentoDao = getDAO(IsentoTaxaInscricaoDao.class);
		isentos = new ArrayList<IsentoTaxaVestibular>();
		// separa os CPFs da lista
		StringTokenizer tokenizer = new StringTokenizer(listaCPF, separador);
		while (tokenizer.hasMoreElements()) {
			String token = tokenizer.nextToken();
			if (token.trim().length() > 0) {
				IsentoTaxaVestibular isento = new IsentoTaxaVestibular();
				// valida o CPF
				Long cpf = UFRNUtils.parseCpfCnpj(token);
				if (cpf == null) {
					addMensagemErro("N�o foi poss�vel verificar o CPF: " + token);
					if (token.length() > 11) 
						addMensagemErro("Verifique se o separador escolhido ("+getDescricaoSeparador()+") est� correto.");
					return null;
				} 
				ValidatorUtil.validateCPF_CNPJ(cpf, token, erros);
				// verifica se h� o cadastro do CPF na lista de isentos
				if (isentoDao.isCpfInscrito(cpf, obj.getProcessoSeletivoVestibular().getId())) {
					addMensagemErro("O CPF " + Formatador.getInstance().formatarCPF_CNPJ(cpf) + " j� est� cadastrado como isento no Processo Seletivo selecionado.");
				}
				// verifica se h� cadastro da pessoa no Vestibular
				PessoaVestibular pessoa = dao.findByCPF(cpf);
				if (pessoa != null) {
					isento.setPessoa(pessoa);
				} else {
					isento.setPessoa(null);
				}
				isento.setCpf(cpf);
				isento.setIsentoTotal(obj.isIsentoTotal());
				isento.setObservacao(obj.getObservacao());
				isento.setProcessoSeletivoVestibular(obj.getProcessoSeletivoVestibular());
				isento.setValor(obj.getValor());
				isento.setTipo(obj.getTipo());
				isento.setRegistroEntrada(getRegistroEntrada());
				if (isentos.contains(isento)) {
					addMensagemErro("J� consta o CPF " + Formatador.getInstance().formatarCPF_CNPJ(cpf) + " na lista de isentos.");
				}
				if (hasErrors()) return null;
				else isentos.add(isento);
			}
		}
		obj.setProcessoSeletivoVestibular(dao.refresh(obj.getProcessoSeletivoVestibular()));
		return forward("/vestibular/IsencaoTaxaInscricao/confirma.jsp");
	}
	
	/** Busca por cadastro de isen��es utilizando uma lista de CPFs.<br> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/vestibular/IsencaoTaxaInscricao/lista.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#buscar()
	 */
	@Override
	public String buscar() throws Exception {
		checkRole(SigaaPapeis.VESTIBULAR);
		IsentoTaxaInscricaoDao isentoDao = getDAO(IsentoTaxaInscricaoDao.class);
		resultadosBusca = new ArrayList<IsentoTaxaVestibular>();
		setTamanhoPagina(25);
		resultadosBusca.addAll(isentoDao.findByCpfProcessoSeletivo(cpf, obj.getProcessoSeletivoVestibular().getId()));
		getPaginacao().setTotalRegistros(resultadosBusca.size());
		getPaginacao().setPaginaAtual(0);
		if (resultadosBusca.isEmpty())
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		return null;
	}
	
	/** Divide o resultado da busca em p�ginas.<br> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/vestibular/IsencaoTaxaInscricao/lista.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getResultadosBusca()
	 */
	@Override
	public Collection<IsentoTaxaVestibular> getResultadosBusca() {
		Collection<IsentoTaxaVestibular> pagina = new ArrayList<IsentoTaxaVestibular>();
		if (!ValidatorUtil.isEmpty(resultadosBusca)) {
			Iterator<IsentoTaxaVestibular> iterator = resultadosBusca.iterator();
			int k = 0;
			// avan�a
			while (k++ < getPaginacao().getPaginaAtual() * getTamanhoPagina() && iterator.hasNext()) 
				iterator.next();
			// monta a p�gina
			k = 0;
			while (k++ < getTamanhoPagina() && iterator.hasNext()) {
				IsentoTaxaVestibular isento = iterator.next();
				pagina.add(isento);
			}
		}
		return pagina;
	}
	
	/** Seta o objeto a ser removido.
	 * <br/>M�todo n�o invocado por JSP�s.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#beforeRemover()
	 */
	@Override
	public void beforeRemover() throws DAOException {
		populateObj(true);
		try {
			prepareMovimento(ArqListaComando.REMOVER);
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
		}
	}
	
	/** Reinicia os atributos do controller ap�s remover o objeto.
	 * <br/>M�todo n�o invocado por JSP�s.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#afterRemover()
	 */
	@Override
	public void afterRemover() {
		init();
	}
	
	/** Valida os dados para o cadastro de isentos: Processo Seletivo, lista de CPFs, separador, tipo de isen��o e valor da taxa.
	 * <br/>M�todo n�o invocado por JSP�s.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(br.ufrn.arq.negocio.validacao.ListaMensagens)
	 */
	@Override
	public boolean validacaoDados(ListaMensagens mensagens) {
		ValidatorUtil.validateRequired(obj.getProcessoSeletivoVestibular(), "Processo Seletivo", mensagens);
		ValidatorUtil.validateRequired(listaCPF, "Lista de CPFs", mensagens);
		if (!"\t".equals(separador)) 
			ValidatorUtil.validateRequired(separador, "Separador", mensagens);
		if (obj.isIsentoTotal()) {
			obj.setValor(null);
		} else {
			ValidatorUtil.validateMinValue(obj.getValor(),1.00, "Valor da Inscri��o", mensagens);
		}
		return mensagens.isErrorPresent();
	}
	
	/**
	 * Prepara para cadastrar uma lista de CPFs de candidatos que ter�o isen��o
	 * da taxa de inscri��o do Vestibular.<br> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/menus/cadastros.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		checkRole(SigaaPapeis.VESTIBULAR);
		init();
		prepareMovimento(SigaaListaComando.CADASTRAR_ISENTO_VESTIBULAR);
		return forward(getFormPage());
	}
	
	/** Exibe o formul�rio de cadastro de CPFs de Isentos.<br> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/vestibular/IsencaoTaxaInscricao/confirma.jsp</li>
	 * </ul>
	 * @return
	 */
	public String formularioCadastro() {
		return forward(getFormPage());
	}
	
	/** Link para a p�gina de formul�rio de cadastro da justificativa.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return "/vestibular/IsencaoTaxaInscricao/form.jsp";
	}

	/** Link para a p�gina de listagem de justificativa.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/vestibular/IsencaoTaxaInscricao/lista.jsp";
	}

	/** Retorna a lista de CPFs de candidatos isentos, separados por v�rgula.
	 * @return
	 */
	public String getListaCPF() {
		return listaCPF;
	}

	/** Seta a lista de CPFs de candidatos isentos, separados por v�rgula.
	 * @param listaCPF
	 */
	public void setListaCPF(String listaCPF) {
		this.listaCPF = listaCPF;
	}

	/** Retorna a cole��o de cadastro de isentos.
	 * @return
	 */
	public Collection<IsentoTaxaVestibular> getIsentos() {
		return isentos;
	}

	/** Retorna o caractere separador utilizado na lista de CPFs
	 * @return
	 */
	public String getSeparador() {
		return separador;
	}

	/** Seta o caractere separador utilizado na lista de CPFs 
	 * @param separador
	 */
	public void setSeparador(String separador) {
		this.separador = separador;
	}
	
	/** Retorna uma cole��o de separadores da lista de CPFs.
	 * @return
	 */
	public Collection<SelectItem> getPossiveisSeparadores() {
		if (listaSeparadores == null) {
			listaSeparadores = new ArrayList<SelectItem>();
			listaSeparadores.add(new SelectItem(String.valueOf(","), "V�rgula"));
			listaSeparadores.add(new SelectItem(String.valueOf(";"), "Ponto e V�rgula"));
			listaSeparadores.add(new SelectItem(String.valueOf("\t"), "Tabula��o"));
		}
		return listaSeparadores;
	}

	/** Retorna um texto descritivo do separador utilizado na lista de CPFs. Ex.: V�rgula, Tabula��o, etc.
	 * @return
	 */
	public String getDescricaoSeparador(){
		if (",".equals(separador)) return "V�rgula";
		else if (";".equals(separador)) return "Ponto-e-V�rgula";
		else if ("\t".equals(separador)) return "Tabula��o";
		else if (" ".equals(separador)) return "Espa�o";
		else return "outro";
	}

	/** Retorna o CPF para o qual a busca por isento se restringe. 
	 * @return
	 */
	public long getCpf() {
		return cpf;
	}

	/** SEta o CPF para o qual a busca por isento se restringe. 
	 * @param cpf
	 */
	public void setCpf(long cpf) {
		this.cpf = cpf;
	}
	
	/** Lista de itens selecion�veis com os tipos de isentos.
	 * @return
	 */
	public Collection<SelectItem> getTiposIsento(){
		Collection<SelectItem> tipos = new ArrayList<SelectItem>();
		tipos.add(new SelectItem(IsentoTaxaVestibular.ESTUDANTE, "Estudante"));
		tipos.add(new SelectItem(IsentoTaxaVestibular.FUNCIONARIO, "Funcion�rio"));
		return tipos;
	}
}
