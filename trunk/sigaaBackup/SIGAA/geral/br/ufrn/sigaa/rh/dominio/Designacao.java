/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '22/11/2006'
 *
 */
package br.ufrn.sigaa.rh.dominio;

import java.util.Date;
import java.util.HashMap;

import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.rh.dominio.AtividadeServidor;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 *
 * @author Gleydson
 *
 */
public class Designacao extends br.ufrn.rh.dominio.Designacao implements ViewAtividadeBuilder{

	private Unidade unidadeDesignacao;

	private Servidor servidorSigaa;

	public Designacao() {}
	
	public Designacao(int idServidor, Date inicio, Date fim, String descricao) {
		this.servidorSigaa = new Servidor(idServidor);
		this.setInicio(inicio);
		this.setFim(fim);
		this.setDescricaoAtividade(descricao);
	}
	
	public Servidor getServidorSigaa() {
		return servidorSigaa;
	}

	public void setServidorSigaa(Servidor servidorSigaa) {
		this.servidorSigaa = servidorSigaa;
	}

	public Unidade getUnidadeDesignacao() {
		return unidadeDesignacao;
	}

	public void setUnidadeDesignacao(Unidade unidadeDesignacao) {
		this.unidadeDesignacao = unidadeDesignacao;
	}

	public String getItemView() {
		return "  <td>"+getAtividade().getDescricao()+ "</td>" +
		   "  <td style=\"text-align:center\">"+Formatador.getInstance().formatarData(getInicio())+" - "+ Formatador.getInstance().formatarData(getFim())+"</td>" +
		   "  <td style=\"text-align:right\">"+CalendarUtils.calculoMeses(getInicio(), getFim())+"</td>";
	}

	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("atividade.descricao", "descricaoAtividade");
		itens.put("inicio", null);
		itens.put("fim", null);
		itens.put("atividade.pontuacao", "pontuacaoAtividade");
		itens.put("gerencia", null);
		return itens;
	}

	/**
	 * Retorna a descrição da atividade acrescido do nível de responsabilidade: Titular ou Substituto.
	 * @return
	 */
	public String getDescricaoAtividade() {
		String desc = "";
		if (getAtividade() != null) {
			desc = getAtividade().getDescricao(); 
			if(getGerencia() != null && getGerencia().equalsIgnoreCase(TITULAR))
				desc += " (Titular)";
			else if(getGerencia() != null && getGerencia().equalsIgnoreCase(SUBSTITUTO))
				desc += " (Substituto)";
		}
		return desc;
	}
	
	public void setDescricaoAtividade( String descricao ) {
		if ( getAtividade() == null ) {
			setAtividade( new AtividadeServidor() );
		}
		getAtividade().setDescricao(descricao);
	}
	
	public void setPontuacaoAtividade( Integer pontuacao ) {
		if ( getAtividade() == null ) {
			setAtividade( new AtividadeServidor() );
		}
		getAtividade().setPontuacao(pontuacao);
	}

	public float getQtdBase() {
		return CalendarUtils.calculoMeses(getInicio(), getFim());
	}

	public String getTituloView() {
		return 	"    <td>Atividade</td>" +
				"    <td style=\"text-align:center\">Período</td>" +
				"	 <td style=\"text-align:right\">Meses</td>";
	}

}
