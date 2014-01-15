<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<script type="text/javascript">
function habilitarDetalhes(idDiscente) {
	var linha = 'linha_'+ idDiscente;
	if ( $(linha).style.display != 'table-cell' ) {
		if ( !Element.hasClassName(linha, 'populado') ) {
			getContent("/sigaa/graduacao/detalhes_discente.jsf?idDiscente=" + idDiscente, linha);
			Element.addClassName(linha, 'populado');
		}
		$(linha).style.display = 'table-cell';
	} else {
		$(linha).style.display = 'none';
	}
}
</script>

<style>
	table.listagem tr.curso td{
		background: #C8D5EC;
		font-weight: bold;
		text-align: left;
	}
	
	table.listagem td.detalhesDiscente { display: none; padding: 0;}
</style>

<f:view>

	<h2><ufrn:subSistema /> > Consultar Requerimentos</h2>

 	<h:form id="form">

	<table class="formulario" width="90%">
	<caption>Busca por Requerimentos</caption>
	<tbody>

		<tr>
			<td> <h:selectBooleanCheckbox value="#{requerimento.checkBuscaCodigo}" id="selectBuscaCodigo" styleClass="noborder"/> </td>
	    	<td> <label for="codigo"> Codigo </label> </td>
	    	<td> <h:inputText id="buscaCodigo" value="#{requerimento.buscaCodigo}" size="6" onfocus="javascript:$('form:selectBuscaCodigo').checked = true;"/></td>
	    </tr>

		<tr>
			<td>
			<h:selectBooleanCheckbox value="#{requerimento.checkBuscaDiscente}"  id="selectBuscaDiscente" />
			</td>
	    	<td> <label> Discente </label> </td>
			<td>
		
			 <h:inputText id="nomeDiscente" value="#{ requerimento.discente.pessoa.nome }" size="60" onfocus="javascript:$('form:selectBuscaDiscente').checked = true;"/>
			 <h:inputHidden id="idDiscente" value="#{ requerimento.discente.id }"/>
		
			<ajax:autocomplete source="form:nomeDiscente" target="form:idDiscente"
				baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
				indicator="indicatorDiscente" minimumCharacters="3" parameters="nivel=G"
				parser="new ResponseXmlToHtmlListParser()" />
			<span id="indicatorDiscente" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
			</td>
		</tr>		
	</tbody>
	
	<tfoot>
		<tr>
			<td colspan="3">
			<h:commandButton value="Buscar" action="#{ requerimento.localizar }"/>
			<h:commandButton value="Cancelar" action="#{ requerimento.cancelar }"/>
	    	</td>
	    </tr>
	</tfoot>
	</table>

	</h:form>

	<br/>
	<br/>


	<div class="infoAltRem">
	    <h:graphicImage value="/img/extensao/user1_view.png" style="overflow: visible;"/>: Visualizar Dados do Discente
	    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Requerimento
	    <h:graphicImage value="/img/check.png" style="overflow: visible;"/>: Efetivar Trancamento
	</div>

	<br/>
 
	<c:set var="requerimentosLocalizadados" value="#{requerimento.resultado}"/>

	<c:if test="${empty requerimentosLocalizadados}">
		<center><i> Nenhum discente localizado </i></center>
	</c:if>


	<c:if test="${not empty requerimentosLocalizadados}">

		<h:form>
		 <table class="listagem">
		    <caption>Requerimentos Encontrados (${ fn:length(requerimentosLocalizadados) })</caption>
		 	<tbody>
		 	
		 	<c:set var="nomeDiscente" />
	       	<c:forEach items="#{requerimentosLocalizadados}" var="re" varStatus="status">
	       	
				<c:if test="${re.discente.nome != nomeDiscente}">
					<c:set var="nomeDiscente" value="${re.discente.nome}"/>
						<tr>
							<td class="subFormulario">
								<a href="javascript: void(0);" onclick="habilitarDetalhes(${re.discente.id});">
									<img src="${ctx}/img/extensao/user1_view.png"/>
								</a>
							</td>
							<td class="subFormulario" colspan="4"> ${nomeDiscente} </td>
						</tr>
						<tr>
							<td colspan="5" id="linha_${re.discente.id}" class="detalhesDiscente" ></td>	
						</tr>
						<tr class="curso">
				      		<td>Codigo</td>
				        	<td>Tipo</td>
				        	<td>Semestres Solicitados</td>
				        	<td>Status</td>
				        	<td></td>
				        </tr>						
					</c:if>
	       	
	               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
	               		<td> ${re.codigoProcesso} </td>
	                    <td> ${re.tipo.descricao} </td>
	                    <td> ${re.trancarQtdSemestres} </td>
	                    <td> ${re.status.descricao} </td>
						<td>
							<h:commandLink action="#{requerimento.exibirRequerimento}" style="border: 0;" title="Visualizar Requerimento" rendered="#{re.alunoEnviou}">
								<f:param name="idRequerimento" value="#{re.id}"/>
								<h:graphicImage url="/img/view.gif" />
							</h:commandLink>																				
							<h:commandLink action="#{requerimento.efetivarTrancamento}"  title="Efetivar Trancamento" style="border: 0;" rendered="#{!re.atendida && re.alunoEnviou}">
							      <f:param name="idRequerimento" value="#{re.id}"/>
							      <h:graphicImage url="/img/check.png" />
							</h:commandLink>
						</td>
	              </tr>
	          </c:forEach>
		 	</tbody>
		 </table>
		</h:form>

	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>