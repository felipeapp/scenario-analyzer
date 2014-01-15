<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria"%>
<%@page import="br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria"%>

<script type="text/javascript" src="/shared/loadScript?src=javascript/jquery.tablesorter.min.js"></script>
<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />


<f:view>
	<h2><ufrn:subSistema /> > Cancelar Bolsas de Monitores</h2>
	<br>
	<h:form id="form">
	
		<c:if test="${monitoria.frequenciaMonitoria}">
			<table width="100%" class="subFormulario" id="aviso">
				<tr>
				<td width="40"><html:img page="/img/help.png"/> </td>
				<td valign="top" style="text-align: justify">
				<font color="red" size="2">Atenção:</font> Esta operação destina-se ao cancelamento de bolsas de monitores que não enviaram os relatórios de atividades mensais (frequência) ou que enviaram mas não foram validados.
				Conforme Art. 23 da Resolução Nº 013/2006 - CONSEPE, que estabelece normas para o Programa de Monitoria.
				<br/>
				</td>
				</tr>
			</table>
		</c:if>
		
		<table class="formulario" width="100%" >
			<caption class="listagem">Buscar Monitores para Cancelamento de Bolsa</caption>


			<tr>
				<th width="40%" class="required">Ano da frequência:</th> 
				<td> 		
					<h:inputText value="#{ cancelarBolsaMonitoria.obj.ano }" id="txtAno" size="5" maxlength="4"/>
					
				</td>
			</tr>	

			<c:if test="${monitoria.frequenciaMonitoria}">
				<tr>
					<th width="40%" class="required">Discentes sem frequências validadas entre os meses de</th> 
					<td> 		
						<h:selectOneMenu
								value="#{ cancelarBolsaMonitoria.obj.mesInicio }" id="txtMesInicio">
								<f:selectItem itemLabel="Janeiro" 	itemValue="1"/>								
								<f:selectItem itemLabel="Fevereiro" itemValue="2"/>																
								<f:selectItem itemLabel="Março" 	itemValue="3"/>
								<f:selectItem itemLabel="Abril" 	itemValue="4"/>
								<f:selectItem itemLabel="Maio" 		itemValue="5"/>
								<f:selectItem itemLabel="Junho" 	itemValue="6"/>
								<f:selectItem itemLabel="Julho" 	itemValue="7"/>
								<f:selectItem itemLabel="Agosto" 	itemValue="8"/>
								<f:selectItem itemLabel="Setembro" 	itemValue="9"/>
								<f:selectItem itemLabel="Outubro" 	itemValue="10"/>
								<f:selectItem itemLabel="Novembro" 	itemValue="11"/>
								<f:selectItem itemLabel="Dezembro" 	itemValue="12"/>
						</h:selectOneMenu>
	                     e					
						<h:selectOneMenu
								value="#{ cancelarBolsaMonitoria.obj.mesFim }" id="txtMesFim">
								<f:selectItem itemLabel="Janeiro" 	itemValue="1"/>								
								<f:selectItem itemLabel="Fevereiro" itemValue="2"/>																
								<f:selectItem itemLabel="Março" 	itemValue="3"/>
								<f:selectItem itemLabel="Abril" 	itemValue="4"/>
								<f:selectItem itemLabel="Maio" 		itemValue="5"/>
								<f:selectItem itemLabel="Junho" 	itemValue="6"/>
								<f:selectItem itemLabel="Julho" 	itemValue="7"/>
								<f:selectItem itemLabel="Agosto" 	itemValue="8"/>
								<f:selectItem itemLabel="Setembro" 	itemValue="9"/>
								<f:selectItem itemLabel="Outubro" 	itemValue="10"/>
								<f:selectItem itemLabel="Novembro" 	itemValue="11"/>
								<f:selectItem itemLabel="Dezembro" 	itemValue="12"/>
						</h:selectOneMenu>
						
					</td>		
				</tr>
			</c:if>
			
			<tr>    
                <th class="required" width="45%">Tipo de Vínculo:</th> 
                <td>                    
                    <h:selectOneMenu
                            value="#{ cancelarBolsaMonitoria.tipoVinculo }" id="tipovinculo">
                            <f:selectItem itemLabel="TODOS"             itemValue="0"/>
                            <f:selectItem itemLabel="NÃO REMUNERADO"    itemValue="1"/>                             
                            <f:selectItem itemLabel="BOLSISTA"          itemValue="2"/>                                                             
                            <f:selectItem itemLabel="NÃO CLASSIFICADO"  itemValue="3"/>
                            <f:selectItem itemLabel="EM ESPERA"         itemValue="4"/>
                    </h:selectOneMenu>                  
                </td>               
            </tr>
			

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Buscar" action="#{cancelarBolsaMonitoria.buscar}" id="BtnBuscar" />
						<h:commandButton value="Cancelar" action="#{cancelarBolsaMonitoria.cancelar}" id="BtnCancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>

		<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center><br/>


		<c:set var="monitores" value="#{cancelarBolsaMonitoria.discentes}"/>
		<br/>
			<div class="infoAltRem">
			    	<h:graphicImage value="/img/monitoria/user1_view.png" style="overflow: visible;"/>: Visualizar Dados do Monitor
			    	<h:graphicImage value="/img/view.gif" 	style="overflow: visible;"/>: Visualizar Relatório do Monitor
			    	<h:graphicImage url="/img/monitoria/user1_delete.png" id="img_finalizarMonitoria"/>: Finalizar Monitor
			</div>
		<br/>


	<c:if test="${empty monitores}">
	<center><i> Nenhum monitor localizado </i></center>
	</c:if>


	<c:if test="${not empty monitores}">
			
		 <table class="listagem tablesorter" id="listagem">
		    <caption>Listagem dos Monitores (${ fn:length(monitores) })</caption>

		      <thead>
		      	<tr>				
		      	    <th><h:selectBooleanCheckbox value="true"  onclick="checkAll(this)"/></th>
		      	    <th>Matricula</th>			      	
		        	<th>Discente</th>
		        	<th>Projeto</th>
		        	<th>Início</th>
		        	<th>Fim</th>
		        	<th>Vínculo</th>
		        	<th>Situação</th>
		        	<th></th>
		        	<th></th>
		        	<th></th>
		        </tr>
		 	</thead>
		 	<tbody>
			
	       	<c:forEach items="#{monitores}" var="monitor" varStatus="status">				
					
	               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
	               
	                   <td><h:selectBooleanCheckbox value="#{monitor.discente.selecionado}" styleClass="check"/></td> 
	                   <td>${monitor.discente.matricula}</td>
	                   <td>${monitor.discente.nome}</td>
	                   <td>${monitor.projetoEnsino.ano}</td>
	                   <td><h:outputText value="#{monitor.dataInicio}"/></td>
	                   <td><h:outputText value="#{monitor.dataFim}"/></td>
   	                   <td>${monitor.tipoVinculo.descricao}</td>
						<td>${monitor.situacaoDiscenteMonitoria.descricao}</td>
						<td>		
								<h:commandLink id="lnkViewDadosMonitor" title="Visualizar Dados Monitor" action="#{ consultarMonitor.view }" immediate="true">
									<h:graphicImage url="/img/monitoria/user1_view.png"/>
									<f:param name="id" value="#{monitor.id}"/>
								</h:commandLink>
						</td>
						<td>		
								<h:commandLink id="lnkViewAtividades" title="Visualizar Atividades do Monitor" action="#{ cancelarBolsaMonitoria.listarAtividadesMonitor }" immediate="true">
									<h:graphicImage url="/img/view.gif"/>
									<f:param name="idDiscente" value="#{monitor.discente.id}"/>
								</h:commandLink>
						</td>
						<td>		
								<h:commandLink id="lnkFinalizarMonitor" title="Finalizar Monitoria" action="#{alterarDiscenteMonitoria.preFinalizarMonitoria}" rendered="#{acesso.monitoria || acesso.coordenadorMonitoria}" styleClass="noborder">				
							       <f:param name="id" value="#{monitor.id}"/>
							       <f:param name="paginaOrigem" value="CANCELAR_BOLSAS"/>							       
				                   <h:graphicImage url="/img/monitoria/user1_delete.png" id="img_finalizar_monitoria_"/>
								</h:commandLink>
						</td>
	              </tr>
	          </c:forEach>
		 	</tbody>		 	
		 </table>	
		 <rich:jQuery selector="#listagem" query="tablesorter({dateFormat: 'uk', headers: {0: { sorter: false }, 4:{ sorter: 'shortDate' }, 5:{ sorter: 'shortDate' }, 8: { sorter: false }, 9: { sorter: false }, 10: { sorter: false }} });" timing="onload" />	 
	</c:if>	
	
	
	
	<br />  
    <table class="subFormulario" width="100%" >
        <caption class="listagem">Notificar Monitores</caption>
            <tr>
                <th width="25%">Mensagem:</th>
                <td> <h:inputTextarea value="#{ cancelarBolsaMonitoria.mensagem }"  rows="5" style="width:90%" /> </td>
            </tr>           
            <tfoot>
                <tr>
                    <td colspan="2">
                        <h:commandButton id="btnEnviarNotificacao" value="Enviar Notificação" action="#{cancelarBolsaMonitoria.notificarMonitores}" />
                        <h:commandButton id="btnCancelar" value="Cancelar" action="#{envioFrequencia.cancelar}" onclick="#{confirm}"/>                       
                    </td>
                </tr>
            </tfoot>
    </table>
	
	<br/><center> <h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center><br/>
	
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