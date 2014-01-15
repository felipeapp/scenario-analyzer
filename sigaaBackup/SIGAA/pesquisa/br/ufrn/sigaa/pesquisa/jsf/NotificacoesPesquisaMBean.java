/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 14/05/2008
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.notificacoes.Destinatario;
import br.ufrn.sigaa.arq.dao.pesquisa.MembroProjetoDiscenteDao;
import br.ufrn.sigaa.arq.dao.pesquisa.TipoBolsaPesquisaDao;
import br.ufrn.sigaa.arq.jsf.AbstractControllerNotificacoes;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;

/**
 * MBean respons�vel pelo envio de notifica��es vinculadas ao
 * m�dulo de pesquisa (discentes em inicia��o cient�fica, pesquisadores, etc.)
 *
 * @author Ricardo Wendell
 *
 */
@Component("notificacoesPesquisaBean") @Scope("session")
public class NotificacoesPesquisaMBean extends AbstractControllerNotificacoes {

	List<Integer> tiposBolsaSelecionados;

	/**
	 * obj � novamente instanciado com o obj recebendo Notificacao e 
	 * inicia novamente os tiposBolsaSelecionados
	 */
	@Override
	public void clear() {
		super.clear();
		tiposBolsaSelecionados = new ArrayList<Integer>();
	}

	/**
	 * Esse m�toto tem como fun��o a popular os destinat�rios para o envio.
	 * <br/>
	 * JSP: N�o invocado por JSP.
	 * Apenas pelo m�todo enviar() 
	 * 
	 */
	@Override
	public void popularDestinatarios() throws ArqException {

		// Buscar os destinat�rios a partir dos tipos de bolsa selecionados
		if (!tiposBolsaSelecionados.isEmpty()) {
			MembroProjetoDiscenteDao dao = getDAO(MembroProjetoDiscenteDao.class);
			Collection<Destinatario> destinatarios = dao.findDestinatariosByTipoBolsa(tiposBolsaSelecionados);

			obj.adicionarDestinatarios(destinatarios);
			
			// Popular descri��o dos tipos de bolsa
			List<String> descricaoBolsas = new ArrayList<String>();
			TipoBolsaPesquisaDao tiposBolsaDao = getDAO(TipoBolsaPesquisaDao.class);
			for (Object idTipoBolsa : tiposBolsaSelecionados) {
				descricaoBolsas.add( tiposBolsaDao.findByPrimaryKey(Integer.valueOf( (String) idTipoBolsa), TipoBolsaPesquisa.class).getDescricao() );
			}
			obj.setDescricaoDestinatarios(StringUtils.transformaEmLista( descricaoBolsas ) );
		}
	}

	/**
	 * Retorna o array de inteiros com os gestores de pesquisa 
	 */
	@Override
	public int[] getPapeis() {
		return new int[] {SigaaPapeis.GESTOR_PESQUISA};
	}

	@Override
	public String getCaminhoFormulario() {
		return "/pesquisa/notificacoes/form.jsf";
	}

	public Collection<SelectItem> getTiposBolsa()  {
		return toSelectItems(getDAO(TipoBolsaPesquisaDao.class).findMapaTiposBolsa());
	}

	public List<Integer> getTiposBolsaSelecionados() {
		return tiposBolsaSelecionados;
	}

	public void setTiposBolsaSelecionados(List<Integer> tiposBolsaSelecionados) {
		this.tiposBolsaSelecionados = tiposBolsaSelecionados;
	}

}