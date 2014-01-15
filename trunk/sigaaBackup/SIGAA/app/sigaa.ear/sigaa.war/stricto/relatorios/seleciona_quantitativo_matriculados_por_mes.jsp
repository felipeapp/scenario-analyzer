<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> >  Quantitativo Geral de Alunos Matriculados Por Mês</h2>

	<h:messages/>

	<div class="descricaoOperacao">
		Informe o Ano para consultar o Relatório Quantitativo Geral de Alunos Matriculados por Mês.
	</div>
	
	<h:form id="form">

		<table class="formulario" width="50%">
			<caption>Informe os Dados para a Geração do Relatório</caption>
			<tbody>
				<tr>
					<th class="obrigatorio">Ano:</th>
					<td>
						<h:inputText value="#{relatoriosStricto.ano}"
							id="ano" size="4" maxlength="4"
							onkeyup="return formatarInteiro(this);"
							converter="#{ intConverter }" />
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Gerar Relatório" action="#{ relatoriosStricto.gerarRelatorioQuantitativoAlunosMatriculadosMes }" id="Botaorelatorio" /> 
						<h:commandButton value="Cancelar" action="#{ relatoriosStricto.cancelar }" id="BotaoCancelar" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br/>
		<div align="center">
			<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		</div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>