/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 14/12/2007
 *
 */	
package br.ufrn.sigaa.arq.dao.ensino;

import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.ensino.dominio.PessoaInscricao;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.EstadoCivil;
import br.ufrn.sigaa.pessoa.dominio.Identidade;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.pessoa.dominio.TipoRaca;
import br.ufrn.sigaa.pessoa.dominio.TituloEleitor;

/**
 * DAO responsável pelas consultas a pessoas inscritas em
 * processos seletivos
 *
 * @author Ricardo Wendell
 *
 */
public class PessoaInscricaoDao extends GenericSigaaDAO {


	/**
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public PessoaInscricao findCompleto(int id) throws DAOException {

		PessoaInscricao pessoa = null;
		try {
			getSession().clear();
			pessoa = (PessoaInscricao) getSession().createCriteria(PessoaInscricao.class)
            	.add( Restrictions.idEq(id) )
            	.uniqueResult();

			if (pessoa == null) return null;

			pessoa.setEnderecoResidencial( (Endereco) HibernateUtils.getTarget( pessoa.getEnderecoResidencial() ));
			if (pessoa.getEnderecoResidencial() != null) {
				Endereco endereco = pessoa.getEnderecoResidencial();
				endereco.setMunicipio((Municipio) initializeClone( endereco.getMunicipio()) );
				if (endereco.getMunicipio() != null) {
					endereco.getMunicipio().setUnidadeFederativa( (UnidadeFederativa) HibernateUtils.getTarget( endereco.getMunicipio().getUnidadeFederativa() ) );
				}
				endereco.setPais( (Pais) HibernateUtils.getTarget(endereco.getPais()) );
				endereco.setUnidadeFederativa( (UnidadeFederativa) initializeClone(endereco.getUnidadeFederativa()) );
			}

			if (pessoa.getIdentidade() != null) {
				Identidade identidade = pessoa.getIdentidade();
				identidade.setUnidadeFederativa( (UnidadeFederativa) initializeClone( identidade.getUnidadeFederativa()) );
			}

			if (pessoa.getTituloEleitor() != null) {
				TituloEleitor titulo = pessoa.getTituloEleitor();
				titulo.setUnidadeFederativa( (UnidadeFederativa) initializeClone( titulo.getUnidadeFederativa()) );
			}

			pessoa.setEstadoCivil( (EstadoCivil) HibernateUtils.getTarget( pessoa.getEstadoCivil() ) );
			pessoa.setMunicipio((Municipio) initializeClone( pessoa.getMunicipio() ) );
			pessoa.setPais((Pais) HibernateUtils.getTarget( pessoa.getPais() ) );
			pessoa.setTipoRaca( (TipoRaca) HibernateUtils.getTarget( pessoa.getTipoRaca() ) );
			pessoa.setUnidadeFederativa( (UnidadeFederativa) initializeClone( pessoa.getUnidadeFederativa()) );

		} catch (Exception e) {
			throw new DAOException(e);
		}
		return pessoa;
	}
	
}
