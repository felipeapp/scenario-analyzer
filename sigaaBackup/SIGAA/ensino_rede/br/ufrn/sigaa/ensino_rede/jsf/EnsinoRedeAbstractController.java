package br.ufrn.sigaa.ensino_rede.jsf;

import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoCoordenacaoGeralRede;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoCoordenadorUnidadeRede;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino_rede.dominio.ProgramaRede;

@SuppressWarnings("serial")
public class EnsinoRedeAbstractController<T> extends AbstractControllerCadastro<T> {

	@Override
	@SuppressWarnings("unchecked")
	public Usuario getUsuarioLogado() {
		return super.getUsuarioLogado();
	}
	
	public ProgramaRede getProgramaRede() {
		if (getUsuarioLogado().getVinculoAtivo().getTipoVinculo().isCoordenadorUnidadeRede()) {
			TipoVinculoCoordenadorUnidadeRede tipoVinculo = (TipoVinculoCoordenadorUnidadeRede) getUsuarioLogado().getVinculoAtivo().getTipoVinculo();
			
			return tipoVinculo.getCoordenacao().getDadosCurso().getProgramaRede();
		} else if (getUsuarioLogado().getVinculoAtivo().getTipoVinculo().isCoordenadorGeralRede()){
			TipoVinculoCoordenacaoGeralRede tipoVinculo = (TipoVinculoCoordenacaoGeralRede) getUsuarioLogado().getVinculoAtivo().getTipoVinculo();
			return tipoVinculo.getCoordenacao().getProgramaRede();
		}
		
		return null;
	}
	
}
