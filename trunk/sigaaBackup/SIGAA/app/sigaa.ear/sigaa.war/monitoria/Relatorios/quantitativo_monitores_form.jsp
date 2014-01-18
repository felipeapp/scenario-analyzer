<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h:messages showDetail="true"/>
	<h2><ufrn:subSistema /> > Relatório Quantitativo de Monitores Por Projetos</h2>
	
	<c:set var="confirmm"
		value="return confirm('Deseja cancelar a operação?');"
		scope="application" />

	<h:outputText value="#{comissaoMonitoria.create}"/>

 	<h:form id="formBuscaProjeto">


	<table class="formulario" width="100%">
	<caption>Busca por Monitores</caption>
	<tbody>
	    
		 <tr>
			<td>
			<h:selectBooleanCheckbox value="#{comissaoMonitoria.checkBuscaCentro}" id="selectBuscaCentro"/>
			</td>
			
	    	<td> <label for="nomeProjeto"> Centro do Projeto: </label> </td>
	    	
	    	<td>
	    	<h:selectOneMenu id="selectUnidade" onfocus="javascript:$('formBuscaProjeto:selectBuscaCentro').checked = true;" 
	    		value="#{comissaoMonitoria.unidade.id}">
				<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM CENTRO --"  />
				<f:selectItems value="#{unidade.allCentroCombo}"/>
			</h:selectOneMenu>
	    </tr>		    
	    
	    
	    <tr>
			<td>
			<h:selectBooleanCheckbox value="#{comissaoMonitoria.checkBuscaCurso}" id="selectBuscaCurso"/>
			</td>
			

	    	<td> <label for="nomeCurso"> Curso do Discente: </label> </td>
	    	
	    	<td>
	    	<h:selectOneMenu id="selectCurso" onchange="javascript:$('formBuscaProjeto:selectBuscaCurso').checked = true;" 
	    		value="#{comissaoMonitoria.curso.id}" style="width: 400px">
				<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM CURSO --"  />
				<f:selectItems value="#{curso.allCursoGraduacaoCombo}"/>
			</h:selectOneMenu>
	    </tr>		    
	    

		 <tr>
			<td>
				<h:selectBooleanCheckbox value="#{comissaoMonitoria.checkBuscaAno}" id="selectBuscaAno"/>
			</td>
			
	    	<td><label for="anoProjeto"> Ano do Projeto: </label></td>
	    	
	    	<td>
	    	<h:inputText id="inputAno" value="#{comissaoMonitoria.ano}" onfocus="javascript:$('formBuscaProjeto:selectBuscaAno').checked = true;" size="6" maxlength="4" onkeyup="formatarInteiro(this)" />
	    </tr>		    
	
	
	
		<tr>
			<td>
			<h:selectBooleanCheckbox value="#{comissaoMonitoria.checkBuscaTipoMonitor}"  id="selectBuscaTipoMonitor" />
			</td>
	    	<td> <label> Vínculo do Monitor: </label> </td>
	    	<td>

	    	 <h:selectOneMenu value="#{comissaoMonitoria.idTipoMonitor}" style="width: 200px"  onchange="javascript:$('formBuscaProjeto:selectBuscaTipoMonitor').checked = true;">
	    	 	<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM TIPO --"  />
				<f:selectItems value="#{discenteMonitoria.tipoMonitoriaCombo}"/>
 			 </h:selectOneMenu>

	    	 </td>
	    </tr>	
	
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{comissaoMonitoria.checkGerarRelatorio}"  id="selectGerarRelatorio" />
			</td>
	    	<td colspan="2"> <label> <b>Gerar Relatório</b></label> </td>
	    </tr>	     
       

	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
				<h:commandButton value="Buscar" action="#{ comissaoMonitoria.relatorioQuantitativoMonitores }"/>
				<h:commandButton value="Cancelar" onclick="#{confirmm}" action="#{ comissaoMonitoria.cancelar }"/>
	    	</td>
	    </tr>
	</tfoot>
	</table>

	</h:form>
	<br/>
	
<h:form>
	<table class="listagem" width="100%">
		<h:outputText value="#{comissaoMonitoria.create}" />
		<c:set value="${comissaoMonitoria.projetos}" var="lista"/>
		<caption class="listagem"> Monitores Ativos Por Projetos <br/>
		 ${comissaoMonitoria.total} Monitores
		 	<c:if test="${comissaoMonitoria.checkBuscaTipoMonitor}">
				${comissaoMonitoria.idTipoMonitor == 1?'Não Remunerados':''}
				${comissaoMonitoria.idTipoMonitor == 2?'Bolsistas':''}
				${comissaoMonitoria.idTipoMonitor == 3?'Não Classificados':''}
				${comissaoMonitoria.idTipoMonitor == 4?'Em Espera':''}
			</c:if> 		
			<c:if test="${comissaoMonitoria.checkBuscaCurso}">
				do curso ${comissaoMonitoria.curso.descricao}
			</c:if>
			distribuídos em projetos 		
			<c:if test="${comissaoMonitoria.checkBuscaCentro}">
				  do ${comissaoMonitoria.unidade.sigla}
			</c:if> 	
			<c:if test="${comissaoMonitoria.checkBuscaAno}">
				 submetidos em ${comissaoMonitoria.ano}
			</c:if>
			 								
		</caption>
<tbody>						


	<tr>
		<td colspan="2">

		<t:dataTable value="#{comissaoMonitoria.projetos}" var="projeto" align="center"  rowClasses="linhaPar, linhaImpar" width="100%">	
		
					<t:column rendered="#{projeto.totalMonitores != 0}">
						<f:facet name="header">
							<f:verbatim>Ano</f:verbatim>
						</f:facet>
						<h:outputText value="#{projeto.ano}" />						
					</t:column>

					<t:column rendered="#{projeto.totalMonitores != 0}">
						<f:facet name="header">
							<f:verbatim>Título</f:verbatim>
						</f:facet>
						<h:outputText value="#{projeto.titulo}" />						
					</t:column>

					<t:column rendered="#{projeto.totalMonitores != 0}">
						<f:facet name="header">
							<f:verbatim>Centro</f:verbatim>
						</f:facet>
						<h:outputText value="#{projeto.unidade.sigla}" />						
					</t:column>
					
					<t:column width="10%" rendered="#{projeto.totalMonitores != 0}">
						<f:facet name="header">
							<f:verbatim>Total</f:verbatim>
						</f:facet>
						<h:outputText value="#{projeto.totalMonitores}" />						
					</t:column>					
					
		</t:dataTable>
		</td>
	</tr>
	<tfoot>
		<tr>
			<td>TOTAL DE MONITORES ATIVOS</td>
	    	<td>${comissaoMonitoria.total}</td>
	    </tr>
	</tfoot>
	</tbody>
	
</table>


</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>