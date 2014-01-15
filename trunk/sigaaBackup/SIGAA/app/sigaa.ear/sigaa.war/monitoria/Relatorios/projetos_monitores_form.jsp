<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria"%>
<style>
	table.listagem tr.curso td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
</style>

<f:view>
<h:messages showDetail="true"/>
	<h2><ufrn:subSistema /> > Relatório Quantitativo de Monitores Por Projetos do Centro</h2>
	<br>
	<h:outputText value="#{comissaoMonitoria.create}"/>

 	<h:form id="formBuscaProjeto">

	<table class="formulario" width="90%">
	<caption>Monitores por Projeto do Centro</caption>
	<tbody>
	    
		 <tr>
			<td>
			<h:selectBooleanCheckbox value="#{comissaoMonitoria.checkBuscaCentro}" id="selectBuscaCentro"/>
			</td>
			

	    	<td> <label for="nomeCentro"> Centro do Projeto: </label> </td>
	    	
	    	<td>
	    	<h:selectOneMenu id="selectUnidade" onchange="javascript:$('formBuscaProjeto:selectBuscaCentro').checked = true;" 
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
			<h:selectBooleanCheckbox value="#{comissaoMonitoria.checkGerarRelatorio}"  id="selectGerarRelatorio" />
			</td>
	    	<td colspan="2"> <label> <b>Gerar Relatório</b></label> </td>
	    </tr>	     
       

	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
			<h:commandButton id="BtnBuscar" value="Buscar" action="#{ comissaoMonitoria.relatorioMonitoresPorProjeto }"/>
			<h:commandButton id="BtnCancelar" value="Cancelar" action="#{ comissaoMonitoria.cancelar }"/>
	    	</td>
	    </tr>
	</tfoot>
	</table>

	</h:form>
	
				
	<c:set var="ASSUMIU_MONITORIA" value="<%= String.valueOf(SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA) %>" scope="application"/>	 	
	<c:set var="projetos" value="${comissaoMonitoria.projetos}"/>
	
	<c:if test="${empty projetos}">
	   <br/>
	   <center><i>Projetos não localizados</i></center>
	</c:if>
	
	<c:if test="${not empty projetos}">
		<br />
		<table class="listagem">
			<caption> Monitores Por Projeto<br/>
			${ fn:length(projetos) } Projetos 
			<c:if test="${comissaoMonitoria.checkBuscaCentro}">
				 do ${comissaoMonitoria.unidade.sigla}
			</c:if> 
			possuem ${comissaoMonitoria.total} Monitores ativos
			<c:if test="${comissaoMonitoria.checkBuscaCurso}">
				do curso de ${comissaoMonitoria.curso.descricao}
			</c:if> 		
		</caption>

			<thead>
			<tr>
				<th> Discente </th>
				<th> Situação</th>
				<th> Vínculo </th>
				<th> </th>
			</tr>
			</thead>
			
			<tbody>
				<c:forEach items="${projetos}" var="projeto" varStatus="status">

					<tr class="curso">
						<td colspan="4"> ${projeto.anoTitulo} - ${projeto.unidade.sigla}</td>
					</tr>

					<c:set var="monitoresAtivos" value="false"/>
					<c:forEach items="${projeto.discentesMonitoria}" var="dm" varStatus="status">
						<c:if test="${dm.situacaoDiscenteMonitoria.id == ASSUMIU_MONITORIA}">
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
									<td>${dm.discente.matriculaNome} </td>
									<td>${dm.situacaoDiscenteMonitoria.descricao}</td>
									<td>${dm.tipoVinculo.descricao}</td>
									<c:set var="monitoresAtivos" value="true"/>
								</tr>
						</c:if>
					</c:forEach>
					
				    <c:if test="${monitoresAtivos == false}">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<td colspan="3"><font color="red"> Não há monitores ativos neste projeto de monitoria</td>
						</tr>
					</c:if>
					
				</c:forEach>
			</tbody>
		</table>
	</c:if>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>