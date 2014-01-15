/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 19/10/2010
 */
package br.ufrn.sigaa.estagio.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Entidade que representa os Interesses às Ofertas de Estágio.
 * 
 * @author arlindo
 *
 */
@Entity
@Table(name = "interesse_oferta", schema = "estagio")
public class InteresseOferta implements PersistDB {
	
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="estagio.hibernate_sequence") })	
	@Column(name = "id_interesse_oferta")		
	private int id;	
	
	/** Oferta de Estágio ao qual o discente se interessou */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_oferta_estagio")
	private OfertaEstagio oferta;
	
	/** Discente interessado na oferta */
	@ManyToOne(targetEntity=Discente.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_discente")
	private DiscenteAdapter discente;
	
	/** Estagio que foi selecionado */
	@OneToOne(mappedBy="interesseOferta", fetch = FetchType.LAZY)
	private Estagiario estagiario;
	
	/** Indica se o interesse está ativo ou não */
	private boolean ativo;
	
	/** Indica se o interesse foi selecionado 
	 * para a vaga de estágio */ 
	private boolean selecionado;
	
	/** Descrição do Perfil do discente inscrito */
	@Column(name = "descricao_perfil")
	private String descricaoPerfil;
	
	/** Data do cadastro. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;
	
	/** Registro entrada de quem cadastrou. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroCadastro;
	
	/** ID do curriculum vitae enviado pelo discente. */
	@Column(name="id_arquivo_curriculo")
	private Integer idArquivoCurriculo;
	
	/** Atributo transiente que auxilia no cadastro, 
	 * armazenando a descrição das atividades */
	@Transient
	private String descricaoAtividades;
	
	/** Arquivo de curriculum vitae que o discente poderá enviar ao se cadastrar para uma oferta de estágio. */
	@Transient
	private UploadedFile arquivoCurriculo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public OfertaEstagio getOferta() {
		return oferta;
	}

	public void setOferta(OfertaEstagio oferta) {
		this.oferta = oferta;
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Estagiario getEstagiario() {
		return estagiario;
	}

	public void setEstagiario(Estagiario estagiario) {
		this.estagiario = estagiario;
	}

	public String getDescricaoPerfil() {
		return descricaoPerfil;
	}

	public void setDescricaoPerfil(String descricaoPerfil) {
		this.descricaoPerfil = descricaoPerfil;
	}

	public String getDescricaoAtividades() {
		return descricaoAtividades;
	}

	public void setDescricaoAtividades(String descricaoAtividades) {
		this.descricaoAtividades = descricaoAtividades;
	}

	public Integer getIdArquivoCurriculo() {
		return idArquivoCurriculo;
	}

	public void setIdArquivoCurriculo(Integer idArquivoCurriculo) {
		this.idArquivoCurriculo = idArquivoCurriculo;
	}

	public UploadedFile getArquivoCurriculo() {
		return arquivoCurriculo;
	}

	public void setArquivoCurriculo(UploadedFile arquivoCurriculo) {
		this.arquivoCurriculo = arquivoCurriculo;
	}
}
