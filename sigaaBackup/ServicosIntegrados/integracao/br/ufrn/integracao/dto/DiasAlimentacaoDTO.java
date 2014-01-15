/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 08/08/2008
 *
 */	
package br.ufrn.integracao.dto;

import java.io.Serializable;

/**
 * Objeto utilizado pelo aplicativo desktop de controle de entrada do RU. Contém informações
 * sobre os dias permitidos para alimentação, se a utilização do cartão na refeição atual é permitida e
 * também informações sobre o usuário.
 * 
 * @author Agostinho
 * @author Bráulio
 */
public class DiasAlimentacaoDTO implements Serializable {

	/** Id da foto registrada no banco. */
	private int idFoto;
	
	/** Nome do usuário. **/
	private String nome;
	
	/** Matrícula do discente ou siape do servidor. **/
	private long matricula;
	
	/** CPF do usuário. */
	private long cpf;
	
	/** Nome da divisão estudantil que utiliza o cartao */
	private String nomeDivisaoEstudantil;
	
	/** Curso do discente. **/
	private String curso;
	
	/** Tipo da bolsa. */
	@Deprecated
	private int vinculo;
	
	/** Tipo da bolsa. */
	private int tipoBolsa;
		
	/** Tipo de vínculo: servidor, discente ou bolsista. */
	private String tipoVinculo;
	
	private int situacaoBolsa;
	
	/** indica se o discente já acessou o RU **/
	@Deprecated
	private boolean discenteAcessouRefeicaoRU;
	
	/** Indica se o usuário já acessou o RU na refeição atual. */
	private boolean acessouRURefeicaoAtual;
	
	//Verifica se o horário é válido
	private boolean horarioValidoCafe;
	private boolean horarioValidoAlmoco;
	private boolean horarioValidoJanta;
	
	/* indica qual refeição foi autorizada */
	private boolean cafeAutorizado;
	private boolean almocoAutorizado;
	private boolean jantaAutorizado;
	
	private boolean segundaCafe;
	private boolean tercaCafe;
	private boolean quartaCafe;
	private boolean quintaCafe;
	private boolean sextaCafe;
	private boolean sabadoCafe;
	private boolean domingoCafe;
	
	private boolean segundaAlmoco;
	private boolean tercaAlmoco;
	private boolean quartaAlmoco;
	private boolean quintaAlmoco;
	private boolean sextaAlmoco;
	private boolean sabadoAlmoco;
	private boolean domingoAlmoco;

	private boolean segundaJantar;
	private boolean tercaJantar;
	private boolean quartaJantar;
	private boolean quintaJantar;
	private boolean sextaJantar;
	private boolean sabadoJantar;
	private boolean domingoJantar;

	/* dias do café */
	@Deprecated
	private boolean segundaC;
	@Deprecated
	private boolean tercaC;
	@Deprecated
	private boolean quartaC;
	@Deprecated
	private boolean quintaC;
	@Deprecated
	private boolean sextaC;
	@Deprecated
	private boolean sabadoC;
	@Deprecated
	private boolean domingoC;
	
	/* dias do almoço */
	@Deprecated
	private boolean segundaA;
	@Deprecated
	private boolean tercaA;
	@Deprecated
	private boolean quartaA;
	@Deprecated
	private boolean quintaA;
	@Deprecated
	private boolean sextaA;
	@Deprecated
	private boolean sabadoA;
	@Deprecated
	private boolean domingoA;

	/* dias da janta */
	@Deprecated
	private boolean segundaJ;
	@Deprecated
	private boolean tercaJ;
	@Deprecated
	private boolean quartaJ;
	@Deprecated
	private boolean quintaJ;
	@Deprecated
	private boolean sextaJ;
	@Deprecated
	private boolean sabadoJ;
	@Deprecated
	private boolean domingoJ;

	@Deprecated
	public String isDomingoA() {
		if (domingoA == true)
			return "<html> &nbsp;&nbsp;&nbsp; X </html>";

		return "";
	}

	@Deprecated
	public void setDomingoA(boolean domingoA) {
		this.domingoA = domingoA;
	}

	@Deprecated
	public String isDomingoJ() {
		if (domingoJ == true)
			return "<html> &nbsp;&nbsp;&nbsp; X </html>";

		return "";
	}

	@Deprecated
	public void setDomingoJ(boolean domingoJ) {
		this.domingoJ = domingoJ;
	}

	@Deprecated
	public String isQuartaA() {
		if (quartaA == true)
			return "<html> &nbsp;&nbsp;&nbsp; X </html>";

		return "";
	}

	@Deprecated
	public void setQuartaA(boolean quartaA) {
		this.quartaA = quartaA;
	}

	@Deprecated
	public String isQuartaJ() {
		if (quartaJ == true)
			return "<html> &nbsp;&nbsp;&nbsp; X </html>";

		return "";
	}

	@Deprecated
	public void setQuartaJ(boolean quartaJ) {
		this.quartaJ = quartaJ;
	}

	@Deprecated
	public String isQuintaA() {
		if (quintaA == true)
			return "<html> &nbsp;&nbsp;&nbsp; X </html>";

		return "";
	}

	@Deprecated
	public void setQuintaA(boolean quintaA) {
		this.quintaA = quintaA;
	}

	@Deprecated
	public String isQuintaJ() {
		if (quintaJ == true)
			return "<html> &nbsp;&nbsp;&nbsp; X </html>";

		return "";
	}

	@Deprecated
	public void setQuintaJ(boolean quintaJ) {
		this.quintaJ = quintaJ;
	}

	@Deprecated
	public String isSabadoA() {
		if (sabadoA == true)
			return "<html> &nbsp;&nbsp;&nbsp; X </html>";

		return "";
	}

	@Deprecated
	public void setSabadoA(boolean sabadoA) {
		this.sabadoA = sabadoA;
	}

	@Deprecated
	public String isSabadoJ() {
		if (sabadoJ == true)
			return "<html> &nbsp;&nbsp;&nbsp; X </html>";

		return "";
	}

	@Deprecated
	public void setSabadoJ(boolean sabadoJ) {
		this.sabadoJ = sabadoJ;
	}

	@Deprecated
	public String isSegundaA() {
		if (segundaA == true)
			return "<html> &nbsp;&nbsp;&nbsp; X </html>";

		return "";
	}

	@Deprecated
	public void setSegundaA(boolean segundaA) {
		this.segundaA = segundaA;
	}

	@Deprecated
	public String isSegundaJ() {
		if (segundaJ == true)
			return "<html> &nbsp;&nbsp;&nbsp; X </html>";

		return "";
	}

	@Deprecated
	public void setSegundaJ(boolean segundaJ) {
		this.segundaJ = segundaJ;
	}

	@Deprecated
	public String isSextaA() {
		if (sextaA == true)
			return "<html> &nbsp;&nbsp;&nbsp; X </html>";

		return "";
	}

	@Deprecated
	public void setSextaA(boolean sextaA) {
		this.sextaA = sextaA;
	}

	@Deprecated
	public String isSextaJ() {
		if (sextaJ == true)
			return "<html> &nbsp;&nbsp;&nbsp; X </html>";

		return "";
	}

	@Deprecated
	public void setSextaJ(boolean sextaJ) {
		this.sextaJ = sextaJ;
	}

	@Deprecated
	public String isTercaA() {
		if (tercaA == true)
			return "<html> &nbsp;&nbsp;&nbsp; X </html>";

		return "";
	}

	@Deprecated
	public void setTercaA(boolean tercaA) {
		this.tercaA = tercaA;
	}

	@Deprecated
	public String isTercaJ() {
		if (tercaJ == true)
			return "<html> &nbsp;&nbsp;&nbsp; X </html>";

		return "";
	}

	@Deprecated
	public void setTercaJ(boolean tercaJ) {
		this.tercaJ = tercaJ;
	}

	// CAFE

	@Deprecated
	public String isSegundaC() {
		if (segundaC  == true)
			return "<html> &nbsp;&nbsp;&nbsp; X </html>";

		return "";
	}

	@Deprecated
	public void setSegundaC(boolean segundaC) {
		this.segundaC = segundaC;
	}

	@Deprecated
	public String isTercaC() {
		if (tercaC  == true)
			return "<html> &nbsp;&nbsp;&nbsp; X </html>";

		return "";
	}

	@Deprecated
	public void setTercaC(boolean tercaC) {
		this.tercaC = tercaC;
	}

	@Deprecated
	public String isQuartaC() {
		if (quartaC  == true)
			return "<html> &nbsp;&nbsp;&nbsp; X </html>";

		return "";
	}

	@Deprecated
	public void setQuartaC(boolean quartaC) {
		this.quartaC = quartaC;
	}

	@Deprecated
	public String isQuintaC() {
		if (quintaC  == true)
			return "<html> &nbsp;&nbsp;&nbsp; X </html>";

		return "";
	}

	@Deprecated
	public void setQuintaC(boolean quintaC) {
		this.quintaC = quintaC;
	}

	@Deprecated
	public String isSextaC() {
		if (sextaC  == true)
			return "<html> &nbsp;&nbsp;&nbsp; X </html>";

		return "";
	}

	@Deprecated
	public void setSextaC(boolean sextaC) {
		this.sextaC = sextaC;
	}

	@Deprecated
	public String isSabadoC() {
		if (sabadoC  == true)
			return "<html> &nbsp;&nbsp;&nbsp; X </html>";

		return "";
	}

	@Deprecated
	public void setSabadoC(boolean sabadoC) {
		this.sabadoC = sabadoC;
	}

	@Deprecated
	public String isDomingoC() {
		if (domingoC  == true)
			return "<html> &nbsp;&nbsp;&nbsp; X </html>";

		return "";
	}

	@Deprecated
	public void setDomingoC(boolean domingoC) {
		this.domingoC = domingoC;
	}

	/*
	 * GETS/SETS...
	 */

	@Deprecated
	public boolean isDiscenteAcessouRefeicaoRU() {
		return discenteAcessouRefeicaoRU;
	}

	@Deprecated
	public void setDiscenteAcessouRefeicaoRU(boolean discenteAcessouRefeicaoRU) {
		this.discenteAcessouRefeicaoRU = discenteAcessouRefeicaoRU;
	}

	public boolean isAcessouRURefeicaoAtual() {
		return acessouRURefeicaoAtual;
	}

	public void setAcessouRURefeicaoAtual(boolean acessouRURefeicaoAtual) {
		this.acessouRURefeicaoAtual = acessouRURefeicaoAtual;
	}
	
	public String getCurso() {
		return curso;
	}

	public void setCurso(String curso) {
		this.curso = curso;
	}

	public long getMatricula() {
		return matricula;
	}

	public void setMatricula(long matricula) {
		this.matricula = matricula;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Deprecated
	public int getVinculo() {
		return vinculo;
	}

	@Deprecated
	public void setVinculo(int vinculo) {
		this.vinculo = vinculo;
	}

	public int getTipoBolsa() {
		return tipoBolsa;
	}

	public void setTipoBolsa(int tipoBolsa) {
		this.tipoBolsa = tipoBolsa;
	}
	
	public int getIdFoto() {
		return idFoto;
	}

	public void setIdFoto(int idFoto) {
		this.idFoto = idFoto;
	}

	public long getCpf() {
		return cpf;
	}

	public void setCpf(long cpf) {
		this.cpf = cpf;
	}

	public String getNomeDivisaoEstudantil() {
		return nomeDivisaoEstudantil;
	}

	public void setNomeDivisaoEstudantil(String nomeDivisaoEstudantil) {
		this.nomeDivisaoEstudantil = nomeDivisaoEstudantil;
	}

	public boolean isCafeAutorizado() {
		return cafeAutorizado;
	}

	public void setCafeAutorizado(boolean cafeAutorizado) {
		this.cafeAutorizado = cafeAutorizado;
	}

	public boolean isAlmocoAutorizado() {
		return almocoAutorizado;
	}

	public void setAlmocoAutorizado(boolean almocoAutorizado) {
		this.almocoAutorizado = almocoAutorizado;
	}

	public boolean isJantaAutorizado() {
		return jantaAutorizado;
	}

	public void setJantaAutorizado(boolean jantaAutorizado) {
		this.jantaAutorizado = jantaAutorizado;
	}

	public boolean isHorarioValidoCafe() {
		return horarioValidoCafe;
	}

	public void setHorarioValidoCafe(boolean horarioValidoCafe) {
		this.horarioValidoCafe = horarioValidoCafe;
	}

	public boolean isHorarioValidoAlmoco() {
		return horarioValidoAlmoco;
	}

	public void setHorarioValidoAlmoco(boolean horarioValidoAlmoco) {
		this.horarioValidoAlmoco = horarioValidoAlmoco;
	}

	public boolean isHorarioValidoJanta() {
		return horarioValidoJanta;
	}

	public void setHorarioValidoJanta(boolean horarioValidoJanta) {
		this.horarioValidoJanta = horarioValidoJanta;
	}

	public int getSituacaoBolsa() {
		return situacaoBolsa;
	}

	public void setSituacaoBolsa(int situacaoBolsa) {
		this.situacaoBolsa = situacaoBolsa;
	}

	public String getTipoVinculo() {
		return tipoVinculo;
	}

	public void setTipoVinculo( String tipoVinculo ) {
		this.tipoVinculo = tipoVinculo;
	}
	
	public boolean isSegundaCafe() {
		return segundaCafe;
	}
	
	public void setSegundaCafe( boolean segundaCafe ) {
		this.segundaCafe = segundaCafe;
	}
	
	public boolean isTercaCafe() {
		return tercaCafe;
	}

	public void setTercaCafe( boolean tercaCafe ) {
		this.tercaCafe = tercaCafe;
	}
	
	public boolean isQuartaCafe() {
		return quartaCafe;
	}
	
	public void setQuartaCafe( boolean quartaCafe ) {
		this.quartaCafe = quartaCafe;
	}
	
	public boolean isQuintaCafe() {
		return quintaCafe;
	}
	
	public void setQuintaCafe( boolean quintaCafe ) {
		this.quintaCafe = quintaCafe;
	}

	public boolean isSextaCafe() {
		return sextaCafe;
	}

	public void setSextaCafe( boolean sextaCafe ) {
		this.sextaCafe = sextaCafe;
	}
	
	public boolean isSabadoCafe() {
		return sabadoCafe;
	}
	
	public void setSabadoCafe( boolean sabadoCafe ) {
		this.sabadoCafe = sabadoCafe;
	}

	public boolean isDomingoCafe() {
		return domingoCafe;
	}
	
	public void setDomingoCafe( boolean domingoCafe ) {
		this.domingoCafe = domingoCafe;
	}

	public boolean isSegundaAlmoco() {
		return segundaAlmoco;
	}
	
	public void setSegundaAlmoco( boolean segundaAlmoco ) {
		this.segundaAlmoco = segundaAlmoco;
	}
	
	public boolean isTercaAlmoco() {
		return tercaAlmoco;
	}
	
	public void setTercaAlmoco( boolean tercaAlmoco ) {
		this.tercaAlmoco = tercaAlmoco;
	}

	public boolean isQuartaAlmoco() {
		return quartaAlmoco;
	}

	public void setQuartaAlmoco( boolean quartaAlmoco ) {
		this.quartaAlmoco = quartaAlmoco;
	}

	public boolean isQuintaAlmoco() {
		return quintaAlmoco;
	}

	public void setQuintaAlmoco( boolean quintaAlmoco ) {
		this.quintaAlmoco = quintaAlmoco;
	}

	public boolean isSextaAlmoco() {
		return sextaAlmoco;
	}

	public void setSextaAlmoco( boolean sextaAlmoco ) {
		this.sextaAlmoco = sextaAlmoco;
	}

	public boolean isSabadoAlmoco() {
		return sabadoAlmoco;
	}
	
	public void setSabadoAlmoco( boolean sabadoAlmoco ) {
		this.sabadoAlmoco = sabadoAlmoco;
	}

	public boolean isDomingoAlmoco() {
		return domingoAlmoco;
	}

	public void setDomingoAlmoco( boolean domingoAlmoco ) {
		this.domingoAlmoco = domingoAlmoco;
	}
	
	public boolean isSegundaJantar() {
		return segundaJantar;
	}
	
	public void setSegundaJantar( boolean segundaJantar ) {
		this.segundaJantar = segundaJantar;
	}

	public boolean isTercaJantar() {
		return tercaJantar;
	}
	
	public void setTercaJantar( boolean tercaJantar ) {
		this.tercaJantar = tercaJantar;
	}
	
	public boolean isQuartaJantar() {
		return quartaJantar;
	}

	public void setQuartaJantar( boolean quartaJantar ) {
		this.quartaJantar = quartaJantar;
	}

	public boolean isQuintaJantar() {
		return quintaJantar;
	}

	public void setQuintaJantar( boolean quintaJantar ) {
		this.quintaJantar = quintaJantar;
	}

	public boolean isSextaJantar() {
		return sextaJantar;
	}
	
	public void setSextaJantar( boolean sextaJantar ) {
		this.sextaJantar = sextaJantar;
	}

	public boolean isSabadoJantar() {
		return sabadoJantar;
	}
	
	public void setSabadoJantar( boolean sabadoJantar ) {
		this.sabadoJantar = sabadoJantar;
	}
	
	public boolean isDomingoJantar() {
		return domingoJantar;
	}
	
	public void setDomingoJantar( boolean domingoJantar ) {
		this.domingoJantar = domingoJantar;
	}
	
}