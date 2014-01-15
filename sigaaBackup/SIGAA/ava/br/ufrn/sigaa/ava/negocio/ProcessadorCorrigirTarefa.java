package br.ufrn.sigaa.ava.negocio;

import java.util.ArrayList;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;

public class ProcessadorCorrigirTarefa extends AbstractProcessador{

	public void validate(Movimento mov) throws NegocioException, ArqException 
	{
	}

	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException
	{
		MovimentoCorrigirTarefa mov = (MovimentoCorrigirTarefa) movimento;
		
		GenericDAO dao = null;
		AvaliacaoDao aDao = null;
		
		try {
			dao = getGenericDAO(mov);
			aDao = getDAO(AvaliacaoDao.class, mov);
					
			ArrayList<Avaliacao> avaliacoes = null;
			
			// Lista os discentes que não foram removidos do grupo.
			if ( mov.getResposta().getTarefa().isEmGrupo() && mov.getResposta().isExisteGrupo() ) {
				ArrayList<Integer> idsDiscentes = new ArrayList<Integer>();
				for ( Discente d : mov.getResposta().getGrupoDiscentes().getDiscentes() )
					if ( !d.isRemovidoGrupo() )
						idsDiscentes.add(d.getId());
				avaliacoes = (ArrayList<Avaliacao>) aDao.findByRespostasDiscentes(mov.getResposta(), idsDiscentes);
			}		
			else 
				avaliacoes = (ArrayList<Avaliacao>) aDao.findByRespostaTarefa(mov.getResposta());
			
			if (mov.getResposta().getTarefa().isPossuiNota() && (avaliacoes == null || avaliacoes.isEmpty()) )
				throw new NegocioException("Não existe avaliação associada a esta tarefa, " +
										"só é possível cadastrar a nota de uma tarefa associada a uma avaliação. " +
										"Por favor, edite a tarefa para possuir nota e a avaliação será criada automaticamente.");
			
			dao.update(mov.getResposta());
			
			if ( avaliacoes != null )
				for ( Avaliacao avaliacao : avaliacoes )
					dao.updateField(Avaliacao.class, avaliacao.getId(), "nota", mov.getResposta().getNota());

		} finally {
			if ( dao != null )
				dao.close();
			if ( aDao != null )
				aDao.close();
		}
		return mov.getResposta();
	}

}
