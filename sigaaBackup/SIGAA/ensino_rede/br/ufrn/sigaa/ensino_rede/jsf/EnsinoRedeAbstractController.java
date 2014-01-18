package br.ufrn.sigaa.ensino_rede.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoCoordenacaoGeralRede;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoCoordenadorUnidadeRede;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino_rede.dao.SelecionaCampusIesDao;
import br.ufrn.sigaa.ensino_rede.dominio.ProgramaRede;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

@SuppressWarnings("serial")
public class EnsinoRedeAbstractController<T> extends SigaaAbstractController<T> {

	@Override
	@SuppressWarnings("unchecked")
	public Usuario getUsuarioLogado() {
		return super.getUsuarioLogado();
	}
	
	public boolean isCoordenadorUnidadeRede() {
		return getUsuarioLogado().getVinculoAtivo().getTipoVinculo().isCoordenadorUnidadeRede();
	}

	public boolean isCoordenadorProgramaRede() {
		return getUsuarioLogado().getVinculoAtivo().getTipoVinculo().isCoordenadorGeralRede();
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
	
	public Pessoa getPessoaLogada() {
		if (getUsuarioLogado().getVinculoAtivo().getTipoVinculo().isCoordenadorUnidadeRede()) {
			TipoVinculoCoordenadorUnidadeRede tipoVinculo = (TipoVinculoCoordenadorUnidadeRede) getUsuarioLogado().getVinculoAtivo().getTipoVinculo();
			
			return tipoVinculo.getCoordenacao().getPessoa();
		} else if (getUsuarioLogado().getVinculoAtivo().getTipoVinculo().isCoordenadorGeralRede()){
			TipoVinculoCoordenacaoGeralRede tipoVinculo = (TipoVinculoCoordenacaoGeralRede) getUsuarioLogado().getVinculoAtivo().getTipoVinculo();
			return tipoVinculo.getCoordenacao().getPessoa();
		}
		
		return null;
	}
	
	public CampusIes getCampusIes() {
		if (getUsuarioLogado().getVinculoAtivo().getTipoVinculo().isCoordenadorUnidadeRede()) {
			TipoVinculoCoordenadorUnidadeRede tipoVinculo = (TipoVinculoCoordenadorUnidadeRede) getUsuarioLogado().getVinculoAtivo().getTipoVinculo();
			return tipoVinculo.getCoordenacao().getDadosCurso().getCampus();
		}
		
		return null;
	}
	
	public Collection<SelectItem> getCampusCombo() throws DAOException {
		
		List<SelectItem> res = new ArrayList<SelectItem>();
		
		if (getUsuarioLogado().getVinculoAtivo().getTipoVinculo().isCoordenadorGeralRede()) {
			SelecionaCampusIesDao dao = getDAO(SelecionaCampusIesDao.class);
			List<CampusIes> resultado = dao.buscar(getProgramaRede(), null, null);
			res = toSelectItems(resultado, "id", "descricao");
		} 
		else {
			res.add( new SelectItem(getCampusIes().getId(), getCampusIes().getDescricao()) );
		}
		
		return res;
	}
	
}
