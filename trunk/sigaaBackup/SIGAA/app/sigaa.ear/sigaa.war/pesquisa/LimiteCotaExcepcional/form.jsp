<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<h2> <ufrn:subSistema /> &gt; Limite de Cota Excepcional</h2>

<div class="descricaoOperacao">
Esta operação permite definir uma quantidade de cotas arbitrária
como limite máximo para um determinado docente.
</div>

<h:form id="form">
<table class="formulario">
<caption> Cadastro de Limite de Cota Excepcional</caption>
<tr>
	<th class="required"> Docente: </th>
	<td> 
		<h:inputHidden id="idDocente" value="#{ limiteCotaExcepcionalMBean.obj.servidor.id }"></h:inputHidden>
		<h:inputText id="nomeDocente" value="#{ limiteCotaExcepcionalMBean.obj.servidor.pessoa.nome }" size="80"
			 maxlength="80" readonly="#{limiteCotaExcepcionalMBean.readOnly}" disabled="#{limiteCotaExcepcionalMBean.readOnly}" />

		 <ajax:autocomplete
			source="form:nomeDocente" target="form:idDocente"
			baseUrl="/sigaa/ajaxDocente" className="autocomplete"
			indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn,situacao=ativo"
			parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
			style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
	</td>
</tr>
<tr>
	<th class="required"> Limite: </th>
	<td> <h:inputText id="limite" value="#{ limiteCotaExcepcionalMBean.obj.limite }" size="5" maxlength="2" onkeyup="formatarInteiro(this);" 
			readonly="#{limiteCotaExcepcionalMBean.readOnly}" disabled="#{limiteCotaExcepcionalMBean.readOnly}" />
</td>
</tr>
<tfoot>
<tr>
<td colspan="2">
<h:inputHidden id="identificador" value="#{limiteCotaExcepcionalMBean.obj.id}"/>
<h:commandButton id="btnCadastrar" value="#{limiteCotaExcepcionalMBean.confirmButton}" action="#{limiteCotaExcepcionalMBean.cadastrar}"/>
<c:if test="${limiteCotaExcepcionalMBean.obj.id > 0}">
<h:commandButton id="btnVoltar" value="<< Voltar" action="#{limiteCotaExcepcionalMBean.listar}" immediate="true"/>
</c:if>
<h:commandButton id="btnCancelar" value="Cancelar" action="#{limiteCotaExcepcionalMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
</td>
</tr>
</tfoot>
</table>
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
