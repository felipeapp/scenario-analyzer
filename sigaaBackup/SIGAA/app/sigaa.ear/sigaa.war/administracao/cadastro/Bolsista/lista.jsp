<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Bolsista</h2>

	<h:outputText value="#{bolsista.create}" />
	
		<center>
			<h:messages/>

			<div class="infoAltRem">
	  			
				<h:form>
					<%-- 
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>:
					<h:commandLink action="#{bolsista.preCadastrar}" value="Cadastrar"/>
					--%>
					<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
					<%-- 
			        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
			        --%>
				</h:form>
			    
			</div>
	</center>


<%-- 
 	<h:form id="formBuscaBolsista">

	<table class="formulario" width="90%">
	<caption>Busca Por Bolsistas</caption>
	<tbody>
	
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{bolsista.checkBuscaTipoBolsa}" id="selectBuscaTipoBolsa"/>
			</td>
	    	<td> <label for="tipoBolsa"> Tipo de Bolsa </label> </td>
	    	<td> 
	    	<h:inputText value="#{bolsista.buscaTipoBolsa}" size="70" onchange="javascript:$('formBusca:selectBuscaTipoBolsa').checked = true;"/>
		    	 <h:selectOneMenu value="#{bolsista.buscaTipoBolsa}" onchange="javascript:$('formBusca:selectBuscaTipoBolsa').checked = true;">
						<f:selectItem itemValue="0" itemLabel="-- OPÇÕES --"  />
						<f:selectItems value="#{tipoBolsaUfrn.allCombo}"/>
	 			 </h:selectOneMenu>	    	
	    	</td>
	    </tr>


		<tr>
			<td>
			<h:selectBooleanCheckbox value="#{bolsista.checkBuscaEntidade}"  id="selectBuscaEntidade" />
			</td>
	    	<td> <label for="entidade"> Entidade Financiadora </label> </td>
	    	<td>

	    	 <h:selectOneMenu value="#{bolsista.buscaEntidade}" onchange="javascript:$('formBusca:selectBuscaEntidade').checked = true;">
					<f:selectItem itemValue="0" itemLabel="-- OPÇÕES --"  />
					<f:selectItems value="#{entidadeFinanciadora.allCombo}"/>
 			 </h:selectOneMenu>

	    	 </td>
	    </tr>
 

	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
			<h:commandButton value="Buscar" action="#{ bolsista.iniciarLocalizacao }"/>
			<h:commandButton value="Cancelar" action="#{ bolsista.cancelar }"/>
	    	</td>
	    </tr>
	</tfoot>
	</table>

	</h:form>

	<br/>
	<br/>
--%>


	
	
	
	<table class="listagem" style="width: 100%">
		<caption>Lista de Bolsistas</caption>
		<thead>
		<tr>
			<td>Discente</td>
			<td>Tipo de Bolsa</td>
			<td>Entidade Financiadora</td>
			<td>Data Inicio</td>
			<td>Data Fim</td>
			<td></td>
			<%-- 
			<td></td>
			--%>
		</tr>
		</thead>
		<c:forEach items="${bolsista.allPaginado}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.discente}</td>
				<td>${item.tipoBolsa.descricao}</td>
				<td>${item.entidadeFinanciadora}</td>
				<td><fmt:formatDate value="${item.inicio}" pattern="dd/MM/yyyy"/></td>
				<td><fmt:formatDate value="${item.fim}" pattern="dd/MM/yyyy"/></td>
				<h:form>
					<td width=20><input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/alterar.gif" value="Alterar"
						action="#{bolsista.atualizar}" /></td>
				</h:form>
				<%-- 
				<h:form>
					<td width=25>
						<input type="hidden" value="${item.id}" name="id" /> 
						<h:commandButton image="/img/delete.gif" alt="Remover" action="#{bolsista.preRemover}" />
					</td>
				</h:form>
				--%>
			</tr>
		</c:forEach>
	</table>
	
	<center>
	<h:form>
		<h:messages showDetail="true"/>
		<h:commandButton image="/img/voltar_des.gif" actionListener="#{paginacao.previousPage}"/>
		<h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{paginacao.changePage}" onchange="submit()" immediate="true">
			<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>
		</h:selectOneMenu>
		<h:commandButton image="/img/avancar.gif" actionListener="#{paginacao.nextPage}"/>
	</h:form>
	</center>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>