/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/09/2006
 *
 */

package br.ufrn.sigaa.pesquisa.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.arq.dao.pesquisa.TipoBolsaPesquisaDao;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.assistencia.cadunico.dominio.AdesaoCadastroUnicoBolsa;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.graduacao.dominio.InteressadoBolsa;
import br.ufrn.sigaa.pesquisa.dominio.CodigoProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.StatusCotaPlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pesquisa.negocio.PlanoTrabalhoHelper;
import br.ufrn.sigaa.pessoa.dominio.ContaBancaria;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.pessoa.dominio.TipoConta;

/**
 * Formulário para casos de uso referentes a planos de trabalho
 *
 * @author Ricardo Wendell
 *
 */
public class PlanoTrabalhoForm extends SigaaForm<PlanoTrabalho> {

	/** Quantidade de meses que o plano de trabalho possui */
	private int numeroMesesCronograma;
	/** Classe responsável pelo gerenciamento da tela de cronograma */
	private TelaCronograma telaCronograma;
	/** Bolsista atual do plano de trabalho */
	private MembroProjetoDiscente bolsistaAtual;
	/** Bolsista anterior do plano de trabalho */
	private MembroProjetoDiscente bolsistaAnterior;
	/** Filtros utilizados para a consulta */
	private int[] filtros = {};
	/** Grupo de pesquisa que o plano de trabalho faz parte */
	private int grupoPesquisa;
	/** Unidade a qual o plano está vinculado */
	private Unidade unidade;
	/** Data da finalização do bolsista */
	private String dataFinalizacao;
	/** Data da indicação do bolsista */
	private String dataIndicacao;
	/** Tipo de bolsa do bolsita atual */
	private int tipoBolsa;
	/** Niveis permitidos para assumir como bolsista do plano */
	private String niveisPermitidos;
	/** utilizado somente no html:select da jsp de indicação pois o atributo property dessa tag e obrigatório */
	private String motivo;
	/** Utilizado para alterar o projeto ao qual um plano de trabalho está associado */
	private boolean alterarProjeto;
	/** Classe reponsável pela geração do código do projeto de pesquisa. */
	private CodigoProjetoPesquisa codigoProjeto;
	/** Utilizado para saber se o usuário está solicitando cotas ou registrando bolsas de fluxo contínuo */
	private boolean solicitacaoCota;
	/** Utilizado para saber se o usuário está castrando um plano de trabalho voluntário */
	private boolean cadastroVoluntario;
	/** Utilizado para saber se o usuário tem permissão de gestor de pesquisa e se encontra no módulo de pesquisa */
	private boolean permissaoGestor;
	/** Data inicial do plano de trabalho */
	private String dataInicio;
	/** Data final do plano de trabalho */
	private String dataFim;
	/** Representa o tipo de vinculo do usuári o que está cadastrando o plano de trabalho */
	private boolean externo;
	/** Discentes que registraram interesse no cadastro único para o plano de trabalho */
	private List<AdesaoCadastroUnicoBolsa> discentesAdesao = new ArrayList<AdesaoCadastroUnicoBolsa>();
	/** Utilizado quando o docente deseja saber os dados de qualificação de um aluno que registrou interesse */
	private InteressadoBolsa interessadoBolsa;
	/** Utilizado para identificar a avaliação de projeto para a qual voltar após a avaliação do plano de trabalho */
	private Integer fromAvaliacaoProjeto;
	/** Pontuação que indica se o bolista é prioritário ou não */
	private int pontuacao = AdesaoCadastroUnicoBolsa.ALUNO_PRIORITARIO;
	private String bolsaDesejada;
	
	public PlanoTrabalhoForm() {
		try {
			this.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void clear() throws Exception {
		obj = new PlanoTrabalho();
		obj.setMembroProjetoDiscente(new MembroProjetoDiscente());
		obj.getMembroProjetoDiscente().getDiscente().getPessoa().setContaBancaria( new ContaBancaria() );
		obj.setOrientador(new Servidor());
		obj.setCota(new CotaBolsas());
		obj.setEdital(new EditalPesquisa());
		obj.setTipoBolsa(new TipoBolsaPesquisa());
		obj.setExterno(new DocenteExterno());

		unidade = new Unidade();
		unidade.setGestora(new Unidade());

		codigoProjeto = new CodigoProjetoPesquisa();
		
		dataFim = "";
		dataInicio = "";
		
		niveisPermitidos = "G";
	}

	public int getNumeroMesesCronograma() {
		return numeroMesesCronograma;
	}

	public void setNumeroMesesCronograma(int numeroMesesCronograma) {
		this.numeroMesesCronograma = numeroMesesCronograma;
	}

	public TelaCronograma getTelaCronograma() {
		return telaCronograma;
	}

	public void setTelaCronograma(TelaCronograma telaCronograma) {
		this.telaCronograma = telaCronograma;
	}

	public MembroProjetoDiscente getBolsistaAtual() {
		return bolsistaAtual;
	}

	public void setBolsistaAtual(MembroProjetoDiscente bolsistaAtual) {
		this.bolsistaAtual = bolsistaAtual;
	}

	/** Retorna os filtros utilizados na consulta */
	public int[] getFiltros() {
		return filtros;
	}

	/** Seta os filtros a serem utilizados na consulta */
	public void setFiltros(int[] filtros) {
		this.filtros = filtros;
	}

	/** Retorna o grupo de pesquisa vinculado ao plano de trabalho */
	public int getGrupoPesquisa() {
		return grupoPesquisa;
	}

	/** Seta o grupo de pesquisa vinculado ao plano de trabalho */
	public void setGrupoPesquisa(int grupoPesquisa) {
		this.grupoPesquisa = grupoPesquisa;
	}

	/** Retorna a unidade de pesquisa vinculado ao plano de trabalho */
	public Unidade getUnidade() {
		return unidade;
	}

	/** Seta a unidade de pesquisa vinculado ao plano de trabalho */
	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public String getDataFinalizacao() {
		return dataFinalizacao;
	}

	public void setDataFinalizacao(String dataFinalizacao) {
		this.dataFinalizacao = dataFinalizacao;
	}

	public String getDataIndicacao() {
		return dataIndicacao;
	}

	public void setDataIndicacao(String dataIndicacao) {
		this.dataIndicacao = dataIndicacao;
	}

	public int getTipoBolsa() {
		return tipoBolsa;
	}

	public void setTipoBolsa(int tipoBolsa) {
		this.tipoBolsa = tipoBolsa;
	}

	public String getNiveisPermitidos() {
		return niveisPermitidos;
	}

	public void setNiveisPermitidos(String niveisPermitidos) {
		this.niveisPermitidos = niveisPermitidos;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public MembroProjetoDiscente getBolsistaAnterior() {
		return bolsistaAnterior;
	}

	public void setBolsistaAnterior(MembroProjetoDiscente bolsistaAnterior) {
		this.bolsistaAnterior = bolsistaAnterior;
	}

	public boolean isAlterarProjeto() {
		return alterarProjeto;
	}

	public void setAlterarProjeto(boolean alterarProjeto) {
		this.alterarProjeto = alterarProjeto;
	}

	public CodigoProjetoPesquisa getCodigoProjeto() {
		return codigoProjeto;
	}

	public void setCodigoProjeto(CodigoProjetoPesquisa codigoProjeto) {
		this.codigoProjeto = codigoProjeto;
	}

	public boolean isSolicitacaoCota() {
		return solicitacaoCota;
	}

	public void setSolicitacaoCota(boolean solicitacaoCota) {
		this.solicitacaoCota = solicitacaoCota;
	}

	public boolean isPermissaoGestor() {
		return permissaoGestor;
	}

	public void setPermissaoGestor(boolean permissaoGestor) {
		this.permissaoGestor = permissaoGestor;
	}

	/** Retorna a data de inicio do plano de trabalho formatada */
	public String getDataInicio() {
		PlanoTrabalho plano = obj;
		if(plano.getDataInicio() != null)
			dataInicio = Formatador.getInstance().formatarData( plano.getDataInicio() );
		return dataInicio;
	}

	/** Seta a data de inicio do plano de trabalho */
	public void setDataInicio(String dataInicio) {
		PlanoTrabalho plano = obj;
		plano.setDataInicio( parseDate(dataInicio) );
		this.dataInicio = dataInicio;
	}

	/** Retorna a data Final do plano de trabalho */
	public String getDataFim() {
		PlanoTrabalho plano = obj;
		if(plano.getDataFim() != null)
			dataFim = Formatador.getInstance().formatarData( plano.getDataFim() );
		return dataFim;
	}
	
	/** Seta a data Final do plano de trabalho */
	public void setDataFim(String dataFim) {
		PlanoTrabalho plano = obj;
		plano.setDataFim( parseDate(dataFim) );				
		this.dataFim = dataFim;
	}

	public InteressadoBolsa getInteressadoBolsa() {
		return interessadoBolsa;
	}

	public void setInteressadoBolsa(InteressadoBolsa interessadoBolsa) {
		this.interessadoBolsa = interessadoBolsa;
	}

	public boolean isCadastroVoluntario() {
		return cadastroVoluntario;
	}

	public void setCadastroVoluntario(boolean cadastroVoluntario) {
		this.cadastroVoluntario = cadastroVoluntario;
	}

	public Integer getFromAvaliacaoProjeto() {
		return fromAvaliacaoProjeto;
	}

	public void setFromAvaliacaoProjeto(Integer fromAvaliacaoProjeto) {
		this.fromAvaliacaoProjeto = fromAvaliacaoProjeto;
	}

	public int getPontuacao() {
		return pontuacao;
	}

	public void setPontuacao(int pontuacao) {
		this.pontuacao = pontuacao;
	}

	public boolean isExterno() {
		return externo;
	}

	public void setExterno(boolean externo) {
		this.externo = externo;
	}

	public List<AdesaoCadastroUnicoBolsa> getDiscentesAdesao() {
		return discentesAdesao;
	}

	public void setDiscentesAdesao(List<AdesaoCadastroUnicoBolsa> discentesAdesao) {
		this.discentesAdesao = discentesAdesao;
	}

	/** Retorna o Dao, para que não seja necessário instanciar um novo Dao. */
	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return DAOFactory.getInstance().getDAO(dao, null, null);
	}

	/** Serve para exibir as Status do plano de trabalho */
	public String getOutGroupStatusPlano() throws DAOException {
		TipoBolsaPesquisaDao dao = getDAO(TipoBolsaPesquisaDao.class);
		Collection<StatusCotaPlanoTrabalho> status;
		try {
			status = dao.findAll(StatusCotaPlanoTrabalho.class, "ordem", "asc");
		} finally {
			dao.close();
		}
		return PlanoTrabalhoHelper.getOutGroupStrutsStatus( status, getObj().getStatus() );
	}
	
	/** Serve para exibir o tipo de bolsa pra o fluxo contínuo. */
	public String getOutGroupStrutsFluxoContinuo() throws DAOException{
		TipoBolsaPesquisaDao dao = getDAO(TipoBolsaPesquisaDao.class);
		Collection<TipoBolsaPesquisa> bolsas;
		try {
			bolsas = dao.findBolsasFluxoContinuo();
		} finally {
			dao.close();
		}
		return PlanoTrabalhoHelper.getOutGroupStrutsTipoBolsa( true, bolsas, verificaTipoBolsa() );
	}
	
	/** Serve para exibir os tipos de bolsa para o gestor de pesquisa */
	public String getOutGroupStrutsGestorBolsa(int tipoBolsa) throws DAOException{
		this.tipoBolsa = tipoBolsa; 
		return PlanoTrabalhoHelper.getOutGroupStrutsTipoBolsa( false, carregarBolsasPesquisa(), tipoBolsa );
	}
	
	/** Serve para exibir os tipos de bolsa para o gestor de pesquisa */
	public String getOutGroupStrutsGestor() throws DAOException{
		return PlanoTrabalhoHelper.getOutGroupStrutsTipoBolsa( false, carregarBolsasPesquisa(), verificaTipoBolsa() );
	}

	/** Carrega as bolsas cadastradas é que possuem entidade financiadora associadas. */
	private Collection<TipoBolsaPesquisa> carregarBolsasPesquisa()
			throws DAOException {
		TipoBolsaPesquisaDao dao = getDAO(TipoBolsaPesquisaDao.class);
		Collection<TipoBolsaPesquisa> bolsas;
		try {
			bolsas = dao.carregarBolsasPesquisa();
		} finally {
			dao.close();
		}
		return bolsas;
	}

	/** Serve para exibir os tipos de bolsa voluntário de Pesquisa */
	public String getOutGroupStrutsVoluntario() throws DAOException{
		TipoBolsaPesquisaDao dao = DAOFactory.getInstance().getDAO(TipoBolsaPesquisaDao.class, null, null);
		Collection<TipoBolsaPesquisa> bolsas;
		try {
			bolsas = dao.carregarBolsasPesquisa(TipoBolsaPesquisa.VOLUNTARIO, TipoBolsaPesquisa.VOLUNTARIO_IT);
		} finally {
			dao.close();
		}

		return PlanoTrabalhoHelper.getOutGroupStrutsTipoBolsa( true, bolsas, verificaTipoBolsa() );
	}

	public Collection<TipoConta> getAllTiposConta() throws DAOException {
		Collection<TipoConta> tipoConta = new ArrayList<TipoConta>(); 
		for ( Entry<Integer, String> linha : TipoConta.getTabela().entrySet() )
			tipoConta.add( new TipoConta(linha.getKey(), linha.getValue()) );
		return tipoConta;
	}
	
	/**
	 * Verifica se for informando algum tipo de bolsa, por padrão é selecionado o voluntário.
	 */
	public int verificaTipoBolsa(){
		if (tipoBolsa == 0) {
			tipoBolsa = TipoBolsaPesquisa.VOLUNTARIO;
		}
		return tipoBolsa;
	}

	public String getBolsaDesejada() {
		return bolsaDesejada;
	}

	public void setBolsaDesejada(String bolsaDesejada) {
		this.bolsaDesejada = bolsaDesejada;
	}

}