<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<script type="text/javascript" src="/shared/loadScript?src=javascript/jquery.tablesorter.min.js"></script>
<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />

<f:view>

	<h2><ufrn:subSistema /> > Homologação de Bolsas para Discentes de Monitoria</h2>	
	<br/>
	
<h:form id="form">


	<table class="formulario" width="90%">
		<caption>Busca por Discentes de Monitoria</caption>
			<tbody>
			    <tr>					
			    	<th class="required" width="30%"> Edital: </th>
			    	<td>	    	
			    	 <h:selectOneMenu id="buscaEdital" value="#{homologacaoBolsistaMonitoria.edital.id}">
						<f:selectItem itemLabel="-- SELECIONE UM EDITAL --" itemValue="0"/>
			    	 	<f:selectItems value="#{editalMonitoria.allCombo}" />
			    	 </h:selectOneMenu>	    	 
			    	 </td>
			    </tr>				
			</tbody>
			
			<tfoot>			
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{ homologacaoBolsistaMonitoria.buscar }"/>
						<h:commandButton value="Cancelar" action="#{ homologacaoBolsistaMonitoria.cancelar }" onclick="#{confirm}"/>
			    	</td>
			    </tr>
			</tfoot>
	</table>
		
	<br/>
		<center><h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/>
				<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		</center>
	<br/>
	
	<div class="infoAltRem">
	    <h:graphicImage url="/img/monitoria/user1_view.png" style="overflow: visible;"/>: Visualizar Discente	    
	</div>

	<br/>

	<c:set var="discentesBolsistasSigaa" value="#{homologacaoBolsistaMonitoria.discentesBolsistasSigaa}"/>



	<c:if test="${not empty discentesBolsistasSigaa}">
		 <table class="listagem tablesorter" id="listagem"" width="100%">
		    <caption>Discentes Encontrados (${ fn:length(discentesBolsistasSigaa) })</caption>

		      <thead>
		      	<tr>
		      	
					<th><h:selectBooleanCheckbox value="true" onclick="checkAll(this)"/></th>		      	
		        	<th align="center">Data da Indicação</th>		        	
		        	<th width="8%">Matricula</th>
		        	<th>Nome</th>
		        	<th>Curso</th>
		        	<th align="center">Início da Bolsa</th>
		        	<th align="center">Fim da Bolsa</th>		        	
		        	<th></th>
		        </tr>
		 	</thead>
		 	<tbody>

	       		<c:forEach items="#{discentesBolsistasSigaa}" var="dm" varStatus="status">				
					
	               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
	                   
	                   <td><h:selectBooleanCheckbox value="#{dm.selecionado}" styleClass="check"/></td> 
	                   <td align="center"><h:outputText value="#{dm.dataCadastro}"/></td>	                   
	                   <td><h:outputText value="#{dm.discente.matricula}"/></td>
	                   <td><h:outputText value="#{dm.discente.nome}"/></td>
	                   <td><h:outputText value="#{dm.discente.curso.descricao}"/></td>
	                   <td align="center"><h:outputText value="#{dm.dataInicio}"/></td>
	                   <td align="center"><h:outputText value="#{dm.dataFim}"/></td>	  
                       <td>
          					<h:commandLink title="Visualizar Discente" action="#{ consultarMonitor.view }" id="ver_discente">
							      <f:param name="id" value="#{dm.id}"/>
							      <h:graphicImage url="/img/monitoria/user1_view.png" />
							</h:commandLink>
						</td>
	                                    	                   
	               </tr>
	          	</c:forEach>
	        </tbody>
	
			
			<tfoot>
					<tr>
						<td colspan="10">
							<center>
								<h:commandButton value="Homologar Cadastro de Bolsistas Selecionados" action="#{ homologacaoBolsistaMonitoria.homologarBolsas }"/>
								<h:commandButton value="Cancelar" action="#{ homologacaoBolsistaMonitoria.cancelar }" onclick="#{confirm}"/>
							</center>
				    	</td>
				    </tr>
			</tfoot>
			
																						
			</table>
		</c:if>
			
		<c:if test="${empty discentesBolsistasSigaa}">
			<center><i> Nenhum discente localizado </i></center>
		</c:if>		
		
</h:form>
	

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script type="text/javascript">
function checkAll(elem) {
	$A(document.getElementsByClassName('check')).each(function(e) {
		e.checked = elem.checked;
	});
}
</script>