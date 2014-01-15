<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2> <ufrn:subSistema /> > Requisição de Número de Registro para Registro de Diploma Externo </h2>
	
<div class="descricaoOperacao">Para registrar um ou mais diplomas
	externos à Instituição, utilize o formulário abaixo para obter os
	próximos Números de Registro da sequência de Números de Registro de
	Diplomas, informando quantos diplomas serão registrados.<br/>
	<b>ATENÇÃO:</b> anote o(s) número(s) gerados pois o(s) números são gerados apenas uma vez.</div>
<br/>
<h:form id="form">

	<table class="formulario" width="60%">
		<caption>Informe a Quantidade de Diplomas a Registrar</caption>
		<tr>
			<th class="required" width="80%">
				Quantidade de Diplomas Externos a Registrar:
			</th>
			<td>
				<h:inputText value="#{requisicaoNumeroRegistro.quantidade}" size="2" maxlength="2" id="quantidade"
				 onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
			</td>
		</tr>
		<tfoot>
		<tr>
			<td colspan="2" align="center">
				<h:commandButton action="#{requisicaoNumeroRegistro.cadastrar}" value="Requisitar Números" id="btnCadastrar"/>
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