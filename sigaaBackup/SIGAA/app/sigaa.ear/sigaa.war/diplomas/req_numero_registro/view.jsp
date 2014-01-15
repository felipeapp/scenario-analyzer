<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2> <ufrn:subSistema /> > Requisição de Número de Registro para Registro de Diploma Externo </h2>
	
<div class="descricaoOperacao">
	<b>ATENÇÃO:</b> anote o(s) número(s) informado(s) pois ele(s) não será(ão) informado(s) novamente.</div>
<br/>
<h:form id="form">

	<table class="listagem" width="100%">
		<caption>Números de Registro de Diplomas Requisitados</caption>
		<tr>
			<th width="55%" class="required">
				Quantidade de Números Solicitados:
			</th>
			<td>
				<h:outputText value="#{requisicaoNumeroRegistro.quantidade}"/>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="subFormulario">Utilize os Seguinte(s) Número(s) para Registrar o(s) Diploma(s):</td>
		</tr>
		<tr>
			<td colspan="2" style="font-size: larger; text-align: center;">
				<h:outputText value="#{requisicaoNumeroRegistro.listaNumeros}"/>
			</td>
		</tr>
		<tfoot>
		<tr>
			<td colspan="2" align="center">
				<h:commandButton action="#{requisicaoNumeroRegistro.preCadastrar}" value="Requisitar Novos Números" id="btnCadastrar"/>
				<h:commandButton action="#{requisicaoNumeroRegistro.cancelar}" value="Cancelar" onclick="#{confirm}" id="btnCancelar"/>
			</td>
		</tr>
		</tfoot>
	</table>
<br>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>