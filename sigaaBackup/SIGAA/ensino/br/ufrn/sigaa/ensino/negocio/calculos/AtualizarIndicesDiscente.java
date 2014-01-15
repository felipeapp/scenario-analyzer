/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 02/03/2010
 */
package br.ufrn.sigaa.ensino.negocio.calculos;

import java.util.Date;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.sigaa.arq.dao.graduacao.IndiceAcademicoDao;
import br.ufrn.sigaa.ensino.dominio.AtualizacaoIndiceAcademicoDiscente;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademico;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademicoDiscente;
import br.ufrn.sigaa.ensino.negocio.CalculoIndiceAcademico;

/**
 * Classe para calcular os índices acadêmicos dos discentes.
 * 
 * @author David Pereira
 *
 */
public abstract class AtualizarIndicesDiscente<T extends DiscenteAdapter> extends CalculosDiscenteChainNode<T> {

	public abstract char getNivelEnsino();
	
	@Override
	public void processar(T discente, Movimento mov, boolean preProcessamento) throws ArqException, NegocioException {
		
		IndiceAcademicoDao dao = getDAO(IndiceAcademicoDao.class);
		
		try {
			List<IndiceAcademico> indices = dao.findIndicesAtivos(getNivelEnsino());
			for (IndiceAcademico ia : indices) {
				// Calcula o índice
				CalculoIndiceAcademico calculo = ReflectionUtils.newInstance(ia.getClasse());
				double valor = calculo.calcular(discente.getDiscente(), mov);
				
				// Atualiza o índice do discente
				IndiceAcademicoDiscente iad = dao.findIndiceAcademicoDiscente(discente.getDiscente(), ia);
				if (iad == null) {
					iad = new IndiceAcademicoDiscente();
					iad.setDiscente(discente.getDiscente());
					iad.setIndice(ia);
				}
				
				iad.setValor(valor);
				dao.createOrUpdate(iad);
				
				// Registra a atualização do índice
				AtualizacaoIndiceAcademicoDiscente update = new AtualizacaoIndiceAcademicoDiscente();
				update.setIndice(iad);
				update.setData(new Date());
				update.setValor(valor);
				dao.create(update);
			}
			
		} finally {
			dao.close();
		}
		
	}

}
