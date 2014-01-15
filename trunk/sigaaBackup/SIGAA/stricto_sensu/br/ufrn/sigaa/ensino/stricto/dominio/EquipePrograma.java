/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 26/11/2007
 *
 */
package br.ufrn.sigaa.ensino.stricto.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;


/**
 * Entidade que armazena a equipe dos docentes do programa de p�s-gradua��o.
 * Representa o vinculo entre um docente e um programa de p�s, a entidade tamb�m
 * armazena a unidade do programa, al�m da �rea de concentra��o, essas s�o as �reas 
 * que o programa faz parte.
 * 
 * @author Gleydson
 * @author Victor Hugo
 */
@Entity
@Table(name = "equipe_programa", schema = "stricto_sensu")
public class EquipePrograma implements Validatable, Comparable<EquipePrograma> {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_equipe_programa", nullable = false)
	private int id;

	/** Programa de p�s */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_programa")
	private Unidade programa;

	/** Tipo de v�nculo do membro: professor, pesquisador */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_vinculo_equipe")
	private VinculoEquipe vinculo;

	/** N�vel do membro no programa: pleno, colaborador */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_nivel_equipe")
	private NivelEquipe nivel;

	/** Servidor membro do programa, se o docente desta associa��o for um docente externo este atributo ser� nulo */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_servidor")
	private Servidor servidor;

	/** Docente externo membro do programa, se o docente desta associa��o for um servidor da UFRN este atributo ser� nulo */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_docente_externo")
	private DocenteExterno docenteExterno;

	private boolean ativo = true;
	
	/** �rea de concentra��o principal do docente no programa */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_area_concentracao_principal")
	private AreaConcentracao areaConcentracaoPrincipal;

	/**
	 * Registro de entrada do cadastro desta entidade
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;

	/** Data de cadastro */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;
	
	/** Registro da �ltima atualiza��o */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/** Data da �ltima atualiza��o */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;

	/** Estes campos indicam o n�mero m�ximo de orientandos que cada docente pode ter no programa de p�s
	 * se o valor for zero indica que n�o pode ter nenhum.
	 * se o valor for nulo indica que n�o tem limite */
	/**
	 * m�ximo de bolsistas de mestrado que o docente pode ter neste programa
	 */
	
	/** M�ximo de Orientantando Regular por Docente que o docente pode ter neste programa*/
	private Integer maxOrientandoRegularMestrado;
	
	private Integer maxOrientandoRegularDoutorado;

	/** M�ximo de Orientando Especial por Docente que o docente pode ter neste programa*/
	private Integer maxOrientandoEspecialMestrado;
	
	private Integer maxOrientandoEspecialDoutorado;

	/** M�ximo de bolsistas de doutorado que o docente pode ter neste programa */
	private Integer maxBolsistasMestrado;

	/** M�ximo de bolsistas de doutorado que o docente pode ter neste programa */
	private Integer maxBolsistasDoutorado;
	
	/** M�ximo de orientandos de mestrado que o docente pode ter neste programa */
	private Integer maxOrientadosMestrado;

	/** M�ximo de orientandos de doutorado que o docente pode ter neste programa */
	private Integer maxOrientadosDoutorado;

	/**
	 * Esses campos indicam em que n�vel de ensino o docente est� vinculado (mestrado e/ou doutorado)
	 */
	/** Indica se o docente � professor do mestrado */
	private boolean mestrado;
	
	/** indica se o docente � professor do doutorado */
	private boolean doutorado;

	@ManyToMany
	@JoinTable(name="stricto_sensu.equipe_linha", 
			joinColumns={@JoinColumn(name="id_equipe_programa")}, 
			inverseJoinColumns={@JoinColumn(name="id_linha_pesquisa")})
	private Set<LinhaPesquisaStricto> linhasPesquisa;
	
	/**
	 * Estes atributos indicam o total de bolsistas e orientandos de mestrado e doutorado que o docente tem no programa de p�s,
	 * � um atributo transiente calculado de acordo com o n�vel de ensino.
	 */
	@Transient
	private Integer totalBolsistasMestrado;

	@Transient
	private Integer totalBolsistasDoutorado;

	@Transient
	private Integer totalOrientadosMestrado;

	@Transient
	private Integer totalOrientadosDoutorado;

	public EquipePrograma() {
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public VinculoEquipe getVinculo() {
		return vinculo;
	}

	public void setVinculo(VinculoEquipe vinculo) {
		this.vinculo = vinculo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Unidade getPrograma() {
		return programa;
	}

	public void setPrograma(Unidade programa) {
		this.programa = programa;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public String getNome() {
		return servidor == null ? docenteExterno.getNome() : servidor.getNome();
	}

	public String getMatriculaNome() {
		return servidor == null ? getNome() + " - " + getPessoa().getCpfCnpjFormatado()  : getNome() + " - " + servidor.getSiape();
	}

	public String getCpf() {
		return servidor == null ? docenteExterno.getPessoa().getCpfCnpjFormatado() : servidor.getPessoa().getCpfCnpjFormatado();
	}

	public String getCategoria() {
		return servidor == null ? "Docente Externo" : servidor.getCategoria().getDescricao();
	}

	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(programa, "Programa", erros);
		ValidatorUtil.validateRequired(vinculo, "vinculo", erros);
		ValidatorUtil.validateRequired(nivel, "n�vel", erros);
		if ((servidor == null || servidor.getId() == 0) && (docenteExterno == null || docenteExterno.getId() == 0)) {
			erros.addErro("Escolha ou um servidor ou um docente externo");
		}

		return erros;
	}

	public String getDescricao() {
		return getMatriculaNome() + " - " + getVinculo().getDenominacao();
	}

	public DocenteExterno getDocenteExterno() {
		return docenteExterno;
	}

	public void setDocenteExterno(DocenteExterno docenteExterno) {
		this.docenteExterno = docenteExterno;
	}

	public NivelEquipe getNivel() {
		return nivel;
	}

	public void setNivel(NivelEquipe nivel) {
		this.nivel = nivel;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Integer getMaxBolsistasMestrado() {
		return maxBolsistasMestrado;
	}

	public void setMaxBolsistasMestrado(Integer maxBolsistasMestrado) {
		this.maxBolsistasMestrado = maxBolsistasMestrado;
	}

	public Integer getMaxBolsistasDoutorado() {
		return maxBolsistasDoutorado;
	}

	public Integer getMaxOrientandoRegularMestrado() {
		return maxOrientandoRegularMestrado;
	}

	public void setMaxOrientandoRegularMestrado(Integer maxOrientandoRegularMestrado) {
		this.maxOrientandoRegularMestrado = maxOrientandoRegularMestrado;
	}

	public Integer getMaxOrientandoRegularDoutorado() {
		return maxOrientandoRegularDoutorado;
	}

	public void setMaxOrientandoRegularDoutorado(
			Integer maxOrientandoRegularDoutorado) {
		this.maxOrientandoRegularDoutorado = maxOrientandoRegularDoutorado;
	}

	public Integer getMaxOrientandoEspecialMestrado() {
		return maxOrientandoEspecialMestrado;
	}

	public void setMaxOrientandoEspecialMestrado(
			Integer maxOrientandoEspecialMestrado) {
		this.maxOrientandoEspecialMestrado = maxOrientandoEspecialMestrado;
	}

	public Integer getMaxOrientandoEspecialDoutorado() {
		return maxOrientandoEspecialDoutorado;
	}

	public void setMaxOrientandoEspecialDoutorado(
			Integer maxOrientandoEspecialDoutorado) {
		this.maxOrientandoEspecialDoutorado = maxOrientandoEspecialDoutorado;
	}

	public void setMaxBolsistasDoutorado(Integer maxBolsistasDoutorado) {
		this.maxBolsistasDoutorado = maxBolsistasDoutorado;
	}

	public Integer getMaxOrientadosMestrado() {
		return maxOrientadosMestrado;
	}

	public void setMaxOrientadosMestrado(Integer maxOrientadosMestrado) {
		this.maxOrientadosMestrado = maxOrientadosMestrado;
	}

	public Integer getMaxOrientadosDoutorado() {
		return maxOrientadosDoutorado;
	}

	public void setMaxOrientadosDoutorado(Integer maxOrientadosDoutorado) {
		this.maxOrientadosDoutorado = maxOrientadosDoutorado;
	}

	public boolean isDoutorado() {
		return doutorado;
	}

	public void setDoutorado(boolean doutorado) {
		this.doutorado = doutorado;
	}

	public boolean isMestrado() {
		return mestrado;
	}

	public void setMestrado(boolean mestrado) {
		this.mestrado = mestrado;
	}

	public Integer getTotalBolsistasMestrado() {
		return totalBolsistasMestrado;
	}

	public void setTotalBolsistasMestrado(Integer totalBolsistasMestrado) {
		this.totalBolsistasMestrado = totalBolsistasMestrado;
	}

	public Integer getTotalBolsistasDoutorado() {
		return totalBolsistasDoutorado;
	}

	public void setTotalBolsistasDoutorado(Integer totalBolsistasDoutorado) {
		this.totalBolsistasDoutorado = totalBolsistasDoutorado;
	}

	public Integer getTotalOrientadosMestrado() {
		return totalOrientadosMestrado;
	}

	public void setTotalOrientadosMestrado(Integer totalOrientadosMestrado) {
		this.totalOrientadosMestrado = totalOrientadosMestrado;
	}

	public Integer getTotalOrientadosDoutorado() {
		return totalOrientadosDoutorado;
	}

	public void setTotalOrientadosDoutorado(Integer totalOrientadosDoutorado) {
		this.totalOrientadosDoutorado = totalOrientadosDoutorado;
	}

	public AreaConcentracao getAreaConcentracaoPrincipal() {
		return areaConcentracaoPrincipal;
	}

	public void setAreaConcentracaoPrincipal(
			AreaConcentracao areaConcentracaoPrincipal) {
		this.areaConcentracaoPrincipal = areaConcentracaoPrincipal;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	@Override
	public String toString() {
		if( !isEmpty(servidor) )
			return servidor.toString();
		else if( !isEmpty( docenteExterno ) )
			return docenteExterno.toString();
		else return null;
	}

	public boolean isServidorUFRN(){
		return !isEmpty( servidor );
	}

	/**
	 * anula o servidor ou docente externo caso o id seja 0
	 */
	public void ajustar(){
		if( servidor != null && servidor.getId() == 0 )
			servidor = null;
		if( docenteExterno != null && docenteExterno.getId() == 0 )
			docenteExterno = null;
	}

	public int getIdDocente(){
		if( servidor != null )
			return servidor.getId();
		else if( docenteExterno != null )
			return docenteExterno.getId();
		return 0;
	}
	
	/**
	 * M�todo que retorna a pessoa, para ambos os casos, 
	 * servidor ou docente Externo.  
	 */
	public Pessoa getPessoa(){
		if( servidor != null )
			return servidor.getPessoa();
		else if( docenteExterno != null )
			return docenteExterno.getPessoa();
		return null;
	}

	public int compareTo(EquipePrograma other) {

		int result = getVinculo().getDenominacao().compareTo( other.getVinculo().getDenominacao() );

		if( result == 0 )
			result = getNome().compareTo(other.getNome());

		return result;

	}

	/**
	 * Esse m�todo serve para instanciar todos os atributos 
	 * que estiverem nulo. 
	 */
	public void iniciarNulos(){
		if( programa == null )
			programa = new Unidade();
		if( nivel == null )
			nivel = new NivelEquipe();
		if( vinculo== null )
			vinculo = new VinculoEquipe();
		if( programa == null )
			programa = new Unidade();
		if( servidor == null && docenteExterno == null ){
			servidor = new Servidor();
			docenteExterno = new DocenteExterno();
		}
		if( areaConcentracaoPrincipal == null )
			areaConcentracaoPrincipal = new AreaConcentracao(); 
	}

	public Set<LinhaPesquisaStricto> getLinhasPesquisa() {
		return linhasPesquisa;
	}

	public void setLinhasPesquisa(Set<LinhaPesquisaStricto> linhasPesquisa) {
		this.linhasPesquisa = linhasPesquisa;
	}

}
