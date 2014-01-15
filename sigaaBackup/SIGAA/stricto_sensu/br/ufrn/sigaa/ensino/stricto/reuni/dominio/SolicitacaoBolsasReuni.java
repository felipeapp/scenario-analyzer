/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.reuni.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateEmptyCollection;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * Entidade que registra as propostas dos coordenadores de programas de pós-graduação 
 * para solicitações de bolsas do REUNI à PPg.
 * 
 * @author wendell
 */
@Entity
@Table(name="solicitacao_bolsas_reuni", schema="stricto_sensu")
public class SolicitacaoBolsasReuni implements Validatable {

	public static final int CADASTRADA = 1;
	public static final int SUBMETIDA = 2;

	private int id;
	private EditalBolsasReuni edital;
	private Unidade programa;
	private Collection<PlanoTrabalhoReuni> planos;
	
	private int status;

	@CriadoPor
	private RegistroEntrada registroCadastro;
	@CriadoEm
	private Date dataCadastro;
	
	private static final Map<Integer, String> descricoesStatus;
	static {
		descricoesStatus = new HashMap<Integer, String>();
		descricoesStatus.put(CADASTRADA, "CADASTRADA");
		descricoesStatus.put(SUBMETIDA, "SUBMETIDA");
	}

	public SolicitacaoBolsasReuni() {
		status = CADASTRADA;
	}
	
	public SolicitacaoBolsasReuni(int id) {
		this();
		this.id = id;
	}

	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_solicitacao_bolsas_reuni")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name = "id_unidade")
	public Unidade getPrograma() {
		return programa;
	}
	public void setPrograma(Unidade programa) {
		this.programa = programa;
	}
	
	@OneToMany(mappedBy = "solicitacao", cascade=CascadeType.ALL)
	public Collection<PlanoTrabalhoReuni> getPlanos() {
		return planos;
	}
	public void setPlanos(Collection<PlanoTrabalhoReuni> planos) {
		this.planos = planos;
	}

	@ManyToOne
	@JoinColumn(name = "id_edital_bolsas_reuni")
	public EditalBolsasReuni getEdital() {
		return edital;
	}
	public void setEdital(EditalBolsasReuni edital) {
		this.edital = edital;
	}

	@ManyToOne
	@JoinColumn(name = "id_registro_cadastro")
	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}
	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	public Date getDataCadastro() {
		return dataCadastro;
	}
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	@Transient
	public boolean isSubmetida() {
		return status == SUBMETIDA;
	}

	@Transient
	public String getDescricaoStatus() {
		return descricoesStatus.get(status);
	}
	
	/**
	 * Adicionar plano de trabalho à lista de planos da solicitação
	 * 
	 * @param planoTrabalho
	 */
	public void adicionarPlanoTrabalho(PlanoTrabalhoReuni planoTrabalho) {
		if (planos == null) {
			planos = new ArrayList<PlanoTrabalhoReuni>();
		}
		
		planoTrabalho.setSolicitacao(this);
		planos.add(planoTrabalho);
	}
	
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		validateRequired(programa, "Programa de Pós-Graduação", lista);
		validateEmptyCollection( "Planos de Trabalho: É necessário cadastrar, pelo menos, um plano de trabalho antes de submeter a solicitação." , planos, lista);
		
		return lista;
	}

	
}
