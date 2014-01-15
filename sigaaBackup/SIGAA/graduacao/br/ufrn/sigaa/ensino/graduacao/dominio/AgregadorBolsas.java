/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/02/2009
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.graduacao.jsf.AgregadorBolsasMBean;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.monitoria.dominio.ProvaSelecao;
import br.ufrn.sigaa.pesquisa.dominio.LinhaPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/**
 * Classe para encapsular os interesses nas bolsas de monitoria, pesquisa, extensão e apoio técnico
 * 
 * @author Henrique André
 * 
 */
public class AgregadorBolsas {

	private Integer id;
	
	private Integer idUsuario;

	private Integer idDetalhe;

	private Integer idProvaSelecao;

	private String descricao;

	private Integer vagasRemuneradas;

	private Integer vagasNaoRemuneradas;

	private Servidor responsavelProjeto;

	private Unidade unidade;

	private AreaConhecimentoCnpq areaConhecimento;

	private LinhaPesquisa linhaPesquisa;

	private String emailContato;

	private boolean selecao;
	
	private String cursosAlvo;

	private String tipoBolsa;
	
	private String bolsaValor;
	
	private Usuario usuario;
	
	private String statusAtualBolsa;
	
	private int idTipoBolsa;
	
	private List<Object> cursos = new ArrayList<Object>();
	
	private Long matricula;
	
	private Date dataAnuncio;
	
	private Integer idCotaBolsa;
	
	public boolean isPesquisa() {
		if (idTipoBolsa == AgregadorBolsasMBean.PESQUISA)
			return true;
		return false;
	}
	
	public boolean isExtensao() {
		if (idTipoBolsa == AgregadorBolsasMBean.EXTENSAO)
			return true;
		return false;
	}

	public boolean isApoioTecnico() {
		if (idTipoBolsa == AgregadorBolsasMBean.APOIO_TECNICO)
			return true;
		return false;
	}
	
	public boolean isMonitoria() {
		if (idTipoBolsa == AgregadorBolsasMBean.MONITORIA)
			return true;
		return false;
	}
	
	public boolean isAssociada() {
		if (idTipoBolsa == AgregadorBolsasMBean.ACOES_ASSOCIADAS)
			return true;
		return false;
	}	
	
	/**
	 * Contrutor AgregadorBolsas 
	 * @param obj
	 */
	public AgregadorBolsas(Object obj) {
		if (obj instanceof PlanoTrabalho)
			popularPesquisa((PlanoTrabalho) obj);
		else if (obj instanceof ProvaSelecao)
			popularMonitoria((ProvaSelecao) obj);
		else if (obj instanceof AtividadeExtensao)
			popularExtensao((AtividadeExtensao) obj);
		else if (obj instanceof Projeto)
			popularAssociada((Projeto) obj);
	}

	public AgregadorBolsas(PlanoTrabalho planoTrabalho) {
		popularPesquisa(planoTrabalho);
	}

	public AgregadorBolsas(ProvaSelecao provaSelecao) {
		popularMonitoria(provaSelecao);
	}

	public AgregadorBolsas(AtividadeExtensao atividade) {
		popularExtensao(atividade);
	}

	public AgregadorBolsas() {
	}

	/**
	 * Popula um objeto do tipo AgregadorBolsas com base em um Plano de Trabalho.
	 * @param planoTrabalho
	 */
	private void popularPesquisa(PlanoTrabalho planoTrabalho) {
		this.descricao = planoTrabalho.getTitulo();
		this.id = planoTrabalho.getId();
		this.responsavelProjeto = planoTrabalho.getOrientador();
		this.areaConhecimento = planoTrabalho.getProjetoPesquisa().getAreaConhecimentoCnpq();
		this.linhaPesquisa = planoTrabalho.getProjetoPesquisa().getLinhaPesquisa();
		this.unidade = planoTrabalho.getProjetoPesquisa().getUnidade();
		this.idDetalhe = planoTrabalho.getId();
		this.statusAtualBolsa = planoTrabalho.getStatusString();
		this.tipoBolsa = "PESQUISA";
		this.idTipoBolsa = AgregadorBolsasMBean.PESQUISA;
		
		if (planoTrabalho.getMembroProjetoDiscente() != null)
			this.matricula = planoTrabalho.getMembroProjetoDiscente().getDiscente().getMatricula();
	}

	/**
	 * Popula um objeto do tipo AgregadorBolsas com base em uma Prova de Seleção.
	 * @param provaSelecao
	 */
	private void popularMonitoria(ProvaSelecao provaSelecao) {
		this.id = provaSelecao.getId();
		this.descricao = provaSelecao.getProjetoEnsino().getAnoTitulo() + " (" + provaSelecao.getTitulo() +")";
		this.vagasRemuneradas = provaSelecao.getVagasRemuneradas();
		this.vagasNaoRemuneradas = provaSelecao.getVagasNaoRemuneradas();
		this.responsavelProjeto = provaSelecao.getProjetoEnsino().getCoordenacao();
		this.idDetalhe = provaSelecao.getProjetoEnsino().getId();
		this.statusAtualBolsa = provaSelecao.getProjetoEnsino().getSituacaoProjeto().getDescricao();
		this.idProvaSelecao = provaSelecao.getId();
		selecao = true;
		this.tipoBolsa = "MONITORIA";
		this.idTipoBolsa = AgregadorBolsasMBean.MONITORIA;
	}

	/**
	 * Popula um objeto do tipo AgregadorBolsas com base em uma Atividade de Extensão.
	 * @param atividade
	 */
	private void popularExtensao(AtividadeExtensao atividade) {
		this.id = atividade.getId();
		this.descricao = atividade.getTitulo();
		this.unidade = atividade.getUnidade();
		this.setVagasRemuneradas(atividade.getVagasRemuneradasDisponiveis());
		this.responsavelProjeto = atividade.getCoordenacao().getServidor();
		this.statusAtualBolsa = atividade.getSituacaoProjeto().getDescricao();
		this.idDetalhe = atividade.getId();
		this.tipoBolsa = "EXTENSÃO";
		this.idTipoBolsa = AgregadorBolsasMBean.EXTENSAO;
		
	}
	
	/**
	 * Popula um objeto do tipo AgregadorBolsas com base em uma Ação Associada.
	 * @param atividade
	 */
	private void popularAssociada(Projeto projeto) {
		this.id = projeto.getId();
		this.descricao = projeto.getTitulo();
		this.unidade = projeto.getUnidade();
		this.setVagasRemuneradas(projeto.getBolsasConcedidas());
		this.responsavelProjeto = projeto.getCoordenador().getServidor();
		this.statusAtualBolsa = projeto.getSituacaoProjeto().getDescricao();
		this.idDetalhe = projeto.getId();
		this.tipoBolsa = "AÇÕES ASSOCIADAS";
		this.idTipoBolsa = AgregadorBolsasMBean.ACOES_ASSOCIADAS;
		
	}
	
	/**
	 * Retorna lista de bolsas do tipo AgregadorBolsas 
	 * @param resultado
	 * @return
	 */
	public static List<AgregadorBolsas> toList(List<?> resultado) {
		List<AgregadorBolsas> lista = new ArrayList<AgregadorBolsas>();
		for (Object obj : resultado) {
			AgregadorBolsas ab = new AgregadorBolsas(obj);
			lista.add(ab);
		}
		
		return lista;
	}

	/**
	 * Totalizado de bolsas do docente.
	 * 
	 * @param bolsas
	 * @param servidor
	 * @param vagasRemunerada
	 * @param idCotaBolsa
	 * @return
	 */
	public static int totalizadorVagasRemuneradas(ArrayList<AgregadorBolsas> bolsas, Servidor servidor, int vagasRemunerada){
		int total = 0;
		for (AgregadorBolsas agregadorBolsas : bolsas) {
			if (agregadorBolsas.getResponsavelProjeto().getId() == servidor.getId()) {
				total = agregadorBolsas.getVagasRemuneradas() + vagasRemunerada;
				agregadorBolsas.setVagasRemuneradas(total);
			}
		}
		return total;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdDetalhe() {
		return idDetalhe;
	}

	public void setIdDetalhe(Integer idDetalhe) {
		this.idDetalhe = idDetalhe;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getVagasRemuneradas() {
		return vagasRemuneradas;
	}

	public void setVagasRemuneradas(Integer vagasRemuneradas) {
		this.vagasRemuneradas = vagasRemuneradas;
	}

	public Integer getVagasNaoRemuneradas() {
		return vagasNaoRemuneradas;
	}

	public void setVagasNaoRemuneradas(Integer vagasNaoRemuneradas) {
		this.vagasNaoRemuneradas = vagasNaoRemuneradas;
	}

	public Servidor getResponsavelProjeto() {
		return responsavelProjeto;
	}

	public void setResponsavelProjeto(Servidor responsavelProjeto) {
		this.responsavelProjeto = responsavelProjeto;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public AreaConhecimentoCnpq getAreaConhecimento() {
		return areaConhecimento;
	}

	public void setAreaConhecimento(AreaConhecimentoCnpq areaConhecimento) {
		this.areaConhecimento = areaConhecimento;
	}

	public LinhaPesquisa getLinhaPesquisa() {
		return linhaPesquisa;
	}

	public void setLinhaPesquisa(LinhaPesquisa linhaPesquisa) {
		this.linhaPesquisa = linhaPesquisa;
	}

	public String getEmailContato() {
		return emailContato;
	}

	public void setEmailContato(String emailContato) {
		this.emailContato = emailContato;
	}

	public boolean isSelecao() {
		return selecao;
	}

	public void setSelecao(boolean selecao) {
		this.selecao = selecao;
	}

	public Integer getIdProvaSelecao() {
		return idProvaSelecao;
	}

	public void setIdProvaSelecao(Integer idProvaSelecao) {
		this.idProvaSelecao = idProvaSelecao;
	}

	public String getCursosAlvo() {
		return cursosAlvo;
	}

	public void setCursosAlvo(String cursosAlvo) {
		this.cursosAlvo = cursosAlvo;
	}

	public String getTipoBolsa() {
		return tipoBolsa;
	}

	public void setTipoBolsa(String tipoBolsa) {
		this.tipoBolsa = tipoBolsa;
	}

	public String getBolsaValor() {
		return bolsaValor;
	}

	public void setBolsaValor(String bolsaValor) {
		this.bolsaValor = bolsaValor;
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Object> getCursos() {
		return cursos;
	}

	public void setCursos(List<Object> cursos) {
		this.cursos = cursos;
	}

	public String getStatusAtualBolsa() {
		return statusAtualBolsa;
	}

	public void setStatusAtualBolsa(String statusAtualBolsa) {
		this.statusAtualBolsa = statusAtualBolsa;
	}

	public int getIdTipoBolsa() {
		return idTipoBolsa;
	}

	public void setIdTipoBolsa(int idTipoBolsa) {
		this.idTipoBolsa = idTipoBolsa;
	}

	public Long getMatricula() {
		return matricula;
	}

	public void setMatricula(Long matricula) {
		this.matricula = matricula;
	}

	public Date getDataAnuncio() {
		return dataAnuncio;
	}

	public void setDataAnuncio(Date dataAnuncio) {
		this.dataAnuncio = dataAnuncio;
	}

	public Integer getIdCotaBolsa() {
		return idCotaBolsa;
	}

	public void setIdCotaBolsa(Integer idCotaBolsa) {
		this.idCotaBolsa = idCotaBolsa;
	}

}