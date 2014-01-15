package br.ufrn.sigaa.ava.negocio;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.ava.dao.GrupoDiscentesDao;
import br.ufrn.sigaa.ava.dominio.GrupoDiscentes;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe auxiliar para grupo de discentes.
 * 
 * @author Diego Jácome
 *
 */
public class GrupoDiscentesHelper {

	/** 
	 * Verifica se um discente pertence as grupo filho ou se ele foi removido do grupo.
	 * 
	 * @param g
	 * @param d
	 * @return
	 * @throws DAOException
	 */
	public static boolean discentePertenceGruposFilhos ( GrupoDiscentes g , Discente d , Date dataFim ) throws DAOException {
		
		GrupoDiscentesDao dao = null;
		GrupoDiscentes grupoFilho = null;
		try {
			dao = DAOFactory.getInstance().getDAO(GrupoDiscentesDao.class);
			boolean res = false;
			
			while ( g != null ){
						
				grupoFilho = dao.findByExactField(GrupoDiscentes.class, "grupoPai.id",g.getId(), true);
				
				if ( g.getDiscentes() == null )
					res = false;
				else if ( g.getDiscentes().contains(d))
					res = true;
				else if ( !g.getDiscentes().contains(d) )
					res = false;
				
				if ( g.getDataAtualizacao().after(dataFim) )
					break;
				
				g = grupoFilho;
			}	

			return res;
		} finally {
			if ( dao != null )
				dao.close();
		}
		
	}
	
	/**
	 * Carrega todos os grupos criados na turma.
	 * @param t
	 * @return
	 * @throws DAOException
	 */
	public static List<GrupoDiscentes> carregarGrupos ( Turma t ) throws DAOException {
		
		GrupoDiscentesDao dao = null;
		try {
			dao = DAOFactory.getInstance().getDAO(GrupoDiscentesDao.class);
			ArrayList<GrupoDiscentes> todosGrupos = (ArrayList<GrupoDiscentes>) dao.findAllByTurma(t.getId());
			ArrayList<GrupoDiscentes> grupos = null;
			
			if ( todosGrupos != null && !todosGrupos.isEmpty()){
				grupos = new ArrayList<GrupoDiscentes>();
				Map<Integer,GrupoDiscentes> mapaGrupo = new HashMap<Integer,GrupoDiscentes>(); 
						
				for ( GrupoDiscentes g : todosGrupos ){
					if ( g.isAtivo() )
						grupos.add(g);
					mapaGrupo.put(g.getId(), g);	
				}	
				
				for ( GrupoDiscentes g : grupos )					
					carregarPais(g,mapaGrupo);			
			}
			
			return grupos;
		} finally {
			if ( dao != null )
				dao.close();
		}
	}
	
	/**
	 * Carrega os grupos pais de um determinado grupo.
	 * @param g
	 * @param mapaGrupo
	 */
	private static void carregarPais ( GrupoDiscentes g , Map<Integer, GrupoDiscentes> mapaGrupo ){
		if ( g.getGrupoPai() != null ){
			GrupoDiscentes pai = mapaGrupo.get( g.getGrupoPai().getId() );
			g.setGrupoPai(pai);
			pai.setGrupoFilho(g);
			carregarPais(pai, mapaGrupo);
		}				
	}
	
	/** 
	 * Carrega todos os ancestrais de um grupo, passando um lista com todos os grupos de uma turma.
	 * @param g
	 * @param grupos
	 */
	public static void carregarAncestrais ( GrupoDiscentes g , List<GrupoDiscentes> grupos ){
		
		if ( g != null && grupos != null ){
			for ( GrupoDiscentes comPai : grupos )
				if ( comPai.getId() == g.getId() )
					g.setGrupoPai(comPai.getGrupoPai());
		}
	}

	/** 
	 * Verifica se um grupo é ancestral de outro.
	 * @param grupoAtivo
	 * @param ancestral
	 * @param grupos
	 * @return
	 */
	public static boolean isAncestral ( GrupoDiscentes grupoAtivo , GrupoDiscentes ancestral , List<GrupoDiscentes> grupos ){
		
		carregarAncestrais(grupoAtivo,grupos);
		GrupoDiscentes g = grupoAtivo;
		while ( g != null ){
			if (g.getId() == ancestral.getId())
				return true;
			g = g.getGrupoPai();
		}
		return false;
	}
	
}
