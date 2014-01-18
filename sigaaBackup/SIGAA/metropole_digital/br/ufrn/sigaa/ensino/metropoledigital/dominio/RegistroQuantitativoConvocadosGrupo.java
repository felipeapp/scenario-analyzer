package br.ufrn.sigaa.ensino.metropoledigital.dominio;

import br.ufrn.sigaa.ensino.tecnico.dominio.OpcaoPoloGrupo;
import br.ufrn.sigaa.ensino.tecnico.dominio.ProcessoSeletivoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.SituacaoCandidatoTecnico;

/**
 * 
 * Esta entidade ser� utilizada de forma auxiliar para armazenar os registros que conter�o a 
 * quantidade de discentes convocados para cada grupo e op��o p�lo grupo no processo seletivo do IMD.
 * 
 * ATEN��O! ESSA CLASSE N�O SER� PERSISTIDA NO BANCO DE DADOS.
 * 
 * @author Rafael Barros
 *
 */
public class RegistroQuantitativoConvocadosGrupo {

	/** Objeto do processo seletivo t�cnico vinculado ao registro */
	private ProcessoSeletivoTecnico processoSeletivo;
	
	/** Objeto da op��o p�lo grupo vinculada ao registro */
	private OpcaoPoloGrupo opcao; 
	
	/** Objeto da situa��o do candidato vinculada ao registro */
	private SituacaoCandidatoTecnico situacaoCandidato;
	
	/** Objeto do grupo de reserva de vagas vinculada ao registro */
	private ReservaVagaGrupo reservaVagaGrupo;
	
	/** Quantidade de candidatos convocados como APROVADOS vinculado ao registro */
	private int qtdConvocada;
	
	public RegistroQuantitativoConvocadosGrupo(){
		
	}

	public ProcessoSeletivoTecnico getProcessoSeletivo() {
		return processoSeletivo;
	}

	public void setProcessoSeletivo(ProcessoSeletivoTecnico processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}

	public OpcaoPoloGrupo getOpcao() {
		return opcao;
	}

	public void setOpcao(OpcaoPoloGrupo opcao) {
		this.opcao = opcao;
	}

	public SituacaoCandidatoTecnico getSituacaoCandidato() {
		return situacaoCandidato;
	}

	public void setSituacaoCandidato(SituacaoCandidatoTecnico situacaoCandidato) {
		this.situacaoCandidato = situacaoCandidato;
	}

	public ReservaVagaGrupo getReservaVagaGrupo() {
		return reservaVagaGrupo;
	}

	public void setReservaVagaGrupo(ReservaVagaGrupo reservaVagaGrupo) {
		this.reservaVagaGrupo = reservaVagaGrupo;
	}

	public int getQtdConvocada() {
		return qtdConvocada;
	}

	public void setQtdConvocada(int qtdConvocada) {
		this.qtdConvocada = qtdConvocada;
	} 
	
}
