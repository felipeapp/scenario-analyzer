<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2> <ufrn:subSistema /> > Requisi��o de N�mero de Registro para Registro de Diploma Externo </h2>
	
<div class="descricaoOperacao">Para registrar um ou mais diplomas
	externos � Institui��o, utilize o formul�rio abaixo para obter os
	pr�ximos N�meros de Registro da sequ�ncia de N�meros de Registro de
	Diplomas, informando quantos diplomas ser�o registrados.<br/>
	<b>ATEN��O:</b> anote o(s) n�mero(s) gerados pois o(s) n�meros s�o gerados apenas uma vez.</div>
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
				<h:commandButton action="#{requisicaoNumeroRegistro.cadastrar}" value="Requisitar N�meros" id="btnCadastrar"/>
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