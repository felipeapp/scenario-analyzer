package br.ufrn.sigaa.arq.vinculo.tipos;

/**
 * Classe abstrata para fazer implementações default em {@link TipoVinculo}
 * 
 * @author Henrique André
 */
public abstract class TipoVinculoAbstract implements TipoVinculo {
	
	@Override
	public boolean isDiscente() {
		return false;
	}

	@Override
	public boolean isServidor() {
		return false;
	}

	@Override
	public boolean isSecretario() {
		return false;
	}

	@Override
	public boolean isResponsavel() {
		return false;
	}

	@Override
	public boolean isDocenteExterno() {
		return false;
	}

	@Override
	public boolean isTutor() {
		return false;
	}

	@Override
	public boolean isCoordenacaoPolo() {
		return false;
	}

	@Override
	public boolean isConcedenteEstagio() {
		return false;
	}

	@Override
	public boolean isFamiliar() {
		return false;
	}

	@Override
	public boolean isCoordenadorGeralRede() {
		return false;
	}
	
	@Override
	public boolean isCoordenadorUnidadeRede() {
		return false;
	}
	
	@Override
	public boolean isTutorIMD() {
		return false;		
	}
	
}
