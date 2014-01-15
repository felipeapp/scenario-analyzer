<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<style type="text/css">
       .direita {text-align: right !important;border:none !important; }
       .esquerda {text-align: left !important;border:none !important; }
       .centro {text-align: center !important;border:none !important; }
	   .dr-table-header-continue {background: #DEDFE3; }
       .dr-table-cell {border:none;}
       .dr-subtable-cell{border:none;}
       .caption {
       			margin: 0 auto !important;
				padding: 3px 0 !important;
				font-family:Verdana;
  				font-style:normal;
  				font-variant:small-caps;
  				font-weight:bold;
				color: #FFF;
				letter-spacing: 1px !important;
				text-align: center !important;
				background: url(/shared/img/bg_caption.gif) center repeat-x;
       	}
</style>

<f:view>

	<h2><ufrn:subSistema /> >  Cart�o de Benef�cio do Discente </h2>

	<h:form id="form">
	<table class="formulario" width="50%">
	<caption>Buscar Discente com Bolsa Alimenta��o</caption>  
	<tbody>
		<tr>
			<td width="1%"> <h:selectBooleanCheckbox value="#{cartaoBeneficio.checkBuscaMatricula}" id="selectBuscaMatricula" styleClass="noborder" /> </td>
	    	<td width="16%"> <label for="buscaMatricula" onclick="$('form:selectBuscaMatricula').checked = !$('form:selectBuscaMatricula').checked">Matr�cula:</label> </td>
	    	<td> <h:inputText id="buscaMatricula" value="#{cartaoBeneficio.buscaMatricula}" size="10" maxlength="10" 
	    		onfocus="javascript:$('form:selectBuscaMatricula').checked = true;" onkeyup="return formatarInteiro(this)" />
	    	</td>
	    </tr>

		
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{cartaoBeneficio.checkBuscaDiscente}" id="selectBuscaDiscente" styleClass="noborder" />
			</td>
			<td><label for="buscaDiscente" onclick="$('form:selectBuscaDiscente').checked = !$('form:selectBuscaDiscente').checked">Discente:</label></td>
			<td>
   				 <h:inputHidden id="idDiscente" value="#{cartaoBeneficio.idDiscente}"/>
   				 <h:inputText id="nomeDiscente" value="#{cartaoBeneficio.buscaNomeDiscente}" size="50" onkeyup="CAPS(this)"
    					 disabled="#{cartaoBeneficio.readOnly}" readonly="#{cartaoBeneficio.readOnly}" 
     					 onfocus="$('form:selectBuscaDiscente').checked = true;"/>

		      <ajax:autocomplete source="form:nomeDiscente" target="form:idDiscente"
		        baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
		        indicator="indicatorDiscente" minimumCharacters="3" parameters="nivel=ufrn"
		        parser="new ResponseXmlToHtmlListParser()" />
		 
		      <span id="indicatorDiscente" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
  				 </td>
		</tr>

		<tr>
			<td>&nbsp;</td>
			<td class="obrigatorio"><span class="requerid"></span>Per�odo:</td>
			<td><h:inputText value="#{cartaoBeneficio.ano}" size="4"
				maxlength="4" onkeyup="return formatarInteiro(this);" />
			&nbsp;-&nbsp; <h:inputText value="#{cartaoBeneficio.periodo}"
				size="1" maxlength="1" onkeyup="return formatarInteiro(this);" />
			</td>
		</tr>

			</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
				<h:commandButton value="Buscar" action="#{ cartaoBeneficio.buscar }" id="btBuscar" />
				<h:commandButton value="Cancelar" action="#{ cartaoBeneficio.cancelar }" onclick="#{confirm}" id="btCancelar" />
	    	</td>
	    </tr>
	</tfoot>
	</table>
	<br />
	<center><h:graphicImage url="/img/required.gif"
		style="vertical-align: top;" /> <span class="fontePequena">
	Campos de preenchimento obrigat�rio. </span></center>
	<br />
	<br/>

	<center>
		<div class="infoAltRem">
			<h:graphicImage width="16px" value="/img/add_user_card.ico" style="overflow: visible;"/>: Associar/Substituir Discente ao Cart�o
		</div>
	</center>
	<table class="listagem">
	<caption>Lista de Discentes Encontrados (${cartaoBeneficio.totalCartoesLocalizadas})</caption>
	 <tr>
	 	<td width="100%">
	 		
	 	
  	<rich:dataTable id="dtCartoesEncontradas"  value="#{cartaoBeneficio.bolsas}" 
 		var="bolsa" binding="#{cartaoBeneficio.uiData}" width="100%"
 		rendered="#{not empty cartaoBeneficio.bolsas}"
 		columnClasses="centro, esquerda, esquerda, centro, centro " rowClasses="linhaPar, linhaImpar">
 		
		<f:facet name="header">
			<rich:columnGroup style="background: #C0C0C0;  " columnClasses="centro, esquerda, esquerda, centro, centro ">
				
				<rich:column width="10%">
					<f:facet name="header">Matr�cula</f:facet>
				</rich:column>
				<rich:column  width="50%">
					<f:facet name="header">Nome</f:facet>
				</rich:column>
				<rich:column >
					<f:facet name="header">Tipo Bolsas</f:facet>
				</rich:column>
				<rich:column width="2%">
					<rich:spacer />
				</rich:column>
			</rich:columnGroup>
		</f:facet>
		
		<rich:column >
			<h:outputText value="#{bolsa.discente.matricula}" />
		</rich:column>
		
		<rich:column >
			<h:outputText value="#{bolsa.discente.pessoa.nome}" />
		</rich:column>
		
		<rich:column >
			<h:outputText value="#{bolsa.tipoBolsaAuxilio.denominacao}" />
		</rich:column>
		
		<rich:column >
			<h:commandLink title="Associar/Substituir Discente ao Cart�o" action="#{ cartaoBeneficio.iniciarAssociarDiscente }" immediate="true" id="lnkAssociarSubst">
		        <f:param name="id" value="#{bolsa.discente.id}"/>
				<h:graphicImage  width="16px" value="/img/add_user_card.ico" />
			</h:commandLink>
		</rich:column>
		
		
		
 	</rich:dataTable>
 		</td>
	 </tr>
	</table>
	<center><h:outputText  value="<i>Nenhum discente foi encontrado.</i>" rendered="#{empty cartaoBeneficio.bolsas}" escape="false"/></center>

	</h:form>
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>	