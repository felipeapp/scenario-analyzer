/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/04/2011
 *
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.ServicosInformacaoReferenciaBiblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.TipoServicoInformacaoReferencia;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 * DAO que gerencia os servicos das bibliotecas.
 *
 * @author Felipe Rivas
 */
public class ServicoInformacaoReferenciaBibliotecaDAO extends GenericSigaaDAO {
	
	/**
	 * Retorna somente as bibliotecas do sistema que prestam o serviço solicitado.
	 */
	public List <Biblioteca> findBibliotecasInternasByServico(
			TipoServicoInformacaoReferencia servico, boolean apenasCentral, boolean apenasSetoriais)
			throws DAOException {
		
		Criteria c = getSession().createCriteria(ServicosInformacaoReferenciaBiblioteca.class);
		
		c.createAlias("biblioteca", "b");
		
		c.add(Restrictions.isNotNull("b.unidade"));
		
		if(apenasCentral)
			c.add(Restrictions.eq("b.id", BibliotecaUtil.getIdBibliotecaCentral()));
		
		if(apenasSetoriais)
			c.add(Restrictions.ne("b.id", BibliotecaUtil.getIdBibliotecaCentral()));
		
		c.add(Restrictions.eq("b.ativo", true));

		switch (servico){
			case LEVANTAMENTO_BIBLIOGRAFICO:
				c.add(Restrictions.eq("realizaLevantamentoBibliografico", true)); break;
			case CATALOGACAO_NA_FONTE:
				c.add(Restrictions.eq("realizaCatalogacaoNaFonte", true)); break;
			case ORIENTACAO_NORMALIZACAO:
				c.add(Restrictions.eq("realizaOrientacaoNormalizacao", true)); break;
			case LEVANTAMENTO_INFRA_ESTRUTURA:
				c.add(Restrictions.eq("realizaLevantamentoInfraEstrutura", true)); break;
			case NORMALIZACAO:
				c.add(Restrictions.eq("realizaNormalizacao", true)); break;
		}
		
		c.setProjection(Projections.property("biblioteca"));
		
		@SuppressWarnings("unchecked")
		List<Biblioteca> lista = c.list();
		return lista;
	}
	
}