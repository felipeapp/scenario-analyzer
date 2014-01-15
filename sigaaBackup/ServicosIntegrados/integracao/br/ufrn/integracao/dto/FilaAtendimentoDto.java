package br.ufrn.integracao.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
/**
 * DTO que concentra dados referente à uma fila de atendimento<br>
 * usada na integração entre o sigprh e o sistema de atendimento<br>
 * do departamento de administração de pessoal.
 * @author Rafael Moreira
 *
 */
public class FilaAtendimentoDto implements Serializable{
	/** Atributo de definição da serialização da classe */
	private static final long serialVersionUID = -1L;
	
	/** Demais atributos. */
	private int id;
	private List<MembroFilaDto> membrosFila;
	private int posicaoAtual;
	private boolean preferencial;
	private int ultimaFicha;
	private Date data;
	
	/** Getters e Setters */
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<MembroFilaDto> getMembrosFila() {
		return membrosFila;
	}
	public void setMembrosFila(List<MembroFilaDto> membrosFila) {
		this.membrosFila = membrosFila;
	}
	public int getPosicaoAtual() {
		return posicaoAtual;
	}
	public void setPosicaoAtual(int posicaoAtual) {
		this.posicaoAtual = posicaoAtual;
	}
	public boolean isPreferencial() {
		return preferencial;
	}
	public void setPreferencial(boolean preferencial) {
		this.preferencial = preferencial;
	}
	public int getUltimaFicha() {
		return ultimaFicha;
	}
	public void setUltimaFicha(int ultimaFicha) {
		this.ultimaFicha = ultimaFicha;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}	
	
	



}
