/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em 20/01/2010
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
import br.ufrn.sigaa.arq.dao.graduacao.OfertaVagasCursoDao;
import br.ufrn.sigaa.arq.dao.vestibular.AreaConhecimentoVestibularDao;
import br.ufrn.sigaa.arq.dao.vestibular.RestricaoInscricaoVestibularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.OfertaVagasCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.vestibular.dominio.AreaConhecimentoVestibular;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;
import br.ufrn.sigaa.vestibular.dominio.RestricaoInscricaoVestibular;
import br.ufrn.sigaa.vestibular.dominio.TipoRestricaoInscricaoVestibular;

/**
 * @author Édipo Elder F. Melo
 *
 */
@Component("restricaoInscricaoVestibular")
@Scope("request")
public class RestricaoInscricaoVestibularMBean extends SigaaAbstractController<RestricaoInscricaoVestibular> {
	
	/** CPFs de candidatos isentos, separados por vírgula. */
	private Long cpf;
	
	/** Lista de SelectItem de id de matrizes curriculares das ofertas de vagas para o Processo Seletivo selecionado. */
	private Collection<SelectItem> listaMatrizOfertaCombo;

	/** Lista de SelectItem de tipos de restrições. */
	private Collection<SelectItem> tiposRestricoes;
	
	/** Construtor padrão. */
	public RestricaoInscricaoVestibularMBean() {
		init();
	}

	/** Inicializa os atributos do controller. */
	private void init() {
		obj = new RestricaoInscricaoVestibular();
		obj.setProcessoSeletivoVestibular(new ProcessoSeletivoVestibular());
		obj.setMatrizCurricular(new MatrizCurricular());
		this.listaMatrizOfertaCombo = new ArrayList<SelectItem>();
	}
	
	/** Cadastrar uma lista de CPFs de candidatos que terão isenção
	 * da taxa de inscrição do Vestibular. <br> Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/vestibular/RestricaoInscricaoVestibular/form.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		return super.cadastrar();
	}
	
	/** remove o CPF de um candidato da lista de restrições.<br> Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/vestibular/IsencaoTaxaInscricao/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws SegurancaException 
	 */
	public String removeCPF() throws SegurancaException, ArqException, NegocioException {
		// recupera o CPF por parametro.
		Long novoCpf = UFRNUtils.parseCpfCnpj(getParameter("cpf"));
		if (novoCpf == null) {
			addMensagemErro("Não foi possível verificar o CPF: " + cpf);
		}  else {
			ValidatorUtil.validateCPF_CNPJ(novoCpf, "CPF", erros);
			// verifica se o CPF consta da lista
			if (obj.getCpfs() == null) obj.setCpfs(new TreeSet<Long>());
			if (!obj.getCpfs().contains(novoCpf))
				addMensagemErro(String.format("O CPF %s não consta da lista de CPFs", Formatador.getInstance().formatarCPF_CNPJ(novoCpf)));
		}
		if (hasErrors()) return null;
		obj.getCpfs().remove(novoCpf);
		return null;
	}
	
	/** Adiciona o CPF de um candidato à lista de restrições.<br> Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/vestibular/IsencaoTaxaInscricao/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws SegurancaException 
	 */
	public String adicionaCPF() throws SegurancaException, ArqException, NegocioException {
		// recupera o CPF por parametro.
		if (cpf == null) {
			addMensagemErro("Não foi possível verificar o CPF: " + cpf);
		}  else {
			ValidatorUtil.validateCPF_CNPJ(cpf, "CPF", erros);
		}
		// verifica se o CPF consta da lista
		if (obj.getCpfs() == null) obj.setCpfs(new TreeSet<Long>());
		if (obj.getCpfs().contains(cpf))
			addMensagemErro(String.format("O CPF %s já está incluído na lista de CPFs.", Formatador.getInstance().formatarCPF_CNPJ(cpf)));
		if (hasErrors()) return null;
		obj.getCpfs().add(cpf);
		this.cpf = new Long(0);
		return null;
	}
	
	/** Busca por cadastro de isenções utilizando uma lista de CPFs.<br> Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/vestibular/IsencaoTaxaInscricao/lista.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#buscar()
	 */
	@Override
	public String buscar() throws Exception {
		if (obj.getMatrizCurricular().getId() < 0) {
			addMensagemErro("Selecione uma matriz curricular válida ou \"TODOS\".");
		}
		if (hasErrors()) return null;
		RestricaoInscricaoVestibularDao dao = getDAO(RestricaoInscricaoVestibularDao.class);
		resultadosBusca = dao.findByProcessoSeletivoMatrizCurricular(obj.getProcessoSeletivoVestibular().getId(), obj.getMatrizCurricular().getId());
		if (resultadosBusca.isEmpty())
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		return null;
	}
	
	
	/** Listener responsável por ajustar o formulário à mudança de Processo Seletivo.<br> Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/vestibular/IsencaoTaxaInscricao/form.jsp</li>
	 * </ul>
	 * @param evt
	 * @return
	 * @throws DAOException 
	 */
	public String processoSeletivoListener(ValueChangeEvent evt) throws DAOException {
		int idProcessoSeletivo = (Integer) evt.getNewValue();
		obj.setProcessoSeletivoVestibular(new ProcessoSeletivoVestibular(idProcessoSeletivo));
		carregaOfertas();
		return null;
	}
	
	/** Carrega a lista de ofertas de cursos.
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private void carregaOfertas() throws HibernateException, DAOException {
		AreaConhecimentoVestibularDao areaDao = getDAO(AreaConhecimentoVestibularDao.class);
		OfertaVagasCursoDao ofertaDao = getDAO(OfertaVagasCursoDao.class);
		ProcessoSeletivoVestibular ps = ofertaDao.refresh(obj.getProcessoSeletivoVestibular());
		obj.setProcessoSeletivoVestibular(ps);
		Collection<AreaConhecimentoVestibular> areas = areaDao.findByFormaIngressoAnoPeriodo(
				ps.getFormaIngresso().getId(),
				ps.getAnoEntrada(), 
				ps.getPeriodoEntrada());
		int k = -1;
		listaMatrizOfertaCombo = new ArrayList<SelectItem>();
		Collection<Municipio> municipios = ofertaDao.findAllMunicipiosByEntradaFormaIngresso(ps.getAnoEntrada(), ps.getPeriodoEntrada(), ps.getFormaIngresso().getId(), null, null);
		for (Municipio municipio : municipios) {
			listaMatrizOfertaCombo.add(new SelectItem(k--, "Município: " + municipio.getNomeUF()));
			for (AreaConhecimentoVestibular area : areas) {
				Collection<OfertaVagasCurso> ofertas = ofertaDao.findAllByProcessoSeletivoAreaVestibularMunicipio(ps.getFormaIngresso().getId(), ps.getAnoEntrada(), ps.getPeriodoEntrada(), area.getId(),municipio.getId());
				if (ofertas != null && !ofertas.isEmpty()) {
					listaMatrizOfertaCombo.add(new SelectItem(k--, "-- Área: " + area.getDescricao() + " --"));
					for (OfertaVagasCurso oferta : ofertas) {
						listaMatrizOfertaCombo.add(new SelectItem(oferta.getMatrizCurricular().getId(), oferta.getMatrizCurricular().getDescricao() + " (" + oferta.getTotalVagas()+" vagas)"));
					}
				}
			}
			listaMatrizOfertaCombo.add(new SelectItem(k--, ""));
		}
	}
	
	/** Listener responsável por ajustar o formulário à mudança de Matriz Curricular.<br> Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/vestibular/IsencaoTaxaInscricao/form.jsp</li>
	 * </ul>
	 * @param evt
	 * @return
	 * @throws DAOException 
	 */
	public String matrizCurricularListener(ValueChangeEvent evt) throws DAOException {
		int idMatriz = (Integer) evt.getNewValue();
		obj.setMatrizCurricular(new MatrizCurricular(idMatriz));
		carregaListaCPF();
		return null;
	}
	
	/**  Listener responsável por ajustar o formulário à mudança do tipo de restrição.<br> Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/vestibular/IsencaoTaxaInscricao/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String tipoRestricaoListener(ValueChangeEvent evt) throws DAOException {
		int tipo = (Integer) evt.getNewValue();
		obj.setTipoRestricao(tipo);
		carregaListaCPF();
		return null;
	}
	public String carregaListaCPF() throws DAOException {
		RestricaoInscricaoVestibularDao dao = getDAO(RestricaoInscricaoVestibularDao.class);
		RestricaoInscricaoVestibular restricao = dao.findByProcessoSeletivoMatrizCurricularTipoRestricao(obj.getProcessoSeletivoVestibular().getId(),
				obj.getMatrizCurricular().getId(),
				obj.getTipoRestricao());
		if (restricao != null) obj = restricao;
		else obj.setCpfs(new TreeSet<Long>());
		return null;
	}
	
	/** Carrega a lista de oferta de curso.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#beforeRemover()
	 */
	@Override
	public void afterAtualizar() throws DAOException {
		carregaOfertas();
	}
	
	/** Reinicia os atributos do controller após remover o objeto.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#afterRemover()
	 */
	@Override
	public void afterRemover() {
		init();
	}
	
	/** Valida os dados para o cadastro de isentos: Processo Seletivo, lista de CPFs, separador, tipo de isenção e valor da taxa.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(br.ufrn.arq.negocio.validacao.ListaMensagens)
	 */
	@Override
	public boolean validacaoDados(ListaMensagens mensagens) {
		mensagens.addAll(obj.validate());
		return mensagens.isErrorPresent();
	}
	
	/**
	 * Prepara para cadastrar uma lista de CPFs de candidatos que terão isenção
	 * da taxa de inscrição do Vestibular.<br> Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/vestibular/menus/cadastros.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		checkRole(SigaaPapeis.VESTIBULAR);
		init();
		prepareMovimento(ArqListaComando.CADASTRAR);
		return forward(getFormPage());
	}
	
	public Collection<SelectItem> getTiposRestricoes(){
		if (tiposRestricoes == null) {
			tiposRestricoes = new ArrayList<SelectItem>();
			tiposRestricoes.add(new SelectItem(TipoRestricaoInscricaoVestibular.EXCLUSIVO_A, "Exclusivo à"));
			tiposRestricoes.add(new SelectItem(TipoRestricaoInscricaoVestibular.EXCETO_A, "Exceto à"));
		}
		return tiposRestricoes;
	}
	
	/** Exibe o formulário de cadastro de CPFs de Isentos.<br> Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/vestibular/IsencaoTaxaInscricao/confirma.jsp</li>
	 * </ul>
	 * @return
	 */
	public String formularioCadastro() {
		return forward(getFormPage());
	}
	
	/** Link para a página de formulário de cadastro da justificativa.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return "/vestibular/RestricaoInscricaoVestibular/form.jsp";
	}

	/** Link para a página de listagem de justificativa.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/vestibular/RestricaoInscricaoVestibular/lista.jsp";
	}

	/** Retorna a lista de CPFs de candidatos isentos, separados por vírgula.
	 * @return
	 */
	public Long getCpf() {
		return cpf;
	}

	/** Seta a lista de CPFs de candidatos isentos, separados por vírgula.
	 * @param listaCPF
	 */
	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	/** Retorna a lista de SelectItem de id de matrizes curriculares das ofertas de vagas para o Processo Seletivo selecionado. 
	 * @return
	 */
	public Collection<SelectItem> getListaMatrizOfertaCombo() {
		return listaMatrizOfertaCombo;
	}

}
