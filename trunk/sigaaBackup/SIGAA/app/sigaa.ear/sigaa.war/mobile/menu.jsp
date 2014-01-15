<%@include file="/mobile/commons/cabecalho.jsp" %>

	<f:view>
		
		<center>

			<strong> Menu Principal </strong>
			<br/>
			${avisoMobile}
			<br/>
			${ usuario.pessoa.nome }
			<br/>
			<br/>
		
			<h:form id="formMenuMobile">
				
				<c:if test="${ acesso.aluno }">
					<strong>Módulo Discente</strong> <br/><br/>
				
					<h:commandButton value="Datas das Provas" 
						action="#{consultaNotasMobileMBean.consultarDataProvas}" 
						 styleClass="botaoMenuMobile"/> 
					<br/>
					
					<h:commandButton value="Consultar Notas" action="#{consultaNotasMobileMBean.redirectPaginaNotas}" 
						 styleClass="botaoMenuMobile"/> 
					<br/>
					
					<h:commandButton value="Atestado de Matricula" 
						action="#{consultaNotasMobileMBean.atestadoMatricula}" 
						 styleClass="botaoMenuMobile"/> 
					<br/>
				</c:if>
	
				<br/><strong>Módulo Biblioteca</strong> <br/><br/>
				
				<h:commandButton value="Entrar" action="#{operacoesBibliotecaMobileMBean.entrarModuloBiblioteca}"
					 styleClass="botaoMenuMobile"/> <br/>
				
				<br/><br/>
				<a href="/sigaa/mobile/logonMobile.do?dispatch=logoff">Sair</a>
			
			</h:form>
		
		</center>
				
	</f:view>	

<%@include file="/mobile/commons/rodape.jsp" %>
