<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<script type="text/javascript">var J = jQuery.noConflict();</script>
<script type="text/javascript">	
	JAWR.loader.script('/javascript/jquery.tablesorter.min.js');	
</script>
<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />

<style>
        .center{
            text-align:center;
        } 
</style>

<f:view>
	
	<c:if test="${!acesso.extensao}">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</c:if>
	
	<h2><ufrn:subSistema /> > Consultar Dados Bancários</h2>

	<h:outputText value="#{discenteExtensao.create}"/>


 	<h:form id="form">

	<table class="formulario" width="90%">
	<caption>Busca por Dados Bancários</caption>
	<tbody>

		<tr>
			<td> <h:selectBooleanCheckbox value="#{discenteExtensao.checkBuscaTituloAtividade}" id="selectBuscaTitulo" styleClass="noborder"/> </td>
	    	<td> <label for="tituloAtividade"> Título da Ação: </label> </td>
	    	<td> <h:inputText id="buscaTitulo" value="#{discenteExtensao.buscaTituloAtividade}" size="50" onfocus="javascript:$('form:selectBuscaTitulo').checked = true;"/></td>
	    </tr>
		
		<tr>
			<td><h:selectBooleanCheckbox value="#{discenteExtensao.checkBuscaAno}" id="selectBuscaAno" /></td>
	    	<td> <label for="anoAcao"> Ano da Ação: </label> </td>
	    	<td> <h:inputText value="#{discenteExtensao.anoReferencia}" maxlength="4" size="10" onfocus="javascript:$('form:selectBuscaAno').checked = true;" onkeyup="return formatarInteiro(this)"/></td>
	    </tr>
	    
	    <tr>
			<td><h:selectBooleanCheckbox value="#{discenteExtensao.checkBuscaAnoInicio}" id="selectBuscaAnoInicioFim" /></td>
	    	<td> <label for="anoAcao"> Período: </label> </td>
	    	<td>
				<t:inputCalendar
					renderAsPopup="true"
					renderPopupButtonAsImage="true"
					value="#{discenteExtensao.dataInicio}"
					popupDateFormat="dd/MM/yyyy"
					popupTodayString="Hoje é"
					size="10"
					maxlength="10"
					onkeypress="return(formataData(this,event))">
					<f:converter converterId="convertData"/>
				</t:inputCalendar>
				a
				<t:inputCalendar
					renderAsPopup="true"
					renderPopupButtonAsImage="true"
					value="#{discenteExtensao.dataFim}"
					popupDateFormat="dd/MM/yyyy"
					popupTodayString="Hoje é"
					size="10"
					maxlength="10"
					onkeypress="return(formataData(this,event))">
					<f:converter converterId="convertData"/>
				</t:inputCalendar>
				<ufrn:help img="/img/ajuda.gif">Período de execução do plano de trabalho do discente.</ufrn:help>
			</td>
	    </tr>
		    
		<tr>
			<td>
			<h:selectBooleanCheckbox value="#{discenteExtensao.checkBuscaDiscente}"  id="selectBuscaDiscente" />
			</td>
	    	<td> <label> Discente: </label> </td>
			<td>
		
			 <h:inputText id="nomeDiscente" value="#{ discenteExtensao.discente.pessoa.nome }" size="60" onchange="javascript:$('form:selectBuscaDiscente').checked = true;" onfocus="javascript:$('form:selectBuscaDiscente').checked = true;"/>
			 <h:inputHidden id="idDiscente" value="#{ discenteExtensao.discente.id }"/>
		
			<ajax:autocomplete source="form:nomeDiscente" target="form:idDiscente"
				baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
				indicator="indicatorDiscente" minimumCharacters="3" parameters="nivel=ufrn"
				parser="new ResponseXmlToHtmlListParser()" />
			<span id="indicatorDiscente" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
			</td>
		</tr>

		<tr>
			<td>
			<h:selectBooleanCheckbox value="#{discenteExtensao.checkBuscaSituacao}"  id="selectBuscaSituacao" />
			</td>
	    	<td> <label> Situação: </label> </td>
	    	<td>

	    	 <h:selectOneMenu value="#{discenteExtensao.situacaoDiscenteExtensao.id}" style="width: 200px"  onchange="javascript:$('form:selectBuscaSituacao').checked = true;">
				<f:selectItems value="#{discenteExtensao.allSituacaoDiscenteExtensaoCombo}"/>
 			 </h:selectOneMenu>

	    	 </td>
	    </tr>
	    
	    
	    <tr>
			<td>
			<h:selectBooleanCheckbox value="#{discenteExtensao.checkBuscaVinculo}"  id="selectBuscaVinculo" />
			</td>
	    	<td> <label> Vínculo: </label> </td>
	    	<td>
		    	 <h:selectOneMenu value="#{discenteExtensao.vinculoDiscente.id}" style="width: 200px"  onchange="javascript:$('form:selectBuscaVinculo').checked = true;">
					<f:selectItems value="#{tipoVinculoDiscenteBean.allAtivosExtensaoCombo}"/>
	 			 </h:selectOneMenu>
	    	 </td>
	    </tr>
	    
	    

		<tr>
			<td>
			<h:selectBooleanCheckbox value="#{discenteExtensao.checkBuscaCurso}"  id="selectBuscaCurso" />
			</td>
	    	<td> <label> Curso do Discente: </label> </td>
	    	<td>

	    	 <h:selectOneMenu value="#{discenteExtensao.curso.id}" style="width: 400px"  onfocus="javascript:$('form:selectBuscaCurso').checked = true;">
				<f:selectItems value="#{curso.allCursoGraduacaoCombo}"/>
 			 </h:selectOneMenu>

	    	 </td>
	    </tr>
	    
	    <tr>
			<td>
			<h:selectBooleanCheckbox value="#{discenteExtensao.checkGerarRelatorio}"  id="selectGerarRelatorio" />
			</td>
	    	<td colspan="2"> <label> <b>Gerar Relatório</b></label> </td>
	    </tr>    

	    
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
			<h:commandButton value="Buscar" action="#{ discenteExtensao.localizarRelatorioDadosBancarios }"/>
			<h:commandButton value="Cancelar" action="#{ discenteExtensao.cancelar }" onclick="#{confirm}"/>
	    	</td>
	    </tr>
	</tfoot>
	</table>
	
	<br/>
	
	<c:if test="${not empty discenteExtensao.discentesExtensao}">
	<div class="infoAltRem">
		 <h:graphicImage value="/img/extensao/user1_view.png" style="overflow: visible;"/>: Visualizar Dados do Discente	
		 <h:graphicImage value="/img/report.png" style="overflow: visible;"/>: Visualizar Plano de Trabalho	 
		 <h:graphicImage value="/img/extensao/ordenacao.png" style="overflow: visible;" width="12px" height="12px"/>: Ordenação da coluna
	</div>
	</c:if>

	<br/>

	<c:set var="discentes" value="${discenteExtensao.discentesExtensao}"/>

	<c:if test="${empty discentes}">
	<center><i> Nenhum Discente localizado </i></center>
	</c:if>


	<c:if test="${not empty discentes}">

		 <table class="listagem tablesorter" id="listagem">
		    <caption>Lista de Discentes Encontrados (${ fn:length(discentes) })</caption>
	
		      <thead>
		      	<tr>
		      		<th>Cadastro</th>
		      		<th>Matrícula</th>
		        	<th>Discente</th>
		        	<th>Banco</th>
		        	<th>Agência</th>
		        	<th>Conta</th>
		        	<th>Operação</th>
		        	<th>Vínculo</th>
		        	<th>Situação</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>
		        	
		        	
		        </tr>
		 	</thead>
		 	<tbody>
		 	
			 	 <c:forEach items="#{discenteExtensao.discentesExtensao}" var="de" varStatus="status">
		               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		               	<td><fmt:formatDate value="${ de.dataCadastro }" pattern="yyyy/MM/dd"/></td>		               	
		               	<td width="15%"><h:outputText value="#{de.discente.matricula}"/></td>
		               	<td width="37%"><h:outputText value="#{de.discente.nome}"/></td>
		               	<td width="10%"><h:outputText value="#{de.banco.codigo}"/> - <h:outputText value="#{de.banco.denominacao}"/></td>
		               	<td><h:outputText value="#{de.agencia}"/></td>
		               	<td><h:outputText value="#{de.conta}"/></td>
		               	<td><h:outputText value="#{de.operacao}"/></td>
		               	<td><h:outputText value="#{de.tipoVinculo.descricao}"/></td>
		               	<td width="15%"><h:outputText value="#{de.situacaoDiscenteExtensao.descricao}"/></td>
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
		 </table>
	
	</c:if>
	<rich:jQuery selector="#listagem" query="tablesorter( {headers: {3: { sorter: false }, 4: { sorter: false }, 5: { sorter: false }, 6: { sorter: false },8: { sorter: false },9: { sorter: false } } });" timing="onload" />
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>