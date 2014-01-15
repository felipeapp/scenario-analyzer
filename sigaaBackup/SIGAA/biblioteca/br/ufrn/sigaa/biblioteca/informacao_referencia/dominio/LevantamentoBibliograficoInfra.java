/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 08/10/2008
 * 
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.dominio;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CollectionId;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Entidade que representa a solicitação de um Levantamento Bibliográfico e de Infra-Estrutura.
 * 
 * @author agostinho
 * @author Fred_Castro
 */

@Entity
@Table(name="levantamento_bibliografico_infra", schema = "biblioteca")
public class LevantamentoBibliograficoInfra implements Validatable {

	// CONSTANTES USANDO MESMO ID DO BANCO
	private final static int ESPANHOL = 1;
	private final static int PORTUGUES = 2;
	private final static int INGLES = 3;
	
	public final static int SITUACAO_AGUARDANDO_VALIDACAO = 1;
	public final static int SITUACAO_VALIDADA = 2;
	public final static int SITUACAO_FINALIZADA = 3;
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_levantamento_bibliografico_infra")
	private int id;
	
	@CriadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_solicitacao")
	/** Data em que a solicitação foi criada. */
	private Date dataSolicitacao;
	
	@ManyToOne
	@JoinColumn(name = "id_pessoa")
	/** Pessoa solicitante */
	private Pessoa pessoa;
	
	@Column(name="detalhes_assunto")
	private String detalhesAssunto;
	
	/** A situação atual da solicitação. Inicia em "Aguardando Validação" */
	private int situacao = SITUACAO_AGUARDANDO_VALIDACAO;
	
	@ManyToMany(cascade=CascadeType.ALL)
	@CollectionId(
		columns = @Column(name = "id_linguas_levant_bibliografico"),
		type = @org.hibernate.annotations.Type(type = "long"),
		generator = "sequence"
	)
	@JoinTable(name="biblioteca.linguas_levant_bibliografico",
	joinColumns=@JoinColumn(name="id_levantamento_bibliografico_infra"),
	inverseJoinColumns=@JoinColumn(name="id_lingua"))
	private List <Linguas> linguasSelecionadas;
	
	/**
	 * Pode assumir os valores:
	 * Ano em curso, Indiferente, Outros
	 */
	private Integer periodo = PeriodoLevantBibliografico.ANO_EM_CURSO;
	
	@Column(name="fontes_pesquisadas")
	private String fontesPesquisadas;
	
	private String observacao;

	private boolean infra;
	
	@Column(name = "id_arquivo")
	/** Guarda o id do arquivo enviado na finalização da solicitação. */
	private Integer idArquivo;

	@Column(name="outra_lingua_descricao")
	private String outraLinguaDescricao;
	
	@Column(name="outro_periodo_descricao")
	private String outroPeriodoDescricao;
	
	@ManyToOne
	@JoinColumn(name="id_biblioteca_responsavel", referencedColumnName="id_biblioteca")
	/** Biblioteca responsável */
	private Biblioteca bibliotecaResponsavel = new Biblioteca();
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_validacao")
	/** Data em que a solicitação foi validada. */
	private Date dataValidacao;
	
	@ManyToOne
	@JoinColumn(name="id_registro_entrada_validacao")
	/** Registro entrada de quem validou. */
	private RegistroEntrada registroEntradaValidacao;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_finalizacao")
	/** Data em que a solicitação foi finalizada. */
	private Date dataFinalizacao;
	
	@ManyToOne
	@JoinColumn(name="id_registro_entrada_finalizacao")
	/** Registro entrada de quem finalizou. */
	private RegistroEntrada registroEntradaFinalizacao;
	
	
	/*
	 * GETS/SETS
	 */
	
	public LevantamentoBibliograficoInfra() {
	}

	public boolean isInfra() {
		return infra;
	}

	public void setInfra(boolean infra) {
		this.infra = infra;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDetalhesAssunto() {
		return detalhesAssunto;
	}

	public void setDetalhesAssunto(String detalhesAssunto) {
		this.detalhesAssunto = detalhesAssunto;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public String getFontesPesquisadas() {
		return fontesPesquisadas;
	}

	public void setFontesPesquisadas(String fontesPesquisadas) {
		this.fontesPesquisadas = fontesPesquisadas;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}

	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public List<Linguas> getLinguasSelecionadas() {
		return linguasSelecionadas;
	}

	public void setLinguasSelecionadas(List<Linguas> linguasSelecionadas) {
		this.linguasSelecionadas = linguasSelecionadas;
	}

	public Integer getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	public String getOutraLinguaDescricao() {
		return outraLinguaDescricao;
	}

	public void setOutraLinguaDescricao(String outraLinguaDescricao) {
		this.outraLinguaDescricao = outraLinguaDescricao;
	}

	public String getOutroPeriodoDescricao() {
		return outroPeriodoDescricao;
	}

	public void setOutroPeriodoDescricao(String outroPeriodoDescricao) {
		this.outroPeriodoDescricao = outroPeriodoDescricao;
	}
	
	public Biblioteca getBibliotecaResponsavel() {
		return bibliotecaResponsavel;
	}

	public void setBibliotecaResponsavel(Biblioteca bibliotecaResponsavel) {
		this.bibliotecaResponsavel = bibliotecaResponsavel;
	}

	public int getSituacao() {
		return situacao;
	}

	public void setSituacao(int situacao) {
		this.situacao = situacao;
	}
		
	public Date getDataValidacao() {
		return dataValidacao;
	}

	public void setDataValidacao(Date dataValidacao) {
		this.dataValidacao = dataValidacao;
	}

	public RegistroEntrada getRegistroEntradaValidacao() {
		return registroEntradaValidacao;
	}

	public void setRegistroEntradaValidacao(RegistroEntrada registroEntradaValidacao) {
		this.registroEntradaValidacao = registroEntradaValidacao;
	}

	public Date getDataFinalizacao() {
		return dataFinalizacao;
	}

	public void setDataFinalizacao(Date dataFinalizacao) {
		this.dataFinalizacao = dataFinalizacao;
	}

	public RegistroEntrada getRegistroEntradaFinalizacao() {
		return registroEntradaFinalizacao;
	}

	public void setRegistroEntradaFinalizacao(RegistroEntrada registroEntradaFinalizacao) {
		this.registroEntradaFinalizacao = registroEntradaFinalizacao;
	}

	/**
	 * Retorna a descrição da situação da solicitação;
	 * @return
	 */
	public String getDescricaoSituacao(){
		switch (situacao){
			case SITUACAO_AGUARDANDO_VALIDACAO: return "Aguardando validação";
			case SITUACAO_VALIDADA: return "Validada";
			case SITUACAO_FINALIZADA: return "Finalizada";
		}
		
		return "";
	}

	public String getDescricaoLinguasSelecionadas(){
		String lingua = "";
		for (Linguas it : linguasSelecionadas) {
			if (it.getId() == PORTUGUES)
				lingua += "Português, ";
			if (it.getId() == INGLES)
				lingua += "Inglês, ";
			if (it.getId() == ESPANHOL)
				lingua += "Espanhol, ";
		}
		return lingua;
	}

	/**
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {

		ListaMensagens ms = new ListaMensagens();
		
		if (bibliotecaResponsavel == null || bibliotecaResponsavel.getId() == 0)
			ms.addErro("Selecione uma biblioteca.");

		if (StringUtils.isEmpty(detalhesAssunto))
			ms.addErro("É necessário informar detalhes do seu assunto!");
		
		// Verifica se "Outras" foi selecionado
		boolean outroIdioma = false;
		for (Linguas l : linguasSelecionadas)
			if (l.getId() == Linguas.OUTRAS)
				outroIdioma = true;
		
		if ( outroIdioma ) {
			if (StringUtils.isEmpty(outraLinguaDescricao))
				ms.addErro("Informe quais outras línguas são aceitas.");
		}
		
		if ( linguasSelecionadas.isEmpty() )
			ms.addErro("Selecione pelo menos uma língua.");
		
		
		boolean outroPeriodo = false;
		if (periodo == PeriodoLevantBibliografico.OUTROS)
			outroPeriodo = true;
		
		if (outroPeriodo){
			if (StringUtils.isEmpty(outroPeriodoDescricao))
				ms.addErro("Informe quais outros períodos são aceitos.");
		} else if (!StringUtils.isEmpty(outroPeriodoDescricao))
			ms.addErro("A opção \"Outros\" deve ser selecionada caso deseje informar outros períodos!");
		
		return ms;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
}