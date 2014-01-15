package br.ufrn.sigaa.vestibular.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.vestibular.InscricaoFiscalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.vestibular.dominio.InscricaoFiscal;
import br.ufrn.sigaa.vestibular.dominio.StatusFoto;

/**
 * Controller responsável pela validação da foto 3x4 enviada pelos Fiscais. <br/>
 * A COMPERVE (Comissão Permanente do Vestibular) verifica se o arquivo enviado
 * pelo fiscal se encontra no padrão de fotos para documentos, isto é, de
 * busto, sem elementos que impeçam a identificação (óculos, chapéu, boné, etc),
 * plano de fundo neutro, etc.
 * 
 * @author guerethes
 */
@Component("validacaoFotoFiscalBean") @Scope("session")
public class ValidacaoFotoFiscalMBean extends SigaaAbstractController<InscricaoFiscal> { 

	/** Filtra a busca por inscrição restringindo a consulta por nome do candidato. */
	private boolean filtroNome;
	/** Filtra a busca por inscrição restringindo a consulta pelo CPF do candidato. */
	private boolean filtroCPF;
	/** Filtra a busca por inscrição restringindo a consulta pelo status da foto. */
	private boolean filtroStatus;

	/** Construtor padrão. */
	public ValidacaoFotoFiscalMBean() {
		init();
	}
	
	/** Inicializa os atributos do controller. */
	private void init() {
		obj = new InscricaoFiscal();
		obj.setPessoa(new Pessoa());
		this.filtroCPF = false;
		this.filtroNome = false;
		this.filtroStatus = false;
		resultadosBusca = null;
	}

	/**
	 * Método responsável por busca todas as fotos tendo como base os parametros informados.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/ValidacaoFotoFiscal/lista.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String buscar() throws Exception {
		checkRole(SigaaPapeis.VESTIBULAR, SigaaPapeis.BOLSISTA_VESTIBULAR);
		String nome = null;
		Long cpf = null;
		int idStatus = 0;
		if (filtroCPF) {
			ValidatorUtil.validateCPF_CNPJ(obj.getPessoa().getCpf_cnpj(), "CPF", erros);
			cpf = obj.getPessoa().getCpf_cnpj();
		}
		if (filtroNome) {
			ValidatorUtil.validateRequired(obj.getPessoa().getNome(), "Nome", erros);
			nome = obj.getPessoa().getNome();
		}
		if (filtroStatus) {
			ValidatorUtil.validateRequired(obj.getStatusFoto(), "Status", erros);
			idStatus = obj.getStatusFoto().getId();
		}
		
		if (!filtroCPF && !filtroStatus && !filtroNome)
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
		if (hasErrors()) return null;
		
		InscricaoFiscalDao dao = getDAO(InscricaoFiscalDao.class);
		resultadosBusca = dao.findByNomeCpf(nome, cpf, idStatus);
		getPaginacao().setTotalRegistros(resultadosBusca.size());
		getPaginacao().setPaginaAtual(0);
		if (resultadosBusca.isEmpty())
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		return forward(getListPage());
	}

	/**
	 * Retorna uma coleção de objetos de acordo com os parâmetros utilizados na
	 * busca.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/ValidacaoFoto/lista.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getResultadosBusca()
	 */
	@Override
	public Collection<InscricaoFiscal> getResultadosBusca() {
		Collection<InscricaoFiscal> pagina = new ArrayList<InscricaoFiscal>();
		if (!ValidatorUtil.isEmpty(resultadosBusca)) {
			Iterator<InscricaoFiscal> iterator = resultadosBusca.iterator();
			int k = 0;
			// avança
			while (k++ < getPaginacao().getPaginaAtual() * getTamanhoPagina() && iterator.hasNext()) 
				iterator.next();
			// monta a página
			k = 0;
			while (k++ < getTamanhoPagina() && iterator.hasNext()) {
				InscricaoFiscal fiscal = iterator.next();
				pagina.add(fiscal);
			}
		}
		return pagina;
	}
	
	/** Atualiza os status das fotos dos candidato do vestibular, de acordo com a avaliação do usuário.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/StatusFoto/lista.jsp</li>
	 * </ul>
	 *  
	 * @return
	 * @throws Exception 
	 */
	@Override
	public String atualizar() throws ArqException {
		try {
			checkRole(SigaaPapeis.VESTIBULAR, SigaaPapeis.BOLSISTA_VESTIBULAR);
			prepareMovimento(SigaaListaComando.ATUALIZAR_STATUS_FOTO);
			setOperacaoAtiva(SigaaListaComando.ATUALIZAR_STATUS_FOTO.getId());
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setRegistroEntrada(getRegistroEntrada());
			mov.setCodMovimento(SigaaListaComando.ATUALIZAR_STATUS_FOTO);
			mov.setColObjMovimentado(getResultadosBusca());
			execute(mov);
			atualizaResultadoBusca();
		} catch (NegocioException e) {
			tratamentoErroPadrao(e);
		}
		return null;
	}
	
	/** 
	 * Atualiza o resultado da busca, após a alteração de status dos fiscais, atualizando os status das fotos alterados. 
	 */
	private void atualizaResultadoBusca() throws DAOException {
		GenericDAO dao = getGenericDAO();
		try{
			for (InscricaoFiscal ins : getResultadosBusca()) {
				InscricaoFiscal atualizada = dao.refresh(ins);
				ins.setStatusFoto(atualizada.getStatusFoto());
				ins.setNovoStatusFoto(new StatusFoto());
			}
		} finally {
			dao.close();
		}
	}
	
	/** 
	 * Retorna o link para a página de listagem de fotos 3x4.
	 */
	@Override
	public String getListPage() {
		return "/vestibular/ValidacaoFotoFiscal/lista.jsp";
	}
	
	/** 
	 * Retorna uma coleção de selectItem de possíveis tamanhos de páginas de resultado da busca.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/vestibular/ValidacaoFotoFiscal/lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public Collection<SelectItem> getTamanhoPaginaCombo(){
		Collection<SelectItem> tamanhos = new ArrayList<SelectItem>();
		tamanhos.add(new SelectItem(Integer.valueOf(10), "10"));
		tamanhos.add(new SelectItem(Integer.valueOf(16), "16"));
		tamanhos.add(new SelectItem(Integer.valueOf(20), "20"));
		return tamanhos;
	}
	
	/**
	 * Inicialza os atributos e direciona para a tela da listagem.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/menus/fiscal.jsp</li>
	 * </ul>
	 */
	@Override
	public String listar() throws ArqException {
		init();
		return super.listar();
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

	public boolean isFiltroStatus() {
		return filtroStatus;
	}

	public void setFiltroStatus(boolean filtroStatus) {
		this.filtroStatus = filtroStatus;
	}
	
}