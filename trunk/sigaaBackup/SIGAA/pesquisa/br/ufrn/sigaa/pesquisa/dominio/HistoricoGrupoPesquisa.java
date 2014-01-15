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

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * Classe utilizada para registrar o histórico de alterações em grupo de pesquisa
 * 
 * @author Jean Guerethes
 */
@Entity
@Table(name = "historico_grupo_pesquisa", schema = "pesquisa", uniqueConstraints = {})
public class HistoricoGrupoPesquisa implements Validatable {

	/** Chave primária */
	@Id
	@Column(name = "id", unique = true, nullable = false, insertable = true, updatable = true)
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	private int id;
	
	/** Grupo de Pesquisa ao qual o histórico se refere */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_grupo_pesquisa", unique = false, nullable = false, insertable = true, updatable = true)
	private GrupoPesquisa grupoPesquisa;
	
	/** status do plano de trabalho (constantes na aplicação) */
	private int status;
	
	/** Data de registro da alteração de status */
	private Date data;
	
	/** Registro de entrada do usuário que efetuou a operação e alterou o status do plano de trabalho */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "registro_entrada", unique = false, nullable = false, insertable = true, updatable = true)
	private RegistroEntrada registroEntrada;

	/** Parecer sobre o Grupo de Pesquisa */
	private String parecer;

	/** Arquivo Anexado */
	@Column(name = "id_arquivo")
	private Integer idArquivo;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public GrupoPesquisa getGrupoPesquisa() {
		return grupoPesquisa;
	}
	public void setGrupoPesquisa(GrupoPesquisa grupoPesquisa) {
		this.grupoPesquisa = grupoPesquisa;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}
	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}
	public String getParecer() {
		return parecer;
	}
	public void setParecer(String parecer) {
		this.parecer = parecer;
	}
	public Integer getIdArquivo() {
		return idArquivo;
	}
	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}
	public String getDescricaoStatus() {
		return StatusGrupoPesquisa.getTiposStatus().get(status);
	}
	@Override
	public ListaMensagens validate() {
		return null;
	}
	
}