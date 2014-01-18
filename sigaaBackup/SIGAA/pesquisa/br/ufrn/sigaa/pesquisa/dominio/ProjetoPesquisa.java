/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/09/2006
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;
import br.ufrn.sigaa.projetos.dominio.CronogramaProjeto;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.HistoricoSituacaoProjeto;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Classe que armazena as principais informações dos projetos de pesquisa
 * 
 * @author ricardo
 */
@Entity
@Table(name = "projeto_pesquisa", schema = "pesquisa", uniqueConstraints = {})
public class ProjetoPesquisa implements PersistDB, ViewAtividadeBuilder, Comparable<ProjetoPesquisa> {

	/** Atributo utilizado para representar o ID do projeto de pesquisa */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_projeto_pesquisa")
	private int id;
	
	/** Atributo utilizado para representar o Projeto de Pesquisa */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_projeto")
	private Projeto projeto = new Projeto(new TipoProjeto(TipoProjeto.PESQUISA));
	
	/** Atributo utilizado para representar a Área de conhecimento CNPQ */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_area_conhecimento_cnpq")
	private AreaConhecimentoCnpq areaConhecimentoCnpq = new AreaConhecimentoCnpq();
	
	/** Atributo utilizado para representar o código do Projeto de Pesquisa */
	@Embedded
	private CodigoProjetoPesquisa codigo = new CodigoProjetoPesquisa();

	/** Projeto de pesquisa original que passou pelo processo de avaliação */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_projeto_original", unique = false, nullable = true, insertable = true, updatable = true)
	private ProjetoPesquisa projetoOriginal;

	/** Atributo utilizado para representar o edital do projeto de pesquisa */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_edital_pesquisa", unique = false, nullable = true, insertable = true, updatable = true)
	private EditalPesquisa edital;

	/** Atributo utilizado para representar o centro academico */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_centro", unique = false, nullable = true, insertable = true, updatable = true)
	private Unidade centro;

	/** Atributo utilizado para representar as linhas de pesquisa que um projeto pode seguir */
	@ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_linha_pesquisa", unique = false, nullable = true, insertable = true, updatable = true)
	private LinhaPesquisa linhaPesquisa;

	/** Atributo utilizado para representar os financiamentos do projeto de pesquisa */
	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "projetoPesquisa")
	private Set<FinanciamentoProjetoPesq> financiamentosProjetoPesq =
		new HashSet<FinanciamentoProjetoPesq>(0);

	/** Atributo utilizado para representar as avaliações do projeto de pesquisa */
	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "projetoPesquisa")
	private Set<AvaliacaoProjeto> avaliacoesProjeto =
		new HashSet<AvaliacaoProjeto>(0);

	/** Atributo utilizado para representar os planos de trabalho do projeto de pesquisa */
	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "projetoPesquisa")
	private Set<PlanoTrabalho> planosTrabalho = new HashSet<PlanoTrabalho>(0);

	/** Atributo utilizado para representar os relatórios do projeto de pesquisa */
	@OneToMany(mappedBy = "projetoPesquisa", fetch = FetchType.LAZY)
	private Set<RelatorioProjeto> relatoriosProjeto;

	/** Atributo utilizado para representar o coordenador do projeto de pesquisa */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_coordenador", unique = false, nullable = true, insertable = true, updatable = true)
	private Servidor coordenador;

	/** Atributo utilizado para representar o coordenador Externo a UFRN do projeto de pesquisa */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_coordenador_externo")
	private DocenteExterno coordenadorExterno;

	/** número de vezes que este projeto foi renovado */
	@Column(name = "numero_renovacoes")
	private int numeroRenovacoes;

	/** Atributo utilizado para representar a definição da propriedade intelectual do projeto de pesquisa */
	@Column(name = "definicao_propriedade_intelectual")
	private String definicaoPropriedadeIntelectual;

	/** Atributo utilizado para representar a categoria do projeto de pesquisa */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "categoria_projeto")
	private CategoriaProjetoPesquisa categoria;

	/** Atributos utilizados durante as avaliações */
	@Transient
	long qtdAvaliacoesSubmetidas = 0;

	/** Atributo utilizado para representar a quantidade de Avaliações Realizadas pelo Projeto de Pesquisa */
	@Transient
	long qtdAvaliacoesRealizadas = 0;

	/** Atributo utilizado para representar a quantidade de Avaliações Negadas pelo Projeto de Pesquisa */
	@Transient
	long qtdAvaliacoesNegadas = 0;

	/** Atributo utilizado para representar se o projeto foi aprovado */
	@Transient
	Boolean aprovado;
	
	/** Atributo utilizado para representar o código da Área do Projeto de Pesquisa */
	@Transient
	String codigoArea;
	
	/** atributo utilizado para controle de seleção na jsp */
	@Transient
	private boolean selecionado;

	/** Quantidade de anos que o projeto vai fica ativo */
	@Transient
	private int tempoEmAnoProjeto;
	
	@Transient
	private boolean permiteAlterarEdital;
	
	/** Serve para saber se o docente está ciente do termo de concordância do edital */
	@Transient
	private boolean concordanciaTermo;
	
	/** default constructor */
	public ProjetoPesquisa() {
	}

	/**
	 * Calcular a média das avaliações do projeto
	 */
	public void calcularMedia() {
		double media  = 0;
		int numAvaliacoes = 0;
		for (AvaliacaoProjeto avaliacao : getAvaliacoesProjeto()) {
			if ( avaliacao.getSituacao() == AvaliacaoProjeto.REALIZADA ){
				media += avaliacao.getMedia();
				numAvaliacoes++;
			}
		}

		if (numAvaliacoes > 0) {
			media = media / numAvaliacoes;
		}
		 projeto.setMedia(media);
	}

	/** minimal constructor */
	public ProjetoPesquisa(int idProjetoPesquisa) {
		setId(idProjetoPesquisa);
	}

	public CodigoProjetoPesquisa getCodigo() {
		return codigo;
	}

	@Transient
	public String getCodigoTitulo() {
		return (codigo != null ? codigo.toString() + " - " : "") + getTitulo();
	}

	public void setCodigo(CodigoProjetoPesquisa codigo) {
		this.codigo = codigo;
	}

	public AreaConhecimentoCnpq getAreaConhecimentoCnpq() {
		return areaConhecimentoCnpq;
	}

	public void setAreaConhecimentoCnpq(AreaConhecimentoCnpq areaConhecimentoCnpq) {
		this.areaConhecimentoCnpq = areaConhecimentoCnpq;
	}

	public LinhaPesquisa getLinhaPesquisa() {
		return linhaPesquisa;
	}

	public void setLinhaPesquisa(LinhaPesquisa linhaPesquisa) {
		this.linhaPesquisa = linhaPesquisa;
	}

	public String getJustificativa() {
		return projeto.getJustificativa();
	}

	/** 
	 * Método utilizado para setar a justificativa do projeto
	 * @param justificativa
	 */
	public void setJustificativa(String justificativa) {
		projeto.setJustificativa(justificativa);
	}

	public String getBibliografia() {
		return projeto.getReferencias();
	}

	/**
	 * Método utilizado para setar a bibliografia do projeto
	 * @param bibliografia
	 */
	public void setBibliografia(String bibliografia) {
		projeto.setReferencias(bibliografia);
	}

	public boolean isInterno() {
		return projeto.isInterno();
	}

	/**
	 * Método utilizado para setar se o projeto é cadastrado com o apoio da universidade
	 * @param interno
	 */
	public void setInterno(boolean interno) {
		this.getProjeto().setInterno(interno);
	}

	public Set<FinanciamentoProjetoPesq> getFinanciamentosProjetoPesq() {
		return financiamentosProjetoPesq;
	}

	public void setFinanciamentosProjetoPesq(
			Set<FinanciamentoProjetoPesq> financiamentoProjetoPesqs) {
		financiamentosProjetoPesq = financiamentoProjetoPesqs;
	}

	public Set<AvaliacaoProjeto> getAvaliacoesProjeto() {
		return avaliacoesProjeto;
	}

	public void setAvaliacoesProjeto(Set<AvaliacaoProjeto> avaliacaoProjetos) {
		avaliacoesProjeto = avaliacaoProjetos;
	}

	public Set<PlanoTrabalho> getPlanosTrabalho() {
		HashSet<PlanoTrabalho> planos = new HashSet<PlanoTrabalho>();
		for(PlanoTrabalho p: planosTrabalho)
			if(p.getStatus() != TipoStatusPlanoTrabalho.EXCLUIDO) planos.add(p);
		return planos;
	}

	public void setPlanosTrabalho(Set<PlanoTrabalho> planoTrabalhos) {
		planosTrabalho = planoTrabalhos;
	}

	public Collection<MembroProjeto> getMembrosProjeto() {
		return projeto.getEquipe();
	}

	/**
	 * Método utilizado para setar os membros do projeto de pesquisa
	 * @param membrosProjeto
	 */
	public void setMembrosProjeto(Collection<MembroProjeto> membrosProjeto) {
		projeto.setEquipe(membrosProjeto);
	}

	public Set<RelatorioProjeto> getRelatoriosProjeto() {
		return relatoriosProjeto;
	}

	public void setRelatoriosProjeto(Set<RelatorioProjeto> relatoriosProjeto) {
		this.relatoriosProjeto = relatoriosProjeto;
	}

	/**
	 * Método utilizado para informar os relatórios do projeto, se houverem
	 * @return
	 */
	@Transient
	public RelatorioProjeto getRelatorioProjeto() {

		getRelatoriosProjeto();
		if ( relatoriosProjeto != null && !relatoriosProjeto.isEmpty()  ) {
			return relatoriosProjeto.iterator().next();
		} else {
			return null;
		}

	}

	/**
	 * Adiciona um financiamento externo ao projeto de pesquisa
	 * @param obj
	 * @return
	 */
	public boolean addFinanciamentoProjetoPesq(FinanciamentoProjetoPesq obj) {
		obj.setProjetoPesquisa(this);
		return financiamentosProjetoPesq.add(obj);
	}

	/**
	 * Remove um financiamento externo de um projeto de pesquisa
	 * @param obj
	 * @return
	 */
	public boolean removeFinanciamentoProjetoPesq(FinanciamentoProjetoPesq obj) {
		obj.setProjetoPesquisa(null);
		return financiamentosProjetoPesq.remove(obj);
	}

	/**
	 * Adiciona uma avaliação de projeto ao projeto de pesquisa
	 * @param obj
	 * @return
	 */
	public boolean addAvaliacaoProjeto(AvaliacaoProjeto obj) {
		obj.setProjetoPesquisa(this);
		return avaliacoesProjeto.add(obj);
	}

	/**
	 * Remove uma avaliação de projeto do projeto de pesquisa
	 * @param obj
	 * @return
	 */
	public boolean removeAvaliacaoProjeto(AvaliacaoProjeto obj) {
		obj.setProjetoPesquisa(null);
		return avaliacoesProjeto.remove(obj);
	}

	/**
	 * Adiciona um plano de trabalho ao projeto de pesquisa
	 * @param obj
	 * @return
	 */
	public boolean addPlanoTrabalho(PlanoTrabalho obj) {
		obj.setProjetoPesquisa(this);
		return planosTrabalho.add(obj);
	}

	/**
	 * Remove um plano de trabalho do projeto de pesquisa
	 * @param obj
	 * @return
	 */
	public boolean removePlanoTrabalho(PlanoTrabalho obj) {
		obj.setProjetoPesquisa(null);
		return planosTrabalho.remove(obj);
	}

	public List<CronogramaProjeto> getCronogramas() {
		return projeto.getCronograma();
	}

	/** 
	 * Método utilizado para setar os cronogramas do projeto de pesquisa
	 * @param cronogramas
	 */
	public void setCronogramas(List<CronogramaProjeto> cronogramas) {
		projeto.setCronograma(cronogramas);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testTransientEquals(this, obj, "id", "projeto.titulo", "projeto.descricao");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(getId(), getTitulo(), getDescricao());
	}

	public long getQtdAvaliacoesNegadas() {
		return qtdAvaliacoesNegadas;
	}

	public void setQtdAvaliacoesNegadas(long qtdAvaliacoesNegadas) {
		this.qtdAvaliacoesNegadas = qtdAvaliacoesNegadas;
	}

	public long getQtdAvaliacoesRealizadas() {
		return qtdAvaliacoesRealizadas;
	}

	public void setQtdAvaliacoesRealizadas(long qtdAvaliacoesRealizadas) {
		this.qtdAvaliacoesRealizadas = qtdAvaliacoesRealizadas;
	}

	@Transient
	public long getQtdAvaliacoesSubmetidas() {
		return qtdAvaliacoesSubmetidas;
	}

	/** Seta a quantidade de Avaliações Submetidas */
	public void setQtdAvaliacoesSubmetidas(long qtdAvaliacoesSubmetidas) {
		this.qtdAvaliacoesSubmetidas = qtdAvaliacoesSubmetidas;
	}

	public Boolean getAprovado() {
		return aprovado;
	}

	public void setAprovado(Boolean aprovado) {
		this.aprovado = aprovado;
	}

	public EditalPesquisa getEdital() {
		return edital;
	}

	public Unidade getCentro() {
		return centro;
	}

	public void setEdital(EditalPesquisa edital) {
		this.edital = edital;
	}

	public void setCentro(Unidade gestoraAcademica) {
		centro = gestoraAcademica;
	}

	/**
	 * Método utilizado para setar a sigla do centro acadêmico do projeto
	 * @param sigla
	 */
	public void setSiglaCentro(String sigla) {
		Unidade centro = new Unidade();
		centro.setSigla(sigla);
		this.centro = centro;
	}

	public ProjetoPesquisa getProjetoOriginal() {
		return projetoOriginal;
	}

	public void setProjetoOriginal(ProjetoPesquisa projetoOriginal) {
		this.projetoOriginal = projetoOriginal;
	}

	/**
	 * Método utilizado para informar o coordenador do projeto de pesquisa
	 * @return
	 */
	public Servidor getCoordenador() {
		if ( (coordenador == null || coordenador.getId() == 0) && coordenadorExterno != null) {
			Servidor proxy = new Servidor();
			proxy.setPessoa(coordenadorExterno.getPessoa());
			return proxy;
		}
		return coordenador;
	}

	public void setCoordenador(Servidor coordenador) {
		this.coordenador = coordenador;
	}

	/**
	 * Retorna uma coleção como todos os membros que são docente do projeto de pesquisa
	 * 
	 * @return
	 */
	public Collection<MembroProjeto> getMembrosDocentes() {
		if (getMembrosProjeto() == null) {
			return null;
		}

		Collection<MembroProjeto> docentes = new ArrayList<MembroProjeto>();
		for (MembroProjeto membro : getMembrosProjeto()) {
			if (membro.getServidor() != null && membro.getServidor().getCategoria().getId() == Categoria.DOCENTE) {
				docentes.add(membro);
			}
		}
		return docentes;
	}

	/**
	 * Retorna uma coleção como todos os membros que são docente do projeto de pesquisa
	 * 
	 * @return
	 */
	public Collection<MembroProjeto> getMembrosExterno() {
		if (getMembrosProjeto() == null) {
			return null;
		}
		Collection<MembroProjeto> docentesExternos = new ArrayList<MembroProjeto>();
		for (MembroProjeto membro : getMembrosProjeto()) {
			if (membro.getDocenteExterno() != null) {
				docentesExternos.add(membro);
			}
		}
		return docentesExternos;
	}
	
	public int getNumeroRenovacoes() {
		return numeroRenovacoes;
	}

	public void setNumeroRenovacoes(int numeroRenovacoes) {
		this.numeroRenovacoes = numeroRenovacoes;
	}

	public String getItemView() {
		return "<td>"+ getCodigo().toString() + "</td>" +
			"<td>" + getTitulo() + "</td>";
	}

	public HashMap<String, String> getItens() {
		return null;
	}

	public String getTituloView() {
		return "<td>Código</td><td>Título</td>";
	}

	public float getQtdBase() {
		return 1;
	}

	/**
	 * Método utilizado para Comparar o projeto de pesquisa atual com o projeto de pesquisa passado por parâmetro
	 */
	public int compareTo(ProjetoPesquisa o) {
		int result = centro.getSigla().compareTo(o.getCentro().getSigla());

		if ( result == 0 ) {
			result = codigo.compareTo(o.getCodigo());
		}
		return result;
	}

	/**
	 * Método utilizado para verificar se o servidor passado por parâmetro faz parte ad equipe do projeto de pesquisa
	 * @param servidor
	 * @return
	 */
	public boolean isMembroProjeto(Servidor servidor) {
		if (servidor == null) {
			return false;
		}

		for ( MembroProjeto membro : getMembrosProjeto() ) {
			if ( membro.getServidor() != null && servidor.getId() == membro.getServidor().getId() ) {
				return true;
			}
		}

		return false;
	}

	/** Utilizado para verificar se há plano de trabalho vínculado ao projeto que não esteja excluído */
	@Transient
	public boolean haPlanoAtivo() {
		for (PlanoTrabalho plano : getPlanosTrabalho()) {
			if ( plano.getStatus() != TipoStatusPlanoTrabalho.EXCLUIDO )
				return true;
		}
		return false;
	}
	
	public String getDefinicaoPropriedadeIntelectual() {
		return definicaoPropriedadeIntelectual;
	}

	public void setDefinicaoPropriedadeIntelectual(
			String definicaoPropriedadeIntelectual) {
		this.definicaoPropriedadeIntelectual = definicaoPropriedadeIntelectual;
	}

	public CategoriaProjetoPesquisa getCategoria() {
		return categoria;
	}

	public void setCategoria(CategoriaProjetoPesquisa categoria) {
		this.categoria = categoria;
	}

	public DocenteExterno getCoordenadorExterno() {
		return coordenadorExterno;
	}

	public void setCoordenadorExterno(DocenteExterno coordenadorExterno) {
		this.coordenadorExterno = coordenadorExterno;
	}

	@Transient
	public String getCategoriaDescricao(){
		return categoria != null ? categoria.getDenominacao() : "";
	}

	public boolean isEmAvaliacao() {
		return getSituacaoProjeto().getId() == TipoSituacaoProjeto.DISTRIBUIDO_AUTOMATICAMENTE ||
			getSituacaoProjeto().getId() == TipoSituacaoProjeto.DISTRIBUIDO_MANUALMENTE;
	}
	
	/**
	 * Verifica se o projeto está em execução
	 * @return
	 */
    public boolean isEmExecucao() {
    	return (getSituacaoProjeto().getId() == TipoSituacaoProjeto.EM_ANDAMENTO ||
    			(getProjeto().getSituacaoProjeto().getId() == TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO));
    }

	/**
	 * Verifica se o projeto está aprovado
	 * @return
	 */
    public boolean isAprovado() {
    	return (getSituacaoProjeto().getId() == TipoSituacaoProjeto.APROVADO);
    }
    
    /**
     * Retorna o coordenador da lista de membro do projeto
     * 
     * @return
     */
    @Transient
    public MembroProjeto getCoordenacao() {
    	if (getProjeto().getCoordenador() != null) {
    		return getProjeto().getCoordenador();    
    	}

    	for(MembroProjeto mp : getProjeto().getEquipe()) {
    		if(mp.getFuncaoMembro() != null && mp.getFuncaoMembro().getId() == FuncaoMembro.COORDENADOR )
    			return mp;
    	}	

    	return null;
    }
    
	public String getCodigoArea() {
		return codigoArea;
	}

	public void setCodigoArea(String codigoArea) {
		this.codigoArea = codigoArea;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

	public String getTitulo() {
		return projeto.getTitulo();
	}

	/**
	 * Método utilizado para setar o título do projeto de pesquisa
	 * @param titulo
	 */
	public void setTitulo(String titulo) {
		projeto.setTitulo(titulo);
	}
	
	public String getDescricao() {
		return projeto.getResumo();
	}

	/**
	 * Método utilizado para setar a descrição do projeto de pesquisa
	 * @param descricao
	 */
	public void setDescricao(String descricao) {
		projeto.setResumo(descricao);
	}
	
	public String getMetodologia() {
		return projeto.getMetodologia();
	}

	/**
	 * Método utilizado para setar a metodologia do projeto de pesquisa
	 * @param metodologia
	 */
	public void setMetodologia(String metodologia) {
		projeto.setMetodologia(metodologia);
	}
	
	public String getObjetivos() {
		return projeto.getObjetivos();
	}

	/**
	 * Método utilizado para setar os objetivos do projeto de pesquisa
	 * @param objetivos
	 */
	public void setObjetivos(String objetivos) {
		projeto.setObjetivos(objetivos);
	}
	
	public String getEmail() {
		return projeto.getEmail();
	}

	/** Seta o email no projeto */
	public void setEmail(String email) {
		projeto.setEmail(email);
	}
	
	public String getPalavrasChave() {
		return projeto.getPalavrasChave();
	}

	/** Seta no projeto a palavra chave */
	public void setPalavrasChave(String palavasChave) {
		projeto.setPalavrasChave(palavasChave);
	}
	
	public Integer getAno() {
		return projeto.getAno();
	}

	/** Seta o ano no projeto */
	public void setAno(Integer ano) {
		projeto.setAno(ano);
	}
	
	public Date getDataCadastro() {
		return projeto.getDataCadastro();
	}
	
	/** Seta a data de cadastro no projeto */
	public void setDataCadastro(Date dataCadastro) {
		projeto.setDataCadastro(dataCadastro);
	}
	
	public Date getDataInicio() {
		return projeto.getDataInicio();
	}

	/** Seta a data de início no projeto */
	public void setDataInicio(Date dataInicio) {
		projeto.setDataInicio(dataInicio);
	}
	
	public Date getDataFim() {
		return projeto.getDataFim();
	}

	/** Seta a data Final do projeto */
	public void setDataFim(Date dataFim) {
		projeto.setDataFim(dataFim);
	}

	public TipoSituacaoProjeto getSituacaoProjeto() {
		return projeto.getSituacaoProjeto();
	}

	/** Seta a situação do projeto */
	public void setSituacaoProjeto(TipoSituacaoProjeto situacaoProjeto) {
		projeto.setSituacaoProjeto(situacaoProjeto);
	}

	public Unidade getUnidade() {
		return projeto.getUnidade();
	}

	/** Seta a unidade do projeto */
	public void setUnidade(Unidade unidade) {
		projeto.setUnidade(unidade);
	}
	
	public Collection<HistoricoSituacaoProjeto> getHistoricoSituacao() {
		return projeto.getHistoricoSituacao();
	}

	/** Seta o histório da situação do projeto */
	public void setHistoricoSituacao(
			Collection<HistoricoSituacaoProjeto> historicoSituacao) {
		projeto.setHistoricoSituacao(historicoSituacao);
	}

	public RegistroEntrada getRegistroEntrada() {
		return projeto.getRegistroEntrada();
	}

	/** Seta o registro de entrada no projeto */
	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		projeto.setRegistroEntrada(registroEntrada);
	}
	
	public boolean isAtivo() {
		return projeto.isAtivo();
	}

	/** Seta o campo ativo no projeto */
	public void setAtivo(boolean ativo) {
		projeto.setAtivo(ativo);
	}
	
	public Integer getNumeroInstitucional() {
		return projeto.getNumeroInstitucional();
	}

	/** Seta o número Institucional no projeto */
	public void setNumeroInstitucional(Integer numeroInstitucional) {
		projeto.setNumeroInstitucional(numeroInstitucional);
	}
	
	public boolean isProjetoAssociado(){
		return projeto.isPesquisa();
	}
	
	/** Seta a chave primária do projeto */
	public void setIdProjeto(int id) {
		projeto.setId(id);
	}

	public Double getMedia() {
		return projeto.getMedia();
	}
	
	/** Seta a média do projeto */
	public void setMedia(Double media) {
		projeto.setMedia(media);
	}

	public int getTempoEmAnoProjeto() {
		return tempoEmAnoProjeto;
	}

	public void setTempoEmAnoProjeto(int tempoEmAnoProjeto) {
		this.tempoEmAnoProjeto = tempoEmAnoProjeto;
	}

	public boolean isPermiteAlterarEdital() {
		return permiteAlterarEdital;
	}

	public void setPermiteAlterarEdital(boolean permiteAlterarEdital) {
		this.permiteAlterarEdital = permiteAlterarEdital;
	}
	
	public boolean isPassivelFinalizacaoCoordenador(){
		return !isInterno() && getSituacaoProjeto().getId() == TipoSituacaoProjeto.EM_ANDAMENTO;
	}
	
	
	public boolean isConcordanciaTermo() {
		return concordanciaTermo;
	}

	public void setConcordanciaTermo(boolean concordanciaTermo) {
		this.concordanciaTermo = concordanciaTermo;
	}

	public Date getDataAtualizacao() {
		return projeto.getDataAtualizacao();
	}
	
	public RegistroEntrada getRegistroAtualizacao() {
		return projeto.getRegistroAtualizacao();
	}
}