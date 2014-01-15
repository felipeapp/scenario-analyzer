/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 21/09/2010
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ArrayUtils;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.vestibular.InscricaoVestibularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.vestibular.dominio.InscricaoVestibular;
import br.ufrn.sigaa.vestibular.dominio.LocalAplicacaoProva;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

/** Controller respons�vel por importar os dados que associam o candidato ao local de aplica��o de prova.
 * 
 * @author �dipo Elder F. Melo
 *
 */
@Component("importaLocalProvaCandidatoBean")
@Scope("request")
public class ImportaLocalProvaCandidatoMBean extends SigaaAbstractController<InscricaoVestibular> {
	
	/** Dados a serem importados para defini��o do local de prova do candidato do vestibular. */
	private String dadosImportacao;

	/** Construtor padr�o. */
	public ImportaLocalProvaCandidatoMBean() {
		init();
	}

	/** Inicializa os atributos do controller. */
	private void init() {
		obj = new InscricaoVestibular();
		obj.setProcessoSeletivo(new ProcessoSeletivoVestibular());
	}
	
	/**
	 * Consolida a associa��o do candidato ao local de aplica��o de prova. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/LocalProvaCandidato/confirma.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setColObjMovimentado(resultadosBusca);
		mov.setCodMovimento(SigaaListaComando.IMPORTAR_LOCAIS_PROVA_CANDIDATO );
		execute(mov);
		addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Lista de Inscri��es");
		return cancelar();
	}
	
	/**
	 * Valida os dados informados.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/LocalProvaCandidato/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String validaDadosImportacao() throws DAOException {
		validacaoDados(erros);
		if (hasErrors()) return null;
		List<Integer> numInscricoes = new ArrayList<Integer>();
		resultadosBusca = new ArrayList<InscricaoVestibular>();
		// separa os dados informados
		String regex = "\\}[\\s,\\s]*\\{";
		String[] dividido = dadosImportacao.split(regex);
		if (isEmpty(dividido)) {
			addMensagem(MensagensArquitetura.FORMATO_INVALIDO, "Dados de Importa��o");
			return null;
		}
		List<InscricaoVestibular> inscricoes = new ArrayList<InscricaoVestibular>();
		List<LocalAplicacaoProva> cacheLP = new ArrayList<LocalAplicacaoProva>();
		for (String linha : dividido) {
			linha = StringUtils.deleteWhitespace(linha);
			Integer[] tupla;
			try {
				tupla = ArrayUtils.toIntArray(linha.replace("{", "").replace("}", "").split(","));
				if (isEmpty(tupla) || tupla.length != 3) {
					addMensagem(MensagensArquitetura.FORMATO_INVALIDO, "Dados de Importa��o");
					return null;
				}
			} catch (NumberFormatException e) {
				addMensagem(MensagensArquitetura.FORMATO_INVALIDO, "Dados de Importa��o");
				return null;
			}
			LocalAplicacaoProva localProva = new LocalAplicacaoProva(tupla[0]);
			InscricaoVestibular inscricao = new InscricaoVestibular();
			inscricao.setLocalProva(localProva);
			inscricao.setNumeroInscricao(tupla[1]);
			inscricao.setTurma(tupla[2]);
			inscricoes.add(inscricao);
			numInscricoes.add(inscricao.getNumeroInscricao());
		}
		// verifica as inscri��es informadas
		InscricaoVestibularDao dao = getDAO(InscricaoVestibularDao.class);
		// recupera as inscri��es a serem validadas
		resultadosBusca = dao.findByListaInscricao(numInscricoes, obj.getProcessoSeletivo());
		Iterator<Integer> iterator = numInscricoes.iterator();
		Set<Integer> inscricoesNaoValidadas = new TreeSet<Integer>();
		while (iterator.hasNext()) {
			Integer numInscricao = iterator.next();
			for (InscricaoVestibular inscricao : resultadosBusca) {
				if (!inscricao.isValidada())
					inscricoesNaoValidadas.add(inscricao.getNumeroInscricao());
				if (inscricao.getNumeroInscricao() == numInscricao) {
					iterator.remove();
					break;
				}
			}
		}
		// remove as inscri��es n�o validadas
		if (!isEmpty(inscricoesNaoValidadas)) {
			Iterator<InscricaoVestibular> iterator2 = resultadosBusca.iterator();
			while (iterator2.hasNext()) {
				InscricaoVestibular inscricao = iterator2.next();
				if (inscricoesNaoValidadas.contains(inscricao.getNumeroInscricao()))
					iterator2.remove();
			}
		}
		// seta os valores informados
		Iterator<InscricaoVestibular> inscIterator = resultadosBusca.iterator();
		while (inscIterator.hasNext()) {
			InscricaoVestibular inscricao = inscIterator.next();
			for (InscricaoVestibular valores : inscricoes) {
				if (inscricao.getNumeroInscricao() == valores.getNumeroInscricao()) {
					if (!cacheLP.contains(valores.getLocalProva())) {
						cacheLP.add(dao.refresh(valores.getLocalProva()));
					}
					int index = cacheLP.indexOf(valores.getLocalProva());
					if (index >= 0) {
						inscricao.setLocalProva(cacheLP.get(index));
						inscricao.setTurma(valores.getTurma());
					} else {
						inscIterator.remove();
						addMensagemErro("Local de Prova N�o Existe: " + valores.getLocalProva().getId());
					}
				}
			}
		}
		// notifica caso haja inscri��o inv�lida na lista informada
		if (!ValidatorUtil.isEmpty(numInscricoes))
			addMensagemErro("As seguintes inscri��es n�o foram encontradas: " + CollectionUtils.toList(numInscricoes)+".");
		if (!ValidatorUtil.isEmpty(inscricoesNaoValidadas))
			addMensagemErro("As seguintes inscri��es n�o foram validadas: " + CollectionUtils.toList(inscricoesNaoValidadas)+".");
		obj.setProcessoSeletivo(dao.refresh(obj.getProcessoSeletivo()));
		dao.close();
		if (ValidatorUtil.isEmpty(resultadosBusca)) {
			addMensagemErro("N�o h� inscri��es v�lidas para confirmar a valida��o.");
			return null;
		}
		// ordena o resultado por local de aplica��o, turma, nome
		Comparator<InscricaoVestibular> comparator = new Comparator<InscricaoVestibular>() {
			@Override
			public int compare(InscricaoVestibular insc1, InscricaoVestibular insc2) {
				int comp = insc1.getLocalProva().compareTo(insc2.getLocalProva());
				if (comp == 0)
					comp = insc1.getTurma().compareTo(insc2.getTurma());
				if (comp == 0)
					comp = insc1.getPessoa().getNomeAscii().compareTo(insc2.getPessoa().getNomeAscii());
				return comp;
			}
		};
		Collections.sort((List<InscricaoVestibular>) resultadosBusca, comparator);
		return forward("/vestibular/LocalProvaCandidato/confirma.jsp");
	}
	
	/** Valida os dados para a valida��o em lote das inscri��es.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(br.ufrn.arq.negocio.validacao.ListaMensagens)
	 */
	@Override
	public boolean validacaoDados(ListaMensagens mensagens) {
		ValidatorUtil.validateRequired(obj.getProcessoSeletivo(), "Processo Seletivo", mensagens);
		ValidatorUtil.validateRequired(dadosImportacao, "Lista de Inscri��es", mensagens);
		if (dadosImportacao != null && (dadosImportacao.indexOf('{') < 0 || dadosImportacao.indexOf('}') < 0))
			mensagens.addErro("Formato inv�lido para importa��o de dados.");
		return mensagens.isErrorPresent();
	}

	/**
	 * Prepara para associar candidatos aos seus respectivos locais de provas..<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/menus/candidatos.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	public String iniciarImportacaoDados() throws ArqException, NegocioException {
		checkRole(SigaaPapeis.VESTIBULAR);
		init();
		prepareMovimento(SigaaListaComando.IMPORTAR_LOCAIS_PROVA_CANDIDATO);
		return formularioCadastro();
	}

	/**
	 * Exibe o formul�rio de defini��o de local de prova dos candidatos.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/LocalProvaCandidato/confirma.jsp</li>
	 * </ul>
	 */
	public String formularioCadastro() {
		return forward("/vestibular/LocalProvaCandidato/form.jsp");
	}
	
	/** Retorna os dados a serem importados para defini��o do local de prova do candidato do vestibular. 
	 * @return
	 */
	public String getDadosImportacao() {
		return dadosImportacao;
	}

	/** Seta os dados a serem importados para defini��o do local de prova do candidato do vestibular.
	 * @param dadosImportacao
	 */
	public void setDadosImportacao(String dadosImportacao) {
		this.dadosImportacao = dadosImportacao;
	}
}
