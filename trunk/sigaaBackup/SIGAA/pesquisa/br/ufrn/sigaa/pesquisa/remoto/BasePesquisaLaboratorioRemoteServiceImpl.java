package br.ufrn.sigaa.pesquisa.remoto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jws.WebService;

import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.web.jsf.AbstractController;
import br.ufrn.integracao.dto.BasePesquisaLaboratorioDto;
import br.ufrn.integracao.interfaces.BasePesquisaLaboratorioRemoteService;
import br.ufrn.sigaa.arq.dao.pesquisa.GrupoPesquisaDao;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;


/** 
 * Classe responsável pelo acesso remoto às bases de pesquisa no SIGAA. Este serviço é utilizado pelo SIPAC no
 * cadastro de laboratório.
 * <br/>
 * @author Mychell Teixeira
 *
 */
@WebService
@Component("basePesquisaLaboratorio")
public class BasePesquisaLaboratorioRemoteServiceImpl extends AbstractController implements BasePesquisaLaboratorioRemoteService {

	private static final long serialVersionUID = 1L;

	@Override
	public List<BasePesquisaLaboratorioDto> getBasesPesquisaLaboratorio( String nome ) {
		GrupoPesquisaDao grupoPesquisaDao = null;
		try {
			grupoPesquisaDao = new GrupoPesquisaDao();
			List<GrupoPesquisa> lista = (List<GrupoPesquisa>) grupoPesquisaDao.findByLikeField( GrupoPesquisa.class, "nome", nome );
			List<BasePesquisaLaboratorioDto> listaBasePesquisaDto =  new ArrayList<BasePesquisaLaboratorioDto>();
			
			for( GrupoPesquisa grupo : lista ) {
				BasePesquisaLaboratorioDto basePesquisa = new BasePesquisaLaboratorioDto();
				basePesquisa.setId(  grupo.getId() );
				basePesquisa.setCodigo( grupo.getCodigo() );
				basePesquisa.setEmail( grupo.getEmail() );
				basePesquisa.setHomePage( grupo.getHomePage() );
				basePesquisa.setNome( grupo.getNome() );
				listaBasePesquisaDto.add( basePesquisa );
			}
			
			return listaBasePesquisaDto;
		} catch (DAOException e) {
			throw new RemoteAccessException(e.getMessage());
		} finally { 
			if ( grupoPesquisaDao != null)
				grupoPesquisaDao.close();
		}
	}

	@Override
	public BasePesquisaLaboratorioDto findBasePesquisaLaboratorio( Integer idGrupoPesquisa ) {
		GrupoPesquisaDao grupoPesquisaDao = null;
		if( idGrupoPesquisa == null ) {
			return new BasePesquisaLaboratorioDto();
		}
		try {
			grupoPesquisaDao = new GrupoPesquisaDao();
			BasePesquisaLaboratorioDto basePesquisa = new BasePesquisaLaboratorioDto();

		 	GrupoPesquisa grupo = grupoPesquisaDao.findByPrimaryKey( idGrupoPesquisa, GrupoPesquisa.class );
		 	if( grupo == null ) {
		 		return new BasePesquisaLaboratorioDto();
		 	}
			basePesquisa.setId(  grupo.getId() );
			basePesquisa.setCodigo( grupo.getCodigo() );
			basePesquisa.setEmail( grupo.getEmail() );
			basePesquisa.setHomePage( grupo.getHomePage() );
			basePesquisa.setNome( grupo.getNome() );
			return  basePesquisa;
		} catch (DAOException e) {
			throw new RemoteAccessException(e.getMessage());
		} finally { 
			if ( grupoPesquisaDao != null)
				grupoPesquisaDao.close();
		}
	}

}
