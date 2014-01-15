/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 05/03/2008
 */ 
package br.ufrn.sigaa.ensino.stricto.dominio;

import java.util.Collection;

/**
 * Classe para guardas os dados relativos a participa��o de um docente
 * externo em uma banca de p�s-gradua��o na UFRN.
 * @author David Pereira
 */
public class ParticipacaoBancaPos {
	/** Informa��es da participan��o da banca */
	private InformacoesParticipacao info = new InformacoesParticipacao();
	/** Unidade do programa */
	private String unidade;
	/** Programa de p�s gradua��o */
	private String programa;

	/** retorna as informa��es da participa��o da banca */
	public InformacoesParticipacao getInfo() {
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

	public String getPrograma() {
		return programa;
	}
	/** Seta o programa */
	public void setPrograma(String programa) {
		this.programa = programa;
		info.setPrograma(programa);
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
	/** Seta o t�tulo do trabalho */
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
	/** seta a data de emiss�o */
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

	public Collection<MembroBancaPos> getMembros() {
		return info.getMembros();
	}
	/** Seta os membros da banca */
	public void setMembros(Collection<MembroBancaPos> membros) {
		info.setMembros(membros);
	}
	
	public String getTipoParticipacao() {
		return info.getTipoParticipacao();
	}
	/** seta o tipo de participa��o */
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
	/** seta o n�vel da banca */
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
