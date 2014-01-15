package br.ufrn.sigaa.ava.negocio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.MaterialTurmaDao;
import br.ufrn.sigaa.ava.dominio.MaterialTurma;


/**
 * Processador responsável por ordenar a exibição de materiais de um tópico
 * na página principal.
 * 
 * @author Ilueny Santos.
 *
 */
public class ProcessadorOrdenarMaterialTurma extends AbstractProcessador{


	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException	{
		
		MaterialTurma material = ((MovimentoCadastro) mov).getObjMovimentado();
		MaterialTurmaDao dao = getDAO(MaterialTurmaDao.class, mov);

		//Retornar uma lista de materiais com a nova ordem.
		List<MaterialTurma> result = new ArrayList<MaterialTurma>();

		try {

			material = dao.findByPrimaryKey(material.getId(), MaterialTurma.class, "id", "idMaterial", "topicoAula.id" ,"ordem", "nivel", "tipoMaterial");

			if (((MovimentoCadastro) mov).getCodMovimento().getId() == SigaaListaComando.ORDENAR_MATERIAL_TURMA_CIMA.getId()) {
				moverParaCima(material, dao);				
			}

			if (((MovimentoCadastro) mov).getCodMovimento().getId() == SigaaListaComando.ORDENAR_MATERIAL_TURMA_BAIXO.getId()) {
				moverParaBaixo(material, dao);
			}

			if (((MovimentoCadastro) mov).getCodMovimento().getId() == SigaaListaComando.ORDENAR_MATERIAL_TURMA_ESQUERDA.getId()) {				
				result.add(moverParaEsquerda(material, dao));
			}

			if (((MovimentoCadastro) mov).getCodMovimento().getId() == SigaaListaComando.ORDENAR_MATERIAL_TURMA_DIREITA.getId()) {
				result.add(moverParaDireita(material, dao));
			}

			if (((MovimentoCadastro) mov).getCodMovimento().getId() == SigaaListaComando.ORDENAR_MATERIAL_TURMA_TROCAR_POSICAO.getId()) {
				MaterialTurma material2 = (MaterialTurma) ((MovimentoCadastro) mov).getObjAuxiliar();
				material2 = dao.findByPrimaryKey(material2.getId(), MaterialTurma.class, "id", "idMaterial", "topicoAula.id", "ordem", "nivel", "tipoMaterial");
				result = mover(material, material2, dao);
			}

		} finally {
			dao.close();
		}

		return result;
	}


	/**
	 * Move o materialOrigem informado até o materialDestino de forma não sequencial
	 * não importando a quantidade de posições.
	 * 
	 * @param material
	 * @param dao
	 * @throws DAOException
	 */
	private List<MaterialTurma> mover(MaterialTurma materialOrigem, MaterialTurma materialDestino, MaterialTurmaDao dao)	throws DAOException {
		int ordemOrigem = materialOrigem.getOrdem();
		int ordemDestino = materialDestino.getOrdem();

		List<MaterialTurma> listaOrdenada = dao.findMateriaisByTopicoAulaOrdem(materialOrigem.getTopicoAula(), ordemOrigem, ordemDestino);

		//Se o deslocamento for positivo os materiais são movidos para baixo.
		int deslocamento = (ordemOrigem - ordemDestino > 0) ? 1 : -1;
		
		for (MaterialTurma mat : listaOrdenada) {
			if(mat.getId() == materialOrigem.getId()) {
				mat.setOrdem(ordemDestino);
			}else {
				mat.setOrdem(mat.getOrdem() + deslocamento);
			}
			dao.updateField(MaterialTurma.class, mat.getId(), "ordem", mat.getOrdem());
		}
		
		return listaOrdenada;		
	}

	/**
	 * Troca de posição com material que está abaixo do material informado.
	 * 
	 * @param material
	 * @param dao
	 * @throws DAOException
	 */
	private void moverParaBaixo(MaterialTurma material, MaterialTurmaDao dao)	throws DAOException {
		int maxOrdem = dao.countMateriaisByTopico(material.getTopicoAula());		
		if (material.getOrdem() < maxOrdem) {
			MaterialTurma materialBaixo = dao.findMaterialByVizinho(material, "inferior");
			trocarPosicao(material, materialBaixo, dao);
		}
		
	}

	/**
	 * Troca de posição com material que está acima do material informado.
	 * 
	 * @param material
	 * @param dao
	 * @throws DAOException
	 */
	private void moverParaCima(MaterialTurma material, MaterialTurmaDao dao)	throws DAOException {
		if (material.getOrdem() > 1) {
			MaterialTurma materialCima = dao.findMaterialByVizinho(material, "superior");
			trocarPosicao(material, materialCima, dao);
		}
		
	}

	/**
	 * Troca de posição com material que está abaixo do material informado.
	 * 
	 * @param material
	 * @param dao
	 * @throws DAOException
	 */
	private MaterialTurma moverParaEsquerda(MaterialTurma material, MaterialTurmaDao dao)	throws DAOException {
		if (material.getNivel() > 0) {
			material.setNivel(material.getNivel() - 1);
			dao.updateField(MaterialTurma.class, material.getId(), "nivel", material.getNivel());
		}
		return material;
	}

	/**
	 * Troca de posição com material que está acima do material informado.
	 * 
	 * @param material
	 * @param dao
	 * @throws DAOException
	 */
	private MaterialTurma moverParaDireita(MaterialTurma material, MaterialTurmaDao dao)	throws DAOException {
		material.setNivel(material.getNivel() + 1);
		dao.updateField(MaterialTurma.class, material.getId(), "nivel", material.getNivel());
		return material;
	}


	/**
	 * Troca de posição com material informado
	 * 
	 * @param material
	 * @param dao
	 * @throws DAOException
	 */
	private void trocarPosicao(MaterialTurma material1, MaterialTurma material2, MaterialTurmaDao dao)	throws DAOException {
		dao.updateField(MaterialTurma.class, material1.getId(), "ordem", material2.getOrdem());
		dao.updateField(MaterialTurma.class, material2.getId(), "ordem", material1.getOrdem());
	}

	
	/**
	 * Método utilizado na validação da operação de ordenação de materiais.
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
	}

}
