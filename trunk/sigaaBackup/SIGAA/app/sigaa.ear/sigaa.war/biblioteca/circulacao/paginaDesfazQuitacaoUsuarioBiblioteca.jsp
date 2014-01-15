<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2> <ufrn:subSistema /> &gt; Defazer a quita��o de um V�nculo </h2>

	<div class="descricaoOperacao">
		<p>Prezado operador,</p>
		<p>Nesta p�gina, � poss�vel desfazer a quita��o de algum v�nculo de um usu�rio espec�fico.</p>
		<p><strong>Essa opera��o se destina a exce��es nas quais o usu�rio tenha emitido a quita��o por engano, mas posteriormente, surgiu a necessidade de continuar realizando empr�stimos na biblioteca. </strong> </p>
		<p>S� ser� poss�vel reativar um v�nculo, caso o usu�rio j� n�o esteja utilizando outro. Sen�o ser� preciso primeiro quitar o outro v�nculo, pois o usu�rio n�o pode
		permanecer com dois ou mais v�nculos n�o quitados ao mesmo tempo. </p>
	</div>

	<%-- Mant�m as informa��es do usu�rio e a situa��o entre as requisi��es --%>
	<a4j:keepAlive beanName="desfazQuitacaoVinculoUsuarioBibliotecaMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />

	<h:form id="formDesfazQuitacaoUsuario">
	
		<c:set var="_infoUsuarioCirculacao" value="${desfazQuitacaoVinculoUsuarioBibliotecaMBean.infoUsuario}" scope="request" />
		<c:set var="_mostrarVinculo" value="false" scope="request" />						
		<%@include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%>
	
		<div class="infoAltRem" style="width: 80%">
			<h:graphicImage value="/img/alterar_old.gif" style="overflow: visible;"/>: 
			Retirar Quita��o
		</div> 
	
		<table class="formulario" style="width:80%;">
			<caption>V�nculos do Usu�rio</caption>
			
			<thead>
				<tr>
					<td>V�nculo</td>
					<td>Data da Quita��o</td>
					<td style="width: 5%"></td>
				</tr>
			</thead>
			
			<c:if test="${not empty desfazQuitacaoVinculoUsuarioBibliotecaMBean.contasUsuarioBiblioteca}">
				<c:forEach items="#{desfazQuitacaoVinculoUsuarioBibliotecaMBean.contasUsuarioBiblioteca}"  var="contaUsuarioBiblioteca" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" >
					<td> ${contaUsuarioBiblioteca.vinculo.descricao} </td>
					<td> <ufrn:format type="dataHora" valor="${contaUsuarioBiblioteca.dataQuitacao}" /> </td>
					<td>
						<h:commandLink id="cmdLinkRetirarQuitacao" action="#{desfazQuitacaoVinculoUsuarioBibliotecaMBean.retirarQuitacao}"
								rendered="#{contaUsuarioBiblioteca.quitado && desfazQuitacaoVinculoUsuarioBibliotecaMBean.todosVinculosQuitados}"
								onclick="return confirm('Retirar a quita��o desse V�nculo ? ');">
							<h:graphicImage url="/img/alterar_old.gif" style="border:none" title="Retirar Quita��o" /> 					
							<f:param name="idContaUsuarioBibliotecaSelecionado" value="#{contaUsuarioBiblioteca.id}" />
						</h:commandLink> 
					</td>
					</tr>
				</c:forEach>
			</c:if>
			
			<c:if test="${empty desfazQuitacaoVinculoUsuarioBibliotecaMBean.contasUsuarioBiblioteca}">
				<tr>
					<td style="color: red; text-align: center;"> Usu�rio n�o possuiu contas na biblioteca </td>
				</tr>
			</c:if>
		
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="<< Voltar" action="#{buscaUsuarioBibliotecaMBean.telaBuscaUsuarioBiblioteca}"></h:commandButton>
					</td>
				</tr>
			</tfoot>
		</table>
	
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>