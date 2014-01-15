package br.ufrn.sigaa.cv.negocio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.cv.dominio.ComunidadeVirtual;
import br.ufrn.sigaa.cv.dominio.TopicoComunidade;

/**
 * Processador responsável por mover tópicos de aula.
 * 
 * @author Diego Jácome.
 *
 */
public class ProcessadorMoverTopicos extends AbstractProcessador{

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoCadastroCv cmov = (MovimentoCadastroCv) mov;
		TopicoComunidade topicoOrigem = cmov.getObjMovimentado();
		TopicoComunidade topicoDestino = (TopicoComunidade) cmov.getObjAuxiliar();
		ComunidadeVirtual comunidade = cmov.getComunidade();
		GenericDAO dao = null;
		
		try {
			
			dao = getGenericDAO(mov);
			topicoOrigem = dao.findByPrimaryKey(topicoOrigem.getId(), TopicoComunidade.class);
			topicoDestino = dao.findByPrimaryKey(topicoDestino.getId(), TopicoComunidade.class);
			List<TopicoComunidade> topicosOrdenados = TopicoComunidadeHelper.getTopicosOrdenados(comunidade);
			
			int ordemOrigem = topicoOrigem.getOrdem();
			int ordemDestino = topicoDestino.getOrdem();
			//Se o deslocamento for positivo os materiais são movidos para baixo.
			int deslocamento = (ordemOrigem - ordemDestino > 0) ? 1 : -1;			
			
			List<TopicoComunidade> topicosParaOrdenar = new ArrayList<TopicoComunidade>();
			
			for ( TopicoComunidade tc : topicosOrdenados ){
				if ( deslocamento == -1 && (tc.getOrdem() >= ordemOrigem && tc.getOrdem() <= ordemDestino) )
					topicosParaOrdenar.add(tc);
				if ( deslocamento == 1 && (tc.getOrdem() <= ordemOrigem && tc.getOrdem() >= ordemDestino) )
					topicosParaOrdenar.add(tc);
			}
			
			for (TopicoComunidade tc : topicosParaOrdenar) {
				
				if(tc.getId() == topicoOrigem.getId()) {
					tc.setOrdem(ordemDestino);
				}else {
					tc.setOrdem(tc.getOrdem() + deslocamento);
				}
				dao.updateField(TopicoComunidade.class, tc.getId(), "ordem", tc.getOrdem());
			}
			
		}finally {
			if ( dao != null )
				dao.close();
		}
		
		return null;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// TODO Auto-generated method stub
		
	}

}
