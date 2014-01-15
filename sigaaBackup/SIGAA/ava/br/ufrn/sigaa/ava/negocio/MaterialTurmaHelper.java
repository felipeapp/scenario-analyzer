/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '30/05/2011'
 *
 */
package br.ufrn.sigaa.ava.negocio;

import java.util.Collections;
import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ava.dao.MaterialTurmaDao;
import br.ufrn.sigaa.ava.dominio.AbstractMaterialTurma;
import br.ufrn.sigaa.ava.dominio.ArquivoTurma;
import br.ufrn.sigaa.ava.dominio.ConteudoTurma;
import br.ufrn.sigaa.ava.dominio.IndicacaoReferencia;
import br.ufrn.sigaa.ava.dominio.MaterialTurma;
import br.ufrn.sigaa.ava.dominio.TipoMaterialTurma;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.dominio.VideoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Classe com métodos auxiliares para gestão do material da turma.
 * 
 * @author ilueny santos
 *
 */
public class MaterialTurmaHelper {
	
	/** 
	 * Determina o novo material para o tópico e turma informados. 
	 * @throws DAOException 
	 */
	public static void definirNovoMaterialParaTopico(AbstractMaterialTurma obj, TopicoAula topicoAula, Turma turma ) throws DAOException {
		MaterialTurmaDao dao = DAOFactory.getInstance().getDAO(MaterialTurmaDao.class);
		try {
			//Alguns materiais podem não ser vinculados diretamente a tópicos de aula.
			if (ValidatorUtil.isNotEmpty(topicoAula)) {
				obj.getMaterial().setTopicoAula(topicoAula);
				int maxOrdem = dao.countMateriaisByTopico(topicoAula);
				obj.getMaterial().setOrdem(maxOrdem + 1);
			}
			
			// Todos os materiais devem estar vinculados a uma turma.
			obj.getMaterial().setTurma(turma);
			obj.getMaterial().setIdMaterial(obj.getId());
		}finally {
			dao.close();
		}
	}
	

	/**
	 * Atualiza os dados do material informado.
	 * Ex. ordem, tópico de aula associado ao material, etc.
	 * 
	 * @param dao
	 * @param materialTurma
	 * @throws DAOException
	 */
	public static void atualizarMaterial(GenericDAO dao, AbstractMaterialTurma obj, boolean cadastro) throws DAOException {
				
		if (cadastro) {
			atualizarMaterialCadastro(dao, obj);
		} else {
			atualizarMaterialAlteracao(obj);
		}
	}
	
	/**
	 * Atualiza o material turma associado ao arquivo ao cadastrar.
	 * 
	 * @param  dao
	 * @param materialTurma
	 * @throws DAOException
	 */
	private static void atualizarMaterialCadastro(GenericDAO dao, AbstractMaterialTurma obj) throws DAOException {
		if (obj.getMaterial() != null) {
//			dao.updateField(MaterialTurma.class, obj.getMaterial().getId(), "idMaterial", obj.getId());
			obj.getMaterial().setIdMaterial(obj.getId());
			dao.update(obj.getMaterial());
		}
	}
	
	/**
	 * Atualiza o material turma associado ao arquivo ao alterar.
	 * 
	 * @param dao
	 * @param materialTurma
	 * @throws DAOException
	 */
	private static void atualizarMaterialAlteracao(AbstractMaterialTurma obj) throws DAOException {
		if (ValidatorUtil.isNotEmpty(obj.getMaterial()) &&  ValidatorUtil.isNotEmpty(obj.getAula())) {
			//Verificando se o material mudou de tópico.
			if (obj.getAula().getId() != obj.getMaterial().getTopicoAula().getId()) {
				MaterialTurmaDao dao = DAOFactory.getInstance().getDAO(MaterialTurmaDao.class);
				try{			
					//Material alterado vai para o final da lista de materiais do novo tópico.
					int maxOrdem = dao.countMateriaisByTopico(obj.getAula()) + 1;
					
					obj.getMaterial().setId(obj.getId());
					obj.getMaterial().setOrdem(maxOrdem);
					obj.getMaterial().getTopicoAula().setId(obj.getAula().getId());
					
					dao.update(obj.getMaterial());
					
//					dao.updateFields(MaterialTurma.class, obj.getMaterial().getId(), 
//							new String [] {"idMaterial", "topicoAula.id" , "ordem"},
//							new Object [] {obj.getId() , obj.getAula().getId(), maxOrdem});

					//Lista de materiais do tópico antigo deve ser reordenada.
					reOrdenarMateriais(obj.getMaterial().getTopicoAula());
				}finally {
					dao.close();
				}
			}
		}
	}

	/**
	 * Atualiza a nova ordem dos materiais do tópico.
	 * 
	 * @param dao
	 * @param materialTurma
	 * @throws DAOException
	 */
	public static void reOrdenarMateriais(TopicoAula topicoAula) throws DAOException {
		if (ValidatorUtil.isNotEmpty(topicoAula)) {
			MaterialTurmaDao dao = DAOFactory.getInstance().getDAO(MaterialTurmaDao.class);
			try{
				List<MaterialTurma> materiaisTopico = dao.findMateriaisByTopicoAula(topicoAula);
				materiaisTopico = reOrdenarMateriais(materiaisTopico);
				for (MaterialTurma m : materiaisTopico) {
					dao.updateField(MaterialTurma.class, m.getId(), "ordem", m.getOrdem());	
				}
			}finally {
				dao.close();
			}
		} 
	}
	
	/**
	 * Reordena os materiais ativos informados.
	 * 
	 * @param materiais
	 */
	private static List<MaterialTurma> reOrdenarMateriais (final List<MaterialTurma> materiais){
		if (materiais != null) {
			Collections.sort(materiais);
			int ordem = 1;
			for (MaterialTurma m : materiais) {
				if (m.isAtivo()) {
					m.setOrdem(ordem++);
				}
			}	
		}
		return materiais;
	}
	
	/**
	 * Cria um novo material para um objeto.
	 * 
	 * @param materialTurma
	 * @param materialTurma
	 * @throws DAOException
	 */
	public static void instanciaMaterial(AbstractMaterialTurma materialTurma) throws DAOException {		
		if ( materialTurma.isTipoConteudo() ) {
			ConteudoTurma c = (ConteudoTurma) materialTurma;
			c.setMaterial(new MaterialTurma(TipoMaterialTurma.CONTEUDO));
		} else if ( materialTurma.isTipoArquivo() ) {
			ArquivoTurma a = (ArquivoTurma) materialTurma;
			a.setMaterial(new MaterialTurma(TipoMaterialTurma.ARQUIVO));
		} else if ( materialTurma.isTipoIndicacao() ) {
			IndicacaoReferencia r = (IndicacaoReferencia) materialTurma;
			r.setMaterial(new MaterialTurma(TipoMaterialTurma.REFERENCIA));
		} else if ( materialTurma.isTipoVideo() ) {
			VideoTurma r = (VideoTurma) materialTurma;
			r.setMaterial(new MaterialTurma(TipoMaterialTurma.VIDEO));
		}
	}
	
}
