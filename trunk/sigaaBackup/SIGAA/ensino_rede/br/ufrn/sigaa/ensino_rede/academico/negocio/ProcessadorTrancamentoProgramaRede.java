package br.ufrn.sigaa.ensino_rede.academico.negocio;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ensino_rede.academico.dominio.MovimentacaoDiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.DiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.StatusDiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.negocio.GrupoDiscentesAlterados;
import br.ufrn.sigaa.ensino_rede.negocio.MovimentoAlterarStatusMatriculaRede;
import br.ufrn.sigaa.ensino_rede.negocio.ProcessadorAlterarStatusDiscente;

public class ProcessadorTrancamentoProgramaRede extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException {

		MovimentoCadastro movC = (MovimentoCadastro) mov;

		MovimentacaoDiscenteAssociado ma = (MovimentacaoDiscenteAssociado) movC.getObjMovimentado();
		GrupoDiscentesAlterados grupoAlteracao = criarGrupoAlteracao(ma.getDiscente());
		
		MovimentoAlterarStatusMatriculaRede movA = new MovimentoAlterarStatusMatriculaRede();
		movA.addGrupo(grupoAlteracao);
		movA.setSistema(mov.getSistema());
		movA.setUsuarioLogado(mov.getUsuarioLogado());
		movA.setCodMovimento(mov.getCodMovimento());
		
		ProcessadorAlterarStatusDiscente proc = new ProcessadorAlterarStatusDiscente();
		proc.execute(movA);
		
		
		getGenericDAO(mov).create(ma);
		
		return null;
	}

	private GrupoDiscentesAlterados criarGrupoAlteracao(DiscenteAssociado discente) {
		GrupoDiscentesAlterados grupo = new GrupoDiscentesAlterados();
		grupo.setStatusNovo(new StatusDiscenteAssociado(StatusDiscenteAssociado.TRANCADO));
		grupo.setStatusAntigo(new StatusDiscenteAssociado(StatusDiscenteAssociado.ATIVO));
		grupo.addDiscente(discente);
		
		return grupo;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
	}

	
	
}