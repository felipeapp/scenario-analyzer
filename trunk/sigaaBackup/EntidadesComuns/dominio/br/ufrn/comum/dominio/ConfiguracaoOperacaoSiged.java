package br.ufrn.comum.dominio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.integracao.siged.dto.DescritorDTO;

/**
 * Classe que representa as configurações das operações que utilizam o armazenamento de arquivo no SIGED.
 * @author Adriana Alves
 */
@Entity
@Table(name = "configuracoes_operacoes_siged", schema = "comum")
public class ConfiguracaoOperacaoSiged implements Validatable {

	/**
	 * Identificador
	 */
	@Id
	@GeneratedValue(generator = "seqGenerator")
	@GenericGenerator(name = "seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", 
				  	  parameters = {@Parameter(name = "sequence_name", value = "ged.seq_configuracao_operacao")})
	@Column(name="id_configuracao_operacao", nullable = false)
	private int id;
	
	/**
	 * Indica a operação a ser realizada, será utilizado o nome da classe que utiliza no qual o caso de uso é referenciado
	 */
	@Column(name="nome_operacao",nullable = false)
	private String nomeOperacao;
	
	/**
	 * Indica o identificador da pasta raiz. Utilizado para não perder a referência da pasta, caso a mesma seja renomeada.
	 */
	@Column(name="id_pasta_raiz", nullable = false)
	private String idPastaRaiz;
	
	/**
	 * Indica o caminho do diretório a ser exibido.
	 */
	@Column(name="pasta_label_raiz", nullable = false)
	private String pastaLabelRaiz;
	
	/**
	 * Indica se a operação está ativa ou inativa. Caso esteja inativa, o arquivo não será armazenado no SIGED.
	 */
	@Column(nullable = false)
	private boolean ativo;
	
	
	/**
	 * Indica o tipo do documento disponível para envio pelo SIGED associado a operação.
	 */
	@Column(name="id_tipo_documento")
	private Integer idTipoDocumento;
	
	/**
	 * Indica o diretório raiz, no qual serão armazenados os arquivos.
	 */
	@Column(name="id_diretorio_raiz")
	private Integer idDiretorioRaiz;
	
	/**
	 * Lista de descritores associados ao tipo de documento da configuração.
	 */
	@Transient
	private List<DescritorDTO> descritores;

	/**
	 * 
	 */
	@Transient
	private Map<String, DescritorDTO> mapaDescritores;
	
	public Integer getIdDiretorioRaiz() {
		return idDiretorioRaiz;
	}

	public void setIdDiretorioRaiz(Integer idDiretorioRaiz) {
		this.idDiretorioRaiz = idDiretorioRaiz;
	}
	
	public String getPastaLabelRaiz() {
		return pastaLabelRaiz;
	}

	public void setPastaLabelRaiz(String pastaLabelRaiz) {
		this.pastaLabelRaiz = pastaLabelRaiz;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIdPastaRaiz() {
		return idPastaRaiz;
	}

	public void setIdPastaRaiz(String idPastaRaiz) {
		this.idPastaRaiz = idPastaRaiz;
	}

	public Integer getIdTipoDocumento() {
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(Integer idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
	}

	@Override
	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNomeOperacao() {
		return nomeOperacao;
	}

	public void setNomeOperacao(String nomeOperacao) {
		this.nomeOperacao = nomeOperacao;
	}

	public List<DescritorDTO> getDescritores() {
		return descritores;
	}

	public void setDescritores(List<DescritorDTO> descritores) {
		this.descritores = descritores;
	}

	/**
	 * Obtém o mapa de descritores com seus alias.
	 * @param alias
	 * @return
	 */
	public DescritorDTO getDescritor(String alias) {
		
		if (mapaDescritores == null)
			mapaDescritores = new HashMap<String, DescritorDTO>();
		
		DescritorDTO result = mapaDescritores.get(alias);
		
		if (result == null && descritores != null) {
			
			for (DescritorDTO descritor : descritores) {
				
				if (descritor.getAlias().equals(alias)) {
					result = descritor;
					mapaDescritores.put(alias, result);
					break;
				}
			}
		}
		
		return result;
	}
	
}