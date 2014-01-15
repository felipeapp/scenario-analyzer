package br.ufrn.comum.dominio.notificacoes;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.util.StringUtils;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Classe de dom�nio que representa um grupo de destinat�rios
 * para envio de notifica��es.
 * 
 * Dever� ser cadastrado no banco caso o grupo de destinat�rios
 * seja vinculado a uma consulta pr�-definida.
 * 
 * Alternativamente, o grupo de destinat�rios poder� ser associado a
 * um Papel (permiss�o).
 * 
 * @author Ricardo Wendell
 */
@Entity
@Table(name = "grupo_destinatarios", schema="comum")
public class GrupoDestinatarios implements PersistDB, Validatable, Comparable<GrupoDestinatarios> {

	public static final int CONSULTA_CADASTRADA  = 1;
	public static final int PAPEL = 2;
	
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	private int id;
	
	/** Descri��o do grupo */
	private String descricao;
	
	/** Consulta em SQL para a busca de usu�rios.
	 */
	@Column(name="sql_destinatarios")
	private String sqlDestinatarios;
	
	@OneToMany(mappedBy="grupo")
	private List<ParametroGrupo> parametros;
	
	/** Sistema em que devem ser buscados os destinat�rios, caso seja cadastrada 
	 * uma consulta pr�-definida 
	 */
	@ManyToOne
	@JoinColumn(name="id_sistema")
	private Sistema sistema;
	
	/**
	 * Subsistema ao qual o grupo est� associado. 
	 * Utilizado para defini��o de permiss�es autom�ticas.
	 */
	@ManyToOne
	@JoinColumn(name="id_subsistema")
	private SubSistema subSistema;
	
	/** Papel dos usu�rios a serem notificados, caso n�o seja utilizada uma consulta espec�fica */
	@Transient
	private Papel papel;
	
	/** Se o grupo est� ativo ou n�o. */
	private boolean ativo = true;
	
	/** Usu�rio que cadastrou o grupo */
	@ManyToOne
	@JoinColumn(name="id_usuario")
	private UsuarioGeral criadoPor;

	/** Data de cadastro do grupo */
	@Column(name="data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	private Date criadoEm;
	
	/** Indica se o grupo pode ser usado no envio de memorandos circulares */
	@Column(name="memorando_circular")
	private boolean memorandoCircular;
	
	/** Indica se o grupo pode ser usado para mostrar telas de aviso no logon do usu�rio */
	@Column(name="tela_aviso_logon")
	private boolean telaAvisoLogon;
	
	/** Atributo transiente para a contabiliza��o dos destinat�rios */
	@Transient
	private int totalDestinatarios;
	
	@Column(name="comunidade_virtual")
	private Boolean participaComunidadeVirtual;

	
	/** Construtores */
	
	public GrupoDestinatarios() {
		
	}

	public GrupoDestinatarios(int id, Integer idPapel) {
		this.id = id;
		if (idPapel != null) {
			this.papel = new Papel(idPapel);
		}
	}

	public GrupoDestinatarios(Papel papel) {
		this.papel = papel;
		this.descricao = papel.getDescricao();
		this.setSistema(papel.getSistema());
	}
	
	public GrupoDestinatarios(int id) {
		this.id = id;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof GrupoDestinatarios)) return false;
		
		GrupoDestinatarios outro = (GrupoDestinatarios) obj;
		if ( this.id != 0 && outro.getId() != 0 ) {
			return this.id == outro.getId();
		} else if (this.papel != null && outro.getPapel() != null ){
			return this.papel.equals(outro.getPapel());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, papel);
	}
	
	@Override
	public int compareTo(GrupoDestinatarios g) {

		int compare = 0;
		
		// Ordena��o pelo sistema
		if(!ValidatorUtil.isEmpty(g.getSistema())) {
			compare = Integer.valueOf(getSistema().getId()).compareTo(g.getSistema().getId());
		}
		// Ordena��o pela descri��o
		else if(!ValidatorUtil.isEmpty(g.getDescricao())) {
			compare = getDescricao().compareTo(g.getDescricao());
		}
		
		return compare;

	}
	
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		
		validateRequired(descricao, "Descri��o do grupo", erros);
		validateRequired(sistema, "Sistema", erros);
		validateRequired(sqlDestinatarios, "Consulta dos destinat�rios", erros);
		
		int params = StringUtils.countOccurrencesOf(sqlDestinatarios, "?");
		if (params > 0 && parametros != null && params != CollectionUtils.size(parametros)) {
			erros.addErro("A consulta informada possui " + params + " par�metro(s). � necess�rio informar os par�metros do grupo.");
		}
		
		return erros;
	}
	
	/** M�todos acessores */
	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<ParametroGrupo> getParametros() {
		return parametros;
	}

	public void setParametros(List<ParametroGrupo> parametros) {
		this.parametros = parametros;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public String getDescricaoCompleta() {
		return (sistema != null ? sistema.getNome() + " - " : "") + descricao;
	}
	
	public void setDescricao(String descricao) {
		if (descricao != null) {
			descricao = descricao.toUpperCase();
		}
		this.descricao = descricao;
	}

	public Papel getPapel() {
		return this.papel;
	}

	public void setPapel(Papel papel) {
		this.papel = papel;
	}

	public Sistema getSistema() {
		return this.sistema;
	}

	public void setSistema(Sistema sistema) {
		this.sistema = sistema;
	}
	
	public String getSqlDestinatarios() {
		return this.sqlDestinatarios;
	}

	public void setSqlDestinatarios(String sqlDestinatarios) {
		this.sqlDestinatarios = sqlDestinatarios;
	}

	public SubSistema getSubSistema() {
		return this.subSistema;
	}

	public void setSubSistema(SubSistema subSistema) {
		this.subSistema = subSistema;
	}

	public int getTotalDestinatarios() {
		return this.totalDestinatarios;
	}

	public void setTotalDestinatarios(int totalDestinatarios) {
		this.totalDestinatarios = totalDestinatarios;
	}

	public UsuarioGeral getCriadoPor() {
		return this.criadoPor;
	}

	public void setCriadoPor(UsuarioGeral criadoPor) {
		this.criadoPor = criadoPor;
	}

	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public boolean isAtivo() {
		return this.ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public int getTipo() {
		if (this.getPapel() != null) {
			return PAPEL;
		}
		return CONSULTA_CADASTRADA;
	}

	public boolean isMemorandoCircular() {
		return memorandoCircular;
	}

	public void setMemorandoCircular(boolean memorandoCircular) {
		this.memorandoCircular = memorandoCircular;
	}

	public boolean isTelaAvisoLogon() {
		return telaAvisoLogon;
	}

	public void setTelaAvisoLogon(boolean telaAvisoLogon) {
		this.telaAvisoLogon = telaAvisoLogon;
	}

	public Boolean isParticipaComunidadeVirtual() {
		return participaComunidadeVirtual;
	}

	public void setParticipaComunidadeVirtual(Boolean participaComunidadeVirtual) {
		this.participaComunidadeVirtual = participaComunidadeVirtual;
	}

	public void addParametro(ParametroGrupo obj) {
		if (parametros == null)
			parametros = new ArrayList<ParametroGrupo>();
		obj.setGrupo(this);
		parametros.add(obj);
	}


}
