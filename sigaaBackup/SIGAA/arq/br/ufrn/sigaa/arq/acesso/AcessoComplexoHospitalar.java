/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 02/07/2009 
 */
package br.ufrn.sigaa.arq.acesso;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConfiguracaoAmbienteException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Processamento de permissões para acesso ao módulo
 * do Complexo Hospitalar
 * 
 * @author Ricardo Wendell
 *
 */
public class AcessoComplexoHospitalar extends AcessoMenuExecutor {

	@Override
	public void processar(DadosAcesso dados, Usuario usuario,
			HttpServletRequest req) throws ArqException {
		
		if (usuario.isUserInSubSistema(SigaaSubsistemas.COMPLEXO_HOSPITALAR.getId())) {
			dados.setComplexoHospitalar(true);
			
			for (Papel p : usuario.getPapeis()){
				if (p.getId() == SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR)
					dados.setGestorComplexoHospitalar(true);
				if (p.getId() == SigaaPapeis.SECRETARIA_RESIDENCIA){
					dados.setGestorResidenciaMedica(true);
					dados.residencias = new ArrayList<Unidade>(0);
					UnidadeGeral unidadePapel = usuario.getPermissao(SigaaPapeis.SECRETARIA_RESIDENCIA).iterator().next().getUnidadePapel();
					if(unidadePapel == null)
						throw new ConfiguracaoAmbienteException("A unidade para a permissão de Secretaria de Residência do usuário logado não está definida.");
					for(UnidadeGeral u: getDAO(UnidadeDao.class, req).findBySubUnidades(
							unidadePapel, 
							TipoUnidadeAcademica.PROGRAMA_RESIDENCIA)) {
						dados.residencias.add((Unidade) u);
					}
				}
			}
			
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.COMPLEXO_HOSPITALAR, true));
			dados.incrementaTotalSistemas();
		}

	}

}
