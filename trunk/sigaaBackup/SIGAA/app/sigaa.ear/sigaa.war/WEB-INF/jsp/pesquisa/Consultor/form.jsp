<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt; Consultor
</h2>

<html:form action="/pesquisa/cadastroConsultor" method="post" focus="obj.servidor.pessoa.nome" styleId="form">
	<html:hidden property="obj.id" />

	<table class="formulario" width="85%">
        <caption>Dados do Consultor</caption>
        <tbody>
		<tr>
			<th> Consultor Interno? </th>
			<td> 
				<html:radio property="obj.interno" styleClass="noborder" value="true" 
					disabled="${formConsultor.obj.id > 0}" 
					onclick="document.getElementById('dispatch').value = 'mudarStatus'; submit();" />Sim
   				<html:radio property="obj.interno" styleClass="noborder" value="false" 
   					disabled="${formConsultor.obj.id > 0}"
   					onclick="document.getElementById('dispatch').value = 'mudarStatus'; submit();"/>Não 
			</td>
		</tr>
    	<tr>
			<th class="required">Nome:</th>
			<c:choose>
				<c:when test="${interno}">
					<td>
						<html:hidden styleId="idDocente" property="obj.servidor.id" styleClass="contentLink" />
						<html:text property="obj.servidor.pessoa.nome" styleId="paramAjaxDocente" size="70" />
						
						<ajax:autocomplete source="paramAjaxDocente" target="idDocente"	baseUrl="/sigaa/ajaxDocente" className="autocomplete"
							indicator="indicatorDocente" minimumCharacters="3" parameters="tipo=ufrn,inativos=false"
							parser="new ResponseXmlToHtmlListParser()" />
					
						<span id="indicatorDocente" style="display:none; ">
							<img src="/sigaa/img/indicator.gif" />
						</span>
					</td>
				</c:when>
				<c:otherwise>
					<td><html:text property="obj.nome" maxlength="50" size="70" /></td>
				</c:otherwise>
			</c:choose>
        </tr>
        <tr>
            <th class="required">E-mail:</th>
            <td><html:text property="obj.email" style="width: 380px;" /></td>
        </tr>
        <tr>
            <th class="required">Área de Conhecimento:</th>
            <td>
				<html:select property="obj.areaConhecimentoCnpq.id" style="width: 380px;">
	            	<html:option value="0">Escolha uma opção</html:option>
	            	<html:options collection="areasCNPQ" property="id" labelProperty="nome" />
	            </html:select>
            </td>
        </tr>
        </tbody>
		<tfoot>
		<tr>
			<td colspan="2">
				<html:button dispatch="persist"> ${formConsultor.confirmButton} </html:button>
				<html:button dispatch="cancelar" cancelar="true"> Cancelar </html:button>
			</td>
	    </tr>
		</tfoot>
	</table>
	
</html:form>

<c:if test="${ param['dispatch'] == 'remove' }">
<script type="text/javascript">
	new Remocao('form');
</script>
</c:if>
<br />
<center>
	<html:img page="/img/required.gif" style="vertical-align: top;" />
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
</center>
<br />

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>