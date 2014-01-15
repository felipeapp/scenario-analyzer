<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2> <ufrn:subSistema /> &gt; Defazer a quitação de um Vínculo </h2>

	<div class="descricaoOperacao">
		<p>Prezado operador,</p>
		<p>Nesta página, é possível desfazer a quitação de algum vínculo de um usuário específico.</p>
		<p><strong>Essa operação se destina a exceções nas quais o usuário tenha emitido a quitação por engano, mas posteriormente, surgiu a necessidade de continuar realizando empréstimos na biblioteca. </strong> </p>
		<p>Só será possível reativar um vínculo, caso o usuário já não esteja utilizando outro. Senão será preciso primeiro quitar o outro vínculo, pois o usuário não pode
		permanecer com dois ou mais vínculos não quitados ao mesmo tempo. </p>
	</div>

	<%-- Mantém as informações do usuário e a situação entre as requisições --%>
	<a4j:keepAlive beanName="desfazQuitacaoVinculoUsuarioBibliotecaMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />

	<h:form id="formDesfazQuitacaoUsuario">
	
		<c:set var="_infoUsuarioCirculacao" value="${desfazQuitacaoVinculoUsuarioBibliotecaMBean.infoUsuario}" scope="request" />
		<c:set var="_mostrarVinculo" value="false" scope="request" />						
		<%@include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%>
	
		<div class="infoAltRem" style="width: 80%">
			<h:graphicImage value="/img/alterar_old.gif" style="overflow: visible;"/>: 
			Retirar Quitação
		</div> 
	
		<table class="formulario" style="width:80%;">
			<caption>Vínculos do Usuário</caption>
			
			<thead>
				<tr>
					<td>Vínculo</td>
					<td>Data da Quitação</td>
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
								onclick="return confirm('Retirar a quitação desse Vínculo ? ');">
							<h:graphicImage url="/img/alterar_old.gif" style="border:none" title="Retirar Quitação" /> 					
							<f:param name="idContaUsuarioBibliotecaSelecionado" value="#{contaUsuarioBiblioteca.id}" />
						</h:commandLink> 
					</td>
					</tr>
				</c:forEach>
			</c:if>
			
			<c:if test="${empty desfazQuitacaoVinculoUsuarioBibliotecaMBean.contasUsuarioBiblioteca}">
				<tr>
					<td style="color: red; text-align: center;"> Usuário não possuiu contas na biblioteca </td>
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