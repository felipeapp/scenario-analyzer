/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/10/2007
 *
 */

package br.ufrn.sigaa.pesquisa.dominio;

import java.util.Date;

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

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Classe que representa um avaliador do congresso de iniciação científica,  
 * sendo subdividido em dois tipos:  	 
 * AVALIADOR_RESUMO ou AVALIADOR_APRESENTACAO
 * 
 * @author Leonardo Campos
 */
@Entity
@Table(name = "avaliador_cic", schema = "pesquisa")
public class AvaliadorCIC implements Validatable {
	
	/** Constante utilizada no filtro, identifica um docente*/
	public final static int STATUS_DOCENTE = 1;
	
	/** Constante utilizada no filtro, identifica um discente*/
	public final static int STATUS_DISCENTE= 2;
	
	/** Geração da chave primária */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_avaliador_cic", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Mapeamento da classe servidor do tipo Muitos pra um*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_docente")
	private Servidor docente;
	
	/** Mapeamento da classe discente do tipo Muitos pra um*/
	@ManyToOne(fetch = FetchType.LAZY, targetEntity=Discente.class)
	@JoinColumn(name = "id_discente")
	private DiscenteAdapter discente;
	
	/** Mapeamento da classe Congresso Iniciação Cientifica do tipo Muitos pra um */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_congresso")
	private CongressoIniciacaoCientifica congresso;
	
	/** Mapeamento da classe Area de Conhecimento do tipo Muitos pra um */
	@ManyToOne(fetch =  FetchType.LAZY)
	@JoinColumn(name = "id_area")
	private AreaConhecimentoCnpq area;
	
	/** Serve para indicar se o Avaliador do CIC é do tipo Avaliador de Resumo */
	@Column(name = "avaliador_resumo")
	private boolean avaliadorResumo;
	
	/** Serve para indicar se o Avaliador do CIC é do tipo Avaliador de Apresentação */
	@Column(name = "avaliador_apresentacao")
	private boolean avaliadorApresentacao;
	
	/** É responsável por indicar se o avaliador esteve presente ao congresso ou não. */
	private boolean presenca;
	
	/** TRANSIENT, para indicar se é um docente ou discente*/
	@Transient
	private Integer tipoUsuario;
	
	public AvaliadorCIC(){
	}

	/** Retorna o congresso */
	public CongressoIniciacaoCientifica getCongresso() {
		return congresso;
	}

	/** Seta o congresso */
	public void setCongresso(CongressoIniciacaoCientifica congresso) {
		this.congresso = congresso;
	}

	/** Retorna o docente */
	public Servidor getDocente() {
		return docente;
	}

	/** Serve para setar o docente */
	public void setDocente(Servidor docente) {
		this.docente = docente;
	}

	/** Serve para retornar o id do Avaliador CIC */
	public int getId() {
		return id;
	}

	/** Serve para setar o id do avaliador do CIC */
	public void setId(int id) {
		this.id = id;
	}

	/** Serve para validar os campos obrigatórios */
	public ListaMensagens validate() {
 		ListaMensagens erros = new ListaMensagens();
		if( isUsuarioDocente() ){
			ValidatorUtil.validateRequired(docente, "Docente", erros);
		}
		if( isUsuarioDiscente() ){
			ValidatorUtil.validateRequired(discente, "Discente", erros);
		}
		ValidatorUtil.validateRequired(congresso, "Congresso", erros);
			if(!avaliadorResumo && !avaliadorApresentacao)
			erros.addErro("Informe pelo menos um dos tipos de avaliador.");
		return erros;
	}
	
	
	/** Retorna a descrição do domínio */
	@Transient
	public String getDescricaoDominio(){
		return "Avaliador do CIC";
	}

	/** Retorna a area de conhecimento */
	public AreaConhecimentoCnpq getArea() {
		return area;
	}

	/** Seta a area de conhecimento */
	public void setArea(AreaConhecimentoCnpq area) {
		this.area = area;
	}

	/** Retorna um booleano informando se o Avaliador do CIC e do tipo Resumo */
	public boolean isAvaliadorResumo() {
		return avaliadorResumo;
	}

	/** Seta se um avaliador do CIC é do tipo Resumo */
	public void setAvaliadorResumo(boolean avaliadorResumo) {
		this.avaliadorResumo = avaliadorResumo;
	}
	
	/** Retorna um booleano informando se o Avaliador do CIC e do tipo Apresentação */
	public boolean isAvaliadorApresentacao() {
		return avaliadorApresentacao;
	}
	
	/** Seta se um avaliador do CIC é do tipo Apresentação */
	public void setAvaliadorApresentacao(boolean avaliadorApresentacao) {
		this.avaliadorApresentacao = avaliadorApresentacao;
	}

	/** Retorna um booleano indicando se o certificado já está disponível ou não */
	public boolean isCertificadoDisponivel(){
		return (new Date()).after( congresso.getFim() );
	}

	/** Retorna um booleano indicando se o Avaliador esteve ou não presente no CIC */
	public boolean isPresenca() {
		return presenca;
	}

	/** Seta a presença ou a falta do Avaliador no CIC. */
	public void setPresenca(boolean presenca) {
		this.presenca = presenca;
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}
	
	public boolean isUsuarioDocente(){
		return (tipoUsuario!=null && tipoUsuario==STATUS_DOCENTE);
	}
	
	public boolean isUsuarioDiscente(){
		return (tipoUsuario!=null && tipoUsuario==STATUS_DISCENTE);
	}

	public Integer getTipoUsuario() {
		return tipoUsuario;
	}

	public void setTipoUsuario(Integer tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}
	
	/*
	 * Implementação do método equals comparando os ids da 
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AvaliadorCIC other = (AvaliadorCIC) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
