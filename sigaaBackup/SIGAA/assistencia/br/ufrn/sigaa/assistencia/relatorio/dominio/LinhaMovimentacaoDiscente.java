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
 * Linha auxiliar para a construção do relatório de movimentação dos discente bolsistas do SAE.
 * 
 * @author Jean Gguerethes
 */
public class LinhaMovimentacaoDiscente {

	/** Relatório dos discentes que apresentam trancamento de componente */
	public static final int TRANCAMENTO_COMPONENTE = 1;
	/** Relatório dos discentes que apresentam trancamento de curso */
	public static final int TRANCAMENTO_CURSO = 2;
	/** Relatório dos discentes que apresentam cancelamento de curso */
	public static final int CANCELAMENTO_CURSO = 3;
	/** Relatório dos discentes que apresentam conclusão de curso */
	public static final int CONCLUSAO_CURSO = 4;
	/** Discente que realizou a movimentação */
	private Discente discente;
	/** Dias de alimentação definidos para o discente selecionado */
	private Collection<DiasAlimentacao> diasAlimentacao;
	/** Descrição da matricula que o discente realizou a movimentação */
	private String matriculaComponente;
	/** Decrição do horário da movimentação do discente */
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

	/** Retornar uma linha com os dias de alimentação do discente */
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
	
	/** Retorna a descrição do relatório de acordo do tipo selecionado */
	public String getDescricaoRelatorio() {
		switch ( discente.getStatus() ) {
			case StatusDiscente.ATIVO:
				return "Alunos que trancaram componentes curriculares nos dias que tinham bolsa alimentação";
			case StatusDiscente.TRANCADO:
				return "Alunos que possuem benefício ativo que trancaram o programa";
			case StatusDiscente.CANCELADO:
				return "Alunos que possuem benefício ativo que cancelaram o programa";
			case StatusDiscente.GRADUANDO:
				return "Alunos que possuem benefício que estão graduando o programa";
			case StatusDiscente.CONCLUIDO:
				return "Alunos que possuem benefício que concluiram o programa";
			default:
				return "";
		}
	}
	
	public boolean isExibirSoNomes(){
		return discente.getStatus() == StatusDiscente.ATIVO; 
	}
	
}