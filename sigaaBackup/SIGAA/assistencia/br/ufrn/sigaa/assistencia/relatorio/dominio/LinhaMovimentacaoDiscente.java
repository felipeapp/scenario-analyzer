package br.ufrn.sigaa.assistencia.relatorio.dominio;

import java.util.Collection;
import java.util.List;

import org.hamcrest.core.Is;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.assistencia.dominio.TipoRefeicaoRU;
import br.ufrn.sigaa.assistencia.restaurante.dominio.DiasAlimentacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Linha auxiliar para a constru��o do relat�rio de movimenta��o dos discente bolsistas do SAE.
 * 
 * @author Jean Gguerethes
 */
public class LinhaMovimentacaoDiscente {

	/** Relat�rio dos discentes que apresentam trancamento de componente */
	public static final int TRANCAMENTO_COMPONENTE = 1;
	/** Relat�rio dos discentes que apresentam trancamento de curso */
	public static final int TRANCAMENTO_CURSO = 2;
	/** Relat�rio dos discentes que apresentam cancelamento de curso */
	public static final int CANCELAMENTO_CURSO = 3;
	/** Relat�rio dos discentes que apresentam conclus�o de curso */
	public static final int CONCLUSAO_CURSO = 4;
	/** Discente que realizou a movimenta��o */
	private Discente discente;
	/** Dias de alimenta��o definidos para o discente selecionado */
	private Collection<DiasAlimentacao> diasAlimentacao;
	/** Descri��o da matricula que o discente realizou a movimenta��o */
	private String matriculaComponente;
	/** Decri��o do hor�rio da movimenta��o do discente */
	private String descricaoHorario;
	
	public Discente getDiscente() {
		return discente;
	}
	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public Collection<DiasAlimentacao> getDiasAlimentacao() {
		return diasAlimentacao;
	}
	public void setDiasAlimentacao(Collection<DiasAlimentacao> diasAlimentacao) {
		this.diasAlimentacao = diasAlimentacao;
	}
	public String getMatriculaComponente() {
		return matriculaComponente;
	}
	public void setMatriculaComponente(String matriculaComponente) {
		this.matriculaComponente = matriculaComponente;
	}

	public String getDescricaoHorario() {
		return descricaoHorario;
	}
	
	public void setDescricaoHorario(String descricaoHorario) {
		this.descricaoHorario = descricaoHorario;
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "discente.matricula");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(discente.getMatricula());
	}

	public static boolean contains( List<String> tipoRelatorio, Integer... relatorio ) {
		for (String string : tipoRelatorio) {
			for (int i = 0; i < relatorio.length; i++) {
				if ( relatorio[i] == Integer.parseInt(string) )
					return true;
			}
		}
		return false;
	}

	public String getHorarioCafe(){
		return getHorario(TipoRefeicaoRU.CAFE);
	}

	public String getHorarioAlmoco(){
		return getHorario(TipoRefeicaoRU.ALMOCO);
	}

	public String getHorarioJanta(){
		return getHorario(TipoRefeicaoRU.JANTA);
	}

	/** Retornar uma linha com os dias de alimenta��o do discente */
	public String getHorario( int refeicao ){
		String result = "";
		String valor = "";
		for (DiasAlimentacao dias : diasAlimentacao) {
			if (dias.getTipoRefeicao().getId() == refeicao ) {
				valor = dias.getSegunda() ? "X" : "";
				result += "<td width='5%' style='text-align: center;'> " + valor + " </td>";
				valor = dias.getTerca() ? "X" : "";
				result += "<td width='5%' style='text-align: center;'> " + valor + " </td>";
				valor = dias.getQuarta() ? "X" : "";
				result += "<td width='5%' style='text-align: center;'> " + valor + " </td>";
				valor = dias.getQuinta() ? "X" : "";
				result += "<td width='5%' style='text-align: center;'> " + valor + " </td>";
				valor = dias.getSexta() ? "X" : "";
				result += "<td width='5%' style='text-align: center;'> " + valor + " </td>";
				valor = dias.getSabado() ? "X" : "";
				result += "<td width='5%' style='text-align: center;'> " + valor + " </td>";
				valor = dias.getDomingo() ? "X" : "";
				result += "<td width='5%' style='text-align: center;'> " + valor + " </td>";
			}
			
		}
		return result; 
	}
	
	/** Retorna a descri��o do relat�rio de acordo do tipo selecionado */
	public String getDescricaoRelatorio() {
		switch ( discente.getStatus() ) {
			case StatusDiscente.ATIVO:
				return "Alunos que trancaram componentes curriculares nos dias que tinham bolsa alimenta��o";
			case StatusDiscente.TRANCADO:
				return "Alunos que possuem benef�cio ativo que trancaram o programa";
			case StatusDiscente.CANCELADO:
				return "Alunos que possuem benef�cio ativo que cancelaram o programa";
			case StatusDiscente.GRADUANDO:
				return "Alunos que possuem benef�cio que est�o graduando o programa";
			case StatusDiscente.CONCLUIDO:
				return "Alunos que possuem benef�cio que concluiram o programa";
			default:
				return "";
		}
	}
	
	public boolean isExibirSoNomes(){
		return discente.getStatus() == StatusDiscente.ATIVO; 
	}
	
}