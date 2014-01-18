package br.ufrn.sigaa.arq.acesso;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.metropoledigital.dao.CoordenadorPoloIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.CoordenadorTutorIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TutoriaIMDDao;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;


/**
 * Processamento de permissões para acesso o módulo do IMD
 * 
 * @author Gleydson Lima
 *
 */
public class AcessoMetropoleDigital extends AcessoMenuExecutor {

	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {

		if (usuario.isUserInSubSistema(SigaaSubsistemas.METROPOLE_DIGITAL.getId())) {
			
			dados.setMetropoleDigital(true);
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.METROPOLE_DIGITAL, true));
			
			dados.incrementaTotalSistemas();
		}
		
		
		if (usuario.getVinculoAtivo().isVinculoTutorIMD()) {
			dados.setTutorIMD(true);
			dados.incrementaTotalSistemas();
			usuario.addPapelTemporario(SigaaPapeis.TUTOR_IMD);
			
		} else {
		
			//VERIFICAÇÃO SE O USUÁRIO É UM TUTOR DO IMD
			TutoriaIMDDao tutDao = new TutoriaIMDDao();
			try {
				if(tutDao.possuiTutoria(usuario.getPessoa().getId())){
					dados.setTutorIMD(true);
					//dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.METROPOLE_DIGITAL, true));
					dados.incrementaTotalSistemas();
					usuario.addPapelTemporario(SigaaPapeis.TUTOR_IMD);
				}
			} finally {
				tutDao.close();
			}
		}
		
		//VERIFICAÇÃO SE O USUÁRIO É UM COORDENADOR DE PÓLO DO IMD
		CoordenadorPoloIMDDao coordDao = new CoordenadorPoloIMDDao();
		try {
			if(coordDao.existeCoordenador(usuario.getPessoa().getId())){
				dados.setMetropoleDigital(true);
				//dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.METROPOLE_DIGITAL, true));
				dados.incrementaTotalSistemas();
				usuario.addPapelTemporario(SigaaPapeis.COORDENADOR_POLO_IMD);
			}
		} finally {
			coordDao.close();
		}
		
		//VERIFICAÇÃO SE O USUÁRIO É UM COORDENADOR DE TUTORES DO IMD
		CoordenadorTutorIMDDao coordTutorDao = new CoordenadorTutorIMDDao();
		try {
			if(coordTutorDao.existeCoordenador(usuario.getPessoa().getId())){
				dados.setMetropoleDigital(true);
				//dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.METROPOLE_DIGITAL, true));
				dados.incrementaTotalSistemas();
				usuario.addPapelTemporario(SigaaPapeis.COORDENADOR_TUTOR_IMD);
			}
		} finally {
			coordTutorDao.close();
		}
		
		
		
		
		if ( usuario.getDiscenteAtivo() !=null &&  usuario.getDiscenteAtivo().isRegular()) {
			
			List<Integer> cursos = (List<Integer>) Arrays.asList(  ParametroHelper.getInstance().getParametroIntegerArray(ParametrosTecnico.ID_CURSO_METROPOLE_DIGITAL) );
			
			if (  cursos.contains( usuario.getDiscenteAtivo().getCurso().getId() ) )  {
				dados.setDiscenteIMD(true);
			}
			
		}
		
		
	}
}