/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 30/04/2008
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

import java.util.Comparator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Entidade que representa um fiscal de aplicação de prova num {@link ProcessoSeletivoVestibular processo seletivo do tipo vestibular}.
 *
 * @author Édipo
 *
 */
@Entity
@Table(name = "fiscal", schema = "vestibular", uniqueConstraints = {})
public class Fiscal implements PersistDB {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_fiscal", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Frequência de trabalho do fiscal. */
	private int frequencia;
	
	/** Conceito do fiscal. */
	private Character conceito;
	
	/** Indica se o fiscal é reserva. */
	private Boolean reserva;
	
	/** Indica se o fiscal é novato. */
	private Boolean novato;
	
	/** Indica se o fiscal justificou a falta. */
	private Boolean justificou;
	
	/** Observações acerca do Fiscal. */
	private String observacao;
	
	/** Indica se o fiscal é do tipo "Recadastro". */
	private Boolean recadastro;
	
	/** Indica se o fiscal estava presente na reunião com o coordenador. */
	@Column(name = "presente_reuniao")
	private Boolean presenteReuniao;
	
	/** Indica se o fiscal esteve presente todos dias de aplicação. */
	@Column(name = "presente_aplicacao")
	private Boolean presenteAplicacao;
	
	
	/** Inscrição associada a este fiscal. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_inscricao_fiscal",nullable=false)
	private InscricaoFiscal inscricaoFiscal;
	
	
	/** Local de prova onde o fiscal está alocado. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_local_aplicacao_prova")
	private LocalAplicacaoProva localAplicacaoProva;
	
	
	/** Processo seletivo para o qual o fiscal trabalha. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_processo_seletivo")
	private ProcessoSeletivoVestibular processoSeletivoVestibular;
	
	/** Dados pessoais do fiscal. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa")
	private Pessoa pessoa;

	/** Dados do perfil de servidor do fiscal. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_servidor")
	private Servidor servidor;

	/** Dados do perfil de discente do fiscal. */
	@ManyToOne(fetch = FetchType.LAZY, targetEntity=Discente.class)
	@JoinColumn(name = "id_discente")
	private DiscenteAdapter discente;
	
	
	/** Comparador utilizado quando se deseja ordenar uma lista de fiscais por Município / Bairro. */
	@Transient
	public static final Comparator<Fiscal> enderecoComparator = new Comparator<Fiscal>(){
		public int compare(Fiscal o1, Fiscal o2) {
			Endereco e1 = o1.getPessoa().getEnderecoContato();
			Endereco e2 = o2.getPessoa().getEnderecoContato();
			// compara-se os municípios
			if (e1 == null && e2 == null) return 0;
			else if (e1 == null) return -1;
			else if (e2 == null) return 1;
			else {
				String m1 = e1.getMunicipio().getNome() == null ? e1.getMunicipioOutro() : e1.getMunicipio().getNome();
				String m2 = e2.getMunicipio().getNome() == null ? e2.getMunicipioOutro() : e2.getMunicipio().getNome();
				if (m1 == null && m2 == null) return 0;
				else if (m1 == null) return -1;
				else if (m2 == null) return 1;
				else if (m1.compareTo(m2) == 0) {
					// municípios iguais, compara-se os bairros
					String b1 = e1.getBairro();
					String b2 = e2.getBairro();
					if (b1 == null && b2 == null) return 0;
					else if (b1 == null) return -1;
					else if (b2 == null) return 1;
					else return b1.compareTo(b2);
				} else return m1.compareTo(m2);
			}
		}
	};

	/** Construtor padrão. */
	public Fiscal() {
		conceito = ConceitoFiscal.SUFICIENTE;
		this.reserva = true;
		this.novato = true;
		this.recadastro = false;
		this.presenteAplicacao = true;
		this.presenteReuniao = true;
	}

	/** Construtor que copia os dados da inscrição do fiscal. 
	 * @param inscricaoFiscal
	 */
	public Fiscal(InscricaoFiscal inscricaoFiscal) {
		this();
		this.processoSeletivoVestibular = inscricaoFiscal
				.getProcessoSeletivoVestibular();
		this.discente = inscricaoFiscal.getDiscente();
		this.pessoa = inscricaoFiscal.getPessoa();
		this.servidor = inscricaoFiscal.getServidor();
		this.inscricaoFiscal = inscricaoFiscal;
		this.recadastro = inscricaoFiscal.isRecadastro();
		this.novato = inscricaoFiscal.isNovato();
	}

	/** Construtor parametrizado.
	 * @param idFiscal
	 * @param idProcessoSeletivo
	 * @param reserva
	 * @param novato
	 * @param recadastro
	 * @param disponibilidadeOutrasCidades
	 * @param nome
	 * @param curso
	 * @param nivel
	 * @param ira
	 */
	public Fiscal(int idFiscal, int idProcessoSeletivo, boolean reserva,
			boolean novato, boolean recadastro,
			boolean disponibilidadeOutrasCidades, String nome, String curso,
			char nivel, float ira) {
		this();
		this.id = idFiscal;
		this.reserva = reserva;
		this.novato = novato;
		this.recadastro = recadastro;
		this.inscricaoFiscal = new InscricaoFiscal();
		this.inscricaoFiscal
				.setDisponibilidadeOutrasCidades(disponibilidadeOutrasCidades);
		this.pessoa = new Pessoa();
		this.pessoa.setNome(nome);
		if (nivel == NivelEnsino.GRADUACAO) {
			DiscenteGraduacao d = new DiscenteGraduacao();
			d.setIra(ira);
			this.setDiscente(d);
		} else if (nivel == NivelEnsino.STRICTO
				|| nivel == NivelEnsino.DOUTORADO
				|| nivel == NivelEnsino.MESTRADO) {
			DiscenteStricto d = new DiscenteStricto();
			d.setMediaGeral(ira);
			this.setDiscente(d);
		}
		Curso c = new Curso();
		c.setNome(curso);
		this.discente.setCurso(c);
		this.discente.setNivel(nivel);
	}

	/** Construtor parametrizado.
	 * @param idFiscal
	 * @param idProcessoSeletivo
	 * @param reserva
	 * @param novato
	 * @param recadastro
	 * @param disponibilidadeOutrasCidades
	 * @param nome
	 * @param unidade
	 */
	public Fiscal(int idFiscal, int idProcessoSeletivo, boolean reserva,
			boolean novato, boolean recadastro,
			boolean disponibilidadeOutrasCidades, String nome, String unidade) {
		this();
		// this.id = idFiscal;
		this.reserva = reserva;
		this.novato = novato;
		this.recadastro = recadastro;
		this.inscricaoFiscal = new InscricaoFiscal();
		this.inscricaoFiscal
				.setDisponibilidadeOutrasCidades(disponibilidadeOutrasCidades);
		this.pessoa = new Pessoa();
		this.pessoa.setNome(nome);
		Unidade u = new Unidade();
		u.setNome(unidade);
		this.servidor = new Servidor();
		this.servidor.setUnidade(u);
	}

	/**
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/**
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna a frequência de trabalho do fiscal. 
	 * @return
	 */
	public int getFrequencia() {
		return frequencia;
	}

	/** Seta a frequência de trabalho do fiscal. 
	 * @param frequencia
	 */
	public void setFrequencia(int frequencia) {
		this.frequencia = frequencia;
	}

	/** Retorna o conceito do fiscal. 
	 * @return
	 */
	public Character getConceito() {
		return conceito;
	}
	
	/** Retorna a descrição do conceito do fiscal.
	 * @return
	 */
	public String getDescricaoConceito() {
		return ConceitoFiscal.descricaoConceitos.get(conceito);
	}

	/** Seta o conceito do fiscal. 
	 * @param conceito
	 */
	public void setConceito(Character conceito) {
		this.conceito = conceito;
	}

	/** Retorna observações acerca do Fiscal. 
	 * @return
	 */
	public String getObservacao() {
		return observacao;
	}

	/** Seta observações acerca do Fiscal. 
	 * @param observacao
	 */
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	/** Concatena uma observação à observações existentes.
	 * @param observacao
	 */
	public void addObservacao(String observacao) {
		if (this.observacao == null)
			this.observacao = observacao;
		else
			this.observacao += " " + observacao;
	}

	/** Retorna a inscrição associada a este fiscal. 
	 * @return
	 */
	public InscricaoFiscal getInscricaoFiscal() {
		return inscricaoFiscal;
	}

	/** Seta a inscrição associada a este fiscal. 
	 * @param inscricaoFiscal
	 */
	public void setInscricaoFiscal(InscricaoFiscal inscricaoFiscal) {
		this.inscricaoFiscal = inscricaoFiscal;
	}

	/** Retorna o local de prova onde o fiscal está alocado. 
	 * @return
	 */
	public LocalAplicacaoProva getLocalAplicacaoProva() {
		return localAplicacaoProva;
	}

	/** Seta o local de prova onde o fiscal está alocado. 
	 * @param localAplicacaoProva
	 */
	public void setLocalAplicacaoProva(LocalAplicacaoProva localAplicacaoProva) {
		this.localAplicacaoProva = localAplicacaoProva;
	}

	/** Retorna o processo seletivo para o qual o fiscal trabalha.  
	 * @return
	 */
	public ProcessoSeletivoVestibular getProcessoSeletivoVestibular() {
		return processoSeletivoVestibular;
	}

	/** Seta o processo seletivo para o qual o fiscal trabalha. 
	 * @param processoSeletivoVestibular
	 */
	public void setProcessoSeletivoVestibular(
			ProcessoSeletivoVestibular processoSeletivoVestibular) {
		this.processoSeletivoVestibular = processoSeletivoVestibular;
	}

	/** Retorna os dados pessoais do fiscal.  
	 * @return
	 */
	public Pessoa getPessoa() {
		return pessoa;
	}

	/** Seta os dados pessoais do fiscal.
	 * @param pessoa
	 */
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	/** Retorna os dados do perfil de servidor do fiscal. 
	 * @return
	 */
	public Servidor getServidor() {
		return servidor;
	}

	/** Seta os dados do perfil de servidor do fiscal.
	 * @param servidor
	 */
	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	/** Retorna os dados do perfil de discente do fiscal. 
	 * @return
	 */
	public DiscenteAdapter getDiscente() {
		return discente;
	}

	/** Retorna os dados do perfil de discente do fiscal.
	 * @param discente
	 */
	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	/** Indica se o fiscal é reserva. 
	 * @return
	 */
	public Boolean getReserva() {
		return reserva;
	}

	/** Seta se o fiscal é reserva. 
	 * @param reserva
	 */
	public void setReserva(Boolean reserva) {
		this.reserva = reserva;
	}

	/** Indica se o fiscal é novato. 
	 * @return
	 */
	public Boolean getNovato() {
		return novato;
	}

	/** Seta se o fiscal é novato. 
	 * @param novato
	 */
	public void setNovato(Boolean novato) {
		this.novato = novato;
	}

	/** Indica se o fiscal justificou a falta. 
	 * @return
	 */
	public Boolean getJustificou() {
		return justificou;
	}

	/** Seta se o fiscal justificou a falta. 
	 * @param justificou
	 */
	public void setJustificou(Boolean justificou) {
		this.justificou = justificou;
	}

	/** Indica se o fiscal é do tipo "Recadastro". 
	 * @return
	 */
	public Boolean getRecadastro() {
		return recadastro;
	}

	/** Seta se o fiscal é do tipo "Recadastro". 
	 * @param recadastro
	 */
	public void setRecadastro(Boolean recadastro) {
		this.recadastro = recadastro;
	}

	/** Indica se o fiscal estava presente na reunião com o coordenador. 
	 * @return
	 */
	public Boolean getPresenteReuniao() {
		return presenteReuniao;
	}

	/** Seta se o fiscal estava presente na reunião com o coordenador. 
	 * @param presenteReuniao
	 */
	public void setPresenteReuniao(Boolean presenteReuniao) {
		this.presenteReuniao = presenteReuniao;
	}

	/** Indica se o fiscal esteve presente todos dias de aplicação. 
	 * @return
	 */
	public Boolean getPresenteAplicacao() {
		return presenteAplicacao;
	}

	/** Seta se o fiscal esteve presente todos dias de aplicação. 
	 * @param presenteAplicacao
	 */
	public void setPresenteAplicacao(Boolean presenteAplicacao) {
		this.presenteAplicacao = presenteAplicacao;
	}
	
	/** Indica se o fiscal é discente
	 * @return
	 */
	public boolean isPerfilDiscente() {
		if (this.discente != null) return true;
		else return false;
	}
	
	/** Indica se o fiscal é servidor
	 * @return
	 */
	public boolean isPerfilServidor() {
		if (this.servidor != null) return true;
		else return false;
	}
	
	/**
	 * Retorna a ordem da preferência na lista de locais de prova optados. Caso
	 * não esteja na lista, retorna 0.
	 * @param idLocalAplicacaoProva
	 * @return Ordem do local de prova (1, 2, 3...) ou 0, caso não tenha optado pelo local especificado. 
	 */
	public int getOrdemPreferencia(int idLocalAplicacaoProva) {
		int i = 0;
		for (LocalAplicacaoProva local : getInscricaoFiscal()
				.getLocalAplicacaoProvas()) {
			i++;
			if (local.getId() == idLocalAplicacaoProva)
				break;
		}
		return i;
	}
	
	/** Retorna uma representação textual deste objeto no formato:
	 * ID, seguido de vírgula, seguido do nome do fiscal, seguido de vírgula,
	 * seguido do perfil (DISCENTE, SERVIDOR). 
	 */
	@Override
	public String toString() {
		return getId() + ", " + getPessoa().getNome() + ", "
				+ (getDiscente() == null ? "SERVIDOR" : "DISCENTE");
	}

}
