/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/06/2010
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.ALTERADO_COM_SUCESSO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.vestibular.InscricaoVestibularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.vestibular.dominio.InscricaoVestibular;
import br.ufrn.sigaa.vestibular.dominio.PessoaVestibular;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

/** Controller responsável por validações das inscrições dos candidatos.
 * @author Édipo Elder F. Melo
 *
 */
@Component("validacaoCandidatoBean")
@Scope("request")
public class ValidacaoCandidatoMBean extends SigaaAbstractController<InscricaoVestibular> {

	/** Lista de CPFs de candidatos isentos, separados por vírgula. */
	private String listaInscricao;

	/** Lista de inscrições a validar.*/
	private Collection<InscricaoVestibular> inscricoes;
	
	/** Caractere separador utilizado na lista de inscrições */
	private String separador;
	
	/** Filtra a busca por inscrição restringido a consulta por nome do candidato. */
	private boolean filtroNome;
	/** Filtra a busca por inscrição restringido a consulta pelo CPF do candidato. */
	private boolean filtroCPF;
	/** Filtra a busca por inscrição restringido a consulta pelo número de inscrição do candidato. */
	private boolean filtroInscricao;
	
	/** Construtor padrão. */
	public ValidacaoCandidatoMBean() {
		init();
	}

	/** Inicializa os atributos do controller. */
	private void init() {
		obj = new InscricaoVestibular();
		obj.setProcessoSeletivo(new ProcessoSeletivoVestibular());
	}
	
	/** Valida uma lista de inscrições de candidatos que terão isenção
	 * da taxa de inscrição do Vestibular. <br> Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/vestibular/InscricaoVestibular/confirma.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		if(isOperacaoAtiva(ArqListaComando.ALTERAR.getId())) {
			validaUnicaInscricao();
			if (hasErrors()) return null;
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(ArqListaComando.ALTERAR);
			prepareMovimento(ArqListaComando.ALTERAR);
			mov.setObjMovimentado(obj);
			execute(mov);
			addMensagem(ALTERADO_COM_SUCESSO, "Inscrição");
		} else {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setColObjMovimentado(inscricoes);
			mov.setCodMovimento(SigaaListaComando.VALIDAR_INSCRICAO_VESTIBULAR_LOTE );
			execute(mov);
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Lista de Inscrições");
		}
		return cancelar();
	}
	
	/** Verifica se o candidato possui apenas uma inscrição validada. 
	 * @throws DAOException */
	private void validaUnicaInscricao() throws DAOException {
		if (obj.isValidada()) {
			InscricaoVestibularDao dao = getDAO(InscricaoVestibularDao.class);
			try {
				Collection<InscricaoVestibular> outrasInscricoes = dao.findByPessoaVestibular(obj.getPessoa().getId(), obj.getProcessoSeletivo().getId());
				if (!ValidatorUtil.isEmpty(outrasInscricoes)) {
					for (InscricaoVestibular inscricao : outrasInscricoes) {
						if (inscricao.isValidada()) {
							addMensagemErro("O candidato possui a inscrição Nº "+inscricao.getNumeroInscricao()+" validada para o mesmo CPF.");
							break;
						}
					}
				}
			} finally {
				dao.close();
			}
		}
	}

	/** Valida os CPFs informados.<br> Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/vestibular/InscricaoVestibular/valida_lote.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String validaListaInscricao() throws DAOException {
		validacaoDados(erros);
		if (hasErrors()) return null;
		List<String> inscricoesInvalidas = new ArrayList<String>();
		List<Integer> numInscricoes = new ArrayList<Integer>();
		// separa as inscrições da lista
		StringTokenizer tokenizer = new StringTokenizer(listaInscricao, separador);
		while (tokenizer.hasMoreElements()) {
			String token = tokenizer.nextToken();
			if (token.trim().length() > 0) {
				try {
					int numInscricao = Integer.parseInt(token.trim());
					numInscricoes.add(numInscricao);
				} catch (NumberFormatException e) {
					inscricoesInvalidas.add(token);
				}
			}
		}
		// recupera as inscrições a serem validadas
		InscricaoVestibularDao dao = getDAO(InscricaoVestibularDao.class);
		try {
			inscricoes = dao.findByListaInscricao(numInscricoes, null);
			// recupera as inscrições já validadas
			Collection<InscricaoVestibular> inscricoesValidadas = dao.findValidadasByVestibular(obj.getProcessoSeletivo().getId());
			if (inscricoesValidadas == null) inscricoesValidadas = new ArrayList<InscricaoVestibular>();
			if (ValidatorUtil.isEmpty(inscricoes)) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			} else {
				// valida se as inscrições são para o mesmo processo seletivo
				ProcessoSeletivoVestibular ps = obj.getProcessoSeletivo();
				Iterator<InscricaoVestibular> iterator = inscricoes.iterator();
				List<Long> listaCPF = new ArrayList<Long>();
				while (iterator.hasNext()) {
					InscricaoVestibular inscricao = iterator.next();
					if (inscricao.getProcessoSeletivo().getId() != ps.getId()) {
						addMensagemErro("A inscrição "+ inscricao.getNumeroInscricao() + " pertence ao " + inscricao.getProcessoSeletivo().getNome()+".");
						iterator.remove();
						break;
					}
					// verifica se há mais de um CPF para a mesma inscrição informada.
					if (listaCPF.contains(inscricao.getPessoa().getCpf_cnpj())) {
						addMensagemErro("A inscrição "+inscricao.getNumeroInscricao()+" possui outra inscrição a validar com o mesmo CPF: "+inscricao.getPessoa().getCpfCnpjFormatado()+".");
						iterator.remove();
						break;
					} else {
						listaCPF.add(inscricao.getPessoa().getCpf_cnpj());
					}
					// Verifica as inscrições validadas anteriormente
					for (InscricaoVestibular validada : inscricoesValidadas) {
						if (validada.getId() == inscricao.getId()) {
							addMensagemErro("A inscrição "+inscricao.getNumeroInscricao()+" já foi validada.");
							iterator.remove();
							break;
						}
						if ( validada.getPessoa().getCpf_cnpj().equals(inscricao.getPessoa().getCpf_cnpj())) {
							addMensagemErro("A inscrição "+inscricao.getNumeroInscricao()+" possui outra inscrição validada com o mesmo CPF: "+validada.getPessoa().getCpfCnpjFormatado()+".");
							iterator.remove();
							break;
						}
					}
					// Verifica se existe a inscrição
					int index = numInscricoes.indexOf(inscricao.getNumeroInscricao());
					if (index >= 0)
						numInscricoes.remove(index);
					// inclui a observação para cada inscrição
					inscricao.setObservacao(obj.getObservacao());
				}
			}
			// notifica caso haja inscrição inválida na lista informada
			if (!ValidatorUtil.isEmpty(inscricoesInvalidas))
				addMensagemErro("As seguintes inscrições não são válidas: " + CollectionUtils.toList(inscricoesInvalidas)+".");
			if (!ValidatorUtil.isEmpty(numInscricoes))
				addMensagemErro("As seguintes inscrições não foram encontradas: " + CollectionUtils.toList(numInscricoes)+".");
			obj.setProcessoSeletivo(dao.refresh(obj.getProcessoSeletivo()));
			
		} finally {
			dao.close();
		}
		
		if (ValidatorUtil.isEmpty(inscricoes)) {
			addMensagemErro("Não há inscrições válidas para confirmar a validação.");
			return null;
		}
		return forward("/vestibular/InscricaoVestibular/confirma.jsp");
	}
	
	/** Busca por candidatos inscritos em um vestibular.<br> Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/vestibular/InscricaoVestibular/lista.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#buscar()
	 */
	@Override
	public String buscar() throws Exception {
		ValidatorUtil.validateRequiredId(obj.getProcessoSeletivo().getId(), "Processo Seletivo", erros);
		String nome = null;
		Long cpf = null;
		int inscricao = 0;
		if (filtroCPF) {
			ValidatorUtil.validateCPF_CNPJ(obj.getPessoa().getCpf_cnpj(), "CPF", erros);
			cpf = obj.getPessoa().getCpf_cnpj();
		}
		if (filtroInscricao) {
			ValidatorUtil.validateRequiredId(obj.getNumeroInscricao(), "Número de Inscrição", erros);
			inscricao = obj.getNumeroInscricao();
		}
		if (filtroNome) {
			ValidatorUtil.validateRequired(obj.getPessoa().getNome(), "Nome", erros);
			nome = obj.getPessoa().getNome();
		}
		if (!filtroCPF && !filtroInscricao && !filtroNome)
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
		if (hasErrors()) return null;
		InscricaoVestibularDao dao = getDAO(InscricaoVestibularDao.class);
		try {
			resultadosBusca = dao.findByNomeCpfInscricao(obj.getProcessoSeletivo().getId(),nome, cpf, inscricao, false);
		} catch (LimiteResultadosException e) {
			return tratamentoErroPadrao(e, e.getMessage());
		}
		if (resultadosBusca.isEmpty())
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		return getListPage();
	}

	/**
	 * Abre a busca por candidatos do vestibular.<br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/menus/cadastros.jsp</li>
	 * <li>/sigaa.war/vestibular/InscricaoVestibular/lista.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#listar()
	 */
	@Override
	public String listar() throws ArqException {
		obj = new InscricaoVestibular();
		obj.setPessoa(new PessoaVestibular());
		resultadosBusca = null;
		return super.listar();
	}
	
	/** Retorna o link para a página de listagem de inscrição.<br/>Método não invocado por JSP´s.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		 return "/vestibular/InscricaoVestibular/lista.jsp";
	}
	
	/** Valida os dados para a validação em lote das inscrições.
	 * 
	 * <ul>
	 * <li>Não invocado por jsp's.</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(br.ufrn.arq.negocio.validacao.ListaMensagens)
	 */
	@Override
	public boolean validacaoDados(ListaMensagens mensagens) {
		ValidatorUtil.validateRequired(obj.getProcessoSeletivo(), "Processo Seletivo", mensagens);
		ValidatorUtil.validateRequired(listaInscricao, "Lista de Inscrições", mensagens);
		if (!"\t".equals(separador)) 
			ValidatorUtil.validateRequired(separador, "Separador", mensagens);
		return mensagens.isErrorPresent();
	}
	
	/**
	 * Prepara para cadastrar uma lista de CPFs de candidatos que terão isenção
	 * da taxa de inscrição do Vestibular.<br> Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/menus/candidatos.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	public String iniciarValidacaoLote() throws ArqException, NegocioException {
		checkRole(SigaaPapeis.VESTIBULAR);
		init();
		prepareMovimento(SigaaListaComando.VALIDAR_INSCRICAO_VESTIBULAR_LOTE);
		return formularioCadastro();
	}

	/** Exibe o formulário de cadastro de CPFs de Isentos.<br/>Método não invocado por JSP´s.
	 * @return
	 */
	public String formularioCadastro() {
		return forward("/vestibular/InscricaoVestibular/valida_lote.jsp");
	}
	
	/** Retorna um texto descritivo do separador utilizado na lista de CPFs. Ex.: Vírgula, Tabulação, etc.<br> Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/vestibular/InscricaoVestibular/lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public String getDescricaoSeparador(){
		if (",".equals(separador)) return "Vírgula";
		else if (";".equals(separador)) return "Ponto-e-Vírgula";
		else if ("\t".equals(separador)) return "Tabulação";
		else if (" ".equals(separador)) return "Espaço";
		else return "outro";
	}

	/** Retorna uma coleção de separadores da lista de inscritos.<br> Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/vestibular/InscricaoVestibular/lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public Collection<SelectItem> getPossiveisSeparadores() {
		Collection<SelectItem> listaSeparadores = new ArrayList<SelectItem>();
		listaSeparadores.add(new SelectItem(String.valueOf(","), "Vírgula"));
		listaSeparadores.add(new SelectItem(String.valueOf(";"), "Ponto e Vírgula"));
		listaSeparadores.add(new SelectItem(String.valueOf("\t"), "Tabulação"));
		return listaSeparadores;
	}
	
	public String getListaInscricao() {
		return listaInscricao;
	}

	public void setListaInscricao(String listaInscricao) {
		this.listaInscricao = listaInscricao;
	}

	public String getSeparador() {
		return separador;
	}

	public void setSeparador(String separador) {
		this.separador = separador;
	}

	public Collection<InscricaoVestibular> getInscricoes() {
		return inscricoes;
	}

	public void setInscricoes(Collection<InscricaoVestibular> inscricoes) {
		this.inscricoes = inscricoes;
	}

	public boolean isFiltroNome() {
		return filtroNome;
	}

	public void setFiltroNome(boolean filtroNome) {
		this.filtroNome = filtroNome;
	}

	public boolean isFiltroCPF() {
		return filtroCPF;
	}

	public void setFiltroCPF(boolean filtroCPF) {
		this.filtroCPF = filtroCPF;
	}

	public boolean isFiltroInscricao() {
		return filtroInscricao;
	}

	public void setFiltroInscricao(boolean filtroInscricao) {
		this.filtroInscricao = filtroInscricao;
	}
}
