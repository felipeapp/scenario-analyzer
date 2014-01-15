<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	.colLeft{text-align: left; }
	.colCenter{text-align: center; }
	.colRight{text-align: right; }
	.rich-progress-bar-width { width: 700px;}
	.rich-progress-bar-uploaded-dig {font-size: 16px;}
	.rich-progress-bar-shell-dig {font-size: 16px;}
</style>
<script>
function desabilitaBotoes() {
	$('form:colarGrauSelecionados').disabled=true;
	$('form:colarGrauSelecionados').value = 'Aguarde...';
	$('form:telaGraduandos').disabled=true;
	$('form:cancelarAOperacao').disabled=true;
}
</script>
<f:view>
<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
<h2> <ufrn:subSistema /> > Conclusão de Programa Coletiva > Confirmação</h2>
<h:form id="form">
<a4j:keepAlive beanName="colacaoColetiva"></a4j:keepAlive>
	<div class="descricaoOperacao"> 
		<p>Caro usuário,</p>
		<p> Verifique se todas informações abaixo estão corretas. Caso estejam, informe sua senha para concluir a Colação de Grau Coletiva dos discentes.
	</div>

	<table class="formulario" width="85%">
	<caption>Dados da Turma de Conclusão Coletiva</caption>
	<tr>
		<th width="20%" class="rotulo"> Curso: </th>
		<td > ${colacaoColetiva.curso.descricao} </td>
	</tr>
	<tr>
		<th class="rotulo"> Ano - Período: </th>
		<td >${ colacaoColetiva.ano}.${ colacaoColetiva.periodo}</td>
	</tr>
	<tr>
		<th class="rotulo">Data da Colação:</th>
		<td>
			<t:outputText value="#{colacaoColetiva.dataColacao}" />
		</td>
	</tr>
	<tr>
		<td colspan="2" class="subFormulario">Graduandos que serão Concluídos (${ fn:length(colacaoColetiva.confirmados) })</td>
	</tr>
	<tr class="caixaCinza">
		<th style="text-align: left;">Matrícula</th>
		<th style="text-align: left;">Nome</th>
	</tr>
	<c:forEach items="#{colacaoColetiva.confirmados}" var="graduando"varStatus="status">
		<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td style="text-align: left;">
				${graduando.matricula}
			</td>
			<td style="text-align: left;">
				${graduando.nome}
			</td>
		</tr>
	</c:forEach>
	<tr>
		<td colspan="2">
			<c:set var="exibirApenasSenha" value="true" scope="request"/>
			<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
		</td>
	</tr>
	</tbody>
	<tfoot>
	<tr>
		<td colspan="2">
			<a4j:commandButton value="Colar Grau dos Discentes Selecionados" action="#{colacaoColetiva.cadastrar}" id="colarGrauSelecionados" 
				reRender="progressBar" onclick="desabilitaBotoes()" />
			<h:commandButton value="<< Voltar" action="#{colacaoColetiva.telaGraduandos}" id="telaGraduandos" />
			<h:commandButton value="Cancelar" action="#{colacaoColetiva.cancelar}" onclick="#{confirm}" id="cancelarAOperacao"/>
		</td>
	</tr>	
	</tfoot>
</table>
<br/>
<div style="width: 650px; margin: 5px auto; text-align: center;">
	<a4j:outputPanel>
		<rich:progressBar interval="1000" id="progressBar" minValue="0" maxValue="100"
			enabled="true"
			value="#{ colacaoColetiva.percentualProcessado }"
			label ="#{ colacaoColetiva.mensagemProgresso }">
		</rich:progressBar>
	</a4j:outputPanel>
</div>
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>