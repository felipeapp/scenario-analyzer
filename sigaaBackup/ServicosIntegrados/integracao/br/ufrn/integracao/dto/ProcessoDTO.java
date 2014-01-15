package br.ufrn.integracao.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * Classe que representa um processo no protocolo.
 * @author Itamir Filho
 *
 */
public class ProcessoDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/** Número fixo do protocolo */
	public static final String numFixo = "23077";
	
	/** Processo Ativo*/
	public final static int ATIVO = 1;
	
	/** Processo cancelado */
	public final static int CANCELADO = 2;
	
	/** Status de processo arquivado */
	public final static int ARQUIVADO = 3;
	
	private Integer id;
	
	private Integer idUsuario;

	private Integer ano;

	private Integer numProtocolo;

	private int DV;

	private String assunto;

	private String observacao;

	private Date dataCadastro;

	private Integer codUnidadeOrigem ;

	private Integer idtipoProcesso;

	private String descricaoTipoProcesso;
	
	private Integer status;
	
	private UnidadeDTO unidadeOrigem;
	
	private Integer idMovimentoAtual;

	SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

	private Collection<MovimentoDTO> movimentacoes;

	private Collection<DocumentoDTO> documentos;

	private Collection<InteressadoDTO> interessados;
		
	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getNumProtocolo() {
		return numProtocolo;
	}

	public void setNumProtocolo(Integer numProtocolo) {
		this.numProtocolo = numProtocolo;
	}

	public int getDV() {
		return DV;
	}

	public void setDV(int dv) {
		DV = dv;
	}

	public String getAssunto() {
		return assunto;
	}

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Integer getCodUnidadeOrigem() {
		return codUnidadeOrigem;
	}

	public void setCodUnidadeOrigem(Integer codUnidadeOrigem) {
		this.codUnidadeOrigem = codUnidadeOrigem;
	}

	public Integer getIdtipoProcesso() {
		return idtipoProcesso;
	}

	public void setIdtipoProcesso(Integer idtipoProcesso) {
		this.idtipoProcesso = idtipoProcesso;
	}

	public String getDescricaoTipoProcesso() {
		return descricaoTipoProcesso;
	}

	public void setDescricaoTipoProcesso(String descricaoTipoProcesso) {
		this.descricaoTipoProcesso = descricaoTipoProcesso;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Collection<MovimentoDTO> getMovimentacoes() {
		return movimentacoes;
	}

	public void setMovimentacoes(Collection<MovimentoDTO> movimentacoes) {
		this.movimentacoes = movimentacoes;
	}

	public Collection<DocumentoDTO> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(Collection<DocumentoDTO> documentos) {
		this.documentos = documentos;
	}

	public Collection<InteressadoDTO> getInteressados() {
		return interessados;
	}

	public void setInteressados(Collection<InteressadoDTO> interessados) {
		this.interessados = interessados;
	}

	public boolean isCancelado() {
		return this.status == CANCELADO;
	}
	
	public UnidadeDTO getUnidadeOrigem() {
		return unidadeOrigem;
	}

	public void setUnidadeOrigem(UnidadeDTO unidadeOrigem) {
		this.unidadeOrigem = unidadeOrigem;
	}

	public String getStatusDescricao(){
		if(status == ARQUIVADO)
			return "ARQUIVADO";
		else if(status == ATIVO)
			return "ATIVO";
		else if(status == CANCELADO)
			return "CANCELADO";
		else
			return "";
	}
	
	public String getNumProtocoloCompleto() {
		return numFixo
				+ "."
				+ formataNumProtocolo(getNumProtocolo().intValue())
				+ "/"
				+ getAno()
				+ "-"
				+ formataDV(calculaDV(numFixo,getNumProtocolo(), getAno()));
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((ano == null) ? 0 : ano.hashCode());
		result = PRIME * result + ((numProtocolo == null) ? 0 : numProtocolo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ProcessoDTO other = (ProcessoDTO) obj;
		if (ano == null) {
			if (other.ano != null)
				return false;
		} else if (!ano.equals(other.ano))
			return false;
		if (numProtocolo == null) {
			if (other.numProtocolo != null)
				return false;
		} else if (!numProtocolo.equals(other.numProtocolo))
			return false;
		return true;
	}
	
	// Métodos adicionais para visualização
	
	public static String formataNumProtocolo(int numProtocolo) {
		int i, k;
		String numFormatado = String.valueOf(numProtocolo);
		if (numProtocolo == 0)
			k = -1;
		else
			k = (int) (Math.log(numProtocolo) / Math.log(10));
		for (i = 0; i < 5 - k; i++)
			numFormatado = "0" + numFormatado;
		return numFormatado;
	}
	
	public static String formataDV(int DV) {
		if (DV > 9)
			return String.valueOf(DV);
		else
			return "0" + DV;

	}
	
	public static int calculaDV(long num) {
		int dv1, dv2, k, sum;
		long tmp = num;
		k = 2;
		sum = 0;
		while (num > 0) {
			sum += (num % 10) * k;
			num = num / 10;
			k++;
		}
		dv1 = sum % 11;
		dv1 = (11 - dv1) % 10;
		k = 2;
		sum = 0;
		num = tmp * 10 + dv1;
		while (num > 0) {
			sum += (num % 10) * k;
			num = num / 10;
			k++;
		}
		dv2 = sum % 11;
		dv2 = (11 - dv2) % 10;
		return (dv1 * 10 + dv2);
	}
	
	public static int calculaDV(String numFixo, Integer numProtocolo,
			Integer ano) {
		long num = Integer.parseInt(numFixo) * 10000000000l
				+ numProtocolo.intValue() * 10000l + ano.intValue();

		return calculaDV(num);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdMovimentoAtual() {
		return idMovimentoAtual;
	}

	public void setIdMovimentoAtual(Integer idMovimentoAtual) {
		this.idMovimentoAtual = idMovimentoAtual;
	}
}