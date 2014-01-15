/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 06/10/2006
 *
 */

package br.ufrn.sigaa.pesquisa.dominio;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademico;
import br.ufrn.sigaa.projetos.dominio.Edital;

/**
 * Classe que representa os editais publicados pelo Gestor do M�dulo de Pesquisa
 * 
 * @author ricardo
 *
 */
@Entity
@Table(name = "edital_pesquisa", schema = "pesquisa", uniqueConstraints = {})
public class EditalPesquisa implements Validatable {

	/** Sem restrin��o sobre a titula��o do docente. */
	public static final int SEM_RESTRICAO = 10;
	
	/** Defini��o da chave prim�ria */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_edital_pesquisa")
	private int id;
	
	/** Referente ao edital base que cont�m dados comuns a todos os editais de projetos */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_edital")
	private Edital edital = new Edital();

	/** Titula��o m�nima do orientador para solicita��o de cotas */
	@Column(name = "titulacao_minima_cotas")
	private int titulacaoMinimaCotas;

	/** Cota a qual os projetos e planos de trabalho deste edital se referem */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cota", unique = false, nullable = false, insertable = true, updatable = true)
	private CotaBolsas cota;

	/** FPPI m�nimo necess�rio para participar da distribui��o de cotas */
	@Column(name = "fppi_minimo")
	private Double fppiMinimo;

	/** Define se o edital ir� realizar distribui��o de cotas de bolsas */
	@Column(name = "distribuicao_cotas")
	private boolean distribuicaoCotas;
	
	/** Indica se o edital � para submiss�o de planos de trabalho volunt�rios */
	private Boolean voluntario;
	
	/** Indica se o resultado da distribui��o de cotas para o edital deve ser divulgado. */
	@Column(name = "resultado_divulgado")
	private Boolean resultadoDivulgado;
	
	/** Indica se o processo de avalia��o dos projetos e planos de trabalho que concorrem a este edital est� em vig�ncia. */
	@Column(name = "avaliacao_vigente")
	private Boolean avaliacaoVigente;
	
	/** Categoria dos projetos que ser�o submetidos para concorrer no edital */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "categoria")
	private CategoriaProjetoPesquisa categoria;

	/** Lista da distribui��o de cotas do edital */
	@OneToMany(cascade = {}, fetch = FetchType.LAZY, mappedBy = "edital")
	private Collection<CotaDocente> cotasDocentes = new TreeSet<CotaDocente>();

	/** Lista das cotas de um tipo de bolsa no edital */
	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "editalPesquisa")
	@OrderBy("tipoBolsa")
	private Collection<Cotas> cotas = new HashSet<Cotas>();
	
	/** Este par�metro define se qualquer tipo de membro do projeto pode ser orientador dos planos de trabalho ou apenas o coordenador do projeto. 
	 * O valor default � false, indicando que qualquer membro pode ser orientador dos discentes no projeto.*/
	@Column(name = "apenas_coordenador_orienta_plano")
	private boolean apenasCoordenadorOrientaPlano = false;
	
	/**
	 * Alguns editais definem uma m�dia geral m�nima necess�ria para que o aluno possa assumir uma bolsa vinculada aquele edital
	 * Este atributo define qual �ndice que ser� checado. Pode ser nulo caso n�o haja este verifica��o
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name="id_indice_academico")
	private IndiceAcademico indiceChecagem;
	
	/**
	 * Este atributo define o valor m�nimo do indice do aluno necess�rio para que o mesmo possa ser vinculado a algum plano de trabalho vinculado a este edital.
	 * Esta valida��o ser� realizada no momento de vincular o aluno ao plano de trabalho.
	 */
	@Column(name = "valor_minimo_indice_checagem")
	private Double valorMinimoIndiceChecagem = null;
	
	/**
	 * Este atributo define projetos de pesquisa relacionados a este edital poder�o ser cadastrados por qualquer tipo de docente externo ou apenas
	 * por docentes externos do tipo colaborador volunt�rio. 
	 */
	@Column(name = "apenas_colaborador_voluntario_cadastra_projeto")
	private boolean apenasColaboradorVoluntarioCadastraProjeto = true;
	
	/**
	 * Este atributo define se professores substitutos podem cadastrar projetos de pesquisa vinculados a este edital.
	 */
	@Column(name = "professor_substituto_cadastra_projeto")
	private boolean professorSubstitutoCadastraProjeto = false;
	
	/** Data inicial de execu��o dos projetos */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio_execucao", unique = false, nullable = false, insertable = true, updatable = true)
	private Date inicioExecucaoProjetos;
	
	/** Data final de execu��o dos projetos */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim_execucao", unique = false, nullable = false, insertable = true, updatable = true)
	private Date fimExecucaoProjetos;
	
	/** Esse atributo define o c�digo do edital de pesquisa */
	private String codigo;
	
	/** Cria��o de um mapa com todas as titula��es poss�veis */
	private static Map<Integer, String> titulacoes = new TreeMap<Integer, String>();
	static {
		titulacoes.put(SEM_RESTRICAO, "SEM RESTRI��ES");
		titulacoes.put(Formacao.MESTRE, "MESTRES");
		titulacoes.put(Formacao.DOUTOR, "DOUTORES");
	}

	public EditalPesquisa() {
		setEdital(new Edital());
		setTipo(Edital.PESQUISA);
		setTitulacaoMinimaCotas(-1);
		setVoluntario(false);
		setResultadoDivulgado(false);
		setAtivo(true);
	}

	/** Retorna a titula��o m�nima para o edital */
	public int getTitulacaoMinimaCotas() {
		return titulacaoMinimaCotas;
	}

	/** Set a titula��o m�nima para o edital em quest�o */
	public void setTitulacaoMinimaCotas(int titulacaoMinimaCotas) {
		this.titulacaoMinimaCotas = titulacaoMinimaCotas;
	}

	public static Map<Integer, String> getTitulacoes() {
		return titulacoes;
	}

	public String getTitulacaoMinimaCotasDescricao() {
		return titulacoes.get(this.getTitulacaoMinimaCotas());
	}

	public CotaBolsas getCota() {
		return cota;
	}

	public void setCota(CotaBolsas cota) {
		this.cota = cota;
	}

	public Double getFppiMinimo() {
		return fppiMinimo;
	}

	public void setFppiMinimo(Double fppiMinimo) {
		this.fppiMinimo = fppiMinimo;
	}

	public Collection<CotaDocente> getCotasDocentes() {
		return cotasDocentes;
	}

	public void setCotasDocentes(Collection<CotaDocente> cotasDocentes) {
		this.cotasDocentes = cotasDocentes;
	}

	public static void setTitulacoes(Map<Integer, String> titulacoes) {
		EditalPesquisa.titulacoes = titulacoes;
	}

	public boolean isDistribuicaoCotas() {
		return this.distribuicaoCotas;
	}

	public void setDistribuicaoCotas(boolean distribuicaoCotas) {
		this.distribuicaoCotas = distribuicaoCotas;
	}

	public CategoriaProjetoPesquisa getCategoria() {
		return categoria;
	}

	public void setCategoria(CategoriaProjetoPesquisa categoria) {
		this.categoria = categoria;
	}

	public Collection<Cotas> getCotas() {
		return cotas;
	}

	public void setCotas(Collection<Cotas> cotas) {
		this.cotas = cotas;
	}

	/** Adiciona ao edital de pesquisa um cota */
	public void addCotas(Cotas cotas){
		cotas.setEditalPesquisa(this);
		this.cotas.add(cotas);
	}
	
	/**
	 * Retorna a quantidade de bolsas distribu�das pelo edital aos docentes
	 * @return
	 */
	public Map<Integer, Integer> getTotalBolsasDistribuidas(){
		Map<Integer, Integer> mapaBolsas = new HashMap<Integer, Integer>();
		if( cotasDocentes != null ){
			for (CotaDocente cD: cotasDocentes) {
				for(Cotas c: cD.getCotas()){
					int idTipoBolsa = c.getTipoBolsa().getId();
					if(mapaBolsas.get(idTipoBolsa) == null)
						mapaBolsas.put(idTipoBolsa, c.getQuantidade());
					else
						mapaBolsas.put(idTipoBolsa, mapaBolsas.get(idTipoBolsa) + c.getQuantidade());
				}
			}
		}
		return mapaBolsas;
	}

	public Boolean getVoluntario() {
		return voluntario;
	}

	public void setVoluntario(Boolean voluntario) {
		this.voluntario = voluntario;
	}

    public Boolean getResultadoDivulgado() {
        return resultadoDivulgado;
    }

    public void setResultadoDivulgado(Boolean resultadoDivulgado) {
        this.resultadoDivulgado = resultadoDivulgado;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Edital getEdital() {
		return edital;
	}

	public void setEdital(Edital edital) {
		this.edital = edital;
	}
	
	public String getDescricao() {
		return edital.getDescricao();
	}

	/** Seta a descri��o do edital de Pesquisa */
	public void setDescricao(String descricao) {
		edital.setDescricao(descricao);
	}
	
	public Integer getIdArquivo() {
		return edital.getIdArquivo();
	}

	/** Seta o chave do arquivo do edital de Pesquisa */
	public void setIdArquivo(Integer idArquivo) {
		edital.setIdArquivo(idArquivo);
	}

	public char getTipo() {
		return edital.getTipo();
	}

	/** Seta o tipo de projeto em quest�o 
	 * Ex.: M - Monitoria (Ensino)
	 * 		I - Inova��o (Ensino)
	 * 		P - Pesquisa		
	 * 		E - Extens�o
	 * 		A - Associados
	 */
	public void setTipo(char tipo) {
		edital.setTipo(tipo);
	}
	
	public Date getInicioSubmissao() {
		return edital.getInicioSubmissao();
	}

	/** Seta o inicio da Submiss�o no edital */
	public void setInicioSubmissao(Date inicioSubmissao) {
		edital.setInicioSubmissao(inicioSubmissao);
	}
	
	public Date getFimSubmissao() {
		return edital.getFimSubmissao();
	}

	/** Seta o fim da submiss�o no edital */
	public void setFimSubmissao(Date fimSubmissao) {
		edital.setFimSubmissao(fimSubmissao);
	}

	public Date getDataCadastro() {
		return edital.getDataCadastro();
	}

	/** Seta a data de cadastro no Edital */
	public void setDataCadastro(Date dataCadastro) {
		edital.setDataCadastro(dataCadastro);
	}
	
	public Usuario getUsuario() {
		return edital.getUsuario();
	}

	/** Seta o usu�rio no edital */
	public void setUsuario(Usuario usuario) {
		edital.setUsuario(usuario);
	}
	
	public String getTipoString(){
		return edital.getTipoString();
	}
	
	public Integer getAno() {
		return edital.getAno();
	}

	/** Seta o ano do edital */
	public void setAno(int ano) {
		edital.setAno(ano);
	}

	public int getSemestre() {
		return edital.getSemestre();
	}

	/** Seta o semestre no edital */
	public void setSemestre(int semestre) {
		edital.setSemestre(semestre);
	}

	public boolean isAtivo() {
		return edital.isAtivo();
	}

	/** Seta se o campo ativo no edital */
	public void setAtivo(boolean ativo) {
		edital.setAtivo(ativo);
	}
	
	/** Retorna uma representa��o textual no formato: descri��o, seguido do tipo entre par�ntese. */
	public String toString() {
		return getDescricao() + " (" + getTipoString() + ")";
	}

	public ListaMensagens validate() {
		return null;
	}
	
	public boolean isAssociado() {
		return edital.getTipo() == Edital.ASSOCIADO;
	}

	public Boolean getAvaliacaoVigente() {
		return avaliacaoVigente;
	}

	public void setAvaliacaoVigente(Boolean avaliacaoVigente) {
		this.avaliacaoVigente = avaliacaoVigente;
	}

	public boolean isApenasCoordenadorOrientaPlano() {
		return apenasCoordenadorOrientaPlano;
	}

	public void setApenasCoordenadorOrientaPlano(
			boolean apenasCoordenadorOrientaPlano) {
		this.apenasCoordenadorOrientaPlano = apenasCoordenadorOrientaPlano;
	}

	public IndiceAcademico getIndiceChecagem() {
		return indiceChecagem;
	}

	public void setIndiceChecagem(IndiceAcademico indiceChecagem) {
		this.indiceChecagem = indiceChecagem;
	}

	public Double getValorMinimoIndiceChecagem() {
		return valorMinimoIndiceChecagem;
	}

	public void setValorMinimoIndiceChecagem(Double valorMinimoIndiceChecagem) {
		this.valorMinimoIndiceChecagem = valorMinimoIndiceChecagem;
	}

	public boolean isApenasColaboradorVoluntarioCadastraProjeto() {
		return apenasColaboradorVoluntarioCadastraProjeto;
	}

	public void setApenasColaboradorVoluntarioCadastraProjeto(
			boolean apenasColaboradorVoluntarioCadastraProjeto) {
		this.apenasColaboradorVoluntarioCadastraProjeto = apenasColaboradorVoluntarioCadastraProjeto;
	}

	public boolean isProfessorSubstitutoCadastraProjeto() {
		return professorSubstitutoCadastraProjeto;
	}

	public void setProfessorSubstitutoCadastraProjeto(
			boolean professorSubstitutoCadastraProjeto) {
		this.professorSubstitutoCadastraProjeto = professorSubstitutoCadastraProjeto;
	}

	public Date getInicioExecucaoProjetos() {
		return inicioExecucaoProjetos;
	}

	public void setInicioExecucaoProjetos(Date inicioExecucaoProjetos) {
		this.inicioExecucaoProjetos = inicioExecucaoProjetos;
	}

	public Date getFimExecucaoProjetos() {
		return fimExecucaoProjetos;
	}

	public void setFimExecucaoProjetos(Date fimExecucaoProjetos) {
		this.fimExecucaoProjetos = fimExecucaoProjetos;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
}