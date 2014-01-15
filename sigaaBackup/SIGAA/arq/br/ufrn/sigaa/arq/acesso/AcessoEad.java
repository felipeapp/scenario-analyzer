/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 30/09/2008 
 */
package br.ufrn.sigaa.arq.acesso;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.sigaa.arq.dao.ead.PoloDao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.Polo;

/**
 * Processamento de permissões para acesso a EAD 
 * 
 * @author David Pereira
 *
 */
public class AcessoEad extends AcessoMenuExecutor {

	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {
		
		if (usuario.isUserInSubSistema(SigaaSubsistemas.SEDIS.getId())) {
			dados.setEad(true);
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.SEDIS, true));
			dados.incrementaTotalSistemas();
		}

		// verifica se é tutor do ensino a distância
		if (usuario.getVinculoAtivo().isVinculoTutorOrientador()) {
			dados.setTutorEad(true);
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.PORTAL_TUTOR, true));
			dados.incrementaTotalSistemas();
			usuario.addPapelTemporario(new Papel(SigaaPapeis.TUTOR_EAD));
		}
		
		// verifica se é coordenador de pólo
		if (usuario.getVinculoAtivo().isVinculoCoordenacaoPolo()) {
			dados.setCoordenadorPolo(true);
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.PORTAL_COORDENADOR_POLO, true));
			dados.incrementaTotalSistemas();
			
			PoloDao dao = getDAO(PoloDao.class, req);
			Polo p = usuario.getVinculoAtivo().getCoordenacaoPolo().getPolo();
			usuario.getVinculoAtivo().getCoordenacaoPolo().setPolo(dao.findByPrimaryKey(p.getId(), Polo.class));
			usuario.getVinculoAtivo().getCoordenacaoPolo().getPolo().getCidade().getNomeUF();
		}		
			
	}

}
