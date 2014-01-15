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
 * <p> Classe que vai salvar as informações referentes as preferências da tela de catalogação para cada catalogador. </p>
 *
 * <p> Caso esse objeto não esteja persistido, será usado as configurações padrão.</p>
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
	
	/** A pessoa para qual essa configuração pertence. */
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_pessoa")
	private Pessoa pessoa;
	
	/** 
	 *  Flag que indica se o usuário deseja que o painel lateral seja exibido ou não por padrão.
	 *  O painel lateral contém informação como as classificações e materiais da catalogação.
	 *  Porém diminui a área para digitar o texto. */
	@Column(name="exibir_painel_lateral", nullable=false)
	private boolean exibirPainelLateral = true;
	
	
	/** Indica se a tela de catalogação a ser usado é a completa ou a simplificada. 
	 *  Por padrão vai ser a completa, mas o usuário pode achar melhor usar a simplificado por padrão.
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
