/*
 * ListaEtiquetas.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendencia de Informatica
 * Diretoria de Sistemas
 * Campos Universitario Lagoa Nova
 * Natal - RN - Brasil
 *
 * Este software eh confidencial e de propriedade intelectual da
 * UFRN - Universidade Federal do Rio Grande no Norte
 * Nao se deve utilizar este produto em desacordo com as normas
 * da referida instituicao.
 */
package br.ufrn.sigaa.biblioteca.util;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.biblioteca.EtiquetaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TipoCatalogacao;

/**
 *
 *    Objeto que mantém uma lista de etiquetas em memória para otimizar a busca no banco.
 *
 *    Usado na interpretação dos arquivo de importação. Serve tanto para bibliográficas como para 
 * etiquetas de autoridades.
 *
 * @author Jadson
 * @since 14/11/2008
 * @version 1.0 criação da classe
 *
 */
public class ListaEtiquetas {

	/** Guarda as etiquetas buscadas na memória para otimizar um pouco. */
	private List<Etiqueta> etiquetas = new ArrayList<Etiqueta>();

	/** Dao usado para recuperar as etiquetas.*/
	private EtiquetaDao dao;

	public ListaEtiquetas() throws DAOException{
		dao = DAOFactory.getInstance().getDAO(EtiquetaDao.class);
	}

	/**
	 *    Método que busca a etiqueta da tag passada e mantém essa etiqueta em memória caso precise
	 *    buscá-la novamente
	 *   
	 *    Só traz uma porque só era para existir uma por etiqueta, existe uma restrição no banco.
	 */
	public Etiqueta getEtiqueta(String tag, short tipo) throws DAOException, NegocioException{

		if (etiquetas.contains(new Etiqueta(tag, tipo)))
			return etiquetas.get(etiquetas.indexOf(new Etiqueta(tag, tipo)));
		else {
			
			Etiqueta e = dao.findEtiquetaPorTagETipoAtiva(tag, tipo);

			if(e == null){
				
				switch (tipo) {
				case TipoCatalogacao.BIBLIOGRAFICA:
					throw new NegocioException("O campo '"+tag+"' Bibliográfico não está cadastrado no sistema.");
				case TipoCatalogacao.AUTORIDADE:
					throw new NegocioException("O campo '"+tag+"' de Autoridades não está cadastrado no sistema.");
				// Os demais tipos se algum dia forem utilizados	
				default:
					throw new NegocioException("O campo "+tag+" não está cadastrado no sistema.");
				}	
			}	
				
			etiquetas.add(e);

			return e;
		}
	}

	/**
	 * Método para fechar o DAO depois que todas as etiquetas foram recuperadas.
	 *
	 * @void
	 */
	public void fechaConexao(){
		try{
			
		}finally{
			if(dao != null) dao.close();
		}
	}

}
