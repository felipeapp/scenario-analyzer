/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/10/2006
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.PessoaJuridica;

/*******************************************************************************
 * <p>
 * Classe utilizada no contexto de PrestacaoServicos. <br>
 * Realiza��o de trabalho oferecido ou contratado por terceiros (comunidade ou
 * empresa), incluindo assessorias, consultorias e coopera��o
 * interinstitucional. <br/>
 * 
 * A presta��o de servi�os se caracteriza pela intangibilidade (o produto n�o
 * pode ser visto, tocado ou provado a priori), inseparabilidade (produzido e
 * utilizado ao mesmo tempo) e n�o resulta na posse de um bem. Deve ser
 * registrada a presta��o de servi�os institucionais realizada pelos hospitais,
 * cl�nicas, laborat�rios, hospitais veterin�rios, centros de psicologia, museus
 * e n�cleos de acervos universit�rios, dentre outros, seja de car�ter
 * permanente ou eventual. Quando a presta��o de servi�o for oferecida como
 * curso ou projeto de extens�o, deve ser registrada como tal (curso ou
 * projeto). <br/>
 * 
 * Os registros de presta��o de servi�os poder�o ter a classifica��o detalhada,
 * a crit�rio de cada universidade - por exemplo: consultoria, assessoria,
 * contrato, etc. <br/>
 * 
 * O m�dulo de presta��o de servi�os ainda n�o est� totalmente definido no
 * SIGAA.
 * </p>
 * 
 * @author Gleydson
 * @author Victor Hugo
 * @author Ilueny Santos
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "prestacao_servico")
public class PrestacaoServico implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_prestacao_servico", nullable = false)
	private int id;

	@ManyToOne
	@JoinColumn(name = "id_pj_contratante")
	private PessoaJuridica pessoaJuridicaContratante = new PessoaJuridica();

	@ManyToOne
	@JoinColumn(name = "id_pf_contratante")
	private Pessoa pessoaFisicaContratante = new Pessoa();

	@ManyToOne
	@JoinColumn(name = "id_orgao_interveniente")
	private PessoaJuridica orgaoInterveniente = new PessoaJuridica();

	@Column(name = "metas")
	private String metas;

	@OneToMany(mappedBy = "prestacaoServico")
	private java.util.Collection<MembroPrestacaoServico> membrosPrestacaoServico;

	@ManyToOne
	@JoinColumn(name = "id_detalhe_prestacao_servico", referencedColumnName = "id_detalhe_prest_servico")
	private DetalhePrestServico detalhePrestacaoServico;

	@ManyToOne
	@JoinColumn(name = "id_fator_gerador")
	private FatorGerador fatorGerador = new FatorGerador();

	@ManyToOne
	@JoinColumn(name = "id_forma_compromisso")
	private FormaCompromisso formaCompromisso = new FormaCompromisso();

	@ManyToOne
	@JoinColumn(name = "id_natureza_servico")
	private NaturezaServico naturezaServico = new NaturezaServico();

	@Column(name = "instituicoes_envolvidas")
	private String instituicoesEnvolvidas;

	@Column(name = "atividade_profissional_contratante")
	private String atividadeProfissionalContratante;

	/** Creates a new instance of PrestacaoServico */
	public PrestacaoServico() {
	}

	public PrestacaoServico(int id) {
		this.setId(id);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getInstituicoesEnvolvidas() {
		return instituicoesEnvolvidas;
	}

	public void setInstituicoesEnvolvidas(String instituicoesEnvolvidas) {
		this.instituicoesEnvolvidas = instituicoesEnvolvidas;
	}

	public PessoaJuridica getOrgaoInterveniente() {
		return orgaoInterveniente;
	}

	public void setOrgaoInterveniente(PessoaJuridica orgaoInterveniente) {
		this.orgaoInterveniente = orgaoInterveniente;
	}

	public Pessoa getPessoaFisicaContratante() {
		return pessoaFisicaContratante;
	}

	public void setPessoaFisicaContratante(Pessoa pessoaFisicaContratante) {
		this.pessoaFisicaContratante = pessoaFisicaContratante;
	}

	public PessoaJuridica getPessoaJuridicaContratante() {
		return pessoaJuridicaContratante;
	}

	public void setPessoaJuridicaContratante(
			PessoaJuridica pessoaJuridicaContratante) {
		this.pessoaJuridicaContratante = pessoaJuridicaContratante;
	}

	public Pessoa getContratante() {
		return (Pessoa) (pessoaJuridicaContratante == null ? pessoaFisicaContratante : pessoaJuridicaContratante);
	}

	public String getMetas() {
		return this.metas;
	}

	public void setMetas(String metas) {
		this.metas = metas;
	}

	public DetalhePrestServico getDetalhePrestacaoServico() {
		return this.detalhePrestacaoServico;
	}

	public void setDetalhePrestacaoServico(
			DetalhePrestServico detalhePrestacaoServico) {
		this.detalhePrestacaoServico = detalhePrestacaoServico;
	}

	public FatorGerador getFatorGerador() {
		return this.fatorGerador;
	}

	public void setFatorGerador(FatorGerador fatorGerador) {
		this.fatorGerador = fatorGerador;
	}

	public FormaCompromisso getFormaCompromisso() {
		return this.formaCompromisso;
	}

	public void setFormaCompromisso(FormaCompromisso formaCompromisso) {
		this.formaCompromisso = formaCompromisso;
	}

	public NaturezaServico getNaturezaServico() {
		return this.naturezaServico;
	}

	public void setNaturezaServico(NaturezaServico naturezaServico) {
		this.naturezaServico = naturezaServico;
	}

	public String toString() {
		return "br.ufrn.sigaa.extensao.dominio.PrestacaoServico[id=" + this.getId() + "]";
	}

	public ListaMensagens validate() {
		return null;
	}

	public String getAtividadeProfissionalContratante() {
		return atividadeProfissionalContratante;
	}

	public void setAtividadeProfissionalContratante(String atividadeProfissionalContratante) {
		this.atividadeProfissionalContratante = atividadeProfissionalContratante;
	}

	public java.util.Collection<MembroPrestacaoServico> getMembrosPrestacaoServico() {
		return membrosPrestacaoServico;
	}

	public void setMembrosPrestacaoServico(java.util.Collection<MembroPrestacaoServico> membrosPrestacaoServico) {
		this.membrosPrestacaoServico = membrosPrestacaoServico;
	}

}
