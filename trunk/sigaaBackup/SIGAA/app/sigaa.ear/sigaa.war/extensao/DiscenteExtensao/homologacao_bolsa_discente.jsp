<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<script type="text/javascript" src="/shared/loadScript?src=javascript/jquery.tablesorter.min.js"></script>
<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />

<f:view>
	<h2><ufrn:subSistema /> > Homologação de Bolsas para Discentes de Extensão</h2>
	<br/>
	
<h:form id="form">

	<table class="formulario" width="90%">
		<caption>Busca por Discentes de Extensão</caption>
			<tbody>
			    <tr>					
			    	<th class="required" width="30%"> Edital: </th>
			    	<td>	    	
			    	 <h:selectOneMenu id="buscaEdital" value="#{homologacaoBolsistaExtensao.edital.id}">
						<f:selectItem itemLabel="-- SELECIONE UM EDITAL --" itemValue="0"/>
			    	 	<f:selectItems value="#{editalExtensao.allCombo}" />
			    	 </h:selectOneMenu>	    	 
			    	 </td>
			    </tr>				
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
					<h:commandButton value="Buscar" action="#{ homologacaoBolsistaExtensao.buscar }"/>
					<h:commandButton value="Cancelar" action="#{ homologacaoBolsistaExtensao.cancelar }" onclick="#{confirm}"/>
			    	</td>
			    </tr>
			</tfoot>
	</table>
	<center><h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/><span class="fontePequena"> Campos de preenchimento obrigatório. </span></center>
	<br/>
	
	<c:if test="${not empty homologacaoBolsistaExtensao.discentesBolsistasSigaa}">
			<div class="infoAltRem">
			    <h:graphicImage value="/img/extensao/user1_view.png" style="overflow: visible;"/>: Visualizar Dados do Discente
			    <h:graphicImage value="/img/report.png" style="overflow: visible;"/>: Visualizar Plano de Trabalho
			</div>
			<br/>
		
			<c:set var="discentesBolsistasSigaa" value="#{homologacaoBolsistaExtensao.discentesBolsistasSigaa}"/>
			 <table class="listagem tablesorter" id="listagem"" width="100%">
				    <caption>Discentes Encontrados (${ fn:length(discentesBolsistasSigaa) })</caption>
		
				      <thead>
				      	<tr>
							<th><h:selectBooleanCheckbox value="true" onclick="checkAll(this)"/></th>		      	
				        	<th align="center">Data da Indicação</th>
				        	<th align="center" width="10%">Cód. Ação</th>
				        	<th width="8%">Matricula</th>
				        	<th>Nome</th>
				        	<th>Curso</th>
				        	<th align="center">Início da Bolsa</th>
				        	<th align="center">Fim da Bolsa</th>
				        	<th></th>
				        	<th></th>		        	
				        </tr>
				 	</thead>
				 	<tbody>
		
			       		<c:forEach items="#{homologacaoBolsistaExtensao.discentesBolsistasSigaa}" var="de" varStatus="status">				
							
			               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			                   <td><h:selectBooleanCheckbox value="#{de.selecionado}" styleClass="check"/></td> 
			                   <td align="center"><h:outputText value="#{de.dataCadastro}"/></td>
			                   <td align="center"><h:outputText value="#{de.planoTrabalhoExtensao.atividade.codigo}" title="#{de.planoTrabalhoExtensao.atividade.codigoTitulo}"/></td>
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
								<td colspan="10">
									<center>
										<h:commandButton value="Homologar Cadastro de Bolsistas Selecionados" action="#{ homologacaoBolsistaExtensao.homologarBolsas }"/>
										<h:commandButton value="Cancelar" action="#{ homologacaoBolsistaExtensao.cancelar }" onclick="#{confirm}"/>
									</center>
						    	</td>
						    </tr>
					</tfoot>																			
			</table>
		</c:if>
			
		<c:if test="${empty discentesBolsistasSigaa}">
			<center><i> Nenhum discente pendente de homologação de bolsa para o edital selecionado </i></center>
		</c:if>
		
		<rich:jQuery selector="#listagem" query="tablesorter( { dateFormat: 'uk', headers: {0: { sorter: false },1: { sorter: 'shortDate' },6: { sorter: 'shortDate' },7: { sorter: 'shortDate' },8: { sorter: false },9: { sorter: false } } });" timing="onload" />
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