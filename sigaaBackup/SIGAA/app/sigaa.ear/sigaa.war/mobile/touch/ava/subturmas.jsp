<%@include file="../include/cabecalho.jsp"%>

<f:view>
	<div data-role="page" id="page-subturmas" data-theme="b">
		<h:form id="form-subturmas">
	
			<div data-role="header" data-theme="b">
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<li><h:commandLink id="lnkVoltar" value="Voltar" action="#{ portalDocenteTouch.exibirTopico }" /></li>
					<li><h:commandLink value="Início" action="#{ portalDocenteTouch.acessarPortal }" id="lnkInicio"/></li>
					<li><h:commandLink value="Sair" action="#{ loginMobileTouch.logoff }" id="lnkSair" onclick="#{logout }" /></li>
				</ul>
			</div>
		
			<div data-role="content">
				<p align="center"><strong>Selecionar Subturma</strong></p>
				
				<p align="center">Selecione uma subturma para lançar frequência.</p>
				
				<%@include file="/mobile/touch/include/mensagens.jsp"%>
				
				<div data-role="fieldcontain">
					<c:if test="${ not empty portalDocenteTouch.turmas }">	
					    <ul data-role="listview" data-inset="true" data-theme="b" data-dividertheme="b">
					    	<li data-role="list-divider">Subturma</li>
					    	
					    	<c:forEach var="st" items="#{portalDocenteTouch.subTurmasDocente}" varStatus="loop">
				           		<li>
			  	           			<h:commandLink action="#{portalDocenteTouch.escolherSubTurma}">
										${st.descricaoSemDocente}
										<f:param name="id" value="#{st.id}"/>
									</h:commandLink>
								</li>
							</c:forEach>
					    </ul>
					</c:if>
					
					
				    
				 </div>
			</div>
			
			<script>				
				$("#form-subturmas\\:lnkVoltar").attr("data-icon", "back");	
				$("#form-subturmas\\:lnkSair").attr("data-icon", "sair");
				$("#form-subturmas\\:lnkInicio").attr("data-icon", "home");
			</script>
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
	  	</h:form>
		<%@include file="../include/modo_classico.jsp"%>
 	 </div>
   </f:view>
<%@include file="../include/rodape.jsp"%>
			
