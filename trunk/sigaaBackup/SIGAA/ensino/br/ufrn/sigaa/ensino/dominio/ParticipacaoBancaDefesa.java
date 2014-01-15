/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 07/11/2011
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.Collection;

/**
 * Classe para guardas os dados relativos a participação de um docente
 * externo em uma banca de graduação na UFRN.
 * 
 * @author Arlindo Rodrigues
 */
public class ParticipacaoBancaDefesa {
	/** Informações da participanção da banca */
	private InformacoesParticipacaoDefesa info = new InformacoesParticipacaoDefesa();
	/** Unidade */
	private String unidade;
	/** Curso de graduação */
	private String curso;

	/** retorna as informações da participação da banca */
	public InformacoesParticipacaoDefesa getInfo() {
		return info;
	}

	public String getUnidade() {
		return unidade;
	}
	/** Seta a unidade do programa */
	public void setUnidade(String unidade) {
		this.unidade = unidade;
		info.setUnidade(unidade);
	}

	public String getCurso() {
		return curso;
	}
	/** Seta o curso */
	public void setCurso(String curso) {
		this.curso = curso;
		info.setCurso(curso);
	}

	public String getParticipante() {
		return info.getParticipante();
	}
	/** Seta o participante */
	public void setParticipante(String participante) {
		info.setParticipante(participante);
	}

	public String getDiscente() {
		return info.getDiscente();
	}
	/** seta o discente da banca */
	public void setDiscente(String discente) {
		info.setDiscente(discente);
	}

	public String getTitulo() {
		return info.getTitulo();
	}
	/** Seta o título do trabalho */
	public void setTitulo(String titulo) {
		info.setTitulo(titulo);
	}

	public String getDataBanca() {
		return info.getDataBanca();
	}
	/** seta a data da banca */
	public void setDataBanca(String dataBanca) {
		info.setDataBanca(dataBanca);
	}

	public String getDataEmissao() {
		return info.getDataEmissao();
	}
	/** seta a data de emissão */
	public void setDataEmissao(String dataEmissao) {
		info.setDataEmissao(dataEmissao);
	}

	public String getCoordenador() {
		return info.getCoordenador();
	}
	/** seta o coordenador do programa */
	public void setCoordenador(String coordenador) {
		info.setCoordenador(coordenador);
	}

	public Collection<MembroBanca> getMembros() {
		return info.getMembros();
	}
	/** Seta os membros da banca */
	public void setMembros(Collection<MembroBanca> membros) {
		info.setMembros(membros);
	}
	
	public String getTipoParticipacao() {
		return info.getTipoParticipacao();
	}
	/** seta o tipo de participação */
	public void setTipoParticipacao(String tipoParticipacao) {
		info.setTipoParticipacao(tipoParticipacao);
	}
	
	public String getTipoBanca() {
		return info.getTipoBanca();
	}
	/** seta o tipo da banca */
	public void setTipoBanca(String tipoBanca) {
		info.setTipoBanca(tipoBanca);
	}
	/** seta o sexo do participante */
	public void setSexo(boolean sexo) {
		info.setSexo(sexo);
	}
	
	public boolean getSexo() {
		return info.getSexo();
	}
	
	public String getNivelBanca() {
		return info.getNivelBanca();
	}
	/** seta o nível da banca */
	public void setNivelBanca(String nivelBanca) {
		info.setNivelBanca(nivelBanca);
	}
	
	public String getCpfPassaporte() {
		return info.getCpfPassaporte();
	}
	/** seta o cpf ou passaporte do participante */
	public void setCpfPassaporte(String CpfPassaporte) {
		info.setCpfPassaporte(CpfPassaporte);
	}
	
	public int getRegistroPessoa() {
		return info.getRegistroPessoa();
	}

	/** Seta o identificador do registro de pessoa do participante. */
	public void setRegistroPessoa(int registroPessoa) {
		info.setRegistroPessoa(registroPessoa);
	}
}
