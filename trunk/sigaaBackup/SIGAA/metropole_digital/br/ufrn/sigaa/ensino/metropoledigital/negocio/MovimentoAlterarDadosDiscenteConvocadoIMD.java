package br.ufrn.sigaa.ensino.metropoledigital.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.ReservaVagaCandidato;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.InscricaoProcessoSeletivoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.PessoaTecnico;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Movimento responsável por armazenar e processar os objetos que serão alterados nos dados pessoais dos discentes convocados do IMD
 * 
 * @author Rafael Barros
 *
 */
public class MovimentoAlterarDadosDiscenteConvocadoIMD extends AbstractMovimentoAdapter {

	/** Inscrição do processo seletivo tecnico a ser alterada*/
	private InscricaoProcessoSeletivoTecnico inscricao = new InscricaoProcessoSeletivoTecnico();
	
	/** Pessoa tecnico a ser alterada*/
	private PessoaTecnico pessoaTecnico = new PessoaTecnico();
	
	/** Pessoa a ser alterada*/
	private Pessoa pessoa = new Pessoa();
	
	/** Reserva Vaga Candidato a ser alterada*/
	private ReservaVagaCandidato reservaCandidato = new ReservaVagaCandidato();
	
	/** Discente tecnico a ser alterado*/
	private DiscenteTecnico discenteTecnico = new DiscenteTecnico();
	
	/**CPF atual correspondente ao discente a ser alterado*/
	private long cpfCnpjAtual;

	public InscricaoProcessoSeletivoTecnico getInscricao() {
		return inscricao;
	}

	public void setInscricao(InscricaoProcessoSeletivoTecnico inscricao) {
		this.inscricao = inscricao;
	}

	public PessoaTecnico getPessoaTecnico() {
		return pessoaTecnico;
	}

	public void setPessoaTecnico(PessoaTecnico pessoaTecnico) {
		this.pessoaTecnico = pessoaTecnico;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public ReservaVagaCandidato getReservaCandidato() {
		return reservaCandidato;
	}

	public void setReservaCandidato(ReservaVagaCandidato reservaCandidato) {
		this.reservaCandidato = reservaCandidato;
	}

	public DiscenteTecnico getDiscenteTecnico() {
		return discenteTecnico;
	}

	public void setDiscenteTecnico(DiscenteTecnico discenteTecnico) {
		this.discenteTecnico = discenteTecnico;
	}

	public long getCpfCnpjAtual() {
		return cpfCnpjAtual;
	}

	public void setCpfCnpjAtual(long cpfCnpjAtual) {
		this.cpfCnpjAtual = cpfCnpjAtual;
	}
	
	
	
}
