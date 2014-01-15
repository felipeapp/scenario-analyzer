/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 17/02/2009 
 */
package br.ufrn.sigaa.arq.acesso;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.SecretariaUnidade;

/**
 * Processamento de permissões para acesso as secretarias
 * 
 * @author David Pereira
 *
 */
public class AcessoSecretarias extends AcessoMenuExecutor {

	/**
	 * TipoUnidadeAcademica para departamentos
	 */
	private static List<Integer> tipoDepartamento = new ArrayList<Integer>();
	
	/**
	 * TipoUnidadeAcademica para centros
	 */
	private static List<Integer> tipoCentro = new ArrayList<Integer>();
	
	/**
	 * TipoUnidadeAcademica para para pos
	 */
	private static List<Integer> tipoPos = new ArrayList<Integer>();
	
	/**
	 * TipoUnidadeAcademica para tecnico
	 */
	private static List<Integer> tipoTecnico = new ArrayList<Integer>();
	
	static {
		tipoDepartamento.add(TipoUnidadeAcademica.DEPARTAMENTO);
		tipoDepartamento.add(TipoUnidadeAcademica.ESCOLA);
		tipoDepartamento.add(TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA);
		
		tipoCentro.add(TipoUnidadeAcademica.CENTRO);
		
		tipoPos.add(TipoUnidadeAcademica.PROGRAMA_POS);
		
		tipoTecnico.add(TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA);
		
	}
	
	/**
	 * Verifica as permissões do usuário para acesso às Secretarias.
	 * 
	 * @param dados
	 * @param usuarios
	 * @param req
	 *  
	 */
	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {
		
		SecretariaUnidade secretaria = usuario.getVinculoAtivo().getSecretariaUnidade();
		
		if (isEmpty(secretaria))
			return;
		
		if (secretaria.getUnidade() != null) {
			
			if (tipoDepartamento.contains(secretaria.getUnidade().getTipoAcademica())) {
				usuario.setNivelEnsino(NivelEnsino.GRADUACAO);
				usuario.addPapelTemporario(new Papel(SigaaPapeis.SECRETARIA_DEPARTAMENTO));

				req.getSession().setAttribute("nivel", NivelEnsino.GRADUACAO);
				
				dados.graduacao = true;
				dados.secretarioDepartamento = true;
				
				dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.GRADUACAO, true));
			}
			
			if (tipoCentro.contains(secretaria.getUnidade().getTipoAcademica())) {
				usuario.addPapelTemporario(new Papel(SigaaPapeis.SECRETARIA_CENTRO));
				usuario.setNivelEnsino(NivelEnsino.GRADUACAO);
				
				req.getSession().setAttribute("nivel", NivelEnsino.GRADUACAO);
				
				dados.graduacao = true;
				dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.GRADUACAO, true));
				dados.secretarioCentro = true;
				dados.setSecretariaCentro(secretaria.getUnidade());
			}
			
			if (tipoPos.contains(secretaria.getUnidade().getTipoAcademica())) {
				usuario.addPapelTemporario(new Papel(SigaaPapeis.SECRETARIA_POS));
				usuario.setNivelEnsino(NivelEnsino.STRICTO);

				req.getSession().setAttribute("nivel", NivelEnsino.STRICTO);
				dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO, true));
				dados.setSecretariaPosGraduacao(true);
			}
			
			if (tipoTecnico.contains(secretaria.getUnidade().getTipoAcademica())) {
				usuario.addPapelTemporario(new Papel(SigaaPapeis.SECRETARIA_TECNICO));
				usuario.setNivelEnsino(NivelEnsino.TECNICO);
				
				req.getSession().setAttribute("nivel", NivelEnsino.TECNICO);
				
				dados.setSecretarioTecnico(true);
				dados.setTecnico(true);
				dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.TECNICO, true));
			}
			
		}
		
		if (secretaria.getCurso() != null) {
			if (secretaria.getCurso().isGraduacao()) {
				usuario.addPapelTemporario(new Papel(SigaaPapeis.SECRETARIA_COORDENACAO));
				usuario.setNivelEnsino(NivelEnsino.GRADUACAO);

				req.getSession().setAttribute("nivel", NivelEnsino.GRADUACAO);
				
				dados.secretarioGraduacao = true;
				dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.PORTAL_COORDENADOR, true));
			}
			
			if (secretaria.getCurso().isLato()) {
				usuario.addPapelTemporario(new Papel(SigaaPapeis.SECRETARIA_LATO));
				usuario.setNivelEnsino(NivelEnsino.LATO);
				
				req.getSession().setAttribute("nivel", NivelEnsino.LATO);
				
				dados.setSecretarioLato(true);
				dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.PORTAL_COORDENADOR_LATO, true));
			}
			
			if (secretaria.getCurso().isMedio()) {
				usuario.addPapelTemporario(new Papel(SigaaPapeis.SECRETARIA_MEDIO));
				usuario.setNivelEnsino(NivelEnsino.MEDIO);
				
				req.getSession().setAttribute("nivel", NivelEnsino.MEDIO);
				
				dados.setSecretarioMedio(true);
				dados.setMedio(true);
				dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.MEDIO, true));
			}
			
			dados.incrementaTotalSistemas();
			
		}
	}
}
