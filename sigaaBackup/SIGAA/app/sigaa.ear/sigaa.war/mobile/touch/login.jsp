<% if (request.getParameter("expirada") != null && request.getParameter("expirada").equals("true")){ %>
	<script type="text/javascript">
		alert("Sua sess\u00e3o foi expirada. \u00c9 necess\u00e1rio autenticar-se novamente!");
	</script>
<% } %> 

<%@include file="include/cabecalho.jsp"%>

<f:view>
	<div data-role="page" id="page-login-mobile-touch" data-theme="b">

		<div data-role="header" data-theme="b">
			<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
		</div>

		<div data-role="content">
			<p align="center">
				${ configSistema['siglaInstituicao']} - ${ configSistema['siglaSigaa'] } - Sistema Integrado de Gestão de Atividades Acadêmicas<br /> <br /> <small><font color="red">ATENÇÃO!</font></small><br />
					O sistema diferencia letras maiúsculas de minúsculas APENAS na
					senha, portanto ela deve ser digitada da mesma maneira que no
					cadastro.<br />
				<br />
			</p>
			
			<%@include file="/mobile/touch/include/mensagens.jsp"%>
			
			<br />

			<h:form id="form-login" styleClass="ui-body ui-body-b ui-corner-all">
				<label for="login">Usuário:</label>
				<h:inputText value="#{loginMobileTouch.login}" />

				<label for="senha">Senha:</label>
				<h:inputSecret value="#{loginMobileTouch.senha}" />

				<p align="center">
					<h:commandButton action="#{ loginMobileTouch.login }" value="Entrar" id="entrar" />
				</p>
				
				<script>
					//$("#form-login").attr("data-ajax", "false");
				</script>
			</h:form>

			<h:form id="form-acesso-publico">
				<fieldset class="ui-grid-b" width="100%">
					<div class="ui-block-a" align="center"
						style="margin-top: 2%; margin-bottom: 2%; width: 100%;">
						<h:commandButton action="#{ portalPublicoTouch.redirectPortalPublico }" id="btnPortalPublico" value="Acessar Área Pública"/>
					</div>
				</fieldset>
				
				<script>
					$("#form-acesso-publico").attr("data-ajax", "false");
				</script>
			</h:form>
		</div>

		<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>	
		
		<h:form>
		<p align="center">
			<small> 
				<h:outputText value="Modo Mobile" id="txtDefinirModoMobile" /> |
				<h:commandLink rel="external" value="Modo Clássico" action="#{ loginMobileTouch.alterarParaModoClassico }"> 
					<f:param name="paginaLogin" value="true"/>
				</h:commandLink>
			</small>
		</p>
		</h:form>
	</div>
	<!-- data-role="page" -->

</f:view>

</body>
</html>
