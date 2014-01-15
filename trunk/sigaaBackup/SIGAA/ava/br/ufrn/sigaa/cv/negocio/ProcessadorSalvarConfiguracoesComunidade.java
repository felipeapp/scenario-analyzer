package br.ufrn.sigaa.cv.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.cv.dominio.ComunidadeVirtual;
import br.ufrn.sigaa.cv.dominio.ConfiguracoesComunidadeVirtual;
import br.ufrn.sigaa.cv.dominio.TopicoComunidade;

/**
 * Processador responsável por salvar as configurações de uma comunidade virtual
 * 
 * @author Diego Jácome.
 *
 */
public class ProcessadorSalvarConfiguracoesComunidade extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException {

		GenericDAO dao = null;
		MovimentoCadastroCv cmov = (MovimentoCadastroCv) mov;
		ConfiguracoesComunidadeVirtual config = cmov.getObjMovimentado();
		ComunidadeVirtual comunidade = cmov.getComunidade();
		config.setComunidade(comunidade);
		List<TopicoComunidade> topicos = TopicoComunidadeHelper.getTopicosOrdenados(comunidade);
		
		try{
			
			dao = getGenericDAO(cmov);
			
			if ( config.getOrdemTopico().equals(ConfiguracoesComunidadeVirtual.ORDEM_TOPICO_LIVRE) ){			
				// Registra a ordem dos tópicos.
				int i = 0;
				if ( !isEmpty(topicos) )
					for ( TopicoComunidade tc : topicos ) {
						dao.updateField(TopicoComunidade.class, tc.getId(), "ordem", i);
						i++;
					}
			}else {
				// Remove a ordem, dos tópicos - pois eles serão ordenados pela data.
				if ( !isEmpty(topicos) )
					for ( TopicoComunidade tc : topicos ) 
						dao.updateField(TopicoComunidade.class, tc.getId(), "ordem", null);	
			}

			if ( config.getId() > 0 )
				dao.update(config);
			else
				dao.create(config);
			
		}finally {
			if (dao != null)
				dao.close();
		}
		
		return null;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// TODO Auto-generated method stub
		
	}

}
