/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/09/2006
 *
 */

package br.ufrn.sigaa.pesquisa.form;


import org.apache.struts.upload.FormFile;

import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.CategoriaProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.CodigoProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.FinanciamentoProjetoPesq;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.LinhaPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.ClassificacaoFinanciadora;
import br.ufrn.sigaa.projetos.dominio.CronogramaProjeto;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Form para manipulações com Projetos de Pesquisa
 * 
 * @author Victor Hugo
 * @author Leonardo Campos
 */
public class ProjetoPesquisaForm extends SigaaForm<ProjetoPesquisa> {

	/** Data de início do projeto de Pesquisa */
	private String dataInicio;
	/** Data final do projeto de Pesquisa */
	private String dataFim;
	/** Membros do Projeto de Pesquisa */
	private MembroProjeto membroProjeto;
	/** Servidor Técnico do Projeto de Pesquisa */
	private Servidor servidorTecnico;
	/** Armazena a grande área do projeto de Pesquisa */
	private AreaConhecimentoCnpq grandeArea;
	/** Armazena a área do projeto de Pesquisa */
	private AreaConhecimentoCnpq area;
	/** Armazena a subárea do projeto de Pesquisa */
	private AreaConhecimentoCnpq subarea;
	/** Armazena a especialidade do projeto de Pesquisa */
	private AreaConhecimentoCnpq especialidade;
	/** Armazena o Cronograma do Projeto de Pesquis */
	private CronogramaProjeto cronogramaPesquisa;
	/** Armazena o título do projeto de Pesquisa */
	private String titulo;
	/** Armazena o objetivo do projeto de Pesquisa */
	private String objetivos;
	/** Armazena o Financiamento dos projetos da Propesq */
	private FinanciamentoProjetoPesq financiamentoProjetoPesq;
	/** Armazena a data de início dos Finaciamentos */
	private String dtInicioFinanciamento;
	/** Armazena a data de fim dos Finaciamentos */
	private String dtFimFinanciamento;

	/** Utilizado durante a renovação de projetos */
	private boolean renovacao;
	
	/** Utilizado durante a renovação de projetos */
	private boolean descricaoIncompleta;

	/** Utilizado quando o coordenador é um professor colaborador voluntário */
	private boolean coordenadorColaborador;
	
	/** Utilizado para testar se o usuário concorda com a cláusula imposta para
	 * professores colaboradores voluntários que desejam coordenar projetos */
	private boolean checkClausula;
	
	/** variável para remover itens de uma lista pela posição */
	private int posLista;

	/** variável para remover uma bolsa externa de um financiamento */
	private int posBolsa;

	/** Controle se o projeto é isolado */
	private boolean isolado;

	/** Controle se o projeto é financiado */
	private boolean financiado;
	
	/** Armazena o cpf do Docente Externo */
	private String cpfExterno;

	/** Monta a tela do cronograma do projeto de Pesquisa */
	private TelaCronograma telaCronograma;

	/** Centro reponsável pelo projeto de pesquisa */
	private Unidade centro = new Unidade();
	/** Unidade responsável pelo projeto de pesquisa */
	private Unidade unidade = new Unidade();
	/** Código do projeto de pesquisa */
	private String codigo;
	/** Filtros utilizados na busca dos projetos de pesquisa */
	private int[] filtros = {};
	/** Armazena a finalidade da busca do projeto de pesquisa (Consulta, Gerencia)*/
	private int finalidadeBusca;
	
	/** utilizado na consulta de projetos */
	private boolean relatorioSubmetido;

	/** Arquivo do projeto */
	private FormFile arquivoProjeto;

	/** Indica que trata-se de renovação */
	private boolean segundaChamada = false;
	
	/** utilizado nos cadastros de projeto anexo a um projeto-base */ 
	private boolean anexoProjetoBase;

	/** Responsável por identificar a finalidade */
	private boolean consulta;
	
	/** Esse campo tem função de exibir ou não o prazo de execução do edital de pesquisa */
	private boolean exibirAnos;
	
	/** Esse campo tem função de controlar a troca de edital do projeto de pesquisa */
	private boolean alteracaoEdital;
	
	public ProjetoPesquisaForm() {
		obj = new ProjetoPesquisa();
		obj.setCodigo(new CodigoProjetoPesquisa());

		obj.setCentro(new Unidade());
		obj.setEdital(new EditalPesquisa());

		obj.setLinhaPesquisa( new LinhaPesquisa() );
		obj.getLinhaPesquisa().setGrupoPesquisa( new GrupoPesquisa() );

		obj.setSituacaoProjeto(new TipoSituacaoProjeto());
		obj.setCategoria( new CategoriaProjetoPesquisa() );
		obj.getProjeto().setRenovacao(false);
		membroProjeto = new MembroProjeto();
		membroProjeto.setProjeto( obj.getProjeto() );
		membroProjeto.setCategoriaMembro( new CategoriaMembro() );
		membroProjeto.setFuncaoMembro( new FuncaoMembro(FuncaoMembro.COLABORADOR) );
		servidorTecnico = new Servidor();

		financiamentoProjetoPesq = new FinanciamentoProjetoPesq();
		financiamentoProjetoPesq.setProjetoPesquisa( obj );

		financiamentoProjetoPesq.getEntidadeFinanciadora()
			.setClassificacaoFinanciadora(new ClassificacaoFinanciadora());

		grandeArea = new AreaConhecimentoCnpq();
		area = new AreaConhecimentoCnpq();
		subarea = new AreaConhecimentoCnpq();
		especialidade = new AreaConhecimentoCnpq();

		cronogramaPesquisa = new CronogramaProjeto();

		financiado = false;
		
		exibirAnos = false;
	}

	public String getCpfExterno() {
		return cpfExterno;
	}

	public void setCpfExterno(String cpfExterno) {
		this.cpfExterno = cpfExterno;
	}

	public int getFinalidadeBusca() {
		return finalidadeBusca;
	}

	public void setFinalidadeBusca(int finalidadeBusca) {
		this.finalidadeBusca = finalidadeBusca;
	}

	public int[] getFiltros() {
		return filtros;
	}

	public void setFiltros(int[] filtros) {
		this.filtros = filtros;
	}

	/** Formata a data de início do projeto */
	public String getDataInicio() {
		ProjetoPesquisa projeto = obj;
		if( projeto.getDataInicio() != null ){
			dataInicio = Formatador.getInstance().formatarData( projeto.getDataInicio() );
		}
		return dataInicio;
	}

	/** Seta a data de início */
	public void setDataInicio(String dataInicio) {
		ProjetoPesquisa projeto = obj;
		projeto.setDataInicio( parseDate( dataInicio ) );
		this.dataInicio = dataInicio;
	}

	/** Retorna a data de Início formatada */
	public String getDataFim() {
		ProjetoPesquisa projeto = obj;
		if( projeto.getDataFim() != null ){
			dataFim = Formatador.getInstance().formatarData( projeto.getDataFim() );
		}
		return dataFim;
	}

	/** Formata a data fim do financiamento */
	public String getDtFimFinanciamento() {
		if( financiamentoProjetoPesq.getDataFim() != null )
			dtFimFinanciamento = Formatador.getInstance().formatarData( financiamentoProjetoPesq.getDataFim() );
		return dtFimFinanciamento;
	}

	/** Seta a data fim do Financiamento*/
	public void setDtFimFinanciamento(String dtFimFinanciamento) {
		financiamentoProjetoPesq.setDataFim( parseDate( dtFimFinanciamento )  );
		this.dtFimFinanciamento = dtFimFinanciamento;
	}

	/** Retorna a data de Início do Financiamento */
	public String getDtInicioFinanciamento() {
		if( financiamentoProjetoPesq.getDataInicio() != null )
			dtInicioFinanciamento = Formatador.getInstance().formatarData( financiamentoProjetoPesq.getDataInicio() );
		return dtInicioFinanciamento;
	}

	/** Seta a Data Fim do Financiamento */
	public void setDtInicioFinanciamento(String dtInicioFinanciamento) {
		financiamentoProjetoPesq.setDataInicio( parseDate( dtInicioFinanciamento )  );
		this.dtInicioFinanciamento = dtInicioFinanciamento;
	}

	/** Seta da data Final do projeto */
	public void setDataFim(String dataFim) {
		ProjetoPesquisa projeto = obj;
		projeto.setDataFim( parseDate( dataFim ) );
		this.dataFim = dataFim;
	}

	public MembroProjeto getMembroProjeto() {
		return membroProjeto;
	}

	public void setMembroProjeto(MembroProjeto membroProjeto) {
		this.membroProjeto = membroProjeto;
	}

	public FinanciamentoProjetoPesq getFinanciamentoProjetoPesq() {
		return financiamentoProjetoPesq;
	}

	public void setFinanciamentoProjetoPesq(
			FinanciamentoProjetoPesq financiamentoProjetoPesq) {
		this.financiamentoProjetoPesq = financiamentoProjetoPesq;
	}

	public CronogramaProjeto getCronogramaPesquisa() {
		return cronogramaPesquisa;
	}

	public void setCronogramaPesquisa(CronogramaProjeto cronogramaPesquisa) {
		this.cronogramaPesquisa = cronogramaPesquisa;
	}

	public int getPosLista() {
		return posLista;
	}

	public void setPosLista(int posLista) {
		this.posLista = posLista;
	}

	public int getPosBolsa() {
		return posBolsa;
	}

	public void setPosBolsa(int posBolsa) {
		this.posBolsa = posBolsa;
	}

	public boolean isIsolado() {
		return isolado;
	}

	public void setIsolado(boolean isolado) {
		this.isolado = isolado;
	}

	public AreaConhecimentoCnpq getArea() {
		return area;
	}

	public void setArea(AreaConhecimentoCnpq area) {
		this.area = area;
	}

	public AreaConhecimentoCnpq getGrandeArea() {
		return grandeArea;
	}

	public void setGrandeArea(AreaConhecimentoCnpq grandeArea) {
		this.grandeArea = grandeArea;
	}

	public AreaConhecimentoCnpq getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AreaConhecimentoCnpq especialidade) {
		this.especialidade = especialidade;
	}

	public AreaConhecimentoCnpq getSubarea() {
		return subarea;
	}

	public void setSubarea(AreaConhecimentoCnpq subarea) {
		this.subarea = subarea;
	}

	public boolean isFinanciado() {
		return financiado;
	}

	public void setFinanciado(boolean financiado) {
		this.financiado = financiado;
	}

	public TelaCronograma getTelaCronograma() {
		return telaCronograma;
	}

	public void setTelaCronograma(TelaCronograma telaCronograma) {
		this.telaCronograma = telaCronograma;
	}

	public Unidade getCentro() {
		return centro;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setCentro(Unidade centro) {
		this.centro = centro;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public String getObjetivos() {
		return objetivos;
	}

	public void setObjetivos(String objetivos) {
		this.objetivos = objetivos;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}


	public boolean isRenovacao() {
		return renovacao;
	}

	public void setRenovacao(boolean renovacao) {
		this.renovacao = renovacao;
	}

	public Servidor getServidorTecnico() {
		return servidorTecnico;
	}

	public void setServidorTecnico(Servidor servidorTecnico) {
		this.servidorTecnico = servidorTecnico;
	}

	public FormFile getArquivoProjeto() {
		return arquivoProjeto;
	}

	public void setArquivoProjeto(FormFile arquivoProjeto) {
		this.arquivoProjeto = arquivoProjeto;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public boolean isSegundaChamada() {
		return this.segundaChamada;
	}

	public void setSegundaChamada(boolean segundaChamada) {
		this.segundaChamada = segundaChamada;
	}

	public boolean isRelatorioSubmetido() {
		return relatorioSubmetido;
	}

	public void setRelatorioSubmetido(boolean relatorioSubmetido) {
		this.relatorioSubmetido = relatorioSubmetido;
	}

	public boolean isDescricaoIncompleta() {
		return descricaoIncompleta;
	}

	public void setDescricaoIncompleta(boolean descricaoIncompleta) {
		this.descricaoIncompleta = descricaoIncompleta;
	}

	public boolean isCoordenadorColaborador() {
		return coordenadorColaborador;
	}

	public void setCoordenadorColaborador(boolean coordenadorColaborador) {
		this.coordenadorColaborador = coordenadorColaborador;
	}

	public boolean isCheckClausula() {
		return checkClausula;
	}

	public void setCheckClausula(boolean checkClausula) {
		this.checkClausula = checkClausula;
	}

	public boolean isAnexoProjetoBase() {
		return anexoProjetoBase;
	}

	public void setAnexoProjetoBase(boolean anexoProjetoBase) {
		this.anexoProjetoBase = anexoProjetoBase;
	}

	public boolean isConsulta() {
		return consulta;
	}

	public void setConsulta(boolean consulta) {
		this.consulta = consulta;
	}

	public boolean isExibirAnos() {
		return exibirAnos;
	}

	public void setExibirAnos(boolean exibirAnos) {
		this.exibirAnos = exibirAnos;
	}

	public boolean isAlteracaoEdital() {
		return alteracaoEdital;
	}

	public void setAlteracaoEdital(boolean alteracaoEdital) {
		this.alteracaoEdital = alteracaoEdital;
	}
	
}