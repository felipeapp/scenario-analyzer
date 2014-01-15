<%@include file="../include/cabecalho.jsp"%>

<f:view>
	<div data-role="page" id="page-biblioteca-android" data-theme="b">			
		<h:form id="form-biblioteca-android">
			<div data-role="header" data-theme="b">
				<a href="http://back" data-icon="back">Voltar</a>
				<h1>${ configSistema['siglaSigaa']} Mobile</h1>
			</div>
			
			<div data-role="content" data-theme="b">
				<p align="center"><strong>Biblioteca</strong></p>
				<%@include file="/mobile/touch/include/mensagens.jsp"%>
								
			    <ul data-role="listview" data-inset="true">
		            <li><h:commandLink value="Visualizar Empréstimos" action="#{operacoesBibliotecaMobileMBean.iniciarVisualizarEmprestimos}"><f:param name="touch" value="true" /></h:commandLink></li>
					<li><h:commandLink value="Renovar Empréstimos" action="#{operacoesBibliotecaMobileMBean.iniciarRenovacao}"><f:param name="touch" value="true" /></h:commandLink></li>
					<li><h:commandLink value="Ultimos Empréstimos" action="#{operacoesBibliotecaMobileMBean.consultaUltimosEmprestimosUsuario}"><f:param name="touch" value="true" /></h:commandLink></li>
					<li><h:commandLink value="Consultar Acervo" action="#{operacoesBibliotecaMobileMBean.iniciarConsultaTitulo}"><f:param name="touch" value="true" /></h:commandLink></li>
		        </ul>
			</div>				
				
			<div data-role="footer" data-theme="b">
				<h4><br/>&copy; <%= br.ufrn.arq.util.UFRNUtils.getCopyright(2006) %></h4>
			</div>
		</h:form>
		
		<script>
			$("#form-biblioteca-android\\:btnVoltar").attr("data-rel", "back");
			$("#form-biblioteca-android\\:btnVoltar").attr("data-role", "link");
			$("#form-biblioteca-android\\:btnVoltar").attr("data-icon", "back");			
		</script>
	</div>
</f:view>

<%@include file="../include/rodape.jsp"%>
