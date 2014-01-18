package br.ufrn.sigaa.arq.acesso;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.dominio.VinculoUsuario;

public class AcessoEnsinoRede extends AcessoMenuExecutor  {

	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {
		
		VinculoUsuario vinculoAtivo = usuario.getVinculoAtivo();
		
		if (vinculoAtivo.getTipoVinculo().isCoordenadorGeralRede()) {
			dados.setModuloEnsinoRede(true);
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.ENSINO_REDE, true));
			dados.incrementaTotalSistemas();
			usuario.addPapelTemporario(new Papel(SigaaPapeis.COORDENADOR_GERAL_REDE));
		} else if (vinculoAtivo.getTipoVinculo().isCoordenadorUnidadeRede()) {
			dados.setPortalEnsinoRede(true);
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.PORTAL_ENSINO_REDE, true));
			dados.incrementaTotalSistemas();
			usuario.addPapelTemporario(new Papel(SigaaPapeis.COORDENADOR_UNIDADE_REDE));
		}
	}

}
