<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<script type="text/javascript" src="/shared/loadScript?src=javascript/jquery.tablesorter.min.js"></script>
<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />

<f:view>

	<h2><ufrn:subSistema /> > Finalização de Bolsas para Discentes de Extensão</h2>
	<br/>
	
<h:form id="form">

	<div class="infoAltRem">
	    <h:graphicImage value="/img/extensao/user1_view.png" style="overflow: visible;"/>: Visualizar Dados do Discente
	    <h:graphicImage value="/img/report.png" style="overflow: visible;"/>: Visualizar Plano de Trabalho
	</div>

	<br/>

	<c:set var="bolsistasFinalizadosSigaa" value="#{finalizacaoBolsistaExtensao.bolsistasFinalizadosSigaa}"/>



	<c:if test="${not empty bolsistasFinalizadosSigaa}">
		 <table class="listagem tablesorter" id="listagem" width="100%">
		    <caption>Discentes Encontrados (${ fn:length(bolsistasFinalizadosSigaa) })</caption>

		      <thead>
		      	<tr>
					<th><h:selectBooleanCheckbox value="true" onclick="checkAll(this)"/></th>		      	
		        	<th align="center">Data da Indicação</th>
		        	<th>Matrícula</th>
		        	<th>Nome</th>
		        	<th>Curso</th>
		        	<th align="center">Início da bolsa</th>
		        	<th align="center">Fim da bolsa</th>
		        	<th></th>
		        	<th></th>		        	
		        </tr>
		 	</thead>
		 	<tbody>

	       		<c:forEach items="#{finalizacaoBolsistaExtensao.bolsistasFinalizadosSigaa}" var="de" varStatus="status">				
					
	               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
	                   <td><h:selectBooleanCheckbox value="#{de.selecionado}" styleClass="check"/></td> 
	                   <td align="center"><h:outputText value="#{de.dataCadastro}"/></td>	                   
	                   <td><h:outputText value="#{de.discente.matricula}"/></td>
	                   <td><h:outputText value="#{de.discente.nome}"/></td>
	                   <td><h:outputText value="#{de.discente.curso.descricao}"/></td>
	                   <td align="center"><h:outputText value="#{de.dataInicio}"/></td>
	                   <td align="center"><h:outputText value="#{de.dataFim}"/></td>
	                   <td>
           					<h:commandLink title="Visualizar Discente" action="#{ discenteExtensao.view }" id="ver_discente">
							      <f:param name="idDiscenteExtensao" value="#{de.id}"/>
							      <h:graphicImage url="/img/extensao/user1_view.png" />
							</h:commandLink>
						</td>
	                   <td>
							<h:commandLink action="#{planoTrabalhoExtensao.view}" id="ver_plano"
								title="Visualizar Plano de Trabalho" style="border: 0;" rendered="#{not empty de.planoTrabalhoExtensao}">
						       <f:param name="id" value="#{de.planoTrabalhoExtensao.id}"/>
				               <h:graphicImage url="/img/report.png" />
							</h:commandLink>									
	                   </td>
	               </tr>
	          	</c:forEach>
	        </tbody>	
			<tfoot>
					<tr>
						<td colspan="9">
							<center>
								<h:commandButton value="Finalizar Bolsistas Selecionados" action="#{ finalizacaoBolsistaExtensao.finalizarBolsas }"/>
								<h:commandButton value="Cancelar" action="#{ finalizacaoBolsistaExtensao.cancelar }" onclick="#{confirm}"/>
							</center>
				    	</td>
				    </tr>
			</tfoot>																			
			</table>
		</c:if>
			
		<c:if test="${empty bolsistasFinalizadosSigaa}">
			<center><i> Nenhum discente finalizado no SIGAA e ativo no SIPAC foi localizado </i></center>
		</c:if>
		
		<rich:jQuery selector="#listagem" query="tablesorter( {dateFormat: 'uk', headers: {0: { sorter: false },1: { sorter: 'shortDate' },5: { sorter: 'shortDate'  }, 6: { sorter: 'shortDate' },7: { sorter: false },8: { sorter: false } } });" timing="onload" />
</h:form>
	

	<br/>
		<center><h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/>
				<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		</center>
	<br/>


</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script type="text/javascript">
function checkAll(elem) {
	$A(document.getElementsByClassName('check')).each(function(e) {
		e.checked = elem.checked;
	});
}
</script>