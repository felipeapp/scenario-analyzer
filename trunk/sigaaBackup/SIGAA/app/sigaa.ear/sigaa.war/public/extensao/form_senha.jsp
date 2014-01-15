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
	<a4j:keepAlive beanName="inscricaoParticipantes"></a4j:keepAlive>
	<h2>Inscrição nos Cursos e Eventos de Extensão</h2>
	<h:form>
	
		<f:subview id="exibirForm" rendered="#{!inscricaoParticipantes.mostrarMensagem}">
			<div class="descricaoOperacao">
				<p>Confirme sua inscrição informando a senha enviada para o seu email. 
				O Acesso a área de inscrição será realizado através do email e	senha.</p>
				<br />
				<p><b>OBS:</b> Para casos onde tenham sido realizadas mais de uma inscrição para uma mesma ação. 
				A confirmação de uma inscrição implicará no cancelamento das inscrições anteriores (as quais tenham 
				sido confirmadas), prevalecendo apenas a inscrição mais recente.</p>
			</div>
			<table class="formulario" width="30%" id="consulta">
				<caption>Informe sua senha</caption>
				<tbody>
					<tr>
						<th>Senha:</th>
						<td><h:inputSecret value="#{inscricaoParticipantes.obj.senha}" id="senha" /></td>
					</tr>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton action="#{inscricaoParticipantes.confirmarInscricao}" value="Enviar" />							
						</td>
					</tr>
				</tfoot>
			</table>
			
		</f:subview>
		
		<f:subview id="exibirMensagem" rendered="#{inscricaoParticipantes.mostrarMensagem}">
			<br />
			<h3>Realize o acompanhamento de sua inscrição através da área de inscritos.
			Para acessa-lá clique <a href="/sigaa/public/extensao/login_inscricao.jsf">aqui.</a></h3>
			<br /><br /><br /><br /><br /><br />
		</f:subview>
		
	</h:form>
	
	<br />
	<div style="width: 80%; text-align: center; margin: 0 auto;">
		<a href="/sigaa/public/home.jsf" style="color: #404E82;">&lt;&lt; voltar ao menu principal</a>
	</div>
	<br />

</f:view>

<%@include file="/public/include/rodape.jsp" %>