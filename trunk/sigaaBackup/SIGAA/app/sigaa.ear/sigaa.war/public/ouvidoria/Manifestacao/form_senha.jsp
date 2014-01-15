<%@include file="/public/include/cabecalho.jsp"%>

<style>

h3 a:hover {
	color: blue;
	text-decoration: none;
}

table.formulario tbody tr th {
	font-weight: bold;
}

.required {
	vertical-align: top;
}

#consulta {
	color: #292;
}

#consulta th,#consulta td {
	padding: 6px;
}

#consulta th {
	font-weight: bold;
}

</style>

<f:view>
	<a4j:keepAlive beanName="esclarecimentoOuvidoria"></a4j:keepAlive>
	<h2>Responder Solicitação de Esclarecimento da Ouvidoria</h2>
	<h:form>
	
		<f:subview id="exibirForm" >
			<div class="descricaoOperacao">
				<p>Digite seu endereço de e-mail e sua senha para poder ter acesso a manifestação da ouvidoria.</p>
				<br />
			</div>
			<table class="formulario" width="30%" id="consulta">
				<caption>Informe seu e-mail e senha</caption>
				<tbody>
					<tr>
						<th>E-mail:</th>
						<td><h:inputText value="#{esclarecimentoOuvidoria.email}" id="email" /></td>
					</tr>
					<tr>
						<th>Código Acesso:</th>
						<td><h:inputSecret value="#{esclarecimentoOuvidoria.codigoAcesso}" id="codigoAcesso" /></td>
					</tr>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton action="#{esclarecimentoOuvidoria.listarManifestacoesPortalPublico}" value="entrar"></h:commandButton>
						</td>
					</tr>
				</tfoot>
			</table>
			
		</f:subview>
		
	</h:form>
	
	<br />
	<div style="width: 80%; text-align: center; margin: 0 auto;">
		<a href="/sigaa/public/home.jsf" style="color: #404E82;">&lt;&lt; voltar ao menu principal</a>
	</div>
	<br />

</f:view>

<%@include file="/public/include/rodape.jsp" %>