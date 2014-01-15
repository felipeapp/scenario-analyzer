/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 'DD/MM/aaaa'
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.vestibular.FiscalDao;
import br.ufrn.sigaa.arq.dao.vestibular.LocalAplicacaoProvaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.vestibular.dominio.Fiscal;
import br.ufrn.sigaa.vestibular.dominio.JustificativaAusencia;
import br.ufrn.sigaa.vestibular.dominio.LocalAplicacaoProva;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

/** Controller responsável por operações com fiscais. 
 * @author Édipo Elder F. Melo
 *
 */
@Component("fiscal")
@Scope("request")
public class FiscalMBean extends SigaaAbstractController<Fiscal> {
	
	/** Indica que a busca deve considerar o nome do fiscal. */
	private boolean buscaNomeFiscal;
	/** Indica que a busca deve considerar o Local de Aplicação de Prova do fiscal. */
	private boolean buscaLocalAplicacao;
	/** Indica que a busca deve considerar o Processo Seletivo do fiscal. */
	private boolean buscaProcessoSeletivo;
	/** Indica que a busca deve considerar a matrícula do discente. */
	private boolean buscaMatricula;
	/** Indica que a busca deve considerar o SIAPE do servidor. */
	private boolean buscaSiape;
	
	/** Objeto Fiscal utilizado como parâmetro de busca por fiscais. */
	private Fiscal fiscal;
	
	/** Justificativa dada pelo fiscal, quando a fez. */
	private JustificativaAusencia justificativa;
	
	/** Lista de locais de aplicação de provas do processo seletivo*/
	private List<SelectItem> locaisAplicacao;
	
	/** Construtor padrão.
	 * 
	 */
	public FiscalMBean() {
		init();
	}
	
	/** Inicializa os atributos do controller.
	 * 
	 */
	private void init() {
		fiscal = new Fiscal();
		fiscal.setPessoa(new Pessoa());
		fiscal.setDiscente(new Discente());
		fiscal.setServidor(new Servidor());
		fiscal.setProcessoSeletivoVestibular(new ProcessoSeletivoVestibular());
		fiscal.setLocalAplicacaoProva(new LocalAplicacaoProva());
		obj = new Fiscal();
	}
	
	/** Busca por fiscais de acordo com os parâmetros informados pelo usuário.
	 * 
	 * <br />
	 * Chamado por:
	 * <ul>
	 * 	<li>/vestibular/Fiscal/lista.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#buscar()
	 */
	@Override
	public String buscar() throws Exception {
		String nomeFiscal = null;
		Long matriculaFiscal = null;
		Integer siapeFiscal = null;
		int idLocalAplicacao = 0;
		int idProcessoSeletivo = 0;
		if (buscaNomeFiscal){
			if (ValidatorUtil.isEmpty(fiscal.getPessoa().getNome())) {
			addMensagemErro("Informe o nome (ou parte do nome) do fiscal.");
			} else {
				nomeFiscal = fiscal.getPessoa().getNome();
			}
		}
		if (buscaMatricula){
			if (ValidatorUtil.isEmpty(fiscal.getDiscente().getMatricula())) {
			addMensagemErro("Informe a matrícula do discente.");
			} else {
				matriculaFiscal = fiscal.getDiscente().getMatricula();
			}
		}
		if (buscaSiape){
			if (ValidatorUtil.isEmpty(fiscal.getServidor().getSiape())) {
			addMensagemErro("Informe o SIAPE do servidor.");
			} else {
				siapeFiscal = fiscal.getServidor().getSiape();
			}
		}
		if (buscaLocalAplicacao) {
			if (ValidatorUtil.isEmpty(fiscal.getLocalAplicacaoProva())) {
				addMensagemErro("Selecione um Local de Aplicação de Prova válido.");
			} else {
				idLocalAplicacao = fiscal.getLocalAplicacaoProva().getId();
			}
		}
		if (buscaProcessoSeletivo) {
			if (ValidatorUtil.isEmpty(fiscal.getProcessoSeletivoVestibular())) {
				addMensagemErro("Selecione um Processo Seletivo Válido");
			} else {
				idProcessoSeletivo = fiscal.getProcessoSeletivoVestibular().getId();
			}
		}
		if (! (buscaNomeFiscal || buscaLocalAplicacao || buscaProcessoSeletivo || buscaMatricula|| buscaSiape) )
			addMensagemErro("Informe um parâmetro para filtrar a busca");
		if (hasOnlyErrors()) {
			resultadosBusca = new ArrayList<Fiscal>();
			return null;
		}
		FiscalDao dao = getDAO(FiscalDao.class);
		try {
			resultadosBusca = dao.findByNomeMatricula(idProcessoSeletivo, idLocalAplicacao, nomeFiscal, matriculaFiscal, siapeFiscal);
		} catch (LimiteResultadosException e) {
			addMensagem(MensagensArquitetura.BUSCA_MAXIMO_RESULTADOS, 256);
			return null;
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
			return null;
		}
		if (ValidatorUtil.isEmpty(resultadosBusca))
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		return forward(getListPage()); 
	}

	/**
	 * Detalha as informações do fiscal selecionado. <br />
	 * Chamado por:
	 * <ul>
	 * <li>/vestibular/Fiscal/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String view() throws DAOException {
		populateObj(true);
		FiscalDao dao = getDAO(FiscalDao.class);
		this.justificativa = dao.findJustificativaByFiscal(obj);
		return forward("/vestibular/Fiscal/view.jsp");
	}
	
	/**
	 * Listener responsável por atualizar a lista de locais de aplicação de
	 * prova quando o usuário altera o ID do processo seletivo.
     * 
     * <br />
	 * Chamado por:
     * <ul>
	 * 	<li>/vestibular/Fiscal/lista.jsp</li>
	 * </ul>
     * 
	 * @param evt
	 * @return
	 * @throws DAOException
	 */
	public String carregaLocalAplicacao(ValueChangeEvent evt)
			throws DAOException {
		fiscal.getProcessoSeletivoVestibular().setId((Integer) evt.getNewValue());
		return carregaLocalAplicacao();
	}
	
	/** Carrega a lista de locais de aplicação de prova associados ao processo seletivo.
	 * @return
	 * @throws DAOException
	 */
	private String carregaLocalAplicacao() throws DAOException {
		LocalAplicacaoProvaDao dao = getDAO(LocalAplicacaoProvaDao.class);
		locaisAplicacao = toSelectItems(dao.findByProcessoSeletivo(fiscal.getProcessoSeletivoVestibular().getId()), "id", "nome");
		fiscal.getLocalAplicacaoProva().setId(0);
		return null;
	}
	
	/** Retorna o link para a página de lista de fiscais.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/vestibular/Fiscal/lista.jsp";
	}

	/** Retorna uma coleção de SelectItem de Locais de Aplicação de acordo com o Processo Seletivo.
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getLocaisAplicacao() throws DAOException {
		if (locaisAplicacao == null)
			carregaLocalAplicacao();
		return locaisAplicacao;
	}

	/** Seta a coleção de SelectItem de Locais de Aplicação.
	 * @param locaisAplicacao
	 */
	public void setLocaisAplicacao(List<SelectItem> locaisAplicacao) {
		this.locaisAplicacao = locaisAplicacao;
	}

	/** Indica que a busca deve considerar o nome do fiscal. 
	 * @return
	 */
	public boolean isBuscaNomeFiscal() {
		return buscaNomeFiscal;
	}

	/** Seta que a busca deve considerar o nome do fiscal. 
	 * @param buscaNomeFiscal
	 */
	public void setBuscaNomeFiscal(boolean buscaNomeFiscal) {
		this.buscaNomeFiscal = buscaNomeFiscal;
	}

	/** Indica que a busca deve considerar o Local de Aplicação de Prova do fiscal. 
	 * @return
	 */
	public boolean isBuscaLocalAplicacao() {
		return buscaLocalAplicacao;
	}

	/** Seta que a busca deve considerar o Local de Aplicação de Prova do fiscal. 
	 * @param buscaLocalAplicacao
	 */
	public void setBuscaLocalAplicacao(boolean buscaLocalAplicacao) {
		this.buscaLocalAplicacao = buscaLocalAplicacao;
	}

	/** Indica que a busca deve considerar o Processo Seletivo do fiscal. 
	 * @return
	 */
	public boolean isBuscaProcessoSeletivo() {
		return buscaProcessoSeletivo;
	}

	/** Seta que a busca deve considerar o Processo Seletivo do fiscal. 
	 * @param buscaProcessoSeletivo
	 */
	public void setBuscaProcessoSeletivo(boolean buscaProcessoSeletivo) {
		this.buscaProcessoSeletivo = buscaProcessoSeletivo;
	}

	/** Indica que a busca deve considerar a matrícula/SIAPE do fiscal.
	 * @return
	 */
	public boolean isBuscaMatricula() {
		return buscaMatricula;
	}

	/** Seta se a busca deve considerar a matrícula/SIAPE do fiscal.
	 * @param buscaMatriculaFiscal
	 */
	public void setBuscaMatricula(boolean buscaMatricula) {
		this.buscaMatricula = buscaMatricula;
	}

	/** Indica que a busca deve considerar o SIAPE do servidor.
	 * @return
	 */
	public boolean isBuscaSiape() {
		return buscaSiape;
	}

	/** Seta que a busca deve considerar o SIAPE do servidor.
	 * @param buscaSiape
	 */
	public void setBuscaSiape(boolean buscaSiape) {
		this.buscaSiape = buscaSiape;
	}

	/** Retorna o objeto Fiscal utilizado como parâmetro de busca por fiscais.
	 * @return
	 */
	public Fiscal getFiscal() {
		return fiscal;
	}

	/** Seta o objeto Fiscal utilizado como parâmetro de busca por fiscais.
	 * @param fiscal
	 */
	public void setFiscal(Fiscal fiscal) {
		this.fiscal = fiscal;
	}

	/** Retorna a justificativa dada pelo fiscal, quando a fez.
	 * @return
	 */
	public JustificativaAusencia getJustificativa() {
		return justificativa;
	}

	/** Seta a justificativa dada pelo fiscal, quando a fez.
	 * @param justificativa
	 */
	public void setJustificativa(JustificativaAusencia justificativa) {
		this.justificativa = justificativa;
	}
}
