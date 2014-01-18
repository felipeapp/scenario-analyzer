package br.ufrn.sigaa.ensino_rede.academico.negocio;

import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ensino_rede.academico.dominio.MovimentacaoDiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.StatusDiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.negocio.GrupoDiscentesAlterados;
import br.ufrn.sigaa.ensino_rede.negocio.MovimentoAlterarStatusMatriculaRede;
import br.ufrn.sigaa.ensino_rede.negocio.ProcessadorAlterarStatusDiscente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

public class ProcessadorRetornarTrancamentoProgramaRede extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoCadastro movC = (MovimentoCadastro) mov;
		MovimentacaoDiscenteAssociado movDiscente = movC.getObjMovimentado();
		
		movDiscente.setDataRetorno(new Date());
		movDiscente.setPessoaRetorno((Pessoa) movC.getObjAuxiliar());
		
		getGenericDAO(movC).updateField(MovimentacaoDiscenteAssociado.class, movDiscente.getId(), "dataRetorno", movDiscente.getDataRetorno());
		getGenericDAO(movC).updateField(MovimentacaoDiscenteAssociado.class, movDiscente.getId(), "pessoaRetorno", movDiscente.getPessoaRetorno());
		
		alterarStatus(movC);
		
		return null;
	}

	private void alterarStatus(MovimentoCadastro mov) throws NegocioException, ArqException {
		
		MovimentacaoDiscenteAssociado movDiscente = mov.getObjMovimentado();
		
		GrupoDiscentesAlterados grupo = new GrupoDiscentesAlterados();
		grupo.addDiscente(movDiscente.getDiscente());
		grupo.setStatusNovo(new StatusDiscenteAssociado(StatusDiscenteAssociado.ATIVO));
		
		MovimentoAlterarStatusMatriculaRede movA = new MovimentoAlterarStatusMatriculaRede();
		movA.addGrupo(grupo);
		movA.setCodMovimento(mov.getCodMovimento());
		movA.setSistema(mov.getSistema());
		movA.setUsuarioLogado(mov.getUsuarioLogado());
		
		ProcessadorAlterarStatusDiscente proc = new ProcessadorAlterarStatusDiscente();
		proc.execute(movA);
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}
