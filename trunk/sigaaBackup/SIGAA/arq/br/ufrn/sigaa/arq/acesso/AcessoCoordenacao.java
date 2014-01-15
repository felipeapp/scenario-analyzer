/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/02/2009
 *
 */
package br.ufrn.sigaa.arq.acesso;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Processamento de permissões para acesso das coordenações
 * 
 * @author Gleydson Lima, David Pereira
 *
 */
public class AcessoCoordenacao extends AcessoMenuExecutor {

	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {
		
		if (usuario.getVinculoAtivo().isVinculoServidor()) {
			
			CoordenacaoCursoDao coordCursoDao = getDAO(CoordenacaoCursoDao.class, req);
			Collection<Object[]> niveisUnidadesCoordenados = coordCursoDao.findNiveisCoordenadosServidor(usuario.getServidor().getId());

			// Collection que unifica os ids dos tipos de unidades coordenadas e os níveis
			ArrayList<Object> niveisUnidades = new ArrayList<Object>();

			
			for ( Object[] o : niveisUnidadesCoordenados ) {
				if ( o[0] != null ) niveisUnidades.add(o[0]);
				if ( o[1] != null ) niveisUnidades.add(o[1]);
			}
			
			
			if ( niveisUnidades.contains(NivelEnsino.GRADUACAO ) ) {
				
				dados.coordenadorCursoGrad = true;
				usuario.addPapelTemporario(new Papel(SigaaPapeis.COORDENADOR_CURSO));
				dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.PORTAL_COORDENADOR, true));
				dados.incrementaTotalSistemas();
				
			}
			
			if ( niveisUnidades.contains(TipoUnidadeAcademica.PROGRAMA_POS ) ) {
				
				dados.coordenadorCursoStricto = true;
				usuario.addPapelTemporario(new Papel(SigaaPapeis.COORDENADOR_CURSO_STRICTO));
				dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO, true));
				dados.incrementaTotalSistemas();
				
			}
			
			if ( niveisUnidades.contains(NivelEnsino.LATO ) ) {
				dados.coordenadorCursoLato = true;
				usuario.addPapelTemporario( new Papel(SigaaPapeis.COORDENADOR_LATO) );
				dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.PORTAL_COORDENADOR_LATO, true));
				dados.incrementaTotalSistemas();
			}
			
			if ( niveisUnidades.contains(NivelEnsino.TECNICO ) ) {
			
				dados.coordenadorCursoTecnico = true;
				usuario.addPapelTemporario(new Papel(SigaaPapeis.COORDENADOR_TECNICO));
				dados.incrementaTotalSistemas();
			}
			
			if ( niveisUnidades.contains(TipoUnidadeAcademica.PROGRAMA_RESIDENCIA )  ) {
			
				dados.complexoHospitalar = true;
				dados.coordenadorResidenciaMedica = true;
				usuario.addPapelTemporario(new Papel(SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA));
				dados.incrementaTotalSistemas();
				
			}
	
		}
	}

}
