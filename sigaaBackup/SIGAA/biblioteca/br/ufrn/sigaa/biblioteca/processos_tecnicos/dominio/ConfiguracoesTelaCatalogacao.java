package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * 
 * <p> Classe que vai salvar as informa��es referentes as prefer�ncias da tela de cataloga��o para cada catalogador. </p>
 *
 * <p> Caso esse objeto n�o esteja persistido, ser� usado as configura��es padr�o.</p>
 *
 * @author jadson
 *
 */
@Entity
@Table(name = "configuracoes_tela_catalogacao", schema = "biblioteca")
public class ConfiguracoesTelaCatalogacao  implements PersistDB{

	/** O id */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.resgistro_extras_sequence") })
	@Column (name="id_configuracoes_tela_catalogacao")
	private int id;
	
	/** A pessoa para qual essa configura��o pertence. */
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_pessoa")
	private Pessoa pessoa;
	
	/** 
	 *  Flag que indica se o usu�rio deseja que o painel lateral seja exibido ou n�o por padr�o.
	 *  O painel lateral cont�m informa��o como as classifica��es e materiais da cataloga��o.
	 *  Por�m diminui a �rea para digitar o texto. */
	@Column(name="exibir_painel_lateral", nullable=false)
	private boolean exibirPainelLateral = true;
	
	
	/** Indica se a tela de cataloga��o a ser usado � a completa ou a simplificada. 
	 *  Por padr�o vai ser a completa, mas o usu�rio pode achar melhor usar a simplificado por padr�o.
	 */
	@Column(name="usar_tela_catalogacao_completa", nullable=false)
	private boolean usarTelaCatalogacaoCompleta = true;
	
	
	
	public ConfiguracoesTelaCatalogacao() {
		
	}
	
	public ConfiguracoesTelaCatalogacao(Pessoa pessoa ,boolean exibirPainelLateral, boolean usarTelaCatalogacaoCompleta) {
		this.pessoa = pessoa;
		this.exibirPainelLateral = exibirPainelLateral;
		this.usarTelaCatalogacaoCompleta = usarTelaCatalogacaoCompleta;
	}
	
	
	public int getId() {return id;}
	public void setId(int id) {this.id = id;}
	public Pessoa getPessoa() {return pessoa;}
	public void setPessoa(Pessoa pessoa) {this.pessoa = pessoa;}
	public boolean isExibirPainelLateral() {	return exibirPainelLateral;}
	public void setExibirPainelLateral(boolean exibirPainelLateral) {	this.exibirPainelLateral = exibirPainelLateral;}
	public boolean isUsarTelaCatalogacaoCompleta() {return usarTelaCatalogacaoCompleta;}
	public void setUsarTelaCatalogacaoCompleta(boolean usarTelaCatalogacaoCompleta) {this.usarTelaCatalogacaoCompleta = usarTelaCatalogacaoCompleta;}
	
}
