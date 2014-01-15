<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>


<f:view>

	<h2><ufrn:subSistema /> > Relatório de Discentes que Possuem Cartão Benefício </h2>

	<h:form id="form">
	<table class="formulario" width="50%">
	<caption>Buscar Discente com Cartão Benefício</caption>  
	<tbody>
		<tr>
			<td width="1%"> <h:selectBooleanCheckbox value="#{relatorioCartaoBeneficio.checkBuscaMatricula}" id="selectBuscaMatricula" styleClass="noborder" /> </td>
	    	<td width="16%"> <label for="buscaMatricula">Matrícula:</label> </td>
	    	<td> <h:inputText id="buscaMatricula" value="#{relatorioCartaoBeneficio.buscaMatricula}" size="10" maxlength="10" 
	    		onfocus="javascript:$('form:selectBuscaMatricula').checked = true;" onkeyup="return formatarInteiro(this)" />
	    	</td>
	    </tr>

		
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{relatorioCartaoBeneficio.checkBuscaDiscente}" id="selectBuscaDiscente" styleClass="noborder" />
			</td>
			<td><label for="buscaDiscente">Discente:</label></td>
			<td>
   				 <h:inputHidden id="idDiscente" value="#{relatorioCartaoBeneficio.idDiscente}"/>
   				 <h:inputText id="nomeDiscente" value="#{relatorioCartaoBeneficio.buscaNomeDiscente}" size="50" onkeyup="CAPS(this)"
    					 disabled="#{relatorioCartaoBeneficio.readOnly}" readonly="#{relatorioCartaoBeneficio.readOnly}" 
     					 onfocus="$('form:selectBuscaDiscente').checked = true;"/>

		      <ajax:autocomplete source="form:nomeDiscente" target="form:idDiscente"
		        baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
		        indicator="indicatorDiscente" minimumCharacters="3" parameters="nivel=ufrn"
		        parser="new ResponseXmlToHtmlListParser()" />
		 
		      <span id="indicatorDiscente" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
  				 </td>
		</tr>

			</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
				<h:commandButton value="Buscar" action="#{ relatorioCartaoBeneficio.buscaDiscentesCartoes }" id="btBuscar" />
				<h:commandButton value="Cancelar" action="#{ relatorioCartaoBeneficio.cancelar }" onclick="#{confirm}" id="btCancelar" />
	    	</td>
	    </tr>
	</tfoot>
	</table>

	<br/>

	
	</h:form>
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>	